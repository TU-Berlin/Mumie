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
import net.mumie.mathletfactory.math.algebra.op.OpTransform;
import net.mumie.mathletfactory.math.algebra.op.node.AbsOp;
import net.mumie.mathletfactory.math.algebra.op.node.NrtOp;
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.math.algebra.op.rule.OpRule;

/**
 *  This rule replaces a
 *  {@link net.mumie.mathletfactory.algebra.op.node.NrtOp} by its child
 *  if the node's or its child's exponent are a divisor of n.
 *
 *  @author Paehler
 *  @mm.docstatus finished
 *  @todo divide n and the exponent by their greatest common divisor.
 */

public class CollapseNrtRule extends OpRule {
  
  /**
   *  This rule applies only to
   *  {@link net.mumie.mathletfactory.algebra.op.node.NrtOp}s
   *  which has either an exponent m*n or a child with an exponent m*n.
   */
  public boolean appliesTo(OpNode node)
  {
    //System.out.println("checking "+node.toDebugString());
    // applies only to function nodes and nth-root nodes
    if(!(node instanceof NrtOp))
      return false;
    OpNode child = node.getChildren()[0];
    // has the node or its child an exponent m*n?
    if(((Math.abs(child.getExponent()) > 1 &&  child.getExponent() % ((NrtOp)node).getN() == 0 )
            ||(Math.abs(node.getExponent()) > 1 && node.getExponent() % ((NrtOp)node).getN() == 0 )))     
      return true;
    return false;
  }
  
  /**
   *  Replace the node by its child and set its factor and exponent accordingly.
   */
  public OpNode transform(OpNode node)
  {
    //System.out.println("ACTION: node is"+node.toDebugString());
    
    // the child, that will be the new node, must be normalized in order to have correct placed exponents
    OpNode child = OpTransform.normalize(node.getChildren()[0]);
    
    NrtOp root = (NrtOp)node;
    boolean isAbs = root.getN() % 2 == 0 && child.getExponent() >= 2;

    // the new factor is the childs factor raised to power of node's exponent
    // multiplied with the node's factor    
    child.setFactor(child.getFactor().power(root.getExponent()).nthRoot(root.getN()).mult(root.getFactor()));
    // multiply the new nodes exponent with the node's exponent and divide it by n
    child.setExponent(child.getExponent() * root.getExponent() / root.getN());
    if(isAbs)
      child = new AbsOp(child);
    child.setParent(node.getParent());
    // replace the node by its child
    node = child;
    return node;
  }
}


