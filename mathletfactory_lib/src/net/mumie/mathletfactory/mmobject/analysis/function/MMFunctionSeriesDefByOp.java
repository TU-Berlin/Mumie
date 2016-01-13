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

import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.analysis.function.FunctionOverBorelSetIF;
import net.mumie.mathletfactory.math.analysis.function.FunctionOverRIF;
import net.mumie.mathletfactory.math.number.MNatural;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.set.FiniteBorelSet;
import net.mumie.mathletfactory.math.set.Interval;
import net.mumie.mathletfactory.math.set.NumberSetIF;
import net.mumie.mathletfactory.mmobject.analysis.sequence.MMSeriesDefByOp;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 * This class represents a monovariate real valued function series that is 
 * defined by an {@link net.mumie.mathletfactory.math.algebra.op.Operation}.
 *  
 * @author Paehler
 * @mm.docstatus finished
 */
public class MMFunctionSeriesDefByOp extends MMSeriesDefByOp implements MMOneChainInRIF, FunctionOverBorelSetIF {

  /** The parameter, the "n" up to which the series counts, when evaluated. */
  private MNatural m_n = new MNatural();

  /** The identifier name that is used as free variable in the function. Default is "x".*/
  private String m_variableId = "x";
  
  private FiniteBorelSet m_borelSet;

  private int m_vertices_count_in_drawable =
    MMOneChainInRIF.DEFAULT_VERTICES_COUNT;

  private boolean m_BoundaryVisibility = true;


  public int getDefaultTransformTypeInCanvas() {
    return GeneralTransformer.ONE_CHAIN_1D_AFFINE_GRAPH_TRANSFORM;
  }
  
  /** Returns the "n" for which the function sequence is evaluated. */
  public MNatural getN() {
    return m_n;
  }

  /** Sets the "n" for which the function series is evaluated. */
  public void setN(MNatural natural) {
    m_n = natural;
  }
  
  /** Sets the "n" for which the function series is evaluated. */
  public void setN(int n){
    m_n.setDouble(n);
  }

  /** Returns the identifier name that is used as free variable in the function. Default is "x".*/
  public String getVariableId() {
    return m_variableId;
  }

  /** Sets the identifier name that is used as free variable in the function. Default is "x".*/
  public void setVariableId(String string) {
    m_variableId = string;
  }
    
  public FiniteBorelSet getBorelSet() {
    return m_borelSet;
  }

  public void setBorelSet(FiniteBorelSet set) {
    m_borelSet = set;
  }

  public NumberSetIF getDomain(){
    return m_borelSet;
  }

  // MMOneChainInRIF methods follow
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
    return getBorelSet();
  }

  public int getComponentsCount() {
    return 1;
  }

  public int getAllIntervalCount() {
    return getBorelSet().getIntervalCount();
  }

  public Interval getInterval(
    int expressionCount,
    int intervalIndexInBorelSet) {
    return getBorelSet().getInterval(intervalIndexInBorelSet);
  }

  public int getIntervalCountInComponent(int index) {
    return getBorelSet().getIntervalCount();
  }

  public FiniteBorelSet getBorelSet(int index) {
    return getBorelSet();
  }

  public MMFunctionSeriesDefByOp(Class numberClass, String expression) {
    this(new Operation(numberClass, expression, true));
  }

  public MMFunctionSeriesDefByOp(Operation operation) {
    super(operation);
    m_n.setDouble(10);
    m_borelSet = new FiniteBorelSet(operation.getNumberClass());
    setStartingIndex(0);
    getDisplayProperties().setFilled(false);
  }


  public double evaluate(double x) {
    m_operation.assignValue(m_variableId, x);
    return getSum(m_n).getDouble();
    //return getSumDouble((int)m_n.getDouble()); buggy!
  }

  public void evaluate(double[] xin, double[] yout) {    
    for(int i=0;i<xin.length;i++)
      yout[i] = evaluate(xin[i]);
  }

  public void evaluate(MNumber xin, MNumber yout) {
    m_operation.assignValue(m_variableId, xin);
    yout.set(getSum(m_n));
  }
}
