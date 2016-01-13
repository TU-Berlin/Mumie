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

package net.mumie.japs.log.analyse.investigate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import net.mumie.japs.log.analyse.LogOutputProcessingException;
import net.mumie.japs.log.analyse.serialize.AbstractLogOutputSerializer;

/**
 * Investigates the durations of certain operations.
 *
 * <p>
 *   The "operation" is defined by two regular expression. A log event with a message
 *   matching the first resp. second regular expression is regarded to be the beginning
 *   resp. end of the operation. Operations may be nested. The investigator calculates the
 *   duration of each occurrence of the operation by the timestamps of the beginning and end
 *   log events. After all log events have been processed, the investigator prints a list of
 *   all occurrences of the operation, their durations, their locations in the log files, as
 *   well as the minimum, maximum, mean, and standard deviation of the durations. The output
 *   looks like the following:
 * </p>
 * <pre>
 *      8     sitemap.log.2007-07-27.13   29688     sitemap.log.2007-07-27.13   29689
 *      4     sitemap.log.2007-07-27.13   30174     sitemap.log.2007-07-27.13   30175
 *      2     sitemap.log.2007-07-27.13   30462     sitemap.log.2007-07-27.13   30463
 *      2     sitemap.log.2007-07-27.13   30750     sitemap.log.2007-07-27.13   30751
 *      3     sitemap.log.2007-07-27.13   31038     sitemap.log.2007-07-27.13   31039
 *      3     sitemap.log.2007-07-27.13   31326     sitemap.log.2007-07-27.13   31327
 *      3     sitemap.log.2007-07-27.13   31614     sitemap.log.2007-07-27.13   31615
 *      4     sitemap.log.2007-07-27.13   32024     sitemap.log.2007-07-27.13   32025
 *      2     sitemap.log.2007-07-27.13   32312     sitemap.log.2007-07-27.13   32313
 * ----------------------------------------------------------------------------------
 * Num   :     9
 * Mean  :     3.444 
 * Max   :     8 
 * Min   :     2 
 * Sigma :     1.771</pre>
 * <p>
 *   Each line in the table before the <code>-----</code> line corresponds to one occurrence
 *   of the operation. The columns are, in that order: duration in milliseconds, log file
 *   where the operation start was found, line number in that log file, log file where the
 *   operation end was found, line number in that log file. The values after the the
 *   <code>-----</code> line are: number of occurrences of the operation, mean duration,
 *   maximum duration, minimum duration, standard deviation of the duration. All times are
 *   in milliseconds.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: TimeInvestigator.java,v 1.6 2007/08/30 22:59:26 rassy Exp $</code>
 */

public class TimeInvestigator extends AbstractLogOutputSerializer
{
  /**
   * Regular expression to match the log records that start the period of time to measure.
   */

  protected Pattern startPattern = null;

  /**
   * Regular expression to match the log records that end the period of time to measure.
   */

  protected Pattern endPattern = null;

  /**
   * The format the time field is expected to be in.
   */

  protected DateFormat timeFormat = new SimpleDateFormat("(yyyy-MM-dd) HH:mm.ss:SSS");

  /**
   * Comprises a time, a source name, and a line number. The list of measurement results
   * consists of objects of this type. The time is the elapsed time between the start and
   * end events. The source name is the name of the log file where the end event was found.
   * The line number is the position therein where the end event was found.
   */

  public static class Record
  {
    /**
     * The source name where the start expression was found.
     */

    protected String startSourceName = null;

    /**
     * The line number where the start expression was found.
     */

    protected int startLineNumber = -1;

    /**
     * The timestamp of the start expression.
     */

    protected long startTime = -1;

    /**
     * The source name where the end expression was found, or null if the end expression has
     * not been determined yet.
     */

    protected String endSourceName = null;

    /**
     * The line number where the end expression was found, or null if the end expression has
     * not been determined yet.
     */

    protected int endLineNumber = -1;

    /**
     * The timestamp of the end expression, or null if the end expression has not been
     * determined yet.
     */

    protected long endTime = -1;

    /**
     * The elapsed time, or null if the elapsed time has not been determined yet.
     */

    protected long time = -1;

    /**
     * Returns the source name where the start of the expression was found.
     */

    public String getStartSourceName ()
    {
      return this.startSourceName;
    }

    /**
     * Returns the line number where the start of the expression was found.
     */

    public int getStartLineNumber ()
    {
      return this.startLineNumber;
    }

    /**
     * Returns the timestamp of the start expression.
     */

    public long getStartTime ()
    {
      return this.time;
    }

    /**
     * Returns the source name where the end expression was found, or null if the end
     * expression has not been determined yet
     */

    public String getEndSourceName ()
    {
      return this.endSourceName;
    }

    /**
     * Returns the line number where the end expression was found, or null if the end
     * expression has not been determined yet.
     */

    public int getEndLineNumber ()
    {
      return this.endLineNumber;
    }

    /**
     * Returns the timestamp of the end expression, or -1 if the end time has not been
     * determined yet.
     */

    public long getEndTime ()
    {
      return this.time;
    }

    /**
     * Returns the elapsed time, or -1 if the elapsed time has not been determined yet.
     */

    public long getTime ()
    {
      return this.time;
    }

    /**
     * Creates a new record with the specified data
     */

    public Record (String startSourceName, int startLineNumber, long startTime)
    {
      this.startSourceName = startSourceName;
      this.startLineNumber = startLineNumber;
      this.startTime = startTime;
    }

    /**
     * 
     */

    public void enterEndExpression (String endSourceName, int endLineNumber, long endTime)
    {
      this.endSourceName = endSourceName;
      this.endLineNumber = endLineNumber;
      this.endTime = endTime;
      this.time = this.endTime - this.startTime;
    }
  }

  /**
   * Lists of "pending" start expressions. This are start expressions for which the
   * corresponding end expression has not been determined yet.
   */

  protected Map<String,Deque<Record>> startExpressions = new HashMap<String,Deque<Record>>();

  /**
   * List of results. See {@link #Record Record} for the information contained in the list
   * items.
   */

  protected List<Record> results = new ArrayList<Record>();

  /**
   * Sets a parameter.
   */

  public void setParameter (String name, String value)
    throws LogOutputProcessingException
  {
    try
      {
        if ( name.equals("target-file") )
          this.setTarget(value);
        else if ( name.equals("start") )
          this.startPattern = Pattern.compile(value);
        else if ( name.equals("end") )
          this.endPattern = Pattern.compile(value);
        else if ( name.equals("time-format") )
          this.timeFormat = new SimpleDateFormat(value);
        else
          throw new LogOutputProcessingException("Unknown parameter: " + name);
      }
    catch (Exception exception)
      {
        throw new LogOutputProcessingException(exception);
      }
  }

  /**
   * Handles a formatted line.
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
    try
      {
        if ( this.startPattern.matcher(message).find() )
          {
            Deque<Record> startExpressions = this.startExpressions.get(thread);
            if ( startExpressions == null )
              this.startExpressions.put(thread, (startExpressions = new ArrayDeque<Record>()));
            Record record = new Record(sourceName, lineNumber, this.getTime(time));
            startExpressions.addFirst(record);
          }
        else if ( this.endPattern.matcher(message).find() )
          {
            Deque<Record> startExpressions = this.startExpressions.get(thread);
            if ( startExpressions != null )
              {
                Record record = startExpressions.removeFirst();
                record.enterEndExpression(sourceName, lineNumber, this.getTime(time));
                this.results.add(record);
              }
          }
      }
    catch (Exception exception)
      {
        throw new LogOutputProcessingException(exception);
      }
  }

  /**
   * Handles the end of the log event stream.
   */

  public void handleEnd ()
  {
    if ( this.results.size() == 0 )
      {
        this.out.println("No values found");
        return;
      }

    // Get times as an array of longs:
    long[] times = this.getMeasuredTimes();

    // Number of values:
    int num = times.length;

    // Get sum, maximum and minimum:
    long sum = 0;
    long min = times[0];
    long max = times[0];
    for (long time : times)
      {
        sum += time;
        if ( time < min ) min = time;
        if ( time > max ) max = time;
      }

    // Mean value:
    double mean = (double)sum/(double)num;

    // Standard deviation:
    double sumSqrDev = 0;
    for (long time : times)
      sumSqrDev += (mean - time)*(mean - time);
    double standardDeviation = Math.sqrt(sumSqrDev/num);

    // Output:
    for (Record record : this.results)
      this.out.println
        (String.format(" %5d %29s %7d %29s %7d",
                       record.getTime(),
                       record.getStartSourceName(),
                       record.getStartLineNumber(),
                       record.getEndSourceName(),
                       record.getEndLineNumber()));
    this.out.println
      ("----------------------------------------------------------------------------------");
    this.out.println(String.format("Num   : %5d", num));
    this.out.println(String.format("Mean  : %9.3f ", mean));
    this.out.println(String.format("Max   : %5d ", max));
    this.out.println(String.format("Min   : %5d ", min));
    this.out.println(String.format("Sigma : %9.3f ", standardDeviation));
  }

  /**
   * Auxiliary method; returns the time in milliseconds since 00:00 Jan 01 1970 from the
   * specified formatted time string.
   */

  protected long getTime (String time)
    throws Exception
  {
    return this.timeFormat.parse(time).getTime();
  }

  /**
   * Returns the measured times as an array of values of type <code>long</code>.
   */

  protected long[] getMeasuredTimes ()
  {
    long[] times = new long[this.results.size()];
    int i = -1;
    for (Record record : this.results)
      times[++i] = record.getTime();
    return times;
  }
  
}
