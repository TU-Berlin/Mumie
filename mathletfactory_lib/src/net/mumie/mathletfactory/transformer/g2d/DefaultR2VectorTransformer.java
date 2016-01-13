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

package net.mumie.mathletfactory.transformer.g2d;

import java.util.ArrayList;

import net.mumie.mathletfactory.display.CanvasDrawable;
import net.mumie.mathletfactory.display.g2d.G2DLineDrawable;
import net.mumie.mathletfactory.display.g2d.G2DPointDrawable;
import net.mumie.mathletfactory.math.algebra.linalg.NumberVector;
import net.mumie.mathletfactory.math.geom.affine.AffinePoint;
import net.mumie.mathletfactory.math.number.NumberTypeDependentIF;
import net.mumie.mathletfactory.math.util.Affine3DDouble;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR2Vector;
import net.mumie.mathletfactory.transformer.Canvas2DObjectTransformer;

/**
 * This class performs the complete display of the abstract mathematical object
 * {@link net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR2Vector} to the screen.
 * Any of such vectors is displayed as an arrow from the origin (i.e. the point
 * 0,0 in math) to the point (x,y) where x,y represent the default coordinates
 * of the vector due to its vector space (see
 * {@link net.mumie.mathletfactory.algebra.NumberVector#getDefaultCoordinates} for
 * more information).
 *
 * More precicsely: If (x,y) are the default coordinates of the
 * {@link net.mumie.mathletfactory.algebra.NumberVector} we have as representation
 * in the &quot;worldDraw&quot; two {@link net.mumie.mathletfactory.geom.affine.Affine2DPoint}s,
 * the initial point having <b>always</b> coordinates (0,0) and the end point
 * having always coordinates (x,y).
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */

public class DefaultR2VectorTransformer extends Canvas2DObjectTransformer {

	private final double[] m_originCoordsInWorld = new double[] { 0., 0.};

	private final double[] m_endCoordsInWorld = new double[2];

	private final double[] m_originCoordsOnScreen = new double[2];

	private final double[] m_endCoordsOnScreen = new double[2];

	private final double[] m_tmp = new double[2];

	/**
	 * Creates the drawables: point (for a degenerated vector) and line (for a
	 * regular vector).
	 */
	public DefaultR2VectorTransformer() {
		m_allDrawables = new CanvasDrawable[] { new G2DPointDrawable(),
				new G2DLineDrawable()};
	}

	public void synchronizeWorld2Screen() {
		// test if master object is the zero vector:
		if (getRealMasterObject().getDefaultCoordinatesRef().isZero()) {
			m_activeDrawable = m_allDrawables[0];
			world2Screen(m_originCoordsInWorld, m_originCoordsOnScreen);
			world2Screen(m_endCoordsInWorld, m_endCoordsOnScreen);
			getPointDrawable().setPoint(m_originCoordsOnScreen[0],
					m_originCoordsOnScreen[1]);
		} else {
			m_activeDrawable = m_allDrawables[1];
			world2Screen(m_originCoordsInWorld, m_originCoordsOnScreen);
			world2Screen(m_endCoordsInWorld, m_endCoordsOnScreen);
			getArrowDrawable().setPoints(m_originCoordsOnScreen[0],
					m_originCoordsOnScreen[1], m_endCoordsOnScreen[0],
					m_endCoordsOnScreen[1]);
		}
	}

	public void synchronizeMath2Screen() {
		// it is possible to use the getDefaultCoordinatesRef() method, because
		// math2World() will not change them!
		m_originCoordsInWorld[0] = getRealMasterObject().getRootingPoint()
				.getEntry(1).getDouble();
		m_originCoordsInWorld[1] = getRealMasterObject().getRootingPoint()
				.getEntry(2).getDouble();
		m_endCoordsInWorld[0] = getRealMasterObject().getDefaultCoordinateRef(1)
				.getDouble();
		m_endCoordsInWorld[1] = getRealMasterObject().getDefaultCoordinateRef(2)
				.getDouble();
		Affine3DDouble.add(m_endCoordsInWorld, m_originCoordsInWorld);

		// used for displaying a smaller vector above a larger vector
		ArrayList objects = getCanvas().getObjects();
		double masterNorm = getRealMasterObject().standardNorm().getDouble();
		for (int i = 0; i < objects.size(); i++) {
			if (!(objects.get(i) instanceof MMDefaultR2Vector)) continue;
			MMDefaultR2Vector v = (MMDefaultR2Vector) objects.get(i);
			if (v == getRealMasterObject()
					|| !v.getRootingPoint()
							.equals(getRealMasterObject().getRootingPoint())) {
				continue;
			}
			int k = objects.indexOf(getRealMasterObject());
			double vNorm = v.standardNorm().getDouble();
			if ((i < k && vNorm < masterNorm) || (i > k && vNorm > masterNorm)) {
				objects.set(k, objects.set(i, getRealMasterObject()));
				getCanvas().setRestartRenderCycle(true);
			}
		}
		synchronizeWorld2Screen();
	}

	public void draw() {
		super.draw();
		renderLabel((float) m_endCoordsOnScreen[0], (float) m_endCoordsOnScreen[1]);
	}

	// the following should be the methods to be used in future:
	public void getMathObjectFromScreen(double[] javaScreenCoordinates,
			NumberTypeDependentIF aNumberVector) {
		screen2World(javaScreenCoordinates, m_tmp);
		Affine3DDouble.sub(m_tmp, m_originCoordsInWorld);
		((NumberVector) aNumberVector).getDefaultCoordinateRef(1).setDouble(
				m_tmp[0]);
		((NumberVector) aNumberVector).getDefaultCoordinateRef(2).setDouble(
				m_tmp[1]);
	}

	public void getScreenPointFromMath(NumberTypeDependentIF aNumberVector,
			double[] javaScreenCoordinates) {
		m_tmp[0] = ((NumberVector) aNumberVector).getDefaultCoordinateRef(1)
				.getDouble();
		m_tmp[1] = ((NumberVector) aNumberVector).getDefaultCoordinateRef(2)
				.getDouble();
	}

	private G2DLineDrawable getArrowDrawable() {
		return (G2DLineDrawable) m_allDrawables[1];
	}

	private G2DPointDrawable getPointDrawable() {
		return (G2DPointDrawable) m_allDrawables[0];
	}

	/**
	 * returns the master object (see
	 * {@link net.mumie.mathletfactory.transformer.CanvasObjectTransformer#m_masterMMObject})
	 * explicitly casted to
	 * {@link net.mumie.mathletfactory.mmobject.MMDefaultR2Vector}.
	 */
	private MMDefaultR2Vector getRealMasterObject() {
		return (MMDefaultR2Vector) m_masterMMObject;
	}

	// methods will be deleted in future!!!
	public NumberTypeDependentIF getMathObjectFromScreen(AffinePoint screenPoint) {
		return null;
	}

	public void getMathObjectFromScreen(AffinePoint screenPoint,
			NumberTypeDependentIF mathObject) {
	}

	public AffinePoint getScreenPointFromMath(NumberTypeDependentIF entity) {
		return null;
	}
}