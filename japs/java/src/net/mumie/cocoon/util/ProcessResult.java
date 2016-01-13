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

package net.mumie.cocoon.util;

/**
 * Comprises several information about a process. The information are:
 * <ul>
 *  <li>exit value</li>
 *  <li>output written to stdout</li>
 *  <li>output written to stderr</li>
 *  <li>time elapsed from start to end of the process</li>
 *  <li>time limit for the process</li>
 *  <li>whether the process was aborted because the time limit was reached</li>
 * </ul>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: ProcessResult.java,v 1.1 2008/08/07 15:36:03 rassy Exp $</code>
 */

public class ProcessResult
{
  /**
   * The exit value.
   */

  protected final int exitValue;

  /**
   * The standard output.
   */

  protected final String stdout;

  /**
   * The standard error output.
   */

  protected final String stderr;

  /**
   * The time the process took to execute.
   */

  protected final long time;

  /**
   * The time limit for the process.
   */

  protected final long timeLimit;

  /**
   * Whether the process was terminated because it exceeded the time limit.
   */

  protected final boolean timeLimitExceeded;

  /**
   * Returns the exit value.
   */

  public final int getExitValue ()
  {
    return this.exitValue;
  }

  /**
   * Returns the content of the standard output stream.
   */

  public final String getStdout ()
  {
    return this.stdout;
  }

  /**
   * Returns the content of the standard error stream.
   */

  public final String getStderr ()
  {
    return this.stderr;
  }

  /**
   * Returns the time the process took to execute.
   */

  public final long getTime ()
  {
    return this.time;
  }

  /**
   * Returns the time limit of the process.
   */

  public final long getTimeLimit ()
  {
    return this.timeLimit;
  }

  /**
   * Returns whether the process was terminated because it exceeded the time limit.
   */

  public boolean timeLimitExceeded ()
  {
    return this.timeLimitExceeded;
  }

  /**
   * Creates a new instance with the specified data.
   */

  public ProcessResult (int exitValue, String stdout, String stderr,
                        long time, long timeLimit, boolean timeLimitExceeded)
  {
    this.exitValue = exitValue;
    this.stdout = stdout;
    this.stderr = stderr;
    this.time = time;
    this.timeLimit = timeLimit;
    this.timeLimitExceeded = timeLimitExceeded;
  }
}