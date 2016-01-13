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

package net.mumie.mathletfactory.mmobject.analysis.function;

import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPoint;

/**
 * This class represents a linear spline, i.e. a piecewise defined function 
 * that consists of a polynomial expression on each interval with a maximum degree of 1 and that 
 * are continuous at the joints of each two neighbouring intervals.
 * 
 * @author vossbeck, Paehler
 * @mm.docstatus finished
 */
public class MMLinearSpline extends MMAbstractSpline {

  public MMLinearSpline(Class numberClass, double[] xVals, double[] yVals) {
    super(numberClass, xVals, yVals);
  }

  public MMLinearSpline(MMAffine2DPoint[] points) {
    super(points);
  }

  protected double specificEvaluate(double x, int i) {
    //  observe index is based on fortran-indexing, m_yVals[] uses standard java-indexing,
    // i.e. index i means that x is element of [x[i-1],x[i]):
    int javaIndex = i-1;
    return m_yVals[javaIndex]
      + (x - m_xVals[javaIndex]) * (m_yVals[javaIndex + 1] - m_yVals[javaIndex]) / (m_xVals[javaIndex+1] - m_xVals[javaIndex]);
  }

	protected void specificEvaluate(MNumber xin, MNumber yout, int i) {
    yout.setDouble(specificEvaluate(xin.getDouble(), i));
	}
}
