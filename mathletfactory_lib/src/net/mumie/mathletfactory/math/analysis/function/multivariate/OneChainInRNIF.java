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

import net.mumie.mathletfactory.math.number.NumberTypeDependentIF;
import net.mumie.mathletfactory.math.set.FiniteBorelSet;
import net.mumie.mathletfactory.math.set.Interval;

/**
 * This class modelises the mathemtical 1-chain with values in a particular <b>R</b>
 * <sup>n</sup>.
 *
 * In our approach a 1-chain in <b>R</b><sup>n</sup> is a formal sum of mappings from
 * simple borel sets (finite unions of disjoint intervals) into the R^n:<br>
 * f<sub>i</sub>: B<sub>i</sub> &rarr; R<sup>n</sup>.<br>
 * Each such mapping is internally described by a vector valued function
 * (see {@link net.mumie.mathletfactory.math.analysis.function.multivariate.VectorFunctionOverBorelSetIF})
 * which defines the "pure" evaluation procedure and also holds the corresponding
 * {@link net.mumie.mathletfactory.math.set.FiniteBorelSet}.
 *
 * @author vossbeck
 * @mm.docstatus finished
 */
public interface OneChainInRNIF extends NumberTypeDependentIF{

	/**
	 * Returns the total number of defining elements for this <code>OneChainInRNIF</code>.
	 *
	 * Internally an array of
	 * {@link VectorFunctionOverBorelSetIF}s
	 * stored and this method returns the length of this array.
	 */
	public int getPartCount();


	/**
	 * Returns the total number of
	 * {@link net.mumie.mathletfactory.math.set.Interval}s on which this <code>OneChainInRNIF</code>
	 * will evaluate.
	 * <br>
	 * Each part of the 1-chain (that is each mapping of the formal sum)
	 * is formally defined to evaluate on
	 * a {@link net.mumie.mathletfactory.math.set.FiniteBorelSet} that is on a finte number of
	 * disjoint intervals. This method returns the total number of all these intervals.
	 */
	public int getAllIntervalCount();

	/**
	 * Returns the finite borel set on which the <code>index</code>th vector valued function
	 * will be evaluated.
	 *
	 * @param index is the index of the evaluation expression (a vector valued function)
	 */
	public FiniteBorelSet getBorelSetInPart(int index);

	/**
	 * This method returns the number of intervals belonging to the
	 * {@link net.mumie.mathletfactory.math.set.FiniteBorelSet} of the <code>index</code>th
	 * expression of the 1-chain.
	 *
	 * @param index the index of the evaluate expression (a vector valued function)
	 */
	public int getIntervalCountInPart(int index);


	/**
	 * Returns the <code>Interval</code> at position <code>intervalIndexInBorelSet</code>
	 * in the {@link net.mumie.mathletfactory.math.set.FiniteBorelSet} being the domain of the
	 * evaluation expression with index <code>indexOfPart</code>
	 *
	 * @param indexOfPart is the index of the evaluation expression (vector valued function)
	 * @param intervalIndexInBorelSet is the index of the <code>Interval</code> in the
	 * <code>FiniteBorelSet</code> of the expression requested.
	 */
	public Interval getInterval(int indexOfPart, int intervalIndexInBorelSet);


	/**
	 * Returns the <code>i</code>th evaluation expression of this 1-chain, which is modeled
	 * as {@link net.mumie.mathletfactory.math.analysis.function.multivariate.VectorFunctionOverRIF}.
	 */
	public VectorFunctionOverRIF getEvaluateExpressionInPart(int i);

}

