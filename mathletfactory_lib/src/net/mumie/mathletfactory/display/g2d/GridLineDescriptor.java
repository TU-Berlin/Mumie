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

package net.mumie.mathletfactory.display.g2d;

import java.awt.Color;

import net.mumie.mathletfactory.math.number.MNumber;

/**
 * Used for defining a grid line descriptor for the given position, description and color.
 * 
 * @author Amsel
 * @mm.docstatus finished
 */
public class GridLineDescriptor {
  
  public static final String PI = "\u03C0";
  public static final String SQRT = "\u221a";
  
  private double m_position;
  private String m_description;
  private Color m_color;
  public GridLineDescriptor(MNumber position, String description, Color color) {
     m_position = position.getDouble();
     m_description = description;
     m_color = color;
  }
  /**
   * Returns the color.
   * @return Color
   */
  public Color getColor() {
    return m_color;
  }

  /**
   * Returns the description.
   * @return String
   */
  public String getDescription() {
    return m_description;
  }

  /**
   * Returns the position.
   * @return double
   */
  public double getPosition() {
    return m_position;
  }

}
