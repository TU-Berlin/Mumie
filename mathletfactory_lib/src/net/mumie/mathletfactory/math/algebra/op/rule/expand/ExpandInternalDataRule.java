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
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;

/**
 *  This rule expands the internally stored exponent and factor (if they are
 *  not already set to 1) to real term nodes.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public class ExpandInternalDataRule extends OpRule {
  
  /**
   *  This rule applies to nodes with an exponent != 1 and a factor != 1.
   */
  public boolean appliesTo(OpNode node){
    if(node.getExponent() != 1 || node.getFactor().getDouble() != 1)
      return true;
    return false;
  }
  
  /**
   *  replace the node with a new <code>PowerOp(node, node.getExponent())</code>
   *  and (afterwards) a new <code>MultOp(node, node.getFactor())</code>, if
   *  necessary.
   */
  public OpNode transform(OpNode node){
    int exponent = node.getExponent();
    if(exponent != 1){
      node.setExponent(1);
      node = OpNode.replaceWith(node, new PowerOp(node, exponent));
    }
    MNumber factor = node.getFactor();
    if(factor.getDouble() != 1){
      node.setFactor(NumberFactory.newInstance(node.getNumberClass(), 1));
      node = OpNode.replaceWith(node, new MultOp(new OpNode[]{node, new NumberOp(factor)}));
    }
    return node;
  }
}

