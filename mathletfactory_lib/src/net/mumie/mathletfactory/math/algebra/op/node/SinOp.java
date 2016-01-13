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

import net.mumie.mathletfactory.math.algebra.op.OpParser;
import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.algebra.rel.Relation;
import net.mumie.mathletfactory.math.algebra.rel.node.AllRel;
import net.mumie.mathletfactory.math.algebra.rel.node.RelNode;
import net.mumie.mathletfactory.math.algebra.rel.node.SimpleRel;
import net.mumie.mathletfactory.math.number.NumberFactory;

/** 
 * Represents the object for sin(x) in the Op-Scheme 
 *
 * 	@author Paehler
 * 	@mm.docstatus finished 
 */

public class SinOp extends FunctionOpNode {

  public SinOp(Class entryClass){
    super(entryClass);
  }

  public SinOp(OpNode child){
    super(child);
  }

  public String nodeToString(){
    return "sin";
  }

  /** Returns {asin(x)+2*pi*Z, -asin(-x)+2*pi*Z} */
  public OpNode[] solveStepFor(String identifier){ 
    OpNode replacer = new VariableOp(m_numberClass, REPLACEMENT_IDENTIFIER);
    OpNode twoPiZ = new MultipleOfZNode(m_numberClass);
    twoPiZ.setFactor(NumberFactory.newInstance(m_numberClass, 2*Math.PI));
    // asin(x)+2*pi*Z
    OpNode firstNode = new AddOp(new OpNode[]{new AsinOp(replacer), twoPiZ});
    // -asin(-x)+2*pi*Z
    OpNode secondNode = new AddOp(new OpNode[]{new AsinOp(new MultOp(new OpNode[]{new NumberOp(m_numberClass, -1), replacer})), twoPiZ});
    secondNode.setFactor(NumberFactory.newInstance(m_numberClass, -1));
    return new OpNode[]{firstNode, secondNode};
  }
  
  public OpNode getInverseOp(OpNode newChild){
    if(newChild != null)
      return new AsinOp(newChild);
    else
      return new AsinOp(m_numberClass);
  }

  /** Simply calls MNumber.sin(). */
  protected void calculate(){
    m_base = m_children[0].getResult().sin();
  }
  
  /** Simply calls Math.sin(child). */
  protected void calculateDouble(){
    m_base.setDouble(Math.sin(m_children[0].getResultDouble()));
  }
  
  public RelNode getMonotonicDecreasingRel(OpNode operand){
    CosOp cos = new CosOp((OpNode)operand.clone());
    return new SimpleRel(new Operation(cos), Relation.LESS_OR_EQUAL);
  }
  
  public RelNode getMonotonicIncreasingRel(OpNode operand){
    CosOp cos = new CosOp((OpNode)operand.clone());
    return new SimpleRel(new Operation(cos), Relation.GREATER_OR_EQUAL);
  }
  
  public RelNode getNodeDefinedRel(OpNode operand){
    return new AllRel(m_numberClass);
  }
  
  /** Returns for sin(x) a relation that is satisfied for x = pi*n. */ 
  public RelNode getZeroRel(OpNode operand){
    OpNode x = (OpNode)operand.clone();
    OpNode piZ = new MultOp(new OpNode[]{new VariableOp(m_numberClass, OpParser.PI), new MultipleOfZNode(m_numberClass)});
    return new SimpleRel(new Operation(x),new Operation(piZ), Relation.EQUAL, Relation.NORMALIZE_OP_SEPARATED);
  }
  
  /**
   *  Implements <i> (sin(f(x)))' = cos(f(x)) * f`(x) </i>.
   */
  public OpNode getDerivative(String variable){
    
    if(getMaxAbsPowerOfVariable(variable) == 0)
      return new NumberOp(m_numberClass, 0);
    
    // cos(f(x))
    OpNode cosOp = new CosOp(m_numberClass);
    cosOp.setChildren(new OpNode[]{(OpNode)(m_children[0].clone())});
    
    // f'(x)
    OpNode derivedChild = m_children[0].getDerivative(variable);
    
    // cos(f(x)) * f'(x)
    MultOp derivedCosOp = new MultOp(m_numberClass);
    derivedCosOp.setChildren(new OpNode[]{cosOp, derivedChild});
    return deriveNode(derivedCosOp);
  }
}
  


