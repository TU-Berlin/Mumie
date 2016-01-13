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
import net.mumie.mathletfactory.display.g2d.G2DPointDrawable;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPoint;
import net.mumie.mathletfactory.transformer.Affine2DDefaultTransformer;

/**
 * Transformer for
 * {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2Point}.
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */
public class Affine2DPointTransformer
	extends Affine2DDefaultTransformer {

	private final double[] m_worldDrawCoordinates = new double[2];
	private final double[] m_javaScreenCoordinates = new double[2];

	public Affine2DPointTransformer() {
		m_allDrawables = new CanvasDrawable[] { new G2DPointDrawable()};
		 // for the point the Drawable never
		// changes! Actual we do not treat the
		// situation when the point becomes
		// infinity.
		m_activeDrawable = m_allDrawables[0];
	}

	public void synchronizeMath2Screen() {
		math2World(((MMAffine2DPoint) m_masterMMObject), m_worldDrawCoordinates);
		synchronizeWorld2Screen();
	}

  public void draw() {
    super.draw();
    renderLabel((float) m_javaScreenCoordinates[0], (float) m_javaScreenCoordinates[1]); 
  }

	public void synchronizeWorld2Screen() {
		world2Screen(m_worldDrawCoordinates, m_javaScreenCoordinates);
		((G2DPointDrawable) m_activeDrawable).setPoint(
			m_javaScreenCoordinates[0],
			m_javaScreenCoordinates[1]);
	}

}
