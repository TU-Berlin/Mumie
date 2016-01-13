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

/**
 * <p>
 *   Represemts a key-value pair
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: KeyValuePair.java,v 1.3 2007/01/11 21:56:22 rassy Exp $</code>
 */

public class KeyValuePair
{
  /**
   * The key
   */

  protected String key;

  /**
   * The value
   */

  protected String value;

  /**
   * Returns the key.
   */

  public String getKey ()
  {
    return this.key;
  }

  /**
   * Returns the value.
   */

  public String getValue ()
  {
    return this.value;
  }

  /**
   * Parses the specified string to get a key-value pair. The string must have the form
   * <code>"<var>key</var>=<var>value</var>"</code> (i.e., it must contain an equality
   * sign), otherwise, an exception is thrown.  After this method has been called
   * successfully, subsequent calls of {@link #getKey getKey} and
   * {@link #getValue getValue} return <code>"<var>key"</code> and
   * <code>"<var>value"</code>, respectively.
   */

  public void parse (String string)
  {
    int posEq = string.indexOf('=');
    if ( posEq == -1 )
      throw new IllegalArgumentException
        ("Missing '=' sign in key-value expression: " + string);
    if ( posEq == 0 || posEq == string.length() - 1 )
      throw new IllegalArgumentException
        ("Illegal key-value expression: " + string);
    this.key = string.substring(0, posEq);
    this.value = string.substring(posEq + 1);
  }

  /**
   * Creates a new instance from the specified key and value.
   */

  public KeyValuePair (String key, String value)
  {
    this.key = key;
    this.value = value;
  }

  /**
   * Creates a new instance from the specified string. The key and the value are obtained by
   * applying {@link #parse parse} to the string.
   */

  public KeyValuePair (String string)
  {
    this.parse(string);
  }

  /**
   * Creates a new instance with a key and value which are initially <code>null</code>. Use
   * the {@link #parse parse} method give this instance with a non-<code>null</code> key and
   * value.
   */

  public KeyValuePair ()
  {
    this.key = null;
    this.value = null;
  }
}
