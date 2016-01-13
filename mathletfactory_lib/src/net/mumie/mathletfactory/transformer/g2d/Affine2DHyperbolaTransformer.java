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
import net.mumie.mathletfactory.display.g2d.G2DHyperbolaDrawable;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DHyperbola;
import net.mumie.mathletfactory.transformer.Affine2DDefaultTransformer;

/**
 * Transformer for
 * {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DHyperbola}.
 * 
 * @author Schimanowski
 * @mm.docstatus finished
 */
public class Affine2DHyperbolaTransformer
	extends Affine2DDefaultTransformer {

	private double[] m_centerInWorld = new double[2];
	private double[] m_upperLeftPointInWorld = new double[2];

	private double[] m_centerOnScreen = new double[2];
	private double[] m_upperLeftPointOnScreen = new double[2];

	private double m_radianInWorld;
	private double m_radianOnScreen;

	public Affine2DHyperbolaTransformer() {
		m_allDrawables = new CanvasDrawable[] { new G2DHyperbolaDrawable()};

	}

	public void synchronizeMath2Screen() {
		//    if( getRealMasterObject().isPoint() ) {
		//      m_activeDrawable = m_allDrawables[0];
		//      m_centerInWorld = getRealMasterObject().getCenter();
		//      world2Screen(m_centerInWorld, m_centerOnScreen);
		//      getPointDrawable().setPoint(m_centerOnScreen[0], m_centerOnScreen[1] );
		//    }
		//    else {
		m_activeDrawable = m_allDrawables[0];
		m_centerInWorld = getRealMasterObject().getCenter();
		m_upperLeftPointInWorld =
			getRealMasterObject().getUpperLeftPointOfBoundingBox();
		m_radianInWorld = getRealMasterObject().getRadian();
		m_radianOnScreen = m_radianInWorld;
		world2Screen(m_centerInWorld, m_centerOnScreen);
		world2Screen(m_upperLeftPointInWorld, m_upperLeftPointOnScreen);

		getHyperbolaDrawable().setPoints(
			m_centerOnScreen,
			m_upperLeftPointOnScreen);
		getHyperbolaDrawable().setRadian(m_radianOnScreen);
		//    }
	}

	public void synchronizeWorld2Screen() {
		//    if( m_activeDrawable.equals(m_allDrawables[0]) ) {
		//      world2Screen(m_centerInWorld, m_centerOnScreen);
		//      getPointDrawable().setPoint(m_centerOnScreen[0], m_centerOnScreen[1]);
		//    }
		//    else {
		m_radianOnScreen = m_radianInWorld;
		world2Screen(m_centerInWorld, m_centerOnScreen);
		world2Screen(m_upperLeftPointInWorld, m_upperLeftPointOnScreen);
		getHyperbolaDrawable().setPoints(
			m_centerOnScreen,
			m_upperLeftPointOnScreen);
		getHyperbolaDrawable().setRadian(m_radianOnScreen);
		//    }

	}

	private MMAffine2DHyperbola getRealMasterObject() {
		return (MMAffine2DHyperbola) m_masterMMObject;
	}

	private G2DHyperbolaDrawable getHyperbolaDrawable() {
		return (G2DHyperbolaDrawable) m_allDrawables[0];
	}

}
