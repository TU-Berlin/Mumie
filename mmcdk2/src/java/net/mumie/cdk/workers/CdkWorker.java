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

package net.mumie.cdk.workers;

import java.io.File;
import java.io.PrintStream;

public abstract class CdkWorker
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The working directory of this worker.
   */

  protected File directory = null;

  /**
   * Status messages are written to this.
   */

  protected PrintStream out = null;

  /**
   * Error messages are written to this.
   */

  protected PrintStream err = null;

  /**
   * Flag indicating that the worker should be stopped.
   */

  protected boolean stop = false;

  /**
   * If true, messages except warnings are suppressed.
   */

  protected boolean quiet = false;

  // --------------------------------------------------------------------------------
  // Messages
  // --------------------------------------------------------------------------------

  /**
   * Prints a message to the output stream of this worker ({@link #out out}) and flushes
   * the stream.. If {@link #quiet quiet} is true or {@link #out out} is null, does nothing
   * (simply returns).
   */

  protected void msg (String message)
  {
    if ( this.quiet || this.out == null )
      return;
    this.out.print(message);
    this.out.flush();
  }

  /**
   * Prints a message to the output stream of this worker ({@link #out out}) and adds a
   * newline. If {@link #quiet quiet} is true or {@link #out out} is null, does nothing
   * (simply returns).
   */

  protected void msgln (String message)
  {
    if ( this.quiet || this.out == null )
      return;
    this.out.println(message);
  }

  /**
   * Prints a newline to the output stream of this worker ({@link #out out}). If
   * {@link #quiet quiet} is true or  {@link #out out} is null, does nothing (simply
   * returns).
   */

  protected void msgln ()
  {
    if ( this.quiet || this.out == null )
      return;
    this.out.println();
  }

  /**
   * Prints a warning to the output stream. A newline is appended automatically.
   */

  protected void warn (String message)
  {
    this.out.println(message);
  }

  // --------------------------------------------------------------------------------
  // Stopping
  // --------------------------------------------------------------------------------

  /**
   * Stops (or tries to stop) this worker. This implementation sets the {@link #stop stop}
   * flag to <code>true</code>.
   */

  public synchronized void stop ()
  {
    this.stop = true;
  }

  /**
   * Throws a {@link CdkWorkerStoppedException CdkWorkerStoppedException} if the
   * {@link #stop stop} flag is set.
   */

  protected void checkStop ()
    throws CdkWorkerStoppedException
  {
    if ( this.stop )
      throw new CdkWorkerStoppedException ();
  }

  // --------------------------------------------------------------------------------
  // Setup and reset
  // --------------------------------------------------------------------------------

  /**
   * Sets working directory, the output and error streams and the {@link #quiet quiet}
   * flag. The {@link #stop stop} flag is set to false.
   */

  public void setup (File directory, PrintStream out, PrintStream err, boolean quiet)
  {
    this.directory = directory;
    this.out = out;
    this.err = err;
    this.quiet = quiet;
    this.stop = false;
  }

  /**
   * Resets this worker for later reuse.
   */

  public void reset ()
  {
    this.directory = null;
    this.out = null;
    this.err = null;
    this.quiet = false;
    this.stop = false;
  }
}
