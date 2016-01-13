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
import net.mumie.mathletfactory.math.geom.affine.Affine2DPoint;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.number.MMComplex;
import net.mumie.mathletfactory.mmobject.number.MMComplexRational;
import net.mumie.mathletfactory.transformer.Canvas2DObjectTransformer;

/**
 * Mouse handler for a complex number 
 * {@link net.mumie.mathletfactory.mmobject.number.MMComplex}
 * which is rendered as a vector.
 * 
 * @author Mrose
 * @mm.docstatus finished
 */
public class ComplexVectorMouseTranslateHandler extends MMHandler {
	private MMEvent m_event;
	private Affine2DPoint m_newMathVector = new Affine2DPoint(MDouble.class);
	private final double[] m_newScreenCoords = new double[2];
	public ComplexVectorMouseTranslateHandler(JComponent display) {
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
    if(m_master instanceof MMComplex){
		((MMComplex) m_master).setComplex(
			m_newMathVector.getXAsDouble(),
			m_newMathVector.getYAsDouble());
    } else {
    ((MMComplexRational) m_master).setComplex(
      m_newMathVector.getXAsDouble(),
      m_newMathVector.getYAsDouble());
    }
	}
  
	public void userDefinedFinish() {
	}
  
	public void setMaster(MMObjectIF master) {
		super.setMaster(master);
	}
}
