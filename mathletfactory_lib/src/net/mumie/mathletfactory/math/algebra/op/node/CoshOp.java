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
 *  Represents the object for cosh(x) in the Op-Scheme
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */

public class CoshOp extends FunctionOpNode {

  public CoshOp(Class entryClass) {
    super(entryClass);
  }

  public CoshOp(OpNode child) {
    super(child);
  }

  public String nodeToString() {
    return "cosh";
  }

  public OpNode getInverseOp(OpNode newChild) {
    if(newChild != null)
      return new AcoshOp(newChild);
    else
      return new AcoshOp(m_numberClass);
  }

  /** Simply calls MNumber.cosh(child). */
  protected void calculate() {
    m_base = m_children[0].getResult().cosh();
  }

  /** Simply calls MNumber.cosh(child). */
  protected void calculateDouble() {
    m_base.setDouble(m_children[0].getResultDouble());
    m_base.cosh();
  }

  public RelNode getMonotonicDecreasingRel(OpNode operand) {
    return new SimpleRel(
      new Operation((OpNode) operand.clone()),
      Relation.LESS_OR_EQUAL);
  }

  public RelNode getMonotonicIncreasingRel(OpNode operand) {
    return new SimpleRel(
      new Operation((OpNode) operand.clone()),
      Relation.GREATER_OR_EQUAL);
  }

  public RelNode getNodeDefinedRel(OpNode operand) {
    return new AllRel(m_numberClass);
  }

  public RelNode getZeroRel(OpNode operand) {
    return new NullRel(m_numberClass);
  }

  /**
   *  Implements <i> cosh(x)'</i>.
   */
  public OpNode getDerivative(String variable) {
    if (getMaxAbsPowerOfVariable(variable) == 0)
      return new NumberOp(m_numberClass, 0);
    // sinh(f(x))
    OpNode sinhOp = new SinhOp(m_numberClass);
    sinhOp.setChildren(new OpNode[] {(OpNode) (m_children[0].clone())});

    // f'(x)
    OpNode derivedChild = m_children[0].getDerivative(variable);

    // sinh(f(x)) * f'(x)
    MultOp derivedSinhOp = new MultOp(m_numberClass);
    derivedSinhOp.setChildren(new OpNode[] { sinhOp, derivedChild });
    return deriveNode(derivedSinhOp);
  }
}
