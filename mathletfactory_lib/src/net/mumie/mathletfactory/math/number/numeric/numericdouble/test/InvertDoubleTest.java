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
import net.mumie.mathletfactory.math.number.numeric.numericdouble.InvertDouble;
import net.mumie.mathletfactory.math.number.numeric.numericdouble.MatrixMultiplikationDouble;

/**
 * JUnit testcase for all classes in
 * {@link net.mumie.mathletfactory.number.numeric.numericdouble.InvertDouble}.
 * It uses
 * {@link net.mumie.mathletfactory.number.numeric.numericdouble.MatrixMultiplikationnxnDouble MatrixMultiplikationnxnDouble}
 * to test if A*A¯¹ is the identity matrix.
 *
 * @author Mrose
 * @mm.docstatus finished
 * @see  <a href="http://junit.sourceforge.net">JUnit</a>
 */
public class InvertDoubleTest extends TestCase {
  double A[]
    = { 51.0, 0.54, -66.0,
      -1.456, 2.64, 3.998,
      3.8, -7.6, -5.55426};
  
  double[]  id
    = { 1.0, 0.0, 0.0,
      0.0, 1.0, 0.0,
      0.0, 0.0, 1.0 };
  
  /**
   * Class constructor.
   */
  public InvertDoubleTest(String name) {
    super(name);
  }
  
  /**
   * Test of
   * {@link net.mumie.mathletfactory.number.numeric.numericdouble.InvertDouble#invertByGauss invertByGauss}.
   */
  public void testInvertByGauss() {
    double[] inverseA = new double[9];
    //    try {
    inverseA = InvertDouble.invertByGauss(A);
    //    }
    //    catch( Exception e ) {
    //      e.printStackTrace();
    //    }
    double[] resultA = MatrixMultiplikationDouble.multinxn(A, inverseA);
    for (int i=0; i<9; i++) {
      Assert.assertEquals(id[i], resultA[i], 1e-10);
    }
  }
  
  /**
   * Test of
   * {@link net.mumie.mathletfactory.number.numeric.numericdouble.InvertDouble#invertByLUDecomposition invertByLUDecomposition}.
   */
  public void testInvertByLUDecomposition() {
    double[] inverseA = new double[9];
    //    try {
    inverseA = InvertDouble.invertByLUDecomposition(A);
    //    }
    //    catch( Exception e ) {
    //      e.printStackTrace();
    //    }
    double[] resultA = MatrixMultiplikationDouble.multinxn(A, inverseA);
    for (int i=0; i<9; i++) {
      Assert.assertEquals(id[i], resultA[i], 1e-10);
    }
  }
  /**
   * Runs the test.
   *
   * @param    args                a  <code>String[]</code>
   *
   */
  public static void main(String args[]) {
    junit.textui.TestRunner.run(InvertDoubleTest.class);
  }
}



