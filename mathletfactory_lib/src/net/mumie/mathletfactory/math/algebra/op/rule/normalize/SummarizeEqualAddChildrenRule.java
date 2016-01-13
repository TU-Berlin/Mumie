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
import net.mumie.mathletfactory.math.algebra.op.node.AddOp;
import net.mumie.mathletfactory.math.algebra.op.node.MultipleOfZNode;
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.math.algebra.op.rule.OpRule;
import net.mumie.mathletfactory.math.number.MNumber;



/**
 *  This Rule does operations like x+x = 2*x, x-x = 0
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class SummarizeEqualAddChildrenRule extends OpRule {
  
  /**
   *  Checks, whether <code>node</code> has two or more children of the same kind.
   */
  public boolean appliesTo(OpNode node)
  {
    //System.out.println("checking "+node.toDebugString());
    if(!(node instanceof AddOp))
      return false;
    int childCount = node.getChildren().length;
    for(int i=0;i<childCount;i++)
      // check for equality
      if(i+1<childCount && node.getChildren()[i].equalWithoutFactor(node.getChildren()[i+1]))
        return true;
    return false;
  }
  
  /**
   *  Replaces one pair of equal children by a single new one with the sum of
   *  their factors as new factor.
   */
  public OpNode transform(OpNode node)
  {
    int originalNumberOfChildren = node.getChildren().length;
    OpNode[] newChildren = new OpNode[originalNumberOfChildren-1];
    int counter = 0;
    for(int i=0;i<node.getChildren().length;i++)
      // check for equality
      if(i+1 < node.getChildren().length && node.getChildren()[i].equalWithoutFactor(node.getChildren()[i+1])){
        
        OpNode child1 = node.getChildren()[i], child2 = node.getChildren()[i+1];
        
        OpNode newNode;
        // add the factors
        // handle special case of multipleOfZ-Nodes:
        if(child1 instanceof MultipleOfZNode){
          child1.setFactor(MNumber.min(child1.getFactor().add(child2.getFactor()).absed(),child1.getFactor().add(child2.getFactor().negated()).absed()));
          if(child1.getFactor().isZero()) // k*Z - k*Z = k*Z
            child1.setFactor(child2.getFactor());
        } else {
          if(child1.getFirstDescendantOfType(MultipleOfZNode.class) != null){ // a multipleOfZNode that is no direct child          
            newChildren[counter++] = node.getChildren()[i++];
            continue;
          }
          child1.setFactor(child1.getFactor().add(child2.getFactor()));
        }  
        newNode = child1;
        newChildren[counter++] = newNode;
        i++; // skip other child
        
        //System.out.println("replacing "+node.toDebugString()+" by "+newNode.toDebugString())
      } else
        newChildren[counter++] = node.getChildren()[i];
    if(counter != originalNumberOfChildren-1){ // more than two children have been replaced
      OpNode[] tmp = newChildren;
      newChildren = new OpNode[counter];
      System.arraycopy(tmp, 0, newChildren, 0, counter);
    }
    if(newChildren.length > 1) // addition requires at least two children
      node.setChildren(newChildren);
    else
      node = OpNode.replaceWith(node, newChildren[0]);
    return node;
  }
}


