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

package net.mumie.mathletfactory.mmobject.analysis.sequence;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import net.mumie.mathletfactory.action.ActionManager;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.math.algebra.op.OpParser;
import net.mumie.mathletfactory.math.algebra.op.OpTransform;
import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.algebra.op.UsesOperationIF;
import net.mumie.mathletfactory.math.algebra.op.rule.normalize.RemoveNeutralElementRule;
import net.mumie.mathletfactory.math.number.MNatural;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.mmobject.MMDefaultCanvasObject;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 * This class represents a series that is defined by an
 * {@link net.mumie.mathletfactory.math.algebra.op.Operation}.
 *  
 * @author Paehler
 * @mm.docstatus finished
 */
public class MMSeriesDefByOp extends  MMDefaultCanvasObject implements UsesOperationIF {

  public void setEditable(boolean isEditable) {
    if(isEditable == isEditable())
      return;
    super.setEditable(isEditable);
    render();
  }

  private Class m_numberClass;

  protected Vector m_variableXOps;

  /**
   * The starting index of this series, from which calculation starts. Default is 1.
   */  
  private int m_startingIndex = 1;
  
  /** The operation that defines the series. Should at least contain the variable "k". */
  protected Operation m_operation = null;
  
  protected boolean m_normalize = true;
  
  /** 
   * The parameters used in this series.
   * @see #setParameter
   * @see #getParameter
   */
  protected Map m_parameters = new HashMap();
  
  /** Constructs the series for the given number class and expression containing the variable "k". */
  public MMSeriesDefByOp(Class numberClass, String stringFunction) {
    this(new Operation(numberClass, stringFunction, true));
  }

  /** Constructs the series for the given operation containing the variable "k". */
  public MMSeriesDefByOp(Operation operation){
    m_numberClass = operation.getNumberClass();
    setOperation(operation);
    setLabel("s");
    setDisplayProperties(new DisplayProperties());
  }

  public Class getNumberClass() {
    return m_numberClass;
  }

  /** Sets the expression for this series. */
  public void setOperation(String expr) {
    setOperation(OpParser.getOperation(getNumberClass(), expr, m_normalize));
  }

  /** Sets the operation for this series. */
  public void setOperation(Operation operation) {
    m_operation = operation;
    m_operation.setNormalForm(m_normalize);
    update();
  }

  /** Returns the operation for this series. */
  public Operation getOperation() {
    return m_operation;
  }

  /** Adds a parameter with the specified value to the function. */
  public void setParameter(String parameterName, MNumber value){
    m_parameters.put(parameterName, value);
    m_operation.setParameter(parameterName);
    m_operation.assignValue(parameterName, value);
    setOperation(m_operation);
  }

  /** Adds a parameter with the specified value to the function. */
  public void setParameter(String parameterName, double value){
    m_parameters.put(parameterName, NumberFactory.newInstance(getNumberClass(),value));
    m_operation.setParameter(parameterName);
    m_operation.assignValue(parameterName, value);
  }

  /** Returns all parameters with values as a map. */
  public Map getParameters(){
    return m_parameters;
  }

  protected void update() {
    m_variableXOps = new Vector();
    m_operation.putVariableOps("n", m_variableXOps);
    // check for other variables than "n" and mark them as parameters
    String[] variables = m_operation.getUsedVariables();
    for (int i = 0; i < variables.length; i++) {
      if(!variables[i].equals("n")){        
        m_operation.setParameter(variables[i]);
        m_operation.assignValue(variables[i], (MNumber)m_parameters.get(variables[i]));
      }
    }
  }

  /** Computes and returns the sum from {@link #getStartingIndex} to the argument. */
  public MNumber getSum(MNatural natural){
    return getSum(natural.getDouble());
  }

  /** Computes and returns the sum from {@link #getStartingIndex} to the argument. */
  public MNumber getSum(double n) {
    MNumber result = NumberFactory.newInstance(getNumberClass()); 
    MNumber tmp = NumberFactory.newInstance(getNumberClass());
    MNumber k = NumberFactory.newInstance(getNumberClass());
    for(int _k=m_startingIndex;_k<=n;_k++){
      m_operation.assignValue("k", _k);
      m_operation.evaluate(k, tmp);
      if(!tmp.isNaN())
        result.add(tmp);
    }
    return result;
  }

  /** Computes and returns the sum from {@link #getStartingIndex} to the argument. */
  public double getSumDouble(int n){
    double result = 0;
    for(int k=m_startingIndex;k<=n;k++){
      m_operation.assignValue("k", k);
      result += m_operation.evaluate(k,"k");
    }
//    System.out.println("result for "+m_operation+", n="+n+" is "+result);
    return result;
  }//buggy!

  /**
   * Returns the starting index of this series, from which calculation starts. Default is 1.
   */
  public int getStartingIndex() {
    return m_startingIndex;
  }

  /**
   * Sets the starting index of this series, from which calculation starts. Default is 1.
   */
  public void setStartingIndex(int i) {
    m_startingIndex = i;
  }

  /** Listens exclusively for OPERATION events and adjusts its operation to the new value. */  
  public void propertyChange(PropertyChangeEvent evt) {
    if (!evt.getPropertyName().equals(OPERATION))
      return;
    setOperation(new Operation((Operation) evt.getNewValue()));
    ActionManager.performActionCycleFromObject(this);
  }

  public int getDefaultTransformTypeAsContainer() {
    return GeneralTransformer.FUNCTION_TRANSFORM;
  }

  public int getDefaultTransformType() {
    return GeneralTransformer.SERIES_TRANSFORM_2D;
  }

  public int getDefaultTransformTypeInCanvas() {
    return GeneralTransformer.SERIES_TRANSFORM_2D;
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
