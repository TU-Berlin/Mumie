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

/**
 * This handler allows scaling of the viewport (the 'window to the world') of the canvas with the
 * keyboard.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public class GlobalKeyboard2DScaleHandler extends Global2DHandler {
  
  
  private double m_scale = 1.5;

  /** Constructs the handler to work in the given canvas. */
  public GlobalKeyboard2DScaleHandler(MM2DCanvas aCanvas) {
    super(aCanvas);
  }
  
  /** Returns true for <ctrl> + cursor key events. */
  public boolean userDefinedDealsWith(MMEvent anEvent) {
    if( (anEvent.getEventType() == KeyEvent.KEY_PRESSED) &&
        (anEvent.getModifier() == KeyEvent.CTRL_MASK) )
      if ((anEvent.getKeyCode() == KeyEvent.VK_LEFT ) ||
          (anEvent.getKeyCode() == KeyEvent.VK_RIGHT) ||
          (anEvent.getKeyCode() == KeyEvent.VK_UP) ||
          (anEvent.getKeyCode() == KeyEvent.VK_DOWN))
        return true;
    
    return false;
  }
  
  public boolean doAction(MMEvent event) {
    switch (event.getKeyCode()) { 
      case KeyEvent.VK_LEFT:
        getCanvas2D().getWorld2Screen().rightScale(1./m_scale, 1);
        break;
      case KeyEvent.VK_RIGHT:
        getCanvas2D().getWorld2Screen().rightScale(m_scale, 1);
        break;
      case KeyEvent.VK_DOWN:
        getCanvas2D().getWorld2Screen().rightScale(1, 1./m_scale);
        break;
      case KeyEvent.VK_UP:
        getCanvas2D().getWorld2Screen().rightScale(1, m_scale);
        break;
    }
    getCanvas2D().adjustTransformations();
    getCanvas2D().renderFromWorldDraw();
    getCanvas2D().repaint();
    return true;
  }
  
  /** Adds a snapshot to the history of the canvas. */
  public void finish() {
    getCanvas2D().getWorld2Screen().addSnapshotToHistory();
    m_canvas.renderFromWorldDraw();
  }
  
}

