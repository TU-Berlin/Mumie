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

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.LineDisplayProperties;
import net.mumie.mathletfactory.math.geom.affine.Affine2DPoint;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 * This class represents an affine 2D line segment of finite length.
 *  
 * @author vossbeck, Paehler
 * @mm.docstatus finished
 */
public class MMAffine2DLineSegment
	extends MMAffine2DPolygon
	implements MMCanvasObjectIF {

	public MMAffine2DLineSegment(Class entryClass) {
		this(
			new MMAffine2DPoint(entryClass, 0, 0),
			new MMAffine2DPoint(entryClass, 0, 1));
	}

	public MMAffine2DLineSegment(
		MMAffine2DPoint initialPoint,
		MMAffine2DPoint endPoint) {
		super(new MMAffine2DPoint[]{initialPoint, endPoint});
    setDisplayProperties(new LineDisplayProperties(LineDisplayProperties.DEFAULT));
	}

	public MMAffine2DLineSegment(
		Class entryClass,
		double x1,
		double y1,
		double x2,
		double y2) {
		this(
			entryClass,
			x1,
			y1,
			x2,
			y2,
			new LineDisplayProperties(LineDisplayProperties.DEFAULT));
	}

  public MMAffine2DLineSegment(
    Class entryClass,
    double x1,
    double y1,
    double x2,
    double y2, LineDisplayProperties props) {
    this(
      new MMAffine2DPoint(entryClass, x1, y1),
      new MMAffine2DPoint(entryClass, x2, y2));
    setDisplayProperties(props);
  }

  /** Returns the first endpoint of this line segment. */ 
  public MMAffine2DPoint getInitialPoint() {
    return (MMAffine2DPoint) getVertexRef(0);
  }

  /** Sets the first endpoint of this line segment. */
  public MMAffine2DLineSegment setInitialPoint(Affine2DPoint anAffine2DPoint) {
    if (anAffine2DPoint.getNumberClass().equals(getNumberClass())) {
      setVertexRef(0, anAffine2DPoint);
      return this;
    }
    else
      throw new IllegalArgumentException(
        "entry class of anAffine2DPoint and the "
          + "Affine2DLineSegment must coincide");
  }

  /** Sets the first endpoint of this line segment. */
  public MMAffine2DLineSegment setInitialPoint(double x, double y) {
    getVertexRef(0).setXY(x, y);
    return this;
  }

  /** Returns the second endpoint of this line segment. */
  public MMAffine2DPoint getEndPoint() {
    return (MMAffine2DPoint) getVertexRef(1);
  }

  /** Returns the slope of this line segment. */
  public double getSlopeAsDouble() {
    return (
      getEndPoint().getYAsDouble() - getInitialPoint().getYAsDouble())
      / (getEndPoint().getXAsDouble() - getInitialPoint().getXAsDouble());
  }

  /** Sets the second endpoint of this line segment. */
  public MMAffine2DLineSegment setEndPoint(Affine2DPoint anAffine2DPoint) {
    if (anAffine2DPoint.getNumberClass().equals(getNumberClass())) {
      setVertexRef(1, anAffine2DPoint);
      return this;
    }
    else
      throw new IllegalArgumentException(
        "entry class of anAffine2DPoint and the "
          + "Affine2DLineSegement must coincide");
  }

  /** Sets the second endpoint of this line segment. */
  public MMAffine2DLineSegment setEndPoint(double x, double y) {
    getVertexRef(1).setXY(x, y);
    return this;
  }

	public int getDefaultTransformType() {
		return GeneralTransformer.AFFINE_DEFAULT_TRANSFORM;
	}

	public int getDefaultTransformTypeInCanvas() {
		return GeneralTransformer.AFFINE_DEFAULT_TRANSFORM;
	}

	public Rectangle2D getWorldBoundingBox() {
		return new Rectangle2D.Double(
			Math.min(
				getInitialPoint().getXAsDouble(),
				getEndPoint().getXAsDouble()),
			Math.min(
				getInitialPoint().getYAsDouble(),
				getEndPoint().getYAsDouble()),
			Math.abs(
				getInitialPoint().getXAsDouble()
					- getEndPoint().getXAsDouble()),
			Math.abs(
				getInitialPoint().getYAsDouble()
					- getEndPoint().getYAsDouble()));
	}
  
  
  /** Sets the object fill color (without border). */
  public void setObjectColor(Color color){
    DisplayProperties props = getDisplayProperties(); 
    props.setObjectColor(color);
    setDisplayProperties(props);
  }

  /** Sets the object border color. */
  public void setBorderColor(Color color){
    DisplayProperties props = getDisplayProperties(); 
    props.setBorderColor(color);
    setDisplayProperties(props);
  }
  
  /** Sets both the object's fill and border color. */
  public void setColor(Color color){
    setObjectColor(color);
    setBorderColor(color);
  }
}
