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

package net.mumie.mathletfactory.mmobject.geom.affine;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.mumie.mathletfactory.math.algebra.GroupElementIF;
import net.mumie.mathletfactory.math.algebra.geomgroup.AffineGroupElement;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.geom.GeometryIF;
import net.mumie.mathletfactory.math.geom.affine.Affine3DPoint;
import net.mumie.mathletfactory.math.geom.affine.AffineGeometryIF;
import net.mumie.mathletfactory.mmobject.MMDefaultCanvasObject;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 * This class represents a 3D (closed or open) polygon determined by a number (>=1) of points.
 *
 * @author Paehler
 * @mm.docstatus finished
 */
public class MMAffine3DPolygon extends MMDefaultCanvasObject implements AffineGeometryIF {


  private static Logger logger = Logger.getLogger(MMAffine3DPolygon.class.getName());

  /**
   * Affine3DPolygon is internally represented by an arbitray number of
   * {@link net.mumie.mathletfactory.math.geom.affine.Affine3DPoint}.
   */
  private final List m_vertices;

  /**
   * This field determines whether the first and the last vertex of the polygon
   * (see {@link MMAffine3DPolygon#m_vertices}) shall be connected or not.
   */
  private boolean m_isClosed = true;


  /**
   * Constructs an <code>Affine3DPolygon</code> using the
   * {@link net.mumie.mathletfactory.math.geom.affine.Affine3DPoint}s in
   * <code>vertices</code> as its vertices. The constructor has no side effects,
   * i.e. a deep copy of the traversed <code>Affine3DPoint</code>s will be used
   * for the polygon's vertices.
   */
  public MMAffine3DPolygon(Affine3DPoint[] vertices) {
    //TODO: check the entryTypes of the vertices
    m_vertices = new ArrayList(vertices.length);
    for(int i=0; i<vertices.length; i++)
      m_vertices.add(i,(Affine3DPoint)vertices[i].deepCopy());
  }

  /** Operates on each of the points of the polygon. */
  public AffineGeometryIF groupAction(AffineGroupElement actingGroupElement) {
    logger.warning("::groupAction currently works without checking the GroupElementIF");
    //System.out.println("before: "+toString());
    for (int i=0; i< getVerticesCount(); i++)
      getVertexRef(i).groupAction(actingGroupElement);
    //System.out.println("after: "+toString());
    return this;
  }

  /** Operates on each of the points of the polygon. */
 	public GeometryIF groupAction(GroupElementIF actingGroupElement){
 		//TODO: check for group consistency
 		return groupAction((AffineGroupElement)actingGroupElement);
 	}

  /**
   * Returns the number of vertices of this {@link MMAffine3DPolygon}.
   */
  public int getVerticesCount() {
    return m_vertices == null ? 0 : m_vertices.size();// first case should never happen!
  }


  /**
   * Returns the vertex at index <code>index</code>. This method returns the
   * reference to the {@link Affine3DPoint}
   */
  public Affine3DPoint getVertexRef(int index) {
    return (Affine3DPoint)m_vertices.get(index);
  }

  /** Sets the given point by reference as the n-th vertex. */
  public MMAffine3DPolygon setVertexRef(int n, Affine3DPoint newVertex) {
    //TODO: check entry type
    m_vertices.set(n,newVertex);
    return this;
  }

  /**  Returns the n-th vertex.  */
  public Affine3DPoint getVertex(int n) {
    return (Affine3DPoint)((Affine3DPoint)m_vertices.get(n)).deepCopy();
  }

  /**  Sets the given point as the n-th vertex.  */
  public MMAffine3DPolygon setVertex(int n, Affine3DPoint newVertex) {
    m_vertices.set(n,newVertex.deepCopy());
    return this;
  }

  /**  Adds the given point as the last vertex of this polygon.  */
  public MMAffine3DPolygon addVertex(Affine3DPoint point){
    m_vertices.add(point);
    return this;
  }

  /** Removes the last vertex of this polygon. */
  public MMAffine3DPolygon removeLastVertex(){
    m_vertices.remove(m_vertices.size()-1);
    return this;
  }

  /** Extends the number of vertices in this polygon by the given number, adding vertices with coordinates (0,0,0).  */
  public MMAffine3DPolygon addVertices(int number){
    for(int i=0;i<number;i++)
      m_vertices.add(new Affine3DPoint(getNumberClass()));
    return this;
  }

  /** Removes the last n vertex of this polygon. */
  public MMAffine3DPolygon removeVertices(int n){
    for(int i=0;i<n;i++)
      removeLastVertex();
    return this;
  }


  public int getGeomType() {
    return GeometryIF.AFFINE3D_GEOMETRY;
  }

  public Class getNumberClass() {
    return ((Affine3DPoint)m_vertices.get(0)).getNumberClass();//all points have equal type,
                                         // m_vertices should never be empty!
  }

  /** Returns true if the polygon has only vertices with the same coordinates. */
  public boolean isDegenerated() {
    Affine3DPoint first = getVertexRef(0);
    for (int i=1;i<m_vertices.size();i++) {
      if (!m_vertices.get(i).equals(first))
        return false;
    }
    return true;
  }

  /**
   * Returns true if this polygon is drawn as closed (Observe that this is simply for displaying
   * purposes, it does not necessarily mean initial point == end point).
   */
  public boolean isClosed() {
    return m_isClosed;
  }

  /** Sets this polygon to be drawn as closed. */
  public void setClosed(boolean value) {
    m_isClosed = value;
  }

  public NumberTuple[] getCoords(){
    NumberTuple[] retVal = new NumberTuple[getVerticesCount()];
    for(int i=0;i<getVerticesCount();i++){
      retVal[i] = new NumberTuple(getVertexRef(i).getAffineCoordinatesOfPoint());
    }
    return retVal;
  }

  public int getDefaultTransformType() {
    return GeneralTransformer.AFFINE_DEFAULT_TRANSFORM;
  }

  public int getDefaultTransformTypeInCanvas() {
    return GeneralTransformer.AFFINE_DEFAULT_TRANSFORM;
  }

  public int hashCode(){
    int retVal = 0;
    for(int i=0;i<getVerticesCount();i++)
      retVal += getVertexRef(i).hashCode() + getVertexRef(i).hashCode() >>> i;
    return retVal;
  }

 public String toString(){
   String retVal = "Poly(";
   for(int i=0;i<getVerticesCount();i++){
     retVal += getVertexRef(i);
     if(i < getVerticesCount()-1)
       retVal += ",";
     else
       retVal += ")";
   }
   return retVal;
 }

}

