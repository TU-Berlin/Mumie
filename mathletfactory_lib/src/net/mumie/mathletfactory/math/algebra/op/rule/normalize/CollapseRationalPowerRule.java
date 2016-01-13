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

import net.mumie.mathletfactory.math.algebra.op.node.NrtOp;
import net.mumie.mathletfactory.math.algebra.op.node.NumberOp;
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.math.algebra.op.node.PowerOp;
import net.mumie.mathletfactory.math.algebra.op.rule.OpRule;
import net.mumie.mathletfactory.math.number.MRational;

/**
 *  If the node is a PowerOp with a rational exponent, it may be collapsed to
 *  the base child node with the exponent numerator stored internally in the 
 *  node. If the denominator of the exponent is n != 1 an n-th root node will
 *  be added as parent.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class CollapseRationalPowerRule extends OpRule {
  
  MRational exponent;
  
  /**
   *  Evaluates to true, if the node is a PowerOp and its exponent is a
   *  NumberOp with rational value.
   */
  public boolean appliesTo(OpNode node) {
    //System.out.println("checking "+node.toDebugString());
    if (node instanceof PowerOp && node.getChildren()[1] instanceof NumberOp) {  
      if (node.getNumberClass() == MRational.class)
        exponent = (MRational) node.getChildren()[1].getResult();
      else{
        if(node.getChildren()[1].getExponent() < 0){
          exponent = new MRational(node.getChildren()[1].getResult(OpNode.PRINT_ALL ^ ~OpNode.PRINT_EXPONENT));
          exponent.power(node.getChildren()[1].getExponent());
        } else
          exponent = new MRational(node.getChildren()[1].getResult().getDouble());
      }
      if (exponent.getDouble()
        == node.getChildren()[1].getResult().getDouble())
        return true;
    }
    return false;
  }

  /**
   *  Replace PowerOp node by it's base (<code>children[0]</code>) and 
   *  multiply the base's exponent by the value of (<code>children[1]</code>)
   */
  public OpNode transform(OpNode node) {
    // initialize the new node as the original node's base
    OpNode newNode = node.getChildren()[0];

    // convert the exponent to a rational and if the denominator is != 1, apply
    // an additional nth root node

    NrtOp nthRoot = null;
    if (exponent.getDenominator() != 1)
      nthRoot =
        new NrtOp(node.getNumberClass(), (int) exponent.getDenominator());
    newNode.multiplyExponent(
      node.getExponent() * (int) exponent.getNumerator());
    newNode.setFactor(
      newNode.getFactor().power(
        (int) (node.getExponent() * exponent.getNumerator())));
    newNode.multiplyFactor(node.getFactor());

    // if we have an additional nth root node, we take it as the new node
    if (nthRoot != null) {
      nthRoot.setChildren(new OpNode[] { newNode });
      newNode = nthRoot;
    }
    newNode.setParent(node.getParent());
    node = newNode;

    return node;
  }
}

