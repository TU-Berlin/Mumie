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

package net.mumie.mathletfactory.mmobject.set;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.math.set.NumberSetIF;
import net.mumie.mathletfactory.math.set.NumberTupelSetIF;
import net.mumie.mathletfactory.mmobject.MMDefaultCanvasObject;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 *  This class represents the basic functionality of a set.
 *  Subclasses must override the {@link net.mumie.mathletfactory.math.set.NumberSetIF#contains(double)} and {@link net.mumie.mathletfactory.math.set.NumberSetIF#contains(net.mumie.mathletfactory.math.number.MNumber)} methods which return
 *  <code>false</code> by default.
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */

public abstract class MMSet extends MMDefaultCanvasObject implements NumberSetIF, NumberTupelSetIF  {

  /** The constant for a standard 2-dimensional set. */
  public final static int STD_ONE_DIMENSIONAL_SET = 1;

  /** The constant for a standard 2-dimensional set. */
  public final static int STD_TWO_DIMENSIONAL_SET = 2;

  /** The constant for a standard 3-dimensional set. */
  public final static int STD_THREE_DIMENSIONAL_SET = 3;

  private int m_stdDimension = 0;


  /** Copy constructor */
  public MMSet(){
    setDisplayProperties(new DisplayProperties());
  }

  /**
   * Sets or removes for this set a standard dimension. Values of
   * <code>stdDimension</code> must be zero or one of
   * <code>STD_xxx_DIMENSIONAL_SET</code>. Zero means, no
   * standard dimension is set.
   *
   * If the standard dimension is one of the <code>STD_xxx_DIMENSIONAL_SET</code> values,
   * {@link #getUsedVariablesArray} returns <code>{x,y}</code> or
   * <code>{x,y,z}</code>, no matter which value the same method of
   * {@link #m_relation} returns.
   */
  public void setStdDimension(int stdDimension){
    if(stdDimension > 3  || stdDimension < 0)
      throw new IllegalArgumentException("argument must be in range 0..3");
    m_stdDimension = stdDimension;
  }

  /**
   * Returns a standard dimension for this set.
   * @see #setStdDimension
   */
  public int getStdDimension(){
    return m_stdDimension;
  }

	public int getDefaultTransformType() {
		return GeneralTransformer.SET_TRANSFORM;
	}

	public int getDefaultTransformTypeInCanvas() {
		return GeneralTransformer.NUMBERSET2D_TRANSFORM;
	}
}
