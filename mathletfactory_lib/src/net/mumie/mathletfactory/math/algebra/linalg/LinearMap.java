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

import net.mumie.mathletfactory.action.message.MathContentException;
import net.mumie.mathletfactory.math.number.NumberTypeDependentIF;

/**
 * This class represents a linear map that mappes from one finite number vector space to another
 *
 * @author vossbeck
 * @mm.docstatus finished
 */
public class LinearMap implements NumberTypeDependentIF {

	/**
	 * This is a permanent link (a reference) to the domain vector space.
	 */
	private NumberVectorSpace m_domain;

	/**
	 * This is a permantent link (a reference) to the range vector space.
	 */
	private NumberVectorSpace m_range;

	/**
	 *Internally any instance of <code>LinearMap</code> stores its matrix
	 *representation with respect to the default basis in the domain vector space
	 *and the default basis in the range vector space.
	 *
	 * More precisely we have the following calculation:<br>
	 * m_default[i](range) = sum over j m_matrix[i][j] * m_default[j](domain)
	 * <p>
	 * <b>Observe</b>: Because this field essentially describes the real mapping
	 * between the spaces, the <code>LinearMap</code> itself is independent of
	 * any base change in domain or range.
	 */
	private NumberMatrix m_matrix;

	private Matrix m_exceptionMatrix;

	private boolean m_isMap = true;

	/**
	 * constructs the linear <it>L</it>map acting between vector spaces
	 * that satisfies
	 * <it>L</it>: domainVec[i] -> rangeVec[i], i=0, N-1
	 * if
	 * Side Effect:  i) domainVec und rangeVec remain unchanged;
	 *              ii) new LinearMap is INDEPENDENT of domainVec and rangeVec
	 */
	public LinearMap(
		NumberVector[] domainVec,
		NumberVector[] rangeVec) {
		// to do: check arguments...
		m_domain = domainVec[0].getVectorSpace();
		m_range = rangeVec[0].getVectorSpace();
		NumberTuple[] dom = new NumberTuple[domainVec.length];
		NumberTuple[] ran = new NumberTuple[domainVec.length];
		for (int i = 0; i < dom.length; ++i) {
			dom[i] = domainVec[i].getDefaultCoordinates();
			ran[i] = rangeVec[i].getDefaultCoordinates();
		}
		m_matrix = new NumberMatrix(dom, ran);
		m_exceptionMatrix =
			Matrix.getStringMatrix(
				m_range.getDimension(),
				m_domain.getDimension(),
				"x");
	}

	/**
	 * constructs a linear map (more precisely an endomorphism) in the given
	 * vector space <code>aSpace</code> which is initialized to the identity map
	 * at the beginning. Use methods like {@link #setDefaultMatrixRepresentation}
	 * to modify this <code>LinearMap</code>.
	 */
	public LinearMap(NumberVectorSpace aSpace) {
		m_domain = aSpace;
		m_range = aSpace;
		m_matrix =
			new NumberMatrix(
				aSpace.getNumberClass(),
				aSpace.getDimension(),
				aSpace.getDimension());
		m_matrix.setToIdentity();
		Object[] X = new Object[m_domain.getDimension() * m_range.getDimension()];
		for (int i = 0; i < X.length; i++)
			X[i] = "x";
		m_exceptionMatrix = new Matrix(String.class, m_domain.getDimension(), X);
	}

	/**
	 * @throws MathContentException will be thrown if <code>domainVec</code> are not
	 * linearly independent.
	 */
	public LinearMap setFrom(
		NumberVector[] domainVec,
		NumberVector[] rangeVec) throws MathContentException {
		//TODO: check consistency of arrays!
		Class numberClass = domainVec[0].getNumberClass();// all must be of same class type!!
		NumberTuple[] dom = new NumberTuple[m_domain.getDimension()];
		NumberTuple[] ran = new NumberTuple[m_range.getDimension()];
		for (int i = 0; i < m_domain.getDimension(); ++i) {
			dom[i] = domainVec[i].getDefaultCoordinates();
			ran[i] = rangeVec[i].getDefaultCoordinates();
		}
		NumberMatrix checkMatrix = new NumberMatrix(numberClass, false,dom);
		if(checkMatrix.rank()<dom.length){
			setMap(false);
			throw new MathContentException("domain vectors are not linearly independent");
		}
		else {
			setMap(true);
			m_matrix = new NumberMatrix(dom, ran);
		}
		return this;
	}

	/**
	 * Applies this <code>LinearMap</code> to the given <code>elementInDomain</code>
	 * if it's state is really a map
	 * (see {@link #isMap()}), otherwise it will return <code>null</code>.
	 *
	 * @param elementInDomain the <code>NumberVector</code> in the domain
	 * @return a new <code>NumberVector</code> in the range
	 *
	 * @mm.sideeffects elementInDomain remains unchanged, creates new vector in range
	 */
	public NumberVector applyTo(NumberVector elementInDomain) {
		if (isMap())
			return (
				m_range.getNewFromCoordinates(
					m_matrix.applyTo(elementInDomain.getDefaultCoordinates())));
		else
			return null;
	}


	/**
	 * Applies this <code>LinearMap</code> to the given <code>preImage</code>
	 * and puts the result into <code>image</code> if it's state is really map
	 * (see {@link #isMap()}), otherwise <code>image</code> will remain unchanged.
	 *
	 * @param preImage the <code>NumberVector</code> of the domain
	 * @param image the <code>NumberVector</code> which must not be <b><code>null</code></b>
	 *
	 * @mm.sideeffects elementInDomain remains unchanged, <code>image</code> will be changed
	 */
	public void applyTo(
		NumberVector preImage,
		NumberVector image) {
		//TODO: check for correct space dimensions
		// apply map only, if it is really a map
		if (isMap())
			image.setDefaultCoordinates(
				m_matrix.applyTo(preImage.getDefaultCoordinates()));
	}

	/**
	 * Returns the number matrix corresponding to the linear mapping with respect
	 * to the default bases in domain and range as reference. This matrix
	 * representation is internally held as field and a deep copy of this matrix
	 * is returned.
   * @see net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix#deepCopy
	 */
	public NumberMatrix getDefaultMatrixRepresentation() {
		return m_matrix.deepCopy();
	}

	/**
	 * this method sets the internal stored matrix representation of the <code>
	 * LinearMap</code> (see {@link #m_matrix}) to <code>aMatrix</code>, that means
	 * the matrix representation with respect to the default basis in the domain
	 * and the range
	 * @mm.sideeffects <code>aMatrix</code> will be deep copied.
	 */
	public void setDefaultMatrixRepresentation(NumberMatrix aMatrix) {
		// check for dimension and entry type of the matrix:
		m_matrix = aMatrix.deepCopy();
	}

	/**
	 * this method sets the internal stored matrix representation of the <code>
	 * LinearMap</code> (see {@link #m_matrix}) to <code>aMatrix</code>, that means
	 * the matrix representation with respect to the default basis in the domain
	 * and the range.
	 *
	 * @mm.sideeffects this <code>LinearMap</code> will hold a reference to <code>
	 * 	aMatrix</code> after the method call.
	 */
	public void setDefaultMatrixRepresentationRef(NumberMatrix aMatrix) {
		// check for dimension and entry type of the matrix:
		m_matrix = aMatrix;
	}

	/**
	 * Returns the number matrix corresponding to the linear mapping with respect
	 * to the default bases in domain and range as reference. This matrix
	 * representation is internally held as field and a reference to this field
	 * is returned.
	 *
	 * <b>SideEffect:</b> A reference pointing to the interanl field
	 * {@link #m_matrix} is returned.
	 *
	 */
	public NumberMatrix getDefaultMatrixRepresentationRef() {
		return m_matrix;
	}

	/**
	 * Returns the matrix representation of this <code>LinearMap</code> with
	 * respect to the actual basis in the domain and the range.
	 *
	 * <b>SideEffect:</b> This method has no side effects, a new instance of
	 * {@link net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix} will be returned.
	 */
	public NumberMatrix getActualMatrixRepresentation() {
		NumberMatrix matrixRepresentation =
			m_range.getBasisAsMatrixRef().deepCopy();
		matrixRepresentation.mult(m_matrix).mult(
			m_domain.getBasisAsMatrixRef().inverted());
		return matrixRepresentation;
	}

	/**
	 * this method sets the matrix representation with respect to the actual basis
	 * of domain and range to be <code>aMatrix</code> and has no side effects.
	 */
	public void setActualMatrixRepresentation(NumberMatrix aMatrix) {
		// do not change the basis matrix in m_domain -->inverted() makes a copy!
		m_matrix = m_domain.getBasisAsMatrixRef().inverted();
		m_matrix.mult(aMatrix).mult(m_range.getBasisAsMatrixRef());
	}

	/**
	 * This method returns the domain vector space due to this instance of <code>
	 * LinearMap</code> (see the field {@link #m_domain}).
	 *
	 * <b>SideEffect</b>: The method returns a reference to the field {@link #m_domain}.
	 */
	public NumberVectorSpace getDomain() {
		return m_domain;
	}

	/**
	 * This method returns the range vector space due to this instance of <code>
	 * LinearMap</code> (see the field {@link #m_range}).
	 *
	 * <b>SideEffect</b>: The method returns a reference to the field {@link #m_range}.
	 */
	public NumberVectorSpace getRange() {
		return m_range;
	}

	// inherits the comment from the interface MathEntityIF:
	public Class getNumberClass() {
		return m_matrix.getNumberClass();
	}

  /** Returns false, if this object is mathematical not a map (might occur from setting to it accidentally to non unique). */
	public boolean isMap() {
		return m_isMap;
	}

  /** Sets to false, if this object is mathematical not a map (might occur from setting to it accidentally to non unique). */
	public void setMap(boolean aValue) {
		m_isMap = aValue;
	}

  /** Offers a fall back solution, when {@link #isMap} returns false. */
	public Matrix getExceptionMatrix() {
		return m_exceptionMatrix;
	}

  public String toString(){
    return getDefaultMatrixRepresentationRef().toString();
  }
}
