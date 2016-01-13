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
 * TestNumberVectorSpacejava.java
 *
 * @author Created by Omnicore CodeGuide
 */

package net.mumie.mathletfactory.math.algebra.test;



import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.algebra.linalg.NumberVectorSpace;
import net.mumie.mathletfactory.math.number.MDouble;

public class TestNumberVectorSpace {
  
  public static void main (String[] args) {
    
    NumberTuple b1 = new NumberTuple(MDouble.class, new Object[]{new MDouble(2),new MDouble(0)});
    NumberTuple b2 = new NumberTuple(MDouble.class, new Object[]{new MDouble(0),new MDouble(2)});
    NumberVectorSpace V1 = new NumberVectorSpace(new NumberTuple[]{b1,b2});
    System.out.println("VectorSpace V1 (initial): dimension = "+V1.getDimension()+", envDimension = "+V1.getEnvDimension() );
		for(int i=1;i<=V1.getDimension(); i++)
			System.out.println("absolute coordinates of default base vector "+i+" are\n"+V1.getCoordinatesOfDefBaseInSurroundingSpace(i));
 

		NumberMatrix baseMatrix = new NumberMatrix(MDouble.class,
				                                       2,
				new Object[]{new MDouble(1),new MDouble(0),new MDouble(1),new MDouble(1)});
//		// check behaviour for handing over a linear dependent base
//		NumberTuple b3 = new NumberTuple(MDouble.class,new Object[]{new MDouble(1), new MDouble(1)});
//		NumberVectorSpace V2 = new NumberVectorSpace(new NumberTuple[]{b1,b2, b3});
		
  }
  
}

