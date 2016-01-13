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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import net.mumie.mathletfactory.action.handler.global.GlobalAffine2DPointCreateHandler;
import net.mumie.mathletfactory.action.updater.PolynomialDefByPointsUpdater;
import net.mumie.mathletfactory.appletskeleton.g2d.SingleG2DCanvasApplet;
import net.mumie.mathletfactory.display.LineDisplayProperties;
import net.mumie.mathletfactory.display.g2d.MMG2DCanvas;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.mmobject.algebra.poly.MMPolynomial;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DLineSegment;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPoint;
import net.mumie.mathletfactory.transformer.GeneralTransformer;
import net.mumie.mathletfactory.util.BasicApplicationFrame;

public class ObjectCreationTest extends SingleG2DCanvasApplet{

  private final MMAffine2DLineSegment m_xAxis = new MMAffine2DLineSegment(MDouble.class,-1000,0,1000,0);
  private final MMAffine2DLineSegment m_yAxis = new MMAffine2DLineSegment(MDouble.class,0,-1000,0,1000);
  
  
  public void init() {
    super.init();
    setTitle("ObjectCreationTest");
    new GlobalAffine2DPointCreateHandler(getCanvas2D());
    
    setAxis();
    getCanvas().addObject(m_xAxis);
    getCanvas().addObject(m_yAxis);
    
    addControl(new ActionButton("createPolynom"));
    addControl(new ClearButton("clearCanvas"));
  }
  
  public static void main(String[] args) {
    ObjectCreationTest myApplet = new ObjectCreationTest();
    BasicApplicationFrame f = new BasicApplicationFrame(myApplet,400);
    myApplet.init();
    myApplet.start();
    f.pack();
    f.setVisible(true);
  }
  
  
  
  private class ActionButton extends JButton implements ActionListener{
    
    public ActionButton(String aTitle) {
      super(aTitle);
      setBackground(new Color(204,114,114));
      addActionListener(this);
    }
    
    public void actionPerformed(ActionEvent e) {
      MMAffine2DPoint[] points = null;
      ArrayList allPoints = getCanvas().getObjectsOfType(MMAffine2DPoint.class);
      if(allPoints.size() == 0) {
        JOptionPane.showMessageDialog(ObjectCreationTest.this.getMyContentPane(),
                                      "Please, choose at least one point by pressing c on the keyboard \n and clicking with left mouse button in the canvas.",
                                      "selection error",
                                       JOptionPane.PLAIN_MESSAGE);
        return;
      }
      points = new MMAffine2DPoint[allPoints.size()];
      for(int i=0; i<allPoints.size(); i++)
        points[i] = (MMAffine2DPoint)allPoints.get(i);
      MMPolynomial p = new MMPolynomial(MDouble.class,points.length);
      p.setCanvasTransformer(GeneralTransformer.FUNCTION_OVER_RN_AFFINE_GRAPH_TRANSFORM,getCanvas().getScreenType());
      
      PolynomialDefByPointsUpdater up =
        new PolynomialDefByPointsUpdater(p,
                                       points,
                                       PolynomialDefByPointsUpdater.BASE);
      getCanvas().addObject(p);
      getCanvas().renderScene();
      getCanvas().repaint();
    }
  }
  
  public void initializeObjects() {}
  
  private class ClearButton extends JButton implements ActionListener {
    
    public ClearButton(String aTitle) {
      super(aTitle);
      addActionListener(this);
    }
    
    public void actionPerformed(ActionEvent e) {
      MMG2DCanvas c = (MMG2DCanvas)getCanvas();
      getCanvas().removeAllObjectsOfType(MMAffine2DPoint.class);
      getCanvas().removeAllObjectsOfType(MMPolynomial.class);
      getCanvas().renderScene();
      getCanvas().repaint();
    }
  }
  
  private void setAxis() {
    LineDisplayProperties lprop = new LineDisplayProperties();
    lprop.setObjectColor(Color.gray);
    lprop.setLineWidth(2);
    lprop.setBorderWidth(0);
    m_xAxis.setDisplayProperties(lprop);
    m_yAxis.setDisplayProperties(lprop);
  }
}

