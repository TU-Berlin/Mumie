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
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.math.algebra.op.rule.OpRule;
import net.mumie.mathletfactory.math.number.NumberFactory;


/**
 *  Propagates the m_factor of an 
 *  {@link net.mumie.mathletfactory.algebra.op.node.AddOp} node to its children 
 *  node.
 * 
 * 	@author Paehler
 *	@mm.docstatus finished
 */
public class NormalizeAddRule extends OpRule {
  
  /**
   *  Evaluates to true, if the node is an AddOp with exponent = 1 and at least one of it's
   *  children is a NumberOp (i.e. a constant) or has a factor != 1.
   */
  public boolean appliesTo(OpNode node)
  {
    //System.out.println("checking "+node.toDebugString());
    if(node instanceof AddOp && node.getFactor().getDouble() != 1 && node.getExponent() == 1)
      return true;
    return false;
  }
  
  /**
   *  Multiply the children with<code>node.factor</code>and set the 
   *  latter to 1. 
   */
  public OpNode transform(OpNode node)
  {
    int numberOpCount = 0;
    
    //System.out.println("ACTION: node is"+node.toDebugString());
    for(int i=0;i<node.getChildren().length;i++)
      // multiply the node's factor by the children's factor
      node.getChildren()[i].multiplyFactor(node.getFactor());
        //System.out.println("ACTION: now node is"+node.toDebugString());
    node.setFactor(NumberFactory.newInstance(node.getNumberClass(),1));
    return node;
  }
}

