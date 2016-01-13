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

import java.awt.Color;
import java.beans.PropertyChangeEvent;

import net.mumie.mathletfactory.action.ActionManager;
import net.mumie.mathletfactory.display.PolygonDisplayProperties;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.algebra.op.UsesOpArrayIF;
import net.mumie.mathletfactory.math.analysis.function.multivariate.VectorFunctionOverRIF;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberTypeDependentIF;
import net.mumie.mathletfactory.math.set.FiniteBorelSet;
import net.mumie.mathletfactory.math.set.Interval;
import net.mumie.mathletfactory.mmobject.MMDefaultCanvasObject;
import net.mumie.mathletfactory.mmobject.analysis.function.MMFunctionDefByOp;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 *  This class takes three strings representing arithmetic expressions
 *  of <i>u</i> and <i>v</i>, a pair of ranges and performs the 
 *  calculations for a twodimensional grid of elements in this range by 
 *  feeding the Operation Tree (produced by 
 *  {@link net.mumie.mathletfactory.algebra.op.OpParser}) with the
 *  appropriate values.
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */

public class MMParametricFunctionInR2
	extends MMDefaultCanvasObject
	implements NumberTypeDependentIF, UsesOpArrayIF, MMOneChainInRNIF {

  /** The name of the parameter used by the component operations. */
	private String m_parameterName = "t";

  /** The range of the parameter used by the component operations. */
	private double[] m_parameterRange = new double[] { -10, 10 };

	/** The number class used in the calculations */
	private Class m_numberClass;

	/**
	 *  The root node of the f_x(u,v) operation tree as returned by 
	 *  {@link net.mumie.mathletfactory.algebra.op.OpParser}.
	 */
	private Operation m_FxOperation = null;

	/**
	 *  The root node of the f_y(u,v) operation tree as returned by 
	 *  {@link net.mumie.mathletfactory.algebra.op.OpParser}.
	 */
	private Operation m_FyOperation = null;

  /** A one chain adapter for g2d rendering. */
  private MMOneChainInR2 m_chain = null;


  /**
   *  Upon construction, this class preparses the string, hands it over to
   *  <code>ExprParser</code>, stores the operation tree and keeps record of
   *  the nodes representing the variables (x,y,z) for later insertion (i.e
   *  filling the {@link net.mumie.mathletfactory.algebra.op.node.VariableOp}s 
   *  with elements of Type <code>MNumber</code>).
   */
  public MMParametricFunctionInR2(
    Class numberClass,
    String xExpr,
    String yExpr) {
    m_numberClass = numberClass;
    setOperations(new Operation[]{new Operation(m_numberClass, xExpr, true), new Operation(m_numberClass, yExpr, true)});
    PolygonDisplayProperties props = new PolygonDisplayProperties(PolygonDisplayProperties.DEFAULT);
    props.setFilled(false);
    props.setObjectColor(Color.RED);
    setDisplayProperties(props);
    
    setLabel("f");
  }

	/**
	 * Constructs a parametric function with the given values.
	 * 
	 * @param numberClass the number class used.
	 * @param xExpr the (u,v)-expression for the <i>x</i> value
	 * @param yExpr the (u,v)-expression for the <i>y</i> value
	 * @param uRange the range in u direction 
	 * @param steps the number of steps in each direction, the function will be evaluated
	 * in a <code>steps x steps</code> mesh.
	 */
	public MMParametricFunctionInR2(
		Class numberClass,
		String xExpr,
		String yExpr,
		double[] uRange,
		int steps) {
		this(numberClass, xExpr, yExpr);
		setParameterRange(uRange);
		setVerticesCount(steps);
	}


  /**
   * Sets the expressions for the parametric function.
   * @param xExpr The value in x direction.
   * @param yExpr The value in y direction.
   */
  public void setOperations(String xExpr, String yExpr) {
    setOperations(new Operation[]{new Operation(getNumberClass(), xExpr, true),
      new Operation(getNumberClass(), yExpr, true)});
  }

  /**
   * Sets the two operations for the parametric function.
   * 
   * @param operations an operation array with length 2.
   */
	public void setOperations(Operation[] operations) {
		m_FxOperation = operations[0];
    m_FyOperation = operations[1];
    Interval i = new Interval(getNumberClass(), m_parameterRange[0], Interval.OPEN, m_parameterRange[1], Interval.OPEN);
    MMFunctionDefByOp function1 =  new MMFunctionDefByOp(m_FxOperation, new FiniteBorelSet(i));
    MMFunctionDefByOp function2 =  new MMFunctionDefByOp(m_FyOperation, new FiniteBorelSet(i));
    function1.setVariableId(m_parameterName);
    function2.setVariableId(m_parameterName);
    m_chain = new MMOneChainInR2(function1, function2, i);
    render();
	}

  /**
   * Returns the two operations for the parametric function.
   * 
   * @return an operation array with length 2.
   */
	public Operation[] getOperations() {
    return new Operation[] { m_FxOperation, m_FyOperation};
	}

//	/**
//	 *  This method sets the expression for <i>f(y)</i> to be evaluated. It
//	 *  constructs the corresponding operation tree and marks it for recalculation.
//	 */
//	private void setFxExpression(String expr) {
//		setFxOperation(OpParser.getOperation(m_numberClass, expr, true));
//	}

//	private void setFxOperation(Operation operation) {
//		m_FxOperation = operation;
//    setOperations(new Operation[]{operation, m_FyOperation});
//	}
//
//	/**
//	 *  This method sets the expression for <i>f(y)</i> to be evaluated. It
//	 *  constructs the corresponding operation tree and marks it for recalculation.
//	 */
//	private void setFyExpression(String expr) {
//		setFyOperation(OpParser.getOperation(m_numberClass, expr, true));
//	}

//	private void setFyOperation(Operation operation) {
//		m_FyOperation = operation;
//    setOperations(new Operation[]{m_FxOperation, operation});
//	}

	/**
	 * Returns the number class used. 
	 * @see net.mumie.mathletfactory.number.NumberTypeDependentIF#getNumberClass()
	 */
	public Class getNumberClass() {
		return m_numberClass;
	}

	/** Sets the range of the parameter used by the component operations. */
	public void setParameterRange(double[] parameterRange) {
		m_parameterRange = parameterRange;
    setOperations(getOperations());
	}

  /** Returns the range of the parameter used by the component operations. */
  public double[] getParameterRange(){
    return m_parameterRange;
  }
  
  /** Returns the name of the parameter used by the component operations. */
  public String getParameterName() {
    return m_parameterName;
  }

  /** Sets the name of the parameter used by the component operations. */
  public void setParameterName(String string) {
    m_parameterName = string;
    setOperations(getOperations());
  }

	/**
	 *  Sets the number of steps for each direction in the grid. So steps^3
	 *  cells are are created.
	 */
	public void setVerticesCount(int steps) {
		m_chain.setVerticesCount(steps);
	}


	/** For debugging purposes: outputs all(!) the calculated values as a twodimensional array. */
	public String toString() {
    return getLabel()+"("+m_parameterName+") = ["+m_FxOperation+" ,"+m_FyOperation+"]";
	}

	/**
	 *  Calculates a result for a certain (u,v) tuple.
	 */
	public NumberTuple evaluateFor(MNumber paramValue) {
    //System.out.println("evaluating for "+paramValue);
		NumberTuple retVal = new NumberTuple(m_numberClass, 2);    
		// insert the argument def into the variable-holders
		m_FxOperation.assignValue(m_parameterName, paramValue);
    m_FyOperation.assignValue(m_parameterName, paramValue);
		retVal.setEntryRef(1, m_FxOperation.getResult());
		retVal.setEntryRef(2, m_FyOperation.getResult());
		return retVal;
	}

	/**
	 * This class catches 
	 * {@link net.mumie.mathletfactory.mmobject.MMPropertyHandlerIF#OPERATION_ARRAY}
	 * property changes.
	 * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent e) {
		if (!e.getPropertyName().equals(OPERATION_ARRAY))
			return;
		setOperations((Operation[]) e.getNewValue());
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
		return m_chain.getVerticesCount();
	}

	public int getDefaultTransformTypeAsContainer() {
		return GeneralTransformer.FUNCTION_TRANSFORM;
	}
  
  public FiniteBorelSet getBorelSetInPart(int index) {
    return m_chain.getBorelSetInPart(index);
  }
  
  public boolean areBoundarysVisible() {
    return m_chain.areBoundarysVisible();
  }

  public void getBoundingBox(double[][] InCorners, double[] outTminTmax) {
    //TODO: must be modified
    outTminTmax[0] = -1;
    outTminTmax[1] = +1; 
  }

  public void setBoundarysVisible(boolean aFlag) {
    m_chain.setBoundarysVisible(aFlag);
  }

  public int getAllIntervalCount() {
    return m_chain.getAllIntervalCount();
  }

  public VectorFunctionOverRIF getEvaluateExpressionInPart(int i) {
    return m_chain.getEvaluateExpressionInPart(i);
  }

  public Interval getInterval(int indexOfPart, int intervalIndexInBorelSet) {
    return m_chain.getInterval(indexOfPart, intervalIndexInBorelSet);
  }

  public int getIntervalCountInPart(int index) {
    return m_chain.getIntervalCountInPart(index);
  }

  public int getPartCount() {
    return m_chain.getPartCount();
  }
}
