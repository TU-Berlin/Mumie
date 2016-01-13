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
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR2Vector;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultRNVector;

/**
 * This updater ensures the updating of a 2d vector that depends on another vector.
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */
public class VectorToVectorUpdater extends MMUpdater {

	private MMDefaultRNVector m_freeVector;
	private MMDefaultRNVector m_dependantVector;
	private VectorDependant m_dependantType;

	public VectorToVectorUpdater(
		MMDefaultR2Vector dependantVector,
		MMDefaultR2Vector freeVector,
		VectorDependant dependantType) {
		super(dependantVector, new MMObjectIF[] { freeVector });
		m_dependantVector = dependantVector;
		m_freeVector = freeVector;
		m_dependantType = dependantType;
		ActionManager.performActionCycleFromObjects(m_parents);
	}

	public void userDefinedUpdate() {
		m_dependantType.setDependant(m_freeVector, m_dependantVector);
	}

	public static final VectorDependant COPY = new VectorDependant() {
		public void setDependant(
			MMDefaultRNVector aVector,
			MMDefaultRNVector depVector) {
			if (aVector.getVectorSpace().getDimension()
				== depVector.getVectorSpace().getDimension()
				&& aVector.getNumberClass().equals(depVector.getNumberClass()))
				depVector.setDefaultCoordinates(aVector.getDefaultCoordinates());
			else
				throw new IllegalArgumentException(
					"overgiven vectors are either of different numbertype"
						+ "or of different dimension, cannot perform operation.");
		}
	};

	public static final VectorDependant SET_DEFAULT_FROM_ACTUAL_COORDINATES =
		new VectorDependant() {
		public void setDependant(
			MMDefaultRNVector aVector,
			MMDefaultRNVector depVector) {
			depVector.setDefaultCoordinates(aVector.getCoordinates());
		}
	};

	public static final VectorDependant SET_ACTUAL_FROM_DEFAULT_COORDINATES =
		new VectorDependant() {
		public void setDependant(
			MMDefaultRNVector aVector,
			MMDefaultRNVector depVector) {
			depVector.setCoordinates(aVector.getDefaultCoordinatesRef());
		}
	};

	interface VectorDependant {
		public void setDependant(
			MMDefaultRNVector vector,
			MMDefaultRNVector dependant);
	}
}
