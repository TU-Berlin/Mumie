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
 * Represents a power series &sum;f(k)(x-x<sub>0</sub>)<sup>g(k)</sup>, where 
 * f(k) and g(k) are operations or expressions and x<sub>0</sub> is the point of expansion.
 * 
 * @author Mrose
 * @mm.docstatus finished
 */
public class MMPowerSeriesDefByOp extends MMFunctionSeriesDefByOp {
	private double m_point = 0.0;
	private Operation m_exponent = new Operation(getNumberClass(), "k", true);

  /**
   * Defines a power series &sum;f(k)(x-x<sub>0</sub>)<sup>g(k)</sup>, where 
   * f(k) is given by <code>expression</code>, g(k) is given by 
   * <code>exponent</code>,  and the point of expansion x<sub>0</sub> is given 
   * by <code>point</code>; the number class is given by <code>numberClass</code>.
   */
	public MMPowerSeriesDefByOp(
		Class numberClass,
		String expression,
		double point,
		Operation exponent) {
		super(numberClass, expression);
		m_point = point;
		m_exponent = exponent;
	}

  /**
   * Defines a power series &sum;f(k)(x-x<sub>0</sub>)<sup>g(k)</sup>, where 
   * f(k) is given by <code>operation</code>, g(k) is given by 
   * <code>exponent</code>,  and the point of expansion x<sub>0</sub> 
   * is given by <code>point</code>. 
   */
	public MMPowerSeriesDefByOp(
		Operation operation,
		double point,
		Operation exponent) {
		super(operation);
		m_point = point;
		m_exponent = exponent;
	}
  
  /** Evaluates this power series in <code>x</code> and returns its value.*/
	public double evaluate(double x) {
		return getSum(getN(), x).getDouble();
	}
  
  /** Evaluates this power series in <code>xin</code> and sets <code>yout</code> on its value.*/
	public void evaluate(MNumber xin, MNumber yout) {
		//      m_operation.assignValue(m_variableId, xin);
		yout.set(getSum(getN(), m_point));
	}

  /** 
   * Returns the value of the <code>n</code>-th partial sum of this power 
   * series in <code>x</code>.
   */
	public MNumber getSum(MNatural n, double x) {
		double N = n.getDouble();
		MNumber result = NumberFactory.newInstance(getNumberClass());
		MNumber tmp = NumberFactory.newInstance(getNumberClass());
		MNumber tmp2 = NumberFactory.newInstance(getNumberClass());
		MNumber k = NumberFactory.newInstance(getNumberClass());
		for (int _k = getStartingIndex(); _k <= N; _k++) {
			m_exponent.assignValue("k", _k);
			m_operation.assignValue("k", _k);
			m_operation.evaluate(k, tmp);
			m_exponent.evaluate(k, tmp2);
			if (!tmp.isNaN() && !tmp2.isNaN())
				result.add(
					tmp.mult(
						new MDouble(Math.pow(x - m_point, tmp2.getDouble()))));
		}
		return result;
	}

  /** Sets the point of expansion of this power series.*/
	public void setPoint(double point) {
		m_point = point;
		update();
	}

  /** Sets the exponent of this power series.*/
	public void setExponent(Operation exponent) {
		m_exponent = exponent;
		update();
	}
}
