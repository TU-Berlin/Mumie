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

package net.mumie.mathletfactory.math.analysis.function.multivariate;

import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.number.MNumber;

/** 
 * A basic implementation of {@link VectorFunctionOverRIF}. 
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */
public abstract class VectorFunctionOverRAdapter implements VectorFunctionOverRIF {

	private Class m_numberClass;

	private int m_dimension;

  /** Constructs this adapter with the given number class and dimension. */
	public VectorFunctionOverRAdapter(Class entryClass, int dimension) {
		m_numberClass = entryClass;
		m_dimension = dimension;
	}
	
	public int getDimension() {
		return m_dimension;
	}

  /** Calls {@link #evaluate(double xin, double[] yout)}. */
	public void evaluate(MNumber xin, NumberTuple yout) {
		double[] tmp = new double[yout.getDimension()];
		evaluate(xin.getDouble(), tmp);
		for (int i = 0; i < getDimension(); i++) {
			yout.getEntryRef(i + 1).setDouble(tmp[i]);
		}
	}

	public Class getNumberClass() {
		return m_numberClass;
	}

  /** Implement this method for the actual evaluation. */
	public abstract void evaluate(double xin, double[] yout);

	public VectorFunctionOverRIF deepCopy() {
		//TODO: to be modified
		return this;
	}

}
