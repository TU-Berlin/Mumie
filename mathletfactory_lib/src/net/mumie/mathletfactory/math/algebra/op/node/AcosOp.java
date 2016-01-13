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
import net.mumie.mathletfactory.math.algebra.rel.node.AndRel;
import net.mumie.mathletfactory.math.algebra.rel.node.NullRel;
import net.mumie.mathletfactory.math.algebra.rel.node.RelNode;
import net.mumie.mathletfactory.math.algebra.rel.node.SimpleRel;

/**
 *  Represents the object for arccos(x) in the Op-Scheme
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */

public class AcosOp extends FunctionOpNode {

  public AcosOp(Class entryClass){
    super(entryClass);
  }
  
  public AcosOp(OpNode child){
    super(child);
  }

  public String nodeToString(){
    return "acos";
  }
  
  public OpNode getInverseOp(OpNode newChild){
    if(newChild != null)
      return new CosOp(newChild);
    else return new CosOp(m_numberClass);
  }
  
  /** Simply calls MNumber.acos(child). */
  protected void calculate(){
    m_base = m_children[0].getResult().arccos();
  }
  
  /** Simply calls Math.acos(child). */
  protected void calculateDouble(){
    m_base.setDouble(Math.acos(m_children[0].getResultDouble()));
  }
     
  public RelNode getMonotonicDecreasingRel(OpNode operand){
    return getNodeDefinedRel(operand);
  }
  
  public RelNode getMonotonicIncreasingRel(OpNode operand){
    return new NullRel(m_numberClass);
  }
  
  public RelNode getNodeDefinedRel(OpNode operand){
    return new AndRel(new RelNode[]{
      new SimpleRel(new Operation((OpNode)operand.clone()), "-1", Relation.GREATER_OR_EQUAL),
      new SimpleRel(new Operation((OpNode)operand.clone()), "1", Relation.LESS_OR_EQUAL)});
  }
  
  public RelNode getZeroRel(OpNode operand){
    return new NullRel(m_numberClass);
  }
  
  /**
   *  Implements <i> (acos(f(x)))' = - f'(x) / sqrt(1-f(x)^2) </i>.
   */
  public OpNode getDerivative(String variable){
    if(getMaxAbsPowerOfVariable(variable) == 0)
      return new NumberOp(m_numberClass, 0);
    
    throw new net.mumie.mathletfactory.action.message.TodoException();
  }
}
  



