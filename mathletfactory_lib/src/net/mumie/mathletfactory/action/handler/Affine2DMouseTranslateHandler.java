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
import net.mumie.mathletfactory.math.algebra.geomgroup.AffineGroupElement;
import net.mumie.mathletfactory.math.geom.affine.Affine2DPoint;
import net.mumie.mathletfactory.math.geom.affine.AffineGeometryIF;
import net.mumie.mathletfactory.mmobject.MMObjectIF;

/**
 *  This class is used for moving 2D Affine-Objects on a
 *  {@link net.mumie.mathletfactory.display.g2d.MMG2DCanvas}.
 *  Moving is done by dragging with the left mouse button, i.e. it internally
 *  uses <code>MOUSE_DRAGGED</code>.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public class Affine2DMouseTranslateHandler extends Abstract2DTranslateHandler {
  private MMEvent m_event;
  private boolean m_firstValuesUnset = true;

  /**
   * These variables are used to store temporarily the pixel coordinates of
   * the drag event.
   */
  private final double[] m_oldCoords = new double[2],
    m_newCoords = new double[2];
  private Affine2DPoint m_oldMathP, m_newMathP;
  private AffineGroupElement m_generatedTrafo;

  /**
   * initializes an Affine2DMouseTranslateHandler. This MMHandler is used to
   * translate any (mathematical) affine twodimensional object rendered in a
   * MMCanvas by dragging it with the left mouse button.
   */
  public Affine2DMouseTranslateHandler(JComponent display) {
    super(display);
    m_event =
      new MMEvent(
        MouseEvent.MOUSE_DRAGGED,
        MouseEvent.BUTTON1_MASK,
        0,
        MMEvent.NO_KEY,
        MMEvent.NO_MODIFIER_SET);
  }
  
  public void setMaster(MMObjectIF master) {
    super.setMaster(master);
    m_oldMathP = new Affine2DPoint(master.getNumberClass());
    m_newMathP = new Affine2DPoint(master.getNumberClass());
    m_generatedTrafo = new AffineGroupElement(master.getNumberClass(), 2);
  }
  
  public boolean userDefinedDealsWith(MMEvent event) {
    return m_event.equals(event);
  }
  
  /**
   * The method uses the pixel coordinates of two consecutive mouse drag events
   * to generate a translation in the abstract mathematics which is then
   * applied to the handlers master object (see
   * {@link net.mumie.mathletfactory.action.MMHandler#m_master MMHandler.m_master}).
   * The new position of the master object is strictly determined by the
   * new mouse position on the screen.
   */
  public void userDefinedAction(MMEvent event) {
    if (m_firstValuesUnset) {
      m_oldCoords[0] = getRealMasterType().getCanvas().getController().getLastPressedX();
      m_oldCoords[1] = getRealMasterType().getCanvas().getController().getLastPressedY();
      m_firstValuesUnset = false;
    }
    m_newCoords[0] = event.getX();
    m_newCoords[1] = event.getY();
    if (isInXOnly()) {
      m_newCoords[1] = 0;
      m_oldCoords[1] = 0;
    }
    if (isInYOnly()) {
      m_newCoords[0] = 0;
      m_oldCoords[0] = 0;
    }
    if (isYDefinedByFunction()) {
      if (m_yFunction == null)
        throw new IllegalArgumentException("function has not been initialized yet!");
      else {
        getTransformer().getMathObjectFromScreen(m_newCoords, m_newMathP);
        m_newMathP.setY(m_yFunction.evaluate(m_newMathP.getXAsDouble()));
        getTransformer().getScreenPointFromMath(m_newMathP, m_newCoords);
      }
    } else if (isXDefinedByFunction()) {
      if (m_xFunction == null)
        throw new IllegalArgumentException("function has not been initialized yet!");
      else {
        getTransformer().getMathObjectFromScreen(m_newCoords, m_newMathP);
        m_newMathP.setX(m_xFunction.evaluate(m_newMathP.getYAsDouble()));
        getTransformer().getScreenPointFromMath(m_newMathP, m_newCoords);
      }
    } else {
      getTransformer().getMathObjectFromScreen(m_newCoords, m_newMathP);
    }
    getTransformer().getMathObjectFromScreen(m_oldCoords, m_oldMathP);
    m_oldCoords[0] = m_newCoords[0];
    m_oldCoords[1] = m_newCoords[1];      
    // generate the corresponding translation in the abstract affine 2d space:
    // generate the affine group element due to this translation.
    m_generatedTrafo.setTranslation(
      m_newMathP.getProjectiveCoordinatesOfPoint().subFrom(
        m_oldMathP.getProjectiveCoordinatesOfPoint()));
    ((AffineGeometryIF) m_master).groupAction(m_generatedTrafo);
    m_oldMathP.setFromPoint(m_newMathP);
  }
  
  public void userDefinedFinish() {
    m_firstValuesUnset = true;
  }
}
