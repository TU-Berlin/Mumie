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

import java.util.List;
import java.util.ArrayList;
import java.nio.CharBuffer; // Referenced in the Javadoc only

/**
 * A buffer for constructing passwords character by character. The purpose of this class is
 * similar to that of {@link CharBuffer CharBuffer}, but care is taken that the content of
 * the buffer can be erased in a way that no information remains in memory. Note that this
 * class does not inherit from {@link CharBuffer CharBuffer}.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: PasswordBuffer.java,v 1.1 2007/03/19 16:50:32 rassy Exp $</code>
 */

public class PasswordBuffer
{
  /**
   * Wraps a variable of the <code>char</code> primitive type. (This auxiliary class is
   * necessary because the buffer content is stored as a list of characters (see
   * {@link #chars chars}, but list entries must be objects and can not be primitive types.)
   */

  protected static class Char
  {
    /**
     * The wrapped character
     */

    public char value = 0;

    /**
     * Creates a new instance wrapping the specified character.
     */

    public Char (char value)
    {
      this.value = value;
    }
  }

  /**
   * Stores the content of the buffer
   */

  protected List<Char> chars = new ArrayList<Char>();

  /**
   * Appends the specified character to the end of the buffer.
   */

  public void append (char value)
  {
    this.chars.add(new Char(value));
  }

  /**
   * Erases the content of the buffer. All characters in the buffer are "nulled", i.e., set
   * to 0; then the internal list of characters is replaced by a new, empty one.
   */

  public void erase ()
  {
    for (Char c : this.chars)
      c.value = 0;
    this.chars = new ArrayList<Char>();
  }

  /**
   * Returns the content of this buffer as a <code>char</code> array. If <code>erase</code>
   * is true, the buffer is erased afterwards by calling {@link #erase erase}.
   */

  public char[] toCharArray (boolean erase)
  {
    char[] array = new char[this.chars.size()];
    int i = -1;
    for (Char c : this.chars)
      array[++i] = c.value;
    if ( erase ) this.erase();
    return array;
  }

  /**
   * Returns the content of this buffer as a <code>char</code> array and erases the buffer
   * afterwards by calling {@link #erase erase}. Same as
   * {@link #toCharArray(boolean) toCharArray(true)}.
   */

  public char[] toCharArray ()
  {
    return this.toCharArray(true);
  }

  /**
   * Sets all characters in the buffer to 0; then sets the internal list of characters to
   * null.
   */

  protected void finalize ()
  {
    for (Char c : this.chars)
      c.value = 0;
    this.chars = null;
  }
}
