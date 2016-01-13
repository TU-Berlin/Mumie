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
import net.mumie.mathletfactory.math.algebra.linalg.LinearMap;
import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR2Endomorphism;

/**
 * This class is a standard <code>MMUpdater</code> working between instances of
 * {@link net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultRNEndomorphism} to
 * easily provide standard functionality  to get (dynamically) the copy, the
 * inverse, the transposed and the square of an <code>MMDefaultRNEndomorphism</code> of
 * an already existing linear map.
 * 
 * @author amsel
 * @mm.docstatus finished
 */
public class LinearMapDefByLinearMapUpdater extends MMUpdater {

	/* although an updater only works for MMObjectIF's (here MMDefaultRNEndomorphisms)
	 * the internal algorithms (until now) can be based on their general super class
	 * LinearMap.
	 */
	private LinearMap m_linearMap;
	private LinearMap m_dependentLinearMap;
	private MatrixDependant m_dependentType;

  /** Creates this updater for the given endomorphisms. */
	public LinearMapDefByLinearMapUpdater(
		MMDefaultR2Endomorphism dependant,
		MMDefaultR2Endomorphism linearMap,
		MatrixDependant dependentType) {
		super(dependant, new MMObjectIF[] { linearMap });
		m_linearMap = linearMap;
		m_dependentLinearMap = dependant;
		m_dependentType = dependentType;
		ActionManager.performActionCycleFromObjects(m_parents);
	}

	public void userDefinedUpdate() {
		m_dependentType.updateDependant(m_linearMap, m_dependentLinearMap);
	}

	/**
	 * Use this constant to get a (all time) copy of a linear map. 
	 */
	public static final MatrixDependant DEPENDANT_COPY = new MatrixDependant() {
		public void updateDependant(LinearMap map, LinearMap dependant) {
			dependant.setMap(map.isMap());
			dependant.setDefaultMatrixRepresentation(
				map.getDefaultMatrixRepresentation());
		}
	};

	/**
	 * Use this constant to get a (all time) inverse of a linear map.
	 * <br>
	 * Observe, that the inverse's map state (see
	 * {@link net.mumie.mathletfactory.algebra.linalg.LinearMap#isMap()} ) will be set
	 * to false, if the current linear map is not invertible but that it's internal matrix
	 * representation remains unchanged.
	 * 
	 */
	public static final MatrixDependant DEPENDANT_INVERTED =
		new MatrixDependant() {
		public void updateDependant(LinearMap map, LinearMap dependant) {
			dependant.setMap(true);
			// standard: it might have been set to false before
			if (map.isMap()) {
				NumberMatrix matrix = map.getDefaultMatrixRepresentation();
				if (matrix.isInvertible()) {
					matrix = map.getDefaultMatrixRepresentation().inverted();
					dependant.setDefaultMatrixRepresentationRef(matrix);
				}
				else {
					dependant.setMap(false);
				}
			}
			else {
				dependant.setMap(false);
			}
		}
	};

	/**
	 * Use this constant to get a (all time) adjoint of a linear map.
	 */
	public static final MatrixDependant DEPENDANT_TRANSPOSED =
		new MatrixDependant() {
		public void updateDependant(LinearMap map, LinearMap dependant) {
			dependant.setMap(map.isMap());
			NumberMatrix matrix = map.getDefaultMatrixRepresentation();
			dependant.setDefaultMatrixRepresentationRef(
				(NumberMatrix) matrix.transposed());
		}
	};

	/**
		 * Use this constant to get a (all time) product of a linear map with itsself.
		 * <br>
		 * Observe, that the square's map state (see
		 * {@link net.mumie.mathletfactory.algebra.linalg.LinearMap#isMap()} ) will be set
		 * to false, if the current linear map is acting between vector spaces of different
		 * dimension and thus it's matrix representation is not a square matrix.
		 * 
		 */
	public static final MatrixDependant DEPENDANT_SQUARED =
		new MatrixDependant() {
		public void updateDependant(LinearMap map, LinearMap dependant) {
			if (map.getDefaultMatrixRepresentationRef().isSquare()) {
				dependant.setMap(map.isMap());
				NumberMatrix matrixOfMap = map.getDefaultMatrixRepresentationRef();
				dependant.setDefaultMatrixRepresentation(
					matrixOfMap.deepCopy().mult(matrixOfMap));
			}
		}
	};

}

/** Used by this class for the concrete calculation of the dependant matrix. */
interface MatrixDependant {
	public void updateDependant(LinearMap map, LinearMap dependant);
}
