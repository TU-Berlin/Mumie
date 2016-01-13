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
import java.io.File;

/**
 * Represents a Unix domain server socket. A server socket is a socket that listens for
 * connections.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: UnixServerSocket.java,v 1.4 2007/07/25 00:07:41 rassy Exp $</code>
 */

public class UnixServerSocket
{
  /**
   * The file descriptor of the socket.
   */

  protected int fileDescriptor = -1;

  /**
   * The absolute filename this socket is bound to, or <code>null</code> if this socket is
   * not bound to a filename.
   */

  protected String filename = null;

  /**
   * Whether this socket is closed.
   */

  protected boolean isClosed = false;

  /**
   * Returns the file descriptor of this socket.
   */

  public int getFileDescriptor ()
  {
    return this.fileDescriptor;
  }

  /**
   * Returns the absolute filename this socket is bound to, or <code>null</code> if this
   * socket is not bound to a filename.
   */

  public String getFilename ()
  {
    return this.filename;
  }

  /**
   * Returns whether this socket is closed.
   */

  public boolean isClosed ()
  {
    return this.isClosed();
  }

  /**
   * Creates a new Unix server socket.
   */

  public UnixServerSocket ()
    throws IOException
  {
    this.fileDescriptor = UnixLib.socket();
    if ( this.fileDescriptor == -1 )
      throw new IOException("Creating socket failed: " + UnixLib.strerror());
  }

  /**
   * Binds this server socket to a filename
   */

  public void bind (String filename)
    throws IOException
  {
    if ( UnixLib.bind(this.fileDescriptor, filename) == -1 )
      throw new IOException
        ("Binding socket to filename \"" + filename + "\" failed: " + UnixLib.strerror());
    this.filename = (new File(filename)).getAbsolutePath();
  }

  /**
   * 
   */

  public void bind (String filename, String umaskString)
    throws IOException
  {
    UnixLib.checkModeString(umaskString);
    umaskString = UnixLib.umask(umaskString);
    try
      {
        this.bind(filename);
      }
    finally
      {
        UnixLib.umask(umaskString);
      }
  }

  /**
   * Starts listening for connections.
   */

  public void listen ()
    throws IOException
  {
    if ( UnixLib.listen(this.fileDescriptor) == -1 )
      throw new IOException("Listen failed: " + UnixLib.strerror());
  }

  /**
   * Accepts a connection and returns a newly created socket corresponding to it.
   */

  public UnixSocket accept ()
    throws IOException
  {
    int newFileDescriptor = UnixLib.accept(this.fileDescriptor);
    if ( newFileDescriptor == -1 )
      throw new IOException("Accept failed: " + UnixLib.strerror());
    return new UnixSocket(newFileDescriptor);
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
    this.isClosed = true;
  }

  /**
   * Removes the file this socket is bound to.
   */

  public void remove ()
    throws IOException
  {
    if ( this.filename == null )
      throw new IOException
        ("Can not remove socket file: socket not bound to a file");
    if ( !this.isClosed )
      throw new IOException
        ("Can not remove socket file: socket is not closed");
    if ( UnixLib.remove(this.filename) == -1 )
      throw new IOException
        ("Failed to remove socket file " + this.filename + ": " + UnixLib.strerror());
    this.filename = null;
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
