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

import net.mumie.mathletfactory.action.message.MathContentException;
import net.mumie.mathletfactory.mmobject.number.MMComplexRational;
import net.mumie.mathletfactory.util.xml.ExerciseObjectFactory;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * The number class that represents complex numbers with rational real and imaginary part.
 *
 * @author Paehler
 * @mm.docstatus finished
 */
public class MComplexRational extends MNumber {

	private final double EPSILON = 1e-12;

	private MRational m_re;

	private MRational m_im;

  /** Constructs a complex number with real and imaginary part both zero. */
	public MComplexRational() {
		m_re = new MRational();
		m_im = new MRational();
	}

  /** Constructs a complex number with the specified real and imaginary part. */
  public MComplexRational(double re, double im) {
    setRe(re);
    setIm(im);
  }


  /** Constructs a complex number with the specified real and imaginary part. */
	public MComplexRational(MRational re, MRational im) {
		setRe(re);
		setIm(im);
	}

	/**
	 * Constructs a new instance with the given MathML/XML content.
	 * @param content a MathML/XML node
	 */
	public MComplexRational(Node content) {
		setMathMLNode(content);
	}

  /** Returns the real part of this number. */
	public MRational getRe() {
		return m_re;
	}

  /** Returns the imaginary part of this number. */
	public MRational getIm() {
		return m_im;
	}

	public double getDouble() {
		if (m_im.isZero())
			return m_re.getDouble();
		throw new UnsupportedOperationException("complex number with imaginary part != 0 cannot return its 'double' value");
	}

  /** Returns the angle phi from polar coordinates. Range: 0-2pi */
	public double getArg() {
		if (m_re.getDouble() > 0 && m_im.getDouble() >= 0)
			return Math.atan(m_im.getDouble() / m_re.getDouble());
		else if (m_re.getDouble() > 0 && m_im.getDouble() < 0)
			return Math.atan(m_im.getDouble() / m_re.getDouble()) + 2 * Math.PI;
		else if (m_re.getDouble() < 0)
			return Math.atan(m_im.getDouble() / m_re.getDouble()) + Math.PI;
		else if (m_re.getDouble() == 0 && m_im.getDouble() > 0)
			return (Math.PI / 2);
		else if (m_re.getDouble() == 0 && m_im.getDouble() < 0)
			return 1.5 * Math.PI;
		else if(m_re.getDouble() == 0 && m_im.getDouble() == 0)
			return Double.NaN;
		else
			return 0;
	}

  /** Returns the norm of this number. */
	public double getNorm() {
		return this.absed().getDouble();
	}

	public MNumber conjugate() {
		m_im.mult(new MRational(-1));
		return this;
	}

	public MNumber add(MNumber aNumber) {
		MComplexRational arg;
		if (aNumber instanceof MRealNumber)
			arg = new MComplexRational(aNumber.getDouble(), 0);
		else
			arg = (MComplexRational) aNumber;
		m_re.add(arg.getRe());
		m_im.add(arg.getIm());
		return this;
	}

	public MNumber sub(MNumber aNumber) {
		MComplexRational arg;
		if (aNumber instanceof MRealNumber)
			arg = new MComplexRational(aNumber.getDouble(), 0);
		else
			arg = (MComplexRational) aNumber;
		m_re.sub(arg.getRe()); 
		m_im.sub(arg.getIm()); 
		return this;
	}

	public MNumber mult(MNumber aNumber) {
    //System.out.println("multiplying: "+this+" * "+aNumber);
    MComplexRational arg;
		if (aNumber instanceof MRealNumber)
			arg = new MComplexRational(aNumber.getDouble(), 0);
		else
			arg = (MComplexRational) aNumber;
		MRational re = new MRational(m_re);
		setRe((MRational)m_re.mult(arg.getRe()).sub(m_im.copy().mult(arg.getIm())));
		setIm((MRational)re.mult(arg.getIm()).add(m_im.copy().mult(arg.getRe())));
    //System.out.println("result: "+this);
		return this;
	}

	public MNumber mult(MNumber a, MNumber b) {
		set(a.mult(b));
		return this;
	}

	public MNumber div(MNumber aNumber) throws ArithmeticException {
		MComplexRational arg;

		if (aNumber instanceof MRealNumber)
      if(aNumber instanceof MRational)
        arg = new MComplexRational((MRational)aNumber, new MRational());
      else
        arg = new MComplexRational(aNumber.getDouble(), 0);
		else
			arg = (MComplexRational) aNumber;
    MRational re = new MRational(m_re);
		MRational norm = (MRational)arg.m_re.copy().mult(arg.m_re).add(arg.m_im.copy().mult(arg.m_im));
		setRe((MRational)(re.copy().mult(arg.getRe()).add(m_im.copy().mult(arg.getIm())).div(norm)));
		setIm((MRational)(m_im.mult(arg.getRe()).sub(re.mult(arg.getIm())).div(norm)));
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
    if(m_re.isZero()){
      m_re = new MRational(Math.log(m_im.getDouble()));
      m_im = m_im.getSign() > 0 ? new MRational(Math.PI/2) : new MRational(3*Math.PI/2);
      return this;
    }
    m_im = new MRational(Math.atan(m_im.getDouble() / m_re.getDouble()));
    m_re = new MRational(Math.log(Math.sqrt(m_re.getDouble()*m_re.getDouble()+m_im.getDouble()*m_im.getDouble())));
    return this;
  }

	public MNumber exp() {
		double tmp_re = Math.exp(m_re.getDouble()) * Math.cos(m_im.getDouble());
		m_im = new MRational(Math.exp(m_re.getDouble()) * Math.sin(m_im.getDouble()));
		m_re = new MRational(tmp_re);
		return this;
	}

	public MNumber negate() {
		m_re.negate();
		m_im.negate();
		return this;
	}

  /**
   * Returns the absolute of this number.
   */
  public MNumber abs() {
    if(getIm().isZero())
      getRe().abs();
    else
      if(getRe().isZero())
        getIm().abs();
      else
        mult(conjugated()).squareRoot();
    return this;
  }

	public boolean isZero() {
		return m_re.isZero() && m_im.isZero();
	}

	public boolean equals(MNumber aNumber) {
		if (aNumber instanceof MComplexRational) {
			MComplexRational arg = (MComplexRational) aNumber;
			return m_re.equals(arg.getRe()) && m_im.equals(arg.getIm());
		}
		else
			throw new IllegalArgumentException("compare to MComplexRational only");
	}

	public boolean lessThan(MNumber aNumber) {
    MComplexRational number = (MComplexRational)aNumber;
    if(m_im.isZero() && number.getIm().isZero())
      return m_re.lessThan(number.getRe());
    throw new UnsupportedOperationException("Cannot compare complex numbers: "+this+" < "+aNumber);
	}

	public boolean lessOrEqualThan(MNumber aNumber) {
    MComplexRational number = (MComplexRational)aNumber;
    if(m_im.isZero() && number.getIm().isZero())
      return m_re.lessOrEqualThan(number.getRe());
    throw new UnsupportedOperationException("Cannot compare complex numbers: "+this+" <= "+aNumber);
	}

	public boolean greaterThan(MNumber aNumber) {
    MComplexRational number = (MComplexRational)aNumber;
    if(m_im.isZero() && number.getIm().isZero())
      return m_re.greaterThan(number.getRe());
    throw new UnsupportedOperationException("Cannot compare complex numbers: "+this+" > "+aNumber);
	}

	public boolean greaterOrEqualThan(MNumber aNumber) {
    MComplexRational number = (MComplexRational)aNumber;
    if(m_im.isZero() && number.getIm().isZero())
      return m_re.greaterOrEqualThan(number.getRe());
	throw new UnsupportedOperationException("Cannot compare complex numbers: "+this+" >= "+aNumber);
	}

	public MNumber setDouble(double aValue) {
    if(aValue == Double.POSITIVE_INFINITY || aValue == Double.NEGATIVE_INFINITY)
      setInfinity();
		m_re = new MRational(aValue);
		m_im = new MRational();
		return this;
	}

  /** Sets the real part of this number. */
	public MNumber setRe(MRational re) {
    if(re.getDenominator() == 0)
    	throw new MathContentException("Denominator must be != 0 !");
		m_re = new MRational(re);
		return this;
	}

  /** Sets the imaginary part of this number. */
	public MNumber setIm(MRational im) {
		m_im = new MRational(im);
		return this;
	}

  /** Sets the real part of this number. */
  public MNumber setRe(double re) {
    m_re = new MRational(re);
    return this;
  }

  /** Sets the imaginary part of this number. */
  public MNumber setIm(double im) {
    m_im = new MRational(im);
    return this;
  }

  public MNumber setComplex(double re, double im){
    setRe(re);
    setIm(im);
    return this;
  }

	public MNumber setComplex(MRational re, MRational im) {
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
		setComplex((norm/getNorm()) * m_re.getDouble(), (norm/getNorm()) * m_im.getDouble());
		return this;
	}

	public MNumber create() {
		return new MComplexRational();
	}

	public MNumber copy() {
		return new MComplexRational(m_re, m_im);
	}

	public MNumber set(MNumber aNumber) {
    if(aNumber.isInfinity())
      setInfinity();
		if (aNumber instanceof MComplexRational) {
			setIm( ((MComplexRational) aNumber).m_im );
			setRe( ((MComplexRational) aNumber).m_re );
		} else if(aNumber instanceof MComplex) {
			getIm().setDouble(((MComplex) aNumber).getIm());
			getRe().setDouble(((MComplex) aNumber).getRe());
		}
		else
			setDouble(aNumber.getDouble());
		return this;
	}

  /** Sets this number to infinity. */
  public void setInfinity(){
    setAttribute(INFINITY, true);
  }

	public boolean isReal() {
		return m_im.getDouble() < EPSILON;
	}

	public boolean isRational() {
		return true;
	}

	public boolean isNaN() {
    setAttribute(NaN, m_re.isNaN() || m_im.isNaN());
    return getAttribute(NaN);
	}

  public void setNaN(){
    super.setNaN();
    m_re.setNaN();
    m_im.setNaN();
  }

	public String toString() {
		if (m_im.isZero()) {
			return m_re + "";
		}
		else if (m_re.isZero()) {
			return (Math.abs(m_im.getDouble()) == 1) ? ((m_im.getSign() < 0) ? "-i": "i") : m_im + "i";
		}
		else if (m_im.getSign() > 0) {
			return m_re + "+" + (m_im.getDouble() != 1 ? "" + m_im : "") + "i";
		}
		else
			return m_re
				+ "-"
				+ (Math.abs(m_im.getDouble()) != 1 ? "" + m_im.absed() : "")
				+ "i";
	}

	public String getDomainString() {
		return "\u2102";
	}

	public String toContentMathML() {
		return "<cn type=\"complex-cartesian\">"
			+ m_re.toContentMathML()
			+ "<sep/>"
			+ m_im.toContentMathML()
			+ "</cn>";
	}

  public Node getMathMLNode() {
    return getMathMLNode(XMLUtils.getDefaultDocument());
  }

  public Node getMathMLNode(Document doc) {
  	if(isEdited()) {
	    if(getIm().isZero()) {
	      return ExerciseObjectFactory.exportExerciseAttributes(getRe().getMathMLNode(doc), this);
	    }
	    Element cnum = ExerciseObjectFactory.createNode(this, "cnum", doc, XMLUtils.MATHML_EXT_NAMESPACE);
	    cnum.setAttribute("xmlns", XMLUtils.MATHML_EXT_NAMESPACE);
	
	    cnum.appendChild(getRe().getMathMLNode(doc));
	    cnum.appendChild(getIm().getMathMLNode(doc));
	    return cnum;
  	} else {
  		return ExerciseObjectFactory.createUneditedNode(this, doc);
  	}
  }

  /**
   * Returns the class of the corresponding MM-Class for this number.
   */
  public Class getMMClass(){
    return MMComplexRational.class;
  }
}
