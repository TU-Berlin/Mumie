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

import java.awt.event.KeyEvent;

import net.mumie.mathletfactory.action.MMEvent;
import net.mumie.mathletfactory.display.MM3DCanvas;
import net.mumie.mathletfactory.math.util.Affine3DDouble;

/**
 * This handler allows the user to "orbit" the viewer around the origin of
 * the world coordinate system (thus virtually rotating it) by pressing the 
 * the arrow keys.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public class GlobalKeyboard3DRotateXYHandler extends Global3DHandler {
  
  private double m_angleDegree = 5;
  
  private double m_angleRad = m_angleDegree*Math.PI/180;
  
  private Affine3DDouble m_rotate = new Affine3DDouble(), m_result = new Affine3DDouble();

  private double[] m_axis = new double[3];
  
  /** Constructs the handler to work in the given canvas. */
  public GlobalKeyboard3DRotateXYHandler(MM3DCanvas canvas) {
    super(canvas);
  }
  
  /** Adds a snapshot to the history of the canvas. */
  public void finish() {
    super.finish();
    getCanvas3D().getWorld2WorldView().addSnapshotToHistory();
  }
  
  /**
   * Rotates the scene the set angle about the world transformed view x and y coordinates.    
   * @see net.mumie.mathletfactory.action.handler.global.MMGlobalHandler#doAction(MMEvent)
   */  
  public boolean doAction(MMEvent event) {
    if (event.getKeyCode() == KeyEvent.VK_LEFT) {
      getCanvas3D().getWorld2WorldView().applyDeformationTo(0, 1, 0, m_axis);
    }
    else if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
      getCanvas3D().getWorld2WorldView().applyDeformationTo(0, -1, 0, m_axis);
    }
    else if (event.getKeyCode() == KeyEvent.VK_UP) {
      getCanvas3D().getWorld2WorldView().applyDeformationTo(1, 0, 0, m_axis);
    }
    else if (event.getKeyCode() == KeyEvent.VK_DOWN) {
      getCanvas3D().getWorld2WorldView().applyDeformationTo(-1, 0, 0, m_axis);
    }
    
    m_rotate.setToRotation(m_axis, m_angleRad);
    
    getCanvas3D().adjustTransformations();
    m_result = new Affine3DDouble();
    Affine3DDouble.compose(m_result, m_rotate, ((MM3DCanvas)getCanvas()).getWorld2WorldView());
    //System.out.println("rot after adjust:\n"+m_rotate+"\n");
    //System.out.println("invrot after adjust:\n"+((MM3DCanvas)getCanvas()).getWorld2WorldView()+"\n");
    //System.out.println("product:\n"+m_result+"\n");
    getCanvas3D().setWorld2WorldView(m_result);
    getCanvas3D().renderFromWorldDraw();
    getCanvas3D().repaint();
    return true;
  }
  
  public boolean userDefinedDealsWith(MMEvent event) {
    if ((event.getEventType() == KeyEvent.KEY_PRESSED) &&
         (event.getModifier() == 0)) {
      if ((event.getKeyCode() == KeyEvent.VK_LEFT) ||
           (event.getKeyCode() == KeyEvent.VK_RIGHT) ||
           (event.getKeyCode() == KeyEvent.VK_UP) ||
           (event.getKeyCode() == KeyEvent.VK_DOWN)) {
        return true;
      }
    }
    return false;
  }
  

  /**
   * Sets the angle the scene is rotated for every key press.
   */
  public GlobalKeyboard3DRotateXYHandler setAngleInDegree(double anAngle) {
    m_angleDegree = anAngle;
    adjustRadAngle();
    return this;
  }
    
  private void adjustRadAngle() {
    m_angleRad = m_angleDegree*Math.PI/180;
  }
}

