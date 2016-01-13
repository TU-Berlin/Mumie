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
import net.mumie.mathletfactory.math.number.numeric.numericdouble.EigenvalueDouble;
import net.mumie.mathletfactory.math.number.numeric.numericdouble.MatrixMultiplikationDouble;

/**
 * JUnit testcase for all classes in
 * {@link net.mumie.mathletfactory.number.numeric.numericdouble.EigenvalueDouble}.
 *
 * @author Mrose
 * @mm.docstatus finished
 * @see <a href="http://junit.sourceforge.net">JUnit</a>
 */
public class EigenvalueDoubleTest extends TestCase {
  
  /**
   * Class constructor.
   */
  public EigenvalueDoubleTest(String name) {
    super(name);
  }
  
  /**
   * Test for
   * {@link net.mumie.mathletfactory.number.numeric.numericdouble.EigenvalueDouble#eigenvalue eigenvalue}
   * and
   * {@link net.mumie.mathletfactory.number.numeric.numericdouble.EigenvalueDouble#eigenvector eigenvector}.
   */
  public void testEigenvalueAndEigenvector() {
    double A[]
      = { (1000000.0),  (-10000000.0), (0.0000002),  (70.0),
        (-10000000.0), (-2.0),  (-1.0),  (-58.0),
        (0.0000002),  (-1.0),  (-2.0), (-3.0),
        (70.0),  (-58.0),  (-3.0), (-0.0000007)};
    int m = A.length;
    int n = (int)Math.sqrt(m);
    
    double[] AEW = EigenvalueDouble.eigenvalue(A);  // vector of eigenvalues (n x 1)
    double[] AEV = EigenvalueDouble.eigenvector(A); // matrix of eigenvectors (n x n)
    double[] EV = new double[n];
    double[] produktA_EV = new double[n];
    double[] produktEV_EW  = new double[n];
    
    for (int i=0; i<n; i++) {
      for (int k=0; k<n; k++) {
        EV[k] = AEV[k*n+i];
        produktEV_EW[k] = EV[k]*AEW[i];
      }
      produktA_EV = MatrixMultiplikationDouble.multinxm(A, EV, n, n, 1);
      for (int j=0; j<n; j++) {
        Assert.assertEquals(produktA_EV[j], produktEV_EW[j], 1e-6);
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
    junit.textui.TestRunner.run(EigenvalueDoubleTest.class);
  }
}
