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

import net.mumie.mathletfactory.display.PolygonDisplayProperties;
import net.mumie.mathletfactory.math.analysis.function.FunctionAndDerivativeOverRIF;
import net.mumie.mathletfactory.math.analysis.function.FunctionOverBorelSetIF;
import net.mumie.mathletfactory.math.analysis.function.FunctionOverRAdapter;
import net.mumie.mathletfactory.math.analysis.function.FunctionOverRIF;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.set.FiniteBorelSet;
import net.mumie.mathletfactory.math.set.Interval;
import net.mumie.mathletfactory.math.set.NumberSetIF;
import net.mumie.mathletfactory.mmobject.MMDefaultCanvasObject;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 * This class offers a body for special functions that are constructed using an implementation of 
 * {@link net.mumie.mathletfactory.math.analysis.function.FunctionOverRIF}.
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */
public class MMFunctionDefinedByExpression extends MMDefaultCanvasObject
  implements FunctionOverBorelSetIF, MMFunctionAndDerivativeOverRIF, MMOneChainInRIF {

  private int m_verticesCount = MMOneChainInRIF.DEFAULT_VERTICES_COUNT;

  private boolean m_BoundaryVisibility = true;

  private Class m_entryClass;

  private FiniteBorelSet m_domain;

  private FunctionOverRIF m_functionExpression;

  /** Constructs the function with an empty (null) expression. */
  public MMFunctionDefinedByExpression(Class entryClass) {
    this(entryClass, new FunctionOverRAdapter(entryClass));
  }

  /** Constructs the function with the given expression as evaluation unit. */
  public MMFunctionDefinedByExpression(Class entryClass, FunctionOverRIF f) {
    m_entryClass = entryClass;
    m_functionExpression = f; //TODO: deepcopy() should be used
    m_domain = FiniteBorelSet.getRealAxis(entryClass);
    PolygonDisplayProperties p = new PolygonDisplayProperties();
    p.setFilled(false);
    setDisplayProperties(p);
  }

  /** Sets the given function expression as evaluation unit. */
  public void setFunction(FunctionOverRIF f) {
    m_functionExpression = f;
  }

  /** Returns the function serving as evaluation unit. */
  public FunctionOverRIF getFunction() {
    return m_functionExpression;
  }

  public int getDefaultTransformType() {
    return GeneralTransformer.ONE_CHAIN_1D_AFFINE_GRAPH_TRANSFORM;
  }

  public int getDefaultTransformTypeInCanvas() {
    return GeneralTransformer.ONE_CHAIN_1D_AFFINE_GRAPH_TRANSFORM;
  }

  public int getDefaultTransformTypeAsContainer() {
    return GeneralTransformer.NO_TRANSFORM_TYPE;
  }
  public int getVerticesCount() {
    return m_verticesCount;
  }

  public void setVerticesCount(int vertexCount) {
    m_verticesCount = vertexCount;
  }

  public void setBoundarysVisible(boolean aFlag) {
    m_BoundaryVisibility = aFlag;
  }

  public boolean areBoundarysVisible() {
    return m_BoundaryVisibility;
  }

  public FiniteBorelSet getBorelSet() {
    return m_domain;
  }

  public NumberSetIF getDomain() {
    return getBorelSet();
  }

  public double evaluate(double x) {
    return m_functionExpression.evaluate(x);
  }

  public void evaluate(double[] xin, double[] yout) {
    m_functionExpression.evaluate(xin, yout);
  }

  public void evaluate(MNumber xin, MNumber yout) {
    m_functionExpression.evaluate(xin, yout);
  }

  public Class getNumberClass() {
    return m_entryClass;
  }

  public int getAllIntervalCount() {
    return getBorelSet().getIntervalCount();
  }

  public Interval getInterval(
    int expressionCount,
    int intervalIndexInBorelSet) {
    return getBorelSet().getInterval(intervalIndexInBorelSet);
  }

  public FunctionOverRIF getEvaluateExpressionInComponent(int index) {
    return m_functionExpression;
  }

  public FiniteBorelSet getBorelSetInComponent(int index) {
    return getBorelSet();
  }

  public int getComponentsCount() {
    return 1;
  }

  public int getIntervalCountInComponent(int index) {
    return getBorelSet().getIntervalCount();
  }

  public void getDerivativeByNumeric(MMFunctionDefinedByExpression f) {
    getDerivativeByNumeric(0, f);
  }

  public void getDerivativeByNumeric(
    final double epsilon,
    MMFunctionDefinedByExpression f) {
    f.setFunction(getDerivativeByNumeric(epsilon));
    f.setBorelSet(m_domain);
  }

  public FunctionOverRIF getDerivativeByNumeric() {
    return getDerivativeByNumeric(FunctionAndDerivativeOverRIF.DEFAULT_DELTA);
  }

  public FunctionOverRIF getDerivativeByNumeric(final double epsilon) {
    FunctionOverRAdapter derivative =
      new FunctionOverRAdapter(getNumberClass()) {
      public double evaluate(double xin) {
        FunctionOverRIF f = MMFunctionDefinedByExpression.this;
        double result =
          (f.evaluate(xin + epsilon) - f.evaluate(xin - epsilon))
            / (2 * epsilon);
        return result;
      }
    };
    return derivative;
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
    f.setBorelSet(m_domain);
  }

  /** Returns the primitive G(x) = int_a^x g(t) dt where g is this function */
  public FunctionOverRIF getPrimitive(double a) {
    final double A = a;
    final FunctionOverRIF f = MMFunctionDefinedByExpression.this;
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

  public void setBorelSet(FiniteBorelSet set) {
    m_domain = set;
  }
}
