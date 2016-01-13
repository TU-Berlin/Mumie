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

public class Glyph
{
  // --------------------------------------------------------------------------------
  // h1: Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The name of the glyph.
   */

  protected String name;

  /**
   * The unicode character(s) represented by this glyp.
   */

  protected String unicode;

  /**
   * The x-coordinate of the bottom left edge of the bounding box of this glyph.
   */

  protected double x;

  /**
   * The y-coordinate of the bottom left edge of the bounding box of this glyph.
   */

  protected double y;

  /**
   * The height of the bounding box of this glyph.
   */

  protected double height;

  /**
   * The width of the bounding box of this glyph.
   */

  protected double width;

  /**
   * The advance width of the glyph.
   */

  protected double advanceWidth;

  /**
   * The outline of the glyph
   */

  protected Outline outline;

  // --------------------------------------------------------------------------------
  // h1: Accessing data
  // --------------------------------------------------------------------------------

  /**
   * Returns the name of this glyph.
   */

  public final String getName ()
  {
    return this.name;
  }

  /**
   * Returns the unicode character(s) represented by this glyph.
   */

  public final String getUnicode ()
  {
    return this.unicode;
  }

  /**
   * Returns the x-coordinate of the bottom left edge of the bounding box of this glyph.
   */

  public final double getX ()
  {
    return this.x;
  }

  /**
   * Returns the y-coordinate of the bottom left edge of the bounding box of this glyph.
   */

  public final double getY ()
  {
    return this.y;
  }

  /**
   * Returns the width of the bounding box of this glyph.
   */

  public double getWidth ()
  {
    return this.width;
  }

  /**
   * Returns the height of the bounding box of this glyph.
   */

  public double getHeight ()
  {
    return this.height;
  }

  /**
   * Returns the advance width of the bounding box of this glyph.
   */

  public double getAdvanceWidth ()
  {
    return this.advanceWidth;
  }

  /**
   * Returns the outline of this glyph.
   */

  public Outline getOutline ()
  {
    return this.outline;
  }

  // --------------------------------------------------------------------------------
  // h1: Constructors
  // --------------------------------------------------------------------------------

  /**
   * Creates a new glyph with the specified data.
   */

  public Glyph (String name,
                String unicode,
                double x,
                double y,
                double height,
                double width,
                double advanceWidth,
                Outline outline)
  {
    this.name = name;
    this.unicode = unicode;
    this.x = x;
    this.y = y;
    this.height = height;
    this.width = width;
    this.advanceWidth = advanceWidth;
    this.outline = outline;
  }

  /**
   * Creates a copy of the specified glyph scaled by the specified factor.
   */

  public Glyph (Glyph glyph, double scale)
  {
    this.name = glyph.getName();
    this.unicode = glyph.getUnicode();
    this.x = glyph.getX()*scale;
    this.y = glyph.getY()*scale;
    this.height = glyph.getHeight()*scale;
    this.width = glyph.getWidth()*scale;
    this.advanceWidth = glyph.getAdvanceWidth()*scale;
    this.outline = new Outline(glyph.getOutline(), scale);
  }
}
