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

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import net.mumie.mathletfactory.action.ActionManager;
import net.mumie.mathletfactory.action.updater.DependencyAdapter;
import net.mumie.mathletfactory.display.PolygonDisplayProperties;
import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.analysis.function.FunctionOverBorelSetIF;
import net.mumie.mathletfactory.math.analysis.function.FunctionOverRAdapter;
import net.mumie.mathletfactory.math.analysis.function.FunctionOverRIF;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.set.FiniteBorelSet;
import net.mumie.mathletfactory.math.set.Interval;
import net.mumie.mathletfactory.math.set.NumberSetIF;
import net.mumie.mathletfactory.mmobject.MMDefaultCanvasObject;
import net.mumie.mathletfactory.mmobject.set.MMInterval;
import net.mumie.mathletfactory.mmobject.util.IndexedEntry;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 * This class represents functions that are piecewise defined by 
 * {@link net.mumie.mathletfactory.math.algebra.op.Operation}s, each for 
 * a different interval.
 *  
 * @author Paehler
 * @mm.docstatus finished
 */
public class MMPiecewiseFunction
  extends MMDefaultCanvasObject
  implements MMOneChainInRIF, MMFunctionAndDerivativeOverRIF, FunctionOverBorelSetIF {

  private Class m_numberClass;

  private Vector m_variableXOps;

  private Operation[] m_operations = new Operation[0];

  private MMInterval[] m_intervals = new MMInterval[0];

  private Map m_parameters = new HashMap();

  private boolean m_hasOverlappingIntervals;


  private boolean m_isEditable = false;

  private int m_vertices_count_in_drawable = 2000;
    //MMOneChainInRIF.DEFAULT_VERTICES_COUNT;

  private boolean m_BoundaryVisibility = true;

  /** 
   * Constructs a piecewise function from the given number class and expressions, each defined 
   * on the corresponding interval with the same array index. 
   */
  public MMPiecewiseFunction(
    Class numberClass,
    String[] stringFunctions,
    MMInterval[] intervals) {
    m_numberClass = numberClass;
    setOperations(stringFunctions, intervals);
    setIntervals(intervals);
    PolygonDisplayProperties p = new PolygonDisplayProperties();
    p.setFilled(false);
    setDisplayProperties(p);
    setLabel("f");
  }

  /** 
   * Constructs a piecewise function from the given operations, each defined 
   * on the corresponding interval with the same array index. 
   */
  public MMPiecewiseFunction(Operation[] operations, MMInterval[] intervals) {
    m_numberClass = operations[0].getNumberClass();
    m_operations = operations;
    setIntervals(intervals);
    PolygonDisplayProperties p = new PolygonDisplayProperties();
    p.setFilled(false);
    setDisplayProperties(p);
    setLabel("f");
  }

  /** Copy constructor */
  public MMPiecewiseFunction(MMPiecewiseFunction function) {
    this(function.getOperations(), function.getIntervals());
    setIntervals(getIntervals());
    MMPiecewiseFunction mmFunction = (MMPiecewiseFunction) function;
    setDisplayProperties(mmFunction.getDisplayProperties());
    setLabel(mmFunction.getLabel());
  }

  public void setBorelSet(FiniteBorelSet set){
    throw new UnsupportedOperationException("Use setIntervals() instead!");
  }

  public FiniteBorelSet getBorelSet() {
    return new FiniteBorelSet(m_intervals);
  }

  public NumberSetIF getDomain() {
    return getBorelSet();
  }

  public Class getNumberClass() {
    return m_numberClass;
  }

  /** Sets the expressions and their corresponding domain intervals. */
  public void setOperations(String[] exprs, MMInterval[] intervals) {
    if (intervals.length != exprs.length)
      throw new IllegalArgumentException("Intervals must have the same length as exprs!");
    setIntervals(intervals);
    m_operations = new Operation[exprs.length];
    for (int i = 0; i < exprs.length; i++)
      m_operations[i] = new Operation(getNumberClass(), exprs[i], true);
    render();
  }

  /** Sets the operations and their corresponding domain intervals. */
  public void setOperations(Operation[] operations, MMInterval[] intervals) {
    m_operations = operations;
    setIntervals(intervals);
    update();
  }

  /** 
   * Returns the operations used by this piecewise function. The order of the array corresponds 
   * to that returned by {@link #getIntervals}. 
   */
  public Operation[] getOperations() {
    return m_operations;
  }

  /** Sets the <i>i</i>-th operation of this piecewise function. */
  public void setOperation(int i, Operation operation) {
    m_operations[i] = operation;
  }

  /** Returns the <i>i</i>-th operation of this piecewise function. */
  public Operation getOperation(int i) {
    return m_operations[i];
  }

  /** Returns the <i>i</i>-th interval of this piecewise function. */
  public MMInterval getInterval(int i) {
    return m_intervals[i];
  }

  /** Returns all parameters with values as a map. */
  public Map getParameters() {
    return m_parameters;
  }

  /**
   *  Returns the intervals on which the different operations are defined. The order corresponds to that
   *  of {@link #getOperations}. 
   */
  public MMInterval[] getIntervals() {
    return m_intervals;
  }

  /** Adds a one character parameter with the specified value to the function. */
  public void setParameter(String parameterName, MNumber value) {
    if (!getParameters().containsKey(parameterName))
      setLabel(getLabel() + "_" + parameterName);
    m_parameters.put(parameterName, value);
    for (int i = 0; i < m_operations.length; i++) {
      m_operations[i].setParameter(parameterName);
      m_operations[i].assignValue(parameterName, value);
    }
  }

  /** Listens exclusively for OPERATION and INTERVAL events and adjusts itself to the new value. */
  public void propertyChange(PropertyChangeEvent e) {
    if (e.getPropertyName().equals(INDEXED_ENTRY)) {
//      for (int i = 0; i < getOperations().length; i++)
//        if (getOperation(i) == e.getOldValue()) {
//          //System.out.println("replacing "+e.getOldValue()+" with "+ e.getNewValue());
//          setOperation(i, (Operation) e.getNewValue());
//        }
    	IndexedEntry newValue = (IndexedEntry) e.getNewValue();
    	setOperation(newValue.getIndex(), (Operation) newValue.getValue());
      ActionManager.performActionCycleFromObject(this);
      return;
    }
    if (e.getPropertyName().equals(INTERVAL)
      || e.getPropertyName().equals(INTERVAL_LOW_BOUND_TYPE)
      || e.getPropertyName().equals(INTERVAL_UP_BOUND_TYPE)) {
            for (int i = 0; i < getIntervals().length; i++)
              if (getInterval(i).equals(e.getOldValue()))
                setInterval(i, new MMInterval((Interval) e.getNewValue()));
      ActionManager.performActionCycleFromObject(this);
      checkIntervals();
    }
  }

  /** Adds an operation and its domain interval to the list. */
  public void addExpression(Operation operation, MMInterval interval) {
    interval.setDisplayProperties(getDisplayProperties());
    interval.setEditable(m_isEditable);
    Operation[] newOperations = new Operation[getOperations().length + 1];
    MMInterval[] newIntervals = new MMInterval[getIntervals().length + 1];
    System.arraycopy(
      getOperations(),
      0,
      newOperations,
      0,
      getOperations().length);
    System.arraycopy(getIntervals(), 0, newIntervals, 0, getIntervals().length);
    newOperations[getOperations().length] = operation;
    newIntervals[getIntervals().length] = interval;
    setOperations(newOperations, newIntervals);
    for (int i = 0; i < getIntervals().length; i++)
       ((MMInterval) getInterval(i)).setEditable(m_isEditable);
    ActionManager.performActionCycleFromObject(this);
  }

  /** Removes the last operation and its domain interval to the list. */
  public void removeLastExpression() {
    Operation[] newOperations = new Operation[getOperations().length - 1];
    MMInterval[] newIntervals = new MMInterval[getIntervals().length - 1];
    System.arraycopy(
      getOperations(),
      0,
      newOperations,
      0,
      getOperations().length - 1);
    System.arraycopy(
      getIntervals(),
      0,
      newIntervals,
      0,
      getIntervals().length - 1);
    setOperations(newOperations, newIntervals);
    ActionManager.performActionCycleFromObject(this);
  }

  /** Replaces the <i>i</i>-th interval with the argument. */
  public void setInterval(int i, MMInterval interval) {
    m_intervals[i] = interval;
    checkIntervals();
    if (getInteractionHelper() == null)
      return;
    dependsOn(((MMInterval) interval), new DependencyAdapter() {
      public void doUpdate() {
        checkIntervals();
      }
    });
  }

  /**
   * Sets the intervals corresponding to the operations of this piecewise function. Note, that
   * the array should consist of disjoint intervals and has the same length as the one returned by
   * {@link #getOperations}.
   */
  public void setIntervals(MMInterval[] intervals) {
    m_intervals = intervals;
    checkIntervals();
    if (getInteractionHelper() == null)
      return;
    for (int i = 0; i < intervals.length; i++) {
      dependsOn(((MMInterval) intervals[i]), new DependencyAdapter() {
        public void doUpdate() {
          checkIntervals();
        }
      });
    }
  }

  /**
   * Checks if intervals are disjoint. Note that no exception is thrown if they do, but only 
   * an information for the user is displayed by its symbolic representation. 
   */
  public void checkIntervals() {
    if (m_hasOverlappingIntervals
      != Interval.overlappingIntervals(m_intervals)) {
      m_hasOverlappingIntervals = Interval.overlappingIntervals(m_intervals);
      render();
    }
  }

  /** Returns the derivative of this function. */
  public MMPiecewiseFunction getDerivative() {
    MMInterval[] dIntervals = new MMInterval[m_intervals.length];
    Operation[] dOperations = new Operation[m_operations.length];
    for (int i = 0; i < dIntervals.length; i++) {
      dIntervals[i] = new MMInterval(m_intervals[i]);
      dOperations[i] = new Operation(m_operations[i]);
    }
    return new MMPiecewiseFunction(dOperations, dIntervals);
  }
  
  private void update() {
    m_variableXOps = new Vector();
    for (int i = 0; i < m_operations.length; i++) {
      m_operations[i].putVariableOps("x", m_variableXOps);
      // check for other variables than "x"
      String[] variables = m_operations[i].getUsedVariables();
      for (int j = 0; j < variables.length; j++) {
        if (!variables[j].equals("x")) {
          m_operations[i].setConstant(variables[j]);
          m_operations[i].assignValue(
            variables[j],
            (MNumber) m_parameters.get(variables[j]));
        }
      }
    }
  }

  public double evaluate(double x) {
    for (int i = 0; i < m_intervals.length; i++)
      if (m_intervals[i].contains(x))
        return m_operations[i].evaluate(x);
    return Double.NaN;
  }

  public void evaluate(double[] xin, double[] yout) {
    for (int i = 0; i < xin.length; i++)
      yout[i] = evaluate(xin[i]);
  }

  public void evaluate(MNumber xin, MNumber yout) {
    for (int i = 0; i < m_intervals.length; i++)
      if (m_intervals[i].contains(xin))
        m_operations[i].evaluate(xin, yout);
  }

  public FunctionOverRIF getDerivativeByNumeric() {
    return getDerivativeByNumeric(DEFAULT_DELTA);
  }

  public FunctionOverRIF getDerivativeByNumeric(final double epsilon) {
    FunctionOverRAdapter derivative =
      new FunctionOverRAdapter(getNumberClass()) {
      public double evaluate(double xin) {
        FunctionOverRIF f = MMPiecewiseFunction.this;
        double result =
          (f.evaluate(xin + epsilon) - f.evaluate(xin - epsilon))
            / (2 * epsilon);
        return result;
      }
    };
    return derivative;
  }

  public int getDefaultTransformType() {
    return GeneralTransformer.ONE_CHAIN_1D_AFFINE_GRAPH_TRANSFORM;
  }

  public int getDefaultTransformTypeInCanvas() {
    return GeneralTransformer.ONE_CHAIN_1D_AFFINE_GRAPH_TRANSFORM;
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

  public FunctionOverRIF getEvaluateExpressionInComponent(int index) {
    return this;
  }

  public FiniteBorelSet getBorelSetInComponent(int index) {
    return new FiniteBorelSet(m_intervals[index]);
  }

  public int getComponentsCount() {
    return m_operations.length;
  }

  public int getAllIntervalCount() {
    return m_intervals.length;
  }

  public Interval getInterval(
    int expressionCount,
    int intervalIndexInBorelSet) {
    return getInterval(expressionCount);
  }

  public int getIntervalCountInComponent(int index) {
    return 1;
  }

  public int getDefaultTransformTypeAsContainer() {
    return GeneralTransformer.FUNCTION_TRANSFORM;
  }

  public void getDerivativeByNumeric(
    double epsilon,
    MMFunctionDefinedByExpression theDerivative) {
    theDerivative.setFunction(getDerivativeByNumeric(epsilon));
  }

  public void getDerivativeByNumeric(MMFunctionDefinedByExpression theDerivative) {
    getDerivativeByNumeric(0, theDerivative);
  }

  public boolean isEditable() {
    return m_isEditable;
  }

  public void setEditable(boolean editable) {
    if (editable == m_isEditable)
      return;
    m_isEditable = editable;
    for (int i = 0; i < getIntervals().length; i++)
      getIntervals()[i].setEditable(editable);
    render();
  }

  /** Returns true if the intervals on which the operations are defined are overlapping. */
  public boolean hasOverlappingIntervals() {
    return m_hasOverlappingIntervals;
  }

  /**
   * Returns the primitive G(x) = int_0^x g(t) dt where g is this function
   */
  public FunctionOverRIF getPrimitive() {
    return getPrimitive(0.0);
  }

  /**
   * Sets f to the primitive G(x) = int_0^x g(t) dt of this function g
   */
  public void getPrimitive(MMFunctionDefinedByExpression f) {
    getPrimitive(f, 0.0);
  }

  /**
   * Sets f to the primitive G(x) = int_a^x g(t) dt of this function g
   */
  public void getPrimitive(MMFunctionDefinedByExpression f, double a) {
    f.setFunction(getPrimitive(a));
  }

  /**
   * Returns the primitive G(x) = int_a^x g(t) dt where g is this function
   */
  public FunctionOverRIF getPrimitive(double a) {
    final double A = a;
    final FunctionOverRIF f = MMPiecewiseFunction.this;
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
}
