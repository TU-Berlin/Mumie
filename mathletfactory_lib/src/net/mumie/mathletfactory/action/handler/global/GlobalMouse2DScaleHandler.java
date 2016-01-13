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
import java.awt.event.MouseEvent;

import net.mumie.mathletfactory.action.MMEvent;
import net.mumie.mathletfactory.display.MM2DCanvas;

/**
 * This handler allows scaling of the viewport (the 'window to the world') of the canvas with the
 * mouse.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public class GlobalMouse2DScaleHandler extends Global2DHandler {
  
  private boolean m_firstValuesUnset = true;
  private boolean m_drawWhileTranslate = true;
  
  private int m_xPixOld;
  private int m_xPixNew;
  
  /** Constructs the handler to work in the given canvas. */
  public GlobalMouse2DScaleHandler(MM2DCanvas aCanvas) {
    super(aCanvas);
  }
  
  /** Adds a snapshot to the history of the canvas. */
  public void finish() {
    getCanvas2D().getWorld2Screen().addSnapshotToHistory();
    m_canvas.renderFromWorldDraw();
    m_firstValuesUnset = true;
  }
  
  protected double calcScale(double size, double delta) {
    if ( delta > 0)
      return (size + delta)/size;
    else if (delta == 0 )
      return 1;
    else
      return size/(size+Math.abs(delta));
  }

  public boolean doAction(MMEvent event) {
     if( m_firstValuesUnset ) {
      m_xPixOld = getCanvas().getController().getLastPressedX();
      m_firstValuesUnset = false;
    }
    m_xPixNew = event.getX();
    double canvasWidth = getCanvas().getDrawingBoard().getWidth();
    double deltaX = m_xPixNew - m_xPixOld;
    double scaleX = calcScale(canvasWidth, deltaX);
    
    int keyCode = event.getKeyCode();
    if(event.getModifier() == MouseEvent.CTRL_MASK)
      keyCode = KeyEvent.VK_S; 
    switch (keyCode) {
      case KeyEvent.VK_S:
        getCanvas2D().getWorld2Screen().rightScale(scaleX);
        break;
      case KeyEvent.VK_X:
        getCanvas2D().getWorld2Screen().rightScale(scaleX, 1);
        break;
      case KeyEvent.VK_Y:
        getCanvas2D().getWorld2Screen().rightScale(1, scaleX);
        break;
    }
    getCanvas2D().adjustTransformations();
    if( m_drawWhileTranslate ) {
      m_canvas.renderFromWorldDraw();
      m_canvas.repaint();
    }
    
    m_xPixOld = m_xPixNew;
    return true;
  }
  
  public boolean userDefinedDealsWith(MMEvent anEvent) {
    boolean keyTest = ((
        (anEvent.getKeyCode() == KeyEvent.VK_S) || 
        (anEvent.getKeyCode() == KeyEvent.VK_X) || 
        (anEvent.getKeyCode() == KeyEvent.VK_Y)) && anEvent.getModifier() == MMEvent.NO_MODIFIER_SET)
    ||
        (anEvent.getKeyCode() == MMEvent.NO_KEY && anEvent.getModifier() == MouseEvent.CTRL_MASK);
    return  keyTest && anEvent.getEventType() == MouseEvent.MOUSE_DRAGGED &&
         anEvent.getMouseButton() == MouseEvent.BUTTON1_MASK &&
         anEvent.getClickCount() == 0 ;
  }
  
}

