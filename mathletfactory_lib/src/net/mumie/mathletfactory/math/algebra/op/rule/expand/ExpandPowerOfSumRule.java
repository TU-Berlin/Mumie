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

package net.mumie.mathletfactory.math.algebra.op.rule.expand;

import net.mumie.mathletfactory.math.algebra.op.node.AddOp;
import net.mumie.mathletfactory.math.algebra.op.node.MultOp;
import net.mumie.mathletfactory.math.algebra.op.node.NumberOp;
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.math.algebra.op.node.PowerOp;
import net.mumie.mathletfactory.math.algebra.op.rule.OpRule;

/**
 *  This rule expands sums raised to an integer power.
 *  E.g. (a+b+c)^3 = (a+b+c)*(a+b+c)*(a+b+c).
 * 
 * @author Paehler
 * @mm.docstatus finished
 */

public class ExpandPowerOfSumRule extends OpRule {
  
  /**
   *  This rule applies to <code>PowerOp</code>s that have an <code>AddOp</code>
   *  as child and an integer exponent.
   */
  public boolean appliesTo(OpNode node){
    if(node instanceof PowerOp && node.getChildren()[0] instanceof AddOp &&
      node.getChildren()[1] instanceof NumberOp
       && ((NumberOp)node.getChildren()[1]).getResult().isInteger()
       && (int)((NumberOp)node.getChildren()[1]).getResult().getDouble() != -1)
      return true;
    return false;
  }
  
  /**
   *  Clone the <code>AddOp</code> node exponent times and insert the clones
   *  into a new <code>MultOp</code> node.
   */
  public OpNode transform(OpNode node) {
    int exponent = (int)((NumberOp)node.getChildren()[1]).getResult().getDouble();
    boolean isFraction = exponent < 0;
    if(isFraction)
    	exponent *= -1;
    OpNode multNode = new MultOp(node.getNumberClass());
    OpNode[] newChildren = new OpNode[exponent];
    for(int i=0; i<exponent; i++)
      newChildren[i] = (OpNode)node.getChildren()[0].clone();
    multNode.setChildren(newChildren);
    if(isFraction) {
    	// transfer internal negative exponent to NumberOp in PowerOp
        OpNode powerNode = new PowerOp(multNode, -1);
        return OpNode.replaceWith(node, powerNode);
  	} else {
        return OpNode.replaceWith(node, multNode);
    }
  }
}

