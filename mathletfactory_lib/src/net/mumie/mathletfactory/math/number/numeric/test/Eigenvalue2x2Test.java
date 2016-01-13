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
import net.mumie.mathletfactory.math.number.MComplex;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.numeric.Determinant;
import net.mumie.mathletfactory.math.number.numeric.Eigenvalue2x2;
import net.mumie.mathletfactory.math.number.numeric.MatrixMultiplikation;
/**
 * JUnit testcase for all classes in
 * {@link net.mumie.mathletfactory.number.numeric.Eigenvalue2x2}.
 *
 * @author Mrose
 * @mm.docstatus finished
 * @see <a href="http://junit.sourceforge.net">JUnit</a>
 */
public class Eigenvalue2x2Test extends TestCase{
  
  static MNumber zero = new MDouble(0.0);
  
  /**
   * Class constructor.
   */
  public Eigenvalue2x2Test(String name){
    super(name);
  }
  
  /**
   * Test for
   * {@link net.mumie.mathletfactory.number.numeric.Eigenvalue2x2#realEigenvaluesExist realEigenvaluesExist}.
   */
  public void testRealEigenvaluesExist(){
    MNumber matrix[] = {new MDouble().create().setDouble(1),
        new MDouble().create().setDouble(-1),
        new MDouble().create().setDouble(2),
        new MDouble().create().setDouble(-1)};
    boolean expected = false;
    boolean result = Eigenvalue2x2.realEigenvaluesExist(matrix);
    Assert.assertEquals(expected,result);
  }
  
  /**
   * Test for
   * {@link net.mumie.mathletfactory.number.numeric.Eigenvalue2x2#eigenvalue eigenvalue}.
   */
  public void testEigenvalue(){
    MNumber matrix[] = {new MDouble().create().setDouble(1),
        new MDouble().create().setDouble(-1),
        new MDouble().create().setDouble(2),
        new MDouble().create().setDouble(-1)};
    MNumber result1 = Eigenvalue2x2.eigenvalue(matrix)[0];
    MNumber result2 = Eigenvalue2x2.eigenvalue(matrix)[1];
    MNumber expected;
    if (!Eigenvalue2x2.realEigenvaluesExist(matrix)){
      expected = new MComplex(0,-1);
    }
    else {
      expected = (new MDouble ().create()).setDouble(5);
    }
    Assert.assertTrue(expected.equals(result1)||expected.equals(result2));
  }
  
  /**
   * Test for
   * {@link net.mumie.mathletfactory.number.numeric.Eigenvalue2x2#eigenvector eigenvector}.
   */
  public  void testEigenvector(){
    MNumber matrix[] = {new MDouble().create().setDouble(1),
        new MDouble().create().setDouble(-1),
        new MDouble().create().setDouble(2),
        new MDouble().create().setDouble(-1)};
    MNumber [] expected = new MNumber [2];
    MNumber[] result1 = new MNumber[]{Eigenvalue2x2.eigenvector(matrix)[0],
        Eigenvalue2x2.eigenvector(matrix)[1]};
    MNumber[] result2 = new MNumber[]{Eigenvalue2x2.eigenvector(matrix)[2],
        Eigenvalue2x2.eigenvector(matrix)[3]};
    if (!Eigenvalue2x2.realEigenvaluesExist(matrix)){
      
      expected[0] = new MComplex(2,0);
      expected[1] = new MComplex(2,-2);
    }
    else {
      expected[0] = new MDouble().create().setDouble(-3);
      expected[1] =new MDouble().create().setDouble(-3);
      Assert.assertTrue(MNumber.divide(expected[0],result1[0]).equals
                          (MNumber.divide(expected[1],result1[1]))
                          ||MNumber.divide(expected[0],result2[0]).equals
                          (MNumber.divide(expected[1],result2[1])));
    }
  }
  
  /**
   * Test for
   * {@link net.mumie.mathletfactory.number.numeric.Eigenvalue2x2#eigenvalue eigenvalue}
   * and
   * {@link net.mumie.mathletfactory.number.numeric.Eigenvalue2x2#eigenvector eigenvector}.
   */
  public void testEigenvalueAndEigenvector() {
    MNumber A[]
      = { new MDouble(1.0),  new MDouble(1.0),
        new MDouble(0.0), new MDouble(3.0)};
    
    int n = (int)Math.sqrt(A.length);
    MNumber[] AEW = Eigenvalue2x2.eigenvalue(A);  // Vektor der EW (n x 1)
    MNumber[] AEV = Eigenvalue2x2.eigenvector(A); // Matrix der EV (n x n)
    MNumber[] EV = new MNumber[n];
    MNumber[] produktA_EV = new MNumber[n];
    MNumber[] produktEV_EW  = new MNumber[n];
    
    //Test whether the eigenvectors are positive oriented:
    MNumber determinante = Determinant.det(AEV);
    Assert.assertEquals(MNumber.divide(determinante, determinante.absed()).getDouble(), 1.0, 1e-12);
    
    //Test whether the eigenvalues are sorted:
    Assert.assertTrue(AEW[0].lessOrEqualThan(AEW[1])|| AEW[1].isZero());
    
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
   * Test for
   * {@link net.mumie.mathletfactory.number.numeric.Eigenvalue2x2#eigenvectorOfSymmetricMatrix eigenvectorOfSymmetricMatrix}.
   */
  public void testEigenvalueAndEigenvectorSymmetric() {
    MNumber A[]
      = { new MDouble(1.0),  new MDouble(1.0),
        new MDouble(1.0), new MDouble(3.0)};
    
    int n = (int)Math.sqrt(A.length);
    MNumber[] AEW = Eigenvalue2x2.eigenvalue(A);  // Vektor der EW (n x 1)
    MNumber[] AEV = Eigenvalue2x2.eigenvectorOfSymmetricMatrix(A); // Matrix der EV (n x n)
    MNumber[] EV = new MNumber[n];
    MNumber[] produktA_EV = new MNumber[n];
    MNumber[] produktEV_EW  = new MNumber[n];
    
    //Test whether the eigenvectors are orthonormal:
    MNumber determinante = Determinant.det(AEV);
    Assert.assertEquals(MNumber.divide(determinante, determinante.absed()).getDouble(), 1.0, 1e-12);
    Assert.assertEquals(determinante.getDouble(), 1.0, 1e-12);
    
    //Test whether the eigenvectors are orthogonal:
    Assert.assertEquals(AEV[0].copy().mult(AEV[2]).add(AEV[1].copy().mult(AEV[3])).getDouble(), 0.0, 1e-12);
    
    //Test whether the eigenvalues are sorted:
    Assert.assertTrue(AEW[0].lessOrEqualThan(AEW[1])|| AEW[1].isZero());
    
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
    junit.textui.TestRunner.run(Eigenvalue2x2Test.class);
  }
  
}




