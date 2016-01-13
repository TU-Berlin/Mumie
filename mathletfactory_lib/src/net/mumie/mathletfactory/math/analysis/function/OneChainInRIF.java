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

package net.mumie.mathletfactory.math.analysis.function;

import net.mumie.mathletfactory.math.set.FiniteBorelSet;
import net.mumie.mathletfactory.math.set.Interval;

/**
 * This interface models the 1-chain in R, i.e. a formal sum of different real valued function 
 * ("components") f_i: B_i -> R, where each B_i is a (finite) borel set of real valued 
 * Intervals. The 1-chain is used as a generalisation of a real valued function that allows 
 * also piecewise defined Functions.
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */
public interface OneChainInRIF {

  /** Returns the number of components for this one-chain. I.e. the number i of different expressions f_i. */
	public int getComponentsCount();

  /** Returns the number of intervals */
	public int getAllIntervalCount();

  /** Returns the number of intervals (of the borel set) for the chosen component f_i. */
	public int getIntervalCountInComponent(int indexOfComponent);

  /** Returns a specific interval of the chosen component f_i. */
	public Interval getInterval(
		int indexOfComponent,
		int indexOfIntervalInComponent);

  /** Returns the borel set of the chosen component f_i. */
	public FiniteBorelSet getBorelSetInComponent(int indexOfComponent);

  /** Returns the expression of the chosen component f_i. */
	public FunctionOverRIF getEvaluateExpressionInComponent(int indexOfComponent);

}
