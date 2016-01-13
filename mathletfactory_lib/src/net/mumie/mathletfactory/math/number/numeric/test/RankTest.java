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
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.numeric.MatrixRank;


/**
 *  This class tests the mathematical correctness of
 *  {@link net.mumie.mathletfactory.number.numeric.MatrixRank}.
 * 
 *  @see  <a href="http://junit.sourceforge.net">JUnit</a>
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class RankTest extends TestCase
{
  public RankTest(String name) {
    super(name);
  }
  
  /** 
   * Tests {@link net.mumie.mathletfactory.number.numeric.MatrixRank#rank}.
   */
  public void testRank(){
    
    // a matrix, that has 3 linear independent row vectors:
    double[] coeffs =
    {    0, -3, -6,  4,  9,
        -1, -2, -1,  3,  1,
        -2, -3,  0,  3, -1,
        1,  4,  5, -9, -7};
    
    MDouble[] inputMatrix = new MDouble[coeffs.length];
    MDouble[] identity = new MDouble[5*5];
    
    // set coeffs
    for(int i=0; i<5*5; i++) {
      identity[i] = new MDouble( i%6==0 ? 1 : 0);
      if(i<coeffs.length)
        inputMatrix[i] = new MDouble(coeffs[i]);
    }
    
    // a NumberMatrix containing the rank-3 Matrix above
    NumberMatrix nMatrix = new NumberMatrix(inputMatrix[0].getClass(), 5, inputMatrix);
    
    Assert.assertEquals(MatrixRank.rank(inputMatrix,4,5),3);
    Assert.assertEquals(MatrixRank.rank(new NumberMatrix(MDouble.class, 5, inputMatrix).transposed()),3);
    Assert.assertEquals(MatrixRank.rank(nMatrix),3);
    Assert.assertEquals(MatrixRank.rank(identity,5,5),5);
  }
  
  public static void main(String args[]) {
    junit.textui.TestRunner.run(RankTest.class);
  }
}

