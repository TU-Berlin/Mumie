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

public class FontStyle
{
  /**
   * Style undefined.
   */

  public static final int UNDEFINED = -1;

  /**
   * Normal style.
   */

  public static final int NORMAL = 0;

  /**
   * Italic style.
   */

  public static final int ITALIC = 1;

  /**
   * Oblique style.
   */

  public static final int OBLIQUE = 2;

  /**
   * Returns the name for the specified style.
   */

  public static final String getName (int style)
  {
    switch (style)
      {
      case UNDEFINED:
        return "undefined";
      case NORMAL:
        return "normal";
      case ITALIC:
        return "italic";
      case OBLIQUE:
        return "oblique";
      default:
        throw new IllegalArgumentException("Unknown font style value: " + style);
      }
  }

  /**
   * Returns the font style for the specified name.
   */

  public static final int forName (String name)
  {
    if ( name.equals("undefined") )
      return UNDEFINED;
    else if ( name.equals("normal") )
      return NORMAL;
    else if ( name.equals("italic") )
      return ITALIC;
    else if ( name.equals("oblique") )
      return OBLIQUE;
    else
      throw new IllegalArgumentException("Unknown font style name: \"" + name + "\"");
  }

  /**
   * Disabled constructor
   */

  private FontStyle ()
  {
    throw new IllegalStateException("FontStyle must not be instanciated");
  }
}
