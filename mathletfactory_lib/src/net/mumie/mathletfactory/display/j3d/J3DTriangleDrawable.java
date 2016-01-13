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

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Geometry;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.TriangleStripArray;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import net.mumie.mathletfactory.display.CanvasDrawable;
import net.mumie.mathletfactory.display.DisplayProperties;

import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickTool;

/**
 *  This class acts as a drawable for an arbitrary triangle in 3d space.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class J3DTriangleDrawable extends J3DRemovableDrawable
{
  /**
   *  Number of rows of subtriangles
   */
  private final int N;
  private final Point3d m_firstPoint = new Point3d();
  private final Point3d m_secondPoint = new Point3d();
  private final Point3d m_thirdPoint = new Point3d();
  private final Point3d m_firstToSecond = new Point3d();
  private final Point3d m_firstToThird = new Point3d();
  private final Point3d m_coord = new Point3d();
  private final Point3d m_tmp1 = new Point3d();
  private final Point3d m_tmp2 = new Point3d();
  private final Shape3D m_planeShape = new Shape3D();
  private TriangleStripArray m_plane = null;
  private final Appearance m_appearance = new Appearance();
  private final TransparencyAttributes m_transparency = new TransparencyAttributes();
  private final Material m_material = new Material();
  private final ColoringAttributes m_coloringAttributes = new ColoringAttributes();
  private final PolygonAttributes m_polygonAttributes = new PolygonAttributes();
  private boolean m_isSelected = false;
  private final int[] m_strips;
  private final int m_numOfVertices;
  
  /** 
   * Constructs the drawable. the actual scenegraph part will be created in 
   * the first call of {@link #getShape}. 
   */  
  public J3DTriangleDrawable(){
    N = 2;
    m_strips = new int[2*N];
    m_numOfVertices = 2*((N+1)*N+N);
    // initialize triangle plane
    for(int i=0; i<N; i++){
      m_strips[i] = 2*N -2*i +1;
      m_strips[2*N-1-i] = 2*N -2*i +1;
    }
    m_plane = new TriangleStripArray(m_numOfVertices, TriangleStripArray.COORDINATES
                                     | TriangleStripArray.NORMALS, m_strips);
    m_shape.setCapability(BranchGroup.ENABLE_PICK_REPORTING);
    m_planeShape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
    m_plane.setCapability(Geometry.ALLOW_INTERSECT);
    m_planeShape.setAppearanceOverrideEnable(true);
    m_planeShape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
    m_appearance.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
    m_coloringAttributes.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);
    m_coloringAttributes.setCapability(ColoringAttributes.ALLOW_COLOR_READ);
    m_transparency.setCapability(TransparencyAttributes.ALLOW_MODE_WRITE);
    m_transparency.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);
    m_polygonAttributes.setCapability(PolygonAttributes.ALLOW_CULL_FACE_WRITE);
    
  }
  
  /** Sets the three points of the triangle. */
  public CanvasDrawable setPoints(double[] firstPoint, double[] secondPoint, double[] thirdPoint) {
    if(isEqual(firstPoint, m_firstPoint) && isEqual(secondPoint, m_secondPoint)
       && isEqual(thirdPoint, m_thirdPoint))
      return this;
    // if the scenegraph is life, detach branchgroup
    if(m_shape != null && m_shape.isLive())
      resetShape();
    
    m_firstPoint.set(firstPoint[0], firstPoint[1], firstPoint[2]);
    m_secondPoint.set(secondPoint[0], secondPoint[1], secondPoint[2]);
    m_thirdPoint.set(thirdPoint[0], thirdPoint[1], thirdPoint[2]);
    /*
     m_firstPoint.set(0, 0, 0);
     m_secondPoint.set(1, 0, 0);
     m_thirdPoint.set(0, -1, 0.0);
     */
    return this;
  }

  /**
   * Initializes the part of the scenegraph for which this drawable is responsible.  
   * @see net.mumie.mathletfactory.display.j3d.J3DRemovableDrawable#createShape(DisplayProperties)
   */
  protected void createShape(DisplayProperties properties){
    m_material.setAmbientColor(new Color3f(properties.getObjectColor()));
    m_material.setDiffuseColor(new Color3f(properties.getObjectColor()));
    m_appearance.setMaterial(m_material);
    if(properties.getTransparency() != 0){
      m_transparency.setTransparency((float)properties.getTransparency());
      m_transparency.setTransparencyMode(TransparencyAttributes.SCREEN_DOOR); // BLENDED does not work for more than one face
      m_appearance.setTransparencyAttributes(m_transparency);
    }
    m_coloringAttributes.setColor(new Color3f(properties.getObjectColor()));
    m_appearance.setColoringAttributes(m_coloringAttributes);
    
    
    // set up geometry
    m_firstToSecond.set(m_secondPoint);
    m_firstToSecond.sub(m_firstPoint);
    m_firstToThird.set(m_thirdPoint);
    m_firstToThird.sub(m_firstPoint);
    int vertexCounter=0;
    Vector3f normal = generateNormal(m_firstPoint, m_secondPoint, m_thirdPoint);
    Vector3f negNormal = new Vector3f();
    negNormal.negate(normal);
    for(int i=0; i<N; i++)
      for(int j=0; j<2*(N-i)+1; j++){
        m_coord.set(m_firstPoint);
        m_tmp1.set(m_firstToSecond);
        m_tmp1.scale(( j%2 == 0 ? ((double)j)/(2*N) : ((double)(j-1))/(2*N)));
        m_tmp2.set(m_firstToThird);
        m_tmp2.scale(( j%2 == 0 ? ((double)i)/N : ((double)(i+1))/N));
        m_coord.add(m_tmp1);
        m_coord.add(m_tmp2);
        m_plane.setCoordinate(vertexCounter, m_coord);
        m_plane.setCoordinate(m_numOfVertices-1-vertexCounter, m_coord);
        m_plane.setNormal(vertexCounter, normal);
        m_plane.setNormal(m_numOfVertices-1-vertexCounter, negNormal);
        vertexCounter++;
      }
      
    m_appearance.setPolygonAttributes(m_polygonAttributes);
    m_planeShape.setGeometry(m_plane);
    if(m_isSelected)
      m_planeShape.setAppearance(getCanvas().getSelectedAppearance());
    else
      m_planeShape.setAppearance(m_appearance);
    
    m_transformGroup.addChild(m_planeShape);
    m_pickCanvas = new PickCanvas((Canvas3D)getCanvas().getDrawingBoard(),
                                  m_shape);
    m_pickCanvas.setMode(PickTool.GEOMETRY);
    m_pickCanvas.setTolerance((float)PICKING_TOLERANCE);
    m_shape.addChild(m_transformGroup);
  }
  
  /**
   *  Picking only works on geometry, that has been build from scratch, so
   *  after a moving a plane a rebuild is necessary.
   */
  public void updateFinished(){
    m_plane = new TriangleStripArray(m_numOfVertices, TriangleStripArray.COORDINATES
                                     | TriangleStripArray.NORMALS, m_strips);
    m_plane.setCapability(Geometry.ALLOW_INTERSECT);
    // rebuild shape in the next render() call
    resetShape();
  }

  public void setSelectedState(){
    m_isSelected = true;
    Appearance selectedAppearance = getCanvas().getSelectedAppearance();
    selectedAppearance.setPolygonAttributes(m_polygonAttributes);
    selectedAppearance.setTransparencyAttributes(m_transparency);
    m_planeShape.setAppearance(selectedAppearance);
    getCanvas().getSelectionBehavior().setColor(m_coloringAttributes);
    getCanvas().getSelectionBehavior().postId(SelectionBehavior.SELECTION_POSTID);
  }
 
  public void removeSelectedState(){
    m_isSelected = false;
    m_planeShape.setAppearance(m_appearance);
  }
}

