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
import java.awt.geom.Point2D;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.mumie.mathletfactory.action.handler.Affine2DKeyboardTranslateHandler;
import net.mumie.mathletfactory.action.handler.Affine2DMouseTranslateHandler;
import net.mumie.mathletfactory.action.updater.DependencyAdapter;
import net.mumie.mathletfactory.appletskeleton.g2d.SingleG2DCanvasApplet;
import net.mumie.mathletfactory.display.LineDisplayProperties;
import net.mumie.mathletfactory.display.PointDisplayProperties;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DLineSegment;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPoint;
import net.mumie.mathletfactory.util.BasicApplicationFrame;

/**
 * Creates a triangle given by three moveable
 * {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPoint}s and
 * computes the altitudes.
 *
 * @author Mrose
 * @mm.docstatus finished
 *
 */
public class TriangleAltitudeTest extends SingleG2DCanvasApplet{
  
  private MMAffine2DPoint A, B, C;
  private MMAffine2DLineSegment AB, BC, CA;
  private MMAffine2DLineSegment aFootC, bFootA, cFootB, bFootC, cFootA, aFootB;
  private MMAffine2DLineSegment h_A, h_B, h_C;
  
  private PointDisplayProperties pp;
  private LineDisplayProperties ll, kk, mm;
  
  private Affine2DKeyboardTranslateHandler akth;
  private Affine2DMouseTranslateHandler amth;
  
  public void init() {
    super.init();
    setLoggerLevel(Level.WARNING);
    setTitle("Triangle Altitude Test");
    createObjects();
    initializeObjects();
    
    getCanvas().addObject(aFootC);
    getCanvas().addObject(bFootC);
    getCanvas().addObject(bFootA);
    getCanvas().addObject(cFootA);
    getCanvas().addObject(cFootB);
    getCanvas().addObject(aFootB);
    getCanvas().addObject(h_A);
    getCanvas().addObject(h_B);
    getCanvas().addObject(h_C);
    getCanvas().addObject(AB);
    getCanvas().addObject(BC);
    getCanvas().addObject(CA);
    getCanvas().addObject(A);
    getCanvas().addObject(B);
    getCanvas().addObject(C);
    getCanvas2D().autoScale();
    addResetButton();
    addScreenShotButton();
  }
  
  private void createObjects() {
    amth = new Affine2DMouseTranslateHandler(getCanvas());
    akth = new Affine2DKeyboardTranslateHandler(getCanvas());
    pp = new PointDisplayProperties();
    ll = new LineDisplayProperties();
    mm = new LineDisplayProperties();
    kk = new LineDisplayProperties();
    
    A = new MMAffine2DPoint(MDouble.class);
    A.addHandler(akth);
    A.addHandler(amth);
    B = new MMAffine2DPoint(MDouble.class);
    B.addHandler(akth);
    B.addHandler(amth);
    C = new MMAffine2DPoint(MDouble.class);
    C.addHandler(akth);
    C.addHandler(amth);
    
    AB = new MMAffine2DLineSegment(A, B);
    BC = new MMAffine2DLineSegment(B, C);
    CA = new MMAffine2DLineSegment(C, A);
    
    aFootC = new MMAffine2DLineSegment(A, getPerpendicularFoot(A, B, C));
    bFootC = new MMAffine2DLineSegment(B, getPerpendicularFoot(A, B, C));
    bFootA = new MMAffine2DLineSegment(B, getPerpendicularFoot(B, C, A));
    cFootA = new MMAffine2DLineSegment(C, getPerpendicularFoot(B, C, A));
    cFootB = new MMAffine2DLineSegment(C, getPerpendicularFoot(C, A, B));
    aFootB = new MMAffine2DLineSegment(A, getPerpendicularFoot(C, A, B));
    
    h_A = new MMAffine2DLineSegment(C, getPerpendicularFoot(A, B, C));
    h_B = new MMAffine2DLineSegment(A, getPerpendicularFoot(B, C, A));
    h_C = new MMAffine2DLineSegment(B, getPerpendicularFoot(C, A, B));
  }
  
  protected void initializeObjects(){
    amth.setDrawDuringAction(true);
    amth.setUpdateDuringAction(true);
    
    pp.setObjectColor(Color.blue);
    pp.setShadowOffset(new Point2D.Double(-1,-1));
    ll.setArrowAtEnd(LineDisplayProperties.LINE_END);
    ll.setArrowAtStart(LineDisplayProperties.LINE_END);
    ll.setObjectColor(Color.red);
    ll.setShadowOffset(new Point2D.Double(0,0));
    mm.setArrowAtEnd(LineDisplayProperties.LINE_END);
    mm.setArrowAtStart(LineDisplayProperties.LINE_END);
    mm.setObjectColor(Color.red);
    mm.setShadowOffset(new Point2D.Double(0,0));
    mm.setFilled(false);
    kk.setArrowAtEnd(LineDisplayProperties.LINE_END);
    kk.setArrowAtStart(LineDisplayProperties.LINE_END);
    kk.setObjectColor(Color.yellow);
    kk.setShadowOffset(new Point2D.Double(0,0));
    
    A.setDisplayProperties(pp);
    B.setDisplayProperties(pp);
    C.setDisplayProperties(pp);
    
    AB.setDisplayProperties(ll);
    BC.setDisplayProperties(ll);
    CA.setDisplayProperties(ll);
    
    aFootC.setDisplayProperties(mm);
    bFootC.setDisplayProperties(mm);
    bFootA.setDisplayProperties(mm);
    cFootA.setDisplayProperties(mm);
    cFootB.setDisplayProperties(mm);
    aFootB.setDisplayProperties(mm);
    
    h_A.setDisplayProperties(kk);
    h_B.setDisplayProperties(kk);
    h_C.setDisplayProperties(kk);
    
    A.setXY(-1, 0.1);
    B.setXY(1, -0.1);
    C.setXY(0, 1.3);
    
    
    DependencyAdapter DPA = new DependencyAdapter() {
      public void doUpdate(MMObjectIF dependant, MMObjectIF[] free) {
        MMAffine2DLineSegment line = (MMAffine2DLineSegment) dependant;
        line.setInitialPoint((MMAffine2DPoint)free[0]);
        line.setEndPoint((MMAffine2DPoint)free[1]);
      }
    };
    AB.dependsOn(new MMObjectIF[]{A, B}, DPA);
    BC.dependsOn(new MMObjectIF[]{B, C}, DPA);
    CA.dependsOn(new MMObjectIF[]{C, A}, DPA);
    
    DPA = new DependencyAdapter() {
      public void doUpdate(MMObjectIF dependant, MMObjectIF[] free) {
        MMAffine2DLineSegment line = (MMAffine2DLineSegment) dependant;
        line.setInitialPoint(((MMAffine2DPoint)free[1]));
        line.setEndPoint(getPerpendicularFoot(((MMAffine2DLineSegment)free[0]).getInitialPoint(),
                                              ((MMAffine2DLineSegment)free[0]).getEndPoint(),
                                              ((MMAffine2DPoint)free[1])));
      }
    };

    h_A.dependsOn(new MMObjectIF[]{BC, A}, DPA);
    h_B.dependsOn(new MMObjectIF[]{CA, B}, DPA);
    h_C.dependsOn(new MMObjectIF[]{AB, C}, DPA);
    
    DPA = new DependencyAdapter() {
      public void doUpdate(MMObjectIF dependant, MMObjectIF[] free) {
        MMAffine2DLineSegment line = (MMAffine2DLineSegment) dependant;
        line.setInitialPoint((MMAffine2DPoint)free[0]);
        line.setEndPoint(getPerpendicularFoot((MMAffine2DPoint)free[1],
                                                (MMAffine2DPoint)free[2],
                                                (MMAffine2DPoint)free[3]));
      }
    };
    aFootC.dependsOn(new MMObjectIF[]{A, A, B, C}, DPA);
    bFootC.dependsOn(new MMObjectIF[]{B, A, B, C}, DPA);
    bFootA.dependsOn(new MMObjectIF[]{B, B, C, A}, DPA);
    cFootA.dependsOn(new MMObjectIF[]{C, B, C, A}, DPA);
    cFootB.dependsOn(new MMObjectIF[]{C, C, A, B}, DPA);
    aFootB.dependsOn(new MMObjectIF[]{A, C, A, B}, DPA);
  }
  
  /**
   * Gets perpendicular footpoint of B in a triangle ABC.
   * Attention: The order of the parameters is important!
   *
   * @param    A           the first corner of the triangle
   * @param    B           the second corner of the triangle for which the
   * footpoint of the altitude is computed
   * @param    C           the third corner of the triangle
   *
   * @return   the footpoint of the altitude through B
   *
   */
  private MMAffine2DPoint getPerpendicularFoot(MMAffine2DPoint A,
                                               MMAffine2DPoint B,
                                               MMAffine2DPoint C){
    
    NumberTuple affineCoordsA = A.getAffineCoordinatesOfPoint();
    NumberTuple affineCoordsB = B.getAffineCoordinatesOfPoint();
    NumberTuple affineCoordsC = C.getAffineCoordinatesOfPoint();
    
    NumberTuple bMinusA = ((NumberTuple)affineCoordsB.deepCopy()).subFrom(affineCoordsA);
    NumberTuple cMinusA = ((NumberTuple)affineCoordsC.deepCopy()).subFrom(affineCoordsA);
    
    //<C-A, B-A>
    MNumber scProdCminusAbMinusA =  cMinusA.scalarProduct(bMinusA);
    
    //<B-A, B-A> = |B-A|²
    MNumber scProdbMinusAbMinusA =  bMinusA.scalarProduct(bMinusA);
    
    //A + (B-A)*(<C-A, B-A>/|B-A|²)
    NumberTuple affineCoordsFoot = ((NumberTuple)affineCoordsA.deepCopy()).addTo(
      bMinusA.multiplyWithScalar(MNumber.divide(scProdCminusAbMinusA, scProdbMinusAbMinusA)));
    
    NumberTuple projCoordsFoot =
      new NumberTuple(MDouble.class,
                      new Object[]{affineCoordsFoot.getEntry(1),
            affineCoordsFoot.getEntry(2),
            C.getProjectiveCoordinatesOfPoint().getEntry(3)});
    
    MMAffine2DPoint foot = new MMAffine2DPoint(MDouble.class);
    foot.setProjectiveCoordinates(projCoordsFoot);
    
    return foot;
  }
  
  /**
   * Runs the test
   *
   * @param    args               a  <code>String[]</code>
   */
  public static void main(String[] args){
    Logger.getLogger("").setLevel(java.util.logging.Level.SEVERE);
    TriangleAltitudeTest myApplet = new TriangleAltitudeTest();
    myApplet.init();
    myApplet.start();
    BasicApplicationFrame f = new BasicApplicationFrame(myApplet, 500);
    f.pack();
    f.setVisible(true);
  }
  
}

