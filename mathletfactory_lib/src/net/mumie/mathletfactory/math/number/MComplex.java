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

import net.mumie.mathletfactory.mmobject.number.MMComplex;
import net.mumie.mathletfactory.util.xml.ExerciseObjectFactory;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * The number class that represents IEEE double precision complex numbers.
 *
 * @author Paehler
 * @mm.docstatus finished
 */
public class MComplex extends MNumber {

	private final double EPSILON = 1e-12;

	private double m_re;

	private double m_im;

  /** Constructs a complex number with real and imaginary part both zero. */
	public MComplex() {
		m_re = 0.0;
		m_im = 0.0;
	}

  /** Constructs a complex number with the specified real and imaginary part. */
	public MComplex(double re, double im) {
		setRe(re);
		setIm(im);
	}

	/**
	 * Constructs a new instance with the given MathML/XML content.
	 * @param content a MathML/XML node
	 */
	public MComplex(Node content) {
		setMathMLNode(content);
	}

  /** Returns the real part of this number. */
	public double getRe() {
		return m_re;
	}

  /** Returns the imaginary part of this number. */
	public double getIm() {
		return m_im;
	}
/** Returns the double value of this number when the imaginary part is 0.*/
	public double getDouble() {
		if (m_im < EPSILON)
			return m_re;
		throw new UnsupportedOperationException("complex number with imaginary part != 0 cannot return its 'double' value");
	}

  /** Returns the angle phi from polar coordinates. Range: 0-2pi */
	public double getArg() {
		if (m_re > 0 && m_im >= 0)
			return Math.atan(m_im / m_re);
		else if (m_re > 0 && m_im < 0)
			return Math.atan(m_im / m_re) + 2 * Math.PI;
		else if (m_re < 0)
			return Math.atan(m_im / m_re) + Math.PI;
		else if (m_re == 0 && m_im > 0)
			return (Math.PI / 2);
		else if (m_re == 0 && m_im < 0)
			return 1.5 * Math.PI;
		else if(m_re == 0 && m_im == 0)
			return Double.NaN;
		else
			return 0;
	}

  /** Returns the norm of this number. */
	public double getNorm() {
		return this.absed().getDouble();
	}

	public MNumber abs() {
	  if(Math.abs(getIm()) < EPSILON)
        m_re = Math.abs(m_re);
      else if(Math.abs(getRe()) < EPSILON)
        m_im = Math.abs(m_im);
      else
        mult(conjugated()).squareRoot();
      return this;
    }

	/**Returns this number after negated his imaginary part.*/
	public MNumber conjugate() {
		m_im *= -1;
		return this;
	}
/** Returns the result of the addition with another number.*/
	public MNumber add(MNumber aNumber) {
		MComplex arg;
		if (aNumber instanceof MRealNumber)
			arg = new MComplex(aNumber.getDouble(), 0);
		else
			arg = (MComplex) aNumber;
		m_re += arg.getRe();
		m_im += arg.getIm();
		return this;
	}
	/** Returns the result of the subtraction with another number.*/
	public MNumber sub(MNumber aNumber) {
		MComplex arg;
		if (aNumber instanceof MRealNumber)
			arg = new MComplex(aNumber.getDouble(), 0);
		else
			arg = (MComplex) aNumber;
		m_re -= arg.getRe();
		m_im -= arg.getIm();
		return this;
	}

	public MNumber mult(MNumber aNumber) {
		MComplex arg;
		if (aNumber instanceof MRealNumber)
			arg = new MComplex(aNumber.getDouble(), 0);
		else
			arg = (MComplex) aNumber;
		double re = m_re;
		m_re = re * arg.getRe() - m_im * arg.getIm();
		m_im = re * arg.getIm() + m_im * arg.getRe();
		return this;
	}

	public MNumber mult(MNumber a, MNumber b) {
		set(a.mult(b));
		return this;
	}

	public MNumber div(MNumber aNumber) throws ArithmeticException {
		MComplex arg;
		if (aNumber instanceof MRealNumber)
			arg = new MComplex(aNumber.getDouble(), 0);
		else
			arg = (MComplex) aNumber;
      double re = m_re;
		double norm = arg.m_re * arg.m_re + arg.m_im * arg.m_im;
		m_re = (re * arg.getRe() + m_im * arg.getIm()) / norm;
		m_im = (m_im * arg.getRe() - re * arg.getIm()) / norm;
		return this;
	}

	public MNumber power(MNumber aNumber) {
    if(aNumber.isInteger()){
      if(aNumber.getDouble() == 0)
        setDouble(1);
      else{
        MNumber multWith = copy();
        if(aNumber.getDouble() > 0)
          for(int i = 1; i< aNumber.getDouble();i++)
            mult(multWith);
        else
          for(int i = 0; i> aNumber.getDouble();i--)
            div(multWith);
      }
      return this;
    }
    ln();
		mult(aNumber);
		return exp();
	}

  /**
   * Sets this to the natural logarithm of this value and return this.
   */
  public MNumber ln(){
    if(m_re == 0){
      m_re = Math.log(m_im);
      m_im = m_im > 0 ? Math.PI/2 : 3*Math.PI/2;
      return this;
    }
    m_im = Math.atan(m_im / m_re);
    m_re = Math.log(Math.sqrt(m_re*m_re+m_im*m_im));
    return this;
  }

	public MNumber exp() {
		double tmp_re = Math.exp(m_re) * Math.cos(m_im);
		m_im = Math.exp(m_re) * Math.sin(m_im);
		m_re = tmp_re;
		return this;
	}

	public MNumber negate() {
		m_re *= -1;
		m_im *= -1;
		return this;
	}

	public boolean isZero() {
		return m_re == 0 && m_im == 0;
	}

	public boolean equals(MNumber aNumber) {
		if (aNumber instanceof MComplex) {
			MComplex arg = (MComplex) aNumber;
			return m_re == arg.getRe() && m_im == arg.getIm();
		}
		else
			throw new IllegalArgumentException("compare to MComplex only");
	}

	public boolean lessThan(MNumber aNumber) {
    MComplex number = (MComplex)aNumber;
    if(m_im == 0 && number.getIm() == 0)
      return m_re < number.getRe();
    throw new UnsupportedOperationException("Cannot compare complex numbers: "+this+" < "+aNumber);
	}

	public boolean lessOrEqualThan(MNumber aNumber) {
    MComplex number = (MComplex)aNumber;
    if(m_im == 0 && number.getIm() == 0)
      return m_re <= number.getRe();
    throw new UnsupportedOperationException("Cannot compare complex numbers: "+this+" <= "+aNumber);
	}

	public boolean greaterThan(MNumber aNumber) {
    MComplex number = (MComplex)aNumber;
    if(m_im == 0 && number.getIm() == 0)
      return m_re > number.getRe();
    throw new UnsupportedOperationException("Cannot compare complex numbers: "+this+" > "+aNumber);
	}

	public boolean greaterOrEqualThan(MNumber aNumber) {
    MComplex number = (MComplex)aNumber;
    if(m_im < EPSILON && number.getIm() < EPSILON )
      return m_re >= number.getRe();
	throw new UnsupportedOperationException("Cannot compare complex numbers: "+this+" >= "+aNumber);
	}

	public MNumber setDouble(double aValue) {
    if(aValue == Double.POSITIVE_INFINITY || aValue == Double.NEGATIVE_INFINITY)
      setInfinity();
		m_re = aValue;
		m_im = 0;
		return this;
	}

  /** Sets the real part of this number. */
	public MNumber setRe(double re) {
    if(re == Double.POSITIVE_INFINITY || re == Double.NEGATIVE_INFINITY)
      setInfinity();
		m_re = re;
		return this;
	}

  /** Sets the imaginary part of this number. */
	public MNumber setIm(double im) {
    if(im == Double.POSITIVE_INFINITY || im == Double.NEGATIVE_INFINITY)
      setInfinity();
		m_im = im;
		return this;
	}

	/** Sets two given double values as a number with a real and an imaginary part.   */
	public MNumber setComplex(double re, double im) {
    setRe(re);
    setIm(im);
		return this;
	}

  /** Sets the argument of this number. */
	public MNumber setArg(double arg) {
		setComplex(getNorm()*Math.cos(arg), getNorm()*Math.sin(arg));
		return this;
	}

  /** Sets the norm of this number. */
	public MNumber setNorm(double norm) {
		setComplex((norm/getNorm()) * m_re, (norm/getNorm()) * m_im);
		return this;
	}

	public MNumber create() {
		return new MComplex();
	}

	public MNumber copy() {
		return new MComplex(m_re, m_im);
	}

	public MNumber set(MNumber aNumber) {
    if(aNumber.isInfinity())
      setInfinity();
		if (aNumber instanceof MComplex) {
			m_im = ((MComplex) aNumber).m_im;
			m_re = ((MComplex) aNumber).m_re;
		} else if(aNumber instanceof MComplexRational) {
			m_re = ((MComplexRational) aNumber).getRe().getDouble();
			m_im = ((MComplexRational) aNumber).getIm().getDouble();
		}
		else
			setDouble(aNumber.getDouble());
		return this;
	}

  /** Sets this number to infinity. */
  public void setInfinity(){
  	setAttribute(INFINITY, true);
//    m_isInfinity = 1;
  }

	public boolean isReal() {
		return Math.abs(m_im) < EPSILON;
	}
/**Returns true if this number is imaginary number.*/
	public boolean isIm() {
		return m_re == 0&& m_im!=0;
	}
	
	public boolean isRational() {
		if (m_im != 0)
			return false;
		if (new MRational(m_re).getDouble() == m_re)
			return true;
		return false;
	}

	public boolean isNaN() {
		setAttribute(NaN, Double.isNaN(m_re) || Double.isNaN(m_im));
    return getAttribute(NaN);
	}

  public void setNaN(){
    super.setNaN();
    m_re = Double.NaN;
    m_im = Double.NaN;
  }
 
 
	public String toString() {
		if (Math.abs(m_im) < EPSILON) {
			return m_re + "";
		}
		else if (Math.abs(m_re) < EPSILON) {
			return (Math.abs(m_im) == 1) ? ((m_im < 0) ? "-i": "i") : m_im + "i";
		}
		else if (m_im > EPSILON) {
			return m_re + "+" + (m_im != 1 ? "" + m_im : "") + "i";
		}
		else
			return m_re
				+ "-"
				+ (Math.abs(m_im) != 1 ? "" + Math.abs(m_im) : "")
				+ "i";
	}

  public String toString(DecimalFormat format){
    if(format == null)
      return toString();
    if (Math.abs(m_im) < EPSILON) {
      return format.format(m_re) + "";
    }
    else if (Math.abs(m_re) < EPSILON) {
      return (m_im < 0 ? "-" : "")+(!format.format(Math.abs(m_im)).equals("1") ? "" + format.format(Math.abs(m_im)) : "")
      + "i";
    }
    else if (m_im > EPSILON) {
      return format.format(m_re) + "+" + (m_im != 1 ? "" + format.format(m_im) : "") + "i";
    }
    else
      return format.format(m_re)
        + "-"
        + (!format.format(Math.abs(m_im)).equals("1") ? "" + format.format(Math.abs(m_im)) : "")
        + "i";
  }

	public String getDomainString() {
		return "\u2102";
	}

	public String toContentMathML() {
		return "<cn type=\"complex-cartesian\">"
			+ m_re
			+ "<sep/>"
			+ m_im
			+ "</cn>";
	}

  /**
   * Returns the class of the corresponding MM-Class for this number.
   */
  public Class getMMClass(){
    return MMComplex.class;
  }

  public Node getMathMLNode() {
    return getMathMLNode(XMLUtils.getDefaultDocument());
  }

  public Node getMathMLNode(Document doc) {
  	if(isEdited()) {
	    if(getIm() == 0) {
	      return ExerciseObjectFactory.exportExerciseAttributes(
	      		new MDouble(getRe()).getMathMLNode(doc), this);
	    }
	    Element mfrac = ExerciseObjectFactory.createNode(this, "cnum", doc, XMLUtils.MATHML_EXT_NAMESPACE);
	    mfrac.setAttribute("xmlns", XMLUtils.MATHML_EXT_NAMESPACE);
	    mfrac.appendChild(new MDouble(getRe()).getMathMLNode(doc));
	    mfrac.appendChild(new MDouble(getIm()).getMathMLNode(doc));
	    return mfrac;
  	} else {
  		return ExerciseObjectFactory.createUneditedNode(this, doc);
  	}
  }
  
  public MNumber round(int accuracy) {
	  m_re = new MDouble(m_re).round(accuracy).getDouble();
	  m_im = new MDouble(m_im).round(accuracy).getDouble();
	  return this;
  }
  
  public MNumber rounded(int accuracy) {
	  return ((MComplex)this.copy()).round(accuracy);
  }
	
  public boolean equals(MNumber aNumber, int accuracy) {
	  return this.rounded(accuracy).equals(aNumber);
  }

}
