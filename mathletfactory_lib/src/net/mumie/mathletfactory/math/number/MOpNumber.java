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

import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.algebra.op.UsesOperationIF;
import net.mumie.mathletfactory.math.algebra.op.node.NumberOp;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.mmobject.number.MMOpNumber;
import net.mumie.mathletfactory.util.xml.ExerciseObjectFactory;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author gronau, jweber
 * @mm.docstatus outstanding
 */
public class MOpNumber extends MNumber implements UsesOperationIF {
	/**
	 * @uml.property  name="m_normalForm"
	 */
	private boolean m_normalForm = false;
	
	private boolean m_usesVariables = false;

	/**
	 * @uml.property  name="m_operation"
	 * @uml.associationEnd  
	 */
	private Operation m_operation;

	/**
	 * @uml.property  name="m_cachedNumber"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private MNumber m_cachedNumber = new MDouble();

	public MOpNumber() {
		this(0);
	}

	public MOpNumber(Class numberClass) {
		set(numberClass, "0");
	}

	public MOpNumber(double aValue) {
		set(MDouble.class, Double.toString(aValue));
	}

	public MOpNumber(Class numberClass, String aOperation) {
		set(numberClass, aOperation);
	}

	public MOpNumber(MNumber aNumber) {
		set(aNumber);
	}

	public MOpNumber(MOpNumber opNumber) {
		set(opNumber);
	}

	/**
	 * Constructs a new instance with the given MathML/XML content.
	 * @param content a MathML/XML node
   * @throws Exception when an error while instantiation occurrs
	 */
	public MOpNumber(Node content) throws Exception {
		set(ExerciseObjectFactory.getNumberClass(content), "0");
		setMathMLNode(content);
	}

	private void updateResult() {
//		if (!isNormalForm()) 
//			m_operation.setOpRoot(OpTransform.applyRules(m_operation.getOpRoot(), new OpRule[] { new RemoveNeutralElementRule()}));
		//if (!isValidOperation(m_operation)) throw new ArithmeticException("using of variables not allowed");
		m_cachedNumber = m_operation.getResult();
	}

	public MNumber abs() {
		m_operation.abs();
		updateResult();
		return this;
	}

	public MNumber conjugate() {
		m_operation.conjugate();
		updateResult();
		return this;
	}

	public MNumber add(MNumber aNumber) {
		if (aNumber instanceof MOpNumber) {
			if (!isValidOperation(((MOpNumber) aNumber).getOperation())) throw new IllegalArgumentException("'aNumber' - not valid operation for this MOpNumber");
			m_operation.addTo(((MOpNumber) aNumber).getOperation().deepCopy());
		} else
			m_operation.addTo(new Operation(aNumber));
		updateResult();
		return this;
	}

	public MNumber mult(MNumber aNumber) {
		if (aNumber instanceof MOpNumber) {
			if (!isValidOperation(((MOpNumber) aNumber).getOperation())) throw new IllegalArgumentException("'aNumber' - not valid operation for this MOpNumber");
			m_operation.mult(((MOpNumber) aNumber).getOperation().deepCopy());
		}
		else
			m_operation.mult(new Operation(aNumber));
		updateResult();
		return this;
	}

	public MNumber mult(MNumber aNumber, MNumber aSecondNumber) {
		set(aNumber.copy().mult(aSecondNumber));
		return this;
	}

	public MNumber div(MNumber aNumber) {
		if (aNumber instanceof MOpNumber) {
			if (!isValidOperation(((MOpNumber) aNumber).getOperation())) throw new IllegalArgumentException("'aNumber' - not valid operation for this MOpNumber");
			m_operation.divBy(((MOpNumber) aNumber).getOperation().deepCopy());
		}
		else {
			m_operation.divBy(new Operation(aNumber));
		}
		updateResult();
		return this;
	}

	public MNumber power(MNumber exponent) {
		String expr = "(" + m_operation.toString() + ")^("
				+ exponent.toString() + ")";
		set(m_operation.getNumberClass(), expr);
		return this;
	}

	public MNumber negate() {
		m_operation.negate();
		updateResult();
		return this;
	}

	public boolean isZero() {
		return m_cachedNumber.isZero();
	}

	public boolean equals(MNumber aNumber) {
		if (aNumber instanceof MOpNumber) {
			Operation temp1 = new Operation(getNumberClass(), ((MOpNumber) aNumber).getOperation().toString(), true);
			Operation temp2 = new Operation(m_operation);
			temp2.normalize();
			return temp1.equals(temp2);
		} else
			return m_cachedNumber.equals(new MDouble(aNumber.getDouble()));
	}

	public boolean lessThan(MNumber aNumber) {
		if (aNumber instanceof MOpNumber) {
			return m_cachedNumber
					.lessThan(((MOpNumber) aNumber).m_cachedNumber);
		} else
			return m_cachedNumber.lessThan(aNumber);
	}

	public boolean lessOrEqualThan(MNumber aNumber) {
		if (aNumber instanceof MOpNumber) {
			return m_cachedNumber
					.lessOrEqualThan(((MOpNumber) aNumber).m_cachedNumber);
		} else
			return m_cachedNumber.lessOrEqualThan(aNumber);
	}

	public boolean greaterThan(MNumber aNumber) {
		if (aNumber instanceof MOpNumber) {
			return m_cachedNumber
					.greaterThan(((MOpNumber) aNumber).m_cachedNumber);
		} else
			return m_cachedNumber.greaterThan(aNumber);
	}

	public boolean greaterOrEqualThan(MNumber aNumber) {
		if (aNumber instanceof MOpNumber) {
			return m_cachedNumber
					.greaterOrEqualThan(((MOpNumber) aNumber).m_cachedNumber);
		} else
			return m_cachedNumber.greaterOrEqualThan(aNumber);
	}

	public void setInfinity() {
		m_operation.setDouble(Double.POSITIVE_INFINITY);
		updateResult();
	}

	public boolean isRational() {
		return m_operation.isRational();
	}

	public double getDouble() {
		return m_cachedNumber.getDouble();
	}

	public void setOperation(Operation aOperation) {
		if (!isValidOperation(aOperation)) throw new IllegalArgumentException("not valid operation for this MOpNumber");
		m_operation = new Operation(aOperation);
		m_operation.setNormalForm(m_normalForm);
		updateResult();
	}

	public Operation getOperation() {
		return m_operation;
	}

	public MNumber setDouble(double aValue) {
		NumberOp value = new NumberOp(this.getNumberClass(), aValue);
		m_operation = new Operation(value, m_normalForm);
		updateResult();
		return this;
	}

	public MNumber create() {
		MNumber result = this.copy();
		result.setDouble( 0 );
		return result;
	}

	public MNumber copy() {
		return new MOpNumber(this);
	}

	public MNumber set(MNumber aNumber) {
		if (aNumber instanceof MOpNumber) {
			m_operation = new Operation(((MOpNumber) aNumber).m_operation);
			m_normalForm = m_operation.isNormalForm();
			m_usesVariables = ((MOpNumber)aNumber).getUsedVariables();
		} else
			m_operation = new Operation(aNumber.getClass(), aNumber.toString(), m_normalForm);
		m_operation.setNormalForm(m_normalForm);
		updateResult();
		return this;
	}

	public MNumber set(Class numberClass, String anOperation) {
		Operation newOperation = new Operation(numberClass, anOperation, m_normalForm);
		if (!isValidOperation(newOperation)) throw new IllegalArgumentException("not valid operation for this MOpNumber");
		m_operation = new Operation(numberClass, anOperation, m_normalForm);
		updateResult();
		return this;
	}

	public String getDomainString() {
		return null;
	}

	public String toString() {
		return m_operation.toString();
	}

	public Node getMathMLNode() {
		return getMathMLNode(XMLUtils.getDefaultDocument());
	}

	public Node getMathMLNode(Document doc) {
		if (isEdited()) {
			return ExerciseObjectFactory.exportExerciseAttributes(m_operation.getMathMLNode(doc), this);
		} else {
			return ExerciseObjectFactory.createUneditedNode(this, doc);
		}
	}
	
	public void setMathMLNode(Node content) {
		m_operation.setOpRoot(XMLUtils.parseMathMLNode(getNumberClass(), content));
		setEdited(m_operation.getOpRoot().isEdited());
		ExerciseObjectFactory.importExerciseAttributes(content, this);
		m_operation.setNormalForm(m_normalForm);
		updateResult();
	}

	public Class getNumberClass() {
		return m_operation.getNumberClass();
	}
	
	public void setEdited(boolean edited) {
		super.setEdited(edited);
		m_operation.setEdited(edited);
	}

	public void setNormalForm(boolean normalize) {
		if (m_normalForm == normalize)
			return;
		m_normalForm = normalize;
		m_operation.setNormalForm(normalize);
	}

	public boolean isNormalForm() {
		return m_normalForm;
	}

	public Class getMMClass() {
		return MMOpNumber.class;
	}
	
	public MNumber round(int accuracy) {
		if (m_cachedNumber instanceof MDouble)
			((MDouble)m_cachedNumber).round(accuracy);
		return this;
	}
	  
	public MNumber rounded(int accuracy) {
		return ((MOpNumber)this.copy()).round(accuracy);
	}
		
	public boolean equals(MNumber aNumber, int accuracy) {
		return this.rounded(accuracy).equals(aNumber);
	}
	
	public boolean isPolynomial() {
		return m_operation.isPolynomial();
	}
	
	public boolean isValidOperation(Operation aOperation) {
		int usedVariablesCount = aOperation.getUsedVariables().length;
		if (m_usesVariables || !(usedVariablesCount > 0)) return true;
		return false;
	}
	
	public void setUsedVariables(boolean b) {
		m_usesVariables = b;
	}
	
	public boolean getUsedVariables() {
		return m_usesVariables;
	}
	
	public static Class getOpClass(Class numberClass) {
		if(MDouble.class.isAssignableFrom(numberClass))
			return MOpDouble.class;
		if(MRational.class.isAssignableFrom(numberClass))
			return MOpRational.class;
		if(MComplex.class.isAssignableFrom(numberClass))
			return MOpComplex.class;
		if(MComplexRational.class.isAssignableFrom(numberClass))
			return MOpComplexRational.class;
		if(MInteger.class.isAssignableFrom(numberClass))
			return MOpInteger.class;
		return null;
	}
}

class MOpDouble extends MOpNumber {}

class MOpRational extends MOpNumber {}

class MOpComplex extends MOpNumber {}

class MOpComplexRational extends MOpNumber {}

class MOpInteger extends MOpNumber {}