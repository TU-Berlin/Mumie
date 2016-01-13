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
import java.util.logging.Logger;

import net.mumie.mathletfactory.display.LineDisplayProperties;
import net.mumie.mathletfactory.math.algebra.GroupElementIF;
import net.mumie.mathletfactory.math.algebra.geomgroup.AffineGroupElement;
import net.mumie.mathletfactory.math.geom.GeometryIF;
import net.mumie.mathletfactory.math.geom.affine.Affine2DLine;
import net.mumie.mathletfactory.math.geom.affine.Affine2DPoint;
import net.mumie.mathletfactory.math.geom.affine.AffineGeometryIF;
import net.mumie.mathletfactory.math.geom.affine.AffineSpace;
import net.mumie.mathletfactory.mmobject.MMDefaultCanvasObject;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 * This class represents rays in the affine 2d space. A ray is defined by an initial point
 * and a second point, where the ray runs through.
 * 
 * @author Mrose
 * @mm.docstatus finished
 */
public class MMAffine2DRay extends MMDefaultCanvasObject implements AffineGeometryIF {

	private static Logger logger =
		Logger.getLogger(MMAffine2DRay.class.getName());

	private Affine2DPoint m_initialPoint, m_rayRunsThrough;

	public MMAffine2DRay(
		Affine2DPoint initalPoint,
		Affine2DPoint rayRunsThrough) {
		if (!initalPoint
			.getNumberClass()
			.equals(rayRunsThrough.getNumberClass()))
			throw new IllegalArgumentException("initial and run through point must have same entry class");
		if (initalPoint.equals(rayRunsThrough))
			throw new IllegalArgumentException("initial and run through point must be different points");
		m_initialPoint = initalPoint;
		m_rayRunsThrough = rayRunsThrough;
    setDisplayProperties(new LineDisplayProperties());
	}

	public MMAffine2DRay(
		Class entryClass,
		double x1,
		double y1,
		double x2,
		double y2) {
		this(
			new Affine2DPoint(entryClass, x1, y1),
			new Affine2DPoint(entryClass, x2, y2));
	}

	public AffineGeometryIF groupAction(AffineGroupElement actingGroupElement) {
		logger.warning(
			"::groupAction currently works without checking the GroupElementIF");
		getInitialPoint().groupAction(actingGroupElement);
		getRayRunsThrough().groupAction(actingGroupElement);
		return this;
	}

	public GeometryIF groupAction(GroupElementIF actingGroupElement) {
		return groupAction((AffineGroupElement) actingGroupElement);
	}

	public int getGeomType() {
		return GeometryIF.AFFINE2D_GEOMETRY;
	}

	public Class getNumberClass() {
		return getInitialPoint().getNumberClass();
		//initial and run through point always have
		// same entry class.
	}

  /** Returns the initial point.*/
	public Affine2DPoint getInitialPoint() {
		return m_initialPoint;
	}

  /** Sets the initial point.*/
	public MMAffine2DRay setInitialPoint(Affine2DPoint anAffine2DPoint) {
		if (anAffine2DPoint.getNumberClass().equals(getNumberClass())) {
			if (!anAffine2DPoint.equals(m_rayRunsThrough)) {
				m_initialPoint = anAffine2DPoint;
				return this;
			}
			else
				throw new IllegalArgumentException("initial and run through point must be different points");
		}
		else
			throw new IllegalArgumentException(
				"entry class of anAffine2DPoint and the "
					+ "Affine2DRay must coincide");
	}

  /** Sets the initial point.*/
	public MMAffine2DRay setInitialPoint(double x, double y) {
		return setInitialPoint(new Affine2DPoint(getNumberClass(), x, y));
	}

  /** Returns a point where the ray runs through.*/
	public Affine2DPoint getRayRunsThrough() {
		return m_rayRunsThrough;
	}

  /** Sets the point where the ray runs through.*/
	public MMAffine2DRay setRayRunsThrough(Affine2DPoint anAffine2DPoint) {
		if (anAffine2DPoint.getNumberClass().equals(getNumberClass())) {
			if (!anAffine2DPoint.equals(m_initialPoint)) {
				m_rayRunsThrough = anAffine2DPoint;
				return this;
			}
			else
				throw new IllegalArgumentException("initial and run through point must be different points");
		}
		else
			throw new IllegalArgumentException(
				"entry class of anAffine2DPoint and the "
					+ "Affine2DRay must coincide");
	}

  /** Sets the point where the ray runs through.*/
	public MMAffine2DRay setRayRunsThrough(double x, double y) {
		return setRayRunsThrough(new Affine2DPoint(getNumberClass(), x, y));
	}

	public int getDefaultTransformType() {
		return GeneralTransformer.AFFINE_DEFAULT_TRANSFORM;
	}

	public int getDefaultTransformTypeInCanvas() {
		return GeneralTransformer.AFFINE_DEFAULT_TRANSFORM;
	}

  /** Returns the slope of the ray.*/
	public double getSlopeAsDouble() {
		return (
			getRayRunsThrough().getYAsDouble()
				- getInitialPoint().getYAsDouble())
			/ (getRayRunsThrough().getXAsDouble()
				- getInitialPoint().getXAsDouble());
	}

	public Rectangle2D getWorldBoundingBox() {
		Affine2DLine help =
			new Affine2DLine(getInitialPoint(), getRayRunsThrough());
		Affine2DLine xAxis =
			new Affine2DLine(this.getNumberClass(), 0, 0, 1, 0);
		Affine2DLine yAxis =
			new Affine2DLine(this.getNumberClass(), 0, 0, 0, 1);
		AffineSpace xintersect = AffineSpace.intersected(xAxis, help);
		AffineSpace yintersect = AffineSpace.intersected(yAxis, help);

		Rectangle2D result = null;
		if (xintersect.getDimension() == 1) {
			result = new Rectangle2D.Double(-1, -1, 2, 2);
		}
		else {
			if (xintersect.hasDefiningPointAtInfinity()) {
				double distance =
					yintersect
						.getAffineCoordinates()[0]
						.getEntry(2)
						.getDouble();
				result =
					new Rectangle2D.Double(
						-distance / 2,
						0,
						distance,
						distance);
			}
		}
		if (result == null) {
			double x1 =
				xintersect.getAffineCoordinates()[0].getEntry(1).getDouble();
			double x2 =
				xintersect.getAffineCoordinates()[0].getEntry(2).getDouble();
			double y1 =
				yintersect.getAffineCoordinates()[0].getEntry(1).getDouble();
			double y2 =
				yintersect.getAffineCoordinates()[0].getEntry(2).getDouble();
			result =
				new Rectangle2D.Double(
					Math.min(x1, x2),
					Math.min(y1, y2),
					Math.abs(x1 - x2),
					Math.abs(y1 - y2));
		}
		result.setFrame(
			result.getX() - result.getWidth() * .3,
			result.getY() - result.getHeight() * .3,
			result.getWidth() * 1.6,
			result.getHeight() * 1.6);
		return result;
	}
}
