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
import net.mumie.mathletfactory.math.number.MBigRational;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.numeric.Determinant;

/**
 * JUnit testcase for all classes in {@link net.mumie.mathletfactory.number.numeric.Determinant}.
 *
 * @author Mrose
 * @mm.docstatus finished
 * @see  <a href="http://junit.sourceforge.net">JUnit</a>
 */
public class DeterminantTest extends TestCase {
  
  private double EPSILON = 1e-12;
  
  /**
   * Class constructor.
   */
  public DeterminantTest(String name) {
    super(name);
  }
  
  /**
   * Test for
   * {@link net.mumie.mathletfactory.number.numeric.Determinant#detByLUDecomposition detByLUDecomposition}.
   *
   */
  public void testDetByLUDecomposition() {
    MNumber matrixA[]
      = { new MDouble(2.0), new MDouble(0.0), new MDouble(1.0), new MDouble(1.0), new MDouble(1.0),
        new MDouble(2.0), new MDouble(1.0), new MDouble(1.0), new MDouble(9.0), new MDouble(0.0),
        new MDouble(1.0), new MDouble(3.0), new MDouble(1.0), new MDouble(3.0), new MDouble(0.5),
        new MDouble(1.0), new MDouble(1.0), new MDouble(1.0), new MDouble(1.0), new MDouble(0.5),
        new MDouble(1.0), new MDouble(7.0), new MDouble(-2.0), new MDouble(0.0), new MDouble(1.0)};
    MNumber expectedA = new MDouble(-13.0);
    MNumber resultA = Determinant.detByLUDecomposition(matrixA);
    Assert.assertEquals(expectedA.getDouble(), resultA.getDouble(), EPSILON);
    Assert.assertEquals(Determinant.detByLUDecomposition(matrixA).getDouble(),
                        Determinant.det(matrixA).getDouble(), EPSILON);
    
    MNumber matrixB[]
      = { new MDouble(0.5), new MDouble(2.0), new MDouble(3.0), new MDouble(0.5), new MDouble(1.0), new MDouble(0.5),
        new MDouble(0.5), new MDouble(-2.0), new MDouble(-0.0), new MDouble(0.5), new MDouble(1.0), new MDouble(0.5),
        new MDouble(-0.5), new MDouble(2.0), new MDouble(-1.0), new MDouble(5.0), new MDouble(0.0), new MDouble(1.0),
        new MDouble(4.0), new MDouble(9.0), new MDouble(0.5), new MDouble(1.0), new MDouble(0.5), new MDouble(4.0),
        new MDouble(2.0), new MDouble(1.0), new MDouble(2.0), new MDouble(-10.0), new MDouble(1.0), new MDouble(1.0),
        new MDouble(5.0), new MDouble(1.0), new MDouble(0.5), new MDouble(0.0), new MDouble(0.0), new MDouble(1.0)};
    MNumber expectedB = new MDouble(-62.0);
    MNumber resultB = Determinant.detByLUDecomposition(matrixB);
    Assert.assertEquals(expectedB.getDouble(), resultB.getDouble(), EPSILON);
    Assert.assertEquals(Determinant.detByLUDecomposition(matrixB).getDouble(),
                        Determinant.det(matrixB).getDouble(), EPSILON);
    
    MNumber matrixC[]
      = { new MDouble(1.5), new MDouble(-2.0), new MDouble(0.3), new MDouble(0.0),
        new MDouble(2.0), new MDouble(3.0), new MDouble(4.0), new MDouble(0.0),
        new MDouble(0.0), new MDouble(1.0), new MDouble(0.25), new MDouble(0.0),
        new MDouble(0.0), new MDouble(0.0), new MDouble(0.0), new MDouble(1.0)};
    MNumber expectedC = new MBigRational(-131, 40);
    MNumber resultC = Determinant.detByLUDecomposition(matrixC);
    Assert.assertEquals(expectedC.getDouble(), resultC.getDouble(), EPSILON);
    Assert.assertEquals(Determinant.detByLUDecomposition(matrixC).getDouble(),
                        Determinant.det(matrixC).getDouble(), EPSILON);
    
    MNumber matrixD[]
      = { new MBigRational(1, 5), new MBigRational(-2, 1), new MBigRational(0, 3), new MBigRational(0, 1),
        new MBigRational(2, 1), new MBigRational(3, 1), new MBigRational(4, 1), new MBigRational(0, 1),
        new MBigRational(0, 1), new MBigRational(1, 1), new MBigRational(0, 25), new MBigRational(0, 1),
        new MBigRational(0, 1), new MBigRational(0, 1), new MBigRational(0, 1), new MBigRational(1, 1)};
    MNumber expectedD = new MBigRational(-4, 5);
    MNumber resultD = Determinant.detByLUDecomposition(matrixD);
    Assert.assertTrue(expectedD.equals(resultD));
    Assert.assertEquals(expectedD.getDouble(), resultD.getDouble(), EPSILON);
    Assert.assertEquals(Determinant.detByLUDecomposition(matrixD).getDouble(),
                        Determinant.det(matrixD).getDouble(), EPSILON);
    
    MNumber matrixE[]
      = { new MBigRational(-3, 2), new MBigRational(-4, 2), new MBigRational(3, 10), new MBigRational(0, 1),
        new MBigRational(-2, 1), new MBigRational(3, 1), new MBigRational(4, 1), new MBigRational(0, 1),
        new MBigRational(0, 1), new MBigRational(-2, -2), new MBigRational(1, 4), new MBigRational(0, 1),
        new MBigRational(0, 1), new MBigRational(0, 1), new MBigRational(0, 1), new MBigRational(1, 1)};
    MNumber expectedE = new MBigRational(131, 40);
    MNumber resultE = Determinant.detByLUDecomposition(matrixE);
    Assert.assertEquals(expectedE.getDouble(), resultE.getDouble(), EPSILON);
    Assert.assertTrue(expectedE.equals(resultE));
    Assert.assertEquals(Determinant.detByLUDecomposition(matrixE).getDouble(),
                        Determinant.det(matrixE).getDouble(), EPSILON);
    
    MNumber matrixF[]
      = { new MDouble(0.09), new MDouble(0.02),
        new MDouble(0.02), new MDouble(0.10) };
    MNumber expectedF = new MDouble(0.0086);
    MNumber resultF = Determinant.detByLUDecomposition(matrixF);
    Assert.assertEquals(expectedF.getDouble(), resultF.getDouble(), EPSILON);
    Assert.assertEquals(Determinant.detByLUDecomposition(matrixF).getDouble(),
                        Determinant.det(matrixF).getDouble(), EPSILON);
    
    MNumber matrixG[]
      = { new MBigRational(1, 1), new MBigRational(-36, 7),
        new MBigRational(0, 900), new MBigRational(31, 21) };
    MNumber expectedG = new MBigRational(31, 21);
    MNumber resultG = Determinant.detByLUDecomposition(matrixG);
    Assert.assertEquals(expectedG.getDouble(), resultG.getDouble(), EPSILON);
    Assert.assertTrue(expectedG.equals(resultG));
    Assert.assertEquals(Determinant.detByLUDecomposition(matrixG).getDouble(),
                        Determinant.det(matrixG).getDouble(), EPSILON);
    
  }
  
  /**
   * Test for
   * {@link net.mumie.mathletfactory.number.numeric.Determinant#det det}.
   *
   */
  public void testDet(){
    NumberMatrix matrix = new NumberMatrix(new MDouble().getClass(), 3, 3);
    matrix.setToIdentity();
    Assert.assertEquals(Determinant.det(matrix.getEntriesAsNumberRef()).getDouble(),1,0);
    matrix.setEntry(1,3,new MDouble(1));
    Assert.assertEquals(Determinant.det(matrix.getEntriesAsNumberRef()).getDouble(),1,0);
    matrix.setEntry(3,1,new MDouble(2));
    Assert.assertEquals(Determinant.det(matrix.getEntriesAsNumberRef()).getDouble(),-1,0);
  }
  
  /**
   * Runs the test.
   *
   * @param    args                a  <code>String[]</code>
   *
   */
  public static void main(String args[]) {
    junit.textui.TestRunner.run(DeterminantTest.class);
  }
}



