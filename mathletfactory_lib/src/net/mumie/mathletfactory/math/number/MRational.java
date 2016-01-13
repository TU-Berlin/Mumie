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

import java.util.logging.Logger;

import net.mumie.mathletfactory.mmobject.number.MMRational;
import net.mumie.mathletfactory.util.xml.ExerciseObjectFactory;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Represents the rational numbers. An object of the class MRational
 * consists of the two fields numerator and denominator of type long. If the
 * absolute value of the numerator or denominator of an MRational gets to big,
 * this will cause an overflow without any warning of the compiler! The
 * absolute value of an MRational should therefore be <100000.
 * The class {@link net.mumie.mathletfactory.math.number.MBigRational}, an extension
 * using {@link java.math.BigInteger} instead of <code>long</code> avoids this
 * problem.
 *
 *
 * @author Mrose
 * @mm.docstatus finished
 * @see net.mumie.mathletfactory.math.number.MBigRational
 */
public class MRational extends MRealNumber{

  private long m_denominator, m_numerator;
  private static Logger logger = Logger.getLogger(MRational.class.getName());

  /**
   * Is used in method {@link #setDouble(double)} and determines the
   * precision of conversion of a <code>double</code> into a
   * <code>rational</code>. To avoid an overflow
   * the value of NUMBER_OF_DIGITS should be 6 or less.
   */
  public static final int NUMBER_OF_DIGITS = 6;

  static int count = 0;
  boolean debug = false;

  /**
   * Class constructor setting the rational to 0/1.
   */
  public MRational() {
    m_numerator = 0;
    m_denominator = 1;
    if(debug){
      count++;
      System.out.println("r: "+count);
    }
  }

  /**
   * Class constructor setting the rational to
   * <code>numerator</code>/<code>denominator</code>.
   */
  public MRational(long numerator, long denominator){
    checkDivisionByZero(numerator, denominator);
    m_numerator = numerator;
    m_denominator = denominator;
    if(debug){
      count++;
      System.out.println("r: "+count);
    }
  }

  /**
   * Class constructor setting the rational to the value of <code>aValue</code>.
   * It can only be used for <code>aValue</code> < 100000 and it works exactly
   * up to {@link #NUMBER_OF_DIGITS} post decimal positions.
   * Otherwise an approximate value is given.
   */
  public MRational(double aValue) {
    this();
    setDouble(aValue);
    if(debug){
      count++;
      System.out.println("r: "+count);
    }
  }

  public MRational(MNumber aValue){
    if(aValue instanceof MRational){
      m_denominator = ((MRational)aValue).m_denominator;
      m_numerator = ((MRational)aValue).m_numerator;
    } else
    setDouble(aValue.getDouble());
  }

	/**
	 * Constructs a new instance with the given MathML/XML content.
	 * @param content a MathML/XML node
	 */
	public MRational(Node content) {
		setMathMLNode(content);
	}

  /**
   * Returns the numerator of this rational.
   *
   * @return   a <code>long</code> holding the numerator.
   *
   */
  public long getNumerator() {
    return m_numerator;
  }

  /**
   * Sets the numerator of this rational.
   */
  public void setNumerator(long longVal) {
    m_numerator = longVal;
  }

  /**
   * Returns the denominator of this rational.
   *
   * @return   a <code>long</code> holding the denominator.
   *
   */
  public long getDenominator() {
    return m_denominator;
  }

  /**
   * Sets the denominator of this rational.
   */
  public void setDenominator(long longVal) {
    m_denominator = longVal;
  }

  private void checkDivisionByZero(MRational aRational) {
    if (aRational.getDenominator() == 0) {
      if (aRational.getNumerator() == 0) {
        logger.severe("Division of zero by zero not allowed!");
      }
      else if (aRational.getNumerator() > 0) {
        setPosInfinity();
      }
      else setNegInfinity();
    }
  }

  private void checkDivisionByZero(long numerator, long denominator) {
    if (denominator == 0) {
      if (numerator == 0) {
        logger.severe("Division of zero by zero not allowed!");//TODO
      }
      else if (numerator > 0) {
        setPosInfinity();
      }
      else setNegInfinity();
    }
  }

  /**
   * Returns the least common multiple of the absolute value of the two integers
   * <code>a</code> and <code>b</code>.
   * If one of the integers is zero, zero is returned. If both integers
   * are zero an exception is thrown.
   *
   * @param    a              a <code>long</code>
   * @param    b              a <code>long</code>
   *
   * @return   a <code>long</code> holding the least common multiple of the
   * absolute value of <code>a</code> and <code>b</code>.
   * @throws ArithmeticException if both <code>a</code> and <code>b</code> are zero.
   */
  public static long leastCommonMultiple(long a, long b) {
    if (a == 0 && b == 0)
      throw new ArithmeticException("LCM of zero and zero not defined!");
    else return Math.abs(a*(b/greatestCommonDivisor(a, b)));
  }

  /**
   * Returns the greatest common divisor of the absolute value of the two
   * integers <code>a</code> and <code>b</code>.
   * If one of the integers is zero the other one is returned. If both integers
   * are zero an exception is thrown.
   *
   * @param    a                   a <code>long</code>
   * @param    b                   a <code>long</code>
   *
   * @return   a <code>long</code> holding the greatest common divisor of the
   * absolute value of <code>a</code> and <code>b</code>.
   * @throws ArithmeticException if both <code>a</code> and <code>b</code> are zero.
   *
   */
  public static long greatestCommonDivisor(long a, long b) {
    long rest=1;
    if (a == 0 && b == 0)
      throw new ArithmeticException("GCD of zero and zero not defined!");
    else if (a == 0 && b != 0) return Math.abs(b);
    else if (b == 0 && a != 0) return Math.abs(a);
    else {
      a = Math.abs(a);
      b = Math.abs(b);
      while ( rest > 0) {
        rest = a % b;
        a = b;
        b = rest;
      }
      return a;
    }
  }

  /**
   * Cancels this rational down. The sign is set in the numerator. A rational
   * with numerator=0 is set to 0/1. A rational with denominator=0 is set to
   * 1/0 if the numerator is positive and to -1/0 if the numerator is negative.
   *
   * @return   a <code>MRational</code> holding this cancelled down rational.
   * @mm.sideeffects this is cancelled down.
   *
   */
  public MRational cancelDown() {
   // if(m_denominator == 1 || m_numerator == 1)
    if(m_denominator == 1 || (m_numerator == 1 && m_denominator >= 0))
      return this;
    checkDivisionByZero(this);
    if(m_numerator == 0 && m_denominator!=0){
      m_denominator = 1;
      return this;
    }
    if (isPosInfinity()) {
      m_numerator = 1;
      return this;
    }
    if (isNegInfinity()) {
      m_numerator = -1;
      return this;
    }
    long gcd = greatestCommonDivisor(m_numerator, m_denominator);
    m_numerator /= gcd;
    m_denominator /= gcd;
    if ((m_numerator/Math.abs(m_numerator))*(m_denominator/Math.abs(m_denominator)) >= 0)
      m_numerator = Math.abs(m_numerator);
    else m_numerator = - Math.abs(m_numerator);
    m_denominator = Math.abs(m_denominator);

    // if the number of digits in both numerator or denominator exceeds
    // NUMBER_OF_DIGITS, reduce them by dividing both with the difference
    int toMuchDigits = Math.min(numberOfDigits(m_numerator) - NUMBER_OF_DIGITS,
                                numberOfDigits(m_denominator) - NUMBER_OF_DIGITS);
    if( toMuchDigits > 0){
      m_numerator /= Math.pow(10, toMuchDigits);
      m_denominator /= Math.pow(10, toMuchDigits);
    }
    return this;
  }

  /**
   * Adds this rational to <code>aNumber</code>. The result is cancelled down.
   *
   * @param    aNumber             a  <code>MNumber</code>
   *
   * @return   a <code>MNumber</code> holding the sum of this rational and
   * <code>aNumber</code>.
   * @mm.sideeffects <code>aNumber</code> is added to this.
   *
   */
  public MNumber add(MNumber aNumber) {
    checkDivisionByZero((MRational)aNumber);
    if (this.isPosInfinity()) {
      if (((MRational)aNumber).isNegInfinity()){
        logger.severe("Addition of +infinity and -infinity not defined!");
        return null;//TODO
      }
      else {
        m_numerator = 1;
        return this;
      }
    }
    else if (this.isNegInfinity()) {
      if (((MRational)aNumber).isPosInfinity()){
        logger.severe("Addition of +infinity and -infinity not defined!");
        return null;//TODO
      }
      else {
        m_numerator = -1;
        return this;
      }
    }
    else if (((MRational)aNumber).isPosInfinity()) {
      m_numerator = 1;
      m_denominator = 0;
      return this;
    }
    else if (((MRational)aNumber).isNegInfinity()) {
      m_numerator = -1;
      m_denominator = 0;
      return this;
    }
    else {
      long lcm = leastCommonMultiple(m_denominator,
                                       ((MRational)aNumber).m_denominator);
      m_numerator = m_numerator*(lcm/m_denominator)
        + ((MRational)aNumber).m_numerator*(lcm/
                                               ((MRational)aNumber).m_denominator);
      m_denominator = lcm;
      return cancelDown();
    }
  }

  /**
   * Subtracts <code>aNumber</code> from this rational. The result is cancelled down.
   *
   * @param    aNumber             a  <code>MNumber</code>
   *
   * @return   a <code>MNumber</code> holding the difference between this
   * rational and <code>aNumber</code>.
   * @mm.sideeffects <code>aNumber</code> is subtracted from this.
   *
   */
  public MNumber sub(MNumber aNumber) {
    return this.add(new MRational(-1,1).mult(aNumber));
  }

  /**
   * Multiplies this rational with <code>aNumber</code>. The result is cancelled down.
   *
   * @param    aNumber             a  <code>MNumber</code>
   *
   * @return   a <code>MNumber</code> holding the product between this
   * rational and <code>aNumber</code>.
   * @mm.sideeffects this is multiplied with <code>aNumber</code>.
   */
  public MNumber mult(MNumber aNumber) {
    checkDivisionByZero((MRational)aNumber);
    checkDivisionByZero((MRational)this);
    m_numerator *= ((MRational)aNumber).m_numerator;
    m_denominator *= ((MRational)aNumber).m_denominator;
    //if(m_denominator == 0)
    //  System.out.println("div by Zero: "+aNumber);
    return cancelDown();
  }

  /**
   * Multiplies <code>multiplicand</code> with <code>multiplier</code> and
   * returns the result. The result is cancelled down.
   *
   * @param    multiplicand        a  <code>MNumber</code>
   * @param    multiplier          a  <code>MNumber</code>
   *
   * @return   a <code>MNumber</code> holding the product between
   * <code>multiplicand</code> and  <code>multiplier</code>.
   * @mm.sideeffects this is set to the product of <code>multiplicand</code>
   * and <code>multiplier</code>.
   *
   */
  public MNumber mult(MNumber multiplicand, MNumber multiplier) {
    m_numerator = ((MRational)multiplicand).m_numerator * ((MRational)multiplier).m_numerator;
    m_denominator = ((MRational)multiplicand).m_denominator * ((MRational)multiplier).m_denominator;
    return cancelDown();
  }

  /**
   * Divides this rational by <code>aNumber</code>. The result is cancelled down.
   *
   * @param    aNumber             a  <code>MNumber</code>
   *
   * @return   a <code>MNumber</code> holding the quotient of this rational and
   * <code>aNumber</code>.
   * @mm.sideeffects this is divided by <code>aNumber</code>.
   */
  public MNumber div(MNumber aNumber) {
    return this.mult( ((MRational)aNumber).inverted() );
  }

  /**
   * Changes a rational in its reciprocal. The result is not cancelled down.
   *
   * @return   a <code>MNumber</code> holding the inverse of this rational.
   * @mm.sideeffects this is set to its inverse.
   */
  public MNumber inverse() {
    long helper = this.m_numerator;
    m_numerator = m_denominator;
    m_denominator = helper;
    cancelDown();
    return this;
  }

  /**
   * Returns the reciprocal of this rational. This remains unchanged.
   *
   * @return   a <code>MNumber</code> holding the inverse of this rational.
   *
   */
  public MNumber inverted() {
    MRational result = new MRational(this.m_denominator,this.m_numerator);
    result.cancelDown();
    return result;
  }

  /**
   * Returns the sign of this rational. If the numerator is positive,
   * 1 is returned. Otherwise -1 is returned.
   *
   * @return   an <code>int</code> holding +/-1 depending on the sign of this
   * rational.
   *
   */
  public int signum(){
    return (m_numerator > 0 ? 1 : -1);
  }

  /**
   * Tests if this rational is equal to <code>aNumber</code>. The rationals are
   * cancelled down before comparison.
   *
   * @param    aNumber             a  <code>MNumber</code>
   *
   * @return   a <code>boolean</code> holding true if this rational is equal to
   * <code>aNumber</code> up to cancellation.
   *
   */
  public boolean equals(MNumber aNumber){
    if ( aNumber instanceof MRational ) {
      if (cancelDown().m_numerator ==
            ((MRational)aNumber).cancelDown().m_numerator
          && cancelDown().m_denominator ==
            ((MRational)aNumber).cancelDown().m_denominator)
        return true;
      return false;
    }
    else
      throw new IllegalArgumentException("compare to MRational only");
  }

  /**
   * Tests if the nominators and denominators of this rational and
   * <code>aNumber</code> are exactly the same. Rationals are not cancelled
   * down and the signs are important.
   *
   * @param    aNumber             a  <code>MNumber</code>
   *
   * @return   a <code>boolean</code> holding true if this rational is exactly
   * equal to <code>aNumber</code>.
   *
   */
  public boolean equalsExactly(MNumber aNumber){
    if (this.m_numerator == ((MRational)aNumber).m_numerator
        && this.m_denominator == ((MRational)aNumber).m_denominator)
      return true;
    return false;
  }

  /**
   * Returns true if this rational is less than <code>aNumber</code>.
   *
   * @param    aNumber             a  <code>MNumber</code>
   *
   * @return   a <code>boolean</code> which is true if this rational is less
   * than <code>aNumber</code>.
   *
   */
  public boolean lessThan(MNumber aNumber) {
    if ( isPosInfinity() && ((MRational)aNumber).isPosInfinity()  ||
        isNegInfinity() && ((MRational)aNumber).isNegInfinity() ) {
      throw new ArithmeticException("Comparison not possible!");
    }
    else if (isPosInfinity() ) {
      return false;
    }
    else if (isNegInfinity() ) {
      return true;
    }
    else if ( ((MRational)aNumber).isPosInfinity() ) {
      return true;
    }
    else if ( ((MRational)aNumber).isNegInfinity() ) {
      return false;
    }
    else {
      long lcm = leastCommonMultiple(m_denominator,
                                       ((MRational)aNumber).m_denominator);
      if (m_numerator*lcm/m_denominator
          < ((MRational)aNumber).m_numerator*lcm/
            ((MRational)aNumber).m_denominator)
        return true;
      else return false;
    }
  }

  /**
   * Returns true if this rational is less or equal than <code>aNumber</code>.
   *
   * @param    aNumber             a  <code>MNumber</code>
   *
   * @return   a <code>boolean</code> which is less or equal than <code>aNumber</code>.
   *
   */
  public boolean lessOrEqualThan(MNumber aNumber) {
    return ( this.lessThan(aNumber) || this.equals(aNumber) );
  }

  /**
   * Returns true if this rational is greater than <code>aNumber</code>.
   *
   * @param    aNumber             a  <code>MNumber
   *
   * @return   a <code>boolean</code> which is true if this rational is greater
   * than <code>aNumber</code>.
   *
   */
  public boolean greaterThan(MNumber aNumber) {
    return ( ! this.lessOrEqualThan(aNumber) );
  }

  /**
   * Returns true if this rational is greater or equal than <code>aNumber</code>.
   *
   * @param    aNumber             a  <code>MNumber</code>
   *
   * @return   a <code>boolean</code> which is true if this rational is greater
   * or equal than <code>aNumber</code>.
   *
   */
  public boolean greaterOrEqualThan(MNumber aNumber) {
    return ( this.greaterThan(aNumber) || this.equals(aNumber) );
  }

  /**
   * Returns true if this rational is zero.
   *
   * @return   a <code>boolean</code> which is
   * true if this rational is zero.
   */
  public boolean isZero() {
    return ( m_numerator == 0 && m_denominator != 0 );
  }

  /**
   * Returns a <code>double</code> with the value of this rational.
   *
   * @return   a <code>double</code> with the value of this rational.
   *
   */
  public double getDouble() {
    if ( isPosInfinity() ) {
      return Double.POSITIVE_INFINITY;
    }
    else if ( isNegInfinity() ) {
      return Double.NEGATIVE_INFINITY;
    }
    else return (double)m_numerator/(double)m_denominator;
  }

  /**
   * Converts <code>aValue</code> into a rational and returns it. It can only be used for
   * <code>aValue</code> < 100000 and it works exactly up to
   * {@link #NUMBER_OF_DIGITS} post decimal positions.
   * Otherwise an approximate value is given. This restriction is necessary since
   * otherwise an overflow causes errors!
   *
   * @param    aValue              a  <code>double</code>
   *
   * @return   a <code>MNumber</code> holding the conversion of
   * <code>aValue</code>.
   * @mm.sideeffects this is set to <code>aValue</code>.
   *
   */
  public MNumber setDouble(double aValue) {
    if(Double.isNaN(aValue)){
      setNaN();
      return this;
    }

    if(aValue == 0){
      setNumerator(0);
      setDenominator(1);
      return this;
    }
    setDouble(aValue, NUMBER_OF_DIGITS);
    //if(getDouble() != aValue)
    //  System.out.println("Setting to "+this);
    return this;
  }

  /**
   * Converts <code>aValue</code> into a rational with
   * <code>numberOfDigits</code> post decimal
   * positions and returns it. <code>numberOfDigits</code> must be <= 6!
   * The method can only be used for <code>aValue</code><100000.
   * The restrictions are necessary since otherwise an overflow causes errors!
   *
   * @param    aNumber              a  <code>double</code>
   * @param    numberOfDigits      an <code>int</code>
   *
   * @return   a <code>MNumber</code> holding the conversion of
   * <code>aValue</code>.
   * @mm.sideeffects this is set to <code>aValue</code>.
   *
   */
  public MNumber setDouble(double aNumber, int numberOfDigits) {
    if(aNumber == Double.POSITIVE_INFINITY)
      setPosInfinity();
    else if(aNumber == Double.NEGATIVE_INFINITY)
      setNegInfinity();
    else
      setNotInfinity();
    if ( aNumber == Double.POSITIVE_INFINITY ) {
      m_numerator = 1;
      m_denominator = 0;
      return this;
    }
    if ( aNumber == Double.NEGATIVE_INFINITY ) {
      m_numerator = -1;
      m_denominator = 0;
      return this;
    }
    if( aNumber == Double.NaN){
      m_numerator = 0;
      m_denominator = 0;
    }

    if (aNumber == 0) {
      m_numerator = 0;
      m_denominator = 1;
    }
    short sign = 1;

    if (aNumber < 0) {
      sign = -1;
      aNumber *= -1;
    }

    // the following produces -3.99999 for 1e-4!
    // double lg = (Math.log(aValue)/Math.log(10));
    MDouble val = new MDouble(aNumber);
    double base = val.mantisse();
    int lg = val.exponent();
    if (aNumber >= 1) {
      m_numerator = (long) sign * Math.round(base * Math.pow(10, numberOfDigits));
      m_denominator = (long) Math.pow(10, numberOfDigits - lg);
    } else {
      m_numerator = (long) sign * Math.round(base * Math.pow(10, numberOfDigits + lg));
      m_denominator = (long) Math.pow(10, numberOfDigits);
    }

    cancelDown();
    return this;
  }

  public Node getMathMLNode() {
    return getMathMLNode(XMLUtils.getDefaultDocument());
  }

  public Node getMathMLNode(Document doc) {
  	if(isEdited()) {
	    if(getDenominator() == 1) {
	      return ExerciseObjectFactory.exportExerciseAttributes(
	      		new MInteger(getNumerator()).getMathMLNode(doc), this);
	    }
	    Element mfrac = ExerciseObjectFactory.createNode(this, "mfrac", doc);
	    mfrac.setAttribute("xmlns", XMLUtils.MATHML_NAMESPACE);
	    mfrac.appendChild(new MInteger(getNumerator()).getMathMLNode(doc));
	    mfrac.appendChild(new MInteger(getDenominator()).getMathMLNode(doc));
	    return mfrac;
  	} else {
  		return ExerciseObjectFactory.createUneditedNode(this, doc);
  	}
  }

  /**
   * Returns a new rational with value 0/1.
   *
   * @return   a <code>MNumber</code>
   *
   */
  public MNumber create() {
    return new MRational();
  }

  /**
   * Returns this rational to the power of <code>exponent</code>.
   *
   * @param    exponent            a  <code>MNumber</code>
   *
   * @return   a <code>MNumber</code> holding this rational to the power of
   * <code>exponent</code>.
   *
   * @throws   ArithmeticException if this or <code>exponent</code> is infinity.
   *
   */
  public MNumber power(MNumber exponent) {
    if ( isInfinity()  ||  exponent.isInfinity() ) {
      throw new ArithmeticException("Infinity is not allowed!");
    }
    else {
//      if (exponent instanceof MInteger) {
//        if (exponent.greaterOrEqualThan(new MInteger(0))){
//          return new
//            MRational((long)(Math.pow((double)(this.m_numerator),
//                                       exponent.getDouble())),
//                         (long)(Math.pow((double)(this.m_denominator),
//                                         exponent.getDouble()))).cancelDown();
//        }
//        else {
//          return inverted().power(multiply(exponent, new MInteger(-1)));
//        }
  	  if (exponent.isInteger()) {
	        if (exponent.greaterOrEqualThan(NumberFactory.newInstance(exponent.getNumberClass(),0))){
	          return new
	            MRational((long)(Math.pow((double)(this.m_numerator),
	                                       exponent.getDouble())),
	                         (long)(Math.pow((double)(this.m_denominator),
	                                         exponent.getDouble()))).cancelDown();
	        }
	        else {
	          return inverted().power(multiply(exponent, NumberFactory.newInstance(exponent.getNumberClass(),-1)));
	        }
	  }else {
        return setDouble(Math.pow(getDouble(), exponent.getDouble()));
      }
    }
  }

  /**
   * Returns true if <code>a</code> is a square number.
   *
   * @param    a                   a  <code>long</code>
   *
   * @return   a <code>boolean</code> which is true if <code>a</code> is a
   * square number.
   *
   */
  public static boolean isSquareNumber(long a) {
    return a == Math.round(Math.sqrt((double)a))*Math.round(Math.sqrt((double)a));
  }

  /**
   * Returns true if <code>aRational</code> is a square number.
   *
   * @param    aRational                   a  <code>MRational</code>
   *
   * @return   a <code>boolean</code> which is true if <code>aRational</code>
   * is a square number.
   *
   */
  public static boolean isSquareNumber(MRational aRational) {
    return isSquareNumber(aRational.m_numerator)
      && isSquareNumber(aRational.m_denominator);
  }

  /**
   * Returns (approximately, if necessary) the square root of this rational.
   *
   * @return   a <code>MNumber</code> holding the square root.
   * @mm.sideeffects this is set to its square root.
   *
   */
  public MNumber squareRoot() {
    if (isSquareNumber(this)) {
      m_numerator = Math.round(Math.sqrt((double)m_numerator));
      m_denominator = Math.round(Math.sqrt((double)m_denominator));
      return this;
    }
    else return setDouble(Math.sqrt(this.getDouble()));
  }

  /**
   * Negates this rational.
   *
   * @return   a <code>MNumber</code> holding the negation.
   * @mm.sideeffects this is multiplied with (-1).
   */
  public MNumber negate() {
    m_numerator = -m_numerator;
    return this;
  }

  public  MNumber copy() {
    return new MRational(m_numerator, m_denominator);
  }

  /**
   * Returns a string representation of this rational.
   *
   * @return   a <code>String</code> representing this rational.
   *
   */
  public String toString(){
    if(m_denominator == 1)
      return ""+m_numerator;
    return m_numerator+"/"+ m_denominator;
  }

  public MNumber set(MNumber aNumber) {
    if(aNumber instanceof MRational){
      m_numerator = ((MRational)aNumber).m_numerator;
      m_denominator = ((MRational)aNumber).m_denominator;
    } else
    setDouble(aNumber.getDouble());
    return this;
  }

  private static int numberOfDigits(long number){
    return new Long(Math.abs(number)).toString().length();
  }

  /**
   * Returns true if this rational is positive infinity, i.e., if the numerator
   * is positive and the denominator is zero.
   *
   * @return   a <code>boolean</code> which is true if this rational is
   * positive infinity.
   *
   */
  public boolean isPosInfinity() {
    return m_denominator == 0 && m_numerator > 0;
  }

  /**
   * Returns true if this rational is negative infinity, i.e., if the numerator
   * is negative and the denominator is zero.
   *
   * @return   a <code>boolean</code> which is true if this rational is
   * negative infinity.
   *
   */
  public boolean isNegInfinity() {
    return m_denominator == 0 && m_numerator < 0;
  }


  public void setPosInfinity(){
    super.setPosInfinity();
    m_numerator = 1;
    m_denominator = 0;
  }

  public void setNegInfinity(){
    super.setNegInfinity();
    m_numerator = -1;
    m_denominator = 0;
  }


  /**
   * Returns true if this rational is not a number, i.e., if both numerator
   * and denominator are zero.
   *
   * @return   a <code>boolean</code> which is true if this rational is not a
   * number.
   *
   */
  public boolean isNaN(){
    return m_denominator == 0 && m_numerator == 0;
  }

  public boolean isRational(){
    return true;
  }

  public String getDomainString(){
    return "\u211a";
  }

  /**
   * Returns the class of the corresponding MM-Class for this number.
   */
  public Class getMMClass(){
    return MMRational.class;
  }
}







