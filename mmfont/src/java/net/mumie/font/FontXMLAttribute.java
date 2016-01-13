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

public final class FontXMLAttribute
{
  /**
   * Id of a font
   */

  public static final String ID = "id";

  /**
   * Name of a font or glyph
   */

  public static final String NAME = "name";

  /**
   * Font family
   */

  public static final String FAMILY = "family";

  /**
   * Font style
   */

  public static final String STYLE = "style";

  /**
   * Font weight
   */

  public static final String WEIGHT = "weight";

  /**
   * Font size
   */

  public static final String SIZE = "size";

  /**
   * Outline of a glypth.
   */

  public static final String OUTLINE = "outline";

  /**
   * Advance width of a glypth.
   */

  public static final String ADVANCE_WIDTH = "advance-width";

  /**
   * Unicode character(s) represented by a glypth.
   */

  public static final String UNICODE = "unicode";

  /**
   * X-coordinate of the bottom left edge of the bounding box of a glyph.
   */

  public static final String X = "x";

  /**
   * Y-coordinate of the bottom left edge of the bounding box of a glyph.
   */

  public static final String Y = "y";

  /**
   * Height of the bounding box of a glyph.
   */

  public static final String HEIGHT = "height";

  /**
   * Width of the bounding box of a glyph.
   */

  public static final String WIDTH = "width";

  /**
   * Disabled constructor
   */

  private FontXMLAttribute ()
  {
    throw new IllegalStateException("FontXMLAttribute must not be instanciated");
  }
}
