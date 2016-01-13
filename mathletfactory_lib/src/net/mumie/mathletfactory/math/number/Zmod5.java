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

import net.mumie.mathletfactory.action.message.IllegalUsageException;
import net.mumie.mathletfactory.action.message.TodoException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * The number class that represents the field Z/5Z.
 *
 * @author Vossbeck, Paehler
 * @mm.docstatus finished
 */
public class Zmod5 extends MRealNumber {


  private int m_infinityStatus;

  private int m_value;// stores the value of the number, is element of
  // 0,1, ..., m_prime-1

  private int m_prime = 5;// the prime p the field is based on


  /** Sets the value of this object to zero. */
  public Zmod5() {
    m_value = 0;
    m_infinityStatus = 1000;//number out of range for attributes in MNumber
  }

  /** Sets the value of this object to the <code>aValue</code> mod 5. */
  public Zmod5(double aValue) {
    setDouble(aValue);
  }

  public MNumber add(MNumber aNumber) {
    m_value = ( m_value +((Zmod5)aNumber).m_value )% m_prime;
    return this;
  }

  public MNumber negate() {
    m_value = (-m_value+m_prime)%m_prime;
    return this;
  }

  public MNumber mult(MNumber aNumber) {
    m_value = (m_value*((Zmod5)aNumber).m_value)%m_prime;
    return this;
  }

  public MNumber mult(MNumber a, MNumber b) {
    m_value = (((Zmod5)a).m_value + ((Zmod5)b).m_value)%m_prime;
    return this;
  }

  // override method inherited from MNumber, because there it is based
  // on method divBy:
  public MNumber inverse() {
    if (m_value == 0 )
      throw new IllegalUsageException("zero cannot be inverted");
    else if (m_value == 1)
      ;// 1 is the inverse of itself
    else if (m_value == 2)
      m_value = 3;
    else if (m_value == 3)
      m_value = 2;
    else if (m_value == 4)
      ;//4 is the inverse of itself
    return this;
  }

  public MNumber div(MNumber aNumber) {
    if ( aNumber.isZero() ){
      m_value = 0;
      m_infinityStatus = POSITIVE_INFINITY;
      return this;
    }
    else
      return mult(aNumber.inverted());
  }


  public boolean equals(MNumber aNumber) {
    return m_value==((Zmod5)aNumber).m_value;
  }

  public MNumber power(MNumber exponent) {
    throw new TodoException();
  }

  public MNumber create() {
    return new Zmod5();
  }

  public boolean isZero() {
    return m_value==0;
  }

  public MNumber conjugate() {
    throw new TodoException();
  }



  public boolean lessThan(MNumber aNumber) {
    throw new TodoException();
  }



  public MNumber copy() {
    Zmod5 tmp = new Zmod5();
    return tmp.setDouble(m_value);
  }

  public MNumber set(MNumber aNumber) {
    m_value = ((Zmod5)aNumber).m_value;
    return this;
  }

  public boolean lessOrEqualThan(MNumber aNumber) {
    throw new TodoException();
  }



  public boolean greaterThan(MNumber aNumber) {
    throw new TodoException();
  }

  public boolean greaterOrEqualThan(MNumber aNumber) {
    throw new TodoException();
  }



  public String toString() {
    return Integer.toString(m_value);
  }

  public MNumber setDouble(double aValue) {
    int tmp =  (int) Math.round(aValue);
    tmp = tmp%m_prime;
    if ( tmp < 0 )
      m_value = tmp+m_prime;

    else
      m_value = tmp;
    return this;
  }

  public String getDomainString(){
    return "\u2124/5\u2124";
  }

  public double getDouble() {
    if(!(m_infinityStatus == INFINITY))
      return (double)m_value;
    else if(m_infinityStatus == POSITIVE_INFINITY)
      return Double.POSITIVE_INFINITY;
    else if(m_infinityStatus == NEGATIVE_INFINITY)
      return Double.NEGATIVE_INFINITY;
    else
      throw new Error("wrong infinity status occured in "+getClass().getName());
  }

  public boolean isPosInfinity() {
    return m_infinityStatus == MRealNumber.POSITIVE_INFINITY;
  }

  public boolean isNegInfinity() {
    return false;
  }

  public boolean isNaN(){
    return false;
  }

  public boolean isRational(){
    return true;
  }

  public Node getMathMLNode() {
    return null;
  }

  public Node getMathMLNode(Document doc) {
    return null;
  }

}

