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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import net.mumie.mathletfactory.action.message.TodoException;
import net.mumie.mathletfactory.math.algebra.AbstractTreeNode;
import net.mumie.mathletfactory.math.algebra.op.OpTransform;
import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.algebra.op.PolynomialOpHolder;
import net.mumie.mathletfactory.math.algebra.op.RationalOpHolder;
import net.mumie.mathletfactory.math.algebra.rel.node.AllRel;
import net.mumie.mathletfactory.math.algebra.rel.node.AndRel;
import net.mumie.mathletfactory.math.algebra.rel.node.NotRel;
import net.mumie.mathletfactory.math.algebra.rel.node.RelNode;
import net.mumie.mathletfactory.math.number.MBigRational;
import net.mumie.mathletfactory.math.number.MComplex;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MInteger;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.MRational;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 *  This class is the base for all kind of calculations. Its subclasses
 *  represent the nodes in the Operational Tree constructed by
 *  {@link net.mumie.mathletfactory.math.algebra.op.OpParser}.
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */
public abstract class OpNode extends AbstractTreeNode implements Comparable {

  public final static int PRINT_SIGN          = 0x01;
  public final static int PRINT_FACTOR        = 0x02;
  public final static int PRINT_EXPONENT      = 0x04;
  public final static int PRINT_EXPONENT_SIGN = 0x08;
  public final static int PRINT_PARENTHESES   = 0x10;
  public final static int PRINT_EXPRESSION    = 0x20;

  public final static String REPLACEMENT_IDENTIFIER = "\ufffc";

  public final static int PRINT_ALL = PRINT_SIGN | PRINT_FACTOR | PRINT_EXPONENT
    | PRINT_EXPONENT_SIGN | PRINT_PARENTHESES | PRINT_EXPRESSION;

  /** A class for comparison in {@link #isOfLesserPrecedenceThan}. */
  public final static OpNode MULTOP = new MultOp(MDouble.class);

  /** A class for comparison in {@link #isOfLesserPrecedenceThan}. */
  public final static OpNode POWEROP = new PowerOp(MDouble.class);

  /** Needed for precedence checking. */
  private static final Hashtable precedence = new Hashtable();

  static {
    precedence.put(AddOp.class, new Integer(1));
    precedence.put(MultOp.class, new Integer(3));
    precedence.put(PowerOp.class, new Integer(5));
    precedence.put(NrtOp.class, new Integer(6));
  }

  /** function nodes all have the same precedence. */
  static int FUNCTION_PRECEDENCE = 4;

  /** The base value, which is calculated in {@link #calculate}. */
  protected MNumber m_base;

  /**
   *  A numerical factor is stored for each operation, to reduce the tree complexity
   *  and allow group actions.
   */
  protected MNumber m_factor;

  /**
   *  The exponent is stored for each operation, to reduce the tree complexity
   *  and allow group actions.
   */
  protected int m_exponent = 1;

  /**
   *  The children of the node, may be null (leaves), one node (e.g. functions) or
   *  an arbitrary number of nodes (e.g. multiplication).
   */
  protected OpNode[] m_children;

  /** The direct ancestor of this node in the Operation Tree. */
  protected OpNode m_parent;

  /**
   *  A flag indicating, that the children should be reordered after a
   *  possibly changing operation
   */
  private boolean m_reorderChildren = false;

  /** A register in which the list of VariableOps for a given identifier are stored. */
  List m_tmpVariableOps = new ArrayList();
  
  /** A flag indicating that an empty/unedited value has been read in from MathML. */
  protected boolean m_isEdited = true;

  /** Called by subclass. */
  protected OpNode(Class entryClass){
    super(entryClass);
    m_factor = NumberFactory.newInstance(getNumberClass(), 1);
    m_base = NumberFactory.newInstance(getNumberClass(), 1);
  }

  /** Called by subclass. */
  public OpNode(OpNode[] children){
    this(children[0].getNumberClass());
    setChildren(children);
  }

  /** Called by subclass. */
  public OpNode(OpNode child){
    this(child.getNumberClass());
    setChildren(new OpNode[]{child});
  }

  /** Called by subclass. */
  public OpNode(OpNode child1, OpNode child2){
    this(child1.getNumberClass());
    setChildren(new OpNode[]{child1, child2});
  }

  /** Set <code>parent</code> as the node's direct ancestor in the Operation Tree. */
  public void setParent(OpNode parent){
    m_parent = parent;
    if(m_parent instanceof MultOp || m_parent instanceof AddOp)
      m_parent.m_reorderChildren = true;
  }

  /**
   *  Returns the node's direct ancestor in the Operation Tree. For root nodes
   *  null is returned.
   */
  public OpNode getParent(){
    return m_parent;
  }

  /**
   *
   *  Defines an order of nodes, which is both needed for ordering (e.g. when
   *  checking for equality) and for output.
   *  This order also depends on the subclass (e.g. in multiplications
   *  numbers are in front of variables while sums are usually ordered by
   *  power).
   *
   *  @see java.lang.Comparable#compareTo(Object)
   */
  public int compareTo(Object shouldBeANode){
    OpNode aNode = (OpNode) shouldBeANode;

    // for products sort by node complexity, leaves are preponed
    if(m_parent != null && m_parent instanceof MultOp){
      if(!isLeaf() && aNode.isLeaf())
        return 1;
      if(isLeaf() && !aNode.isLeaf())
        return -1;
      if(this instanceof VariableOp && !(aNode instanceof VariableOp))
        return 1;
      if(!(this instanceof VariableOp) && aNode instanceof VariableOp)
        return -1;
      if(this instanceof VariableOp && aNode instanceof VariableOp){
        if(!((VariableOp)this).isVariable() && ((VariableOp)aNode).isVariable())
          return -1;
        if(((VariableOp)this).isVariable() && !((VariableOp)aNode).isVariable())
          return 1;
       /*
      // look for x, which is the variable that has the highest priority
      double comparison = getMaxPowerOfVariable("x");
      comparison -= aNode.getMaxPowerOfVariable("x");
      if(Math.abs(comparison) >= 0.5)
        return (int)comparison;
      */
      }
    }

    // special case for expressions with complex numbers
    if(getMaxAbsPowerOfVariable("i") != 0 && aNode.getMaxAbsPowerOfVariable("i") == 0)
      return 1;
    if(aNode.getMaxAbsPowerOfVariable("i") != 0 && getMaxAbsPowerOfVariable("i") == 0)
      return -1;

    /*
    // look for x, which is the variable that has the highest priority
    double comparison = aNode.getMaxPowerOfVariable("x");
    comparison -= getMaxPowerOfVariable("x");
    if(Math.abs(comparison) >= 0.5)
      return (int)comparison;
    */
    // sort by contained variables (alphabetically) and for equal variables by power
    String[] myVariables = getContainedVariables();
    String[] variables = aNode.getContainedVariables();

    if(myVariables.length > 0){
      if(variables.length > 0){
        double comparison = myVariables[0].compareTo(variables[0]);
        if(comparison == 0){ // both variables are contained
          comparison = aNode.getMaxPowerOfVariable(myVariables[0]);
          comparison -= getMaxPowerOfVariable(myVariables[0]);
        }
        if(comparison != 0)
          return (int)comparison;
      } else
        return -1;
    } else // this node contains no variables
      if(variables.length > 0)
        return 1;
    // sort by higher exponent
    if(getExponent() < aNode.getExponent())
      return 1;
    if(getExponent() > aNode.getExponent())
      return -1;
    // sort by length
    if(toString().length() < aNode.toString().length())
      return 1;
    if(toString().length() > aNode.toString().length())
      return -1;
    // sort by higher factor
    if(getFactor().getDouble() < aNode.getFactor().getDouble())
      return 1;
    if(getFactor().getDouble() > aNode.getFactor().getDouble())
      return -1;
    // no logical meaning, only for ensuring defined order
    if(toString().hashCode() > aNode.toString().hashCode())
      return 1;
    if(toString().hashCode() < aNode.toString().hashCode())
      return -1;
    return 0;
  }

  /**
   *  Returns the minimum number of arguments for this operation. Needed for
   *  consistency-checks.
   */
  public abstract int getMinChildrenNr();

  /**
   *  Returns the minimum number of arguments for this operation. Needed for
   *  consistency-checks.
   */
  public abstract int getMaxChildrenNr();

  /**
   *  A node equals this node, when it has the same class, sign and exponent
   *  and all their children are equal.
   */
  public boolean equals(OpNode aNode){
    return equalWithoutFactor(aNode) && m_factor.equals(aNode.m_factor);
  }

  /**
   *  A node equals this node, when it has the same class, exponent and all
   *  their children are equal. The factor is disregarded in this comparison.
   */
  public boolean equalWithoutFactor(OpNode aNode){
    return equalWithoutFactorAndExponent(aNode) && m_exponent == aNode.m_exponent;
  }

  public boolean equalWithoutFactorAndExponent(OpNode aNode){
    //System.out.println("comparing "+toString() +" and "+aNode.toString());
    if(!aNode.getClass().equals(getClass()))
      return false;
    if(isLeaf())
      return leafEquals(aNode);
    if(aNode.m_children == null || m_children.length != aNode.m_children.length)
      return false;
    boolean equality = true;
    for(int i=0; i<m_children.length;i++)
      equality = equality && m_children[i].equals(aNode.m_children[i]);
    return equality;
  }

  /**
   *  Returns true, if the two leaves (variables or constants) are equal.
   */
  public boolean leafEquals(OpNode aNode){
    // this should work both for VariableOps and NumberOps
    return nodeToString().equals(aNode.nodeToString());
  }


  /**
   *  Connects the Object to another OpNode and uses its output as input
   */
  public void setChildren(OpNode[] children) throws IllegalArgumentException{
    if(children.length < getMinChildrenNr())
      throw new IllegalArgumentException("Illegal Number of Arguments to "
                                           +this.getClass().getName()+"!\nneeds at least "
                                           +getMinChildrenNr()+" - got "+ children.length);
    for(int i=0;i<children.length;i++)
      children[i].m_parent = this;
    // for MultOp and AddOp sort children by their string representation (-> normal form!)
    if(this instanceof AddOp || this instanceof MultOp)
      Arrays.sort(children);
    m_children = children;
  }

  /** Returns the array of child-nodes. */
  public OpNode[] getChildren(){
    if(m_reorderChildren && m_children != null){
      Arrays.sort(m_children);
      m_reorderChildren = false;
    }
    return m_children;
  }

  /**
   * Adds this node to <code>leavesList</code> if it is a leaves
   * or call this method in children otherwise.
   * @param leavesList The (by reference passed) list in which all leaves are stored.
   */
  public void getLeaves(List leavesList){
    if(isLeaf())
      leavesList.add(this);
    else
      for(int i=0;i<m_children.length;i++)
        m_children[i].getLeaves(leavesList);
  }

  /**
   * Returns the first child in the list of children, that is of type
   * <code>nodeType</code>.
   */
  public OpNode getFirstChildOfType(Class nodeType, int startIndex){
    if(m_children == null)
      return null;
    for(int i=startIndex; i<m_children.length;i++)
      if(nodeType.isInstance(m_children[i]))
        return m_children[i];
    return null;
  }

  /**
   * Returns the first descendant in depth-order, that is of type
   * <code>nodeType</code>.
   */
  public OpNode getFirstDescendantOfType(Class nodeType){
    if(m_children == null)
      return null;
    for(int i=0; i<m_children.length;i++)
      if(nodeType.isInstance(m_children[i]))
        return m_children[i];
      else{
        OpNode maybeDescendant = m_children[i].getFirstDescendantOfType(nodeType);
        if(maybeDescendant != null)
          return maybeDescendant;
      }
    return null;
  }


  /** Returns a list of all nodes of the given type in the subtree represented by this node. */
  public List getAllNodesOfType(Class nodeType){
    List nodes = null;
    if(m_children != null)
      for(int i=0;i<m_children.length;i++){
        if(nodes != null){
          List childrenList = m_children[i].getAllNodesOfType(nodeType);
          if(childrenList != null)
            nodes.addAll(childrenList);
        }
        else
          nodes = m_children[i].getAllNodesOfType(nodeType);
      }
    if(getClass().isAssignableFrom(nodeType))
      if(nodes != null)
        nodes.add(this);
      else {
        nodes = new LinkedList();
        nodes.add(this);
      }
    return nodes;
  }

  /**
   *  Replaces all descendants of this node containing a variable of
   *  <code>identifier</code> with a clone of the given node.
   */
  public boolean replace(String identifier, OpNode withNode){
  	if(m_children == null)
  		return false;
  	boolean didReplace = false;
  	for(int i = 0; i < m_children.length; i++) {
  		if(m_children[i] instanceof VariableOp &&
         ((VariableOp)m_children[i]).getIdentifier().equals(identifier)){
  			OpNode old = m_children[i];
  			m_children[i] = (OpNode)withNode.clone();
  			OpNode.transferFactorAndExponent(old, m_children[i], false);
  			didReplace = true;
  		} else
  			didReplace = m_children[i].replace(identifier, withNode);
  	}
  	setChildren(m_children); // reorder
  	return didReplace;
  }

  /**
   *  Replaces all descendants of this node containing a {@link #REPLACEMENT_IDENTIFIER} with a
   *  clone of the given node.
   */
  public boolean replace(OpNode node){
    return replace(REPLACEMENT_IDENTIFIER, node);
  }

  /**
   * Returns a new node with the specified replacement done.
   * @see #replace(OpNode)
   */
  public OpNode replaced(OpNode node){
    OpNode newNode = (OpNode)clone();
    newNode.replace(node);
    return newNode;
  }

  /**
   *  Implements <i>(m*a(x)^n)' = (n*a(x) ^ (n-1) * m*a'(x)</i>.
   *  @param derivedNode a'(x)
   */
  protected OpNode deriveNode(OpNode derivedNode){
    if(m_exponent == 1){
      derivedNode.setFactor(m_factor.copy());
      return derivedNode;
    }
    MultOp derivedPower = new MultOp(getNumberClass());
    OpNode newPower = (OpNode)clone();
    newPower.m_factor.mult(NumberFactory.newInstance(getNumberClass(), m_exponent));
    newPower.m_exponent--;
    derivedPower.setChildren(new OpNode[]{newPower, derivedNode});
    return derivedPower;
  }

  /**
   *  Inverts the algebraic operation represented by this node with respect to
   *  the children containing the given identifier. It returns null if
   *  <code>this</code> is a NumberOp or a VariableOp, or this node is not
   *  invertible. Otherwise it returns the operations to apply to this node in
   *  order to perform a solving step towards the separation of terms containing
   *  <code>identifier</code>.<br>
   *  This method is called, when a simple relation containing this node as left
   *  hand side should be transformed, so that <code>identifier</code> or an
   *  irreducible expression of <code>identifier</code> remains on the left hand side.
   * */
  public abstract OpNode[] solveStepFor(String identifier);

  /**
   * Does the same as {@link #solveStepFor}, but returns only one node for non-equivalence
   * transformations. The default implementation returns the first return value of
   * {@link #solveStepFor}.
   */
  public OpNode simpleSolveStepFor(String identifier){
    OpNode[] retVal = solveStepFor(identifier);
    if(retVal == null || retVal.length == 0)
      return null;
    return retVal[0];
  }

  /**
   *  Returns true if this node cannot be reduced by {@link #solveStepFor(String)}, false
   *  otherwise.
   */
  public boolean isIrreducibleFor(String identifier){
    if(isLeaf())
      return true;
    if((this instanceof AddOp || this instanceof MultOp || this instanceof PowerOp) && getChildrenWithVariableCount(identifier) == m_children.length)
      return true;
    return false;
  }

  /**
   * Returns the relation that must be satisfied, so that this node
   * with <code>operand</code> as child represents a monotonously
   * rising operation.
   */
  public abstract RelNode getMonotonicIncreasingRel(OpNode operand);

  /**
   * Returns the relation that must be satisfied, so that this node
   * with <code>operand</code> as child represents a monotonously
   * rising operation.
   * @see #getMonotonicIncreasingRel(OpNode operand)
   */
  public RelNode getMonotonicIncreasingRel(){
    RelNode result = new AllRel(getNumberClass());
    if(m_children != null)
      for(int i=0;i<m_children.length;i++)
        result = new AndRel(getMonotonicIncreasingRel(m_children[i]), result);
    return result;
  }

  /**
   * Returns the relation that must be satisfied, so that this node
   * with <code>operand</code> as child represents a monotonously
   * falling operation
   */
  public abstract RelNode getMonotonicDecreasingRel(OpNode operand);

  /**
   * Returns the relation that must be satisfied, so that this node
   * represents a monotonously falling operation
   * @see #getMonotonicDecreasingRel(OpNode operand)
   */
  public RelNode getMonotonicDecreasingRel(){
    RelNode result = new AllRel(getNumberClass());
    if(m_children != null)
      for(int i=0;i<m_children.length;i++)
        result = new AndRel(getMonotonicDecreasingRel(m_children[i]), result);
    return result;
  }

  /**
   * Returns the relation subtree for which this operation with
   * <code>operand</code> as child is defined. It does not consider the
   * exponent of this node, which is be checked in {@link #getDefinedRel}.
   */
  public abstract RelNode getNodeDefinedRel(OpNode operand);

  /**
   * Returns the relation that must be satisfied, that all the children of
   * this operation are defined. This method should be only called, if the
   * node has children. Otherwise it throws a <code>NullPointerException</code>.
   */
  protected RelNode getChildrenDefinedRel(){
    RelNode[] childrenRel = new RelNode[m_children.length];
    for(int i=0;i<m_children.length;i++){
      childrenRel[i] = m_children[i].getDefinedRel();
    }
    if(childrenRel.length == 1){
      return childrenRel[0];
    }else
      return new AndRel(childrenRel);
  }


  /**
   * Returns the relation for which the operation represented by this node is defined.
   * @see #getNodeDefinedRel(OpNode operand)
   */
  public RelNode getDefinedRel(){

    // by default the operation is defined totally for any variable
    RelNode definedRel = new AllRel(getNumberClass());

    // retrieve the definition range of this node with the child as argument
    // (non unary operations overload this method)
    if(m_children != null)
      definedRel = getNodeDefinedRel(m_children[0]);

    // for nodes with a negative exponent the base may not become zero
    if(getExponent() < 0 && !(this instanceof NumberOp)                                                                                                                                                                                                                                                     ){
      OpNode nodeWithoutFactorAndExponent = (OpNode)clone();
      nodeWithoutFactorAndExponent.setExponent(1);
      nodeWithoutFactorAndExponent.setFactor(1);
      definedRel = new AndRel(definedRel, new NotRel(nodeWithoutFactorAndExponent.getZeroRel()));
    }

    // intersect the defintion range of this node with the definion range of the children
    if(m_children == null)
      return definedRel;
    else
      return new AndRel(definedRel, getChildrenDefinedRel());
  }

  /**
   * Returns the relation for which this operation with <code>operand</code> as
   * child is zero.
   */
  public abstract RelNode getZeroRel(OpNode operand);


  /**
   * Returns the relation for which this operation is zero. This default
   * implementation works for nodes with one child.
   * @see #getZeroRel(OpNode operand)
   */
  public RelNode getZeroRel(){
    if(m_children == null || m_children.length > 1)
      throw new IllegalStateException("should only be called for nodes with one child! not "+getClass()+": "+toString());
   return getZeroRel(m_children[0]);
  }

  /**
   *  Does the real calculation for MMNumbers by recursively calling its
   *  children <code>calculate</code> and applying the operation of this node
   *  onto it.
   */
  protected abstract void calculate();

  /**
   *  Does the real calculation for doubles by recursively calling its
   *  children <code>calculateDouble</code> and applying the operation of this
   *  node onto it.
   */
  protected abstract void calculateDouble();

  /**
   *  Returns the derivative with respect to <code>variable</code>
   *  of the subtree of this node.
   */
  public abstract OpNode getDerivative(String variable);

  /**
   *  Returns the result calculated by {@link #calculate}.
   */
  public MNumber getResult(){
    calculate();
    if(m_exponent != 1)
      m_base = m_base.power(m_exponent);
    m_base.mult(m_factor);
    return m_base;
  }

  /** Returns the result calculated by {@link #calculate} modified by print Flags. */
  public MNumber getResult(int printFlags){
    MNumber result = m_base.copy();
    if(m_exponent != 1 && (printFlags & PRINT_EXPONENT) != 0)
      if((printFlags & PRINT_EXPONENT_SIGN) != 0)
        result = result.power(m_exponent);
      else
        result = result.power(Math.abs(m_exponent));
    if((printFlags & PRINT_FACTOR) != 0)
      if((printFlags & PRINT_SIGN) != 0)
        result.mult(m_factor);
      else
        result.mult(m_factor.absed());
    return result;
  }

  /**
   * Returns the result calculated by  {@link #calculateDouble}.
   */
  public double getResultDouble(){
    calculateDouble();
    double result= m_base.getDouble();
    if(m_exponent != 1)
      result = Math.pow(result, m_exponent);
    return result * m_factor.getDouble();
  }

  /**
   *  Returns true if <code>this</code> is a NumberOp and it is the neutral
   *  element for the given operation.
   */
  public final boolean isNeutralElementFor(OpNode operation){
    if(!(this instanceof NumberOp))
      return false;
    return (operation instanceof MultOp && Math.abs(getResult().getDouble()) == 1) ||
      (operation instanceof AddOp && getResult().isZero());
  }

  /**
   *  Returns true for non-unary operations which are commutative, false
   *  otherwise. The default implementation returns <code>false</code>.
   */
  public boolean isCommutativeOperation(){
    return false;
  }

  /**
   *  Returns true if the subtree rooted by this node is a pure rational
   *  expression over the number class i.e. its non-leaves are exclusively
   *  MultOps, AddOps and PowerOps with integers as exponents.
   */
  public boolean isRational(){
    return isRationalOrPolynomial(false);
  }

  /**
   *  Returns true if the subtree rooted by this node is a polynomial
   *  over the number class i.e. its non-leaves are exclusively  MultOps,
   *  AddOps and PowerOps with positive integers as exponents and the
   *  exponents of all nodes are greater than zero.
   */
  public boolean isPolynomial(){
    return isRationalOrPolynomial(true);
  }

  /**
   * Returns true if this node is a polynomial that contains no higher powers
   * than 1.
   */
  public boolean isAffine(){
    if(!isPolynomial())
      return false;
    OpNode expandedNormalizedNode = OpTransform.normalize(OpTransform.expand(this));
    String[] variables = new Operation(expandedNormalizedNode).getUsedVariables();
    for(int i=0;i<variables.length;i++)
      if(expandedNormalizedNode.getMaxAbsPowerOfVariable(variables[i])>1)
        return false;
    return true;
  }

  /**
   *  Implementation for  {@link #isRational} and {@link #isPolynomial}.
   *  Since polynomial conditions extend rational conditions with a positive
   *  exponent for each node, the <code>polynomialFlag</code> signals the
   *  additional test for positive exponents.
   */
  private boolean isRationalOrPolynomial(boolean polynomialFlag){

    if(isLeaf() && (polynomialFlag ? getExponent() > 0 : true))
      return true; // by definition

    // check node
    boolean thisIsRationalOrPolynomial = this instanceof MultOp || this instanceof AddOp ||
    (this instanceof PowerOp && m_children[1] instanceof NumberOp
       && m_children[1].getResult().isInteger()
       && (polynomialFlag ? m_children[1].getResult().getDouble() > 0 : true));
    if(polynomialFlag)
      thisIsRationalOrPolynomial &= getExponent() > 0;

    if(!thisIsRationalOrPolynomial)
      return false;

    // recursively check children
    boolean childrenAreRationalOrPolynomial = true;
    for(int i=0;i<m_children.length;i++)
      childrenAreRationalOrPolynomial &= m_children[i].isRationalOrPolynomial(polynomialFlag);

    return thisIsRationalOrPolynomial && childrenAreRationalOrPolynomial;
  }

  /**
   *  Returns the functionality for operations on rational expressions or
   *  null if the expression rooted by this node is not rational.
   */
  public RationalOpHolder getRationalHolder(){
    if(!isRational())
      return null;
    return new RationalOpHolder(this);
  }

  /**
   *  Returns the functionality for operations on polynomial expressions or
   *  null if the expression rooted by this node is not polynomial.
   */
  public PolynomialOpHolder getPolynomialHolder(){
    if(!isPolynomial())
      return null;
    return new PolynomialOpHolder(this);
  }

  /**
   *  Returns the number of children, that contain variables. Constants
   *  do not count as variables.
   *  @see #getChildrenWithVariablesCount(boolean constantsCount)
   */
  public int getChildrenWithVariablesCount(){
    return getChildrenWithVariablesCount(false);
  }


  /**
   *  Returns the number of children, that contain variables. If the
   *  flag <code>constantsCount</code> is set to true, constants and parameters also
   *  count as variables.
   */
  public int getChildrenWithVariablesCount(boolean constantsCount){

    // check for leaves
    if(this instanceof MultipleOfZNode)
      return 1;
    if(this instanceof VariableOp)
      return  !constantsCount && (!((VariableOp)this).isVariable()) ? 0 : 1;
    if(isLeaf())
      return 0;
    int childrenWithVariables = 0;
    for(int i=0; i<m_children.length; i++)
      if(m_children[i].getChildrenWithVariablesCount(constantsCount) > 0)
        childrenWithVariables++;
    return childrenWithVariables;
  }


  /**
   *  Returns the number of children that contain any VariableOp with the name
   *  <code>identifier</code>.
   */
  public int getChildrenWithVariableCount(String identifier){
    // check for leaves
    if(this instanceof VariableOp)
      if(((VariableOp)this).getIdentifier().equals(identifier))
        return 1;
      else
        return 0;
    if(isLeaf())
      return 0;
    int childrenWithVariable = 0;
    for(int i=0; i<m_children.length; i++)
      if(m_children[i].getChildrenWithVariableCount(identifier) > 0)
        childrenWithVariable++;
    return childrenWithVariable;
  }

  /**
   *  Returns the identifiers of the variables that this node contains as an
   *  alphabetically sorted array.
   */
  public String[] getContainedVariables(){
    List variablesList = containedVariables();
    String[] stringArray = new String[variablesList.size()];
    String[] variables = (String[])containedVariables().toArray(stringArray);
    Arrays.sort(variables);
    return variables;
  }
  
  public String[] getContainedConstants() {
	  List constantList = containedConstants();
	  String[] stringArray = new String[constantList.size()];
	  String[] constants = (String[])constantList.toArray(stringArray);
	  Arrays.sort(constants);
	  return constants;
  }
  
  protected List containedConstants() {
	  List constants = new ArrayList();
	  
	  if(this instanceof VariableOp && ((VariableOp)this).isConstant()
	      && !((VariableOp)this).getIdentifier().equals(OpNode.REPLACEMENT_IDENTIFIER))
	      constants.add(((VariableOp)this).getIdentifier());
	    if(m_children != null)
	      for(int i=0;i<m_children.length;i++)
	        constants.addAll(m_children[i].containedConstants());
	    return constants;
  }

  /**
   *  Returns the identifiers of the variables that this node contains as a
   *  list.
   */
  protected List containedVariables(){
    m_tmpVariableOps.clear();
    if(this instanceof VariableOp && ((VariableOp)this).isVariable()
      && !((VariableOp)this).getIdentifier().equals(OpNode.REPLACEMENT_IDENTIFIER))
      m_tmpVariableOps.add(((VariableOp)this).getIdentifier());
    if(m_children != null)
      for(int i=0;i<m_children.length;i++)
        m_tmpVariableOps.addAll(m_children[i].containedVariables());
    return m_tmpVariableOps;
  }

  /**
   * Returns the maximal absolute exponent of a variable of the given identifier
   * that occurs in the subtree rooted by this node.
   */
  public int getMaxAbsPowerOfVariable(String identifier){
    // check for leaves
    if(this instanceof VariableOp)
      if(((VariableOp)this).getIdentifier().equals(identifier))
        return Math.abs(m_exponent);
      else
        return 0;
    
    if(m_children == null)
      return 0;

    if(this instanceof PowerOp)
    	if(m_children[0].getMaxAbsPowerOfVariable(identifier) != 0)
    		return (int) Math.abs(m_children[1].getResult().getDouble());

    int maxExponent = 0;
    for(int i=0; i<m_children.length; i++)
      maxExponent = Math.max(maxExponent, m_children[i].getMaxAbsPowerOfVariable(identifier));
    return maxExponent;
  }

  /**
   * Returns the maximal exponent of a variable of the given identifier
   * that occurs in the subtree rooted by this node. Note that this method
   * returns 0 for any expression containing only negative powers of identifier
   * plus at least one constant.
   */
  public int getMaxPowerOfVariable(String identifier){
    // check for leaves
    if(this instanceof VariableOp)
      if(((VariableOp)this).getIdentifier().equals(identifier))
        return m_exponent;
      else
        return 0;
    if(m_children == null)
      return 0;
    if(this instanceof PowerOp)
    	if(m_children[0].getMaxAbsPowerOfVariable(identifier) != 0)
    		return (int) Math.abs(m_children[1].getResult().getDouble());
        
    int maxExponent = Integer.MIN_VALUE;
    for(int i=0; i<m_children.length; i++)
      maxExponent = Math.max(maxExponent, m_children[i].getMaxAbsPowerOfVariable(identifier));
    return maxExponent;
  }

  /**
   *  Invert the sign of this node. Needed, when a subtraction is stored inside
   *  an addition node.
   */
  public OpNode negateFactor(){
	  m_factor.negate();
    return this;
  }


  /**
   *  Negates the internal stored exponent of this node. Needed, when a division
   *  is stored inside a multiplication node.
   */
  public OpNode negateExponent(){
    m_exponent = -m_exponent;
    return this;
  }

  /** Multiplies the internal stored exponent of this node with <code>exponent</code>. */
  public OpNode multiplyExponent(int exponent){
    m_exponent *= exponent;
    return this;
  }

  /** Sets the internal stored exponent of this node. */
  public void setExponent(int exponent){
    m_exponent = exponent;
  }

  /** Returns the internal stored exponent of this node. */
  public int getExponent(){
    return m_exponent;
  }

  /** Sets the internal stored factor of this node. */
  public void setFactor(MNumber factor){
    m_factor = factor;
  }

  /** Sets the internal stored factor of this node. */
  public void setFactor(double factor){
    m_factor.setDouble(factor);
  }

  /**
   * Multiplies the internal stored factor of this node with <code>factor</code>.
   */
  public void multiplyFactor(MNumber factor){
    m_factor.mult(factor);
  }

  /**
   *  Returns the internal stored factor of this node.
   */
  public MNumber getFactor(){
    return m_factor.copy();
  }

  /** Returns the sign of the factor of this node. */
  public int getSign(){
    return m_factor.getSign();
  }

  /**
   *  Returns true, if this node is of lesser mathematical precedence than
   *  the given node (e.g. '+' is of lesser precedence than '*').
   */
  protected boolean isOfLesserPrecedenceThan(OpNode node){
  int myPrecedence;
  if(this instanceof FunctionOpNode)
    myPrecedence = FUNCTION_PRECEDENCE;
  else {
    Integer myPrecedenceObj = (Integer)precedence.get(getClass());
    // not defined (e.g. numbers, variables) means always least precedence
    if(myPrecedenceObj == null)
    return true;
    else
    myPrecedence = myPrecedenceObj.intValue();
  }
  int nodePrecedence;
  if(node instanceof FunctionOpNode)
    nodePrecedence = FUNCTION_PRECEDENCE;
  else{
    Integer nodePrecedenceObj = (Integer)precedence.get(node.getClass());
    if(nodePrecedenceObj == null)
    return false;
    else
    nodePrecedence = nodePrecedenceObj.intValue();
  }
  return myPrecedence < nodePrecedence;
  }

  /**
   *  Returns the string representation with or without sign, depending on the
   *  value of <code>printFlags</code>.
   */
  public abstract String toString(int printFlags, DecimalFormat format);

  public String toString(){
    return toString(PRINT_ALL, null);
  }

  /** Returns the string representation of this node with numbers respecting the given decimal format. */
  public String toString(DecimalFormat format){
    return toString(PRINT_ALL, format);
  }

  /**
   *  Wraps the value of {#nodeToContentMathML} in a content MathML expression
   *  considering the factor and exponent.
   */
  public String toContentMathML(){

    String retVal="";
    if(m_exponent != 1)
      retVal = "<apply><power/>"+nodeToContentMathML()+"<cn>"+m_exponent+"</cn></apply>";
    else
      retVal =  nodeToContentMathML();
    if(m_factor.getDouble() != 1)
      retVal = "<apply><times/>"+m_factor.toContentMathML()+retVal+"</apply>";

    return retVal;
  }

  /**
   *  Returns the value of the expression represented by this node
   *  (without factor and exponent) to content MathML.
   *  @see #toContentMathML
   */
  protected abstract String nodeToContentMathML();

  /**
   *  Returns the OpML-representation of the expression represented by this
   *  node. Needed for XSLT-Transformations of Operation Tree.
   */
  public String toOpML(){
    if(this instanceof NumberOp)
      return "<n>"+toString()+"</n>";
    String factorAndExpAttributes = ""+(m_factor.getDouble() != 1 ? " a=\"" + m_factor.getDouble() + "\"" : "")
      +(m_exponent != 1 ? " n=\"" + m_exponent + "\"" : "");
    if(this instanceof VariableOp)
      return "<var id=\""+nodeToString()+"\""+factorAndExpAttributes+"/>";
    String retVal = "<"+nodeToOpML() +factorAndExpAttributes+">";
    for(int i=0;i<m_children.length;i++)
      retVal += m_children[i].toOpML();
    retVal += "</"+nodeToOpML()+">";
    return retVal;
  }

  /**
   *  The default implementation simply returns {@link #toString()}. Some
   *  subclasses (e.g. AddOp) overwrite this method for a xml-conform
   *  implementation.
   */
  protected String nodeToOpML(){
    return nodeToString();
  }

  /**
   *  This method prints out the factor and exponent of this node. Also, the
   *  need for parentheses is checked.
   */
  protected String printFactorAndExponent(String value, int printFlags, DecimalFormat format){
  // print factor
  String factor = getFactorString(printFlags, format);

  // print exponent
  String exponentString = getExponentString(printFlags);

  if(!exponentString.equals("")){
    // non-leaf nodes of lesser precedence than "^" need parentheses
    if(m_children != null && isOfLesserPrecedenceThan(POWEROP))
    return  factor+"("+value+")^"+exponentString;
    else // leaves need no parentheses
    return factor+value+"^"+exponentString;
  }
  if(m_children == null) // leaves need no parentheses
    return factor+value;
  if(parenthesesNeeded())
    return factor+"("+value+")";
  return factor+value;
  }

  /**
   *  Returns the factor as String. Needed by {@link #toString()} and views of
   *  this node.
   */
  public String getFactorString(int printFlags, DecimalFormat format){
  if((printFlags & (PRINT_FACTOR | PRINT_SIGN)) == 0)
    return "";
  String factorString =  m_factor.toString(format);
  if(factorString.equals("1.0") || factorString.equals("1"))
    return "";
  if(factorString.equals("-1.0") || factorString.equals("-1"))
    return (printFlags & PRINT_SIGN) != 0 ? "-" : "";

  if(m_factor instanceof MComplex && ((MComplex)m_factor).getIm() != 0){
    return "("+factorString+")";
  }
  return // print sign if needed
    (m_factor.getDouble() < 0  && (printFlags & PRINT_SIGN) != 0 ? "-" : "")
    // print factor if != 1 and PRINT_FACTOR set
    +(Math.abs(m_factor.getDouble()) != 1  && (printFlags & PRINT_FACTOR) != 0 ?
      m_factor.absed().toString(format)
      // print "*" if needed
      +(!(this instanceof VariableOp || this instanceof MultOp) ?  "*" : "") : "");
  }

  /**
   *  Returns the exponent as a String. Needed by {@link #toString()} and views of
   *  this node.
   */
  public String getExponentString(int printFlags){
  if(m_exponent == 1 || ((printFlags & PRINT_EXPONENT_SIGN) == 0 && m_exponent == -1))
    return "";
  return ""+((printFlags & PRINT_EXPONENT) == 0 ? "" : ""+
         ((printFlags & PRINT_EXPONENT_SIGN) != 0 ? m_exponent
          : Math.abs(m_exponent)));
  }

  /**
   *  Returns true, if this expression needs parentheses, false otherwise.
   */
  public boolean parenthesesNeeded(){
  // leaves  need no parentheses
  if(isLeaf())
    return false;

  // abs and floor nodes with factor 1 need no parentheses
  if((this instanceof AbsOp || this instanceof FloorOp) && getFactor().getDouble() == 1)
  	return false;

  // non-leaf nodes with exponent != 1 and a precendece lesser than "^" need parentheses
  if(m_exponent != 1 &&  (isOfLesserPrecedenceThan(POWEROP) || this instanceof NrtOp))
    return true;

  // nodes that have a factor != 1 and are of lesser precedence than MultOp need parentheses
  if(!getFactorString(PRINT_ALL, null).equals("") && isOfLesserPrecedenceThan(MULTOP))
    return true;

  // nodes which are root nodes and for which the rules above do not apply
  // need no parentheses
  if(m_parent == null)
    return false;

  // children of factorials with a factor need parentheses
  if(m_parent instanceof FacOp && getFactor().getDouble() !=1)
  	return true;

  // children in an nth root or function need no parentheses
  if(m_parent instanceof NrtOp || (m_parent instanceof FunctionOpNode && !(m_parent instanceof ConjOp || m_parent instanceof FacOp)))
    return false;

  // nodes, whose parent is of greater precedence (e.g. a '+' being child of
  // a '*') need parentheses
  if(isOfLesserPrecedenceThan(m_parent))
    return true;

  // otherwise no parentheses are needed
  return false;
  }

  /**
   *  Returns the string representation for this node (without children).
   */
  public abstract String nodeToString();

  /**
   *  Returns a string representing the arithmetic operation as tree.
   */
  public String toDebugString(){
  String retVal = "";
  String factor = (m_factor.getDouble() < 0 ? "-" : "")+(Math.abs(m_factor.getDouble()) != 1 ? m_factor.absed().toString()+"*" : "");
  if(m_children != null){
    for(int i=0;i<m_children.length;i++)
    retVal += " "+m_children[i].toDebugString();
    return factor+nodeToString()+"("+retVal+" )"+(m_exponent != 1 ?  "^"+m_exponent : "");
  }
  return factor+nodeToString()+(m_exponent != 1 ?  "^"+m_exponent : "");
  }



  /**
   *  Returns the deep copied tree rooted by this node.
   */
  public Object clone(){
    OpNode clone = null;
    clone = (OpNode)super.clone();
    clone.setExponent(getExponent());
    clone.setFactor(getFactor());
    if(getChildren() != null){
      OpNode[] clonedChildren = new OpNode[clone.getChildren().length];
      for(int i=0; i<clonedChildren.length; i++)
        clonedChildren[i] = (OpNode)clone.getChildren()[i].clone();
      clone.setChildren(clonedChildren);
    }
    return clone;
  }

  public OpNode deepCopy(boolean withoutFactor, boolean withoutExponent){
    OpNode result = (OpNode)clone();
    if(withoutExponent)
      result.setExponent(1);
    if(withoutFactor)
      result.setFactor(1);
    return result;
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

  public Node getMathMLNode(Document doc) {
    throw new TodoException("Not yet implemented for node type " + getClass());
//		return null;
	}
  
  /**
   * Returns true if an empty/unedited values has been read in from MathML.
   */
  public boolean isEdited() {
  	return m_isEdited;
  }

  protected boolean needsParanthesises() {
    boolean forNr = false;
    if(this instanceof NumberOp) {
      MNumber n = getResult();
      if(n.isReal()) forNr = n.getDouble() < 0;
      forNr = forNr || !n.isReal();
    }
    return this instanceof AddOp
    || this instanceof MultOp
    || forNr;
  }
  
  /**
   * Writes the XML nodes for the factor and exponent (where needed) to <code>xmlNode</code>
   * and returns the root of the complete XML node.   
   */
  protected Node writeFactorAndExponent(OpNode opNode, Node xmlNode) {
		boolean usesParantheses = (getFactor().getDouble() < 0
				|| needsParanthesises()
				|| (this instanceof NumberOp && ((getNumberClass().isAssignableFrom(MRational.class))
				|| (getNumberClass().isAssignableFrom(MBigRational.class)))));

		Document doc = xmlNode.getOwnerDocument();
		DocumentFragment fragment = doc.createDocumentFragment();

		if (opNode.getFactor().getDouble() == 0) {
			fragment.appendChild(NumberFactory.newInstance(getNumberClass()).setDouble(0).getMathMLNode(doc));
			return fragment;
		}

		Node result = doc.createDocumentFragment();
		Node node;

		if (getExponent() == -1) {
			node = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mfrac");
			node.appendChild(getFactor().getMathMLNode(doc));
			node.appendChild(xmlNode);
		} else if (getExponent() == 1) {
			if (getFactor().getDouble() == 1.0) {
				fragment.appendChild(xmlNode);
				return fragment;
			} else if (getFactor().getDouble() == -1.0) {
				node = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mrow");
				Element mi = doc.createElementNS(XMLUtils.MATHML_NAMESPACE,	"mo");
				mi.appendChild(doc.createTextNode("-"));
				node.appendChild(mi);
				node.appendChild(xmlNode);
				fragment.appendChild(node);
				return fragment;
			} else {
				node = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mrow");
				node.appendChild(getFactor().getMathMLNode(doc));
				node.appendChild(xmlNode);
			}
		} else if (getExponent() == 0) {
			node = getFactor().getMathMLNode(doc);
		} else {
			Element msup = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "msup");
			if (usesParantheses) {
				node = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mrow");
				Element mo = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mo");
				mo.appendChild(doc.createTextNode("("));
				node.appendChild(mo);
				node.appendChild(xmlNode);
				mo = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mo");
				mo.appendChild(doc.createTextNode(")"));
				node.appendChild(mo);
				xmlNode = node;
			}
			msup.appendChild(xmlNode);
			if (getExponent() < -1) {
				node = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mfrac");
				node.appendChild(getFactor().getMathMLNode(doc));
				msup.appendChild(new MInteger(-1 * getExponent()).getMathMLNode(doc));
				node.appendChild(msup);
			} else {
				msup.appendChild(new MInteger(getExponent()).getMathMLNode(doc));
				if (getFactor().getDouble() == 1.0) {
					node = msup;
				} else if (getFactor().getDouble() == -1.0) {
					node = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mrow");
					Element mi = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mo");
					mi.appendChild(doc.createTextNode("-"));
					node.appendChild(mi);
					node.appendChild(msup);
				} else {
					node = doc.createElementNS(XMLUtils.MATHML_NAMESPACE,"mrow");
					node.appendChild(getFactor().getMathMLNode(doc));
					node.appendChild(msup);
				}
			}
		}
		result.appendChild(node);
		return result;
	}

  /**
   *  Raises the factor of <code>to</code> to power of
   *  <code>from.getExponent()</code> then multiplies it with the factor from
   *  <code>from</code> and multiplies the exponent of <code>to</code> with
   *  <code>from.getExponent()</code>.
   */
  public static void transferFactorAndExponent(OpNode from, OpNode to, boolean deleteInNode) {
    to.setFactor(to.getFactor().power(from.getExponent()));
    to.multiplyFactor(from.getFactor());
    to.multiplyExponent(from.getExponent());
    if (deleteInNode) {
      from.setFactor(from.getFactor().setDouble(1));
      from.setExponent(1);
    }
  }

  /**
   *  Replaces <code>node</code> with <code>with</code> by transferring the factor and
   *  exponent of <code>node</code> onto <code>with</code>.
   *
   *  @return OpNode the changed node same as <code>with</code>.
   */
  public static OpNode replaceWith(OpNode node, OpNode with) {
    transferFactorAndExponent(node, with, true);
    with.setParent(node.getParent());
    return with;
  }

  /**
   *  Extracts <code>toBeExtracted</code> from <code>nodes</code> and returns
   *  the shortened array. Throws an exception if <code>toBeExtracted</code> is
   *  not contained in <code>nodes</code>.
   */
  public static OpNode[] extractNode(OpNode[] nodes, OpNode toBeExtracted) {
    OpNode[] newNodes = new OpNode[nodes.length - 1];
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
  public static OpNode[] insertNode(OpNode[] nodes, OpNode toBeInserted, boolean clone) {
    OpNode[] newNodes = new OpNode[nodes.length + 1];
    if (clone)
      for (int i = 0; i < nodes.length; i++)
        newNodes[i] = (OpNode) nodes[i].clone();
    else
      System.arraycopy(nodes, 0, newNodes, 0, nodes.length);
    newNodes[nodes.length] = toBeInserted;
    return newNodes;
  }

  /**
   * Inserts <code>toBeInserted</code> into the array <code>nodes</code> and returns it.
   */
  public static OpNode[] insertNodes(OpNode[] nodes, OpNode[] toBeInserted) {
    OpNode[] newNodes = new OpNode[nodes.length + toBeInserted.length];
    System.arraycopy(nodes, 0, newNodes, 0, nodes.length);
    System.arraycopy(toBeInserted, 0, newNodes, nodes.length, toBeInserted.length);
    return newNodes;
  }
}


