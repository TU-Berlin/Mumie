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

import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.algebra.rel.Relation;
import net.mumie.mathletfactory.math.algebra.rel.node.AllRel;
import net.mumie.mathletfactory.math.algebra.rel.node.NullRel;
import net.mumie.mathletfactory.math.algebra.rel.node.RelNode;
import net.mumie.mathletfactory.math.algebra.rel.node.SimpleRel;

/**
 *  Represents the object for arctan(x) in the Op-Scheme
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */

public class AtanOp extends FunctionOpNode {
  
  public AtanOp(Class entryClass){
    super(entryClass);
  }
  
  public AtanOp(OpNode child){
    super(child);
  }
  
  public String nodeToString(){
    return "atan";
  }
  
  public OpNode getInverseOp(OpNode newChild){
    if(newChild != null)
      return new TanOp(newChild);
    else
      return new TanOp(m_numberClass);
  }
  
  /** Simply calls MNumber.atan(child). */
  protected void calculate(){
    m_base = m_children[0].getResult().arctan();
  }
  
  /** Simply calls Math.atan(child). */
  protected void calculateDouble(){
    m_base.setDouble(Math.atan(m_children[0].getResultDouble()));
  }
   
  public RelNode getMonotonicDecreasingRel(OpNode operand){
    return new NullRel(m_numberClass);
  }
  
  public RelNode getMonotonicIncreasingRel(OpNode operand){
    return getNodeDefinedRel(operand);
  }

  public RelNode getNodeDefinedRel(OpNode operand){
    return new AllRel(m_numberClass);
  }
  
  public RelNode getZeroRel(OpNode operand){
    return new SimpleRel(new Operation((OpNode)operand.clone()), Relation.EQUAL);
  }
  
  /**
   *  Implements <i> (atan(f(x)))' = f'(x) / 1+x^2) </i>.
   */
  public OpNode getDerivative(String variable){
    if(getMaxAbsPowerOfVariable(variable) == 0)
      return new NumberOp(m_numberClass, 0);
    
    // 1/(1+(f(x))^2)
    OpNode xSquare = (OpNode)m_children[0].clone();
    xSquare.setExponent(2);
    OpNode atanOp = new AddOp(new OpNode[]{new NumberOp(m_numberClass, 1.0), xSquare});
    atanOp.setExponent(-1);
    
    // f'(x)
    OpNode derivedChild = m_children[0].getDerivative(variable);
    
    // (1 / (1+(f(x))^2)) * f'(x)
    MultOp derivedAtanOp = new MultOp(new OpNode[]{atanOp, derivedChild});
    return deriveNode(derivedAtanOp);
  } 
}



