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

import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.algebra.rel.Relation;
import net.mumie.mathletfactory.math.algebra.rel.node.AllRel;
import net.mumie.mathletfactory.math.algebra.rel.node.RelNode;
import net.mumie.mathletfactory.math.algebra.rel.node.SimpleRel;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *  Represents the object for additions in the Op-Scheme
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */

public class AddOp extends OpNode {

  public AddOp(Class entryClass){
    super(entryClass);
  }

  public AddOp(OpNode[] children){
    super(children);
  }

  public int getMinChildrenNr(){
    return 2;
  }

  public int getMaxChildrenNr(){
    return Integer.MAX_VALUE;
  }

  public boolean isCommutativeOperation(){
    return true;
  }

  public String nodeToString(){
    return "+";
  }

  public String toString(int printFlags, DecimalFormat format){
    String retVal = m_children[0].toString(format);
    for(int i=1;i<m_children.length;i++)
      if (m_children[i].getFactor().getDouble() < 0)
        retVal += "-"+m_children[i].toString(PRINT_ALL & ~PRINT_SIGN, format);
      else
        retVal += "+"+m_children[i].toString(format);

    return printFactorAndExponent(retVal, printFlags, format);
  }

  protected String nodeToContentMathML(){
    String retVal = "<apply> <plus/> ";
    for(int i=0; i<m_children.length;i++)
      retVal += m_children[i].toContentMathML()+" ";
    return retVal+"</apply>";
  }

  protected String nodeToOpML(){
    return "add";
  }

  /** Simply calls MNumber.addTo() with all children as arguments. */
  protected void calculate(){
    m_base = m_children[0].getResult();
    for(int i=1;i<m_children.length;i++)
      m_base.add(m_children[i].getResult());
  }

  /** Simply adds the children. */
  protected void calculateDouble(){
    double result = 0;
    for(int i=0;i<m_children.length;i++)
      result += m_children[i].getResultDouble();
    m_base.setDouble(result);
  }

  public RelNode getMonotonicDecreasingRel(OpNode operand){
    return new AllRel(m_numberClass);
  }

  public RelNode getMonotonicIncreasingRel(OpNode operand){
    return new AllRel(m_numberClass);
  }

  public RelNode getNodeDefinedRel(OpNode operand){
    return new AllRel(m_numberClass);
  }

  public RelNode getZeroRel(OpNode operand){
    AddOp newAdd = (AddOp)clone();
    newAdd.replace((OpNode)operand.clone());
    return new SimpleRel(new Operation(newAdd), Relation.EQUAL);
  }

  public RelNode getZeroRel(){
    return new SimpleRel(new Operation((OpNode)clone()),"=");
  }

  public OpNode[] solveStepFor(String identifier){

    OpNode[] tmpRightHandSideChildren = new OpNode[m_children.length];
    int newRightHandSideChildrenCount = 0, newLeftHandSideChildrenCount = 0;

    //  put all the children not containing variables of the given identifier
    //  to the right hand side
    for(int i=0;i<m_children.length;i++)
      if(m_children[i].getChildrenWithVariableCount(identifier) == 0)
        tmpRightHandSideChildren[newRightHandSideChildrenCount++]
          = ((OpNode)m_children[i].clone()).negateFactor();

    // put the new children into an array of the correct size
    OpNode[] rightHandSideChildren = new OpNode[newRightHandSideChildrenCount+1];
    System.arraycopy(tmpRightHandSideChildren, 0, rightHandSideChildren, 0,
                     newRightHandSideChildrenCount);
    rightHandSideChildren[newRightHandSideChildrenCount] = new VariableOp(m_numberClass, REPLACEMENT_IDENTIFIER);
    return new OpNode[]{new AddOp(rightHandSideChildren)};
  }

  /**
   *  Implements <i>(a(x) + b(x) +...)' = a'(x) + b'(x) + ...</i>.
   */
  public OpNode getDerivative(String variable){
    if(getMaxAbsPowerOfVariable(variable) == 0)
      return new NumberOp(m_numberClass, 0);

    OpNode derivedAddOp = new AddOp(m_numberClass);

    OpNode[] derivedChildren = new OpNode[m_children.length];
    for(int i=0;i<m_children.length;i++)
      derivedChildren[i] = m_children[i].getDerivative(variable);
    derivedAddOp.setChildren(derivedChildren);
    return deriveNode(derivedAddOp);
  }
  

  public Node getMathMLNode(Document doc) {
    Node fragment = doc.createDocumentFragment();
   
    Element mo, mrow = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mrow");
    
    for(int i = 0; i < m_children.length; i++) {
    	boolean userParantheses = m_children[i].getFactor().getDouble() < 0 || m_children[i].needsParanthesises();
    	if(i > 0) {
        mo = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mo");
        OpNode n = (OpNode)m_children[i].clone();
        if(n.getFactor().getDouble() < 0) {
          mo.appendChild(doc.createTextNode("-"));
          n.negateFactor();
        } else {
          mo.appendChild(doc.createTextNode("+"));
        }
        mrow.appendChild(mo);
        if (userParantheses) {
        	mo = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mo");
        	mo.appendChild(doc.createTextNode("("));
        	mrow.appendChild(mo);
        }
        mrow.appendChild(n.getMathMLNode(doc));
        if (userParantheses) {
        	mo = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mo");
        	mo.appendChild(doc.createTextNode(")"));
        	mrow.appendChild(mo);
        }
      } else {
    	  if (userParantheses) {
          	mo = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mo");
          	mo.appendChild(doc.createTextNode("("));
          	mrow.appendChild(mo);
          }
    	  mrow.appendChild(m_children[i].getMathMLNode(doc));
    	  if (userParantheses) {
          	mo = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mo");
          	mo.appendChild(doc.createTextNode(")"));
          	mrow.appendChild(mo);
          }
      }
    }
    fragment.appendChild(mrow);
  	return writeFactorAndExponent(this, fragment);
	}
}