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
import net.mumie.mathletfactory.action.jr3d.MMJREvent;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.algebra.linalg.NumberVector;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR3Vector;
import net.mumie.mathletfactory.transformer.Canvas3DObjectTransformer;


/**
 * This handler allows a user to translate the end point of a selected {@link net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR3Vector} by dragging it with the mouse. This is done in a plane
 * parallel to the view plane (the virtual world coordinates of the screen).
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public class DefaultR3VectorMouseTranslateHandler extends MMHandler {

	private MMEvent m_event;
	private NumberVector m_newMathVector;
	private final double[] m_newScreenCoords = new double[2];

	public DefaultR3VectorMouseTranslateHandler( JComponent display ) {
		super( display );
		m_event = new MMEvent( MouseEvent.MOUSE_DRAGGED, MouseEvent.BUTTON1_MASK, 0, MMEvent.NO_KEY, MMEvent.NO_MODIFIER_SET );
		setDrawDuringAction( true );
		setUpdateDuringAction( true );
	}

	/**
	 * Accepts button 1 mouse dragging events with no keys pressed.
	 * 
	 * @see net.mumie.mathletfactory.action.handler.MMHandler#userDefinedDealWith(MMEvent)
	 */
	public boolean userDefinedDealsWith( MMEvent event ) {
		boolean dealsWith = false;
		if ( event instanceof MMJREvent ) {
			dealsWith = true;
		} else {
			dealsWith = this.m_event.equals( event );
		}
		return dealsWith;
	}

	/**
	 * Translates the end point of the vector to the new math coordinates using {@link net.mumie.mathletfactory.transformer.CanvasObjectTransformer#getMathObjectFromScreen}.
	 * 
	 * @see net.mumie.mathletfactory.action.handler.MMHandler#userDefinedAction(MMEvent)
	 */
	public void userDefinedAction( MMEvent event ) {
		if ( event instanceof MMJREvent ) {
			MMDefaultR3Vector vector = ( ( MMDefaultR3Vector ) m_master );

			NumberTuple translationVector = ( ( MMJREvent ) event ).getTranslation();
			vector.setDefaultCoordinates( vector.getDefaultCoordinates().addTo( translationVector ) );
		} else {
			m_newScreenCoords[0] = event.getX();
			m_newScreenCoords[1] = event.getY();

			getTransformer().getMathObjectFromScreen( m_newScreenCoords, m_newMathVector );
			( ( MMDefaultR3Vector ) m_master ).setDefaultCoordinates( m_newMathVector.getDefaultCoordinatesRef(), false );
		}
	}

	/**
	 * Calls {@link net.mumie.mathletfactory.transformer.Canvas3DObjectTransformer#updateFinished}. This method will typically be called from within a
	 * {@link net.mumie.mathletfactory.action.CanvasControllerIF CanvasControllerIF}, when the current event cycle is finished.
	 */
	public void userDefinedFinish() {
		if ( getTransformer() != null ) {
			getTransformer().updateFinished();
		}
	}

	public void setMaster( MMObjectIF master ) {
		super.setMaster( master );
		m_newMathVector = ( NumberVector ) ( ( MMDefaultR3Vector ) m_master ).getVectorSpace().getZeroVector();
	}

	private Canvas3DObjectTransformer getTransformer() {
		return ( Canvas3DObjectTransformer ) ( ( MMCanvasObjectIF ) m_master ).getCanvasTransformer();
	}
}
