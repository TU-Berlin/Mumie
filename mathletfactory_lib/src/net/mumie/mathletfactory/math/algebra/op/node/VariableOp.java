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

package net.mumie.mathletfactory.math.algebra.op.node;

import java.text.DecimalFormat;

import net.mumie.mathletfactory.math.algebra.op.OpParser;
import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.algebra.rel.node.AllRel;
import net.mumie.mathletfactory.math.algebra.rel.node.NullRel;
import net.mumie.mathletfactory.math.algebra.rel.node.RelNode;
import net.mumie.mathletfactory.math.algebra.rel.node.SimpleRel;
import net.mumie.mathletfactory.math.number.MComplex;
import net.mumie.mathletfactory.math.number.MComplexRational;
import net.mumie.mathletfactory.math.number.MInteger;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Represents a variable in the Op-Scheme. Variables are (like
 * {@link NumberOp numbers}) leaves in the Operation Tree, therefore they
 * return as result the value that was set in {@link #setValue(MNumber)}.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */

public class VariableOp extends OpNode {

	private String m_identifier;

	private MNumber m_value;

	public final int IS_CONSTANT = 0;

	public final int IS_PARAMETER = 1;

	public final int IS_VARIABLE = 2;

	/**
	 * Determines if this variable is a constant, a variable or a parameter
	 * (e.g. PI, i, etc.)
	 */
	private int m_kind = IS_VARIABLE;

	/** Keep the argument for distinction between different variables. */
	public VariableOp(Class entryClass, String identifier) {
		super(entryClass);
		setIdentifier(identifier);
		initializeConstants();
	}

	private void initializeConstants() {
		// handle constants
		if (m_identifier.equals("e")) {
			setValue(NumberFactory.newInstance(m_numberClass, Math.E));
			setConstant();
		}
		if (m_identifier.equals(OpParser.PI)) {
			setValue(NumberFactory.newInstance(m_numberClass, Math.PI));
			setConstant();
		}
		if (m_identifier.equals("i")) {
			if (MComplex.class.isAssignableFrom(m_numberClass)) {
				setValue(new MComplex(0, 1));
				setConstant();
			}
			if (MComplexRational.class.isAssignableFrom(m_numberClass)) {
				setValue(new MComplexRational(0, 1));
				setConstant();
			}
		}
	}

	/**
	 * Determines, which identifier is used for this variable. Needed for
	 * comparison and operations like {@link #getDerivative}. If for example
	 * the identifier is "x", this node can be added and simplified with other
	 * variable operations whose identifier is also "x".
	 */
	public void setIdentifier(String identifier) {
		m_identifier = identifier;
		m_identifier = m_identifier.replaceAll(OpParser._PLUS, "+");
		m_identifier = m_identifier.replaceAll(OpParser._MINUS, "-");
	}

	/**
	 * Returns the identifier for this variable operation.
	 * 
	 * @see #setIdentifier
	 */
	public String getIdentifier() {
		return m_identifier;
	}

	public int getMinChildrenNr() {
		return 0;
	}

	public int getMaxChildrenNr() {
		return 0;
	}

	public String nodeToString() {
		return m_identifier;
	}

	public String toString(int printFlags, DecimalFormat format) {
		return printFactorAndExponent(m_identifier, printFlags, format);
	}

	protected String nodeToContentMathML() {
		return "<ci>" + m_identifier + "</ci>";
	}

	/**
	 * Sets the value of the variable. This method is called for every element
	 * of the definition lattice, when the values are calculated
	 */
	public void setValue(MNumber value) {
		// the reference "walks" the tree up, so it needs be cloned
		m_value = value;
	}

	/**
	 * Sets the value of the variable as double.
	 */
	public void setValue(double value) {
		if (m_value == null)
			m_value = NumberFactory.newInstance(m_numberClass);
		m_value.setDouble(value);
	}

	/**
	 * Does nothing, since the result is already set in Constructor or in
	 * {@link #setValue(MNumber)}.
	 */
	protected void calculate() {
		if (isConstant())
			initializeConstants();
		if (m_value != null)
			m_base = m_value.copy();
		else
			m_base = NumberFactory.newInstance(m_numberClass);
	}

	/**
	 * Does nothing, since the result is already set in Constructor or in
	 * {@link #setValue(MNumber)}.
	 */
	protected void calculateDouble() {
		if (m_value != null)
			m_base = m_value;
		else
			m_base = NumberFactory.newInstance(m_numberClass);
	}

	public OpNode[] solveStepFor(String identifier) {
		return new OpNode[0];
	}

	public RelNode getMonotonicDecreasingRel(OpNode operand) {
		return getNodeDefinedRel(operand);
	}

	public RelNode getMonotonicIncreasingRel(OpNode operand) {
		return getNodeDefinedRel(operand);
	}

	public RelNode getNodeDefinedRel(OpNode operand) {
		return new AllRel(m_numberClass);
	}

	/**
	 * Phantom implementation. Should never be called.
	 * 
	 * @see net.mumie.mathletfactory.math.algebra.op.node.OpNode#getZeroRel(OpNode)
	 */
	public RelNode getZeroRel(OpNode operand) {
		return null;
	}

	public RelNode getZeroRel() {
		if (isConstant())
			if (getResult().isZero())
				return new AllRel(m_numberClass);
			else
				return new NullRel(m_numberClass);
		if (m_exponent < 0)
			return new AllRel(m_numberClass);
		return new SimpleRel(new Operation(this), "=");
	}

	/**
	 * Implements <i> d/dx x = 1; d/dy x = 0 </i> (if "x" is the identifier for
	 * this node).
	 */
	public OpNode getDerivative(String variable) {
		if (!variable.equals(m_identifier))
			return new NumberOp(m_numberClass, 0);

		return deriveNode(new NumberOp(m_numberClass, 1));
	}

	public void setConstant() {
		m_kind = IS_CONSTANT;
		if (m_parent != null)
			m_parent.setChildren(m_parent.getChildren());
	}

	public boolean isConstant() {
		return m_kind == IS_CONSTANT;
	}

	public void setParameter() {
		m_kind = IS_PARAMETER;
		if (m_parent != null)
			m_parent.setChildren(m_parent.getChildren());
	}

	public boolean isParameter() {
		return m_kind == IS_PARAMETER;
	}

	public void setVariable() {
		m_kind = IS_VARIABLE;
		if (m_parent != null)
			m_parent.setChildren(m_parent.getChildren());
	}

	public boolean isVariable() {
		return m_kind == IS_VARIABLE;
	}

	public Object clone() {
		VariableOp retVal = (VariableOp) super.clone();
		retVal.m_value = m_value;
		retVal.m_kind = m_kind;
		return retVal;
	}

	public Node getMathMLNode(Document doc) {
		Node fragment = doc.createDocumentFragment();

		if (getFactor().getDouble() == 0) {
			fragment.appendChild(NumberFactory.newInstance(getNumberClass()).setDouble(0).getMathMLNode(doc));
			return fragment;
		}

		Node result = doc.createDocumentFragment();
		Node node, identifier;
		
		identifier = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mi");
		identifier.appendChild(doc.createTextNode(getIdentifier()));

		if (getExponent() == -1) {
			node = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mfrac");
			node.appendChild(getFactor().getMathMLNode(doc));
			node.appendChild(identifier);
		} else if (getExponent() == 1) {
			if (getFactor().getDouble() == 1.0) {
				fragment.appendChild(identifier);
				return fragment;
			} else if (getFactor().getDouble() == -1.0) {
				node = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mrow");
				Element mi = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mo");
				mi.appendChild(doc.createTextNode("-"));
				node.appendChild(mi);
				node.appendChild(identifier);
				fragment.appendChild(node);
				return fragment; 
			} else {
				node = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mrow");
				node.appendChild(getFactor().getMathMLNode(doc));
				node.appendChild(identifier);
			}
		} else if (getExponent() == 0) {
			node = getFactor().getMathMLNode(doc);
		} else {		
			Element msup = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "msup");
			msup.appendChild(identifier);
			if (getExponent() < -1) {
				node = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mfrac");
				node.appendChild(getFactor().getMathMLNode(doc));
				msup.appendChild(new MInteger(-1 * getExponent()).getMathMLNode(doc));
				node.appendChild(msup);
			} else {
				msup.appendChild(new MInteger(getExponent()).getMathMLNode(doc));
				if (getFactor().getDouble() == 1.0) {
					node = msup;
				} else if (getFactor().getDouble() == -1.0) {
					node = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mrow");
					Element mi = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mo");
					mi.appendChild(doc.createTextNode("-"));
					node.appendChild(mi);
					node.appendChild(msup);
				} else {
					node = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mrow");
					node.appendChild(getFactor().getMathMLNode(doc));
					node.appendChild(msup);
				}
			}
		}
		result.appendChild(node);
		return result;
	}
}
