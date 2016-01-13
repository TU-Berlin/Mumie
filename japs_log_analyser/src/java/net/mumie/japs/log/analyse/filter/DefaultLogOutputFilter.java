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

package net.mumie.japs.log.analyse.filter;

import net.mumie.japs.log.analyse.DefaultLogOutputHandler;
import net.mumie.japs.log.analyse.LogOutputHandler;
import net.mumie.japs.log.analyse.LogOutputProcessingException;

/**
 * Base class for implementing {@link LogOutputFilter LogOutputFilter}s.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: DefaultLogOutputFilter.java,v 1.4 2006/06/07 15:57:52 rassy Exp $</code>
 */

public class DefaultLogOutputFilter extends DefaultLogOutputHandler
  implements LogOutputFilter
{
  /**
   * The handler that recieves the filtered output.
   */

  protected LogOutputHandler handler = null;

  /**
   * Sets the global {@link #handler handler} variable to the specified handler.
   */

  public void setHandler (LogOutputHandler handler)
  {
    this.handler = handler;
  }

  /**
   * Calls the <code>handleStart</code> method of the super-class and of the handler that
   * recieves the filtered ouput, both with the arguments passed to this method.
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
    // Set all this.withXxx variables to the values passed to this method:
    this.withSourceName = withSourceName;
    this.withLineNumber = withLineNumber;
    this.withPriority = withPriority;
    this.withTime = withTime;
    this.withCategory = withCategory;
    this.withUri = withUri;
    this.withThread = withThread;
    this.withClassName = withClassName;
    this.withMessage = withMessage;

    // Call the handleStart method of the subsequent component; passing the values of the
    // this.withXxx variables:
    this.handler.handleStart
      (this.withSourceName,
       this.withLineNumber,
       this.withPriority,
       this.withTime,
       this.withCategory,
       this.withUri,
       this.withThread,
       this.withClassName,
       this.withMessage);
  }

  /**
   * Calls the <code>handleFormattedLine</code> method of the handler that recieves the
   * filtered ouput, with the arguments passed to this method.
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
    throws LogOutputProcessingException 
  {
    this.handler.handleFormattedLine
      (sourceName,
       lineNumber,
       priority,
       time,
       category,
       uri,
       thread,
       className,
       message,
       highlight);
  }

  /**
   * Calls the <code>handleUnformattedLine</code> method of the handler that recieves the
   * filtered ouput, with the arguments passed to this method.
   */

  public void handleUnformattedLine (String sourceName,
                                     int lineNumber,
                                     String line)
    throws LogOutputProcessingException 
  {
    this.handler.handleUnformattedLine(sourceName, lineNumber, line);
  }

  /**
   * Calls the <code>handleEnd</code> method the handler that recieves the filtered ouput.
   */

  public void handleEnd ()
    throws LogOutputProcessingException 
  {
    this.handler.handleEnd();
  }
}