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

package net.mumie.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Static utilities for {@link Throwable} objects.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: ErrorUtil.java,v 1.1 2007/01/23 01:25:17 rassy Exp $</code>
 */

public class ErrorUtil
{
  /**
   * Returns the "root" of the specified <code>Throwable</code>. This is the
   * <code>Throwable</code> that initially caused the problem.
   */

  public static Throwable unwrapThrowable (Throwable throwable)
  {
    Throwable cause;
    while ( true )
      {
	cause = throwable.getCause();
	if ( cause == null ) break;
	throwable = cause;
      }
    return throwable;
  }

  /**
   * Returns the stack trace of the specified <code>Throwable</code> as a string.
   */

  public static String getStackTrace (Throwable throwable)
  {
    StringWriter writer = new StringWriter();
    throwable.printStackTrace(new PrintWriter(writer));
    return writer.toString();
  }
}
