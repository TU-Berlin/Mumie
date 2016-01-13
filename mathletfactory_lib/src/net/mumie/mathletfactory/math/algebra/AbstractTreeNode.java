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

package net.mumie.mathletfactory.math.algebra;

import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberTypeDependentIF;

/**
 * This class implements the basic functionality of all node-like structures used in 
 * algebra (operations, relations, sets,..).
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public abstract class AbstractTreeNode implements Cloneable, NumberTypeDependentIF {
  
  /** The number class being used. */
  protected Class m_numberClass;
  
  /** Sets the number class (a subclass of {@link MNumber}) being used. */
  public AbstractTreeNode(Class numberClass){
    if(numberClass == null)
      System.out.println("Warning: numberClass of "+getClass()+" is null");
    m_numberClass = numberClass;
  }

  /** The children of this node, is <code>null</code> for leaves. */
  protected abstract AbstractTreeNode[] children();

  /** Sets the children of this node, <code>null</code> for leaves. */
  public abstract void setChildren(AbstractTreeNode[] children);
  
  /** Returns a reference to the parent node. */
  protected abstract AbstractTreeNode parent(); 

  /** Sets the reference to the parent node. */  
  public abstract void setParent(AbstractTreeNode node);
  
  /* Returns true for nodes without children. */
  public boolean isLeaf(){
    return children() == null || children().length == 0;
  }
  
  /**
   *  Returns the depth of the subtree rooted by this node. The
   *  depth is defined as depth(leaf) = 0 and depth(node(subtree1, subtree2, ...))
   *  = 1+max(depth(subtree1), depth(subtree2),...). 
   */
  public int getDepth(){
    int depth = 0;
    if(isLeaf())
      return depth;
    for(int i=0;i<children().length;i++)
      depth = Math.max(depth, children()[i].getDepth());
    return depth+1;  
  }
  
  /**
   *  Returns true if the reference to the given node is contained in the 
   *  subtree represented by this node (Will also return true, if 
   *  <code>node == this</code>.
   */
  public boolean contains(AbstractTreeNode node){
    if(this == node)
      return true;
    if(children() == null)
      return false;
    boolean retVal = false;
    for(int i=0;i<children().length;i++)
      retVal |= children()[i].contains(node);
    return retVal;  
  }
  
  /**
   *  If <code>node</code> is equal to one of the children of this node it
   *  is replaced with <code>with</code>, otherwise this method is recursively 
   *  called for each child.  
   */
  public void replaceByValue(AbstractTreeNode node, AbstractTreeNode with) {
    if (isLeaf())
      return;
    for (int i = 0; i < children().length; i++)
      if (node.equals(children()[i])) {
        with.setParent(this);
        setChildren(extractNode(children(), children()[i]));
        setChildren(insertNode(children(), with, false));
        return;
      } else
        children()[i].replaceByValue(node, with);
  }

  /**
   * If <code>node</code> is one of the children of this node it
   * is replaced with <code>with</code>, otherwise this method is recursively 
   * called for each child.  
   */
  public void replace(AbstractTreeNode node, AbstractTreeNode with) {
    if (isLeaf())
      return;
    for (int i = 0; i < children().length; i++)
      if (node == children()[i]) {
        with.setParent(this);
        setChildren(extractNode(children(), children()[i]));
        setChildren(insertNode(children(), with, false));
        return;
      } else
        children()[i].replaceByValue(node, with);
  }


  /** Removes the <code>CloneNotSupportedException</code>. */ 
  public Object clone(){
    try {
      return super.clone();   
    } catch(CloneNotSupportedException e){e.printStackTrace();}
    return null;
  }

  /**
   *  Replaces <code>node</code> with <code>with</code> by transferring the parent 
   *  of <code>node</code> onto <code>with</code>.
   */
  public static AbstractTreeNode replaceWith(AbstractTreeNode node, AbstractTreeNode with) {
    with.setParent(node.parent());
    return with;
  }
  
  /**
   *  Extracts <code>toBeExtracted</code> from <code>nodes</code> and returns
   *  the shortened array. Throws an exception if <code>toBeExtracted</code> is
   *  not contained in <code>nodes</code>.
   */
  public static AbstractTreeNode[] extractNode(AbstractTreeNode[] nodes, AbstractTreeNode toBeExtracted) {
    AbstractTreeNode[] newNodes = new AbstractTreeNode[nodes.length - 1];
    int counter = 0;
    for (int i = 0; i < nodes.length; i++)
      if (nodes[i] != toBeExtracted)
        newNodes[counter++] = nodes[i];
    if (counter != nodes.length - 1)
      throw new IllegalArgumentException(toBeExtracted + " not contained in " + nodes);
    return newNodes;
  }
  

  /**
   * Inserts <code>toBeInserted</code> into the array <code>nodes</code> and returns it. 
   * If <code>clone</code> is set to false this is done for the original array, 
   * otherwise it will be cloned before insertion.
   */
  public static AbstractTreeNode[] insertNode(AbstractTreeNode[] nodes, AbstractTreeNode toBeInserted, boolean clone) {
    AbstractTreeNode[] newNodes = new AbstractTreeNode[nodes.length + 1];
    if (clone)
      for (int i = 0; i < nodes.length; i++)                    
        newNodes[i] = (AbstractTreeNode) nodes[i].clone();
    else
      System.arraycopy(nodes, 0, newNodes, 0, nodes.length);
    newNodes[nodes.length] = toBeInserted;
    return newNodes;
  } 
    
  /** Returns the number class (a subclass of {@link MNumber}) being used. */
  public Class getNumberClass(){
    if(m_numberClass == null || !(MNumber.class.isAssignableFrom(m_numberClass)))
      System.out.println("Warning: number class of "+getClass()+" is "+m_numberClass);
    return m_numberClass;
  }
}
