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

import net.mumie.mathletfactory.display.CanvasDrawable;
import net.mumie.mathletfactory.display.j3d.J3DPointDrawable;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine3DPoint;

/**
 *  This is the transformer for an affine 3d point using the Java3D rendering system.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class Affine3DPointTransformer
	extends Affine3DDefaultTransformer {

	private final double[] m_worldPoint = new double[3];

	public Affine3DPointTransformer() {
		m_allDrawables = new CanvasDrawable[] { new J3DPointDrawable()};
		m_activeDrawable = m_allDrawables[0];
	}

	public double[] getWorldPickPointFromMaster() {
		return m_worldPoint;
	}

	public void synchronizeMath2Screen() {
		math2World(getRealMaster(), m_worldPoint);
		((J3DPointDrawable) m_activeDrawable).setPoint(getRealMaster());
	}

	private MMAffine3DPoint getRealMaster() {
		return (MMAffine3DPoint) m_masterMMObject;
	}

	public void render() {
    if(getRealMaster().getDimension() < 0)
      return;
		super.render();
		renderLabel(m_worldPoint);
	}

}
