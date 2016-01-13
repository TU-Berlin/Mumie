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

package net.mumie.mathletfactory.display.jr3d;


import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.mumie.mathletfactory.action.CanvasControllerIF;
import net.mumie.mathletfactory.action.handler.global.GlobalHandler;
import net.mumie.mathletfactory.action.jr3d.JR3DCanvasController;
import net.mumie.mathletfactory.appletskeleton.BaseApplet;
import net.mumie.mathletfactory.display.MM3DCanvas;
import net.mumie.mathletfactory.display.MMCanvas;
import net.mumie.mathletfactory.display.j3d.MMJ3DCanvas;
import net.mumie.mathletfactory.math.util.Affine3DDouble;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;
import net.mumie.mathletfactory.transformer.GeneralTransformer;
import net.mumie.mathletfactory.transformer.jr3d.JR3DCanvasTransformer;
import net.mumie.mathletfactory.util.CanvasImage;
import net.mumie.mathletfactory.util.Graphics2DUtils;
import net.mumie.mathletfactory.util.History;
import net.mumie.mathletfactory.util.ResourceManager;
import net.mumie.mathletfactory.util.logging.LogCategory;
import net.mumie.mathletfactory.util.logging.MumieLogger;
import de.jreality.io.JrScene;
import de.jreality.io.JrSceneFactory;
import de.jreality.math.Matrix;
import de.jreality.math.MatrixBuilder;
import de.jreality.scene.Appearance;
import de.jreality.scene.DirectionalLight;
import de.jreality.scene.Geometry;
import de.jreality.scene.IndexedFaceSet;
import de.jreality.scene.IndexedLineSet;
import de.jreality.scene.Light;
import de.jreality.scene.PointLight;
import de.jreality.scene.PointSet;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.SpotLight;
import de.jreality.scene.Transformation;
import de.jreality.scene.tool.Tool;
import de.jreality.shader.CommonAttributes;
import de.jreality.shader.RenderingHintsShader;
import de.jreality.shader.ShaderUtility;
import de.jreality.tools.ClickWheelCameraZoomTool;
import de.jreality.tools.DragEventTool;
import de.jreality.tools.DraggingTool;
import de.jreality.tools.RotateTool;
import de.jreality.ui.viewerapp.ViewerApp;


/**
 * {@link MMJR3DCanvas} ist eine alternative 3D-Anzeigekomponente.<br>
 * <br>
 * JReality gibt den Entwicklern die Moeglichkeit eine 3D-Scene aus mehreren {@link SceneGraphComponent}s aufzubauen. <br>
 * Jeder dieser Knoten kann {@link Geometry} und {@link Light} als Information aufnehmen.<br>
 * <br>
 * Bei {@link Geometry} handelt es sich um Daten eines darzustellenden Objekts. <br>
 * {@link PointSet}, {@link IndexedLineSet} und {@link IndexedFaceSet} sind die moeglichen Varianten der {@link Geometry}.<br>
 * <br>
 * Zu jedem {@link MMCanvasObjectIF}s wird ein {@link SceneGraphComponent} erzeugt und in das Scene-Baum angehaengt.<br>
 * <br>
 * Es sollten nach Moeglichkeit zwischen Licht-Knoten und Geometrie-Knoten unterschieden werden (separate Speicherung).<br>
 * <br>
 * <br>
 * Folgende Eigenschaften der Komponente koennen veraendert werden:<br>
 * Position und Ausrichtung der Kamera {@link MMJR3DCanvas#setViewerPosition(double[])}, {@link MMJR3DCanvas#setViewDirection(double[], double[])}<br>
 * Lichteigenschaften wie Intensitaet und Farbe {@link MMJR3DCanvas#setLightProperties(JR3DCanvasLightProperties)}<br>
 * <br>
 * Allgemeine Einstellungen (Antialiasing, Hintergrundfarbe, Nebel) fuer {@link MMJR3DCanvas} steuerbar durch {@link MMJR3DCanvas#setViewProperties(JR3DCanvasViewerProperties)}<br>
 * <br>
 * 
 * @see MMJ3DCanvas
 * @see JR3DPointSetDrawable
 * @see JR3DLineSetDrawable
 * @see JR3DFaceSetDrawable
 * 
 * @author Juergen Weber
 */

public class MMJR3DCanvas extends MM3DCanvas {

	/**
	 * Logger der Klasse.
	 */
	private static final MumieLogger LOGGER = MumieLogger.getLogger( MMJR3DCanvas.class );

	private static final LogCategory TOOLS = LOGGER.getCategory( "jreality.tools" );
	private static final LogCategory COMPONENTS = LOGGER.getCategory( "canvas.components" );
	private static final LogCategory LIGHTS = LOGGER.getCategory( "canvas.lights" );
	private static final LogCategory HISTORY = LOGGER.getCategory( "canvas.history" );
	private static final LogCategory VIEW_PROPERTIES = LOGGER.getCategory( "canvas.properties" );
	private static final LogCategory SCENE_GRAPH = LOGGER.getCategory( "canvas.scenegraph" );

	/**
	 * Die Root-Knoten eines Scene-Baums tragen diesen Namen, soweit nicht anders definiert.<br>
	 * 
	 * @see #MMJRealityCanvas(String, CanvasControllerIF)
	 */
	public static final String NAME = "jrcanvas";

	/**
	 * Der oberste Knoten des Scene-Baums. An diesen werden die Lichter-Nodes und Camera angehaengt.<br>
	 * Wird entweder mit {@value #NAME} oder mit einem selbst definierten Namen benannt.
	 */
	protected SceneGraphComponent m_root;

	/**
	 * An diesen Knoten werden die globalen Tools (Drehen der Scene, Zoom) angehaengt.<br>
	 * Grund: man soll zwischen globalen und fuer die Manipulation der Objekte benoetigten Tools unterscheiden<br>
	 */
	protected SceneGraphComponent m_sceneContent;

	/**
	 * Dieser Knoten besitzt alle {@link SceneGraphComponent}s der {@link MMCanvasObjectIF}.<br>
	 * Die Objekte-Interaktionstools haengen an diesem Knoten.<br>
	 */
	protected SceneGraphComponent m_objectRoot;

	/**
	 * Die Transformation der ganzen Scene (Drehung).<br>
	 * Wird am Anfang erzeugt und dann entsprechend veraendert.<br>
	 * 
	 * @see #setViewDirection(double[], double[])
	 */
	protected Transformation m_worldTransformation;

	/**
	 * Die Transformation der Kamera.<br>
	 * Wird am Anfang erzeugt und spaeter entsprechend veraendert. (Verschiebung der Sicht, Zoom).<br>
	 * 
	 * @see #setViewerPosition(double[])
	 */
	protected Transformation m_cameraTransformation;

	/**
	 * Bestimmt, wie die Kamera gekippt wird.<br>
	 * 
	 * @see #setViewDirection(double[], double[])
	 * @see #createTransformationMatrix(double[], double[], double[])
	 */
	protected double[] m_cameraUpwards = new double[] { 0.0, 0.0, 1.0 };

	/**
	 * Die Liste aller Lichtquellen der Scene. <br>
	 * Wird benoetigt um die Eigenschaften der Lichter zu veraendern.<br>
	 * 
	 * @see #setLightProperties(JR3DCanvasLightProperties)
	 * @see #getLightNodes()
	 * @see #getLights()
	 */
//	protected List<SceneGraphComponent> m_lightNodes;
	protected List m_lightNodes;

	/**
	 * Die Anzeige-Komponente (von JReality erzeugte Componente).<br>
	 * 
	 * @see #getDrawingBoard()
	 */
	protected Component m_drawingBoard;

	/**
	 * History: Veraenderungen, welche mit den Canvas-Buttons verursacht wurden, koennen rueckgaengig gemacht werden.<br>
	 * {@link #m_history}[0] enthaelt Informationen ueber die Veraenderungen der Kamera.<br>
	 * {@link #m_history}[1] enthaelt Informationen ueber die Veraenderungen der Scene.<br>
	 */
	protected History[] m_history;

	/**
	 * Erzeugt eine neue 3D-Anzeige-Componente. <br>
	 * Die Behandlung der durch die Objekte erzeugten Ereignisse wird durch {@link JR3DCanvasController} uebernommen.<br>
	 * Der Name des Root-Knotens lautet {@value #NAME}
	 */
	public MMJR3DCanvas() {
		this( NAME, new JR3DCanvasController() );
	}

	/**
	 * Erzeugt eine neue 3D-Anzeige-Componente. <br>
	 * Der Name des Root-Knotens lautet {@value #NAME}
	 * 
	 * @param control
	 *            der uebergebene Controller soll die Behandlung der durch die Objekte erzeugten Ereignisse uebernommen.<br>
	 */
	public MMJR3DCanvas( CanvasControllerIF control ) {
		this( NAME, control );
	}

	/**
	 * Erzeugt eine neue 3D-Anzeige-Componente. <br>
	 * 
	 * @param control
	 *            der uebergebene Controller soll die Behandlung der durch die Objekte erzeugten Ereignisse uebernommen.<br>
	 * 
	 * @param name
	 *            Name des Root-Knotens
	 */
	public MMJR3DCanvas( String name, CanvasControllerIF control ) {
		// Initialisierung des drawingBoards, Veraendern der Kamera (viewDirection)
		super( control );

		// Name der Componente und zugleich Bennenung des Roots
		this.setName( name );

		// Inititalisieren der allgemeinen Anzeige-Eigenschaften
		this.initializeProperties();

		// History initialisieren (sonst kann es zu einer NullPointerException kommen)
		this.initializeHistory();

		// Erzeugen und Ausrichten der Buttons, Labels usw.
		this.createComponents();
		this.layoutComponents();

		// alle globalen Haendler werden entfernt, da JReality dazu die Tools mitliefert
		this.m_globalHandlers.removeAll( this.m_globalHandlers );
	}

	/**
	 * Name der Componente sowie Name des SceneGraph-Roots festlegen.<br>
	 * 
	 * @param name
	 *            Bennenung der Componente, wenn null, dann wird {@value #NAME} als Name verwendet.
	 */
	public void setName( String name ) {
		if ( name == null ) {
			name = MMJR3DCanvas.NAME;
		}
		LOGGER.log( COMPONENTS, "setting component name to '" + name + "' \n" );
		super.setName( name );
		LOGGER.log( SCENE_GRAPH, "setting root node name to '" + name + "' \n" );
		this.m_root.setName( name );
	}

	/**
	 * Laden der default-Einstellungen:<br>
	 * Eigenschaften aller Lichtquellen.<br>
	 * Werte fuer Antialiasing, Hintergrundfarbe, Nebel, Lichtverhalten.<br>
	 * 
	 * Verwendet werden dabei die defaults {@link JR3DCanvasLightProperties} und {@link JR3DCanvasViewerProperties}
	 */
	protected void initializeProperties() {
		LOGGER.log( VIEW_PROPERTIES, "canvas standard transformer type (screen type): " + this.getScreenType() );
		this.setLightProperties( new JR3DCanvasLightProperties() );
		this.setViewProperties( new JR3DCanvasViewerProperties() );
	}

	/**
	 * Setzen der allgemeinen Eigenschaften der Anzeige: Antialiasing, Hintergrundfarbe, Nebel, Lichtverhalten.<br>
	 * 
	 * @param p
	 *            Einstellungen fuer die Anzeige
	 */
	public void setViewProperties( JR3DCanvasViewerProperties p ) {
		LOGGER.log( VIEW_PROPERTIES, this.getName() + " - setting view properties:" );

		// Alle Einstellungen werden am Root-Knoten durchgefuert. Es ist prinzipiell
		// moeglich auch an anderen Knoten die hier verwendeten Einstellungen anzuwenden. In dem Fall sind aber alle
		// Knoten 'individuell', da aber die Anzeige einheitlich aussehen soll, sind die Werte fuer alle Knoten gleich umd im Root abgelegt.
		Appearance rootApp = this.m_root.getAppearance();

		RenderingHintsShader shader = ShaderUtility.createDefaultRenderingHintsShader( rootApp, true );
		LOGGER.log( VIEW_PROPERTIES, "lighting enabled: " + p.isLightingEnabled() );
//		shader.setLightingEnabled( p.isLightingEnabled() ); // Lichtverhalten, Schattenwurf (an, aus)
		shader.setLightingEnabled( new Boolean (p.isLightingEnabled() )); // Lichtverhalten, Schattenwurf (an, aus)

		LOGGER.log( VIEW_PROPERTIES, "antialiasing enabled: " + p.isAntialiasingEnabled() );
		rootApp.setAttribute( CommonAttributes.ANTIALIASING_ENABLED, p.isAntialiasingEnabled() ); // Antialiasing (an, aus)

		LOGGER.log( VIEW_PROPERTIES, "background color: " + p.getBgColor() );
		rootApp.setAttribute( CommonAttributes.BACKGROUND_COLOR, p.getBgColor() ); // Hintergrundfarbe

		LOGGER.log( VIEW_PROPERTIES, "fog enabled: " + p.isFogEnabled() );
		rootApp.setAttribute( CommonAttributes.FOG_ENABLED, p.isFogEnabled() ); // Nebel (an, aus)

		LOGGER.log( VIEW_PROPERTIES, "fog color: " + p.getFogColor() + "\n" );
		rootApp.setAttribute( CommonAttributes.FOG_COLOR, p.getFogColor() ); // Nebelfarbe
	}

	/**
	 * Uebernimmt fuer alle enthaltenen Lichtquellen die uebergebenen Einstellungen (Intensitaet und Farbe der Lichter).<br>
	 * Dabei wird auf die Art der Lichter Ruecksicht genommen. D.h. dass nur in {@link JR3DCanvasLightProperties} angegebene Art der Lichtquellen ist von Aenderungen betroffen.<br>
	 * Unterstuetzt werden: <br>
	 * {@link DirectionalLight}, {@link PointLight}, {@link SpotLight}
	 * 
	 * @param p
	 *            Einstellungen fuer eine bestimmte Lichtart.
	 */
	public void setLightProperties( JR3DCanvasLightProperties p ) {
		LOGGER.log( LIGHTS, this.getName() + " - setting light properties for " + p.getLightType() + " to:" );
		LOGGER.log( LIGHTS, "light color: " + p.getLightColor() );
		LOGGER.log( LIGHTS, "light intensity: " + p.getIntensity() );

		int count = 0;

		// alle Lichtquellen abfragen und ablaufen
//		List<Light> lights = this.getLights();
		List lights = this.getLights();
		Light current = null;
//		for ( Iterator<Light> it = lights.iterator(); it.hasNext(); ) {
		for ( Iterator it = lights.iterator(); it.hasNext(); ) {
//			current = it.next();
			current = (Light)it.next();

			// pruefen, ob die aktuelle Quelle von der richtigen Art ist
			boolean apply = false;
			if ( current instanceof DirectionalLight && p.getLightType().equals( JR3DCanvasLightProperties.DIRECTIONAL_LIGHT ) ) {
				apply = true;
			} else if ( current instanceof SpotLight && p.getLightType().equals( JR3DCanvasLightProperties.SPOT_LIGHT ) ) {
				apply = true;
			} else if ( current instanceof PointLight && p.getLightType().equals( JR3DCanvasLightProperties.POINT_LIGHT ) ) {
				apply = true;
			}

			// erst uebernehmen, wenn die Art der Lichtquelle stimmt
			if ( apply ) {
				count++;
				current.setColor( p.getLightColor() );
				current.setIntensity( p.getIntensity() );
			}

			LOGGER.log( LIGHTS, "light properties applied for " + count + " lights \n" );
		}
	}

	/**
	 * Gibt alle am root angehaengten Lichtquellen zurueck.<br>
	 * 
	 * @return Liste mit allen Lichtquellen unter root.
	 * 
	 * @see #getLightNodes()
	 */
//	public List<Light> getLights() {
//		List<Light> lights = new ArrayList<Light>();
//		for ( Iterator<SceneGraphComponent> it = this.getLightNodes().iterator(); it.hasNext(); ) {
//			lights.add( it.next().getLight() );
	public List getLights() {
		List lights = new ArrayList();
		for ( Iterator it = this.getLightNodes().iterator(); it.hasNext(); ) {
			lights.add(((SceneGraphComponent) it.next()).getLight() );
		}
		LOGGER.log( LIGHTS, this.getName() + " - contains " + lights.size() + " light nodes" );
		return lights;
	}

	/**
	 * Gibt alle {@link SceneGraphComponent}s, welche an den root angehaengt sind und eine Lichtquelle {@link Light} besitzen, zurueck.<br>
	 * Es wird <b>nur<b> durch root iterriert! Die tiefer liegenden Lichtquellen werden nicht erkannt! <br>
	 * 
	 * @return Liste mit Knoten, welche Lichtquellen beinhalten.
	 * 
	 * @see #addLight(JR3DCanvasLightProperties, double[], double[])
	 */
//	public List<SceneGraphComponent> getLightNodes() {
	public List getLightNodes() {
		// nur beim ersten Aufruf wird nach Knoten mit Lichtquellen gesucht.
		// Bei Manipulation der Lichter wird die Liste aktualisiert.
		if ( this.m_lightNodes == null ) {
//			this.m_lightNodes = new ArrayList<SceneGraphComponent>();
			this.m_lightNodes = new ArrayList();
//			for ( Iterator<?> it = this.m_root.getChildNodes().iterator(); it.hasNext(); ) {
			for ( Iterator it = this.m_root.getChildNodes().iterator(); it.hasNext(); ) {
				Object current = it.next();
				if ( current instanceof SceneGraphComponent ) {
					SceneGraphComponent sgc = ( SceneGraphComponent ) current;
					if ( sgc.getLight() != null ) {
						// Dem Knoten ist eine Lichtquelle zugeordnet
						this.m_lightNodes.add( sgc );
					}
				}
			}
		}
		LOGGER.log( LIGHTS, this.getName() + " - contains " + this.m_lightNodes.size() + " light nodes" );
		return this.m_lightNodes;
	}

	/**
	 * Fuegt eine neue Lichtquelle hinzu. Dabei wird der Lichtknoten an root angehaengt, die Liste mit Lichtquellen wird aktualisiert.<br>
	 * Die Intensitaet, Farbe und die Art des Lichts wird uebernommen. Andere Einstellungen sind irrelevant. <br>
	 * Erzeugt koennen {@link DirectionalLight}, {@link SpotLight}, {@link PointLight}. <br>
	 * 
	 * @param p
	 *            legt fest, welche Eigenschaften die neue Lichtquelle besitzen soll.
	 * @param position
	 *            Koordinaten der Lichtquelle (x, y, z).
	 * @param direction
	 *            die Ausrichtung der Lichtquelle (Punkt: x, y, z).
	 * @return die erzeugte Lichtquelle wird zurueckgegeben.
	 * 
	 * @throws IllegalArgumentException,
	 *             wenn keine der unterstuetzte Lichtquelle erzeugt werden soll.
	 */
	public Light addLight( JR3DCanvasLightProperties p, double[] position, double[] direction ) throws IllegalArgumentException {
		LOGGER.log( LIGHTS, this.getName() + " - add light" );
		// eine entsprechende Lichtquelle erzeugen
		Light light = null;
		String type = p.getLightType();
		if ( type.equals( JR3DCanvasLightProperties.DIRECTIONAL_LIGHT ) ) {
			light = new DirectionalLight( "light" );
			LOGGER.log( LIGHTS, "create " + JR3DCanvasLightProperties.DIRECTIONAL_LIGHT + " light" );
		} else if ( type.equals( JR3DCanvasLightProperties.SPOT_LIGHT ) ) {
			light = new SpotLight( "light" );
			LOGGER.log( LIGHTS, "create " + JR3DCanvasLightProperties.SPOT_LIGHT + " light" );
		} else if ( type.equals( JR3DCanvasLightProperties.POINT_LIGHT ) ) {
			light = new PointLight( "light" );
			LOGGER.log( LIGHTS, "create " + JR3DCanvasLightProperties.POINT_LIGHT + " light" );
		} else {
			throw new IllegalArgumentException( "Light type not supported, use directional, spot or point light." );
		}

		// Eigenschaften uebernehmen
		light.setColor( p.getLightColor() );
		LOGGER.log( LIGHTS, "light color " + p.getLightColor() );
		light.setIntensity( p.getIntensity() );
		LOGGER.log( LIGHTS, "light intensity " + p.getIntensity() );

		// Knoten erzeugen und Licht zuordnen
		SceneGraphComponent sgc = new SceneGraphComponent( "lightNode" );
		sgc.setLight( light );

		// Transformation berechnen
		sgc.setTransformation( new Transformation( "lightNode trafo", this.createTransformationMatrix( position, direction, this.m_cameraUpwards ).getArray() ) );

		// Lichtknoten hinzufuegen und die Liste aktualisieren
//		List<SceneGraphComponent> lights = this.getLightNodes();
		List lights = this.getLightNodes();
		this.m_root.addChild( sgc );
		LOGGER.log( SCENE_GRAPH, "add node '" + sgc.getName() + "' to '" + this.m_root.getName() + "'" );
		lights.add( sgc );

		return light;
	}

	/**
	 * Loescht alle unter root angehaengten Lichtquellen.<br>
	 * 
	 */
	public void removeAllLights() {
		LOGGER.log( LIGHTS, this.getName() + " - remove all lights" );
		// alle Lichtquellen aus dem Baum entfernen

//		for ( Iterator<SceneGraphComponent> it = this.getLightNodes().iterator(); it.hasNext(); ) {
//			SceneGraphComponent sgc = it.next();
		for ( Iterator it = this.getLightNodes().iterator(); it.hasNext(); ) {
			SceneGraphComponent sgc = (SceneGraphComponent)it.next();
			this.m_root.removeChild( sgc );
			LOGGER.log( SCENE_GRAPH, "remove node '" + sgc.getName() + "' from '" + sgc.getName() + "'" );
		}

		// die Liste leeren
//		List<SceneGraphComponent> lights = this.getLightNodes();
		List lights = this.getLightNodes();
		lights.clear();
		LOGGER.log( LIGHTS, " clear light nodes list" );
	}

	/**
	 * Initialisiert die History.<br>
	 * Dabei werden zwei {@link History} Instanzen erzeugt (Array).<br>
	 * Die erste Instance speichert die Veraenderungen an der Kamera. Die zweite Instanz ist fuer die Speicherung der Transformation (Drehung) der Scene zustaendig.<br>
	 * 
	 * @see History
	 */
	protected void initializeHistory() {
		if ( this.m_history == null ) {
			LOGGER.log( HISTORY, this.getName() + " - create and initialize history" );
			this.m_history = new History[] { new History(), new History() };
		}
	}

	/**
	 * Gibt die Zeichenflaeche als Componente zurueck. <br>
	 * Beim ersten Aufruf wird eine neue JReality-Zeichenflaeche erzeugt. Bei weiteren Aufrufen bekommt man diese erzeugte Componente.<br>
	 * <br>
	 * Die globalen 'Handler' (JReality-Tools) werden direkt hier eingebunden. Dies sind: {@link ClickWheelCameraZoomTool}, {@link DraggingTool}, {@link RotateTool}.<br>
	 * 
	 * @return JReality-Zeichenflaeche
	 */
	public Component getDrawingBoard() {
		if ( this.m_drawingBoard == null ) {
			LOGGER.log( COMPONENTS, this.getName() + " - create and initialize drawing board" );
			// Eine default-Scene erzeugen. Hier erfolgt die Inialisierung der wichtigen Objekte (Kamera, Licht usw.)
			LOGGER.log( SCENE_GRAPH, "create default scene" );
			JrScene scene = JrSceneFactory.getDefaultDesktopScene();

			// root Knoten setzen
			this.m_root = scene.getSceneRoot();
			LOGGER.log( SCENE_GRAPH, "setting root node '" + this.m_root.getName() + "'" );

			// die default-Scene enthaelt schon einen Scene-Knoten. Hier wird nach diesem Knoten gesucht.
			Object node = null;
//			for ( Iterator<?> it = this.m_root.getChildNodes().iterator(); this.m_sceneContent == null && it.hasNext(); ) {
			for ( Iterator it = this.m_root.getChildNodes().iterator(); this.m_sceneContent == null && it.hasNext(); ) {
				node = it.next();
				if ( node instanceof SceneGraphComponent && ( ( SceneGraphComponent ) node ).getName().equals( "scene" ) ) {
					this.m_sceneContent = ( SceneGraphComponent ) node;
					LOGGER.log( SCENE_GRAPH, "found scene node: '" + this.m_sceneContent.getName() + "'" );
				}
			}

			// wenn der Scene-Knoten nicht gefunden wuerde -> kein gueltiger SceneGraph
			if ( this.m_sceneContent == null ) {
				throw new IllegalStateException( "scene node not initialized" );
			}

			// die default-Scene verfuegt ueber eingebundene Tools. RotateTool ist eines davon.
			// Rotiert soll stehts um einen festen Punkt, deswegen soll die Eigenschaft fixOrigin auf true gesetzt werden.
			// RotateTool ausfindig machen und fixOrigin setzen.
			Object tools[] = this.m_sceneContent.getTools().toArray();
			for ( int i = 0; i < tools.length; i++ ) {
				if ( tools[i] instanceof RotateTool ) {
					LOGGER.log( TOOLS, "found rotate tool" );
					RotateTool rt = ( RotateTool ) tools[i];
					rt.setFixOrigin( true );
					LOGGER.log( TOOLS, "setting rotate tool properties: " + "fixOrigin=true" );
				}
			}

			// Standardmaessig ist ein ZoomTool nicht eingebunden -> ein ZoomTool hinzufuegen
			this.m_sceneContent.addTool( new ClickWheelCameraZoomTool() );
			LOGGER.log( TOOLS, "add zoom tool " + "'ClickWheelCameraZoomTool'" + " to '" + this.m_sceneContent.getName() + "'" );

			// einen Objekt-Knoten erzeugen. An diesen werden alle MMCanvasObjectIF-Knoten angehaengt.
			this.m_objectRoot = new SceneGraphComponent( "objects root" );
			LOGGER.log( SCENE_GRAPH, "create object node: '" + this.m_objectRoot.getName() + "'" );
			this.m_sceneContent.addChild( this.m_objectRoot );
			LOGGER.log( SCENE_GRAPH, "add '" + this.m_objectRoot.getName() + "' to '" + this.m_sceneContent.getName() + "'" );

			// die Transformationen fuer die Kamera und die Scene erzeugen und zuordnen.
			// Eine veraenderung einer Transformaionsmatrix fuehrt direkt zu einer sichtbaren Aenderung der Kamera bzw. der Scene
			this.m_cameraTransformation = new Transformation( "camera trafo", null );
			this.m_worldTransformation = new Transformation( "content trafo", null );

			scene.getPath( "cameraPath" ).getLastComponent().setTransformation( this.m_cameraTransformation );
			this.m_sceneContent.setTransformation( this.m_worldTransformation );

			// ViewerApp erzeugt eine neue Componente
			ViewerApp va = new ViewerApp( scene );
			va.update();

			// drawingBoard setzen
			this.m_drawingBoard = va.getViewingComponent();

			// va.display();
			// this.m_drawingBoard = new JPanel();
		}
		return this.m_drawingBoard;
	}

	/**
	 * Erzeugt und initialisiert alle Bedienungsbuttons fuer die Zeichenflaeche.<br>
	 * Aufnahme-Button, Zoom-, Rotations- und Skrollbuttons werden erzeugt.<br>
	 * Toolbar wird initialisiert.
	 */
	protected void createComponents() {
		// Toolbar initialisieren
		LOGGER.log( VIEW_PROPERTIES, "setting toolbar properties: " + " floatable=false" );
		this.m_toolBar.setFloatable( false );
		LOGGER.log( VIEW_PROPERTIES, "setting toolbar properties: " + " focusable=false" );
		this.m_toolBar.setFocusable( false );
		LOGGER.log( VIEW_PROPERTIES, "setting toolbar properties: " + " visible=" + ( ( this.m_toolFlags & ( MMCanvas.HORIZONTAL_TOOLBAR | MMCanvas.VERTICAL_TOOLBAR ) ) != 0 ) );
		this.m_toolBar.setVisible( ( this.m_toolFlags & ( MMCanvas.HORIZONTAL_TOOLBAR | MMCanvas.VERTICAL_TOOLBAR ) ) != 0 );

		// Aufnahme-Button erzeugen
		LOGGER.log( COMPONENTS, "create record button" );
		if ( Graphics2DUtils.isJMFAvailable() && ( this.m_toolFlags & MMCanvas.VERTICAL_TOOLBAR ) == 0 ) {
			try {
				Class clazz = Class.forName( "net.mumie.mathletfactory.util.JMFHelper" );
				Method method = clazz.getDeclaredMethod( "createRecordAction", new Class[] { Component.class } );
				Action action = ( Action ) method.invoke( null, new Object[] { getDrawingBoard() } );
				this.createToolbarButton( action, action.isEnabled() );
				method = clazz.getDeclaredMethod( "createStopAction", new Class[] { Action.class } );
				action = ( Action ) method.invoke( null, new Object[] { action } );
				this.createToolbarButton( action, action.isEnabled() );
				this.m_toolBar.addSeparator();
			} catch ( Exception e ) {
				Logger.getLogger( "" ).severe( e.getMessage() );
				// doesn't matter
			}
		}

		// Historie-Buttons erzeugen
		LOGGER.log( COMPONENTS, "create history buttons" );
		if ( ( this.m_toolFlags & MMCanvas.VERTICAL_TOOLBAR ) == 0 ) {
			final JButton btnPrev = this.createToolbarButton( new AbstractAction( "", new ImageIcon( getClass().getResource( "/resource/icon/canvas/Back24.gif" ) ) ) {

				public void actionPerformed( ActionEvent e ) {
					// Historie einen Schritt zuruecksetzen
					History[] history = MMJR3DCanvas.this.m_history;
					history[0].prev();
					history[1].prev();

					// Transformationen uebernehmen
					LOGGER.log( HISTORY, "restore previous camera transformation" );
					MMJR3DCanvas.this.m_cameraTransformation.setMatrix( ( double[] ) history[0].getCurrentItem() );
					LOGGER.log( HISTORY, "restore previous scene transformation" );
					MMJR3DCanvas.this.m_worldTransformation.setMatrix( ( double[] ) history[1].getCurrentItem() );
					MMJR3DCanvas.this.updateCanvas();
				}
			}, true );
			btnPrev.setToolTipText( ResourceManager.getMessage( "hist_backward" ) );

			final JButton btnNext = this.createToolbarButton( new AbstractAction( "", new ImageIcon( getClass().getResource( "/resource/icon/canvas/Forward24.gif" ) ) ) {

				public void actionPerformed( ActionEvent e ) {
					// Historie einen Schritt vorwaehrts
					History[] history = MMJR3DCanvas.this.m_history;
					history[0].next();
					history[1].next();

					// Transformationen uebernehmen
					LOGGER.log( HISTORY, "restore next camera transformation" );
					MMJR3DCanvas.this.m_cameraTransformation.setMatrix( ( double[] ) history[0].getCurrentItem() );
					LOGGER.log( HISTORY, "restore next scene transformation" );
					MMJR3DCanvas.this.m_worldTransformation.setMatrix( ( double[] ) history[1].getCurrentItem() );
					MMJR3DCanvas.this.updateCanvas();
				}
			}, true );
			btnNext.setToolTipText( ResourceManager.getMessage( "hist_forward" ) );

			// bei jeder Aenderung der Historie wird die Interaktionsfaehigkeit der Historie-Buttons geprueft.
			ChangeListener historyChange = new ChangeListener() {

				public void stateChanged( ChangeEvent e ) {
					History[] history = MMJR3DCanvas.this.m_history;

					// wenn der interne Zeiger der Historie auf ihrem ersten Eintrag steht, soll prevButton abgeschaltet werden
					btnPrev.setEnabled( !history[0].isFirst() );
					// wenn der interne Zeiger der Historie auf ihrem letzten Eintrag steht, soll nextButton abgeschaltet werden
					btnNext.setEnabled( !history[0].isLast() );
				}
			};

			this.m_history[0].addChangeListener( historyChange );
			this.m_history[1].addChangeListener( historyChange );
			this.m_history[0].stateChanged();

			this.m_toolBar.addSeparator();
		}

		// Zoom-Buttons erzeugen
		LOGGER.log( COMPONENTS, "create zoom buttons" );
		final JButton btnZoomIn = createToolbarButton( new AbstractAction( "", new ImageIcon( getClass().getResource( "/resource/icon/canvas/ZoomIn24.gif" ) ) ) {

			public void actionPerformed( ActionEvent e ) {
				// Historie um die aktuellen Transformationen erweitern
				MMJR3DCanvas.this.addSnapShotToHistory();

				// Camera um -0.2 Einheiten auf der z-Achse verschieben.
				Transformation translate = new Transformation();
				MatrixBuilder.euclidean().translate( new double[] { 0.0, 0.0, -0.2 } ).assignTo( translate );

				MMJR3DCanvas.this.m_cameraTransformation.multiplyOnRight( translate.getMatrix() );
				MMJR3DCanvas.this.updateCanvas();
			}
		}, true );
		btnZoomIn.setToolTipText( ResourceManager.getMessage( "zoom_in" ) );

		final JButton btnZoomOut = createToolbarButton( new AbstractAction( "", new ImageIcon( getClass().getResource( "/resource/icon/canvas/ZoomOut24.gif" ) ) ) {

			public void actionPerformed( ActionEvent e ) {
				// Historie um die aktuellen Transformationen erweitern
				MMJR3DCanvas.this.addSnapShotToHistory();

				// Camera um 0.2 Einheiten auf der z-Achse verschieben.
				Transformation translate = new Transformation();
				MatrixBuilder.euclidean().translate( new double[] { 0.0, 0.0, 0.2 } ).assignTo( translate );

				MMJR3DCanvas.this.m_cameraTransformation.multiplyOnRight( translate.getMatrix() );
				MMJR3DCanvas.this.updateCanvas();
			}
		}, true );
		btnZoomOut.setToolTipText( ResourceManager.getMessage( "zoom_out" ) );

		// Rotieren-Buttons erzeugen
		LOGGER.log( COMPONENTS, "create rotate buttons" );
		final JButton btnRotate = this.createToolbarButton( new AbstractAction( "", new ImageIcon( this.getClass().getResource( "/resource/icon/canvas/rotateLeft.png" ) ) ) {

			public void actionPerformed( ActionEvent e ) {
				// Historie um die aktuellen Transformationen erweitern
				MMJR3DCanvas.this.addSnapShotToHistory();

				// zuerst um -Pi/24 grad rotieren (y-Achse) dann die aktuelle Transformation anwenden
				// wenn man diese Reihenfolge nicht beachtet, kann es passieren, dass nicht um y-Achse rotiert wird.
				// es wird nur die Scene rotiert, die Camera bleibt auf ihrer Position
				Transformation rotate = new Transformation();
				MatrixBuilder.euclidean().rotate( -Math.PI / 24, new double[] { 0, 1, 0 } ).assignTo( rotate );

				double[] m = MMJR3DCanvas.this.m_worldTransformation.getMatrix();

				MMJR3DCanvas.this.m_worldTransformation.setMatrix( rotate.getMatrix() );
				MMJR3DCanvas.this.m_worldTransformation.multiplyOnRight( m );
				MMJR3DCanvas.this.updateCanvas();
			}
		}, true );
		btnRotate.setToolTipText( ResourceManager.getMessage( "rotate_vertical" ) );

		final JButton btnAntiRotate = createToolbarButton( new AbstractAction( "", new ImageIcon( getClass().getResource( "/resource/icon/canvas/rotateRight.png" ) ) ) {

			public void actionPerformed( ActionEvent e ) {
				// Historie um die aktuellen Transformationen erweitern
				MMJR3DCanvas.this.addSnapShotToHistory();

				// zuerst um Pi/24 grad rotieren (y-Achse) dann die aktuelle Transformation anwenden
				// wenn man diese Reihenfolge nicht beachtet, kann es passieren, dass nicht um y-Achse rotiert wird.
				// es wird nur die Scene rotiert, die Camera bleibt auf ihrer Position
				Transformation rotate = new Transformation();
				MatrixBuilder.euclidean().rotate( Math.PI / 24, new double[] { 0, 1, 0 } ).assignTo( rotate );

				double[] m = MMJR3DCanvas.this.m_worldTransformation.getMatrix();

				MMJR3DCanvas.this.m_worldTransformation.setMatrix( rotate.getMatrix() );
				MMJR3DCanvas.this.m_worldTransformation.multiplyOnRight( m );
				MMJR3DCanvas.this.updateCanvas();
			}
		}, true );
		btnAntiRotate.setToolTipText( ResourceManager.getMessage( "rotate_vertical" ) );

		final JButton btnRotateUp = createToolbarButton( new AbstractAction( "", new ImageIcon( getClass().getResource( "/resource/icon/canvas/rotateUp.png" ) ) ) {

			public void actionPerformed( ActionEvent e ) {
				MMJR3DCanvas.this.addSnapShotToHistory();

				// zuerst um -Pi/24 grad rotieren (x-Achse) dann die aktuelle Transformation anwenden
				// wenn man diese Reihenfolge nicht beachtet, kann es passieren, dass nicht um x-Achse rotiert wird.
				// es wird nur die Scene rotiert, die Camera bleibt auf ihrer Position
				Transformation rotate = new Transformation();
				MatrixBuilder.euclidean().rotate( -Math.PI / 24, new double[] { 1, 0, 0 } ).assignTo( rotate );

				double[] m = MMJR3DCanvas.this.m_worldTransformation.getMatrix();

				MMJR3DCanvas.this.m_worldTransformation.setMatrix( rotate.getMatrix() );
				MMJR3DCanvas.this.m_worldTransformation.multiplyOnRight( m );
				MMJR3DCanvas.this.updateCanvas();
			}
		}, true );
		btnRotateUp.setToolTipText( ResourceManager.getMessage( "rotate_horizontal" ) );

		final JButton btnRotateDown = createToolbarButton( new AbstractAction( "", new ImageIcon( getClass().getResource( "/resource/icon/canvas/rotateDown.png" ) ) ) {

			public void actionPerformed( ActionEvent e ) {
				// Historie um die aktuellen Transformationen erweitern
				MMJR3DCanvas.this.addSnapShotToHistory();

				// zuerst um Pi/24 grad rotieren (x-Achse) dann die aktuelle Transformation anwenden
				// wenn man diese Reihenfolge nicht beachtet, kann es passieren, dass nicht um x-Achse rotiert wird.
				// es wird nur die Scene rotiert, die Camera bleibt auf ihrer Position
				Transformation rotate = new Transformation();
				MatrixBuilder.euclidean().rotate( Math.PI / 24, new double[] { 1, 0, 0 } ).assignTo( rotate );

				double[] m = MMJR3DCanvas.this.m_worldTransformation.getMatrix();

				MMJR3DCanvas.this.m_worldTransformation.setMatrix( rotate.getMatrix() );
				MMJR3DCanvas.this.m_worldTransformation.multiplyOnRight( m );
				MMJR3DCanvas.this.updateCanvas();
			}
		}, true );
		btnRotateDown.setToolTipText( ResourceManager.getMessage( "rotate_horizontal" ) );

		// Scroll-Buttons erzeugen
		LOGGER.log( COMPONENTS, "create scroll buttons" );
		this.m_btnScrollUp = createScrollButton( new AbstractAction( "", new ImageIcon( getClass().getResource( "/resource/icon/canvas/up.png" ) ) ) {

			public void actionPerformed( ActionEvent e ) {
				// Historie um die aktuellen Transformationen erweitern
				MMJR3DCanvas.this.addSnapShotToHistory();

				// die Camera wird um 0.1 entlang der y-Achse verschoben (nach oben)
				Transformation translate = new Transformation();
				MatrixBuilder.euclidean().translate( new double[] { 0.0, 0.1, 0.0 } ).assignTo( translate );

				MMJR3DCanvas.this.m_cameraTransformation.multiplyOnRight( translate.getMatrix() );
				MMJR3DCanvas.this.updateCanvas();
			}
		}, new Insets( 5, 0, 0, 0 ), ( this.m_toolFlags & MMCanvas.SCROLL_BUTTONS ) != 0 );
		this.m_btnScrollUp.setToolTipText( ResourceManager.getMessage( "scroll_up" ) );

		this.m_btnScrollDown = createScrollButton( new AbstractAction( "", new ImageIcon( getClass().getResource( "/resource/icon/canvas/down.png" ) ) ) {

			public void actionPerformed( ActionEvent e ) {
				// Historie um die aktuellen Transformationen erweitern
				MMJR3DCanvas.this.addSnapShotToHistory();

				// die Camera wird um -0.1 entlang der y-Achse verschoben (nach unten)
				Transformation translate = new Transformation();
				MatrixBuilder.euclidean().translate( new double[] { 0.0, -0.1, 0.0 } ).assignTo( translate );

				MMJR3DCanvas.this.m_cameraTransformation.multiplyOnRight( translate.getMatrix() );
				MMJR3DCanvas.this.updateCanvas();
			}
		}, new Insets( 0, 0, 5, 0 ), ( this.m_toolFlags & MMCanvas.SCROLL_BUTTONS ) != 0 );
		this.m_btnScrollDown.setToolTipText( ResourceManager.getMessage( "scroll_down" ) );

		this.m_btnScrollLeft = createScrollButton( new AbstractAction( "", new ImageIcon( getClass().getResource( "/resource/icon/canvas/left.png" ) ) ) {

			public void actionPerformed( ActionEvent e ) {
				// Historie um die aktuellen Transformationen erweitern
				MMJR3DCanvas.this.addSnapShotToHistory();

				// die Camera wird um -0.1 entlang der x-Achse verschoben (nach links)
				Transformation translate = new Transformation();
				MatrixBuilder.euclidean().translate( new double[] { -0.1, 0.0, 0.0 } ).assignTo( translate );

				MMJR3DCanvas.this.m_cameraTransformation.multiplyOnRight( translate.getMatrix() );
				MMJR3DCanvas.this.updateCanvas();
			}
		}, new Insets( 0, 5, 0, 0 ), ( this.m_toolFlags & MMCanvas.SCROLL_BUTTONS ) != 0 );
		this.m_btnScrollLeft.setToolTipText( ResourceManager.getMessage( "scroll_left" ) );

		this.m_btnScrollRight = createScrollButton( new AbstractAction( "", new ImageIcon( getClass().getResource( "/resource/icon/canvas/right.png" ) ) ) {

			public void actionPerformed( ActionEvent e ) {
				// Historie um die aktuellen Transformationen erweitern
				MMJR3DCanvas.this.addSnapShotToHistory();

				// die Camera wird um 0.1 entlang der x-Achse verschoben (nach rechts)
				Transformation translate = new Transformation();
				MatrixBuilder.euclidean().translate( new double[] { 0.1, 0.0, 0.0 } ).assignTo( translate );

				MMJR3DCanvas.this.m_cameraTransformation.multiplyOnRight( translate.getMatrix() );
				MMJR3DCanvas.this.updateCanvas();
			}
		}, new Insets( 0, 0, 0, 5 ), ( this.m_toolFlags & MMCanvas.SCROLL_BUTTONS ) != 0 );
		this.m_btnScrollRight.setToolTipText( ResourceManager.getMessage( "scroll_right" ) );
	}

	/**
	 * Gibt die Art der von der Zeichenflaeche unterstuetzten Transformer zurueck.<br>
	 * 
	 * @return Transformerart (type) - {@link GeneralTransformer#ST_JR3D}
	 */
	public int getScreenType() {
		return GeneralTransformer.ST_JR3D;
	}

	/**
	 * Rendert die Scene neu.<br>
	 * <b>Vorsicht!!!</b> Nicht gerade performant bei JReality!
	 */
	public void renderScene() {
		super.renderScene();
	}

	/**
	 * Da JReality das Zeichnen uebernimmt, so bleibt diese Methode erstmal leer.<br>
	 */
	public void renderFromWorldDraw() {}

	/**
	 * Da JReality das Zeichnen uebernimmt, so bleibt diese Methode erstmal leer.<br>
	 */
	protected void drawScene() {}

	/**
	 * Es werden keine globalen Handler mehr genutzt. <br>
	 * JReality bietet hier eine umfangreiche Menge an Tools, welche die Aufgabe der globalen Handler erledigen<br>
	 * 
	 * @see #addTools(Tool[], boolean)
	 * @deprecated
	 */
//	@Deprecated
	public void addGlobalHandler( GlobalHandler aGlobalHandler ) {}

	/**
	 * Es werden keine globalen Handler mehr genutzt. <br>
	 * JReality bietet hier eine umfangreiche Menge an Tools, welche die Aufgabe der globalen Handler erledigen<br>
	 * 
	 * @see #removeTools(Tool[], boolean)
	 * @deprecated
	 */
//	@Deprecated
	public void removeGlobalHandler( Class type ) {}

	/**
	 * Es werden keine globalen Handler mehr genutzt. <br>
	 * JReality bietet hier eine umfangreiche Menge an Tools, welche die Aufgabe der globalen Handler erledigen<br>
	 * 
	 * @deprecated
	 */
//	@Deprecated
	public void globalHandlerFinished() {}

	/**
	 * Diese Methode bleibt leer, da die Anpassung der Groesse von JReality aus funktioniert.
	 */
	public void componentResized( ComponentEvent e ) {}

	/**
	 * Speichert die aktuelle Ausrichtung der Camera und der Scene in der Historie ab.<br>
	 */
	public void addSnapShotToHistory() {
		// wenn die Historie noch nicht verfuegbar ist, so muss diese initialisiert werden
		this.initializeHistory();

		// die Camera Ausrichtung abspeichern
		double[] transform = new double[16];
		System.arraycopy( this.m_cameraTransformation.getMatrix(), 0, transform, 0, 16 );
		LOGGER.log( HISTORY, "save current camera transformation" );
		this.m_history[0].addItem( transform );

		// die Scene Ausrichtung abspeichern
		transform = new double[16];
		System.arraycopy( this.m_worldTransformation.getMatrix(), 0, transform, 0, 16 );
		LOGGER.log( HISTORY, "save current scene transformation" );
		this.m_history[1].addItem( transform );
	}

	/**
	 * Fuegt die Tools hinzu. Diese sind dann fuer die Verarbeitung der Ereignisse verantwortlich.<br>
	 * Unterschieden wird hier zwischen globalen oder object Tools. <br>
	 * Die globalen Tools sind fuer die ganze Scene verantwortlich, die objekt Tools akzeptieren von Objekten erzeugten Eregnisse. <br>
	 * <br>
	 * {@link RotateTool} ist fuer das Rotieren der Scene gedacht (global) (linke Maustaste).<br>
	 * {@link ClickWheelCameraZoomTool} ermoeglicht das Zoomen (global) (Mausrad).<br>
	 * {@link DraggingTool} ist fuer das Ziehen der ganzen Scene verantwortlich (global) (mittlere Maustaste oder Mausrad gedrueckt halten).<br>
	 * <br>
	 * {@link DragEventTool} ermoeglicht das Ziehen der Objekte (local, object) (linke Maustaste)<br>
	 * <br>
	 * Die object Tools (local) bieten die Moeglichkeit auf die erzeugten Events zu reagieren. <br>
	 * Will man die {@link MMCanvasObjectIF} aktualisieren, so muessen die entsrechenden Events abgefangen werden. <br>
	 * Dies wird in {@link JR3DCanvasController} ausgenutzt. <br>
	 * 
	 * @param jrtools
	 *            Tools, welche hinzugefuegt werden sollen.
	 * @param global
	 *            gibt an, ob die Tools global oder local wirken. (true = global)
	 */
	public void addTools( Tool[] jrtools, boolean global ) {
		// wenn die Tools global angebunden werden, dann im scene_content
		// sonst im object_root
		SceneGraphComponent node = null;
		if ( global ) {
			node = this.m_sceneContent;
			LOGGER.log( TOOLS, "add global tools to '" + this.m_sceneContent.getName() + "'" );
		} else {
			node = this.m_objectRoot;
			LOGGER.log( TOOLS, "add local tools to '" + this.m_objectRoot.getName() + "'" );
		}

		for ( int i = 0; i < jrtools.length; i++ ) {
			this.addTool( node, jrtools[i] );
		}
	}

	/**
	 * Fuegt einem Knoten ein Tool hinzu (nur wenn das tool nicht vorhanden). <br>
	 * Die Entscheidung ueber das Vorhandensein geschiet anhand der Klassen.<br>
	 * 
	 * @param node
	 *            Knoten, dem das Tool hinzugefuegt werden soll
	 * @param tool
	 *            Tool, was hinzugefuegt werden soll
	 */
	private void addTool( SceneGraphComponent node, Tool tool ) {
//		List<Tool> tools = node.getTools();
		List tools = node.getTools();

		boolean contains = false;
//		for ( Iterator<Tool> it = tools.iterator(); !contains && it.hasNext(); ) {
		for ( Iterator it = tools.iterator(); !contains && it.hasNext(); ) {
			contains |= it.next().getClass() == tool.getClass();
		}

		if ( !contains ) {
			node.addTool( tool );
			LOGGER.log( TOOLS, "add tool '" + tool.getClass() + "' to '" + node.getName() + "'" );
		} else {
			LOGGER.log( TOOLS, "node '" + node.getName() + "' contains tool '" + tool.getClass() + "'" );
		}
	}

	/**
	 * Entfernt die globalen oder die lokalen Tools. <br>
	 * 
	 * @param jrtools
	 *            Tools, welche entfernt werden sollen
	 * @param global
	 *            zeigt an, ob globale oder lokale Tools geloescht werden sollen (true = global)
	 */
	public void removeTools( Tool[] jrtools, boolean global ) {
		SceneGraphComponent node = null;
		if ( global ) {
			node = this.m_sceneContent;
			LOGGER.log( TOOLS, "remove global tools from '" + this.m_sceneContent.getName() + "'" );
		} else {
			node = this.m_objectRoot;
			LOGGER.log( TOOLS, "remove local tools from '" + this.m_objectRoot.getName() + "'" );
		}

		for ( int i = 0; i < jrtools.length; i++ ) {
			this.removeTool( node, jrtools[i] );
		}
	}

	/**
	 * Entfernt ein Tool vom Knoten.<br>
	 * 
	 * @param node
	 *            Knoten, welchem das Tool zugeordnet ist
	 * @param tool
	 *            Tool, was entfernt werden soll
	 */
	private void removeTool( SceneGraphComponent node, Tool tool ) {
//		List<Tool> tools = node.getTools();
		List tools = node.getTools();

		if ( tools.contains( tool ) ) {
			node.removeTool( tool );
			LOGGER.log( TOOLS, "remove tool '" + tool.getClass() + "' from '" + node.getName() + "'" );
		} else {
			LOGGER.log( TOOLS, "tool '" + tool.getClass() + "' not found" );
		}
	}

	/**
	 * Die {@link MMCanvasObjectIF}e werden als eine Menge von Geometrien {@link Geometry} repraesentiert.<br>
	 * Das Finden eines Objects zu einer {@link Geometry} soll mit dieser Methode ermoeglicht werden.<br>
	 * <br>
	 * Die Events von JReality aggieren auf {@link Geometry}. Z.B. wird beim Ziehen einer Flaeche die entsprechende Geometrie gezogen.<br>
	 * Um an einen {@link MMCanvasObjectIF} zu kommen, muss man pruefen, welchem Objekt die Geometrie zugeordnet ist.<br>
	 * Vorgehen: alle regestrierte {@link MMCanvasObjectIF}e durchlaufen und schauen, ob die Geometrie diesem Objekt gehoert.
	 * 
	 * @see JR3DCanvasController
	 * @see JR3DCanvasTransformer#contains(SceneGraphComponent, Geometry)
	 * 
	 * @param g
	 *            {@link Geometry}, dessen {@link MMCanvasObjectIF} gesucht werden soll
	 * @return null, wenn keinem Objekt zugeodnet, sonst das {@link MMCanvasObjectIF}
	 * 
	 * 
	 */
	public MMCanvasObjectIF getGeometryOwner( Geometry g ) {
		// alle Objekte durchgehen
//		Iterator<MMCanvasObjectIF> it = this.m_objects.iterator();
		Iterator it = this.m_objects.iterator();
		MMCanvasObjectIF current = null;
		while ( it.hasNext() ) {
//			current = it.next();
			current = (MMCanvasObjectIF)it.next();
			// die Transformer verfuegen ueber die Geometrie-Information
			if ( JR3DCanvasTransformer.contains( ( ( JR3DCanvasTransformer ) current.getDisplayTransformerMap().get( this ) ).getViewContent(), g ) ) {
				LOGGER.log( SCENE_GRAPH, "geometry '" + g + "' found: owner '" + current.getLabel() + "'" );
				return current;
			}
		}
		LOGGER.log( SCENE_GRAPH, "geometry '" + g + "' not found" );
		return null;
	}

	/**
	 * Gibt die Position der Kamera.<br>
	 * 
	 * 
	 */
	public double[] getViewerPosition() {
		Matrix m = new Matrix( this.m_cameraTransformation.getMatrix() );
		double[] translation = new double[] { m.getEntry( 0, 3 ), m.getEntry( 1, 3 ), m.getEntry( 2, 3 ) };
		return translation;
	}

	public void setViewerPosition( double[] position ) {
		MMJR3DCanvas.this.addSnapShotToHistory();
		MatrixBuilder.euclidean().translate( position ).assignTo( this.m_cameraTransformation );
	}

	public void setViewDirection( double[] direction, double[] upwards ) {
		this.m_cameraUpwards = upwards;

		MMJR3DCanvas.this.addSnapShotToHistory();

		double[] position = this.getViewerPosition();
		this.m_viewDirection = new double[] { direction[0] - position[0], direction[1] - position[1], direction[2] - position[2] };

		this.m_worldTransformation.setMatrix( this.createTransformationMatrix( position, direction, upwards ).getArray() );
	}

	private Matrix createTransformationMatrix( double[] position, double[] direction, double[] upwards ) {
		double[] f = new double[] { direction[0] - position[0], direction[1] - position[1], direction[2] - position[2] };
		Affine3DDouble.normalize( f );
		Affine3DDouble.normalize( upwards );

		double[] s = Affine3DDouble.crossProduct( f, upwards );
		double[] u = Affine3DDouble.crossProduct( s, f );
		
		// normalize verhindert die Verkr�mmung der Objekte
		Affine3DDouble.normalize(s);
		Affine3DDouble.normalize(u);
		Matrix transform = new Matrix();

		transform.setEntry( 0, 0, s[0] );
		transform.setEntry( 0, 1, s[1] );
		transform.setEntry( 0, 2, s[2] );

		transform.setEntry( 1, 0, u[0] );
		transform.setEntry( 1, 1, u[1] );
		transform.setEntry( 1, 2, u[2] );

		transform.setEntry( 2, 0, -f[0] );
		transform.setEntry( 2, 1, -f[1] );
		transform.setEntry( 2, 2, -f[2] );

		return transform;
	}
	
	/**
	 * @deprecated
	 */
//	@Deprecated
	public void adjustTransformations() {}

	/**
	 * @deprecated
	 */
//	@Deprecated
	protected void calculateScreen2World() {}

	/**
	 * @deprecated
	 */
//	@Deprecated
	public Affine3DDouble getScreen2World() {
		return null;
	}

	/**
	 * @deprecated
	 */
//	@Deprecated
	public Affine3DDouble getWorld2Screen() {
		return null;
	}

	/**
	 * @deprecated
	 */
//	@Deprecated
	public Affine3DDouble getWorld2WorldView() {
		return null;
	}

	/**
	 * @deprecated
	 */
//	@Deprecated
	public Affine3DDouble getWorldView2World() {
		return null;
	}

	/**
	 * @deprecated
	 */
//	@Deprecated
	public void setScreen2World( Affine3DDouble aFastTransformation ) {}

	/**
	 * @deprecated
	 */
//	@Deprecated
	public void setViewerOrientation( Affine3DDouble orientation ) {}

	/**
	 * @deprecated
	 */
//	@Deprecated
	public void setWorld2WorldView( Affine3DDouble trafo ) {}

	/**
	 * Fuegt ein {@link MMCanvasObjectIF} der Scene hinzu.<br>
	 * Das Obekt wird am Ende der Liste angehaengt (irrelevant).
	 * 
	 * @param obj
	 *            {@link MMCanvasObjectIF}, was zum Content aufgenommen werden soll.
	 */
	public void addObject( MMCanvasObjectIF obj ) {
		this.addObject( obj, this.m_objects.size() );
	}

	/**
	 * Fuegt ein {@link MMCanvasObjectIF} der Scene hinzu.<br>
	 * Das Obekt wird am einer bestimmten Stelle in der Liste angehaengt (irrelevant).
	 * 
	 * @param obj
	 *            {@link MMCanvasObjectIF}, was zum Content aufgenommen werden soll.
	 * 
	 * @param position
	 *            Stelle, an die das Objekt eingefuegt werden soll
	 */
	public void addObject( MMCanvasObjectIF obj, int position ) {
		// nur hinzufuegen wenn noch nicht vorhanden
		if ( !this.m_objects.contains( obj ) ) {
			LOGGER.log( SCENE_GRAPH, "try add '" + obj.getClass() + "' to '" + this.m_objectRoot.getName() + "'" );

			// den passenden Transformer finden
			GeneralTransformer gt = GeneralTransformer.getTransformer( obj.getDefaultTransformType(), this.getScreenType(), obj );

			// nur wenn ein passender Transformer gefunden wuerde, kann weitergemacht werden
			if ( gt != null && ( gt instanceof JR3DCanvasTransformer ) ) {
				JR3DCanvasTransformer t = ( JR3DCanvasTransformer ) gt;
				LOGGER.log( SCENE_GRAPH, "transformer for '" + obj.getClass() + "' is '" + t.getClass() + "'" );
				if ( getTopLevelAncestor() instanceof BaseApplet ) {
					obj.addSpecialCaseListener( ( BaseApplet ) getTopLevelAncestor() );
				}

				// Transformer in die Transformer Map eintragen
				LOGGER.log( SCENE_GRAPH, "add transformer '" + t.getClass() + "' to transformer map" );
				obj.getDisplayTransformerMap().put( this, t );

				// Objekt rendern
				LOGGER.log( SCENE_GRAPH, "render '" + obj.getClass() + "'" );
				t.render();

				// die Daten regestrieren und als Knoten der Scene hinzufuegen
				LOGGER.log( SCENE_GRAPH, "add '" + obj.getClass() + "' content to '" + this.m_objectRoot.getName() + "'\n" );
				this.m_objectRoot.addChild( t.getViewContent() );
				this.m_objects.add( obj );
			} else {
				LOGGER.log( SCENE_GRAPH, "cannot find valid transformer for '" + obj.getClass() + "'\n" );
			}
		} else {
			LOGGER.log( SCENE_GRAPH, "scene graph contains this object allready\n" );
		}
	}

	/**
	 * Entfernt ein {@link MMCanvasObjectIF} aus der Scene.<br>
	 * 
	 * @param obj
	 *            das Objekt, was entfernt werden soll
	 */
	public void removeObject( MMCanvasObjectIF obj ) {
		LOGGER.log( SCENE_GRAPH, "try remove '" + obj.getClass() + "' from '" + this.m_objectRoot.getName() + "'" );

		if ( this.m_objects.remove( obj ) ) {
			if ( getTopLevelAncestor() instanceof BaseApplet ) {
				obj.removeSpecialCaseListener( ( BaseApplet ) getTopLevelAncestor() );
			}

			// Content Knoten entfernen und das Objekt aus der Liste loeschen
			LOGGER.log( SCENE_GRAPH, "remove '" + obj.getClass() + "' from '" + this.m_objectRoot.getName() + "'" );
			this.m_objectRoot.removeChild( ( ( JR3DCanvasTransformer ) obj.getDisplayTransformerMap().get( this ) ).getViewContent() );

			LOGGER.log( SCENE_GRAPH, "remove transformer from transformer map\n" );
			// Transformer von der Transformer Map entfernen
			obj.getDisplayTransformerMap().remove( this );
		} else {
			LOGGER.log( SCENE_GRAPH, "cannot remove '" + obj.getClass() + "' from '" + this.m_objectRoot.getName() + "', object not found" );
		}
	}

	/**
	 * Entfernt alle {@link MMCanvasObjectIF}s vom Content.<br>
	 */
	public void removeAllObjects() {
		for ( int i = this.m_objects.size() - 1; i >= 0; i-- ) {
			this.removeObject( ( MMCanvasObjectIF ) this.m_objects.get( i ) );
		}
	}

	/**
	 * Entfernt alle {@link MMCanvasObjectIF}, welche der uebergebenen Klasse entsprechen.<br>
	 * 
	 * @param type
	 *            Klasse der Objekte
	 */
	public void removeAllObjectsOfType( Class type ) {
		List objects = this.getObjectsOfType( type );
		for ( Iterator it = objects.iterator(); it.hasNext(); ) {
			this.removeObject( ( MMCanvasObjectIF ) it.next() );
		}
	}

	public void addImage( CanvasImage img ) {
	// TODO Die Bilder sollen in ein SceneGraph umgewandelt werden
	}

	public void removeImage( CanvasImage img ) {
	// TODO Die SceneGraph muss auch entfernt werden
	}
}
