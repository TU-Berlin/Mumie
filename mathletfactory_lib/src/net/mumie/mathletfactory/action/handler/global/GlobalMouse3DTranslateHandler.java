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
import net.mumie.mathletfactory.display.MM3DCanvas;
import net.mumie.mathletfactory.math.util.Affine3DDouble;

/**
 * This handler allows the user to translate himself in the world 
 * (thus virtually translating the scene in the other direction) by dragging
 * the mouse with the right button pressed. 
 * The direction in which he is translated is the direction the mouse is 
 * dragged on the view plane (the screen coordinates transformed into world 
 * coordinates) the distance to be translated is proportional to the 
 * distance dragged.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */

public class GlobalMouse3DTranslateHandler extends Global3DHandler {
  
  
  private final MMEvent[] m_events;
  
  private boolean m_firstValuesUnset = true;
  private boolean m_drawWhileTranslate = true;
  
  private int m_xPixOld;
  private int m_yPixOld;
  private int m_xPixNew;
  private int m_yPixNew;
  
  private double m_xMove;
  private double m_yMove;
  private double[] t = new double[3];
  private double[] deformed_t = new double[3];
  
  /** Constructs the handler to work in the given canvas. */
  public GlobalMouse3DTranslateHandler(MM3DCanvas aCanvas) {
    super(aCanvas);
    m_events = new MMEvent[2];
    m_events[0] = new MMEvent(MouseEvent.MOUSE_DRAGGED,
        MouseEvent.BUTTON3_MASK,
        0,
        MMEvent.NO_KEY,
        MMEvent.NO_MODIFIER_SET);
    m_events[1] = new MMEvent(MouseEvent.MOUSE_DRAGGED,
        MouseEvent.BUTTON1_MASK,
        0,
        KeyEvent.VK_T,
        MMEvent.NO_MODIFIER_SET);
      
  }
  
  /** Adds a snapshot to the history of the canvas. */
  public void finish() {
    super.finish();
    getCanvas3D().getWorld2WorldView().addSnapshotToHistory();
    m_firstValuesUnset = true;
  }

  /**
   * Calculates the distance dragged and uses it for translation. The 
   * (world draw coordinate) axis for the translation is the direction 
   * dragged in the view plane.  
   * @see net.mumie.mathletfactory.action.handler.global.MMGlobalHandler#doAction(MMEvent)
   */  
  public boolean doAction(MMEvent event) {
    //System.out.println("GlobalMouse3DTranslateHandler::doAction called!");
    //AffineGroupElement afg = ((MMJ3DCanvas)getCanvas()).getWorldViewTrafo();
    Affine3DDouble afg = getCanvas3D().getWorld2WorldView();
    if( m_firstValuesUnset ) {
      m_xPixOld = m_canvas.getController().getLastPressedX();
      m_yPixOld = m_canvas.getController().getLastPressedY();
      m_firstValuesUnset = false;
    }
    m_xPixNew = event.getX();
    m_yPixNew = event.getY();
    
    m_xMove = -(double)(m_xPixNew-m_xPixOld)/m_canvas.getWidth();
    m_yMove =  (double)(m_yPixNew-m_yPixOld)/m_canvas.getHeight();
    
    //NumberTuple t = new NumberTuple(MDouble.class,new Object[]{m_xMove,m_yMove, new MDouble()});
    t[0] = m_xMove;
    t[1] = m_yMove;
    
    afg.applyDeformationPartTo(t,deformed_t);
    afg.leftTranslate(deformed_t);
    getCanvas3D().setWorld2WorldView(afg);
    if( m_drawWhileTranslate ) {
      m_canvas.renderFromWorldDraw();
      m_canvas.repaint();
    }
    
    m_xPixOld = m_xPixNew;
    m_yPixOld = m_yPixNew;
    return true;
  }
  
  public boolean userDefinedDealsWith(MMEvent anEvent) {
    //System.out.println("incoming event keycode: "+anEvent.getKeyCode()
    //                     +"comparing with "+m_events[1].getKeyCode());
    for(int i=0; i<m_events.length; i++)
      if(m_events[i].equals(anEvent))
        return true;
    return false;
  }
}


