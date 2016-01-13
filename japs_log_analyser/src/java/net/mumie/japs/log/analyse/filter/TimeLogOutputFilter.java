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
import java.util.Date;
import net.mumie.japs.log.analyse.LogOutputProcessingException;

/**
 * <p>
 *   Filters by time. Only those records with timestamps in a certain period of time are passed
 *   through. Recognizes the following parameters:
 * </p>
 * <table class="genuine">
 *   <tr>
 *     <th>Name</th><th>Description</th><th>Required</th>
 *   </tr>
 *   <tr>
 *     <td>
 *       <code>start</code>
 *     </td>
 *     <td>
 *       The lower limit of the period of time, or <code>"none"</code> if the latter should
 *       have no lower limit.
 *     </td>
 *     <td>
 *       No. Default is to have no lower limit.
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>
 *       <code>end</code>
 *     </td>
 *     <td>
 *       The upper limit of the period of time, or <code>"none"</code> if the latter should
 *       have no upper limit.
 *     </td>
 *     <td>
 *       No. Default is to have no upper limit.
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>
 *       <code>time-format</code>
 *     </td>
 *     <td>
 *       The format the time strings are expected to be. See
 *       {@link SimpleDateFormat SimpleDateFormat} for the syntax.
 *     </td>
 *     <td>
 *       No. Default is <code>"(yyyy-MM-dd)&nbsp;HH:mm.ss:SSS"</code>.
 *     </td>
 *   </tr>
 * </table>
 * <p>
 *   If both <code>start</code> and <code>end</code> are specified, a record is passed
 *   through if and only if
 *     <pre> <var>start</var> &lt;= <var>time</var> &lt;= <var>end</var> </pre>
 *   holds true, where <var>start</var> and <var>end</var> are the times specified by the
 *   two parameters, and <var>time</var> is the timestamp of the record. If only
 *   <code>start</code> is specified, a record is passed through if and only if
 *     <pre> <var>start</var> &lt;= <var>time</var> </pre>
 *   holds true. If only <var>end</var> is specified, a record is passed through if and only
 *   if 
 *     <pre> <var>time</var> &lt;= <var>end</var> </pre>
 *   holds true. If neither <var>start</var> nor <var>end</var> is specified, all records
 *   are passed through.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: TimeLogOutputFilter.java,v 1.6 2007/06/11 23:50:06 rassy Exp $</code>
 */

public class TimeLogOutputFilter extends DefaultLogOutputFilter
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The time format used in the log output.
   */

  protected DateFormat timeFormat = null;

  /**
   * The beginning of the time intervall.
   */

  protected Date startTime = null;

  /**
   * The end of the time intervall.
   */

  protected Date endTime = null;

  /**
   * Wether the last checked time was inside the time intervall.
   */

  protected boolean insideTimeIntervall = false;

  // --------------------------------------------------------------------------------
  // Constructor
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>TimeLogOutputFilter</code> and sets the time format used in the log
   * output according to the default pattern, which is
   * <code>"(yyyy-MM-dd) HH:mm.ss:SSS"</code>.
   */

  public TimeLogOutputFilter ()
  {
    this.setTimeFormat("(yyyy-MM-dd) HH:mm.ss:SSS");
  }

  // --------------------------------------------------------------------------------
  // Settings (time format, start and end time)
  // --------------------------------------------------------------------------------

  /**
   * Sets the time format used in the log output.
   */

  public void setTimeFormat (String pattern)
  {
    this.timeFormat = new SimpleDateFormat(pattern);
  }

  /**
   * Sets the beginning of the time intervall.
   */

  public void setStartTime (String startTime)
    throws LogOutputProcessingException
  {
    this.startTime = this.stringToDate(startTime);
  }

  /**
   * Sets the beginning of the time intervall.
   */

  public void setStartTime (long startTime)
  {
    this.startTime = (startTime != -1 ? new Date(startTime) : null);
  }

  /**
   * Sets the beginning of the time intervall.
   */

  public void setStartTime (Date startTime)
  {
    this.startTime = startTime;
  }

  /**
   * Sets the end of the time intervall.
   */

  public void setEndTime (String endTime)
    throws LogOutputProcessingException
  {
    this.endTime = this.stringToDate(endTime);
  }

  /**
   * Sets the end of the time intervall.
   */

  public void setEndTime (long endTime)
  {
    this.endTime = (endTime != -1 ? new Date(endTime) : null);
  }

  /**
   * Sets the end of the time intervall.
   */

  public void setEndTime (Date endTime)
  {
    this.endTime = endTime;
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
    if ( name.equals("start") )
      this.setStartTime(value);
    else if ( name.equals("end") )
      this.setEndTime(value);
    else if ( name.equals("time-format") )
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
    if ( this.withTime && this.checkIfInsideTimeIntervall(time) )
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
   * Handles a unformatted log line.
   */

  public void handleUnformattedLine (String sourceName,
                                     int lineNumber,
                                     String line)
    throws LogOutputProcessingException
  {
    if ( this.withTime && this.insideTimeIntervall )
      this.handler.handleUnformattedLine(sourceName, lineNumber, line);
  }

  // --------------------------------------------------------------------------------
  // Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * If the specified string is <code>null</code> or <code>"none"</code>, returns
   * <code>null</code>, otherwise, returns the date parsed ftom the string.
   */

  protected Date stringToDate (String string)
    throws LogOutputProcessingException
  {
    try
      {
        if ( string == null || string.equals("none") )
          return null;
        else
          return this.timeFormat.parse(string);
      }
    catch (Exception exception)
      {
        throw new LogOutputProcessingException(exception);
      }
  }

  /**
   * Checks if the time specified by <code>timeString</code> is inside the interval, sets
   * {@link #insideTimeIntervall insideTimeIntervall} according to the result and returns
   * {@link #insideTimeIntervall insideTimeIntervall}.
   */

  protected boolean checkIfInsideTimeIntervall (String timeString)
    throws LogOutputProcessingException
  {
    try
      {
        Date time = this.timeFormat.parse(timeString);
        this.insideTimeIntervall =
          ( ( this.startTime == null || !time.before(this.startTime) ) &&
            ( this.endTime == null || !time.after(this.endTime) ) );
        return this.insideTimeIntervall;
      }
    catch (Exception exception)
      {
        throw new LogOutputProcessingException(exception);
      }
  }
}
