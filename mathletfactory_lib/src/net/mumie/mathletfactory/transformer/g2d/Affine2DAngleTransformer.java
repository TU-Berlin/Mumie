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
import net.mumie.mathletfactory.display.g2d.G2DAngleDrawable;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DAngle;
import net.mumie.mathletfactory.transformer.Affine2DDefaultTransformer;
/**
 * Transformer for {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DAngle}.
 * 
 * @author Mrose
 * @mm.docstatus finished
 */
public class Affine2DAngleTransformer
	extends Affine2DDefaultTransformer {
	private double[] m_vertexOnScreen = new double[2];
	private double[] m_vertexInWorld = new double[2];
	private double[] m_firstPointOnScreen = new double[2];
	private double[] m_firstPointInWorld = new double[2];
	private double[] m_secondPointOnScreen = new double[2];
	private double[] m_secondPointInWorld = new double[2];

	public Affine2DAngleTransformer() {
		m_allDrawables = new CanvasDrawable[] { new G2DAngleDrawable()};
		m_activeDrawable = m_allDrawables[0];
	}

	public void synchronizeMath2Screen() {
		math2World(getRealMasterObject().getFirstPoint(), m_firstPointInWorld);
		math2World(
			getRealMasterObject().getSecondPoint(),
			m_secondPointInWorld);
		math2World(getRealMasterObject().getVertex(), m_vertexInWorld);
		synchronizeWorld2Screen();
	}

	public void synchronizeWorld2Screen() {
		world2Screen(m_vertexInWorld, m_vertexOnScreen);
		world2Screen(m_firstPointInWorld, m_firstPointOnScreen);
		world2Screen(m_secondPointInWorld, m_secondPointOnScreen);
		((G2DAngleDrawable) m_activeDrawable).setPoints(
			getUpperLeftCorner(
				m_vertexOnScreen,
				m_firstPointOnScreen,
				m_secondPointOnScreen,
				getRealMasterObject().getFactor()),
			getWidthAndHeight(
				m_vertexOnScreen,
				m_firstPointOnScreen,
				m_secondPointOnScreen,
				getRealMasterObject().getFactor()),
			getRealMasterObject().getStartingAngle(),
			getRealMasterObject().getAngularExtent(),
			getRealMasterObject().getClosure());
	}

	private MMAffine2DAngle getRealMasterObject() {
		return (MMAffine2DAngle) m_masterMMObject;
	}

	private double[] getUpperLeftCorner(
		double[] vertex,
		double[] firstPoint,
		double[] secondPoint,
		double factor) {
		double r1 =
			Math.sqrt(
				(firstPoint[0] - vertex[0]) * (firstPoint[0] - vertex[0])
					+ (firstPoint[1] - vertex[1]) * (firstPoint[1] - vertex[1]));
		double r2 =
			Math.sqrt(
				(secondPoint[0] - vertex[0]) * (secondPoint[0] - vertex[0])
					+ (secondPoint[1] - vertex[1])
						* (secondPoint[1] - vertex[1]));
		double length = factor * Math.min(r1, r2);
		return new double[] { -length + vertex[0], -length + vertex[1] };
		//WARUM???
	}

	private double[] getWidthAndHeight(
		double[] vertex,
		double[] firstPoint,
		double[] secondPoint,
		double factor) {
		double r1 =
			Math.sqrt(
				(firstPoint[0] - vertex[0]) * (firstPoint[0] - vertex[0])
					+ (firstPoint[1] - vertex[1]) * (firstPoint[1] - vertex[1]));
		double r2 =
			Math.sqrt(
				(secondPoint[0] - vertex[0]) * (secondPoint[0] - vertex[0])
					+ (secondPoint[1] - vertex[1])
						* (secondPoint[1] - vertex[1]));
		double length = factor * Math.min(r1, r2);
		return new double[] { 2 * length, 2 * length };
	}
	
  public void draw() {
    super.draw();
    renderLabel((float)m_vertexOnScreen[0],(float)m_vertexOnScreen[1]-20);
  }
}
