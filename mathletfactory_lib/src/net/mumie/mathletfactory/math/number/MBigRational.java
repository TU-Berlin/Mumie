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
import java.util.logging.Logger;

import net.mumie.mathletfactory.util.xml.ExerciseObjectFactory;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *  This is a (mathematical) extension of {@link MRational} using BigIntegers
 *  instead of longs.
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */

public class MBigRational extends MRealNumber {

  private static Logger logger = Logger.getLogger(MBigRational.class.getName());
  private BigInteger m_numerator, m_denominator;

  /** Constructs a zero value big rational. */
  public MBigRational() {
    m_numerator = new BigInteger("0");
    m_denominator = new BigInteger("1");
  }

  /**
   *  Constructs a big rational from the given numerator and denominator
   *  without cancelling it down.
   */
  public MBigRational(BigInteger numerator, BigInteger denominator){
    checkDivisionByZero(numerator, denominator);
    m_numerator = numerator;
    m_denominator = denominator;
  }

  /**
   *  Constructs a big rational from the given numerator and denominator
   *  after cancelling it down.
   */
  public MBigRational(long numerator, long denominator){
    this(new BigInteger(Long.toString(numerator)),
         new BigInteger(Long.toString(denominator)));
  }

  /**
   *  Constructs a big rational from the given double value using
   *  {@link #setDouble}.
   */
  public MBigRational(double aValue) {
    this();
    setDouble(aValue);
  }

  public MBigRational(MBigRational aRational) {
	this(aRational.getNumerator(), aRational.getDenominator());
  }

  /** Returns the numerator. */
  public BigInteger getNumerator() {
    return m_numerator;
  }

  /** Returns the denominator. */
  public BigInteger getDenominator() {
    return m_denominator;
  }

  /**
   * Since division by zero is allowed from the java point of view (the
   * mathematical layer is responsible for handling the resulting NaN
   * values), this method only adds an entry to the log if division by
   * zero occurs.
   */
  private static void checkDivisionByZero(MBigRational aRational) {
    if (aRational.getDenominator().doubleValue() == 0)
      logger.fine("Division by zero: "+aRational);
  }

  /**
   * Since division by zero is allowed from the java point of view (the
   * mathematical layer is responsible for handling the resulting NaN
   * values), this method only adds an entry to the log if division by
   * zero occurs.
   */
  private static void checkDivisionByZero(BigInteger nominator, BigInteger denominator) {
    if (denominator.doubleValue() == 0)
      logger.fine("division by zero! ");
  }

  /**
   * Returns the least common multiple of the absolute value of two integers.
   * If one of the integers is zero, zero is returned. If both integers
   * are zero an exception is thrown.
   */
  public static BigInteger leastCommonMultiple(BigInteger a, BigInteger b) {
    if (a.doubleValue() == 0 && b.doubleValue() == 0)
      throw new ArithmeticException("LCM of zero and zero not defined!");
    else return a.multiply(b.divide(a.gcd(b))).abs();
  }

  /**
   * Returns the greatest common divisor of the absolute value of two integers.
   * If one of the integers is zero the otherone is returned. If both integers
   * are zero an exception is thrown.
   */
  public static BigInteger greatestCommonDivisor(BigInteger a, BigInteger b) {
    BigInteger rest =  new BigInteger("1");
    if (a.doubleValue() == 0 && b.doubleValue() == 0)
      throw new ArithmeticException("GCD of zero and zero not defined!");
    else if (a.doubleValue() == 0 && b.doubleValue() != 0) return b.abs();
    else if (b.doubleValue() == 0 && a.doubleValue() != 0) return a.abs();
    else {
      while ( rest.compareTo(new BigInteger("0")) > 0) {
        rest = a.abs().mod(b.abs());
        a = b;
        b = rest;
      }
      return a;
    }
  }

  /**
   * Cancels a fraction down. The sign is set in the nominator. A fraction
   * with nominator=0 is set to 0/1.
   */
  public MBigRational cancelDown() {
    if(m_numerator.doubleValue() == 0){
      m_denominator = BigInteger.ONE;
      return this;
    }
    checkDivisionByZero(this);
    BigInteger gcd = greatestCommonDivisor(m_numerator, m_denominator);
    m_numerator = m_numerator.divide(gcd);
    m_denominator = m_denominator.divide(gcd);
    if ((m_numerator.divide(m_numerator.abs())).multiply(m_denominator.divide(m_denominator.abs()))
        .compareTo(new BigInteger("0")) >= 0)
      m_numerator = m_numerator.abs();
    else m_numerator = m_numerator.abs().negate();
    m_denominator = m_denominator.abs();
    return this;
  }

  /**
   * Adds a fraction to a fraction. The result is cancelled down.
   */
  public MNumber add(MNumber aNumber) {
    //System.out.println("+: numerator1   = "+m_numerator);
    //System.out.println("+: denominator1 = "+m_denominator);

    //System.out.println("+: numerator2   = "+((MBigRational)aNumber).m_numerator);
    //System.out.println("+: denominator2 = "+((MBigRational)aNumber).m_denominator);


    BigInteger lcm = leastCommonMultiple(m_denominator,
                                           ((MBigRational)aNumber).m_denominator);
    m_numerator = m_numerator.multiply(lcm.divide(m_denominator))
      .add( ((MBigRational)aNumber).m_numerator.multiply((lcm.divide(
                                                                       ((MBigRational)aNumber).m_denominator))));
    m_denominator = lcm;
    //System.out.println("+: numerator   = "+m_numerator);
    //System.out.println("+: denominator = "+m_denominator);
    return cancelDown();
  }

  /**
   * Subtracts a fraction from a fraction. The result is cancelled down.
   */
  public MNumber sub(MNumber aNumber) {
    BigInteger lcm = leastCommonMultiple(m_denominator,
                                           ((MBigRational)aNumber).m_denominator);
    m_numerator = m_numerator.multiply(lcm.divide(m_denominator))
      .subtract(((MBigRational)aNumber).m_numerator.multiply(lcm.divide(
                                                                          ((MBigRational)aNumber).m_denominator)));
    m_denominator = lcm;
    return cancelDown();
  }

  /**
   * Multiplies this with a fraction. The result is cancelled down.
   */
  public MNumber mult(MNumber aNumber) {
    checkDivisionByZero((MBigRational)aNumber);
    checkDivisionByZero((MBigRational)this);
    m_numerator = m_numerator.multiply(((MBigRational)aNumber).m_numerator);
    m_denominator = m_denominator.multiply(((MBigRational)aNumber).m_denominator);
    return cancelDown();
  }

  /**
   *  Multiplies <code>a</code> with <code>b</code>. The result is cancelled down.
   *  @see MNumber#mult(MNumber, MNumber)
   */
  public MNumber mult(MNumber a, MNumber b) {
    return a.mult(b);
  }

  /**
   * Divides this fraction by <code>aNumber</code>.
   */
  public MNumber div(MNumber aNumber) {
    if ( aNumber.isZero() ) {
      throw new ArithmeticException("Division by zero not allowed!");
    }
    else {
      return this.mult( ((MBigRational)aNumber).inverted() );
    }
  }

  /**
   * Changes a fraction to its reciprocal. The result is not cancelled down.
   */
  public MNumber inverse() {
    BigInteger helper = this.m_numerator;
    m_numerator = m_denominator;
    m_denominator = helper;
    cancelDown();
    return this;
  }

  /**
   * Returns the reciprocal of a given fraction as a new big rational.
   */
  public MNumber inverted() {
    MBigRational result = new MBigRational(this.m_denominator,this.m_numerator);
    result.cancelDown();
    return result;
  }

  /**
   * Tests if two fractions are the same. Fractions are cancelled down before
   * comparison.
   */
  public boolean equals(MNumber aNumber){
    if (cancelDown().m_numerator.equals(((MBigRational)aNumber).cancelDown().m_numerator)
        && cancelDown().m_denominator.equals(((MBigRational)aNumber).cancelDown().m_denominator))
      return true;
    return false;
  }

  /**
   * Tests if the nominators and denominators of two fractions
   * are exactly the same. Fractions are not cancelled down and the signs are
   * important.
   */
  public boolean equalsExactly(MNumber aNumber){
    if (this.m_numerator.equals(((MBigRational)aNumber).m_numerator)
        && this.m_denominator.equals(((MBigRational)aNumber).m_denominator))
      return true;
    return false;
  }

  /**
   *  Returns true, if this number is less than <code>aNumber</code>.
   */
  public boolean lessThan(MNumber aNumber) {
    BigInteger lcm = leastCommonMultiple(m_denominator,
                                           ((MBigRational)aNumber).m_denominator);
    if (m_numerator.multiply(lcm.divide(m_denominator)).
        compareTo(((MBigRational)aNumber).m_numerator.multiply(lcm.divide(
                                                                            ((MBigRational)aNumber).m_denominator))) < 0)
      return true;
    else return false;
  }

  /**
   *  Returns true, if this number is less or equal than <code>aNumber</code>.
   */
  public boolean lessOrEqualThan(MNumber aNumber) {
    BigInteger lcm = leastCommonMultiple(m_denominator, ((MBigRational)aNumber).m_denominator);
    if (m_numerator.multiply(lcm.divide(m_denominator)).
        compareTo(((MBigRational)aNumber).m_numerator.multiply(lcm
                                                                  .divide(((MBigRational)aNumber).m_denominator))) <= 0)
      return true;
    else return false;
  }

  /**
   * Method greaterThan
   *  Returns true, if this number is greater than <code>aNumber</code>.
   */
  public boolean greaterThan(MNumber aNumber) {
    BigInteger lcm = leastCommonMultiple(m_denominator,
                                           ((MBigRational)aNumber).m_denominator);
    if (m_numerator.multiply(lcm.divide(m_denominator)).
        compareTo(((MBigRational)aNumber).m_numerator.multiply(lcm
                                                                  .divide(((MBigRational)aNumber).m_denominator))) > 0)
      return true;
    else return false;
  }


  /**
   *  Returns true, if this number is greater or equal than <code>aNumber</code>.
   */
  public boolean greaterOrEqualThan(MNumber aNumber) {
    BigInteger lcm = leastCommonMultiple(m_denominator, ((MBigRational)aNumber).m_denominator);
    //System.out.println("comparing "+m_numerator.multiply(lcm.divide(m_denominator))+
    //                     "and "+((MBigRational)aNumber).m_numerator.multiply(lcm
    //    .divide(((MBigRational)aNumber).m_denominator)));
    if (m_numerator.multiply(lcm.divide(m_denominator)).
        compareTo(((MBigRational)aNumber).m_numerator.multiply(lcm
                                                                  .divide(((MBigRational)aNumber).m_denominator))) >= 0)
      return true;
    else return false;
  }


  public boolean isZero() {
    return m_numerator.doubleValue() == 0 && m_denominator.doubleValue() != 0;
  }

  public double getDouble() {
    if(isPosInfinity())
      return Double.POSITIVE_INFINITY;
    if(isNegInfinity())
      return Double.NEGATIVE_INFINITY;
    return m_numerator.doubleValue()/m_denominator.doubleValue();
  }

  /**
   *  Sets the big rational from the value of the double using the precision
   *  specified by {@link MDouble#mantisse}.
   *
   *  @see MNumber#setDouble(double)
   */

  public MNumber setDouble(double aValue) {

    if( aValue == 0)
      return new MBigRational(0,1);

    // the following produces -3.99999 for 1e-4!
    // double lg = (Math.log(aValue)/Math.log(10));
    MDouble val = new MDouble(aValue);
    StringBuffer base = new StringBuffer(String.valueOf(Math.abs(val.mantisse()))
                                           .replaceAll("(,|\\.)",""));
    //System.out.println("base = "+base);

    int lg = val.exponent();

    if(Math.abs(aValue) >= 1) {

      // numerator is greater than denominator, so we need at least lg digits
      // for the numerator and less or equal digits for the denominator,
      StringBuffer numerator = new StringBuffer(Math.max(lg, base.length()));
      StringBuffer denominator = new StringBuffer(Math.max(lg,base.length())).append("1");

      // take the leading digit from base as the first entry of the numerator
      numerator.append(base.substring(0,1));
      base.delete(0,1);// lg is >= 0
      while(lg-- > 0){
        // shift the decimal point one position to the right by appending
        // a new digit at numerator and "pop" the base-string like a queue
        if(base.length() != 0){
          numerator.append(base.substring(0,1));
          base.delete(0,1);
        }
        else
          // if there is no digit left, but lg is still > 0 then append a zero
          numerator.append("0");
      }

      // if we still have some digits left, we append a zero to both the numerator
      // and the denominator
      while(base.length() > 0 && base.substring(0,1).matches("\\d")){
        numerator.append(base.substring(0,1));
        base.delete(0,1);
        denominator.append("0");
      }
      // because base has one digit _before_ the point, delete the last zero of
      // denominator
      m_numerator =  new BigInteger(numerator.toString());
      m_denominator = new BigInteger(denominator.toString());
    }

    else {
      // denominator is greater than numerator, so we need at least length(base)
      // digits for the denominator and less or equal digits for the numerator

      lg = -lg; // lg is < 0
      StringBuffer numerator = new StringBuffer(base.length());
      StringBuffer denominator = new StringBuffer(base.length()).append("1");

      // take the leading digit from base as the first entry of the numerator
      numerator.append(base.substring(0,1));
      base.delete(0,1);

      while(base.length() > 0 && base.substring(0,1).matches("\\d")){
        lg++;
        // shift the decimal point one position to the right by appending
        // a new digit at numerator and "pop" the base-string like a queue
        numerator.append(base.substring(0,1));
        base.delete(0,1);
      }

      // append lg zeros to the denominator
      while(lg-- > 0){
        denominator.append("0");
      }

      m_numerator =  new BigInteger(numerator.toString());
      m_denominator = new BigInteger(denominator.toString());
    }
    //System.out.println("numerator   = "+m_numerator);
    //System.out.println("denominator = "+m_denominator);
    cancelDown();
    //System.out.println("numerator   = "+m_numerator);
    //System.out.println("denominator = "+m_denominator);

    if(aValue < 0)
      return this.negate();
    return this;
  }

  /** Creates a new zero value big rational. */
  public MNumber create() {
    return new MBigRational();
  }

  /**
   * Raises the big rational to power <code>exponent</code>.
   * @throws ArithmeticException if <code>exponent</code> is non integer.
   */
  public MNumber power(MNumber exponent) {
    if (exponent instanceof MInteger) {
      if (exponent.greaterOrEqualThan(new MInteger(0))){
        return new
          MBigRational(m_numerator.pow(((MInteger)exponent).getIntValue()),
                        m_denominator.pow(((MInteger)exponent).getIntValue())).cancelDown();
      }
      else {
        return inverted().power(multiply(exponent, new MInteger(-1)));
      }
    }
    else throw new ArithmeticException("Exponent must be an integer!");
  }

  public MNumber negate() {
    m_numerator = m_numerator.negate();
    return this;
  }

  public MNumber set(MNumber aNumber) {
    if(aNumber instanceof MBigRational){
      this.m_numerator = ((MBigRational)aNumber).getNumerator();
      this.m_denominator = ((MBigRational)aNumber).getDenominator();
    } else
    setDouble(aNumber.getDouble());
    return this;
  }

  public  MNumber copy() {
    return new MBigRational(m_numerator, m_denominator);
  }

  /**
   * Returns the String representation of this number.
   * @see java.lang.Object#toString()
   */
  public String toString(){
    return m_numerator+"/"+ m_denominator;
  }

  /**
   * Returns "Q"
   * @see MNumber#getDomainString()
   */
  public String getDomainString(){
    return "Q";
  }

  /**
   * Returns true.
   * @see MNumber#isRational()
   */
  public boolean isRational(){
    return true;
  }

  public boolean isPosInfinity() {
    return m_denominator.equals(BigInteger.ZERO) && m_numerator.compareTo(BigInteger.ZERO) == 1;
  }

  public boolean isNegInfinity() {
    return m_denominator.equals(BigInteger.ZERO) && m_numerator.compareTo(BigInteger.ZERO) == -1;
  }

  public void setPosInfinity(){
    super.setPosInfinity();
    m_numerator = new BigInteger("1");
    m_denominator = new BigInteger("0");
  }

  public void setNegInfinity(){
    super.setNegInfinity();
    m_numerator = new BigInteger("-1");
    m_denominator = new BigInteger("0");
  }

  public boolean isNaN(){
    return m_denominator.equals(BigInteger.ZERO) && m_numerator.equals(BigInteger.ZERO);
  }

  public Node getMathMLNode() {
    return getMathMLNode(XMLUtils.getDefaultDocument());
  }

  public Node getMathMLNode(Document doc) {
    if(getDenominator().doubleValue() == 1) {
      return ExerciseObjectFactory.exportExerciseAttributes(
      		new MInteger(getNumerator().intValue()).getMathMLNode(doc), this);
    } else {
      Element result = ExerciseObjectFactory.createNode(this, "mfrac", doc);
      result.setAttribute("xmlns", XMLUtils.MATHML_NAMESPACE);
      result.appendChild(new MInteger(getNumerator().intValue()).getMathMLNode(doc));
      result.appendChild(new MInteger(getDenominator().intValue()).getMathMLNode(doc));
      return result;
    }
  }
}

