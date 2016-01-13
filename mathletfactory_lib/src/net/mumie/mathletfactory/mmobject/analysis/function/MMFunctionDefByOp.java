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

package net.mumie.mathletfactory.mmobject.analysis.function;

import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import net.mumie.mathletfactory.action.ActionManager;
import net.mumie.mathletfactory.action.message.SpecialCaseEvent;
import net.mumie.mathletfactory.display.PolygonDisplayProperties;
import net.mumie.mathletfactory.math.algebra.op.OpParser;
import net.mumie.mathletfactory.math.algebra.op.OpTransform;
import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.algebra.op.UsesOperationIF;
import net.mumie.mathletfactory.math.algebra.op.rule.normalize.RemoveNeutralElementRule;
import net.mumie.mathletfactory.math.algebra.rel.RelTransform;
import net.mumie.mathletfactory.math.algebra.rel.Relation;
import net.mumie.mathletfactory.math.algebra.rel.node.SimpleRel;
import net.mumie.mathletfactory.math.analysis.function.FunctionOverBorelSetIF;
import net.mumie.mathletfactory.math.analysis.function.FunctionOverRAdapter;
import net.mumie.mathletfactory.math.analysis.function.FunctionOverRIF;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.MOpNumber;
import net.mumie.mathletfactory.math.set.FiniteBorelSet;
import net.mumie.mathletfactory.math.set.Interval;
import net.mumie.mathletfactory.math.set.NumberSetIF;
import net.mumie.mathletfactory.mmobject.MMDefaultCanvasObject;
import net.mumie.mathletfactory.mmobject.set.MMSetDefByRel;
import net.mumie.mathletfactory.transformer.GeneralTransformer;
import net.mumie.mathletfactory.util.xml.ExerciseObjectFactory;
import net.mumie.mathletfactory.util.xml.ExerciseObjectIF;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * This class represents a monovariate function over a borel set that is defined by an
 * {@link net.mumie.mathletfactory.math.algebra.op.Operation}.
 *
 * @author Paehler
 * @mm.docstatus finished
 */

public class MMFunctionDefByOp
  extends MMDefaultCanvasObject
  implements
    FunctionOverBorelSetIF,
    MMFunctionAndDerivativeOverRIF,
    UsesOperationIF,
    MMOneChainInRIF,
		ExerciseObjectIF {

  private Class m_numberClass;

	/**	This field holds the current <code>hidden</code> flag. */
	private boolean m_hidden = false;

  private int m_vertices_count_in_drawable = 4000;
    //MMOneChainInRIF.DEFAULT_VERTICES_COUNT;

  private boolean m_BoundaryVisibility = true;

  /** The set this function is defined for. */
  protected FiniteBorelSet m_borelSet;

  private Vector m_variableOps;

  private String m_variableId = "x";

  private Operation m_operation = null;
  
  private boolean m_normalize = true;

  private Map m_parameters = new HashMap();

  private boolean renderParamsInLabel = true;

	/** Constructs the function from the given number class, expression and domain. */
  public MMFunctionDefByOp(
    Class numberClass,
    String stringFunction,
    Interval interval) {
    this(numberClass, stringFunction, new FiniteBorelSet(interval));
  }

  /** Constructs the function from the given number class, expression and domain. */
  public MMFunctionDefByOp(
    Class numberClass,
    String stringFunction,
    Interval[] intervals) {
    this(numberClass, stringFunction, new FiniteBorelSet(intervals));
  }

  /** Constructs the function from the given number class, expression and domain. */
  public MMFunctionDefByOp(
    Class numberClass,
    String stringFunction,
    FiniteBorelSet borelDomain) {
	if (numberClass.isAssignableFrom(MOpNumber.class)) throw new IllegalArgumentException("MOpNumbers may not be used");
    m_numberClass = numberClass;
    m_borelSet = borelDomain;
    setOperation(stringFunction);
    setLabel("f");
    setDisplayProperties(new PolygonDisplayProperties());
  }

  /** Constructs the function from the given number class and expression. The domain is the complete real axis. */
  public MMFunctionDefByOp(Class numberClass, String stringExpression) {
    this(
      numberClass,
      stringExpression,
      FiniteBorelSet.getRealAxis(numberClass));
  }

  /** Constructs the function from the given operation and domain. */
  public MMFunctionDefByOp(Operation operation, FiniteBorelSet borelSet) {
    m_borelSet = borelSet;
    m_numberClass = operation.getNumberClass();
    setOperation(operation);
    setLabel("f");
    setDisplayProperties(new PolygonDisplayProperties());
  }

  /** Constructs the function from the given operation and the real-axis domain. */
  public MMFunctionDefByOp(Operation operation) {
  	this(operation, null);
  }
  
  /** Copy constructor. */
  public MMFunctionDefByOp(MMFunctionDefByOp function) {
    this(function.getOperation(), function.getBorelSet());
    if (function instanceof MMFunctionDefByOp) {
      MMFunctionDefByOp mmFunction = (MMFunctionDefByOp) function;
      setDisplayProperties(mmFunction.getDisplayProperties());
    }
    else
      setDisplayProperties(new PolygonDisplayProperties());
  }

	/**
	 * Constructs a new instance with the given MathML/XML content.
	 * @param content a MathML/XML node
   * @throws Exception when an error while instantiation occurrs
	 */
  public MMFunctionDefByOp(Node content) throws Exception {
  	this(ExerciseObjectFactory.getNumberClass(content), "0");
  	setMathMLNode(content);
  }

  /** Listens exclusively for OPERATION events and adjusts its operation to the new value. */
  public void propertyChange(PropertyChangeEvent e) {
    if (!e.getPropertyName().equals(OPERATION))
      return;
    setOperation(new Operation((Operation) e.getNewValue()));
    setEdited(true);
    ActionManager.performActionCycleFromObject(this);
  }

  public FiniteBorelSet getBorelSet() {
    if (m_borelSet == null)
      m_borelSet = FiniteBorelSet.getRealAxis(m_numberClass);
    return m_borelSet;
  }

  public NumberSetIF getDomain() {
    return getBorelSet();
  }

  public Class getNumberClass() {
    return m_numberClass;
  }

  /** Sets the operation for this function. */
  public void setOperation(String expr) {
    setOperation(OpParser.getOperation(getNumberClass(), expr, m_normalize));
  }

  /** Sets the operation for this function. */
  public void setOperation(Operation operation) {
    m_operation = operation;
    setNormalForm(m_normalize);
    /*
    Relation rel = m_operation.getDefinedRelation();
    rel.setNormalForm(Relation.NORMALIZE_OP_SEPARATED);
    try {
      m_borelSet = rel.getTrueForSet();
    } catch(IllegalStateException e){}
    m_borelSet.intersectWith(m_originalSet);
    */
    updateVars();
//    render();
//    invokeUpdaters();
  }

  /** Returns the operation for this function. */
  public Operation getOperation() {
    return m_operation;
  }

  /** Adds a parameter with the specified value to the function. */
  public void setParameter(String parameterName, MNumber value) {
    m_parameters.put(parameterName, value);
    m_operation.setParameter(parameterName);
    m_operation.assignValue(parameterName, value);
    setOperation(m_operation);
  }
  
  /** Returns all parameters with values as a map. */
  public Map getParameters() {
    return m_parameters;
  }

  public void setBorelSet(FiniteBorelSet aBorelDomain) {
    m_borelSet = aBorelDomain;
    /*
    Relation rel = m_operation.getDefinedRelation();
    rel.setNormalForm(Relation.NORMALIZE_OP_SEPARATED);
    try {
      m_borelSet = rel.getTrueForSet(1e-3, new Interval(getNumberClass(),"(-10;10)"));
    } catch(IllegalStateException e){}
    m_borelSet.intersect(aBorelDomain);
    */
  }

  private void updateVars() {
    m_variableOps = new Vector();
    m_operation.putVariableOps(m_variableId, m_variableOps);
    // check for other variables than m_variable and mark them as parameters
    String[] variables = m_operation.getUsedVariables();
    for (int i = 0; i < variables.length; i++) {
      if (!variables[i].equals(m_variableId)) {
        m_operation.setParameter(variables[i]);
        m_operation.assignValue(variables[i],
          (MNumber) m_parameters.get(variables[i]));
      }
    }
  }

  public double evaluate(double x) {
    return m_operation.evaluate(x, m_variableId);
  }

  public void evaluate(double[] xin, double[] yout) {
    for (int i = 0; i < xin.length; i++)
      yout[i] = evaluate(xin[i]);
  }

  public void evaluate(MNumber xin, MNumber yout) {
    yout.setDouble(xin.getDouble());
  }

  public FunctionOverRIF getDerivativeByNumeric() {
    return getDerivativeByNumeric(DEFAULT_DELTA);
  }

  public FunctionOverRIF getDerivativeByNumeric(final double delta) {
    FunctionOverRAdapter derivative =
      new FunctionOverRAdapter(getNumberClass()) {
      public double evaluate(double xin) {
        FunctionOverRIF f = MMFunctionDefByOp.this;
        double result =
          (f.evaluate(xin + delta) - f.evaluate(xin - delta))
            / (2 * delta);
        return result;
      }
    };
    return derivative;
  }

  public void getDerivativeByNumeric(
    double delta,
    MMFunctionDefinedByExpression theDerivative) {
    theDerivative.setFunction(getDerivativeByNumeric(delta));
    theDerivative.setBorelSet(m_borelSet);
  }

  public void getDerivativeByNumeric(MMFunctionDefinedByExpression theDerivative) {
    getDerivativeByNumeric(DEFAULT_DELTA, theDerivative);
  }


  /** Returns the inverse of this function. Its definition range is set accordingly. */
  public MMFunctionDefByOp getInverse() {
    Operation inverseOperation = new Operation(m_operation);
    inverseOperation.substitute(m_variableId, "y");
    SimpleRel functionEquation =
      new SimpleRel(
        inverseOperation,
        new Operation(m_numberClass, m_variableId, true),
        "=",
        Relation.NORMALIZE_OP);
    functionEquation = RelTransform.simpleSeparateFor(functionEquation, "y");
    if (functionEquation.getLeftHandSide().toString().equals("y")
      || functionEquation.getLeftHandSide().toString().equals("|y|"))
      return new MMFunctionDefByOp(
        functionEquation.getRightHandSide(),
        new FiniteBorelSet(m_numberClass));
    fireSpecialCaseEvent(new SpecialCaseEvent(this, "not_invertible"));
    return null;
    //throw new MathContentException("Function could not be inverted!");

  }

  /** Returns the derivative of this function. */
  public MMFunctionDefByOp getDerivative() {
    try {
      return new MMFunctionDefByOp(
        m_operation.getDerivative(m_variableId),
        m_borelSet);
    }
    catch (Exception e) {
      fireSpecialCaseEvent(new SpecialCaseEvent(this, "not_differentiable"));
      return null;
    }
  }

  /** Returns a new function f containing the composition f = g o h  of this function g and the given function h. */
  public MMFunctionDefByOp getComposed(MMFunctionDefByOp composeWith) {
    return new MMFunctionDefByOp(
      m_operation.getComposed(m_variableId, composeWith.getOperation()),
      m_borelSet);
  }

  /** Returns the set for which this function can be maximally defined. */
  public MMSetDefByRel getDefinedSet() {
    MMSetDefByRel set = new MMSetDefByRel(m_operation.getDefinedRelation());
    set.setStdDimension(MMSetDefByRel.STD_ONE_DIMENSIONAL_SET);
    return set;
  }

  /** Returns the relation that must be true for a member of the definition range.*/
  public Relation getDefinedRelation() {
    return m_operation.getDefinedRelation();
  }

  public int getDefaultTransformTypeAsContainer() {
    return GeneralTransformer.FUNCTION_TRANSFORM;
  }

  public int getDefaultTransformType() {
    return GeneralTransformer.ONE_CHAIN_1D_AFFINE_GRAPH_TRANSFORM;
  }

  public int getDefaultTransformTypeInCanvas() {
    return GeneralTransformer.ONE_CHAIN_1D_AFFINE_GRAPH_TRANSFORM;
  }

  public Rectangle2D getWorldBoundingBox() {
    return null;
  }

  public int getVerticesCount() {
    return m_vertices_count_in_drawable;
  }

  public void setVerticesCount(int aNumber) {
    m_vertices_count_in_drawable = aNumber;
  }

  public void setBoundarysVisible(boolean aFlag) {
    m_BoundaryVisibility = aFlag;
  }

  public boolean areBoundarysVisible() {
    return m_BoundaryVisibility;
  }

  /**
   * Returns this object, since the function has only one component.
   *
   * @see net.mumie.mathletfactory.math.analysis.function.OneChainInRIF#getEvaluateExpressionInComponent(int)
   */
  public FunctionOverRIF getEvaluateExpressionInComponent(int index) {
    return this;
  }

  /**
   * Returns the domain of this function, since the function has only one component.
   *
   * @see net.mumie.mathletfactory.math.analysis.function.OneChainInRIF#getEvaluateExpressionInComponent(int)
   */
  public FiniteBorelSet getBorelSetInComponent(int index) {
    return getBorelSet();
  }

  /**
   * Returns 1, since the function has only one component.
   *
   * @see net.mumie.mathletfactory.math.analysis.function.OneChainInRIF#getComponentsCount()
   */
  public int getComponentsCount() {
    return 1;
  }

  public int getAllIntervalCount() {
    return getBorelSet().getIntervalCount();
  }

  public Interval getInterval(
    int componentCount,
    int intervalIndexInBorelSet) {
    return getBorelSet().getInterval(intervalIndexInBorelSet);
  }

  public int getIntervalCountInComponent(int index) {
    return getBorelSet().getIntervalCount();
  }

  /** Returns the name of the computation variable. Default is "x". */
  public String getVariableId() {
    return m_variableId;
  }

  /** Sets the name of the computation variable. Default is "x". */
  public void setVariableId(String string) {
    m_variableId = string;
    m_operation.setVariable(string);
  }

  /** Returns a String that looks like <code>f(x) = [expression], D(f) = [domain]</code> */
  public String toString() {
    return getLabel()
      + "("
      + getVariableId()
      + ") = "
      + m_operation.toString()
      + ", D("
      + getLabel()
      + ") = "
      + getBorelSet().toString();
  }

  /** Returns the primitive G(x) = int_0^x g(t) dt where g is this function */
  public FunctionOverRIF getPrimitive() {
    return getPrimitive(0.0);
  }

  /** Sets f to the primitive G(x) = int_0^x g(t) dt of this function g */
  public void getPrimitive(MMFunctionDefinedByExpression f) {
    getPrimitive(f, 0.0);
  }

  /** Sets f to the primitive G(x) = int_a^x g(t) dt of this function g */
  public void getPrimitive(MMFunctionDefinedByExpression f, double a) {
    f.setFunction(getPrimitive(a));
    f.setBorelSet(m_borelSet);
  }

  /** Returns the primitive G(x) = int_a^x g(t) dt where g is this function */
  public FunctionOverRIF getPrimitive(double a) {
    final double A = a;
    final FunctionOverRIF f = MMFunctionDefByOp.this;
    final MMStepFunction F =
      new MMStepFunction((FunctionOverBorelSetIF) f, 200);
    FunctionOverRAdapter primitive =
      new FunctionOverRAdapter(getNumberClass()) {
      public double evaluate(double xin) {
        F.setType(MMStepFunction.MEAN);
        F.approximate(f, A, xin, 200);
        double result = F.integral();
        return result;
      }
    };
    return primitive;
  }

  public String getLabel(){
    int p_size = getParameters().size();
    String pStr = "";
    if(p_size > 0 && renderParamsInLabel){
      String[] params = (String[])m_parameters.keySet().toArray(new String[p_size]);
      pStr += "_{";
      for(int i=0;i<params.length;i++){
        pStr += params[i];
      }
      pStr += "}";
    }
    //System.out.println(pStr);
    return super.getLabel() + pStr;
  }

  /**
   * Returns, whether parameters are rendered as substrings in the function label.
   */
  public boolean isRenderParamsInLabel() {
    return renderParamsInLabel;
  }

  /**
   * Sets, whether parameters are rendered as substrings in the function label.
   */
  public void setRenderParamsInLabel(boolean b) {
    renderParamsInLabel = b;
  }

  public Node getMathMLNode() {
    return getMathMLNode(XMLUtils.getDefaultDocument());
  }

	/*
	 * @see net.mumie.mathletfactory.util.xml.MathMLSerializable#getMathMLNode()
	 */
	public Node getMathMLNode(Document doc) {
		return ExerciseObjectFactory.exportExerciseAttributes(getOperation().getMathMLNode(doc), this);
	}
	/*
	 * @see net.mumie.mathletfactory.util.xml.MathMLSerializable#setMathMLNode(org.w3c.dom.Node)
	 */
	public void setMathMLNode(Node content) {
		getOperation().setMathMLNode(content);
		ExerciseObjectFactory.importExerciseAttributes(content, this);
	}
	
	public boolean isEdited() {
		return m_operation.isEdited();
	}

	public void setEdited(boolean edited) {
		m_operation.setEdited(edited);
	}
	
	public boolean isHidden() {
		return m_hidden;
	}
	
	public void setHidden(boolean hidden) {
		m_hidden = hidden;
	}

	public void setNormalForm(boolean normalize) {
		m_normalize = normalize;
		m_operation.setNormalForm(normalize);
		if (!m_normalize)
			m_operation.setOpRoot(OpTransform.applyRule(m_operation.getOpRoot(), new RemoveNeutralElementRule()));
	}
	
	public boolean isNormalForm() {
		return m_operation.isNormalForm();
	}
}
