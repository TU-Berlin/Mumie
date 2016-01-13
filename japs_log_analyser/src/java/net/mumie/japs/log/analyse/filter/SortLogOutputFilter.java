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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import net.mumie.japs.log.analyse.LogOutputProcessingException;
import net.mumie.japs.log.analyse.util.FormattedLogLine;
import net.mumie.japs.log.analyse.util.UnformattedLogLine;

/**
 * <p>
 *   Sorts the log records by time. Recognizes the following parameters:
 * </p>
 * <table class="genuine">
 *   <tr>
 *     <th>Name</th><th>Description</th><th>Required</th>
 *   </tr>
 *   <tr>
 *     <td>
 *       <code>time-format</code>
 *     </td>
 *     <td>
 *       Format the time field is expected to be in.
 *     </td>
 *     <td>
 *       No. Defaults to <code>"(yyyy-MM-dd) HH:mm.ss:SSS"</code>.
 *     </td>
 *   </tr>
 * </table>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: SortLogOutputFilter.java,v 1.4 2007/06/11 23:50:06 rassy Exp $</code>
 */

public class SortLogOutputFilter extends DefaultLogOutputFilter
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The time format used in the log output.
   */

  protected DateFormat timeFormat = null;

  /**
   * Represents the complete log output as a list of
   * {@link FormattedLogLine FormattedLogLine}s with attached
   * {@link UnformattedLogLine UnformattedLogLine}s.
   */

  protected List logRecords = new ArrayList();

  /**
   * The last {@link FormattedLogLine FormattedLogLine} added to the
   * {@link #logRecords logRecords}.
   */

  protected FormattedLogLine lastLogRecord = null;

  // --------------------------------------------------------------------------------
  // Constructor
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>SortLogOutputFilter</code> and sets the time format according to
   * the default pattern, which is <code>"(yyyy-MM-dd) HH:mm.ss:SSS"</code>.
   */

  public SortLogOutputFilter ()
  {
    this.setTimeFormat("(yyyy-MM-dd) HH:mm.ss:SSS");
  }

  // --------------------------------------------------------------------------------
  // Settings (time format)
  // --------------------------------------------------------------------------------

  /**
   * Sets the time format used in the log output.
   */

  public void setTimeFormat (String pattern)
  {
    this.timeFormat = new SimpleDateFormat(pattern);
  }

  // --------------------------------------------------------------------------------
  // Parameterization
  // --------------------------------------------------------------------------------

  /**
   * Sets a parameter.
   */

  public void setParameter (String name, String value)
    throws LogOutputProcessingException
  {
    if ( name.equals("time-format") )
      this.setTimeFormat(value);
    else
      throw new LogOutputProcessingException("Unknown parameter: " + name);
  }

  // --------------------------------------------------------------------------------
  // Implementation of the handler methods
  // --------------------------------------------------------------------------------

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
    throws LogOutputProcessingException
  {
    this.lastLogRecord = new FormattedLogLine
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
    this.lastLogRecord.createTimeValue(this.timeFormat);
    this.logRecords.add(this.lastLogRecord);
  }

  /**
   * Handles a unformatted log line.
   */

  public void handleUnformattedLine (String sourceName,
                                     int lineNumber,
                                     String line)
    throws LogOutputProcessingException
  {
    if ( this.lastLogRecord == null )
      this.handler.handleUnformattedLine(sourceName, lineNumber, line);
    else
      this.lastLogRecord.attachUnformattedLine
        (new UnformattedLogLine(sourceName, lineNumber, line));
  }

  /**
   * Sorts the log output and sends it to the subsequent handler.
   */

  public void handleEnd ()
    throws LogOutputProcessingException
  {
    try
      {
        Collections.sort(this.logRecords);
        Iterator iterator = this.logRecords.iterator();
        while ( iterator.hasNext() )
          {
            FormattedLogLine logRecord = (FormattedLogLine)(iterator.next());
            this.handler.handleFormattedLine
              (logRecord.getSourceName(),
               logRecord.getLineNumber(),
               logRecord.getPriority(),
               logRecord.getTime(),
               logRecord.getCategory(),
               logRecord.getUri(),
               logRecord.getThread(),
               logRecord.getClassName(),
               logRecord.getMessage(),
               logRecord.getHighlight());
            UnformattedLogLine[] unformattedLines =
              logRecord.getAttachedUnformattedLines();
            for (int i = 0; i < unformattedLines.length; i++)
              {
                this.handler.handleUnformattedLine
                  (unformattedLines[i].getSourceName(),
                   unformattedLines[i].getLineNumber(),
                   unformattedLines[i].getLine());
              }
          }
        this.handler.handleEnd();
      }
    catch(Exception exception)
      {
        throw new LogOutputProcessingException(exception);
      }
  }
}
