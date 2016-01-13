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

package net.mumie.mathletfactory.math.algebra.op.test;

import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.number.MComplex;
import net.mumie.mathletfactory.math.number.MDouble;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * This testcase compares normalised mathematical expressions with expected values.
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public class NormalizeOpTest extends TestCase {

	public NormalizeOpTest(String name) {
		super(name);
	}

	public void testNormalize() {
		normalizeAndCompare("1-0", "1");
		normalizeAndCompare("x-0", "x");
		normalizeAndCompare("x-0*x", "x");
		normalizeAndCompare("2^0", "1");
		normalizeAndCompare("x^(0*3)", "1");
		normalizeAndCompare("-2(x-0)^3", "-2x^3", true);
		normalizeAndCompare("2*(0-x)^3", "2(-x)^3", true);
		normalizeAndCompare("(x-(x)^0)^3", "(x-1)^3");
		normalizeAndCompare("-2*(-1*x)^2", "-2x^2", true);
		normalizeAndCompare("-2*(-1*x)^3", "2x^3", true);
	}
	
	public void testCollapsePowerOfI() {
		normalizeAndCompare("i^2", "-1", MComplex.class);
		normalizeAndCompare("i^3", "-i", MComplex.class);
		normalizeAndCompare("i^4", "1", MComplex.class);
		normalizeAndCompare("i^5", "i", MComplex.class);
		normalizeAndCompare("i^-2", "-1", MComplex.class);
		normalizeAndCompare("i^-3", "i", MComplex.class);
		normalizeAndCompare("-2/i^3", "-2i", true, MComplex.class);
	}
  
  public void testSummarizeEqualMult() {
    normalizeAndCompare("(-x+1)*(-x+1)", "(-x+1)^2");
    normalizeAndCompare("(-x+1)^2*(-x-1)*(-x-1)", "(-x+1)^2*(-x-1)^2");
  }
	
	protected void normalizeAndCompare(String actual, String expected) {
		normalizeAndCompare(actual, expected, false);
	}
	
	protected void normalizeAndCompare(String actual, String expected, boolean normalize) {
		normalizeAndCompare(actual, expected, normalize, MDouble.class);
	}
	
	protected void normalizeAndCompare(String actual, String expected, Class numberClass) {
		normalizeAndCompare(actual, expected, false, numberClass);
	}
	
	protected void normalizeAndCompare(String actual, String expected, boolean normalize, Class numberClass) {
		Operation actualOp = new Operation(numberClass, actual, normalize);
		actualOp.normalize();
		Operation expectedOp = new Operation(numberClass, expected, normalize);
		Assert.assertEquals(expectedOp, actualOp);
	}
}
