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

package net.mumie.jvmd.cmd;

import java.io.InputStream;
import java.io.IOException;

/**
 * <p>
 *   An input stream implementing the Jvmd input protocol. This protocol can be specified as
 *   follows: Data is sent in blocks of more than 0 and up to 255 bytes. Each block is
 *   preceeeded by a <em>control byte</em> which contains the length of the data block. A 
 *   control byte with the value 0 signals the end of the stream. Control bytes are
 *   interpreted as integral numbers in the range from 0 (inclusive) to 255 (inclusive). 
 * </p>
 * <p>
 *   The following figure illustrates the Jvmd input protocol:
 * </p>
 * <p>
 *   <img src="doc-files/input_stream.png">
 * </p>
 * <p>
 *   A <code>CommandInputStream</code> wraps another input stream, which is assumed to
 *   respect the Jvmd input protocol as described above. The <code>CommandInputStream</code>
 *   decodes the protocol and allows normal access to the data via the <code>read</code>
 *   methods.
 * </p>
 * <p>
 *   The Jvmd input protocol is used to send the standard input of a command from the client
 *   to the server. The transmission goes through the socket by which the client and server
 *   communicate. At the server side, the socket input stream is wrapped by a 
 *   <code>CommandInputStream</code>, which becomes the standard input stream of the
 *   command.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: CommandInputStream.java,v 1.3 2007/07/16 10:49:29 grudzin Exp $</code>
 */

public class CommandInputStream extends InputStream
{
  /**
   * The underlying input stream.
   */

  protected InputStream in = null;

  /**
   * Byte array containing the current data block. The data block may not comprise the
   * entire array. Only the part starting at index {@link #blockStart blockStart} with
   * length {@link #blockLength blockLength} is the data block.
   */

  protected byte[] block = new byte[255];

  /**
   * Current length of the current data block.
   */

  protected int blockLength = 0;

  /**
   * The current position in the current data block.
   */

  protected int blockStart = -1;

  /**
   * Flag indicating that the end of the stream has been reached.
   */

  protected boolean endOfStream = false;

  /**
   * Creates a new instance with the spacified underlying input stream.
   */

  public CommandInputStream (InputStream in)
    throws IllegalArgumentException
  {
    if ( in == null )
      throw new IllegalArgumentException("Underlying input stream null");
    this.in = in;
  }

  /**
   * Reads the next data block from the underlying stream and stores it in
   * {@link #block block}.
   */

  protected void nextBlock ()
    throws IOException
  {
    // Get length of next block from control byte:
    this.blockLength = this.in.read();
    if ( this.blockLength == -1 )
      throw new IOException("Missing control byte (unexpected end of stream)");

    // Set endOfStream flag if control byte was 0:
    this.endOfStream = ( this.blockLength == 0 );

    // Read next block:
    int length = this.blockLength;
    int count = 0;
    int offset = 0;
    while ( length > 0 )
      {
        count = this.in.read(this.block, offset, length);
        if ( count == -1 )
          throw new IllegalArgumentException
            ("Could not read next block (unexpected end of stream)");
        length-=count;
        offset+=count;
      }

    // Reset block position:
    this.blockStart = 0;
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

    // If end of stream is reached, return -1:
    if ( this.endOfStream )
      return -1;

    int count = 0;
    int total = 0;
    while ( length > 0 )
      {
        // Read next block if necessary:
        if ( this.blockLength == 0 )
          {
            this.nextBlock();
            // Exit loop if end of stream is reached:
            if ( this.endOfStream ) break;
          }

        // Copy data from block to buffer:
        count = Math.min(this.blockLength, length);
        System.arraycopy(this.block, this.blockStart, buffer, offset, count);
        this.blockLength-=count;
        this.blockStart+=count;
        length-=count;
        offset+=count;
        total+=count;
      }

    return total;
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
    return this.read(buffer, 0, buffer.length);
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
    return (count == 1 ? buffer[0] : -1);
  }
}
