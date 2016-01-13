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

package net.mumie.mathletfactory.math.algebra.op.rule;

import net.mumie.mathletfactory.math.algebra.op.node.NumberOp;
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.math.algebra.op.node.VariableOp;

/**
 * This rule collapses symbolic constants nodes to numerical values. This is useful for 
 * operations that are not symbolically displayed and need to be simplified for a 
 * transformation.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public class NumerizeConstantsAndParametersRule extends OpRule {


  /** 
   *  Returns true for constant and parameter 
   *  {@link net.mumie.mathletfactory.algebra.op.node.VariableOp}
   */
  public boolean appliesTo(OpNode node) {
    if(node instanceof VariableOp && !((VariableOp)node).isVariable())
      return true;
    return false;
  }

  /**
   * Replaces the node with a {@link net.mumie.mathletfactory.algebra.op.node.NumberOp} 
   * of the same numeric value.
   */
  public OpNode transform(OpNode node) {
    node = OpNode.replaceWith(node, new NumberOp(node.getResult()));
    return node;
  }

}
