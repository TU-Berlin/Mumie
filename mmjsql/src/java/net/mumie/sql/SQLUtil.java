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

package net.mumie.sql;

import java.util.Date;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Static utility methods to deal with SQL code.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: SQLUtil.java,v 1.7 2009/07/24 09:48:40 rassy Exp $</code>
 */

public class SQLUtil
{
  /**
   * Escapes single quotes and backslashes in <code>string</code>. This is done by replacing
   * each quote by two quotes, and each backslash by two backslashes.
   */

  public static String quote (String string)
  {
    StringBuffer result = new StringBuffer();
    char[] chars = string.toCharArray();
    for (int i = 0; i < chars.length; i++)
      {
        switch ( chars[i] )
          {
          case '\\':
            result.append("\\\\"); break;
          case '\'':
            result.append("''"); break;
          default:
            result.append(chars[i]);
          }
      }
    return result.toString();
  }

  /**
   * Converts the specified string into a SQL literal. This means the string is quoted (see
   * method {@link #quote quote}) and enclosed in single quotes. 
   */

  public static String toSQL (String string)
  {
    return "'" + quote(string) + "'";
  }

  /**
   * Converts the specified timestamp into a SQL literal.
   */

  public static String toSQL (Timestamp timestamp)
  {
    return toSQL(timestamp.toString());
  }

  /**
   * Converts the specified date into a SQL literal.
   */

  public static String toSQL (Date date)
  {
    return toSQL(new Timestamp(date.getTime()));
  }

  /**
   * Converts the specified calendar object into a SQL literal.
   */

  public static String toSQL (Calendar calendar)
  {
    return toSQL(new Timestamp(calendar.getTimeInMillis()));
  }

  /**
   * Converts an int into a SQL literal.
   */

  public static String toSQL (int value)
  {
    return Integer.toString(value);
  }

  /**
   * Converts a boolean into a SQL literal.
   */

  public static String toSQL (boolean value)
  {
    return (value ? "true" : "false");
  }

  /**
   * Converts the specified object into a SQL literal.
   */

  public static String toSQL (Object object)
  {
    if ( object == null )
      return "NULL";
    if ( object instanceof Number )
      return object.toString();
    else if ( object instanceof Boolean )
      return toSQL(((Boolean)object).booleanValue());
    else if ( object instanceof String )
      return toSQL((String)object);
    else if ( object instanceof Timestamp )
      return toSQL((Timestamp)object);
    else if ( object instanceof Date )
      return toSQL((Date)object);
    else if ( object instanceof Calendar )
      return toSQL((Calendar)object);
    else if ( object instanceof SQLizable )
      return ((SQLizable)object).toSQL();
    else
      throw new IllegalArgumentException
        ("Can not convert to sql code: " + object);
  }
}
