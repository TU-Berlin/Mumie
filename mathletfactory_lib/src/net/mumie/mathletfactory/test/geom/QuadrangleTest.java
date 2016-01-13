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
 * Create a quadrangle given by four movable
 * {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPoint}s and
 * connect the four points with
 * {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DLineSegment}s
 *
 * @author Schimanowski
 * @mm.docstatus finished
 */
public class QuadrangleTest extends SingleG2DCanvasApplet {

  private  MMAffine2DPoint A, B, C, D;
  private  MMAffine2DLineSegment AB, BC, CD, DA;
  private  MMAffine2DLineSegment AC, BD;
  
  private  PointDisplayProperties pp;
  private  LineDisplayProperties ll, kk;
  
  private  Affine2DKeyboardTranslateHandler akth;
  private  Affine2DMouseTranslateHandler amth;
  
  public void init(){
    super.init();
    setTitle("Quadrangle Test");
    amth = new Affine2DMouseTranslateHandler(getCanvas());
    akth = new Affine2DKeyboardTranslateHandler(getCanvas());
    
    createObjects();
    initializeObjects();
    pp = new PointDisplayProperties();
    pp.setObjectColor(Color.blue);
    
    getCanvas().addObject(AB);
    getCanvas().addObject(BC);
    getCanvas().addObject(CD);
    getCanvas().addObject(DA);

    getCanvas().addObject(AC);
    getCanvas().addObject(BD);
     
    getCanvas().addObject(A);
    getCanvas().addObject(B);
    getCanvas().addObject(C);
    getCanvas().addObject(D);
    
    addResetButton();
    addScreenShotButton();
  }
    public void createObjects(){
     pp = new PointDisplayProperties();
     ll = new LineDisplayProperties();
     kk = new LineDisplayProperties();
    
    A = new MMAffine2DPoint(MDouble.class,0.25, 0.25);
    A.addHandler(akth);
    A.addHandler(amth);
    
    B = new MMAffine2DPoint(MDouble.class,-0.25, 0.25);
    B.addHandler(akth);
    B.addHandler(amth);
    
    C = new MMAffine2DPoint(MDouble.class,-0.25, -0.25);
    C.addHandler(akth);
    C.addHandler(amth);
    
    D = new MMAffine2DPoint(MDouble.class,0.25, -0.25);
    D.addHandler(akth);
    D.addHandler(amth);
    
    AB = new MMAffine2DLineSegment(A, B);
    BC = new MMAffine2DLineSegment(B, C);
    CD = new MMAffine2DLineSegment(C, D);
    DA = new MMAffine2DLineSegment(D, A);
    AC = new MMAffine2DLineSegment(A, C);
    BD = new MMAffine2DLineSegment(B, D);
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
    D.setDisplayProperties(pp);
    
    AB.setDisplayProperties(ll);
    BC.setDisplayProperties(ll);
    CD.setDisplayProperties(ll);
    DA.setDisplayProperties(ll);
    
    AC.setDisplayProperties(kk);
    BD.setDisplayProperties(kk);
    
    A.setXY(0.25, 0.25);
    B.setXY(-0.25, 0.25);
    C.setXY(-0.25, -0.25);
    D.setXY(0.25, -0.25);
    
    
    DependencyAdapter DPA = new DependencyAdapter() {
      public void doUpdate(MMObjectIF dependant, MMObjectIF[] free) {
      MMAffine2DLineSegment line = (MMAffine2DLineSegment) dependant;
        line.setInitialPoint((MMAffine2DPoint)free[0]);
        line.setEndPoint((MMAffine2DPoint)free[1]);
      }
    };
   AB.dependsOn(new MMObjectIF[]{A,B}, DPA);
   BC.dependsOn(new MMObjectIF[]{B,C}, DPA);
   CD.dependsOn(new MMObjectIF[]{C,D}, DPA);
   DA.dependsOn(new MMObjectIF[]{D,A}, DPA);
    
   AC.dependsOn(new MMObjectIF[]{A,C}, DPA);
   BD.dependsOn(new MMObjectIF[]{B,D}, DPA);
  }
  public void reset(){
    initializeObjects();
    getCanvas().renderScene();
    getCanvas().repaint();
  }

  
  public static void main(String[] args){
    Logger.getLogger("").setLevel(java.util.logging.Level.SEVERE);
    QuadrangleTest myApplet = new QuadrangleTest();
    myApplet.init();
    myApplet.start();
    BasicApplicationFrame f = new BasicApplicationFrame(myApplet, 500);
    f.pack();
    f.setVisible(true);
  }
  
}

