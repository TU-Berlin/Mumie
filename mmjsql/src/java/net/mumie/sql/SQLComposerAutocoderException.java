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

public class SQLComposerAutocoderException extends Exception
{
  /**
   * String to indicate the location of an error.
   */

  public static final String MARK = "<*>";

  /**
   * Creates a new <code>SQLComposerAutocoderException</code> with message
   * <code>description</code>.
   */

  public SQLComposerAutocoderException (String description)
  {
    super(description);
  }

  /**
   * Creates a new <code>SQLComposerAutocoderException</code> that wraps
   * <code>throwable</code>.
   */

  public SQLComposerAutocoderException (Throwable throwable)
  {
    super(throwable);
  }

  /**
   * Creates a new <code>SQLComposerAutocoderException</code> with message
   * <code>description</code> that wraps <code>throwable</code>.
   */

  public SQLComposerAutocoderException (String description, Throwable throwable)
  {
    super(description, throwable);
  }

  /**
   * Creates a new <code>SQLComposerAutocoderException</code> with message
   * <code>description</code> prefixed by <code>source</code> and <code>pos</code>.
   */

  public SQLComposerAutocoderException (String source, int pos, String description)
  {
    super(createPrefix(source, pos) + description);
  }

  /**
   * 
   */

  protected static String createPrefix (String source, int pos)
  {
    // return "Source = -->" + source + "<--, pos = " + pos + ": ";
    return
      (pos >= 0 && pos < source.length()
       ? "\nError at position " + pos + " (marked with " + MARK + "):\n" +
         source.substring(0, pos) + MARK + source.substring(pos) + "\n"
       : "");
  }
}
