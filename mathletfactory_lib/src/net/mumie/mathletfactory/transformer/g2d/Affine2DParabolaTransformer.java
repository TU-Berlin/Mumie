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
import net.mumie.mathletfactory.display.g2d.G2DParabolaDrawable;
import net.mumie.mathletfactory.display.g2d.G2DPointDrawable;
import net.mumie.mathletfactory.math.geom.affine.Affine2DPoint;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DParabola;
import net.mumie.mathletfactory.transformer.Affine2DDefaultTransformer;

/**
 * Transformer class for
 * {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DParabola}.
 * @author Liu
 * @mm.docstatus finished
 */

public class Affine2DParabolaTransformer
	extends Affine2DDefaultTransformer {

	private double[] m_apexInWorld = new double[2];
	private double[] m_focalInWorld = new double[2];

	private double[] m_apexOnScreen = new double[2];
	private double[] m_focalOnScreen = new double[2];

	private double m_radianInWorld;
	private double m_radianOnScreen;

	public Affine2DParabolaTransformer() {
		m_allDrawables =
			new CanvasDrawable[] {
				new G2DPointDrawable(),
				new G2DParabolaDrawable()};
	}
	/**
	 *  The coordinates need not be transformed, since math coordinates are
	 *  identical to world coordinates (euklidean vector space) and
	 *  world2screen conversion is done implicitly in
	 *  {@link net.mumie.mathletfactory.display.MMG2DCanvas} by
	 *  transforming the view platform.
	 */
	public void synchronizeMath2Screen() {
		m_activeDrawable = m_allDrawables[1];
//		m_apexInWorld = getRealMasterObject().getApex();
//		m_focalInWorld = getRealMasterObject().getFocalPoint();
		m_radianInWorld = getRealMasterObject().getRadian();
		m_radianOnScreen = m_radianInWorld;
       math2World(new Affine2DPoint(MDouble.class,
									getRealMasterObject().getApex()[0],
										getRealMasterObject().getApex()[1]),
				  m_apexInWorld);
		math2World(new Affine2DPoint(MDouble.class,
									getRealMasterObject().getFocalPoint()[0],
										getRealMasterObject().getFocalPoint()[1]),
				  m_focalInWorld);
		//math2World(getRealMasterObject().getRadian(),m_radianInWorld);
		world2Screen(m_apexInWorld, m_apexOnScreen);
		world2Screen(m_focalInWorld, m_focalOnScreen);
 //       world2Screen(new double[]{m_radianInWorld,m_radianInWorld}, new double[] {m_radianOnScreen,m_radianOnScreen});
		getParabolaDrawable().setPoints(m_apexOnScreen, m_focalOnScreen);
		getParabolaDrawable().setRadian(m_radianOnScreen);

	}

	public void synchronizeWorld2Screen() {
		m_radianOnScreen = m_radianInWorld;

		world2Screen(m_apexInWorld, m_apexOnScreen);
		world2Screen(m_focalInWorld, m_focalOnScreen);

		getParabolaDrawable().setPoints(m_apexOnScreen, m_focalOnScreen);
		getParabolaDrawable().setRadian(m_radianOnScreen);
	}
	private G2DParabolaDrawable getParabolaDrawable() {
		return (G2DParabolaDrawable) m_allDrawables[1];
	}

	private MMAffine2DParabola getRealMasterObject() {
		return (MMAffine2DParabola) m_masterMMObject;
	}

	//  private G2DPointDrawable getPointDrawable() {
	//      return (G2DPointDrawable)m_allDrawables[0];
	//    }

	public void render() {
		super.render();
	}
}
