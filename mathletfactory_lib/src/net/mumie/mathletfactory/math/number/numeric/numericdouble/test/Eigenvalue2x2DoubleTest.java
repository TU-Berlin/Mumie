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
import net.mumie.mathletfactory.math.number.numeric.numericdouble.Eigenvalue2x2Double;
import net.mumie.mathletfactory.math.number.numeric.numericdouble.MatrixMultiplikationDouble;
/**
 * JUnit testcase for all classes in
 * {@link net.mumie.mathletfactory.number.numeric.numericdouble.Eigenvalue2x2Double}.
 *
 * @author Mrose
 * @mm.docstatus finished
 * @see <a href="http://junit.sourceforge.net">JUnit</a>
 */
public class Eigenvalue2x2DoubleTest extends TestCase{
  
  /**
   * Class constructor.
   */
  public Eigenvalue2x2DoubleTest(String name){
    super(name);
  }
  
  /**
   * Test for
   * {@link net.mumie.mathletfactory.number.numeric.numericdouble.Eigenvalue2x2Double#realEigenvaluesExist realEigenvaluesExist}.
   */
  public void testRealEigenvaluesExist(){
    double matrix[] = {1.0, -1.0, 2.0, -1.0};
    boolean expected = false;
    boolean result = Eigenvalue2x2Double.realEigenvaluesExist(matrix);
    Assert.assertEquals(expected, result);
  }
  
  /**
   * Test for
   * {@link net.mumie.mathletfactory.number.numeric.numericdouble.Eigenvalue2x2Double#eigenvalue eigenvalue}.
   */
  public void testEigenvalue(){
    double[] matrix = {1.0, 1.0, 2.0, 0.0};
    double result1 = Eigenvalue2x2Double.eigenvalue(matrix)[0];
    double result2 = Eigenvalue2x2Double.eigenvalue(matrix)[1];
    double expected1 = -1.0;
    double expected2 = 2.0;
    Assert.assertEquals(expected1, result1, 1e-12);
    Assert.assertEquals(expected2, result2, 1e-12);
    
  }
  
  /**
   * Test for
   * {@link net.mumie.mathletfactory.number.numeric.numericdouble.Eigenvalue2x2Double#eigenvector eigenvector}.
   */
  public  void testEigenvector(){
    double[] matrix = {7.0, 0.0, 0.0, 3.0};
    double[] expected1 = new double[2];
    double[] expected2 = new double[2];
    double[] result1 = {Eigenvalue2x2Double.eigenvector(matrix)[0],
        Eigenvalue2x2Double.eigenvector(matrix)[1]};
    double[] result2 = {Eigenvalue2x2Double.eigenvector(matrix)[2],
        Eigenvalue2x2Double.eigenvector(matrix)[3]};
    expected1[0] =0.0;
    expected1[1] =1.0;
    expected2[0] =-1.0;
    expected2[1] =0.0;
    Assert.assertEquals(expected1[0], result1[0], 1e-12);
    Assert.assertEquals(expected1[1], result1[1], 1e-12);
    Assert.assertEquals(expected2[0], result2[0], 1e-12);
    Assert.assertEquals(expected2[1], result2[1], 1e-12);
  }
  
  /**
   * Test for
   * {@link net.mumie.mathletfactory.number.numeric.numericdouble.Eigenvalue2x2Double#eigenvalue eigenvalue}
   * and
   * {@link net.mumie.mathletfactory.number.numeric.numericdouble.Eigenvalue2x2Double#eigenvector eigenvector}.
   */
  public void testEigenvalueAndEigenvector() {
    double A[] = {1.0, 1.0, 0.0, 3.0};
    
    int n = (int)Math.sqrt(A.length);
    double[] AEW = Eigenvalue2x2Double.eigenvalue(A);  // Vektor der EW (n x 1)
    double[] AEV = Eigenvalue2x2Double.eigenvector(A); // Matrix der EV (n x n)
    double[] EV = new double[n];
    double[] produktA_EV = new double[n];
    double[] produktEV_EW  = new double[n];
    
    //Test whether the eigenvectors are positive oriented:
    double determinante = DeterminantDouble.det(AEV);
    Assert.assertEquals(determinante/Math.abs(determinante), 1.0, 1e-12);
    
    //Test whether the eigenvalues are sorted:
    Assert.assertTrue(AEW[0]<=AEW[1]|| AEW[1]==0);
    
    //Test whether A*v = lambda*v for all eigenvectors v and eigenvalues lambda.
    for (int i=0; i<n; i++) {
      for (int k=0; k<n; k++) {
        EV[k] = AEV[i*n+k];
        produktEV_EW[k] = EV[k]*AEW[i];
      }
      produktA_EV = MatrixMultiplikationDouble.multinxm(A, EV, n, n, 1);
      for (int j=0; j<n; j++) {
        Assert.assertEquals(produktA_EV[j], produktEV_EW[j], 1e-12);
      }
    }
  }
  
  /**
   * Test for
   * {@link net.mumie.mathletfactory.number.numeric.numericdouble.Eigenvalue2x2Double#eigenvectorOfSymmetricMatrix eigenvectorOfSymmetricMatrix}.
   */
  public void testEigenvalueAndEigenvectorSymmetric() {
    double A[] = {1.0, 1.0, 1.0, 3.0};
    
    int n = (int)Math.sqrt(A.length);
    double[] AEW = Eigenvalue2x2Double.eigenvalue(A);  // Vektor der EW (n x 1)
    double[] AEV = Eigenvalue2x2Double.eigenvectorOfSymmetricMatrix(A); // Matrix der EV (n x n)
    double[] EV = new double[n];
    double[] produktA_EV = new double[n];
    double[] produktEV_EW  = new double[n];
    
    //Test whether the eigenvectors are orthonormal:
    double determinante = DeterminantDouble.det(AEV);
    Assert.assertEquals(determinante/Math.abs(determinante), 1.0, 1e-12);
    Assert.assertEquals(determinante, 1.0, 1e-12);
    
    //Test whether the eigenvectors are orthogonal:
    Assert.assertEquals(AEV[0]*AEV[2] + AEV[1]*AEV[3], 0.0, 1e-12);
    
    //Test whether the eigenvalues are sorted:
    Assert.assertTrue(AEW[0]<=AEW[1]|| AEW[1]==0);
    
    //Test whether A*v = lambda*v for all eigenvectors v and eigenvalues lambda.
    for (int i=0; i<n; i++) {
      for (int k=0; k<n; k++) {
        EV[k] = AEV[i*n+k];
        produktEV_EW[k] = EV[k]*AEW[i];
      }
      produktA_EV = MatrixMultiplikationDouble.multinxm(A, EV, n, n, 1);
      for (int j=0; j<n; j++) {
        Assert.assertEquals(produktA_EV[j], produktEV_EW[j], 1e-12);
      }
    }
  }
  
  /**
   * Runs the test.
   *
   * @param    args                a  <code>String[]</code>
   *
   */
  public static void main(String args[]) {
    junit.textui.TestRunner.run(Eigenvalue2x2DoubleTest.class);
  }
}




