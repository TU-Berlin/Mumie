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

import net.mumie.mathletfactory.math.algebra.rel.node.AllRel;
import net.mumie.mathletfactory.math.algebra.rel.node.NullRel;
import net.mumie.mathletfactory.math.algebra.rel.node.RelNode;
import net.mumie.mathletfactory.math.algebra.rel.node.SimpleRel;
import net.mumie.mathletfactory.math.algebra.rel.rule.RelRule;
import net.mumie.mathletfactory.math.number.MNumber;

/**
 * This rule removes a simple relation
 * {@link net.mumie.mathletfactory.algebra.rel.node.SimpleRel}
 * that contains only constants by 
 * evaluating it.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public class RemoveConstantSimpleRelRule extends RelRule {

  /** 
   * Returns true if the node is a 
   * {@link net.mumie.mathletfactory.algebra.rel.node.SimpleRel} that contains
   * only constants.
   * @see net.mumie.mathletfactory.algebra.rel.rule.RelRule#appliesTo(RelNode)
   */
  public boolean appliesTo(RelNode node) {
    if(node instanceof SimpleRel 
    && ((SimpleRel)node).getLeftHandSide().getUsedVariables().length == 0
    && ((SimpleRel)node).getRightHandSide().getUsedVariables().length == 0)
      return true;
    return false;
  }

  /**
   * Returns a 
   * {@link net.mumie.mathletfactory.algebra.rel.node.NullRel} if the
   * simple relation <code>node</code> evaluates to true, false otherwise.
   * @see net.mumie.mathletfactory.algebra.rel.rule.RelRule#transform(net.mumie.mathletfactory.algebra.rel.node.RelNode)
   */
  public RelNode transform(RelNode node) {
    if(((SimpleRel)node).evaluate(new MNumber[]{}))
      return new AllRel(node.getNumberClass());
    return new NullRel(node.getNumberClass());
  }

  
}
