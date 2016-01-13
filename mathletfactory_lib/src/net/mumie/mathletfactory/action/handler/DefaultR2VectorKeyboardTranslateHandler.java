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
import net.mumie.mathletfactory.math.algebra.linalg.NumberVector;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.transformer.CanvasObjectTransformer;

/**
 * This handler allows a user to translate the end point of a selected 
 * {@link net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR2Vector} 
 * by using the arrow keys.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public class DefaultR2VectorKeyboardTranslateHandler extends MMHandler {
	private final double[] m_screenPos1 = new double[2];
	private final double[] m_screenPos2 = new double[2];
	private NumberVector m_translationVector, m_vectorPos1;
	public DefaultR2VectorKeyboardTranslateHandler(JComponent display) {
		super(display);
	}
	public void setMaster(MMObjectIF master) {
		super.setMaster(master);
		m_translationVector =
			(NumberVector) ((NumberVector)getMaster())
				.getVectorSpace()
				.getZeroVector();
		m_vectorPos1 =
			(NumberVector) ((NumberVector)getMaster())
				.getVectorSpace()
				.getZeroVector();
	}
  
	public void userDefinedAction(MMEvent event) {
		if (event.getKeyCode() == KeyEvent.VK_LEFT) {
			m_screenPos2[0] = -1;
			m_screenPos2[1] = 0;
		}
		else if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
			m_screenPos2[0] = 1;
			m_screenPos2[1] = 0;
		}
		else if (event.getKeyCode() == KeyEvent.VK_UP) {
			m_screenPos2[0] = 0;
			m_screenPos2[1] = -1;
		}
		else if (event.getKeyCode() == KeyEvent.VK_DOWN) {
			m_screenPos2[0] = 0;
			m_screenPos2[1] = 1;
		}
		(
			(CanvasObjectTransformer) ((MMCanvasObjectIF)m_master)
				.getCanvasTransformer())
				.getMathObjectFromScreen(
			m_screenPos1,
			m_vectorPos1);
		(
			(CanvasObjectTransformer) ((MMCanvasObjectIF)m_master)
				.getCanvasTransformer())
				.getMathObjectFromScreen(
			m_screenPos2,
			m_translationVector);
		m_translationVector.subFrom(m_vectorPos1);
		((NumberVector)getMaster()).addTo(m_translationVector);
		if (isReDrawDuringAction())
			m_master.render();
		else
			((CanvasObjectTransformer) ((MMCanvasObjectIF)m_master)
				.getCanvasTransformer())
				.synchronizeMath2Screen();
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
