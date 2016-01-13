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

package net.mumie.mathletfactory.display.j3d;

import javax.media.j3d.Geometry;

import net.mumie.mathletfactory.display.j3d.shape.FunctionValuesOverR2Geometry;
import net.mumie.mathletfactory.math.number.MNumber;

/**
 *  This class acts as a drawable for an "analytic landscape", a surface with rectangular 
 *  xy-projection and the function value f(x,y) in the z axis.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */

public class J3DFunctionGraphOverR2Drawable extends J3DSurfaceDrawable {
  
  private MNumber[][] m_values;
  
  private double m_xMin, m_yMin, m_xStep, m_yStep;

  /**
   *  sets the values of an rectangular equidistant grid (which has specified by
   *  {@link #setGridParameters}). This method is called by 
   *  {@link net.mumie.mathletfactory.transformer.j3d.FunctionOverR2Transformer#synchronizeMath2Screen}. 
   */
  public void setValues(MNumber[][] values){
    m_values = values;
    resetShape();
  }
  /**
   *  Sets the grid parameters in the following form: <code>{xMin, yMin, xStep, yStep}</code>.
   *  Where the <code>min</code> values specify the lower left corner of the xy-rectangle and
   *  the <code>step</code> values specify the distance between the vertices on which the 
   *  function has been evaluated. 
   */
  public void setGridParameters(double[] params){
    m_xMin = params[0]; m_yMin = params[1];  m_xStep = params[2]; m_yStep = params[3];
  }

  protected Geometry getGeometry(){
    return FunctionValuesOverR2Geometry.getValueShapeGeometry(m_xMin, m_yMin, m_xStep, m_yStep, m_values);
  }
  
}
