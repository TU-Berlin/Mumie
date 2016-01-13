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

package net.mumie.cocoon.sitemap;

/**
 * Indicates an error while parsing the parameter block of a sitemap autocoding function.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: ParamBlockParseException.java,v 1.2 2007/07/11 15:38:50 grudzin Exp $</code>
 */

public class ParamBlockParseException extends Exception
{
  /**
   * Creates a new <code>ParamBlockParseException</code> with message
   * <code>description</code>.
   */

  public ParamBlockParseException (String description)
  {
    super(description);
  }

  /**
   * Creates a new <code>ParamBlockParseException</code> that wraps
   * <code>throwable</code>.
   */

  public ParamBlockParseException (Throwable throwable)
  {
    super(throwable);
  }

  /**
   * Creates a new <code>ParamBlockParseException</code> with message <code>description</code> that
   * wraps <code>throwable</code>.
   */

  public ParamBlockParseException (String description, Throwable throwable)
  {
    super(description, throwable);
  }

  /**
   * Creates a new <code>ParamBlockParseException</code> with message
   * <code>description</code> prefixed by <code>source</code> and <code>pos</code>.
   */

  public ParamBlockParseException (String source, int pos, String description)
  {
    super(createPrefix(source, pos) + description);
  }

  /**
   * 
   */

  protected static String createPrefix (String source, int pos)
  {
    // return "Source = -->" + source + "<--, pos = " + pos + ": ";
    final String MARK = "<*>";
    return
      (pos >= 0 && pos < source.length()
       ? "\nError at position " + pos + " (marked with " + MARK + "):\n" +
         source.substring(0, pos) + MARK + source.substring(pos) + "\n"
       : "");
  }
}
