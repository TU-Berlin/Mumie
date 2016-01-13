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
import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.numeric.numericdouble.DeterminantDouble;
import net.mumie.mathletfactory.math.number.numeric.numericdouble.Eigenvalue3x3SymDouble;
import net.mumie.mathletfactory.math.number.numeric.numericdouble.MatrixMultiplikationDouble;
import net.mumie.mathletfactory.math.number.numeric.test.Eigenvalue3x3SymTest;

/**
 * JUnit testcase for all classes in {@link net.mumie.mathletfactory.number.numeric.numericdouble.Eigenvalue3x3SymDouble}.
 *
 * @author Mrose
 * @mm.docstatus finished
 * @see <a href="http://junit.sourceforge.net">JUnit</a>
 */
public class Eigenvalue3x3SymDoubleTest extends TestCase {
  
  /**
   * Class constructor.
   */
  public Eigenvalue3x3SymDoubleTest(String name) {
    super(name);
  }
  
  /**
   * Test for
   * {@link net.mumie.mathletfactory.number.numeric.numericdouble.Eigenvalue3x3SymDouble#eigenvalue eigenvalue}
   * and
   * {@link net.mumie.mathletfactory.number.numeric.numericdouble.Eigenvalue3x3SymDouble#eigenvector eigenvector}.
   */
  public void testEigenvalueAndEigenvector() {
    double A[] = {-1.0, -5.0, 2.0,
        -5.0, 8.0, -1.0,
        2.0, -1.0, 4.0};
    MNumber[] matrixAnumber = new MNumber[A.length];
    for (int i=0; i<A.length; i++){
      matrixAnumber[i] = new MDouble(A[i]);
    }
    NumberMatrix matrixA = new NumberMatrix(MDouble.class, 3, matrixAnumber);
    int n = (int)Math.sqrt(A.length);
    double[] AEW = Eigenvalue3x3SymDouble.eigenvalue(matrixA);  // Vektor der EW (n x 1)
    double[] AEV = Eigenvalue3x3SymDouble.eigenvector(matrixA); // Matrix der EV (n x n)
    double[] EV = new double[n];
    double[] produktA_EV = new double[n];
    double[] produktEV_EW  = new double[n];
    
    //Test whether the eigenvectors are orthonormal:
    double determinante = DeterminantDouble.det(AEV);
    Assert.assertEquals(determinante/Math.abs(determinante), 1.0, 1e-12);
    Assert.assertEquals(1.0, determinante, 1e-12);
    
    //Test whether the eigenvalues are sorted:
    Assert.assertTrue(AEW[0]<=AEW[1] && AEW[1]<=AEW[2]
                        || AEW[0]<=AEW[1] && AEW[2]==0.0
                        || AEW[1]==0.0 && AEW[2]==0.0);
    
    //Test whether the eigenvectors are orthogonal:
    Assert.assertEquals(AEV[0]*AEV[3] + AEV[1]*AEV[4] + AEV[2]*AEV[5], 0.0, 1e-12);
    Assert.assertEquals(AEV[0]*AEV[6] + AEV[1]*AEV[7] + AEV[2]*AEV[8], 0.0, 1e-12);
    Assert.assertEquals(AEV[3]*AEV[6] + AEV[4]*AEV[7] + AEV[5]*AEV[8], 0.0, 1e-12);
    
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
    junit.textui.TestRunner.run(Eigenvalue3x3SymTest.class);
  }
}
