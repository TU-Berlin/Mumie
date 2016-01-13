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

package net.mumie.japs.client;

import java.io.PrintStream;
import java.text.DateFormat;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

/**
 * <p>
 *   A {@link CookieEnabledJapsClient} that performs logging.
 * </p>
 * <p>
 *   The log messages are written to a {@link PrintStream} that must be specified in the
 *   constructor call. This can be done directly, or indirectly by giving a file or a
 *   filename. In the latter cases, a {@link PrintStream} is created that wrappes a
 *   {@link FileOutputStream} associated with the file (so the log messages are written to
 *   the file).
 * </p>
 * <p>
 *   Each log message has the form<pre>
 *     "JapsClient: <var>timestamp</var>: <var>message</var>"</pre>
 *   where <var>timestamp</var> is the current timestamp and <var>message</var> the actual
 *   message. The format of <var>timestamp</var> is controlled by a pattern as
 *   described in <em>"Date and Time Patterns"</em> in the API documentation of
 *   {@link SimpleDateFormat}. The default pattern is given by the static constant
 *   {@link #DEFAULT_TIMESTAMP_PATTERN}, which has the value<pre>
 *     "yyyy-MM-dd HH:mm:ss S"</pre>
 *   This can be overwritten by the system property<pre>
 *     <var>class_qname</var>.timestampPattern</pre>
 *   where <var>class_qname</var> is the fully qualified name of this class. 
 * </p>
 * 
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: LoggingCookieEnabledJapsClient.java,v 1.3 2007/07/16 11:15:37 grudzin Exp $</code>
 */

public abstract class LoggingCookieEnabledJapsClient extends CookieEnabledJapsClient
{
  /**
   * The common prefix of the log messages (<code>"JapsClient"</code>).
   */

  protected static final String LOG_PREFIX = "JapsClient";

  /**
   * The default timestamp pattern (<code>"yyyy-MM-dd HH:mm:ss S"</code>)
   */

  protected static final String DEFAULT_TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss S";

  /**
   * The print stream that recieves the log messages.
   */

  protected PrintStream logStream = null;

  /**
   * The format of timestamps.
   */

  protected DateFormat timestampFormat = null;

  /**
   * Writes a log message to {@link #logStream}. The message has the form<pre>
   *   "JapsClient: <var>timestamp</var>: <var>message</var>"</pre>
   * where <var>timestamp</var> is the current timestamp and <var>message</var> the string
   * passed to the method.
   */

  protected void log (String message)
  {
    try
      {
        logStream.println
          (LOG_PREFIX + ": " +
           this.timestampFormat.format(new Date(System.currentTimeMillis())) + ": " +
           message);
      }
    catch (Exception exception)
      {
        System.err.println("WARNING: Problem with logging: " + exception);
      }
  }

  /**
   * Creates a new <code>LoggingCookieEnabledJapsClient</code> that writes its log messages
   * to the stream <code>logStream</code>.
   */

  public LoggingCookieEnabledJapsClient (String urlPrefix, PrintStream logStream)
  {
    super(urlPrefix);
    this.logStream = logStream;
    String timestampPattern = System.getProperty
      (this.getClass().getName() + ".timestampPattern", DEFAULT_TIMESTAMP_PATTERN);
    this.timestampFormat = new SimpleDateFormat(timestampPattern);
  }

  /**
   * Creates a new <code>LoggingCookieEnabledJapsClient</code> that writes its log messages
   * to the file <code>logFile</code>.
   */

  public LoggingCookieEnabledJapsClient (String urlPrefix, File logFile)
    throws FileNotFoundException 
  {
    this(urlPrefix, new PrintStream(new FileOutputStream(logFile)));
  }

  /**
   * Creates a new <code>LoggingCookieEnabledJapsClient</code> that writes its log messages
   * to the file with the name <code>logFilename</code>.
   */

  public LoggingCookieEnabledJapsClient (String urlPrefix, String logFilename)
    throws FileNotFoundException 
  {
    this(urlPrefix, new File(logFilename));
  }
}
