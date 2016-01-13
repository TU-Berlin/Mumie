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

package net.mumie.mathletfactory.math.geom.affine.test;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DEllipse;

/**
 * JUnit tests for methods of the class
 * {@link net.mumie.mathletfactory.geom.affine.Affine2DQuadric}.
 *
 * @author Mrose
 * @mm.docstatus finished
 * @see  <a href="http://junit.sourceforge.net">JUnit</a>
 */
public class Affine2DQuadricMethodsTest extends TestCase {
  
  private static MNumber[] matrix1 =
  {new MDouble(4), new MDouble(-2), new MDouble(0),
      new MDouble(-2), new MDouble(4), new MDouble(0),
      new MDouble(0), new MDouble(0), new MDouble(-6) };
  private static NumberMatrix quadricMatrix1 = new NumberMatrix(MDouble.class, 3, matrix1);
  private static MMAffine2DEllipse ellipse1 = new MMAffine2DEllipse(quadricMatrix1);
  
  /**
   * Class constructor.
   */
  public  Affine2DQuadricMethodsTest(String name) {
    super(name);
  }
  
  /**
   * Test of
   * {@link net.mumie.mathletfactory.geom.affine.Affine2DQuadric#eigenvalueOfMinorMatrixDouble eigenvalueOfMinorMatrixDouble}
   * and
   * {@link net.mumie.mathletfactory.geom.affine.Affine2DQuadric#eigenvectorOfMinorMatrixDouble eigenvectorOfMinorMatrixDouble}
   * for the first eigenvalue and eigenvector.
   *
   */
  public void testFirstEigenvectorOfMinorMatrix() {
    // testet Gleichung EW*EV = A*EV  f&uuml;r Matrix A.
    // Achtung: testet nicht Reihenfolge der EV und auch nicht Normierung!
    double EW1 = ellipse1.eigenvalueOfMinorMatrixDouble(quadricMatrix1)[0];
    double[] EV1 = {ellipse1.eigenvectorOfMinorMatrixDouble(quadricMatrix1)[0],
        ellipse1.eigenvectorOfMinorMatrixDouble(quadricMatrix1)[1]};
    double[] prodLeft = new double[2];
    double[] prodRight = new double[2];
    for (int i=0; i<2; i++) {
      prodLeft[i] = EW1*EV1[i];
    }
    prodRight[0] = matrix1[0].getDouble()*EV1[0]+matrix1[1].getDouble()*EV1[1];
    prodRight[1] = matrix1[3].getDouble()*EV1[0]+matrix1[4].getDouble()*EV1[1];
    
    Assert.assertEquals(prodLeft[0], prodRight[0], 1e-12);
    Assert.assertEquals(prodLeft[1], prodRight[1], 1e-12);
  }
  
  /**
   * Test of
   * {@link net.mumie.mathletfactory.geom.affine.Affine2DQuadric#eigenvalueOfMinorMatrixDouble eigenvalueOfMinorMatrixDouble}
   * and
   * {@link net.mumie.mathletfactory.geom.affine.Affine2DQuadric#eigenvectorOfMinorMatrixDouble eigenvectorOfMinorMatrixDouble}
   * for the second eigenvalue and eigenvector.
   *
   */
  public void testSecondEigenvectorOfMinorMatrix() {
    // testet Gleichung EW*EV = A*EV  f&uuml;r Matrix A.
    // Achtung: testet nicht Reihenfolge der EV und auch nicht Normierung!
    double EW2 = ellipse1.eigenvalueOfMinorMatrixDouble(quadricMatrix1)[1];
    double[] EV2 = {ellipse1.eigenvectorOfMinorMatrixDouble(quadricMatrix1)[2],
        ellipse1.eigenvectorOfMinorMatrixDouble(quadricMatrix1)[3]};
    double[] prodLeft = new double[2];
    double[] prodRight = new double[2];
    for (int i=0; i<2; i++) {
      prodLeft[i] = EW2*EV2[i];
    }
    prodRight[0] = matrix1[0].getDouble()*EV2[0]+matrix1[1].getDouble()*EV2[1];
    prodRight[1] = matrix1[3].getDouble()*EV2[0]+matrix1[4].getDouble()*EV2[1];
    
    Assert.assertEquals(prodLeft[0], prodRight[0], 1e-12);
    Assert.assertEquals(prodLeft[1], prodRight[1], 1e-12);
  }
}



