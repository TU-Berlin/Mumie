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
import java.awt.geom.Rectangle2D;
import java.math.BigInteger;
import java.util.logging.Logger;

import net.mumie.mathletfactory.math.algebra.GroupElementIF;
import net.mumie.mathletfactory.math.algebra.geomgroup.AffineGroupElement;
import net.mumie.mathletfactory.math.geom.GeometryIF;
import net.mumie.mathletfactory.math.geom.affine.Affine2DPoint;
import net.mumie.mathletfactory.math.geom.affine.AffineGeometryIF;
import net.mumie.mathletfactory.mmobject.MMDefaultCanvasObject;
import net.mumie.mathletfactory.transformer.GeneralTransformer;
/**
 * This class represents an angle in the 2-dim space. An angle is internally
 * represented by the three
 * {@link Affine2DPoint}s
 * {@link #m_vertex}, {@link #m_firstPoint}, and {@link #m_secondPoint}, by the
 * closure type {@link #m_closure}, and by {@link #m_factor}, which determines
 * the radius of the angle, i.e., the distance between the vertex and the
 * circular arc which marks the angle.
 *
 * @author Mrose
 * @mm.docstatus finished
 */
public class MMAffine2DAngle extends MMDefaultCanvasObject implements AffineGeometryIF {
  /**
   * Represents the vertex of the angle.
   */
  protected Affine2DPoint m_vertex;
  /**
   * Represents an arbitrary point on the first arm of the angle.
   */
  protected Affine2DPoint m_firstPoint;
  /**
   * Represents an arbitrary point on the second arm of the angle.
   */
  protected Affine2DPoint m_secondPoint;
  /**
   * Determines the closure type of the angle. Feasible values are 0 (OPEN),
   * 1 (CHORD), or 2 (PIE).
   *
   * @see java.awt.geom.Arc2D
   */
  protected int m_closure;
  /**
   * Determines the radius af the angle, i.e., the distance between the vertex
   * and the circular arc which marks the angle. <code>m_factor</code> has to
   * be positive or zero and determines the radius in the following way:
   * radius = <code>m_factor</code> * min(d1, d2), where d1 and d2 are the
   * distances between
   * <code>m_vertex</code> and <code>m_firstPoint</code> or
   * <code>m_vertex</code> and <code>m_secondPoint</code>, resp.
   */
  protected double m_factor = 0.5;
  private static Logger logger =
    Logger.getLogger(MMAffine2DAngle.class.getName());

  /**
   * Defines a zero angle where vertex, first point and second point are
   * {@link Affine2DPoint}s of entry class
   * <code>entryClass</code> lying on the origin. The closure type is 2 (PIE),
   * the factor is 0.5.
   *
   * @see java.awt.geom.Arc2D
   */
  public MMAffine2DAngle(Class entryClass) {
    this(new Affine2DPoint(entryClass, 0.0, 0.0),
    new Affine2DPoint(entryClass, 0.0, 0.0),
    new Affine2DPoint(entryClass, 0.0, 0.0), 2, 0.5);
  }

  /**
   * Defines an angle where the vertex is the origin, the first arm of the
   * angle is the positive x-axis and the second arm of the angle is determined
   * by <code>secondPoint</code>. The <code>closure</code> type can be
   * 0 (OPEN), 1 (CHORD), or 2 (PIE). The radius, i.e., the distance between
   * the origin and the circular arc which marks the angle, is given by:
   * radius = 0.5 * d1, where d1 is the distance between the origin and the
   * <code>secondPoint</code>.
   *
   * @see java.awt.geom.Arc2D
   */
  public MMAffine2DAngle(Affine2DPoint secondPoint, int closure) {
    this(
    new Affine2DPoint(
      secondPoint.getNumberClass(),
      0.0,
      0.0),
      new Affine2DPoint(secondPoint.getNumberClass(),1.0, 0.0),
      new Affine2DPoint(secondPoint.getNumberClass(), secondPoint.getXAsDouble(),
      secondPoint.getYAsDouble()),
      closure);
  }

  /**
   * Defines an angle by three
   * {@link Affine2DPoint}s, the
   * <code>closure</code> type, and a <code>factor</code>, which determines the
   * radius, i.e., the distance between the <code>vertex</code>
   * and the circular arc which marks the angle. The three points are the
   * <code>vertex</code> and the two points <code>firstPoint</code> and
   * <code>secondPoint</code> lying on the arms of the angle. The
   * <code>closure</code> type can be 0 (OPEN), 1 (CHORD), or 2 (PIE). The
   * <code>factor</code> has to be positive or zero and determines the radius
   * in the following way: radius = <code>factor</code> * min(d1, d2), where
   * d1 and d2 are the distances between  the <code>vertex</code> and the
   * <code>firstPoint</code> or the <code>vertex</code> and the
   * <code>secondPoint</code>, resp.
   *
   * @see java.awt.geom.Arc2D
   */
  private MMAffine2DAngle(
    Affine2DPoint vertex,
    Affine2DPoint firstPoint,
    Affine2DPoint secondPoint,
    int closure,
    double factor) {
    if (!firstPoint.getNumberClass().equals(secondPoint.getNumberClass())
      || !firstPoint.getNumberClass().equals(vertex.getNumberClass())) {
      throw new IllegalArgumentException("initial, end point and vertex must have same entry class");
    }
    m_vertex = (Affine2DPoint) vertex.deepCopy();
    m_firstPoint = (Affine2DPoint) firstPoint.deepCopy();
    m_secondPoint = (Affine2DPoint) secondPoint.deepCopy();
    if (m_closure == 0 || m_closure == 1 || m_closure == 2) {
      m_closure = closure;
    }
    else
      throw new IllegalArgumentException("closure type must be 0 = OPEN, 1 = CHORD, or 2 = PIE.");
    if (factor >= 0.0) {
      m_factor = factor;
    }
    else
      throw new IllegalArgumentException("factor or radius must be positive or zero.");
  }

  /**
   * Defines an angle by three
   * {@link Affine2DPoint}s, and the
   * <code>closure</code> type. The three points are the <code>vertex</code>
   * and the two points
   * <code>firstPoint</code> and <code>secondPoint</code> lying on the arms of
   * the angle. The <code>closure</code> type can be 0 (OPEN), 1 (CHORD), or
   * 2 (PIE). The radius, i.e., the distance between the <code>vertex</code>
   * and the circular arc which marks the angle, is given by:
   * radius = 0.5 * min(d1, d2), where d1 and d2 are the distances between the
   * <code>vertex</code> and the <code>firstPoint</code> or the
   * <code>vertex</code> and the <code>secondPoint</code>, resp.
   *
   * @see java.awt.geom.Arc2D
   */
  public MMAffine2DAngle(
    Affine2DPoint vertex,
    Affine2DPoint firstPoint,
    Affine2DPoint secondPoint,
    int closure) {
    this(vertex, firstPoint, secondPoint, closure, 0.5);
  }

  /**
   * Constructs a new angle, initialized to the specified <code>vertex</code>,
   * the <code>startingAngle</code> and the <code>angularExtent</code> of the
   * angle in radians, the <code>radius</code>, and the <code>closure</code>
   * type. The <code>closure</code> type can be  0 (OPEN), 1 (CHORD), or
   * 2 (PIE). The <code>radius</code> has to be positive or zero.
   *
   * @see java.awt.geom.Arc2D
   */
  public MMAffine2DAngle(
    Affine2DPoint vertex,
    double radius,
    double startingAngle,
    double angularExtent,
    int closure) {
    this(
      vertex,
      new Affine2DPoint(
        vertex.getNumberClass(),
        Math.cos(startingAngle),
        Math.sin(startingAngle)),
      new Affine2DPoint(
        vertex.getNumberClass(),
        Math.cos(startingAngle + angularExtent),
        Math.sin(startingAngle + angularExtent)),
      closure,
      radius);
  }

  /**
   * Constructs a new angle starting parallel to the x-axis, initialized to the
   * specified <code>vertex</code>, the <code>angularExtent</code> of the
   * angle in radians, the <code>radius</code>, and the <code>closure</code>
   * type. The <code>closure</code> type can be 0 (OPEN), 1 (CHORD), or
   * 2 (PIE). The <code>radius</code> has to be positive or zero.
   *
   * @see java.awt.geom.Arc2D
   */
  public MMAffine2DAngle(
    Affine2DPoint vertex,
    double radius,
    double angularExtent,
    int closure) {
    this(vertex, radius, 0.0, angularExtent, closure);
  }

  /**
   * Constructs a new angle, initialized to the specified <code>vertex</code>,
   * the <code>startingAngle</code> and the <code>angularExtent</code> of the
   * angle in degrees, the <code>radius</code>, and the <code>closure</code>
   * type. The <code>closure</code> type can be 0 (OPEN), 1 (CHORD), or
   * 2 (PIE). The <code>radius</code> has to be positive or zero.
   *
   * @see java.awt.geom.Arc2D
   */
  public MMAffine2DAngle(
    Affine2DPoint vertex,
    double radius,
    int startingAngle,
    int angularExtent,
    int closure) {
    this(
      vertex,
      new Affine2DPoint(
        vertex.getNumberClass(),
        Math.cos(Math.toRadians(startingAngle)),
        Math.sin(Math.toRadians(startingAngle))),
      new Affine2DPoint(
        vertex.getNumberClass(),
        Math.cos(
          Math.toRadians(
            new BigInteger(
              new Integer(startingAngle + angularExtent).toString())
              .mod(new BigInteger("360"))
              .doubleValue())),
        Math.sin(
          Math.toRadians(
            new BigInteger(
              new Integer(startingAngle + angularExtent).toString())
              .mod(new BigInteger("360"))
              .doubleValue()))),
      closure,
      radius);
  }

  /**
   * Constructs a new angle starting parallel to the x-axis, initialized to the
   * specified <code>vertex</code>, the <code>angularExtent</code> of the
   * angle in degrees, the <code>radius</code>, and the <code>closure</code>
   * type. The <code>closure</code> type can be 0 (OPEN), 1 (CHORD), or
   * 2 (PIE). The <code>radius</code> has to be positive or zero.
   *
   * @see java.awt.geom.Arc2D
   */
  public MMAffine2DAngle(
    Affine2DPoint vertex,
    double radius,
    int angularExtent,
    int closure) {
    this(vertex, radius, 0, angularExtent, closure);
  }

  /**
   * Constructs a new angle at the origin of the given <code>entryClass</code>
   * starting parallel to the x-axis and initialized to the
   * specified <code>angularExtent</code> of the angle in degrees, the
   * <code>radius</code>, and the <code>closure</code> type. The
   * <code>closure</code> type can be 0 (OPEN), 1 (CHORD), or 2 (PIE). The
   * <code>radius</code> has to be positive or zero.
   *
   * @see java.awt.geom.Arc2D
   */
  public MMAffine2DAngle(
    Class entryClass,
    double radius,
    int angularExtent,
    int closure) {
    this(new Affine2DPoint(entryClass, 0.0, 0.0), radius, 0, angularExtent, closure);
  }

  /**
   * Constructs a new angle at the origin of the given <code>entryClass</code>
   * starting parallel to the x-axis and initialized to the
   * specified <code>angularExtent</code> of the angle in radians, the
   * <code>radius</code>, and the <code>closure</code> type. The
   * <code>closure</code> type can be 0 (OPEN), 1 (CHORD), or 2 (PIE). The
   * <code>radius</code> has to be positive or zero.
   *
   * @see java.awt.geom.Arc2D
   */
  public MMAffine2DAngle(
    Class entryClass,
    double radius,
    double angularExtent,
    int closure) {
    this(new Affine2DPoint(entryClass, 0.0, 0.0), radius, 0.0, angularExtent, closure);
  }

  public AffineGeometryIF groupAction(AffineGroupElement actingGroupElement) {
    logger.warning(
      "::groupAction currently works without checking the GroupElementIF");
    getVertex().groupAction(actingGroupElement);
    getFirstPoint().groupAction(actingGroupElement);
    getSecondPoint().groupAction(actingGroupElement);
    return this;
  }

  public int getGeomType() {
    return GeometryIF.AFFINE2D_GEOMETRY;
  }

  public Class getNumberClass() {
    return getFirstPoint().getNumberClass();
    // first point, second point and vertex always have
    // the same entry class.
  }

  /**
   * Returns {@link #m_firstPoint}, the first point of this angle.
   * @return an <code>Affine2DPoint</code> holding the first point of this angle.
   */
  public Affine2DPoint getFirstPoint() {
    return m_firstPoint;
  }

  /**
   * Sets the first point {@link #m_firstPoint} of this angle to
   * <code>firstPoint</code> and returns the changed angle.
   * @param firstPoint the new first point.
   * @return an <code>Affine2DAngle</code> holding the angle with the new first point.
   */
  public MMAffine2DAngle setFirstPoint(Affine2DPoint firstPoint) {
    if (firstPoint.getNumberClass().equals(getNumberClass())) {
      m_firstPoint.setFromPoint(firstPoint);
      return this;
    }
    else
      throw new IllegalArgumentException(
        "entry class of anAffine2DPoint and the "
          + "Affine2DAngle must coincide");
  }

  /**
   * Sets the coordinates of the first point {@link #m_firstPoint} of this
   * angle to the values given by <code>x</code> and <code>y</code> and returns
   * the changed angle.
   * @param x the x-coordinate of the new first point.
   * @param y the y-coordinate of the new first point.
   * @return an <code>Affine2DAngle</code> holding the angle with the new first point.
   */
  public MMAffine2DAngle setFirstPoint(double x, double y) {
    m_firstPoint.setXY(x, y);
    return this;
  }
  /**
   * Returns {@link #m_secondPoint}, the second point of this angle.
   * @return an <code>Affine2DPoint holding the second point of this angle.
   */
  public Affine2DPoint getSecondPoint() {
    return m_secondPoint;
  }

  /**
   * Sets the second point {@link #m_secondPoint} of this angle to
   * <code>secondPoint</code> and returns the changed angle.
   * @param secondPoint the new second point.
   * @return an <code>Affine2DAngle</code> holding the angle with the new second point.
   */
  public MMAffine2DAngle setSecondPoint(Affine2DPoint secondPoint) {
    if (secondPoint.getNumberClass().equals(getNumberClass())) {
      m_secondPoint.setFromPoint(secondPoint);
      return this;
    }
    else
      throw new IllegalArgumentException(
        "entry class of anAffine2DPoint and the "
          + "Affine2DAngle must coincide");
  }

  /**
   * Sets the coordinates of the second point {@link #m_secondPoint} of this
   * angle to the values given by <code>x</code> and <code>y</code> and returns
   * the changed angle.
   * @param x the x-coordinate of the new second point.
   * @param y the y-coordinate of the new second point.
   * @return an <code>Affine2DAngle</code> holding the angle with the new second point.
   */
  public MMAffine2DAngle setSecondPoint(double x, double y) {
    m_secondPoint.setXY(x, y);
    return this;
  }

  /**
   * Returns {@link #m_vertex}, the vertex of this angle.
   * @return an <code>Affine2DPoint</code> holding the vertex of this angle.
   */
  public Affine2DPoint getVertex() {
    return m_vertex;
  }

  /**
   * Returns {@link #m_closure}, the closure type of this angle.
   * @return the closure type of this angle. Possible values are 0 (OPEN),
   * 1 (CHORD), or 2 (PIE).
   */
  public int getClosure() {
    return m_closure;
  }

  /**
   * Sets the vertex {@link #m_vertex} of this angle to <code>vertex</code> and
   * returns the changed angle.
   * @param vertex the new vertex.
   * @return an <code>Affine2DAngle</code> holding the angle with the new vertex.
   */
  public MMAffine2DAngle setVertex(Affine2DPoint vertex) {
    if (vertex.getNumberClass().equals(getNumberClass())) {
      m_vertex.setFromPoint(vertex);
      return this;
    }
    else
      throw new IllegalArgumentException(
        "entry class of anAffine2DPoint and the "
          + "Affine2DAngle must coincide");
  }

  /**
   * Sets the coordinates of the vertex {@link #m_vertex} of this angle to the
   * values given by <code>x</code> and <code>y</code> and returns the changed
   * angle.
   * @param x the x-coordinate of the new vertex.
   * @param y the y-coordinate of the new vertex.
   * @return an <code>Affine2DAngle</code> holding the angle with the new vertex.
   */
  public MMAffine2DAngle setVertex(double x, double y) {
    m_vertex.setXY(x, y);
    return this;
  }

  /**
   * Sets this angle to the angle given by the parameters <code>vertex</code>,
   * <code>firstPoint</code>, <code>secondPoint</code>, and
   * <code>closure</code>, and returns the changed angle.
   * @param vertex the vertex of the angle.
   * @param firstPoint an arbitrary point on the first arm of the angle.
   * @param secondPoint an arbitrary point on the second arm of the angle.
   * @param closure the closure type of the angle. Feasible values are
   * 0 (OPEN), 1 (CHORD), or 2 (PIE).
   * @return an <code>Affine2DAngle</code> holding the angle with the new values.
   */
  public MMAffine2DAngle setAngle(
    Affine2DPoint vertex,
    Affine2DPoint firstPoint,
    Affine2DPoint secondPoint,
    int closure) {
    this.setVertex(vertex);
    this.setFirstPoint(firstPoint);
    this.setSecondPoint(secondPoint);
    this.setClosure(closure);
    return this;
  }

  /**
   * Sets this angle to the angle given by the parameters <code>vertex</code>,
   * <code>firstPoint</code>, <code>secondPoint</code>, <code>closure</code>,
   * and <code>factor</code> and returns the changed angle.
   * @param vertex the vertex of the angle.
   * @param firstPoint an arbitrary point on the first arm of the angle.
   * @param secondPoint an arbitrary point on the second arm of the angle.
   * @param closure the closure type of the angle. Feasible values are
   * 0 (OPEN), 1 (CHORD), or 2 (PIE).
   * @param factor determines the radius af the angle, i.e., the distance
   * between the vertex and the circular arc which marks the angle.
   * <code>factor</code> has to be positive or zero and determines the radius
   * in the following way:
   * radius = <code>factor</code> * min(d1, d2), where d1 and d2 are the
   * distances between <code>vertex</code> and <code>firstPoint</code> or
   * <code>vertex</code> and <code>secondPoint</code>, resp.
   * @return an <code>Affine2DAngle</code> holding the angle with the new values.
   */
  public MMAffine2DAngle setAngle(
    Affine2DPoint vertex,
    Affine2DPoint firstPoint,
    Affine2DPoint secondPoint,
    int closure,
    double factor) {
    this.setVertex(vertex);
    this.setFirstPoint(firstPoint);
    this.setSecondPoint(secondPoint);
    this.setClosure(closure);
    this.setFactor(factor);
    return this;
  }

  /**
   * Sets the closure type {@link #m_closure} of this angle to
   * <code>closure</code> and returns the changed angle.
   * @param closure an <code>int</code> holding the new closure type. Feasible values are
   * 0 (OPEN), 1 (CHORD), or 2 (PIE).
   * @return an <code>Affine2DAngle</code> holding the angle with the new closure type.
   */
  public MMAffine2DAngle setClosure(int closure) {
    m_closure = closure;
    return this;
  }

  /**
   * Sets the factor {@link #m_factor} of this angle to <code>factor</code> and
   * returns the changed angle. The
   * <code>factor</code> has to be positive or zero and determines the radius
   * in the following way: radius = <code>factor</code> * min(d1, d2), where
   * d1 and d2 are the distances between  the <code>vertex</code> and the
   * <code>firstPoint</code> or the <code>vertex</code> and the
   * <code>secondPoint</code>, resp.
   * @param factor a <code>double</code> value holding the new factor.
   * @return an <code>Affine2DAngle</code> holding the angle with the new factor.
   */
  public MMAffine2DAngle setFactor(double factor) {
    if (factor >= 0.0) {
      m_factor = factor;
      return this;
    }
    else
      throw new IllegalArgumentException("factor or radius must be positive or zero.");
  }

  /**
   * Sets the angular extend of this angle in degrees.
   * @param angularExtent a <code>double</code> value holding the angular extend in degrees.<br>
   * If the value is positive, only the second point will be changed.
   * If it is negative, the first point will first be set to the second point,
   * and then it will be set according to angularExtent.
   * @return an <code>Affine2DAngle</code> holding the angle.
   */
  public MMAffine2DAngle setAngularExtent(double angularExtent) {
  	double x, y;	// the new coordinates of the second point
  	double r;		// the distance between the first point and the vertex
  	boolean isPos = (angularExtent >= 0);
  	if (!isPos) m_firstPoint.setFromPoint(m_secondPoint);
    angularExtent += getStartingAngle();
    r = Math.sqrt(
    	Math.pow(m_firstPoint.getYAsDouble() - m_vertex.getYAsDouble(), 2) +
    	Math.pow(m_firstPoint.getXAsDouble() - m_vertex.getXAsDouble(), 2));
    x = m_vertex.getXAsDouble() + r * Math.cos(Math.toRadians(angularExtent));
    y = m_vertex.getYAsDouble() + r * Math.sin(Math.toRadians(angularExtent));
    if (isPos)
    	m_secondPoint.setXY(x, y);
    else {
    	m_firstPoint.setXY(x, y);
    }
    return this;
  }

  /**
   * See {@link #setAngularExtent}
   */
  public MMAffine2DAngle setDegrees(double angularExtent) {
    return setAngularExtent(angularExtent);
  }

  /**
   * Returns the starting angle of this angle in degrees.
   * @return a <code>double</code> holding the starting angle of this angle in degrees.
   */
  public double getStartingAngle() {
    double startingAngle;
    if (m_firstPoint.getYAsDouble() - m_vertex.getYAsDouble() >= 0.0) {
      startingAngle =
        Math.toDegrees(
          Math.atan2(
            m_firstPoint.getYAsDouble() - m_vertex.getYAsDouble(),
            m_firstPoint.getXAsDouble() - m_vertex.getXAsDouble()));
    }
    else {
      startingAngle =
        Math.toDegrees(
          Math.atan2(
            m_firstPoint.getYAsDouble() - m_vertex.getYAsDouble(),
            m_firstPoint.getXAsDouble() - m_vertex.getXAsDouble())
            + 2 * Math.PI);
    }
    double diff = Math.floor(Math.abs(startingAngle) / 360.0);
    if (startingAngle > 0)
      return startingAngle - diff * 360.0;
    else
      return startingAngle + diff * 360.0;
  }

  /**
   * Returns the angular extend of this angle in degrees.
   * @return a <code>double</code> holding the angular extend of this angle in degrees.
   */
  public double getAngularExtent() {
    double angularExtent;
    if (m_secondPoint.getYAsDouble() - m_vertex.getYAsDouble() > 0.0) {
      angularExtent =
        Math.toDegrees(
          Math.atan2(
            m_secondPoint.getYAsDouble() - m_vertex.getYAsDouble(),
            m_secondPoint.getXAsDouble() - m_vertex.getXAsDouble()))
          - getStartingAngle();
    }
    else {
      angularExtent =
        Math.toDegrees(
          Math.atan2(
            m_secondPoint.getYAsDouble() - m_vertex.getYAsDouble(),
            m_secondPoint.getXAsDouble() - m_vertex.getXAsDouble())
            + 2 * Math.PI)
          - getStartingAngle();
    }
    double diff = Math.floor(Math.abs(angularExtent) / 360.0);
    if (angularExtent > 0)
      return angularExtent - diff * 360.0;
    else if (angularExtent + diff * 360.0 >= 0)
      return angularExtent + diff * 360.0;
    else
      return angularExtent + (diff + 1.0) * 360.0;
  }

  /**
   * Returns the angular extend of this angle in degrees.
   * @return a <code>double</code> holding the angular extend of this angle in degrees.
   */
  public double getDegrees() {
    return getAngularExtent();
  }

  /**
   * Returns the angular extend of this angle in radians.
   * @return a <code>double</code> holding the angular extend of this angle in radians.
   */
  public double getRadians() {
    return Math.toRadians(getAngularExtent());
  }

  /**
   * Returns {@link #m_factor}.
   * @return a <code>double</code> holding {@link #m_factor}.
   */
  public double getFactor() {
    return m_factor;
  }
  public GeometryIF groupAction(GroupElementIF actingGroupElement) {
    logger.warning(
      "::groupAction currently works without checking the GroupElementIF");
    getFirstPoint().groupAction(actingGroupElement);
    getSecondPoint().groupAction(actingGroupElement);
    getVertex().groupAction(actingGroupElement);
    return this;
  }

  /**
   * Returns a <code>String</code> representation of this angle, giving the angular extend
   * in degrees.
   * @return a <code>String</code> representation of this angle.
   */
  public String toString() {
    return getDegrees() + " ";
  }

  public Rectangle2D getWorldBoundingBox() {
    double radius =
      Math.max(
        Math.sqrt(
          m_firstPoint.getXAsDouble() * m_firstPoint.getXAsDouble()
            + m_firstPoint.getYAsDouble() * m_firstPoint.getYAsDouble()),
        Math.sqrt(
          m_secondPoint.getXAsDouble() * m_secondPoint.getXAsDouble()
            + m_secondPoint.getYAsDouble() * m_secondPoint.getYAsDouble()));
    return new Rectangle2D.Double(
      m_vertex.getXAsDouble() - radius,
      m_vertex.getYAsDouble() - radius,
      2.0 * radius,
      2.0 * radius);
  }

  public int getDefaultTransformType() {
    return GeneralTransformer.AFFINE_DEFAULT_TRANSFORM;
  }

  public int getDefaultTransformTypeAsContainer() {
    return GeneralTransformer.NUMBER_STD_TRANSFORM;
  }

  public int getDefaultTransformTypeInCanvas() {
    return GeneralTransformer.AFFINE_DEFAULT_TRANSFORM;
  }
}