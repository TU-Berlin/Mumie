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
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR2Vector;
import net.mumie.mathletfactory.transformer.Canvas2DObjectTransformer;

/**
 * This handler allows a user to translate the end point of a selected 
 * {@link net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR2Vector} 
 * by using the mouse.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public class DefaultR2VectorMouseTranslateHandler extends MMHandler {

	private MMEvent m_event;
	private NumberVector m_newMathVector;
	private final double[] m_newScreenCoords = new double[2];

	public DefaultR2VectorMouseTranslateHandler(JComponent display) {
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
	}

	public void userDefinedFinish() {
	}

	public void setMaster(MMObjectIF master) {
		super.setMaster(master);
		m_newMathVector =
			(NumberVector) ((MMDefaultR2Vector) m_master)
				.getVectorSpace()
				.getZeroVector();
	}

}
