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
import net.mumie.mathletfactory.math.algebra.op.node.NumberOp;
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.math.algebra.op.rule.OpRule;
import net.mumie.mathletfactory.math.number.MComplex;
import net.mumie.mathletfactory.math.number.MComplexRational;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;

/**
 *  Normalizes the format of real valued constant nodes 
 *  @author Paehler
 *  @mm.docstatus finished
 */

public class NormalizeConstantRule extends OpRule {
  
  /**
   *  Checks, if the node is a constant number which is not in normal form
   *  (i.e. exponent = 1, base = |base|, factor = +-1)
   *  <=> <code>Math.abs(node.getResult()) == Math.abs(node.getFactor())</code>).
   */
  public boolean appliesTo(OpNode node)
  {
    // applies to real NumberOps only
//    if(!(node instanceof NumberOp) || node.getNumberClass().isAssignableFrom(MComplex.class)
//    		|| node.getNumberClass().isAssignableFrom(MComplexRational.class))
	if(!(node instanceof NumberOp) || MComplex.class.isAssignableFrom(node.getNumberClass())
	   	|| MComplexRational.class.isAssignableFrom(node.getNumberClass()))
    return false;
    if(Math.abs(node.getFactor().getDouble()) != 1 || node.getExponent() != 1 || 
         (node.getResult().getDouble() < 0 && node.getFactor().getDouble() > 0))
      return true;
    return false;
  }
  
  /**
   *  Set the factor to the sign of the result and set the result to its abs()
   *  and exponent to 1.
   */
  public OpNode transform(OpNode node)
  {
    //System.out.println("normalizing "+node.toDebugString());
    
    MNumber result = node.getResult();
    if(result.getDouble() < 0)
      node.setFactor(NumberFactory.newInstance(node.getNumberClass(), -1));
    else
      node.setFactor(NumberFactory.newInstance(node.getNumberClass(), 1));
    result.abs();
    ((NumberOp)node).setResult(result);
    node.setExponent(1);
    return node;
  }
}

