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

import net.mumie.mathletfactory.action.handler.MMHandler;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.algebra.linalg.NumberVector;

/**
 *  This class represents the default R<sup>2</sup> as vector space.
 *  @see MMDefaultR2Vector
 * 
 *  @author vossbeck
 *  @mm.docstatus finished
 */
public class MMDefaultR2 extends MMDefaultRN {

  /**
   * Class constructor which creates a vectorspace K<sup>2</sup> over a field K, where 
   * K may be any of the number types <code>numberClass</code> inheriting from MNumber.
   */
	public MMDefaultR2(Class entryClass) {
		super(entryClass, 2);
	}

  /**
   * Returns a new {@link MMDefaultR2Vector} from a given <code>NumberTuple</code> 
   * as coordinates with respect to the current basis.
   */ 
	public MMDefaultR2Vector getNewMMVectorFromDefaultCoordinates(NumberTuple absoluteCoords) {
		return getNewMMVectorFromDefaultCoordinates(absoluteCoords,null);
	}

  /**
   * Returns a new {@link MMDefaultR2Vector} from a given <code>NumberTuple</code> 
   * as coordinates with respect to the current basis and associates the specified handlers
   * <code>listOfHandlers</code> to the MMObject.
   */ 
	public MMDefaultR2Vector getNewMMVectorFromDefaultCoordinates(
		NumberTuple absoluteCoords,
		MMHandler[] listOfHandlers) {
		MMDefaultR2Vector v =
			new MMDefaultR2Vector(
				(NumberVector) super.getNewFromCoordinates(absoluteCoords));
		if(listOfHandlers != null){
			for(int i=0; i<listOfHandlers.length; i++)
				v.addHandler(listOfHandlers[i]);
		}
		return v;
	}

  /**
   * Returns a new {@link MMDefaultR2Vector} with the coordinates (0, 0).
   */ 
	public MMDefaultR2Vector getNewFromDefaultCoordinates() {
    return getNewFromDefaultCoordinates(0, 0);
	}
  
  /**
   * Returns a new {@link MMDefaultR2Vector} with the given coordinates 
   * <code>x</code> and <code>y</code> with respect to the current basis.
   */ 
  public MMDefaultR2Vector getNewFromDefaultCoordinates(double x, double y) {
    NumberTuple absoluteCoords = new NumberTuple(getNumberClass(), x, y);
    return getNewMMVectorFromDefaultCoordinates(absoluteCoords);
  } 

  /**
   * Returns a new {@link MMDefaultR2Vector} from a given <code>NumberTuple</code> as coordinates.
   */ 
  public NumberVector getNewFromCoordinates(NumberTuple coordinates) {
		//use the method inherited from class NumberVector:
		NumberVector v =
			(NumberVector) super.getNewFromCoordinates(coordinates);
		// constructor will work properly, because this space is really 2D default:
		MMDefaultR2Vector mmv = new MMDefaultR2Vector(v);
		return mmv;
	}

  /**
   * Returns a new {@link MMDefaultR2Vector} with the given coordinates 
   * <code>x</code> and <code>y</code>.
   */ 
  public NumberVector getNewFromCoordinates(double x, double y) {
    // use method inherited from class NumberVector:
    NumberVector v = (NumberVector) super.getNewFromCoordinates(new NumberTuple(getNumberClass(), x, y));
    return new MMDefaultR2Vector(v);
  }
}
