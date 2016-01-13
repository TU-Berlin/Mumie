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

package net.mumie.mathletfactory.math.algebra.op;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import net.mumie.mathletfactory.math.algebra.op.node.AbsOp;
import net.mumie.mathletfactory.math.algebra.op.node.AddOp;
import net.mumie.mathletfactory.math.algebra.op.node.ConjOp;
import net.mumie.mathletfactory.math.algebra.op.node.MultOp;
import net.mumie.mathletfactory.math.algebra.op.node.NrtOp;
import net.mumie.mathletfactory.math.algebra.op.node.NumberOp;
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.math.algebra.op.node.VariableOp;
import net.mumie.mathletfactory.math.algebra.op.rule.OpRule;
import net.mumie.mathletfactory.math.algebra.rel.Relation;
import net.mumie.mathletfactory.math.algebra.rel.node.RelNode;
import net.mumie.mathletfactory.math.analysis.function.FunctionOverRIF;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.MOpNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.math.number.NumberTypeDependentIF;
import net.mumie.mathletfactory.util.xml.ExerciseObjectFactory;
import net.mumie.mathletfactory.util.xml.ExerciseObjectIF;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *  This class represents a mathematical expression (e.g. "3*x+y") that is
 *  used in equations, functions, set definitions etc. It acts as a wrapper
 *  for a {@link net.mumie.mathletfactory.math.algebra.op.node.OpNode}-Tree and
 *  additionally keeps track of the variables used within.
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class Operation implements FunctionOverRIF, NumberTypeDependentIF, ExerciseObjectIF {

//  private static Logger logger = Logger.getLogger(OpTransform.class.getName());

  /** The root of the operation tree. */
  protected OpNode m_opRoot;

  /** A flag indicating that the operation should be normalized after each transformation. */
  protected boolean m_normalize;

  /** The set of variables used in this operations. */
  protected HashSet m_usedVariables = new HashSet();

  /** A register in which the list of VariableOps for a given identifier are stored. */
  private List m_tmpVariableOps = new Vector();

	/**	This Field describes the current edited status. */
	private boolean m_isEdited = true;

	/**	This field holds the current label. */
	private String m_label = null;
	
	/**	This field holds the current <code>hidden</code> flag. */
	private boolean m_hidden = false;

	/** Constructs the operation from a given expression, number class and normalize flag. */
  public Operation(Class numberClass, String expr, boolean normalize) {
    this(OpParser.getOperation(numberClass, expr, normalize));
    m_normalize = normalize;
  }

  /** Constructs the operation from a given root node of the operation tree. */
  public Operation(OpNode rootNode) {
    this(rootNode, true);
  }

  /** Constructs the operation from a given root node of the operation tree. */
  public Operation(OpNode rootNode, boolean normalize) {
    m_normalize = normalize;
    m_opRoot = rootNode;
    m_opRoot.setParent(null);
  }

  /** Copy constructor. */
  public Operation(Operation anOperation) {
    set(anOperation);
  }

  /** Copy constructor. */
  public Operation(MNumber aNumber) {
    set(aNumber);
    m_normalize = true;
  }


  public void set(Operation anOperation){
    m_opRoot = (OpNode) (anOperation.m_opRoot.clone());
    m_usedVariables = new HashSet(anOperation.m_usedVariables);
    m_normalize = anOperation.isNormalForm();
  }

  public void set(MNumber aNumber){
  	if(aNumber instanceof MOpNumber) {
  		set(((MOpNumber)aNumber).getOperation());
  	} else {
	    m_opRoot = new NumberOp(aNumber);
	    m_usedVariables = new HashSet();
  	}
  }

  /** Sets the root node of this operation. */
  public void setOpRoot(OpNode opRoot) {
    m_opRoot = opRoot;
  }

  /** Returns the root node of this operation. */
  public OpNode getOpRoot() {
    return m_opRoot;
  }

  /** Returns the number class of this operation. */
  public Class getNumberClass() {
    return m_opRoot.getNumberClass();
  }

  /**
   * Returns the result of the evaluation of this operation. For this evaluation the values
   * that are currently assigned to the variables in the operation tree are used.
   */
  public MNumber getResult() {
    return m_opRoot.getResult();
  }

  /**
   * Returns the result of the evaluation of this operation. For this evaluation the values
   * that are currently assigned to the variables in the operation tree are used.
   */
  public double getResultDouble() {
    return m_opRoot.getResultDouble();
  }

  /**
   * Returns the derivative of the operation with respect to
   * <code>variable</code>.
   */
  public Operation getDerivative(String variable) {
    Operation tmp = this.deepCopy();
	  if (!m_normalize)
    	tmp.normalize();
    Operation retVal = new Operation(tmp.m_opRoot.getDerivative(variable));
    retVal.setNormalForm(true);
    return retVal;
  }

  /**
   * Returns the composition of this operation and <code>operation</code> for every occurence of the given identifier
   * in this operation.
   * 
   * @param identifier the identifier to be replaced
   * @param operation the operation to be composed with
   * @return the composition of both operations as a new {@link Operation} instance 
   */
  public Operation getComposed(String identifier, Operation operation) {
    Operation newOperation = new Operation(this);
    OpNode newRoot = newOperation.getOpRoot();
    if(newRoot instanceof VariableOp && ((VariableOp)newRoot).getIdentifier().equals(identifier)) {
      Operation tmp = new Operation(operation);
      OpNode.transferFactorAndExponent(newRoot, tmp.getOpRoot(), false);
      return tmp;
    }
    newOperation.getOpRoot().replace(identifier, operation.getOpRoot());
    newOperation.normalize();
    return newOperation;
  }

  /**
   * Substitutes/Replaces the given identifier with <code>operation</code> in this operation.
   * 
   * @param identifier the identifier to be replaced
   * @param operation the replacing operation
   */
  public void substitute(String identifier, Operation operation) {
    if(m_opRoot instanceof VariableOp && ((VariableOp)m_opRoot).getIdentifier().equals(identifier)) {
    	OpNode old = m_opRoot.deepCopy(true, true);
    	m_opRoot = operation.getOpRoot();
    	OpNode.transferFactorAndExponent(old, operation.getOpRoot(), false);
    } else {
    	m_opRoot.replace(identifier, operation.getOpRoot());
    }
    if(isNormalForm()) normalize();
	}

  /** Returns the relation for which this operation is defined. */
  public Relation getDefinedRelation() {
    RelNode rel = m_opRoot.getDefinedRel();
    return new Relation(rel, Relation.NORMALIZE_OP);
  }

  /** Returns the relation for which this operation is monotonously decreasing. */
  public Relation getMonotonicDecreasingRel() {
    return new Relation(
      m_opRoot.getMonotonicDecreasingRel(),
      Relation.NORMALIZE_OP);
  }

  /** Returns the relation for which this operation is monotonously increasing. */
  public Relation getMonotonicIncreasingRel() {
    return new Relation(
      m_opRoot.getMonotonicIncreasingRel(),
      Relation.NORMALIZE_OP);
  }

  /** Returns the relation for which this operation evaluates to zero. */
  public Relation getZeroRelation() {
    return new Relation(m_opRoot.getZeroRel(), Relation.NORMALIZE_OP);
  }

  /** Normalizes this operation. */
  public void normalize() {
    m_opRoot = OpTransform.normalize(m_opRoot);
  }

  /**
   * When setting <code>normalize</code> to true, this means that the operation
   * is normalized after each transformation.
   */
  public void setNormalForm(boolean normalize) {
    m_normalize = normalize;
    if (m_normalize)
      normalize();
  }

  /** Indicates if the operation should be normalized after each transformation. */
  public boolean isNormalForm() {
    return m_normalize;
  }

  /** Expands the operation (i.e. turns all expandable products into sums)
   *  and normalizes the result.
   */
  public void expand() {
    m_opRoot = OpTransform.expand(m_opRoot);
  }

  /** Expands the operation (i.e. turns all expandable products into sums) 
   *  and normalizes the result if the <code>normalize</code> flag 
   *  is set to <code>true</code>.
   */
  public void expand(boolean normalize) {
    m_opRoot = OpTransform.expand(m_opRoot, normalize);
  }

  /** Expands the internal data (factor, exponents) of each node of the operation. */
  public void expandInternal() {
    m_opRoot = OpTransform.expandInternal(m_opRoot);
  }

  /** Numerizes the operation (i.e. turns all symbolic constants into numbers). */
  public void numerize() {
    m_opRoot = OpTransform.expand(m_opRoot);
  }
  
  /**
   * Applies the given rule to this operation.
   */
  public void applyRule(OpRule rule) {
	  m_opRoot = OpTransform.applyRule(m_opRoot, rule);
  }

  /**
   * Applies the given rules to this operation.
   */
  public void applyRules(OpRule[] rules) {
	  m_opRoot = OpTransform.applyRules(m_opRoot, rules);
  }

  /** Returns true, if operation is a polynomial expression. */
  public boolean isPolynomial(){
    return m_opRoot.isPolynomial();
  }

  public boolean isAffine(){
    return m_opRoot.isAffine();
  }

  /** Returns true, if operation is a rational expression. */
  public boolean isRational(){
    return m_opRoot.isRational();
  }

  /** Returns this operation as polynomial holder. Will raise an exception if operation is no polynomial. */
  public PolynomialOpHolder getPolynomialHolder(){
    return m_opRoot.getPolynomialHolder();
  }

  /** Returns this operation as rationial holder. Will raise an exception if operation is no rational. */
  public RationalOpHolder getRationalHolder(){
    return m_opRoot.getRationalHolder();
  }

  /**
   *  The string representation of operation is the expression, which can be
   *  parsed anew into an operation whose normal form is equivalent to the normal form of this.
   */
  public String toString() {
    return m_opRoot.toString();
  }

  /**
   *  The {@link #toString() string} representation with respect to the given decimal format.
   */
  public String toString(DecimalFormat format) {
    return m_opRoot.toString(format);
  }

  /**
   * Outputs a scheme-like (functional) notation of the operation tree this
   * operation contains.
   */
  public String toDebugString() {
    return m_opRoot.toDebugString();
  }

  /**
   *  Returns the OpML-representation of the expression represented by this
   *  node. Needed for XSLT-Transformations of Operation Tree.
   */
  public String toOpML() {
    return m_opRoot.toOpML();
  }

  /**
   *  Returns the value of this operation in a content MathML expression.
   */
  public String toContentMathML() {
    return m_opRoot.toContentMathML();
  }

  public Node getMathMLNode() {
    return getMathMLNode(XMLUtils.getDefaultDocument());
  }

	/*
	 * @see net.mumie.mathletfactory.util.xml.MathMLSerializable#getMathMLNode()
	 */
	public Node getMathMLNode(Document doc) {
		Element mrow = ExerciseObjectFactory.createNode(this, "mrow", doc);
		if(isEdited()) {
			mrow.appendChild(m_opRoot.getMathMLNode(doc));
		} else {
			return ExerciseObjectFactory.createUneditedNode(this, doc);
		}
		return mrow;
	}

	/*
	 * @see net.mumie.mathletfactory.util.xml.MathMLSerializable#setMathMLNode(org.w3c.dom.Node)
	 */
	public void setMathMLNode(Node content) {
		OpNode opNode = XMLUtils.parseMathMLNode(getNumberClass(), content);
		setOpRoot(opNode);
		setEdited(opNode.isEdited());
		ExerciseObjectFactory.importExerciseAttributes(content, this);
		setNormalForm(m_normalize);
	}

  /**
   *  Assigns the given value to all <code>VariableOp</code>s with the given
   *  identifier.
   */
  public void assignValue(String identifier, MNumber value) {
    m_tmpVariableOps.clear();
    putVariableOps(identifier, m_tmpVariableOps);
    Iterator iterator = m_tmpVariableOps.iterator();
    while (iterator.hasNext())
       ((VariableOp) iterator.next()).setValue(value);
  }

  /**
   *  Assigns the given value to all <code>VariableOp</code>s with the given
   *  identifier.
   */
  public void assignValue(String identifier, double value) {
    m_tmpVariableOps.clear();
    putVariableOps(identifier, m_tmpVariableOps);
    Iterator iterator = m_tmpVariableOps.iterator();
    while (iterator.hasNext())
       ((VariableOp) iterator.next()).setValue(value);
  }

  /** Returns an alpahbetically sorted array of the variables used in this operation.*/
  public String[] getUsedVariables() {
    updateUsedVariables();
    String[] usedVariables = new String[m_usedVariables.size()];
    usedVariables = (String[]) m_usedVariables.toArray(usedVariables);
    Arrays.sort(usedVariables);
    return usedVariables;
  }
  
  public String[] getUsedConstants() {
	  String[] usedConstants = this.m_opRoot.getContainedConstants();
	  Arrays.sort( usedConstants );
	  return usedConstants;
  }
  
  public boolean isOnlyAVariable() {
	  if (this.m_opRoot instanceof VariableOp) {
		  MNumber one = NumberFactory.newInstance( this.getNumberClass(), 1.0 );
		  boolean result = !((VariableOp)this.m_opRoot).getIdentifier().equalsIgnoreCase( OpNode.REPLACEMENT_IDENTIFIER );
		  result = result && this.m_opRoot.getFactor().equals( one ) && this.m_opRoot.getExponent() == 1; 
		  return  result; 
	  }
	  return false;
  }

  /**
   *  If <code>usedVariables</code> is null, returns a new <code>HashSet</code>
   *  with all used variables (as Strings). Otherwise inserts the used variables
   *  in the <code>HashSet</code>.
   */
  public void getUsedVariables(HashSet usedVariables) {
    updateUsedVariables();
    if (usedVariables == null)
      usedVariables = new HashSet(m_usedVariables);
    else
      usedVariables.addAll(m_usedVariables);
  }

  /**
   * Refreshes {@link #m_usedVariables}.
   */
  private void updateUsedVariables() {
	m_usedVariables = new HashSet();
    String[] variables = m_opRoot.getContainedVariables();
    for (int i = 0; i < variables.length; i++)
      m_usedVariables.add(variables[i]);
  }

  /**
   * Substitutes all appearances of <code>identifier</code> in this
   * operation with <code>with</code>. E.g.
   * Operation("sin(x)/x").substitute("x","y") -> Operation("sin(y)/y").
   */
  public void substitute(String identifier, String with) {
    List variableOps = new LinkedList();
    putVariableOps(identifier, variableOps);
    Iterator i = variableOps.iterator();
    while (i.hasNext())
       ((VariableOp) i.next()).setIdentifier(with);
  }

  public double evaluate(double x){
    return evaluate(x, "x");
  }

  /**
   * Evaluates this operation for <code>x</code>.
   * @see net.mumie.mathletfactory.math.analysis.function.FunctionOverRIF#evaluate(double)
   */
  public double evaluate(double x, String identifier){
    //System.out.println("evaluating "+m_opRoot.toDebugString());
    m_tmpVariableOps.clear();
    putVariableOps(identifier, m_tmpVariableOps);
    for(int i=0;i<m_tmpVariableOps.size();i++)
      ((VariableOp)m_tmpVariableOps.get(i)).setValue(x);
    return m_opRoot.getResultDouble();
  }

  /**
   * Evaluates this operation for all the values of <code>xin</code> and
   * stores the results in <code>xout</code>.
   * @throws IllegalArgumentException if the operation contains more than one
   * variable.
   * @see net.mumie.mathletfactory.math.analysis.function.FunctionOverRIF#evaluate(double[], double[])
   */
  public void evaluate(double[] xin, double[] yout) {
    for (int i = 0; i < xin.length; i++) {
      yout[i] = evaluate(xin[i]);
    }
  }

  /**
   * Evaluates this operation for all the values of the array <code>xin[][]</code> and
   * stores the results in the array <code>xout[]</code>.
   * @throws IllegalArgumentException if the operation contains more than one
   * variable.
   */
  public void evaluate(double[][] xin, double[] yout) {
    //  determine all used variables
    Vector[] variableOps = new Vector[xin[0].length];
    String[] usedVariables = getUsedVariables();
    if (usedVariables.length != variableOps.length)
      throw new IllegalArgumentException("incorrect number of assigned values!");
    for (int i = 0; i < variableOps.length; i++)
      putVariableOps(usedVariables[i], variableOps[i]);

    for (int i = 0; i < xin.length; i++) {
      // iterate over all number tuples
      for (int j = 0; j < variableOps.length; j++) {
        // iterate over all variables
        Iterator iterator = variableOps[j].iterator();
        while (iterator.hasNext())
           ((VariableOp) iterator.next()).setValue(xin[i][j]);
      }
      yout[i] = m_opRoot.getResultDouble();
    }
  }


  /**
   * Evaluates this operation for <code>xin</code> and stores the result in
   * <code>yout</code>.
   * @throws IllegalArgumentException if the operation contains more than one
   * variable.
   *
   * @see net.mumie.mathletfactory.math.analysis.function.FunctionOverRIF#evaluate(MNumber, MNumber)
   */
  public void evaluate(MNumber xin, MNumber yout) {
    if(yout instanceof MDouble)
      yout.setDouble(evaluate(new MNumber[] { xin }).getDouble());
    else {
          Vector variableOps = new Vector();
          String[] usedVariables = getUsedVariables();
          if(usedVariables.length > 0)
            putVariableOps(usedVariables[0], variableOps);
          Iterator iterator = variableOps.iterator();
          while (iterator.hasNext())
             ((VariableOp) iterator.next()).setValue(xin);
          yout.set(m_opRoot.getResult());
        }
  }
  
  /**
   * Returns the result for the evaluated operation in x. 
   * Convenience method for {@link #evaluate(MNumber, MNumber)}.
   * The result is of the same number class as the argument.
   */
  public MNumber evaluate(MNumber x) {
  		MNumber y = NumberFactory.newInstance(x.getClass());
  		evaluate(x, y);
  		return y;
  }

  /**
   * Negates this operation (i.e. putting a "-" before its expression).
   * @return this operation
   */
  public Operation negate() {
    m_opRoot.negateFactor();
    return this;
  }

  /**
   * Returns the negated operation of this operation without changing
   * this operation.
   * @see #negate
   */
  public Operation negated() {
    return new Operation(this).negate();
  }

  /**
   * Conjugates this operation (i.e. putting a "conj()" around its expression).
   * @return this operation
   */
  public Operation conjugate() {
    m_opRoot = new ConjOp(m_opRoot);
    return this;
  }

  /**
   * Returns the conjugated operation of this operation without changing
   * this operation.
   * @see #conjugate
   */
  public Operation conjugated() {
    return new Operation(this).conjugate();
  }

  /**
   * Makes this operation the absolute of this operation (i.e. putting a "abs()" around its expression).
   * @return this operation
   */
  public Operation abs() {
    m_opRoot = new AbsOp(m_opRoot);
    return this;
  }

  /**
   * Returns the "absed" operation of this operation without changing
   * this operation.
   * @see #abs
   */
  public Operation absed() {
    return new Operation(this).abs();
  }

  /**
   * Negates the exponent of this operation (i.e. inverting it).
   * @return this operation
   */
  public Operation negateExponent() {
    m_opRoot.negateExponent();
    return this;
  }

  /**
   * Returns the inverted operation of this operation without changing
   * this operation.
   * @see #negateExponent
   */
  public Operation negatedExponent() {
    return new Operation(this).negateExponent();
  }

  /**
   * Sets this operation to its nth root.
   * @return this operation
   */
  public Operation root(int n){
    m_opRoot = new NrtOp(m_opRoot, n);
    return this;
  }

  /**
   * Returns the rooted operation of this operation without changing
   * this operation.
   * @see #conjugate
   */
  public Operation rooted(int n) {
    return new Operation(this).root(n);
  }


  /**
   *  Returns a List with all nodes of type <code>VariableOp</code>
   *  used for the insertion of values
   */
  public void putVariableOps(String identifier, List variables) {
    if (m_opRoot instanceof VariableOp
      && ((VariableOp) m_opRoot).getIdentifier().equals(identifier))
      variables.add(m_opRoot);
    walkChildren(variables, m_opRoot, identifier);
  }

  /**
   *  Recursively walks through the Operation Tree (in depth order), inserting
   *  instances of type <code>VariableOp</code> into <code>parent</code>
   */
  private void walkChildren(List retVal, OpNode parent, String identifier) {
    OpNode children[] = parent.getChildren();
    if (children != null)
      for (int i = 0; i < children.length; i++) {
        if (children[i] instanceof VariableOp
          && ((VariableOp) children[i]).getIdentifier().equals(identifier))
          retVal.add(children[i]);
        walkChildren(retVal, children[i], identifier);
      }
  }

  /**
   *  Returns the result for a given set of values assigned to the used variables
   *  (alphabetically sorted).
   *  @throws IllegalArgumentException if not all the variables are assigend
   *  with values.
   */
  public MNumber evaluate(MNumber[] assignedValues) {

    HashSet usedVariablesSet = new HashSet();
    getUsedVariables(usedVariablesSet);
    if (usedVariablesSet.size() == 0)
      return evaluate(new HashMap());
    //System.out.println(toString()+": used variables are "+ usedVariablesSet);
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
   *  Returns the result for the given set of values assigned to variables
   *  @throws IllegalArgumentException if not all the variables are assigned
   *  with values.
   */
  public MNumber evaluate(HashMap variableValues) {
    // determine all used variables
    HashSet usedVariables = new HashSet();
    getUsedVariables(usedVariables);

    // assign each value of used variables with the value specified in variableValues
    for (Iterator i = usedVariables.iterator(); i.hasNext();) {
      String variable = (String) i.next();
      MNumber value = (MNumber) variableValues.get(variable);
      if (value == null)
        throw new IllegalArgumentException("unassigned variable " + variable);
      assignValue(variable, value);
    }
    return getResult();
  }

  /**
   * Sets the given identifier as constant in this expression.
   */
  public void setConstant(String identifier) {
    m_tmpVariableOps.clear();
    putVariableOps(identifier, m_tmpVariableOps);
    Iterator iterator = m_tmpVariableOps.iterator();
    while (iterator.hasNext()) {
      ((VariableOp) iterator.next()).setConstant();
    }
    m_usedVariables = new HashSet();
  }

  /**
   * Sets the given identifier as parameter in this expression.
   */
  public void setParameter(String identifier) {
    m_tmpVariableOps.clear();
    putVariableOps(identifier, m_tmpVariableOps);
    Iterator iterator = m_tmpVariableOps.iterator();
    while (iterator.hasNext()) {
      ((VariableOp) iterator.next()).setParameter();
    }
    m_usedVariables = new HashSet();
  }

  /**
   * Sets the given identifier as variable in this expression.
   */
  public void setVariable(String identifier) {
    m_tmpVariableOps.clear();
    putVariableOps(identifier, m_tmpVariableOps);
    Iterator iterator = m_tmpVariableOps.iterator();
    while (iterator.hasNext()) {
      ((VariableOp) iterator.next()).setVariable();
    }
    m_usedVariables = new HashSet();
  }

  /**
   *  Returns true if the operation has a root that is a leaf that has no internal factor or exponent.
   */
  public boolean isIsolated(){
    if(m_opRoot.isLeaf() && m_opRoot.getExponent() == 1 && m_opRoot.getFactor().getDouble() == 1)
      return true;
    return false;
  }

  /**
   *  Sets this operation as the sum of this and <code>anOperation</code>.
   */
  public Operation addTo(Operation anOperation) {
    OpNode node1 = getOpRoot();
    OpNode node2 = anOperation.getOpRoot().deepCopy(false, false);
    OpNode newRoot = new AddOp(getNumberClass());
    newRoot.setChildren(new OpNode[] { node1, node2 });
    setOpRoot(newRoot);
    if (m_normalize)
      normalize();
    return this;
  }

  /** Returns the sum of the two operations */
  public static Operation add(
    Operation operation1,
    Operation operation2) {
    Operation result = new Operation(operation1);
    result.addTo(operation2);
    return result;
  }

  /**
   *  Sets this operation as the difference of this and <code>anOperation</code>.
   */
  public Operation subFrom(Operation anOperation) {
    OpNode node1 = getOpRoot();
    OpNode node2 = anOperation.getOpRoot().deepCopy(false, false);
    node2.negateFactor();
    OpNode newRoot = new AddOp(getNumberClass());
    newRoot.setChildren(new OpNode[] { node1, node2 });
    setOpRoot(newRoot);
    if (m_normalize)
      normalize();
    return this;
  }

  /** Returns the difference of the two operations */
  public static Operation sub(
    Operation operation1,
    Operation operation2) {
    Operation result = new Operation(operation1);
    result.subFrom(operation2);
    return result;
  }

  /**
   *  Sets this operation as the product of this and <code>anOperation</code>.
   */
  public Operation mult(Operation anOperation) {
    OpNode node1 = getOpRoot();
    OpNode node2 = anOperation.getOpRoot().deepCopy(false, false);
    OpNode newRoot = new MultOp(getNumberClass());
    newRoot.setChildren(new OpNode[] { node1, node2 });
    setOpRoot(newRoot);
    if (m_normalize)
      normalize();
    return this;
  }

  /** Returns the product of the two operations */
  public static Operation mult(
    Operation operation1,
    Operation operation2) {
    Operation result = new Operation(operation1);
    result.mult(operation2);
    return result;
  }


  /**
   *  Sets this operation as the division of this and <code>anOperation</code>.
   */
  public Operation divBy(Operation anOperation) {
    OpNode node1 = getOpRoot();
    OpNode node2 = anOperation.getOpRoot().deepCopy(false, false);
    node2.negateExponent();
    OpNode newRoot = new MultOp(getNumberClass());
    newRoot.setChildren(new OpNode[] { node1, node2 });
    setOpRoot(newRoot);
    if (m_normalize)
      normalize();
    return this;
  }

  /** Returns the division of the two operations */
  public static Operation div(
    Operation operation1,
    Operation operation2) {
    Operation result = new Operation(operation1);
    result.divBy(operation2);
    return result;
  }

  /**
   *  Two operations are considered equal, when their string representations are equal.
   */
  public boolean equals(Object object){
    if(!(object instanceof Operation))
      return false;
    return toString().equals(((Operation)object).toString());
  }

  /**
   *  Checks two operations for equality by using an algebraical approach.
   *  Returns true, if the operations are equal, false otherwise.
   */
  public static boolean compareOperations(Operation op1, Operation op2){
    //System.out.println("comparing "+op1+", "+op2);
    // first test for algebraic equality with respect to normalization
    Operation testOp = new Operation(op1.getNumberClass(), op1+"-("+op2+")", true);
    //System.out.println("testing "+testOp);
    if(testOp.toString().equals("0"))
      return true;
    return false;
  }


  /**
   *  Checks two operations for equality by using an numerical approach.
   *  Returns true, if |op1(x)-op2(x)| < 1e-8 for 1000 different x between lowerLimit and upperLimit,
   *  false otherwise.
   */
  public static boolean compareOperationsNumerically(Operation op1, Operation op2, double lowerLimit, double upperLimit){
    return compareOperationsNumerically(op1, op2, lowerLimit, upperLimit, 1000, 1e-8);
  }

  /**
   *  Checks two operations for equality by using an numerical approach.
   *  Returns true, if |op1(x)-op2(x)| < epsilon for a number of steps equidistant x between
   *  lowerLimit and upperLimit, false otherwise.
   */
  public static boolean compareOperationsNumerically(Operation op1, Operation op2, double lowerLimit, double upperLimit, int steps, double epsilon){
    double x = lowerLimit;
		// set other variables than "x" to random values
		String[] vars1 = op1.getUsedVariables();
	  String[] vars2 = op2.getUsedVariables();
	  if(vars1.length != vars2.length)
		  return false;
		for(int i = 0;i<vars1.length;i++){
	  	if(!vars1[i].equals(vars2[i]))
		  	return false;
	  	if(vars1[i].equals("x"))
		  	continue;
	  	double random = Math.random();
			op1.assignValue(vars1[i], random);
			op2.assignValue(vars1[i], random);
		}

    for(int i=0;i<steps;i++){
      x = lowerLimit + (upperLimit-lowerLimit)/(steps-1)*i;
      double result1 = op1.evaluate(x);
      double result2 = op2.evaluate(x);
      if((Double.isNaN(result1) && !Double.isNaN(result2))
       || (Double.isInfinite(result1) && !Double.isInfinite(result2))
       || ((!Double.isNaN(result1) || !Double.isInfinite(result1)) && Math.abs(result1 - result2) > epsilon))
        return false;
    }
    return true;
  }

  /** Returns true, if operation is the zero operation. */
  public boolean isZero(){
    return compareOperations(this, new Operation(getNumberClass(),"0",true));
  }

  /** Returns true, if operation evaluates to zero. */
  public boolean isNumericallyZero(){
    return Math.abs(m_opRoot.getResultDouble()) < 1e-8;
  }

  public static Operation[] getNewOpArray(Class numberClass, int numberOfEntries, boolean normalize){
    Operation[] retVal = new Operation[numberOfEntries];
    for(int i=0;i<retVal.length;i++){
      retVal[i] = new Operation(numberClass, "0", normalize);
    }
    return retVal;
  }

  public Operation deepCopy(){
    return new Operation(this);
  }

  public double getDouble(){
    return m_opRoot.getResultDouble();
  }

  public void setDouble(double x){
    MNumber value = NumberFactory.newInstance(getNumberClass(), x);
    set(value);
  }
  
	public boolean isEdited() {
		return m_isEdited;
	}

	public void setEdited(boolean edited) {
		m_isEdited = edited;
	}

	public String getLabel() {
		return m_label;
	}
	
	public void setLabel(String label) {
		m_label = label;
	}

	public boolean isHidden() {
		return m_hidden;
	}
	
	public void setHidden(boolean hidden) {
		m_hidden = hidden;
	}
}
