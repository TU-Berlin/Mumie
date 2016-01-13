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
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.math.algebra.op.rule.OpRule;
import net.mumie.mathletfactory.math.number.MRational;

/**
 *  If a node is a 
 *  {@link net.mumie.mathletfactory.algebra.op.node.MultOp}
 *  and its children's exponents have a gcd &gt; 1, it is moved to 
 *  the exponent of the <code>MultOp</code>.
 * 
 * 	@author Paehler
 * 	@mm.docstatus finished
 */
public class NormalizeExponentsRule extends OpRule {
  
  public boolean appliesTo(OpNode node){
    if(!(node instanceof MultOp))
      return false;
    // check child nodes' exponents for a common gcd
    int gcd = node.getChildren()[0].getExponent();
    for(int i=1; i<node.getChildren().length;i++)
      gcd = (int)MRational.greatestCommonDivisor(gcd, node.getChildren()[i].getExponent());
    if(gcd > 1)
      return true;
    return false;
  }
  /**
   * Transfer the gcd of the children's exponent to <code>node</code>. 
   * @see net.mumie.mathletfactory.algebra.op.rule.OpRule#transform(OpNode)
   */
  public OpNode transform(OpNode node){
    int gcd = node.getChildren()[0].getExponent();
    for(int i=1; i<node.getChildren().length;i++)
      gcd = (int)MRational.greatestCommonDivisor(gcd, node.getChildren()[i].getExponent());
    // divide the nodes exponents by gcd
    for(int i=1; i<node.getChildren().length;i++)
      node.getChildren()[i].setExponent(node.getChildren()[i].getExponent()/gcd);
    node.multiplyExponent(gcd);
    return node;
  }
  
  
  
  
}


