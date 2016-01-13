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

import net.mumie.mathletfactory.action.ActionManager;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR2Endomorphism;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR2Vector;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR3Endomorphism;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR3Vector;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultRNEndomorphism;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultRNVector;

/**
 * This updater ensures the updating of a 2d vector that is the result of another vector
 * projected by a linear map.
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */
public class VectorDefByVectorAndLinearMapUpdater extends MMUpdater {

	private MMDefaultRNVector m_dependantVector;
	private MMDefaultRNVector m_vector;
	private MMDefaultRNEndomorphism m_linearMap;

	private Dependant m_dependantType;

	public VectorDefByVectorAndLinearMapUpdater(
		MMDefaultR3Vector dependantVector,
		MMDefaultR3Vector aVector,
		MMDefaultR3Endomorphism aMap,
		Dependant dependantType) {
		super(dependantVector, new MMObjectIF[] { aVector, aMap });
		m_dependantVector = dependantVector;
		m_vector = aVector;
		m_linearMap = aMap;
		m_dependantType = dependantType;
		ActionManager.performActionCycleFromObjects(m_parents);
	}

	public VectorDefByVectorAndLinearMapUpdater(
		MMDefaultR2Vector dependantVector,
		MMDefaultR2Vector aVector,
		MMDefaultR2Endomorphism aMap,
		Dependant dependantType) {
		super(dependantVector, new MMObjectIF[] { aVector, aMap });
		m_dependantVector = dependantVector;
		m_vector = aVector;
		m_linearMap = aMap;
		m_dependantType = dependantType;
		ActionManager.performActionCycleFromObjects(m_parents);
	}

	public void userDefinedUpdate() {
		m_dependantType.setDependant(m_dependantVector, m_vector, m_linearMap);
	}

	public static final Dependant LINEAR_MAPPED = new Dependant() {
		public void setDependant(
			MMDefaultRNVector dependantVector,
			MMDefaultRNVector aVector,
			MMDefaultRNEndomorphism aLinearMap) {
			if (aLinearMap.isMap()) {
				dependantVector.setVisible(true);
				aLinearMap.applyTo(aVector, dependantVector);
			}
			else {
				dependantVector.setVisible(false);
			}
		}
	};

	interface Dependant {
		public void setDependant(
			MMDefaultRNVector dependantVector,
			MMDefaultRNVector aVector,
			MMDefaultRNEndomorphism aLinearMap);
	}

}
