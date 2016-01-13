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

import net.mumie.mathletfactory.action.handler.Affine2DKeyboardTranslateHandler;
import net.mumie.mathletfactory.action.handler.Affine2DMouseTranslateHandler;
import net.mumie.mathletfactory.action.updater.DependencyAdapter;
import net.mumie.mathletfactory.appletskeleton.g2d.SingleG2DCanvasApplet;
import net.mumie.mathletfactory.display.LineDisplayProperties;
import net.mumie.mathletfactory.display.PointDisplayProperties;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.geom.affine.Affine2DPoint;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DLine;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DLineSegment;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPoint;
import net.mumie.mathletfactory.util.BasicApplicationFrame;


/**
 * Creates a triangle given by three moveable
 * {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPoint}s and
 * computes the perpendicular bisectors of the sides of the triangle.
 *
 * @author Mrose
 * @mm.docstatus finished
 */
public class TrianglePerpendicularBisectorTest extends SingleG2DCanvasApplet{
  
  private MMAffine2DPoint A, B, C;
  private MMAffine2DLineSegment AB, BC, CA;
  private MMAffine2DLine perpendicularBisector_AB, perpendicularBisector_BC,
    perpendicularBisector_CA;
  
  private PointDisplayProperties pp;
  private LineDisplayProperties ll, kk;
  
  private Affine2DKeyboardTranslateHandler akth = new Affine2DKeyboardTranslateHandler(m_canvas);
  private Affine2DMouseTranslateHandler amth = new Affine2DMouseTranslateHandler(m_canvas);
  
  public void init() {
    super.init();
    setTitle("Triangle Perpendicular Bisector Test");
    amth = new Affine2DMouseTranslateHandler(getCanvas());
    akth = new Affine2DKeyboardTranslateHandler(getCanvas());
    createObjects();
    initializeObjects();
    
    getCanvas().addObject(AB);
    getCanvas().addObject(BC);
    getCanvas().addObject(CA);
    getCanvas().addObject(perpendicularBisector_AB);
    getCanvas().addObject(perpendicularBisector_BC);
    getCanvas().addObject(perpendicularBisector_CA);
    getCanvas().addObject(A);
    getCanvas().addObject(B);
    getCanvas().addObject(C);
    
    addResetButton();
    addScreenShotButton();
  }
  
  private void createObjects() {
    pp = new PointDisplayProperties();
    ll = new LineDisplayProperties();
    kk = new LineDisplayProperties();
    
    A = new MMAffine2DPoint(MDouble.class, -0.3, 0.3);
    A.addHandler(akth);
    A.addHandler(amth);
    B = new MMAffine2DPoint(MDouble.class, 0.25, 0.25);
    B.addHandler(akth);
    B.addHandler(amth);
    C = new MMAffine2DPoint(MDouble.class, -0.25, -0.25);
    C.addHandler(akth);
    C.addHandler(amth);
    AB = new MMAffine2DLineSegment(A, B);
    BC = new MMAffine2DLineSegment(B, C);
    CA = new MMAffine2DLineSegment(C, A);
    perpendicularBisector_AB = new MMAffine2DLine(getInitialPoint(A,B),getEndPoint(A,B));
    perpendicularBisector_BC = new MMAffine2DLine(getInitialPoint(B,C),getEndPoint(B,C));
    perpendicularBisector_CA = new MMAffine2DLine(getInitialPoint(C,A),getEndPoint(C,A));
  }
  
  protected void initializeObjects(){
    amth.setDrawDuringAction(true);
    amth.setUpdateDuringAction(true);
    
    pp.setObjectColor(Color.blue);
    ll.setArrowAtEnd(LineDisplayProperties.LINE_END);
    ll.setArrowAtStart(LineDisplayProperties.LINE_END);
    ll.setObjectColor(Color.red);
    ll.setShadowOffset(new Point2D.Double(0,0));
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
    perpendicularBisector_AB.setDisplayProperties(kk);
    perpendicularBisector_BC.setDisplayProperties(kk);
    perpendicularBisector_CA.setDisplayProperties(kk);
    
    A.setXY(-0.3, 0.3);
    B.setXY(0.25, 0.25);
    C.setXY(-0.25, -0.25);
    
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
        MMAffine2DLine line = (MMAffine2DLine) dependant;
        Affine2DPoint initPoint = 
          getInitialPoint((MMAffine2DPoint)free[0], (MMAffine2DPoint)free[1]);
        Affine2DPoint endPoint = 
          getEndPoint((MMAffine2DPoint)free[0], (MMAffine2DPoint)free[1]); 
        line.setFromPoints(new Affine2DPoint[] {initPoint, endPoint});
      }
    };
    perpendicularBisector_AB.dependsOn(new MMObjectIF[]{A, B}, DPA);
    perpendicularBisector_BC.dependsOn(new MMObjectIF[]{B, C}, DPA);
    perpendicularBisector_CA.dependsOn(new MMObjectIF[]{C, A}, DPA);
  }
  
  public void reset(){
    initializeObjects();
    getCanvas().renderScene();
    getCanvas().repaint();
  }
  
  /**
   * Computes the initialpoint of the perpendicular bisector of the side PQ of
   * a triangle.
   *
   * @param    P                   a  <code>MMAffine2DPoint</code>
   * @param    Q                   a  <code>MMAffine2DPoint</code>
   *
   * @return   a <code>MMAffine2DPoint</code>
   *
   */
  private MMAffine2DPoint getInitialPoint(MMAffine2DPoint P, MMAffine2DPoint Q){
    MMAffine2DPoint m = MMAffine2DPoint.getCentrePoint(P,Q);
    MMAffine2DPoint orth = calcOrthDirection(P,Q);
    MNumber alpha = calculateAlpha(orth);
    NumberTuple affineCoords = m.getAffineCoordinatesOfPoint().addTo((NumberTuple)orth.getAffineCoordinatesOfPoint().multWithNumber(alpha));
    NumberTuple projCoords = new NumberTuple(P.getNumberClass(),new Object[]{affineCoords.getEntry(1),affineCoords.getEntry(2),P.getProjectiveCoordinatesOfPoint().getEntry(3)});
    MMAffine2DPoint ret = new MMAffine2DPoint(MDouble.class);
    ret.setProjectiveCoordinates(projCoords);
    return ret;
  }
  
  /**
   * Computes the endpoint of the perpendicular bisector of the side PQ of
   * a triangle.
   *
   * @param    P                   a  <code>MMAffine2DPoint</code>
   * @param    Q                   a  <code>MMAffine2DPoint</code>
   *
   * @return   a <code>MMAffine2DPoint</code>
   *
   */
  private MMAffine2DPoint getEndPoint(MMAffine2DPoint P, MMAffine2DPoint Q) {
    MMAffine2DPoint m = MMAffine2DPoint.getCentrePoint(P,Q);
    MMAffine2DPoint orth = calcOrthDirection(P,Q);
    MNumber alpha = calculateAlpha(orth).negate();
    NumberTuple affineCoords = m.getAffineCoordinatesOfPoint().addTo((NumberTuple)orth.getAffineCoordinatesOfPoint().multWithNumber(alpha));
    NumberTuple projCoords = new NumberTuple(P.getNumberClass(),new Object[]{affineCoords.getEntry(1),affineCoords.getEntry(2),P.getProjectiveCoordinatesOfPoint().getEntry(3)});
    MMAffine2DPoint ret = new MMAffine2DPoint(MDouble.class);
    ret.setProjectiveCoordinates(projCoords);
    return ret;
  }
  
  /**
   * If P and Q are points in the 2-dimensional space then the method returns a
   * point C such that the vector 0C is orthogonal to the vector Q-P.
   *
   * @param    P                   a  <code>MMAffine2DPoint</code>
   * @param    Q                   a  <code>MMAffine2DPoint</code>
   *
   * @return   a <code>MMAffine2DPoint</code>
   *
   */
  private MMAffine2DPoint calcOrthDirection(MMAffine2DPoint P, MMAffine2DPoint Q) {
    NumberTuple pcoords = P.getProjectiveCoordinatesOfPoint();
    NumberTuple qcoords = Q.getProjectiveCoordinatesOfPoint();
    MNumber c1 = MNumber.subtract(qcoords.getEntry(2),pcoords.getEntry(2)).negate();
    MNumber c2 = MNumber.subtract(qcoords.getEntry(1),pcoords.getEntry(1));
    MNumber c3 = pcoords.getEntry(3);
    // we expect both points being "finite":
    NumberTuple v = new NumberTuple(P.getNumberClass(),new Object[]{c1,c2,c3});
    MMAffine2DPoint ret = new MMAffine2DPoint(P.getNumberClass());
    ret.setProjectiveCoordinates(v);
    return ret;
  }
  
  /**
   * Returns 1/(2*|P|) where |P| is the standard norm of P.
   *
   * @param    P                   a  <code>MMAffine2DPoint</code>
   *
   * @return   a <code>MNumber</code>
   *
   */
  private MNumber calculateAlpha(MMAffine2DPoint P) {
    //would not work for MInteger:
    MNumber half = NumberFactory.newInstance(P.getNumberClass(), 0.5);
    return P.getAffineCoordinatesOfPoint().standardNorm().inverse().mult(half);//1/(2*|P|)
  }
  
  /**
   * Runs the test.
   *
   * @param    args                a  <code>String[]</code>
   *
   */
  public static void main(String[] args){
    java.util.logging.Logger.getLogger("").setLevel(java.util.logging.Level.SEVERE);
    TrianglePerpendicularBisectorTest myApplet = new TrianglePerpendicularBisectorTest();
    myApplet.init();
    myApplet.start();
    BasicApplicationFrame f = new BasicApplicationFrame(myApplet, 500);
    f.pack();
    f.setVisible(true);
  }
  
}

