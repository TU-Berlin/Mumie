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

package net.mumie.font;

public class FontWeight
{
  /**
   * Weight undefined.
   */

  public static final int UNDEFINED = -1;

  /**
   * Normal weight.
   */

  public static final int NORMAL = 0;

  /**
   * Bold.
   */

  public static final int BOLD = 1;

  /**
   * Returns the name for the specified weight.
   */

  public static final String getName (int weight)
  {
    switch (weight)
      {
      case UNDEFINED:
        return "undefined";
      case NORMAL:
        return "normal";
      case BOLD:
        return "bold";
      default:
        throw new IllegalArgumentException("Unknown font weight value: " + weight);
      }
  }

  /**
   * Returns the font weight for the specified name.
   */

  public static final int forName (String name)
  {
    if ( name.equals("undefined") )
      return UNDEFINED;
    else if ( name.equals("normal") )
      return NORMAL;
    else if ( name.equals("bold") )
      return BOLD;
    else
      throw new IllegalArgumentException("Unknown font weight name: \"" + name + "\"");
  }

  /**
   * Disabled constructor
   */

  private FontWeight ()
  {
    throw new IllegalStateException("FontWeight must not be instanciated");
  }
}
