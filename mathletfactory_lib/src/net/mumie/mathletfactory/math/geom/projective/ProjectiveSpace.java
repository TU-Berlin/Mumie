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

package net.mumie.mathletfactory.math.geom.projective;

import net.mumie.mathletfactory.action.message.TodoException;
import net.mumie.mathletfactory.math.algebra.GroupElementIF;
import net.mumie.mathletfactory.math.algebra.geomgroup.AffineGroupElement;
import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.algebra.linalg.NumberVectorSpace;
import net.mumie.mathletfactory.math.geom.GeometryIF;

/**
 *  This implements PC(n) if the underlying number field is C
 *  and all its projective subspaces
 *  in addition it includes the standard affine space associated with it
 *  for the moment only two dimensional space is implemented...
 * 
 *  @author vossbeck
 *  @mm.docstatus finished
 */

public class ProjectiveSpace implements GeometryIF {

	// dimSpace+1 points determine an n dimensional projective subspace
	// in a dimEnvironment dimensional projective space
	protected NumberVectorSpace m_vectorspace;

	/**
	 * Creates an ProjectiveSpace with all data based on numbers with class
	 * type <code>entryClass</code>. <code>dim</code> defines the dimension of this
	 * <code>ProjectiveSpace</code>,
	 * i.e. a zero dimensional space would be a projective point, a one dimensional
	 * space would be a projective line, ...
	 * <code>extDim</code> describes the dimension of the environmental space.
	 */
	public ProjectiveSpace(Class entryClass, int dim, int extDim) {
		// problem: default basis vectors will be initialized to (1,0,...0), (0,1,0,...0), ...
		m_vectorspace = new NumberVectorSpace(entryClass, dim + 1, extDim + 1);
		//NumberMatrix defaultBaseInAbsCoords = m_vectorspace.get
		NumberTuple[] defBaseInAbsCoords = m_vectorspace.getBasisAsTuples();
		for (int i = 0; i < dim + 1; i++) {
			defBaseInAbsCoords[i].getEntryRef(extDim + 1).setDouble(1);
		}
		m_vectorspace.setBasis(defBaseInAbsCoords);
	}

	public ProjectiveSpace(NumberVectorSpace aVecSpace) {
		m_vectorspace = aVecSpace;
	}

	// ????? this method I do not understand.....does it work correctly?
	// is used to create a ProjectiveSpace from projective points in class
	// AffineSpace!
	public ProjectiveSpace(ProjectiveSpace[] spacev) {
		NumberVectorSpace[] temp_basis = new NumberVectorSpace[spacev.length];
		for (int i = 0; i < spacev.length; i++)
			temp_basis[i] = spacev[i].m_vectorspace;
		m_vectorspace = (NumberVectorSpace) temp_basis[0].joined(temp_basis);
	}

	/**
	 * Creates a zero dimensional ProjectiveSpace (that is in fact a point),
	 * with environmental dimension equal to the dimension of the <code>
	 * projPointCoord</code> minus one.
	 */
	public ProjectiveSpace(NumberTuple projPointCoord) {
		m_vectorspace = new NumberVectorSpace(projPointCoord);
	}

	public boolean equals(ProjectiveSpace aSpace) {
		return m_vectorspace.equals(aSpace.m_vectorspace);
	}

	/**
	 * This computes the intersection of <code>ProjectiveSpace</code>
	 * with <code>aProjectiveSpace</code> and afterwards returned by this method.
	 * This and <code>aProjectiveSpace</code> remain unchanged
	 */
	public ProjectiveSpace intersected(ProjectiveSpace aProjectiveSpace) {
		// m_vectorspace becomes the intersection between itsself and
		// s.m_vectorspace:
		return new ProjectiveSpace(
			(NumberVectorSpace) m_vectorspace.intersected(
				aProjectiveSpace.m_vectorspace));
	}

	public int getGeomType() {
		return GeometryIF.PROJECTIVEND_GEOMETRY;
	}

	public boolean isInfinite() {
		// checks if the space is contained in the standard infinite hyperplane
		throw new TodoException();
	}

	/**
	 * Returns the internal dimension of this <code>ProjectiveSpace</code>, which
	 * would be 0 for a point, 1 for a line, ...
	 */
	public int getDimension() {
		return m_vectorspace.getDimension() - 1;
	}

	/**
	 * Returns the environmental space dimension of this ProjectiveSpace.
	 * Observe: If an element of this ProjectiveSpace is defined
	 * by (n+1) homogeneous coordinates, the returned environmental dimension
	 * is n.
	 */
	public int getEnvDimension() {
		return m_vectorspace.getEnvDimension() - 1;
	}

	/**
	 *  Shouldn't this method call m_vectorspace.groupAction()? This would not
	 *  work in the current version, because NumberVectorSpace.groupAction()
	 *  uses the actual basis, whereas ProjectiveSpace.setProjectiveBasis() calls
	 *  NumberVectorSpace.setDefaultBasis()!
	 */
	public GeometryIF groupAction(GroupElementIF g) {
		// passen denn zum ProjectiveSpace und zum NumberVectorSpace die
		// gleichen GroupElements?
		// nein, zumindest die Gruppen, die auf RealVector agieren
		// machen Probleme. Daher ==> RealProjective space....
		NumberTuple[] defaultBasis = m_vectorspace.getBasisAsTuples();
		for (int i = 0; i < defaultBasis.length; ++i) {
			((AffineGroupElement)g).applyToProjectively(defaultBasis[i]);
		}
		m_vectorspace.setBasis(defaultBasis);
		return this;
	}

	/**
	 * Returns the basis vector array of the underlying <code>NumberVectorSpace
	 * </code> of this <code>ProjectiveSpace</code>.
	 *
	 * This method has side effects: Working on the data of the returned
	 * vectors will affect this ProjectiveSpace also.
	 */
	public NumberTuple[] getProjectiveBasis() {
		return m_vectorspace.getBasisAsTuples();
	}

	/**
	 * Sets the basis of the underlying <code>NumberVectorSpace</code> to
	 * <code>projCoordBasis</code>.
	 * It is expected that the elements in <code>projCoordBasis</code> are
	 * linearly independent.
	 */
	public ProjectiveSpace setProjectiveBasis(NumberTuple[] projCoordBasis) {
		// for performance reasons we assume, the basis has been checked to
		// be linear independent
		m_vectorspace.setBasis(projCoordBasis, true);
		return this;
	}

	public ProjectiveSpace setAffineBasis(NumberTuple[] coord) {
		NumberTuple[] tmp = new NumberTuple[coord.length];
		for (int i = 0; i < tmp.length; ++i) {
			tmp[i] = new NumberTuple(getNumberClass(), coord[0].getDimension() + 1);
			for (int j = 1; j < tmp[i].getDimension(); ++j) {
				//tmp[i].setEntryRef(j, coord[i].getEntry(j));
				tmp[i].getEntryRef(j).set(coord[i].getEntryRef(j));
			}
			int j = tmp[i].getDimension();
			tmp[i].getEntryRef(j).setDouble(1.0);
		}
		// for performance reasons we assume, the basis is checked to
		// be linear independent
		m_vectorspace.setBasis(tmp, true);
		return this;
	}

	public NumberTuple[] getAffineBasis() {
		NumberTuple[] tmp = new NumberTuple[getDimension() + 1];
		for (int i = 0; i < tmp.length; ++i) {
			tmp[i] = new NumberTuple(getNumberClass(), getEnvDimension());
			for (int j = 1; j <= getEnvDimension(); ++j) {
				tmp[i].setEntryRef(
					j,
					m_vectorspace.getBasisAsTuples()[i].getEntry(j).div(
						m_vectorspace.getBasisAsTuples()[i].getEntryRef(
							getEnvDimension() + 1)));
			}
		}

		return tmp;
	}

	/**
	 * Returns the class type of the underlying number class (currently something
	 * like MDouble.class, MRational.class, ...)
	 */
	public Class getNumberClass() {
		return m_vectorspace.getNumberClass();
	}

	protected boolean isValidAffineCoordinateIndex(int i) {
		return (i <= getEnvDimension()) && (i >= 1);
		// we use fortan-indexing in the coordinates!
	}

	public NumberMatrix getBasisMatrixRef() {
		return m_vectorspace.getBasisAsMatrixRef();
	}

}
