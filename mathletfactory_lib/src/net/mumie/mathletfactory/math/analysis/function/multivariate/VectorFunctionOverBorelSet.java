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
import net.mumie.mathletfactory.math.analysis.function.FunctionOverBorelSetIF;
import net.mumie.mathletfactory.math.analysis.function.FunctionOverRIF;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.set.FiniteBorelSet;
import net.mumie.mathletfactory.math.set.Interval;

/**
 * A basic adapter implementation of {@link VectorFunctionOverBorelSetIF} using an
 * {@link VectorFunctionOverRIF} expression for the evaluation.
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */
public class VectorFunctionOverBorelSet
	implements VectorFunctionOverBorelSetIF {

	/**
	 * This interface holds the real evaluate expression.
	 */
	private VectorFunctionOverRIF m_func;

	/**
	 * The domain is a finite borel set.
	 */
	private FiniteBorelSet m_domain;

	/**
	 * This is the numberClass on which this vectorfunction is based on,
	 * observe that m_domain and m_func must be of this entry class.
	 */
	private Class m_entryClass;

	public VectorFunctionOverBorelSet(
		VectorFunctionOverRIF func,
		FiniteBorelSet domain) {
		m_func = func.deepCopy();
		m_domain = domain.deepCopy();
		m_entryClass = func.getNumberClass();
	}

  /** Constructs this vector function with the given evaluation expression and with R as domain. */
	public VectorFunctionOverBorelSet(VectorFunctionOverRIF func) {
		this(func, FiniteBorelSet.getRealAxis(func.getNumberClass()));
	}

  /** Constructs this vector function with the given evaluation expression and domain. */
	public VectorFunctionOverBorelSet(
		VectorFunctionOverRIF func,
		Interval domain) {
		this(func, new FiniteBorelSet(domain));
	}

  /** Constructs this vector function from the given evaluation expressions and their (assumend identical) borel sets. */
	public VectorFunctionOverBorelSet(final FunctionOverBorelSetIF[] realFunctions) {
		m_entryClass = realFunctions[0].getNumberClass();
		//expecting all are of same type
		m_domain = realFunctions[0].getBorelSet().deepCopy();
		//TODO:intersection is necessary
		m_func =
			new VectorFunctionOverRAdapter(
				realFunctions[0].getNumberClass(),
				realFunctions.length) {
			private int dim = realFunctions.length;
			private FunctionOverRIF[] evaluators = realFunctions;
			//should be a deep copy
			public void evaluate(double xin, double yout[]) {
				for (int i = 0; i < dim; i++)
					yout[i] = evaluators[i].evaluate(xin);
			}
		};
	}

  /** Constructs this vector function from the given evaluation expressions and the given domain. */
  public VectorFunctionOverBorelSet(
    Class entryClass,
    final FunctionOverRIF[] realValuedFunctions,
    final FiniteBorelSet domain) {
    m_entryClass = entryClass;
    m_func =
      new VectorFunctionOverRAdapter(
        realValuedFunctions[0].getNumberClass(),
        realValuedFunctions.length) {
      private int dim = realValuedFunctions.length;
      private FunctionOverRIF[] evaluators = realValuedFunctions;
      //should be a deep copy
      public void evaluate(double xin, double yout[]) {
        for (int i = 0; i < dim; i++)
          yout[i] = evaluators[i].evaluate(xin);
      }
    };
    m_domain = domain.deepCopy();
  }
  
  /** Constructs this vector function from the given evaluation expressions and the given domain. */
	public VectorFunctionOverBorelSet(
		Class entryClass,
		final FunctionOverRIF[] realValuedFunctions,
		final Interval interval) {
      this(entryClass, realValuedFunctions, new FiniteBorelSet(interval));
	}

  /** Copy constructor. */
	public VectorFunctionOverBorelSet(VectorFunctionOverBorelSet aVectorFunction) {
		m_entryClass = aVectorFunction.getNumberClass();
		m_domain = aVectorFunction.getBorelSet().deepCopy();
		// should also be deep copy, but I dont know how to realise...
		m_func = aVectorFunction.m_func;
	}

	public void evaluate(double xin, double[] yout) {
		m_func.evaluate(xin, yout);
	}

	public FiniteBorelSet getBorelSet() {
		return m_domain;
	}

  public void setBorelSet(FiniteBorelSet set) {   
    m_domain = set;
  }

	public VectorFunctionOverRIF deepCopy() {
		return new VectorFunctionOverBorelSet(this);
	}

	public void evaluate(MNumber xin, NumberTuple yout) {
		m_func.evaluate(xin, yout);
	}

	public Class getNumberClass() {
		return m_entryClass;
	}

	public int getDimension() {
		return m_func.getDimension();
	}
}
