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
 *  This rule applies to nodes MultOp(0,x) and replace them by a zero
 *  {@link net.mumie.mathletfactory.algebra.op.node.NumberOp}.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */

public class RemoveZeroMultRule extends OpRule {
  /**
   *  Checks if the node has factor zero or the form 
   *  {@link net.mumie.mathletfactory.algebra.op.node.MultOp}<code>(... , 0 , ...)</code>.
   */
  public boolean appliesTo(OpNode node){
    if(node.getFactor().isZero())
      return true;
    if(node.getChildren() == null || ! (node instanceof MultOp))
      return false;
    for(int i=0; i<node.getChildren().length; i++)
      if(node.getChildren()[i] instanceof NumberOp && node.getChildren()[i].getResult().isZero())
        return true;
    return false;
  }
  
  /**
   *  Replaces the node with a zero 
   * {@link net.mumie.mathletfactory.algebra.op.node.NumberOp}.
   */
  public OpNode transform(OpNode node){
    //System.out.println("replacing "+node+" by 0");
    OpNode newNode = new NumberOp(node.getNumberClass(), 0);
    newNode.setParent(node.getParent());
    node = newNode;
    return node;
  }
}


