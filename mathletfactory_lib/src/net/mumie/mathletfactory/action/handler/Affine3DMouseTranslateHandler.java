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
import net.mumie.mathletfactory.math.algebra.geomgroup.AffineGroupElement;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.geom.affine.Affine3DPoint;
import net.mumie.mathletfactory.math.geom.affine.AffineGeometryIF;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.transformer.Canvas3DObjectTransformer;
import de.jreality.scene.IndexedFaceSet;
import de.jreality.scene.IndexedLineSet;
import de.jreality.scene.PointSet;


/**
 * This class is used for moving 3D Affine-Objects on a {@link net.mumie.mathletfactory.display.j3d.MMJ3DCanvas} in the viewers xy-plane. Moving is done by dragging with the left mouse button, i.e. it
 * internally uses <code>MOUSE_DRAGGED</code>.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */

public class Affine3DMouseTranslateHandler extends MMHandler {

	private MMEvent m_event;
	private boolean m_firstValuesUnset = true;

	/**
	 * These variables are used to store temporarily the pixel coordinates of the drag event.
	 */
	private int m_xPixOld, m_yPixOld, m_xPixNew, m_yPixNew;

	private Affine3DPoint m_oldMathP, m_newMathP;
	private AffineGroupElement m_generatedTrafo;
	private double[] m_newScreenPoint = new double[3];
	private double[] m_oldScreenPoint = new double[3];

	/**
	 * initializes an Affine3DMouseTranslateHandler. This MMHandler is used to translate any (mathematical) affine 3-dimensional object rendered in a MMCanvas in the viewers xy-plane by dragging it
	 * with the left mouse button.
	 * 
	 */
	public Affine3DMouseTranslateHandler( JComponent display ) {
		super( display );
		m_event = new MMEvent( MouseEvent.MOUSE_DRAGGED, MouseEvent.BUTTON1_MASK, 0, MMEvent.NO_KEY, MMEvent.NO_MODIFIER_SET );
	}

	public void setMaster( MMObjectIF master ) {
		super.setMaster( master );
		m_oldMathP = new Affine3DPoint( master.getNumberClass() );
		m_newMathP = new Affine3DPoint( master.getNumberClass() );
		m_generatedTrafo = new AffineGroupElement( master.getNumberClass(), 3 );
	}

	/**
	 * This handler accepts button 1 dragged mouse events with no other keys pressed.
	 * 
	 * @see net.mumie.mathletfactory.action.handler.MMHandler#userDefinedDealWith(MMEvent)
	 */
	public boolean userDefinedDealsWith( MMEvent event ) {
		boolean dealsWith = false;
		if ( event instanceof MMJREvent ) {
			MMJREvent jrEvent = ( MMJREvent ) event;
			if ( jrEvent.getEventType() == MMJREvent.POINT_DRAG_EVENT ) {
				dealsWith = ( jrEvent.getSource() instanceof PointSet ) && !( jrEvent.getSource() instanceof IndexedLineSet ) && !( jrEvent.getSource() instanceof IndexedFaceSet );
			} else if ( jrEvent.getEventType() == MMJREvent.LINE_DRAG_EVENT ) {
				dealsWith = ( jrEvent.getSource() instanceof IndexedLineSet ) && !( jrEvent.getSource() instanceof IndexedFaceSet );
			} else if ( jrEvent.getEventType() == MMJREvent.FACE_DRAG_EVENT ) {
				dealsWith = jrEvent.getSource() instanceof IndexedFaceSet;
			}
		} else {
			dealsWith = this.m_event.equals( event );
		}
		return dealsWith;
	}

	/**
	 * The method uses the pixel coordinates of two consecutive mouse drag events to generate a translation in the abstract mathematics which is then applied to the handlers master object (see
	 * {@link net.mumie.mathletfactory.action.handler.MMHandler#m_master}). The new position of the master object is strictly determined by the new mouse position on the screen.
	 * 
	 * <p>
	 * Observe: While the (real) coordinates of the master object (which is expected to be an element of the affine 2d geometry) are recomputed for every mouse drag event, the object will only be
	 * rerendered during the mouse drag
	 */
	public void userDefinedAction( MMEvent event ) {
		// System.out.println("Affine3DMOuseTranslateHandler::userDefinedAction
		// called!");

		NumberTuple translationVector = null;

		if ( event instanceof MMJREvent ) {
			translationVector = ( ( MMJREvent ) event ).getTranslation();
			// System.err.println( "Translate: " + translationVector );
		} else {
			if ( m_firstValuesUnset ) {
				m_xPixOld = getRealMasterType().getCanvas().getController().getLastPressedX();
				m_yPixOld = getRealMasterType().getCanvas().getController().getLastPressedY();
				m_firstValuesUnset = false;
			}
			m_xPixNew = event.getX();
			m_yPixNew = event.getY();

			m_newScreenPoint[0] = m_xPixNew;
			m_newScreenPoint[1] = m_yPixNew;
			m_oldScreenPoint[0] = m_xPixOld;
			m_oldScreenPoint[1] = m_yPixOld;

			getTransformer().getMathObjectFromScreen( m_oldScreenPoint, m_oldMathP );

			getTransformer().getMathObjectFromScreen( m_newScreenPoint, m_newMathP );

			translationVector = ( NumberTuple ) m_newMathP.getProjectiveCoordinatesOfPoint();
			translationVector.subFrom( m_oldMathP.getProjectiveCoordinatesOfPoint() );
			m_xPixOld = m_xPixNew;
			m_yPixOld = m_yPixNew;

		}
		if ( translationVector != null ) {
			m_generatedTrafo.setTranslation( translationVector );
			( ( AffineGeometryIF ) m_master ).groupAction( m_generatedTrafo );
		}

		// if ( isReDrawDuringAction() ) m_master.render();
		// System.out.println( m_master );
	}

	/**
	 * Resets this handler. This method will typically be called from within a {@link net.mumie.mathletfactory.action.CanvasControllerIF Controller} when the current event cycle is finished.
	 */
	public void userDefinedFinish() {
		m_firstValuesUnset = true;
		if ( getTransformer() != null ) {
			getTransformer().updateFinished();
		}
	}

	/**
	 * To avoid casting in the code: This handler can only work for instances of MMCanvasObjectIF.
	 */
	private MMCanvasObjectIF getRealMasterType() {
		return ( MMCanvasObjectIF ) m_master;
	}

	private Canvas3DObjectTransformer getTransformer() {
		return ( Canvas3DObjectTransformer ) ( ( MMCanvasObjectIF ) m_master ).getCanvasTransformer();
	}
}
