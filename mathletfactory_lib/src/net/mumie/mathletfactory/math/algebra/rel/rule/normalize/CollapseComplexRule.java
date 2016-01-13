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

import net.mumie.mathletfactory.math.algebra.rel.node.ComplexRel;
import net.mumie.mathletfactory.math.algebra.rel.node.RelNode;
import net.mumie.mathletfactory.math.algebra.rel.rule.RelRule;

/**
 * This rule collapses complex nodes that have a children of the same type.
 * (E.g. an {@link net.mumie.mathletfactory.algebra.rel.node.AndRel} 
 * containing another <code>AndRel</code> as child).
 *  
 * @author Paehler
 * @mm.docstatus finished
 */
public class CollapseComplexRule extends RelRule {

  
  /**
   * Applies to nodes that are 
   * {@link net.mumie.mathletfactory.algebra.rel.node.ComplexRel}s and
   * have at least one child of the same type.
   * @see net.mumie.mathletfactory.algebra.rel.rule.RelRule#appliesTo
   */
  public boolean appliesTo(RelNode node) {
    if(node instanceof ComplexRel && node.getFirstChildOfType(node.getClass(), 0) != null)
      return true;
    return false;
  }

  /**
   * Add the children of the child with the same relation type to the
   * nodes children (without the child with the same relation type).  
   * @see net.mumie.mathletfactory.algebra.rel.rule.RelRule#transform
   */
  public RelNode transform(RelNode node) {
    RelNode sameRelChild = node.getFirstChildOfType(node.getClass(), 0);
    RelNode[] children = node.getChildren();
    children = RelNode.extractNode(children, sameRelChild);
    node.setChildren(RelNode.insertNodes(sameRelChild.getChildren(), children));
    return node;    
  }
}
