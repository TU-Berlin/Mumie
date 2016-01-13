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

/**
 * TestFindIntervals.java
 *
 * @author Created by Omnicore CodeGuide
 */

package net.mumie.mathletfactory.math.util.test;

import net.mumie.mathletfactory.math.util.FunctionRenderLib;

public class TestFindIntervals {
		
	public static void main(String[] args) {
	    TempTestFunction func = new TempTestFunction();
		FunctionRenderLib test = new FunctionRenderLib();
		double[][] outLimits = new double[10000][2];
		int M = FunctionRenderLib.findIntervals(func, -10., 10., 19, outLimits);
		
		System.out.println("M = " + M);
		for(int i=0; i < M; i++) {
			System.out.println("["+outLimits[i][0]+" ; "+outLimits[i][1]+"]");
		}
	}
	
}

