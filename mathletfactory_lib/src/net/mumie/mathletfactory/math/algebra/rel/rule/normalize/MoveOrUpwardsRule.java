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

import net.mumie.mathletfactory.math.algebra.rel.node.AndRel;
import net.mumie.mathletfactory.math.algebra.rel.node.OrRel;
import net.mumie.mathletfactory.math.algebra.rel.node.RelNode;
import net.mumie.mathletfactory.math.algebra.rel.rule.RelRule;

/**
 *  This rule implements the distributive law for logical operations:
 *  <pre>[x OR y] AND a <=> [x AND a] OR [y AND a]</pre>
 *  or
 *  <pre>[x OR y OR z] AND a AND b <=> [x AND a AND b] OR [y AND a AND b] OR [z AND a AND b]</pre>
 *  As can be seen in the example, by applying this rule, the 
 *  {@link net.mumie.mathletfactory.algebra.rel.node.AndRel}s are moved 
 *  downwards in the tree, whereas the 
 *  {@link net.mumie.mathletfactory.algebra.rel.node.OrRel}s are moved
 *  upwards.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class MoveOrUpwardsRule extends RelRule {

  /**
   *  Returns true if the node is an 
   *  {@link net.mumie.mathletfactory.algebra.rel.node.AndRel} that has 
   *  an {@link net.mumie.mathletfactory.algebra.rel.node.OrRel} as
   *  child.
   *  @see net.mumie.mathletfactory.algebra.rel.rule.RelRule#transform
   */
  public boolean appliesTo(RelNode node) {
    if(node instanceof AndRel && node.getFirstChildOfType(OrRel.class,0) != null)
      return true;
    return false;
  }

  /**
   *  Replace <code>AndRel</code> with its first <code>OrRel</code> child and
   *  replace each child of the <code>OrRel</code> with a <code>AndRel</code>
   *  that has the <code>OrRel</code>s original child and the other children
   *  of the original <code>AndRel</code> as new children (Yes, it sounds a bit
   *  complicated! ;-)
   *  @see net.mumie.mathletfactory.algebra.rel.rule.RelRule#appliesTo
   */
  public RelNode transform(RelNode andRel) {
    RelNode childOr = andRel.getFirstChildOfType(OrRel.class, 0);
    childOr.setParent(andRel.getParent());
    RelNode[] andChildren = RelNode.extractNode(andRel.getChildren(), childOr);
    RelNode[] orChildren = childOr.getChildren();
    for(int i=0; i < orChildren.length; i++)
      orChildren[i] = new AndRel(RelNode.insertNode(andChildren, orChildren[i], true));
    childOr.setChildren(orChildren);
    return childOr;
  }
}
