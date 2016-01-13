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

package net.mumie.mathletfactory.transformer.j3d;

import net.mumie.mathletfactory.action.message.TodoException;
import net.mumie.mathletfactory.display.CanvasDrawable;
import net.mumie.mathletfactory.display.j3d.J3DCoordinateSystemDrawable;
import net.mumie.mathletfactory.math.geom.affine.AffinePoint;
import net.mumie.mathletfactory.math.number.NumberTypeDependentIF;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR3;

/**
 *  This class transforms the standard euclidean 3d vector space in the Java3D 
 *  rendering system.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class DefaultR3Transformer
	extends Canvas3DObjectJ3DTransformer {

	private boolean renderedFirstTime = true;

	/** 
	 * Constructs the transformer for a given 
	 * {@link net.mumie.mathletfactory.display.j3d.MMJ3DCanvas} display.
	 */
	public DefaultR3Transformer() {
		m_allDrawables = new CanvasDrawable[] { new J3DCoordinateSystemDrawable()};
		m_activeDrawable = m_allDrawables[0];
	}

	/**
	 * Returns the origin {0,0,0}.
	 * @see net.mumie.mathletfactory.transformer.Canvas3DObjectTransformer#getWorldPickPointFromMaster()
	 */
	public double[] getWorldPickPointFromMaster() {
		return new double[] { 0, 0, 0 };
	}

	/**
	 * Sets the (screen-) coordinates of the active drawable by using the
	 * math coordinates and {@link #world2screen world2screen}
	 */
	public void synchronizeWorld2Screen() {
	}

	// return a MMDefaultR3Vector here?
	public NumberTypeDependentIF getMathObjectFromScreen(AffinePoint screenPoint) {
		throw new TodoException();
	}

	/**
	 *  The coordinates need not be transformed, since math coordinates are
	 *  identical to world coordinates (euclidean vector space) and
	 *  world2screen conversion is done implicitly in
	 *  {@link net.mumie.mathletfactory.display.j3d.MMJ3DCanvas#renderScene} by
	 *  transforming the view platform.
	 */
	public void synchronizeMath2Screen() {
	}

	// what kind of MathEntity should be the argument?
	public AffinePoint getScreenPointFromMath(NumberTypeDependentIF entity) {
		throw new TodoException();
	}

	/**
	 *  Constructs the coordinate axes and calls
	 *  {@link net.mumie.mathletfactory.display.j3d.J3DDrawable#render}.
	 *  Called by the associated {@link net.mumie.mathletfactory.display.j3d.MMJ3DCanvas}.
	 *
	 */
	public void render() {
		if (renderedFirstTime) {
			((J3DCoordinateSystemDrawable) m_activeDrawable).updateFinished();
			renderedFirstTime = false;
		}
		super.render();
	}

	public MMDefaultR3 getRealMaster() {
		return (MMDefaultR3) m_masterMMObject;
	}

	/**
	 * Empty implementation, since a vector space must always be located at origin.
	 * @see net.mumie.mathletfactory.transformer.CanvasObjectTransformer#getMathObjectFromScreen(double[], NumberTypeDependentIF)
	 */
	public void getMathObjectFromScreen(
		double[] javaScreenCoordinates,
		NumberTypeDependentIF mathObject) {
	}

	/**
	 * Empty implementation, since a vector space must always be located at origin.
	 * @see net.mumie.mathletfactory.transformer.CanvasObjectTransformer#getMathObjectFromScreen(double[], NumberTypeDependentIF)
	 */
	public void getScreenPointFromMath(
		NumberTypeDependentIF entity,
		double[] javaScreenCoordinates) {
	}
}
