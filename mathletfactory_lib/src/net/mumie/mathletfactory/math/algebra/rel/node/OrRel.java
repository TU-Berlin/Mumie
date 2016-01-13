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

import java.util.HashMap;
import java.util.List;

import net.mumie.mathletfactory.math.algebra.rel.Relation;
import net.mumie.mathletfactory.math.set.FiniteBorelSet;
import net.mumie.mathletfactory.math.set.Interval;

/**
 *  This class models the OR-Conjunction in the Rel-Scheme.
 *  It has two or more {@link RelNode}s as children.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */

public class OrRel extends ComplexRel {

  public OrRel(RelNode[] children) {
    super(children);
  }

  public OrRel(RelNode child1, RelNode child2) {
    this(new RelNode[] { child1, child2 });
  }
  
  /**
   * Returns true, if {@link RelNode#evaluate(HashMap variableValues)} is
   * true for any of the children.
   * @see net.mumie.mathletfactory.algebra.rel.node.RelNode#evaluate(HashMap)
   */
  public boolean evaluate(HashMap variableValues) {
    boolean result = false;
    for (int i = 0; i < m_children.length; i++){
      result = m_children[i].evaluate(variableValues);
    if(result)
      return result;
    }
    return result;
  }
  
  /**
   * Returns true, if {@link RelNode#evaluateFast} is true for any of the children.
   * 
   * @see net.mumie.mathletfactory.algebra.rel.node.RelNode#evaluateFast()
   */
  
  public boolean evaluateFast() {
    boolean result = false;
    for (int i = 0; i < m_children.length; i++){
      result = m_children[i].evaluateFast();
      if(result)
        return result;
    }
    return result;
  }

  /**
   * Adds the children of this node as new subrelations if they are not 
   * <code>OrRel</code>s themselves. Otherwise the subrelations of the children
   * are added.
   * @see net.mumie.mathletfactory.algebra.rel.node.RelNode#getSubrelations(List)
   */
  public List getSubrelations(List subrelations){
    for(int i=0; i<m_children.length;i++)
      if(!(m_children[0] instanceof OrRel))
        subrelations.add(new Relation(m_children[i], getNormalForm()));
      else
        subrelations.addAll(m_children[i].getSubrelations(subrelations));
    return subrelations;
  }
   
  public RelNode negate(boolean deepCopy){
    RelNode[] newChildren = m_children;
    if(deepCopy)
      newChildren = new RelNode[m_children.length];
     
    for(int i=0;i<m_children.length;i++)
      if(deepCopy)
        newChildren[i] = new NotRel((RelNode)m_children[i].clone());
      else
        newChildren[i] = new NotRel(m_children[i]);
    return new AndRel(newChildren);
  } 
  
  /** Returns "OR". */
  public  String getStringSymbol(){
    return "OR";
  }
  
  /**
   * Returns true if any <code>values</code> is true, false otherwise.
   * @see net.mumie.mathletfactory.algebra.rel.node.ComplexRel#getLogicalResult(boolean[])
   */
  public boolean getLogicalResult(boolean[] values){
    for(int i=0;i<values.length;i++)
      if(values[i])
        return true;
    return false;
  }

  /** Joins the intervals for which the children of this node are true. */
  public FiniteBorelSet getTrueFor(double epsilon, Interval interval){
    FiniteBorelSet retVal = m_children[0].getTrueFor(epsilon, interval);
    for(int i=1;i<m_children.length;i++)      
      retVal.join(m_children[i].getTrueFor(epsilon, interval));
    return retVal;
  }

  /** Intersects the intervals for which the children of this node are false. */
  public FiniteBorelSet getFalseFor(double epsilon, Interval interval){
    FiniteBorelSet retVal = m_children[0].getTrueFor(epsilon,interval);
    for(int i=1;i<m_children.length;i++)      
      retVal.intersect(m_children[i].getTrueFor(epsilon,interval));
    return retVal;
  }
    
}
