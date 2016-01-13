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
import net.mumie.mathletfactory.math.algebra.rel.Relation;
import net.mumie.mathletfactory.math.algebra.rel.node.AllRel;
import net.mumie.mathletfactory.math.algebra.rel.node.AndRel;
import net.mumie.mathletfactory.math.algebra.rel.node.RelNode;
import net.mumie.mathletfactory.math.algebra.rel.node.SimpleRel;

/**
 *  Represents the object for floor(x) (i.e. the largest (closest to positive
 *  infinity) double value that is not greater than the argument and is equal
 *  to a mathematical integer) in the Op-Scheme.
 *
 *  @author Paehler
 *  @mm.docstatus finished
 *  @todo implement {@link #getDerivative}
 */
public class FloorOp extends FunctionOpNode {
  public FloorOp(Class entryClass) {
    super(entryClass);
  }

  public FloorOp(OpNode child) {
    super(child);
  }
 
  public OpNode[] solveStepFor(String identifier){
    throw new TodoException();
  }

  public RelNode getMonotonicIncreasingRel(OpNode operand) {
    return new AllRel(m_numberClass);
  }

  public RelNode getMonotonicDecreasingRel(OpNode operand) {
    return new AllRel(m_numberClass);
  }

  public RelNode getNodeDefinedRel(OpNode operand) {
    return new AllRel(m_numberClass);
  }

  /** Returns a relation that is satisfied for 0 <= x < 1. */
  public RelNode getZeroRel(OpNode operand) {
    return new AndRel(new SimpleRel(new Operation((OpNode)m_children[0].clone()), "0", Relation.GREATER_OR_EQUAL),
                new SimpleRel(new Operation((OpNode)m_children[0].clone()), "1", Relation.LESS));
  }

  /** Simply uses MNumber.floor(child). */
  protected void calculate() {
    m_base = m_children[0].getResult().floor();
  }

  /** Simply calls Math.floor(child). */
  protected void calculateDouble(){
    m_base.setDouble(Math.floor(m_children[0].getResultDouble()));
  }
  
  /**
   * TODO
   */
  public OpNode getDerivative(String variable) {
    throw new TodoException();
    //return deriveNode(m_children[0].getDerivative(variable));
  }

  public String toString(int printFlags, DecimalFormat format) {
    return printFactorAndExponent(nodeToString()+"("+m_children[0].toString(format)+")", printFlags, format);
  }

  public String nodeToString() {
    return "floor";
  }

  public OpNode getInverseOp(OpNode newChild) {
	throw new TodoException();
  }
}
