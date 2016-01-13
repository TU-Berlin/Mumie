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

package net.mumie.japs.log.analyse.util;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import net.mumie.japs.log.analyse.LogOutputProcessingException;

/**
 * Represents a formatted log line.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: FormattedLogLine.java,v 1.3 2007/06/10 19:15:07 rassy Exp $</code>
 */

public class FormattedLogLine
  implements Comparable
{
  // --------------------------------------------------------------------------------
  // Gloabl variables
  // --------------------------------------------------------------------------------

  /**
   * The source name.
   */

  protected String sourceName = null;

  /**
   * The line number.
   */

  protected int lineNumber = -1;

  /**
   * The priority.
   */

  protected String priority = null;

  /**
   * The time.
   */

  protected String time = null;

  /**
   * <p>
   *   The time, in milliseconds since 00:00, January 1, 1970 UTC.
   * </p>
   * <p>
   *  This is an auxiliary variable. It caches the time in milliseconds so it must be
   *  created only once. Initially, it is not set, i.e., has the value -1. To set it, call
   *  {@link #createTimeValue createTimeValue}.
   * </p>
   */

  protected long timeValue = -1;

  /**
   * The category.
   */

  protected String category = null;

  /**
   * The URI.
   */

  protected String uri = null;

  /**
   * The thread.
   */

  protected String thread = null;

  /**
   * The class name.
   */

  protected String className = null;

  /**
   * The message.
   */

  protected String message = null;

  /**
   * The highlight label of this record.
   */

  protected String highlight = null;

  /**
   * Attached unformatted log lines.
   */

  protected List unformattedLines = null;

  // --------------------------------------------------------------------------------
  // Constructor
  // --------------------------------------------------------------------------------

  /**
   * Creates a new instance with the specified field data.
   */

  public FormattedLogLine (String sourceName,
			   int lineNumber,
			   String priority,
			   String time,
			   String category,
			   String uri,
			   String thread,
			   String className,
			   String message,
			   String highlight)
  {
    this.sourceName = sourceName;
    this.lineNumber = lineNumber;
    this.priority = priority;
    this.time = time;
    this.category = category;
    this.uri = uri;
    this.thread = thread;
    this.className = className;
    this.message = message;
    this.highlight = highlight;    
  }

  // --------------------------------------------------------------------------------
  // Get methods for the encapsulated data
  // --------------------------------------------------------------------------------

  /**
   * Returns the source name.
   */

  public String getSourceName ()
  {
    return this.sourceName;
  }

  /**
   * Returns the line number.
   */

  public int getLineNumber ()
  {
    return this.lineNumber;
  }

  /**
   * Returns the priority.
   */

  public String getPriority ()
  {
    return this.priority;
  }

  /**
   * Returns the time.
   */

  public String getTime ()
  {
    return this.time;
  }

  /**
   * Returns the category.
   */

  public String getCategory ()
  {
    return this.category;
  }

  /**
   * Returns the uri.
   */

  public String getUri ()
  {
    return this.uri;
  }

  /**
   * Returns the thread.
   */

  public String getThread ()
  {
    return this.thread;
  }

  /**
   * Returns the class name.
   */

  public String getClassName ()
  {
    return this.className;
  }

  /**
   * Returns the message.
   */

  public String getMessage ()
  {
    return this.message;
  }

  /**
   * Returns the highlight label of this record.
   */

  public String getHighlight ()
  {
    return this.highlight;
  }

  // --------------------------------------------------------------------------------
  // Cached time value
  // --------------------------------------------------------------------------------

  /**
   * Computes the time in milliseconds since 00:00, January 1, 1970 UTC, and saves it in
   * an internal variable ({@link #timeValue timeValue}). The latter is initially not set,
   * i.e., has the value -1.
   *
   * @param timeFormat the fromat in which the time was written to the log record.
   */

  public void createTimeValue (DateFormat timeFormat)
    throws LogOutputProcessingException
  {
    if ( this.time == null )
      throw new LogOutputProcessingException
        ("Can not create time value: Log record does not contain time");
    try
      {
        this.timeValue = timeFormat.parse(this.time).getTime();
      }
    catch (Exception exception)
      {
        throw new LogOutputProcessingException(exception);
      }
  }

  /**
   * Returns the value of an internal auxiliary variable ({@link #timeValue timeValue})
   * which stores the time in milliseconds since 00:00, January 1, 1970 UTC. The latter is
   * initially not set, i.e., has the value -1. To set it, call
   * {@link #createTimeValue createTimeValue}.
   */

  public final long getTimeValue ()
  {
    return this.timeValue;
  }

  // --------------------------------------------------------------------------------
  // Comparison
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Compares this formatted log line to another according to time.
   * </p>
   */

  public int compareTo (Object object)
  {
    FormattedLogLine other = (FormattedLogLine)object;
    long thisTimeValue = this.getTimeValue();
    long otherTimeValue = other.getTimeValue();
    if ( thisTimeValue == -1 || otherTimeValue == -1 )
      throw new IllegalStateException
        ("Can not compare formatted log lines: time value not initialized");
    return
      (thisTimeValue < otherTimeValue
       ? -1
       : (thisTimeValue > otherTimeValue
          ? 1
          : 0));
  }

  // --------------------------------------------------------------------------------
  // Attached unformatted lines
  // --------------------------------------------------------------------------------

  /**
   * Adds an attached unformatted line.
   */

  public void attachUnformattedLine (UnformattedLogLine line)
  {
    if ( this.unformattedLines == null )
      this.unformattedLines = new ArrayList();
    this.unformattedLines.add(line);
  }

  /**
   * Returns the attached unformatted lines.
   */

  public UnformattedLogLine[] getAttachedUnformattedLines ()
  {
    return
      this.unformattedLines == null
      ? new UnformattedLogLine[0]
      : (UnformattedLogLine[])this.unformattedLines.toArray
          (new UnformattedLogLine[this.unformattedLines.size()]);
  }
}
