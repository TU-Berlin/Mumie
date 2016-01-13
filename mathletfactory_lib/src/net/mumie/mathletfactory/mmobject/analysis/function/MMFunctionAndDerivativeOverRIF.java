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

import net.mumie.mathletfactory.math.analysis.function.FunctionAndDerivativeOverRIF;

/**
 * This interface is implemented by all classes that represent (numerically) differentiable
 * functions over R.
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */
public interface MMFunctionAndDerivativeOverRIF extends FunctionAndDerivativeOverRIF {

  /**
   * Stores the numerically computed derivative for the specified <code>epsilon</code> in the given
   * function argument.  
   */
	public void getDerivativeByNumeric(final double epsilon, MMFunctionDefinedByExpression theDerivative);

  /**
   * Stores the numerically computed derivative of this function in the given
   * function argument.  
   */	
	public void getDerivativeByNumeric(MMFunctionDefinedByExpression theDerivative);

}
