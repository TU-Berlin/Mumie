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

package net.mumie.mathletfactory.math.algebra.rel;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.mumie.mathletfactory.action.message.TodoException;
import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.algebra.op.node.NumberOp;
import net.mumie.mathletfactory.math.algebra.op.node.VariableOp;
import net.mumie.mathletfactory.math.algebra.rel.node.AllRel;
import net.mumie.mathletfactory.math.algebra.rel.node.AndRel;
import net.mumie.mathletfactory.math.algebra.rel.node.InSetRel;
import net.mumie.mathletfactory.math.algebra.rel.node.NullRel;
import net.mumie.mathletfactory.math.algebra.rel.node.OrRel;
import net.mumie.mathletfactory.math.algebra.rel.node.RelNode;
import net.mumie.mathletfactory.math.algebra.rel.node.SimpleRel;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberTypeDependentIF;
import net.mumie.mathletfactory.math.set.FiniteBorelSet;
import net.mumie.mathletfactory.math.set.Interval;
import net.mumie.mathletfactory.math.set.NumberSetIF;

/**
 *  This class represents an arbitrary complex algebraic relation based on a 
 *  dedicated number class. Basically it consists of a tree of 
 *  {@link net.mumie.mathletfactory.algebra.rel.node.RelNode}s that determine 
 *  the structure of the relation.
 * 	
 * 	@author Paehler
 * 	@mm.docstatus finished
 */
public class Relation implements NumberTypeDependentIF {

  /** The constant for the equation sign. */
  public final static String EQUAL = "=";
  /** The constant for the inequality sign. */
  public final static String NOT_EQUAL = "!=";
  /** The constant for the greater than inequality sign. */
  public final static String GREATER = ">";
  /** The constant for the less than inequality sign. */
  public final static String LESS = "<";
  /** The constant for the less than or equal inequality sign. */
  public final static String LESS_OR_EQUAL = "<=";
  /** The constant for the greater or equal inequality sign. */
  public final static String GREATER_OR_EQUAL = ">=";

  /** The flag indicating that the relation should not be kept in normalized state. */
  public final static int NORMALIZE_NONE = 0;
  
  /** 
   * The flag indicating that the relation should be kept in relation level 
   * normalized (the relation tree is normal form, but not necessarily the 
   * operation trees at the leaves) state.
   */
  public final static int NORMALIZE_REL = 1;

  /** 
   * The flag indicating that the relation should be kept in operation level 
   * normalized state (the relation tree is in normal form and also all the 
   * operation trees in the 
   * {@link net.mumie.mathletfactory.algebra.rel.node.SimpleRel}s).
   */
  public final static int NORMALIZE_OP = 2;
  
  /** 
   * The flag indicating that the relation should be kept in operation level 
   * normalized state (the relation tree is normal form and also the operation 
   * trees at the leaves) and the 
   * {@link net.mumie.mathletfactory.algebra.rel.node.SimpleRel}s) are all 
   * separated for the same variable.
   */
  public final static int NORMALIZE_OP_SEPARATED = 3;

  /**
   *  This variable determines, whether this relation should be kept in 
   *  normalized state and at which level. the possible values are 
   *  {@link #NORMALIZE_NONE}, {@link #NORMALIZE_REL}, {@link #NORMALIZE_OP}
   *  and {@link #NORMALIZE_OP_SEPARATED}.
   */
  protected int m_normalFormLevel = NORMALIZE_NONE;

  /** 
   * If this variable is not null, it defines for which identifier the leaves
   * of this relation should be separated.
   * @see #getSeparateVariable
   */
  protected String m_separateFor = null;

  /** The root node of the relation tree. */
  private RelNode m_relRoot;
 
  /** Temporary register that stores the used identifiers. Used in {@link #evaluateFast}. */
  private String[] m_identifiers;
  
  /** Temporary register that stores the mapping from identifier to VariableOps. Used in {@link #evaluateFast}. */
  private HashMap m_ids2variableOps;
  
  /**
   *  Constructs a relation containing <code>relRoot</code> as root.
   */
  public Relation(RelNode relRoot, int normalForm) {
    setRelRoot(relRoot);
    setNormalForm(normalForm);
  }
  
  /** Constructs a relation from the given arguments. */
  public Relation(Class entryClass, String expression, int normalForm) {
    this(RelParser.getRelationTree(entryClass, expression),normalForm);
  }

  /**
   *  Constructs a relation containing a 
   *  {@link net.mumie.mathletfactory.algebra.rel.node.SimpleRel} as root.
   */
  public Relation(Operation leftHandSide, Operation rightHandSide, 
                  String relationSign, int normalform) {
    this(new SimpleRel(leftHandSide, rightHandSide, relationSign, normalform), normalform);
  }

  /**
   * Copy constructor.
   */
  public Relation(Relation relation) {
    this((RelNode)relation.getRelRoot().clone(), relation.getNormalForm());
  }

	/** 
	 * Constructs a relation that consists of the expression "x \in S" where x is the identifier for a variable and
	 * S is an arbitrary {@link net.mumie.mathletfactory.set.NumberSetIF NumberSet}.
	 * @param set
	 * @param variable
	 */
	public Relation(NumberSetIF set, String variable){
		 m_relRoot = new InSetRel(set, new VariableOp(set.getNumberClass(), variable));
	}

  /**
   *  The Constructor for relations of type "expression > 0" , "expression = 0",
   *  etc.
   */
  public Relation(Operation leftHandSide, String relationSign) {
    this(leftHandSide, new Operation(new NumberOp(leftHandSide.getNumberClass(), 0)),
      relationSign, NORMALIZE_OP);
  }

  /**
   *  The Constructor for relations of type "expression > 0" , "expression = 0",
   *  etc.
   */
  public Relation(Operation leftHandSide, String rightHandSide, String relationSign) {
    this(
      leftHandSide,
      new Operation(leftHandSide.getNumberClass(), rightHandSide, true),
      relationSign,
      NORMALIZE_OP);
  }

  /**
   *  Logically negates this relation.  
   */ 
  public void negate(){
    m_relRoot = m_relRoot.negate(false);
    normalize();
  }
  
  /**
   *  Returns a relation that is the logically negated of this relation.  
   */ 
  public Relation negated(){
    return new Relation(m_relRoot.negate(true), m_normalFormLevel);
  }

  /**
   *  Returns true if the given set of values assigned to the used variables
   *  (alphabetically sorted) satisfies the relation.
   *  @throws IllegalArgumentException if the number of assigned values does 
   *  not match the number of variables used.
   */
  public boolean evaluate(MNumber[] assignedValues) {
    HashSet usedVariablesSet = m_relRoot.getUsedVariables(new HashSet());
    String[] usedVariables = new String[usedVariablesSet.size()];
    usedVariables = (String[]) usedVariablesSet.toArray(usedVariables);
    if (assignedValues.length < usedVariables.length)
      throw new IllegalArgumentException(
        "number of assigned values (" +assignedValues.length+ ") does not match the number of variables used ("+usedVariables.length+")");
    Arrays.sort(usedVariables);
    HashMap variableValues = new HashMap(usedVariables.length);
    for (int i = 0; i < usedVariables.length; i++)
      variableValues.put(usedVariables[i], assignedValues[i]);
    return evaluate(variableValues);
  }

  /**
   * This method updates the registers {@link #m_identifiers} and 
   * {@link #m_ids2variableOps} needed by {@link #evaluateFast}. It should be 
   * called, whenever the relation tree has changed.
   */
  public void prepareEvaluateFast(){
    
    SimpleRel[] simpleRels = getSimpleRelations();
    m_identifiers = getUsedVariablesArray();
    // initialize ids -> variableOps mapping
    m_ids2variableOps = new HashMap(m_identifiers.length);
    for(int i=0;i<m_identifiers.length;i++)
      m_ids2variableOps.put(m_identifiers[i], new LinkedList());
      
    // fill the mapping with the variableOps from the (simple relation) leaves of this relation
    for(int i=0;i<simpleRels.length;i++){
      HashMap mapForSimpleRel = simpleRels[i].getVariableOps();
      Iterator iter = mapForSimpleRel.keySet().iterator();
      // add all VariableOps for this simple relations to the relations list
      while(iter.hasNext()){
        String identifier = (String)iter.next();
        ((List)m_ids2variableOps.get(identifier)).addAll((List)mapForSimpleRel.get(identifier));
      }
    }
  }
  
  /**
   *  Returns true if the given set of double values assigned to the used variables
   *  (alphabetically sorted) satisfies the relation.
   *  WARNING: Be sure to call {@link #prepareEvaluateFast} prior to this 
   *  method (Generally it should be called for graphical purposes only).   
   */
  public boolean evaluateFast(double[] assignedValues){
    if(m_identifiers.length != 0 && m_identifiers.length != assignedValues.length)
      throw new IllegalArgumentException("identifiers length "+m_identifiers.length+" does not match the length of the given array: "+assignedValues.length);
    for(int i=0;i<m_identifiers.length;i++){
      LinkedList variableOps = (LinkedList)m_ids2variableOps.get(m_identifiers[i]); 
      for(int j=0;j < variableOps.size();j++)
        ((VariableOp)variableOps.get(j)).setValue(assignedValues[i]);
    }
    return m_relRoot.evaluateFast();   
  }
  
  /** Returns the variables that have been prepared in {@link #prepareEvaluateFast}. */
  public String[] getPreparedIdentifiers(){
    return m_identifiers;
  }
  
  /**
   *  Returns true if the given set of values satisfies the relation, false if
   *  not.
   *  @throws IllegalArgumentException if not all the variables are assigend
   *  with values.
   */
  public boolean evaluate(HashMap variableValues) {
    return m_relRoot.evaluate(variableValues);
  }

  /**
   * Fills and returns the given HashSet with all variables used within this relation.
   */
  public HashSet getUsedVariables(HashSet usedVariables) {
    return m_relRoot.getUsedVariables(usedVariables);
  }

  /** 
   * Returns the used variables as a sorted string array.
   */
  public String[] getUsedVariablesArray(){
    HashSet tmp = getUsedVariables(new HashSet());
    String[] array = new String[tmp.size()];
    array = (String[]) tmp.toArray(array);
    Arrays.sort(array);
    return array;
  }

  /**
   * Returns an array containing the subrelations of this relation (i.e. the 
   * parts of the relation tree, that contain no 
   * {@link net.mumie.mathletfactory.algebra.rel.node.OrRel} Conjunctions). 
   */
  public Relation[] getSubRelations() {

    List subRelationsList = new LinkedList();
    m_relRoot.getSubrelations(subRelationsList);
    Relation[] subRelationsArray = new Relation[subRelationsList.size()];
    subRelationsArray = (Relation[]) subRelationsList.toArray(subRelationsArray);
    return subRelationsArray;
  }

  /**
   * Returns an array containing all the simple relations within the relation
   * tree of this relation (i.e. the leaves that are no 
   * {@link net.mumie.mathletfactory.algebra.rel.node.NullRel}s and
   * {@link net.mumie.mathletfactory.algebra.rel.node.AllRel}s).
   */
  public SimpleRel[] getSimpleRelations(){
    List simpleRelationsList = new LinkedList();
    m_relRoot.getSimpleRelations(simpleRelationsList);
    SimpleRel[] simpleRelationsArray = new SimpleRel[simpleRelationsList.size()];
    simpleRelationsArray = (SimpleRel[]) simpleRelationsList.toArray(simpleRelationsArray);
    return simpleRelationsArray;
  }
  
  /** 
   * Returns an array containing all simple relations, that contain the variable 
   * identified  by <code>identifier</code>.
   */
  public SimpleRel[] getSimpleRelationsWith(String identifier){
    List simpleRelationsList = new LinkedList();
    m_relRoot.getSimpleRelations(simpleRelationsList);
    Iterator i = simpleRelationsList.iterator();
    while(i.hasNext()){
      SimpleRel current = (SimpleRel)i.next();
      if(!current.getVariableOps().containsKey(identifier))
        simpleRelationsList.remove(current);  
    }      
    SimpleRel[] simpleRelationsArray = new SimpleRel[simpleRelationsList.size()];
    simpleRelationsArray = (SimpleRel[]) simpleRelationsList.toArray(simpleRelationsArray);
    return simpleRelationsArray;
  }

  /**
   * Returns a human readable form of the relation with the given decimal format.
   */
  public String toString(DecimalFormat format) {
    return m_relRoot.toString(format);
  }
  
  /**
   * Returns a human readable form of the relation, e.g. 
   * <code>"sin(x)*y<=0 AND x > 0"</code>.
   */
  public String toString() {
    return m_relRoot.toString();
  }

  /** 
   * Returns the set for which this relation is true. This works only for 
   * monovariate normalized and separated relations! 
   */
  public FiniteBorelSet getTrueForSet(double epsilon,  Interval interval){
    return m_relRoot.getTrueFor(epsilon, interval);
  }

  /** 
   * Returns the set for which this relation is false. This works only for 
   * monovariate normalized and separated relations! 
   */
  public FiniteBorelSet getFalseForSet(double epsilon,  Interval interval){
    return m_relRoot.getFalseFor(epsilon, interval);
  }

  /**
   * Sets {@link #m_normalForm}.
   */
  public void setNormalForm(int normalFormLevel) {
    m_normalFormLevel = normalFormLevel;
    m_relRoot.setNormalForm(normalFormLevel);
    if (normalFormLevel > NORMALIZE_NONE)
      normalizeRel();
    if(normalFormLevel >= NORMALIZE_OP)
      normalizeOp();
    if(normalFormLevel >= NORMALIZE_OP_SEPARATED){
      separate();
      m_relRoot.setNormalForm(normalFormLevel);
    }
  }

  /**
   * Returns {@link #m_normalForm}.
   */
  public int getNormalForm() {
    return m_normalFormLevel;
  }

  /** 
   * Normalizes the relation based on the normalization level. 
   */
  public void normalizeRel() {
    if(m_normalFormLevel == NORMALIZE_NONE)
      return;
    if(m_normalFormLevel >= NORMALIZE_REL){      
      m_relRoot = RelTransform.normalizeRelation(m_relRoot);
      m_relRoot.setRelation(this);
    }
    if(m_normalFormLevel >= NORMALIZE_OP){
      normalizeOp();
    }
    if(m_normalFormLevel >= NORMALIZE_OP_SEPARATED)
      separate();
  }

  /**
   *  Normalizes this relation according to its normal form level.
   *  @see #setNormalForm  
   */
  public void normalize(){
    setNormalForm(m_normalFormLevel);
  }

  /** Normalizes all operation of the simple relation leaves. */
  private void normalizeOp(){
    SimpleRel[] leaves = getSimpleRelations();
     for(int i=0;i<leaves.length;i++)
       leaves[i].normalizeOp();
  }



  /**
   * If {@link #m_separateFor} is set it is returned. Otherwise the alphabetically
   * first variable used by this relation is returned. If there is no
   * variable used by this relation, null is returned.
   */
  public String getSeparateVariable(){
    if(m_separateFor != null)
      return m_separateFor;
    HashSet variables = m_relRoot.getUsedVariables(new HashSet());
    String[] result = new String[variables.size()];
    result = (String[])variables.toArray(result);
    Arrays.sort(result);
    if(result.length > 0)
      return result[0];
    return null; 
  }
  
  /** 
   * Separates the relation for the given identifier. 
   */
  public void separateFor(String identifier){
    m_separateFor = identifier;
    separate();
  }

  /** 
   * Separates the relation for the {@link #getSeparateVariable separate variable}. 
   * If this variable is null, the alphabetically first variable will be used as 
   * separation variable. 
   */
  public void separate(){
    String[] usedVariables = getUsedVariablesArray();
    if(usedVariables.length == 0)
      return;
    if(m_separateFor == null)
      m_separateFor = usedVariables[0];
    if(m_normalFormLevel < NORMALIZE_OP)
      setNormalForm(NORMALIZE_OP);
    m_relRoot = RelTransform.separateFor(m_relRoot, m_separateFor);
    normalizeOp();
  }
  
  /**
   * Sets the root of the relation tree for this relation. The primary simple 
   * relation for this relation (i.e. the 
   * {@link net.mumie.mathletfactory.algebra.rel.node.SimpleRel} that is 
   * subject to relation transformations) is set to the first simple relation
   * in a breadth-first traversion. 
   */
  protected void setRelRoot(RelNode relRoot) {
    m_relRoot = relRoot;
    m_relRoot.setRelation(this);
    prepareEvaluateFast();
    if(m_normalFormLevel > Relation.NORMALIZE_NONE)
      normalizeRel();
  }
  
  /**
   * Returns the root of the relation tree for this relation.
   */
  public RelNode getRelRoot() {
    return m_relRoot;
  }

  /**
   * Every node in the tree of this relation that is equal to 
   * <code>node</code> is replaced by <code>with</code>.  
   */
  public void replace(RelNode node, RelNode with) {
    if(m_relRoot.equals(node))
      m_relRoot = with;
    else m_relRoot.replaceByValue( node, with);
    setRelRoot(m_relRoot); // for normalization etc.
  }

  public Class getNumberClass(){
    if(m_relRoot.getNumberClass() == null || !(MNumber.class.isAssignableFrom(m_relRoot.getNumberClass())))
      System.out.println("Warning: number class of "+getClass()+" is "+m_relRoot.getNumberClass());
    return m_relRoot.getNumberClass();
  }

  /**
   * Replaces the current root of the relation tree by an 
   * {@link net.mumie.mathletfactory.algebra.rel.node.AndRel} 
   * that has the old root and <code>andComposite</code> as children. 
   */
  public void addAndComposite(Relation andComposite) {
    setRelRoot(new AndRel(new RelNode[] { m_relRoot, andComposite.getRelRoot()}));
  }

  /**
   * Replaces the current root of the relation tree by an 
   * {@link net.mumie.mathletfactory.algebra.rel.node.AndRel} 
   * that has the old root and the parsed result of <code>andCompositeString</code> as children. 
   */
  public void addAndComposite(String andCompositeString) {
    addAndComposite(new Relation(getNumberClass(), andCompositeString, m_normalFormLevel));
  }

  /**
   * Replaces the current root of the relation tree by an 
   * {@link net.mumie.mathletfactory.algebra.rel.node.OrRel} 
   * that has the old root and <code>orComposite</code> as children. 
   */
  public void addOrComposite(Relation orComposite) {
    setRelRoot(new OrRel(new RelNode[] { m_relRoot, orComposite.getRelRoot()}));
  }

  /**
   * Replaces the current root of the relation tree by an 
   * {@link net.mumie.mathletfactory.algebra.rel.node.OrRel} 
   * that has the old root and the parsed result of 
   * <code>orCompositeString</code> as children. 
   */
  public void addOrComposite(String orCompositeString) {
    addOrComposite(new Relation(getNumberClass(), orCompositeString, m_normalFormLevel));
  }
  
  /** Returns true if relation is the all relation. */
  public boolean isAll(){
    return m_relRoot instanceof AllRel;
  }
  
  /** Returns true if relation is the null relation. */
  public boolean isNull(){
    return m_relRoot instanceof NullRel;
  }
  
  /**
   *  Returns the value of this relation in a content MathML expression.
   */
  public String toContentMathML() {
    throw new TodoException();
  }

}
