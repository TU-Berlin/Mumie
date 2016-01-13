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


import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.number.MNumber;


/**
 * This class represents a projective point of arbitrary dimension.
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */
public class ProjectivePoint extends ProjectiveSpace {
  
  
  /**
   * Creates a <code>ProjectivePoint</code> with the coordinate tuple
   * <code>projPointCoord</code> as projective (homogeneous) coordinates.
   */
  public ProjectivePoint(NumberTuple projPointCoord) {
    super(projPointCoord);
  }
  
  /**
   * Creates a <code>ProjectivePoint</code> living in a <code>externalDimension
   * </code> dimensional space, with coordinates based on numbers of class
   * type <code>entryClass</code>.
   * The projective coordinates will be (0,0,0,...,0,1).
   */
  public ProjectivePoint(Class entryClass, int externalDimension){
    // a ProjectivePoint is a 0-dimensional Projective Space:
    super (entryClass, 0, externalDimension);
  }
  
  /**
   * The projective space is equipped with the standard infinite plane
   * (being orthogonal to the last homogeneous coordinate.)
   * So we return the i-th coordinate of the projective point divided by its
   * last coordinate if this does not equal zero, otherwise, for the moment,
   * we throw an exception (the affine coordinate would be "infinity")
   */
  public MNumber getAffineCoordinate(int i){
    if ( isValidAffineCoordinateIndex(i) ){
      MNumber lastCoord = getProjectiveBasis()[0].getEntryRef(getEnvDimension()+1);
      if ( lastCoord.isZero() )
        throw new RuntimeException("last homogeneous coordinate is zero");
      else
        return getProjectiveBasis()[0].getEntry(i).div(lastCoord);
    }
    else{
      throw new IllegalArgumentException ("invalid coordinate index");
    }
  }
  
  
  /**
   * The projective space is equiped with the standard infinite plane
   * (being orthogonal to the last homogeneous coordinate.)
   * So we return the first to the pre last coordinate of the projective point divided by it's
   * last coordinate if this does not equal zero, otherwise, for the moment,
   * we throw an exception (the affine coordinates would be "infinity")
   */
  public NumberTuple getAffineCoordinates(){
    NumberTuple tmp = new NumberTuple(getNumberClass(), getEnvDimension());
    for (int i=1; i<=tmp.getDimension(); ++i){
      tmp.setEntryRef(i, getAffineCoordinate(i));
    }
    return tmp;
  }
  
  
  /**
   * The last (homogeneous) coordinate of this projective point will be set to
   * one (1), the other will be set to the affine coordinates.
   */
  public ProjectivePoint setAffineCoordinates(NumberTuple affineCoord){
    if ( affineCoord.getDimension() == getEnvDimension() ) {
      for (int i=1; i<=getEnvDimension(); ++i){
        getProjectiveCoordinates().setEntry(i,affineCoord.getEntryRef(i));
      }
      // also the last coordinate, indicating if the point is at infintiy or not,
      // must be reset:
      getProjectiveCoordinates().getEntryRef(getEnvDimension()+1).setDouble(1.0);
      return this;
    }
    else
      throw new IllegalArgumentException("the length of affine coordinates does" +
          "not suit to this projective point");
  }

  
  /**
   * returns the complete projective (homogeneous) coordinates of this
   * <code>ProjectivePoint</code>.
   */
  public NumberTuple getProjectiveCoordinates() {
    // for the projective point the basis of the underlying vectorspace
    // consists only of a single vector:
    return (NumberTuple)m_vectorspace.getBasisAsTuples()[0].deepCopy();
  }
  
  /**
   * sets the projective (homogeneous) coordinates of this <code>ProjectivePoint
   * </code> to the values of <code>projCoords</code>.
   */
  public ProjectivePoint setProjectiveCoordinates(NumberTuple projCoords) {
    if ( getEnvDimension()+1 == projCoords.getDimension() ){
      for (int i=1; i<=getEnvDimension(); i++)
        getProjectiveCoordinatesRef().setEntry(i,projCoords.getEntryRef(i));
    }
    else
      throw new IllegalArgumentException("dimension of projCoords does not suit to this point");
    return this;
    
  }

  /** Returns the projective coordinates of this point as reference. */
  private NumberTuple getProjectiveCoordinatesRef(){
    return m_vectorspace.getBasisAsTuples()[0];// only a single vector
                                                    // exists in this array.
  }

  /**
   * Returns whether this <code>ProjectivePoint</code> is infinite (i.e. the
   * last coordinate is zero) or not.
   */
  public boolean isInfinite() {
    return getProjectiveCoordinates().getEntry(getDimension()+1).isZero();
  }
  
}

