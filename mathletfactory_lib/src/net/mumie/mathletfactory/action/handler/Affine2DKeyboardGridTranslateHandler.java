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
import net.mumie.mathletfactory.math.algebra.geomgroup.AffineGroupElement;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.geom.affine.AffineGeometryIF;
import net.mumie.mathletfactory.mmobject.MMObjectIF;

/**
 * This class acts as a handler that allows the translation of mmobjects by keyboard (cursor keys) 
 * on a grid with a user defined width.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public class Affine2DKeyboardGridTranslateHandler extends MMHandler {

	private double m_unitInMath = 0;

	private AffineGroupElement m_translation;
	private NumberTuple translation;

	public Affine2DKeyboardGridTranslateHandler(JComponent display) {
		super(display);
		m_unitInMath = 0;
	}

	public Affine2DKeyboardGridTranslateHandler(
		JComponent display,
		double unit) {
		super(display);
		m_unitInMath = unit;
	}

	public void setMaster(MMObjectIF master) {
		super.setMaster(master);
		m_translation = new AffineGroupElement(master.getNumberClass(), 2);
		translation = new NumberTuple(m_master.getNumberClass(), 2);
	}

	public void userDefinedAction(MMEvent event) {

		if (event.getKeyCode() == KeyEvent.VK_LEFT) {
			translation.getEntryRef(1).setDouble(-m_unitInMath);
			translation.getEntryRef(2).setDouble(0);
		}
		else if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
			translation.getEntryRef(1).setDouble(m_unitInMath);
			translation.getEntryRef(2).setDouble(0);
		}
		else if (event.getKeyCode() == KeyEvent.VK_UP) {
			translation.getEntryRef(1).setDouble(0);
			translation.getEntryRef(2).setDouble(m_unitInMath);
		}
		else if (event.getKeyCode() == KeyEvent.VK_DOWN) {
			translation.getEntryRef(1).setDouble(0);
			translation.getEntryRef(2).setDouble(-m_unitInMath);
		}
		m_translation.setTranslation(translation);
		((AffineGeometryIF) m_master).groupAction(m_translation);
	}

	public void userDefinedFinish() {
	}

	public boolean userDefinedDealsWith(MMEvent event) {
		if ((event.getEventType() == KeyEvent.KEY_PRESSED)
			&& (event.getModifier() == KeyEvent.ALT_MASK)) {
			if ((event.getKeyCode() == KeyEvent.VK_LEFT)
				|| (event.getKeyCode() == KeyEvent.VK_RIGHT)
				|| (event.getKeyCode() == KeyEvent.VK_UP)
				|| (event.getKeyCode() == KeyEvent.VK_DOWN)) {
				return true;
			}
		}
		return false;
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
