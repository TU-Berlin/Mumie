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

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;

import net.mumie.mathletfactory.action.handler.Affine2DKeyboardTranslateHandler;
import net.mumie.mathletfactory.action.handler.Affine2DMouseTranslateHandler;
import net.mumie.mathletfactory.display.LineDisplayProperties;
import net.mumie.mathletfactory.display.PointDisplayProperties;
import net.mumie.mathletfactory.display.PolygonDisplayProperties;
import net.mumie.mathletfactory.display.g2d.MMG2DCanvas;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DLineSegment;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPoint;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPolygon;
import net.mumie.mathletfactory.mmobject.geom.affine.MMCoordinateSystem;
import net.mumie.mathletfactory.util.BasicApplicationFrame;

/**
 * @author lars
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class AffinePolygonTest extends JPanel {

  
  
  public AffinePolygonTest(){
    
    setLayout(new BorderLayout());
    
    MMG2DCanvas m_canvas = new MMG2DCanvas();
    add(m_canvas,BorderLayout.CENTER);

    m_canvas.addObject(new MMCoordinateSystem());
    
    MMAffine2DPolygon p;
    Affine2DMouseTranslateHandler amth = new Affine2DMouseTranslateHandler(m_canvas);
    Affine2DKeyboardTranslateHandler akth = new Affine2DKeyboardTranslateHandler(m_canvas);
    MMAffine2DPoint p1,p2,p3,p4,p5;
    p1 = new MMAffine2DPoint(MDouble.class, 0, 0);
    p2 = new MMAffine2DPoint(MDouble.class, 0.3,0.3);
    p3 = new MMAffine2DPoint(MDouble.class, 0.5, 0.4);
    p4 = new MMAffine2DPoint(MDouble.class, 0.7, 0.0);
    p5 = new MMAffine2DPoint(MDouble.class, 0.9, 0.2);
//    ((PointDisplayProperties)p5.getDisplayProperties()).setPointRadius(15);
    ((PointDisplayProperties)p5.getDisplayProperties()).setObjectColor(Color.RED);
    
    MMAffine2DPoint[] points = new MMAffine2DPoint[]{p1,p2,p3,p4,p5};
        

    
    m_canvas.addObject(p1);
    m_canvas.addObject(p2);
    m_canvas.addObject(p3);
    m_canvas.addObject(p4);
    m_canvas.addObject(p5);
    MMAffine2DLineSegment l = new MMAffine2DLineSegment(p1, new MMAffine2DPoint(MDouble.class, 0, 1));
    l.setDisplayProperties(LineDisplayProperties.VECTOR_DEFAULT);
    ((LineDisplayProperties)l.getDisplayProperties()).setLineWidth(5);
    l.getDisplayProperties().setObjectColor(Color.RED);
    //m_canvas.addObject(l);
    
    MMAffine2DPolygon pol = new MMAffine2DPolygon(points);
    pol.setLabel("a");
    pol.addHandler(akth);
    pol.addHandler(amth);
    PolygonDisplayProperties prop = (PolygonDisplayProperties)pol.getDisplayProperties();
    prop.setFilled(false);
    prop.setLineWidth(10);
    prop.setBorderWidth(1);
    m_canvas.addObject(pol);
  }
  
  public static void main(String[] args){
    java.util.logging.Logger.getLogger("").setLevel(java.util.logging.Level.WARNING);
    AffinePolygonTest myPanel = new AffinePolygonTest();
    BasicApplicationFrame f = new BasicApplicationFrame(myPanel);
    f.pack();
    f.setVisible(true);
  }
}
