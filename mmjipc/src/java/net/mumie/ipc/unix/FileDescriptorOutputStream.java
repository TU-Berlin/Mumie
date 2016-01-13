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
import java.io.OutputStream;
import java.io.IOException;

/**
 * An output stream given by a file descriptor.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: FileDescriptorOutputStream.java,v 1.4 2007/07/16 11:03:26 grudzin Exp $</code>
 */

public class FileDescriptorOutputStream extends OutputStream
{
  /**
   * The file descriptor of this ouput stream.
   */

  protected int fileDescriptor;

  /**
   * Creates a new instance with the specified file descriptor
   */

  public FileDescriptorOutputStream (int fileDescriptor)
    throws IllegalStateException
  {
    if ( fileDescriptor < 0 )
      throw new IllegalStateException("Negative file descriptor: " + fileDescriptor);
    this.fileDescriptor = fileDescriptor;
  }

  /**
   * 
   */

  public void write (byte[] buffer, int offset, int length)
    throws IOException, NullPointerException, IndexOutOfBoundsException
  {
    // Check if arguments are ok:
    if ( buffer == null )
      throw new NullPointerException("write: buffer null");
    if ( offset < 0 )
      throw new IndexOutOfBoundsException("write: negtive offset: " + offset);
    if ( length < 0 )
      throw new IndexOutOfBoundsException("write: negtive length: " + length);
    if ( offset+length > buffer.length )
      throw new IndexOutOfBoundsException
        ("write: offset+length exceeds buffer size:" +
         " offset = " + offset +
         ", length = " + length +
         ", buffer size = " + buffer.length);

    // Call native write method:
    int count = UnixLib.write(this.fileDescriptor, buffer, offset, length);

    // Check if an error occured:
    if ( count < length )
      throw new IOException("write: failed to write: " + UnixLib.strerror());
  }

  /**
   * 
   */

  public void write (byte[] buffer)
    throws IOException, NullPointerException
  {
    // Check if arguments are ok:
    if ( buffer == null )
      throw new NullPointerException("write: buffer null");

    // Call native write method:
    int count = UnixLib.write(this.fileDescriptor, buffer);

    // Check if an error occured:
    if ( count < buffer.length )
      throw new IOException("write: failed to write: " + UnixLib.strerror());
  }

  /**
   * 
   */

  public void write (int byteValue)
    throws IOException
  {
    byte[] buffer = {(byte)byteValue};
    this.write(buffer);
  }
}
