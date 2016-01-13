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

import java.awt.Graphics2D;
import java.awt.geom.Path2D;

public class GlyphString
{
  /**
   * The glyphs
   */

  protected final Glyph[] glyphs;

  /**
   * Space between two glyphs
   */

  protected final double space;

  /**
   * Width of the glyph string.
   */

  protected final double width;

  /**
   * Height of the glyph string.
   */

  protected final double height;

  /**
   * Creates a new glyph string with the specified glyphs and space.
   */

  public GlyphString (Glyph[] glyphs, double space)
  {
    this.glyphs = glyphs;
    this.space = space;

    // Compute width and height:
    double minY = 0;
    double maxY = 0;
    double width = 0;
    for (Glyph glyph : this.glyphs)
      {
        width += glyph.getWidth();
        double bottomY = glyph.getY();
        double topY = bottomY + glyph.getHeight();
        if ( bottomY < minY ) minY = bottomY;
        if ( topY > maxY ) maxY = topY;
      }
    width += (glyphs.length-1)*space;
    this.width = width;
    this.height = maxY - minY;
  }

  /**
   * Creates a new glyph string corresponding to the specified "normal" string. The glyphs
   * are taken from the specified font and have the specified size. The space is set to the
   * specified value.
   */

  public GlyphString (String string, Font font, double size, double space)
  {
    this(toGlyphArray(string, font, size), space);
  }

  /**
   * Returns the width of the glyph string.
   */

  public double getWidth ()
  {
    return this.width;
  }

  /**
   * Returns the height of the glyph string.
   */

  public double getHeight ()
  {
    return this.height;
  }

  /**
   * Returns the glyph at the specified position.
   */

  public Glyph getGlyph (int pos)
  {
    return this.glyphs[pos];
  }

  /**
   * Paints this glyph string at the specified position, using the specified graphics
   * object and the specified path object.
   */

  public void paint (Graphics2D g2d, double x, double y, Path2D.Double path)
  {
    for (Glyph glyph : this.glyphs)
      {
        glyph.getOutline().toPath(path, x - glyph.getX(), y);
        g2d.fill(path);
        x += glyph.getWidth() + this.space;
      }
  }

  /**
   * Paints this glyph string at the specified position, using the specified graphics
   * object.
   */

  public void paint (Graphics2D g2d, double x, double y)
  {
    this.paint(g2d, x, y, new Path2D.Double());
  }

  /**
   * Returns a glyph array for the specified string. The glyphs are taken from the specified
   * font and have the specified size. This is an auxiliary method for the constructor
   * {@link #<constructor>(String,Font,double,double) GlyphString (string, font, size, space)}.
   */

  protected static Glyph[] toGlyphArray (String string, Font font, double size)
   {
     double scale = size / font.getSize();
     int length = string.length();
     Glyph[] glyphs = new Glyph[length];
     for (int i = 0; i < length; i++)
       {
         Glyph glyph = font.getGlyphForUnicode(string.charAt(i));
         glyphs[i] = new Glyph(glyph, scale);
       }
     return glyphs;
   }
}
