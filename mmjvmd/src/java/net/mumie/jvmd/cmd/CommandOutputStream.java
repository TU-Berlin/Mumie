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

import java.io.OutputStream;
import java.io.IOException;

/**
 * <p>
 *   Represents a channel in the sense of the Jvmd output protocol. By means of this
 *   protocol, a single output stream called the <em>carrier stream</em> can be used to
 *   implement several output streams called <em>channels</em>. The protocol is block-oriented;
 *   i.e., the data is send in blocks through the carrier stream, where each block belongs
 *   to a certain channel. Each block is preceeded by a header of two <em>control
 *   bytes</em>. Control bytes are interpreted as integral numbers in the range from 0
 *   (inclusive) to 255 (inclusive). The first control byte specifies the channel. Thus,
 *   channels are identified by numbers from 0 to 255. If the channel is not 0, the second
 *   control byte specifies the length of the data block. The channel 0 is special: is
 *   signals the end of the data stream. In that case, the second control byte contains a
 *   status code; and no data block follows the control bytes.
 * </p>
 * <p>
 *   The following figure illustrates the Jvmd output protocol:
 * </p>
 * <p>
 *   <img src="doc-files/output_stream.png">
 * </p>
 * <p>
 *   The Jvmd output protocol is used to send the standard output and standard error streams
 *   of a command from the server to the client. The two streams are channels with respect
 *   to the protocol. The carrier stream is the output stream of the socket by which the
 *   client and server communicate. The standard output stream is channel 1, the standard
 *   error stream is channel 2. The exit code of the command is sent as the status code in
 *   channel 0.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: CommandOutputStream.java,v 1.4 2007/07/16 10:49:29 grudzin Exp $</code>
 */

public class CommandOutputStream extends OutputStream
{
  /**
   * The "carrier output stream".
   */

  protected OutputStream out = null;

  /**
   * The channel of this output stream.
   */

  protected int channel = -1;

  /**
   * The (maximum) length of the data blocks. Default is 64.
   */

  protected int blockLength;

  /**
   * Creates a new instance with the specified carrier stream, channel and block length. The
   * latter is the maximal length of the data blocks. This must be a value in the range of 1
   * (inclusive) to 255 (inclusive).
   */

  public CommandOutputStream (OutputStream out, int channel, int blockLength)
    throws IllegalArgumentException
  {
    if ( out == null )
      throw new IllegalArgumentException("Carrier stream null");
    if ( channel < 0 || channel > 255 )
      throw new IllegalArgumentException
        ("Channel value out of range: " + channel + " (must be in 0 ... 255)");
    if ( blockLength < 1 || blockLength > 255 )
      throw new IllegalArgumentException
        ("Block length value out of range: " + blockLength + " (must be in 1 ... 255)");
    this.out = out;
    this.channel = channel;
    this.blockLength = blockLength;
  }

  /**
   * Creates a new instance with the specified carrier stream and channel and the default
   * block length, which is 64. 
   */

  public CommandOutputStream (OutputStream out, int channel)
    throws IllegalArgumentException
  {
    this(out, channel, 64);
  }

  /**
   * <p>
   *   Writes the portion of <code>buffer</code> specified by the start position
   *   <code>offset</code> and the size <code>length</code> to the stream.
   * </p>
   * <p>
   *   For a detailed description, see {@link OutputStream#write(byte[],int,int) write)} in
   *   {@link OutputStream  OutputStream}.
   * </p>
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

    // Write the data:
    while ( length > 0 )
      {
        int segmentLength = Math.min(this.blockLength, length);
        this.out.write(channel);
        this.out.write(segmentLength);
        this.out.write(buffer, offset, segmentLength);
        offset += segmentLength;
        length -= segmentLength;
      }
  }

  /**
   * <p>
   *   Writes <code>buffer</code> to the stream.
   * </p>
   * <p>
   *   For a detailed description, see {@link OutputStream#write(byte[])} in
   *   {@link OutputStream  OutputStream}.
   * </p>
   */

  public void write (byte[] buffer)
    throws IOException, NullPointerException
  {
    this.write(buffer, 0, buffer.length);
  }

  /**
   * <p>
   *   Writes the byte specified by <code>byteValue</code> to the stream.
   * </p>
   * <p>
   *   For a detailed description, see {@link OutputStream#write(int)} in
   *   {@link OutputStream  OutputStream}.
   * </p>
   */

  public void write (int byteValue)
    throws IOException
  {
    this.out.write(channel);
    this.out.write(1);
    this.out.write(byteValue);
  }
}
