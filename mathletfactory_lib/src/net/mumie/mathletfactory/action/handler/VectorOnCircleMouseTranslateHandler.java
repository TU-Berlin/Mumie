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

import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import net.mumie.mathletfactory.action.MMEvent;
import net.mumie.mathletfactory.math.algebra.linalg.NumberVector;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR2Vector;
import net.mumie.mathletfactory.transformer.Canvas2DObjectTransformer;

/**
 * This class represents a handler that allows the translation of an mmobject
 * on a circle of defined radius.
 * 
 * @author Mrose
 * @mm.docstatus finished
 */
public class VectorOnCircleMouseTranslateHandler extends MMHandler {
  private MMEvent m_event;
  private NumberVector m_newMathVector;
  private double radius = 1;
  private final double[] m_newScreenCoords = new double[2];

  public VectorOnCircleMouseTranslateHandler(JComponent display) {
    super(display);
    m_event =
      new MMEvent(
        MouseEvent.MOUSE_DRAGGED,
        MouseEvent.BUTTON1_MASK,
        0,
        MMEvent.NO_KEY,
        MMEvent.NO_MODIFIER_SET);
  }
  
  public boolean userDefinedDealsWith(MMEvent event) {
    return m_event.equals(event);
  }
  
  public void userDefinedAction(MMEvent event) {
    m_newScreenCoords[0] = event.getX();
    m_newScreenCoords[1] = event.getY();
    (
      (Canvas2DObjectTransformer) ((MMCanvasObjectIF) getMaster())
        .getCanvasTransformer())
        .getMathObjectFromScreen(
      m_newScreenCoords,
      m_newMathVector);
    ((MMDefaultR2Vector) m_master).setDefaultCoordinates(
      m_newMathVector.getDefaultCoordinatesRef());
      
    MNumber coord1 = ((MMDefaultR2Vector) m_master).getDefaultCoordinate(1);
    MNumber coord2 = ((MMDefaultR2Vector) m_master).getDefaultCoordinate(2);
    MNumber norm =
      (coord1.copy().mult(coord1).add(coord2.copy().mult(coord2)))
        .squareRoot();
    if(radius != 1)
      norm.setDouble(norm.getDouble()*radius);
    ((MMDefaultR2Vector) m_master).setDefaultCoordinate(
      1,
      coord1.copy().div(norm));
    ((MMDefaultR2Vector) m_master).setDefaultCoordinate(
      2,
      coord2.copy().div(norm));
  }
  
  /**
   * resets the boolean #m_fistValuesUnset to <it>true</it>. This method will
   * typically be called from within a {@link net.mumie.mathletfactory.action.CanvasControllerIF CanvasControllerIF},
   * when the current event cycle is finished.
   */
  public void userDefinedFinish() {
  }
  
  public void setMaster(MMObjectIF master) {
    super.setMaster(master);
    m_newMathVector =
      (NumberVector) ((MMDefaultR2Vector) m_master)
        .getVectorSpace()
        .getZeroVector();
  }

  /**
   * Returns the radius of the cirle.
   */
  public double getRadius() {
    return radius;
  }

  /**
   * Sets the radius of the cirle.
   */
  public void setRadius(double d) {
    radius = d;
  }

}
