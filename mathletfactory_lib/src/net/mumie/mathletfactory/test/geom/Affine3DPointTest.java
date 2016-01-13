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

import net.mumie.mathletfactory.action.handler.Affine3DKeyboardTranslateHandler;
import net.mumie.mathletfactory.action.handler.Affine3DMouseTranslateHandler;
import net.mumie.mathletfactory.display.PointDisplayProperties;
import net.mumie.mathletfactory.display.j3d.MMJ3DCanvas;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine3DPoint;
import net.mumie.mathletfactory.util.BasicApplicationFrame;

/**
 *  Test of {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine3DPoint}.
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class Affine3DPointTest extends JPanel {


  public Affine3DPointTest(){

    setLayout(new BorderLayout());

    MMJ3DCanvas m_canvas = new MMJ3DCanvas();
    add(m_canvas, BorderLayout.CENTER);

    MMAffine3DPoint p;
    PointDisplayProperties pp;
    
    p = new MMAffine3DPoint(MDouble.class, 0.5, 0.5, 0);
    Affine3DMouseTranslateHandler mth = new Affine3DMouseTranslateHandler(m_canvas);
    p.addHandler(mth);
    Affine3DKeyboardTranslateHandler kth = new Affine3DKeyboardTranslateHandler(m_canvas);
    p.addHandler(kth);
    p.setLabel("p1");
    
    pp = new PointDisplayProperties();
    pp.setObjectColor(Color.BLUE);
    p.setDisplayProperties(pp);
    m_canvas.addObject(p.getAsCanvasContent());
    m_canvas.setToolbarVisible(false);
    m_canvas.setScrollButtonsVisible(false);
  }

  public static void main(String[] args){
    java.util.logging.Logger.getLogger("").setLevel(java.util.logging.Level.SEVERE);
    Affine3DPointTest myPanel = new Affine3DPointTest();
    BasicApplicationFrame f = new BasicApplicationFrame(myPanel);
    f.pack();
    f.setVisible(true);
  }

}




