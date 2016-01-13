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

import java.text.DecimalFormat;

import net.mumie.mathletfactory.util.xml.ExerciseObjectFactory;
import net.mumie.mathletfactory.util.xml.ExerciseObjectIF;

import org.w3c.dom.Node;

/**
 * The base for all number classes.
 * @author Vossbeck, Paehler
 * @mm.docstatus finished
 */
public abstract class MNumber implements Cloneable, ExerciseObjectIF {

  /** This attribute indicates that the number is (positive) infinity. */
  public static final int INFINITY = 0;
  
  /** This attribute indicates that the number is positive infinity. */
  public static final int POSITIVE_INFINITY = INFINITY;
  
  /** This attribute indicates that the number is negative infinity. */
  public static final int NEGATIVE_INFINITY = 1;

  /** This attribute indicates that the number is NaN (not a number). */
  public static final int NaN = 2;
	
	/** 
	 * This field holds the attributes for this number
	 * where each attribute is initialized with <code>false</code>.
	 */
  protected int m_attributes;

	/**	This field describes the current edited status. */
	private boolean m_isEdited = true;

	/**	This field holds the current label. */
	private String m_label = null;
	
	/**	This field holds the current <code>hidden</code> flag. */
	private boolean m_hidden = false;

  /**
   * This method returns a new initialized number array of the desired
   * <code>numberClass</code> and <code>dimension</code>. All contained entries
   * are new created <code>MNumber</code>s initialized to zero.
   */
  public static MNumber[] getNewNumberArray(Class numberClass, int dimension){
    MNumber[] array = new MNumber[dimension];
    for(int i=0; i<dimension; i++)
      array[i] = NumberFactory.newInstance(numberClass);
    return array;
  }

  /**
   * Adds two numbers and returns the result.
   */
  public static MNumber add(MNumber augend, MNumber addend) {
    return augend.copy().add(addend);
  }

  /**
   * Subtracts two numbers and returns the result.
   */
  public static MNumber subtract(MNumber minuend, MNumber subtrahend) {
    return minuend.copy().sub(subtrahend);
  }

  /**
   * Multiplies two numbers and returns the result.
   */
  public static MNumber multiply(MNumber multiplicand, MNumber multiplier) {
    return multiplicand.copy().mult(multiplier);
  }

  /**
   * Divides two numbers and returns the result.
   */
  public static MNumber divide(MNumber divident, MNumber divisor) throws ArithmeticException {
    return divident.copy().div(divisor);
  }

  /**
   * Raises basis to power of exponent and returns the result.
   */
  public static MNumber power(MNumber basis, MNumber exponent) {
    return basis.copy().power(exponent);
  }

  /**
   * Returns <code>number*number</code>.
   */
  public static MNumber square(MNumber number) {
    return number.copy().square();
  }

  /**
   * Returns the square root of <code>number</code>.
   */
  public static MNumber squareRoot(MNumber number) {
    return number.copy().squareRoot();
  }

  /**
   * Returns the absolute of this number.
   */
  public MNumber abs() {
    mult(conjugated()).squareRoot();
    return this;
  }

  /**
   * Returns a new number with the absolute of this number.
   */
  public MNumber absed() {
    if(MComplex.class.isAssignableFrom(getClass())){
      return new MDouble(((MComplex)copy().abs()).getRe());
    }
    if(MComplexRational.class.isAssignableFrom(getClass())){
      return new MRational(((MComplexRational)copy().abs()).getRe());
    }
    else return copy().abs();
  }

  /**
   *  Returns 1 for x> 0, 0 for x == 0, -1 for x < 0.
   */
  public int getSign(){
    if(getDouble() > 0)
      return 1;
    if(getDouble() < 0)
      return -1;
    return 0;
  }

  /**
   * Returns the number with negative imaginary part.
   */
  public abstract MNumber conjugate();

  /**
   * Returns a new number that is the conjugated of this one.
   */
  public MNumber conjugated() {
    return copy().conjugate();
  }

  /**
   *  Adds <code>aNumber</code> to <code>this</code> and returns <code>this</code>.
   */
  public abstract MNumber add(MNumber aNumber);

  /**
   *  Subtracts <code>aNumber</code> from <code>this</code> and returns <code>this</code>.
   */
  public  MNumber sub(MNumber aNumber) {
    add(aNumber.negated());
    return this;
  };

  /**
   *  Multiplies  <code>this</code> with <code>aNumber</code> and returns <code>this</code>.
   */
  public abstract MNumber mult(MNumber aNumber);

  /**
   * Sets <code>this</code> to the result of <code>aNumber*aSecondNumber</code>.
   */
  public abstract MNumber mult(MNumber aNumber, MNumber aSecondNumber);

  /**
   *  Divides  <code>this</code> by <code>aNumber</code> and returns <code>this</code>.
   */
  public abstract MNumber div(MNumber aNumber); // throws ArithmeticException ;

  /**
   *  Raises <code>this</code> to <code>exponent</code> and returns <code>this</code>.
   */
  public abstract MNumber power(MNumber exponent);

  /**
   *
   *  Raises <code>this</code> to <code>n</code> and returns <code>this</code>.
   *
   *  For integer exponents this is more accurate than Math.power(), which uses
   *  rounded logarithms.
   */
  public MNumber power(int n){
    if(n == 1)
      return this;
    if(n == 0){
      setDouble(1);
      return this;
    }
    MNumber base = copy();
    if(n > 1)
      for(int i=1; i<n; i++)
        mult(base);
    else
      for(int i=0; i<=-n; i++)
        div(base);
    return this;
  }

  /** Returns the result of <code>this^n</code> as a new number. */
  public MNumber powered(int n){
    return copy().power(n);
  }

  /**
   *  Sets <code>this</code> to the the nth root of this number and returns it.
   *  May be overridden in subclass by a more suitable impementation.
   */
  public MNumber nthRoot(int n){
    return power(NumberFactory.newInstance(getClass(),1./n));
  }

  /** Multiplies this number with itself and return <code>this</code>.*/
  public MNumber square() {
    return mult(this);
  }

  /**
   * Sets this number to its square root and returns <code>this</code>.
   */
  public MNumber squareRoot() {
  	if(this.isZero())
  		return create();
    return power(new MDouble(0.5d));
  }

  /**
   * Sets this number to <code>exp(this)</code> and returns <code>this</code>.
   */
  public MNumber exp() {
    setDouble(Math.exp(getDouble()));
    return this;
  }

  /**
   * Sets this number to <code>log(this)</code> with <code>log</code> being the
   * natural logarithm and returns <code>this</code>.
   */
  public MNumber log() {
    setDouble(Math.log(getDouble()));
    return this;
  }

  /**
   * Sets this number to <code>sin(this)</code> and returns <code>this</code>.
   */
  public MNumber sin() {
    setDouble(Math.sin(getDouble()));
    return this;
  }

  /**
   * Sets this number to <code>sinh(this)</code> and returns <code>this</code>.
   */
  public MNumber sinh() {
    setDouble((Math.exp(getDouble()) - Math.exp(-getDouble()))/2);
    return this;
  }

  /**
   * Sets this number to <code>arsinh(this)</code> and returns <code>this</code>.
   */
  public MNumber arsinh() {
    setDouble(Math.log(getDouble() + Math.sqrt(getDouble()*getDouble() + 1)));
    return this;
  }

  /**
   * Sets this number to <code>cosh(this)</code> and returns <code>this</code>.
   */
  public MNumber cosh() {
    setDouble((Math.exp(getDouble()) + Math.exp(-getDouble()))/2);
    return this;
  }

  /**
   * Sets this number to <code>arcosh(this)</code> and returns <code>this</code>.
   */
  public MNumber arcosh() {
    setDouble(Math.log(getDouble() + Math.sqrt(getDouble()*getDouble() - 1)));
    return this;
  }

  /**
   * Sets this number to <code>tanh(this)</code> and returns <code>this</code>.
   */
  public MNumber tanh() {
    setDouble((Math.exp(getDouble()) - Math.exp(-getDouble()))/(Math.exp(getDouble()) + Math.exp(-getDouble())));
    return this;
  }

  /**
   * Sets this number to <code>artanh(this)</code> and returns <code>this</code>.
   */
  public MNumber artanh() {
    if(Math.abs(getDouble()) >= 1)
      setDouble(Double.NaN);
    else
      setDouble(0.5 * Math.log((1 + getDouble()) / (1 - getDouble())));
    return this;
  }

  /**
   * Sets this number to <code>coth(this)</code> and returns <code>this</code>.
   */
  public MNumber coth() {
    setDouble((Math.exp(getDouble()) + Math.exp(-getDouble()))/(Math.exp(getDouble()) - Math.exp(-getDouble())));
    return this;
  }

  /**
   * Sets this number to <code>arcoth(this)</code> and returns <code>this</code>.
   */
  public MNumber arcoth() {
    if(Math.abs(getDouble()) <= 1)
      setDouble(Double.NaN);
    else
      setDouble(0.5 * Math.log((getDouble() + 1) / (getDouble() - 1)));
    return this;
  }

  	/**
	 * Sets this number to <code>arcot(this)</code> and returns
	 * <code>this</code>.
	 */
	public MNumber arcot() {
		setDouble( Math.PI / 2 - Math.atan( getDouble() ) );
		return this;
	}
	
  /**
   * Sets this number to <code>arcsin(this)</code> and returns <code>this</code>.
   */
  public MNumber arcsin() {
    setDouble(Math.asin(getDouble()));
    return this;
  }

  /**
   * Sets this number to <code>cos(this)</code> and returns <code>this</code>.
   */
  public MNumber cos() {
    setDouble(Math.cos(getDouble()));
    return this;
  }

  /**
   * Sets this number to <code>arccos(this)</code> and returns <code>this</code>.
   */
  public MNumber arccos() {
    setDouble(Math.acos(getDouble()));
    return this;
  }

  /**
   * Sets this number to <code>tan(this)</code> and returns <code>this</code>.
   */
  public MNumber tan() {
    setDouble(Math.tan(getDouble()));
    return this;
  };

  /**
   * Sets this number to <code>arctan(this)</code> and returns <code>this</code>.
   */
  public MNumber arctan() {
    setDouble(Math.atan(getDouble()));
    return this;
  }

  /**
   * Sets this number to <code>atan2(this)</code> and returns <code>this</code>.
   */
  public MNumber arctan2(MNumber aNumberA, MNumber aNumberB) {
    setDouble(Math.atan2(aNumberA.getDouble(), aNumberB.getDouble()));
    return this;
  }

  /**
   * Sets <code>this</code> to <code>-this</code> and returns <code>this</code>.
   */
  public abstract MNumber negate();

  /**
   * Sets <code>this</code> to <code>1/this</code> and returns <code>this</code>.
   */
  public MNumber inverse() {
    MNumber unit = NumberFactory.newInstance(getClass());
    unit.setDouble(1.0);
    unit.div(this);
    setDouble(unit.getDouble());
    return this;
  }

  /**
   * Returns a new number being the inverse of <code>this</code>.
   */
  public MNumber inverted() {
    MNumber unit = NumberFactory.newInstance(getClass());
    unit.setDouble(1.0);
    return unit.div(this);
  }

  /**
   * Returns a new number being the negated of <code>this</code>.
   */
  public MNumber negated() {
    return copy().negate();
  }


  /**
   * Returns true if this number is zero with respect to the certain number
   * class.
   */
  public abstract boolean isZero();

  /**
   * Returns true if this number equals <code>aNumber</code> with respect to
   * the certain number class.
   */
  public abstract boolean equals(MNumber aNumber);

  /**
   * Returns true if this number equals <code>aNumber</code> with respect to
   * the certain number class.
   */
  public boolean equals(Object shouldBeANumber) {
    if ( getClass().isAssignableFrom(shouldBeANumber.getClass()))
      return equals((MNumber)shouldBeANumber);
    else
      return false;
  }

  /**
   * Returns true if this number is less than <code>aNumber</code>.
   * <b>Note for MDouble:</b> returns true if both numbers are exactly equal!
   */
  public abstract boolean lessThan(MNumber aNumber);

  /**
   * Returns true if this number is less or equal than <code>aNumber</code>.
   */
  public abstract boolean lessOrEqualThan(MNumber aNumber);

  /**
   * Returns true if this number is greater than <code>aNumber</code>.
   * <b>Note for MDouble:</b> returns true if both numbers are exactly equal!
   */
  public abstract boolean greaterThan(MNumber aNumber);

  /**
   * Returns true if this number is greater or equal than <code>aNumber</code>.
   */
  public abstract boolean greaterOrEqualThan(MNumber aNumber);

  /**
   * Returns true if this number is positive or negative infinity.
   */
  public boolean isInfinity() {
    return getAttribute(INFINITY);//m_isInfinity != IS_NOT_INFINITY;
  }

  /**
   * Returns true if this number is NaN (not a number).
   */
  public boolean isNaN(){
    return getAttribute(NaN);//m_isInfinity != IS_NAN;
  }

  /**
   * Sets this number to NaN (not a number).
   */
  public void setNaN(){
    setAttribute(NaN, true);//m_isInfinity = IS_NAN;
  }

  /** Sets the number to infinity. This is positive infinity for real numbers. */
  public abstract void setInfinity();

  /** Sets the number to non-infinity. */
  protected void setNotInfinity(){
  	setAttribute(INFINITY, false);//m_isInfinity = IS_NOT_INFINITY;
  }


  /**
   *  Returns the greatest integer that is lesser or equal than this number.
   */
  public MNumber floor(){
    return NumberFactory.newInstance(getClass(), Math.floor(getDouble()));
  }

  /**
   *  Returns the content MathML representation of this number.
   *  This default implementation returns "<cn>"+{@link #toString()}+"</cn>".
   *  Some Subclasses (e.q. {@link MComplex} must override this method.
   */
  public String toContentMathML(){
    return "<cn>"+toString()+"</cn>";
  }

  /**
   * Returns true if this number is an integer.
   */
  public boolean isInteger(){
    if(this instanceof MComplex && ((MComplex)this).getIm() != 0)
      return false;
    return getDouble() == (int)getDouble();
  }

  /**
   * Returns true if this number is a rational number.
   */
  public abstract boolean isRational();


  /**
   * Returns true if this number is real number.
   */
  public boolean isReal(){
    return true;
  }

  /**
   * Returns the double value of this number.
   */
  public abstract double getDouble();

  /**
   * Sets this number to the double value. This can be done
   * eventually only approximately.
   */
  public abstract MNumber setDouble(double aValue);


  /**
   * Creates a new zero value number of this number class.
   */
  public abstract MNumber create();

  /**
   *  Returns a copy of this number (i.e. a new number with the same type and value).
   */
  public abstract MNumber copy();

  /**
   * Sets the value of this number to that from <code>aNumber</code>. Returns
   * <code>this</code>.
   */
  public abstract MNumber set(MNumber aNumber);

  /**
   * Returns the mathematical string representation that fits most closely to
   * the number class of this number (e.g. R for {@link MDouble}, Z for
   * {@link MInteger}, Q for {@link MRational}, etc.).
   */
  public abstract String getDomainString();

  /** Every instance of MNumber needs a string representation. */
  public abstract String toString();

  /**
   * Returns the string representation with respect to the given decimal format. The default
   * implementation returns the result of {@link #toString()}.
   */
  public String toString(DecimalFormat format){
    return toString();
  }

  /** Returns the minimum of two numbers. */
  public static MNumber min(MNumber one, MNumber two){
    if(one.lessThan(two))
      return one;
    return two;
  }

  /** Returns the maximum of two numbers. */
  public static MNumber max(MNumber one, MNumber two){
    if(two.lessThan(one))
      return one;
    return two;
  }

  /** Returns the corresponding MM<Number> class or <code>null</code> if none exists. */
  public Class getMMClass(){
    return null;
  }

  /**
   * Returns the {@link java.lang.Double#hashCode} result for the {@link #getDouble} result
   * of this number.
   */
  public int hashCode(){
    long bits = Double.doubleToLongBits(getDouble());
    return (int)(bits ^ (bits >>> 32));
  }

  /** Returns the MathML-Node for this MNumber. */
  public abstract Node getMathMLNode();
  
  /** Sets the MathML-Node for this MNumber. */
  public void setMathMLNode(Node content) {
    NumberFactory.setMathMLNode(content, this);
    ExerciseObjectFactory.importExerciseAttributes(content, this);
  }
  
  /**
   * Sets the named attribute to <code>value</code> for this number.
   */
  protected void setAttribute(int attr, boolean value) {
		if (value) {
			m_attributes |= (1 << attr);
		} else {
			m_attributes &= ~(1 << attr);
		}
	}

  /**
   * Returns the named attribute for this number.
   */
  protected boolean getAttribute(int attr) {
		int mask = (1 << attr);
		return ((m_attributes & mask) == mask);
	}
  
	public boolean isEdited() {
		return m_isEdited;
	}

	public void setEdited(boolean edited) {
		m_isEdited = edited;
		if(edited == false) {
			setDouble(0);
		}
	}

	public String getLabel() {
		return m_label;
	}
	
	public void setLabel(String label) {
		m_label = label;
	}

	public Class getNumberClass() {
		return getClass();
	}

	public boolean isHidden() {
		return m_hidden;
	}
	
	public void setHidden(boolean hidden) {
		m_hidden = hidden;
	}
}
