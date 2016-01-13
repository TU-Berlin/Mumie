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
import java.awt.geom.GeneralPath;

import net.mumie.mathletfactory.display.DisplayProperties;

/**
 * This drawable renders an arbitrary 
 * {@link net.mumie.mathletfactory.set.NumberTupelSetIF} in a 
 * {@link net.mumie.mathletfactory.display.g2d.MMG2DCanvas}.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public class Set2DDrawable extends G2DDrawable {

  /** The shape for the area contained by the set. */
  protected GeneralPath m_area = new GeneralPath(GeneralPath.WIND_NON_ZERO);
  
  protected Shape getShape() {
    return m_area;
  }

  /**
   * Adds the given area to the "marked" area (the area, in which 
   * {@link net.mumie.mathletfactory.set.NumberTupelSetIF#contains} evals to true). 
   */
  public void addArea(Shape area){
    m_area.append(area,false);
  }
  
  /** 
   * Resets the area to empty.
   */
  public void resetArea(){
    m_area.reset();
  }
  
  /**
   * Empty implementation. 
   * @see net.mumie.mathletfactory.display.CanvasDrawable#render(DisplayProperties)
   */  
  public void render(DisplayProperties properties) {
  }
  
  public boolean isAtScreenLocation(int xOnScreen, int yOnScreen) {
    return m_area.contains(xOnScreen, yOnScreen);
  }

}
