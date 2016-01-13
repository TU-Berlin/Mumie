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
import net.mumie.mathletfactory.math.algebra.op.node.AcoshOp;
import net.mumie.mathletfactory.math.algebra.op.node.FunctionOpNode;
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.math.algebra.op.rule.OpRule;

/**
 *  Collapses a function node and its inverse function child. This rule 
 *  applies to
 *  {@link net.mumie.mathletfactory.algebra.op.node.FunctionOpNode}s only.
 *
 * @author Paehler
 * @mm.docstatus finished
 */

public class CollapseInverseFunctionRule extends OpRule {
  
  /**
   *  Evaluates to true, if node and its child are of the type
   *  {@link net.mumie.mathletfactory.algebra.op.node.FunctionOpNode},
   *  and where the child is the inverse operation to node and has factor == 1
   *  and exponent == 1.
   */
  public boolean appliesTo(OpNode node)
  {
    //System.out.println("checking "+node.toDebugString());
    // applies only to function nodes and nth-root nodes
    if(!(node instanceof FunctionOpNode))
      return false;
    
    OpNode child = node.getChildren()[0];
    
    // is the class of the node and the inverse of its child the same?
    if(node instanceof FunctionOpNode && child instanceof FunctionOpNode
       && ((FunctionOpNode)child).getInverseOp(null).getClass().equals(node.getClass())
       && child.getFactor().getDouble() == 1 && child.getExponent() == 1)
      return true;
    
    return false;
  }
  
  /**
   *  Replace the node by its grandchild, multiplicate its factor and exponent
   *  with the values of node.
   */
  public OpNode transform(OpNode node)
  {
    //System.out.println("ACTION: node is "+node.toDebugString());    
    // the grandchild, that will be the new node
    OpNode grandChild = node.getChildren()[0].getChildren()[0];
 
    // replace the node by its grandchild
    if(node instanceof AcoshOp) // check for non-injective functions
      node = OpNode.replaceWith(node, new AbsOp(grandChild));
    else
      node = OpNode.replaceWith(node, grandChild);
    return node;
  }
}

