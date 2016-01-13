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

import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import net.mumie.mathletfactory.action.MMEvent;
import net.mumie.mathletfactory.display.MMCanvas;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;

/**
 * This handler is responsible for the selection of objects in the canvas. 
 * @author Paehler
 * @mm.docstatus finished
 */
public class GlobalMouseSelectionHandler extends GlobalHandler {
  
  private final MMEvent m_event;

  /** Constructs the handler to work in the given canvas. */
  public GlobalMouseSelectionHandler(MMCanvas canvas) {
    super(canvas);
    m_event = new MMEvent(MouseEvent.MOUSE_PRESSED, InputEvent.BUTTON1_MASK, 1,
        MMEvent.NO_KEY, MMEvent.NO_MODIFIER_SET);
  }

  /**
   * This method simply adds the objects, which have the same mouse coordinates
   * as recorded by the event to the list of preselected objects in the
   * {@link net.mumie.mathletfactory.action.CanvasControllerIF Controller}.
   * @see net.mumie.mathletfactory.action.handler.GlobalHandlerInterface#doAction(MMEvent)
   * it always returns false, because the preselected objects themselves should
   * be iterated later by the Controller with calls to
   * {@link net.mumie.mathletfactory.mmobject.MMInteractivityIF#doAction}
   * (as for example done in {@link net.mumie.mathletfactory.action.DefaultCanvasController#controlAction}).
   */
  public boolean doAction(MMEvent event) {
    boolean visibleObjectFound = false;
    for (int i = 0; i < m_canvas.getObjectCount(); i++) {
      MMCanvasObjectIF obj = m_canvas.getObject(i);
      if ( obj.isAtScreenLocation(event.getX(), event.getY())) {
        visibleObjectFound = obj.isVisible();
        if (obj.isSelectable()) {
          m_canvas.getController().addPreSelectedObject(obj);
        }
      }
    }
    if (visibleObjectFound && (m_canvas.getController().getPreselectedObjectCount() == 0)) {
      //JOptionPane.showMessageDialog(m_canvas, "Dieses Objekt kann nicht ausgwaehlt werden.");
      Toolkit.getDefaultToolkit().beep();
    }
    
    if (m_canvas.getController().getPreselectedObjectCount() == 0) {
      m_canvas.getController().setSelectedObject(null);
      m_canvas.repaint();
    }
    return false;//if returning true, no drag events will be performed for the object
  }

  public boolean userDefinedDealsWith(MMEvent event) {
    return m_event.equals(event);
  }
  
  /** Emtpy implementation. */
  public void finish() {
  }

}
