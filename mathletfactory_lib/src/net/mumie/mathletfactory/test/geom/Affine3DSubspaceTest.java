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

import net.mumie.mathletfactory.action.handler.Affine3DKeyboardTranslateHandler;
import net.mumie.mathletfactory.action.handler.Affine3DMouseTranslateHandler;
import net.mumie.mathletfactory.action.updater.DependencyAdapter;
import net.mumie.mathletfactory.appletskeleton.j3d.SingleJ3DCanvasApplet;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.SurfaceDisplayProperties;
import net.mumie.mathletfactory.display.j3d.MMJ3DCanvas;
import net.mumie.mathletfactory.math.geom.affine.AffineSpace;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR3;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine3DPlane;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine3DPoint;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine3DSubspace;
import net.mumie.mathletfactory.util.BasicApplicationFrame;

/**
 *  Test of two intersecting 
 *  {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine3DSubspace}
 *  and the calculation and rendering of the resulting 
 *  {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine3DSubspace}.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class Affine3DSubspaceTest extends SingleJ3DCanvasApplet {

  public void init() {
    super.init();
    initializeObjects();
  }

  public void initializeObjects() {
    getCanvas3D().setProjectionType(MMJ3DCanvas.PERSPECTIVE_PROJECTION);
    final MMAffine3DSubspace p1, p2;
    Affine3DMouseTranslateHandler amth1 =
      new Affine3DMouseTranslateHandler(m_canvas);
    Affine3DMouseTranslateHandler amth2 =
      new Affine3DMouseTranslateHandler(m_canvas);
    Affine3DKeyboardTranslateHandler kth1 =
      new Affine3DKeyboardTranslateHandler(m_canvas);
    Affine3DKeyboardTranslateHandler kth2 =
      new Affine3DKeyboardTranslateHandler(m_canvas);
    MMAffine3DPoint[] points =
      new MMAffine3DPoint[] {
        new MMAffine3DPoint(MDouble.class, 0.5, 0.5, 0.0),
        new MMAffine3DPoint(MDouble.class, -0.0, 0.5, -1.0),
        new MMAffine3DPoint(MDouble.class, 0.5, 0.0, -0.0)};
    p1 = new MMAffine3DSubspace(MDouble.class, points);
    DisplayProperties dp = new SurfaceDisplayProperties();
    //dp.setTransparency(0.2);
    dp.setObjectColor(Color.green);

    p1.addHandler(amth2);
    p1.addHandler(kth2);
    p1.setLabel("s1");
    p1.setDisplayProperties(dp);

    points =
      new MMAffine3DPoint[] {
        new MMAffine3DPoint(MDouble.class, 0.0, 0.5, 0.5),
        new MMAffine3DPoint(MDouble.class, 1.0, -0.0, 0.5),
        new MMAffine3DPoint(MDouble.class, 0.0, 0.5, 0.0)};

    p2 = new MMAffine3DPlane(points[0], points[1], points[2]);
    DisplayProperties dp2 = p2.getDisplayProperties();
    dp2.setObjectColor(Color.blue);

    p2.addHandler(amth2);
    p2.addHandler(kth2);
    p2.setLabel("s2");
    p2.setDisplayProperties(dp2);

    final MMAffine3DSubspace intersection =
      new MMAffine3DSubspace(MMAffine3DSubspace.intersected(p1, p2));
    intersection.setLabel("intersection");
    DisplayProperties dp3 = new DisplayProperties();
    dp3.setObjectColor(Color.red);
    intersection.setDisplayProperties(dp3);
    m_canvas.addObject(intersection);
    //System.out.println("intersection has the coords: "+intersection.getAffineCoordinates()[0]
    //                     +", "+intersection.getAffineCoordinates()[1]);
    // for orientation:
    MMDefaultR3 r3 = new MMDefaultR3(MDouble.class);
    m_canvas.addObject(r3);

    //p.addHandler(akth);
    //m_canvas.addObject(l1);
    m_canvas.addObject(p1);
    m_canvas.addObject(p2);
		addMMObjectAsContainerContent(p2);
    p2.setEditable(true);

    intersection.dependsOn(new MMObjectIF[] { p1, p2 }, new DependencyAdapter() {
      public void doUpdate() {
        //System.out.println(m_line1.getIntersection(m_line2));
        AffineSpace intersect = p1.intersected(p2);
        //System.out.println(intersection.getDimension());
        if (intersect.getDimension() < 1);
        //m_intersection.setFrom(new double[]{Double.NaN, Double.NaN, Double.NaN});
        else
          intersection.setAffineCoordinates(intersect.getAffineCoordinates());
        //System.out.println(m_intersection.getAffineCoordinates().length);
      }
    });

  }

  public static void main(String[] args) {
    java.util.logging.Logger.getLogger("").setLevel(
      java.util.logging.Level.SEVERE);

    Affine3DSubspaceTest myApplet = new Affine3DSubspaceTest();
    BasicApplicationFrame f = new BasicApplicationFrame(myApplet, 600);
    myApplet.init();
    myApplet.start();
    f.pack();
    f.setVisible(true);
  }
}
