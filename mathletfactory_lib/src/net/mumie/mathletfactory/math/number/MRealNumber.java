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

package net.mumie.mathletfactory.math.number;

/**
 * @author amsel
 *
 * The base class for all real (non-complex) numbers.
 */
public abstract class MRealNumber extends MNumber {

//  /** This value of {@link #m_isInfinity} indicates that the number is negative infinity. */
//  public static final int IS_NEG_INFINITY = -1;
//
//  /** This value of {@link #m_isInfinity} indicates that the number is positive infinity. */
//  public static final int IS_POS_INFINITY = 1;

  public MNumber abs() {
    if (getDouble() < 0)
      return negate();
    return this;
  }

  /**
   * Returns true if this number is greater than every other number.
   */
  public boolean isPosInfinity() {
    return getAttribute(POSITIVE_INFINITY);//m_isInfinity == IS_POS_INFINITY;
  }

  /**
   * Returns true if this number is less than every other number.
   */
  public boolean isNegInfinity() {
    return getAttribute(NEGATIVE_INFINITY);//m_isInfinity == IS_NEG_INFINITY;
  }

  /** Sets this number to positive infinity. */
  public void setPosInfinity() {
    setAttribute(POSITIVE_INFINITY, true);//m_isInfinity = IS_POS_INFINITY;
  }

  /** Sets this number to negative infinity. */
  public void setNegInfinity() {
    setAttribute(NEGATIVE_INFINITY, true);//m_isInfinity = IS_NEG_INFINITY;
  }

  public void setInfinity() {
    setPosInfinity();
  }

  public boolean isInfinity(){
    return isPosInfinity() || isNegInfinity();
  }

  public static MRealNumber min(
    MRealNumber aNumber,
    MRealNumber aSecondNumber) {
    if (aNumber.lessOrEqualThan(aSecondNumber))
      return aNumber;
    else
      return aSecondNumber;
  }

  public static MRealNumber max(
    MRealNumber aNumber,
    MRealNumber aSecondNumber) {
    if (aNumber.lessOrEqualThan(aSecondNumber))
      return aSecondNumber;
    else
      return aNumber;
  }

  public String getDomainString() {
    return "\u211d";
  }

  /**
   * Since real numbers are invariant under conjugation. <code>this</code> is
   * returned.
   * @see MNumber#conjugate()
   */
  public MNumber conjugate() {
    return this;
  }
}
