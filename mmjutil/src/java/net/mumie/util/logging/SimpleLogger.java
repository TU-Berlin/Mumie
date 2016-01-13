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

package net.mumie.util.logging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.io.IOException;

/**
 * <p>
 *   A simple logger. May by used in cases where the standard logging APIs would be too
 *   involved. 
 * </p>
 * <p>
 *   All log messages are written to a file. The logger can be configured in such a way that
 *   a new file is started each time the current one gets too large. Log rotating is
 *   possible, too. See below for details.
 * </p>
 * <p>
 *   Log messages are written by means of the <code>log</code> method, which comes in two
 *   forms: {@link #log(String) log(String)} and
 *   {@link #log(String,Throwable) log(String, Throwable)}. The first form takes a message
 *   as an argument and writes it to the log file. A timestamp is added at the beginning and
 *   a newline at the end of the message; timestamp and message are separated by
 *   <code>":&nbsp;"</code>. The text written by a single call of {@link #log log} is named
 *   a <em>log record</em>. Usually, one log record is one line. The second form, 
 *   {@link #log(String,Throwable) log(String, Throwable)}, is similar, but appends the
 *   stacktrace of the specified Throwable to the message. Message and stacktrace are
 *   separated by a newline.
 * </p>
 * <p>
 *   The behaviour of the logger is controlled by the following parameters, which are passed
 *   to the contructors:
 * </p>
 * <table class="genuine">
 *  <thead>
 *    <tr>
 *      <td>Name</td>
 *      <td>Type</td>
 *      <td>description</td>
 *    </tr>
 *  </thead>
 *  <tbody>
 *    <tr>
 *      <td><code>filename</code></td>
 *      <td><code>String</code></td>
 *      <td>
 *        The name of the log file. If the output is spread over several files, each
 *        file has this name plus a number, separated by a dot.
 *      </td>
 *    </tr>
 *    <tr>
 *      <td><code>maxFileNumber</code></td>
 *      <td><code>int</code></td>
 *      <td>
 *        The maximum number of log files. -1 means unlimited. If greater then 1, log
 *        rotation is enabled. Default: 1.
 *      </td>
 *    </tr>
 *    <tr>
 *      <td><code>maxRecordNumber</code></td>
 *      <td><code>int</code></td>
 *      <td>
 *        The maximum number of records in a log file. -1 means unlimited. If greater then
 *        1, a new log file is started each time the number of records in the current file
 *        exceeds this value.  Default: -1.
 *      </td>
 *    </tr>
 *    <tr>
 *      <td><code>datePattern</code></td>
 *      <td><code>String</code></td>
 *      <td>
 *        The pattern for the timestamp in the log messages. This should be a string as
 *        described with {@link SimpleDateFormat SimpleDateFormat}. Default:
 *        <code>"yyyy-MM-dd HH:mm:ss&nbsp;S"</code>.
 *      </td>
 *    </tr>
 *  </tbody>
 * </table>
 * <p>
 *   There are three contructors: one that takes all four parameters, one that uses the
 *   default for <code>datePattern</code>, and one that uses the defaults for
 *   <code>maxFileNumber</code>, <code>maxRecordNumber</code>, and <code>datePattern</code>.
 * </p>
 * <p>
 *   Note that <code>maxFileNumber</code> is only in effect if <code>maxRecordNumber</code>
 *   is not -1. Otherwise, the length of the first log file is unlimited, so the next log
 *   file is never used.
 * </p>
 * <p>
 *   Examples:
 * </p>
 * <ol>
 *   <li>
 *     <pre>new SimpleLogger("server.log", 10, 10000)</pre>
 *     <p>
 *       Log rotating with ten log files <code>server.log.01</code>, &hellip;,
 *       <code>server.log.10</code>. The length of each log file is limited to 10000
 *       records.
 *     </p>
 *   </li>
 *   <li>
 *     <pre>new SimpleLogger("server.log")</pre>
 *     <p>
 *       One single log file <code>server.log</code>, no length limit.
 *     </p>
 *   </li>
 *   <li>
 *     <pre>new SimpleLogger("server.log", 1, 100000)</pre>
 *     <p>
 *       One single log file <code>server.log</code>. Its length is limited to 100000
 *       records. If the limit is exceeded, the log file is deleted and replaced by a new,
 *       empty file with the same name (log rotating with one log file).
 *     </p>
 *   </li>
 * </ol>
 * <p>
 *  The logger can be disabled if desired. To disable the logger, call the
 *  {@link #setDisabled setDisabled} method with <code>true</code> as argument. When the
 *  logger is disabled, the {@link #log log} method simply returns without writing
 *  anything. To re-enable the logger, call {@link #setDisabled setDisabled} with
 *  <code>false</code> as argument.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: SimpleLogger.java,v 1.2 2007/02/02 00:37:20 rassy Exp $</code>
 */

public class SimpleLogger
{
  /**
   * The name of the log file(s). In case of log rotation, a number is appended to this
   * name.
   */

  protected String filename;

  /**
   * The maximum number of log files to use.
   */

  protected int maxFileNumber;

  /**
   * The maximum length of a log file, as its number of records. -1 means unlimited.
   */

  protected int maxRecordNumber;

  /**
   * The format of the timestamp.
   */

  protected DateFormat dateFormat;

  /**
   * Whether this logger is disabled.
   */

  protected boolean disabled = false;

  /**
   * The stream the log messages go to.
   */

  protected PrintStream out = null;

  /**
   * The current log file number.
   */

  protected int fileNumber = 1;

  /**
   * The current record number.
   */

  protected int recordNumber = 0;

  /**
   * Static reference to the shared instance.
   */

  protected static SimpleLogger sharedInstance = null;

  /**
   * Creates a new logger.
   *
   * @param filename
   *   the name of the log file(s). If more than one log file is used, a number is appended
   *   to this.
   *
   * @param maxFileNumber
   *   the maximum number of log files. -1 means unlimited.
   *
   * @param maxRecordNumber
   *   the maximum number of records in a log file. -1 means unlimited.
   *
   * @param datePattern
   *   the pattern for the timestamp in the log messages. This should be a string as
   *   described with {@link SimpleDateFormat SimpleDateFormat}.
   *
   * @throws IllegalArgumentException
   *   if <code>maxFileNumber</code> is neither positive nor -1, or if
   *   <code>maxRecordNumber</code> is neither positive nor -1, or if <code>datePattern</code>
   *   is invalid.
   */

  public SimpleLogger (String filename,
                       int maxFileNumber,
                       int maxRecordNumber,
                       String datePattern)
    throws IllegalArgumentException
  {
    this.filename = filename;
    if ( maxFileNumber < 1 && maxFileNumber != -1 )
      throw new IllegalArgumentException("maxFileNumber must be positive or -1");
    this.maxFileNumber = maxFileNumber;
    if ( maxRecordNumber < 1 && maxRecordNumber != -1 )
      throw new IllegalArgumentException("maxRecordNumber must be positive or -1");
    this.maxRecordNumber = maxRecordNumber;
    this.dateFormat = new SimpleDateFormat(datePattern);
  }

  /**
   * Creates a new logger. The parameters have the same meaning as the respective parameters
   * of {@link #SimpleLogger(String,int,int,String) SimpleLogger(String,int,int,String)}.
   * The pattern for the timestamps is set to
   * <pre>"yyyy-MM-dd HH:mm:ss&nbsp;S"</pre>
   */

  public SimpleLogger (String filename,
                       int maxFileNumber,
                       int maxRecordNumber)
    throws IllegalArgumentException
  {
    this(filename, maxFileNumber, maxRecordNumber, "yyyy-MM-dd HH:mm:ss S");
  }

  /**
   * Creates a new logger. Same as
   * {@link #SimpleLogger(String,int,int) SimpleLogger(filename, 1, -1)}.
   */

  public SimpleLogger (String filename)
    throws IllegalArgumentException
  {
    this(filename, 1, -1);
  }

  /**
   * Returns the shared instance
   */

  public static synchronized SimpleLogger getSharedInstance ()
  {
    if ( sharedInstance == null )
      {
        final String PREFIX = "net.mumie.util";
        String filename = System.getProperty(PREFIX + ".logFile");
        int maxFileNumber = Integer.parseInt(System.getProperty(PREFIX + ".logMaxFiles"));
        int maxRecordNumber = Integer.parseInt(System.getProperty(PREFIX + ".logMaxRecords"));
        String datePattern = System.getProperty(PREFIX + ".logDateFormat");
        sharedInstance =
          new SimpleLogger(filename, maxFileNumber, maxRecordNumber, datePattern);
      }
    return sharedInstance;
  }

  /**
   * Auxiliary method, creates the number suffix for the log file with number
   * {@link #fileNumber fileNumber}. The suffix is composed as follows:
   * <ul>
   *   <li>
   *     If {@link #maxFileNumber maxFileNumber} is greater than 1, the suffix is a dot
   *     followed by the log file number. If necessary, the log file number contains leading
   *     zeros to get the suffixes of all log files equally long. Example: <code>".003"</code>.
   *   </li>
   *   <li>
   *     If {@link #maxFileNumber maxFileNumber} is -1, the suffix is a dot followed by the
   *     log file number. No leading zeros are added. 
   *   </li>
   *   <li>
   *     Otherwise, the suffix is the empty string.
   *   </li>
   * </ul>
   */

  protected String createSuffix ()
  {
    if ( this.maxFileNumber > 1 )
      {
        // Create a suffix with leading zeros, like "002":
        int length = Integer.toString(this.maxFileNumber).length();
        StringBuffer suffix = new StringBuffer(length+1);
        suffix.append(this.fileNumber);
        while ( suffix.length() < length)
          suffix.insert(0, '0');
        suffix.insert(0, '.');
        return suffix.toString();
      }
    else if ( this.maxFileNumber == -1 )
      return "." + Integer.toString(this.fileNumber);
    else
      return "";
  }

  /**
   * Auxiliary method, opens the log file for writing.
   */

  protected void startLogFile ()
    throws IOException
  {
    this.out = new PrintStream(new FileOutputStream(this.filename + this.createSuffix()));
    this.recordNumber = 0;
  }

  /**
   * Writes a log message. The message comprises the specified string and the stacktrace of
   * the spacified Throwable. This method is thread-save.
   */

  public synchronized void log (String message, Throwable throwable)
  {
    try
      {
        if ( this.disabled )
          return;

        if ( this.out == null )
          {
            this.fileNumber = 1;
            this.startLogFile();
          }
        else if ( this.recordNumber == this.maxRecordNumber && this.maxRecordNumber != -1 )
          {
            this.out.flush();
            this.out.close();

            this.fileNumber =
              (this.fileNumber == this.maxFileNumber && this.maxFileNumber != -1
               ? 1
               : this.fileNumber+1);

            this.startLogFile();
          }
        
        this.out.println
          (this.dateFormat.format(new Date(System.currentTimeMillis())) + ": " + message);
        if ( throwable != null )
          throwable.printStackTrace(this.out);
        this.recordNumber++;
      }
    catch (Exception exception)
      {
        // ignored
      }
  }

  /**
   * Writes a log message. This method is thread-save.
   */

  public final void log (String message)
  {
    this.log(message, null);
  }

  /**
   * Disables or re-enables this logger. This method is thread-save.
   */

  public synchronized void setDisabled (boolean disabled)
  {
    this.disabled = disabled;
  }

}
