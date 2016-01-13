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

import net.mumie.mathletfactory.mmobject.number.MMInteger;
import net.mumie.mathletfactory.util.xml.ExerciseObjectFactory;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * The number class that represents integer real numbers. Internally it uses
 * an IEEE double value.
 *
 * @author Vossbeck, Paehler
 * @mm.docstatus finished
 */
public class MInteger extends MRealNumber {

  private double m_number;// will be an int value in reality

  /** Sets the value of this object to zero. */
  public MInteger() {
    m_number = 0;
  }

  /** Sets the value of this object to the integer part of <code>aNumber</code>. */
  public MInteger(double number) {
    if(number == Double.POSITIVE_INFINITY)
      setAttribute(POSITIVE_INFINITY, true);//m_isInfinity = IS_POS_INFINITY;
    else if(number == Double.NEGATIVE_INFINITY)
      setAttribute(NEGATIVE_INFINITY, true);//m_isInfinity = IS_NEG_INFINITY;
    else
      setAttribute(INFINITY, false);//m_isInfinity = IS_NOT_INFINITY;
    m_number = (int)number;
  }

  /** Creates a number with the given value. */
  public MInteger(MInteger number) {
    this(number.getDouble());
  }

	/**
	 * Constructs a new instance with the given MathML/XML content.
	 * @param content a MathML/XML node
	 */
  public MInteger(Node content) {
    setMathMLNode(content);
  }

  public MNumber add(MNumber aNumber) {
    m_number += ((MInteger)aNumber).m_number;
    return this;
  }

  public MNumber sub(MNumber aNumber) {
    m_number -= ((MInteger)aNumber).m_number;
    return this;
  }

  public MNumber mult(MNumber aNumber) {
    m_number *= ((MInteger)aNumber).m_number;
    return this;
  }

  public MNumber div(MNumber aNumber) throws ArithmeticException {
//    int number;
    if (!aNumber.isZero())
      m_number = (int)m_number / (int)((MInteger)aNumber).m_number;
    else
      m_number /= ((MInteger)aNumber).m_number;

    return this;
  }

  public MNumber mult(MNumber multiplicand, MNumber multiplier) {
    m_number = ((MInteger)multiplicand).m_number * ((MInteger)multiplier).m_number;
    return this;
  }

  public MNumber power(MNumber exponent) {
    return setDouble(Math.pow(getDouble(), exponent.getDouble()));
  }

  public MNumber negate() {
    m_number = -m_number;
    return this;
  }

  public boolean isZero() {
    return m_number == 0;
  }

  public void setPosInfinity(){
    super.setPosInfinity();
    m_number = Integer.MAX_VALUE;
  }

  public void setNegInfinity(){
    super.setNegInfinity();
    m_number = Integer.MIN_VALUE;
  }

  public boolean isNaN(){
    return false;
  }

  public boolean isRational(){
    return true;
  }

  public boolean equals(MNumber aNumber) {
    if( aNumber instanceof MInteger )
      return m_number == ((MInteger)aNumber).m_number;
    else
      throw new IllegalArgumentException("compare to MInteger only");
  }

  public boolean lessThan(MNumber aNumber) {
    return m_number < ((MInteger)aNumber).m_number;
  }

  public boolean lessOrEqualThan(MNumber aNumber) {
    return m_number <= ((MInteger)aNumber).m_number;
  }

  public boolean greaterThan(MNumber aNumber) {
    return m_number > ((MInteger)aNumber).m_number;
  }

  public boolean greaterOrEqualThan(MNumber aNumber) {
    return m_number >= ((MInteger)aNumber).m_number;
  }

  public double getDouble() {
    if(isPosInfinity())
      return Double.POSITIVE_INFINITY;
    if(isNegInfinity())
      return Double.NEGATIVE_INFINITY;
    return m_number;
  }

  public MNumber setDouble(double aNumber) {
    if(aNumber == Double.POSITIVE_INFINITY)
      setPosInfinity();
    else if(aNumber == Double.NEGATIVE_INFINITY)
      setNegInfinity();
    else
      setNotInfinity();
    m_number = (int)aNumber;
    return this;
  }

  public MNumber setInteger(int value) {
	setDouble(value);
	return this;
  }
  
  public MNumber set(MNumber number) {
    if (getClass().isAssignableFrom(number.getClass())) {
      m_number = ((MInteger)number).m_number;
    } else {
      setDouble(number.getDouble());
    }
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

//  public void setMathMLNode(Node node) {
//    if(!node.getNodeName().equalsIgnoreCase("mn"))
//      throw new NumberFormatException("Node name must be mn");
//    NodeList list = node.getChildNodes();
//    for(int i = 0; i < list.getLength(); i++) {
//      Node n = list.item(i);
//      setDouble(Integer.parseInt(node.getChildNodes().item(i).getNodeValue()));
//    }
//  }

  public MNumber create() {
    return new MInteger();
  }

  public MNumber copy() {
    return new MInteger((int)m_number);
  }

  public String toString() {
  	//TODO: is critical, because we also allow to store infinite values
    return Integer.toString((int)m_number);
  }

  public String getDomainString(){
    return "\u2124";
  }

  /**
   * Returns the value of this number as an <code>int</code>.
   */
  public int getIntValue(){
    return (int)m_number;
  }

  /**
   * Returns the class of the corresponding MM-Class for this number.
   */
  public Class getMMClass(){
    return MMInteger.class;
  }
}
