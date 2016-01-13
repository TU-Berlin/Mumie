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
import net.mumie.mathletfactory.math.algebra.op.node.NumberOp;
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.math.algebra.op.rule.OpRule;


/**
 *  If a child node has only constant numbers as leaves, it may be replaced
 *  by a {@link net.mumie.mathletfactory.algebra.op.node.NumberOp} containing 
 *  the {@link net.mumie.mathletfactory.algebra.op.node.MultOp#getResult result}.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class PropagateConstantsRule extends OpRule {
  
  /**
   *  Checks, if a) the node has multiple constants as children (in case of 
   * {@link net.mumie.mathletfactory.algebra.op.node.AddOp}s)
   *  or b) has no variables in its subtree (thus being a constant expression)
   */
  public boolean appliesTo(OpNode node)
  {
    if(node.getChildren() == null)
      return false;
    
    // if the node is an addition or a multiplication, all constant children
    // may be summarized as one
    if(node instanceof AddOp){
      int count=0;
      // count the constants
      for(int i=0; i<node.getChildren().length;i++)
        if(node.getChildren()[i] instanceof NumberOp)
          count++;
      if(count >= 2)
        return true;
    }
    // subtrees containing variables cannot be simplified, except for the ones containing only "i"
    if(node.getChildrenWithVariablesCount(true) > 0)
      return false;
    
    // this subtree has only numbers as leaves, so we can simply set the result
    // as a constant numeral
    return true;
  }
  
  /**
   *  Either summarize the constant children of a node to one (for case a)) or
   *  make the node a new NumeralOp containing the calculated result as constant
   *  (for case b)).
   */
  public OpNode transform(OpNode node)
  {
    //System.out.println("summarizing "+node.toDebugString());
    // first check for constant expressions
    if(node.getChildrenWithVariablesCount(true) == 0){
      node = new NumberOp(node.getResult());
      return node;
    }
    // node must be an AddOp with more than 2 number children
    int newChildrenCount = 0;
    int count=0;
    // count the constants
    for(int i=0; i<node.getChildren().length;i++)
      if(node.getChildren()[i] instanceof NumberOp)
        count++;
    OpNode[] newChildren = new OpNode[node.getChildren().length - count+1];
    NumberOp sum = new NumberOp(node.getNumberClass(), 0);
    for(int i=0;i<node.getChildren().length;i++)
      if(node.getChildren()[i] instanceof NumberOp)
        // add or multiply constants
        sum.setResult(sum.getResult().add(node.getChildren()[i].getResult()));
      else
        newChildren[newChildrenCount++] = node.getChildren()[i];
    // finally insert the summarized constants
    newChildren[newChildrenCount] = sum;
    if(newChildren.length > 1) // multiplication and addition require at least two children
      node.setChildren(newChildren);
    else {
      newChildren[0].setParent(node.getParent());
      node = newChildren[0];
    }
    return node;
  }
}

