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
import net.mumie.mathletfactory.math.number.MDouble;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * This testcase compares mathematical expressions without an explicit normalisation.
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public class EqualityOpTest extends TestCase {

	public EqualityOpTest(String name) {
		super(name);
	}

	public void testEquality() {
		compare("2x", "2*x");
		compare("-2*(x)^3", "-2x^3");
		compare("-x^3", "(-x)^3");
	}
	
	protected void compare(String actual, String expected) {
		compare(actual, expected, false);
	}
	
	protected void compare(String actual, String expected, boolean normalize) {
		compare(actual, expected, normalize, MDouble.class);
	}
	
	protected void compare(String actual, String expected, boolean normalize, Class numberClass) {
		Operation actualOp = new Operation(numberClass, actual, normalize);
		Operation expectedOp = new Operation(numberClass, expected, normalize);
		Assert.assertEquals(expectedOp, actualOp);
	}
}
