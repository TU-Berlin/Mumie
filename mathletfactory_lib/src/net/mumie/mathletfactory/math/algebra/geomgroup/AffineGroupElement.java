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

package net.mumie.mathletfactory.math.algebra.geomgroup;

import net.mumie.mathletfactory.action.message.IllegalUsageException;
import net.mumie.mathletfactory.action.message.TodoException;
import net.mumie.mathletfactory.math.algebra.GroupElementIF;
import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.math.number.NumberTypeDependentIF;

/**
 *  Implements the affine group A(N) in arbitrary dimension N,
 *  and the standard representation of A(N) in GL(N+1) (GL(N) = space
 *  of linear endomorphisms of C^N). The affine group A(N) is
 *  the group of automorphisms of an N dimensional affine space.
 *  The internal Structure is a GL(N) for the deformation
 *  and a N+1 dimensional vector for the translation part.
 *  This allows the inversion in one space dimension lower.
 *
 * @author vossbeck
 * @mm.docstatus finished
 */

public class AffineGroupElement implements GroupElementIF, NumberTypeDependentIF {

  public final static double EPSILON = 1e-14;
  private NumberMatrix m_linearMatrixRepresentation;
  private NumberMatrix m_deformationPart;


  public AffineGroupElement(Class numberClass, int dimension) {
    //    m_deformation = new RealGeneralLinearGroupElement(numberClass, dimension);
    //    m_translation = new NumberTuple(numberClass,dimension);
    //    m_translation.setRealType(true);
    m_linearMatrixRepresentation = new NumberMatrix(numberClass,dimension+1,dimension+1);
    m_linearMatrixRepresentation.setToIdentity();
    m_deformationPart = new NumberMatrix(numberClass,getDimension(),getDimension());
  }

  public AffineGroupElement(AffineGroupElement anAffineGroupElement) {
    setTo(anAffineGroupElement);
  }

  public Class getNumberClass() {
    return m_linearMatrixRepresentation.getNumberClass();
  }

  /**
   *  returns the dimension of the affine group. Note: the N-dimensional
   *  affine group has a representation
   */
  public int getDimension() {
    return m_linearMatrixRepresentation.getDimension()-1;
  }

  /**
   */
  public boolean isIdentity() {
    return m_linearMatrixRepresentation.isIdentity();
  }

  public void setToIdentity(){
    m_linearMatrixRepresentation.setToIdentity();
  }

  /**
   * Method computes the composition of this with an other affine Group Element
   *
   * @return The changed froup element is returnes.
   * Side effect:
   *  i) this is replaced by the composition
   * ii) argument group remains unchanged
   */

  public GroupElementIF composeWith(GroupElementIF group) {
    m_linearMatrixRepresentation.mult(((AffineGroupElement)group).m_linearMatrixRepresentation);
    return this;
  }

  /**
   */
  public GroupElementIF inverse() {
    m_linearMatrixRepresentation.inverse();
    return this;
  }

  public GroupElementIF inverted() {
    return deepCopy().inverse();
  }

  /**
   */
  public boolean isEqual(GroupElementIF group) {
    // what to do  with EuclideanGroupElement
    if( group instanceof AffineGroupElement )
      return m_linearMatrixRepresentation.equals(((AffineGroupElement)group).m_linearMatrixRepresentation);
    return false;
  }

  public NumberTuple applyToProjectively(NumberTuple coords) {
    // here we expect that the dimension of element is one bigger than the
    // dimension of this affine transformation

    // we must not change the translation part of the affine Group itsself:
    if (coords.getDimension() == getDimension()+1 ) {
      return m_linearMatrixRepresentation.applyTo(coords);
    }
    else
      throw new IllegalArgumentException(
        "dimension of element vector must be one bigger than group dimension");
  }

  public void applyToProjectively(NumberTuple coords, NumberTuple result){
  if (coords.getDimension() == getDimension()+1
     && result.getDimension() == getDimension()+1 ) {
      m_linearMatrixRepresentation.applyTo(coords, result);
    }
    else
      throw new IllegalArgumentException(
        "dimension of element vector must be one bigger than group dimension");

  }


  public NumberTuple applyInvToProjectively(NumberTuple element) {
    if (element.getDimension() == getDimension()+1 ) {
      return m_linearMatrixRepresentation.inverted().applyTo(element);
    }
    else
      throw new IllegalArgumentException(
        "dimension of element vector must be one bigger than group dimension");
  }

  /**
   *  Applies this group element to the given coordinates
   *  For higher performance better use {@link #applyToProjectively(NumberTuple)} directly.
   */
  public NumberTuple applyTo(NumberTuple coords) {
    NumberTuple coordsProjectively = new NumberTuple(coords.getNumberClass(),
                                                      getDimension()+1);
    for(int i=1; i<=3; i++)
      coordsProjectively.setEntryRef(i, coords.getEntryRef(i));
    MNumber one = NumberFactory.newInstance(coords.getNumberClass());
    one.setDouble(1);
    coordsProjectively.setEntryRef(4, one);
    return applyToProjectively(coordsProjectively);
  }

  public AffineGroupElement setTranslation(NumberTuple translationVector) {
    //m_linearMatrixRepresentation.setColumnVector(getDimension()+1,translationVector);
    int calcLength = Math.min(getDimension(),translationVector.getDimension());
    for(int i=1; i<= calcLength; i++){
      m_linearMatrixRepresentation.getEntryAsNumberRef(i,getDimension()+1).set(translationVector.getEntryRef(i));
    }
    return this;
  }

  public AffineGroupElement setUniformScale(MNumber aScalingFactor) {
    if( aScalingFactor.getClass().equals(getNumberClass()) ) {
      MNumber zero = NumberFactory.newInstance(getNumberClass());
      for(int i=1;i<=getDimension(); i++)
        for(int j=1; j<=getDimension();j++) {
          if(i==j)
            m_linearMatrixRepresentation.setEntry(i,j,aScalingFactor);
          else
            m_linearMatrixRepresentation.setEntry(i,j,zero);
        }
    }
    else
      throw new IllegalArgumentException("scaling factor must be same number " +
          "type as the affine group element");
    return this;
  }


  public boolean isDeformationScaling() {
    adjustDeformationPart();
    return m_deformationPart.isDiagonalMatrix();
  }

  public MNumber getScale (int coordinateIndex) {
    if( isDeformationScaling() ) {
      if ( isValidCoordinateIndex(coordinateIndex) )
        return m_linearMatrixRepresentation.getEntry(coordinateIndex,coordinateIndex);
      else
        throw new IllegalArgumentException("invalid coordinate index entered");
    }
    else
      throw new IllegalUsageException("affine group element is not a pure scaling");
  }

  public NumberTuple getTranslation() {
    NumberTuple t = new NumberTuple(getNumberClass(),getDimension());
    t.copyFrom(m_linearMatrixRepresentation.getColumnVector(getDimension()+1));
    return t;
  }

  public RealGeneralLinearGroupElement getDeformation() {
    NumberMatrix deformation = new NumberMatrix(getNumberClass(),getDimension(),getDimension());
    for(int i=1; i<=getDimension(); i++)
      deformation.setColumnVector(i,m_linearMatrixRepresentation.getColumnVector(i));
    return new RealGeneralLinearGroupElement(deformation);
  }

  public AffineGroupElement setDeformation(RealGeneralLinearGroupElement aDeformation) {
    if(getDimension() == aDeformation.getDimension() &&
      getNumberClass() == aDeformation.getEntryClass() ) {
      for (int i=1; i<= getDimension(); i++)
        m_linearMatrixRepresentation.setColumnVector(i,aDeformation.getMatrixRef().getColumnVector(i));
    }
    else
      throw new IllegalArgumentException("aDeformation must be of same dimension and entry class as this AffineGroupElement");
    return this;
  }

  public AffineGroupElement translate(NumberTuple translationVector){
    for (int i=1; i<= getDimension(); i++)
      m_linearMatrixRepresentation.getEntryAsNumberRef(i,getDimension()+1).add(translationVector.getEntryRef(i));
    return this;
  }

  /**
   * Let this affine group element be A = t(v)*D and let D(angle) be the linear
   * mapping realising the rotation around <it>angle</it> then here A becomes
   * the mapping A = t(v)*D*D(angle). The angle is treated in radiant.
   */
  public AffineGroupElement rotate(MNumber angle) {
    throw new TodoException("currently only implemented for dimension 2");
  }

  /**
   * Rotation in 3D Space
   */
  public AffineGroupElement rotate(NumberTuple axis, MNumber angle) {
    if(angle.isZero() || axis.isZero())
      return this;
    // normalize axis-vector
    axis.multWithNumber(axis.standardNorm().inverted());

    NumberMatrix outerProd = axis.outerProduct(axis);
    NumberMatrix id = new NumberMatrix(getNumberClass(), 3, 3);
    NumberMatrix s = new NumberMatrix(getNumberClass(), 3, 3);
    s.setEntryRef(1,2, axis.getEntry(3).negate());
    s.setEntryRef(1,3, axis.getEntry(2));
    s.setEntryRef(2,1, axis.getEntry(3));
    s.setEntryRef(2,3, axis.getEntry(1).negate());
    s.setEntryRef(3,1, axis.getEntry(2).negate());
    s.setEntryRef(3,2, axis.getEntry(1));
    id.setToIdentity();
    // calculate the 3x3 matrix for the rotation and store the result in outerProd:
    outerProd.addTo((id.subFrom(outerProd)).multWithNumber(angle.copy().cos())
        .addTo(s.multWithNumber(angle.sin())));

    // now copy the result in a 4x4 matrix with homogenous coordinates and apply
    // the matrix on our linear Matrix representation
    NumberMatrix result = new NumberMatrix(getNumberClass(), 4, 4);

    for(int i=1; i<= getDimension(); i++)
      for(int j=1; j<= getDimension(); j++){
        result.setEntryRef(i,j,(MNumber)outerProd.getEntryRef(i,j));
		// set entries which are lesser than EPSILON to zero
		if(Math.abs(((MNumber)result.getEntryRef(i,j)).getDouble()) < EPSILON)
		  ((MNumber)result.getEntryRef(i,j)).setDouble(0);
      }
    MNumber one = NumberFactory.newInstance(getNumberClass());
    one.setDouble(1);

    result.setEntryRef(4, 4 , one);
    m_linearMatrixRepresentation = result.mult(m_linearMatrixRepresentation);
    return this;
  }

  public AffineGroupElement uniformScale(MNumber scaling) {
    // dirty solution, but we want do not want to change the translation:
    NumberTuple t = getTranslation();
    NumberMatrix tmp = new NumberMatrix(getNumberClass(),getDimension()+1,getDimension()+1);
    tmp.setToIdentity();
    tmp.multWithNumber(scaling);
    tmp.getEntryAsNumberRef(getDimension()+1,getDimension()+1).setDouble(1);
    tmp.mult(m_linearMatrixRepresentation);
    m_linearMatrixRepresentation.copyFrom(tmp);
    // reconstruct old translation:
    setTranslation(t);
    return this;
  }

  public AffineGroupElement uniformScale(double aScaling) {
    return uniformScale(NumberFactory.newInstance(getNumberClass(), aScaling));
  }

  public AffineGroupElement setTo(AffineGroupElement aAffineGroupElement) {
    m_linearMatrixRepresentation = aAffineGroupElement.m_linearMatrixRepresentation.deepCopy();
    return this;
  }

  public AffineGroupElement deepCopy() {
    return new AffineGroupElement(this);
  }


  /**
   * Returns the "linearized" matrix representation of this AffineGroupElement,
   * i.e. for a two dimensional AffineGroupElement a 3x3 Matrix would be returned.
   * the last row of the matrix consists of zeros with a final one.
   */
  public NumberMatrix getLinearMatrixRepresentation() {
    return m_linearMatrixRepresentation.deepCopy();
  }

  public NumberMatrix getLinearMatrixRepresentationRef() {
    return m_linearMatrixRepresentation;
  }

  private boolean isValidCoordinateIndex(int index) {
    return 1<=index && index <= getDimension();
  }

  private void adjustDeformationPart() {
    for(int i=1; i<=getDimension(); i++)
      m_deformationPart.setColumnVector(i,m_linearMatrixRepresentation.getColumnVector(i));
  }
}


