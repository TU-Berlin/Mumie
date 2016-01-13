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
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DLineSegment;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPoint;
import net.mumie.mathletfactory.util.BasicApplicationFrame;

/**
 * Creates a triangle given by three moveable
 * {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPoint}s and
 * computes the median lines and the centroid of the triangle.
 *
 * @author Mrose
 * @mm.docstatus finished
 */
public class TriangleMedianLineTest extends SingleG2DCanvasApplet{
  
  private MMAffine2DPoint A, B, C, centroid;
  private MMAffine2DLineSegment AB, BC, CA;
  private MMAffine2DLineSegment median_AB, median_BC, median_CA;
  
  private PointDisplayProperties pp;
  private LineDisplayProperties ll, kk;
  
  private Affine2DKeyboardTranslateHandler akth;
  private Affine2DMouseTranslateHandler amth;
  
  public void init() {
    super.init();
    setTitle("Triangle Median Line Test");
    amth = new Affine2DMouseTranslateHandler(getCanvas());
    akth = new Affine2DKeyboardTranslateHandler(getCanvas());
    createObjects();
    initializeObjects();
    
    getCanvas().addObject(AB);
    getCanvas().addObject(BC);
    getCanvas().addObject(CA);
    getCanvas().addObject(median_AB);
    getCanvas().addObject(median_BC);
    getCanvas().addObject(median_CA);
    getCanvas().addObject(A);
    getCanvas().addObject(B);
    getCanvas().addObject(C);
    getCanvas().addObject(centroid);
    
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
    centroid = getCentroid(A,B,C);
    AB = new MMAffine2DLineSegment(A, B);
    BC = new MMAffine2DLineSegment(B, C);
    CA = new MMAffine2DLineSegment(C, A);
    median_AB = new MMAffine2DLineSegment(C, MMAffine2DPoint.getCentrePoint(A,B));
    median_BC = new MMAffine2DLineSegment(A, MMAffine2DPoint.getCentrePoint(C,B));
    median_CA = new MMAffine2DLineSegment(B, MMAffine2DPoint.getCentrePoint(A,C));
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
    centroid.setDisplayProperties(pp);
    AB.setDisplayProperties(ll);
    BC.setDisplayProperties(ll);
    CA.setDisplayProperties(ll);
    median_AB.setDisplayProperties(kk);
    median_BC.setDisplayProperties(kk);
    median_CA.setDisplayProperties(kk);
    
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
        MMAffine2DLineSegment line = (MMAffine2DLineSegment) dependant;
        line.setInitialPoint((MMAffine2DPoint)free[2]);
        line.setEndPoint(MMAffine2DPoint.getCentrePoint((MMAffine2DPoint)free[0], (MMAffine2DPoint)free[1]));
      }
    };
    median_AB.dependsOn(new MMObjectIF[]{A, B, C}, DPA);
    median_BC.dependsOn(new MMObjectIF[]{B, C, A}, DPA);
    median_CA.dependsOn(new MMObjectIF[]{C, A, B}, DPA);
    
    DPA = new DependencyAdapter() {
      public void doUpdate(MMObjectIF dependant, MMObjectIF[] free) {
        MMAffine2DPoint point = (MMAffine2DPoint) dependant;
        point.setX(getCentroid((MMAffine2DPoint)free[0], (MMAffine2DPoint)free[1], (MMAffine2DPoint)free[2]).getXAsDouble());
        point.setY(getCentroid((MMAffine2DPoint)free[0], (MMAffine2DPoint)free[1], (MMAffine2DPoint)free[2]).getYAsDouble());
      }
    };
    centroid.dependsOn(new MMObjectIF[]{A, B, C}, DPA);
  }
  
  public void reset(){
    initializeObjects();
    getCanvas().renderScene();
    getCanvas().repaint();
  }
  
  /**
   * Gets the centroid of a triangle ABC.
   *
   * @param    A    the first corner of the triangle
   * @param    B    the second corner of the triangle
   * @param    C    the third corner of the triangle
   *
   * @return   the centroid
   *
   */
  private MMAffine2DPoint getCentroid(MMAffine2DPoint A,
                                      MMAffine2DPoint B,
                                      MMAffine2DPoint C){
    
    MNumber one_third  = NumberFactory.newInstance(A.getNumberClass(), 1).div(
      NumberFactory.newInstance(A.getNumberClass()).setDouble(3));
    NumberTuple newCentroidCoords =
      (NumberTuple)A.getAffineCoordinatesOfPoint().addTo(
      B.getAffineCoordinatesOfPoint().addTo
      (C.getAffineCoordinatesOfPoint())).multWithNumber(one_third);
    NumberTuple affineCoordsA = A.getAffineCoordinatesOfPoint();
    NumberTuple affineCoordsB = B.getAffineCoordinatesOfPoint();
    NumberTuple affineCoordsC = C.getAffineCoordinatesOfPoint();
    
    NumberTuple affineCoordsCentroid =
      ((NumberTuple)affineCoordsA.deepCopy()).addTo
      ((NumberTuple)affineCoordsB.deepCopy().addTo
         ((NumberTuple)affineCoordsC.deepCopy())).multiplyWithScalar
      ((new MDouble(1.0)).div(new MDouble(3.0)));
    
    NumberTuple projCoordsCentroid =
      new NumberTuple(MDouble.class, new Object[]{affineCoordsCentroid.getEntry(1),
            affineCoordsCentroid.getEntry(2),
            C.getProjectiveCoordinatesOfPoint().getEntry(3)});
    
    MMAffine2DPoint centroid = new MMAffine2DPoint(MDouble.class);
    centroid.setProjectiveCoordinates(projCoordsCentroid);
    
    return centroid;
  }
  
  /**
   * Runs the test.
   *
   * @param    args                a  <code>String[]</code>
   *
   */
  public static void main(String[] args){
    java.util.logging.Logger.getLogger("").setLevel(java.util.logging.Level.SEVERE);
    TriangleMedianLineTest myApplet = new TriangleMedianLineTest();
    myApplet.init();
    myApplet.start();
    BasicApplicationFrame f = new BasicApplicationFrame(myApplet, 500);
    f.pack();
    f.setVisible(true);
  }
  
}

