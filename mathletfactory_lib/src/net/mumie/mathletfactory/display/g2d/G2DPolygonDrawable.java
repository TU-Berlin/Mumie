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

package net.mumie.mathletfactory.display.g2d;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.PolygonDisplayProperties;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.geom.affine.Affine2DPoint;

/**
 * Contains methods to draw a polygon, (e.g. given by an 
 * {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPolygon}) in a
 * {@link net.mumie.mathletfactory.display.g2d.MMG2DCanvas}.
 * 
 * @author Amsel
 * @mm.docstatus finished
 */
public class G2DPolygonDrawable extends G2DDrawable {
  private static final int X = 0;
  private static final int Y = 1;
  private float[][] m_vertices;
  /**
   * describes whether the i-th segment will be drawn or not. The i-th segment
   * joins the vertices i-1 and i.
   */
  private boolean[] m_validSegment;
  private GeneralPath m_shape;
  private boolean m_closed = true;

  public G2DPolygonDrawable() {
    this(0);
  }

  public G2DPolygonDrawable(int verticesCount) {
    initLength(verticesCount);
  }

  public void createShape() {
    m_shape = new GeneralPath(GeneralPath.WIND_NON_ZERO, m_vertices[X].length);
    int tmpLength = m_vertices[X].length;

    for (int i = 0; i < tmpLength; i++) {
//      for (int coord = 0; coord < 2; coord++) {
//        if (Float.isNaN(m_vertices[coord][i]) || Float.isInfinite(m_vertices[coord][i])){
//          m_vertices[coord][i] = 0;
//          m_validSegment[i] = false;
//          //System.out.println("after conversion: "+m_vertices[coord][i]);
//        }
//        if (Float.isInfinite(m_vertices[coord][i])){        
//          m_vertices[coord][i] = (m_vertices[coord][i] >= 0.0f ? 1.0f : -0.9f) * Float.MAX_VALUE;
//          //System.out.println("after conversion: "+m_vertices[coord][i]);
//        }
//      }
      if (m_validSegment[i] && i > 0) {
        m_shape.lineTo(m_vertices[X][i], m_vertices[Y][i]);
      } else {
        m_shape.moveTo(m_vertices[X][i], m_vertices[Y][i]);
      }
    }
    //System.out.println("path is "+m_shape.getBounds2D());
    if (m_closed && m_validSegment[0])
      m_shape.closePath();
  }
  /**
   * @see net.mumie.mathletfactory.display.G2DDrawable#getShape()
   */
  protected Shape getShape() {
    if(m_shape == null)
      createShape();
    return m_shape;
  }

  protected boolean isFilled(DisplayProperties properties) {
    boolean result = properties.isFilled() && m_closed;
    return result;
  }

  /**
   * Creates the shape to be drawn in {@link #draw}.
   * @see net.mumie.mathletfactory.display.CanvasDrawable#render()
   */
  public void render(DisplayProperties properties) {
    createShape();
  }

  protected void prepareCanvas(Graphics2D gr, DisplayProperties properties) {
    if (properties instanceof PolygonDisplayProperties) {
      PolygonDisplayProperties polygonProperties = (PolygonDisplayProperties) properties;
      Stroke polygonStroke = new BasicStroke((float) polygonProperties.getLineWidth());
      gr.setStroke(polygonStroke);
    }
  }

  /**
   * @see net.mumie.mathletfactory.display.CanvasDrawable#isAtScreenLocation(int, int)
   */
  public boolean isAtScreenLocation(int xOnScreen, int yOnScreen) {
    /* If the polygon is a closed line we may take the intersects method
     * from GeneralPath. Otherwise we have to walk along the _flattend_ path
     * of the GeneralPath and may check every line of that path against the
     * given screen point.
     *
     * This is necessary because GeneralPath always asumes a _closed_ polygon
     * and hits the interior of a path also on open pathes (e.g. function
     * graphs).
     *
     * The path must be flattend to handle rounded elements as well as straight
     * lines.
     */

    if (m_closed) {
      return m_shape.intersects(
        xOnScreen - PICKING_TOLERANCE,
        yOnScreen - PICKING_TOLERANCE,
        PICKING_TOLERANCE,
        PICKING_TOLERANCE);
    }

    PathIterator it = m_shape.getPathIterator(null, 1);
    float[] current = new float[6];
    Point2D start = null, stop = null;
    while (!it.isDone()) {
      int type = it.currentSegment(current);
      if (type == 0) {
        start = new Point2D.Float(current[0], current[1]);
      } else {
        stop = new Point2D.Float(current[0], current[1]);
        Line2D line = new Line2D.Float(start, stop);
        if (line
          .intersects(
            xOnScreen - PICKING_TOLERANCE,
            yOnScreen - PICKING_TOLERANCE,
            PICKING_TOLERANCE,
            PICKING_TOLERANCE)) {
          return true;
        }
        start = stop;
      }
      it.next();
    }
    return false;
  }

  /**
   * @see net.mumie.mathletfactory.display.CanvasDrawable#getBoundingBox
   */
  public Rectangle getBoundingBox() {
    return m_shape.getBounds();
  }

  public void setPoint(int vertexNum, Affine2DPoint vertex) {
    NumberTuple coordinates = vertex.getProjectiveCoordinatesOfPoint();
    m_vertices[X][vertexNum] = (float) coordinates.getEntryRef(1).getDouble();
    m_vertices[Y][vertexNum] = (float) coordinates.getEntryRef(2).getDouble();
  }

  public void setPoint(int vertexNum, double x, double y) {
    m_vertices[X][vertexNum] = (float) x;
    m_vertices[Y][vertexNum] = (float) y;
  }

  public void setPoint(int vertexNum, float x, float y) {
    m_vertices[X][vertexNum] = x;
    m_vertices[Y][vertexNum] = y;
  }

  public void initLength(int numOfVertices) {
    m_vertices = new float[2][numOfVertices];
    m_validSegment = new boolean[numOfVertices];
    for (int i = 0; i < numOfVertices; i++)
      m_validSegment[i] = true;
  }

  /**
   * Determines if the <code>i</code>th line segement of this
   * <code>G2DPolygonDrawable</code> is visible or not. Observe, that the
   * <code>i</code>th linesegment is defined as the segment joining the vertices
   * <code>i-1</code> and <code>i</code>.
   *
   * @param index
   * @param aValue
   */
  public void setValid(int index, boolean aValue) {
    m_validSegment[index] = aValue;
  }

  public boolean isValid(int index) {
    return m_validSegment[index];
  }

  public void setClosed() {
    m_closed = true;
  }

  public void setOpened() {
    m_closed = false;
  }

  public float[] getXValsRef() {
    return m_vertices[X];
  }

  public float[] getYValsRef() {
    return m_vertices[Y];
  }
}
