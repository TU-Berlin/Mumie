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

package net.mumie.mathletfactory.display;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.mumie.mathletfactory.action.CanvasControllerIF;
import net.mumie.mathletfactory.action.DefaultCanvasController;
import net.mumie.mathletfactory.action.handler.global.GlobalKeyboard3DRotateXYHandler;
import net.mumie.mathletfactory.action.handler.global.GlobalKeyboard3DRotateZHandler;
import net.mumie.mathletfactory.action.handler.global.GlobalMouse3DRotateHandler;
import net.mumie.mathletfactory.action.handler.global.GlobalMouse3DScaleHandler;
import net.mumie.mathletfactory.action.handler.global.GlobalMouse3DTranslateHandler;
import net.mumie.mathletfactory.action.handler.global.GlobalMouseWheel3DScaleHandler;
import net.mumie.mathletfactory.appletskeleton.util.ControlPanel;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.util.Affine3DDouble;
import net.mumie.mathletfactory.math.util.AffineDouble;
import net.mumie.mathletfactory.transformer.CanvasObjectTransformer;
import net.mumie.mathletfactory.util.CanvasMessage;
import net.mumie.mathletfactory.util.Graphics2DUtils;
import net.mumie.mathletfactory.util.ResourceManager;


/**
 * This class acts as a base class for all 3 dimensional rendering.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */

public abstract class MM3DCanvas extends MMCanvas {

	private static String m_default3DScreenType;
	
	/** The transform type of this canvas. Should be overriden by subclass. */
	protected int m_transformType = CanvasObjectTransformer.NO_TRANSFORM_TYPE;

	protected double[] m_viewDirection = new double[3];

//	private final static double[] X_DIRECTION = new double[] { 1, 0, 0 };
//	private final static double[] Y_DIRECTION = new double[] { 0, 1, 0 };
//	private final static double[] Z_DIRECTION = new double[] { 0, 0, 1 };
//	private final static double[] INITIAL_DIRECTION = new double[] { 0, 0, -1 };

	private Affine3DDouble m_world2Screen = new Affine3DDouble();
	private Affine3DDouble m_screen2World = null;
	private Affine3DDouble m_worldViewTrafo = new Affine3DDouble();
	private Affine3DDouble m_worldViewInverseTrafo = new Affine3DDouble();

	protected CanvasMessage m_fpsMessage;

	private JPanel m_centerPane;

	ControlPanel cp = new ControlPanel();
	
	private Panel m_canvasPane = new Panel();

	private Point m_toolBarLoc = new Point();

	private DecimalFormat m_posFormat = new DecimalFormat( "###.#" );
	
	  private boolean antiAliasingEnabled = true;
	  
	  protected JCheckBox antiAliasingCheckBox = new JCheckBox("antiAliasing");
	  
	public MM3DCanvas() {
		this( new DefaultCanvasController() );
	}
	
	public MM3DCanvas( CanvasControllerIF controller ) {
		super( controller );

		new GlobalMouse3DTranslateHandler( this );
		new GlobalMouse3DRotateHandler( this );
		new GlobalMouse3DScaleHandler( this );
		new GlobalKeyboard3DRotateZHandler( this );
		new GlobalKeyboard3DRotateXYHandler( this );
		new GlobalMouseWheel3DScaleHandler(this);
		// new GlobalMousePopupHandler(this);
		setLookAt( new double[] { 0, -2, 0 }, new double[] { 0, 1, 0 }, new double[] { 0, 0, 1 } );

		setToolbarVisible( true );
		setScrollButtonsVisible( true );
		m_toolFlags |= HORIZONTAL_TOOLBAR;
		m_fpsMessage = new CanvasMessage( this );
		addMessage( m_fpsMessage );
	}

	/**
	 * Returns the transformation from (3D) world coordinates to (2d) screen
	 * coordinates.
	 */
	public Affine3DDouble getWorld2Screen() {
		return m_world2Screen;
	}

	/**
	 * Sets the transformation from screen coordinates to world coordinates.
	 * Since screen coordinates are twodimensional, the z value is determined by
	 * using a point in the "screen plane", i.e. where the screen on which the
	 * scene is projected is located the world.
	 */
	public void setScreen2World( Affine3DDouble aFastTransformation ) {
		m_screen2World = aFastTransformation;
		adjustTransformations();
	}

	/**
	 * Returns the transformation from screen coordinates to world coordinates.
	 * 
	 * @see #setScreen2World
	 */
	public Affine3DDouble getScreen2World() {
		return m_screen2World;
	}

	/**
	 * Calculates the screen to world transformation as the inverse of the world
	 * to screen transformation.
	 */
	protected abstract void calculateScreen2World();

	public void adjustTransformations() {
		if ( m_screen2World != null ) m_screen2World.putInverseOfMeInto( m_world2Screen );
	}

	/**
	 * Returns the transformation that transforms object (world) coordinates to
	 * view (world) coordinates by positioning the camera in the origin facing
	 * towards the z-axis. projectionTrafo * worldViewTrafo =
	 * worldDraw2NormalizedScreen
	 */
	public Affine3DDouble getWorld2WorldView() {
		return m_worldViewTrafo;
	}

	/**
	 * Returns the transformation that transforms view (world) coordinates to
	 * object (world) coordinates (the inverse of {@link #getWorld2WorldView}.
	 */
	public Affine3DDouble getWorldView2World() {
		m_worldViewTrafo.putInverseOfMeInto( m_worldViewInverseTrafo );
		return m_worldViewInverseTrafo;
	}

	/**
	 * Sets the transformation that transforms view (world) coordinates to
	 * object (world) coordinates (the inverse of {@link #getWorld2WorldView}.
	 */
	public void setWorld2WorldView( Affine3DDouble trafo ) {
		m_worldViewTrafo.set( trafo );
		calculateScreen2World();
		adjustTransformations();
		// System.out.println("world2worldView = "+m_worldViewTrafo);
		// if(getViewDirection() != null)
		// System.out.println(AffineDouble.printAsPoint(getViewDirection()));
	}

	/**
	 * Sets the transformation that transforms view (world) coordinates to
	 * object (world) coordinates by giving the viewers position and two
	 * vectors, where the first is the direction into which the viewer looks.
	 * The second is the vector that points "upwards" for the viewer.
	 */
	public void setLookAt( double[] position, double[] direction, double[] upwards ) {
		setViewerPosition( position );
		setViewDirection( direction, upwards );
	}

	/**
	 * Sets the transformation that transforms view (world) coordinates to
	 * object (world) coordinates by giving two vectors, where the first is the
	 * direction into which the viewer looks. The second is the vector that
	 * points "upwards" for the viewer.
	 */
	public void setViewDirection( double[] direction, double[] upwards ) {

		double[] position = getViewerPosition();
		double[] right = Affine3DDouble.crossProduct( direction, upwards );
		upwards = Affine3DDouble.crossProduct( right, direction );

		Affine3DDouble.normalize( right );
		Affine3DDouble.normalize( direction );
		Affine3DDouble.normalize( upwards );

		Affine3DDouble lookAtTransformation = new Affine3DDouble();
		lookAtTransformation.set( 0, 0, right[0] );
		lookAtTransformation.set( 1, 0, right[1] );
		lookAtTransformation.set( 2, 0, right[2] );
		lookAtTransformation.set( 0, 1, upwards[0] );
		lookAtTransformation.set( 1, 1, upwards[1] );
		lookAtTransformation.set( 2, 1, upwards[2] );
		lookAtTransformation.set( 0, 2, -direction[0] );
		lookAtTransformation.set( 1, 2, -direction[1] );
		lookAtTransformation.set( 2, 2, -direction[2] );
		lookAtTransformation.set( 0, 3, position[0] );
		lookAtTransformation.set( 1, 3, position[1] );
		lookAtTransformation.set( 2, 3, position[2] );

		setWorld2WorldView( lookAtTransformation );
	}

	/**
	 * Itself called by the system after a issue of <code>repaint()</code>,
	 * this method simply calls the rendering routine {@link #renderScene()}.
	 * It may also used internally for calculating the Frames per second (fps).
	 */
	public void paintComponent( Graphics g ) {
		if ( m_calculateFPS ) startFrameRateCounter();
		renderScene();
		drawScene();
		super.paintComponent( g );
		if ( m_calculateFPS ) {
			stopFrameRateCounter();
			m_fpsMessage.setMessage( ( int ) getFrameRate() + " fps" );
		}
	}

	/**
	 * Additionally displays the coordinates of the viewers position to the
	 * right of the toolbar.
	 */
	public void paint( Graphics g ) {
		super.paint( g );
		m_toolBar.getLocation( m_toolBarLoc );
		if (m_toolBar.isVisible()) g.drawString( "Pos: " + AffineDouble.printAsPoint( getViewerPosition(), m_posFormat ), m_toolBarLoc.x + m_toolBar.getWidth() + 20, m_toolBarLoc.y + m_toolBar.getHeight() / 2 );
		// g.drawString("Dir: "+AffineDouble.printAsPoint(getViewDirection(),
		// m_posFormat),
		// m_toolBarLoc.x + m_toolBar.getWidth() + 150,
		// m_toolBarLoc.y + m_toolBar.getHeight()/2);
	}

	/**
	 * Returns the position of the view platform in world coordinates.
	 */
	public double[] getViewerPosition() {
		// System.out.println("viewer position is
		// "+m_position[0]+","+m_position[1]+","+m_position[2]);
		return m_worldViewTrafo.getTranslation();
	}

	/**
	 * Sets the position of the view platform in world coordinates.
	 */
	public void setViewerPosition( NumberTuple position ) {
		setViewerPosition( position.toDoubleArray() );
	}

	/**
	 * Sets the position of the view platform in world coordinates.
	 */
	public void setViewerPosition( double[] position ) {
		setWorld2WorldView( getWorld2WorldView().setTranslation( position ) );
	}

	/**
	 * Sets the orientation of the view platform in world coordinates. This
	 * method should only be called by the system, since it may be overwritten
	 * otherwise.
	 */
	public void setViewerOrientation( Affine3DDouble orientation ) {
		setWorld2WorldView( getWorld2WorldView().setDeformationPart( orientation ) );
	}

	/**
	 * Returns the (world) direction of the view platform as a normalized
	 * vector.
	 */
	public double[] getViewDirection() {
		return m_viewDirection;
	}

	public void addSnapShotToHistory() {
		getWorld2Screen().addSnapshotToHistory();
	}

	protected void createComponents() {
		m_toolBar.setFloatable( false );
		m_toolBar.setFocusable( false );
		m_toolBar.setVisible( ( m_toolFlags & ( HORIZONTAL_TOOLBAR | VERTICAL_TOOLBAR ) ) != 0 );
		// && (m_toolFlags & (HORIZONTAL_TOOLBAR | VERTICAL_TOOLBAR)) != 0);
		if ( Graphics2DUtils.isJMFAvailable() && ( m_toolFlags & VERTICAL_TOOLBAR ) == 0 ) {
			try {
				Class clazz = Class.forName( "net.mumie.mathletfactory.util.JMFHelper" );
				Method method = clazz.getDeclaredMethod( "createRecordAction", new Class[] { Component.class } );
				Action action = ( Action ) method.invoke( null, new Object[] { getDrawingBoard() } );
				createToolbarButton( action, action.isEnabled() );
				method = clazz.getDeclaredMethod( "createStopAction", new Class[] { Action.class } );
				action = ( Action ) method.invoke( null, new Object[] { action } );
				createToolbarButton( action, action.isEnabled() );
				m_toolBar.addSeparator();
			} catch ( Exception e ) {
				Logger.getLogger( "" ).severe( e.getMessage() );
				// doesn't matter
			}
		}
		if ( ( m_toolFlags & VERTICAL_TOOLBAR ) == 0 ) {
			final JButton btnPrev = createToolbarButton( new AbstractAction( "", new ImageIcon( getClass().getResource( "/resource/icon/canvas/Back24.gif" ) ) ) {

				public void actionPerformed( ActionEvent e ) {
					Affine3DDouble afg = getWorld2WorldView();
					afg.getHistory().prev();
					afg.setFromHistory();
					setWorld2WorldView( afg );
					updateCanvas();
				}
			}, true );
			btnPrev.setToolTipText( ResourceManager.getMessage( "hist_backward" ) );
			final JButton btnNext = createToolbarButton( new AbstractAction( "", new ImageIcon( getClass().getResource( "/resource/icon/canvas/Forward24.gif" ) ) ) {

				public void actionPerformed( ActionEvent e ) {
					Affine3DDouble afg = getWorld2WorldView();
					afg.getHistory().next();
					afg.setFromHistory();
					setWorld2WorldView( afg );
					updateCanvas();
				}
			}, true );
			btnNext.setToolTipText( ResourceManager.getMessage( "hist_forward" ) );

			getWorld2WorldView().getHistory().addChangeListener( new ChangeListener() {

				public void stateChanged( ChangeEvent e ) {
					// System.out.println("changed:
					// "+getWorld2WorldView().getHistory().isFirst()+","+getWorld2WorldView().getHistory().isLast());
					btnPrev.setEnabled( !getWorld2WorldView().getHistory().isFirst() );
					btnNext.setEnabled( !getWorld2WorldView().getHistory().isLast() );
				}
			} );

			m_toolBar.addSeparator();
		}
		final JButton btnZoomIn = createToolbarButton( new AbstractAction( "", new ImageIcon( getClass().getResource( "/resource/icon/canvas/ZoomIn24.gif" ) ) ) {

			public void actionPerformed( ActionEvent e ) {
				Affine3DDouble afg = getWorld2WorldView();
				double[] translationV = new double[] { 0, 0, -0.2 };
				afg.applyDeformationPartTo( translationV );
				afg.leftTranslate( translationV );
				setWorld2WorldView( afg );

				updateCanvas();
				getWorld2WorldView().addSnapshotToHistory();
			}
		}, true );
		btnZoomIn.setToolTipText( ResourceManager.getMessage( "zoom_in" ) );
		final JButton btnZoomOut = createToolbarButton( new AbstractAction( "", new ImageIcon( getClass().getResource( "/resource/icon/canvas/ZoomOut24.gif" ) ) ) {

			public void actionPerformed( ActionEvent e ) {
				Affine3DDouble afg = getWorld2WorldView();
				double[] translationV = new double[] { 0, 0, 0.2 };
				afg.applyDeformationPartTo( translationV );
				afg.leftTranslate( translationV );
				setWorld2WorldView( afg );
				updateCanvas();
				getWorld2WorldView().addSnapshotToHistory();
			}
		}, true );
		btnZoomOut.setToolTipText( ResourceManager.getMessage( "zoom_out" ) );

		final JButton btnRotate = createToolbarButton( new AbstractAction( "", new ImageIcon( getClass().getResource( "/resource/icon/canvas/rotateLeft.png" ) ) ) {

			public void actionPerformed( ActionEvent e ) {
				Affine3DDouble afg = getWorld2WorldView();
				double[] axis = new double[] { 0, 1, 0 };
				afg.applyDeformationPartTo( axis );
				Affine3DDouble rotation = new Affine3DDouble();
				rotation.setToRotation( axis, Math.PI / 24 );

				Affine3DDouble.compose( rotation, afg );

				setWorld2WorldView( rotation );
				updateCanvas();
				getWorld2WorldView().addSnapshotToHistory();
			}
		}, true );
		btnRotate.setToolTipText( ResourceManager.getMessage( "rotate_vertical" ) );
		final JButton btnAntiRotate = createToolbarButton( new AbstractAction( "", new ImageIcon( getClass().getResource( "/resource/icon/canvas/rotateRight.png" ) ) ) {

			public void actionPerformed( ActionEvent e ) {
				Affine3DDouble afg = getWorld2WorldView();
				double[] axis = new double[] { 0, 1, 0 };
				afg.applyDeformationPartTo( axis );
				Affine3DDouble rotation = new Affine3DDouble();
				rotation.setToRotation( axis, -Math.PI / 24 );

				Affine3DDouble.compose( rotation, afg );

				setWorld2WorldView( rotation );
				updateCanvas();
				getWorld2WorldView().addSnapshotToHistory();
			}
		}, true );
		btnAntiRotate.setToolTipText( ResourceManager.getMessage( "rotate_vertical" ) );
		final JButton btnRotateUp = createToolbarButton( new AbstractAction( "", new ImageIcon( getClass().getResource( "/resource/icon/canvas/rotateUp.png" ) ) ) {

			public void actionPerformed( ActionEvent e ) {
				Affine3DDouble afg = getWorld2WorldView();
				double[] axis = new double[] { 1, 0, 0 };
				afg.applyDeformationPartTo( axis );
				Affine3DDouble rotation = new Affine3DDouble();
				rotation.setToRotation( axis, Math.PI / 24 );

				Affine3DDouble.compose( rotation, afg );

				setWorld2WorldView( rotation );
				updateCanvas();
				getWorld2WorldView().addSnapshotToHistory();
			}
		}, true );
		btnRotateUp.setToolTipText( ResourceManager.getMessage( "rotate_horizontal" ) );
		final JButton btnRotateDown = createToolbarButton( new AbstractAction( "", new ImageIcon( getClass().getResource( "/resource/icon/canvas/rotateDown.png" ) ) ) {

			public void actionPerformed( ActionEvent e ) {
				Affine3DDouble afg = getWorld2WorldView();
				double[] axis = new double[] { 1, 0, 0 };
				afg.applyDeformationPartTo( axis );
				Affine3DDouble rotation = new Affine3DDouble();
				rotation.setToRotation( axis, -Math.PI / 24 );

				Affine3DDouble.compose( rotation, afg );

				setWorld2WorldView( rotation );
				updateCanvas();
				getWorld2WorldView().addSnapshotToHistory();
			}
		}, true );
		btnRotateDown.setToolTipText( ResourceManager.getMessage( "rotate_horizontal" ) );

		m_btnScrollUp = createScrollButton( new AbstractAction( "", new ImageIcon( getClass().getResource( "/resource/icon/canvas/up.png" ) ) ) {

			public void actionPerformed( ActionEvent e ) {
				Affine3DDouble afg = getWorld2WorldView();
				double[] translationV = new double[] { 0, 0.1, 0 };
				afg.applyDeformationPartTo( translationV );
				afg.leftTranslate( translationV );
				setWorld2WorldView( afg );
				updateCanvas();
				getWorld2WorldView().addSnapshotToHistory();
			}
		}, new Insets( 5, 0, 0, 0 ), ( m_toolFlags & SCROLL_BUTTONS ) != 0 );
		// m_btnScrollUp.setToolTipText(ResourceManager.getMessage("scroll_up"));
		m_btnScrollDown = createScrollButton( new AbstractAction( "", new ImageIcon( getClass().getResource( "/resource/icon/canvas/down.png" ) ) ) {

			public void actionPerformed( ActionEvent e ) {
				Affine3DDouble afg = getWorld2WorldView();
				double[] translationV = new double[] { 0, -0.1, 0 };
				afg.applyDeformationPartTo( translationV );
				afg.leftTranslate( translationV );
				setWorld2WorldView( afg );
				updateCanvas();
				getWorld2WorldView().addSnapshotToHistory();
			}
		}, new Insets( 0, 0, 5, 0 ), ( m_toolFlags & SCROLL_BUTTONS ) != 0 );
		// m_btnScrollDown.setToolTipText(ResourceManager.getMessage("scroll_down"));
		m_btnScrollLeft = createScrollButton( new AbstractAction( "", new ImageIcon( getClass().getResource( "/resource/icon/canvas/left.png" ) ) ) {

			public void actionPerformed( ActionEvent e ) {
				Affine3DDouble afg = getWorld2WorldView();
				double[] translationV = new double[] { -0.1, 0, 0 };
				afg.applyDeformationPartTo( translationV );
				afg.leftTranslate( translationV );
				setWorld2WorldView( afg );
				updateCanvas();
				getWorld2WorldView().addSnapshotToHistory();
			}
		}, new Insets( 0, 5, 0, 0 ), ( m_toolFlags & SCROLL_BUTTONS ) != 0 );
		// m_btnScrollLeft.setToolTipText(ResourceManager.getMessage("scroll_left"));
		m_btnScrollRight = createScrollButton( new AbstractAction( "", new ImageIcon( getClass().getResource( "/resource/icon/canvas/right.png" ) ) ) {

			public void actionPerformed( ActionEvent e ) {
				int inc = ( ( ExtendedButtonModel ) m_btnScrollUp.getModel() ).getIncrement();
				Affine3DDouble afg = getWorld2WorldView();
				double[] translationV = new double[] { 0.1, 0, 0 };
				afg.applyDeformationPartTo( translationV );
				afg.leftTranslate( translationV );
				setWorld2WorldView( afg );
				updateCanvas();
				getWorld2WorldView().addSnapshotToHistory();
			}
		}, new Insets( 0, 0, 0, 5 ), ( m_toolFlags & SCROLL_BUTTONS ) != 0 );
		// m_btnScrollRight.setToolTipText(ResourceManager.getMessage("scroll_right"));*/
	}

	protected void layoutComponents() {
		setLayout( new BorderLayout() );
		m_canvasPane.setLayout( new BorderLayout() );
		m_canvasPane.setBackground( Color.BLACK );
		m_canvasPane.add( getDrawingBoard(), BorderLayout.CENTER );
		m_canvasPane.addComponentListener( this );
		m_centerPane = new JPanel( new BorderLayout() );
		m_centerPane.add( m_btnScrollUp, BorderLayout.NORTH );
		m_centerPane.add( m_btnScrollDown, BorderLayout.SOUTH );
		m_centerPane.add( m_btnScrollLeft, BorderLayout.WEST );
		m_centerPane.add( m_btnScrollRight, BorderLayout.EAST );
		m_centerPane.add( m_canvasPane, BorderLayout.CENTER );
//		Box allBox, toolBox;
		Box toolBox;
		
	    cp.setLeftAlignment();
	    if((m_toolFlags & VERTICAL_TOOLBAR) != 0){
//	      allBox = Box.createHorizontalBox();
	      toolBox = Box.createVerticalBox();
	      toolBox.add(m_toolBar);
	      toolBox.add(Box.createVerticalGlue());
	      m_toolBar.setOrientation(JToolBar.VERTICAL);
	    } else { // per default toolBar is horizontal
//	      allBox = Box.createVerticalBox();
	      toolBox = Box.createHorizontalBox();
	      toolBox.add(m_toolBar);
	      toolBox.add(Box.createHorizontalGlue());
	    }
	    antiAliasingCheckBox.setSelected(antiAliasingEnabled);
	    cp.add(toolBox);
	    cp.insertLineBreak();
	    cp.add(antiAliasingCheckBox);
//	    allBox.add(cp);
//	    //allBox.add(new JButton("test"));
//	    allBox.add(m_centerPane);
	    add(cp,BorderLayout.NORTH);
	    add(m_centerPane, BorderLayout.CENTER);
	}

	/**
	 * Called by
	 * {@link net.mumie.mathletfactory.action.handler.global.Global3DHandler}.
	 */
	public abstract void globalHandlerFinished();

	public void componentResized( ComponentEvent e ) {
		// System.out.println("resizing from "+getDrawingBoard().getSize()+"to
		// "+m_canvasPane.getSize()+" due to "+e.getSource());
		if ( m_centerPane.getHeight() + m_toolBar.getHeight() > getParent().getHeight() )
			m_centerPane.setSize( m_centerPane.getSize().width, ( int ) ( getParent().getHeight() - m_toolBar.getHeight() ) );
		if ( m_canvasPane.getHeight() > m_centerPane.getHeight() )
			m_canvasPane.setSize( m_canvasPane.getSize().width, ( int ) ( m_centerPane.getSize().height - 2 * m_btnScrollDown.getPreferredSize().getHeight() ) );

		getDrawingBoard().setBounds( 0, 0, m_canvasPane.getBounds().width, m_canvasPane.getBounds().height );

		if ( m_centerPane.getHeight() > getHeight() - 2 * m_btnScrollDown.getPreferredSize().getHeight() ) m_centerPane.revalidate();
		getWorld2WorldView().getHistory().clear();
		getWorld2WorldView().addSnapshotToHistory();
		renderFromWorldDraw();
		m_btnScrollDown.repaint();
		// System.out.println("resizing done:
		// db="+getDrawingBoard().getHeight()+",
		// cap="+m_canvasPane.getHeight()+", cep="+m_centerPane.getHeight()+
		// ", this="+getHeight()+", p="+getParent().getHeight()+",
		// p^3="+getParent().getParent().getParent()+",
		// "+getParent().getParent().getParent().getHeight());
	}

	// empty because of problems with j3d canvas with hiding the toolbar
	public void reloadProperties() {
		
	}
	
	public void setToolbarVisible(boolean b){
		if (b == isToolbarVisible()){
//			System.out.println("Tu nichts, Toolbar ist schon visible:"+isToolbarVisible());
			return;
		}
		super.setToolbarVisible(b);
		//Bedingung: Toolbar ist vorhanden
		if (!m_toolBar.isVisible()){
			remove(cp);
//			System.out.println("Entferne Toolbar, cp.getComponentCount():"+cp.getComponentCount());
		}else{
			add(cp,BorderLayout.NORTH);
//			System.out.println("Fuege Toolbar hinzu, cp.getComponentCount():"+cp.getComponentCount());
		}
		
	}
	
	 public void setAntiAliasingEnabled(boolean s){
		  antiAliasingCheckBox.setSelected(s);
	  }
	  /**
	   * this method is reserved so that the applet programmer can place 
	   * the checkbox in a different location different than the default one
	   * that is described in layoutComponents()
	   */
	  public JCheckBox getAntiAliasingCheckBox(){
		  return antiAliasingCheckBox;
	  }
	  
  public static MM3DCanvas createCanvas3D() {
  	if(m_default3DScreenType == null)
  		throw new RuntimeException("Default 3D screen type was not defined !");
  	return (MM3DCanvas) MMCanvas.createCanvas(m_default3DScreenType);
  }
  
  public static void setDefault3DScreenType(String screenTypeName) {
  	m_default3DScreenType = screenTypeName;
  }
  
  public static String getDefault3DScreenType() {
  	return m_default3DScreenType;
  }
  public void setAntiAliasingCheckBoxEnabled(boolean s){
		antiAliasingCheckBox.setVisible(s);
  }
}
