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

package net.mumie.mathletfactory.mmobject.analysis.function.multivariate;
import java.beans.PropertyChangeEvent;
import java.util.logging.Logger;

import javax.swing.event.ChangeEvent;

import net.mumie.mathletfactory.action.ActionManager;
import net.mumie.mathletfactory.action.MMEvent;
import net.mumie.mathletfactory.display.SurfaceDisplayProperties;
import net.mumie.mathletfactory.math.algebra.op.OpTransform;
import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.algebra.op.UsesOperationIF;
import net.mumie.mathletfactory.math.algebra.op.rule.normalize.RemoveNeutralElementRule;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.MOpNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.math.number.NumberTypeDependentIF;
import net.mumie.mathletfactory.mmobject.MMDefaultCanvasObject;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 *  This class takes a string representing an arithmetic expression of
 *  x and y, a pair of ranges and performs the calculations for a 
 *  twodimensional grid of elements in this range by feeding the 
 *  Operation Tree (produced by {@link net.mumie.mathletfactory.algebra.op.OpParser})
 *  with the appropriate values.
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class MMFunctionOverR2
  extends MMDefaultCanvasObject
  implements NumberTypeDependentIF, UsesOperationIF {

  /**
   *  The root node of the operation tree, returned by 
   * {@link net.mumie.mathletfactory.algebra.op.OpParser}.
   */
  private Operation m_operation = null;
  
  private boolean m_normalize = true;

  /** The Range on the x-axis */
  private double[] m_xRange = { -1, 1 };

  /** The Range on the y-axis */
  private double[] m_yRange = { -1, 1 };

  /** The number of steps in the grid */
  private int m_steps = 30;

  /** The twodimensional ((steps+1) x (steps+1)) array of R2 Points
   containing the calculated value for the specified grid */
  private MNumber[][] m_fValues;

  /** A flag indicating that {@link #calculate} needs to be called prior to
   {@link #getValues}. */
  private boolean m_recalculationNeeded = true;

  private int m_version;

  private int m_vertices_per_direction_in_drawable = 50;

  private static Logger logger = Logger.getLogger(MMFunctionOverR2.class.getName());

  public MMFunctionOverR2(MMFunctionOverR2 function) {
    this(function.m_operation);
    setDisplayProperties(SurfaceDisplayProperties.DEFAULT);
    setSteps(function.m_steps);
    setRange(function.m_xRange, function.m_yRange);
  }

  public MMFunctionOverR2(Operation operation) {
    setOperation(operation);
    setLabel("f");
    setDisplayProperties(SurfaceDisplayProperties.DEFAULT);
    setSteps(m_vertices_per_direction_in_drawable);
    
  }

  public MMFunctionOverR2(Class numberClass, String expression) {
    this(new Operation(numberClass, expression, true));
    if (numberClass.isAssignableFrom(MOpNumber.class)) throw new IllegalArgumentException("MOpNumbers may not be used");
  }

  /**
   * Constructs the function for the given values. 
   * 
   * @param numberClass the number class used
   * @param expr an expression f(x,y)
   * @param xRange the minimal and maximal x value to be calculated and displayed
   * @param yRange the minimal and maximal y value to be calculated and displayed
   */
  public MMFunctionOverR2(Class numberClass, String expr, double[] xRange, double yRange[]) {
    this(numberClass, expr);
    setRange(xRange, yRange);
  }

  /**
   * Constructs the function for the given values.
   *  
   * @param numberClass the number class used
   * @param expr an expression f(x,y)
   * @param xMin the minimal x value to be calculated and displayed
   * @param xMax the maximal x value to be calculated and displayed
   * @param yMin the minimal y value to be calculated and displayed
   * @param yMax the maximal y value to be calculated and displayed
   */
  public MMFunctionOverR2(
    Class numberClass,
    String expr,
    double xMin,
    double xMax,
    double yMin,
    double yMax) {
    this(numberClass, expr);
    double[] xRange = new double[2], yRange = new double[2];
    xRange[0] = xMin;
    xRange[1] = xMax;
    yRange[0] = yMin;
    xRange[1] = yMax;
    setRange(xRange, yRange);
  }

  /**
   * Constructs the function for the given values.
   *  
   * @param numberClass the number class used
   * @param expr an expression f(x,y)
   * @param xMin the minimal x value to be calculated and displayed
   * @param xMax the maximal x value to be calculated and displayed
   * @param yMin the minimal y value to be calculated and displayed
   * @param yMax the maximal y value to be calculated and displayed
   * @param steps the number of steps in each direction, the function will be evaluated
   * in a <code>steps x steps</code> mesh.
   */
  public MMFunctionOverR2(
    Class numberClass,
    String expr,
    double xMin,
    double xMax,
    double yMin,
    double yMax,
    int steps) {
    this(numberClass, expr);
    double[] xRange = new double[2], yRange = new double[2];
    xRange[0] = xMin;
    xRange[1] = xMax;
    yRange[0] = yMin;
    yRange[1] = yMax;
    setRange(xRange, yRange);
    setSteps(steps);
  }

  /**
   * Constructs the function for the given values. 
   * 
   * @param numberClass the number class used
   * @param expr an expression f(x,y)
   * @param xRange the minimal and maximal x value to be calculated and displayed
   * @param yRange the minimal and maximal y value to be calculated and displayed
   * @param steps the number of steps in each direction, the function will be evaluated
   * in a <code>steps x steps</code> mesh.
   */
  public MMFunctionOverR2(
    Class numberClass,
    String expr,
    double[] xRange,
    double yRange[],
    int steps) {
    this(numberClass, expr);
    setRange(xRange, yRange);
    setSteps(steps);
  }

  /** Sets the operation of the function. */
  public void setOperation(Operation operation) {
    m_operation = operation;
    m_operation.setNormalForm(m_normalize);
    if (getInteractionHelper() != null) {
      doAction(new MMEvent(new ChangeEvent(this), this));
      if (getActiveHandler() != null) {
        getActiveHandler().finish();
        resetActiveHandler();
      }
    }
    update();
  }

  /**
   *  This method sets the expression to be evaluated. It constructs the
   *  corresponding Operation Tree and marks it for recalculation.
   */
  public void setOperation(String expression) {
    setOperation(new Operation(getNumberClass(), expression, m_normalize));
  }

  /**
   * Returns the operation of this function.
   * @see net.mumie.mathletfactory.algebra.op.UsesOperationIF#getOperation()
   */
  public Operation getOperation() {
    return m_operation;
  }

  /**
   * The number class used. 
   * @see net.mumie.mathletfactory.number.NumberTypeDependentIF#getNumberClass()
   */
  public Class getNumberClass() {
    return m_operation.getNumberClass();
  }

  /**
   *  Returns an array containing the minimum x and y values and the width of
   *  a unit in x and y direction.
   */
  public double[] getGridParameters() {
    double[] parameters = new double[4];
    parameters[0] = m_xRange[0];
    parameters[1] = m_yRange[0];
    parameters[2] = (m_xRange[1] - m_xRange[0]) / m_steps;
    parameters[3] = (m_yRange[1] - m_yRange[0]) / m_steps;
    return parameters;
  }

  /**
   *  Sets the range of the grid to be calculated (default is (-1,1) in each
   *  direction).
   */
  public void setRange(double[] xRange, double[] yRange) {
    m_xRange = xRange;
    m_yRange = yRange;
    m_recalculationNeeded = true;
  }

  /**
   *  Sets the number of steps for each direction in the grid. So
   *  <code>steps</code>^2 cells are created.
   */
  public void setSteps(int steps) {
    m_steps = steps;
    m_fValues = new MNumber[steps + 1][steps + 1];
    m_recalculationNeeded = true;
  }

  /**
   *  Returns the grid of values for this calculation.
   */
  public MNumber[][] getValues() {
    if (m_recalculationNeeded)
      calculate();
    return m_fValues;
  }

  /**
   * A helper method.
   * Returns the derivative d/d<code>variable</code> of the given <code>operation</code>.
   */
  private static Operation getDerivative(Operation operation, String variable) {
    Operation derivative = operation.getDerivative(variable);
    logger.fine("d/d" + variable + " " + operation.toDebugString() + " = " + derivative.toString());
    logger.fine("before simplification f'(" + variable + ") = " + derivative.toDebugString());
    derivative.normalize();
    logger.fine("after simplification f'(" + variable + ") = " + derivative.toDebugString());
    return derivative;
  }

  /**
   *  Derive the current function with respect to <code>variable</code>.
   */
  public void derive(String variable) {
    setOperation(getDerivative(m_operation, variable));
  }

  /**
   *  Simply outputs the expression and the function values (may be huge!).
   */
  public String toString() {
    return "f(x,y) = " + getOperation().toString();
  }

  /**
   *  Calculates a result for a certain pair (x,y).
   */
  public MNumber evaluate(MNumber xNumber, MNumber yNumber) {
    m_operation.assignValue("x", xNumber);
    m_operation.assignValue("y", yNumber);
    return m_operation.getResult();
  }

  /**
   *  Calculates a result for a certain pair (x,y).
   */
  public double evaluate(double xNumber, double yNumber){
    m_operation.assignValue("x", xNumber);
    m_operation.assignValue("y", yNumber);
    return m_operation.getResultDouble();        
  }

  /**
   *  This method should be called every time, the operation tree has changed.
   *  It resets the references to variables in the tree and sets a mark for
   *  the recalculation of values.
   */
  public void update() {
    m_recalculationNeeded = true;
    m_version++;
  }

  /**
   *  Iterates over the grid and produces the results, storing them for
   *  retrieval with {@link #getValues}.
   */
  public void calculate() {

    double width = m_xRange[1] - m_xRange[0];
    double height = m_yRange[1] - m_yRange[0];

    // calculate an equidistant grid within the range and
    // evaluate the operation tree for each value of (x,y) in the grid
    MNumber defX = NumberFactory.newInstance(getNumberClass());
    MNumber defY = NumberFactory.newInstance(getNumberClass());
    for (int i = 0; i <= m_steps; i++)
      for (int j = 0; j <= m_steps; j++) {
        defX.setDouble(m_xRange[0] + (i * width) / m_steps);
        defY.setDouble(m_yRange[0] + (j * width) / m_steps);

        m_operation.assignValue("x", defX);
        m_operation.assignValue("y", defY);
        
        // calculate the result of the whole operation and store
        // it in the grid
        m_fValues[i][j] = m_operation.getResult();
        //System.out.println("value["+i+"]["+j+" is "+m_values[i][j]);
      }
    m_recalculationNeeded = false;
    m_version++;
  }

  /**
   *  For each call of {@link #calculate} the version number is incremented, so
   *  clients of this object (e.q. transformers) can check for themselves if
   *  they are up to date.
   */
  public int getVersion() {
    return m_version;
  }

  /**
   * Returns the partial derivative with respect to the given variable for this function. 
   */
  public MMFunctionOverR2 getDerivative(String variable) {
    MMFunctionOverR2 derivative = new MMFunctionOverR2(this);
    derivative.derive(variable);
    return derivative;
  }

  /**
   * This class catches 
   * {@link net.mumie.mathletfactory.mmobject.MMPropertyHandlerIF#OPERATION}
   * property changes.
   * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
   */
  public void propertyChange(PropertyChangeEvent e) {
    if (!e.getPropertyName().equals(OPERATION))
      return;
    setOperation(new Operation((Operation) e.getNewValue()));
    ActionManager.performActionCycleFromObject(this);
    //System.out.println("expression is now " + getOperation());
  }

  public int getDefaultTransformType() {
    return GeneralTransformer.FUNCTION_OVER_RN_AFFINE_GRAPH_TRANSFORM;
  }

  public int getDefaultTransformTypeInCanvas() {
    return GeneralTransformer.FUNCTION_OVER_RN_AFFINE_GRAPH_TRANSFORM;
  }

  public int getVerticesCount() {
    return m_vertices_per_direction_in_drawable;
  }

  public int getDefaultTransformTypeAsContainer() {
    return GeneralTransformer.FUNCTION_TRANSFORM;
  }
  
  public void setNormalForm(boolean normalize) {
		m_normalize = normalize;
		m_operation.setNormalForm(normalize);
		if (!m_normalize)
			m_operation.setOpRoot(OpTransform.applyRule(m_operation.getOpRoot(), new RemoveNeutralElementRule()));
	}
	
	public boolean isNormalForm() {
		return m_normalize;
	}
}

