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

package net.mumie.mathletfactory.action.updater;

import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.geom.affine.Affine2DPoint;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR2Endomorphism;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPolygon;

/**
 * This updater ensures the updating of a polygon that is the image of a linear map applied onto
 * another polygon.
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */
public class PolygonMappedLinearUpdater extends MMUpdater {

	private MMAffine2DPolygon m_dependentPolygon;
	private MMDefaultR2Endomorphism m_linearMap;
	private MMAffine2DPolygon m_parentPolygon;

	public PolygonMappedLinearUpdater(
		MMAffine2DPolygon dependentPolygon,
		MMDefaultR2Endomorphism linearMap,
		MMAffine2DPolygon parentPolygon) {
		super(dependentPolygon, new MMObjectIF[] { linearMap, parentPolygon });
		m_dependentPolygon = dependentPolygon;
		m_linearMap = linearMap;
		m_parentPolygon = parentPolygon;
		/* to ensure that the child is directly adjusted after creating this updater*/
		startUpdateCycleFromHere();
	}

	public void userDefinedUpdate() {
		if (m_linearMap.isMap()) {
			m_dependentPolygon.setVisible(true);
			NumberMatrix m = m_linearMap.getDefaultMatrixRepresentationRef();
			for (int i = 0; i < m_dependentPolygon.getVerticesCount(); i++) {
				Affine2DPoint p = m_dependentPolygon.getVertexRef(i);
				Affine2DPoint q = m_parentPolygon.getVertexRef(i);
				NumberTuple t = q.getProjectiveCoordinatesOfPoint();
				NumberTuple t_mapped = m.applyTo(t);
				p.setProjectiveCoordinates(t_mapped);
			}
		}
		else {
			m_dependentPolygon.setVisible(false);
		}
	}

}
