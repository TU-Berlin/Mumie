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
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.ListIterator;

import java.awt.geom.Rectangle2D;

import net.mumie.mathletfactory.display.PolygonDisplayProperties;
import net.mumie.mathletfactory.math.algebra.GroupElementIF;
import net.mumie.mathletfactory.math.algebra.geomgroup.AffineGroupElement;
import net.mumie.mathletfactory.math.geom.GeometryIF;
import net.mumie.mathletfactory.math.geom.affine.Affine2DPoint;
import net.mumie.mathletfactory.math.geom.affine.AffineGeometryIF;
import net.mumie.mathletfactory.mmobject.MMDefaultCanvasObject;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 * This class represents a 3D (closed or open) polygon determined by a number (>= 1) of points.
 *
 * @author amsel, Paehler
 * @mm.docstatus finished
 */
public class MMAffine2DPolygon extends MMDefaultCanvasObject implements AffineGeometryIF {


  private static Logger logger = Logger.getLogger(MMAffine2DPolygon.class.getName());

  /**
   * Affine2DPolygon is internally represented by an arbitray number of
   * {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPoint}.
   */
  private final List m_vertices;

  /**
   * This field determines whether the first and the last vertex of the polygon
   * (see {@link #m_vertices}) shall be connected or not.
   */
  private boolean m_isClosed = true;


  /**
   * Constructs an <code>Affine2DPolygon</code> using the
   * {@link net.mumie.mathletfactory.math.geom.affine.Affine2DPoint}s in
   * <code>vertices</code> as its vertices. The constructor has no side effects,
   * i.e. a deep copy of the traversed <code>Affine2DPoint</code>s will be used
   * for the polygon's vertices.
   */
  public MMAffine2DPolygon(Affine2DPoint[] vertices) {
    //TODO: check the entryTypes of the vertices
    m_vertices = new ArrayList(vertices.length);
    for(int i=0; i<vertices.length; i++)
      m_vertices.add(i,(Affine2DPoint)vertices[i].deepCopy());
    setDisplayProperties(new PolygonDisplayProperties());
  }

  /** Operates on each of the points of the polygon. */
  public AffineGeometryIF groupAction(AffineGroupElement actingGroupElement) {
    logger.warning("::groupAction currently works without checking the GroupElementIF");
    for (VertexIterator iter = getVertexIterator(); iter.hasNext();)
      iter.nextPoint().groupAction(actingGroupElement);
    return this;
  }

  /** Operates on each of the points of the polygon. */
 	public GeometryIF groupAction(GroupElementIF actingGroupElement){
 		//TODO: check for group consistency
 		return groupAction((AffineGroupElement)actingGroupElement);
 	}

  /**
   * Returns the number of vertices of this {@link MMAffine2DPolygon}.
   */
  public int getVerticesCount() {
    return m_vertices == null ? 0 : m_vertices.size();// first case should never happen!
  }


  /**
   * Returns the vertex at index <code>index</code>. This method returns the
   * reference to the {@link Affine2DPoint}
   */
  public Affine2DPoint getVertexRef(int index) {
    return (Affine2DPoint)m_vertices.get(index);
  }

  /** Sets the given point by reference as the n-th vertex. */
  public MMAffine2DPolygon setVertexRef(int n, Affine2DPoint newVertex) {
    //TODO: check entry type
    m_vertices.set(n,newVertex);
    return this;
  }

  /**  Returns the n-th vertex.  */
  public Affine2DPoint getVertex(int n) {
    return (Affine2DPoint)((Affine2DPoint)m_vertices.get(n)).deepCopy();
  }

  /**  Sets the given point as the n-th vertex.  */
  public MMAffine2DPolygon setVertex(int n, Affine2DPoint newVertex) {
    m_vertices.set(n,newVertex.deepCopy());
    return this;
  }

  /**  Adds the given point as the last vertex of this polygon.  */
  public MMAffine2DPolygon addVertex(Affine2DPoint point){
    m_vertices.add(point);
    return this;
  }
  
  /** Removes the first vertex of this polygon. */
  public MMAffine2DPolygon removeFirstVertex(){
    m_vertices.remove(0);
    return this;
  }
  /** Removes the first n vertex of this polygon. */
  public MMAffine2DPolygon removeFirstVertices(int n){
	  for(int i=0;i<n;i++) removeFirstVertex();
	  return this;
  }
  /** Removes the last vertex of this polygon. */
  public MMAffine2DPolygon removeLastVertex(){
    m_vertices.remove(m_vertices.size()-1);
    return this;
  }

  /** Extends the number of vertices in this polygon by the given number, adding vertices with coordinates (0,0,0).  */
  public MMAffine2DPolygon addVertices(int number){
    for(int i=0;i<number;i++)
      m_vertices.add(new Affine2DPoint(getNumberClass()));
    return this;
  }

  /** Removes the last n vertex of this polygon. */
  public MMAffine2DPolygon removeVertices(int number){
    for(int i=0;i<number;i++)
      removeLastVertex();
    return this;
  }

  /** Returns an iterator for the vertices. */
  public VertexIterator getVertexIterator() {
    return new VertexIterator((Affine2DPoint[])m_vertices.toArray(new Affine2DPoint[m_vertices.size()]),0);
  }


  public int getGeomType() {
    return GeometryIF.AFFINE2D_GEOMETRY;
  }

  public Class getNumberClass() {
    return ((Affine2DPoint)m_vertices.get(0)).getNumberClass();//all points have equal type,
                                         // m_vertices should never be empty!
  }

  /** Returns true if the polygon has only vertices with the same coordinates. */
  public boolean isDegenerated() {
    Affine2DPoint first = getVertexRef(0);
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


  /**
   * An iterator for the vertices of the polygon.
   *
   * @author amsel, Paehler
   * @mm.docstatus finished
   */
  public final class VertexIterator implements Iterator {
    private Affine2DPoint[] m_vertices;
    private int index;

    /** Makes the iterator iterate over the given vertices starting with the given index. */
    public VertexIterator(Affine2DPoint[] vertices, int startIndex) {
      m_vertices = vertices;
      index = startIndex - 1;
    }

    /** Makes the iterator iterate over the given vertices. */
    public VertexIterator(Affine2DPoint[] vertices) {
      this(vertices, 0);
    }

    public boolean hasNext() {
      return index + 1 < m_vertices.length;
    }

    public Object next() {
      return nextPoint();
    }

    /** Returns the next point. */
    public Affine2DPoint nextPoint() {
      return m_vertices[++index];
    }

    public void remove() {
      throw new UnsupportedOperationException(
        "VertexIterator doesn't support remove");
    }
  }


  public int getDefaultTransformType() {
    return GeneralTransformer.AFFINE_DEFAULT_TRANSFORM;
  }

  public int getDefaultTransformTypeInCanvas() {
    return GeneralTransformer.AFFINE_DEFAULT_TRANSFORM;
  }

    public Rectangle2D getWorldBoundingBox() {

	double minX = Double.MAX_VALUE;
	double minY = Double.MAX_VALUE;
	double maxX = -Double.MAX_VALUE;
	double maxY = -Double.MAX_VALUE;
	ListIterator li = m_vertices.listIterator();
	while(li.hasNext()) {
	    Affine2DPoint a2p = (Affine2DPoint)li.next();
	    minX = Math.min(minX, a2p.getAffineCoordinatesOfPoint().getEntry(1).getDouble());
	    minY = Math.min(minY, a2p.getAffineCoordinatesOfPoint().getEntry(2).getDouble());
	    maxX = Math.max(maxX, a2p.getAffineCoordinatesOfPoint().getEntry(1).getDouble());
	    maxY = Math.max(maxY, a2p.getAffineCoordinatesOfPoint().getEntry(2).getDouble());
	}

	return new Rectangle2D.Double(minX,minY,maxX-minX,maxY-minY);

    }

}

