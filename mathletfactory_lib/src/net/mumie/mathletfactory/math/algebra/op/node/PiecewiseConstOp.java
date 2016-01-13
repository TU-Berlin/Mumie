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
import net.mumie.mathletfactory.math.algebra.rel.node.RelNode;
import net.mumie.mathletfactory.math.algebra.rel.node.SimpleRel;
import net.mumie.mathletfactory.math.number.MNumber;

/**
 *  Represents the object for piecewise constant functions (like sign(x), theta(x), etc.) in the Op-Scheme
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class PiecewiseConstOp extends FunctionOpNode {

  double[] m_jumps, m_values;
  String m_name;

  public PiecewiseConstOp(Class entryClass, double[] jumps, double[] values, String name){
    super(entryClass);
    m_jumps = jumps;
    m_values = values;
    m_name = name;
  }
  
  public PiecewiseConstOp(OpNode child, double[] jumps, double[] values, String name){
    super(child);
    m_jumps = jumps;
    m_values = values;
    m_name = name;
  }

  public int getMaxChildrenNr(){
    return 1;
  }
  
  public int getMinChildrenNr(){
    return 1;
  }
  
  public String nodeToString(){
    return m_name;
  }
      
  /** Simply calls Math.abs(child). */
  protected void calculate(){
    int i=0;
    MNumber result = m_children[0].getResult();
    while(i < m_jumps.length && result.getDouble() > m_jumps[i])
      i++;
    m_base.setDouble(m_values[i]);
  }

  /** Simply calls MNumber.acos(child). */
   protected void calculateDouble(){
     int i=0;
     double result = m_children[0].getResultDouble();
     while(i < m_jumps.length && result > m_jumps[i])
       i++;
     m_base.setDouble(m_values[i]);
   }
    
  public RelNode getMonotonicDecreasingRel(OpNode operand){
    return new AllRel(getNumberClass());
  }
  
  public RelNode getMonotonicIncreasingRel(OpNode operand){
    return new AllRel(getNumberClass());
  }
  
  public RelNode getNodeDefinedRel(OpNode operand){
    return new AllRel(getNumberClass());
  }
  
  public RelNode getZeroRel(OpNode operand){
    return new SimpleRel(new Operation((OpNode)operand.clone()), Relation.EQUAL);
  }
  
  public OpNode getDerivative(String variable){
    return new NumberOp(m_numberClass, 0);
  }

  public boolean isIrreducibleFor(String identifier){
    return true;
  }
  
  public OpNode[] solveStepFor(String identifier){
    throw new TodoException();
  }
  
  /* (non-Javadoc)
   * @see net.mumie.mathletfactory.math.algebra.op.node.FunctionOpNode#getInverseOp(net.mumie.mathletfactory.math.algebra.op.node.OpNode)
   */
  public OpNode getInverseOp(OpNode newChild) {
    throw new IllegalStateException("no inverse for "+m_name+"!");
  }

}
  



