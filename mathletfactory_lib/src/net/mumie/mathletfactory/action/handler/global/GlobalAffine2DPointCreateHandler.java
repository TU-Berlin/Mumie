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

package net.mumie.mathletfactory.action.handler.global;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import net.mumie.mathletfactory.action.MMEvent;
import net.mumie.mathletfactory.action.handler.Affine2DKeyboardTranslateHandler;
import net.mumie.mathletfactory.action.handler.Affine2DMouseTranslateHandler;
import net.mumie.mathletfactory.display.MM2DCanvas;
import net.mumie.mathletfactory.display.PointDisplayProperties;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPoint;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 * A handler that allows the creation of 2d points by clicking with the mouse
 * on the canvas while the 'c' key is pressed.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public class GlobalAffine2DPointCreateHandler extends Global2DHandler {
  
  
  protected MMEvent m_event;
  protected MMAffine2DPoint m_createdPoint;
  protected PointDisplayProperties m_pointProperties;
  
  private Affine2DMouseTranslateHandler m_mouseTranslateHandler = new Affine2DMouseTranslateHandler(getCanvas());
  private Affine2DKeyboardTranslateHandler m_keyBoardTranslateHandler = new Affine2DKeyboardTranslateHandler(getCanvas());
  
  /** Constructs the handler to work in the given canvas. */
  public GlobalAffine2DPointCreateHandler(MM2DCanvas aCanvas) {
    super(aCanvas);
    m_event = new MMEvent(MouseEvent.MOUSE_CLICKED,
                          MouseEvent.BUTTON1_MASK,
                          1,
                          KeyEvent.VK_C,
                          MMEvent.NO_MODIFIER_SET);
    m_pointProperties = new PointDisplayProperties();
    m_pointProperties.setObjectColor(Color.red);
    m_pointProperties.setBorderColor(Color.black);
    m_pointProperties.setBorderWidth(1);
    m_pointProperties.setPointRadius(3);
  }
  
  public boolean userDefinedDealsWith(MMEvent event) {
    return m_event.equals(event);
  }
  
  public boolean doAction(MMEvent event) {
    int xPos = event.getX();
    int yPos = event.getY();
    double[] worldCoords = new double[2];
    getCanvas2D().getScreen2World().applyTo(xPos,yPos,worldCoords);
    
    m_createdPoint = new MMAffine2DPoint(MDouble.class,worldCoords[0],worldCoords[1]);
    m_createdPoint.setCanvasTransformer(GeneralTransformer.AFFINE_DEFAULT_TRANSFORM,
                                  getCanvas().getScreenType());
    m_createdPoint.addHandler(m_mouseTranslateHandler);
    m_createdPoint.addHandler(m_keyBoardTranslateHandler);
    m_createdPoint.setDisplayProperties(m_pointProperties);
    getCanvas().addObject(m_createdPoint);
    pointCreated(m_createdPoint);
    return true;
  }
  
  protected void pointCreated(MMAffine2DPoint point){
    

  }
  
  public void finish() {
    getCanvas().renderScene();
    getCanvas().repaint();
  }
  
}


