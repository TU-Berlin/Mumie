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
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.transformer.Canvas2DObjectTransformer;

/**
 * This class handles the translation of mmobjects by keyboard (cursor keys).
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public class Affine2DKeyboardTranslateHandler
	extends Abstract2DTranslateHandler {

	private final double[] m_worldTrans = new double[2];
	private AffineGroupElement m_trans; //translation group element
	private NumberTuple m_translationVector;

	public Affine2DKeyboardTranslateHandler(JComponent display) {
		super(display);
		setDrawDuringAction(true);
		setUpdateDuringAction(true);
	}

	public void setMaster(MMObjectIF master) {
		super.setMaster(master);
		m_translationVector = new NumberTuple(master.getNumberClass(), 2);
		m_trans = new AffineGroupElement(master.getNumberClass(), 2);
	}

	public void userDefinedAction(MMEvent event) {

		m_worldTrans[0] = 0;
		m_worldTrans[1] = 0;
		if ((event.getKeyCode() == KeyEvent.VK_LEFT) && !isInYOnly()) {
			m_worldTrans[0] = -1;
			m_worldTrans[1] = 0;
		}
		else if ((event.getKeyCode() == KeyEvent.VK_RIGHT) && !isInYOnly()) {
			m_worldTrans[0] = 1;
			m_worldTrans[1] = 0;
		}
		else if ((event.getKeyCode() == KeyEvent.VK_UP) && !isInXOnly()) {
			m_worldTrans[0] = 0;
			m_worldTrans[1] = -1;
		}
		else if ((event.getKeyCode() == KeyEvent.VK_DOWN) && !isInXOnly()) {
			m_worldTrans[0] = 0;
			m_worldTrans[1] = 1;
		}

		((Canvas2DObjectTransformer) ((MMCanvasObjectIF)m_master).getCanvasTransformer())
			.getScreen2World()
			.applyDeformationPartTo(m_worldTrans);

		m_translationVector.getEntryRef(1).setDouble(m_worldTrans[0]);
		m_translationVector.getEntryRef(2).setDouble(m_worldTrans[1]);
		m_trans.setTranslation(m_translationVector);
		((AffineGeometryIF) m_master).groupAction(m_trans);
	}

	public void userDefinedFinish() {
	}

	public boolean userDefinedDealsWith(MMEvent event) {
		if ((event.getEventType() == KeyEvent.KEY_PRESSED)
			&& (event.getModifier() == 0)) {
			if ((event.getKeyCode() == KeyEvent.VK_LEFT)
				|| (event.getKeyCode() == KeyEvent.VK_RIGHT)
				|| (event.getKeyCode() == KeyEvent.VK_UP)
				|| (event.getKeyCode() == KeyEvent.VK_DOWN)) {
				return true;
			}
		}
		return false;
	}

}
