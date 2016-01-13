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

import java.awt.Color;

import net.mumie.mathletfactory.display.PolygonDisplayProperties;
import net.mumie.mathletfactory.math.analysis.function.FunctionOverBorelSetIF;
import net.mumie.mathletfactory.math.analysis.function.FunctionOverRIF;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.set.FiniteBorelSet;
import net.mumie.mathletfactory.math.set.Interval;
import net.mumie.mathletfactory.math.set.NumberSetIF;
import net.mumie.mathletfactory.mmobject.MMDefaultCanvasObject;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 * This class represents a step function (a piecewise constant function
 * over an interval with a finite distribution of it). Note that its 
 * distribution is not necessary equidistant, but currently the only 
 * {@link #approximate} method produces an equidistant distribution.
 * Instances of this class are often used for displaying riemannian sums. 
 *  
 * @author Paehler
 * @mm.docstatus finished
 */
public class MMStepFunction
	extends MMDefaultCanvasObject
	implements FunctionOverBorelSetIF, MMOneChainInRIF {

	/** Value for step functions that provide the lower riemannian sum */
	public static final int LOWER = 1;

	/** Value for step functions that provide the upper riemannian sum */
	public static final int UPPER = 2;

	/** Value for step functions that use the function value of the mean of left and right bound of the interval for each step. */
	public static final int MEAN = 3;

	/** Value for step functions that use the mean of the function values at the left and right bound of the interval for each step. */
	public static final int FUNCTIONMEAN = 4;

	/** Value for step functions that use function value at the left bound of the interval for each step. */
	public static final int LEFT = 5;

	/** Value for step functions that use function value at the right bound of the interval for each step. */
	public static final int RIGHT = 6;

	/** The type of this step function. Used in {@link #approximate}. */
	private int m_type = LOWER;

 /** Currently only borel sets that consist of a single interval are allowed. */
	protected FiniteBorelSet m_borelSet;

  /** The values that are the left borders of the interval's distribution. */
	protected double[] m_distribution;

  /** The value, the function assumes on each of the distribution intervals. */
	protected double[] m_distributionValues;

	private boolean m_BoundaryVisibility = true;

  /** Constructs this step function from the given distribution values.  */
	public MMStepFunction(
		double[] distribution,
		double[] distributionValues,
		FiniteBorelSet borelSet) {
		m_borelSet = borelSet;
		m_distribution = copyArray(distribution);
		m_distributionValues = copyArray(distributionValues);
		setLabel("T_f");
		setDisplayProperties(new PolygonDisplayProperties());
		getDisplayProperties().setTransparency(0.5);
		getDisplayProperties().setObjectColor(Color.YELLOW);
		getDisplayProperties().setFilled(true);
	}

  /** Constructs this step function as the approximation of the given function with the given steps.  */
	public MMStepFunction(FunctionOverBorelSetIF function, int steps) {
		this(
			new double[steps],
			new double[steps],
			function.getBorelSet().deepCopy());
		approximate(
			function,
			function.getBorelSet().getLowerBound().getDouble(),
			function.getBorelSet().getUpperBound().getDouble(),
			steps);
	}

  /** Copy constructor.  */
  public MMStepFunction(MMStepFunction function) {
    this(
      function.m_distribution,
      function.m_distributionValues,
      function.getBorelSet());
    if (function instanceof MMStepFunction) {
      MMStepFunction mmFunction = (MMStepFunction) function;
      setDisplayProperties(mmFunction.getDisplayProperties());
    }
    else
      setDisplayProperties(new PolygonDisplayProperties());
  }

  /** Returns a number indicating the oriented area between the function graph and the x-axis. */
  public double integral() {
    double area = 0;
    for (int i = 0; i < m_distribution.length - 1; i++) {
      area += m_distributionValues[i]
        * (m_distribution[i + 1] - m_distribution[i]);
    }
    return area;
  }

  /** For debugging purposes, prints out the first and the last distribution point with value.*/
  public String toString() {
    return getLabel()
      + "(x) = "
      + m_distribution[0]
      + ", "
      + m_distributionValues[0]
      + " .."
      + m_distribution[m_distribution.length
      - 2]
      + ", "
      + m_distributionValues[m_distribution.length
      - 2]
      + ", D("
      + getLabel()
      + ") = "
      + getBorelSet().toString();
  }

  private static double[] copyArray(double[] src) {
    double[] dest = new double[src.length];
    System.arraycopy(src, 0, dest, 0, src.length);
    return dest;
  }

  /** Returns the distribution for this step function. */
  public double[] getDistribution() {
    return m_distribution;
  }

  /** Returns the distribution values for this step function. */
  public double[] getDistributionValues() {
    return m_distributionValues;
  }

  /** Sets the distribution for this step function. */
  public void setDistribution(double[] ds) {
    m_distribution = copyArray(ds);
  }

  /** Sets the distribution values for this step function. */
  public void setDistributionValues(double[] ds) {
    m_distributionValues = copyArray(ds);
  }

  /** Returns the type of the approximation used (See constants above for values). */
  public int getType() {
    return m_type;
  }

  /** Sets the type of the approximation used (See constants above for values). */
  public void setType(int i) {
    m_type = i;
  }

  /** 
   * Sets this step function to the approximation of the given function using the functions
   * domain and the current distribution length.
   */
  public void approximate(FunctionOverBorelSetIF function) {
    approximate(
      function,
      m_borelSet.getLowerBound().getDouble(),
      m_borelSet.getUpperBound().getDouble(),
      m_distribution.length - 1);
  }
  
  /** Sets this step function to the approximation of the given function. */
  public void approximate(
    FunctionOverRIF function,
    double lowerBound,
    double upperBound,
    int steps) {
    m_borelSet =
      new FiniteBorelSet(
        new Interval(
          MDouble.class,
          lowerBound,
          Interval.CLOSED,
          upperBound,
          Interval.CLOSED));
    if (function instanceof FunctionOverBorelSetIF)
      m_borelSet.intersect(
        ((FunctionOverBorelSetIF) function).getBorelSet());
    double intervalLength = upperBound - lowerBound;
    if (steps != m_distribution.length - 1) {
      m_distribution = new double[steps + 1];
      m_distributionValues = new double[steps];
    }
    for (int i = 0; i < steps; i++) {
      m_distribution[i] = lowerBound + intervalLength / steps * i;
      m_distribution[i + 1] =
        lowerBound + intervalLength / steps * (i + 1);
      if (m_type == LOWER) {
        double f_min = Double.POSITIVE_INFINITY;
        for (int j = 0; j < 20; j++)
          f_min =
            Math.min(
              f_min,
              function.evaluate(
                m_distribution[i]
                  + (m_distribution[i + 1] - m_distribution[i])
                    * j
                    / 20));
        m_distributionValues[i] = f_min;
      }
      else if (m_type == UPPER) {
        double f_max = Double.NEGATIVE_INFINITY;
        for (int j = 0; j < 20; j++)
          f_max =
            Math.max(
              f_max,
              function.evaluate(
                m_distribution[i]
                  + (m_distribution[i + 1] - m_distribution[i])
                    * j
                    / 20));
        m_distributionValues[i] = f_max;
      }
      else if (m_type == MEAN)
        m_distributionValues[i] =
          function.evaluate(
            (m_distribution[i + 1] + m_distribution[i]) / 2);
      else if (m_type == FUNCTIONMEAN)
        m_distributionValues[i] =
          (function.evaluate(m_distribution[i + 1])
            + function.evaluate(m_distribution[i]))
            / 2;
      else if (m_type == LEFT)
        m_distributionValues[i] = function.evaluate(m_distribution[i]);
      else if (m_type == RIGHT)
        m_distributionValues[i] =
          function.evaluate(m_distribution[i + 1]);
      else
        throw new IllegalStateException("no valid type specified!");
    }
    m_distribution[steps] =
      m_distribution[steps - 1] + intervalLength / steps;
  }

	public FiniteBorelSet getBorelSet() {
		if (m_borelSet == null)
			m_borelSet = FiniteBorelSet.getRealAxis(MDouble.class);
		return m_borelSet;
	}

	public NumberSetIF getDomain() {
		return getBorelSet();
	}

	public double evaluate(double x) {
		for (int i = 0; i < m_distribution.length - 1; i++) {
			if (m_distribution[i] <= x && m_distribution[i + 1] >= x) {
				//System.out.println("evaluate of "+x+" is "+m_distributionValues[i]);
				return m_distributionValues[i];
			}
		}
		return 0;
	}

	public void evaluate(double[] xin, double[] yout) {
		for (int i = 0; i < xin.length; i++)
			yout[i] = evaluate(xin[i]);
	}

	public void evaluate(MNumber xin, MNumber yout) {
		yout.setDouble(evaluate(xin.getDouble()));
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

	public int getVerticesCount() {
		return MMOneChainInRIF.DEFAULT_VERTICES_COUNT;
	}

	public void setVerticesCount(int aNumber) {
		throw new UnsupportedOperationException("use approximate() instead!");
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

	public FunctionOverRIF getFunctionExpression(int index) {
		return this;
	}

	public Class getNumberClass() {
		return MDouble.class;
	}

  public void setBorelSet(FiniteBorelSet set) {
    m_borelSet = set;
  }
}
