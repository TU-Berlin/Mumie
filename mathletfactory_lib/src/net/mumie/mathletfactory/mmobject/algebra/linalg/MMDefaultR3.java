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

import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.algebra.linalg.NumberVector;

/**
 *  This class represents the default R<sup>3</sup> as vector space.
 *  @see MMDefaultR3Vector
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class MMDefaultR3 extends MMDefaultRN {

  /**
   * Constructs the vectorspace above the given number class.
   */
	public MMDefaultR3(Class numberClass) {
		super(numberClass, 3);
	}

  /**
   * Constructs the vectorspace above the given number class. The unit length
   * is used if the vector space is displayed with a coordinate system.
   */
  public MMDefaultR3(Class numberClass, double unitLength){
    this(numberClass);
    setGridInMath(unitLength);
  }

  /**
   * Returns a new {@link MMDefaultR3Vector} with the given coordinates.
   */ 
	public NumberVector getNewFromCoordinates(NumberTuple coordinates) {
		// use method inherited from class NumberVector:
		NumberVector v = (NumberVector) super.getNewFromCoordinates(coordinates);
		return new MMDefaultR3Vector(v);
	}

  /**
   * Returns a new {@link MMDefaultR3Vector} with the given coordinates.
   */ 
  public NumberVector getNewFromCoordinates(double x, double y, double z) {
    // use method inherited from class NumberVector:
    NumberVector v = (NumberVector) super.getNewFromCoordinates(new NumberTuple(getNumberClass(), x, y, z));
    return new MMDefaultR3Vector(v);
  }
}
