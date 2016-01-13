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

import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MNatural;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;

/**
 * Represents a Taylor series &sum;f<sup>(k)</sup>(x<sub>0</sub>)/k! (x-x<sub>0</sub>)<sup>k</sup>, where 
 * f is a function and x<sub>0</sub> is the point of expansion.
 * 
 * @author Mrose
 * @mm.docstatus finished
 */
public class MMTaylorSeriesDefByOp extends MMPowerSeriesDefByOp {

  private MMFunctionDefByOp m_function;
  private double[] m_kthDerivatives = new double[1000];
  private double m_point = 0.0;

  /**
   * Defines a Taylor series &sum;f<sup>(k)</sup>(x<sub>0</sub>)/fac(k) (x-x<sub>0</sub>)<sup>k</sup>, where 
   * f is given by <code>function</code> and the point of expansion x<sub>0</sub> is given 
   * by <code>point</code>; the number class is given by <code>numberClass</code>.
   */
  public MMTaylorSeriesDefByOp(Class numberClass, MMFunctionDefByOp function, double point) {
    super(numberClass, ""+0 , 0.0, new Operation(numberClass, "k", true));
    m_function = function;
    m_point = point;
    getKthDerivatives();
  }

  /** Evaluates this Taylor series in <code>x</code> and returns its value.*/
  public double evaluate(double x) {
    return getSum(getN(), x).getDouble();
  }

  /** 
   * Returns the value of the <code>n</code>-th partial sum of this Taylor 
   * series in <code>x</code>.
   */
  public MNumber getSum(MNatural n, double x) {
    double N = n.getDouble();
    MNumber result = NumberFactory.newInstance(getNumberClass());
    for (int _k = getStartingIndex(); _k <= N; _k++) {
      result.add(
        new MDouble(1.0/factorial(_k)).mult(
          new MDouble(Math.pow((x - m_point), _k)).mult(
            new MDouble(m_kthDerivatives[_k]))));
    }
    return result;
  }

  /** Sets the function approximated by this Taylor series.*/
  public void setFunction(MMFunctionDefByOp function) {
    m_function = function;
    getKthDerivatives();
    update();
  }

  /** Sets the point of expansion of this Taylor series.*/
  public void setPoint(double point) {
    m_point = point;
    getKthDerivatives();
    update();
  }

  private double factorial(int n) {
    double tmp = 1.0;
    for (int i = 1; i <= n; i++) {
      tmp = tmp * i;
    }
    return tmp;
  }

  private void getKthDerivatives() {
    int n = (int) getN().getDouble();
    MMFunctionDefByOp function =
      new MMFunctionDefByOp(
        new Operation(m_function.getOperation()),
        m_function.getBorelSet());
    for (int k = getStartingIndex(); k <= n; k++) {
      m_kthDerivatives[k] = function.evaluate(m_point);
      function = function.getDerivative();
    }
  }
}
