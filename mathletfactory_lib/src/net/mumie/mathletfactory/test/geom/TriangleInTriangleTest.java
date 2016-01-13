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
import java.util.logging.Logger;

import net.mumie.mathletfactory.action.handler.Affine2DKeyboardTranslateHandler;
import net.mumie.mathletfactory.action.handler.Affine2DMouseTranslateHandler;
import net.mumie.mathletfactory.action.updater.DependencyAdapter;
import net.mumie.mathletfactory.appletskeleton.g2d.SingleG2DCanvasApplet;
import net.mumie.mathletfactory.display.LineDisplayProperties;
import net.mumie.mathletfactory.display.PointDisplayProperties;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DLineSegment;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPoint;
import net.mumie.mathletfactory.util.BasicApplicationFrame;

/**
 * Create a triangle given by three moveable
 * {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPoint}s and
 * computes the Triangleln Triangle.
 *
 * @author Liu
 * @mm.docstatus finished
 */
public class TriangleInTriangleTest extends SingleG2DCanvasApplet{
	
    private MMAffine2DPoint A, B, C, X, Y, Z;
    private MMAffine2DLineSegment AB, BC, CA,XY, YZ, ZX;
	
	private PointDisplayProperties pp;
    private LineDisplayProperties ll, kk;
	
	private Affine2DKeyboardTranslateHandler akth;
    private Affine2DMouseTranslateHandler amth;
	
	public void init(){
    super.init();
		setTitle("Triangle In Triangle Test");
		amth = new Affine2DMouseTranslateHandler(getCanvas());
        akth = new Affine2DKeyboardTranslateHandler(getCanvas());
		createObjects();
		initializeObjects();
		
		getCanvas().addObject(AB);
        getCanvas().addObject(BC);
        getCanvas().addObject(CA);
		getCanvas().addObject(XY);
        getCanvas().addObject(YZ);
        getCanvas().addObject(ZX);
		getCanvas().addObject(A);
        getCanvas().addObject(B);
        getCanvas().addObject(C);
		getCanvas().addObject(X);
        getCanvas().addObject(Y);
        getCanvas().addObject(Z);
		
		addResetButton();
		addScreenShotButton();
	}
	
	private void createObjects(){
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
		
		X = MMAffine2DPoint.getCentrePoint(A, B);
        Y = MMAffine2DPoint.getCentrePoint(B, C);
        Z = MMAffine2DPoint.getCentrePoint(C, A);
		
		AB = new MMAffine2DLineSegment(A, B);
        BC = new MMAffine2DLineSegment(B, C);
        CA = new MMAffine2DLineSegment(C, A);
		
		XY = new MMAffine2DLineSegment(X, Y);
        YZ = new MMAffine2DLineSegment(Y, Z);
        ZX = new MMAffine2DLineSegment(Z, X);
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
		X.setDisplayProperties(pp);
        Y.setDisplayProperties(pp);
        Z.setDisplayProperties(pp);
		
		AB.setDisplayProperties(ll);
        BC.setDisplayProperties(ll);
        CA.setDisplayProperties(ll);
		XY.setDisplayProperties(kk);
        YZ.setDisplayProperties(kk);
        ZX.setDisplayProperties(kk);
		
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
           line.setInitialPoint(MMAffine2DPoint.getCentrePoint((MMAffine2DPoint)free[2], (MMAffine2DPoint)free[3]));
           line.setEndPoint(MMAffine2DPoint.getCentrePoint((MMAffine2DPoint)free[0], (MMAffine2DPoint)free[1]));
         }
       };
	   
	   XY.dependsOn(new MMObjectIF[]{B,C,A,B},DPA);
	   YZ.dependsOn(new MMObjectIF[]{A,C,B,C},DPA);
	   ZX.dependsOn(new MMObjectIF[]{A,B,A,C},DPA);
	   
	   DPA = new DependencyAdapter(){
		   public void doUpdate(MMObjectIF dependant, MMObjectIF[] free) {
             MMAffine2DPoint point = (MMAffine2DPoint) dependant;
             point.setX(MMAffine2DPoint.getCentrePoint((MMAffine2DPoint)free[0], (MMAffine2DPoint)free[1]).getXAsDouble());
             point.setY(MMAffine2DPoint.getCentrePoint((MMAffine2DPoint)free[0], (MMAffine2DPoint)free[1]).getYAsDouble());
          }
       };
	   X.dependsOn(new MMObjectIF[]{A,B},DPA);
	   Y.dependsOn(new MMObjectIF[]{B,C},DPA);
	   Z.dependsOn(new MMObjectIF[]{C,A},DPA);
   }
	
	public void reset(){
      initializeObjects();
      getCanvas().renderScene();
      getCanvas().repaint();
    }
	
	public static void main(String[] args){
      Logger.getLogger("").setLevel(java.util.logging.Level.SEVERE);
    
      TriangleInTriangleTest myApplet = new TriangleInTriangleTest();
      myApplet.init();
      myApplet.start();
      BasicApplicationFrame f = new BasicApplicationFrame(myApplet, 500);
      f.pack();
      f.setVisible(true);
    }
}

