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

package net.mumie.mathletfactory.math.number.numeric.numericdouble.test;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.mumie.mathletfactory.math.number.numeric.numericdouble.DeterminantDouble;

/**
 * JUnit testcase for all classes in {@link net.mumie.mathletfactory.number.numeric.DeterminantDouble}.
 *
 * @author Mrose
 * @mm.docstatus finished
 * @see  <a href="http://junit.sourceforge.net">JUnit</a>
 */
public class DeterminantDoubleTest extends TestCase {
  
  private double EPSILON = 1e-12;
  
  /**
   * Class constructor.
   */
  public DeterminantDoubleTest(String name) {
    super(name);
  }
  
  /**
   * Test for
   * {@link net.mumie.mathletfactory.number.numeric.numericdouble.DeterminantDouble#detByLUDecomposition detByLUDecomposition}.
   *
   */
  public void testDetByLUDecomposition() {
    double matrixA[]
      = { (2.0), (0.0), (1.0), (1.0), (1.0),
        (2.0), (1.0), (1.0), (9.0), (0.0),
        (1.0), (3.0), (1.0), (3.0), (0.5),
        (1.0), (1.0), (1.0), (1.0), (0.5),
        (1.0), (7.0), (-2.0), (0.0), (1.0)};
    double expectedA = (-13.0);
    double resultA = DeterminantDouble.detByLUDecomposition(matrixA);
    Assert.assertEquals(expectedA, resultA, EPSILON);
    Assert.assertEquals(DeterminantDouble.detByLUDecomposition(matrixA),
                        DeterminantDouble.det(matrixA), EPSILON);
    
    double matrixB[]
      = { (0.5), (2.0), (3.0), (0.5), (1.0), (0.5),
        (0.5), (-2.0), (-0.0), (0.5), (1.0), (0.5),
        (-0.5), (2.0), (-1.0), (5.0), (0.0), (1.0),
        (4.0), (9.0), (0.5), (1.0), (0.5), (4.0),
        (2.0), (1.0), (2.0), (-10.0), (1.0), (1.0),
        (5.0), (1.0), (0.5), (0.0), (0.0), (1.0)};
    double expectedB = (-62.0);
    double resultB = DeterminantDouble.detByLUDecomposition(matrixB);
    Assert.assertEquals(expectedB, resultB, EPSILON);
    Assert.assertEquals(DeterminantDouble.detByLUDecomposition(matrixB),
                        DeterminantDouble.det(matrixB), EPSILON);
    
    double matrixC[]
      = { (1.5), (-2.0), (0.3), (0.0),
        (2.0), (3.0), (4.0), (0.0),
        (0.0), (1.0), (0.25), (0.0),
        (0.0), (0.0), (0.0), (1.0)};
    double expectedC = -131.0/40.0;
    double resultC = DeterminantDouble.detByLUDecomposition(matrixC);
    Assert.assertEquals(expectedC, resultC, EPSILON);
    Assert.assertEquals(DeterminantDouble.detByLUDecomposition(matrixC),
                        DeterminantDouble.det(matrixC), EPSILON);
    
    double matrixD[]
      = { (1.0/5.0), (-2.0/1.0), (0.0/3.0), (0.0/1.0),
        (2.0/1.0), (3.0/1.0), (4.0/1.0), (0.0/1.0),
        (0.0/1.0), (1.0/1.0), (0.0/25.0), (0.0/1.0),
        (0.0/1.0), (0.0/1.0), (0.0/1.0), (1.0/1.0)};
    double expectedD = (-4.0/5.0);
    double resultD = DeterminantDouble.detByLUDecomposition(matrixD);
    Assert.assertEquals(expectedD, resultD, EPSILON);
    Assert.assertEquals(expectedD, resultD, EPSILON);
    Assert.assertEquals(DeterminantDouble.detByLUDecomposition(matrixD),
                        DeterminantDouble.det(matrixD), EPSILON);
    
    double matrixE[]
      = { (-3.0/2.0), (-4.0/2.0), (3.0/10.0), (0.0/1.0),
        (-2.0/1.0), (3.0/1.0), (4.0/1.0), (0.0/1.0),
        (0.0/1.0), (-2.0/-2.0), (1.0/4.0), (0.0/1.0),
        (0.0/1.0), (0.0/1.0), (0.0/1.0), (1.0/1.0)};
    double expectedE = (131.0/40.0);
    double resultE = DeterminantDouble.detByLUDecomposition(matrixE);
    Assert.assertEquals(expectedE, resultE, EPSILON);
    Assert.assertEquals(expectedE, resultE, EPSILON);
    Assert.assertEquals(DeterminantDouble.detByLUDecomposition(matrixE),
                        DeterminantDouble.det(matrixE), EPSILON);
    
    double matrixF[]
      = { (0.09), (0.02),
        (0.02), (0.10) };
    double expectedF = (0.0086);
    double resultF = DeterminantDouble.detByLUDecomposition(matrixF);
    Assert.assertEquals(expectedF, resultF, EPSILON);
    Assert.assertEquals(DeterminantDouble.detByLUDecomposition(matrixF),
                        DeterminantDouble.det(matrixF), EPSILON);
    
    double matrixG[]
      = { (1.0/1.0), (-36.0/7.0),
        (0.0/900.0), (31.0/21.0) };
    double expectedG = (31.0/21.0);
    double resultG = DeterminantDouble.detByLUDecomposition(matrixG);
    Assert.assertEquals(expectedG, resultG, EPSILON);
    Assert.assertEquals(expectedG, resultG, EPSILON);
    Assert.assertEquals(DeterminantDouble.detByLUDecomposition(matrixG),
                        DeterminantDouble.det(matrixG), EPSILON);
    
  }
  
  /**
   * Runs the test.
   *
   * @param    args                a  <code>String[]</code>
   *
   */
  public static void main(String args[]) {
    junit.textui.TestRunner.run(DeterminantDoubleTest.class);
  }
}



