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

import net.mumie.mathletfactory.math.algebra.op.node.MultOp;
import net.mumie.mathletfactory.math.algebra.op.node.NumberOp;
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.math.algebra.op.node.PowerOp;
import net.mumie.mathletfactory.math.algebra.op.rule.OpRule;

/**
 *  Expands a power of products by using the distributive law.
 *
 * @author Paehler
 * @mm.docstatus finished
 */

public class ExpandPowerRule extends OpRule {
  
  /**
   *  This rule applies to nodes, which are <code>MultOp</code> and whose
   *  children consist of at least one <code>AddOp</code>.
   */
  public boolean appliesTo(OpNode node){
    if(node instanceof PowerOp && node.getChildren()[0] instanceof MultOp
      && node.getChildren()[1] instanceof NumberOp
      && (int)((NumberOp)node.getChildren()[1]).getResult().getDouble() != -1)
      return true;
    return false;
  }
  
  /**
   *  Replace <code>node</code> with its first child (the <code>MultOp</code>)
   *  and replace each child of the <code>MultOp</code> with a <code>PowerOp</code>
   *  that has the <code>MultOp</code>s original child and the exponent as new
   *  children.
   */
  public OpNode transform(OpNode node){
    OpNode childMult = node.getChildren()[0];
    OpNode.transferFactorAndExponent(node, childMult, true);
    OpNode exponent = node.getChildren()[1];
    OpNode[] multChildren = childMult.getChildren();
    for(int i=0; i < multChildren.length; i++)
      multChildren[i] = new PowerOp(multChildren[i], (OpNode)exponent.clone());
    childMult.setChildren(multChildren);
    return childMult;
  }
}

