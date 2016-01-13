/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2010 Technische Universitaet Berlin
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

#define _GNU_SOURCE

#include <stdlib.h>
#include <stdio.h>
#include <stdarg.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <errno.h>
#include <signal.h>
#include <pwd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <sys/wait.h>
#include "paths.h"
#include "reqtype.h"
#include "rspcode.h"
#include "channel.h"
#include "exitval.h"

// --------------------------------------------------------------------------------
// Global constants and variables
// --------------------------------------------------------------------------------

/* Boolean constants  */
#define TRUE 1
#define FALSE 0

/* Filename of the default Mmjvmd directory  */
#define MMJVMD_DIR_NAME ".mmjvmd"

/* Filename of the server socket  */
#define SERVER_SOCKET_FNAME "mmjvmd.socket"

/* Environment variable containing the absolute filename of the Mmjvmd directory  */
#define ENV_MMJVMD_DIR_NAME_ABS "MMJVMD_DIR"

/* Maximum length of error messages  */
#define ERR_MAX_SIZE 4096

/* Buffer size for i/o operations  */
#define IO_BUF_SIZE 255

/* The different tasks of this program  */
#define EXECUTE 0
#define STOP_SERVER 1
#define STOP_SERVER_FORCE 2
#define PING 3
#define SHOW_HELP 4
#define SHOW_VERSION 5

/* Filename of the mmjvmc program, possibly with path  */
#define MMJVMC_PATH PREFIX "/bin/mmjvmc"

/* Name of the command to stop another command  */
#define STOP_CMD "stopcmd"

/* Return value of the fork call. Before the fork, this variable has the value -2.
   Thus, the possible values of this variable and their respective meanings are as
   follows (cf. fork(2)):

     -2  No fork yet
     -1  Fork failed
      0  Fork done, child process
     >0  Fork done, parent process

   In the last case (value >0) the value contains the pid of the child process.  */

int fork_value = -2;

// --------------------------------------------------------------------------------
// Error handling
// --------------------------------------------------------------------------------

/* Prints an error message to stderr and exits with code 'CLIENT_ERROR_EXITVAL'.
   The message is composed by passing 'format' and the remaining arguments to
   'vsprintf.' */

static void error (char *format, ...)
{
  va_list args;
  va_start(args, format);
  char msg[ERR_MAX_SIZE];
  vsprintf(msg, format, args);
  fprintf(stderr, "ERROR: %s\n", msg);
  va_end(args);
  if ( fork_value == 0 )       // Already forked and this is the child process
    kill(getppid(), SIGKILL);  // Stop parent process
  else if ( fork_value > 0 )   // Already forked and this is the parent process
    kill(fork_value, SIGKILL); // Stop child process
  exit(CLIENT_ERROR_EXITVAL);
}

/* Returns the message corresponding to the current value of errno, or the string
   "Unknown error" if errno is 0. */

char *error_msg ()
{
  char buf[ERR_MAX_SIZE];
  return (errno != 0 ? strerror_r(errno, buf, ERR_MAX_SIZE) : "Unknown error");
}

// --------------------------------------------------------------------------------
// String utility functions
// --------------------------------------------------------------------------------

/* Returns a copy of the specified string.  */

char *copy_str (char *str)
{
  char *cp_str = (char *)malloc((strlen(str)+1)*sizeof(char));
  if ( cp_str == NULL )
    error("copy_str: Failed to allocate memory: %s", error_msg());
  strcpy(cp_str, str);
  return cp_str;
}

/* Returns true if the two specified strings are equal, othrwise false. */

int str_equals (char *str1, char *str2)
{
  return
    (strcmp(str1, str2) == 0 ? TRUE : FALSE);
}

/* Returns true if 'str1' starts with 'str2', otherwise false.  */

int str_starts_with (char *str1, char *str2)
{
  size_t len1 = strlen(str1);
  size_t len2 = strlen(str2);
  return
    (len1 < len2
     ? FALSE
     : (strncmp(str1, str2, len2) == 0 ? TRUE : FALSE));
}

/* Returns a copy of the specified string with leading and trailing whitespaces
   removed.  */

char *trim_str (char *str)
{
  int start = 0;
  int end = strlen(str) - 1;

  while ( start <= end && isspace(str[start]) )
    start++;

  while ( end > start && isspace(str[end]) )
    end--;

  int len = end-start+1;
  char *trim_str = (char *)malloc((len+1)*sizeof(char));
  if ( trim_str != NULL )
    strncpy(trim_str, str+=start, len);

  return trim_str;
}

/* Concatenates the specified paths. */

char *concat_paths (char *path1, char *path2)
{
  // Remove trailing backslashes from path1:
  int len1 = strlen(path1);
  while ( len1 > 0 && path1[len1-1] == '/' )
    len1--;

  // Remove leading backslashes from path2:
  while ( strlen(path2) > 0 && path2[0] == '/' )
    path2++;
  int len2 = strlen(path2);

  char *path = (char *)malloc((len1+len2+2)*sizeof(char));
  if ( path == NULL )
    error("concat_paths: Failed to allocate memory: %s", error_msg());
  strcpy(path, path1);
  path[len1] = '/';
  strcpy(path+len1+1, path2);
  return path;
}

/* Returns true if the specified string is an option, otherwise false. */

int str_is_opt (char *str)
{
  return
    ( strlen(str) >= 2 && str[0] == '-' && str[1] != '-' )
    || ( strlen(str) >= 3 && str[0] == '-' && str[1] == '-' && str[2] != '-' );
}

/* Returns the number of lines of the specified string */

int count_lines (char str[])
{
  int count = 1;
  int i;
  int len = strlen(str);
  for (i = 0; i < len; i++)
    {
      if ( str[i] == '\n' )
        count++;
    }
  return count;
}

/* Converts an int to a string. */

char *int_to_str (int value)
{
  char *str;
  if ( asprintf(&str, "%i", value) < 0 )
    error("int_to_str: %s", error_msg());
  return str;
}

// --------------------------------------------------------------------------------
// Misc. utility functions
// --------------------------------------------------------------------------------

/* Returns the home directory of the user.  */

char *get_home_dir ()
{
  struct passwd *data = getpwuid(getuid());
  if ( data == NULL )
    error("Failed to get home directory: %s", error_msg());
  char *home_dir = data->pw_dir;
  if ( home_dir == NULL )
    error("No home directory found");
  return copy_str(home_dir);
}

/* Returns the filename of the server socket. In the first step, the user's Mmjvmd
   directory is determined. To this end, the function checks the environment variable
   given by the macro 'ENV_MMJVMD_DIR_NAME_ABS'. If the variable is set and is not
   the empty string, the directory is obtained from it. Otherwise, the directory is
   obtained by concatenating the user's home directory and the value of the macro 
   MMJVMD_DIR_NAME. Finally, the value of the macro SERVER_SOCKET_FNAME is added to
   the directory.  */

char *server_socket_fname ()
{
  char *buf = getenv(ENV_MMJVMD_DIR_NAME_ABS);
  char *dir =
    (buf != NULL && strlen(buf) > 1
     ? copy_str(buf)
     : concat_paths(get_home_dir(), MMJVMD_DIR_NAME));
  return concat_paths(dir, SERVER_SOCKET_FNAME);
}

// --------------------------------------------------------------------------------
// I/O utility functions
// --------------------------------------------------------------------------------

/* Similar to 'write', but if an error occurrs, calls 'error' instead of returning
   -1. Furthermore, if less then 'len' bytes are written, calls 'error', too. (The
   "_chk" in the function name stands for "check".) */

ssize_t write_chk (int fd, unsigned char *buf, size_t len)
{
  errno = 0;
  ssize_t count = write(fd, buf, len);
  if ( count == -1 )
    error("Failed to write to file descriptor %i: %s", fd, error_msg());
  if ( count != len )
    error("Failed to write to file descriptor %i: Incomplete write", fd);
  return count;
}

/* Similar to 'read', but if an error occurrs, calls 'error' instead of returning
   -1 (the "_chk" in the function name stands for "check").  */

ssize_t read_chk (int fd, unsigned char *buf, size_t len)
{
  errno = 0;
  ssize_t count = read(fd, buf, len);
  if ( count == -1 )
    error("Failed to read from file descriptor %i: %s", fd, error_msg());
  return count;
}

/* Similar to 'read', but with the following two differences: (1) Reads exactly 'len'
   bytes, and (2) calls 'error' if an error occurrs instead of returning -1. Concerning
   the first point, recall that the normal 'read' function may read less then 'len'
   bytes. This may occur for instance if 'fd' is a socket and not all 'len' bytes are
   already written to the socket. This function calls 'read' repeatedly until 'len'
   bytes are read.  */

void read_cmpl_chk (int fd, unsigned char *buf, size_t len)
{
  size_t count = 0;
  do
    {
      buf+=count;
      len-=count;
      errno = 0;
      count = read(fd, buf, len);
      if ( count == -1 )
        error("Failed to read from file descriptor: %s", fd, error_msg());
    }
  while ( count < len );
}

/* Redirects file descriptor 'fd' to file 'fname'. The file is opened using 'open', then
   the file descriptor of the file is duplicated to 'fd', then the file is closed again.
   'flags' is passed as the second argument to 'open'. In particular, 'flags' determines
   whether the file is opened for reading, writing, or both. It must be choosen compatible
   with the readable/writable status of 'fd'. For a detailed description of the meaning
   of 'flags', see open(2).  */

void redirect (int fd, char *fname, int flags)
{
  int fd_tmp = open(fname, flags);
  if ( fd_tmp == -1 )
    error("Failed to open file: %s: %s", fname, error_msg());
  if ( dup2(fd_tmp, fd) == -1 )
    error("Failed to duplicate file descriptor %i -> %i: %s", fd_tmp, fd, error_msg());
  if ( close(fd_tmp) == -1 )
    error("Failed to close file: %s: %s", fname, error_msg());
}

// --------------------------------------------------------------------------------
// Functions implementing the main task
// --------------------------------------------------------------------------------

/* Signal handler for SIGINT.

   If this is the parent process (before or after the main fork), does another fork and
   lets the new child process start a second mmjvmc process which sends STOP_CMD <pid>
   to the server in order to terminate the command started by this mmjvmc process normally.
   <pid> is the pid of the parent process.

   If this is the child process with respect to the main fork, simply calls 'exit(0)'.   */

void terminate (int sig)
{
  if ( fork_value > 0 || fork_value == -2 ) // Parent process
    {
      switch ( vfork() )
        {
        case 0:
          redirect(STDIN_FILENO, "/dev/null", O_RDONLY);
          if ( execl(MMJVMC_PATH, MMJVMC_PATH, STOP_CMD, int_to_str(getppid()), NULL) == -1 )
            error("Failed to send stop command: %s", error_msg());
          break;
        case -1:
          error("Failed to send stop command: %s", error_msg());
          break;
        }
    }
  else // Child process
    {
      exit(0);
    }
}

/* Does the fork and sets the 'fork_value' variable. During the fork, the SIGINT
   signal is blocked. This is necessary because otherwise it may happen that the
   child process has already started but the fork() call has not yet returned
   the signal arrives at the child process; which leds to the situation that the
   when 'fork_value' variable is -2 in the child process. This would be wrongly
   interpreted as "no fork yet" by the signal handler.  */

void save_fork ()
{
  sigset_t tmp_mask, orig_mask;

  // Setup tmp_mask:
  sigemptyset(&tmp_mask);
  sigaddset(&tmp_mask, SIGINT);

  // Change signal mask:
  sigprocmask(SIG_BLOCK, &tmp_mask, &orig_mask);

  // Do the fork:
  fork_value = fork();

  // Restore signal mask:
  sigprocmask(SIG_UNBLOCK, &tmp_mask, NULL);
}

/* Sends the request to the server.  */

void send_request (int socket_fd, char *cmd, int argc, char *argv[], char* dir, char* classpath)
{
  // Send request type byte:
  unsigned char type[1] = {CMD_REQ};
  if ( write(socket_fd, type, 1) != 1 )
    error("Failes to send request type byte to server: %s", error_msg());

  // Open stream for writing to server:
  FILE *socket_in = fdopen(socket_fd, "w");
  if ( socket_in == NULL )
    error("Failed to create i/o stream for writing to server: %s", error_msg());

  // Write request to stream:
  fprintf(socket_in, "pid\n%i\n\n", getpid());
  fprintf(socket_in, "cmd\n%s\n\n", cmd);
  fprintf(socket_in, "argc\n%i\n\n", argc);
  if ( argv != NULL )
    {
      int i;
      for (i = 0; i < argc; i++)
        fprintf(socket_in, "arg\n%i\n%s\n\n", count_lines(argv[i]), argv[i]);
    }
  fprintf(socket_in, "dir\n%s\n\n", dir);
  if ( classpath != NULL )
    fprintf(socket_in, "classpath\n%s\n\n", classpath);
  fprintf(socket_in, "start\n\n");
  fflush(socket_in);
}

/* Reads data from stdin and writes them to socket_fd until stdin's end-of-file is
   reached.  */

void handle_input (int socket_fd)
{
  unsigned char ctrl[1];
  char buf[IO_BUF_SIZE];
  ssize_t count;
  while ( 1 )
    {
      // Read from stdin:
      count = read_chk(STDIN_FILENO, buf, IO_BUF_SIZE);

      // Write control byte:
      ctrl[0] = count;
      write_chk(socket_fd, ctrl, 1);

      // Exit loop if end of data is reached:
      if ( count == 0 )
        break;

      // Write to socket_fd:
      write_chk(socket_fd, buf, count);
    }
}

/* Reads data from 'socket_fd' and writes them to stdout or stderr, depending on the
   first control byte sent from the server.  */

int handle_output (int socket_fd)
{
  unsigned char ctrl[2];
  char buf[IO_BUF_SIZE];
  ssize_t count;
  while ( 1 )
    {
      // Read control bytes:
      read_cmpl_chk(socket_fd, ctrl, 2);

      // Get channel:
      int chan = ctrl[0];

      // If channel is TERM_CHAN, return with second control byte as
      // return value:
      if ( chan == TERM_CHAN )
        return(ctrl[1]);

      // Get data length:
      int len = ctrl[1];

      // Read data:
      read_cmpl_chk(socket_fd, buf, len);

      switch ( chan )
        {
        case STDOUT_CHAN:
          write_chk(STDOUT_FILENO, buf, len);
          break;
        case STDERR_CHAN:
          write_chk(STDERR_FILENO, buf, len);
          break;
        default:
            error("Unknown output channel: %i", chan);
        }
    }
}

/* Opens a connection to the server and returns the corresponding file descriptor.  */

int open_connection ()
{
  // Create socket:
  int socket_fd =  socket(PF_UNIX, SOCK_STREAM, 0);
  if ( socket_fd == -1 )
    error("Failed to create socket: %s", error_msg());

  // Set permissions:
  if ( fchmod(socket_fd, S_IRUSR | S_IWUSR) == -1 )
    error("Failed to set socket permissions: %s", error_msg());

  // Connect to server:
  char *fname = server_socket_fname();
  struct sockaddr_un socket_addr;
  socket_addr.sun_family = AF_UNIX;
  strcpy(socket_addr.sun_path, fname);
  size_t socket_addr_len = sizeof(socket_addr.sun_family) + strlen(socket_addr.sun_path);
  if ( connect(socket_fd, (struct sockaddr *)&socket_addr, socket_addr_len) == -1 )
    error("Connection to %s failed: %s", fname, error_msg());

  return socket_fd;
}

/* Executes the command on the server.  */

void execute (char *cmd, int argc, char *argv[], char* dir, char* classpath)
{
  // Set a new signal handler for SIGINT:
  if ( signal(SIGINT, terminate) == SIG_ERR )
    error("Failed to install new signal handler for SIGINT: %s", error_msg());

  // Connect to server:
  int socket_fd = open_connection();

  // Send request:
  send_request(socket_fd, cmd, argc, argv, dir, classpath);

  // Fork to handle stdin, stdout and stderr simultaneously:
  save_fork();

  if ( fork_value == -1 )
    error("Fork failed: %s", error_msg());

  else if ( fork_value > 0 ) // Parent process; handles stdout and stderr
    {
      int exit_value = handle_output(socket_fd);

      if ( kill(fork_value, SIGINT) == -1 )
        error("Stopping child failed: %s", error_msg());
      exit(exit_value);
    }
  else // Child process; handles stdin
    {
      handle_input(socket_fd);
      errno = 0;
      if ( close(socket_fd) != 0 )
        error("Failed to close socket: %s", error_msg());
    }
}

/* Stops the server provided no active clients exist.  */

void stop_server ()
{
  int socket_fd = open_connection();
  unsigned char buf[1] = {STOP_REQ};
  write_chk(socket_fd, buf, 1);
  read_cmpl_chk(socket_fd, buf, 1);
  if ( buf[0] == FAILED_RSPCODE )
    error("Stop refused by server: Active clients exist\n");
}

/* Stops the server regardless wether active clients exist or not.  */

void stop_server_force ()
{
  int socket_fd = open_connection();
  unsigned char buf[1] = {FORCED_STOP_REQ};
  write_chk(socket_fd, buf, 1);
  read_cmpl_chk(socket_fd, buf, 1);
}

/* Sends a "ping" to the server. If one byte can be recieved as response,
   prints a message saying the connection is ok. Otherwise, signals an
   error.  */

void ping ()
{
  int socket_fd = open_connection();
  unsigned char buf[1] = {PING_REQ};
  write_chk(socket_fd, buf, 1);
  read_cmpl_chk(socket_fd, buf, 1);
  printf("Connection ok\n");
}

/* Prints a short help text to stdout.  */

void show_help ()
{
  puts("Usage:\n"
       "  mmjvmc [--dir=DIR | -d DIR] [--classpath=CLASSPATH] CMD [PARAMS]\n"
       "  mmjvmc --stop-server | --stop-server-fast | --stop-server-immediately\n"
       "  mmjvmc --ping\n"
       "  mmjvmc --help | -h | --version | -v\n"
       "Options and arguments:\n"
         "  CMD\n"
       "      Command name\n"
       "  PARAMS\n"
       "      Parameters passed to the command\n"
       "  --dir=DIR, -d DIR\n"
       "      Sets the working directory of the command.\n"
       "  --dir-file=FILE\n"
       "      Sets the working directory of the command to the content\n"
       "      of the specified file.\n"
       "  --classpath=CLASSPATH\n"
       "      Sets the classpath of the command. Currently ignored.\n"
       "  --stop-server\n"
       "      Stops the server provided no active clients exist\n"
       "  --stop-server-force\n"
       "      Stops the virtual machine\n"
       "  --ping\n"
       "      Sends a ping signal to the server\n"
       "  --help, -h\n"
       "      Prints this help text and exits\n"
       "  --version, -v\n"
       "      Prints version information and exits");
}

/* Prints version information to stdout.  */

void show_version ()
{
  puts("$Revision: 1.17 $");
}

/* Auxiliary function to read the working directory from a file. Reads the file with
   the specified name ('fname') and returns its content as a string. Leading and
   trailing whitespaces are stripped by the 'trim_str' function. If the file does not
   exist, returns NULL.  */

char *read_file (char *fname)
{
  int fd = open(fname, O_RDONLY);
  if ( fd == -1 )
    {
      if ( errno == ENOENT )
        return NULL;
      else
        error("Failed to open file: %s: %s", fname, error_msg());
    }
  struct stat attribs;
  if ( fstat(fd, &attribs) == -1 )
    error("Failed to access file attributes: %s: %s", fname, error_msg());
  int len = attribs.st_size;
  char *content = (char *)malloc(len+1);
  if ( content == NULL )
    error("Failed to allocate memory for content: %s", error_msg());
  read_cmpl_chk(fd, content, len);
  content[len] = 0;
  return trim_str(content);
}

/* Main entry point.  */

int main (int argc, char *argv[])
{
  char *dir = NULL;
  char *classpath = NULL;
  char *cmd = NULL;
  int task = EXECUTE;

  // Process command line parameters:
  while ( TRUE )
    {
      // Proceed to next parameter:
      if ( --argc > 0 )
        argv++;
      else
        argv = NULL;

      // Exit loop if command was found or no more parameters exist:
      if ( cmd != NULL || argc == 0 )
        break;

      // Next parameter:
      char *arg = argv[0];

      // --dir, -d:
      if ( str_starts_with(arg, "--dir=") )
        dir = arg+strlen("--dir=");
      else if ( str_equals(arg, "--dir") )
        {
          if ( --argc == 0 || str_is_opt((++argv)[0]) )
            error("Missing value after --dir");
          dir = argv[0];
        }
      else if ( str_starts_with(arg, "-d=") )
        dir = arg+strlen("-d=");
      else if ( str_equals(arg, "-d") )
        {
          if ( --argc == 0 || str_is_opt((++argv)[0]) )
            error("Missing value after -d");
          dir = argv[0];
        }

      // --dir-file:
      else if ( str_starts_with(arg, "--dir-file=") )
        {
          dir = read_file(arg+strlen("--dir-file="));
        }
      else if ( str_equals(arg, "--dir-file") )
        {
          if ( --argc == 0 || str_is_opt((++argv)[0]) )
            error("Missing value after --dir-file");
          dir = read_file(argv[0]);
        }

      // --classpath:
      else if ( str_starts_with(arg, "--classpath=") )
        classpath = arg+strlen("--classpath=");
      else if ( str_equals(arg, "--classpath") )
        {
          if ( --argc == 0 || str_is_opt((++argv)[0]) )
            error("Missing value after --classpath");
          classpath = argv[0];
        }

      // --stop-server:
      else if ( str_equals(arg, "--stop-server") )
        task = STOP_SERVER;

      // --stop-server-force:
      else if ( str_equals(arg, "--stop-server-force") )
        task = STOP_SERVER_FORCE;

      // --ping:
      else if ( str_equals(arg, "--ping") )
        task = PING;

      // --help:
      else if ( str_equals(arg, "--help") || str_equals(arg, "-h") )
        task = SHOW_HELP;

      // --version:
      else if ( str_equals(arg, "--version") || str_equals(arg, "-v") )
        task = SHOW_VERSION;

      // Unknown option:
      else if ( str_is_opt(arg) )
        error("Unknown option: %s", arg);

      // Command:
      else
        cmd = arg;
    }

  // Set dir to default if necessary:
  if ( dir == NULL )
    dir = getcwd (NULL, 0); // GNU specifiec

  switch ( task )
    {
    case EXECUTE:
      execute(cmd, argc, argv, dir, classpath);
      break;
    case STOP_SERVER:
      stop_server();
      break;
    case STOP_SERVER_FORCE:
      stop_server_force();
      break;
    case PING:
      ping();
      break;
    case SHOW_HELP:
      show_help();
      break;
    case SHOW_VERSION:
      show_version();
      break;
    }

  return(0);
}
