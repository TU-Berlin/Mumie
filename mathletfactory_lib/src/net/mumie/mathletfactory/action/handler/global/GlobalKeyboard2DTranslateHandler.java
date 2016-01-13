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
 * This handler allows translation of the viewport (the 'window to the world') of the canvas with the
 * keyboard.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public class GlobalKeyboard2DTranslateHandler extends Global2DHandler {
  
  
  /** Constructs the handler to work in the given canvas. */
  public GlobalKeyboard2DTranslateHandler(MM2DCanvas canvas) {
    super(canvas);
  }
  
  /** Adds a snapshot to the history of the canvas. */
  public void finish() {
    getCanvas2D().getWorld2Screen().addSnapshotToHistory();
    m_canvas.renderFromWorldDraw();
  }
  
  public boolean doAction(MMEvent event) {
    if (event.getKeyCode() == KeyEvent.VK_LEFT) {
      getCanvas2D().getWorld2Screen().leftTranslateX(-1.0);
      getCanvas2D().getWorld2Screen().rightTranslateX(1.0);
    }
    else if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
      getCanvas2D().getWorld2Screen().leftTranslateX(1.0);
      getCanvas2D().getWorld2Screen().rightTranslateX(-1.0);
    }
    else if (event.getKeyCode() == KeyEvent.VK_UP) {
      getCanvas2D().getWorld2Screen().leftTranslateY(-1.0);
      getCanvas2D().getWorld2Screen().rightTranslateY(1.0);
    }
    else if (event.getKeyCode() == KeyEvent.VK_DOWN) {
      getCanvas2D().getWorld2Screen().leftTranslateY(1.0);
      getCanvas2D().getWorld2Screen().rightTranslateY(-1.0);
    }
    
    getCanvas2D().renderFromWorldDraw();
    getCanvas2D().repaint();
    getCanvas2D().adjustTransformations();
    return true;
  }
  
  /** Returns true for <ctrl> + cursor key events. */
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
  
}

