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

import net.mumie.mathletfactory.math.algebra.rel.node.AllRel;
import net.mumie.mathletfactory.math.algebra.rel.node.RelNode;
import net.mumie.mathletfactory.math.number.MInteger;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Represents a number in the Op-Scheme. numbers and {@link VariableOp variable}s
 * are the leaves in the Operation Tree, therefore they simply return the
 * argument handed over to them in the constructor
 * 
 * @author Paehler
 * @mm.docstatus finished
 */

public class NumberOp extends OpNode {

	/** Keep the argument as the result */
	public NumberOp(MNumber arg) {
		super(arg.getClass());
		m_base = arg;
	}

	public NumberOp(Class entryClass, double arg) {
		super(entryClass);
		m_base = NumberFactory.newInstance(entryClass);
		m_base.setDouble(arg);
	}
	
	public int getMinChildrenNr() {
		return 0;
	}

	public int getMaxChildrenNr() {
		return 0;
	}

	public String nodeToString() {
		return m_base.toString();
	}

	protected String nodeToContentMathML() {
		return m_base.toContentMathML();
	}

	/***************************************************************************
	 * public OpNode negateExponent(){ m_base.inverse(); m_factor.inverse();
	 * return this; }
	 * 
	 * public void setExponent(int exponent){ m_base.power(exponent);
	 * m_factor.power(exponent); }
	 * 
	 * public OpNode multiplyExponent(int exponent){ m_base.power(exponent);
	 * m_factor.power(exponent); return this; }
	 **************************************************************************/

	/** Return the resulting number. */
	public MNumber getResult() {
		// because the m_base of a Numeral is passed as reference, it needs to
		// be
		// deep-copied
		return m_base.copy().power(m_exponent).mult(m_factor);
	}

	public void setResult(MNumber value) {
		m_base = value;
	}

	public String nodeToDebugString() {
		return toString();
	}

	public String toString(int printFlags, DecimalFormat format) {
		return printFactorAndExponent(m_base.toString(format), printFlags,
				format);
	}

	/** Does nothing, since the result is already set in constructor. */
	protected void calculate() {
	}

	/** Does nothing, since the result is already set in constructor. */
	protected void calculateDouble() {
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

	public RelNode getZeroRel(OpNode operand) {
		if (getResult().isZero())
			return getNodeDefinedRel(null);
		return null;
	}

	public OpNode[] solveStepFor(String identifier) {
		return new OpNode[0];
	}

	/**
	 * Implements <i>d/dx c = 0</i>.
	 */
	public OpNode getDerivative(String variable) {
		return new NumberOp(m_numberClass, 0);
	}

	public Node getMathMLNode(Document doc) {
		Node fragment = doc.createDocumentFragment();

		if (m_base.equals(NumberFactory.newInstance(getNumberClass(),1))|| m_base.isZero()
				|| getFactor().isZero() || getExponent() == 1 || getExponent() == 0) {
			fragment.appendChild(getResult(PRINT_ALL).getMathMLNode(doc));
			return fragment;
		}

		Node result = doc.createDocumentFragment();
		Element node;

		if (getExponent() == -1) {
			node = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mfrac");
			node.appendChild(getFactor().getMathMLNode(doc));
			node.appendChild(m_base.getMathMLNode(doc));
		} else {
			Element msup = doc.createElementNS(XMLUtils.MATHML_NAMESPACE,
					"msup");
			msup.appendChild(getMathMLNodeFor(doc, m_base, getExponent()));
			if (getExponent() < -1) {
				node = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mfrac");
				node.appendChild(getFactor().getMathMLNode(doc));
				msup.appendChild(new MInteger(-1 * getExponent())
						.getMathMLNode(doc));
				node.appendChild(msup);
			} else {
				msup
						.appendChild(new MInteger(getExponent())
								.getMathMLNode(doc));
				if (getFactor().getDouble() == 1.0) {
					node = msup;
				} else if (getFactor().getDouble() == -1.0) {
					node = doc.createElementNS(XMLUtils.MATHML_NAMESPACE,
							"mrow");
					Element mi = doc.createElementNS(XMLUtils.MATHML_NAMESPACE,
							"mo");
					mi.appendChild(doc.createTextNode("-"));
					node.appendChild(mi);
					node.appendChild(msup);
				} else {
					node = doc.createElementNS(XMLUtils.MATHML_NAMESPACE,
							"mrow");
					Element mi = doc.createElementNS(XMLUtils.MATHML_NAMESPACE,
							"mi");
					mi.appendChild(doc.createTextNode("*"));
					node.appendChild(getFactor().getMathMLNode(doc));
					node.appendChild(mi);
					node.appendChild(msup);
				}
			}
		}
		result.appendChild(node);
		return result;
	}

	private Node getMathMLNodeFor(Document doc, MNumber number, int exponent) {
		Node numberNode, result;
		numberNode = number.getMathMLNode(doc);
		if ((numberNode.getNodeName().equalsIgnoreCase("mfrac") && (Math
				.abs(exponent) != 1 || exponent != 0))
				|| number.getDouble() < 0) {
			result = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mrow");
			Element mo = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mo");
			mo.appendChild(doc.createTextNode("("));
			result.appendChild(mo);
			result.appendChild(numberNode);
			mo = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mo");
			mo.appendChild(doc.createTextNode(")"));
			result.appendChild(mo);
		} else {
			result = number.getMathMLNode(doc);
		}
		return result;
	}
}
