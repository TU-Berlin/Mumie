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
import net.mumie.mathletfactory.display.g2d.G2DPointDrawable;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.util.Graphics2DHelper;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DLine;
import net.mumie.mathletfactory.transformer.Affine2DDefaultTransformer;

/**
 * Transformer for
 * {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DLine}.
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */
public class Affine2DLineTransformer
	extends Affine2DDefaultTransformer {

	private double[] m_initInWorld = new double[2];
	private double[] m_initOnScreen = new double[2];
	private double[] m_endInWorld = new double[2];
	private double[] m_endOnScreen = new double[2];

	public Affine2DLineTransformer() {
		m_allDrawables =
			new CanvasDrawable[] {
				new G2DPointDrawable(),
				new G2DLineDrawable()};
	}

	private void math2World(NumberTuple tuple, double[] worldPoints) {
		worldPoints[0] = tuple.getEntryRef(1).getDouble();
		worldPoints[1] = tuple.getEntryRef(2).getDouble();
	}

	public void synchronizeMath2Screen() {
		if (getRealMasterObject().isDegenerated()) {
			m_activeDrawable = m_allDrawables[0];
			math2World(
				getRealMasterObject().getAffineCoordinatesOfInitialPoint(),
				m_initInWorld);
		}
		else {
			m_activeDrawable = m_allDrawables[1];
			math2World(
				getRealMasterObject().getAffineCoordinatesOfInitialPoint(),
				m_initInWorld);
			math2World(
				getRealMasterObject().getAffineCoordinatesOfEndPoint(),
				m_endInWorld);
		}
		synchronizeWorld2Screen();
	}

	public void synchronizeWorld2Screen() {
		if (getRealMasterObject().isDegenerated()) {
			world2Screen(m_initInWorld, m_initOnScreen);
			getPointDrawable().setPoint(m_initOnScreen[0], m_initOnScreen[1]);
		}
		else {
			world2Screen(m_initInWorld, m_initOnScreen);
			world2Screen(m_endInWorld, m_endOnScreen);
			double[] coord = new double[4];
			MMCanvas canvas = getMasterAsCanvasObject().getCanvas();
			int res =
				Graphics2DHelper.lineRectIntersection2D(
					new double[] {
						m_initOnScreen[0],
						m_initOnScreen[1],
						m_endOnScreen[0],
						m_endOnScreen[1] },
					new double[] {0.0, 0.0, canvas.getWidth(), canvas.getHeight()},
					coord);
			switch (res) {
				case 0 :
					getArrowDrawable().setPoints(0, 0, 0, 0);
					break;
				case 1 :
					getArrowDrawable().setPoints(coord[0], coord[1], coord[0], coord[1]);
					break;
				case 2 :
					getArrowDrawable().setPoints(coord[0], coord[1], coord[2], coord[3]);
					break;
			}
		}
	}

  public void draw() {
    super.draw();
    renderLabel((float) (m_initOnScreen[0] + (m_endOnScreen[0] - m_initOnScreen[0])/2), 
                (float) (m_initOnScreen[1] + (m_endOnScreen[1] - m_initOnScreen[1])/2));
  }

	private MMAffine2DLine getRealMasterObject() {
		return (MMAffine2DLine) m_masterMMObject;
	}

	private G2DPointDrawable getPointDrawable() {
		return (G2DPointDrawable) m_allDrawables[0];
	}

	private G2DLineDrawable getArrowDrawable() {
		return (G2DLineDrawable) m_allDrawables[1];
	}
}
