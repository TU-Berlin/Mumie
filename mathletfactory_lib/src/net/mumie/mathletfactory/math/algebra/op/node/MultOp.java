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
import java.util.Vector;

import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.algebra.rel.Relation;
import net.mumie.mathletfactory.math.algebra.rel.node.AllRel;
import net.mumie.mathletfactory.math.algebra.rel.node.NullRel;
import net.mumie.mathletfactory.math.algebra.rel.node.OrRel;
import net.mumie.mathletfactory.math.algebra.rel.node.RelNode;
import net.mumie.mathletfactory.math.algebra.rel.node.SimpleRel;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *  Represents the object for Multiplication in the Op-Scheme
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */

public class MultOp extends OpNode {

  public MultOp(Class entryClass) {
    super(entryClass);
  }

  public MultOp(OpNode[] children) {
    super(children);
  }

  public int getMinChildrenNr() {
    return 2;
  }

  public int getMaxChildrenNr() {
    return Integer.MAX_VALUE;
  }

  public String nodeToString() {
    return "*";
  }

  public boolean isCommutativeOperation() {
    return true;
  }

  public String toString(int printFlags, DecimalFormat format) {
    String retVal = m_children[0].toString(format);
    for (int i = 1; i < m_children.length; i++) {
      if (m_children[i].getExponent() < 0)
        retVal += "/" + m_children[i].toString(PRINT_ALL & ~PRINT_EXPONENT_SIGN, format);
      else
        retVal += "*" + m_children[i].toString(format);
    }
    return printFactorAndExponent(retVal, printFlags, format);
  }

  protected String nodeToContentMathML() {
    String retVal = "<apply> <times/> ";
    for (int i = 0; i < m_children.length; i++)
      retVal += m_children[i].toContentMathML() + " ";
    return retVal + "</apply>";
  }

  protected String nodeToOpML() {
    return "mul";
  }

  /** simply calls MNumber.mult(). */
  protected void calculate() {
    m_base = m_children[0].getResult();
    for (int i = 1; i < m_children.length; i++)
      m_base.mult(m_children[i].getResult());
  }

  /** Simply multiplies the children. */
  protected void calculateDouble(){
    double result = 1;
    for(int i=0;i<m_children.length;i++)
      result *= m_children[i].getResultDouble();
    m_base.setDouble(result);
  }

  public RelNode getMonotonicDecreasingRel(OpNode operand) {
    // compute the number of children with variables (one less since the REPLACEMENT_IDENTIFIER is not counted)
    int childrenWithVariables = getChildrenWithVariablesCount() - 1;

    OpNode[] relOpChildren = new OpNode[childrenWithVariables];
    int counter = 0;
    int signOfConstantsFactors = 1;
    for (int i = 0; i < m_children.length; i++) {
      // if child is an expression with variables, create a SimpleRel with
      // expr > 0
      if (m_children[i].getChildrenWithVariablesCount() != 0
          && m_children[i].getChildrenWithVariableCount(REPLACEMENT_IDENTIFIER) == 0)
        relOpChildren[counter++] = (OpNode)m_children[i].clone();
      else
        if(m_children[i].getChildrenWithVariableCount(REPLACEMENT_IDENTIFIER) == 0)
          signOfConstantsFactors *= m_children[i].getResult().getSign();

    }
    if(childrenWithVariables == 0){
      if(signOfConstantsFactors <= 0)
        return new AllRel(m_numberClass);
      else
        return new NullRel(m_numberClass);
    }
    // case with one or more variable expressions
    if(childrenWithVariables == 1)
      return new SimpleRel(new Operation(relOpChildren[0]), signOfConstantsFactors >= 0 ? "<=" : ">=");
    return new SimpleRel(new Operation(new MultOp(relOpChildren)), signOfConstantsFactors >= 0 ? "<=" : ">=");
  }

  public RelNode getMonotonicIncreasingRel(OpNode operand) {
    // compute the number of children with variables
    int childrenWithVariables = getChildrenWithVariablesCount() - 1;
    OpNode[] relOpChildren = new OpNode[childrenWithVariables];
    int counter = 0;
    int signOfConstantsFactors = 1;

    for (int i = 0; i < m_children.length; i++) {
      // if child is an expression with variables, create a SimpleRel with
      // expr > 0
      if (m_children[i].getChildrenWithVariablesCount() != 0
          && m_children[i].getChildrenWithVariableCount(REPLACEMENT_IDENTIFIER) == 0)
        relOpChildren[counter++] = (OpNode)m_children[i].clone();
      else
        if(m_children[i].getChildrenWithVariableCount(REPLACEMENT_IDENTIFIER) == 0)
          signOfConstantsFactors *= m_children[i].getResult().getSign();

    }
    if(childrenWithVariables == 0){
      if(signOfConstantsFactors >= 0)
        return new AllRel(m_numberClass);
      else
        return new NullRel(m_numberClass);
    }
    // case with one or more variable expressions
    if(childrenWithVariables == 1)
      return new SimpleRel(new Operation(relOpChildren[0]), signOfConstantsFactors >= 0 ? ">=" : "<=");
    return new SimpleRel(new Operation(new MultOp(relOpChildren)), signOfConstantsFactors >= 0 ? ">=" : "<=");
  }

  public RelNode getNodeDefinedRel(OpNode operand) {
    return new AllRel(m_numberClass);
  }

  public RelNode getZeroRel(){
    int counter=0;
    RelNode[] relChildren = new RelNode[m_children.length];
    for (int i = 0; i < m_children.length; i++)
      relChildren[i] = new SimpleRel(new Operation((OpNode)m_children[i].clone()), Relation.EQUAL);
    return new OrRel(relChildren);

  }

  public RelNode getZeroRel(OpNode operand) {
    OpNode[] newChildren = new OpNode[m_children.length+1];
    int counter=0;
    for(int i=0;i<newChildren.length;i++){
      if(m_children[i] instanceof VariableOp && ((VariableOp)m_children[i]).getIdentifier()
        .equals(REPLACEMENT_IDENTIFIER))
        newChildren[counter++] = (OpNode)operand.clone();
      else
        newChildren[counter++] = (OpNode)m_children[i].clone();
    }
    RelNode[] relChildren = new RelNode[newChildren.length];
    for (int i = 0; i < newChildren.length; i++)
      relChildren[i] = new SimpleRel(new Operation(newChildren[i]), Relation.EQUAL);
    return new OrRel(relChildren);
  }

  public OpNode[] solveStepFor(String identifier){

    OpNode[] tmpRightHandSideChildren = new OpNode[m_children.length];
    int newRightHandSideChildrenCount = 0, newLeftHandSideChildrenCount = 0;

    //  put all the children not containing variables of the given identifier
    //  to the right hand side
    for(int i=0;i<m_children.length;i++)
      if(m_children[i].getChildrenWithVariableCount(identifier) == 0)
        tmpRightHandSideChildren[newRightHandSideChildrenCount++]
          = ((OpNode)m_children[i].clone()).negateExponent();

    // put the new children into an array of the correct size
    OpNode[] rightHandSideChildren = new OpNode[newRightHandSideChildrenCount+1];
    System.arraycopy(tmpRightHandSideChildren, 0, rightHandSideChildren, 0,
                     newRightHandSideChildrenCount);
    rightHandSideChildren[newRightHandSideChildrenCount] = new VariableOp(m_numberClass, REPLACEMENT_IDENTIFIER);
    return new OpNode[]{new MultOp(rightHandSideChildren)};
  }

  /**
   *  Implements <i>(a(x) * b(x) * ...)' = a'(x) * b(x) * ... + a(x) * b'(x) * ... + ...</i>.
   */
  public OpNode getDerivative(String variable) {
    if (getMaxAbsPowerOfVariable(variable) == 0)
      return new NumberOp(m_numberClass, 0);

    OpNode derivedMultOp = new AddOp(m_numberClass);
    OpNode[] products = new OpNode[m_children.length];

    for (int i = 0; i < m_children.length; i++) {
      products[i] = new MultOp(m_numberClass);
      OpNode[] factors = new OpNode[m_children.length];
      if (m_children[i].getMaxAbsPowerOfVariable(variable) == 0)
        products[i] = new NumberOp(m_numberClass, 0);
      else {
        factors[i] = m_children[i].getDerivative(variable);
        for (int j = 0; j < m_children.length; j++)
          if (j != i)
            factors[j] = (OpNode) m_children[j].clone();
        products[i].setChildren(factors);
      }
    }
    derivedMultOp.setChildren(products);
    return deriveNode(derivedMultOp);
  }

    public Node getMathMLNode(Document doc) {
		Node addEl = doc.createDocumentFragment();
		boolean mfrac = false;

		Vector nodes[] = new Vector[2];
		nodes[0] = new Vector();
		nodes[1] = new Vector();

		OpNode tmp;
		for (int i = 0; i < m_children.length; i++) {
			if (m_children[i].getExponent() < 0) {
				tmp = m_children[i].deepCopy(false, false);
				tmp.setExponent(Math.abs(m_children[i].getExponent()));
				nodes[1].add(tmp);
			} else {
				nodes[0].add(m_children[i]);
			}
		}

		Element mrow = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mrow");
		Node mElem, mo;

		if (nodes[1].size() > 0) {
			mElem = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mfrac");
			mfrac = true;
		} else {
			mElem = mrow;
		}

		for (int i = 0; i < nodes.length; i++) {
			Node mfracrow = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mrow");
			for (int j = 0; j < nodes[i].size(); j++) {
				boolean userParantheses = (((OpNode) nodes[i].get(j)).getFactor().getDouble() < 0 && i > 0) 
											|| (((OpNode)nodes[i].get(j)).getFactor().getDouble() > 0 
											&& ((OpNode)nodes[i].get(j)).needsParanthesises());
				if (j > 0) {// not for first element!
					mo = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mo");
					mo.appendChild(doc.createTextNode("*"));
					if (mfrac)
						mfracrow.appendChild(mo);
					else
						mrow.appendChild(mo);
				}
				if (userParantheses) {
					mo = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mo");
					mo.appendChild(doc.createTextNode("("));
					if (mfrac)
						mfracrow.appendChild(mo);
					else
						mrow.appendChild(mo);
				}

				if (mfrac)
					mfracrow.appendChild(((OpNode) nodes[i].get(j))
							.getMathMLNode(doc));
				else
					mrow.appendChild(((OpNode) nodes[i].get(j))
							.getMathMLNode(doc));

				if (userParantheses) {
					mo = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mo");
					mo.appendChild(doc.createTextNode(")"));
					if (mfrac)
						mfracrow.appendChild(mo);
					else
						mrow.appendChild(mo);
				}
			}
			if (mfrac)
				mElem.appendChild(mfracrow);
		}
		// mElem.appendChild(doc.createElementNS(XMLUtils.MATHML_NAMESPACE,
		// "mrow"));
		// addEl.appendChild(mElem);
		if (mfrac)
			mrow.appendChild(mElem);

		addEl.appendChild(mrow);
		return writeFactorAndExponent(this, addEl);
	}
}