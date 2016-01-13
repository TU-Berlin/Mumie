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
import net.mumie.mathletfactory.math.number.MComplex;

/**
 *  Represents the object for Conjugated(z) in the Op-Scheme
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */

public class ConjOp extends FunctionOpNode {

  public ConjOp(Class entryClass){
    super(entryClass);
  }

  public ConjOp(OpNode child){
    super(child);
  }
  
  public String nodeToString(){
    return "conj";
  }
   
  public OpNode getInverseOp(OpNode newChild){
    return this;
  }
  
  /** Simply calls MComplex.getIm() from the child result. */
  protected void calculate(){
    if(m_numberClass.isAssignableFrom(MComplex.class))
      ((MComplex)m_base).set(((MComplex)m_children[0].getResult()).conjugate());
    else
      m_base = m_children[0].getResult();
  }
  
  /** Simply propagates child value. */
  protected void calculateDouble(){
    m_base.setDouble(m_children[0].getResultDouble());
  }
  
  public String toString(int printFlags, DecimalFormat format){
		String result = m_children[0].toString(format);
		if(result.startsWith("(") && result.endsWith(")"))
			return printFactorAndExponent(nodeToString()+result, printFlags, format);
	    else 
			return printFactorAndExponent(nodeToString()+"("+result+")", printFlags, format);
  }
  
  public RelNode getMonotonicDecreasingRel(OpNode operand){
    return operand.getMonotonicDecreasingRel();
  }
  
  public RelNode getMonotonicIncreasingRel(OpNode operand){
    return operand.getMonotonicIncreasingRel();
  }
  
  public RelNode getNodeDefinedRel(OpNode operand){
    return new AllRel(m_numberClass);
  }
  
  public RelNode getZeroRel(OpNode operand){
    return operand.getZeroRel();
  }
    
  /**
   * Should not be called.
   */
  public OpNode getDerivative(String variable){
    throw new IllegalArgumentException("Can not be derived!");
  } 
}
  


