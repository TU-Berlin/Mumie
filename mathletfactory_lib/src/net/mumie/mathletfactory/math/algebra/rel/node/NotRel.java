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

package net.mumie.mathletfactory.math.algebra.rel.node;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;

import net.mumie.mathletfactory.math.set.Interval;

/**
 * This class models the NOT-Operation on relations. It has one
 * {@link RelNode} as child.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */

public class NotRel extends RelNode {


  public NotRel(RelNode child){
    super(child.getNumberClass());
    m_children = new RelNode[]{child};
  }

  /**
   * Returns true, if {@link RelNode#evaluate(HashMap variableValues)} is
   * false for the child.
   * @see net.mumie.mathletfactory.algebra.rel.node.RelNode#evaluate(HashMap)
   */
  public boolean evaluate(HashMap variableValues) {
    return !m_children[0].evaluate(variableValues);
  }

  /**
   * Returns true, if {@link RelNode#evaluate(HashMap variableValues)} is
   * false for the child. 
   * @see net.mumie.mathletfactory.algebra.rel.node.RelNode#evaluateFast()
   */
  public boolean evaluateFast(){
    return !m_children[0].evaluateFast();
  }

  public void separateFor(String identifier) {
    m_children[0].separateFor(identifier);
  }

  public void setNormalForm(int normalForm) {
    m_children[0].setNormalForm(normalForm);
  }

  public int getNormalForm() {
    return m_children[0].getNormalForm();
  }

  public HashSet getUsedVariables(HashSet usedVariables) {
    return m_children[0].getUsedVariables(usedVariables);
  }

  public RelNode negate(boolean deepCopy){
    if( deepCopy)
      return (RelNode)m_children[0].clone();
    else
      return (RelNode)m_children[0];
  }

  /**
   * Returns "NOT(x)" where x is the child expression.
   * @see java.lang.Object#toString()
   */
  public String toString(DecimalFormat format){
    return "NOT("+m_children[0].toString(format)+")";
  }
  
  /**
   * Returns "NOT(x)" where x is the child expression.
   * @see java.lang.Object#toString()
   */
  public String toString(){
    return "NOT("+m_children[0].toString()+")";
  }
  
  public boolean isLeaf(){
    return false;
  }
    
  public Class getNumberClass() {
    return m_children[0].getNumberClass();
  }
   
  /**
   *  Since the relation should be in normalized and separated state, a call of 
   *  this method should not occur. 
   */
  public Interval[] getIntervalsFalse(double epsilon,  Interval interval){
    throw new IllegalStateException("Relation should be in normalized, separated state!");
  }
    
  /**
   *  Since the relation should be in normalized and separated state, a call of 
   *  this method should not occur. 
   */
  public Interval[] getIntervalsTrue(double epsilon,  Interval interval){
    throw new IllegalStateException("Relation should be in normalized, separated state!");
  }
}
