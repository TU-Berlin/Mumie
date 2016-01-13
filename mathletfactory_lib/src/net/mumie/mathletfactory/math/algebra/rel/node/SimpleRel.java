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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.mumie.mathletfactory.math.algebra.op.OpParser;
import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.algebra.op.node.MultipleOfZNode;
import net.mumie.mathletfactory.math.algebra.op.node.NumberOp;
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.math.algebra.rel.Relation;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.set.FiniteBorelSet;
import net.mumie.mathletfactory.math.set.Interval;

/**
 *  This class represents an arbitrary "simple" relation (a relation that contains
 *  no logical connective like AND, OR, NOT) based on a dedicated number class. 
 *  Basically it consists of three pieces of data, two
 *  {@link net.mumie.mathletfactory.algebra.op.Operation}s for the left hand and
 *  right hand side and a {@link #m_relationSign relation sign}, which is
 *  represented by a string and denotes the type of this relation 
 *  ("=", ">", ">=", "!=", etc.).
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */

public class SimpleRel extends RelNode {

  /** The left hand side operation of this simple relation. */
  protected Operation m_leftHandSide;
  
  /** The right hand side operation of this simple relation. */
  protected Operation m_rightHandSide;
  
  /**
   *  This variable determines, whether this simple relation should be kept in 
   *  normalized state and at which level. the possible values are 
   *  {@link net.mumie.mathletfactory.algebra.rel.Relation#NORMALIZE_NONE}, 
   *  {@link net.mumie.mathletfactory.algebra.rel.Relation#NORMALIZE_REL}, 
   *  {@link net.mumie.mathletfactory.algebra.rel.Relation#NORMALIZE_OP}
   *  and {@link net.mumie.mathletfactory.algebra.rel.Relation#NORMALIZE_OP_SEPARATED}.
   */
  protected int m_normalForm = Relation.NORMALIZE_NONE;

  /**
   * The relation sign for this simple relation. It may have one of the 
   * constant values specified in 
   * {@link net.mumie.mathletfactory.algebra.rel.Relation}.
   */
  protected String m_relationSign = "=";
  
  private MDouble leftResultDouble = new MDouble(), rightResultDouble = new MDouble();

  /**
   *  Parses a simple relation from the given expression (should be something
   *  like "f(x,y,...) >= g(a,b,...)") and constructs it using 
   *  <code>normalForm</code> as flag, whether the left and right hand side
   *  should be normalized.
   */
  public SimpleRel(Class numberClass, String expression, int normalForm) {
    super(numberClass);
    parse(expression);
    setNormalForm(normalForm);
  }

  /**
   *  Constructs a simple relation from a given left and right hand side, a
   *  relation sign and a flag indicating, whether the left and right hand
   *  side should be normalized.
   */
  public SimpleRel(Operation leftHandSide,  Operation rightHandSide,
    String relationSign, int normalform) {
    super(leftHandSide.getNumberClass());
    m_leftHandSide = new Operation((OpNode) leftHandSide.getOpRoot().clone());
    m_rightHandSide = new Operation((OpNode) rightHandSide.getOpRoot().clone());
    m_numberClass = m_leftHandSide.getNumberClass();
    setRelationSign(relationSign);
    setNormalForm(normalform);
  }

  /** Copy constructor. */
  public SimpleRel(SimpleRel relation) {
    this(
      relation.getLeftHandSide(),
      relation.getRightHandSide(),
      relation.getRelationSign(),
      relation.getNormalForm());
    m_relation = relation.getRelation();
  }

  /**
   *  The Constructor for relations of type "expression > 0" , "expression = 0",
   *  etc.
   */
  public SimpleRel(Operation leftHandSide, String relationSign) {
    this(
      leftHandSide,
      new Operation(new NumberOp(leftHandSide.getNumberClass(), 0)),
      relationSign,
      Relation.NORMALIZE_OP_SEPARATED);
  }

  /**
   *  The Constructor for relations of type "expression > 0" , "expression = 0",
   *  etc.
   */
  public SimpleRel(Operation leftHandSide, String rightHandSide, String relationSign) {
    this(
      leftHandSide,
      new Operation(leftHandSide.getNumberClass(), rightHandSide, true),
      relationSign,
      Relation.NORMALIZE_OP_SEPARATED);
  }

  /** Sets the left hand side of this (in)equation. */
  public void setLeftHandSide(Operation leftHandSide) {
    m_leftHandSide = leftHandSide;
    if (m_normalForm >= Relation.NORMALIZE_OP)
      m_leftHandSide.normalize();
  }

  /** Sets the left hand side of this (in)equation. */
  public void setLeftHandSide(OpNode leftHandSide) {
    m_leftHandSide.setOpRoot(leftHandSide);
    if (m_normalForm >= Relation.NORMALIZE_OP)
      m_leftHandSide.normalize();
  }

  /** Sets the right hand side of this (in)equation. */
  public void setRightHandSide(Operation rightHandSide) {
    m_rightHandSide = rightHandSide;
    if (m_normalForm >= Relation.NORMALIZE_OP)
      m_rightHandSide.normalize();
  }

  /** Sets the right hand side of this (in)equation. */
  public void setRightHandSide(OpNode rightHandSide) {
    m_rightHandSide.setOpRoot(rightHandSide);
    if (m_normalForm >= Relation.NORMALIZE_OP)
      m_rightHandSide.normalize();
  }

  /** Sets the relation Sign for this simple relation. */
  public void setRelationSign(String sign) {
    if (sign.length() == 0 || !sign.matches("[<>!]*=*"))
      throw new IllegalArgumentException(
        "sign must be either '=', '<', '>', " + "'>=', '<=' or '!='!");
    m_relationSign = sign;
  }

  /** Returns the relation Sign for this simple relation. */
  public String getRelationSign() {
    return m_relationSign;
  }

  /** Returns the left hand side of this (in)equation. */
  public Operation getLeftHandSide() {
    return m_leftHandSide;
  }

  /** Returns the right hand side of this (in)equation. */
  public Operation getRightHandSide() {
    return m_rightHandSide;
  }
  
  /** Phantom implementation, should not be called. */
  public void separateFor(String string){}
  
  /**
   *  Sets this simple relation as the symbolic representation of the given
   *  string.
   */
  public void parse(String relationString) {
    String[] parts = relationString.split("[<>!=]+");
    if (parts.length != 2)
      throw new IllegalArgumentException(
        "must contain an expression like a <expression-sign> b, with "
          + "<expression-sign> = '=', '>', '<', '>=', '<=' or '!='");
    setLeftHandSide(OpParser.getOperation(m_numberClass, parts[0], m_normalForm >= Relation.NORMALIZE_OP));
    setRelationSign(relationString.replaceAll("[^<>!=]", ""));
    setRightHandSide(OpParser.getOperation(m_numberClass, parts[1], m_normalForm >= Relation.NORMALIZE_OP));
  }

  /**
   *  Returns true if the given set of values assigned to the used variables
   *  (alphabetically sorted) satisfies the relation.
   *  @throws IllegalArgumentException if not all the variables are assigend
   *  with values.
   */
  public boolean evaluate(MNumber[] assignedValues) {
    HashSet usedVariablesSet = new HashSet();
    getUsedVariables(usedVariablesSet);
    String[] usedVariables = new String[usedVariablesSet.size()];
    Arrays.sort(usedVariables);
    usedVariables = (String[]) usedVariablesSet.toArray(usedVariables);

    // store the pairs in a hash map
    HashMap variableValues = new HashMap(assignedValues.length);
    for (int i = 0; i < assignedValues.length; i++)
      variableValues.put(usedVariables[i], assignedValues[i]);
    return evaluate(variableValues);
  }

  /**
   *  Returns true if the given set of values satisfies the relation, false if
   *  not.
   *  @throws IllegalArgumentException if not all the variables are assigend
   *  with values.
   */
  public boolean evaluate(HashMap variableValues) {

    MNumber leftResult = m_leftHandSide.evaluate(variableValues);
    MNumber rightResult = m_rightHandSide.evaluate(variableValues);
    return compareResults(leftResult, rightResult);
  }
  
  public boolean evaluateFast(){
    leftResultDouble.setDouble(m_leftHandSide.getOpRoot().getResultDouble());
    rightResultDouble.setDouble(m_rightHandSide.getOpRoot().getResultDouble());
    return compareResults(leftResultDouble, rightResultDouble);
  }

  /**
   * Compares the values of <code>leftResult</code> and 
   * <code>rightResult</code> using {@link #m_relationSign} and returns
   * the result of the comparison.
   */
  private boolean compareResults(MNumber leftResult, MNumber rightResult){

    if(m_relationSign.length() == 1){
    // "="
    if (m_relationSign.equals(Relation.EQUAL))
      return leftResult.equals(rightResult);

    // ">"
    if (m_relationSign.equals(Relation.GREATER))
      return !leftResult.lessOrEqualThan(rightResult);
    // "<"
    if (m_relationSign.equals(Relation.LESS))
      return !leftResult.greaterOrEqualThan(rightResult);
    
    }else {
      
    //  "!="
    if (m_relationSign.equals(Relation.NOT_EQUAL))
      return !leftResult.equals(rightResult);
    // "<="
    if (m_relationSign.equals(Relation.LESS_OR_EQUAL))
      return leftResult.lessOrEqualThan(rightResult);
    // ">="
    if (m_relationSign.equals(Relation.GREATER_OR_EQUAL))
      return leftResult.greaterOrEqualThan(rightResult);
    }
    throw new IllegalStateException(); // never reached
  }

  /** Returns all used variables as a HashSet of identifiers (Strings). */
  public HashSet getUsedVariables(HashSet usedVariables) {
    m_leftHandSide.getUsedVariables(usedVariables);
    m_rightHandSide.getUsedVariables(usedVariables);
    return usedVariables;
  }

  /** 
   *  Returns a HashMap that contains the identifiers of all the used 
   *  variables as keys and a list with the corresponding 
   *  {@link net.mumie.mathletfactory.algebra.op.node.VariableOp}s as values.
   */ 
  public HashMap getVariableOps(){
    HashSet variablesUsed = getUsedVariables(new HashSet());
    HashMap map = new HashMap(variablesUsed.size());
    Iterator i = variablesUsed.iterator();
    while(i.hasNext()){
      List variableOps = new LinkedList();
      String identifier = (String)i.next();
      m_leftHandSide.putVariableOps(identifier, variableOps);
      m_rightHandSide.putVariableOps(identifier, variableOps);
      map.put(identifier, variableOps);
    }
    return map;
  }


  /**
   *  The {@link #toString string} representation with respect to the given decimal format. 
   */
  public String toString(DecimalFormat format) {
    return m_leftHandSide.toString(format) + m_relationSign + m_rightHandSide.toString(format);
  }


  /** Returns the String representation of this simple relation. */
  public String toString() {
    return m_leftHandSide.toString() + m_relationSign + m_rightHandSide.toString();
  }

  /** 
   *  Sets the operations (left and right hand side) of this simple relation 
   *  to normal form. 
   */
  public void setNormalForm(int normalForm) {
    m_normalForm = normalForm;
    if(normalForm >= Relation.NORMALIZE_OP){
      m_leftHandSide.setNormalForm(true);
      m_rightHandSide.setNormalForm(true);
    }
  }

  public void normalizeOp(){
    m_leftHandSide.normalize();
    m_rightHandSide.normalize();
  }

  /**
   * * logically negates this simple relation by replacing the relation
   * sign with its counterpart (e.g. "=" is replaced with "!=", ">" with
   * "<=", etc.). 
   *
   * @see net.mumie.mathletfactory.algebra.rel.node.RelNode#negate(boolean)
   */ 
  public RelNode negate(boolean deepCopy) {
    if (m_relationSign.equals("="))
      m_relationSign = "!=";
    else if (m_relationSign.equals(">"))
      m_relationSign = "<=";
    else if (m_relationSign.equals(">="))
      m_relationSign = "<";
    else if (m_relationSign.equals("<="))
      m_relationSign = ">";
    else if (m_relationSign.equals("<"))
      m_relationSign = "<=";
    else if (m_relationSign.equals("!="))
      m_relationSign = "=";
    else
      // unreachable
      throw new IllegalStateException();
    if(deepCopy)
      return new SimpleRel(this);
    return this;
  }

  /**
   * Returns a new simple relation that is the logical negated equivalent of
   * this simple relation. 
   * @see #negate
   */
  public SimpleRel negated() {
    return (SimpleRel)new SimpleRel((SimpleRel)this).negate(false);
  }

  /** 
   *  Returns whether the operations (left and right hand side) of this 
   *  simple relation are in normal form. 
   */
  public int getNormalForm() {
    return m_normalForm;
  }

  /**
   *
   * Returns the depth of the subtree rooted by this node.
   * 
   * If <code>opNodesCount</code> is set to true, the
   * {@link net.mumie.mathletfactory.algebra.op.node.OpNode}s do of
   * {@link SimpleRel}s also count as tree nodes.
   * @see net.mumie.mathletfactory.algebra.rel.node.RelNode#getDepth(boolean)
   */
  public int getDepth(boolean opNodesCount) {
    if (opNodesCount)
      return 1
        + Math.max(m_leftHandSide.getOpRoot().getDepth(), m_rightHandSide.getOpRoot().getDepth());
    else
      return 0;
  } 

  public boolean isLeaf() {
    return true;
  }

  /**
   *  Returns the deep copied tree rooted by this node.
   */
  public Object clone() {
    SimpleRel clone = new SimpleRel(this);
    return clone;
  }
  
  /** 
   * Returns the set representation of the right hand side of this simple relation. 
   */
  public Interval[] getIntervalsTrue(double epsilon, Interval interval){
    if(m_normalForm < Relation.NORMALIZE_OP_SEPARATED)
      throw new IllegalStateException("Relation is not in normalized, separated state: "+getRelation());
    if(m_rightHandSide.getUsedVariables().length > 0)
      throw new IllegalStateException("Relation contains other variables: "+getRelation());  
    if(!m_leftHandSide.isIsolated())
      throw new IllegalStateException("Variable on left hand side is not isolated: "+m_leftHandSide);
    //TODO: return an interval regarding the relation sign and the right hand side operation
    MNumber rightHandSideResult = null;
    try {
      rightHandSideResult = getRightHandSide().evaluate(new MNumber[0]); 
    } catch(IllegalStateException e){
      // contains MultipleOfZNodes
      List zNodes = getRightHandSide().getOpRoot().getAllNodesOfType(MultipleOfZNode.class);
      FiniteBorelSet trueSet = new FiniteBorelSet(getNumberClass());
      trueSet.setEmpty();
      //System.out.println(toString()+" contains MultipleOfZNodes :"+zNodes);
      double lastVal = interval.getLowerBoundaryVal().getDouble();
      
      // iterate all zNodes with values from -100 to 100
      for(int i=0;i<zNodes.size();i++)
        ((MultipleOfZNode)zNodes.get(i)).setZ(-100);
      for(int i=0;i<zNodes.size();i++){
        for(int z = -100;z<100;z++){        
          ((MultipleOfZNode)zNodes.get(i)).setZ(z);
          rightHandSideResult = getRightHandSide().evaluate(new MNumber[0]);
          if(trueSet.isEmpty())
            trueSet.addIntervals(getIntervalsTrueFor(rightHandSideResult));
          else {
            if(m_relationSign.equals("="))
              trueSet.addIntervals(getIntervalsTrueFor(rightHandSideResult));
            else
              trueSet.intersect(getIntervalsTrueFor(rightHandSideResult));
          }
        }
        if(Math.abs(lastVal - rightHandSideResult.getDouble()) < epsilon)
          break;        
      }
      // invalidate all zNodes
      for(int i=0;i<zNodes.size();i++)
        ((MultipleOfZNode)zNodes.get(i)).setZ(null);
      return trueSet.intersect(new Interval[] {interval}).getIntervals();
    }
    return getIntervalsTrueFor(rightHandSideResult);
  }
    private Interval[] getIntervalsTrueFor(MNumber rightHandSideResult){
    // handle all cases
    if(m_relationSign.equals("!=")){
      Interval retVal1 = Interval.getRealAxis(m_numberClass);
      Interval retVal2 = Interval.getRealAxis(m_numberClass);
      retVal1.setUpperBoundary(rightHandSideResult, Interval.OPEN);
      retVal2.setLowerBoundary(rightHandSideResult, Interval.OPEN);
      return new Interval[]{retVal1, retVal2};
    }

    if(m_relationSign.equals("="))
      return new Interval[]{new Interval(m_numberClass, rightHandSideResult.getDouble(), Interval.CLOSED,
                                         rightHandSideResult.getDouble(), Interval.CLOSED, Interval.SQUARE)};
    // must be >, >= , < or <=
    Interval retVal = Interval.getRealAxis(m_numberClass);                                     
    if(m_relationSign.startsWith(">")){
      retVal.setLowerBoundary(rightHandSideResult, m_relationSign.equals(">") ? Interval.OPEN : Interval.CLOSED);
      return new Interval[]{retVal};  
    }
    
    // < or <=                                       
    retVal.setUpperBoundary(rightHandSideResult, m_relationSign.equals("<") ? Interval.OPEN : Interval.CLOSED);
    return new Interval[]{retVal};      
  }
    
  /**
   * Returns the set representation of the right hand side of negated version 
   * of this simple relation. 
   */
  public Interval[] getIntervalsFalse(double epsilon, Interval interval){
    SimpleRel negated = negated();
    return negated.getIntervalsTrue(epsilon, interval);
  }
  
  /**
   * Retursn true, if this simple relation logically includes the given simple relation, i.e.
   * that this relation follows from the given relation
   * (e.g. a<=b includes a<b, but a<b does not include a<=b)
   */
  public boolean includes(SimpleRel aRel){
    //if left hand side and right hand site are exchanged, look at the reexchanged version
    if(aRel.getLeftHandSide().equals(getRightHandSide()) && aRel.getRightHandSide().equals(getLeftHandSide())){
      SimpleRel newRel = (SimpleRel)aRel.clone();
      newRel.setLeftHandSide(aRel.getRightHandSide());
      newRel.setRightHandSide(aRel.getLeftHandSide());
      if(newRel.getRelationSign().startsWith(">"))
        newRel.setRelationSign(newRel.getRelationSign().replace('>', '<'));
      else
        newRel.setRelationSign(newRel.getRelationSign().replace('<', '>'));
      aRel = newRel;
    }
          
    // the two operations should be equal
    if(!(aRel.getLeftHandSide().equals(getLeftHandSide()) && aRel.getRightHandSide().equals(getRightHandSide())))
      return false;    
    
    if(aRel.getRelationSign().equals(getRelationSign())) // the trivial case
      return true;
      
    if(getRelationSign().equals("!=") && !aRel.getRelationSign().equals("="))
      return true;
    if(getRelationSign().equals("<=") && aRel.getRelationSign().equals("<"))  
      return true;
    if(getRelationSign().equals(">=") && aRel.getRelationSign().equals(">"))  
      return true;
      
    return false;
  }
  
  /** 
   * Two simple relations are considered equal if they both include each other.
   * @see #includes
   */
  public boolean equals(Object shouldBeASimpleRel){
    if(!(shouldBeASimpleRel instanceof SimpleRel))
      return false;
    return includes((SimpleRel)shouldBeASimpleRel) && ((SimpleRel)shouldBeASimpleRel).includes(this);
  }
}
