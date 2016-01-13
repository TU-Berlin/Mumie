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

package net.mumie.mathletfactory.math.util;

/**
 * Library for mathematical utilities.
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */
public class MathUtilLib {

	public static final double log10 = Math.log(10);

	/**
	 * Returns ln(<code>value</code>)/ln(10)
	 * 
	 * @param value
	 * @return ln(<code>value</code>)/ln(10)
	 */
	public static double log10(double value) {
		return Math.log(value) / log10;
	}

	/**
	 * Returns the signum of the entered <code>value</code>.
	 *
	 * @param value 
	 * @return int which is -1 if <code>value</code> is negative, 
	 * 0 if <code>value</code> is zero, and 1 if <code>value</code> is positive.
	 */
	public static int sign(double value) {
		return value < 0 ? -1 : value == 0 ? 0 : 1;
	}

	/**
	 * Returns true if the difference between <code>x</code> and 
	 * <code>y</code> is less than <code>eps</code>.
	 * 
	 * @param x the first parameter to be compared.
	 * @param y the second parameter to be compared.
	 * @param eps the bound for the difference between <code>x</code> and 
	 * <code>y</code>.
	 * @return a boolean which is true if the difference between <code>x</code> and 
	 * <code>y</code> is less than <code>eps</code>.
	 */
	public static boolean equalModEpsilon(double x, double y, double eps) {
		if (x <= y)
			return y - x < eps;
		else
			return x - y < eps;
	}

}
