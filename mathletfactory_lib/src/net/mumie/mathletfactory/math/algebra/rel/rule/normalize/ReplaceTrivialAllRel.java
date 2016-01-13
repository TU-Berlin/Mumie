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

import net.mumie.mathletfactory.math.algebra.op.node.AbsOp;
import net.mumie.mathletfactory.math.algebra.op.node.ExpOp;
import net.mumie.mathletfactory.math.algebra.op.node.NrtOp;
import net.mumie.mathletfactory.math.algebra.op.node.NumberOp;
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.math.algebra.op.node.SinhOp;
import net.mumie.mathletfactory.math.algebra.rel.Relation;
import net.mumie.mathletfactory.math.algebra.rel.node.AllRel;
import net.mumie.mathletfactory.math.algebra.rel.node.RelNode;
import net.mumie.mathletfactory.math.algebra.rel.node.SimpleRel;
import net.mumie.mathletfactory.math.algebra.rel.rule.RelRule;

/**
 * This rule removes a trivial simple relation, such as |x| > 0
 * and replaces it with an <code>AllRel</code>
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public class ReplaceTrivialAllRel extends RelRule {

  /**
   * Returns true if <code>node</code> is simple relation and trivial.
   * 
   * @see net.mumie.mathletfactory.algebra.rel.rule.RelRule#appliesTo(RelNode)
   */
  public boolean appliesTo(RelNode node) {
    if(!(node instanceof SimpleRel))
      return false;
    SimpleRel simpleRel = (SimpleRel)node;
    OpNode leftNode = simpleRel.getLeftHandSide().getOpRoot();
    OpNode rightNode = simpleRel.getRightHandSide().getOpRoot();
    if(isPositiveOp(leftNode) && rightNode instanceof NumberOp && ((NumberOp)rightNode).getResult().getSign()<=0
        && (simpleRel.getRelationSign().equals(Relation.GREATER) || simpleRel.getRelationSign().equals(Relation.GREATER_OR_EQUAL)))
      return true;
    if(isPositiveOp(rightNode) && leftNode instanceof NumberOp && ((NumberOp)leftNode).getResult().getSign()<=0
        && (simpleRel.getRelationSign().equals(Relation.LESS) || simpleRel.getRelationSign().equals(Relation.LESS_OR_EQUAL)))
      return true;
    return false;
  }
  
  private boolean isPositiveOp(OpNode node){
    if(node instanceof AbsOp ||
        node instanceof NrtOp  && ((NrtOp)node).getN() % 2 == 0 ||
        node instanceof AbsOp ||
        node instanceof ExpOp ||
        node instanceof SinhOp)
      return true;
    return false;
  }
  
  /**
   *  Return an <code>AllRel</code>.  
   */
  public RelNode transform(RelNode node) {
    //System.out.println("negating "+node.getChildren()[0]);
    return new AllRel(node.getNumberClass());
  }

  
}
