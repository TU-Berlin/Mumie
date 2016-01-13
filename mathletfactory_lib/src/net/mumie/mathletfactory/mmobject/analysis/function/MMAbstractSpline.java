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
import net.mumie.mathletfactory.math.set.FiniteBorelSet;
import net.mumie.mathletfactory.math.set.Interval;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPoint;

/**
 * This class represents the base for all spline functions, i.e. piecewise defined functions 
 * that consist of a polynomial expression on each interval with a maximum degree k and that 
 * are k-1 times differentiable at the joints of each two neighbouring intervals.
 *  
 * @author vossbeck, Paehler
 * @mm.docstatus finished
 */
public abstract class MMAbstractSpline extends MMFunctionDefinedBySamples {

  /** Constructs a spline that has its joints at the given points. */
  public MMAbstractSpline(Class entryClass, double[] xVals, double[] yVals) {
    super(entryClass, xVals, yVals);
  }

  /** Constructs a spline that has its joints at the given points. */
  public MMAbstractSpline(MMAffine2DPoint[] points) {
    super(points);
  }

  /**
   * This overrides the inherited method due to the
   * fact, that the spline should generally  be defined on the closed interval 
   * [xvals [0], xvals[xvals.length -1] ].
   */
  protected void adjustBorelDomain() {
    m_borelSet = new FiniteBorelSet(new Interval(getNumberClass(), m_xVals[0], Interval.CLOSED, m_xVals[m_xVals.length-1],Interval.CLOSED));
  }

  public double evaluate(double x) {
    return specificEvaluate(x, getIntervalIndex(x));
  }

  public void evaluate(MNumber xin, MNumber yout) {
    specificEvaluate(xin, yout, getIntervalIndex(xin));
  }

  /** Returns the index for the interval the given x value is contained in. */
  protected final int getIntervalIndex(double x) {
    // should always be equal to x coordinate of m_dataPoints[0]:
    double xmin = getXminDouble();
    // should always be equal to x coordinate of m_dataPoints[getSamplesCount()-1]:
    double xmax = getXmaxDouble();
    int imin = 1;
    int imax = getSamplesCount();

    if (x < xmin) {
      return 0;
    }
    else if (x >= xmax) {
      return getSamplesCount();
    }
    else {
      //starting bisection search
      while (imax - imin > 1) {
        int midi = (imax + imin) / 2;
        if (x >= m_xVals[midi - 1])
          imin = midi;
        else
          imax = midi;

      }
      // here we get 1<=imin<= getSamplesCount()-1
      return imin;
    }
  }

  /** Returns the index for the interval the given x value is contained in. */
  protected final int getIntervalIndex(MNumber x) {
    MNumber xmin = getXmin();
    MNumber xmax = getXmax();
    int imin = 1;
    int imax = getSamplesCount();

    if (x.lessThan(xmin)) {
      return 0;
    }
    else if (xmax.lessOrEqualThan(x)) {
      return getSamplesCount();
    }
    else {
      //starting bisection search
      while (imax - imin > 1) {
        int midi = (imax + imin) / 2;
        if (m_dataPoints[midi
          - 1].getAffineCoordinatesOfPoint().getEntryRef(1).lessOrEqualThan(x))
          imin = midi;
        else
          imax = midi;

      }
      // here we get 1<=imin<= getSamplesCount()-1
      return imin;
    }
  }

  /** Should return the function value for the given x and interval index. */
  protected abstract double specificEvaluate(double x, int i);

  /** Should return the function value for the given x and interval index. */
  protected abstract void specificEvaluate(MNumber xin, MNumber yout, int i);

}
