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
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.TriangleFanArray;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

import net.mumie.mathletfactory.display.CanvasDrawable;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;

import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickTool;

/**
 *  This class acts as a drawable for an arbitrary polygon.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class J3DPolygonDrawable extends J3DRemovableDrawable {
  
  private Point3d m_points[], m_center;  
  private final Shape3D m_polygonShape = new Shape3D();
  private TriangleFanArray m_triangleFan;
  private final Appearance m_appearance = new Appearance();
  private final Material m_material = new Material();
  private final ColoringAttributes m_coloringAttributes = new ColoringAttributes();
  private boolean m_isSelected = false;
  private final TransparencyAttributes m_transparency = new TransparencyAttributes();
  
  private Color c;
  private double transp;
  private double tmode;
  /** 
   * Constructs the drawable. The actual scenegraph part will be created in 
   * the first call of {@link #getShape}. 
   */  
  public J3DPolygonDrawable(){
    m_shape.setCapability(BranchGroup.ENABLE_PICK_REPORTING);
    m_polygonShape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
    m_polygonShape.setAppearanceOverrideEnable(true);
    m_polygonShape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
    m_appearance.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
    m_coloringAttributes.setCapability(ColoringAttributes.ALLOW_COLOR_READ);
    m_coloringAttributes.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);
    m_transparency.setCapability(TransparencyAttributes.ALLOW_MODE_WRITE);
    m_transparency.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);
  }
  
  /** Sets the points of the polygon. */
  public CanvasDrawable setPoints(NumberTuple[] points) {
    if(m_points == null || m_points.length != points.length)
      m_points = new Point3d[points.length];
    //System.out.println(m_points.length);
    if(m_triangleFan == null || m_triangleFan.getVertexCount() != points.length)
      m_triangleFan = new TriangleFanArray(2*(m_points.length+2), TriangleFanArray.COORDINATES, 
                                           new int[]{m_points.length+2, m_points.length+2});
    // if the scenegraph is life, detach branchgroup
    if(m_shape != null && m_shape.isLive())
      resetShape();
    for(int i=0;i<m_points.length;i++){    
      if(m_points[i] == null)
        m_points[i] = new Point3d();
      m_points[i].set(points[i].getEntry(1).getDouble(), points[i].getEntry(2).getDouble(), points[i].getEntry(3).getDouble());
    }
    if(m_center == null)
      m_center = new Point3d();
    m_center.set(m_points[0]);
    for(int i=1;i<m_points.length;i++)
      m_center.add(m_points[i]);    
    m_center.scale(1/(double)m_points.length);
    //System.out.println("center of "+m_points.length+" is "+m_center);
    return this;
  }
  
  /**
   * Initializes the part of the scenegraph for which this drawable is responsible.  
   * @see net.mumie.mathletfactory.display.j3d.J3DRemovableDrawable#createShape(DisplayProperties)
   */
  protected void createShape(DisplayProperties properties){
	  c = properties.getObjectColor();
	  transp = properties.getTransparency();
	  tmode = properties.getDoubleProperty(J3DRenderingHints.J3D_TRANSP_MODE_PROPERTY, J3DRenderingHints.BLENDED);
    m_material.setAmbientColor(new Color3f(properties.getObjectColor()));
    m_material.setDiffuseColor(new Color3f(properties.getObjectColor()));
    m_material.setLightingEnable(true);
    m_appearance.setMaterial(m_material);
    m_transparency.setTransparency((float)properties.getTransparency());
    if(m_transparency.getTransparency()!=0f){
    	m_transparency.setTransparencyMode((int)tmode);
    }else{
    	m_transparency.setTransparencyMode(TransparencyAttributes.NONE);
    }
    m_appearance.setTransparencyAttributes(m_transparency);
    m_coloringAttributes.setColor(new Color3f(properties.getObjectColor()));
    m_appearance.setColoringAttributes(m_coloringAttributes);
    m_shape.addChild(m_transformGroup);
    m_triangleFan.setCoordinate(0, m_center);
    m_triangleFan.setCoordinate(m_points.length+2, m_center);
    // --------- draw the polygon ----------
    for(int i=0;i<m_points.length;i++){    
      m_triangleFan.setCoordinate(i, m_points[i]);
      m_triangleFan.setCoordinate(2*m_points.length+3-i, m_points[i]);
    }
    m_triangleFan.setCoordinate(m_points.length,m_points[0]);
    m_triangleFan.setCoordinate(m_points.length+3,m_points[0]);
    //System.out.println("polyon has "+m_triangleFan.getVertexCount()+", "+m_points.length+" points");

    m_polygonShape.setGeometry(m_triangleFan);
    m_triangleFan.setCapability(Geometry.ALLOW_INTERSECT);
    m_triangleFan.setCapability(TriangleFanArray.ALLOW_COUNT_READ);
    if(m_isSelected)
      m_polygonShape.setAppearance(getCanvas().getSelectedAppearance());
    else
      m_polygonShape.setAppearance(m_appearance);
    
    m_transformGroup.addChild(m_polygonShape);
              
    m_pickCanvas = new PickCanvas((Canvas3D)getCanvas().getDrawingBoard(),
                                  m_shape);
    m_pickCanvas.setMode(PickTool.GEOMETRY);
    m_pickCanvas.setTolerance((float)PICKING_TOLERANCE);
  }
  
 /**
   *  Picking only works on geometry, that has been build from scratch, so
   *  after a reshaping of a polygon a rebuild is necessary.
   * 
   * @see net.mumie.mathletfactory.display.j3d.J3DDrawable#updateFinished()
   */
  public void updateFinished(){
    //m_line = new LineArray(100, LineArray.COORDINATES);
    //m_line.setCapability(Geometry.ALLOW_INTERSECT);
    // rebuild shape in the next render() call
    resetShape();
  }
  
  public double[] getCenter(){
    if(m_center != null)
      return new double[]{m_center.x, m_center.y, m_center.z};
    return new double[]{0,0,0};
  }
        
  public void setSelectedState(){
    m_isSelected = true;
    Appearance selectedAppearance = getCanvas().getSelectedAppearance();
    //selectedAppearance.setLineAttributes(m_lineAttributes);
    m_polygonShape.setAppearance(selectedAppearance);
    getCanvas().getSelectionBehavior().setColor(m_coloringAttributes);
    getCanvas().getSelectionBehavior().postId(SelectionBehavior.SELECTION_POSTID);
  }
  
 public void removeSelectedState(){
    m_isSelected = false;
    m_polygonShape.setAppearance(m_appearance);
  }
 
 public void render(DisplayProperties props){
	 super.render(props);
	 if(!(c.equals(props.getObjectColor())&&transp==props.getTransparency())
			 ||tmode != props.getDoubleProperty(J3DRenderingHints.J3D_TRANSP_MODE_PROPERTY, J3DRenderingHints.BLENDED)){
		 resetShape();
	 }
 }
}

