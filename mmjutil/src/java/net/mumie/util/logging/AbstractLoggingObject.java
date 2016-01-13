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
import java.util.logging.Logger;

/**
 * Base class for objects that need logging.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: AbstractLoggingObject.java,v 1.2 2007/01/11 21:56:22 rassy Exp $</code>
 *
 * @deprecated should be removed
 */

public abstract class AbstractLoggingObject
{

  /**
   * The logger used by this object.
   */

  protected Logger logger;

  /**
   * Class name that occurs in the log messages. Extending classes should set this to their
   * name.
   */

  protected String classNameForLogging = null;

  /**
   * Returns a common prefix for all log messages. This implementation always returns the
   * empty string, so prefixes are turned off.
   */

  protected String getLogMessagePrefix ()
  {
    return "";
  }

  /**
   * Sends a log message. Same as
   * <pre>
   *   this.logger.logp(level, this.classNameForLogging, methodName, this.getLogMessagePrefix() + message);
   * </pre>
   * @see java.util.logging.Logger
   * @see java.util.logging.Level
   * @see java.util.logging.Logger#logp(java.util.logging.Level,String,String,String) logp
   * @see #classNameForLogging
   * @see #getLogMessagePrefix
   */

  protected void log (Level level, String methodName,  String message)
  {
    this.logger.logp
      (level, this.classNameForLogging, methodName, this.getLogMessagePrefix() + message);
  }

  /**
   * Sends a log message. Same as
   * <pre>
   *   this.logger.logp(level, this.classNameForLogging, methodName,
   *                    this.getLogMessagePrefix() + message, throwable);
   * </pre>
   * @see java.util.logging.Logger
   * @see java.util.logging.Level
   * @see java.util.logging.Logger#logp(java.util.logging.Level,String,String,String,java.lang.Throwable) logp
   * @see #classNameForLogging
   * @see #getLogMessagePrefix
   */

  protected void log (Level level, String methodName,  String message, Throwable throwable)
  {
    this.logger.logp
      (level,
       this.classNameForLogging,
       methodName,
       this.getLogMessagePrefix() + message,
       throwable);
  }

  /**
   * Sends a log message. Same as
   * <pre>
   *   this.log(Level.INFO, methodName, message);
   * </pre>
   * @see #log(java.util.logging.Level,String,String) log
   * @see java.util.logging.Level
   * @see java.util.logging.Level#INFO
   */

  protected void log (String methodName,  String message)
  {
    this.log(Level.INFO, methodName, message);
  }

  /**
   * Creates a new <code>AbstractLoggingObject</code>
   */

  public AbstractLoggingObject ()
  {
    this.classNameForLogging = this.getClass().getName();
    this.logger = Logger.getLogger(this.classNameForLogging);
  }
}
