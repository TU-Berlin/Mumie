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

package net.mumie.mathletfactory.math.analysis.function.test;

import net.mumie.mathletfactory.math.geom.affine.Affine2DPoint;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.mmobject.analysis.function.MMFunctionDefinedBySamples;

/**
 * @author vossbeck
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class FunctionDefinedBySamplesTest {
	
	private static Class m_numberClass;
	
	public static void main(String[] args){
		m_numberClass = MDouble.class;
		testEvaluateOnMMNumber();
	}
	
	public static void testEvaluateOnMMNumber(){

		int nSamples = 10;
		Affine2DPoint[] samplePoints = new Affine2DPoint[nSamples];
		for(int i=0; i<nSamples; i++){
			samplePoints[i] = new Affine2DPoint(m_numberClass,i,i/2.);
		}
		MMFunctionDefinedBySamples f = new MMFunctionDefinedBySamples(samplePoints);
		MNumber xin = NumberFactory.newInstance(m_numberClass);
		MNumber yout = NumberFactory.newInstance(m_numberClass);
		
		System.out.println("The matrix of samples:\n");
		System.out.println(f);
		
		double dx = 0.5;
		double xStart = -1;
		double xMax = samplePoints[nSamples-1].getXAsDouble();
		double x = xStart;
	  while(x <= xMax+dx){
	  	xin.setDouble(x);
	  	f.evaluate(xin,yout);
	  	System.out.println(xin+" --> "+yout);
	  	x += dx;
	  }
	}

}
