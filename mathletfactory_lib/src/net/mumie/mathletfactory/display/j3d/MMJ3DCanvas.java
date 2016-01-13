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

package net.mumie.mathletfactory.display.j3d;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Collections;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BadTransformException;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.GraphicsContext3D;
import javax.media.j3d.Group;
import javax.media.j3d.PointLight;
import javax.media.j3d.Raster;
import javax.media.j3d.Transform3D;
import javax.media.j3d.View;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import net.mumie.mathletfactory.appletskeleton.util.MumieTheme;
import net.mumie.mathletfactory.display.MM3DCanvas;
import net.mumie.mathletfactory.math.util.Affine3DDouble;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;
import net.mumie.mathletfactory.transformer.CanvasObjectTransformer;
import net.mumie.mathletfactory.transformer.j3d.Canvas3DObjectJ3DTransformer;

import com.sun.j3d.utils.universe.SimpleUniverse;

/**
 * This class renders 3 dimensional Objects on a Java 3D-Canvas
 * {@link javax.media.j3d.Canvas3D} using retained mode.
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class MMJ3DCanvas extends MM3DCanvas {
  
   /** The field of view */
  private static final double VIEW_ANGLE = Math.PI/3;
  
  /** A possible value for {@link #setProjectionType} */
  public static final int PERSPECTIVE_PROJECTION = View.PERSPECTIVE_PROJECTION;
  /** A possible value for {@link #setProjectionType} */
  public static final int PARALLEL_PROJECTION = View.PARALLEL_PROJECTION;
  
  /**
   *  The type of the projection from world to screen, must be
   *  <code>PERSPECTIVE_PROJECTION</code> or <code>PARALLEL_PROJECTION</code>.
   */
  private int m_projectionType = PERSPECTIVE_PROJECTION;
 
  
  // global Java3D variables
  private Canvas3D  m_canvas3D = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
  private SimpleUniverse m_universe = new SimpleUniverse(m_canvas3D);
  private BranchGroup m_root = new BranchGroup();
  private BranchGroup m_content = new BranchGroup();
  private Background m_background = new Background();
  private Color3f m_bgColor = new Color3f(MumieTheme.getCanvasBackground());
  private AmbientLight m_ambientLight = new AmbientLight();
  private DirectionalLight m_directionalLight = new DirectionalLight();
  private DirectionalLight m_directionalLight2 = new DirectionalLight();
  private PointLight m_pointLight = new PointLight();
  private PointLight m_pointLight2 = new PointLight();
  
  // declared global for performance reasons:
  private Matrix4d m_globalT3D = new Matrix4d();
  private Transform3D m_viewTransform3D = new Transform3D();
  private Point3d m_yUnit = new Point3d(), m_xUnit = new Point3d(), m_originInCanvas = new Point3d();
  private Transform3D m_imagePlateToVWorld = new Transform3D();
  private Vector3d m_viewPositionV = new Vector3d();
  private Vector3d m_viewDirectionV = new Vector3d();
  private double[] m_tmp_coords = new double[3];
  private Vector3d m_viewPosToRealWorldPoint = new Vector3d();
  private Vector3d m_viewPosToWorldPointOnProjPlane = new Vector3d();

  
  private boolean m_selectionBehaviourAdded = false;
  private J3DDrawable selectedDrawable = null;
  /** The <code>Appearance</code> of the selected object</code>. Needed for highlighting. */
  protected final Appearance selectedAppearance = new Appearance();
  /** The highlighting behavior that is applied onto {@link #selectedAppearance} */
  protected final SelectionBehavior selectionBehavior = new SelectionBehavior(selectedAppearance);
  
  public MMJ3DCanvas(){
    setLayout(new BorderLayout());
    m_canvas3D.addMouseListener(m_controller);
    m_canvas3D.addMouseMotionListener(m_controller);
    m_canvas3D.addMouseWheelListener(m_controller);
    m_canvas3D.addComponentListener(this);
    m_canvas3D.addFocusListener(this);
    m_canvas3D.addKeyListener(m_controller);
    addKeyListener(m_controller);
    setShowFocusBorder(false); // setting it to true would result in flickering repaints when being focused
    
    // only the drawing board should deal with focus events
    setFocusable(false);
    m_canvas3D.setFocusable(true);
    
    // we do not want the tab key to be used internally
    setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
    setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
   
    createComponents();
    layoutComponents();
    
    m_background.setCapability(Background.ALLOW_COLOR_WRITE);
    // let there be lights:
    BoundingSphere bounds = new BoundingSphere(new Point3d(0,0,0),100);
    m_ambientLight.setColor(new Color3f(0.5f, 0.5f, 0.5f));
    m_ambientLight.setInfluencingBounds(bounds);
    m_directionalLight.setDirection(-1, -1, -1);
    m_directionalLight.setColor(new Color3f(1.0f, 1.0f, 1.0f));
    m_directionalLight.setInfluencingBounds(bounds);
    m_directionalLight2.setDirection(1, 1, 1);
    m_directionalLight2.setColor(new Color3f(1.0f, 1.0f, 1.0f));
    m_directionalLight2.setInfluencingBounds(bounds);
    m_pointLight.setPosition(0, 0, 2.7f);
    m_pointLight.setColor(new Color3f(0.2f,0.2f,0.2f));
    m_pointLight.setInfluencingBounds(bounds);
    m_pointLight2.setPosition(0, 0, -2.7f);
    m_pointLight2.setColor(new Color3f(0.2f,0.2f,0.2f));
    m_pointLight2.setInfluencingBounds(bounds);
    
    // add "global" nodes to root scenegraph
    BranchGroup bg = new BranchGroup();
    bg.addChild(m_ambientLight);
    bg.addChild(m_directionalLight);
    bg.addChild(m_directionalLight2);
    bg.addChild(m_pointLight);
    bg.addChild(m_pointLight2);
    bg.addChild(m_background);
    
    // the scenegraph is quite often extended or collapsed by handlers, drawables or transformers
    m_root.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
    m_content.setCapability(Group.ALLOW_CHILDREN_WRITE);
    m_content.setCapability(Group.ALLOW_CHILDREN_EXTEND);
    
    m_background.setColor(new Color3f(m_bgColor));
    m_background.setApplicationBounds(bounds);
    m_universe.getViewingPlatform().getMultiTransformGroup()
       .getTransformGroup(0).addChild(bg);
  
    m_universe.addBranchGraph(m_root);
    initView();
    
    antiAliasingCheckBox.addActionListener(new ActionListener(){
    	public void actionPerformed(ActionEvent e){
    		if(antiAliasingCheckBox.isSelected()){
    			setAntiAliasingEnabled(true);
    		}else{
    			setAntiAliasingEnabled(false);
    		}
    	}
    });
   }
  
  
  /**
   *  Returns the associated {@link javax.media.j3d.Canvas3D}
   */
  public Component getDrawingBoard(){
    return m_canvas3D;
  }
  
  /**
   *  Returns the content branch of the Java3D scenegraph. Needed by the
   *  {@link net.mumie.mathletfactory.display.j3d.J3DDrawable}s to insert
   *  their <code>Shape3D</code>s and local <code>TransformGroup</code>s.
   */
  public BranchGroup getContentBranch(){
    return m_content;
  }
  
  public int getScreenType(){
    return CanvasObjectTransformer.ST_J3D;
  }
      
  /*
   
  public Affine3DDouble getScreen2World() {
    if(m_forceRecalculationOfScreen2World){
      calculateScreen2World();
      m_forceRecalculationOfScreen2World = false;
    }
    return super.getScreen2World();
  }
  */
  
  /**
   * Returns for a given screen location (in pixels) the position in the virtual world. Since
   * a point on the screen produces a ray in vworld, an additional z distance from the viewer
   * (i.e. the projection of viewer-object onto the viewers z-axis) must be specified.
   *
   * @param worldCoords the by reference returned vworld coordinates
   */
  public void getWorldPointFromScreenLocation(int x, int y, double distanceInZProjFromViewPos,
                                              double[] worldCoords){
    //System.out.println("distance is "+distanceInZProjFromViewPos);
    m_tmp_coords[0] = x;
    m_tmp_coords[1] = y;
    m_tmp_coords[2] = 0;
    
    // this creates a world point within the projection plane
    if(getScreen2World() == null)
      calculateScreen2World();
    //System.out.println("RealScreen2WorldDraw : " +getRealScreen2WorldDrawTrafo().getLinearMatrixRepresentation());
    //System.out.println("RealScreen2WorldDrawFast : " +getRealScreen2WorldDrawFast());
    getScreen2World().applyTo(m_tmp_coords, worldCoords);
    //System.out.println("worldpoint (in projection plane): "+worldCoords[0]+","
    //                  +worldCoords[1]+","+worldCoords[2]);
    // the vector pointing from view position to the world point in the projection plane
    m_viewPosToWorldPointOnProjPlane.set(worldCoords);
    m_viewPosToWorldPointOnProjPlane.sub(m_viewPositionV);
    
    // the vector pointing from view position to the worldpoint with the
    // above distance
    m_viewPosToRealWorldPoint.scale(distanceInZProjFromViewPos
                                      / m_viewPosToWorldPointOnProjPlane.dot(m_viewDirectionV),
                                    m_viewPosToWorldPointOnProjPlane);
    
    m_viewPosToRealWorldPoint.add(m_viewPositionV);
    worldCoords[0] = m_viewPosToRealWorldPoint.x;
    worldCoords[1] = m_viewPosToRealWorldPoint.y;
    worldCoords[2] = m_viewPosToRealWorldPoint.z;
    //System.out.println("worldCoords are "+m_viewPosToRealWorldPoint);
  }
 
  /**
   *  This method returns for a given direction a point, that is outside the
   *  view pyramid. Needed by objects with "infinite boundaries" (mathematical
   *  lines, planes etc.).
   */
  public void getPointOutOfViewRange(double[] startingPoint, double[] direction, double[] point){
    double[] transformedStartingPoint = new double[3];
    double[] transformedDirection = new double[3];
    //System.out.println("examining point "+startingPoint[0]+","+startingPoint[1]+","+startingPoint[2]);
    
    // perform the inverse view transformation for simplifying the calculation
    Affine3DDouble worldView2world = getWorldView2World();
    worldView2world.applyTo(startingPoint, transformedStartingPoint);
    worldView2world.applyDeformationPartTo(direction, transformedDirection);
    Affine3DDouble.normalize(transformedDirection);
    /*
    // test if starting point is already outside the view volume
    if(transformedStartingPoint[2] > 0 || transformedStartingPoint[2] > m_canvas3D.getView().getBackClipDistance()
       || Math.abs(transformedStartingPoint[0] * Math.tan(m_viewAngle)) > -transformedStartingPoint[2]
       || Math.abs(transformedStartingPoint[1] * Math.tan(m_viewAngle)) > -transformedStartingPoint[2]){
      getWorld2WorldView().applyTo(transformedStartingPoint, point);
      //System.out.println("point "+transformedStartingPoint[0]+","+transformedStartingPoint[1]
      //                     +","+transformedStartingPoint[2]+
      //                    "lies outside the view volume");
      return;
    }
     */
    // the back clip distance is (practically) the highest possible distance for
    // an object to be seen, so scaling the direction with it brings the object
    // out of range
    Affine3DDouble.scale(transformedDirection, m_canvas3D.getView().getBackClipDistance());
    Affine3DDouble.add(transformedStartingPoint, transformedDirection);
    getWorld2WorldView().applyTo(transformedStartingPoint, point);
  }

  /**
   *  Returns {@link #getWorld2WorldView} as Java3D
   *  <code>Matrix4d</code>
   */
  private Matrix4d getJ3DWorldViewTrafo(){
    Affine3DDouble viewTrafo = getWorld2WorldView();
    for(int i=0; i<3; i++)
      for(int j=0; j<4; j++)
        m_globalT3D.setElement(i,j,viewTrafo.get(i,j));
    m_globalT3D.setElement(3,3,1);
    return m_globalT3D;
  }
  
  /**
   *  Mainly iterates through the {@link net.mumie.mathletfactory.mmobject.MMCanvasObjectIF CanvasObject}s
   *  and asks them to render themselves. If this method is called for the first
   *  time, the scenegraph is constructed in this method as well as in the
   *  {@link net.mumie.mathletfactory.mmobject.MMCanvasObjectIF#render render} method.
   */
  public void renderScene(){
    if (!m_selectionBehaviourAdded) {
      //System.out.println("adding selection behavior!");
      selectedAppearance.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
      selectedAppearance.setCapability(Appearance.ALLOW_LINE_ATTRIBUTES_WRITE);
      selectedAppearance.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);
      selectedAppearance.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);
      selectionBehavior.setSchedulingBounds(new BoundingSphere(new Point3d(0, 0, 0), 1000));
      getContentBranch().addChild(selectionBehavior);
      m_selectionBehaviourAdded = true;
    }

    //construct and insert scene graph by iterating throught m_Objects
    boolean anyObjectSelected = false;
    for(int i=0;i<m_objects.size(); i++){
      MMCanvasObjectIF obj = getObject(i);
      obj.render();
      anyObjectSelected = anyObjectSelected || obj.isSelected();
    }
    
    if(!anyObjectSelected)
      setSelectedDrawable(null);
    
    // set the transformation for the view platform and update m_viewDirection
    Matrix4d viewTransform = getJ3DWorldViewTrafo();
    //System.out.println("view transform is "+viewTransform);
    m_viewTransform3D.set(viewTransform);
    try {
      m_universe.getViewingPlatform().getMultiTransformGroup()
       .getTransformGroup(0).setTransform(m_viewTransform3D);
    } catch (BadTransformException e){
      System.out.println("non congruent transform!");
      Affine3DDouble viewTrafo = getWorld2WorldView();
      viewTrafo.setToIdentity();
      initView();
      viewTransform = getJ3DWorldViewTrafo();
      m_viewTransform3D.set(viewTransform);
      m_universe.getViewingPlatform().getMultiTransformGroup()
       .getTransformGroup(0).setTransform(m_viewTransform3D);
    }
    m_viewDirectionV.x = 0;
    m_viewDirectionV.y = 0;
    m_viewDirectionV.z = -1; // initial j3d internal view direction is (0,0,-1)
    viewTransform.transform(m_viewDirectionV);
    m_viewDirection[0] = -viewTransform.getElement(0,2);
    m_viewDirection[1] = -viewTransform.getElement(1,2);
    m_viewDirection[2] = -viewTransform.getElement(2,2);
    m_viewTransform3D.get(m_viewPositionV);
    //System.out.println("Standing at "+pos+" backClipDistance at:"+m_universe.getViewer().getView().getFrontClipDistance());
    
    // if this method is called for the first time, the content scenegraph has been created
    if(!m_content.isLive())
      m_root.addChild(m_content);
    
    //System.out.println(getWorldDrawToNormalizedScreenTrafo().getLinearMatrixRepresentation());
    //update NumberTuple versions
    double[] position = getViewerPosition();
    
    
    position[0] = m_viewPositionV.x;
    position[1] = m_viewPositionV.y;
    position[2] = m_viewPositionV.z;
    
    setViewerPosition(position);
  }
  
  /**
   * Sets up viewing platform and view and resets the viewport transformation.
   * The view settings are the following:<br>
   *  <li>The field of view (the opening angle of the view cone) is m_viewAngle.</li>
   *  <li>The projection type of view (parallel or central) is m_projectionType.</li>
   *  <li>Front clip distance is 0.2, back clip distance 100.</li>
   * </ul>
   */
  protected void initView(){
    reset();
    Transform3D trafo = new Transform3D();
    
    Affine3DDouble viewTrafo = getWorld2WorldView();
    //viewTrafo.setToIdentity();
    setViewerPosition(viewTrafo.getTranslation());
    viewTrafo.setTranslation(getViewerPosition());
    setWorld2WorldView(viewTrafo);
    getWorld2WorldView().getHistory().clear();
    getWorld2WorldView().addSnapshotToHistory();    

    // setup view:
    m_universe.getViewer().getView().setProjectionPolicy(m_projectionType);
    m_universe.getViewer().getView().setFrontClipPolicy(View.VIRTUAL_EYE);
    m_universe.getViewer().getView().setBackClipPolicy(View.VIRTUAL_EYE);
    m_universe.getViewer().getView().setBackClipDistance(100);
    m_universe.getViewer().getView().setFrontClipDistance(0.2);
    m_canvas3D.getView().setFieldOfView(VIEW_ANGLE);
    // update all transformer
    globalHandlerFinished();
  }
  
  /**
   *  Performs a parallel projection from world to screen.
   */
  public void setProjectionType(int projectionType){
    m_projectionType = projectionType;
    m_universe.getViewer().getView().setProjectionPolicy(m_projectionType);
  }
  
  /**
   *  Performs a perspective projection from world to screen.
   */
  public int getProjectionType(){
    return m_projectionType;
  }
  
  /**
   *  Calculates the transformation from screen coordinates (pixels) to
   *  Java3D universe coordinates (which are also mumie world coordinates).
   */
  protected void calculateScreen2World(){
    if(m_canvas3D == null)
      return;
    m_canvas3D.getImagePlateToVworld(m_imagePlateToVWorld);
    
    // get the translation vector
    m_canvas3D.getPixelLocationInImagePlate(0, 0, m_originInCanvas);
    m_imagePlateToVWorld.transform(m_originInCanvas);
    
    // measure the width and length of the unit vectors in 3D world coordinates
    m_canvas3D.getPixelLocationInImagePlate(0, 1, m_yUnit);
    m_canvas3D.getPixelLocationInImagePlate(1, 0, m_xUnit);
    m_imagePlateToVWorld.transform(m_yUnit);
    m_imagePlateToVWorld.transform(m_xUnit);
    // get rid of translation
    m_yUnit.sub(m_originInCanvas);
    m_xUnit.sub(m_originInCanvas);
    
    // if the canvas is visible but not live, the conversion might fail
    if(m_xUnit.x - m_yUnit.x == 0 && m_xUnit.y - m_yUnit.y == 0)
      return;
    
    //System.out.println("lowerLeft is: "+m_originInCanvas);
    //System.out.println("upperLeft is "+m_yUnit);
    //System.out.println("lowerRight is "+m_xUnit);
    Affine3DDouble real2world = super.getScreen2World();
    if(real2world == null);
      real2world = new Affine3DDouble();
    real2world.set(0, 0, m_xUnit.x);
    real2world.set(1, 0, m_xUnit.y);
    real2world.set(2, 0, m_xUnit.z);
    real2world.set(0, 1, m_yUnit.x);
    real2world.set(1, 1, m_yUnit.y);
    real2world.set(2, 1, m_yUnit.z);
    // the z coord is the normal of the xy-plane, i.e. it is the cross product of the normalized screen axes
    real2world.set(0, 2, -(m_xUnit.y * m_yUnit.z - m_xUnit.z * m_yUnit.y)
                  * m_canvas3D.getWidth() * m_canvas3D.getHeight());
    real2world.set(1, 2, (m_xUnit.x * m_yUnit.z - m_xUnit.z * m_yUnit.x)
                  * m_canvas3D.getWidth() * m_canvas3D.getHeight());
    real2world.set(2, 2, -(m_xUnit.x * m_yUnit.y - m_xUnit.y * m_yUnit.x)
                  * m_canvas3D.getWidth() * m_canvas3D.getHeight());
    real2world.set(0, 3, m_originInCanvas.x);
    real2world.set(1, 3, m_originInCanvas.y);
    real2world.set(2, 3, m_originInCanvas.z);
    setScreen2World(real2world);
    //System.out.println("screen2World: "+getScreen2World());
  }
  
 
  /**
   *  Called by {@link net.mumie.mathletfactory.action.handler.global.MMGlobal3DHandler}.
   */
  public void globalHandlerFinished(){
    for(int i=0;i<getObjectCount();i++)
      ((Canvas3DObjectJ3DTransformer)getObject(i).getCanvasTransformer()).updateFinished();
  }
  
  /**
   *  Since rendering of drawables is actually done by the Java3D renderer, this
   *  method does nothing.
   */
  public final void renderFromWorldDraw(){
  }
  

  public void removeObject(MMCanvasObjectIF object) {
    ((Canvas3DObjectJ3DTransformer)object.getCanvasTransformer()).remove();
    super.removeObject(object);
  }

  public void removeAllObjectsOfType(Class type) {
    for (int i = 0; i < m_objects.size(); i++) {
      if (getObject(i).getClass() == type) {
        removeObject(getObject(i));
        i--;
      }
    }
  }
  
  /**
   * @return
   */
  public J3DDrawable getSelectedDrawable() {
    return selectedDrawable;
  }

  /**
   * @return
   */
  public Appearance getSelectedAppearance() {
    return selectedAppearance;
  }

  /**
   * @return
   */
  public SelectionBehavior getSelectionBehavior() {
    return selectionBehavior;
  }

  /**
   * @param drawable
   */
  public void setSelectedDrawable(J3DDrawable drawable) {
    if (selectedDrawable != null)
      selectedDrawable.removeSelectedState();
    selectedDrawable = drawable;
  }
  
  public void setAntiAliasingEnabled(boolean s){
	  super.setAntiAliasingEnabled(s);
	  for (int i = 0; i < m_objects.size(); i++) {
		  MMCanvasObjectIF o = getObject(i);
		  o.getDisplayProperties().setAntiAliasing(s);
	  }
	  renderScene();
  }
  
  public void addObject(MMCanvasObjectIF object){
	  object.getDisplayProperties().setAntiAliasing(antiAliasingCheckBox.isSelected());
	  super.addObject(object);
  }
  
  public void cleanup(){
//	 m_universe.cleanup();
	  if(m_canvas3D.isOffScreen()){
	 m_canvas3D.setOffScreenBuffer(null);}
	  View v = m_canvas3D.getView();
	 v.removeAllCanvas3Ds();
	 v.attachViewPlatform(null);
	 m_universe.removeAllLocales();
	 m_canvas3D = null;
  }
  
  public BufferedImage getSnapshot(){
	  GraphicsContext3D ctx = m_canvas3D.getGraphicsContext3D();
	  Dimension scrDim = m_canvas3D.getSize();

	// setting raster component
	Raster ras =
	new javax.media.j3d.Raster(
	new javax.vecmath.Point3f(-1.0f, -1.0f, -1.0f),
	javax.media.j3d.Raster.RASTER_COLOR,
	0,
	0,
	scrDim.width,
	scrDim.height,
	new javax.media.j3d.ImageComponent2D(
	javax.media.j3d.ImageComponent.FORMAT_RGB,
	new java.awt.image.BufferedImage(scrDim.width, scrDim.height, java.awt.image.BufferedImage.TYPE_INT_RGB)),
	null);
	
	ctx.readRaster(ras);
	BufferedImage img = ras.getImage().getImage();
	return img;
  }
  
  public void setBackground(Color c){
	  super.setBackground(c);
	  if(m_background==null)return;//Canvas not yet inited
	  m_background.setColor(new Color3f(c));
  }
}







