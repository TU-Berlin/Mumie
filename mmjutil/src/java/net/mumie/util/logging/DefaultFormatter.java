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

import java.util.logging.Level;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * @deprecated should be removed
 */

public class DefaultFormatter extends Formatter
{
  /**
   * The date format used by this <code>DefaultFormatter</code>.
   */

  SimpleDateFormat dateFormat;

  /**
   * String that separates log records.
   */

  String recordSeparator;

  /**
   * Formats a log record.
   */

  public String format (LogRecord record)
  {
    Throwable throwable = record.getThrown();
    StringBuffer buffer = new StringBuffer();
    buffer
      .append("[").append(this.dateFormat.format(new Date(record.getMillis()))).append("] ")
      .append(record.getLevel().getName()).append(" ")
      .append(record.getSourceClassName()).append(" ")
      .append(record.getSourceMethodName()).append(" ")
      .append(record.getMessage());
    if ( throwable != null )
      {
	buffer.append(" ").append(throwable.getMessage());
	StackTraceElement[] stackTrace = throwable.getStackTrace();
	for (int i = 0; i < stackTrace.length; i++)
	  buffer.append("\n    at ").append(stackTrace[i]);
      }
    buffer.append(this.recordSeparator);
    return buffer.toString();
  }

  /**
   * Creates a new <code>DefaultFormatter</code>.
   */

  public DefaultFormatter ()
  {
    String dateFormatPattern = System.getProperty
      ("net.mumie.util.logging.DefaultFormatter.dateFormatPattern", "yyyy-MM-dd HH:mm:ss SSS zzz");
    this.dateFormat = new SimpleDateFormat(dateFormatPattern);
    this.recordSeparator = System.getProperty
      ("net.mumie.util.logging.DefaultFormatter.recordSeparator", "\n\n");
  }
}

