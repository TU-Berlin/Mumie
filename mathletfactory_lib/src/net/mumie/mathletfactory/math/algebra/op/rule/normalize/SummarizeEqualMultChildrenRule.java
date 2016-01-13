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



/**
 *  This Rule does operations like x*x = x^2, x/x = 1
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class SummarizeEqualMultChildrenRule extends OpRule {
  
  /**
   *  Checks, whether a node has two or more children of the same kind.
   */
  public boolean appliesTo(OpNode node)
  {
    if(!(node instanceof MultOp))
      return false;
    int childCount = node.getChildren().length;
    for(int i=0;i<childCount;i++)
      // check for equality
      if(i+1<childCount && (node.getChildren()[i].equalWithoutFactorAndExponent(node.getChildren()[i+1])))
        return true;
    return false;
  }
  
  /**
   *  Replaces one pair of equal children by a single new one with double
   *  exponent and the product of their factors.
   */
  public OpNode transform(OpNode node){
    int originalNumberOfChildren = node.getChildren().length;
    OpNode[] newChildren = new OpNode[originalNumberOfChildren-1];
    int counter = 0;
    for(int i=0;i<node.getChildren().length;i++)
      // check for equality
      if(i+1 < node.getChildren().length && node.getChildren()[i].equalWithoutFactorAndExponent(node.getChildren()[i+1])){
        OpNode child1 = node.getChildren()[i], child2 = node.getChildren()[i+1];
        // multiply the factors and add exponents
        child1.setFactor(child1.getExponent() * child2.getExponent() > 0 ?
                           child1.getFactor().mult(child2.getFactor()) :
                           child1.getFactor().div(child2.getFactor()));
        child1.setExponent(child1.getExponent() + child2.getExponent());
        // check for nodes with a zero exponent
        if(child1.getExponent() == 0){
          OpNode newChild = new NumberOp(child1.getNumberClass(), 1);
          newChild.setFactor(child1.getFactor());
          child1 = newChild;
        }
        newChildren[counter++] = child1;
        i++; // skip other child
        //System.out.println("replacing "+node.toDebugString()+" by "+newNode.toDebugString())
      } else
        newChildren[counter++] = node.getChildren()[i];
    if(counter != originalNumberOfChildren-1){ // more than two children have been replaced
      OpNode[] tmp = newChildren;
      newChildren = new OpNode[counter];
      System.arraycopy(tmp, 0, newChildren, 0, counter);
    }
    if(newChildren.length > 1) // multiplication and addition require at least two children
      node.setChildren(newChildren);
    else
      node = OpNode.replaceWith(node, newChildren[0]);
    return node;
  }
}


