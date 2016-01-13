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

package net.mumie.mathletfactory.transformer;

import net.mumie.mathletfactory.math.geom.affine.Affine2DPoint;
import net.mumie.mathletfactory.math.number.NumberTypeDependentIF;
import net.mumie.mathletfactory.mmobject.MMObjectIF;

/**
 *  This class is the base for all affine (i.e. math coordinates = world 
 *  coordinates) 2D transformers.
 * 
 *  @author vossbeck, Paehler
 *  @mm.docstatus finished
 */
public abstract class Affine2DDefaultTransformer extends Canvas2DObjectTransformer {

  private final double[] m_tmpForCalc = new double[2];

  public void initialize(MMObjectIF masterObject) {
    super.initialize(masterObject);
  }

  protected void math2World(Affine2DPoint mathPoint, double[] worldCoords) {
    worldCoords[0] = mathPoint.getXAsDouble();
    worldCoords[1] = mathPoint.getYAsDouble();
  }

  protected void math2World(Affine2DPoint[] mathPoints, double[][] worldCoords) {
    for (int i = 0; i < mathPoints.length; i++)
      math2World(mathPoints[i], worldCoords[i]);
  }

  public void getMathObjectFromScreen(
    double[] javaScreenCoords,
    NumberTypeDependentIF affine2DPoint) {
    if (affine2DPoint instanceof Affine2DPoint) {
      screen2World(javaScreenCoords, m_tmpForCalc);
      ((Affine2DPoint) affine2DPoint).setXY(m_tmpForCalc[0], m_tmpForCalc[1]);
    } else
      throw new IllegalArgumentException("can only be applied to instance of Affine2DPoint here");
  }

  public void getScreenPointFromMath(
    NumberTypeDependentIF affine2DPoint,
    double[] javaScreenCoords) {
    if (affine2DPoint instanceof Affine2DPoint) {
      m_tmpForCalc[0] = ((Affine2DPoint) affine2DPoint).getXAsDouble();
      m_tmpForCalc[1] = ((Affine2DPoint) affine2DPoint).getYAsDouble();
      world2Screen(m_tmpForCalc, javaScreenCoords);
    } else
      throw new IllegalArgumentException("can only be applied to instance of Affine2DPoint here");
  }

}
