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

import net.mumie.mathletfactory.action.message.TodoException;
import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.algebra.rel.Relation;
import net.mumie.mathletfactory.math.algebra.rel.node.AllRel;
import net.mumie.mathletfactory.math.algebra.rel.node.NullRel;
import net.mumie.mathletfactory.math.algebra.rel.node.RelNode;
import net.mumie.mathletfactory.math.algebra.rel.node.SimpleRel;

/**
 *  Represents the object for artanh(x) in the Op-Scheme
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */

public class AtanhOp extends FunctionOpNode {
  
  public AtanhOp(Class entryClass){
    super(entryClass);
  }
  
  public AtanhOp(OpNode child){
    super(child);
  }

  public String nodeToString(){
    return "atanh";
  }
  
  public OpNode getInverseOp(OpNode newChild){
    if(newChild != null)
      return new TanhOp(newChild);
    else
      return new TanhOp(m_numberClass);
  }
  
  protected void calculate(){
    m_base = m_children[0].getResult().artanh();
  }
  
  protected void calculateDouble(){
    m_base.setDouble(m_children[0].getResultDouble());
    m_base.artanh();
  }
   
  public RelNode getMonotonicDecreasingRel(OpNode operand){
    return new NullRel(m_numberClass);
  }
  
  public RelNode getMonotonicIncreasingRel(OpNode operand){
    return getNodeDefinedRel(operand);
  }
  
  public RelNode getNodeDefinedRel(OpNode operand){
    return new AllRel(getNumberClass());
  }
  
  public RelNode getZeroRel(OpNode operand){
    return new SimpleRel(new Operation((OpNode)operand.clone()), Relation.EQUAL);
  }
  
  public OpNode getDerivative(String variable){
    throw new TodoException();
  } 
}



