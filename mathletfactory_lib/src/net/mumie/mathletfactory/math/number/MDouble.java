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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import net.mumie.mathletfactory.mmobject.number.MMDouble;
import net.mumie.mathletfactory.util.xml.ExerciseObjectFactory;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * The number class that represents IEEE double precision real numbers.
 *
 * @author Vossbeck, Paehler
 * @mm.docstatus finished
 */
public class MDouble extends MRealNumber {

  // for getting the basis and the exponent:
  // the ieee754-double format has a a mantisse of 53 bit 2^52 = 4.5 * 10^15
  // =>the basis has a 15 digit precision
  private NumberFormat m_formatter;

  public final static String DEFAULT_PATTERN = new String("#.##");

  private static final double EPSILON = 1e-15;

  private double m_number; //the data field for this MDouble


  static int count = 0;
  boolean debug = false;

  /** Creates a zero number. */
  public MDouble() {
    m_number = 0.0;
    if(debug) {
      count++;
      System.out.println("d: "+count);
    }
  }

  /** Creates a number with the given value. */
  public MDouble(double number) {
    if(number == Double.POSITIVE_INFINITY)
      setPosInfinity();
    else if(number == Double.NEGATIVE_INFINITY)
      setNegInfinity();
    else
      setNotInfinity();
    m_number = number;
    if(debug) {
      count++;
      System.out.println("d: "+count);
    }
  }

  /** Copy constructor. */
  public MDouble(MDouble number) {
  	this(number.getDouble());
  }

	/**
	 * Constructs a new instance with the given MathML/XML content.
	 * @param mathMLnode a MathML/XML node
	 */
  public MDouble(Node mathMLnode) {
  	setMathMLNode(mathMLnode);
  }
  
  public MNumber add(MNumber aNumber) {
    m_number += ((MDouble)aNumber).m_number;
    return this;
  }

  public MNumber sub(MNumber aNumber) {
    m_number -= ((MDouble)aNumber).m_number;
    return this;
  }

  public MNumber mult(MNumber aNumber) {
    m_number *= ((MDouble)aNumber).m_number;
    return this;
  }


  public MNumber mult(MNumber multiplicand, MNumber multiplier) {
    m_number = ((MDouble)multiplicand).m_number * ((MDouble)multiplier).m_number;
    return this;
  }

  public MNumber div(MNumber aNumber) throws ArithmeticException {
    m_number /= ((MDouble)aNumber).m_number;
    return this;
  }

  public MNumber inverse() {
    m_number = 1./m_number;
    return this;
  }

  public MNumber power(MNumber exponent) {
    return setDouble(Math.pow(getDouble(), exponent.getDouble()));
  }

  public MNumber abs() {
    if(m_number<0)
      return negate();
    else
      return this;
  }

  public MNumber negate() {
    m_number = -m_number;
    return this;
  }

  public boolean isZero() {
    return Math.abs(m_number) <= EPSILON;
  }

  public boolean isNegInfinity(){
    return (m_number == Double.NEGATIVE_INFINITY);
  }

  public boolean isPosInfinity(){
    return (m_number == Double.POSITIVE_INFINITY);
  }

  public void setPosInfinity(){
    super.setPosInfinity();
    m_number = Double.POSITIVE_INFINITY;
  }

  public void setNegInfinity(){
    super.setNegInfinity();
    m_number = Double.NEGATIVE_INFINITY;
  }

  public boolean isNaN(){
    setAttribute(NaN, Double.isNaN(m_number));
    return getAttribute(NaN);
  }

  public void setNaN(){
    super.setNaN();
    m_number = Double.NaN;
  }

  public boolean isRational(){
    if(Double.isNaN(m_number))
      return false;
    if(new MRational(m_number).getDouble() == m_number)
      return true;
    return false;
  }

  /** Beware that this method returns true for same positive or negative infinity values! */
  public boolean equals(MNumber aNumber) {
    // check for infinity
    if(isPosInfinity())
      if(((MDouble)aNumber).isPosInfinity())
        return true;
      else
        return false;
    if(isNegInfinity())
      if(((MDouble)aNumber).isNegInfinity())
        return true;
      else
        return false;
    if ( aNumber instanceof MDouble )
      return Math.abs(m_number - ((MDouble)aNumber).m_number) <= EPSILON;
    else
      throw new IllegalArgumentException("must be an MDouble to compare");
  }

  public boolean lessThan(MNumber aNumber) {
    return m_number < ((MDouble)aNumber).m_number + EPSILON;
  }

  public boolean lessOrEqualThan(MNumber aNumber) {
    return m_number <= ((MDouble)aNumber).m_number + EPSILON;
  }

  public boolean greaterThan(MNumber aNumber) {
    return m_number > ((MDouble)aNumber).m_number - EPSILON;
  }

  public boolean greaterOrEqualThan(MNumber aNumber) {
    return m_number >= ((MDouble)aNumber).m_number - EPSILON;
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

    m_number = aNumber;
    return this;
  }

  public MNumber set(MNumber number) {
    if (getClass().isAssignableFrom(number.getClass())) {
      setDouble( ((MDouble)number).m_number );
    } else {
      setDouble(number.getDouble());
    }
    return this;
  }

  public MNumber create() {
    return new MDouble();
  }

  public MNumber copy() {
    return new MDouble(m_number);
  }


  public String toString() {
  	return toString(null);
  }

  public String toString(DecimalFormat format) {

  	if (Double.NEGATIVE_INFINITY == m_number) {
  		return "-Infinity";
  	} else if (Double.POSITIVE_INFINITY == m_number) {
  		return "Infinity"; 
  	} else if (Double.isNaN( m_number )) {
  		return "NaN";
  	}
  	
  	String retVal = null;
    if(format == null) {
    	retVal = Double.toString(m_number);
	    // remove trailing ".0" for integer numbers
	    retVal = retVal.replaceAll("\\.0$", "");
    } else {
    	retVal = format.format(m_number);
    }
    if(retVal.equals("-0")) {
      retVal="0";
    }
    return retVal;
  }

  /**
   * Method exponent
   * returns the base 10 exponent (lg(x)) as an integer
   * @return   an int
   *
   */
  public int exponent() {
  	if(m_formatter == null)
  		m_formatter = new DecimalFormat("#.###############E0");
    String number = m_formatter.format(m_number);
    //System.out.println(number);
    return Integer.parseInt(number.split("E")[1]);
  }

  /**
   *  Returns the "significand" of the MDouble: when its double value is
   *  a * 10^x with 1 &lt; a &lt; 10, a is returned.<br>
   *  The ieee754-double format has a a mantisse of 53 bit 2^52 = 4.5 * 10^15
   *  which means that the basis has a 15 digit precision.
   */
  public double mantisse(){
  	if(m_formatter == null)
  		m_formatter = new DecimalFormat("#.###############E0");
    String number = m_formatter.format(m_number);
    try {
      return (double)(m_formatter.parse(number.split("E")[0])).doubleValue();
    }catch(ParseException e){}
    return 0;
  }

  /**
   * Returns the class of the corresponding MM-Class for this number.
   */
  public Class getMMClass(){
    return MMDouble.class;
  }

  public Node getMathMLNode() {
    return getMathMLNode(XMLUtils.getDefaultDocument());
  }

  public Node getMathMLNode(Document doc) {
  	if(isEdited()) {
	    Element mn = ExerciseObjectFactory.createNode(this, "mn", doc);
	    mn.appendChild(doc.createTextNode(this.toString(new DecimalFormat(DEFAULT_PATTERN))));
	    return mn;
  	} else {
  		return ExerciseObjectFactory.createUneditedNode(this, doc);
  	}
  }


  /* IEEE double- Format is too complex to accomplish this by using the bitmap
   long bits = Double.doubleToLongBits(m_number);
   System.out.println(Long.toBinaryString(bits));
   // according to IEEE 754, exponent e resides in bits 52-62
   int e = (int) (bits >> 52);
   System.out.println(Long.toBinaryString(e)+ " = " +e );
   e &= 2047;
   System.out.println(Long.toBinaryString(e)+ " = " + e);
   e -= 1023;
   System.out.println(Long.toBinaryString(e));
   // number of (decimal) digits of fraction f
   int f = (int) bits & (0x);
   System.out.println(Long.toBinaryString(f));
   */

  public MNumber round(int accuracy) {
	  BigDecimal value = new BigDecimal(this.toString());
	  BigInteger temp = new BigInteger("0"); 
	  
	  value = value.multiply(new BigDecimal(Math.pow(10, accuracy)));
	  temp = value.toBigInteger();
	  if (temp.multiply(new BigInteger("10")).subtract(value.multiply(new BigDecimal(10)).toBigInteger()).abs().compareTo(new BigInteger("5")) >= 0) {
		  temp = temp.add(new BigInteger("1"));
	  }
	
	  value = new BigDecimal(temp);
	  value = value.movePointLeft(accuracy);
	  this.setDouble(value.doubleValue());
	  return this;
  }
  
  public MNumber rounded(int accuracy) {
	return ((MDouble)this.copy()).round(accuracy);
  }
	
  public boolean equals(MNumber aNumber, int accuracy) {
	return this.rounded(accuracy).equals(aNumber);
  }
  

  public static void main(String[] args){
    System.out.println(new MDouble(5).equals(new MDouble(5)));
    /*
    MDouble number = new MDouble(0.1415*Math.pow(10,40));
    System.out.println("mantisse("+number+") = " + number.mantisse());
    System.out.println("exponent("+number+") = " + number.exponent());
    */
   }

}


