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
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;

import net.mumie.mathletfactory.display.DisplayProperties;

import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickTool;

/**
 *  This class is the base for all J3DDrawables which render an arbitrary 
 *  (curved) surface in 3d space.
 *  
 *  @author Paehler
 *  @mm.docstatus finished
 */
public abstract class J3DSurfaceDrawable extends J3DRemovableDrawable {
  
  private Shape3D m_valueShape = new Shape3D();
  private Appearance m_appearance = new Appearance();
  private TransparencyAttributes m_transparency = new TransparencyAttributes();
  private boolean m_isSelected = false;
  private final PolygonAttributes m_polygonAttributes = new PolygonAttributes();
  private final ColoringAttributes m_coloringAttributes = new ColoringAttributes();
  private final Material m_material = new Material();
  private Color c;
  private double transp;
  private double tmode;
  
  /** 
   *  Constructs the drawable. It is fully operable after {@link #getShape} 
   *  has been called for the first time.
   */
  public J3DSurfaceDrawable(){
    m_shape.setCapability(BranchGroup.ENABLE_PICK_REPORTING);
    m_valueShape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
    m_valueShape.setAppearanceOverrideEnable(true);
    m_valueShape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
    m_appearance.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
    m_coloringAttributes.setCapability(ColoringAttributes.ALLOW_COLOR_READ);
    m_coloringAttributes.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);
    m_transparency.setCapability(TransparencyAttributes.ALLOW_MODE_WRITE);
    m_transparency.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);
    m_polygonAttributes.setCapability(PolygonAttributes.ALLOW_CULL_FACE_WRITE);
    m_polygonAttributes.setCullFace(PolygonAttributes.CULL_NONE);
  }
  /**
    * Initializes the part of the scenegraph for which this drawable is responsible.  
    * @see net.mumie.mathletfactory.display.j3d.J3DRemovableDrawable#createShape(DisplayProperties)
    */
  protected void createShape(DisplayProperties properties){
    c = properties.getObjectColor();
    transp = properties.getTransparency();
    tmode = properties.getDoubleProperty(J3DRenderingHints.J3D_TRANSP_MODE_PROPERTY, J3DRenderingHints.BLENDED);
    // to pick objects:
    m_pickCanvas = new PickCanvas((Canvas3D)getCanvas().getDrawingBoard(),
                                  getCanvas().getContentBranch());
    m_pickCanvas.setMode(PickTool.GEOMETRY);
    m_pickCanvas.setTolerance((float)PICKING_TOLERANCE);
    
    if(properties.getObjectColor() != null){
      Color3f diffuseColor = new Color3f(properties.getObjectColor());
      //diffuseColor.scale(0.5f);
      m_material.setDiffuseColor(diffuseColor);
      m_appearance.setMaterial(m_material);
      m_coloringAttributes.setColor(new Color3f(properties.getObjectColor()));
    }
    m_transparency.setTransparency((float)properties.getTransparency());
    if(m_transparency.getTransparency()!=0f){
    	m_transparency.setTransparencyMode((int)tmode);
    }else{
    	m_transparency.setTransparencyMode(TransparencyAttributes.NONE);
    }
    m_valueShape.setGeometry(getGeometry());
    m_valueShape.getGeometry().setCapability(Geometry.ALLOW_INTERSECT);
    m_appearance.setPolygonAttributes(m_polygonAttributes);
    m_appearance.setColoringAttributes(m_coloringAttributes);    
    m_appearance.setTransparencyAttributes(m_transparency);
    m_valueShape.setAppearance(m_appearance);
    m_transformGroup.addChild(m_valueShape);
    m_shape.addChild(m_transformGroup);
  }
  
  /** This method is implemented by the subclass to create the specific geometry. */
  protected abstract Geometry getGeometry();
  
  public void setSelectedState(){
    m_isSelected = true;
    m_valueShape.setAppearance(getCanvas().getSelectedAppearance());
    getCanvas().getSelectionBehavior().setColor(m_coloringAttributes);
    getCanvas().getSelectionBehavior().postId(SelectionBehavior.SELECTION_POSTID);
  }
  
  public void removeSelectedState(){
    m_isSelected = false;
    m_valueShape.setAppearance(m_appearance);
  }
  
  public boolean isSelectedState() {
  	return m_isSelected;
  }
  
  public void render(DisplayProperties props){
	  super.render(props);
	  if(!(c.equals(props.getObjectColor()))||transp!=props.getTransparency()
			  || tmode != props.getDoubleProperty(J3DRenderingHints.J3D_TRANSP_MODE_PROPERTY, J3DRenderingHints.BLENDED))
		  resetShape();
  }
}

