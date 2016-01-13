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

package net.mumie.mathletfactory.display.j3d.shape;

import java.awt.Color;
import java.awt.Font;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Font3D;
import javax.media.j3d.Group;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.OrientedShape3D;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Text3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Sphere;

/**
 *  This class is the Java3D scenegraph part that is necessary for rendering a 3D coordinate
 *  system. It is fully operable after calling the constructor (i.e. it only needs to be
 *  inserted into the scenegraph).
 *  @see net.mumie.mathletfactory.display.j3d.J3DCoordinateSystemDrawable
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class CoordinateSystem extends Group {
  
  private Appearance greyAppearance = new Appearance();
  private Appearance blackAppearance = new Appearance();
  
  // geometric data
  private float m_arrowRadius;
  private float m_arrowLength;
  private float m_unit;
  private Material g_material, b_material;
 
  // for temporary use
  private Transform3D m_trans = new Transform3D();
  private Vector3f m_vector = new Vector3f();
 
  /** Constructs a coordinate system with axis length <code>length</code> 
   * and unit width <code>unit</code>.
   */
  public CoordinateSystem(float length, float unit){
    m_unit = unit;
    setPickable(false);
    Color3f grey = new Color3f(0.5f, 0.5f, 0.5f);
    ColoringAttributes atts = new ColoringAttributes(grey, ColoringAttributes.FASTEST);
    b_material = new Material();
    b_material.setDiffuseColor(new Color3f(Color.black));
    g_material = new Material();
    g_material.setDiffuseColor(grey);
    greyAppearance.setColoringAttributes(atts);
    greyAppearance.setMaterial(g_material);
    blackAppearance.setMaterial(b_material); 
    setGeometry(length);
  }
  
  /**
   *  Sets the geometric data: Constructs the axes and adds unit markers.
   */
  private void setGeometry(float length){
    m_arrowRadius = length/60;
    m_arrowLength = length/15;
    
    // create axes
    addChild(createAxis(length, m_unit, "y"));
    addChild(rotate(new AxisAngle4f(0, 0, 1.0f, -(float)Math.PI/2), createAxis(length, m_unit, "x")));
    addChild(rotate(new AxisAngle4f(1.0f, 0, 0, (float)Math.PI/2), createAxis(length, m_unit, "z")));
  }
 
  /** Creates an axis in y-direction which can then be rotated to point into an arbitrary direction. */
  private Group createAxis(float length, float unit, String label){
    //System.out.println("length is"+length);
    double numberOfTicks = length / unit;
    Group axis = new Group();
    
    // add axis shape
    LineArray line = new LineArray(2, LineArray.COORDINATES);
    line.setCoordinate(0, new double[]{0,-(length-m_arrowLength),0});
    line.setCoordinate(1, new double[]{0,length-m_arrowLength,0});
    LineAttributes m_la = new LineAttributes();
//    m_la.setLinePattern(LineAttributes.PATTERN_DASH);
    m_la.setLineAntialiasingEnable(true);
    greyAppearance.setLineAttributes(m_la);
    axis.addChild(new Shape3D(line, greyAppearance));
//    axis.addChild(new Cylinder(length / 200, 2 * (length-m_arrowLength), greyAppearance));
    
    // add ticks
    for(int i=1; i< numberOfTicks; i++){
      if(i * unit<=length - m_arrowLength){
        axis.addChild(translate(0, i * unit , 0, new Sphere(m_arrowRadius*0.5f, greyAppearance)));//0.75
        axis.addChild(translate(0, -i * unit , 0, new Sphere(m_arrowRadius*0.5f, greyAppearance)));
      }
    }
    
    // add arrow head
    axis.addChild(translate(0, length - m_arrowLength/2, 0,
                            new Cone(m_arrowRadius, m_arrowLength, greyAppearance)));
   
    Point3f labelPos = new Point3f(0, length, 0);
    // add label at arrow head
    axis.addChild(scale(0.07,
      new OrientedShape3D(new Text3D(new Font3D(new Font("Default", Font.PLAIN, (int)Math.max(2*length,1)),
                                                null),label, labelPos),
                          blackAppearance,
                          OrientedShape3D.ROTATE_ABOUT_POINT,labelPos)));
   
    return axis;
  }
  
  /** utility method: translates a scenegraph node by the specified values. */
  private TransformGroup translate(float x, float y, float z, Node node){
    TransformGroup TG = new TransformGroup();
    m_vector.set( x, y, z);
    m_trans.set(m_vector);
    
    TG.setTransform(m_trans);
    TG.addChild(node);
    return TG;
  }
  
  /** utility method: rotates a scenegraph node by the specified values. */
  private TransformGroup rotate(AxisAngle4f axisAngle, Node node){
    TransformGroup TG = new TransformGroup();
    m_trans.set(axisAngle);
    TG.setTransform(m_trans);
    TG.addChild(node);
    return TG;
  }
  
  /** utility method: scales a scenegraph node by the specified values. */
  private TransformGroup scale(double scale, Node node){
    TransformGroup TG = new TransformGroup();
    m_trans.setScale(scale);
    TG.setTransform(m_trans);
    TG.addChild(node);
    return TG;
  }
}

