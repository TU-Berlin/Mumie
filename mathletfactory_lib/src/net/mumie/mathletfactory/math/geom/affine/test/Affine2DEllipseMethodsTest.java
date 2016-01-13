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
 * {@link net.mumie.mathletfactory.geom.affine.Affine2DEllipse}.
 *
 * @author Mrose
 * @mm.docstatus finished
 * @see  <a href="http://junit.sourceforge.net">JUnit</a>
 */

public class Affine2DEllipseMethodsTest extends TestCase {
  
  /**
   * Class constructor.
   */
  public  Affine2DEllipseMethodsTest(String name) {
    super(name);
  }
  
  /**
   * Ellipse given by 2x² + 2xy + 3y² = 1 with
   * semiaxes a=sqrt(2/(5+sqrt(5))),  b=sqrt(2/(5-sqrt(5))), and
   * center (0,0).
   */
  MNumber[] matrix1 =
  { new MDouble(2), new MDouble(1), new MDouble(0),
      new MDouble(1), new MDouble(3), new MDouble(0),
      new MDouble(0), new MDouble(0), new MDouble(-1) };
  NumberMatrix quadricMatrix1 = new NumberMatrix(MDouble.class, 3, matrix1);
  MMAffine2DEllipse ellipse1 = new MMAffine2DEllipse(quadricMatrix1);
  
  /**
   * Ellipse given by 4x² - 4xy + 4y² - 6 = 0 with
   * semiaxes a = sqrt(3),  b = 1, center (0,0), and
   * radian PI/4
   */
  MNumber[] matrix2 =
  { new MDouble(4), new MDouble(-2), new MDouble(0),
      new MDouble(-2), new MDouble(4), new MDouble(0),
      new MDouble(0), new MDouble(0), new MDouble(-6) };
  NumberMatrix quadricMatrix2 = new NumberMatrix(MDouble.class, 3, matrix2);
  MMAffine2DEllipse ellipse2 = new MMAffine2DEllipse(quadricMatrix2);
  
  /**
   * Ellipse given by x² - 2xy + 2y² + 2x - 6y + 1  = 0 with
   * center (1,2).
   */
  MNumber[] matrix3 =
  { new MDouble(1), new MDouble(-1), new MDouble(1),
      new MDouble(-1), new MDouble(2), new MDouble(-3),
      new MDouble(1), new MDouble(-3), new MDouble(1) };
  NumberMatrix quadricMatrix3 = new NumberMatrix(MDouble.class, 3, matrix3);
  MMAffine2DEllipse ellipse3 = new MMAffine2DEllipse(quadricMatrix3);
  
  /**
   * Circle given by x² + y² - 6x - 8y - 24  = 0 with
   * semiaxes a=1,  b=1, and center (3,4).
   */
  MNumber[] matrix4 =
  { new MDouble(1), new MDouble(0), new MDouble(-3),
      new MDouble(0), new MDouble(1), new MDouble(-4),
      new MDouble(-3), new MDouble(-4), new MDouble(24) };
  NumberMatrix quadricMatrix4 = new NumberMatrix(MDouble.class, 3, matrix4);
  MMAffine2DEllipse ellipse4 = new MMAffine2DEllipse(quadricMatrix4);
  
  /**
   * Ellipse given by 36x² - 24xy + 29y² - 180  = 0 with
   * semiaxes a=3,  b=2, and center (0,0).
   */
  MNumber[] matrix5 =
  { new MDouble(36), new MDouble(-12), new MDouble(0),
      new MDouble(-12), new MDouble(29), new MDouble(0),
      new MDouble(0), new MDouble(0), new MDouble(-180) };
  NumberMatrix quadricMatrix5 = new NumberMatrix(MDouble.class, 3, matrix5);
  MMAffine2DEllipse ellipse5 = new MMAffine2DEllipse(quadricMatrix5);
  
  /**
   * Ellipse given by 52x² + 28xy + 73y² - 208x - 56y - 512  = 0 with
   * semiaxes a=3,  b=4 and center (2,0).
   */
  MNumber[] matrix6 =
  { new MDouble(52), new MDouble(14), new MDouble(-104),
      new MDouble(14), new MDouble(73), new MDouble(-28),
      new MDouble(-104), new MDouble(-28), new MDouble(-512) };
  NumberMatrix quadricMatrix6 = new NumberMatrix(MDouble.class, 3, matrix6);
  MMAffine2DEllipse ellipse6 = new MMAffine2DEllipse(quadricMatrix6);
  
  /**
   * Test of {@link net.mumie.mathletfactory.geom.affine.Affine2DEllipse#getCenter}.
   *
   */
  public void testGetCenter() {
    
    double[] expected1 = {0.0, 0.0};
    double[] result1 = ellipse1.getCenter();
    Assert.assertEquals(expected1[0], result1[0],1e-12);
    Assert.assertEquals(expected1[1], result1[1],1e-12);
    
    double[] expected2 = {0.0, 0.0};
    double[] result2 = ellipse2.getCenter();
    Assert.assertEquals(expected2[0], result2[0], 1e-12);
    Assert.assertEquals(expected2[1], result2[1], 1e-12);
    
    double[] expected3 = {1.0, 2.0};
    double[] result3 = ellipse3.getCenter();
    Assert.assertEquals(expected3[0], result3[0], 1e-12);
    Assert.assertEquals(expected3[1], result3[1], 1e-12);
    
    double[] expected4 = {3.0, 4.0};
    double[] result4 = ellipse4.getCenter();
    Assert.assertEquals(expected4[0], result4[0], 1e-12);
    Assert.assertEquals(expected4[1], result4[1], 1e-12);
    
    double[] expected5 = {0.0, 0.0};
    double[] result5 = ellipse5.getCenter();
    Assert.assertEquals(expected5[0], result5[0], 1e-12);
    Assert.assertEquals(expected5[1], result5[1], 1e-12);
    
    double[] expected6 = {2.0, 0.0};
    double[] result6 = ellipse6.getCenter();
    Assert.assertEquals(expected6[0], result6[0], 1e-12);
    Assert.assertEquals(expected6[1], result6[1], 1e-12);
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.geom.affine.Affine2DEllipse#getRadian}.
   *
   */
  public void testGetRadian() {
    
    double expected2 = Math.PI/4;
    double result2 = ellipse2.getRadian();
    Assert.assertEquals(expected2, result2, 1e-12);
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.geom.affine.Affine2DEllipse#getLengthFirstSemiAxis}.
   *
   */
  public void testLengthFirstSemiAxis() {
    
    double expected1 = Math.sqrt(2.0/(5.0+Math.sqrt(5.0)));
    double result1 = ellipse1.getLengthFirstSemiAxis();
    Assert.assertEquals(expected1, result1, 0.0);
    
    double expected2 = Math.sqrt(3.0);
    double result2 = ellipse2.getLengthFirstSemiAxis();
    Assert.assertEquals(expected2, result2, 0.0);
    
    double expected4 = 1.0;
    double result4 = ellipse4.getLengthFirstSemiAxis();
    Assert.assertEquals(expected4, result4, 0.0);
    
    double expected5 = 3.0;
    double result5 = ellipse5.getLengthFirstSemiAxis();
    Assert.assertEquals(expected5, result5, 0.0);
    
    double expected6 = 3.0;
    double result6 = ellipse6.getLengthFirstSemiAxis();
    Assert.assertEquals(expected6, result6, 0.0);
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.geom.affine.Affine2DEllipse#getLengthSecondSemiAxis}.
   *
   */
  public void testLengthSecondSemiAxis() {
    double expected1 = Math.sqrt(2.0/(5.0-Math.sqrt(5.0)));
    double result1 = ellipse1.getLengthSecondSemiAxis();
    Assert.assertEquals(expected1, result1, 0.0);
    
    double expected2 = 1.0;
    double result2 = ellipse2.getLengthSecondSemiAxis();
    Assert.assertEquals(expected2, result2, 0.0);
    
    double expected4 = 1.0;
    double result4 = ellipse4.getLengthSecondSemiAxis();
    Assert.assertEquals(expected4, result4, 0.0);
    
    double expected5 = 2.0;
    double result5 = ellipse5.getLengthSecondSemiAxis();
    Assert.assertEquals(expected5, result5, 0.0);
    
    double expected6 = 4.0;
    double result6 = ellipse6.getLengthSecondSemiAxis();
    Assert.assertEquals(expected6, result6, 0.0);
  }
  /**
   * Creates an ellipse for given semi-axes, center, and radian and compares
   * its return values of
   * {@link net.mumie.mathletfactory.geom.affine.Affine2DEllipse#getCenter},
   * {@link net.mumie.mathletfactory.geom.affine.Affine2DEllipse#getRadian},
   * {@link net.mumie.mathletfactory.geom.affine.Affine2DEllipse#getLengthFirstSemiAxis}, and
   * {@link net.mumie.mathletfactory.geom.affine.Affine2DEllipse#getLengthSecondSemiAxis}
   * with the given values.
   */
  public void testToQuadricMatrixAndBack() {
    double semiAxis1 = 4.7;
    double semiAxis2 = 2.3;
    double center[] = {-1.0, 3.0};
    double radian = 0.5*Math.PI/3;
    
    MMAffine2DEllipse ell = new MMAffine2DEllipse(center, radian, semiAxis1, semiAxis2);
    double[] centerNew = ell. getCenter();
    double radianNew = ell.getRadian();
    double semiAxis1New = ell.getLengthFirstSemiAxis();
    double semiAxis2New = ell.getLengthSecondSemiAxis();
    Assert.assertEquals(center[0], centerNew[0], 1e-12);
    Assert.assertEquals(center[1], centerNew[1], 1e-12);
    Assert.assertEquals(radian, radianNew, 1e-12);
    Assert.assertEquals(semiAxis1, semiAxis1New, 1e-12);
    Assert.assertEquals(semiAxis2, semiAxis2New, 1e-12);
    
  }
  
  /**
   * Creates an ellipse for a given matrix and compares its return value of
   * {@link net.mumie.mathletfactory.geom.affine.Affine2DEllipse#getMatrix}
   * with the given matrix.
   */
  public void testFromQuadricMatrixAndBack() {
    MNumber[] testMatrix =
    { new MDouble(2), new MDouble(-2.1), new MDouble(-1),
        new MDouble(-2.1), new MDouble(9), new MDouble(-3),
        new MDouble(-1), new MDouble(-3), new MDouble(1) };
    NumberMatrix testQuadricMatrix = new NumberMatrix(MDouble.class, 3, testMatrix);
    MMAffine2DEllipse testEllipse = new MMAffine2DEllipse(testQuadricMatrix);
    
    NumberMatrix m_matrix = testEllipse.getMatrix();
    MMAffine2DEllipse new_ell = new MMAffine2DEllipse(m_matrix);
    Assert.assertEquals( testEllipse.getMatrix().getEntry(1,1).getDouble(), new_ell.getMatrix().getEntry(1,1).getDouble(), 1e-12);
    Assert.assertEquals( testEllipse.getMatrix().getEntry(1,2).getDouble(), new_ell.getMatrix().getEntry(1,2).getDouble(), 1e-12);
    Assert.assertEquals( testEllipse.getMatrix().getEntry(1,3).getDouble(), new_ell.getMatrix().getEntry(1,3).getDouble(), 1e-12);
    Assert.assertEquals( testEllipse.getMatrix().getEntry(2,1).getDouble(), new_ell.getMatrix().getEntry(2,1).getDouble(), 1e-12);
    Assert.assertEquals( testEllipse.getMatrix().getEntry(2,2).getDouble(), new_ell.getMatrix().getEntry(2,2).getDouble(), 1e-12);
    Assert.assertEquals( testEllipse.getMatrix().getEntry(2,3).getDouble(), new_ell.getMatrix().getEntry(2,3).getDouble(), 1e-12);
    Assert.assertEquals( testEllipse.getMatrix().getEntry(3,1).getDouble(), new_ell.getMatrix().getEntry(3,1).getDouble(), 1e-12);
    Assert.assertEquals( testEllipse.getMatrix().getEntry(3,2).getDouble(), new_ell.getMatrix().getEntry(3,2).getDouble(), 1e-12);
    Assert.assertEquals( testEllipse.getMatrix().getEntry(3,3).getDouble(), new_ell.getMatrix().getEntry(3,3).getDouble(), 1e-12);
  }
}





