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

import java.awt.Cursor;
import java.awt.event.MouseEvent;

import net.mumie.mathletfactory.action.MMEvent;
import net.mumie.mathletfactory.display.MMCanvas;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;

/**
 * This handler changes the shape of the mouse pointer if it is pointing at
 * an object that can be selected.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public class MouseShapeHandler extends GlobalHandler {
  
  
  private final Cursor m_handCursor = new Cursor(Cursor.HAND_CURSOR);
  private final Cursor m_defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
  
  /** Constructs the handler to work in the given canvas. */
  public  MouseShapeHandler (MMCanvas aCanvas)  {
    super(aCanvas);
  }
  
  public boolean doAction(MMEvent anMMEvent) {
    if ( isAnyMMObjectAtScreenLocation(anMMEvent.getX(),anMMEvent.getY())){
      m_canvas.setCursor(m_handCursor);
    }
    else {
      m_canvas.setCursor(m_defaultCursor);
    }
    return false;
  }
  
  private boolean isAnyMMObjectAtScreenLocation(int x, int y) {
    MMCanvasObjectIF obj;
    for(int i=0; i<m_canvas.getObjectCount(); i++) {
      obj = m_canvas.getObject(i);
      if (obj.isAtScreenLocation(x,y) && obj.isSelectable())
        return true;
    }
    return false;
  }
  
  /** Returns true for mouse move events. */
  public boolean userDefinedDealsWith(MMEvent event) {
    if(event.getEventType() == MouseEvent.MOUSE_MOVED)
      return true;
    else
      return false;
  }
  
  /** Empty implementation. */
  public void finish() {
  }
  
}

