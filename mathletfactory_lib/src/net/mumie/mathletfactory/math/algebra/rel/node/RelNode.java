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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import net.mumie.mathletfactory.math.algebra.AbstractTreeNode;
import net.mumie.mathletfactory.math.algebra.rel.Relation;
import net.mumie.mathletfactory.math.set.FiniteBorelSet;
import net.mumie.mathletfactory.math.set.Interval;

/**
 *  This class is the base for all kind of complex relations. Its 
 *  subclasses represent the nodes in a Relation Tree constructed by 
 *  {@link net.mumie.mathletfactory.algebra.rel.RelParser}. The leaves
 *  in the relation tree are the {@link #SimpleRel simple relation}s.
 *  
 *  @author Paehler
 *  @mm.docstatus finished
 */

public abstract class RelNode extends AbstractTreeNode implements Comparable {
  
  /** The children of this node. */
  protected RelNode[] m_children;

  /** The direct ancestor of this node in the Relation Tree. */
  protected RelNode m_parent;
 
  /** The relation this node belongs to. */
  protected Relation m_relation;

  public RelNode(Class numberClass){
    super(numberClass);
  }
 
  protected AbstractTreeNode[] children() {
    return m_children;
  }

  protected AbstractTreeNode parent() {
    return m_parent;
  }

  public void setChildren(AbstractTreeNode[] children) {
    setChildren((RelNode[])children);
  }

  public void setParent(AbstractTreeNode node) {
    setParent((RelNode)node);
  }

  /** 
   *  Returns true if the relation represented by this node evaluates to true 
   *  for <code>variableValues</code>.
   *  
   *  @param variableValues a mapping of identifiers (Strings)
   *                       to values (MMNumbers)
   */  
  public abstract boolean evaluate(HashMap variableValues);
  
  /** 
   *  Returns true if the relation represented by this node evaluates to true 
   *  for the variable values already assigned.
   *  @see net.mumie.mathletfactory.algebra.rel.Relation#evaluateFast 
   */  
  public abstract boolean evaluateFast();
  
  /**
   *  Performs for each leaf (a {@link SimpleRel}) a simple solving strategy: Leave 
   *  everything, that contains the given identifier on the left hand side and 
   *  put the rest onto the right hand side.
   */
  public abstract void separateFor(String identifier);
  
  /**
   *  Determines whether the operations on the leaves should be in normalform. 
   */
  public abstract void setNormalForm(int normalForm);
  
  /**
   *  Returns whether the relation and the operations on the leaves should be 
   *  in normalform. 
   */
  public abstract int getNormalForm();
  
  /** Returns all used variables as a HashSet of identifiers (Strings). */
  public abstract HashSet getUsedVariables(HashSet usedVariables);  
  
  /**
   *  Logically negates the relation represented by this node and returns this. 
   * 
   * @param deepCopy true if the complete relation tree should be deep copied
   *                 otherwise only the top node will be new.
   */ 
   
  public abstract RelNode negate(boolean deepCopy);
  
  /**
   *  Defines an order of nodes, which is both needed for ordering (e.g. when
   *  checking for equality) and for output.
   *  @see java.lang.Comparable#compareTo(Object)
   */
  public int compareTo(Object shouldBeANode){
    RelNode aNode = (RelNode) shouldBeANode;
    // nodes with greater depth come first
    if(getDepth(false) > aNode.getDepth(false))
      return -1;
    if(getDepth(false) < aNode.getDepth(false))
      return 1;
      
    // if both nodes are simple relations, the one with the deeper operation tree comes first. 
    if(aNode instanceof SimpleRel && this instanceof SimpleRel){
      if(getDepth(true) > aNode.getDepth(true))
        return -1;
      if(getDepth(true) < aNode.getDepth(true))
        return 1;
    }
      
    // nodes with wider string representation come first for AndRels, 
    // for OrRels it is the other way around
    if(toString().length() > aNode.toString().length())
      return this instanceof AndRel ? -1 : 1;
    if(toString().length() < aNode.toString().length())
      return this instanceof AndRel ? 1 : -1;
    
    // compare the string representations lexicographically
    return toString().compareTo(aNode.toString());
  }
   
  /**
   * Two <code>RelNode</code>s are considered equal if their String
   * representations are equal.
   * 
   * @see java.lang.Object#equals(Object)
   */
  public boolean equals(Object shouldBeARelNode){
    if(!(shouldBeARelNode instanceof RelNode))
      return false;
    return compareTo(shouldBeARelNode) == 0;
  }
  
  /** 
   * Returns the list of subrelations (i.e. the relations in which no 
   * {@link OrRel} occurs. This default implementation simply returns
   * the argument.
   * 
   * @see OrRel#getSubrelations
   */   
  public List getSubrelations(List subrelations){
    return subrelations;
  }
  
  /** 
   * Returns the list of all leaves in the subtree rooted by this node, that 
   * are {@link SimpleRel}s.
   */   
  public List getSimpleRelations(List simpleRelations){
    if(isLeaf()){
     if(this instanceof SimpleRel)
        simpleRelations.add(this);
      return simpleRelations;
    }
    for(int i=0;i<m_children.length;i++)
      m_children[i].getSimpleRelations(simpleRelations);
    return simpleRelations;
  }
  
  /** Returns true if the node has no children. */ 
  public abstract boolean isLeaf();
  
  /** Returns the direct ancestor of this node in the relation tree. */
  public RelNode getParent(){
    return m_parent;
  }
  
  /** Returns the direct ancestor of this node in the relation tree. */
  public void setParent(RelNode node){
    m_parent = node;
  }
  
  /** 
   * Inserts the argument as child nodes, after sorting and checking that their
   * size is >= 2. 
   */
  public void setChildren(RelNode[] children) {
    Arrays.sort(children);
    if (children.length < 2 && !(this instanceof NotRel))
      throw new IllegalArgumentException("children must have at least size 2!");
    if (m_children != null)
      for (int i = 0; i < m_children.length; i++)
        m_children[i].m_parent = null;
    m_children = children;
    for (int i = 0; i < m_children.length; i++){
      m_children[i].m_parent = this;
      m_children[i].setRelation(m_relation);
    }
  }

  /**
   * Returns the children of this node.
   */
  public RelNode[] getChildren() {
    return m_children;
  }

  /**
   * Returns the first child in the list of children, that is of type
   * <code>nodeType</code>.
   */
  public RelNode getFirstChildOfType(Class nodeType, int startIndex){
    if(m_children == null)
      return null;
    for(int i=startIndex; i<m_children.length;i++)
      if(nodeType.isInstance(m_children[i]))
        return m_children[i];
    return null;
  }
  
  /** 
   *  Returns if this node needs parentheses when displayed. It returns true 
   *  for a non-root node which is of different class than its parent.
   */
  public boolean parenthesesNeeded(){
    if(this instanceof ComplexRel && m_parent != null && !m_parent.getClass().equals(getClass()))
      return true;
    return false;
  }
  
  /**
   * Returns the depth of the subtree rooted by this node.
   * The depth is defined as depth(leaf) = 0 and depth(node(subtree1, subtree2, ...))
   * = 1+max(depth(subtree1), depth(subtree2),...). 
   * If <code>opNodesCount</code> is set to true, the
   * {@link net.mumie.mathletfactory.algebra.op.node.OpNode}s do of
   * {@link SimpleRel}s also count as tree nodes.
   * @see SimpleRel#getDepth
   */
  public int getDepth(boolean opNodesCount){
    int depth = 0;
    if(isLeaf())
      return depth;
    for(int i=0;i<m_children.length;i++)
      depth = Math.max(depth, m_children[i].getDepth(opNodesCount));
    return depth+1;
  }
  
  /**
   *  Returns the deep copied tree rooted by this node.
   */
  public Object clone(){
    RelNode clone = null;
      clone = (RelNode)super.clone();
    if(getChildren() != null){
      RelNode[] clonedChildren = new RelNode[clone.getChildren().length];
      for(int i=0; i<clonedChildren.length; i++)
        clonedChildren[i] = (RelNode)clone.getChildren()[i].clone();
      clone.setChildren(clonedChildren);
    }
    return clone;
  }
  
  /** Sets the relation, this node belongs to. */
  public void setRelation(Relation relation){
    m_relation = relation;
    if(m_children != null)
      for (int i = 0; i < m_children.length; i++)
        m_children[i].setRelation(relation);
  }
  
  /** Returns the relation, this node belongs to. */
  public Relation getRelation(){
    return m_relation;
  }
    
  /**
   *  Recursively sets the intervals for which this relation is true. For leaves 
   *  the set containing {@link #getIntervalsTrue} is returned.
   */
  public FiniteBorelSet getTrueFor(double epsilon, Interval interval){
    return new FiniteBorelSet(getIntervalsTrue(epsilon, interval));
  }

  /** 
   *  Recursively sets the intervals for which this relation is false. For leaves 
   *  the set containing {@link #getIntervalsTrue} is returned.
   */
  public FiniteBorelSet getFalseFor(double epsilon, Interval interval){
    return new FiniteBorelSet(getIntervalsFalse(epsilon, interval));
  }
  
  public abstract Interval[] getIntervalsTrue(double epsilon, Interval interval);
  
  public abstract Interval[] getIntervalsFalse(double epsilon, Interval interval);

  public abstract String toString(DecimalFormat format);
  
  /**
   *  Extracts <code>toBeExtracted</code> from <code>nodes</code> and returns
   *  the shortened array. Throws an exception if <code>toBeExtracted</code> is
   *  not contained in <code>nodes</code>.
   */
  public static RelNode[] extractNode(RelNode[] nodes, RelNode toBeExtracted) {
    RelNode[] newNodes = new RelNode[nodes.length - 1];
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
  public static RelNode[] insertNode(RelNode[] nodes, RelNode toBeInserted, boolean clone) {
    RelNode[] newNodes = new RelNode[nodes.length + 1];
    if (clone)
      for (int i = 0; i < nodes.length; i++)
        newNodes[i] = (RelNode) nodes[i].clone();
    else
      System.arraycopy(nodes, 0, newNodes, 0, nodes.length);
    newNodes[nodes.length] = toBeInserted;
    return newNodes;
  }

  /**
   * Inserts <code>toBeInserted</code> into the array <code>nodes</code> and returns it. 
   */
  public static RelNode[] insertNodes(RelNode[] nodes, RelNode[] toBeInserted) {
    RelNode[] newNodes = new RelNode[nodes.length + toBeInserted.length];
    System.arraycopy(nodes, 0, newNodes, 0, nodes.length);
    System.arraycopy(toBeInserted, 0, newNodes, nodes.length, toBeInserted.length);
    return newNodes;
  }    
}

