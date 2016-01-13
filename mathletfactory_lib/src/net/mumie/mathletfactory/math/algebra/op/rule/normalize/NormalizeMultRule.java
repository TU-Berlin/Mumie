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
import net.mumie.mathletfactory.math.algebra.op.node.MultOp;
import net.mumie.mathletfactory.math.algebra.op.node.NumberOp;
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.math.algebra.op.rule.OpRule;
import net.mumie.mathletfactory.math.number.MNumber;


/**
 *  Propagates all m_factors of the children to their parent 
 *  {@link net.mumie.mathletfactory.algebra.op.node.MultOp} node.
 *  If the node also has 1 to n constant children, those children will be
 *  removed and stored internally by multiplying the node's factor with the product
 *  of the children.
 * 
 * 	@author Paehler
 *	@mm.docstatus finished
 */
public class NormalizeMultRule extends OpRule {
  
  /**
   *  Evaluates to true, if the node is a MultOp and at least one of it's
   *  children is a NumberOp (i.e. a constant) or has a factor != 1.
   */
  public boolean appliesTo(OpNode node)
  {
    //System.out.println("checking "+node.toDebugString());
    if(node instanceof MultOp && node.getChildrenWithVariablesCount(true) > 0)
      for(int i=0;i<node.getChildren().length;i++)
        if(node.getChildren()[i] instanceof NumberOp || node.getChildren()[i].getFactor().getDouble() != 1)
          return true;
    return false;
  }
  
  /**
   *  Multiply <code>node.factor</code> with the childrens factor and set the 
   *  latter to 1. Multiply <code>node.factor</code> with all constant children. 
   *  If the MultOp has only one children left afterwards, it is replaced by its 
   *  child, if it has no children left, it is replaced by a 
   *  {@link net.mumie.mathletfactory.algebra.op.node.NumberOp}.
   */
  public OpNode transform(OpNode node)
  {
    int numberOpCount = 0;
    
    //System.out.println("ACTION: node is"+node.toDebugString());
    for(int i=0;i<node.getChildren().length;i++){
      if(node.getChildren()[i] instanceof NumberOp)
        numberOpCount++;
      // multiply the node's factor by the children's factor
      node.multiplyFactor(node.getChildren()[i].getFactor().power(node.getExponent()));
      node.getChildren()[i].setFactor(node.getChildren()[i].getFactor().setDouble(1));
    }
    OpNode[] newChildren = new OpNode[node.getChildren().length - numberOpCount];
    int count=0;
    
    // node.m_factor is multiplied by all numberOps, the other children are the
    // new children for the node (or, in case of one new child, the new node itself)
    for(int i=0;i<node.getChildren().length;i++)
      if(node.getChildren()[i] instanceof NumberOp){
        MNumber multWith = node.getChildren()[i].getResult();
        if(node.getExponent() != 1)
          multWith.power(node.getExponent());
        node.multiplyFactor(multWith);
      }
      else
        newChildren[count++] = node.getChildren()[i];
      
    if(newChildren.length > 1)
      node.setChildren(newChildren);
    else if(newChildren.length == 1)
      node = OpNode.replaceWith(node, newChildren[0]);
    else
      node = new NumberOp(node.getResult());
      
    //System.out.println("ACTION: now node is"+node.toDebugString());
    return node;
  }
}

