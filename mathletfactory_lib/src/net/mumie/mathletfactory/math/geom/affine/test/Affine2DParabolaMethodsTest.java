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
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DParabola;

/**
 * @author Liu
 * @mm.docstatus outstanding
 */
public class Affine2DParabolaMethodsTest extends TestCase {
  
  public  Affine2DParabolaMethodsTest(String name) {
    super(name);
  }
  
  // 2*x^2 + 2*x*y + 3*y^2 = 1;           (Meyberg/Vachenauer)
  // Halbachsen: a=sqrt( 2/(5-sqrt(5)) ),  b=sqrt( 2/(5+sqrt(5)) )
  // Mittelpunkt: (0,0)
  // Winkel:
  MNumber[] matrix1 =
  { new MDouble(0.7352941176470589), new MDouble(-0.44117647058823534), new MDouble(-5.852941176470588),
      new MDouble(-0.44117647058823534), new MDouble(0.26470588235294124), new MDouble(-10.088235294117647),
      new MDouble(-5.852941176470588), new MDouble(-10.088235294117647), new MDouble(-83.97058823529412) };
  NumberMatrix quadricMatrix1 = new NumberMatrix(MDouble.class, 3, matrix1);
  MMAffine2DParabola p1 = new MMAffine2DParabola(quadricMatrix1);
  
  // 4*x^2  - 4*x*y + 4*y^2 - 6 = 0
  // Halbachsen: a = sqrt(3),  b = 1
  // Mittelpunkt:  (0,0)
  // Winkel:  PI/4
  MNumber[] matrix2 =
  { new MDouble(1), new MDouble(-2), new MDouble(1),
      new MDouble(-2), new MDouble(4), new MDouble(2),
      new MDouble(1), new MDouble(2), new MDouble(-6) };
  NumberMatrix quadricMatrix2 = new NumberMatrix(MDouble.class, 3, matrix2);
  MMAffine2DParabola p2 = new MMAffine2DParabola(quadricMatrix2);
  
  MNumber[] matrix4 =
  { new MDouble(0.2500000000000001), new MDouble(-0.4330127018922194), new MDouble(-1.6160254037844388),
      new MDouble(-0.4330127018922194), new MDouble(0.7499999999999999), new MDouble(-1.2009618943233418),
      new MDouble(-1.6160254037844388), new MDouble(-1.2009618943233418), new MDouble(14.446152422706632) };
  NumberMatrix quadricMatrix4 = new NumberMatrix(MDouble.class, 3, matrix4);
  MMAffine2DParabola p4 = new MMAffine2DParabola(quadricMatrix4);
  
  
  public void testGetApex() {
    
    double[] expected1 = {-2, -3};
    double[] result1 = p1.getApex();
    Assert.assertEquals(expected1[0], result1[0],1e-12);
    Assert.assertEquals(expected1[1], result1[1],1e-12);
    
    double[] expected2 = {1.7100000000000002,0.555 };
    double[] result2 = p2.getApex();
    Assert.assertEquals(expected2[0], result2[0], 1e-12);
    Assert.assertEquals(expected2[1], result2[1], 1e-12);
    
    double[] expected4 = {3, 2.0};
    double[] result4 = p4.getApex();
    Assert.assertEquals(expected4[0], result4[0], 1e-12);
    Assert.assertEquals(expected4[1], result4[1], 1e-12);
   
    
  }
  
  public void testGetFocal() {
    
    /*double[] expected1 = {0.0, 0.0};
    double[] result1 = p1.getFocalPoint();
    Assert.assertEquals(expected1[0], result1[0],1e-12);
     Assert.assertEquals(expected1[1], result1[1],1e-12);*/
    
    double[] expected2 = {1,2 };
    double[] result2 = p1.getFocalPoint();
    Assert.assertEquals(expected2[0], result2[0], 1e-12);
    Assert.assertEquals(expected2[1], result2[1], 1e-12);
    
    /*double[] expected4 = {1.0, 2.0};
    double[] result4 = p4.getFocalPoint();
    Assert.assertEquals(expected4[0], result4[0], 1e-12);
   Assert.assertEquals(expected4[1], result4[1], 1e-12);*/
   
    
  }
  
  public void testGetRadian() {
    //-1.1071487177940904+PI
    double expected2 =-0.5404195002705842;
    double result2 = p1.getRadian();
    Assert.assertEquals(expected2, result2, 1e-12);
    
  }
  
  public void testGetP() {
    
    double expected4 =11.661903789690601 ;
    double result4 = p1.getDistanceBetweenGuidelineAndFocalPoint();
    Assert.assertEquals(expected4, result4, 0.0);
    
  }
  
  double P = 0.5;//Math.sqrt(3);
  double center[] = {-1.0, 2.0};
  double radian = 0.5*Math.PI/6;
  
  public void testToQuadricMatrixAndBack() {
    MMAffine2DParabola p = new MMAffine2DParabola(center, radian, P);
    double[] centerNew = p. getApex();
    double radianNew = p.getRadian();
    double pNew = p.getDistanceBetweenGuidelineAndFocalPoint();
    Assert.assertEquals(center[0], centerNew[0], 1e-12);
    Assert.assertEquals(center[1], centerNew[1], 1e-12);
    Assert.assertEquals(radian, radianNew, 1e-12);
    Assert.assertEquals(P, pNew, 1e-12);
  }
  
//  MNumber[] testMatrix =
//  { new MDouble(2), new MDouble(1), new MDouble(0),
//      new MDouble(1), new MDouble(3), new MDouble(0),
//      new MDouble(0), new MDouble(0), new MDouble(-1) };
//  NumberMatrix testQuadricMatrix = new NumberMatrix(MDouble.class, 3, testMatrix);
//  Affine2DParabola testP = new Affine2DParabola(testQuadricMatrix);
  

  
  public void testFromQuadricMatrixAndBack() {
    double[] m_center = p1.getApex();
    double m_radian = p1.getRadian();
    double m_P = p1.getDistanceBetweenGuidelineAndFocalPoint();
    MMAffine2DParabola new_p = new MMAffine2DParabola(m_center, m_radian, m_P);
    Assert.assertEquals( p1.getMatrix().getEntry(1,1).getDouble(),  new_p.getMatrix().getEntry(1,1).getDouble(), 1e-12);
    Assert.assertEquals( p1.getMatrix().getEntry(1,2).getDouble(),  new_p.getMatrix().getEntry(1,2).getDouble(), 1e-12);
    Assert.assertEquals( p1.getMatrix().getEntry(1,3).getDouble(),  new_p.getMatrix().getEntry(1,3).getDouble(), 1e-12);
    Assert.assertEquals( p1.getMatrix().getEntry(2,1).getDouble(),  new_p.getMatrix().getEntry(2,1).getDouble(), 1e-12);
    Assert.assertEquals( p1.getMatrix().getEntry(2,2).getDouble(),  new_p.getMatrix().getEntry(2,2).getDouble(), 1e-12);
    Assert.assertEquals( p1.getMatrix().getEntry(2,3).getDouble(),  new_p.getMatrix().getEntry(2,3).getDouble(), 1e-12);
    Assert.assertEquals( p1.getMatrix().getEntry(3,1).getDouble(),  new_p.getMatrix().getEntry(3,1).getDouble(), 1e-12);
    Assert.assertEquals( p1.getMatrix().getEntry(3,2).getDouble(),  new_p.getMatrix().getEntry(3,2).getDouble(), 1e-12);
    Assert.assertEquals( p1.getMatrix().getEntry(3,3).getDouble(),  new_p.getMatrix().getEntry(3,3).getDouble(), 1e-12);
    
  }

}

