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


import java.util.logging.Logger;

import net.mumie.mathletfactory.math.algebra.GroupElementIF;
import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.geom.affine.AffinePoint;

/**
 * Implements the general linear group in N dimensions
 * and its standard representation in C^N together with its
 * projective representation in C^{N+1}.
 * the internal data structure is a square matrix of dimension N x N.
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */
public class RealGeneralLinearGroupElement implements GroupElementIF {

  private static Logger logger = Logger.getLogger(RealGeneralLinearGroupElement.class.getName());
  
  
  
  /** RealGeneralLinearGroup is based on a RealNumberSquareMatrix.
   * It has itsself to care that the matrix is always invertable.
   */
  private NumberMatrix m_matrixRepresentation;
  
  public RealGeneralLinearGroupElement(Class numberClass, int dimension){
    m_matrixRepresentation = new NumberMatrix(numberClass, dimension,new Object[dimension*dimension]);
    m_matrixRepresentation.setRealType(true);
    m_matrixRepresentation.setToIdentity();
  }
  
  public RealGeneralLinearGroupElement(NumberMatrix matrix){
    if(matrix.isInvertible())
      m_matrixRepresentation = matrix.deepCopy();
    else//should not be reached!
      throw new IllegalArgumentException("matrix must be invertible");
  }
  
  public Class getEntryClass() {
    return m_matrixRepresentation.getNumberClass();
  }
  
  /**
   *  Returns the dimension; that is N for GL(N)
   */
  public int getDimension() {
    return m_matrixRepresentation.getDimension();
  }
  
  /**
   */
  public NumberTuple applyToProjectively(NumberTuple projectiveVector) {
    // we expect at least an object of type "vector" as argument!
    // we also expect that the dimension of "vector" is one bigger than the
    // dimension of the RealGeneralLinearGroup
    if (projectiveVector.getDimension() != getDimension()+1 ){
      throw new IllegalArgumentException("dimension of vector must be one bigger than dimension of group element");
    } else
      // perhaps we change this to "zero-extension": we had to preserve the
      // last component of the projectiveVector then...
      return m_matrixRepresentation.applyTo(projectiveVector);
  }
  
  
  public NumberTuple applyTo(NumberTuple aVector) {
    if( ((NumberTuple) aVector).getDimension() != getDimension() ){
      throw new IllegalArgumentException("dimension of vector and group element must be the same");
    }
    else
      return m_matrixRepresentation.applyTo((NumberTuple)aVector);
  }
  
  public NumberTuple applyInvToProjectively(NumberTuple aVector) {
    // for the moment:
    logger.warning("NumberVectorSpace::getBasisForSpanOf:not yet fully implemented");
    return aVector;
  }
  
  
  /**
   */
  public boolean isEqual(GroupElementIF group) {
    if( group instanceof RealGeneralLinearGroupElement )
      return m_matrixRepresentation.equals(((RealGeneralLinearGroupElement)group).m_matrixRepresentation);
    return false;
  }
  
  /**
   */
  public boolean isIdentity() {
    return m_matrixRepresentation.isIdentity();
  }
  
  /**
   */
  public GroupElementIF composeWith(GroupElementIF group) {
    //this = this * group
    // schlecht:  man wuerde auch gerne mit OrthogonalGroup komponieren...
    if( group instanceof RealGeneralLinearGroupElement ) {
      m_matrixRepresentation.
      mult(((RealGeneralLinearGroupElement)group).m_matrixRepresentation);
      return this;
    }
    else
      throw new IllegalArgumentException(
        "RealGeneralLinearGroup can only be composed with element of type RealGeneralLinearGroup");
    
  }
  
  /**
   */
  public GroupElementIF inverse() {
    // this = this^-1
    m_matrixRepresentation.inverse();
    return this;
  }
  
  public GroupElementIF inverted() {
    return deepCopy().inverse();
  }
  
  protected boolean checkGLNRMatrix(NumberMatrix aMatrix) {
    return (getDimension() == aMatrix.getDimension()) && (aMatrix.isRealType());
  }
  
  public RealGeneralLinearGroupElement setMatrix(NumberMatrix aMatrix) {
    if (checkGLNRMatrix(aMatrix)) {
      m_matrixRepresentation = aMatrix.deepCopy();
      return this;
    }
    else
      throw new IllegalArgumentException("dimension of group and matrix must conicide");
  }

  
  public NumberMatrix getMatrix() {
    return m_matrixRepresentation.deepCopy();
  }
  
  public RealGeneralLinearGroupElement setMatrixRef(NumberMatrix aMatrix) {
    if (checkGLNRMatrix(aMatrix)) {
      m_matrixRepresentation = aMatrix;
      return this;
    } else
      throw new IllegalArgumentException("dimension of group and matrix must conicide");
  }

  public NumberMatrix getMatrixRef() {
    return m_matrixRepresentation;
  }
  
  public RealGeneralLinearGroupElement deepCopy() {
    // create an instance of GeneralLinearGroupElement with correct
    // entry type and dimension:
    RealGeneralLinearGroupElement copy = new RealGeneralLinearGroupElement(getEntryClass(),getDimension());
    // this group element is fully described by it's matrix representation:
    copy.m_matrixRepresentation = m_matrixRepresentation.deepCopy();
    return copy;
  }

  
  public RealGeneralLinearGroupElement beLinearMappingBetweenAffinePoints(
                                      AffinePoint[] preImages,
                                      AffinePoint[] images) {
  
    return this;
  }
  
}

