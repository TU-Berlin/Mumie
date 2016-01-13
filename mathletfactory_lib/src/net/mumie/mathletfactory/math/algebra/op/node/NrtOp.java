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
import net.mumie.mathletfactory.math.algebra.rel.node.NullRel;
import net.mumie.mathletfactory.math.algebra.rel.node.RelNode;
import net.mumie.mathletfactory.math.algebra.rel.node.SimpleRel;
import net.mumie.mathletfactory.math.number.MComplex;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MInteger;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 *  Represents the object for taking the n-th root in the Op-Scheme
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class NrtOp extends OpNode {

  /** by default this is the operation for a square root */
  private int n=2;

  public NrtOp(OpNode child, int n){
    super(child);
    setN(n);
  }

  public NrtOp(Class entryClass){
    super(entryClass);
  }

  public NrtOp(Class entryClass, int n){
    super(entryClass);
    setN(n);
  }

  public void setN(int n){
    if(n<2)
      throw new IllegalArgumentException("n must be >= 2!");
    this.n = n;
  }

  public int getN(){
    return n;
  }

  public int getMinChildrenNr(){
    return 1;
  }

  public int getMaxChildrenNr(){
    return 1;
  }

  public String nodeToString(){
    return n == 2 ? "sqrt" : "("+n+"th)-rt";
  }

  public String toString(int printFlags, DecimalFormat format){
    if(n == 2)
      return printFactorAndExponent(nodeToString()+"("+m_children[0].toString(format)+")", printFlags, format);
    if (n == 3) {
    	String child = m_children[0].toString(format);
    	if (child.startsWith( "(" ) && child.endsWith( "" )) {
    		return printFactorAndExponent("cbrt"+m_children[0].toString(format), printFlags, format);	
    	} else {
    		return printFactorAndExponent("cbrt"+"("+m_children[0].toString(format)+")", printFlags, format);
    	}
    }
    else {
    	if (!this.m_children[0].isLeaf()) {
    		return printFactorAndExponent("("+m_children[0].toString(format)+")#" + n, printFlags, format);
    	}
    	else {
    		return printFactorAndExponent(m_children[0].toString(format)+"#" + n, printFlags, format);
    	}
    }
  }

  protected String nodeToContentMathML(){
    if(n == 2)
      return "<apply> <sqrt/> "+m_children[0].toContentMathML()+" </apply>";
    return "<apply> <root/> "+getN()+" "+m_children[0].toContentMathML()+" </apply>";
  }

  /** Simply calls MNumber.power(1/n, child) */
  protected void calculate(){
    m_base = m_children[0].getResult().power(NumberFactory.newInstance(MDouble.class, 1./n));
  }

  /** Simply calls Math.pow(child,1/n). */
  protected void calculateDouble(){
    if(n % 2 == 0)
      m_base.setDouble(Math.pow(m_children[0].getResultDouble(), 1./n));
    else{ //odd n
      double result = m_children[0].getResultDouble();
      m_base.setDouble((result < 0 ? -1 : 1) * Math.pow(Math.abs(result),1./n));
    }
  }

  public RelNode getMonotonicDecreasingRel(OpNode operand){
    return new NullRel(m_numberClass);
  }

  public RelNode getMonotonicIncreasingRel(OpNode operand){
    return getNodeDefinedRel(operand);
  }

  public RelNode getNodeDefinedRel(OpNode operand){
    if(n % 2 == 0 && !MComplex.class.isAssignableFrom(getNumberClass()))
      return new SimpleRel(new Operation((OpNode)operand.clone()), Relation.GREATER_OR_EQUAL);
    return new AllRel(m_numberClass);
  }

  public RelNode getZeroRel(OpNode operand){
    return new SimpleRel(new Operation((OpNode)operand.clone()), Relation.EQUAL);
  }

  public OpNode[] solveStepFor(String identifier){
    return new OpNode[]{new PowerOp(new VariableOp(m_numberClass, REPLACEMENT_IDENTIFIER), n)};
  }

  /**
   *  Implements <i>(a(x)^(1/n))' = (1/n) * a(x) ^ (1/n-1) *  a'(x)</i>.
   */
  public OpNode getDerivative(String variable){
    if(getMaxAbsPowerOfVariable(variable) == 0)
      return new NumberOp(m_numberClass, 0);

    OpNode derivedSqrtOp = new MultOp(m_numberClass);

    // a(x)^(1/n-1)
    OpNode newExponent = new NumberOp(m_numberClass, 1./n-1);
    OpNode power = new PowerOp(m_numberClass);
    power.setChildren(new OpNode[]{(OpNode)m_children[0].clone(), newExponent});

    // a(x)^(1/n-1)/n
    OpNode quotient = new MultOp(m_numberClass);
    OpNode oneDivByN = new NumberOp(m_numberClass, n);
    oneDivByN.m_exponent = -1;
    quotient.setChildren(new OpNode[]{power, oneDivByN});

    // a(x)^(1/n-1)/n * a'(x)
    derivedSqrtOp.setChildren(new OpNode[]{quotient, m_children[0].getDerivative(variable)});
    return deriveNode(derivedSqrtOp);
  }

    public Node getMathMLNode(Document doc) {
    DocumentFragment fragment = doc.createDocumentFragment();
  	
    Element mroot;
    if (n == 2) {
    	mroot = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "msqrt");
    	mroot.appendChild(m_children[0].getMathMLNode(doc));
    }
    else {
    	mroot = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mroot");
    	mroot.appendChild(m_children[0].getMathMLNode(doc));
    	mroot.appendChild(new MInteger(getN()).getMathMLNode(doc));
    }
    
	fragment.appendChild(mroot);
    return writeFactorAndExponent(this, fragment);
  }
}