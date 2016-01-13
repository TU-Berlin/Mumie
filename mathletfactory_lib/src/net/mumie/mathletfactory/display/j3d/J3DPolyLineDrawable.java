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

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Geometry;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

import net.mumie.mathletfactory.display.CanvasDrawable;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.LineDisplayProperties;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;

import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickTool;

/**
 *  This class acts as a drawable for a finite polyline.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class J3DPolyLineDrawable extends J3DRemovableDrawable {
  
  private Point3d[] m_points;
  private final Shape3D m_lineShape = new Shape3D();
  private LineArray m_line;
  private final Appearance m_appearance = new Appearance();
  private final Material m_material = new Material();
  private final ColoringAttributes m_coloringAttributes = new ColoringAttributes();
  private final LineAttributes m_lineAttributes = new LineAttributes();
  private final TransparencyAttributes m_transparency = new TransparencyAttributes();
  private boolean m_isSelected = false;
  private boolean antiAliasing = true;
  private Color c;
  private double lineWidth;
  private double transp;
  private boolean dashed;
  private double tmode;
  /** 
   * Constructs the drawable. the actual scenegraph part will be created in 
   * the first call of {@link #getShape}. 
   */  
  public J3DPolyLineDrawable(){
    m_shape.setCapability(BranchGroup.ENABLE_PICK_REPORTING);
    m_lineShape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
    m_lineShape.setAppearanceOverrideEnable(true);
    m_lineShape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
    m_appearance.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
    m_coloringAttributes.setCapability(ColoringAttributes.ALLOW_COLOR_READ);
    m_coloringAttributes.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);
    m_transparency.setCapability(TransparencyAttributes.ALLOW_MODE_WRITE);
    m_transparency.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);
  }
  
  /** Sets the initial and end point of the line segment. */
  public CanvasDrawable setPoints(NumberTuple[] points) {
    if(m_points == null || m_points.length != points.length)
      m_points = new Point3d[points.length];
    //System.out.println(m_points.length);
    if(m_line == null || m_line.getVertexCount() != points.length)
      m_line = new LineArray(m_points.length*2, LineArray.COORDINATES);
    // if the scenegraph is life, detach branchgroup
    if(m_shape != null && m_shape.isLive())
      resetShape();
    for(int i=0;i<m_points.length;i++){    
      if(m_points[i] == null)
        m_points[i] = new Point3d();
      m_points[i].set(points[i].getEntry(1).getDouble(), points[i].getEntry(2).getDouble(), points[i].getEntry(3).getDouble());
    }
    return this;
  }
  
  /**
   * Initializes the part of the scenegraph for which this drawable is responsible.  
   * @see net.mumie.mathletfactory.display.j3d.J3DRemovableDrawable#createShape(DisplayProperties)
   */
  protected void createShape(DisplayProperties properties){
	  c = properties.getObjectColor();
	  transp = properties.getTransparency();
	  lineWidth = getLineWidth(properties);
	  dashed = properties.isDashed();
	  tmode = properties.getDoubleProperty(J3DRenderingHints.J3D_TRANSP_MODE_PROPERTY, J3DRenderingHints.BLENDED);
    m_material.setAmbientColor(new Color3f(properties.getObjectColor()));
    m_material.setDiffuseColor(new Color3f(properties.getObjectColor()));
    m_material.setLightingEnable(true);
    m_appearance.setMaterial(m_material);
    m_coloringAttributes.setColor(new Color3f(properties.getObjectColor()));
    m_appearance.setColoringAttributes(m_coloringAttributes);
    m_shape.addChild(m_transformGroup);
    m_line.setCoordinate(0, m_points[0]);
    // --------- draw the line ----------
    for(int i=2;i<m_points.length*2-1;i++){    
      if(m_points[i/2] == null)
        m_points[i/2] = new Point3d();
      m_line.setCoordinate(i-1, m_points[i/2]);
    }
    m_line.setCoordinate(m_points.length*2-2, m_points[m_points.length-2]);
    m_line.setCoordinate(m_points.length*2-1, m_points[m_points.length-1]);
    m_line.setCoordinate(1, m_points[1]);
    //System.out.println("line has "+m_line.getVertexCount()+", "+m_points.length+" points");

    m_lineAttributes.setLineWidth((float)lineWidth);
    if(antiAliasing) {
    	m_lineAttributes.setLineAntialiasingEnable(true);
    }else{
    	m_lineAttributes.setLineAntialiasingEnable(false);
    }
    if(dashed){
    	m_lineAttributes.setLinePattern(LineAttributes.PATTERN_DOT);
    }else{
    	m_lineAttributes.setLinePattern(LineAttributes.PATTERN_SOLID);
    }
    m_appearance.setLineAttributes(m_lineAttributes);
    m_lineShape.setGeometry(m_line);
    m_line.setCapability(Geometry.ALLOW_INTERSECT);
    m_line.setCapability(LineArray.ALLOW_COUNT_READ);
    m_transparency.setTransparency((float)properties.getTransparency());
    if(m_transparency.getTransparency()!=0f){
    	m_transparency.setTransparencyMode((int)tmode);
    }else{
    	m_transparency.setTransparencyMode(TransparencyAttributes.NONE);
    }
    m_appearance.setTransparencyAttributes(m_transparency);
    if(m_isSelected)
      m_lineShape.setAppearance(getCanvas().getSelectedAppearance());
    else
      m_lineShape.setAppearance(m_appearance);
    
    m_transformGroup.addChild(m_lineShape);
              
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
    //m_line = new LineArray(100, LineArray.COORDINATES);
    //m_line.setCapability(Geometry.ALLOW_INTERSECT);
    // rebuild shape in the next render() call
    resetShape();
  }
      
  /**
   * Extracts from the given display properties the width of the line.
   */
  protected double getLineWidth(DisplayProperties properties) {
    return(properties.getDoubleProperty(LineDisplayProperties.LINE_WIDTH_PROPERTY, 3));
  }
  
  public void setSelectedState(){
    m_isSelected = true;
    Appearance selectedAppearance = getCanvas().getSelectedAppearance();
    selectedAppearance.setLineAttributes(m_lineAttributes);
    m_lineShape.setAppearance(selectedAppearance);
    getCanvas().getSelectionBehavior().setColor(m_coloringAttributes);
    getCanvas().getSelectionBehavior().postId(SelectionBehavior.SELECTION_POSTID);
  }
  
 public void removeSelectedState(){
    m_isSelected = false;
    m_lineShape.setAppearance(m_appearance);
  }
 
 public void render(DisplayProperties props){
	  super.render(props);
	  if(!(c.equals(props.getObjectColor()))||transp!=props.getTransparency()||
			  lineWidth!=getLineWidth(props)||dashed!=props.isDashed()
			  || tmode != props.getDoubleProperty(J3DRenderingHints.J3D_TRANSP_MODE_PROPERTY, J3DRenderingHints.BLENDED)){
		  resetShape();
	  }
	  if(antiAliasing!=props.isAntiAliasing()){
		  antiAliasing = props.isAntiAliasing();
		  resetShape();
	  }
 }
}

