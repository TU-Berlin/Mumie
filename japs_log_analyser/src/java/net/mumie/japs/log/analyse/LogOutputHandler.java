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

/**
 * <p>
 *   A component that handles log output events.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: LogOutputHandler.java,v 1.4 2006/11/09 00:19:12 rassy Exp $</code>
 */

public interface LogOutputHandler extends LogAnalyserComponent
{
  /**
   * Handles the beginning of the log output. The parameters specify which fields are
   * included and which are not. The contract is that the handler ignores all fields for
   * which the corresponding parameter is <code>false</code>.
   */

  public void handleStart (boolean withLineNumbers,
                           boolean withSourceName,
                           boolean withPriority,
                           boolean withTime,
                           boolean withCategory,
                           boolean withUri,
                           boolean withThread,
                           boolean withClassName,
                           boolean withMessage)
    throws LogOutputProcessingException;

  /**
   * Handles the beginning of the log output. The handler should process all fields.
   */

  public void handleStart ()
    throws LogOutputProcessingException;

  /**
   * Handles a formatted log line.
   */

  public void handleFormattedLine (String sourceName,
                                   int lineNumber,
                                   String priority,
                                   String time,
                                   String category,
                                   String uri,
                                   String thread,
                                   String className,
                                   String message,
                                   String highlight)
    throws LogOutputProcessingException;

  /**
   * Handles an unformatted log line.
   */

  public void handleUnformattedLine (String sourceName,
                                     int lineNumber,
                                     String line)
    throws LogOutputProcessingException;

  /**
   * Handles the end of the log output.
   */

  public void handleEnd ()
    throws LogOutputProcessingException;

  /**
   * Sets a parameter.
   */

  public void setParameter (String name, String value)
    throws LogOutputProcessingException;
}
