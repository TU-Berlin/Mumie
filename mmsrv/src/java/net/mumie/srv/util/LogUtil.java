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

package net.mumie.srv.util;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import net.mumie.srv.notions.TimeFormat;
import org.apache.avalon.framework.parameters.Parameters;

/**
 * Provides static auxiliary methods to compose log messages.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: LogUtil.java,v 1.3 2008/10/31 16:22:22 rassy Exp $</code>
 */

public class LogUtil
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The date format used for log messages.
   */

  protected static DateFormat dateFormat = null;

  // --------------------------------------------------------------------------------
  // "Ensure" methods
  // --------------------------------------------------------------------------------

  /**
   * Initializes the {@link #dateFormat} if it is <code>null</code>.
   */

  protected static void ensureDateFormat ()
  {
    if ( dateFormat == null )
      dateFormat = new SimpleDateFormat(TimeFormat.PRECISE);
  }

  // --------------------------------------------------------------------------------
  // To-string conversion methods
  // --------------------------------------------------------------------------------


  /**
   * Converts an array of objects to a string which can be inserted into log messages.
   */

  public static String arrayToString (Object[] objects)
  {
    if ( objects == null )
      return "null";
    StringBuffer buffer = new StringBuffer("{");
    for (int i = 0; i < objects.length; i++)
      {
        if ( i > 0 ) buffer.append(", ");
        buffer.append(objects[i]);
      }
    buffer.append("}");
    return buffer.toString();
  }

  /**
   * Converts an array of strings to a string which can be inserted into log messages.
   */

  public static String arrayToString (String[] strings)
  {
    if ( strings == null )
      return "null";
    StringBuffer buffer = new StringBuffer("{");
    for (int i = 0; i < strings.length; i++)
      {
        if ( i > 0 ) buffer.append(", ");
        buffer.append('"').append(strings[i]).append('"');
      }
    buffer.append("}");
    return buffer.toString();
  }

  /**
   * Converts an array of integers to a string which can be inserted into log messages.
   */

  public static String arrayToString (int[] ints)
  {
    if ( ints == null )
      return "null";
    StringBuffer buffer = new StringBuffer("{");
    for (int i = 0; i < ints.length; i++)
      {
        if ( i > 0 ) buffer.append(", ");
        buffer.append(ints[i]);
      }
    buffer.append("}");
    return buffer.toString();
  }

  /**
   * Converts an array of integers to a string which can be inserted into log messages.
   * @deprecated use {@link #arrayToString(int[]) arrayToString(int[])} instead.
   */

  public static String intArrayToString (int[] ints)
  {
    return arrayToString(ints);
  }

  /**
   * Converts the specified date to a string using
   * {@link TimeFormat#PRECISE TimeFormat.PRECISE}. 
   */

  public static String dateToString (Date date)
  {
    ensureDateFormat();
    return dateFormat.format(date);
  }

  /**
   * Interprets a long value as a time (milliseconds since 1970-01-01) and converts it to a
   * string using {@link TimeFormat#PRECISE TimeFormat.PRECISE}.
   */

  public static String timeToString (long time)
  {
    return dateToString(new Date(time));
  }

  /**
   * 
   */

  public static String parametersToString (Parameters parameters)
  {
    StringBuffer buffer = new StringBuffer("{");
    String[] names = parameters.getNames();
    for (int i = 0; i < names.length; i++)
      {
        String name = names[i];
        String value = parameters.getParameter(name, "");
        buffer.append(" " + name + "=\"" + value + "\"");
      }
    buffer.append(" }");
    return buffer.toString();
  }

  // --------------------------------------------------------------------------------
  // Logging confidential data
  // --------------------------------------------------------------------------------

  /**
   * Method to replace a confidential piece of text by a sequence of asterisks.
   */

  public static String hide (char[] chars)
  {
    char[] asterisks = new char[chars.length];
    Arrays.fill(asterisks, '*');
    return new String(asterisks);
  }

  /**
   * Method to replace a confidential piece of text by a sequence of asterisks.
   */

  public static String hide (String string)
  {
    char[] asterisks = new char[string.length()];
    Arrays.fill(asterisks, '*');
    return new String(asterisks);
  }

  // --------------------------------------------------------------------------------
  // Encoding issues
  // --------------------------------------------------------------------------------

  /**
   * Auxiliary, converts an integer to a string.
   */

  protected static String intToString (int number, int radix)
  {
    String string = Integer.toString(number, radix);
    if ( radix == 2 )
      {
        int diff = 8 - string.length();
        if ( diff > 0 )
          {
            char[] space = new char[diff];
            Arrays.fill(space, '0');
            string = new String(space) + string;
          }
      }
    return string;
  }

  /**
   * Returns a string displaying the characters of the specified string. 
   */

  public static String stringCharsToString (String string, int radix)
  {
    StringBuffer buffer = new StringBuffer ();
    char[] chars = string.toCharArray();
    for (int i = 0; i < chars.length; i++)
      {
        if ( i > 0 ) buffer.append(':');
        buffer.append(intToString((int)chars[i], radix));
      }
    return buffer.toString();
  }

  /**
   * Returns a string displaying the bytes of the specified string in the specified
   * character set (encoding).
   */

  public static String stringBytesToString (String string, String charset, int radix)
    throws UnsupportedEncodingException
  {
    StringBuffer buffer = new StringBuffer ();
    byte[] bytes = string.getBytes(charset);
    for (int i = 0; i < bytes.length; i++)
      {
        if ( i > 0 ) buffer.append(':');
        int ithByte = (bytes[i] >= 0 ? (int)bytes[i] : 256 + bytes[i]);
        buffer.append(intToString(ithByte, radix));
      }
    return buffer.toString();
  }

  // --------------------------------------------------------------------------------
  // Formatted output
  // --------------------------------------------------------------------------------

  /**
   * Returns a string made of <code>number</code> characters <code>fillChar</code>
   */

  public static String fill (char fillChar, int number)
  {
    char[] chars = new char[number];
    Arrays.fill(chars, fillChar);
    return new String(chars);
  }

  /**
   * <p>
   *   Returns a copy of the specified string with spaces inserted on the left so that the
   *   specified width is reached. If the specified width is too small, expands it so that
   *   the string fits.
   * </p>
   * <p>
   *   Examples:
   *   <pre>  flushLeft("abc", 5)</pre>
   *   returns
   *   <pre>  "  abc"</pre>
   *   whereas
   *   <pre>  flushLeft("abcde", 3)</pre>
   *   returns
   *   <pre>  "abcde"</pre>
   * </p>
   *
   * @param string the string
   * @param width the width
   */

  public static String flushLeft (String string, int width)
  {
    StringBuffer buffer = new StringBuffer(string);
    while ( buffer.length() < width )
      buffer.insert(0, ' ');
    return buffer.toString();
  }

  /**
   * <p>
   *   Returns a copy of the specified string with spaces inserted on the right so that the
   *   specified width is reached. If the specified width is too small, expands it so that
   *   the string fits.
   * </p>
   * <p>
   *   Examples:
   *   <pre>  flushRight("abc", 5)</pre>
   *   returns
   *   <pre>  "abc  "</pre>
   *   whereas
   *   <pre>  flushRight("abcde", 3)</pre>
   *   returns
   *   <pre>  "abcde"</pre>
   * </p>
   *
   * @param string the string
   * @param width the width
   */

  public static String flushRight (String string, int width)
  {
    StringBuffer buffer = new StringBuffer(string);
    while ( buffer.length() < width )
      buffer.append(' ');
    return buffer.toString();
  }

  /**
   * <p>
   *   Returns a <code>long</code> value as a string, inserts spaces on the left so that the
   *   specified width is reached. If the specified width is too small, expands it so that
   *   the number fits.
   * </p>
   * <p>
   *   Examples:
   *   <pre>  numberToStringFlushLeft(123, 5)</pre>
   *   returns
   *   <pre>  "  123"</pre>
   *   whereas
   *   <pre>  numberToStringFlushLeft(12345, 3)</pre>
   *   returns
   *   <pre>  "12345"</pre>
   * </p>
   *
   * @param number the number
   * @param width the width
   */

  public static String numberToStringFlushLeft (long number, int width)
  {
    return flushLeft(Long.toString(number), width);
  }

  /**
   * Returns the identification string of the argument, or the string <code>"null"</code> if
   * the argument is <code>null</code>.
   */

  public static String identify (Identifyable identifyable)
  {
    return
      (identifyable != null
       ? identifyable.getIdentification()
       : "null");
  }

  // --------------------------------------------------------------------------------
  // Unwrap throwables
  // --------------------------------------------------------------------------------

  /**
   * Returns the "root" of <code>throwable</code>. This is the Throwable that
   * initially caused the problem.
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
}
