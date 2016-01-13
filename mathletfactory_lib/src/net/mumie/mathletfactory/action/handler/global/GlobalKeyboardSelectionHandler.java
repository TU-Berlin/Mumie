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

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import net.mumie.mathletfactory.action.CanvasControllerIF;
import net.mumie.mathletfactory.action.MMEvent;
import net.mumie.mathletfactory.display.MMCanvas;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;

/**
 * This handler allows the selection of objects in the <code>MMCanvas</code> by using the
 * tabulator key or <shift>+tabulator.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public class GlobalKeyboardSelectionHandler extends GlobalHandler {
  
  private final MMEvent m_forwardEvent;
  private final MMEvent m_backwardEvent;
  private final MMEvent m_deselectEvent;
  
  
  /** Constructs the handler to work in the given canvas. */
  public GlobalKeyboardSelectionHandler(MMCanvas canvas) {
    super(canvas);
    m_forwardEvent = new MMEvent(KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_TAB, 0);
    m_backwardEvent = new MMEvent(KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_TAB, InputEvent.SHIFT_MASK);
    m_deselectEvent = new MMEvent(KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_ESCAPE, 0);
  }
  
  public boolean doAction(MMEvent event) {
    CanvasControllerIF controller = getCanvas().getController();
    
    // Wenn ESC gedrueckt wurde...
    if (event.equals(m_deselectEvent)) {
      controller.setSelectedObject(null);
      getCanvas().repaint();
      
     // ESC wurde NICHT gedrueckt !
    } else {
      MMCanvasObjectIF oldSelected = controller.getSelectedObject();
      MMCanvasObjectIF newSelected = null;
      int position = 0;
			
			if (event.equals(m_forwardEvent)) {
				if (oldSelected != null) 
					position = getCanvas().getObjectIndex(oldSelected);
					
				position = nextSelectableIndex(position);
				newSelected = getCanvas().getObject(position);
				if (newSelected == null) 
						 m_canvas.getDrawingBoard().transferFocus();
			}
			else if(event.equals(m_backwardEvent)) {
				if (oldSelected == null) 
					position = getCanvas().getObjectCount();
				else
					position = getCanvas().getObjectIndex(oldSelected);
					
				position = previousSelectableIndex(position);
				newSelected = getCanvas().getObject(position);
				if (newSelected == null) 
						 m_canvas.getDrawingBoard().transferFocusBackward();
    }			
			controller.setSelectedObject(newSelected);
			m_canvas.repaint();
    }
		return false;
  }
  
  private int nextSelectableIndex(int index) {
    for(int i=index+1;  i<getCanvas().getObjectCount(); i++) {
      if ( getCanvas().getObject(i).isSelectable() )
        return i;
    }
    return -1;// output if no object is selectable
  }
  
  private int previousSelectableIndex(int index) {
    for(int i=index-1;  i>=0; i--) {
      if ( getCanvas().getObject(i).isSelectable() )
        return i;
    }
    return -1;// output if no object is selectable
  }
  /** Returns true for tab or <shift>+tab key events. */
  public boolean userDefinedDealsWith(MMEvent event) {
    return event.equals(m_forwardEvent) ||
    event.equals(m_backwardEvent) ||
    event.equals(m_deselectEvent);
  }
  
  public void finish() {
  }
  
}
