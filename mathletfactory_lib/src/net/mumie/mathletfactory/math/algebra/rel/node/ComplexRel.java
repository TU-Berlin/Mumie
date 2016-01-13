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
import java.util.HashSet;

import net.mumie.mathletfactory.math.algebra.rel.RelTransform;
import net.mumie.mathletfactory.math.algebra.rel.Relation;
import net.mumie.mathletfactory.math.set.Interval;

/**
 *  This class models complex Relations or Conjunctions in the Rel-Scheme.
 *  It has two or more {@link RelNode}s as children which are logically
 *  conjuncted (the type of conjunction is specified by the subclass).
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public abstract class ComplexRel extends RelNode {

  public ComplexRel(RelNode[] children) {
    super(children[0].getNumberClass());
    setChildren(children);
  }

  public ComplexRel(RelNode child1, RelNode child2) {
    this(new RelNode[] { child1, child2 });
  }
  
  /**
   * Returns the union of variables used by the children. 
   * @see net.mumie.mathletfactory.algebra.rel.node.RelNode#getUsedVariables(HashSet)
   */
  public HashSet getUsedVariables(HashSet usedVariables) {
    for (int i = 0; i < m_children.length; i++)
      m_children[i].getUsedVariables(usedVariables);
    return usedVariables;
  }

  public void separateFor(String identifier) {
    for (int i = 0; i < m_children.length; i++){
      if(!(m_children[i] instanceof SimpleRel))
        m_children[i].separateFor(identifier);
      else
        m_children[i] = RelTransform.separateFor(m_children[i], identifier);
    }
    setChildren(m_children);
  }
  
  /**
   * Returns true if all children are in normal form.
   * 
   * @see net.mumie.mathletfactory.algebra.rel.node.RelNode#isNormalForm()
   */
  public int getNormalForm() {
    int normalForm = Relation.NORMALIZE_OP_SEPARATED;
    for (int i = 0; i < m_children.length; i++)
      normalForm = Math.min(normalForm, m_children[i].getNormalForm());
    return normalForm;
  }

  /**
   * Sets in all children {@link RelNode#setNormalForm} to <code>normalForm</code>.
   * 
   * @see net.mumie.mathletfactory.algebra.rel.node.RelNode#setNormalForm(boolean)
   */
  public void setNormalForm(int normalForm) {
    for (int i = 0; i < m_children.length; i++)
      m_children[i].setNormalForm(normalForm);
  }

  /** 
   * To be implemented by subclass.
   * @see #toString
   */
  public abstract String getStringSymbol();

  /** 
   *  The string representation of this object is the concatenation of
   *  all children separated by {@link #getStringSymbol}.
   */
  public String toString(DecimalFormat format) {
      String result = parenthesesNeeded() ? "[" : "";
      for (int i = 0; i < m_children.length; i++) {
        result += m_children[i].toString(format);
        if (i + 1 < m_children.length)
          result += " "+getStringSymbol()+" ";
      }
      result += parenthesesNeeded() ? "]" : "";
      return result;
    }

  /** 
   *  The string representation of this object is the concatenation of
   *  all children separated by {@link #getStringSymbol}.
   */
  public String toString() {
      String result = parenthesesNeeded() ? "[" : "";
      for (int i = 0; i < m_children.length; i++) {
        result += m_children[i];
        if (i + 1 < m_children.length)
          result += " "+getStringSymbol()+" ";
      }
      result += parenthesesNeeded() ? "]" : "";
      return result;
    }
    
  public Class getNumberClass() {
    return m_children[0].getNumberClass();
  }
  
  /**
   * ComplexRels are no leaves, therefor this method returns false.
   * @see net.mumie.mathletfactory.algebra.rel.node.RelNode#isLeaf()
   */
  public boolean isLeaf(){
    return false;
  }
  
  /** Returns the boolean equivalent of this complex relation. */
  public abstract boolean getLogicalResult(boolean[] values); 
  
  /** Phantom Implementation. Should not be called. */
  public Interval[] getIntervalsTrue(double epsilon,  Interval interval){
    throw new IllegalStateException();
  }
  
  /** Phantom Implementation. Should not be called. */
  public Interval[] getIntervalsFalse(double epsilon,  Interval interval){
    throw new IllegalStateException();
  }
}
