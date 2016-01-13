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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberTypeDependentIF;

/**
 *  Implements a number vector space
 *  This class has no public constructor, since it has to be
 *  constructed through the NumberVectorSpace, i.e. every NumberVector is
 * uniquely related to one NumberVectorSpace.
 *
 * @author vossbeck
 * @mm.docstatus finished
 */
public class NumberVector implements NumberTypeDependentIF {

	/**
	 * These are the coordinates of this NumberVector relative to the default
	 * base of its (unique) NumberVectorSpace.
	 */
	private NumberTuple m_coordsRel2DefBase;

	/**
	 * This is the unique NumberVectorSpace within this NumberVector lives.
	 */
	private NumberVectorSpace m_space;

	/**
	 * Creates an instance of {@link NumberVector} being an element of the
	 * {@link net.mumie.mathletfactory.math.algebra.linalg.NumberVectorSpace} <code>aSpace</code>
	 * and with <code>coefficients</code> being the coefficients of this <code>
	 * NumberVector</code> due to the default basis of <code>aSpace</code>.
	 *
	 * <b>Observe:</b><br>
	 * This constructor is typically used from within the class
	 * {@link net.mumie.mathletfactory.math.algebra.linalg.NumberVectorSpace} and its child
	 * classes and not intended for public usage. Use methods
	 * {@link net.mumie.mathletfactory.math.algebra.linalg.NumberVectorSpace#getNewFromCoordinates(NumberTuple)}
	 * to create new instances of a <code>NumberVector</code>
	 * instead.
	 */
	protected NumberVector(NumberTuple coefficients, NumberVectorSpace aSpace) {
		this.m_space = aSpace;
		m_coordsRel2DefBase = (NumberTuple) coefficients.deepCopy();
	}

	/**
	 * Copy constructor: Returns a new instance of <code>NumberVector</code> that has
	 * the same coordinates as the overgiven vector. Both <code>NumberVector</code>s
	 * will hold a reference to the same &quot;mother vector space&quot; (which is an
	 * instance of {@link net.mumie.mathletfactory.math.algebra.linalg.NumberVectorSpace}).
	 */
	protected NumberVector(NumberVector aNumberVector) {
		m_space = aNumberVector.m_space;
		m_coordsRel2DefBase =
			(NumberTuple) aNumberVector.m_coordsRel2DefBase.deepCopy();
	}

	/**
	 * returns the number class type this NumberVector is based on.
	 */
	public Class getNumberClass() {
		/* simply call getNumberClass from the mother space*/
		return m_space.getNumberClass();
	}

	/**
	 * Performs the multiplication of this <code>
	 * NumberVector</code> with a scalar that must be of type <code>MNumber</code>.
	 * This <code>NumberVector</code> will be changed and returned.
	 */
	public NumberVector multiplyWithScalar(MNumber aNumber) {
		m_coordsRel2DefBase.multWithNumber(aNumber);
		return this;
	}

	/**
	 * Performs the addition between this <code>NumberVector</code>
	 * and <code>addend</code>. This will be changed, while <code>addend</code>
	 * remains unchanged.
	 */
	public NumberVector addTo(NumberVector addend) {
		m_coordsRel2DefBase.addTo(addend.m_coordsRel2DefBase);
		return this;
	}

	/**
	 * Performs the subtraction between this <code>NumberVector</code>
	 * and <code>subtrahend</code>. This will be changed and returned, while
	 * <code>subtrahend</code> remains unchanged.
	 */
	public NumberVector subFrom(NumberVector subtrahend) {
		m_coordsRel2DefBase.subFrom(subtrahend.m_coordsRel2DefBase);
		return this;
	}

	/**
	 * Returns the reference to the <code>NumberVectorSpace</code>
	 * to which this <code>NumberVector</code> belongs.
	 */
	public NumberVectorSpace getVectorSpace() {
		return m_space;
	}

	/**
	 * Simply negates this <code>NumberVector</code>, i.e.
	 * multiplies it with -1 and then returns it.
	 */
	public NumberVector negate() {
		m_coordsRel2DefBase.negate();
		return this;
	}

  /** Returns the standard (euklidean) norm of this vector. */
  public MNumber standardNorm(){
  	return m_coordsRel2DefBase.standardNorm();
  }

  /** Sets the length of this vector to 1. */
  public void normalize(){
    m_coordsRel2DefBase.multiplyWithScalar(standardNorm().inverted());
  }


	/**
	 * Returns the coefficients of this <code>
	 * NumberVector</code> relative to the default basis of its <code>
	 * NumberVectorSpace</code>. It works without side effects, i.e a deep copy
	 * of the coordinates is returned.
	 */
	public NumberTuple getDefaultCoordinates() {
		return (NumberTuple) m_coordsRel2DefBase.deepCopy();
	}

	/**
	 * Returns the coefficients of this <code>
	 * NumberVector</code> relative to the default basis of it's <code>
	 * NumberVectorSpace</code>. These coordintes are internally stored as <code>
	 * NumberTuple</code> and a reference to this field is returned.
	 */
	public NumberTuple getDefaultCoordinatesRef() {
		return m_coordsRel2DefBase;
	}

	/**
	 * Sets the coeifficients of this <code>
	 * NumberVector</code> relative to the default basis of it's <code>
	 * NumberVectorSpace</code> to <code>coordinates</code> and returns this
	 * <code>NumberVector</code>. This method must be implemented due to the
	 * interface <code>NumberVectorSpace</code>, internally the method <code>
	 * setDefCoordinates</code> is called.
	 */
	public NumberVector setDefaultCoordinates(NumberTuple coordinatesRel2DefaultBasis) {
		return setDefCoordinates(coordinatesRel2DefaultBasis);
	}

	/**
	 * Sets the coeifficients of this <code>
	 * NumberVector</code> relative to the default basis of it's <code>
	 * NumberVectorSpace</code> to <code>coordinates</code> and returns this
	 * <code>NumberVector</code>.
	 *
	 * The <code>NumberVector</code> internally stores the <code>NumberVectorSpace
	 * </code> to which it belongs (m_space) and the coefficients relative
	 * to the default basis of it's <code>NumberVectorSpace</code> as a <code>
	 * NumberTuple</code>. If we sign this NumberTuple as coefficient[], this
	 * <code>NumberVector</code> is given as
	 * Sum over i: coefficient[i]*(m_space.getDefaultBasis())[i]
	 *
	 * @see NumberVectorSpace
	 */
	public NumberVector setDefCoordinates(NumberTuple coordinates) {
		m_coordsRel2DefBase.copyFrom(coordinates);
		return this;
	}

	/**
	 * Sets the coeifficients of this <code>
	 * NumberVector</code> relative to the actual basis of it's <code>
	 * NumberVectorSpace</code> to <code>coordinates</code> and returns this
	 * <code>NumberVector</code>.
	 */
	public NumberVector setCoordinates(NumberTuple coordsRelToBase) {
		// Let this NumberVector be v, b[i] the actual i-th basis vector of the space:
		// so we want to have v = sum over i: coord[i]*b[i], but
		// we have internally to store v = sum over i: defCoord[i]*e[i], when
		// e[i] is the i-th default base vector.
		// So we have to recalculate the cooefficients relative to the default
		// basis elements:
		NumberMatrix base_matrix = m_space.getBasisAsMatrixRef();
		// method applyTo changes the argument, so we make a copy:
		NumberTuple copy = (NumberTuple) coordsRelToBase.deepCopy();
		m_coordsRel2DefBase = base_matrix.applyTo(copy);
		return this;
	}

	/**
	 * Returns the coefficients of this <code>
	 * NumberVector</code> relative to the actual basis of it's <code>
	 * NumberVectorSpace</code>.
	 */
	public NumberTuple getCoordinates() {
		// m_coorfsRel2Base = (m_base_matrix)^-1 * m_coordsRel2Base
		NumberMatrix base_inverse_matrix = m_space.getBasisAsMatrixRef().inverted();
		// method applyTo will change the argument, so we need a copy:
		return base_inverse_matrix.applyTo(
			(NumberTuple) m_coordsRel2DefBase.deepCopy());
	}

	/**
	 * Returns the (absolute) coordinates
	 * of this <code>NumberVector</code> in the surrounding vector space!
	 */
	public NumberTuple getCoordinatesInSurroundingSpace() {
		//
		NumberTuple absCoordinates =
			m_space.getCoordinatesOfDefBaseInSurroundingSpace(1);
		// argument needs not to be copied:
		absCoordinates.multiplyWithScalar(m_coordsRel2DefBase.getEntryRef(1));
		NumberTuple tmp;
		for (int i = 2; i <= m_coordsRel2DefBase.getDimension(); i++) {
			// get the absolute coordinates of i-th default basis vector:
			tmp = m_space.getCoordinatesOfDefBaseInSurroundingSpace(i);
			// multiply with the corresponding coefficient:
			tmp.multiplyWithScalar(m_coordsRel2DefBase.getEntryRef(i));
			// add to:
			absCoordinates.addTo(tmp);
		}
		return absCoordinates;
	}

	/**
	 * Sets the <code>i</code>-th of this <code>
	 * NumberVector</code> relative to the actual basis of it's <code>
	 * NumberVectorSpace</code> to <code>coordinates</code> and returns this
	 * <code>NumberVector</code>.
	 */
	public NumberVector setCoordinate(int i, MNumber c) {
    m_coordsRel2DefBase.setEntry(i, c);
    return this;
	}

	/**
	 * Returns the i-th coordinate of this NumberVector relative to the default base.
	 * @return a copy of the i-th coordinate
	 */
	public MNumber getDefaultCoordinate(int i) {
		return m_coordsRel2DefBase.getEntry(i);
	}

	/**
	 * Returns the i-th coordinate of this NumberVector relative to the default base.
	 * @return the reference to the i-th coordinate
	 */
	public MNumber getDefaultCoordinateRef(int i) {
		return m_coordsRel2DefBase.getEntryRef(i);
	}

	/**
	 * This method sets the <it>i</it>th coordinate of this <code>NumberVector
	 * </code> to the value of <code>aCoordinate</code> and works without any
	 * side effects.
	 */
	public NumberVector setDefaultCoordinate(int i, MNumber aCoordinate) {
		//m_coordsRel2DefBase.setEntry(i,aCoordinate);
		m_coordsRel2DefBase.getEntryRef(i).set(aCoordinate);
		return this;
	}

	/**
	 * Returns the <code>i</code>-th coefficient of this <code>
	 * NumberVector</code> relative to the actual basis of it's <code>
	 * NumberVectorSpace</code>.
	 */
	public MNumber getCoordinate(int i) {
		return getCoordinates().getEntry(i);
	}

	/**
	 * A utility method that converts an array of <code>NumberTuple</code>s to an
	 * array of <cpde>NumberVector</code>s.
	 */
	public static NumberVector[] tuples2Vectors(
		NumberTuple[] tuples,
		NumberVectorSpace space) {
		Class entryClass = tuples[0].getNumberClass();
		NumberVector[] vecs = new NumberVector[tuples.length];
		for (int i = 0; i < tuples.length; i++)
			vecs[i] = new NumberVector(tuples[i], space);
		return vecs;
	}

	/**
	 * a utility method that converts an array of <cpde>NumberVector</code> to an
	 * array of <code>NumberTuple</code>ss.
	 */
	public static NumberTuple[] vectors2Tuples(NumberVector[] vecs) {
		NumberTuple[] tuples = new NumberTuple[vecs.length];
		for (int i = 0; i < vecs.length; i++) {
			tuples[i] = vecs[i].getDefaultCoordinates();
		}
		return tuples;
	}

	/**
	 * Checks whether this <code>NumberVector</code> is collinear to <code>
	 * aVector</code> and throws an exception if <code>aVector</code> is not an
	 * instance of <code>NumberVector</code>.
	 */
	public boolean isCollinear(NumberVector aVector) {
		if (aVector instanceof NumberVector) {
//			if(this.getCoordinates().isZero() || aVector.getCoordinates().isZero())
//				return true;
//			else
				return m_coordsRel2DefBase.isCollinear(
				((NumberVector) aVector).m_coordsRel2DefBase);
		}
		else
			throw new IllegalArgumentException(
				"NumberVector can only be checked to "
					+ "collinerarity with another instance of"
					+ " NumberVector");

	}

	/**
	 *  This method judges two vectors to be equal when their vectorspaces are
	 *  equal and their representation in these vectorspaces are equal.
	 *  This means, that both vectorspaces need to have the _same_default_basis_
	 *  (could be changed in future versions by addition of a check for different
	 *  default bases)
	 */
	public boolean equals(NumberVector vector) {
		return vector.getVectorSpace().equals(getVectorSpace())
			&& vector.getCoordinatesInSurroundingSpace().equals(
				getCoordinatesInSurroundingSpace());
	}

	/**
	 * This method returns a new instance of <code>NumberVector</code> which has
	 * the same dimension as this instance.
	 *
	 * The method is programmed such that inheriting classes (e.g.
	 * {@link net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR2Vector})
	 * do return an instance of their own class type.
	 * <b>Observe:</b> Due to this item every inheriting class must provide
	 * a &quot;copy constructor&quot; !
	 */
	public NumberVector deepCopy() {
		try {
			Constructor constructor =
				getClass().getConstructor(new Class[] { getClass()});
			return (NumberVector) constructor.newInstance(new Object[] { this });

		}
		catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(
				getClass() + " needs a constructor that takes a " + getClass());
		}
		catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		catch (InstantiationException e) {
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
  }

  /** Returns true if the given vector is orthogonal to this vector, false otherwise. */
  public boolean isOrthogonalTo(NumberVector aVector){
    return m_coordsRel2DefBase.dotProduct(aVector.m_coordsRel2DefBase).isZero();
  }

  /** Returns true if the given vector is parallel to this vector, false otherwise. */
  public boolean isParallelTo(NumberVector aVector){
    return m_coordsRel2DefBase.isCollinear(aVector.m_coordsRel2DefBase);
  }
}
