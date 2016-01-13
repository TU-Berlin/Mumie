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
import net.mumie.mathletfactory.display.MM2DCanvas;
import net.mumie.mathletfactory.math.util.Affine2DDouble;

/**
 * This handler allows rotating the viewport (the 'window to the world') of the canvas with the
 * keyboard.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public class GlobalKeyboard2DRotateHandler extends Global2DHandler {
  
  
  private double m_angleDegree = 45;
  private double m_angleRad = m_angleDegree*Math.PI/180;
  
  /** Constructs the handler to work in the given canvas. */
  public GlobalKeyboard2DRotateHandler(MM2DCanvas canvas) {
    super(canvas);
  }
  
  public boolean doAction(MMEvent event) {
    Affine2DDouble m_rotate = ((MM2DCanvas)getCanvas()).getWorld2Screen();
    
    if (event.getKeyCode() == KeyEvent.VK_PAGE_UP) {
      m_rotate.leftRotate(m_angleRad);
    }
    if (event.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
      m_rotate.leftRotate(-m_angleRad);
    }
    getCanvas2D().adjustTransformations();
    
    Affine2DDouble mult = new Affine2DDouble();
		Affine2DDouble.compose(mult,m_rotate,((MM2DCanvas)getCanvas()).getScreen2World());
		System.out.println("rot after adjust:\n"+m_rotate+"\n");
		System.out.println("invrot after adjust:\n"+((MM2DCanvas)getCanvas()).getScreen2World()+"\n");
		System.out.println("product:\n"+mult+"\n");
    getCanvas2D().renderFromWorldDraw();
    getCanvas2D().repaint();
    return true;
  }
  
  /** Adds a snapshot to the history of the canvas. */
  public void finish() {
    getCanvas2D().getWorld2Screen().addSnapshotToHistory();
    m_canvas.renderFromWorldDraw();
  }
  
  public boolean userDefinedDealsWith(MMEvent event) {
    if (event.getEventType() == KeyEvent.KEY_PRESSED && event.getModifier() == 0 ) {
      if ((event.getKeyCode() == KeyEvent.VK_PAGE_UP) ||
           (event.getKeyCode() == KeyEvent.VK_PAGE_DOWN)) {
        return true;
      }
    }
    return false;
  }
  
  /** Sets the angle in degree, that each 'tick' rotates the canvas. */ 
  public GlobalKeyboard2DRotateHandler setAngleInDegree(double anAngle) {
    adjustRadAngle();
    m_angleDegree = anAngle;
    return this;
  }

  /** Returns the angle in degree, that each 'tick' rotates the canvas. */ 
  public double getAngleInDegree() {
    return m_angleDegree;
  }
    
  private void adjustRadAngle() {
    m_angleRad = m_angleDegree*Math.PI/180;
  }
  
}

