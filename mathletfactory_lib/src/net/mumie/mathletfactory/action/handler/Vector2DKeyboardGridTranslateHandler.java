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
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR2;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR2Vector;
import net.mumie.mathletfactory.mmobject.number.MMComplex;

/**
 * This handler allows a user to translate the end point of a selected 
 * {@link net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR2Vector} 
 * on a grid of user defined width by using the arrow keys.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public class Vector2DKeyboardGridTranslateHandler extends MMHandler {
  
  private double unitInMath;
  private double[] m_newWorldCoords = new double[2];

  public Vector2DKeyboardGridTranslateHandler(JComponent display, double unit){
    super(display);
    setUnitInMath(unit);
  }
  
  public Vector2DKeyboardGridTranslateHandler(JComponent display){
    this(display, 0);
  }
  
  public void userDefinedAction(MMEvent event) {
    // case unitInMath=0 is not good implemented!
    // case unitInMath=0 is not good implemented!
    if(unitInMath == 0){
      if(getMaster() instanceof MMDefaultR2Vector)             
        setUnitInMath(((MMDefaultR2)((MMDefaultR2Vector)getMaster()).getVectorSpace()).getGridInMath());        
      else        
        setUnitInMath(0.1);
    } 

    if(getMaster() instanceof MMDefaultR2Vector){
      m_newWorldCoords[0] = ((MMDefaultR2Vector)getMaster()).getDefaultCoordinatesRef().getEntry(1).getDouble();
      m_newWorldCoords[1] = ((MMDefaultR2Vector)getMaster()).getDefaultCoordinatesRef().getEntry(2).getDouble();
    } else {
      m_newWorldCoords[0] = ((MMComplex)getMaster()).getRe();
      m_newWorldCoords[1] = ((MMComplex)getMaster()).getIm();
    }


        
    // set the corresponding grid in the canvas,
    // the grid always
    if (event.getKeyCode() == KeyEvent.VK_LEFT) {
      m_newWorldCoords[0] -= getUnitInMath();
    }
    if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
      m_newWorldCoords[0] += getUnitInMath();
    }
    if (event.getKeyCode() == KeyEvent.VK_UP) {
      m_newWorldCoords[1] += getUnitInMath();
    }
    if (event.getKeyCode() == KeyEvent.VK_DOWN) {
      m_newWorldCoords[1] -= getUnitInMath();
    }
    
    if(getMaster() instanceof MMDefaultR2Vector){
      ((MMDefaultR2Vector)getMaster()).setDefaultCoordinates(m_newWorldCoords[0], m_newWorldCoords[1]);
    } else 
    ((MMComplex)getMaster()).setComplex(m_newWorldCoords[0], m_newWorldCoords[1]);

    if ( isReDrawDuringAction() )
      m_master.render();
  }
  
  public void userDefinedFinish() {
  }
  
  public boolean userDefinedDealsWith(MMEvent event) {
    if ((event.getEventType() == KeyEvent.KEY_PRESSED)) {
      if ((event.getKeyCode() == KeyEvent.VK_LEFT) ||
           (event.getKeyCode() == KeyEvent.VK_RIGHT) ||
           (event.getKeyCode() == KeyEvent.VK_UP) ||
           (event.getKeyCode() == KeyEvent.VK_DOWN)) {
        return true;
      }
    }
    return false;
  }

  /** Returns the width of a unit in the grid (in mathematical coordinates).*/
  public double getUnitInMath() {
    return unitInMath;
  }

  /** Sets the width of a unit in the grid (in mathematical coordinates).*/
  public void setUnitInMath(double number) {
    unitInMath = number;
  }
}


