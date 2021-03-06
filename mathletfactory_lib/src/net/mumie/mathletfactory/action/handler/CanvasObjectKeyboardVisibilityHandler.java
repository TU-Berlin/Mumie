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

package net.mumie.mathletfactory.action.handler;

import java.awt.event.KeyEvent;

import javax.swing.JComponent;

import net.mumie.mathletfactory.action.MMEvent;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;

/**
 * Toggles the visibility of an mmobject, if the user presses the <code>V</code> key.
 * @author Paehler
 * @mm.docstatus finished
 */
public class CanvasObjectKeyboardVisibilityHandler extends MMHandler {  
  
  public CanvasObjectKeyboardVisibilityHandler(JComponent display){
    super(display);
  }
  
  public boolean userDefinedDealsWith(MMEvent event) {
    if ((event.getEventType() == KeyEvent.KEY_PRESSED) &&
         (event.getModifier() == 0)) {
      if( (event.getKeyCode() == KeyEvent.VK_V))
        return true;
    }
    return false;
  }
  
  public void userDefinedAction(MMEvent event) {
    MMCanvasObjectIF obj = (MMCanvasObjectIF)getMaster();
    if(obj.isVisible())
      obj.setVisible(false);
    else
      obj.setVisible(true);
  }
  
  public void userDefinedFinish() {
    
  }
}

