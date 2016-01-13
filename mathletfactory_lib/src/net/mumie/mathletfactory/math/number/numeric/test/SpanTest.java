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

package net.mumie.mathletfactory.math.number.numeric.test;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.numeric.Span;


/**
 *  Tests the mathematical correctness of 
 *  {@link net.mumie.mathletfactory.number.numeric.Span}.
 * 
 *  @see  <a href="http://junit.sourceforge.net">JUnit</a>
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class SpanTest extends TestCase
{
  public SpanTest(String name) {
		super(name);
	}
	
  /**
   * Tests {@link net.mumie.mathletfactory.number.numeric.Span#span}.
   */
	public void testSpan(){
		
		// a matrix, that has 3 linear independent row vectors:
		double[] coeffs =
		{    0, -3, -6,  4,  9,
				-1, -2, -1,  3,  1,
				-2, -3,  0,  3, -1,
				1,  4,  5, -9, -7};
		
		MDouble[] inputMatrix = new MDouble[coeffs.length];
	  MDouble[] identity = new MDouble[5*5];
		NumberTuple[] vectors = new NumberTuple[5];

				// set coeffs
		for(int j=0; j<5; j++){
		  vectors[j] = new NumberTuple(new MDouble().getClass(), 4);
		  
			for(int i=0; i<4; i++){
			  identity[i*5+j] = new MDouble( i==j ? 1 : 0);
			  vectors[j].setEntry(i+1, new MDouble(coeffs[i*5+j]));
			  inputMatrix[i*5+j] = new MDouble(coeffs[i]);
					}
				}

	
		// a NumberMatrix containing the rank-3 Matrix above
		NumberMatrix nMatrix = new NumberMatrix(inputMatrix[0].getClass(), 5, inputMatrix);
		NumberTuple[] span = Span.span(vectors);
		//System.out.println(span[0]+" " +span[1]+" "+span[2]+" "+span.length);
		Assert.assertEquals(span.length,3);
		Assert.assertEquals(span[2].getEntry(2).getDouble(), 4, 0);
				
		// the first, the second and the fourth vectors serve as basis vectors
	  // for span(vectors)
		Assert.assertEquals(Span.spanIndices(vectors)[0], 0);
		Assert.assertEquals(Span.spanIndices(vectors)[1], 1);
		Assert.assertEquals(Span.spanIndices(vectors)[2], 3);

		}
		
		/*
		public static void main(String[] args){
			new SpanTest("test").testSpan();
		}
		 */
}

