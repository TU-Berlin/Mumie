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

import net.mumie.mathletfactory.math.algebra.op.node.ExpOp;
import net.mumie.mathletfactory.math.algebra.op.node.MultOp;
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.math.algebra.op.node.PowerOp;
import net.mumie.mathletfactory.math.algebra.op.node.VariableOp;
import net.mumie.mathletfactory.math.algebra.op.rule.OpRule;

/**
 *  Implements two rules:
 * 
 *  a) (a^b)^c -> a^(b*c)
 *  If the node is a PowerOp with another PowerOp as first child, the 
 *  node will be replaced by a PowerOp with the base of the child as new
 *  base and a MultOp of the old exponent and the old childs exponent.
 * 
 *  b) e^a -> exp(a)   
 *  
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class CollapsePowerRule extends OpRule {
  
  /**
   *  Evaluates to true, if the node is a PowerOp and its first child (the 
   *  base) is a PowerOp or its base is the variable "e".
   */
  public boolean appliesTo(OpNode node) {
    //System.out.println("checking "+node.toDebugString());
    if (!(node instanceof PowerOp))
      return false;
    if(node.getChildren()[0] instanceof PowerOp) 
      return true;
    if(node.getChildren()[0] instanceof VariableOp && ((VariableOp)node.getChildren()[0]).getIdentifier().equals("e"))
      return true;
    return false;
  }

  /**
   *  Replace PowerOp node by a new PowerOp that has the base of the child
   *  as new base and the product of the node's and the child's exponent as
   *  new exponent. 
   */
  public OpNode transform(OpNode node) {
    if(node.getChildren()[0] instanceof VariableOp){
      OpNode newNode = new ExpOp(node.getChildren()[1]);
      OpNode.transferFactorAndExponent(node, newNode, false);
      return newNode;
    }
    OpNode newExponent = new MultOp(new OpNode[]{node.getChildren()[1], node.getChildren()[0].getChildren()[1]});
    return new PowerOp(node.getChildren()[0].getChildren()[0], newExponent);
  }
}

