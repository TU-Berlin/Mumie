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

import javax.media.j3d.TransformGroup;
import javax.vecmath.Matrix4d;

import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.j3d.shape.CoordinateSystem;
import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.util.Affine3DDouble;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultRN;

/**
 *  This class draws a
 *  {@link net.mumie.mathletfactory.display.j3d.shape.CoordinateSystem 3D Coordinate System}
 *  with arbitrary axis length and unit length. If the distance of the viewer from the
 *  origin of the coordinate system changes, the axis length is extended or reduced, whereas
 *  for orientation purposes the unit remains the same.
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class J3DCoordinateSystemDrawable extends J3DRemovableDrawable {
  
 
  private float m_axisLength = 1.0f;
  
//  /** The transform group for the scaling of axes (parent for each axis-shape). */
//  private TransformGroup m_axisTransformGroup = new TransformGroup();
//  
  /** for measuring a change of distance from viewer to coordinate system origin. */
  private double m_distance;
  
  /** the length of a "tick" in the coordinate system. */
  private double m_unitLength = 0.2;
  
  /**
   * Sets the transform group for this drawable.
   * @see net.mumie.mathletfactory.display.j3d.J3DDrawable#setTransformGroup(TransformGroup)
   */
  protected  void setTransformGroup(TransformGroup tg){
    m_transformGroup = tg;
    m_distance = Affine3DDouble.standardNorm(getCanvas().getViewerPosition());
  }
  
  /**
   * Creates the {@link net.mumie.mathletfactory.display.j3d.shape.CoordinateSystem}
   * with the given display properties.
   * @see net.mumie.mathletfactory.display.j3d.J3DRemovableDrawable#createShape(DisplayProperties)
   */
  protected void createShape(DisplayProperties properties){
    
    if(properties instanceof MMDefaultRN.RNDisplayProperties)
      m_unitLength = ((MMDefaultRN.RNDisplayProperties)properties).getUnit();
    //System.out.println("unit length is: "+m_unitLength);
    m_transformGroup.addChild(new CoordinateSystem(m_axisLength, (float)m_unitLength));
    m_shape.addChild(m_transformGroup);
  }
  
  /**
   *  Must be called by
   *  {@link net.mumie.mathletfactory.transformer.j3d.Canvas3DObjectTransformer}, when
   *  an update cycle has been finished.
   */
  public void updateFinished(){
    double distance = Affine3DDouble.standardNorm(getCanvas().getViewerPosition());
    // redraw coord system if new distance differs about more than 10% from old
    if(Math.abs(distance - m_distance)/m_distance > 0.1){
      m_distance = distance;
      setAxisLength(m_distance * 0.4);
      //setUnitLength(m_distance / 10);
      resetShape();
      
    }
  }
  public void render(DisplayProperties props){
	  super.render(props);
	  double distance = Affine3DDouble.standardNorm(getCanvas().getViewerPosition());
	  if(distance != m_distance){
		  updateFinished();
	  }
  }
  
  /** Sets the (initial) length of the axis. If the viewer moves, this value changes. */
  public void setAxisLength(double axisLength){
    m_axisLength = (float) axisLength;
  }
  
  /** Returns the current axis length. */
  public double getAxisLength(){
    return (double) m_axisLength;
  }
  
  /** Sets the length of a "tick" in the coordinate system. */
  public void setUnitLength(double unitLength){
    m_unitLength = (float) unitLength;
  }
  
  /** Returns the length of a "tick" in the coordinate system. */
  public double getUnitLength(){
    return (double) m_unitLength;
  }
   
  /**
   *  This Drawable is not selectable, so this method always returns false.
   */
  public boolean isAtScreenLocation(int x, int y){
    return false;
  }
  
  /**
   *  Sets the transformation for the coordinate system from origin.
   * @param matrix a 4x4 matrix with homogeneous coordinates.
   */
  public void setTransformation(NumberMatrix matrix){
    Matrix4d trafoFromOrigin = getTrafoFromOrigin();
    for(int i=0; i<4; i++)
      for(int j=0; j<4; j++)
       trafoFromOrigin.setElement(i,j, ((MNumber)matrix.getEntryRef(i+1, j+1)).getDouble());
    setTrafoFromOrigin(trafoFromOrigin);
  }
  
  /**
   *  Not selectable. Empty method.
   */
  public void setSelectedState(){}
  
  /**
   *  Not selectable. Empty method.
   */
  public void removeSelectedState(){}
}


