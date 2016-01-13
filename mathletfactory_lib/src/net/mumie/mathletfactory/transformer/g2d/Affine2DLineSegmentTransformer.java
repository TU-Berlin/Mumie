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
import net.mumie.mathletfactory.display.g2d.G2DLineDrawable;
import net.mumie.mathletfactory.display.g2d.G2DPointDrawable;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DLineSegment;
import net.mumie.mathletfactory.transformer.Affine2DDefaultTransformer;

/**
 * Transformer for
 * {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DLineSegment}.
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */
public class Affine2DLineSegmentTransformer
	extends Affine2DDefaultTransformer {

	private double[] m_initInWorld = new double[2];
	private double[] m_initOnScreen = new double[2];
	private double[] m_endInWorld = new double[2];
	private double[] m_endOnScreen = new double[2];

	public Affine2DLineSegmentTransformer() {
		m_allDrawables =
			new CanvasDrawable[] {
				new G2DPointDrawable(),
				new G2DLineDrawable()};
	}

	public void synchronizeMath2Screen() {
		if (getRealMasterObject().isDegenerated()) {
			m_activeDrawable = m_allDrawables[0];
			math2World(getRealMasterObject().getInitialPoint(), m_initInWorld);
			world2Screen(m_initInWorld, m_initOnScreen);
			getPointDrawable().setPoint(m_initOnScreen[0], m_initOnScreen[1]);
		}
		else {
			m_activeDrawable = m_allDrawables[1];
			math2World(getRealMasterObject().getInitialPoint(), m_initInWorld);
			math2World(getRealMasterObject().getEndPoint(), m_endInWorld);
			world2Screen(m_initInWorld, m_initOnScreen);
			world2Screen(m_endInWorld, m_endOnScreen);
			getArrowDrawable().setPoints(
				m_initOnScreen[0],
				m_initOnScreen[1],
				m_endOnScreen[0],
				m_endOnScreen[1]);
		}
	}

	public void synchronizeWorld2Screen() {
		if (getRealMasterObject().isDegenerated()) {
			world2Screen(m_initInWorld, m_initOnScreen);
			getPointDrawable().setPoint(m_initOnScreen[0], m_initOnScreen[1]);
		}
		else {
			world2Screen(m_initInWorld, m_initOnScreen);
			world2Screen(m_endInWorld, m_endOnScreen);
			getArrowDrawable().setPoints(
				m_initOnScreen[0],
				m_initOnScreen[1],
				m_endOnScreen[0],
				m_endOnScreen[1]);
		}
	}

  public void draw() {
    super.draw();
    renderLabel((float) (m_initOnScreen[0] + (m_endOnScreen[0] - m_initOnScreen[0])/2), 
                (float) (m_initOnScreen[1] + (m_endOnScreen[1] - m_initOnScreen[1])/2));
  }

	private MMAffine2DLineSegment getRealMasterObject() {
		return (MMAffine2DLineSegment) m_masterMMObject;
	}

	private G2DPointDrawable getPointDrawable() {
		return (G2DPointDrawable) m_allDrawables[0];
	}

	private G2DLineDrawable getArrowDrawable() {
		return (G2DLineDrawable) m_allDrawables[1];
	}

}
