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
import net.mumie.mathletfactory.display.MM3DCanvas;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.util.Affine3DDouble;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR3Vector;
import net.mumie.mathletfactory.transformer.Canvas3DObjectTransformer;

/**
 * This handler allows a user to translate the end point of a selected 
 * {@link net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR3Vector} 
 * by using the arrow keys.
 * This is done in a plane parallel to the view plane (the virtual world 
 * coordinates of the screen).
 * 
 * @author Paehler
 * @mm.docstatus finished
 */

public class DefaultR3VectorKeyboardTranslateHandler extends Abstract2DTranslateHandler {
  
  
  private final double[] m_worldTrans = new double[3];
  private NumberTuple m_vectorCoordinates;
  
  public DefaultR3VectorKeyboardTranslateHandler(JComponent display) {
    super(display);
    setDrawDuringAction(true);
    setUpdateDuringAction(false);
  }
  
  /**
   * Translates the end point of the vector to the new math coordinates using
   * {@link net.mumie.mathletfactory.transformer.CanvasObjectTransformer#getMathObjectFromScreen}.
   * 
   * @see net.mumie.mathletfactory.action.handler.MMHandler#userDefinedAction(MMEvent)
   */
  public void userDefinedAction(MMEvent event) {
    double[] worldPoint = ((Canvas3DObjectTransformer)((MMCanvasObjectIF)m_master).getCanvasTransformer()).getWorldPickPointFromMaster();
    
    if ( (event.getKeyCode() == KeyEvent.VK_LEFT) &&
         !isInYOnly() ) {
      m_worldTrans[0] = -1./getCanvas().getWidth();
      m_worldTrans[1] = 0;
    }
    else if ( (event.getKeyCode() == KeyEvent.VK_RIGHT) &&
               !isInYOnly() ) {
      m_worldTrans[0] = 1./getCanvas().getWidth();
      m_worldTrans[1] = 0;
    }
    else if ( (event.getKeyCode() == KeyEvent.VK_UP) &&
               !isInXOnly() ) {
      m_worldTrans[0] = 0;
      m_worldTrans[1] = 1./getCanvas().getHeight();
    }
    else if ( (event.getKeyCode() == KeyEvent.VK_DOWN) &&
               !isInXOnly() ) {
      m_worldTrans[0] = 0;
      m_worldTrans[1] = -1./getCanvas().getHeight();
    }
    ((MM3DCanvas)getCanvas()).getWorld2WorldView().applyDeformationPartTo(m_worldTrans);
    Affine3DDouble.add(worldPoint, m_worldTrans);
    m_vectorCoordinates.getEntryRef(1).setDouble(worldPoint[0]);
    m_vectorCoordinates.getEntryRef(2).setDouble(worldPoint[1]);
    m_vectorCoordinates.getEntryRef(3).setDouble(worldPoint[2]);
  
    //System.out.println("object is"+m_newMathVector.getCoordinates());
    ((MMDefaultR3Vector)((MMCanvasObjectIF)m_master)).setDefaultCoordinates(m_vectorCoordinates, false);
     
    if ( isReDrawDuringAction() )
        m_master.render();
  }
  
  public void setMaster(MMObjectIF master){
    super.setMaster(master);
    m_vectorCoordinates = new NumberTuple(master.getNumberClass(), 3);
  }

  private MM3DCanvas getCanvas(){
    return ((MM3DCanvas)((MMCanvasObjectIF)m_master).getAsCanvasContent().getCanvas());
  }
  
  private Canvas3DObjectTransformer getTransformer3D(){
    return (Canvas3DObjectTransformer)((MMCanvasObjectIF)m_master).getCanvasTransformer();
  }
  
  /**
   * Calls {@link net.mumie.mathletfactory.transformer.Canvas3DObjectTransformer#updateFinished}.
   * This method will typically be called from within a 
   * {@link net.mumie.mathletfactory.action.CanvasControllerIF CanvasControllerIF},
   * when the current event cycle is finished.
   */
  public void userDefinedFinish(){
    getTransformer3D().updateFinished();
  }
  
  /**
   * Accepts arrow key events.
   * @see net.mumie.mathletfactory.action.handler.MMHandler#isResponsibleFor(MMEvent)
   */
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


