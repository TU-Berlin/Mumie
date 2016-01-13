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

import net.mumie.mathletfactory.math.algebra.rel.node.NotRel;
import net.mumie.mathletfactory.math.algebra.rel.node.RelNode;
import net.mumie.mathletfactory.math.algebra.rel.rule.RelRule;

/**
 * This rule removes a 
 * {@link net.mumie.mathletfactory.algebra.rel.node.NotRel} by replacing it
 * with the negated children.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public class RemoveNotRelRule extends RelRule {

  /**
   * Returns true if <code>node</code> is a 
   * {@link net.mumie.mathletfactory.algebra.rel.node.NotRel}.
   * 
   * @see net.mumie.mathletfactory.algebra.rel.rule.RelRule#appliesTo(RelNode)
   */
  public boolean appliesTo(RelNode node) {
    if(node instanceof NotRel)
      return true;
    return false;
  }
  
  /**
   *  Returns the negated child of <code>node</code>.  
   */
  public RelNode transform(RelNode node) {
    //System.out.println("negating "+node.getChildren()[0]);
    return node.getChildren()[0].negate(false);
  }

  
}
