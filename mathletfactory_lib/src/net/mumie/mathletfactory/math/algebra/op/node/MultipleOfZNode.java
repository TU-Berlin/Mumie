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

package net.mumie.mathletfactory.math.algebra.op.node;

import java.text.DecimalFormat;

import net.mumie.mathletfactory.math.algebra.rel.node.RelNode;
import net.mumie.mathletfactory.math.number.NumberFactory;


/**
 * This node represents an Operation that is a multiple of Z, i.e. this node
 * represents any integer Number that is needed solving an equation or
 * relation where this node occurs in a left- or right hand side.
 * WARNING: This node is only for internal use and should not be used by
 * Application Programmers!
 * @author Paehler
 * @mm.docstatus finished
 */
public class MultipleOfZNode extends OpNode {

  private Integer m_zValue = null;

  private boolean m_naturalsOnly = false;

  public MultipleOfZNode(Class entryClass) {
    super(entryClass);
    m_base.setDouble(0);
    m_zValue = new Integer(0);
  }

  public boolean isLeaf(){
    return true;
  }

  protected String nodeToContentMathML() {
    return "<integers/>";
  }

  public String nodeToString() {
    return m_naturalsOnly ? "\u2115" : "\u2124";
  }

  public String toString(int printFlags, DecimalFormat format){
    return printFactorAndExponent(nodeToString(), printFlags, format);
  }

  /**
   * Returns true if only the natural numbers are allowed.
   */
  public boolean isNaturalsOnly(){
    return m_naturalsOnly;
  }

  /**
   * Sets, whether only the natural numbers are allowed.
   */
  public void setNaturalsOnly(boolean naturalsOnly){
    m_naturalsOnly = naturalsOnly;
  }

  public int getMinChildrenNr(){
    return 0;
  }

  public int getMaxChildrenNr(){
    return 0;
  }

  public void calculate(){
    if(m_zValue == null)
      throw new IllegalStateException("calculation not allowed for this node without setting Z!");
    m_base = NumberFactory.newInstance(m_numberClass, m_zValue.doubleValue());
  }

  public void calculateDouble(){
    if(m_zValue == null)
      throw new IllegalStateException("calculation not allowed for this node without setting Z!");
    m_base.setDouble(m_zValue.doubleValue());
  }

  public void setZ(int z){
    m_zValue = new Integer(z);
  }

  public void setZ(Integer z){
    m_zValue = z;
  }

  public Integer getZ(){
    return m_zValue;
  }

  /**
   * Should not be called for this node.
   * @throws IllegalArgumentException
   * @see net.mumie.mathletfactory.math.algebra.op.node.OpNode#getDerivative(String)
   */
  public OpNode getDerivative(String string){
    throw new IllegalArgumentException();
  }

  /**
   * Should not be called for this node.
   * @throws IllegalArgumentException
   */
  public OpNode[] solveStepFor(String identifier){
    throw new IllegalArgumentException();
  }

  /**
   * Should not be called for this node.
   * @throws IllegalArgumentException
   */
  public RelNode getMonotonicDecreasingRel(OpNode operand){
    throw new IllegalArgumentException();
  }

  /**
   * Should not be called for this node.
   * @throws IllegalArgumentException
   */
  public RelNode getMonotonicIncreasingRel(OpNode operand){
    throw new IllegalArgumentException();
  }

  /**
   * Should not be called for this node.
   * @throws IllegalArgumentException
   */
  public RelNode getNodeDefinedRel(OpNode operand){
    throw new IllegalArgumentException();
  }

  /**
   * Should not be called for this node.
   * @throws IllegalArgumentException
   */
  public RelNode getZeroRel(OpNode operand){
    throw new IllegalArgumentException();
  }

  /**
   * Should not be called for this node.
   * @throws IllegalArgumentException
   */
  public RelNode getZeroRel(){
    throw new IllegalArgumentException();
  }


}
