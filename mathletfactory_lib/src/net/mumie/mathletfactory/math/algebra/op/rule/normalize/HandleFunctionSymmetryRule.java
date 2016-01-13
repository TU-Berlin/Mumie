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

package net.mumie.mathletfactory.math.algebra.op.rule.normalize;
import net.mumie.mathletfactory.math.algebra.op.node.AbsOp;
import net.mumie.mathletfactory.math.algebra.op.node.AcosOp;
import net.mumie.mathletfactory.math.algebra.op.node.AsinOp;
import net.mumie.mathletfactory.math.algebra.op.node.AtanOp;
import net.mumie.mathletfactory.math.algebra.op.node.CosOp;
import net.mumie.mathletfactory.math.algebra.op.node.CoshOp;
import net.mumie.mathletfactory.math.algebra.op.node.NrtOp;
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.math.algebra.op.node.SinOp;
import net.mumie.mathletfactory.math.algebra.op.node.SinhOp;
import net.mumie.mathletfactory.math.algebra.op.node.TanOp;
import net.mumie.mathletfactory.math.algebra.op.rule.OpRule;
import net.mumie.mathletfactory.math.number.NumberFactory;

/**
 *  does sin(-x) -> -sin(x), cos(-x) -> cos(x), etc. 
 *   This rule applies to
 *  {@link net.mumie.mathletfactory.algebra.op.node.FunctionOpNode}s only.
 *
 * @author Paehler
 * @mm.docstatus finished
 */

public class HandleFunctionSymmetryRule extends OpRule {
  
  /**
   *  Evaluates to true, if node is of the type
   *  {@link net.mumie.mathletfactory.algebra.op.node.FunctionOpNode},
   *  and its child has a factor &lt; 0.
   */
  public boolean appliesTo(OpNode node)
  {
    //System.out.println("checking "+node.toDebugString());
    // applies only to function nodes and nth-root nodes
    if((node instanceof AbsOp || node instanceof AcosOp || node instanceof AsinOp 
     || node instanceof AtanOp || node instanceof CosOp || node instanceof CoshOp 
     || (node instanceof NrtOp && ((NrtOp)node).getN() % 2 == 1)
     || node instanceof SinhOp || node instanceof SinOp || node instanceof TanOp)
     && node.getChildren()[0].getFactor().getDouble() < 0)
      return true;
    return false;
  }
  
  /**
   *  Remove the child's minus sign, multiply the factor of the node by -1 or 1, 
   *  depending on whether the function is symmetric or antisymmetric. 
   */
  public OpNode transform(OpNode node)
  {
    //System.out.println("ACTION: node is"+node.toDebugString());
    node.getChildren()[0].multiplyFactor(NumberFactory.newInstance(node.getNumberClass(), -1));
    // symmetric case
    if(node instanceof AbsOp || node instanceof CoshOp || node instanceof CosOp)
      return node;
    // antisymmetric case
    node.multiplyFactor(NumberFactory.newInstance(node.getNumberClass(),-1));
      return node;
  }
}

