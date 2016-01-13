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
 * This handler allows translation of the viewport (the 'window to the world') of the canvas with the
 * mouse.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public class GlobalMouse2DTranslateHandler extends Global2DHandler {
  
  
  private boolean m_firstValuesUnset = true;
  private boolean m_drawWhileTranslate = true;
  
  private int m_xPixOld;
  private int m_yPixOld;
  private int m_xPixNew;
  private int m_yPixNew;
  
  private double m_trans[] = new double[2];
  
  /** Constructs the handler to work in the given canvas. */
  public GlobalMouse2DTranslateHandler(MM2DCanvas aCanvas) {
    super(aCanvas);
  }
  
  /** Adds a snapshot to the history of the canvas. */
  public void finish() {
    getCanvas2D().getWorld2Screen().addSnapshotToHistory();
    m_canvas.renderFromWorldDraw();
    m_firstValuesUnset = true;
  }
  
  public boolean doAction(MMEvent event) {
    if( m_firstValuesUnset ) {
      m_xPixOld = getCanvas().getController().getLastPressedX();
      m_yPixOld = getCanvas().getController().getLastPressedY();
      m_firstValuesUnset = false;
    }
    m_xPixNew = event.getX();
    m_yPixNew = event.getY();
    
    m_trans[0] = m_xPixNew - m_xPixOld;
    m_trans[1] = m_yPixNew - m_yPixOld;
    getCanvas2D().getWorld2Screen().leftTranslate(m_trans);
    // make "fast" adjustment of the inverse transformation:
    m_trans[0] *= -1;
    m_trans[1] *= -1;
    getCanvas2D().getScreen2World().rightTranslate(m_trans);
    if( m_drawWhileTranslate ) {
      m_canvas.renderFromWorldDraw();
      m_canvas.repaint();
    }
    
    m_xPixOld = m_xPixNew;
    m_yPixOld = m_yPixNew;
    return true;
  }
  
  public boolean userDefinedDealsWith(MMEvent anEvent) {
    boolean k1 = anEvent.getKeyCode() == KeyEvent.VK_T;
    boolean k2 = anEvent.getModifier() == MMEvent.NO_MODIFIER_SET;
    boolean k3 = anEvent.getKeyCode() == MMEvent.NO_KEY;

    boolean keyTest = (k1 && k2) || (k3 && k2);
    
    if(  anEvent.getEventType() == MouseEvent.MOUSE_DRAGGED &&
         anEvent.getMouseButton() == MouseEvent.BUTTON1_MASK &&
         anEvent.getClickCount() == 0 &&
         keyTest)
      return true;
    return false;
  }
  
}

