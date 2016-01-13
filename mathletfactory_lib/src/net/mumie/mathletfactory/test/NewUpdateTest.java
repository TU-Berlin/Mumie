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

package net.mumie.mathletfactory.test;


import java.awt.BorderLayout;
import java.awt.Point;

import javax.swing.JPanel;

import net.mumie.mathletfactory.action.handler.Affine2DKeyboardTranslateHandler;
import net.mumie.mathletfactory.action.handler.Affine2DMouseTranslateHandler;
import net.mumie.mathletfactory.action.updater.DependencyAdapter;
import net.mumie.mathletfactory.display.PointDisplayProperties;
import net.mumie.mathletfactory.display.g2d.MMG2DCanvas;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DLineSegment;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPoint;
import net.mumie.mathletfactory.util.BasicApplicationFrame;

public class NewUpdateTest extends JPanel {
  
  
  public NewUpdateTest(){
    
    setLayout(new BorderLayout());
    
    MMG2DCanvas m_canvas = new MMG2DCanvas();
    add(m_canvas,BorderLayout.CENTER);
    
    
    
    final MMAffine2DPoint p,q;
    MMAffine2DLineSegment l;
    PointDisplayProperties pp;
    Affine2DMouseTranslateHandler amth = new Affine2DMouseTranslateHandler(m_canvas);
    amth.setDrawDuringAction(true);
    amth.setUpdateDuringAction(true);

    Affine2DKeyboardTranslateHandler akth = new Affine2DKeyboardTranslateHandler(m_canvas);
    
    p = new MMAffine2DPoint(MDouble.class,0.5,0.5);
    q = new MMAffine2DPoint(MDouble.class,0.3,0.3);
    pp = new PointDisplayProperties();
    pp.setShadowOffset(new Point(-5, -5));
    p.setDisplayProperties(pp);
    p.addHandler(amth);
    
    
    q.dependsOn(p,
        new DependencyAdapter() {
          
          public void doUpdate(MMObjectIF p1, MMObjectIF p2) {
            ((MMAffine2DPoint)p1).setXY(0.3,Math.sin(((MMAffine2DPoint)p2).getYAsDouble()));
          }
        });
    
    m_canvas.addObject(p.getAsCanvasContent());
    m_canvas.addObject(q.getAsCanvasContent());
    
  }
  
  public static void main(String[] args){
    java.util.logging.Logger.getLogger("").setLevel(java.util.logging.Level.FINE);
    NewUpdateTest myPanel = new NewUpdateTest();
    BasicApplicationFrame f = new BasicApplicationFrame(myPanel);
    f.pack();
    f.setVisible(true);
  }
  
}




