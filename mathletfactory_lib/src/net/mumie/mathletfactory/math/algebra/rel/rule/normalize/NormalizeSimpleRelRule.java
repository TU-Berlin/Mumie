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

package net.mumie.mathletfactory.math.algebra.rel.rule.normalize;

import net.mumie.mathletfactory.math.algebra.op.node.AddOp;
import net.mumie.mathletfactory.math.algebra.op.node.MultipleOfZNode;
import net.mumie.mathletfactory.math.algebra.op.node.NumberOp;
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.math.algebra.rel.node.RelNode;
import net.mumie.mathletfactory.math.algebra.rel.node.SimpleRel;
import net.mumie.mathletfactory.math.algebra.rel.rule.RelRule;

/**
 * This rule removes multiple multiplesOfZ nodes a 
 * {@link net.mumie.mathletfactory.algebra.rel.node.SimpleRel simple relation}
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public class NormalizeSimpleRelRule extends RelRule {

  /** 
   * Returns true if the node is a 
   * {@link net.mumie.mathletfactory.algebra.rel.node.SimpleRel} that contains
   * at least two {@link net.mumie.mathletfactory.algebra.op.node.MultipleOfZNode}s
   * either as root node or in an {@link net.mumie.mathletfactory.algebra.op.node.AddOp}
   * node.
   * @see net.mumie.mathletfactory.algebra.rel.rule.RelRule#appliesTo(RelNode)
   */
  public boolean appliesTo(RelNode node) {
    if(!(node instanceof SimpleRel))
      return false;
    SimpleRel snode = (SimpleRel)node;  
    // if on both sides a multipleofZNode is the root node or a child of a root AddOp
    if((snode.getLeftHandSide().getOpRoot() instanceof MultipleOfZNode || 
         (snode.getLeftHandSide().getOpRoot() instanceof AddOp &&  
         snode.getLeftHandSide().getOpRoot().getFirstChildOfType(MultipleOfZNode.class, 0) != null))
      && (snode.getRightHandSide().getOpRoot() instanceof MultipleOfZNode || 
    (snode.getRightHandSide().getOpRoot() instanceof AddOp &&  
    snode.getRightHandSide().getOpRoot().getFirstChildOfType(MultipleOfZNode.class, 0) != null)))
      return true;
    return false;
  }

  /**
   * Returns the simple relation with the surplus multipleOfZNode removed. 
   * @see net.mumie.mathletfactory.algebra.rel.rule.RelRule#transform(net.mumie.mathletfactory.algebra.rel.node.RelNode)
   */
  public RelNode transform(RelNode node) {
    SimpleRel snode = (SimpleRel)node;

    OpNode rightNode = snode.getRightHandSide().getOpRoot();
    if(rightNode instanceof AddOp){
      rightNode = rightNode.getFirstChildOfType(MultipleOfZNode.class, 0);
    }
    
    OpNode leftNode = snode.getLeftHandSide().getOpRoot();
    if(leftNode instanceof AddOp){
      leftNode = leftNode.getFirstChildOfType(MultipleOfZNode.class, 0);
      if(leftNode.getFactor().absed().equals(rightNode.getFactor().absed()) && leftNode.getExponent() == rightNode.getExponent()){
        OpNode[] newChildren = OpNode.extractNode(leftNode.getParent().getChildren(), leftNode);
        if(newChildren.length == 1)
          leftNode = newChildren[0];
        else
          leftNode = new AddOp(newChildren);
        snode.setLeftHandSide(leftNode);
      }    
    } else // node itself is a MultipleOfZNode
      snode.setLeftHandSide(new NumberOp(node.getNumberClass(), 0));
    return snode;
  }

  
}
