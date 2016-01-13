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

import net.mumie.mathletfactory.display.CanvasDrawable;
import net.mumie.mathletfactory.display.MMCanvas;
import net.mumie.mathletfactory.display.g2d.G2DLineDrawable;
import net.mumie.mathletfactory.math.util.Graphics2DHelper;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DRay;
import net.mumie.mathletfactory.transformer.Affine2DDefaultTransformer;

/**
 * Transformer for
 * {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DRay}.
 * 
 * @author Mrose
 * @mm.docstatus finished
 */
public class Affine2DRayTransformer
	extends Affine2DDefaultTransformer {

	private double[] m_initInWorld = new double[2];
	private double[] m_initOnScreen = new double[2];
	private double[] m_rayRunsThroughInWorld = new double[2];
	private double[] m_rayRunsThroughOnScreen = new double[2];

	public Affine2DRayTransformer() {
		m_allDrawables = new CanvasDrawable[] { new G2DLineDrawable()};
	}

	public void synchronizeMath2Screen() {
		m_activeDrawable = m_allDrawables[0];
		math2World(getRealMasterObject().getInitialPoint(), m_initInWorld);
		math2World(
			getRealMasterObject().getRayRunsThrough(),
			m_rayRunsThroughInWorld);
		world2Screen(m_initInWorld, m_initOnScreen);
		world2Screen(m_rayRunsThroughInWorld, m_rayRunsThroughOnScreen);
		getArrowDrawable().setPoints(
			m_initOnScreen[0],
			m_initOnScreen[1],
			m_rayRunsThroughOnScreen[0],
			m_rayRunsThroughOnScreen[1]);
		synchronizeWorld2Screen();
	}

	public void synchronizeWorld2Screen() {
		world2Screen(m_initInWorld, m_initOnScreen);
		world2Screen(m_rayRunsThroughInWorld, m_rayRunsThroughOnScreen);
		double[] coord = new double[4];
		MMCanvas canvas = getMasterAsCanvasObject().getCanvas();
		double[] rect = new double[4];
		if (m_initInWorld[0] <= m_rayRunsThroughInWorld[0]
			&& m_initInWorld[1] <= m_rayRunsThroughInWorld[1]) // erster Quadrant
			rect =
				new double[] {
					m_initOnScreen[0],
					canvas.getBounds().getY(),
					canvas.getWidth(),
					Math.abs(canvas.getY() - m_initOnScreen[1])};
		else if (
			m_initInWorld[0] > m_rayRunsThroughInWorld[0]
				&& m_initInWorld[1] < m_rayRunsThroughInWorld[1])
			// zweiter Quadrant
			rect =
				new double[] {
					canvas.getBounds().getX(),
					canvas.getBounds().getY(),
					Math.abs(canvas.getX() - m_initOnScreen[0]),
					Math.abs(canvas.getY() - m_initOnScreen[1])};
		else if (
			m_initInWorld[0] >= m_rayRunsThroughInWorld[0]
				&& m_initInWorld[1] >= m_rayRunsThroughInWorld[1])
			// dritter Quadrant
			rect =
				new double[] {
					canvas.getBounds().getX(),
					m_initOnScreen[1],
					Math.abs(canvas.getX() - m_initOnScreen[0]),
					canvas.getHeight()};
		else // vierter Quadrant
			rect =
				new double[] {
					m_initOnScreen[0],
					m_initOnScreen[1],
					canvas.getWidth(),
					canvas.getHeight()};
		int res =
			Graphics2DHelper.lineRectIntersection2D(
				new double[] {
					m_initOnScreen[0],
					m_initOnScreen[1],
					m_rayRunsThroughOnScreen[0],
					m_rayRunsThroughOnScreen[1] },
				rect,
				coord);
		getArrowDrawable().setPoints(coord[0], coord[1], coord[2], coord[3]);
	}

  public void draw() {
    super.draw();
    renderLabel((float) m_rayRunsThroughOnScreen[0], (float) m_rayRunsThroughOnScreen[1]); 
  }

	private MMAffine2DRay getRealMasterObject() {
		return (MMAffine2DRay) m_masterMMObject;
	}

	private G2DLineDrawable getArrowDrawable() {
		return (G2DLineDrawable) m_allDrawables[0];
	}
}
