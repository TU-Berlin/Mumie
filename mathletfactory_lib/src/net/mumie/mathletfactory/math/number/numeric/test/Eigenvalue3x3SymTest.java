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
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.numeric.Determinant;
import net.mumie.mathletfactory.math.number.numeric.Eigenvalue3x3Sym;
import net.mumie.mathletfactory.math.number.numeric.MatrixMultiplikation;

/**
 * JUnit testcase for all classes in {@link net.mumie.mathletfactory.number.numeric.Eigenvalue3x3Sym}.
 *
 * @author Mrose
 * @mm.docstatus finished
 * @see <a href="http://junit.sourceforge.net">JUnit</a>
 */
public class Eigenvalue3x3SymTest extends TestCase {
  
  /**
   * Class constructor.
   */
  public Eigenvalue3x3SymTest(String name) {
    super(name);
  }
  
  /**
   * Test for
   * {@link net.mumie.mathletfactory.number.numeric.Eigenvalue3x3Sym#eigenvalue eigenvalue}
   * and
   * {@link net.mumie.mathletfactory.number.numeric.Eigenvalue3x3Sym#eigenvector eigenvector}.
   */
  public void testEigenvalueAndEigenvector() {
    MNumber A[]
      = { new MDouble(-1.0),  new MDouble(-5.0), new MDouble(2.0),
        new MDouble(-5.0), new MDouble(8.0),  new MDouble(-1.0),
        new MDouble(2.0),  new MDouble(-1.0),  new MDouble(4.0)};
    
    NumberMatrix matrixA = new NumberMatrix(MNumber.class, 3, A);
    int n = (int)Math.sqrt(A.length);
    MNumber[] AEW = Eigenvalue3x3Sym.eigenvalue(matrixA);  // Vektor der EW (n x 1)
    MNumber[] AEV = Eigenvalue3x3Sym.eigenvector(matrixA); // Matrix der EV (n x n)
    MNumber[] EV = new MNumber[n];
    MNumber[] produktA_EV = new MNumber[n];
    MNumber[] produktEV_EW  = new MNumber[n];
    
    //Test whether the eigenvectors are orthonormal:
    MNumber determinante = Determinant.det(AEV);
    Assert.assertEquals(MNumber.divide(determinante, determinante.absed() ).getDouble(), 1.0, 1e-12);
    Assert.assertEquals(1.0, determinante.getDouble(), 1e-12);
    
    //Test whether the eigenvalues are sorted:
//    Assert.assertTrue(AEW[0].lessOrEqualThan(AEW[1]) && AEW[1].lessOrEqualThan(AEW[2])
//                        || AEW[0].lessOrEqualThan(AEW[1]) && AEW[2].isZero()
//                        || AEW[1].isZero() && AEW[2].isZero());
    
    //Test whether the eigenvectors are orthogonal:
    Assert.assertEquals((AEV[0].copy().mult(AEV[3]).add(AEV[1].copy().mult(AEV[4])).add(AEV[2].copy().mult(AEV[5]))).getDouble(), 0.0, 1e-12);
    Assert.assertEquals((AEV[0].copy().mult(AEV[6]).add(AEV[1].copy().mult(AEV[7])).add(AEV[2].copy().mult(AEV[8]))).getDouble(), 0.0, 1e-12);
    Assert.assertEquals((AEV[3].copy().mult(AEV[6]).add(AEV[4].copy().mult(AEV[7])).add(AEV[5].copy().mult(AEV[8]))).getDouble(), 0.0, 1e-12);
    
    //Test whether A*v = lambda*v for all eigenvectors v and eigenvalues lambda.
    for (int i=0; i<n; i++) {
      for (int k=0; k<n; k++) {
        EV[k] = AEV[i*n+k];
        produktEV_EW[k] = MNumber.multiply(EV[k], AEW[i]);
      }
      produktA_EV = MatrixMultiplikation.multinxm(A, EV, n, n, 1);
      for (int j=0; j<n; j++) {
        Assert.assertEquals(produktA_EV[j].getDouble(), produktEV_EW[j].getDouble(), 1e-12);
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
