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
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.mmobject.MMObjectIF;

/**
 * This class acts as a handler that allows the translation of mmobjects by mouse on a grid with a user defined width.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public class Affine2DMouseGridTranslateHandler
	extends Abstract2DTranslateHandler {
	private double m_unitInMath = 0;
	private MMEvent m_event;
	private boolean m_firstValuesUnset = true;

	/**
	 * These variables are used to store temporarily the pixel coordinates of
	 * the drag event.
	 */
	private final double[] m_oldCoords = new double[2],
		m_newCoords = new double[2],
		m_tmp = new double[2];

	private Affine2DPoint m_oldMathP, m_newMathP;
	private AffineGroupElement m_generatedTrafo;

	public Affine2DMouseGridTranslateHandler(JComponent display) {
		super(display);
		m_unitInMath = 0;
		m_event =
			new MMEvent(
				MouseEvent.MOUSE_DRAGGED,
				MouseEvent.BUTTON1_MASK,
				0,
				MMEvent.NO_KEY,
        MMEvent.NO_MODIFIER_SET);
	}

	public Affine2DMouseGridTranslateHandler(JComponent display, double unit) {
		this(display);
		m_unitInMath = unit;
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
	 * {@link MMHandler#m_master MMHandler.m_master}).
	 * The new position of the master object is strictly determined by the
	 * new mouse position on the screen.
	 *
	 * <p>
	 * Observe: While the (real) coordinates of the master object (which is
	 * expected to be an element of the affine 2d geometry) are recomputed for
	 * every mouse drag event, the object will only be rerendered during the
	 * mouse drag
	 */
	public void userDefinedAction(MMEvent event) {
		if (m_firstValuesUnset) {
			m_oldCoords[0] =
				getRealMasterType().getCanvas().getController().getLastPressedX();
			m_oldCoords[1] =
				getRealMasterType().getCanvas().getController().getLastPressedY();
			m_firstValuesUnset = false;
		}
		m_newCoords[0] = event.getX();
		m_newCoords[1] = event.getY();
		m_tmp[0] = m_newCoords[0];
		m_tmp[1] = m_newCoords[1];
    
		if (isInXOnly()) {
			m_tmp[1] = 0;
			m_oldCoords[1] = 0;
		}
		if (isInYOnly()) {
			m_tmp[0] = 0;
			m_oldCoords[0] = 0;
		}

		getTransformer().getMathObjectFromScreen(m_tmp, m_newMathP);

		getTransformer().getMathObjectFromScreen(m_oldCoords, m_oldMathP);

		/*newMathPoint becomes the translation vector now*/
    m_newMathP.setProjectiveCoordinates(m_newMathP.getProjectiveCoordinatesOfPoint().subFrom(
			m_oldMathP.getProjectiveCoordinatesOfPoint()));

    MNumber entry;
		entry = m_newMathP.getXProjAsNumberRef();
		entry.setDouble(
			Math.rint((entry.getDouble() / m_unitInMath)) * m_unitInMath);
    m_newMathP.setX(entry);
		entry = m_newMathP.getYProjAsNumberRef();
		entry.setDouble(
			Math.rint((entry.getDouble() / m_unitInMath)) * m_unitInMath);
    m_newMathP.setY(entry);

    if(m_newMathP.getXAsDouble() != 0 || m_newMathP.getYAsDouble() != 0) {
  		m_generatedTrafo.setTranslation(
  			m_newMathP.getProjectiveCoordinatesOfPoint());
  		((AffineGeometryIF) m_master).groupAction(m_generatedTrafo);
      
      m_oldCoords[0] = m_newCoords[0];
      m_oldCoords[1] = m_newCoords[1];
    }
	}

	/**
	 * resets the boolean {@link #m_firstValuesUnset m_fistValuesUnset} to <it>true</it>. This method
	 * will typically be called from within a
	 * {@link net.mumie.mathletfactory.action.CanvasControllerIF CanvasControllerIF},
	 * when the current event cycle is finished.
	 */
	public void userDefinedFinish() {
		m_firstValuesUnset = true;
	}

  /** Sets the width of a unit in the grid (in mathematical coordinates).*/
	public void setUnitInMath(double aValue) {
		m_unitInMath = aValue;
	}

  /** Returns the width of a unit in the grid (in mathematical coordinates).*/
	public double getUnitInMath() {
		return m_unitInMath;
	}
}
