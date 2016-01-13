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

package net.mumie.mathletfactory.mmobject.algebra.linalg;

import java.awt.geom.Rectangle2D;

import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.algebra.linalg.NumberVector;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.transformer.Canvas3DObjectTransformer;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 *  This class represents a vector in the {@link MMDefaultR3}. 
 *  @see MMDefaultR3
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class MMDefaultR3Vector extends MMDefaultRNVector {
  
  
  /**
   * Constructs this vector from a given vector space and with the given
   * coordinates.
   */
  protected MMDefaultR3Vector(NumberTuple coefficients, MMDefaultR3 aSpace) {
    super(coefficients, aSpace);
    m_rootingPoint = new NumberTuple(getNumberClass(), 3);
  }
  
  /**
   * Copy constructor.
   * @see net.mumie.mathletfactory.algebra.linalg.NumberVector#NumberVector(NumberVector)
   */
  protected MMDefaultR3Vector(NumberVector aNumberVector) {
    super(aNumberVector);
    m_rootingPoint = new NumberTuple(getNumberClass(), 3);
  }
    
  public int getDefaultTransformType(){
    return GeneralTransformer.NUMBERVECTORSPACE_DEFAULT_TO_DEFAULTBASE_TRANSFORM;
  }
  
  public int getDefaultTransformTypeInCanvas() {
    return GeneralTransformer.NUMBERVECTORSPACE_DEFAULT_TO_DEFAULTBASE_TRANSFORM;
  }
  
  /**
   * Sets the default coordinates of this vector. Additionally if 
   * <code>rebuild</code> is set to true 
   * {@link net.mumie.mathletfactory.transformer.Canvas3DObjectTransformer#updateFinished}
   * will be called.
   */
  public NumberVector setDefaultCoordinates(NumberTuple coords, boolean rebuild){
    super.setDefaultCoordinates(coords);
    if(rebuild)
      if((Canvas3DObjectTransformer)getCanvasTransformer() != null )
          ((Canvas3DObjectTransformer)getCanvasTransformer()).updateFinished();
    return this;
  }
  
  public NumberVector setDefaultCoordinates(NumberTuple coords){
    super.setDefaultCoordinates(coords);
    if((Canvas3DObjectTransformer)getCanvasTransformer() != null )
        ((Canvas3DObjectTransformer)getCanvasTransformer()).updateFinished();
    return this;
  }
  
  public NumberVector setCoordinate(int i, MNumber c) {
    super.setCoordinate(i,c);
    if((Canvas3DObjectTransformer)getCanvasTransformer() != null )
        ((Canvas3DObjectTransformer)getCanvasTransformer()).updateFinished();
    return this;
  }
  
  public NumberVector setCoordinates(NumberTuple coordsRelToBase) {
    super.setCoordinates(coordsRelToBase);
    if((Canvas3DObjectTransformer)getCanvasTransformer() != null )
        ((Canvas3DObjectTransformer)getCanvasTransformer()).updateFinished();
    return this;
  }
  
  public void setCoordinates(double x, double y, double z){
    NumberTuple coords = getCoordinates();
    coords.setEntry(1,x);
    coords.setEntry(2,y);
    coords.setEntry(3,z);
    setCoordinates(coords);
  }
  
  /**
   *  Sets the origin of this vector. Note that this value is purely used for rendering and
   *  has no mathematical meaning.
   */  
  public void setRootingPoint(double x, double y, double z){
    setRootingPoint(new NumberTuple(getNumberClass(), x,y,z));
  }
  
  /**
   *  returns the angle theta in spherical coordinates.
   */
  public MNumber getTheta() {
    // theta = acos( z/r )
    MNumber theta = getDefaultCoordinate(3).div(getDefaultCoordinatesRef().standardNorm());
    return theta.arccos();
  }
  
  /**
   *  returns the angle phi in spherical coordinates.
   */
  public MNumber getPhi() {
    // phi = atan( y/x )
    MNumber phi = getDefaultCoordinate(2).div(getDefaultCoordinate(1));
    return phi.arctan();
  }
  
  public Rectangle2D getWorldBoundingBox() {
    return null;
  }
  
  /** Returns a vector that is the cross product (this x aVector). */
  public MMDefaultR3Vector vectorProduct(MMDefaultR3Vector aVector){
    MMDefaultR3Vector result = (MMDefaultR3Vector)((MMDefaultR3)getVectorSpace()).getNewFromCoordinates(getCoordinates());
    result.setCoordinate(1, getCoordinate(2).mult(aVector.getCoordinate(3)).sub(getCoordinate(3).mult(aVector.getCoordinate(2))));
    result.setCoordinate(2, getCoordinate(3).mult(aVector.getCoordinate(1)).sub(getCoordinate(1).mult(aVector.getCoordinate(3))));
    result.setCoordinate(3, getCoordinate(1).mult(aVector.getCoordinate(2)).sub(getCoordinate(2).mult(aVector.getCoordinate(1))));    
    return result;    
  }
}


