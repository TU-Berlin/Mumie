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

package net.mumie.ipc.unix;

/**
 * Provides Java bindings for the C functions needed to implement IPC.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: UnixLib.java,v 1.5 2007/07/25 00:07:41 rassy Exp $</code>
 */

public class UnixLib
{
  // --------------------------------------------------------------------------------
  // Open and close
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Opens the file with the specified filename for reading and/or writing. The latter is
   *   controlled by the parameters <code>readable</code> and <code>writable</code>. The
   *   file may be a normal file or a fifo. Returns the file descriptor, or -1 if an error
   *   occurred.
   * </p>
   * <p>
   *   Corresponds to the C function <code>open</code>.
   * </p>
   *
   * @param filename name of the file or fifo
   * @param readable whether the file is opened for reading
   * @param writable whether the file is opened for writing
   *
   * @return the file descriptor, or -1 if an error occurred.
   */

  public static native int open (String filename,
                                 boolean readable,
                                 boolean writable);

  /**
   * <p>
   *   Closes the file with the specified file descriptor. The file may be a normal file, a
   *   fifo, or a socket. Returns 0 in case of  success, or -1 if an error occurred.
   * </p>
   * <p>
   *   Corresponds to the C function <code>close</code>.
   * </p>
   *
   * @param fileDescriptor the file descriptor. May represent a normal file as well as a
   *   fifo or socket.
   *
   * @return 0 in case of success, -1 if an error occurred.
   */

  public static native int close (int fileDescriptor);

  // --------------------------------------------------------------------------------
  // Removing files
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Removes the file with the specified name. If the file is a directory, it must be
   *   empty.
   * </p>
   * <p>
   *   Corresponds to the C function <code>remove</code>.
   * </p>
   *
   * @param filename the name of the file
   *
   * @return 0 on success, -1 on error.
   */

  public static native int remove (String filename);

  // --------------------------------------------------------------------------------
  // File permissions
  // --------------------------------------------------------------------------------

  /**
   * 
   */

  public static native String umask (String modeString);

  /**
   * <p>
   *   Changes file permissions. The permissions of the file corresponding to the specified
   *   file descriptor are set according to the specified boolean flags.
   * </p>
   * <p>
   *   Corresponds to the C function <code>fchmod</code>.
   * </p>
   *
   * @param fileDescriptor the file descriptor of the file
   * @param userRead whether the owner of the file can read it
   * @param userWrite whether the owner of the file can write it
   * @param userExecute whether the owner of the file can execute it
   * @param groupRead whether the group of the file can read it
   * @param groupWrite whether the group of the file can write it
   * @param groupExecute whether the group of the file can execute it
   * @param othersRead whether others of the file can read it
   * @param othersWrite whether others of the file can write it
   * @param othersExecute whether others of the file can execute it
   *
   * @return 0 on success, -1 on error
   */

  public static native int chmod (int fileDescriptor,
                                  boolean userRead,
                                  boolean userWrite,
                                  boolean userExecute,
                                  boolean groupRead,
                                  boolean groupWrite,
                                  boolean groupExecute,
                                  boolean othersRead,
                                  boolean othersWrite,
                                  boolean othersExecute);

  /**
   * 
   */

  public static void checkModeString (String modeString)
  {
    char[] modeChars = modeString.toCharArray();
    if ( modeChars.length != 9 )
      throw new IllegalArgumentException
        ("Invalid mode string: " + modeString + " (has not exactly 9 characters)");
    if ( modeChars[0] != 'r' && modeChars[0] != '-' )
      throw new IllegalArgumentException
        ("Invalid mode string: " + modeString + " (first character must be 'r' or '-')");
    if ( modeChars[1] != 'w' && modeChars[0] != '-' )
      throw new IllegalArgumentException
        ("Invalid mode string: " + modeString + " (second character must be 'w' or '-')");
    if ( modeChars[2] != 'x' && modeChars[0] != '-' )
      throw new IllegalArgumentException
        ("Invalid mode string: " + modeString + " (third character must be 'x' or '-')");
    if ( modeChars[3] != 'r' && modeChars[0] != '-' )
      throw new IllegalArgumentException
        ("Invalid mode string: " + modeString + " (4th character must be 'r' or '-')");
    if ( modeChars[4] != 'w' && modeChars[0] != '-' )
      throw new IllegalArgumentException
        ("Invalid mode string: " + modeString + " (5th character must be 'w' or '-')");
    if ( modeChars[5] != 'x' && modeChars[0] != '-' )
      throw new IllegalArgumentException
        ("Invalid mode string: " + modeString + " (6th character must be 'x' or '-')");
    if ( modeChars[6] != 'r' && modeChars[0] != '-' )
      throw new IllegalArgumentException
        ("Invalid mode string: " + modeString + " (7th character must be 'r' or '-')");
    if ( modeChars[7] != 'w' && modeChars[0] != '-' )
      throw new IllegalArgumentException
        ("Invalid mode string: " + modeString + " (8th character must be 'w' or '-')");
    if ( modeChars[8] != 'x' && modeChars[0] != '-' )
      throw new IllegalArgumentException
        ("Invalid mode string: " + modeString + " (9th character must be 'x' or '-')");
  }

  // --------------------------------------------------------------------------------
  // Socket methods
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Creates a Unix domain socket. Returns the new file descriptor, or -1 if an error
   *   occurred. The socket uses a streaming protocol.
   * </p>
   * <p>
   *   Corresponds to the C function <code>socket</code> called with the arguments
   *   <code>PF_UNIX, SOCK_STREAM, 0</code>.
   * </p>
   *
   * @return The file descriptor of the socket, or -1 if an error occurred.
   */

  public static native int socket ();

  /**
   * <p>
   *   Binds a Unix domain socket to a filename.
   * </p>
   * <p>
   *   Corresponds to the C function <code>bind</code>.
   * </p>
   *
   * @param fileDescriptor the file descriptor of the socket
   * @param filename the filename the socket is to be bound to
   *
   * @return 0 in case of success, -1 when an error occurred
   */

  public static native int bind (int fileDescriptor, String filename);

  /**
   * <p>
   *   Listens on a socket for connections.
   * </p>
   * <p>
   *   Corresponds to the C function <code>listen</code>.
   * </p>
   *
   * @param fileDescriptor the file descriptor of the socket
   *
   * @return 0 on success, -1 on error.
   */

  public static native int listen (int fileDescriptor);

  /**
   * <p>
   *   Accepts a connection at the socket with the specified file descriptor. Returns
   *   the file descriptor of the socket of the new connection.
   * </p>
   * <p>
   *   Corresponds to the C function <code>accept</code>.
   * </p>
   *
   * @param fileDescriptor the file descriptor of the socket which listening for connections
   *
   * @return the file descriptor of the socked which emerged from the accepted connection,
   *   or -1 if an error occurred
   */

  public static native int accept (int fileDescriptor);

  /**
   * <p>
   *   Connects the socket with the specified file descriptor to the socket with the
   *   specified filename. This is commonly used to establish a conection from a "client" to
   *   a "server". The client socket is the one specified by the file descriptor, the server
   *   socket is the one specified by the filename.
   * </p>
   * <p>
   *   Corresponds to the C function <code>connect</code>.
   * </p>
   *
   * @param fileDescriptor the file descriptor of the socket to be connected
   * @param filename the filename of the socket the above socket is to be connected to
   *
   * @return 0 on success, -1 on error.
   */

  public static native int connect (int fileDescriptor, String filename);

  // --------------------------------------------------------------------------------
  // Reading and writing
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Reads from a file descriptor.
   * </p>
   * <p>
   *   Tries to read up to <code>length</code> bytes from the specified file descriptor into
   *   the region of the specified buffer starting at <code>offset</code>. Returns the
   *   number of bytes read, or -1 if an error occurred. A return value of 0 means that the
   *   end of the file has been reached.
   * </p>
   * <p>
   *   Corresponds to the C function <code>read</code>.
   * </p>
   *
   * @param fileDescriptor the file descriptor. May represent a normal file as well as a
   *   fifo or socket.
   * @param buffer the array that recieves the read bytes
   * @param offset index of the element of <code>buffer</code> which contains the first byte
   *   read by this method
   * @param length maximum number of bytes to read. The actual number may be smaller
   *
   * @return the number of bytes actually read, or -1 if an error occurred
   */

  public static native int read (int fileDescriptor,
                                 byte[] buffer,
                                 int offset,
                                 int length);

  /**
   * <p>
   *   Reads from a file descriptor.
   * </p>
   * <p>
   *   Tries to read up to <code>buffer.length</code> bytes from the specified file
   *   descriptor into the specified buffer. Returns the number of bytes read, or -1 if an
   *   error occurred. A return value of 0 means that the end of the file has been reached.
   * </p>
   * <p>
   *   Corresponds to the C function <code>read</code>.
   * </p>
   * <p>
   *   The effect of this method is the same as
   *   {@link #read(int,byte[],int,int) read(fileDescriptor, buffer, 0, buffer.length)},
   *   but it is implemented more efficiently.
   * </p>
   *
   * @param fileDescriptor the file descriptor. May represent a normal file as well as a
   *   fifo or socket.
   * @param buffer the array that receives the read bytes
   *
   * @return the number of bytes actually read, or -1 if an error occurred
   */

  public static native int read (int fileDescriptor, byte[] buffer);

  /**
   * <p>
   *   Writes to a file descriptor.
   * </p>
   * <p>
   *   Writes <code>length</code> bytes from the region of the specified buffer starting at
   *   <code>offset</code> to the specified file descriptor. Returns the number of bytes
   *   written, or -1 if an error occurred.
   * </p>
   * <p>
   *   NOTE: If the return value is non-negativ, but less then <code>length</code>, this
   *   means that not all bytes could be written, which usually indicates an error.
   * </p>
   * <p>
   *   Corresponds to the C function <code>write</code>.
   * </p>
   *
   * @param fileDescriptor the file descriptor. May represent a normal file as well as a
   *   fifo or socket.
   * @param buffer the array containing the bytes to write
   * @param offset index of the element of <code>buffer</code> which contains the first byte
   *   written by this method
   * @param length number of bytes to write
   *
   * @return the number of bytes written, or -1 if an error occurred
   */

  public static native int write (int fileDescriptor,
                                  byte[] buffer,
                                  int offset,
                                  int length);

  /**
   * <p>
   *   Writes to a file descriptor.
   * </p>
   * <p>
   *   Writes the bytes from of the specified buffer starting to the specified file
   *   descriptor. Returns the number of bytes written, or -1 if an error occurred.
   * </p>
   * <p>
   *   NOTE: If the return value is non-negativ, but less then <code>buffer.length</code>,
   *   this means that not all bytes could be written, which usually indicates an error.
   * </p>
   * <p>
   *   Corresponds to the C function <code>write</code>.
   * </p>
   *
   * @param fileDescriptor the file descriptor. May represent a normal file as well as a
   *   fifo or socket.
   * @param buffer the array containing the bytes to write
   *
   * @return the number of bytes written, or -1 if an error occurred
   */

  public static native int write (int fileDescriptor, byte[] buffer);

  // --------------------------------------------------------------------------------
  // Error handling
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Error message.
   * </p>
   * <p>
   *   Returns the error message corresponding to the last error number. The latter given by
   *   the C variable <code>errno</code>.
   * </p>
   * <p>
   *   Corresponds to the C function <code>strerror_r</code> called with <code>errno</code>
   *   as argument.
   * </p>
   */

  public static native String strerror ();

  // --------------------------------------------------------------------------------
  // Signals
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Sends a signal to a process.
   * </p>
   * <p>
   *   Corresponds to the C function <code>kill</code>.
   * </p>
   *
   * @param pid the process id. This specified the process to which the signal is sent.
   * @param signal the signal number
   *
   * @return 0 on success, -1 on error.
   */

  public static native int kill (int pid, int signal);

  /**
   * <p>
   *   Returns the number of the <code>SIGINT</code> signal.
   * </p>
   * <p>
   *   Corresponds to the C macro <code>SIGINT</code>.
   * </p>
   */

  public static native int SIGINT ();

  /**
   * <p>
   *   Returns the number of the <code>SIGQUIT</code> signal.
   * </p>
   * <p>
   *   Corresponds to the C macro <code>SIGQUIT</code>.
   * </p>
   */

  public static native int SIGQUIT ();

  /**
   * <p>
   *   Returns the number of the <code>SIGABRT</code> signal.
   * </p>
   * <p>
   *   Corresponds to the C macro <code>SIGABRT</code>.
   * </p>
   */

  public static native int SIGABRT ();

  /**
   * <p>
   *   Returns the number of the <code>SIGKILL</code> signal.
   * </p>
   * <p>
   *   Corresponds to the C macro <code>SIGKILL</code>.
   * </p>
   */

  public static native int SIGKILL ();

  /**
   * <p>
   *   Returns the number of the <code>SIGALRM</code> signal.
   * </p>
   * <p>
   *   Corresponds to the C macro <code>SIGALRM</code>.
   * </p>
   */

  public static native int SIGALRM ();

  /**
   * <p>
   *   Returns the number of the <code>SIGTERM</code> signal.
   * </p>
   * <p>
   *   Corresponds to the C macro <code>SIGTERM</code>.
   * </p>
   */

  public static native int SIGTERM ();

  /**
   * <p>
   *   Returns the number of the <code>SIGCHLD</code> signal.
   * </p>
   * <p>
   *   Corresponds to the C macro <code>SIGCHLD</code>.
   * </p>
   */

  public static native int SIGCHLD ();

  /**
   * <p>
   *   Returns the number of the <code>SIGCONT</code> signal.
   * </p>
   * <p>
   *   Corresponds to the C macro <code>SIGCONT</code>.
   * </p>
   */

  public static native int SIGCONT ();

  /**
   * <p>
   *   Returns the number of the <code>SIGSTOP</code> signal.
   * </p>
   * <p>
   *   Corresponds to the C macro <code>SIGSTOP</code>.
   * </p>
   */

  public static native int SIGSTOP ();

  // --------------------------------------------------------------------------------
  // Disabled constructor
  // --------------------------------------------------------------------------------

  /**
   * Private contructor, should never be called. If called anyway, throws an excpetion.
   */

  private UnixLib ()
  {
    throw new IllegalStateException
      ("Class " + UnixLib.class.getName() + " must not be instantiated");
  }
}
