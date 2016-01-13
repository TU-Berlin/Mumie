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

package net.mumie.cdk.util;

import java.util.List;
import java.util.ArrayList;

/**
 * A special character buffer for passwords. The buffer content can be erased completely, so
 * no parts of the password remain in memory.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: PasswordBuffer.java,v 1.3 2007/10/30 00:14:51 rassy Exp $</code>
 */

public class PasswordBuffer
{
  /**
   * Auxiliary class for {@link PasswordBuffer PasswordBuffer}. Wraps a variable of the
   * primitive type <code>char</code>. Internally, password buffers are implemented as lists
   * of objects of this type. The usual primitive type wrapper, i.e.,
   * {@link Character Character}, is not used because the wrapped character would not be
   * erasable, then.
   */

  protected static class Char
  {
    /**
     * The wrapped character
     */

    public char value = 0;

    /**
     * Creates a new instance which wraps the specified character.
     */

    public Char (char value)
    {
      this.value = value;
    }
  }

  /**
   * The content of the buffer.
   */

  protected List<Char> chars = new ArrayList<Char>();

  /**
   * Adds the specified character to the buffer.
   */

  public void add (char value)
  {
    this.chars.add(new Char(value));
  }

  /**
   * Erases the buffer. First, all characters of the buffer are set to 0. Then, the internal
   * list of buffered characters {@link #chars chars} is replaced by a new, empty list.
   */

  public void erase ()
  {
    for (Char c : this.chars)
      c.value = 0;
    this.chars = new ArrayList<Char>();
  }

  /**
   * Returns the content of the buffer as an array of <code>char</code>s.
   */

  public char[] toCharArray ()
  {
    char[] array = new char[this.chars.size()];
    int i = -1;
    for (Char c : this.chars)
      array[++i] = c.value;
    return array;
  }

}
