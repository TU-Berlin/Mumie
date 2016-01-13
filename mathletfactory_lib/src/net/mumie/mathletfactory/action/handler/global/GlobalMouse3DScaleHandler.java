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
import java.awt.event.MouseEvent;

import net.mumie.mathletfactory.action.MMEvent;
import net.mumie.mathletfactory.display.MM3DCanvas;
import net.mumie.mathletfactory.math.util.Affine3DDouble;

/**
 * This handler allows the user to virtually scale the scene by moving closer or 
 * farther away from it by dragging the dragging the mouse with the middle
 * button pressed.
 *  
 * @author Paehler
 * @mm.docstatus finished
 */

public class GlobalMouse3DScaleHandler extends Global3DHandler {
  
  private boolean m_drawWhileDragging = true;
  private boolean m_firstValuesUnset = true;
  
  private final MMEvent[] m_events;
  
  private int m_xPixOld;
//  private int m_yPixOld;
  private int m_xPixNew;
//  private int m_yPixNew;
  
  private double    m_canvasWidth;
  private int       m_xMouseMoveInPixel;
  private double    m_adjustScale = 1.1;
  private double[]  m_direction = new double[3];
  private double[]  m_transformedDirection = new double[3];
  
  /** Constructs the handler to work in the given canvas. */
  public GlobalMouse3DScaleHandler(MM3DCanvas canvas){
    super(canvas);
    m_events = new MMEvent[3];
    m_events[0] = new MMEvent(MouseEvent.MOUSE_DRAGGED,
        MouseEvent.BUTTON2_MASK,
        0,
        MMEvent.NO_KEY,
        InputEvent.ALT_MASK); // on my mouse, this works without alt being pressed!
    m_events[1] = new MMEvent(MouseEvent.MOUSE_DRAGGED,
        MouseEvent.BUTTON1_MASK,
        0,
        KeyEvent.VK_S,
        MMEvent.NO_MODIFIER_SET);
    m_events[2] = new MMEvent(MouseEvent.MOUSE_DRAGGED,
        MouseEvent.BUTTON1_MASK,
        0,
        MMEvent.NO_KEY,
        InputEvent.CTRL_MASK);
  }
  
  
  /**
   * This will move the view platform in (viewers) z direction.
   * Right drag will enlarge, left drag will downsize the objects in canvas.
   * The distance of the movement is proportional to the distance the
   * mouse was dragged on the screen.
   */
  public boolean doAction(MMEvent event){
    //System.out.println("GlobalMouse3DScaleHandler::doAction called!");
    
    Affine3DDouble afg = getCanvas3D().getWorld2WorldView();
    
    if (m_firstValuesUnset){
      m_xPixOld = m_canvas.getController().getLastPressedX();
//      m_yPixOld = m_canvas.getController().getLastPressedY();
      m_firstValuesUnset = false;
    }
    m_xPixNew = event.getX();
//    m_yPixNew = event.getY();
    m_xMouseMoveInPixel = Math.abs(m_xPixNew-m_xPixOld);
    m_canvasWidth = m_canvas.getWidth();
    
    if(m_xPixNew >= m_xPixOld)
      m_direction[2] = 1 - (m_adjustScale*(m_canvasWidth + m_xMouseMoveInPixel)/m_canvasWidth);
    else
      m_direction[2] = -1 + (m_adjustScale*(m_canvasWidth + m_xMouseMoveInPixel)/m_canvasWidth);
    afg.applyDeformationPartTo(m_direction, m_transformedDirection);
    afg.leftTranslate(m_transformedDirection);
    getCanvas3D().setWorld2WorldView(afg);
    
    // draw / do-not-draw the objects while dragging:
    if(m_drawWhileDragging) {
      m_canvas.renderFromWorldDraw();
      m_canvas.repaint();
    }
    
    m_xPixOld = m_xPixNew;
//    m_yPixOld = m_yPixNew;
    return true;
  }
  
  /** Adds a snapshot to the history of the canvas. */
  public void finish(){
    super.finish();
    getCanvas3D().getWorld2WorldView().addSnapshotToHistory();
    m_firstValuesUnset = true;
  }
  
   
  public boolean userDefinedDealsWith(MMEvent anEvent) {
    for(int i=0; i<m_events.length; i++)
      if(m_events[i].equals(anEvent))
        return true;
    return false;
  }
  
  
}

