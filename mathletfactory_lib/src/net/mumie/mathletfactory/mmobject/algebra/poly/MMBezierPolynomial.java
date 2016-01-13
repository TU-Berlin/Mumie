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

package net.mumie.mathletfactory.mmobject.algebra.poly;

import net.mumie.mathletfactory.math.number.MNumber;

/**
 * This class represents a bezier polynomial, i.e. $\sum_{i=0}^n b_i B_i^n(t)$ with
 * $B_i^n$ being the i-th Bernstein polynomial of degree n and $b_i$ being the 
 * control points .
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */
public class MMBezierPolynomial extends MMPolynomial {

  //numbers for temporary computation
	private MNumber[] m_tmpcalc; 
	private MNumber m_dummy, m_dimmy, m_dammy;
	
  /** Constructs the bezier polynomial from the given coefficients. */	
	public MMBezierPolynomial(MNumber[] coeff) {
		super(coeff[0].getClass(), coeff.length - 1);
		for (int i = 0; i < coeff.length; ++i) {
			m_standardCoefficients.setEntry(i + 1, coeff[i]);
		}

		int d = coeff.length * (coeff.length + 1) / 2 - coeff.length;
		m_tmpcalc = new MNumber[d];
		for (int i = 0; i < coeff.length; ++i) {
			m_tmpcalc[i] = m_standardCoefficients.getEntryRef(i + 1);
		}
		for (int i = coeff.length; i < d; ++i) {
			m_tmpcalc[i] = (MNumber) coeff[0].copy();
		}
		m_dummy = (MNumber) coeff[0].copy();
		m_dimmy = (MNumber) coeff[0].copy();
	}

	/** Creates the bezier polynomial with the given degree and all coefficients set to zero. */
	public MMBezierPolynomial(Class entry, int deg) {
		super(entry, deg);
		init();
	}

	private void init() {
		int d = m_standardCoefficients.getDimension() * (m_standardCoefficients.getDimension() + 1) / 2;
		m_tmpcalc = new MNumber[d];
		for (int i = 0; i < m_standardCoefficients.getDimension(); ++i) {
			m_tmpcalc[i] = m_standardCoefficients.getEntryRef(i + 1);
		}
		for (int i = m_standardCoefficients.getDimension(); i < d; ++i) {
			m_tmpcalc[i] = m_standardCoefficients.getEntry(1);
		}
		m_dummy = m_standardCoefficients.getEntry(1);
		m_dimmy = m_standardCoefficients.getEntry(1);
		m_dammy = m_standardCoefficients.getEntry(1);
	}

  /** Sets the given coefficients as new coefficients for the bezier polynomial. */
	public MMBezierPolynomial setCoefficients(MNumber[] coeff) {
		for (int i = 0; i < m_standardCoefficients.getDimension(); ++i) {
			m_standardCoefficients.getEntryRef(i).set(coeff[i]);
		}
		return this;
	}

  /** Sets the given coefficient as new coefficient of index <code>i</code> for the bezier polynomial. */
	public MMBezierPolynomial setCoefficient(int i, MNumber x) {
		m_standardCoefficients.getEntryRef(i + 1).set(x);
		return this;
	}

	/**  Evaluation of Bezier polynomial based on Neville type algorithm. */
	public void evaluate(MNumber in, MNumber out) {
		int nn = m_standardCoefficients.getDimension() - 1;
		int kk1 = 0;
		int kk2 = m_standardCoefficients.getDimension();
		out.setDouble(1.);
		out.sub(in); // out = 1-t
		for (int i = 0; i < m_standardCoefficients.getDimension() - 1; ++i) {
			for (int j = 0; j < nn; ++j) {
				m_tmpcalc[kk2 + j].mult(out, m_tmpcalc[kk1 + j]);
				m_dummy.mult(in, m_tmpcalc[kk1 + 1 + j]);
				m_tmpcalc[kk2 + j].add(m_dummy);
			}
			kk1 = kk2;
			kk2 = kk2 + nn;
			--nn;
		}
		out.set(m_tmpcalc[m_tmpcalc.length - 1]);
	}

  /**  Evaluation of Bezier polynomial based on Neville type algorithm. */
	public double evaluate(double x) {
		m_dimmy.setDouble(x);
		evaluate(m_dimmy, m_dammy);
		return m_dammy.getDouble();
	}

}
