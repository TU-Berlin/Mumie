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

/**
 * Affine2DHyperbola_Methods_Test.java
 *
 * @author Created by Omnicore CodeGuide
 */


package net.mumie.mathletfactory.math.geom.affine.test;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DHyperbola;

/**
 * @author Liu
 * @mm.docstatus outstanding
 */
public class Affine2DHyperbolaMethodsTest extends TestCase {
  
  public  Affine2DHyperbolaMethodsTest(String name) {
    super(name);
  }
  
  // 2*x^2 + 2*x*y + 3*y^2 = 1;           (Meyberg/Vachenauer)
  // Halbachsen: a=sqrt( 2/(5-sqrt(5)) ),  b=sqrt( 2/(5+sqrt(5)) )
  // Mittelpunkt: (0,0)
  // Winkel:
  MNumber[] matrix1 =
  { new MDouble(3), new MDouble(-3), new MDouble(0),
      new MDouble(-3), new MDouble(-5), new MDouble(0),
      new MDouble(0), new MDouble(0), new MDouble(24) };
  NumberMatrix quadricMatrix1 = new NumberMatrix(MDouble.class, 3, matrix1);
  MMAffine2DHyperbola h1 = new MMAffine2DHyperbola(quadricMatrix1);
  
  
  public void testGetCenter() {
    
    double[] expected1 = {0, 0};
    double[] result1 = h1.getCenter();
    Assert.assertEquals(expected1[0], result1[0],1e-4);
    Assert.assertEquals(expected1[1], result1[1],1e-4);
    
  }
  
  public void testGetRadian() {
    
    double expected2 =1.2490457723982544 ;
    double result2 = h1.getRadian();
    Assert.assertEquals(expected2, result2, 1e-12);
  }
  
  public void testLengthFirstSemiAxis() {
    
    //    double expected1 = Math.sqrt(2/(5-Math.sqrt(5)));
    //    double result1 = ellipse1.getLengthMajorSemiAxis();
    //    Assert.assertEquals(expected1, result1, 0.0);
//
    double expected2 =2;
    double result2 = h1.getLengthSemimajorAxis();
    Assert.assertEquals(expected2, result2, 1e-12);
    
  }
  
  public void testLengthSecondSemiAxis() {
    //    double expected1 = Math.sqrt(2/(5+Math.sqrt(5)));
    //    double result1 = ellipse1.getLengthMinorSemiAxis();
    //    Assert.assertEquals(expected1, result1, 0.0);
//
    double expected2 =2.449489742783178 ;
    double result2 = h1.getLengthSemiminorAxis();
    Assert.assertEquals(expected2, result2, 0.00000000000001);
    
    
  }
  double semiAxis1 = 2;//Math.sqrt(3);
  double semiAxis2 = 2.449489742783178;
  double center[] = {0, 0};
  double radian = Math.PI/4;
  
  public void testToQuadricMatrixAndBack() {
    MMAffine2DHyperbola hy = new MMAffine2DHyperbola(center, radian, semiAxis1, semiAxis2);
    double[] centerNew = hy. getCenter();
    double radianNew = hy.getRadian();
    double semiAxis1New = hy.getLengthSemimajorAxis();
    double semiAxis2New = hy.getLengthSemiminorAxis();
    Assert.assertEquals(center[0], centerNew[0], 1e-12);
    Assert.assertEquals(center[1], centerNew[1], 1e-12);
    Assert.assertEquals(radian, radianNew, 1e-12);
    Assert.assertEquals(semiAxis1, semiAxis1New, 1e-12);
    Assert.assertEquals(semiAxis2, semiAxis2New, 1e-12);
    
  }
  
//  MNumber[] testMatrix =
//  { new MDouble(2), new MDouble(1), new MDouble(0),
//      new MDouble(1), new MDouble(-3), new MDouble(0),
//      new MDouble(0), new MDouble(0), new MDouble(-1) };
//  NumberMatrix testQuadricMatrix = new NumberMatrix(MDouble.class, 3, testMatrix);
//  Affine2DHyperbola testHy = new Affine2DHyperbola(testQuadricMatrix);
  

  
  public void testFromQuadricMatrixAndBack() {
    double[] m_center = h1.getCenter();
    double m_radian = h1.getRadian();
    double m_semiAxis1 = h1.getLengthSemimajorAxis();
    double m_semiAxis2 = h1.getLengthSemiminorAxis();
    MMAffine2DHyperbola new_hy = new MMAffine2DHyperbola(m_center, m_radian, m_semiAxis1, m_semiAxis2);
    Assert.assertEquals( h1.getMatrix().getEntry(1,1).getDouble(),  new_hy.getMatrix().getEntry(1,1).getDouble(), 1e-12);
    Assert.assertEquals( h1.getMatrix().getEntry(1,2).getDouble(),  new_hy.getMatrix().getEntry(1,2).getDouble(), 1e-12);
    Assert.assertEquals( h1.getMatrix().getEntry(1,3).getDouble(),  new_hy.getMatrix().getEntry(1,3).getDouble(), 1e-12);
    Assert.assertEquals( h1.getMatrix().getEntry(2,1).getDouble(),  new_hy.getMatrix().getEntry(2,1).getDouble(), 1e-12);
    Assert.assertEquals( h1.getMatrix().getEntry(2,2).getDouble(),  new_hy.getMatrix().getEntry(2,2).getDouble(), 1e-12);
    Assert.assertEquals( h1.getMatrix().getEntry(2,3).getDouble(),  new_hy.getMatrix().getEntry(2,3).getDouble(), 1e-12);
    Assert.assertEquals( h1.getMatrix().getEntry(3,1).getDouble(),  new_hy.getMatrix().getEntry(3,1).getDouble(), 1e-12);
    Assert.assertEquals( h1.getMatrix().getEntry(3,2).getDouble(),  new_hy.getMatrix().getEntry(3,2).getDouble(), 1e-12);
    Assert.assertEquals( h1.getMatrix().getEntry(3,3).getDouble(),  new_hy.getMatrix().getEntry(3,3).getDouble(), 1e-12);
    
  }
}






