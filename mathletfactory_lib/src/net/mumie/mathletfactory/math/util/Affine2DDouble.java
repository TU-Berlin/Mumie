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

package net.mumie.mathletfactory.math.util;

import java.awt.geom.Rectangle2D;

import net.mumie.mathletfactory.action.message.IllegalUsageException;
import net.mumie.mathletfactory.action.message.MathContentException;
import net.mumie.mathletfactory.util.History;

/**
 * This class mainly serves as the affine mapping between two dimensional (world-) coordinate
 * space and the (flat) screen space.
 * <p>
 * Main methods will be applying this <code>Affine2DDouble</code> (
 * see {@link #applyTo(double[])}, where the <code>double</code> arrays will be treated
 * as column vectors. Applying this <code>Affine2DDouble</code> means, to perform
 * a matrix - vector product, that is applying the deformation part of the affine map
 * to the vector first,  and then adding the transformation part, which is simply "adding"
 * a translation vector tuple.
 *
 * @author vossbeck
 * @mm.docstatus finished
 */
public class Affine2DDouble extends AffineDouble {

	protected double a00 = 1, a01 = 0, a10 = 0, a11 = 1, t0 = 0, t1 = 0;

	private History m_history;

	public Affine2DDouble() {
		m_history = new History();
	}

  public Affine2DDouble(double a_00, double a_01, double a_10, double a_11, double t_0, double t_1){
    a00 = a_00;
    a01 = a_01;
    a10 = a_10;
    a11 = a_11;
    t0 = t_0;
    t1 = t_1;
  }

	public History getHistory() {
		return m_history;
	}

	public void addSnapshotToHistory() {
		m_history.addItem(new double[] { a00, a01, a10, a11, t0, t1 });
	}

	public void setFromHistory() {
		double[] historyItem = (double[]) m_history.getCurrentItem();
		a00 = historyItem[0];
		a01 = historyItem[1];
		a10 = historyItem[2];
		a11 = historyItem[3];
		t0 = historyItem[4];
		t1 = historyItem[5];
	}

	/**
	 * Sets this instance of <code>Affine2DDouble</code> to be the affine transformation, that maps
	 * the (axis parallel) <code>worldRectangle</code> onto the screen without any shearing and
	 * mapping the lower left (i.e. upper right) corner onto the lower left (i.e. upper right)
	 * corner of the screen and returns this (modified) instance of <code>Affine2DDouble</code>.
	 * <p>
	 * <b>Remark:</b>
	 * <br>
	 * Because java screen coordinates have positive y direction  defined "downward" on the screen,
	 * the resulting affine map contains a reflection in it's deformation part.
	 *
	 * @param LL represents the lower left corner of the rectangle, first component in x and the
	 * second component in y direction.
	 * @param UR reprensents the x,y coordinates of the upper right corner of the rectangle.
	 *
	 * @param width the width of the screen.
	 * @param height the height of the screen
	 */
	public Affine2DDouble setRectToScreen(
		double[] LL,
		double UR[],
		double width,
		double height) {
		double dx = UR[0] - LL[0];
		double dy = UR[1] - LL[1];
		double x1 = LL[0];
		double y2 = UR[1];
		if (dx <= 0)
			throw new IllegalUsageException("lower left point has x coordinate greater or equal to upper right! ");
		if (dx <= 0)
			throw new IllegalUsageException("lower left point has y coordinate greater or equal to upper right! ");
		a00 = width / dx;
		a01 = 0;
		a10 = 0;
		a11 = -height / dy;
		t0 = (-x1 * width) / dx;
		t1 = (y2 * height) / dy;
		return this;
	}

	/**
	 * Sets this instance of <code>Affine2DDouble</code> to be the affine transformation, that maps
	 * the (axis parallel) <code>worldRectangle</code> onto the screen without any shearing and
	 * mapping the lower left (i.e. upper right) corner onto the lower left (i.e. upper right)
	 * corner of the screen and returns this (modified) instance of <code>Affine2DDouble</code>.
	 * <p>
	 * <b>Remark:</b>
	 * <br>
	 * Because java screen coordinates have positive y direction  defined "downward" on the screen,
	 * the resulting affine map contains a reflection in it's deformation part.
	 *
	 * @param worldRectangle is treated as an axis parallel rectangle to be mapped onto the screen.
	 * <br>
	 * ( <code>worldRectangle.getX()</code>, <code>worldRectangle.getY()</code> ) is the lower left
	 * corner of the rectangle.
	 */
	public Affine2DDouble setRectToScreen(
		Rectangle2D worldRectangle,
		double w,
		double h) {
		double dx = worldRectangle.getWidth();
		double dy = worldRectangle.getHeight();
		double x1 = worldRectangle.getX();
		double y2 = worldRectangle.getY() + dy;
		a00 = w / dx;
		a01 = 0;
		a10 = 0;
		a11 = -h / dy;
		t0 = (-x1 * w) / dx;
		t1 = (y2 * h) / dy;
		return this;
	}

    /**
     * Sets this instance of <code>Affine2DDouble</code> to be the affine transformation, that maps
     * the (axis parallel) <code>worldRectangle</code> onto the screen with keeping the current
     * aspect ratio.
     * <p>
     * <b>Remark:</b>
     * <br>
     * Because java screen coordinates have positive y direction  defined "downward" on the screen,
     * the resulting affine map contains a reflection in it's deformation part.
     *
     * @param worldRectangle is treated as an axis parallel rectangle to be mapped onto the screen.
     * <br>
     * ( <code>worldRectangle.getX()</code>, <code>worldRectangle.getY()</code> ) is the lower left
     * corner of the rectangle.
     */
    public Affine2DDouble setRectToScreenKeepAR(
	Rectangle2D worldRectangle,
	double w,
       	double h) {

	double dx = worldRectangle.getWidth();
	double dy = worldRectangle.getHeight();
	double x1 = worldRectangle.getX();
	double y2 = worldRectangle.getY() + dy;

	double scale = Math.min(w/dx, h/dy);

	a00 = scale;
	a01 = 0;
	a10 = 0;
	a11 = -scale;
	t0 = (-x1 * w) / dx;
	t1 = (y2 * h) / dy;
	return this;
    }

	/**
	 * Sets this <code>Affine2DDouble</code> to become the affine map without shearing part, that
	 * transforms the (standard) unit square [0,1]x[0,1] onto the screen which is <code>width</code>
	 * broad and <code>height</code> high -the point (0,0) will be mapped onto the lower left corner
	 * of the screen and point (1,1) will be mapped onto the upper right corner of the screen.
	 * <p>
	 * <b>Attention:</b>
	 * <br>
	 * Because in java screen coordinates the y-axis points downstairs this map will contain a
	 * reflection part within it's deformation.
	 */
	public Affine2DDouble setNormalizedToScreen(double width, double height) {
		a00 = width;
		a11 = -height;
		a01 = 0;
		a10 = 0;
		t0 = 0;
		t1 = height;
		return this;
	}

	public Affine2DDouble setPointToCanvasCentreUF(
		double pointX,
		double pointY,
		double ufScale,
		double screenWidth,
		double screenHeight) {
		a00 = ufScale;
		a11 = -ufScale;
		a10 = 0;
		a01 = 0;
		t0 = screenWidth / 2. - a00 * pointX;
		t1 = screenHeight / 2. - a11 * pointY;
		return this;
	}



	/**
	 * Applies this <code>Affine2DDouble</code> as affine transformation to the affine
	 * point with coordinates <code>xIn, yIn</code> and stores the result into <code>out
	 * </code>.
	 * <p>
	 * Observe, that application of an <code>Affine2DDouble</code> always means: applying
	 * first the deformation part to the element and then adding the translation part.
	 */
	public void applyTo(double xIn, double yIn, double[] out) {
		out[0] = a00 * xIn + a01 * yIn + t0;
		out[1] = a10 * xIn + a11 * yIn + t1;
	}

	/**
	 * Applies this <code>Affine2DDouble</code> as affine transformation to the affine
	 * point with coordinates <code>in</code> and stores the result into <code>out
	 * </code>. <b>Attention</b> - make sure that <code>in</code> and <code>out</code>
	 * do not refer to the same double array. If you really want to change the (incoming)
	 * argument, use method {@link #applyTo(double[])} instead.
	 */
	public void applyTo(double[] in, double[] out) {
		out[0] = a00 * in[0] + a01 * in[1] + t0;
		out[1] = a10 * in[0] + a11 * in[1] + t1;
	}

	/**
	 * This is a vectorized form of the map-to-point-application as performed
	 * in {@link #applyTo(double[], double[])},
	 * but observe that no check about correctly corresponding array dimensions will be
	 * done.
	 */
	public void applyTo(double[] xin, double[] yin, float[] xout, float[] yout) {
		for (int i = 0; i < xin.length; ++i) {
			xout[i] = (float) (a00 * xin[i] + a01 * yin[i] + t0);
			yout[i] = (float) (a10 * xin[i] + a11 * yin[i] + t1);
		}
	}

  /**
   * This is a vectorized form of the map-to-point-application as performed
   * in {@link #applyTo(double[], double[])},
   * but observe that no check about correctly corresponding array dimensions will be
   * done.
   */
  public void applyTo(double[] xin, double[] yin, float[] xout, float[] yout, int count) {
    for (int i = 0; i < count; ++i) {
      xout[i] = (float) (a00 * xin[i] + a01 * yin[i] + t0);
      yout[i] = (float) (a10 * xin[i] + a11 * yin[i] + t1);
    }
  }

	/**
	 * Applies this <code>Affine2DDouble</code> as affine transformation to the affine
	 * point with coordinates <code>inout</code> and stores them into <code>inout</code>
	 * again. Observe, that the method will instantiate a (temporary) double, which may
	 * cause performing problems in huge applications.
	 */
	public void applyTo(double[] inout) {
		double tmp = a00 * inout[0] + a01 * inout[1] + t0;
		inout[1] = a10 * inout[0] + a11 * inout[1] + t1;
		inout[0] = tmp;
	}

	/**
	 * Applies the deformation part (i.e. the linear part) of this <code>Affine2DDouble</code>
	 * to the tupel <code>inout</code>, which is treated as column vector -- <code>inout</code>
	 * will hold the values of the deformed vector after this method call.
	 *
	 * @param inout is treated as column vector and will hold the entries of the deformed vector after
	 * this method call.
	 */
	public void applyDeformationPartTo(double[] inout) {
		double tmp = a00 * inout[0] + a01 * inout[1];
		inout[1] = a10 * inout[0] + a11 * inout[1];
		inout[0] = tmp;
	}

	/**
	 * Calculates the inverse affine map of <code>this</code> and assigns
	 * the result to <code>theInverseMap</code> but will throw an exception if
	 * <code>this</code> and <code>theInverseMap</code> point to the same <code>Affine2DDouble</code>.
	 */
	public void getInverse(Affine2DDouble theInverseMap) {
		if (this == theInverseMap)
			throw new IllegalUsageException(
				"calling instance of Affine2DDouble and the parameter must"
					+ "not point to the same map!");
		double invdet = 1. / (a00 * a11 - a10 * a01);
		theInverseMap.a00 = invdet * a11;
		theInverseMap.a11 = invdet * a00;
		theInverseMap.a01 = -invdet * a01;
		theInverseMap.a10 = -invdet * a10;
		theInverseMap.t0 = - (theInverseMap.a00 * t0 + theInverseMap.a01 * t1);
		theInverseMap.t1 = - (theInverseMap.a10 * t0 + theInverseMap.a11 * t1);
	}

	/**
	 * Makes this <code>Affine2DDouble</code> be the same affine map as <code>anAffineMap</code>.
	 *
	 * @mm.sideeffects Both <code>Affine2DDouble</code>s will be independent after the method call.
	 * @param anAffineMap is an instance of <code>Affine2DDouble</code>.
	 * @return the calling instance of <code>Affine2DDouble</code>.
	 */
	public Affine2DDouble setFrom(Affine2DDouble anAffineMap) {
		a00 = anAffineMap.a00;
		a01 = anAffineMap.a01;
		a10 = anAffineMap.a10;
		a11 = anAffineMap.a11;
		t0 = anAffineMap.t0;
		t1 = anAffineMap.t1;
		return this;
	}

	/**
	 * This instance of <code>Affine2DDouble</code> will become it's own inverse.
	 * <p>
	 * <b>Remark</b>:
	 * <br>
	 * Each method call will (temporary) create a new instance of <code>Affine2DDouble</code>.
	 */
	public Affine2DDouble invertMe() {
		Affine2DDouble m_helper = new Affine2DDouble();
		getInverse(m_helper);
		this.setFrom(m_helper);
		return this;
	}

	public Affine2DDouble leftTranslate(double[] b) {
		t0 += b[0];
		t1 += b[1];
		return this;
	}

	public Affine2DDouble rightTranslate(double[] b) {
		//    double invdet = 1. / (a00*a11 - a10*a01);
		//    t0 -= (b[0]*a00 - b[1]*a10)*invdet;
		//    t1 -= (-b[0]*a01+ b[1]*a11)*invdet;
		t0 += b[0] * a00 + b[1] * a01;
		t1 += b[0] * a10 + b[1] * a11;
		return this;
	}

	public Affine2DDouble leftTranslateX(double b) {
		t0 += b;
		return this;
	}

	public Affine2DDouble rightTranslateX(double b) {
		double invdet = 1. / (a00 * a11 - a10 * a01);
		t0 -= b * a00 * invdet;
		t1 += b * a01 * invdet;
		return this;
	}

	public Affine2DDouble leftTranslateY(double b) {
		t1 += b;
		return this;
	}

	public Affine2DDouble rightTranslateY(double b) {
		double invdet = 1. / (a00 * a11 - a10 * a01);
		t0 += b * a10 * invdet;
		t1 -= b * a11 * invdet;
		return this;
	}

	public Affine2DDouble leftScale(double lambdaX, double lambdaY) {
		a00 *= lambdaX;
		a01 *= lambdaY;
		a10 *= lambdaX;
		a11 *= lambdaY;

		t0 *= lambdaX;
		t1 *= lambdaY;

		return this;
	}

	public Affine2DDouble leftScale(double lambda) {
		return leftScale(lambda, lambda);
	}

	/**
	 * Will replace this <code>Affine2DDouble</code> like this = this*S, where <code>S</code>
	 * represents the linear (and by this affine) map, which does scaling by <code>lambdaX
	 * </code> in x-direction and by <code>lambdaY</code> in y direction.
	 * <br>
	 * <b>Observe:</b>
	 * <br>
	 * The translation part of this <code>Affine2DDouble</code> will remain unchainged.
	 */
	public Affine2DDouble rightScale(double lambdaX, double lambdaY) {
		a00 *= lambdaX;
		a01 *= lambdaY;
		a10 *= lambdaX;
		a11 *= lambdaY;
		return this;
	}

	/**
	 * Will replace this <code>Affine2DDouble</code> like this = this*S, where <code>S</code>
	 * represents the linear (and by this affine) map, which does scaling by <code>
	 * lambda</code> in x and y direction.
	 * <br>
	 * <b>Observe:</b>
	 * <br>
	 * The translation part of this <code>Affine2DDouble</code> will remain unchainged.
	 */
	public Affine2DDouble rightScale(double lambda) {
		return rightScale(lambda, lambda);
	}

	public Affine2DDouble leftRotate(double lambda) {
		double tmp00 = a00;
		a00 = tmp00 * Math.cos(lambda) - a10 * Math.sin(lambda);
		a10 = tmp00 * Math.sin(lambda) + a10 * Math.cos(lambda);
		double tmp01 = a01;
		a01 = tmp01 * Math.cos(lambda) - a11 * Math.sin(lambda);
		a11 = tmp01 * Math.sin(lambda) + a11 * Math.cos(lambda);
		return this;
	}

	/**
	 *   this -> this * aTrafo
	 */
	public Affine2DDouble multRight(Affine2DDouble aTrafo) {
		t0 = t0 + a00 * aTrafo.t0 + a01 * aTrafo.t1;
		t1 = t1 + a10 * aTrafo.t0 + a11 * aTrafo.t1;
		double tmp00 = a00;
		double tmp01 = a01;
		double tmp11 = a11;
		double tmp10 = a10;

		a00 = tmp00 * aTrafo.a00 + tmp01 * aTrafo.a10;
		a10 = tmp10 * aTrafo.a00 + tmp11 * aTrafo.a10;
		a01 = tmp00 * aTrafo.a01 + tmp01 * aTrafo.a11;
		a11 = tmp10 * aTrafo.a01 + tmp11 * aTrafo.a11;

		return this;
	}

	/**
	 *   this -> aTrafo * this
	 */
	public Affine2DDouble multLeft(Affine2DDouble aTrafo) {
		t0 = aTrafo.t0 + aTrafo.a00 * t0 + aTrafo.a01 * t1;
		t1 = aTrafo.t1 + aTrafo.a10 * t0 + aTrafo.a11 * t1;
		double tmp00 = a00;
		double tmp01 = a01;
		double tmp11 = a11;
		double tmp10 = a10;

		a00 = aTrafo.a00 * tmp00 + aTrafo.a01 * tmp10;
		a10 = aTrafo.a10 * tmp00 + aTrafo.a11 * tmp10;
		a01 = aTrafo.a00 * tmp01 + aTrafo.a01 * tmp11;
		a11 = aTrafo.a10 * tmp01 + aTrafo.a11 * tmp11;

		return this;
	}

	/**
	 * Performs the composition of the <code>Affine2DDouble</code>s like map1 --> map1 * map2
	 */
	public static void compose(Affine2DDouble map1, Affine2DDouble map2) {
		map1.t0 = map1.a00 * map2.t0 + map1.a01 * map2.t1 + map1.t0;
		map1.t1 = map1.a10 * map2.t0 + map1.a11 * map2.t1 + map1.t1;
		double tmp00 = map1.a00;
		double tmp10 = map1.a10;
		map1.a00 = tmp00 * map2.a00 + map1.a01 * map2.a10;
		map1.a01 = tmp00 * map2.a01 + map1.a01 * map2.a11;
		map1.a10 = tmp10 * map2.a00 + map1.a11 * map2.a10;
		map1.a11 = tmp10 * map2.a01 + map1.a01 * map2.a11;
	}

	/**
	 * Performs the composition of <code>Affine2DDouble</code>s resulting in
	 * <code>result<code> = <code>map1</code>*<code>map2</code> after this method call.
	 */
	public static void compose(
		Affine2DDouble result,
		Affine2DDouble map1,
		Affine2DDouble map2) {
		result.t0 = map1.a00 * map2.t0 + map1.a01 * map2.t1 + map1.t0;
		result.t1 = map1.a10 * map2.t0 + map1.a11 * map2.t1 + map1.t1;
		result.a00 = map1.a00 * map2.a00 + map1.a01 * map2.a10;
		result.a01 = map1.a00 * map2.a01 + map1.a01 * map2.a11;
		result.a10 = map1.a10 * map2.a00 + map1.a11 * map2.a10;
		result.a11 = map1.a10 * map2.a01 + map1.a11 * map2.a11;
	}

	/**
	 * Performs the invertation of the affine map <code>map</code> and puts the inverse
	 * into <code>invMap</code>.
	 * <p>
	 * Observe, that no check about invertibility is done and that <code>map</code> and
	 * <code>invMap</code> do not point to the same <code>Affine2DDouble</code>.
	 */
	public static void invert(Affine2DDouble map, Affine2DDouble invMap) {
		double invdet = 1. / (map.a00 * map.a11 - map.a10 * map.a01);
		invMap.a00 = invdet * map.a11;
		invMap.a11 = invdet * map.a00;
		invMap.a01 = -invdet * map.a01;
		invMap.a10 = -invdet * map.a10;
		invMap.t0 = - (invMap.a00 * map.t0 + invMap.a01 * map.t1);
		invMap.t1 = - (invMap.a10 * map.t0 + invMap.a11 * map.t1);
	}

  /** If the matrix has a diagonal form, the x scale factor is returned. Otherwise, an exception is thrown. */
	public double getXScale() throws MathContentException {
		if (a01 == 0 && a10 == 0)
			return a00;
		else
			throw new MathContentException("Matrix has not diagonal form due to standard coordinates");
	}

  /** Returns a string representation of this object. */
	public String toString() {
		String strRep =
			"| "
				+ String.valueOf(a00)
				+ "\t"
				+ String.valueOf(a01)
				+ "\t"
				+ String.valueOf(t0)
				+ " |\n"
				+ "| "
				+ String.valueOf(a10)
				+ "\t"
				+ String.valueOf(a11)
				+ "\t"
				+ String.valueOf(t1)
				+ " |";
		return strRep;
	}

}
