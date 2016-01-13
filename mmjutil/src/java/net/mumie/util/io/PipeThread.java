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

package net.mumie.util.io;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * A thread that redirects data from an input stream to an output stream. Data is read in
 * blocks from the input stream until the end of the stream is reached. The (maximum) block
 * size is 1024 bytes by default, but can be changed if desired. Each block is written to
 * the output stream immediately after reading. If auto-flush is turned on, the output
 * stream is flushed after each block. Default is off. In any case, the output stream is
 * flushed after the last block.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: PipeThread.java,v 1.4 2007/06/24 16:09:21 rassy Exp $</code>
 */

public class PipeThread extends Thread
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The input stream where to read from.
   */

  protected InputStream from;

  /**
   * The input stream where to write to.
   */

  protected OutputStream to;

  /**
   * The (maximum) buffer size.
   */

  protected int bufferSize;

  /**
   * Whether the output stream is flushed after each block.
   */

  protected boolean autoFlush;

  /**
   * Flag to abort the thread.
   */

  protected boolean abort = false;

  /**
   * Whether the {@link #run run} method has already finished.
   */

  protected boolean finished = false;

  /**
   * The throwable thrown in the {@link #run run} method, if any.
   */

  protected Throwable throwable = null;

  // --------------------------------------------------------------------------------
  // Run method
  // --------------------------------------------------------------------------------

  /**
   * See class description for what this method does.
   */

  public void run ()
  {
    byte[] buffer = new byte[this.bufferSize];
    int count = -1;
    try
      {
        while ( !this.abort && (count = this.from.read(buffer)) != -1 )
          {
            this.to.write(buffer, 0, count);
            if ( this.autoFlush ) this.to.flush();
          }
        this.to.flush();
      }
    catch (Throwable throwable)
      {
        this.throwable = throwable;
      }
    finally
      {
        this.finished = true;
      }
  }

  // --------------------------------------------------------------------------------
  // Other methods
  // --------------------------------------------------------------------------------

  /**
   * Returns wether the {@link #run run} has already finished.
   */

  public boolean hasFinished ()
  {
    return this.finished;
  }

  /**
   * Returns the throwable thrown in the {@link #run run} method, or null if no throwable
   * has been thrown.
   */

  public Throwable getThrowable ()
  {
    return this.throwable;
  }

  /**
   * Sets the abort flag.
   */

  public synchronized void abort ()
  {
    this.abort = true;
    this.interrupt();
  }

  /**
   * Re-throws an error occurred in the {@link #run run}. If a throwable has been thrown in
   * the {@link #run run} method, it is wrapped by an {@link IOException} and thrown by this
   * method. Otherwise, nothing is done.
   */

  public void checkError ()
    throws IOException 
  {
    if ( this.throwable != null )
      {
        IOException ioException = new IOException(this.throwable);
        this.throwable = null;
        throw ioException;
      }
  }

  // --------------------------------------------------------------------------------
  // Constructors
  // --------------------------------------------------------------------------------

  /**
   * Creates a <code>PipeThread</code> that reads from the specified input stream and writes
   * to the specified output stream. Data is read in blocks from the input stream until the
   * latter has no more data. The (maximum) block size in bytes is given by
   * <code>bufferSize</code>. Each block is written to the output stream immediately after
   * reading. <code>autoFlush</code> switches auto-flush on or off: if true, the output
   * stream is flushed after each block, otherwise not. In any case, the output stream is
   * flushed after the last block.
   *
   * @param from the input stream the data is read from
   * @param to the output stream the data is written to
   * @param bufferSize maximum block size
   * @param autoFlush whether the output stream is flushed after each block written.
   */

  public PipeThread (InputStream from,
                     OutputStream to,
                     int bufferSize,
                     boolean autoFlush)
  {
    this.from = from;
    this.to = to;
    this.bufferSize = bufferSize;
    this.autoFlush = autoFlush;
    this.start();
  }

  /**
   * <p>
   *   Creates a <code>PipeThread</code> that reads from the specified input stream and
   *   writes to the specified output stream. Data is read in blocks from the input stream
   *   until the latter has no more data. The (maximum) block size is 1024 bytes. Each block
   *   is written to the output stream immediately after reading. Auto-fluch is turned
   *   off, but the output stream is flushed after the last block.
   * </p>
   * <p>
   *   Implemented as
   *   {@link #PipeThread(InputStream,OutputStream,int,boolean) this(from, to, 1024, false)}.
   * </p>
   *
   * @param from the input stream the data is read from
   * @param to the output stream the data is written to
   */

  public PipeThread (InputStream from, OutputStream to)
  {
    this(from, to, 1024, false);
  }

}
