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
import net.mumie.mathletfactory.display.j3d.J3DLineSegmentDrawable;
import net.mumie.mathletfactory.display.j3d.J3DPointDrawable;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine3DLineSegment;

/**
 * Transformer for
 * {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine3DLineSegment}.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public class Affine3DLineSegmentTransformer
	extends Affine3DDefaultTransformer {

	private double[] m_initInWorld = new double[3];
	private double[] m_endInWorld = new double[3];
  private double[] m_centerOfGravity = new double[3];

	public Affine3DLineSegmentTransformer() {
		m_allDrawables =
			new CanvasDrawable[] {
				new J3DPointDrawable(),
				new J3DLineSegmentDrawable()};
	}
  
	public void synchronizeMath2Screen() {
    if (getRealMasterObject().isDegenerated()) {
			m_activeDrawable = m_allDrawables[0];
			math2World(getRealMasterObject().getInitialPoint().getXYZ(), m_initInWorld);
      ((J3DPointDrawable) m_allDrawables[0]).setPoint(m_initInWorld);
		}
		else {
			m_activeDrawable = m_allDrawables[1];
			math2World(getRealMasterObject().getInitialPoint().getXYZ(), m_initInWorld);
			math2World(getRealMasterObject().getEndPoint().getXYZ(), m_endInWorld);
      ((J3DLineSegmentDrawable) m_allDrawables[1]).setPoints(m_initInWorld, m_endInWorld);
		}
    // calculate the center of gravity
    m_centerOfGravity[0] = (m_initInWorld[0] + m_endInWorld[0])/2;
    m_centerOfGravity[1] = (m_initInWorld[1] + m_endInWorld[1])/2;
    m_centerOfGravity[2] = (m_initInWorld[2] + m_endInWorld[2])/2;
	}

  public void render() {
    renderLabel(m_centerOfGravity);
    super.render();
  }

  public double[] getWorldPickPointFromMaster() {
    return m_centerOfGravity;
  }

	private MMAffine3DLineSegment getRealMasterObject() {
		return (MMAffine3DLineSegment) m_masterMMObject;
	}
}
