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

import net.mumie.mathletfactory.math.algebra.rel.node.NotRel;
import net.mumie.mathletfactory.math.algebra.rel.node.NullRel;
import net.mumie.mathletfactory.math.algebra.rel.node.RelNode;
import net.mumie.mathletfactory.math.number.NumberFactory;

/** 
 * Represents the object for tan(x) in the Op-Scheme 
 *
 * 	@author Paehler
 * 	@mm.docstatus finished 
 */

public class TanOp extends FunctionOpNode {

  public TanOp(Class entryClass){
    super(entryClass);
  }

  public TanOp(OpNode child){
    super(child);
  }

  public String nodeToString(){
    return "tan";
  }

  /** Returns atan(x)+2*pi*Z */
   public OpNode[] solveStepFor(String identifier){ 
     OpNode replacer = new VariableOp(m_numberClass, REPLACEMENT_IDENTIFIER);
     OpNode twoPiZ = new MultipleOfZNode(m_numberClass);
     twoPiZ.setFactor(NumberFactory.newInstance(m_numberClass, 2*Math.PI));
     // atan(x)+2*pi*Z
     return new OpNode[]{ new AddOp(new OpNode[]{new AtanOp(replacer), twoPiZ})};
   }
   
  public OpNode getInverseOp(OpNode newChild){
    if(newChild != null)
      return new AtanOp(newChild);
    else
      return new AtanOp(m_numberClass);
  }

  /** Simply calls MNumber.tan() for the child. */
  protected void calculate(){
    m_base = m_children[0].getResult().tan();
  }

  /** Simply calls Math.tan(child). */
  protected void calculateDouble(){
    m_base.setDouble(Math.tan(m_children[0].getResultDouble()));
  }
  
  public RelNode getMonotonicDecreasingRel(OpNode operand){
    return new NullRel(m_numberClass);
  }
  
  public RelNode getMonotonicIncreasingRel(OpNode operand){
    return getNodeDefinedRel(operand);
  }
  
  public RelNode getNodeDefinedRel(OpNode operand){
    return new NotRel(new CosOp(m_numberClass).getZeroRel((OpNode)operand.clone()));
  }
  
  public RelNode getZeroRel(OpNode operand){
    return new SinOp(m_numberClass).getZeroRel((OpNode)operand.clone());
  }
  
  /**
   *  Implements <i> (tan(f(x)))' = 1/cos(f(x))^2 * f`(x) </i>.
   */
  public OpNode getDerivative(String variable){
    
    if(getMaxAbsPowerOfVariable(variable) == 0)
      return new NumberOp(m_numberClass, 0);
    
    // cos(f(x))^-2
    OpNode cosOp = new CosOp(m_numberClass);
    cosOp.setChildren(new OpNode[]{(OpNode)(m_children[0].clone())});
    cosOp.setExponent(-2);
    
    // f'(x)
    OpNode derivedChild = m_children[0].getDerivative(variable);
    
    // cos(f(x))^-2 * f'(x)
    MultOp derivedCosOp = new MultOp(m_numberClass);
    derivedCosOp.setChildren(new OpNode[]{cosOp, derivedChild});
    return deriveNode(derivedCosOp);
  } 
}
  



