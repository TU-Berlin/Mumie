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

import net.mumie.mathletfactory.action.handler.MMHandler;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.algebra.linalg.NumberVector;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 *  This class represents a vector in the {@link MMDefaultR2}. 
 *  
 * @see MMDefaultR2
 * @author vossbeck
 * @mm.docstatus finished
 */
public class MMDefaultR2Vector extends MMDefaultRNVector {
  
  /**
   * Constructs this vector from a given vector space and with the given
   * coordinates.
   */
  protected MMDefaultR2Vector(NumberTuple coefficients, MMDefaultR2 aSpace) {
    super(coefficients, aSpace);
  }
  
  /**
   * Copy constructor.
   * @see net.mumie.mathletfactory.math.algebra.linalg.NumberVector#NumberVector(NumberVector)
   */
  protected MMDefaultR2Vector(NumberTuple coefficients,
                              MMDefaultR2 aSpace,
                              MMHandler[] handlers) {
    this(coefficients,aSpace);
    m_rootingPoint = new NumberTuple(getNumberClass(), 2);
    for(int i=0; i<handlers.length; i++)
      addHandler(handlers[i]);
  }
  
  protected MMDefaultR2Vector(NumberVector aNumberVector) {
    super(aNumberVector);
    m_rootingPoint = new NumberTuple(getNumberClass(), 2);    
  }
  
	/**
	 * Class constructor which constructs a <code>MMDefaultR2Vector</code> that is a deep copy of
	 * <code>vector</code>.
	 */
	public MMDefaultR2Vector(MMDefaultR2Vector vector) {
		super(vector);
		m_rootingPoint = new NumberTuple(getNumberClass(), 2);
	}
  
  public int getDefaultTransformType(){
    return GeneralTransformer.NUMBERVECTORSPACE_DEFAULT_TO_DEFAULTBASE_TRANSFORM;
  }
  
  public int getDefaultTransformTypeInCanvas() {
    return GeneralTransformer.NUMBERVECTORSPACE_DEFAULT_TO_DEFAULTBASE_TRANSFORM;
  }
  
  /**
   * Sets the coordinates of this <code>MMDefaultR2Vector</code> relative to the actual basis
   * to <code>x</code> and <code>y</code>.
   */
  public void setCoordinates(double x, double y){
    NumberTuple coords = getCoordinates();
    coords.setEntry(1,x);
    coords.setEntry(2,y);
    setCoordinates(coords);
  }
  
  /**
   * Sets the coordinates of this <code>MMDefaultR2Vector</code> relative to the actual basis
   * to <code>x</code> and <code>y</code>, where <code>x</code> and <code>y</code>
   * are {@link net.mumie.mathletfactory.math.number.MNumber}s.
   */
  public void setCoordinates(MNumber x, MNumber y){
    NumberTuple coords = getCoordinates();
    coords.setEntry(1,x);
    coords.setEntry(2,y);
    setCoordinates(coords);    
  }

  /**
   * Sets the coordinates of this <code>MMDefaultR2Vector</code> relative to the default basis
   * to <code>x</code> and <code>y</code>.
   */
  public void setDefaultCoordinates(double x, double y){
    NumberTuple coords = getCoordinates();
    coords.setEntry(1,x);
    coords.setEntry(2,y);
    setDefaultCoordinates(coords);
  }
  
  /**
   * Sets the coordinates of this <code>MMDefaultR2Vector</code> relative to the default basis
   * to <code>x</code> and <code>y</code>.
   */
  public void setDefaultCoordinates(MNumber x, MNumber y){
    NumberTuple coords = getCoordinates();
    coords.setEntry(1,x);
    coords.setEntry(2,y);
    setDefaultCoordinates(coords);    
  }

  /**
   *  Sets the origin of this vector. Note that this value is purely used for rendering and
   *  has no mathematical meaning.
   */    
  public void setRootingPoint(double x, double y){
    setRootingPoint(new NumberTuple(getNumberClass(), x,y));
  }
  
  /**
   * Returns a new instance of a <code>MMDefaultR2Vector</code> that has
   * the same coordinates as this vector.
   */
  public MMDefaultR2Vector copy() {
    return new MMDefaultR2Vector(this);
  }

  public Rectangle2D getWorldBoundingBox() {
    Rectangle2D result = new Rectangle2D.Double();
    NumberTuple tuple = getDefaultCoordinates();
    result.setRect(0, 0, tuple.getEntry(1).getDouble(), tuple.getEntry(2).getDouble());
    result.setFrame(
        result.getX() - result.getWidth() * .3, 
        result.getY() - result.getHeight() * .3, 
        result.getWidth() * 1.6, 
        result.getHeight() * 1.6);
    return result;
  }
}


