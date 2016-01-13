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

package net.mumie.mathletfactory.test.geom;

import java.awt.Color;

import net.mumie.mathletfactory.appletskeleton.j3d.SingleJ3DCanvasApplet;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.SurfaceDisplayProperties;
import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR3;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine3DQuadric;
import net.mumie.mathletfactory.util.BasicApplicationFrame;


/**
 *  Test of {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine3DQuadric}.
 *
 * @author Mrose
 * @mm.docstatus finished
 */
public class Affine3DQuadricTest extends SingleJ3DCanvasApplet {
  private MNumber[] matrix14 = //hyperboloid of two sheets
  { new MDouble(9.0), new MDouble(-4.5), new MDouble(4.5), new MDouble(-4.5),
      new MDouble(-4.5), new MDouble(-9.0), new MDouble(4.5), new MDouble(4.5),
      new MDouble(4.5), new MDouble(4.5), new MDouble(9.0), new MDouble(-4.5),
      new MDouble(-4.5), new MDouble(4.5), new MDouble(-4.5), new MDouble(9.0)};
  
/*  private MNumber[] matrix13 = //elliptic cone (WEB) ok
  { new MDouble(2.0), new MDouble(1.0), new MDouble(-3.0), new MDouble(-1.0),
      new MDouble(1.0), new MDouble(-1.0), new MDouble(2.0), new MDouble(0.0),
      new MDouble(-3.0), new MDouble(2.0), new MDouble(1.0), new MDouble(5.0),
      new MDouble(-1.0), new MDouble(0.0), new MDouble(5.0), new MDouble(5.0)};
  
  private MNumber[] matrix12 = //hyperbolic cylinder (REP LA S.287, 5.4.5a) ok
  { new MDouble(2.0), new MDouble(1.5), new MDouble(0.0), new MDouble(-2.0),
      new MDouble(1.5), new MDouble(-2.0), new MDouble(0.0), new MDouble(-1.5),
      new MDouble(0.0), new MDouble(0.0), new MDouble(0.0), new MDouble(0.0),
      new MDouble(-2.0), new MDouble(-1.5), new MDouble(0.0), new MDouble(-23.0)};
  
  private MNumber[] matrix11 = //hyperbolic paraboloid (WEB) ok
  { new MDouble(-1.0), new MDouble(3.0), new MDouble(1.0), new MDouble(-6.0),
      new MDouble(3.0), new MDouble(-1.0), new MDouble(1.0), new MDouble(2.0),
      new MDouble(1.0), new MDouble(1.0), new MDouble(1.0), new MDouble(-5.0),
      new MDouble(-6.0), new MDouble(2.0), new MDouble(-5.0), new MDouble(-11.0)};
  
  private MNumber[] matrix10 = //hyperboloid of two sheets (WEB) ok
  { new MDouble(1.0), new MDouble(4.0), new MDouble(0.0), new MDouble(0.5),
      new MDouble(4.0), new MDouble(2.0), new MDouble(2.0), new MDouble(2.0),
      new MDouble(0.0), new MDouble(2.0), new MDouble(1.0), new MDouble(0.5),
      new MDouble(0.5), new MDouble(2.0), new MDouble(0.5), new MDouble(6.0)};
  
  private MNumber[] matrix9 = //imaginary elliptic cylinder (REP ING S. 243, 10.18) ok
  { new MDouble(2.0), new MDouble(-1.0), new MDouble(0.0), new MDouble(0.0),
      new MDouble(-1.0), new MDouble(1.0), new MDouble(0.0), new MDouble(-2.0),
      new MDouble(0.0), new MDouble(0.0), new MDouble(0.0), new MDouble(0.0),
      new MDouble(0.0), new MDouble(-2.0), new MDouble(0.0), new MDouble(9.0)};
  
  private MNumber[] matrix8 = //pair of coincident parallel planes (REP ING S. 243, 10.20) ok
  { new MDouble(4.0), new MDouble(-6.0), new MDouble(0.0), new MDouble(2.0),
      new MDouble(-6.0), new MDouble(9.0), new MDouble(0.0), new MDouble(-3.0),
      new MDouble(0.0), new MDouble(0.0), new MDouble(0.0), new MDouble(0.0),
      new MDouble(2.0), new MDouble(-3.0), new MDouble(0.0), new MDouble(1.0)};
  
  private MNumber[] matrix7 = //pair of distinct parallel planes (REP ING S. 242, 10.17) ok
  { new MDouble(1.0), new MDouble(-Math.sqrt(3.0)), new MDouble(0.0), new MDouble(0.0),
      new MDouble(-Math.sqrt(3.0)), new MDouble(3.0), new MDouble(0.0), new MDouble(0.0),
      new MDouble(0.0), new MDouble(0.0), new MDouble(0.0), new MDouble(0.0),
      new MDouble(0.0), new MDouble(0.0), new MDouble(0.0), new MDouble(-8.0)};
  
  private MNumber[] matrix6 = //straight line (REP ING S. 242, 10.19) ok
  { new MDouble(3.0), new MDouble(-2.0), new MDouble(0.0), new MDouble(-4.0),
      new MDouble(-2.0), new MDouble(4.0), new MDouble(0.0), new MDouble(0.0),
      new MDouble(0.0), new MDouble(0.0), new MDouble(0.0), new MDouble(0.0),
      new MDouble(-4.0), new MDouble(0.0), new MDouble(0.0), new MDouble(8.0)};
  
  private MNumber[] matrix5 = //elliptic paraboloid (REP ING S. 242, 10.16) ok
  { new MDouble(1.6), new MDouble(-1.2), new MDouble(1.2), new MDouble(0.8*Math.sqrt(2.0)-0.25),
      new MDouble(-1.2), new MDouble(0.9), new MDouble(-0.9), new MDouble(-0.6*Math.sqrt(2.0)-0.4),
      new MDouble(1.2), new MDouble(-0.9), new MDouble(2.5), new MDouble(-Math.sqrt(2.0)),
      new MDouble(0.8*Math.sqrt(2.0)-0.25), new MDouble(-0.6*Math.sqrt(2.0)-0.4), new MDouble(-Math.sqrt(2.0)), new MDouble(3.0)};
  
  private MNumber[] matrix4 = //hyperboloid of one sheet (REP LA S.297, 5.4.10) ok
  { new MDouble(-3.0), new MDouble(0.0), new MDouble(-5.0), new MDouble(-3.0*Math.sqrt(2.0)),
      new MDouble(0.0), new MDouble(4.0), new MDouble(0.0), new MDouble(0.0),
      new MDouble(-5.0), new MDouble(0.0), new MDouble(-3.0), new MDouble(-5.0*Math.sqrt(2.0)),
      new MDouble(-3.0*Math.sqrt(2.0)), new MDouble(0.0), new MDouble(-5.0*Math.sqrt(2.0)), new MDouble(-14.0)};
  
  private MNumber[] matrix3 = //elliptic cylinder (REP LA S.296, 5.4.9) ok
  { new MDouble(5.0), new MDouble(-4.0), new MDouble(2.0), new MDouble(0.0),
      new MDouble(-4.0), new MDouble(5.0), new MDouble(2.0), new MDouble(0.0),
      new MDouble(2.0), new MDouble(2.0), new MDouble(8.0), new MDouble(0.0),
      new MDouble(0.0), new MDouble(0.0), new MDouble(0.0), new MDouble(-36.0)};
  
  private MNumber[] matrix2 = //parabolic cylinder (REP LA S.294, 5.4.8) ok
  { new MDouble(9.0), new MDouble(12.0), new MDouble(0.0), new MDouble(5.0),
      new MDouble(12.0), new MDouble(16.0), new MDouble(0.0), new MDouble(-10.0),
      new MDouble(0.0), new MDouble(0.0), new MDouble(0.0), new MDouble(10.0),
      new MDouble(5.0), new MDouble(-10.0), new MDouble(10.0), new MDouble(21.0)};
  
  private MNumber[] matrix1 = //ellipsoid (REP ING S. 241, 10.15) ok
  { new MDouble(45.0), new MDouble(0.0), new MDouble(-27.0), new MDouble(27.0*Math.sqrt(2)),
      new MDouble(0.0), new MDouble(8.0), new MDouble(0.0), new MDouble(-16.0),
      new MDouble(-27.0), new MDouble(0.0), new MDouble(45.0), new MDouble(27.0*Math.sqrt(2)),
      new MDouble(27.0*Math.sqrt(2)), new MDouble(-16.0), new MDouble(27.0*Math.sqrt(2)), new MDouble(122.0)};
*/  
  private NumberMatrix quadricMatrix = new NumberMatrix(MDouble.class, 4, matrix14);
  
  private MMAffine3DQuadric quadric = new MMAffine3DQuadric(quadricMatrix);
  
  private DisplayProperties dp;
  private MMDefaultR3 r3;
  
  //    Affine3DPoint testpoint = quadric.getPoint();
  //    DisplayProperties pp = new PointDisplayProperties();
  //    pp.setObjectColor(Color.red);
  //    ((MMAffine3DPoint)testpoint).setDisplayProperties(pp);
  //    m_canvas.addObject((MMAffine3DPoint)testpoint);
  
  public void init(){
    super.init();
    initializeObjects();
    getCanvas().addObject(r3);
    addResetButton();
    addScreenShotButton();
  }
  
  public void initializeObjects(){
    r3 = new MMDefaultR3(MDouble.class);
    dp = new SurfaceDisplayProperties();
    dp.setObjectColor(Color.blue);
    quadric.setFromQuadricMatrix(quadricMatrix);
    quadric.setDisplayProperties(dp);
    getCanvas().addObject(quadric);
  }
  
  public void reset() {
    getCanvas().removeAllObjects();
    initializeObjects();
    getCanvas().renderScene();
    getCanvas().repaint();
  }
  
  /**
   * Runs the test.
   *
   * @param    args                a  <code>String[]</code>
   *
   */
  public static void main(String[] args){
    java.util.logging.Logger.getLogger("").setLevel(java.util.logging.Level.SEVERE);
    Affine3DQuadricTest myApplet = new Affine3DQuadricTest();
    myApplet.init();
    myApplet.start();
    BasicApplicationFrame f = new BasicApplicationFrame(myApplet, 600);
    f.pack();
    f.setVisible(true);
  }
}



