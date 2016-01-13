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

package net.mumie.mathletfactory.action.jr3d;


import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.mumie.mathletfactory.action.CanvasControllerIF;
import net.mumie.mathletfactory.action.MMEvent;
import net.mumie.mathletfactory.action.handler.MMHandler;
import net.mumie.mathletfactory.display.MMCanvas;
import net.mumie.mathletfactory.display.jr3d.MMJR3DCanvas;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine3DPoint;
import de.jreality.scene.tool.Tool;
import de.jreality.tools.DragEventTool;
import de.jreality.tools.FaceDragEvent;
import de.jreality.tools.FaceDragListener;
import de.jreality.tools.LineDragEvent;
import de.jreality.tools.LineDragListener;
import de.jreality.tools.PointDragEvent;
import de.jreality.tools.PointDragListener;


/**
 * Der Kontroller uebernimmt die Verarbeitung der von JReality ausgeloesten Eregnisse. <br>
 * Dabei beschraenkt man sich nur auf die Behandlung der Objekt-Ereignisse (Ereignisse, welche durch die Manipulation der Objekte ausgeloest werden). <br>
 * <br>
 * Die vorverarbeitete Events werden dann an die aktuelle {@link MMHandler} weitergereicht. Diese bestimmen auch, wie <br>
 * ein Event interpretiert wird.<br>
 * Das Ziehen von Objecten ist der einzige Ausloeser.
 * 
 * @see JR3DCanvasControllerIF
 * 
 * @author Juergen Weber
 */
public class JR3DCanvasController implements JR3DCanvasControllerIF {

	/**
	 * Jeder Kontroller wird einer Zeichenflaeche zugeordnet
	 */
	private MMCanvas m_canvas;

	/**
	 * zeigt an, ob der Kontroller die ankommenden Eregnisse ueberhaupt annehmen soll
	 */
	private boolean m_enabled;

	/**
	 * die folgenden Felder dienen nur der Abwaehrtskompabilitaet mit der Oberklasse {@link CanvasControllerIF}
	 */
	private int m_lastPressedX;
	private int m_lastPressedY;
	private int m_lastClickedX;
	private int m_lastClickedY;

	/**
	 * das aktuell ausgewaehlte Objekt
	 */
	private MMCanvasObjectIF m_selectedObject;

	/**
	 * Liste mit vor-ausgewaehlten Objekten. <br>
	 * Noetig um mehrere Objekte gleichzeitig zu manipulieren (zu ziehen).<br>
	 * Sollte im Verbindung mit der 'Strg' Taste eingesetzt werden.
	 */
//	private List<MMCanvasObjectIF> m_listOfSelectedObjects = new ArrayList<MMCanvasObjectIF>();
	private List m_listOfSelectedObjects = new ArrayList();

	/**
	 * die Liste mit gerade aktiven Handlern.<br>
	 * Wird dann aktualisiert, wenn ein neues Object selektiert bzw. gezogen wird.
	 */
//	private List<MMHandler> m_activeHandlers = new ArrayList<MMHandler>();
	private List m_activeHandlers = new ArrayList();

	/**
	 * die Tools, welche die JReality-Events bereitstellen.<br>
	 * zur Zeit nur {@link DragEventTool}
	 */
	private Tool[] m_tools;

	/**
	 * Erzeugt einen neuen Kontroller. <br>
	 * Anschliessend sollte die Methode {@link #setCanvas(MMCanvas)} aufgerufen werden.<br>
	 */
	public JR3DCanvasController() {
		// die Tools erzeugen und entsprechend anpassen (Listener hinzufuegen)
		this.createTools();
	}

	/**
	 * Erzeugt und initialisiert die Liste mit den Tools.<br>
	 * Verwendet wird ausschliesslich ein {@link DragEventTool}.<br>
	 * {@link DragEventTool} reagiert auf das Ziehen von Punkten, Linien oder Flaechen. <br>
	 * Da alle Objekte aus den oben genannten Primitiven aufgebaut werden, so ist es ausreichend, nur dieses Tool zu benutzen. <br>
	 * {@link DragEventTool} erwartet drei Listener, die dann durch die Manipulation der Objekte aufgerufen werden.<br>
	 * <br>
	 * Listener: {@link PointDragListener}, {@link LineDragListener}, {@link FaceDragListener}
	 */
	private void createTools() {
		DragEventTool objectDragTool = new DragEventTool();

		objectDragTool.addPointDragListener( new PointDragListener() {

			private MMCanvasObjectIF m_obj;
			private boolean m_perform;

			private double[] m_position;

			public void pointDragEnd( PointDragEvent e ) {}

			public void pointDragStart( PointDragEvent e ) {
				System.out.println( "Point Drag" );

				MMAffine3DPoint point = JRGeometryUtils.getPointFromPointSet( e.getPointSet(), e.getIndex() );
				this.m_position = new double[4];
				this.m_position[0] = point.getXAsDouble();
				this.m_position[1] = point.getYAsDouble();
				this.m_position[2] = point.getZAsDouble();
				this.m_position[3] = 1.0;

				this.m_obj = ( ( MMJR3DCanvas ) JR3DCanvasController.this.m_canvas ).getGeometryOwner( e.getPointSet() );
				JR3DCanvasController.this.setSelectedObject( this.m_obj );

				this.m_perform = JR3DCanvasController.this.initializeActiveHandlers( new MMJREvent( e.getPointSet(), e.getIndex(), new double[] { 0, 0, 0 }, MMJREvent.POINT_DRAG_EVENT ) ) > 0;
			}

			public void pointDragged( PointDragEvent e ) {
				if ( this.m_perform ) {
					double[] translation = ( double[] ) e.getPosition().clone();

					for ( int i = 0; i < 3; i++ ) {
						translation[i] -= this.m_position[i];
					}
					this.m_position = e.getPosition();

					JR3DCanvasController.this.controlAction( new MMJREvent( e.getPointSet(), e.getIndex(), translation, MMJREvent.POINT_DRAG_EVENT ) );
				}
			}
		} );

		objectDragTool.addLineDragListener( new LineDragListener() {

			private MMCanvasObjectIF m_obj;
			private boolean m_perform;

			private double[] m_translation;

			public void lineDragEnd( LineDragEvent e ) {}

			public void lineDragStart( LineDragEvent e ) {
				System.out.println( "Line Drag" );

				this.m_translation = new double[] { 0, 0, 0, 1 };

				this.m_obj = ( ( MMJR3DCanvas ) JR3DCanvasController.this.m_canvas ).getGeometryOwner( e.getIndexedLineSet() );
				JR3DCanvasController.this.setSelectedObject( this.m_obj );

				this.m_perform = JR3DCanvasController.this.initializeActiveHandlers( new MMJREvent( e.getIndexedLineSet(), e.getIndex(), new double[] { 0, 0, 0 }, MMJREvent.LINE_DRAG_EVENT ) ) > 0;
			}

			public void lineDragged( LineDragEvent e ) {

				if ( this.m_perform ) {
					double[] tmp = ( double[] ) e.getTranslation().clone();

					for ( int i = 0; i < 3; i++ ) {
						tmp[i] = e.getTranslation()[i] - this.m_translation[i];
					}
					this.m_translation = e.getTranslation();

					JR3DCanvasController.this.controlAction( new MMJREvent( e.getIndexedLineSet(), e.getIndex(), tmp, MMJREvent.LINE_DRAG_EVENT ) );
				}
			}
		} );

		objectDragTool.addFaceDragListener( new FaceDragListener() {

			private MMCanvasObjectIF m_obj;
			private boolean m_perform;

			private double[] m_translation;

			public void faceDragEnd( FaceDragEvent e ) {}

			public void faceDragStart( FaceDragEvent e ) {
				System.out.println( "Face Drag" );

				this.m_translation = new double[] { 0, 0, 0, 1 };

				this.m_obj = ( ( MMJR3DCanvas ) JR3DCanvasController.this.m_canvas ).getGeometryOwner( e.getIndexedFaceSet() );
				JR3DCanvasController.this.setSelectedObject( this.m_obj );

				this.m_perform = JR3DCanvasController.this.initializeActiveHandlers( new MMJREvent( e.getIndexedFaceSet(), e.getIndex(), new double[] { 0, 0, 0 }, MMJREvent.FACE_DRAG_EVENT ) ) > 0;
			}

			public void faceDragged( FaceDragEvent e ) {
				if ( this.m_perform ) {
					double[] tmp = ( double[] ) e.getTranslation().clone();

					for ( int i = 0; i < 3; i++ ) {
						tmp[i] = e.getTranslation()[i] - this.m_translation[i];
					}
					this.m_translation = e.getTranslation();

					JR3DCanvasController.this.controlAction( new MMJREvent( e.getIndexedFaceSet(), e.getIndex(), tmp, MMJREvent.FACE_DRAG_EVENT ) );
				}
			}
		} );

		this.m_tools = new Tool[] { objectDragTool };
	}

	private int initializeActiveHandlers( MMEvent e ) {
		this.m_activeHandlers.clear();

		if ( this.m_enabled && e != null ) {
			if ( !this.m_listOfSelectedObjects.isEmpty() ) {
//				for ( Iterator<MMCanvasObjectIF> it = this.m_listOfSelectedObjects.iterator(); it.hasNext(); ) {
//					MMCanvasObjectIF current = it.next();
//					for ( Iterator<?> handlers = current.getHandlers().iterator(); handlers.hasNext(); ) {
				for ( Iterator it = this.m_listOfSelectedObjects.iterator(); it.hasNext(); ) {
					MMCanvasObjectIF current = (MMCanvasObjectIF)it.next();
					for ( Iterator handlers = current.getHandlers().iterator(); handlers.hasNext(); ) {
						MMHandler handler = ( MMHandler ) handlers.next();
						if ( handler.dealsWith( e ) ) {
							this.m_activeHandlers.add( handler );
						}
					}
				}
			}
		}
		return this.m_activeHandlers.size();
	}

	public void controlAction( MMEvent event ) {
		if ( !this.m_activeHandlers.isEmpty() ) {
//			for ( Iterator<?> handlers = this.m_activeHandlers.iterator(); handlers.hasNext(); ) {
			for ( Iterator handlers = this.m_activeHandlers.iterator(); handlers.hasNext(); ) {
				MMHandler handler = ( MMHandler ) handlers.next();
				if ( handler.doAction( event ) ) {
					handler.finish();
				}
			}
		}
	}

	public int getPreselectedObjectCount() {
		return this.m_listOfSelectedObjects.size();
	}

	public void addPreSelectedObject( MMCanvasObjectIF object ) {
		if ( object != null && !this.m_listOfSelectedObjects.contains( object ) ) {
			this.m_listOfSelectedObjects.add( object );
		}
	}

	public MMCanvasObjectIF getSelectedObject() {
		return this.m_selectedObject;
	}

	public void setSelectedObject( MMCanvasObjectIF obj ) {
//		List<MMCanvasObjectIF> oldSelection = Collections.emptyList();
		List oldSelection = Collections.EMPTY_LIST;
		if ( obj == null || !this.m_listOfSelectedObjects.contains( obj ) ) {
//			oldSelection = new ArrayList<MMCanvasObjectIF>();
			oldSelection = new ArrayList();
			oldSelection.addAll( this.m_listOfSelectedObjects );
			this.m_listOfSelectedObjects.clear();
		}

		if ( obj != null && obj.isSelectable() ) {
			this.addPreSelectedObject( obj );
			this.m_selectedObject = obj;
			this.m_selectedObject.render();
		} else {
			this.m_selectedObject = null;
		}

		if ( !oldSelection.isEmpty() ) {
//			for ( Iterator<MMCanvasObjectIF> it = oldSelection.iterator(); it.hasNext(); ) {
//				it.next().render();
			for ( Iterator it = oldSelection.iterator(); it.hasNext(); ) {
				((MMCanvasObjectIF)it.next()).render();
			}
		}
	}

	public boolean isEnabled() {
		return this.m_enabled;
	}

	public void setEnabled( boolean aFlag ) {
		this.m_enabled = aFlag;
	}

	public MMCanvas getCanvas() {
		return this.m_canvas;
	}

	public void setCanvas( MMCanvas aCanvas ) {
		if ( this.m_canvas == null ) {
			this.m_canvas = aCanvas;

			this.m_canvas.getDrawingBoard().addMouseListener( this );
			this.m_canvas.getDrawingBoard().addMouseMotionListener( this );
			this.m_canvas.getDrawingBoard().addMouseWheelListener( this );
			this.m_canvas.getDrawingBoard().addKeyListener( this );

			if ( this.m_canvas instanceof MMJR3DCanvas ) {
				( ( MMJR3DCanvas ) this.m_canvas ).addTools( this.getTools(), false );
			}

			this.setEnabled( true );
		}
	}

	public Tool[] getTools() {
		return this.m_tools;
	}

	public Object clone() {
		try {
			return super.clone();
		} catch ( CloneNotSupportedException e ) {
			e.printStackTrace();
		}
		return null;
	}

	public int getLastClickedX() {
		return this.m_lastClickedX;
	}

	public int getLastClickedY() {
		return this.m_lastClickedY;
	}

	public int getLastPressedX() {
		return this.m_lastPressedX;
	}

	public int getLastPressedY() {
		return this.m_lastPressedY;
	}

	public void mouseClicked( MouseEvent e ) {
		this.m_lastClickedX = e.getX();
		this.m_lastClickedY = e.getY();
	}

	public void mouseEntered( MouseEvent e ) {}

	public void mouseExited( MouseEvent e ) {}

	public void mousePressed( MouseEvent e ) {
		this.m_lastPressedX = e.getX();
		this.m_lastPressedY = e.getY();
	}

	public void mouseReleased( MouseEvent e ) {}

	public void mouseDragged( MouseEvent e ) {}

	public void mouseMoved( MouseEvent e ) {}

	public void mouseWheelMoved( MouseWheelEvent e ) {}

	public void keyPressed( KeyEvent e ) {}

	public void keyReleased( KeyEvent e ) {}

	public void keyTyped( KeyEvent e ) {}

	public void finishAction() {}

	public void flashObject( MMCanvasObjectIF object ) {}

	public void flashObjects( MMCanvasObjectIF[] objects ) {}

	public void flashObjects( MMCanvasObjectIF[] objects, long duration ) {}

	public void flashObjects( MMCanvasObjectIF[] objects, long duration, int flashNum ) {}
}
