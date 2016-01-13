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
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Geometry;
import javax.media.j3d.Group;
import javax.media.j3d.PointArray;
import javax.media.j3d.PointAttributes;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;

import net.mumie.mathletfactory.display.CanvasDrawable;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.PointDisplayProperties;
import net.mumie.mathletfactory.math.geom.affine.Affine3DPoint;

import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickTool;

/**
 *  This class acts as a drawable for a single 3d point.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class J3DPointDrawable extends J3DRemovableDrawable {
  
  private float pointRadius = 1.01f;
  private PointArray m_point = new PointArray(1,1);
  private Appearance m_appearance = new Appearance();
  private ColoringAttributes m_coloringAttributes = new ColoringAttributes();
  private PointAttributes m_pointAttributes = new PointAttributes();
  private boolean m_isSelected = false;
  private Shape3D m_pointShape = new Shape3D();
  private boolean antiAliasing = true;
  private Color c;
  private int radius;
  /**
   * Initializes the part of the scenegraph for which this drawable is responsible.  
   * @see net.mumie.mathletfactory.display.j3d.J3DRemovableDrawable#createShape(DisplayProperties)
   */
  protected void createShape(DisplayProperties properties){
    c = properties.getObjectColor();
    radius = (int)properties.getDoubleProperty(PointDisplayProperties.POINT_RADIUS_PROPERTY, 3);
	  // to pick objects:
    m_pickCanvas = new PickCanvas((Canvas3D)getCanvas().getDrawingBoard(),
                                  m_shape);
    
    m_pickCanvas.setMode(PickTool.GEOMETRY);
    m_pickCanvas.setTolerance((float)PICKING_TOLERANCE);
    
    pointRadius = getPointRadius(properties);
    if(properties.getObjectColor() != null)
      m_coloringAttributes.setColor(new Color3f(properties.getObjectColor()));
    m_appearance.setColoringAttributes(m_coloringAttributes);
    m_point.setCoordinate(0, new float[]{0,0,0});
    //m_point.setCapability()
    m_pointAttributes.setPointSize(5*pointRadius);
    if(antiAliasing){
    	m_pointAttributes.setPointAntialiasingEnable(true);
    }else{
    	m_pointAttributes.setPointAntialiasingEnable(false);
    }
    m_appearance.setPointAttributes(m_pointAttributes);
    m_pointShape.setGeometry(m_point);
    m_pointShape.setAppearance(m_appearance);
    m_transformGroup.addChild(m_pointShape);
    m_shape.addChild(m_transformGroup);
    m_point.setCapability(Geometry.ALLOW_INTERSECT);
    m_shape.setCapability(Group.ENABLE_PICK_REPORTING);
    m_appearance.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
    m_coloringAttributes.setCapability(ColoringAttributes.ALLOW_COLOR_READ);
    m_coloringAttributes.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);
    m_pointShape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
  }
  
  /** Sets the affine coordinates for this point. */
  public CanvasDrawable setPoint(Affine3DPoint worldPoint) {
    Vector3d position = getPosition();
    position.set((float)worldPoint.getXAsDouble(),
                   (float)worldPoint.getYAsDouble(),
                   (float)worldPoint.getZAsDouble());
    setPosition(position);
    return this;
  }
  
  /** Sets the affine coordinates for this point. */
  public CanvasDrawable setPoint(double[] worldPoint) {
    Vector3d position = getPosition();
    position.set(worldPoint);
    setPosition(position);
    return this;
  }
 
  /** Returns the point radius as specified by the given properties. */
  protected float getPointRadius(DisplayProperties properties) { 
    if (properties instanceof PointDisplayProperties)
      return  ((PointDisplayProperties)properties).getPointRadius();
    	return 3;
//    return  5 * PointDisplayProperties.DEFAULT.getPointRadius();
  }
  
  public boolean isSelectedState() {
  	return m_isSelected;
  }
  
  public void setSelectedState(){
    m_isSelected = true;
    Appearance selectedAppearance = getCanvas().getSelectedAppearance();
    selectedAppearance.setPointAttributes(m_pointAttributes);
    m_pointShape.setAppearance(selectedAppearance);
    getCanvas().getSelectionBehavior().setColor(m_coloringAttributes);
    getCanvas().getSelectionBehavior().postId(SelectionBehavior.SELECTION_POSTID);
  }
  
  public void removeSelectedState(){
    m_isSelected = false;
    m_pointShape.setAppearance(m_appearance);
  }
  
  public void render(DisplayProperties props){
	  super.render(props);
	  if(!(c.equals(props.getObjectColor())&&radius==(int)props.getDoubleProperty(PointDisplayProperties.POINT_RADIUS_PROPERTY, 3))){
		  resetShape();
	  }
	  if(antiAliasing!=props.isAntiAliasing()){
		  antiAliasing = props.isAntiAliasing();
		  resetShape();
	  }
  }
}
