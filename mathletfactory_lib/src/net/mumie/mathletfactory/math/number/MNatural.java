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

import java.math.BigInteger;

import net.mumie.mathletfactory.action.message.IllegalUsageException;
import net.mumie.mathletfactory.util.xml.ExerciseObjectFactory;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * The number class that represents natural numbers. Internally it uses a <code>BigInteger</code>.
 *
 * @author Amsel
 * @mm.docstatus finished
 */
public class MNatural extends MRealNumber {
  private BigInteger m_value;

  /** Sets the value of this object to zero. */
  public MNatural() {
    m_value = new BigInteger("0");
  }

  /** Sets the value of this object to the integer part of <code>aNumber</code>. */
  public MNatural(MNumber aNumber) {
    this();
    set(aNumber);
  }

  private void checkValue() {
    if (m_value.compareTo(BigInteger.ZERO) < 0)
      throw new IllegalArgumentException("Natural numbers can't be smaller than zero");
  }

  public boolean isPosInfinity() {
    return false;
  }

  public boolean isNegInfinity() {
    return false;
  }

  public void setPosInfinity(){
    super.setPosInfinity();
    m_value = new BigInteger(""+Double.MAX_VALUE);
  }

  public void setNegInfinity(){
    super.setNegInfinity();
    m_value = new BigInteger("-"+Double.MAX_VALUE);
  }

  public MNumber add(MNumber aNumber) {
    m_value = m_value.add(((MNatural)aNumber).m_value);
    return this;
  }

  public MNumber mult(MNumber aNumber) {
    m_value = m_value.multiply(((MNatural)aNumber).m_value);
    return this;
  }

  public MNumber mult(MNumber aNumber, MNumber aSecondNumber) {
    MNatural sum = (MNatural)aNumber.copy();
    sum.add(aSecondNumber);
    return sum;
  }

  public MNumber div(MNumber aNumber) {
    m_value = m_value.divide(((MNatural)aNumber).m_value);
    return this;
  }

  public MNumber power(MNumber exponent) {
    m_value = m_value.pow((int)exponent.getDouble());
    return this;
  }

  public MNumber negate() {
    throw new IllegalUsageException("Natural numbers can't be negated");
  }

  public boolean isZero() {
    return m_value.equals(BigInteger.ZERO);
  }

  public boolean equals(MNumber aNumber) {
    return m_value.equals(((MNatural)aNumber).m_value);
  }

  public boolean lessThan(MNumber aNumber) {
    return m_value.compareTo(((MNatural)aNumber).m_value) < 0;
  }

  public boolean lessOrEqualThan(MNumber aNumber) {
    return !(m_value.compareTo(((MNatural)aNumber).m_value) > 0);
  }

  public boolean greaterThan(MNumber aNumber) {
    return m_value.compareTo(((MNatural)aNumber).m_value) > 0;
  }

  public boolean greaterOrEqualThan(MNumber aNumber) {
    return !(m_value.compareTo(((MNatural)aNumber).m_value) < 0);
  }

  public boolean isNaN() {
    return false;
  }

  public boolean isRational() {
    return true;
  }

  public double getDouble() {
    if(isPosInfinity())
      return Double.POSITIVE_INFINITY;
    if(isNegInfinity())
      return Double.NEGATIVE_INFINITY;
    return m_value.doubleValue();
  }

  public MNumber setDouble(double aNumber) {
    if(aNumber == Double.POSITIVE_INFINITY)
      setPosInfinity();
    else if(aNumber == Double.NEGATIVE_INFINITY)
      setNegInfinity();
    else
      setNotInfinity();
    m_value = new BigInteger(new Integer((int)aNumber).toString());
    return this;
  }

  public MNumber create() {
    return new MNatural();
  }

  public MNumber copy() {
    return new MNatural(this);
  }

  public String toString() {
    return m_value.toString();
  }

  public MNumber set(MNumber aNumber) {

    if (aNumber instanceof MNatural) {
      MNatural number = (MNatural) aNumber;
      if(number.isPosInfinity())
        setPosInfinity();
      else if(number.isNegInfinity())
        setNegInfinity();
      else
        setNotInfinity();
      m_value = new BigInteger(((MNatural)aNumber).m_value.toByteArray());
    } else {
      setDouble(aNumber.getDouble());
    }
    checkValue();
    return this;
  }

  public Node getMathMLNode() {
    return getMathMLNode(XMLUtils.getDefaultDocument());
  }

  public Node getMathMLNode(Document doc) {
  	if(isEdited()) {
	    Element mn = ExerciseObjectFactory.createNode(this, "mn", doc);
	    mn.appendChild(doc.createTextNode(this.toString()));
	    return mn;
  	} else {
  		return ExerciseObjectFactory.createUneditedNode(this, doc);
  	}
  }

}
