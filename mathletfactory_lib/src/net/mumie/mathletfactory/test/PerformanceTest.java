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


import java.awt.Color;
import java.util.logging.Logger;

import javax.swing.JPanel;

import net.mumie.mathletfactory.action.handler.Vector2DKeyboardGridTranslateHandler;
import net.mumie.mathletfactory.action.handler.Vector2DMouseGridTranslateHandler;
import net.mumie.mathletfactory.display.LineDisplayProperties;
import net.mumie.mathletfactory.display.g2d.MMG2DCanvas;
import net.mumie.mathletfactory.display.layout.AnchorGridLayout;
import net.mumie.mathletfactory.display.layout.SimpleGridConstraints;
import net.mumie.mathletfactory.display.layout.SimpleGridLayout;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.number.MRational;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR2;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR2Vector;
import net.mumie.mathletfactory.util.BasicApplicationFrame;

public class PerformanceTest extends JPanel {
  
  
  public PerformanceTest(){
    
    SimpleGridLayout sgl = new AnchorGridLayout(2,2,this);
    //sgl.fixRow(2);
    setLayout(sgl);
    
    MMG2DCanvas m_domainCanvas = new MMG2DCanvas();
    add(m_domainCanvas, new SimpleGridConstraints(1,1));
    

    LineDisplayProperties lineProp1 = new LineDisplayProperties();
    lineProp1.setObjectColor(Color.blue);
    lineProp1.setArrowAtEnd(LineDisplayProperties.COMPLEX_ARROW_END);
    
    Vector2DKeyboardGridTranslateHandler gkh = new Vector2DKeyboardGridTranslateHandler(m_domainCanvas);
    Vector2DMouseGridTranslateHandler gmh = new Vector2DMouseGridTranslateHandler(m_domainCanvas);
    gmh.setDrawDuringAction(true);
    // create an instance of our Default R^2 (that has immutable default basis):
    MMDefaultR2 domain = new MMDefaultR2(MRational.class);
    domain.setGridInMath(0.1);
    
 
    // create the elements of the domain:
    NumberTuple coords = new NumberTuple(MRational.class,new Object[]{new MRational(0.5),new MRational(0.0)});
    MMDefaultR2Vector v1 = (MMDefaultR2Vector)domain.getNewFromCoordinates(coords);
    v1.addHandler(gkh);
    v1.addHandler(gmh);
    v1.setDisplayProperties(lineProp1);
    
    int numOfObjects = 100;
    for (int i=1; i<=numOfObjects; i++) {
      NumberTuple c = new NumberTuple(MRational.class,new Object[]{new MRational(0),new MRational(0.5)});
      MMDefaultR2Vector v = (MMDefaultR2Vector)domain.getNewFromCoordinates(c);
      m_domainCanvas.addObject(v.getAsCanvasContent());
    }

    m_domainCanvas.addObject(v1.getAsCanvasContent());
    m_domainCanvas.addObject(domain.getAsCanvasContent());
  }

  public static void main(String[] args){
    Logger.getLogger("").setLevel(java.util.logging.Level.FINE);
    PerformanceTest myPanel = new PerformanceTest();
    BasicApplicationFrame f = new BasicApplicationFrame(myPanel);
    f.pack();
    f.setVisible(true);
  }
  
}




