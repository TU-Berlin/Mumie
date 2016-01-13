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

import java.io.InputStream;
import java.io.IOException;

/**
 * An input stream given by a file descriptor.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: FileDescriptorInputStream.java,v 1.4 2007/07/16 11:03:26 grudzin Exp $</code>
 */

public class FileDescriptorInputStream extends InputStream
{
  /**
   * The file descriptor of this input stream.
   */

  protected int fileDescriptor;

  /**
   * Creates a new instance with the specified file descriptor
   */

  public FileDescriptorInputStream (int fileDescriptor)
    throws IllegalStateException
  {
    if ( fileDescriptor < 0 )
      throw new IllegalStateException("Negative file descriptor: " + fileDescriptor);
    this.fileDescriptor = fileDescriptor;
  }

  /**
   * <p>
   *   Reads up to <code>length</code> bytes and writes them to the portion of the specified
   *   buffer starting at the index <code>offset</code>.
   * </p>
   * <p>
   *   For a detailed specification, see
   *  {@link InputStream#read(byte[],int,int) read(byte[],int,int)} in 
   *  {@link InputStream InputStream}.
   * </p>
   */

  public int read (byte[] buffer, int offset, int length)
    throws IOException, NullPointerException, IndexOutOfBoundsException
  {
    // Check if arguments are ok:
    if ( buffer == null )
      throw new NullPointerException("read: buffer null");
    if ( offset < 0 )
      throw new IndexOutOfBoundsException("read: negtive offset: " + offset);
    if ( length < 0 )
      throw new IndexOutOfBoundsException("read: negtive length: " + length);
    if ( offset+length > buffer.length )
      throw new IndexOutOfBoundsException
        ("read: offset+length exceeds buffer size:" +
         " offset = " + offset +
         ", length = " + length +
         ", buffer size = " + buffer.length);

    // Call native read method:
    int count = UnixLib.read(this.fileDescriptor, buffer, offset, length);

    // Check if an error occured:
    if ( count == -1 )
      throw new IOException("read: failed to read: " + UnixLib.strerror());

    // Return number of bytes read, or -1 if end of source is reached:
    return (count > 0 ? count : -1);
  }

  /**
   * <p>
   *   Reads several bytes and writes them to the specified buffer.
   * </p>
   * <p>
   *   For a detailed specification, see
   *  {@link InputStream#read(byte[]) read(byte[])} in 
   *  {@link InputStream InputStream}.
   * </p>
   */

  public int read (byte[] buffer)
    throws IOException, NullPointerException
  {
    // Check if buffer is not null:
    if ( buffer == null )
      throw new NullPointerException("read: buffer null");

    // Call native read method:
    int count = UnixLib.read(this.fileDescriptor, buffer);

    // Check if an error occured:
    if ( count == -1 )
      throw new IOException("read: failed to read: " + UnixLib.strerror());

    // Return number of bytes read, or -1 if end of source is reached:
    return (count > 0 ? count : -1);
  }

  /**
   * <p>
   *   Reads one byte and returns it.
   * </p>
   * <p>
   *   For a detailed specification, see the
   *  {@link InputStream#read() read()} in 
   *  {@link InputStream InputStream}.
   * </p>
   */

  public int read ()
    throws IOException
  {
    byte[] buffer = new byte[1];
    int count = this.read(buffer);
    if ( count == 1 )
      {
        int value = (int)buffer[0];
        if ( value < 0 ) value+=256;
        return value;
      }
    else
      return -1;
  }
}
