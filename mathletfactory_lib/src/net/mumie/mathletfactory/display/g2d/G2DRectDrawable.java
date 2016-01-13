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
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import net.mumie.mathletfactory.display.DisplayProperties;

/**
 * Contains methods to draw a 2D rectangle in a
 * {@link net.mumie.mathletfactory.display.g2d.MMG2DCanvas}.
 *
 * @author Paehler
 * @mm.docstatus finished
 */
public class G2DRectDrawable extends G2DDrawable {
  
  private Rectangle2D m_rect = new Rectangle2D.Double(0,0,0,0);
  
  /**
   * Sets the coordinates for the upperleft point and width and height of the rectangle. 
   */
  public void setPoints(double x, double y, double w, double h){
    // rectangles with negative width or height can not be drawn:
    if(h<0){
      h *= -1;
      y -= h;
    }
    if(w<0){
      w *= -1;
      x -= w;
    }
    m_rect.setRect(x, y, w, h);
  }
    
  /**
   * Empty implementation.
   */
  public void render(DisplayProperties properties) {    
  }
  
  /** Returns true if the given point is inside the rectangle, false otherwise. */
  public boolean isAtScreenLocation(int xOnScreen, int yOnScreen) {
    return m_rect.contains(xOnScreen, yOnScreen);
  }
  
  /**
   * Returns the Java 2D-{@link java.awt.Shape}, that acts as graphical
   * representation of the corresponding mathematical object   
   */
  protected Shape getShape() {
    return m_rect;
  }
}



