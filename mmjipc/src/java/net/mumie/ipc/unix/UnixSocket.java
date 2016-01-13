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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Represents a Unix domain socket.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: UnixSocket.java,v 1.3 2007/07/16 11:03:26 grudzin Exp $</code>
 */

public class UnixSocket
{
  /**
   * The file descriptor of the socket.
   */

  protected int fileDescriptor = -1;

  /**
   * Returns the file descriptor of this socket.
   */

  public int getFileDescriptor ()
  {
    return this.fileDescriptor;
  }

  /**
   * Creates a new, unconnected Unix socket.
   */

  public UnixSocket ()
    throws IOException
  {
    this.fileDescriptor = UnixLib.socket();
    if ( this.fileDescriptor == -1 )
      throw new IOException("Creating socket failed: " + UnixLib.strerror());
  }

  /**
   * Creates a new Unix socket with the specified file descriptor.
   */

  public UnixSocket (int fileDescriptor)
  {
    this.fileDescriptor = fileDescriptor;
  }

  /**
   * Connects this socket to the server socket with the specified filename.
   */

  public void connect (String filename)
    throws IOException
  {
    if ( UnixLib.connect(this.fileDescriptor, filename) != 0 )
      throw new IOException
        ("Failed to connect file descriptor " + this.fileDescriptor +
         " to " + filename + ": " + UnixLib.strerror());
  }

  /**
   * Closes this socket.
   */

  public void close ()
    throws IOException
  {
    if ( UnixLib.close(this.fileDescriptor) != 0 )
      throw new IOException
        ("Failed to close file descriptor " + this.fileDescriptor + ": " + UnixLib.strerror());
  }

  /**
   * Returns an input stream to read from this socket.
   */

  public InputStream getInputStream ()
  {
    return new FileDescriptorInputStream(this.fileDescriptor);
  }

  /**
   * Returns an output stream to write on this socket.
   */

  public OutputStream getOutputStream ()
  {
    return new FileDescriptorOutputStream(this.fileDescriptor);
  }

  /**
   * Sets the permissions of the this socket according to the specified boolean flags.
   *
   * @param userRead whether the owner of the socket can read it
   * @param userWrite whether the owner of the socket can write it
   * @param userExecute whether the owner of the socket can execute it
   * @param groupRead whether the group of the socket can read it
   * @param groupWrite whether the group of the socket can write it
   * @param groupExecute whether the group of the socket can execute it
   * @param othersRead whether others of the socket can read it
   * @param othersWrite whether others of the socket can write it
   * @param othersExecute whether others of the socket can execute it
   *
   * @throws IOException if the permissions can not be set
   */

  public void setPermissions (boolean userRead,
                              boolean userWrite,
                              boolean userExecute,
                              boolean groupRead,
                              boolean groupWrite,
                              boolean groupExecute,
                              boolean othersRead,
                              boolean othersWrite,
                              boolean othersExecute)
    throws IOException
  {
    if ( UnixLib.chmod
           (this.fileDescriptor,
            userRead, userWrite, userExecute,
            groupRead, groupWrite, groupExecute,
            othersRead, othersWrite, othersExecute)
         == -1 )
      throw new IOException("Failed to set permissions: " + UnixLib.strerror());
  }
}
