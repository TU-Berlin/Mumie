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

import net.mumie.mathletfactory.action.message.TodoException;
import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.algebra.rel.node.AllRel;
import net.mumie.mathletfactory.math.algebra.rel.node.AndRel;
import net.mumie.mathletfactory.math.algebra.rel.node.NullRel;
import net.mumie.mathletfactory.math.algebra.rel.node.RelNode;
import net.mumie.mathletfactory.math.algebra.rel.node.SimpleRel;
import net.mumie.mathletfactory.math.number.MBigRational;
import net.mumie.mathletfactory.math.number.MComplex;
import net.mumie.mathletfactory.math.number.MInteger;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.MRational;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *  Represents the object for raising to power in the Op-Scheme.
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */

public class PowerOp extends OpNode {

  public PowerOp(Class entryClass){
    super(entryClass);
  }

  public PowerOp(OpNode child1, OpNode child2){
    super(child1, child2);
  }

  public PowerOp(OpNode child1, double child2Value){
    this(child1, new NumberOp(child1.getNumberClass(), child2Value));
  }

  public int getMinChildrenNr(){
    return 2;
  }

  public int getMaxChildrenNr(){
    return 2;
  }


  public String nodeToString(){
    return "^";
  }

  protected String nodeToOpML(){
    return "pow";
  }

  public String toString(int printFlags, DecimalFormat format){
    boolean parenthesesNeeded = false;
    if(!m_children[0].getFactorString(PRINT_ALL, format).equals(""))
      parenthesesNeeded = true;
    //System.out.println("parenthesesNeeded for "+toDebugString()+" is "+parenthesesNeeded);
    String exp = m_children[1].toString(format);
    boolean printPForExp = m_children[1].parenthesesNeeded() && !exp.startsWith( "(" ) && !exp.endsWith( "" );
//    boolean pForExponentNeeded = (printFlags & PRINT_PARENTHESES) != 0;
    return printFactorAndExponent((parenthesesNeeded ? "(" : "") + m_children[0].toString(format) + (parenthesesNeeded ? ")" : "")+"^"
            +(printPForExp ? "(" : "") + m_children[1].toString(format)+(printPForExp ? ")" : ""), printFlags, format);
  }

  protected String nodeToContentMathML(){
    return "<apply> <power/> "+m_children[0].toContentMathML()+" "+m_children[1].toContentMathML()+" </apply>";
  }

  /** simply calls MNumber.power() */
  protected void calculate(){
    m_base = m_children[0].getResult().power(m_children[1].getResult());
  }

  /** Simply calls Math.pow(child1, child2). */
  protected void calculateDouble(){
    m_base.setDouble(Math.pow(m_children[0].getResultDouble(),m_children[1].getResultDouble()));
  }

  public RelNode getMonotonicDecreasingRel(OpNode operand){
    if(m_children[1].getChildrenWithVariablesCount() == 0){
      MNumber exponent = m_children[1].getResult();
      if( exponent.getDouble() > 0)
        return new NullRel(m_numberClass);
      if(exponent.getDouble() < 0){
        if(exponent.isRational() && new MRational(exponent).getNumerator() % 2 != 0)
          return getNodeDefinedRel(operand);
        else
          return new AndRel(getNodeDefinedRel(operand), new SimpleRel(new Operation((OpNode)operand.clone()), "<"));
      }
    }
    throw new TodoException();
  }

  public RelNode getMonotonicIncreasingRel(OpNode operand){
    if(m_children[1].getChildrenWithVariablesCount() == 0){
      MNumber exponent = m_children[1].getResult();
      if( exponent.getDouble() > 0)
        return getNodeDefinedRel(operand);
      if(exponent.getDouble() < 0){
        if(exponent.isRational() && new MRational(exponent).getNumerator() % 2 != 0)
          return new NullRel(m_numberClass);
        else
          return new AndRel(getNodeDefinedRel(operand), new SimpleRel(new Operation((OpNode)operand.clone()), ">"));
      }
    }
    throw new TodoException();
  }

  public RelNode getNodeDefinedRel(OpNode operand){
    // handle complex case first
    if(MComplex.class.isAssignableFrom(getNumberClass()))
      if(m_children[1].getResult().getDouble() >= 0)
        return new AllRel(m_numberClass);
      else
        return new SimpleRel(new Operation((OpNode)operand.clone()), "!=");
    if(m_children[1].getChildrenWithVariablesCount(false) == 0 && ((m_children[1].getResult().isInteger()))){
    //|| (m_children[1].getResult().isRational()) && new MRational(m_children[1].getResult()).getDenominator() % 2 != 0))){
      if(m_children[1].getResult().getDouble() >= 0)
        return new AllRel(m_numberClass);
      else
        return new SimpleRel(new Operation((OpNode)operand.clone()), "!=");
    }
    return new SimpleRel(new Operation((OpNode)operand.clone()),">");
  }

  public RelNode getZeroRel(OpNode operand){
    return new NullRel(m_numberClass);
  }

  public RelNode getZeroRel(){
    return new NullRel(m_numberClass);
  }

  public OpNode[] solveStepFor(String identifier){
    if(m_children[0].getChildrenWithVariableCount(identifier) > 0 &&
       m_children[1].getChildrenWithVariableCount(identifier) > 0) // has the form a(x)^c(x) (not solvable by this method)
      return null;
    if(m_children[1].getChildrenWithVariableCount(identifier) > 0){ // has the form c^a(x)
      OpNode lnBase = new LnOp(m_children[0]);
      lnBase.negateExponent();
      return new OpNode[]{new MultOp(new OpNode[]{new LnOp(new VariableOp(m_numberClass, REPLACEMENT_IDENTIFIER)), lnBase})};
    }
    // has the form a(x)^c
    OpNode inverseExponent = ((OpNode)m_children[1].clone());
    if(inverseExponent.getResult().getDouble() % 2 == 0)
      inverseExponent = new AbsOp(inverseExponent);
    inverseExponent.setFactor(inverseExponent.getFactor().inverse());
    inverseExponent.negateExponent();
    return new OpNode[]{new PowerOp(new VariableOp(m_numberClass, REPLACEMENT_IDENTIFIER), inverseExponent)};
  }

   /**
   *  Implements <i>(a(x) ^ c)' = c * a(x) ^ (c-1) *  a'(x)</i> and
   *  <i>(c^a(x))' = c^a(x) * ln(c) * a'(x)</i>.
   */
  public OpNode getDerivative(String variable){
    if(getMaxAbsPowerOfVariable(variable) == 0)
      return new NumberOp(m_numberClass, 0);

    OpNode derivedPowerOp = new MultOp(m_numberClass);

    //(c^a(x))' = c^a(x) * ln(c) * a'(x).
    if(m_children[0].getMaxAbsPowerOfVariable(variable) == 0){
      if(m_children[0] instanceof VariableOp && ((VariableOp)m_children[0]).getIdentifier().equals("e"))
        return new MultOp(new OpNode[]{(OpNode)clone(),(OpNode)m_children[1].getDerivative(variable)});
      derivedPowerOp.setChildren(new OpNode[]{(OpNode)clone(),new LnOp((OpNode)m_children[0].clone())});
      return new MultOp(new OpNode[]{derivedPowerOp,(OpNode)m_children[1].getDerivative(variable)});
    }

    if(m_children[1].getMaxAbsPowerOfVariable(variable) != 0){
      throw new TodoException("not yet implemented!");
    }
    // a(x)^(c-1)
    OpNode reducedPower = new PowerOp(m_numberClass);
    OpNode cMinusOne = new AddOp(m_numberClass);
    cMinusOne.setChildren(new OpNode[]{(OpNode)m_children[1].clone(), new NumberOp(m_numberClass, -1)});
    reducedPower.setChildren(new OpNode[]{(OpNode)m_children[0].clone(), cMinusOne});

    // c * a(x)^(c-1)
    OpNode product = new MultOp(m_numberClass);
    product.setChildren(new OpNode[]{(OpNode)m_children[1].clone(), reducedPower});

    // c * a(x) ^ (c-1) * a'(x)
    derivedPowerOp.setChildren(new OpNode[]{product, m_children[0].getDerivative(variable)});
    return derivedPowerOp;
  }
  
  

    public Node getMathMLNode(Document doc) {
		boolean usesParantheses = m_children[0].getFactor().getDouble() < 0 || m_children[0].needsParanthesises() ||
				(m_children[0] instanceof NumberOp && ((getNumberClass().isAssignableFrom(MRational.class))
				|| (getNumberClass().isAssignableFrom(MBigRational.class))));
		
		Node basis, mo;
		Element mElem;
		
		if (usesParantheses) {
			basis = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mrow");
			mo = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mo");
			mo.appendChild(doc.createTextNode("("));
			basis.appendChild(mo);
			basis.appendChild(m_children[0].getMathMLNode(doc));
			mo = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mo");
			mo.appendChild(doc.createTextNode(")"));
			basis.appendChild(mo);
		}
		else {
			basis = doc.createDocumentFragment();
			basis.appendChild(m_children[0].getMathMLNode(doc));
		}

//		if (!((m_children[1] instanceof NumberOp) && m_children[1].getResult().getDouble() == 1.0)) {
		if ((m_children[1] instanceof NumberOp) && m_children[1].getResult().getDouble() == -1.0) {
				mElem = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mfrac");
				mElem.appendChild(new MInteger(1).getMathMLNode(doc));
				mElem.appendChild(basis);
				basis = mElem;
		} else {
				Element msup = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "msup");
				msup.appendChild(basis);
				Node exponent = doc.createDocumentFragment();
				exponent.appendChild(m_children[1].getMathMLNode(doc));
				msup.appendChild(exponent);
				// if ((m_children[1] instanceof NumberOp) &&
				// m_children[1].getResult().getDouble() < 0) {
				// mElem = doc.createElementNS(XMLUtils.MATHML_NAMESPACE,
				// "mfrac");
				// mElem.appendChild(new MInteger(1).getMathMLNode());
				// mElem.appendChild(msup);
				// basis = mElem;
				// } else {
				basis = msup;
				// }
		}
//		}
		return writeFactorAndExponent(this, basis);
	}
  
  protected boolean needsParanthesises() {
  	return false;
  }
}
  




