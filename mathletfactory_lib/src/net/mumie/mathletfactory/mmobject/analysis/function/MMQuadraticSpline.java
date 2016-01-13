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
 * This class represents  a quadratic spline, i.e. a piecewise defined function 
 * that consists of a polynomial expression on each interval with a maximum degree of 2 and that 
 * are 1 times differentiable at the joints of each two neighbouring intervals.
 * 
 * @author vossbeck, Paehler
 * @mm.docstatus finished
 */

public class MMQuadraticSpline extends MMAbstractSpline {

  /** The type constant indicating on which boundary the derivative is fixed. */
	public static final int FROM_RIGHT_DERIVATIVE = 0;
  
  /** The type constant indicating on which boundary the derivative is fixed. */
	public static final int FROM_LEFT_DERIVATIVE = 1;

	private double[] m_coeffs_f;

	private double m_derivative = 0;
	private int m_derivativeType = FROM_RIGHT_DERIVATIVE;

	private double[] m_xVals_f, m_yVals_f;

  /** Constructs a quadratic spline that has its joints at the given points. */
	public MMQuadraticSpline(Class numberClass, double[] xVals, double[] yVals) {
		super(numberClass, xVals, yVals);
	}

  /** Constructs a quadratic spline that has its joints at the given points. */
	public MMQuadraticSpline(MMAffine2DPoint[] points) {
		super(points);
	}

	protected void adjustInternalData() {
		super.adjustInternalData();
		adjustFortranFields();

	}

	private void adjustFortranFields() {
		if (m_coeffs_f == null || m_coeffs_f.length != getSamplesCount() + 1) {
			m_coeffs_f = new double[getSamplesCount() + 1];
		}
		if (m_xVals_f == null || m_xVals_f.length != getSamplesCount()) {
			m_xVals_f = new double[getSamplesCount() + 1];
			m_yVals_f = new double[getSamplesCount() + 1];
		}
		System.arraycopy(m_xVals, 0, m_xVals_f, 1, m_xVals.length);
		System.arraycopy(m_yVals, 0, m_yVals_f, 1, m_xVals.length);
		if (m_derivativeType == FROM_LEFT_DERIVATIVE)
			generateCoefficientsLeft();
		else if (m_derivativeType == FROM_RIGHT_DERIVATIVE)
			generateCoefficientsRight();
	}

  /** Sets the spline's derivatives at the left border of the domain. */
	public void setLeftDerivative(double aValue) {
		m_derivative = aValue;
		m_derivativeType = FROM_LEFT_DERIVATIVE;
		generateCoefficientsLeft();
	}


  /** Sets the spline's derivatives at the right border of the domain. */
  public void setRightDerivative(double aValue) {
    m_derivative = aValue;
    m_derivativeType = FROM_RIGHT_DERIVATIVE;
    generateCoefficientsRight();
  }

	/** Generation fixing left derivative. */
	private void generateCoefficientsLeft() {
		m_coeffs_f[1] = m_derivative;
		for (int i = 2; i < m_coeffs_f.length; i++) {
			m_coeffs_f[i] =
				2
					* (m_yVals_f[i] - m_yVals_f[i - 1])
					/ (m_xVals_f[i] - m_xVals_f[i - 1])
					- m_coeffs_f[i
					- 1];
		}
	}

	/** Generation fixing right derivative. */
	private void generateCoefficientsRight() {
		m_coeffs_f[m_coeffs_f.length - 1] = m_derivative;
		for (int i = m_coeffs_f.length - 2; i > 0; i--) {
			m_coeffs_f[i] =
				2
					* (m_yVals_f[i] - m_yVals_f[i + 1])
					/ (m_xVals_f[i] - m_xVals_f[i + 1])
					- m_coeffs_f[i
					+ 1];
		}
	}

	protected double specificEvaluate(double x, int i) {
		int javaIndex = i - 1;
		double dxi = m_xVals[javaIndex + 1] - m_xVals[javaIndex];
		double ddyi = m_coeffs_f[i + 1] - m_coeffs_f[i];
		double xi = m_xVals[javaIndex];
		double yi = m_yVals[javaIndex];
		return yi
			+ m_coeffs_f[i] * (x - xi)
			+ ddyi / 2. / dxi * (x - xi) * (x - xi);
	}

	protected void specificEvaluate(MNumber xin, MNumber yout, int i) {
    yout.setDouble(specificEvaluate(xin.getDouble(), i));
	}
}
