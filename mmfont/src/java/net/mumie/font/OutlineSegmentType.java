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

public class OutlineSegmentType
{
  // --------------------------------------------------------------------------------
  // Static constants for segment types.
  // --------------------------------------------------------------------------------

  /**
   * Absolute move (segment type)
   */

  public static final int MOVE = 0;

  /**
   * Relative move (segment type)
   */

  public static final int MOVE_REL = 1;

  /**
   * Close (segment type)
   */

  public static final int CLOSE = 2;

  /**
   * Absolute line (segment type)
   */

  public static final int LINE = 3;

  /**
   * Relative line (segment type)
   */

  public static final int LINE_REL = 4;

  /**
   * Absolute horizontal_line (segment type)
   */

  public static final int HOR_LINE = 5;

  /**
   * Relative horizontal_line (segment type)
   */

  public static final int HOR_LINE_REL = 6;

  /**
   * Absolute vertical_line (segment type)
   */

  public static final int VERT_LINE = 7;

  /**
   * Relative vertical_line (segment type)
   */

  public static final int VERT_LINE_REL = 8;

  /**
   * Absolute cubic Bezier curve (segment type)
   */

  public static final int CUBIC_CURVE = 9;

  /**
   * Relative cubic Bezier curve (segment type)
   */

  public static final int CUBIC_CURVE_REL = 10;

  /**
   * Absolute "smooth" cubic Bezier curve (segment type)
   */

  public static final int CUBIC_CURVE_SMOOTH = 11;

  /**
   * Relative "smooth" cubic Bezier curve (segment type)
   */

  public static final int CUBIC_CURVE_SMOOTH_REL = 12;

  /**
   * Absolute quadratic Bezier curve (segment type)
   */

  public static final int QUAD_CURVE = 13;

  /**
   * Relative quadratic Bezier curve (segment type)
   */

  public static final int QUAD_CURVE_REL = 14;

  /**
   * Absolute "smooth" quadratic Bezier curve (segment type)
   */

  public static final int QUAD_CURVE_SMOOTH = 15;

  /**
   * Relative "smooth" quadratic Bezier curve (segment type)
   */

  public static final int QUAD_CURVE_SMOOTH_REL = 16;

  /**
   * Undefined segment type.
   */

  public static final int UNDEFINED = -1;

  // --------------------------------------------------------------------------------
  // Other constants
  // --------------------------------------------------------------------------------

  /**
   * Maps path segment types to the corresponding SVG path letters.
   */

  protected static final char[] letters =
  {'M', 'm', 'Z', 'L', 'l', 'H', 'h', 'V', 'v', 'C', 'c', 'S', 's', 'Q', 'q', 'T', 't'};

  // --------------------------------------------------------------------------------
  // Segment types vs. characters
  // --------------------------------------------------------------------------------

  /**
   * Returns the letter for the specified command type.
   */

  public static char getLetter (int type)
  {
    return (type >= 0 && type < letters.length ? letters[type] : '?');
  }

  /**
   * Returns the type for the specified letter.
   */

  public static int getType (char c)
  {
    if ( c == 'z' ) c = 'Z';
    int type = UNDEFINED;
    for (int i = 0; i < letters.length && type == UNDEFINED; i++)
      {
        if ( letters[i] == c )
          type = i;
      }
    return type;
  }

  /**
   * Returns the type for the specified letter.
   */

  public static int getType (String letter)
  {
    return (letter.length() == 1 ? getType(letter.charAt(0)) : UNDEFINED);
  }

  // --------------------------------------------------------------------------------
  // Categories
  // --------------------------------------------------------------------------------

  /**
   * Returns true if th specified type is one of the cubic curve commands.
   */

  public static boolean isCubicCurve (int type)
  {
    return
      ( type == CUBIC_CURVE ||
        type == CUBIC_CURVE_REL ||
        type == CUBIC_CURVE_SMOOTH ||
        type == CUBIC_CURVE_SMOOTH_REL );
  }

  /**
   * Returns true if th specified type is one of the quadratic curve commands.
   */

  public static boolean isQuadCurve (int type)
  {
    return
      ( type == QUAD_CURVE ||
        type == QUAD_CURVE_REL ||
        type == QUAD_CURVE_SMOOTH ||
        type == QUAD_CURVE_SMOOTH_REL );
  }

  // --------------------------------------------------------------------------------
  // Disabled constructor
  // --------------------------------------------------------------------------------

  /**
   * Disabled constructor
   */

  private OutlineSegmentType ()
  {
    throw new IllegalStateException("OutlineSegmentType must not be instanciated");
  }
}
