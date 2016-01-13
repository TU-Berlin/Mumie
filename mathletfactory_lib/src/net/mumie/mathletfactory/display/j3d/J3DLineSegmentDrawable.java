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

import java.awt.Color;

import javax.media.j3d.Alpha;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Geometry;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.PositionInterpolator;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import net.mumie.mathletfactory.display.CanvasDrawable;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.LineDisplayProperties;

import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickTool;

/**
 *  This class acts as a drawable for a finite line segment with an optional arrow 
 *  at the head.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class J3DLineSegmentDrawable extends J3DRemovableDrawable {
  
  private final Point3d m_initialPoint = new Point3d();
  private final Point3d m_endPoint = new Point3d();
  private final Vector3d m_lineVector = new Vector3d();
//  private final Vector3d m_arrowHeadDir = new Vector3d();
//  private final Vector3d m_arrowBaseX = new Vector3d();
//  private final Vector3d m_arrowBaseY = new Vector3d();
  private final Shape3D m_lineShape = new Shape3D();
  private LineArray m_line = new LineArray(2, LineArray.COORDINATES);
  private final Appearance m_appearance = new Appearance();
  private final Material m_material = new Material();
  private final ColoringAttributes m_coloringAttributes = new ColoringAttributes();
  private final LineAttributes m_lineAttributes = new LineAttributes();
  private TransparencyAttributes m_transparency = new TransparencyAttributes();
  private final Point3d lineEnd = new Point3d();
  private Cone arrowHead;
  private float height;
//  private final Point3d m_coord = new Point3d(), m_previousCoord = new Point3d();
//  private TriangleFanArray m_arrowGeometry = new TriangleFanArray(6, TriangleFanArray.COORDINATES | TriangleFanArray.NORMALS, new int[]{6});
//  private final PolygonAttributes m_polygonAttributes = new PolygonAttributes();
//  private Shape3D m_arrowShape = new Shape3D();
  private boolean m_isSelected = false;
  private boolean antiAliasing = true;
  private Color c;
  private double lineWidth;
  private double transp;
  private boolean dashed;
  private double tmode;
  private TransformGroup pITG;
  private Vector3d labelPos = new Vector3d();
  /** 
   * Constructs the drawable. the actual scenegraph part will be created in 
   * the first call of {@link #getShape}. 
   */  
  public J3DLineSegmentDrawable(){
    m_shape.setCapability(BranchGroup.ENABLE_PICK_REPORTING);
    m_lineShape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
//    m_arrowShape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
    m_line.setCapability(Geometry.ALLOW_INTERSECT);
//    m_arrowGeometry.setCapability(Geometry.ALLOW_INTERSECT);
//    m_arrowShape.setAppearanceOverrideEnable(true);
    m_lineShape.setAppearanceOverrideEnable(true);
//    m_arrowShape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
    m_lineShape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
    m_appearance.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
    m_coloringAttributes.setCapability(ColoringAttributes.ALLOW_COLOR_READ);
    m_coloringAttributes.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);
    m_transparency.setCapability(TransparencyAttributes.ALLOW_MODE_WRITE);
    m_transparency.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);
    m_lineAttributes.setCapability(LineAttributes.ALLOW_WIDTH_WRITE);
  }
  
  /** Sets the initial and end point of the line segment. */
  public CanvasDrawable setPoints(double[] initialPoint, double[] endPoint) {
    if(isEqual(initialPoint, m_initialPoint) && isEqual(endPoint, m_endPoint))
      return this;
    // if the scenegraph is life, detach branchgroup
    if(m_shape != null && m_shape.isLive())
      resetShape();
    m_initialPoint.set(initialPoint[0], initialPoint[1], initialPoint[2]);
    m_endPoint.set(endPoint[0], endPoint[1], endPoint[2]); 
    m_lineVector.sub(m_endPoint, m_initialPoint);
//    m_arrowHeadDir.x = m_lineVector.x;
//    m_arrowHeadDir.y = m_lineVector.y;
//    m_arrowHeadDir.z = m_lineVector.z;
//    m_arrowHeadDir.normalize();
//    if(lineLength()<1){
//    	m_arrowHeadDir.scale(0.15f);
//    }else{  
//    	m_arrowHeadDir.scale(0.3f);
//    }
    
    // m_arrowBase{X,Y} are orthogonal to m_arrowHeadDir
//    m_arrowBaseX.x = m_arrowHeadDir.y;
//    m_arrowBaseX.y = -m_arrowHeadDir.x;
//    m_arrowBaseY.x = -m_arrowHeadDir.z;
//    m_arrowBaseY.z = m_arrowHeadDir.x;
//    if(m_arrowBaseY.length() != 0)
//      m_arrowBaseX.cross(m_arrowHeadDir, m_arrowBaseY);
//    if(m_arrowBaseX.length() != 0)
//      m_arrowBaseY.cross(m_arrowHeadDir, m_arrowBaseX);
//    
//    m_arrowBaseX.normalize();
//    m_arrowBaseY.normalize();
//    if(lineLength()<1){
//   	 m_arrowBaseX.scale(0.04f);//0.05
//   	 m_arrowBaseY.scale(0.04f);
//   }else{
//   	m_arrowBaseX.scale(0.075f);//0.05
//   	m_arrowBaseY.scale(0.075f);
//   }
    return this;
  }
  
  /**
   * Initializes the part of the scenegraph for which this drawable is responsible.  
   * @see net.mumie.mathletfactory.display.j3d.J3DRemovableDrawable#createShape(DisplayProperties)
   */
  protected void createShape(DisplayProperties properties){
	  height = (float)lineLength()/6;
//	  if(height<0.1f){
//		  height = 0.1f;
//	  }
	  if(height>0.4f){
		  height = 0.4f;
	  }
	  c = properties.getObjectColor();
	  lineWidth = properties.getDoubleProperty(LineDisplayProperties.LINE_WIDTH_PROPERTY, 3);
	  transp = properties.getTransparency();
	  dashed = properties.isDashed();
	  tmode = properties.getDoubleProperty(J3DRenderingHints.J3D_TRANSP_MODE_PROPERTY, J3DRenderingHints.BLENDED);
    m_material.setAmbientColor(new Color3f(properties.getObjectColor()));
    m_material.setDiffuseColor(new Color3f(properties.getObjectColor()));
    m_material.setLightingEnable(true);
    m_appearance.setMaterial(m_material);
    m_coloringAttributes.setColor(new Color3f(properties.getObjectColor()));
    m_transparency.setTransparency((float)properties.getTransparency());
    if(m_transparency.getTransparency()!=0f){
    	m_transparency.setTransparencyMode((int)tmode);
    }else{
    	m_transparency.setTransparencyMode(TransparencyAttributes.NONE);
    }
    m_appearance.setTransparencyAttributes(m_transparency);
    m_appearance.setColoringAttributes(m_coloringAttributes);
    
    // --------- draw the line ----------
    m_line.setCoordinate(0, m_initialPoint);
    double scale = (lineLength()-(height/3))/lineLength();
    if(getArrowAtEnd(properties))
      m_lineVector.scale(scale);  // scale, to hide the line end under the arrow pyramid
    lineEnd.add(m_initialPoint, m_lineVector);
    m_line.setCoordinate(1, lineEnd);
    if(getArrowAtEnd(properties))
      m_lineVector.scale(1/scale); // rescale
    m_lineAttributes.setLineWidth((float)lineWidth);
    if(antiAliasing){
    	m_lineAttributes.setLineAntialiasingEnable(true);
    }else{
    	m_lineAttributes.setLineAntialiasingEnable(false);
    }
    if(dashed){
    	m_lineAttributes.setLinePattern(LineAttributes.PATTERN_DASH);
    }else{
    	m_lineAttributes.setLinePattern(LineAttributes.PATTERN_SOLID);
    }
    m_appearance.setLineAttributes(m_lineAttributes);
    m_lineShape.setGeometry(m_line);
    if(m_isSelected)
      m_lineShape.setAppearance(getCanvas().getSelectedAppearance());
    else
      m_lineShape.setAppearance(m_appearance);
    
    m_transformGroup.addChild(m_lineShape);
    
    
    // --------- draw the arrow head ----------
    if(getArrowAtEnd(properties) && lineLength()!=0){
      // draw a pyramid with the point at m_endPoint
    	arrowHead = new Cone(height/3,height);
    	arrowHead.getShape(Cone.BODY).setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
    	arrowHead.getShape(Cone.CAP).setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
//      m_coord.set(m_endPoint);
//      m_arrowGeometry.setCoordinate(0, m_coord);
//      m_coord.sub(m_coord, m_arrowHeadDir);
//      
//      m_coord.sub(m_arrowBaseX);
//      m_previousCoord.set(m_coord);
//      m_coord.sub(m_arrowBaseY);
//      m_previousCoord.set(m_coord);
//      m_arrowGeometry.setCoordinate(1, m_coord);
//      m_arrowGeometry.setNormal(1, generateNormal(m_endPoint, m_previousCoord, m_coord));
//      m_previousCoord.set(m_coord);
//      m_coord.add(m_arrowBaseY);
//      m_coord.add(m_arrowBaseY);
//      m_arrowGeometry.setCoordinate(2, m_coord);
//      m_arrowGeometry.setNormal(2, generateNormal(m_endPoint, m_previousCoord, m_coord));
//      m_previousCoord.set(m_coord);
//      m_coord.add(m_arrowBaseX);
//      m_coord.add(m_arrowBaseX);
//      m_arrowGeometry.setCoordinate(3, m_coord);
//      m_arrowGeometry.setNormal(3, generateNormal(m_endPoint, m_previousCoord, m_coord));
//      m_previousCoord.set(m_coord);
//      m_coord.sub(m_arrowBaseY);
//      m_coord.sub(m_arrowBaseY);
//      m_arrowGeometry.setCoordinate(4, m_coord);
//      m_arrowGeometry.setNormal(4, generateNormal(m_endPoint, m_previousCoord, m_coord));
//      m_previousCoord.set(m_coord);
//      m_coord.sub(m_arrowBaseX);
//      m_coord.sub(m_arrowBaseX);
//      m_arrowGeometry.setCoordinate(5, m_coord);
//      m_arrowGeometry.setNormal(5, generateNormal(m_endPoint, m_previousCoord, m_coord));
      
      
      // because we can not predict the orientation of the polygons in space,
      // we have to disable culling for the arrow head
//      m_polygonAttributes.setCullFace(PolygonAttributes.CULL_NONE);
      
//      m_appearance.setPolygonAttributes(m_polygonAttributes);
//      m_arrowShape.setGeometry(m_arrowGeometry);
//      if(m_isSelected)
//        m_arrowShape.setAppearance(getCanvas().getSelectedAppearance());
//      else
//        m_arrowShape.setAppearance(m_appearance);
//      m_arrowShape = c.getShape(1);
//      m_transformGroup.addChild(m_arrowShape);
      arrowHead.setAppearance(m_appearance);
      if(m_isSelected) arrowHead.setAppearance(getCanvas().getSelectedAppearance());
      Transform3D c_trafo = new Transform3D();
//      c_trafo.setScale(0.2);
      Vector3d orig = new Vector3d(0,1,0);
      Vector3d dir = new Vector3d(m_endPoint.x-m_initialPoint.x,m_endPoint.y-m_initialPoint.y,m_endPoint.z-m_initialPoint.z);
      double angle = orig.angle(dir);
      Vector3d axis = new Vector3d(0,0,1);
      if(!(dir.getX()==0&&dir.getY()<0&&dir.getZ()==0)){
    	  axis.cross(orig, dir);
      }
      c_trafo.setRotation(new AxisAngle4d(axis,angle));
      double abs = lineLength();
      if(abs>0.0001){
		  scale = (abs-(height/2))/abs;
		  dir.scale(scale);
	      dir.add(m_initialPoint);
//	      System.out.println(dir);
	      c_trafo.setTranslation(dir);
      }
      TransformGroup tg = new TransformGroup();
      tg.setTransform(c_trafo);
      tg.addChild(arrowHead);
      m_transformGroup.addChild(tg);
    }
    //label shape
//    if(properties.isLabelDisplayed()){
//    	labelPos.set(m_endPoint);
//    	double[] pos = (double[])properties.getProperty(J3DRenderingHints.J3D_LABEL_POSITION);
//    	if(pos!=null)labelPos.set(pos);
//    	Shape3D labelShape3D = getLabelShape(, labelApp, pos)
//    }
    //PositionInterpolator
    Alpha alpha = (Alpha)properties.getProperty(J3DRenderingHints.J3D_POSITION_ALPHA, null);
    float start = (float)properties.getDoubleProperty(J3DRenderingHints.J3D_POSITION_START, 0);
    float end = (float)properties.getDoubleProperty(J3DRenderingHints.J3D_POSITION_END, 0);
//    System.out.println("start,end="+start+","+end);
    if(alpha!=null){
//    	System.out.println("juhuuu");
    	Transform3D trafo = new Transform3D();
    	pITG = new TransformGroup();
    	pITG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    	PositionInterpolator pI = new PositionInterpolator(alpha,pITG,trafo,start,end);
    	pI.setSchedulingBounds(new BoundingSphere());
//    	m_shape.removeChild(m_transformGroup);
    	pITG.addChild(m_transformGroup);
    	pITG.addChild(pI);
    	m_shape.addChild(pITG);
    }else{ m_shape.addChild(m_transformGroup);}
    
    m_pickCanvas = new PickCanvas((Canvas3D)getCanvas().getDrawingBoard(),
                                  m_shape);
    m_pickCanvas.setMode(PickTool.GEOMETRY);
    m_pickCanvas.setTolerance((float)PICKING_TOLERANCE);
  }
  
 /**
   *  Picking only works on geometry, that has been build from scratch, so
   *  after a reshaping of a line a rebuild is necessary.
   * 
   * @see net.mumie.mathletfactory.display.j3d.J3DDrawable#updateFinished()
   */
  public void updateFinished(){
    m_line = new LineArray(2, LineArray.COORDINATES);
    m_line.setCapability(Geometry.ALLOW_INTERSECT);
//    m_arrowGeometry.setCapability(Geometry.ALLOW_INTERSECT);
//    // rebuild shape in the next render() call
//    resetShape();
  }
  
  /**
   * Returns the euclidean length of the current line segment.
   */
  protected double lineLength() {
    return m_lineVector.length();
  }
  
  /**
   * Extracts from the given display properties, whether an Arrow should be at 
   * the head of this line segment.
   */
  protected boolean getArrowAtEnd(DisplayProperties properties) {
    if (properties instanceof LineDisplayProperties)
      return ((LineDisplayProperties)properties).getArrowAtEnd() != LineDisplayProperties.ARC_END;
    return false;
  }
  
  /**
   * Extracts from the given display properties the width of the line.
   */
  protected double getLineWidth(DisplayProperties properties) {
    if (properties instanceof LineDisplayProperties)
      return ((LineDisplayProperties)properties).getLineWidth();
    return LineDisplayProperties.DEFAULT.getLineWidth();
  }
  
  public void setSelectedState(){
    m_isSelected = true;
    Appearance selectedAppearance = getCanvas().getSelectedAppearance();
    selectedAppearance.setLineAttributes(m_lineAttributes);
//    selectedAppearance.setPolygonAttributes(m_polygonAttributes);
    m_lineShape.setAppearance(selectedAppearance);
    arrowHead.setAppearance(selectedAppearance);
//    m_arrowShape.setAppearance(selectedAppearance);
    getCanvas().getSelectionBehavior().setColor(m_coloringAttributes);
    getCanvas().getSelectionBehavior().postId(SelectionBehavior.SELECTION_POSTID);
  }
  
 public void removeSelectedState(){
    m_isSelected = false;
    m_lineShape.setAppearance(m_appearance);
    arrowHead.setAppearance(m_appearance);
  }
 
 public void render(DisplayProperties props){
	  super.render(props);
	  if(!(c.equals(props.getObjectColor()) && lineWidth==props.getDoubleProperty(LineDisplayProperties.LINE_WIDTH_PROPERTY, 3)
		   && transp==props.getTransparency() && dashed==props.isDashed())
		   || tmode !=props.getDoubleProperty(J3DRenderingHints.J3D_TRANSP_MODE_PROPERTY, J3DRenderingHints.BLENDED)){
		  resetShape();
	  }
	  if(antiAliasing!=props.isAntiAliasing()){
		  antiAliasing = props.isAntiAliasing();
		  resetShape();
	  }
 }
 public void removeShape(){
	 super.removeShape();
	 if(pITG!=null){
		 m_shape.removeChild(pITG);
		 pITG.removeChild(m_transformGroup);
	 }
 }
}


