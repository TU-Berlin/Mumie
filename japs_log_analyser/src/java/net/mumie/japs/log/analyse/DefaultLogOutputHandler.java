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

package net.mumie.japs.log.analyse;

public abstract class DefaultLogOutputHandler extends AbstractLogAnalyserComponent
  implements LogOutputHandler
{
  /**
   * Wether source name is included.
   */

  protected boolean withSourceName = true;

  /**
   * Wether line numbers are included.
   */

  protected boolean withLineNumber = true;

  /**
   * Wether priority is included.
   */

  protected boolean withPriority = true;

  /**
   * Wether time is included.
   */

  protected boolean withTime = true;

  /**
   * Wether category is included.
   */

  protected boolean withCategory = true;

  /**
   * Wether uri is included.
   */

  protected boolean withUri = true;

  /**
   * Wether class name is included.
   */

  protected boolean withClassName = true;

  /**
   * Wether thread is included.
   */

  protected boolean withThread = true;

  /**
   * Wether message is included.
   */

  protected boolean withMessage = true;

  /**
   * Sets the global <code>withXxx</code> flags according to the specified values.
   */

  public void handleStart (boolean withSourceName,
                           boolean withLineNumber,
                           boolean withPriority,
                           boolean withTime,
                           boolean withCategory,
                           boolean withUri,
                           boolean withThread,
                           boolean withClassName,
                           boolean withMessage)
    throws LogOutputProcessingException 
  {
    this.withSourceName = withSourceName;
    this.withLineNumber = withLineNumber;
    this.withPriority = withPriority;
    this.withTime = withTime;
    this.withCategory = withCategory;
    this.withUri = withUri;
    this.withThread = withThread;
    this.withClassName = withClassName;
    this.withMessage = withMessage;
  }

  /**
   * Sets all global <code>withXxx</code> flags to <code>true</code>.
   */

  public void handleStart ()
    throws LogOutputProcessingException 
  {
    this.handleStart(true, true, true, true, true, true, true, true, true);
  }

  /**
   * Does nothing.
   */

  public void handleFormattedLine (int lineNumber,
                                   String priority,
                                   String time,
                                   String category,
                                   String uri,
                                   String thread,
                                   String className,
                                   String message,
                                   String highlight)
    throws LogOutputProcessingException 
  {}

  /**
   * Does nothing.
   */

  public void handleUnformattedLine (String sourceName,
                                     int lineNumber,
                                     String line)
    throws LogOutputProcessingException 
  {}

  /**
   * Does nothing.
   */

  public void handleEnd ()
    throws LogOutputProcessingException 
  {}
}