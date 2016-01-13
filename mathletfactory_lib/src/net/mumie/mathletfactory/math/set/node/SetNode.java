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

package net.mumie.mathletfactory.math.set.node;

import net.mumie.mathletfactory.action.message.TodoException;
import net.mumie.mathletfactory.math.algebra.AbstractTreeNode;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.set.FiniteBorelSet;
import net.mumie.mathletfactory.math.set.Interval;

/**
 *  Offers basic functionality for set nodes.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public abstract class SetNode extends AbstractTreeNode implements Cloneable, Comparable {

  /** The "child" nodes that act as operands of this nodes. */
  protected SetNode[] m_children;
  
  /** The "parent" nodes that this node act as operand for. */
  protected SetNode m_parent;

  /** Creates this node with emtpy children and parent. */
  public SetNode(Class numberClass){
    super(numberClass);
  }

  /** Returns true if the given number is inside the set represented by this subtree. */
  public abstract boolean contains(double aNumber);
  
  /** Returns true if the given number is inside the set represented by this subtree. */
  public abstract boolean contains(MNumber aNumber);
  

  /**
   * Returns the finite approximation of this <code>BorelSet</code> with respect to the
   * given <code>boundingInterval</code> and the small-cut-off <code>epsilon</code>.
   */
  public abstract FiniteBorelSet getFiniteBorelSet(Interval boundingInterval, double eps);
 
  
  /** Sets the "child" nodes that act as operands of this nodes. */
  public void setChildren(SetNode[] children){
    m_children = children;
  }

  /** Returns the "child" nodes that act as operands of this nodes. */
  protected AbstractTreeNode[] children() {
    return m_children;
  }

  /** Returns the "child" nodes that act as operands of this nodes. */
  public SetNode[] getChildren(){
    return m_children;
  }

  /** Sets the "child" nodes that act as operands of this nodes. */
  public void setChildren(AbstractTreeNode[] children) {
    setChildren((SetNode[]) children);
  }

  /** Returns the "parent" nodes that this node act as operand for. */
  protected AbstractTreeNode parent() {
    return m_parent;
  }

  /** Sets the "parent" nodes that this node act as operand for. */
  public void setParent(AbstractTreeNode node) {
    m_parent = (SetNode) node;
  }
  
  /** Not yet implemented. */
  public int compareTo(Object object){
    throw new TodoException();
  } 
}
