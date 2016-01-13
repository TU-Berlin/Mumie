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
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.math.algebra.op.rule.OpRule;

/**
 *  Expands a product of sums by using the distributive law.
 *	
 *  @author Paehler
 *  @mm.docstatus finished
 */

public class ExpandProductRule extends OpRule {
  
  /**
   *  This rule applies to nodes, which are <code>MultOp</code> and whose
   *  children consist of at least one <code>AddOp</code>.
   */
  public boolean appliesTo(OpNode node){
    OpNode child = node.getFirstChildOfType(AddOp.class, 0);
    if(node instanceof MultOp && child != null && child.getExponent() == 1)
      return true;
    return false;
  }
  
  /**
   *  Replace <code>node</code> with its first <code>AddOp</code> child and
   *  replace each child of the <code>AddOp</code> with a <code>MultOp</code>
   *  that has the <code>AddOp</code>s original child and the other children
   *  of the original <code>MultOp</code> as new children (Yes, it sounds a bit
   *  complicated! ;-)
   */
  public OpNode transform(OpNode node){
    OpNode childAdd = node.getFirstChildOfType(AddOp.class, 0);
    OpNode.transferFactorAndExponent(node, childAdd, true);
    OpNode[] multChildren = OpNode.extractNode(node.getChildren(), childAdd);
    OpNode[] addChildren = childAdd.getChildren();
    for(int i=0; i < addChildren.length; i++)
      addChildren[i] = new MultOp(OpNode.insertNode(multChildren, addChildren[i], true));
    childAdd.setChildren(addChildren);
    return childAdd;
  }
}


