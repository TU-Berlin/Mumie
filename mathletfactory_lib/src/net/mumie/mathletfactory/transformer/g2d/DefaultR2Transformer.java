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

import java.awt.Color;
import java.awt.Graphics2D;

import net.mumie.mathletfactory.action.message.TodoException;
import net.mumie.mathletfactory.display.CanvasDrawable;
import net.mumie.mathletfactory.display.g2d.G2DPointDrawable;
import net.mumie.mathletfactory.display.g2d.MMG2DCanvas;
import net.mumie.mathletfactory.math.geom.affine.AffinePoint;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.NumberTypeDependentIF;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR2;
import net.mumie.mathletfactory.transformer.Canvas2DObjectTransformer;

/**
 * Transformer for
 * {@link net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR2}.
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */
public class DefaultR2Transformer
	extends Canvas2DObjectTransformer {

	private final double[] m_originCoordsInWorld = new double[] { 0., 0. };
	private final double[] m_originCoordsOnScreen = new double[2];

	private final double[] m_01InWorld = new double[2];
	private final double[] m_01OnScreen = new double[2];

	private final double[] m_10InWorld = new double[2];
	private final double[] m_10OnScreen = new double[2];

//	/**
//	 * This is the world draw point of the vector (m_MathUnit, 0);
//	 */
//	private final Affine2DPoint m_MathUnitZero =
//		new Affine2DPoint(MDouble.class);
//	private final Affine2DPoint m_U0Screen = new Affine2DPoint(MDouble.class);

//	/**
//	 * This is the world draw point of the vector (0, MathUnit):
//	 */
//	private final Affine2DPoint m_ZeroMathUnit =
//		new Affine2DPoint(MDouble.class);
//	private final Affine2DPoint m_0UScreen = new Affine2DPoint(MDouble.class);

//	/**
//	 * This value is only (temporarily) stored in the transformer. The actual value
//	 * is always retrieved from the {@link net.mumie.mathletfactory.mmobject.MMDefaultR2}
//	 * (see {@link net.mumie.mathletfactory.mmobject.MMDefaultR2#getGridInMath}).
//	 */
//	private double m_currentUnitInMath = 0;

	private final MDouble m_unit;
//	private final MDouble m_zero = new MDouble();

	public DefaultR2Transformer() {
		m_allDrawables = new CanvasDrawable[] { new G2DPointDrawable()};
		m_activeDrawable = m_allDrawables[0];
		m_unit = new MDouble();
	}

	public void synchronizeWorld2Screen() {
		world2Screen(m_originCoordsInWorld, m_originCoordsOnScreen);
		((G2DPointDrawable) m_activeDrawable).setPoint(
			m_originCoordsOnScreen[0],
			m_originCoordsOnScreen[1]);
	}

	public void synchronizeMath2Screen() {
		// we do always only render the "origin" of the vector space:
		synchronizeWorld2Screen();
	}

	// the following should be the methods to be used in future:
	public void getMathObjectFromScreen(
		double[] javaScreenCoordinates,
		NumberTypeDependentIF mathObject) {
		throw new TodoException();
	}

	public void getScreenPointFromMath(
		NumberTypeDependentIF entity,
		double[] javaScreenCoordinates) {
		throw new TodoException();
	}

	/**
	 *
	 */
	public void draw() {
		if (getRealMaster().getGridInMath() > 0) {
			if (getRealMaster().getGridInMath() != m_unit.getDouble()) {
				m_unit.setDouble(getRealMaster().getGridInMath());
				updateUnitPoints();
			}
			if (getRealMaster().displayGrid())
				drawGrid();
		}
		super.draw();
	}

	private MMDefaultR2 getRealMaster() {
		return (MMDefaultR2) m_masterMMObject;
	}

	// necessary because the unit may change...
	private void updateUnitPoints() {
		m_01InWorld[0] = 0;
		m_01InWorld[1] = getRealMaster().getGridInMath();
		m_10InWorld[0] = getRealMaster().getGridInMath();
		m_10InWorld[1] = 0;
	}

	// should be replaced by a standalone Drawable!
	private void drawGrid() {
		// origin screen coordinates are supposed to be at correct position!
		// So, first calculate the pixel coordinates of the origin and the two points
		// (unit,0) and (0,unit):
		world2Screen(m_01InWorld, m_01OnScreen);
		world2Screen(m_10InWorld, m_10OnScreen);
		double[] origin =
			new double[] { m_originCoordsOnScreen[0], m_originCoordsOnScreen[1] };
		double[] dirX =
			new double[] {
				m_10OnScreen[0] - m_originCoordsOnScreen[0],
				m_10OnScreen[1] - m_originCoordsOnScreen[1] };
		double[] dirY =
			new double[] {
				m_01OnScreen[0] - m_originCoordsOnScreen[0],
				m_01OnScreen[1] - m_originCoordsOnScreen[1] };

		// works only for MMG2DCanvas, of course:
		Graphics2D gr =
			((MMG2DCanvas) getMasterAsCanvasObject().getCanvas())
				.getGraphics2D();
		gr.setColor(Color.gray);
		double xMax = getMasterAsCanvasObject().getCanvas().getWidth();
		double yMax = getMasterAsCanvasObject().getCanvas().getHeight();
		double xMin = 0.0;
		double yMin = 0.0;
		double tanAlpha, tmp, deltaX, deltaY;
		double lambda1, lambda2;
		int k;

		if (Math.abs(dirY[1]) < 0.011) {
			tmp = dirY[1];
			dirY[1] = dirY[0];
			dirY[0] = tmp;
			tmp = dirX[0];
			dirX[0] = dirX[1];
			dirX[1] = tmp;
		}
		tanAlpha = dirY[0] / dirY[1];

		if (origin[0] <= 60.0) {
			xMax = (xMax + (60.0 - origin[0]));
			xMin = origin[0] - 60.0;
		}
		else {
			xMin = 60.0 - origin[0];
			xMax = xMax + (origin[0] - 60.0);
		}
		if (origin[1] <= 60.0) {
			yMax = yMax + (60.0 - origin[1]);
			yMin = origin[1] - 60.0;
		}
		else {
			yMin = 60.0 - origin[1];
			yMax = yMax + (origin[1] - 60.0);
		}
		deltaY = yMax - yMin;
		deltaX = xMax - xMin;

		yMax = yMax + (Math.sin(Math.abs(Math.atan(tanAlpha))) * deltaX);
		yMin = yMin - (Math.sin(Math.abs(Math.atan(tanAlpha))) * deltaX);
		xMax = xMax + (Math.sin(Math.abs(Math.atan(tanAlpha))) * deltaY);
		xMin = xMin - (Math.sin(Math.abs(Math.atan(tanAlpha))) * deltaY);

		int i = 0;
		while (origin[1] + i * dirY[1] < yMax
			&& origin[1] + i * dirY[1] > yMin
			&& origin[0] + i * dirY[0] < xMax
			&& origin[0] + i * dirY[0] > xMin) {
			lambda1 = (origin[0] + i * dirY[0] - xMin) * tanAlpha;
			lambda2 = (xMax - origin[0] - i * dirY[0]) * tanAlpha;
			gr.drawLine(
				(int) xMin,
				(int) (origin[1] + i * dirY[1] + lambda1),
				(int) xMax,
				(int) (origin[1] + i * dirY[1] - lambda2));
			if (i == 0) {
				for (k = 1;
					k <= 1 + 4 * Math.pow(Math.sin(Math.abs(Math.atan(tanAlpha))), 8);
					k++) {
					gr.drawLine(
						(int) xMin,
						(int) (origin[1] + k + lambda1),
						(int) xMax,
						(int) (origin[1] + k - lambda2));
					gr.drawLine(
						(int) xMin,
						(int) (origin[1] - k + lambda1),
						(int) xMax,
						(int) (origin[1] - k - lambda2));
				}
			}
			i++;
		}

		i = -1;
		while (origin[1] + i * dirY[1] < yMax
			&& origin[1] + i * dirY[1] > yMin
			&& origin[0] + i * dirY[0] < xMax
			&& origin[0] + i * dirY[0] > xMin) {
			lambda1 = (origin[0] + i * dirY[0] - xMin) * tanAlpha;
			lambda2 = (xMax - origin[0] - i * dirY[0]) * tanAlpha;
			gr.drawLine(
				(int) xMin,
				(int) (origin[1] + i * dirY[1] + lambda1),
				(int) xMax,
				(int) (origin[1] + i * dirY[1] - lambda2));
			i--;
		}

		tanAlpha = dirX[1] / dirX[0];
		i = 0;

		while (origin[1] + i * dirX[1] < yMax
			&& origin[1] + i * dirX[1] > yMin
			&& origin[0] + i * dirX[0] < xMax
			&& origin[0] + i * dirX[0] > xMin) {
			lambda1 = (yMax - (origin[1] + i * dirX[1])) * tanAlpha;
			lambda2 = (origin[1] + i * dirX[1] - yMin) * tanAlpha;
			gr.drawLine(
				(int) (origin[0] + i * dirX[0] - lambda1),
				(int) yMax,
				(int) (origin[0] + i * dirX[0] + lambda2),
				(int) yMin);
			if (i == 0) {
				for (k = 1;
					k <= 1 + 4 * Math.pow(Math.sin(Math.abs(Math.atan(tanAlpha))), 8);
					k++) {
					gr.drawLine(
						(int) (origin[0] + k - lambda1),
						(int) yMax,
						(int) (origin[0] + k + lambda2),
						(int) yMin);
					gr.drawLine(
						(int) (origin[0] - k - lambda1),
						(int) yMax,
						(int) (origin[0] - k + lambda2),
						(int) yMin);
				}
			}
			i++;
		}

		i = -1;
		while (origin[1] + i * dirX[1] < yMax
			&& origin[1] + i * dirX[1] > yMin
			&& origin[0] + i * dirX[0] < xMax
			&& origin[0] + i * dirX[0] > xMin) {
			lambda1 = (yMax - (origin[1] + i * dirX[1])) * tanAlpha;
			lambda2 = (origin[1] + i * dirX[1] - yMin) * tanAlpha;
			gr.drawLine(
				(int) (origin[0] + i * dirX[0] - lambda1),
				(int) yMax,
				(int) (origin[0] + i * dirX[0] + lambda2),
				(int) yMin);
			i--;
		}
	}

	// will all be omitted in future:
	public NumberTypeDependentIF getMathObjectFromScreen(AffinePoint screenPoint) {
		return null;
	}
	// will be left in abstract CanvasObjectTransformer in future:
	public AffinePoint getScreenPointFromMath(NumberTypeDependentIF entity) {
		return null;
	}
	public void getMathObjectFromScreen(
		AffinePoint screenPoint,
		NumberTypeDependentIF mathObject) {
	}

}
