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

#include <errno.h>
#include <fcntl.h>
#include <pwd.h>
#include <stdarg.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>

// --------------------------------------------------------------------------------
// Global constants and variables
// --------------------------------------------------------------------------------

/* Boolean constants  */
#define TRUE 1
#define FALSE 0

/* The different tasks of this program  */
#define START_DAEMON 0
#define SHOW_HELP 1
#define SHOW_VERSION 2

/* Maximum length of error messages  */
#define ERR_MAX_SIZE 4096

// --------------------------------------------------------------------------------
// Error handling
// --------------------------------------------------------------------------------

/* Prints an error message to stderr and exits with code 1  */

static void error (char *format, ...)
{
  va_list args;
  va_start(args, format);
  char msg[ERR_MAX_SIZE];
  vsprintf(msg, format, args);
  fprintf(stderr, "ERROR: %s\n", msg);
  va_end(args);
  exit(1);
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

/* Returns true if the specified string is an option, otherwise false. */

int str_is_opt (char *str)
{
  return
    ( strlen(str) >= 2 && str[0] == '-' && str[1] != '-' )
    || ( strlen(str) >= 3 && str[0] == '-' && str[1] == '-' && str[2] != '-' );
}

// --------------------------------------------------------------------------------
// I/O utility functions
// --------------------------------------------------------------------------------

/* Redirects file descriptor 'fd' to file 'fname'. The file is opened using 'open', then
   the file descriptor of the file is duplicated to 'fd', then the file is closed again.
   'flags' and 'mode' are passed as the second and third arguments to 'open', respectively.
   For a description of the meaning of these parameters, see open(2).  */

void redirect (int fd, char *fname, int flags, mode_t mode)
{
  int fd_tmp = open(fname, flags, mode);
  if ( fd_tmp == -1 )
    error("Failed to open file: %s: %s", fname, error_msg());
  if ( dup2(fd_tmp, fd) == -1 )
    error("Failed to duplicate file descriptor %i -> %i: %s", fd_tmp, fd, error_msg());
  if ( close(fd_tmp) == -1 )
    error("Failed to close file: %s: %s", fname, error_msg());
}

// --------------------------------------------------------------------------------
// Tasks
// --------------------------------------------------------------------------------

void start_daemon(char *cmd, char *argv[], char *dir,
                  char *stdin_fname, char *stdout_fname, char *stderr_fname)
{
  switch (fork())
    {
    case -1:
      // Fork failed
      error("Fork failed: %s", error_msg());
      break;
    case 0:
      // Child process; starts the command
      setsid();
      if ( chdir(dir) == -1 )
        error("Failed to change into diectory: %s: $s", dir, error_msg());
      umask(0);
      redirect(STDIN_FILENO, stdin_fname, O_RDONLY, 0);
      redirect(STDOUT_FILENO, stdout_fname, O_WRONLY | O_CREAT, S_IRUSR | S_IWUSR);
      redirect(STDERR_FILENO, stderr_fname, O_WRONLY | O_CREAT, S_IRUSR | S_IWUSR);
      execv(cmd, argv);
      break;
    default:
      // Parent process; exits immediately
      exit(0);
    }
}

/* Prints a short help text to stdout.  */

void show_help ()
{
  puts("Usage:\n"
       "  mmdaemon [OPTIONS] COMMAND [PARAMS]\n"
       "  mmdaemon --help | -h | --version | -v\n"
       "Description:\n"
       "  Starts COMMAND as a daemon process. PARAMS are passed to COMMAND as command\n"
       "  line parameters.\n"
       "Options:\n"
       "  --dir=DIR, -d DIR\n"
       "      Sets the working directory of the daemon. Defaults to the current working\n"
       "      directory\n"
       "  --stdin=IN_FILE, -i IN_FILE\n"
       "      Specifies a file where the standard input of the daemon is read\n"
       "      from. Defaults to \"/dev/null\", i.e., the daemon gets no input.\n"
       "  --stdout=OUT_FILE, -o OUT_FILE\n"
       "      Specifies a file where the standard output of the daemon is redirected\n"
       "      to. Defaults to \"/dev/null\", i.e., the standard output is discarded.\n"
       "  --stderr=ERR_FILE, -e ERR_FILE\n"
       "      Specifies a file where the standard error stream of the daemon is\n"
       "      redirected to. Defaults to \"/dev/null\", i.e., the standard error stream\n"
       "      is discarded.\n"
       "  --help, -h\n"
       "      Prints this help text and exits\n"
       "  --version, -v\n"
       "      Prints version information and exits");
}

/* Prints version information to stdout.  */

void show_version ()
{
  puts("$Revision: 1.2 $");
}

// --------------------------------------------------------------------------------
// Main program
// --------------------------------------------------------------------------------


/* Main entry point.  */

int main (int argc, char *argv[])
{
  char *dir = getcwd(NULL, 0); // GNU specifiec
  char *stdin_fname = "/dev/null";
  char *stdout_fname = "/dev/null";;
  char *stderr_fname = NULL;
  char *cmd = NULL;
  int task = START_DAEMON;

  // Process command line parameters:
  while ( TRUE )
    {
      // Proceed to next parameter:
      if ( --argc > 0 )
        argv++;
      else
        argv = NULL;

      // Exit loop if no more parameters exist:
      if ( argc == 0 )
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

      // --stdin, -i:
      else if ( str_starts_with(arg, "--stdin=") )
        stdin_fname = arg+strlen("--stdin=");
      else if ( str_equals(arg, "--stdin") )
        {
          if ( --argc == 0 || str_is_opt((++argv)[0]) )
            error("Missing value after --stdin");
          stdin_fname = argv[0];
        }
      else if ( str_starts_with(arg, "-i=") )
        stdin_fname = arg+strlen("-i=");
      else if ( str_equals(arg, "-i") )
        {
          if ( --argc == 0 || str_is_opt((++argv)[0]) )
            error("Missing value after -i");
          stdin_fname = argv[0];
        }

      // --stdout, -o:
      else if ( str_starts_with(arg, "--stdout=") )
        stdout_fname = arg+strlen("--stdout=");
      else if ( str_equals(arg, "--stdout") )
        {
          if ( --argc == 0 || str_is_opt((++argv)[0]) )
            error("Missing value after --stdout");
          stdout_fname = argv[0];
        }
      else if ( str_starts_with(arg, "-o=") )
        stdout_fname = arg+strlen("-o=");
      else if ( str_equals(arg, "-o") )
        {
          if ( --argc == 0 || str_is_opt((++argv)[0]) )
            error("Missing value after -o");
          stdout_fname = argv[0];
        }

      // --stderr, -e:
      else if ( str_starts_with(arg, "--stderr=") )
        stderr_fname = arg+strlen("--stderr=");
      else if ( str_equals(arg, "--stderr") )
        {
          if ( --argc == 0 || str_is_opt((++argv)[0]) )
            error("Missing value after --stderr");
          stderr_fname = argv[0];
        }
      else if ( str_starts_with(arg, "-e=") )
        stderr_fname = arg+strlen("-e=");
      else if ( str_equals(arg, "-e") )
        {
          if ( --argc == 0 || str_is_opt((++argv)[0]) )
            error("Missing value after -e");
          stderr_fname = argv[0];
        }

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
        {
          cmd = arg;
          // Exit loop:
          break;
        }
    }

  if ( stderr_fname == NULL )
    stderr_fname = stdout_fname;

  switch ( task )
    {
    case START_DAEMON:
      if ( cmd == NULL )
        error("No command specified");
      start_daemon(cmd, argv, dir, stdin_fname, stdout_fname, stderr_fname);
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
