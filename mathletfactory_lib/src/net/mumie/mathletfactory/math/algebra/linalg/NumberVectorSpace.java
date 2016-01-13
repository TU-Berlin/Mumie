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

package net.mumie.mathletfactory.math.algebra.linalg;

import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.math.number.NumberTypeDependentIF;
import net.mumie.mathletfactory.math.number.numeric.SolveHomogen;
import net.mumie.mathletfactory.math.number.numeric.Span;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultRN;


/**
 * This class implements a number Vector space with arbitrary dim inside another
 * vectorspace extDim. The NumberVectorSpace is a mathematical model for a
 * <it>K</it><sup>n</sup> or a subspace of it, where <it>K</it> means the field
 * of complex, real or rational numbers.
 * <p>
 * Internally we store a basis for the number vector space (more precisely
 * the absolute coordinates of these basis vectors in the appropriate
 * (full) surrounding space). This basis is per default the canonical basis
 * of unit vectors. 
 * <p>
 * The <code>NumberVectorSpace</code> offers methods to instantiate
 * {@link NumberVector}s, i.e getting a new vector from the coordinates with
 * respect to the basis
 * (see {@link NumberVectorSpace#getNewFromCoordinates getNewFromCoordinates}).
 * <p>
 * <b>Observe:</b>
 * These should be the only methods used to get instances of class {@link NumberVector}.
 * 
 * @author Paehler
 * @mm.docstatus finished
 *
 */
public class NumberVectorSpace implements NumberTypeDependentIF {


	/**
	 * The basis is stored as a matrix. The <it>i</it>-th
	 * column of this matrix are the absolute coordinates of the <it>i</it>-th
	 * basis vector relative to the canonical base of the surrounding <b>K</b>
	 * <sup><it>n</it></sup> with <b>n</b> equal to the environment dimension.
	 */
	private NumberMatrix m_basisMatrix;

	/**
	 *  Creates a new NumberVectorSpace with entry type c
	 *  of dimension <code>dim</code> in a space of exterior dimension <code>extDim
	 * </code>. The basis is in this case (e1,e2,..edim). 
	 */
	public NumberVectorSpace(Class c, int dim, int extDim) {
		initializeBasis(c, dim, extDim);
	}

	/**
	 * Sets the basis to <code>aBasis</code>, which has to be understood as the
	 * absolute coordinates of the basis elements for this <code>
	 * NumberVectorSpace</code> within the surrounding space, e.g. for a 2-D
	 * sphere within R^3 aBasis would consist of two <code>NumberTuple</code> each
	 * with 3 entries.
	 */
	public NumberVectorSpace(NumberTuple[] absoluteCoordsOfBasis) {
		if (!checkEntryClass(absoluteCoordsOfBasis))
			throw new IllegalArgumentException("all vectors must have the same entry type");
		Class c = absoluteCoordsOfBasis[0].getNumberClass();
		int baseLength = absoluteCoordsOfBasis.length;
		int envDim = absoluteCoordsOfBasis[0].getDimension();
		// first initialize the basis:
		initializeBasis(c, baseLength, envDim);
		// set the appropriate values for the basis:
		setBasis(absoluteCoordsOfBasis);
  }

	/**
	 * Creates a one dimensional NumberVectorSpace that is the span of
	 * <code>aOneDSpace</code>. The constructor has no side effects.
	 */
	public NumberVectorSpace(NumberTuple aOneDSpace) {
		this(aOneDSpace.asOneDArray());
	}

  /**
   * Copy constructor. 
   */
	public NumberVectorSpace(NumberVectorSpace aNumberVectorSpace) {
		m_basisMatrix = aNumberVectorSpace.m_basisMatrix.deepCopy();
	}

	/**
	 * Creates a NumberVectorSpace which is the span of all the spaces
	 * given as argument.
	 */
	public NumberVectorSpace(NumberVectorSpace[] spaces) {
		NumberVectorSpace joinedSpace = (NumberVectorSpace) joined(spaces);
		m_basisMatrix = joinedSpace.m_basisMatrix.deepCopy();
	}

	private void initializeBasis(Class c, int dim, int extDim) {
		m_basisMatrix = new NumberMatrix(c, dim, extDim);
		MNumber one = NumberFactory.newInstance(c, 1.0);
		for (int i = 1; i <= dim; i++) { // NumberTuple asks for fortran indexing!
			m_basisMatrix.setColumnVector(i, new NumberTuple(c, extDim));
			m_basisMatrix.setEntry(i, i, one);
		}
	}

	/**
	 * The traversed <code>NumberTuple</code> array will be treated as column
	 * vectors with coordinates in the surrounding space. These coordinates
	 * are part of the internal definition of a vector spac. 
   * After the method call this <code>NumberVectorSpace</code>
	 * and <code>basisCoords</code> are independant.
	 */
	public void setBasis(
		NumberTuple[] basisCoords,
		boolean checked) {
		if (!checked && basisCoords.length != 1)
			checkLinearIndependence(basisCoords);
    if(m_basisMatrix.getColumnCount() != basisCoords.length) // has the length of the basis changed?
      m_basisMatrix = new NumberMatrix(getNumberClass(), basisCoords.length, m_basisMatrix.getRowCount());
		m_basisMatrix.setColumnVectors(basisCoords);
	}

  /** Sets the given basis of this vectorspace. */
  public void setBasis(NumberMatrix coords){
    m_basisMatrix = coords;
  }

  /** Sets the given basis of this vectorspace. */ 
	public void setBasis(NumberTuple[] basisCoords) {
		setBasis(basisCoords, false);
	}

	/** 
   * Checks an array of NumberTupels for linear independence. 
   */
	private void checkLinearIndependence(NumberTuple[] vecs) {
		NumberMatrix testMatrix =
			new NumberMatrix(
				vecs[0].getNumberClass(),
				false,
				vecs);
		if (testMatrix.rank() < vecs.length)
			throw new IllegalArgumentException("vectors not linear independent!");
	}


	/** Returns the basis as an array of number tuples. */
	public NumberTuple[] getBasisAsTuples() {
		return m_basisMatrix.getColumnVectors();
	}

	/**
	 * Method getNumberClass returns the number class type on which this <code>
	 * NumberVectorSpace</code> is based on.
	 */
	public Class getNumberClass() {
		return m_basisMatrix.getNumberClass();
	}

	/**
	 * Returns the dimension of this <code>
	 * NumberVectorSpace</code>
	 */
	public int getDimension() {
		return m_basisMatrix.isZero() ? 0 : m_basisMatrix.getColumnCount();
	}

	/**
	 *  Returns the dimension of the smallest space
	 *  in the inductive sequence that contains this space.
	 */
	public int getEnvDimension() {
		return m_basisMatrix.getRowCount();
	}

	/**
	 * Tests whether all NumberTuple elements of the
	 * given argument use the same entry class (MDouble, MRational, ...).
	 */
	private boolean checkEntryClass(NumberTuple[] aListOfVectors) {
		Class entryClass = aListOfVectors[0].getNumberClass();
		for (int i = 1; i < aListOfVectors.length; i++) {
			if (entryClass != aListOfVectors[i].getNumberClass())
				return false;
		}
		return true;
	}

	/**
	 * Checks if this NumberVectorSpace is equal to aSpace, i.e.
	 * if the bases of each of them span the same space.
	 */
	public boolean equals(NumberVectorSpace aSpace) {
		// first perform trivial checks
		if (getDimension() != aSpace.getDimension()
			|| getNumberClass() != aSpace.getNumberClass())
			return false;
		NumberTuple[] allSpan =
			new NumberTuple[aSpace.getDimension() + getDimension()];
		System.arraycopy(
			NumberVector.vectors2Tuples(getBasis()),
			0,
			allSpan,
			0,
			getDimension());
		System.arraycopy(
			NumberVector.vectors2Tuples(aSpace.getBasis()),
			0,
			allSpan,
			getDimension(),
			getDimension());
		// if the Span of the union of bases is equal to the span of the
		// basis vectors (i.e. the dimension remains unchanged of the addition of
		// the other basis, the two vectorspaces are equal.
		if (Span.span(allSpan).length != getDimension())
			return false;
		return true;
	}

  /** Returns the zero vector of this vector space. */
	public NumberVector getZeroVector() {
		return getNewFromCoordinates(
			new NumberTuple(getNumberClass(), getDimension()));
	}

  /** Returns a reference to the basis matrix. */
	public NumberMatrix getBasisAsMatrixRef() {
		return m_basisMatrix;
	}

	/**
   * Returns the basis as an array of number vectors. 
	 */
	public NumberVector[] getBasis() {
		NumberVector[] tmp = new NumberVector[m_basisMatrix.getColumnCount()];
    NumberTuple[] cols = getBasisAsTuples();
		for (int i = 1; i <= m_basisMatrix.getColumnCount(); i++)
			tmp[i - 1] = new NumberVector(cols[i - 1], this);
		return tmp;
	}

	/**
   * Returns a new vector of this space with coordinates with respect to the current basis.
	 */
	public NumberVector getNewFromCoordinates(NumberTuple coord) {
		return new NumberVector(m_basisMatrix.applyTo(coord), this);
	}

  /**
   * Returns a new vector of this space with coordinates with respect to the default basis.
   */
  public NumberVector getNewFromDefaultCoordinates(NumberTuple coord) {
    return new NumberVector(coord, this);
  }

	/**
   * Returns a copy of this vectorspace that contains also a copy of the basis of this vectorspace.
	 */
	public NumberVectorSpace deepCopy() {
		NumberTuple[] basisCoords = new NumberTuple[getDimension()];
		for (int i = 0; i < m_basisMatrix.getColumnCount(); i++)
			basisCoords[i] = m_basisMatrix.getColumnVector(i);
		NumberVectorSpace space = new NumberVectorSpace(basisCoords);
		return space;
	}

	/**
   * Returns a new vector space that is the intersection of this vectorSpace and the argument.
	 */
	public NumberVectorSpace intersected(NumberVectorSpace aVS) {
		NumberTuple[] newBasis =
			SolveHomogen.intersection(
				NumberVector.vectors2Tuples(getBasis()),
				NumberVector.vectors2Tuples(aVS.getBasis()));

		return new NumberVectorSpace(newBasis);
	}

  /**
   * Returns a new vector space that is the join of this vectorSpace and the arguments.
   */
	public NumberVectorSpace joined(NumberVectorSpace[] vectorSpaces) {
		NumberVectorSpace joinedSpace = (NumberVectorSpace) vectorSpaces[0];
		for (int i = 1; i < vectorSpaces.length; i++)
			joinedSpace = (NumberVectorSpace) joinedSpace.joined(vectorSpaces[i]);
		return joinedSpace;
	}

  /**
   * Returns a new vector space that is the join of this vectorSpace and the argument.
   */
	public NumberVectorSpace joined(NumberVectorSpace aVectorSpace) {

		// we need at most dim(aVectorSpace)+dim(this) basis vectors:
		NumberTuple[] newBasis =
			new NumberTuple[aVectorSpace.getDimension() + getDimension()];

		// put the both bases of the vectorspaces together in one array
		System.arraycopy(
			m_basisMatrix.getColumnVectors(),
			0,
			newBasis,
			0,
			m_basisMatrix.getColumnCount());
		System.arraycopy(
			NumberVector.vectors2Tuples(aVectorSpace.getBasis()),
			0,
			newBasis,
			m_basisMatrix.getColumnCount(),
			aVectorSpace.getDimension());

		// the new space has the span of the two bases as new basis
		return new NumberVectorSpace(Span.getBasisForSpanOf(newBasis));
	}

	/**
	 * This method returns the full vector space with environment dimension
	 * equal to the environment dimension of this space. The method returns
	 * an instance of {@link net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultRN} with
	 * appropriate envrionment dimension, i.e. the default type of the surrounding
	 * number vector space.
	 */
	public NumberVectorSpace getSurroundingSpace() {
		return new MMDefaultRN(getNumberClass(), getEnvDimension());
	}

  /**
   * Returns a new vector space that is the subspace of this vector space which is 
   * spanned by the given vectors. 
   */
	public NumberVectorSpace getSubspace(NumberVector[] vecs) {
		return new NumberVectorSpace(
			Span.getBasisForSpanOf((NumberVector.vectors2Tuples(vecs))));
	}

	/**
	 * Returns true, if the given vector space is a subspace of this vector space.
	 */
	public boolean isASubspaceOf(NumberVectorSpace aSpace) {

		return joined(aSpace).getDimension() == getDimension();
	}

  /** Returns true if this vector space has the dimension 0. */
	public boolean isZeroVectorSpace() {
		return getDimension() == 0;
	}

	/**
	 * Returns the absolute
	 * coordinate of the <code>i</code>-th base vector in the surrounding
	 * space of this <code>NumberVectorSpace</code>. The method works without side
	 * effects, i.e. a deep copy of those coordinates is returned.
	 */
	public NumberTuple getCoordinatesOfDefBaseInSurroundingSpace(int i) {
		return (NumberTuple) m_basisMatrix.getColumnVector(i).deepCopy(); //use fortran
		// indexing for enumeration of base elements
	}
}
