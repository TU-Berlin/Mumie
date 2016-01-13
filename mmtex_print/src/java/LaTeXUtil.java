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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;

/**
 * <p>
 *   Provides static XSLT extension functions related to LaTeX.
 * </p>
 *
 * @author  Tilman Rassy
 * @version <span class="file">$Id: LaTeXUtil.java,v 1.1 2007/09/13 21:57:41 rassy Exp $</span>
 */

public class LaTeXUtil
{
  /**
   * Whether double quotes (<code>"</code>) should be quoted by {@link #quote} as well or
   * not.
   */

  protected static boolean quoteDoubleQuotes = false;

  /**
   * Matches any sequence of whitespaces. Defined by the regular expression
   * <code>"\s+"</code>.
   */

  /**
   * Get method for {@link #quoteDoubleQuotes quoteDoubleQuotes}.
   */

  public static boolean getQuoteDoubleQuotes ()
  {
    return quoteDoubleQuotes;
  }

  /**
   * Set method for {@link #quoteDoubleQuotes quoteDoubleQuotes}.
   */

  public static void setQuoteDoubleQuotes (boolean quoteDoubleQuotes_new)
  {
    quoteDoubleQuotes = quoteDoubleQuotes_new;
  }


  protected static Pattern whitespacePattern = Pattern.compile("\\s+");

  /**
   * Escapes the special characters <code>{}$&#_^~\"</code>.
   */

  public static String quote (String string)
  {
    char[] chars = string.toCharArray();
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < chars.length; i++)
      {
        char c = chars[i];
        switch ( c )
          {
          case '{' : ;
          case '}' : ;
          case '$' : ;
          case '&' : ; 
          case '#' : ; 
          case '_' : ; 
          case '%' :
            buffer.append('\\').append(c); break;
          case '\\' :
            buffer.append("\\ensuremath{\\backslash}"); break;
          case '^' :
            buffer.append("\\symbol{94}"); break;
          case '~' :
            buffer.append("\\symbol{126}"); break;
          case '"' :
            if ( quoteDoubleQuotes )
              {
                buffer.append("\\symbol{125}");
                break;
              }
          default :
            buffer.append(c);
          }
      }
    return buffer.toString();
  }

  /**
   * Replaces any sequence of whitespaces by a single <code>' '</code> (space) character.
   */

  public static String normalizeWhitespaces (String string)
  {
    Matcher matcher = whitespacePattern.matcher(string);
    return matcher.replaceAll(" ");
  }

  /**
   * Replaces any sequence of whitespaces by a single <code>' '</code> (space) character and
   * quotes the spacial characters <code>{}$&#_^~\"</code>.
   */

  public static String processText (String text)
  {
    return quote(normalizeWhitespaces(text));
  }

  /**
   * Generates a LaTeX table format string.
   */

  public static String generateTableFormat (int cols)
  {
    StringBuffer format = new StringBuffer("|");
    for (int i = 0; i < cols; i++)
      format.append("c|");
    return format.toString();
  }

  /**
   * 
   */

  public static String processPreformattedText (String text)
  {
    StringBuffer output = new StringBuffer();
    for (char c : text.toCharArray())
      {
        switch ( c )
          {
          case ' ':
            output.append("~");
            break;
          case '~':
            output.append("~~~~");
            break;
          case '\n':
            output.append("\\\\\n");
            break;
          default:
            output.append(c);
          }
      }
    return output.toString();
  }

}
