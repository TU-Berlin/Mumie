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

import java.awt.geom.Path2D;

public class Outline
{
  /**
   * The segments of the outline.
   */

  protected final OutlineSegment[] segments;

  /**
   * Returns a copy of the segments.
   */

  public OutlineSegment[] getSegments ()
  {
    OutlineSegment[] segments = new OutlineSegment[this.segments.length];
    for (int i = 0 ; i < this.segments.length; i++)
      segments[i] = new OutlineSegment(this.segments[i]);
    return segments;
  }

  /**
   * Returns a copy of the segments scaled by the specified factor.
   */

  public OutlineSegment[] getSegments (double scale)
  {
    OutlineSegment[] segments = new OutlineSegment[this.segments.length];
    for (int i = 0 ; i < this.segments.length; i++)
      segments[i] = new OutlineSegment(this.segments[i], scale);
    return segments;
  }

  /**
   * Returns the code of this outline, translated to the specified position, and with
   * inverted y-coordinates if <code>flipY</code> is true.
   */

  public String getCode (double x, double y, boolean flipY)
  {
    StringBuilder buffer = new StringBuilder();
    for (OutlineSegment segment : this.segments)
      {
        double[] data = segment.getData();

        // Transform coordinates:
        for (int k = 0; k < data.length; k++)
          {
            data[k] = x + data[k];
            k++;
            data[k] = (flipY ? y - data[k] : y + data[k]);
          }

        if ( buffer.length() > 0 )
          buffer.append(" ");

        // Type letter:
        buffer.append(OutlineSegmentType.getLetter(segment.getType()));

        // Data:
        for (double value : data)
          buffer.append(" ").append(value);
      }
    return buffer.toString();
  }

  /**
   * Converts this outline to a path.
   *
   * @param path the path object which recieves the outline
   * @param x x-coordinate of the bottom-left edge of the <em>size box</em> of the glyph
   * @param y y-coordinate of the bottom-left edge of the <em>size box</em> of the glyph
   */

  public void toPath (Path2D.Double path, double x, double y)
  {
    path.reset();
    path.setWindingRule(Path2D.WIND_EVEN_ODD );
    for (OutlineSegment segment : this.segments)
      {
        double[] data = segment.getData();

        // Transform coordinates:
        for (int k = 0; k < data.length; k++)
          {
            data[k] = x + data[k];
            k++;
            data[k] = y - data[k];
          }

        // Add to path:
        switch ( segment.getType() )
          {
          case OutlineSegmentType.MOVE:
            path.moveTo(data[0], data[1]);
            break;
          case OutlineSegmentType.CLOSE:
            path.closePath();
            break;
          case OutlineSegmentType.LINE:
            path.lineTo(data[0], data[1]);
            break;
          case OutlineSegmentType.CUBIC_CURVE:
            path.curveTo(data[0], data[1], data[2], data[3], data[4], data[5]);
            break;
          case OutlineSegmentType.QUAD_CURVE:
            path.quadTo(data[0], data[1], data[2], data[3]);
            break;
          }
      }
  }

  /**
   * Creates a new instance with the specified outline segments.
   */

  public Outline (OutlineSegment[] segments)
  {
    this.segments = segments;
  }

  /**
   * Creates a copy of the specified outline scaled by the specified factor.
   */

  public Outline (Outline outline, double scale)
  {
    this.segments = outline.getSegments(scale);
  }

  /**
   * Returns a string representation of this segment.
   */

  public String toString ()
  {
    StringBuilder buffer = new StringBuilder();

    for (int i = 0; i < this.segments.length; i++)
      {
        if ( i > 0 ) buffer.append(" ");
        buffer.append(this.segments[i]);
      }

    return buffer.toString();
  }
}
