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

package net.mumie.xslt.ext;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;

/**
 * <p>
 *   Provides static XSLT extension functions related to SQL.
 * </p>
 *
 * @author  Tilman Rassy
 *
 * @version <span class="file">$Id: SQLUtil.java,v 1.8 2005/05/27 12:14:00 rassy Exp $</span>
 */

public class SQLUtil
{
  /**
   * The {@link java.util.regexp.Pattern Pattern} used by {@link #quote quote} to protect
   * the SQL special characters <code>"\"</code> and <code>"'"</code>.
   */

  protected static Pattern quotePattern = null;

  /**
   * Escapes single quotes and backslashes in <code>string</code>. 
   * This is done by replacing each single quote by two single quotes and
   * each single backslash by two single backslashes.
   */

  public static String quote (String string)
  {
    try
      {
	if ( quotePattern == null ) quotePattern = Pattern.compile("\\\\|'");
	return quotePattern.matcher(string).replaceAll("$0$0");
      }
    catch (Exception exception)
      {
	throw new RuntimeException(exception);
      }
  }

  /**
   * Returns <code>string</code> as an SQL string literal.
   */

  public static String stringLiteral (String string)
  {
    return "'" + quote(string) + "'";
  }
}
