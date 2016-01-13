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
import net.mumie.mathletfactory.math.number.MBigRational;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.numeric.Invert;
import net.mumie.mathletfactory.math.number.numeric.MatrixMultiplikation;

/**
 * JUnit testcase for all classes in
 * {@link net.mumie.mathletfactory.number.numeric.Invert}.
 * It uses
 * {@link net.mumie.mathletfactory.number.numeric.MatrixMultiplikationnxn MatrixMultiplikationnxn}
 * to test if A*A¯¹ is the identity matrix.
 *
 * @author Mrose
 * @mm.docstatus finished
 * @see  <a href="http://junit.sourceforge.net">JUnit</a>
 */
public class InvertTest extends TestCase {
  MNumber A[]
    = { new MDouble(7.452), new MDouble(252.670), new MDouble(27.054),
      new MDouble(-71.0), new MDouble(2.063), new MDouble(-6.025),
      new MDouble(3.057), new MDouble(-58.368), new MDouble(-5.63) };
  
  MNumber rationalA[]
    = { new MBigRational(1, 36), new MBigRational(2, 13), new MBigRational(5, 51),
      new MBigRational(17, 37), new MBigRational(2, 16), new MBigRational(33, 51),
      new MBigRational(3, 16), new MBigRational(2, 17), new MBigRational(6, 18) };
  
  MNumber[]  idDouble
    = { new MDouble(1.0), new MDouble(0.0), new MDouble(0.0),
      new MDouble(0.0), new MDouble(1.0), new MDouble(0.0),
      new MDouble(0.0), new MDouble(0.0), new MDouble(1.0) };
  
  MNumber[]  idRational
    = { new MBigRational(1, 1), new MBigRational(0, 1), new MBigRational(0, 1),
      new MBigRational(0, 1), new MBigRational(1, 1), new MBigRational(0, 1),
      new MBigRational(0, 1), new MBigRational(0, 1), new MBigRational(1, 1) };
  
  /**
   * Class constructor.
   */
  public InvertTest(String name) {
    super(name);
  }
  
  /**
   * Test of
   * {@link net.mumie.mathletfactory.number.numeric.Invert#invertByLUDecomposition invertByLUDecomposition}.
   */
  public void testInvertByLUDecomposition() {
    //    try {
    MNumber[] inverseA = Invert.invertByLUDecomposition(A);
    MNumber[] inverseArational = Invert.invertByLUDecomposition(rationalA);
    //    }
    //    catch( Exception e ) {
    //      e.printStackTrace();
    //    }
//
    MNumber[] resultA = MatrixMultiplikation.multinxn( A, inverseA);
    MNumber[] resultArational = MatrixMultiplikation.multinxn( rationalA, inverseArational);
    
    for (int i=0; i<9; i++) {
      Assert.assertEquals(idDouble[i].getDouble(), resultA[i].getDouble(), 1e-10);
      Assert.assertTrue(idRational[i].equals(resultArational[i]));
    }
  }
  
  /**
   * Test of
   * {@link net.mumie.mathletfactory.number.numeric.Invert#invertByGauss invertByGauss}.
   */
  public void testInvertByGauss() {
    //    try {
    MNumber[] inverseA = Invert.invertByGauss(A);
    MNumber[] inverseArational = Invert.invertByGauss(rationalA);
    //    }
    //    catch( Exception e ) {
    //      e.printStackTrace();
    //    }
    MNumber[] resultA = MatrixMultiplikation.multinxn( A, inverseA);
    MNumber[] resultArational = MatrixMultiplikation.multinxn( rationalA, inverseArational);
    
    for (int i=0; i<9; i++) {
      Assert.assertEquals(idDouble[i].getDouble(), resultA[i].getDouble(), 1e-10);
      Assert.assertTrue(idRational[i].equals(resultArational[i]));
    }
  }
  
  /**
   * Runs the test.
   *
   * @param    args                a  <code>String[]</code>
   *
   */
  public static void main(String args[]) {
    junit.textui.TestRunner.run(InvertTest.class);
  }
}



