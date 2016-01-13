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
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.math.set.FiniteBorelSet;
import net.mumie.mathletfactory.math.set.Interval;
import net.mumie.mathletfactory.mmobject.MMDefaultCanvasObject;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 * Represents the Fourier series of a periodic function.
 *
 * @author Mrose
 * @mm.docstatus finished
 */
public class MMFourierSeriesDefByOp extends MMDefaultCanvasObject implements FunctionOverBorelSetIF, MMOneChainInRIF {

  private MMFunctionDefByOp m_function;
  private double m_period = 2.0 * Math.PI;
  private double[] m_ak = new double[1000];
  private double[] m_bk = new double[1000];

  private boolean m_boundariesVisible = false;
  private int m_verticesCount = 4000;
  private Class m_numberClass;

  private MNumber m_sum;
  private int m_n;

  /**
   * Defines the Fourier series of the function <code>function</code> with period
   * <code>period</code>; the number class is given by <code>numberClass</code>.
   */
  public MMFourierSeriesDefByOp(
    Class numberClass,
    MMFunctionDefByOp function,
    double period) {
    m_numberClass = numberClass;
    m_function = function;
    if (period >= 0.0) {
      m_period = period;
    }
    getCoefficients(function, period);
    m_sum = NumberFactory.newInstance(getNumberClass());
  }

  /** Evaluates this Fourier series in <code>x</code> and returns its value.*/
  public double evaluate(double x) {
    return getSum(m_n, x).getDouble();
  }

  /** Evaluates this Fourier series in <code>xin</code> and sets <code>yout</code> on its value.*/
  public void evaluate(MNumber xin, MNumber yout) {
    yout.set(getSum(m_n, xin.getDouble()));
  }

  /**
   * Returns the value of the <code>n</code>-th partial sum of this Fourier
   * series in <code>x</code>.
   */
  public MNumber getSum(int n, double x) {
    double result = m_ak[0] / 2.0;
    for (int _k = 1; _k <= m_n; _k++) {
      result +=
          m_ak[_k] * Math.cos(2.0 * Math.PI * _k * x / m_period)
            + m_bk[_k] * Math.sin(2.0 * Math.PI * _k * x / m_period);
    }
    m_sum.setDouble(result);
    return m_sum;
  }

  /** Sets the function approximated by this Fourier series.*/
  public void setFunction(MMFunctionDefByOp function) {
    m_function = function;
    getCoefficients(function, m_period);
  }

  /** Sets the period of this Fourier series.*/
  public void setPeriod(double period) {
    if (period >= 0.0) {
      m_period = period;
    }
    getCoefficients(m_function, period);
  }

  /** Returns the period of this Fourier series.*/
	public double getPeriod() {
		return m_period;
	}

  private void getCoefficients(MMFunctionDefByOp function, double period) {
    Operation operationA =
      new Operation(getNumberClass(), "cos(x*k*2.0*pi/p)", true);
    Operation operationB =
      new Operation(getNumberClass(), "sin(x*k*2.0*pi/p)", true);
    operationA.assignValue("p", period);
    operationA.setParameter("p");
    operationB.assignValue("p", period);
    operationB.setParameter("p");
    operationA.mult(function.getOperation());
    operationB.mult(function.getOperation());
    FiniteBorelSet interval =
      new FiniteBorelSet(
        new Interval(
          getNumberClass(),
          0.0,
          Interval.CLOSED,
          period,
          Interval.CLOSED));
    MMFunctionDefByOp helpFunction =
      new MMFunctionDefByOp(operationA, interval);
    for (int k = 0; k <= m_n; k++) {
      operationA.assignValue("k", k);
      operationA.setParameter("k");
      helpFunction.setOperation(operationA);
      m_ak[k] = 2.0 * helpFunction.getPrimitive().evaluate(period) / period;

      operationB.assignValue("k", k);
      operationB.setParameter("k");
      helpFunction.setOperation(operationB);
      m_bk[k] = 2.0 * helpFunction.getPrimitive().evaluate(period) / period;
    }
  }

  /* (non-Javadoc)
   * @see net.mumie.mathletfactory.mmobject.analysis.function.MMOneChainInRIF#areBoundarysVisible()
   */
  public boolean areBoundarysVisible() {
    return m_boundariesVisible;
  }

  /* (non-Javadoc)
   * @see net.mumie.mathletfactory.mmobject.analysis.function.MMOneChainInRIF#setBoundarysVisible(boolean)
   */
  public void setBoundarysVisible(boolean aFlag) {
    m_boundariesVisible = aFlag;
  }

  /* (non-Javadoc)
   * @see net.mumie.mathletfactory.math.analysis.function.OneChainInRIF#getBorelSetInComponent(int)
   */
  public FiniteBorelSet getBorelSetInComponent(int indexOfComponent) {
    return FiniteBorelSet.getRealAxis(getNumberClass());
  }

  /* (non-Javadoc)
   * @see net.mumie.mathletfactory.math.analysis.function.OneChainInRIF#getEvaluateExpressionInComponent(int)
   */
  public FunctionOverRIF getEvaluateExpressionInComponent(int indexOfComponent) {
    // TODO Auto-generated method stub
    return this;
  }

  /**
   * Returns 1, since the function has only one component.
   *
   * @see net.mumie.mathletfactory.math.analysis.function.OneChainInRIF#getIntervalCountInComponent(int)
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


  public int getVerticesCount() {
    return m_verticesCount;
  }

  public void setVerticesCount(int aNumber) {
    m_verticesCount = aNumber;
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

  /* (non-Javadoc)
   * @see net.mumie.mathletfactory.math.number.NumberTypeDependentIF#getNumberClass()
   */
  public Class getNumberClass() {
    // TODO Auto-generated method stub
    return m_numberClass;
  }

  /* (non-Javadoc)
   * @see net.mumie.mathletfactory.math.analysis.function.FunctionOverBorelSetIF#getBorelSet()
   */
  public FiniteBorelSet getBorelSet() {
    return m_function.getBorelSet();
  }

  /* (non-Javadoc)
   * @see net.mumie.mathletfactory.math.analysis.function.FunctionOverBorelSetIF#setBorelSet(net.mumie.mathletfactory.math.set.FiniteBorelSet)
   */
  public void setBorelSet(FiniteBorelSet set) {
    // TODO Auto-generated method stub
  }

  /* (non-Javadoc)
   * @see net.mumie.mathletfactory.math.analysis.function.FunctionOverRIF#evaluate(double[], double[])
   */
  public void evaluate(double[] xin, double[] yout) {
    for(int i=0;i<xin.length;i++)
      yout[i] = evaluate(xin[i]);
  }

  public int getN() {
    return m_n;
  }

  /**
   * @param i
   */
  public void setN(int i) {
    m_n = i;
  }

}
