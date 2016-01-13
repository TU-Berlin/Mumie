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

public class OutlineSegment
{
  // --------------------------------------------------------------------------------
  // h1: Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The type.
   */

  protected final int type;

  /**
   * The data
   */

  protected final double[] data;

  // --------------------------------------------------------------------------------
  // h1: Getting type and data
  // --------------------------------------------------------------------------------

  /**
   * Returns the type.
   */

  public final int getType ()
  {
    return this.type;
  }

  /**
   * Returns the data. The data is copied, so changes in the returned array do not
   * affect the data of this object.
   */

  public final double[] getData ()
  {
    double[] data = new double[this.data.length];
    System.arraycopy(this.data, 0, data, 0, this.data.length);
    return data;
  }

  /**
   * Returns a copy of the data and scales all values by the specified factor.
   */

  public final double[] getData (double scale)
  {
    double[] data = this.getData();
    for (int i = 0; i < data.length; i++)
      data[i] *= scale;
    return data;
  }

  // --------------------------------------------------------------------------------
  // h1: Constructors
  // --------------------------------------------------------------------------------

  /**
   * Creates a new instance with the specified type and data
   */

  public OutlineSegment (int type, double... data)
  {
    this.type = type;
    this.data = data;
  }

  /**
   * Creates a copy of the specified segement.
   */

  public OutlineSegment (OutlineSegment segment)
  {
    this.type = segment.getType();
    this.data = segment.getData();
  }

  /**
   * Creates a scaled version of the specified segement.
   */

  public OutlineSegment (OutlineSegment segment, double scale)
  {
    this.type = segment.getType();
    this.data = segment.getData(scale);
  }

  // --------------------------------------------------------------------------------
  // h1: To-string method
  // --------------------------------------------------------------------------------

  /**
   * Returns a string representation of this segment.
   */

  public String toString ()
  {
    StringBuilder buffer = new StringBuilder();

    buffer.append(OutlineSegmentType.getLetter(this.type));

    for (double value : this.data)
      buffer.append(" ").append(value);

    return buffer.toString();
  }
}
