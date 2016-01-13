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

package net.mumie.mathletfactory.math.geom.affine;

import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.algebra.op.PolynomialOpHolder;
import net.mumie.mathletfactory.math.algebra.op.UsesOperationIF;
import net.mumie.mathletfactory.math.algebra.poly.Polynomial;
import net.mumie.mathletfactory.math.number.MBigRational;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.mmobject.number.MMDouble;
import net.mumie.mathletfactory.mmobject.number.MMRational;

/**
 * This class represents a line living in a 2d affine space.
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */
public class Affine2DLine extends AffineLine implements UsesOperationIF {

	public Affine2DLine(Class entryClass) {
		super(entryClass, 2);
	}

	public Affine2DLine(Affine2DLine anAffine2DLine) {
		super(anAffine2DLine);
	}

	public Affine2DLine(Affine2DPoint coord1, Affine2DPoint coord2) {
		this(coord1.getNumberClass());
		if (!coord1.getNumberClass().equals(coord1.getNumberClass())) {
			throw new IllegalArgumentException("initial and end point must have same entry class");
		}
		setProjectiveCoordinates(
			new NumberTuple[] {
				coord1.getProjectiveCoordinatesOfPoint(),
				coord2.getProjectiveCoordinatesOfPoint()});
	}

	public Affine2DLine(
		Class entryClass,
		double x1,
		double y1,
		double x2,
		double y2) {
		this(
			new Affine2DPoint(entryClass, x1, y1),
			new Affine2DPoint(entryClass, x2, y2));
	}

	public AffineSpace setFromPoints(
		Class entryClass,
		double x1,
		double y1,
		double x2,
		double y2) {
		if ((x1==x2) && (y1==y2) ) {
			throw new ArithmeticException("identic points do not define affine line");
		}
		return setFromPoints(
			
			new Affine2DPoint[] {
				new Affine2DPoint(entryClass, x1, y1),
				new Affine2DPoint(entryClass, x2, y2)});
	}

	public MNumber getSlope() {
		if (getAffineCoordinatesOfInitialPoint()
			.getEntry(1)
			.equals(getAffineCoordinatesOfEndPoint().getEntry(1))) {
			return NumberFactory.newInstance(getNumberClass(), Double.POSITIVE_INFINITY); 
		}
		else
			return (
				getAffineCoordinatesOfEndPoint().getEntry(2).copy().sub(
					getAffineCoordinatesOfInitialPoint().getEntry(2))).div(
				getAffineCoordinatesOfEndPoint().getEntry(1).copy().sub(
					getAffineCoordinatesOfInitialPoint().getEntry(1)));
	}

	public MNumber getAxisIntersect() {
		if (getAffineCoordinatesOfInitialPoint()
			.getEntry(2)
			.equals(getAffineCoordinatesOfEndPoint().getEntry(2))) {
        return NumberFactory.newInstance(getNumberClass(), Double.POSITIVE_INFINITY);
		}
		else
      if(getSlope().isInfinity())
        return getAffineCoordinatesOfInitialPoint().getEntry(1);
        else
			 return getAffineCoordinatesOfInitialPoint().getEntry(2).copy().sub(
			   	this.getSlope().mult(
				  	getAffineCoordinatesOfInitialPoint().getEntry(1)));
	}

	public String toString() {
		MNumber m_slope, m_axisIntercept;
		if (isDegenerated()) {
			if (getNumberClass() == MBigRational.class) {
				m_slope =
					new MMRational(
						(MBigRational) (getAffineCoordinatesOfInitialPoint()
							.getEntry(1)));
				m_axisIntercept =
					new MMRational(
						(MBigRational) (getAffineCoordinatesOfInitialPoint()
							.getEntry(2)));
			}
			else {
				m_slope =
					new MMDouble(
						(MDouble) (getAffineCoordinatesOfInitialPoint()
							.getEntry(1)));
				m_axisIntercept =
					new MMDouble(
						(MDouble) (getAffineCoordinatesOfInitialPoint()
							.getEntry(2)));
			}
			return "x = " + m_slope + ", y = " + m_axisIntercept;
		}
		else if (
			(getAffineCoordinatesOfInitialPoint().getEntry(1)).equals(
				getAffineCoordinatesOfEndPoint().getEntry(1))) {
			MNumber help;
			if (getNumberClass() == MBigRational.class) {
				help =
					new MMRational(
						(MBigRational) getAffineCoordinatesOfInitialPoint()
							.getEntry(
							1));
			}
			else {
				help =
					new MMDouble(
						(MDouble) getAffineCoordinatesOfInitialPoint()
							.getEntry(
							1));
			}
			return "x = " + help;
		}
		else {
			if (getNumberClass() == MBigRational.class) {
				m_slope = new MMRational((MBigRational) getSlope());
				m_axisIntercept =
					new MMRational((MBigRational) getAxisIntersect());
			}
			else {
				m_slope = new MMDouble((MDouble) getSlope());
				m_axisIntercept = new MMDouble((MDouble) getAxisIntersect());
			}
			return "y = " + m_slope + "x + " + m_axisIntercept;
		}
	}

	public void setFromAxisInterceptAndSlope(NumberTuple data) {
		if (data.getDimension() == 2) {
			setFromPoints(
				getNumberClass(),
				0.0,
				data.getEntry(1).getDouble(),
				1.0,
				data.getEntry(1).copy().add(data.getEntry(2)).getDouble());
		}
		else if (data.getDimension() == 1) {
			setFromPoints(
				getNumberClass(),
				0.0,
				data.getEntry(1).getDouble(),
				1.0,
				data.getEntry(1).getDouble());
		}
		else
			throw new IllegalArgumentException(
				"Data " + data + " unapplicable for axis intercept and slope");
	}

	public void setOperation(Operation operation) {
		if (!operation.isAffine())
			throw new IllegalArgumentException(
				"No straight line expression: " + operation);
		PolynomialOpHolder help1 = operation.getPolynomialHolder();
		Polynomial help2 = help1.getAsPolynomial("x");
		NumberTuple help3 = help2.getStandardCoefficientsRef();
		//    NumberTuple help3 = operation.getPolynomialHolder().getAsPolynomial("x").getStandardCoefficientsRef();
		setFromAxisInterceptAndSlope(help3);
	}

	public Polynomial getAsPolynomial() {
		return new Polynomial(
			new NumberTuple(
				getNumberClass(),
				getAxisIntersect().getDouble(),
				getSlope().getDouble()));
	}

	public Operation getOperation() {
		return new PolynomialOpHolder(this.getAsPolynomial()).getAsOperation();
	}
}
