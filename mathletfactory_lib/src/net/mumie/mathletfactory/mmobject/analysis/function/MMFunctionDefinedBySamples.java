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

import java.util.Arrays;
import java.util.Comparator;

import net.mumie.mathletfactory.display.PolygonDisplayProperties;
import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.analysis.function.FunctionOverBorelSetIF;
import net.mumie.mathletfactory.math.analysis.function.FunctionOverRIF;
import net.mumie.mathletfactory.math.geom.affine.Affine2DPoint;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.set.FiniteBorelSet;
import net.mumie.mathletfactory.math.set.Interval;
import net.mumie.mathletfactory.math.set.NumberSetIF;
import net.mumie.mathletfactory.math.util.MathUtilLib;
import net.mumie.mathletfactory.mmobject.MMDefaultCanvasObject;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 * This class is the base for all functions that are defined by a discrete set of points.
 * Note that this also implies continuous functions like splines.
 * 
 * @author vossbeck, Paehler
 * @mm.docstatus finished
 */
public class MMFunctionDefinedBySamples
	extends MMDefaultCanvasObject
	implements MMOneChainInRIF, FunctionOverBorelSetIF {

    /** determines the number type this function is based on */
    private Class m_numberClass;

    /**
    * <code>m_borelSet</code> defines the domain for this instance of
    * <code>FunctionOverBorelSet</code>.
    */
    protected FiniteBorelSet m_borelSet;

    /**
     * This array defines the sample data due to each instance of
     * <code>FunctionDefinedBySamples</code>. This array is always expected to be
     * ordered in ascending x coordinate, i.e. any method manipulating these data
     * should also resort this array, use the inner class {@link Affine2DPointComparator}
     * for this issue.
     */
    protected Affine2DPoint[] m_dataPoints;

    /**
     * This array will hold the x coordinates of {@link m_dataPoints}. In some
     * algorithms it is favourably to use the inner data this way. But make sure
     * to adjust this array before usage (see @link #adjustDoubleArrays()} )
     * because only {@link m_dataPoints} store the actual data
     */
    protected double[] m_xVals;

    /**
     * This array will hold the y coordinates of {@link m_dataPoints}. In some
     * algorithms it is favourably to use the inner data this way. But make sure
     * to adjust this array before usage (see @link #adjustDoubleArrays()} )
     * because only {@link m_dataPoints} store the actual data
     */
    protected double[] m_yVals;

    /**
     * <code>pointComparator</code> is used to resort the array of
     * <code>Affine2DPoint</code>s due to their x coordinate.
     */
    private Affine2DPointComparator pointComparator =
      new Affine2DPointComparator();

    /** Flag indicating that the points may not be in sorted order. */
    protected boolean m_dataPointsNeedResort = true;

    /** Flag indicating that the double array needs to be reconstructed from the points array. */
    protected boolean m_doubleArraysNeedAdjust = true;

    /**
     * Generates a <code>FunctionDefinedBySamples</code> <tt>f</tt> with <tt>f(xVals
     * (i)) = yVals(i). This function \quot;lives\quot; on exactly these xValues i.
     * e. has the BorelSet [xVals(0)]&cup;[xVals(1)]&cup;...&cup;[xVals(n)] as
     * domain.
     */
    public MMFunctionDefinedBySamples(
      Class numberClass,
      double[] xVals,
      double[] yVals) {
      m_numberClass = numberClass;
      if (xVals.length == yVals.length) {
        m_dataPoints = new Affine2DPoint[xVals.length];
        for (int i = 0; i < m_dataPoints.length; i++) {
          m_dataPoints[i] = new Affine2DPoint(numberClass, xVals[i], yVals[i]);
        }
        adjustInternalData();
      }
      else
        throw new IllegalArgumentException("number of x- and y samples was not equal");
      PolygonDisplayProperties p = new PolygonDisplayProperties();
      setDisplayProperties(p);
    }

    /**
     * Constructs an instance of <code>FunctionDefinedBySamples</code> based on
     * the given <code>points</code> as samples. These points may traversed
     * disordered, but internally they will be resorted with respect to ascending
     * x coordinate.
     */
    public MMFunctionDefinedBySamples(Affine2DPoint[] points) {
      //  expecting all affine points have the same entryClass,
      // we take the first AffinePoint in the list to determine it:
      m_numberClass = points[0].getNumberClass();
      m_dataPoints = new Affine2DPoint[points.length];
      for (int i = 0; i < points.length; i++) {
        m_dataPoints[i] = (Affine2DPoint) points[i].deepCopy();
      }
      adjustInternalData();
      PolygonDisplayProperties p = new PolygonDisplayProperties();
      setDisplayProperties(p);
    }

    public Class getNumberClass() {
      return m_numberClass;
    }

    public NumberSetIF getDomain() {
      return getBorelSet();
    }

    public FiniteBorelSet getBorelSet() {
      return m_borelSet;
    }

    /**
     * This method generates the domain, a BorelSet, from the xVals. For the
     * general <code>FunctionDefinedBySamples</code> this <code>BorelSet</code> is
     * simply the union of the x values of the sample pairs, that are interpreted
     * as closed one-point-interval.
     * <br>
     *  This method should be owerwritten in inheriting classes  (i.e. splines).
     */
    protected void adjustBorelDomain() {
      Interval[] intervals = new Interval[getSamplesCount()];
      for (int i = 0; i < getSamplesCount(); i++) {
        intervals[i] =
          new Interval(
            m_numberClass,
            m_dataPoints[i].getXAsDouble(),
            Interval.CLOSED,
            m_dataPoints[i].getXAsDouble(),
            Interval.CLOSED, 
            Interval.SQUARE);
      }
      m_borelSet = new FiniteBorelSet(intervals);
    }

    /**
     * This method resorts the affine points defining this sampled function (see
     * {@link #m_dataPoints}) with respect to their x coordinate in ascending
     * order.
     */
    protected void resortPoints() {
      Arrays.sort(m_dataPoints, pointComparator);
      m_dataPointsNeedResort = false;
    }
    
    /** Constructs the double array from the points array. */
    protected void adjustDoubleArrays() {
      // we always ensure m_xVals.length == m_yVals.length
      if (m_xVals == null || m_xVals.length != getSamplesCount()) {
        m_xVals = new double[getSamplesCount()];
        m_yVals = new double[getSamplesCount()];
      }
      for (int i = 0; i < getSamplesCount(); i++) {
        m_xVals[i] = m_dataPoints[i].getXAsDouble();
        m_yVals[i] = m_dataPoints[i].getYAsDouble();
      }
      m_doubleArraysNeedAdjust = false;
    }

    /** Resorts the point array, constructs the double array from it and adjusts the borel domain. */
    protected void adjustInternalData() {
      resortPoints();
      adjustDoubleArrays();
      adjustBorelDomain();
    }

    public double evaluate(double x) {
      for (int i = 0; i < getSamplesCount(); i++)
        if (x == m_xVals[i])
          return m_yVals[i];
      return Double.NaN; // function is only defined on the samples
    }

    public void evaluate(double[] xin, double[] yout) {
      for (int i = 0; i < xin.length; ++i) {
        yout[i] = evaluate(xin[i]);
      }
    }

    public void evaluate(MNumber xin, MNumber yout) {
      for (int i = 0; i < getSamplesCount(); i++)
        if (xin
          .equals(m_dataPoints[i].getAffineCoordinatesOfPoint().getEntryRef(1))) {
          yout.set(
            m_dataPoints[i].getAffineCoordinatesOfPoint().getEntryRef(2));
          return;
        }
      yout.setDouble(Double.NaN);
    }

    /**
     * Returns the number of samples due to this instance of
     * <code>FunctionDefinedBySamples</code>.
     */
    public int getSamplesCount() {
      return m_dataPoints.length;
    }

    /**
     * Fills <code>p</code> with the coordinates of the <code>i</code>th sample
     * point, if <code>i</code> is a valid sample index and if <code>p</code> and
     * this instance of <code>FunctionDefinedBySamples</code> are of the same
     * number type (see {@link #getNumberClass()} for more information about this
     * topic).
     */
    public void getSamplePoint(int i, Affine2DPoint p) {
      if (getNumberClass().equals(p.getNumberClass())) {
        if (i >= 0 && i < getSamplesCount()) {
          p.setFromPoint(m_dataPoints[i]);
        }
        else {
          throw new IllegalArgumentException(
            "Index " + i + " is not appropriate for adjusting for this Function");
        }
      }
      else {
        throw new IllegalArgumentException(
          "Function has "
            + getNumberClass().getName()
            + " as number class, whereas the affine poinit is of type "
            + p.getNumberClass().getName());
      }
    }

    /**
     * The <code>i</code>th sample pair will be set to <code>x,y</code>, if
     * <code>i</code> is appropriate for this function. Observe however, that the
     * samples might be reorderd!
     */
    public MMFunctionDefinedBySamples setSample(int i, double x, double y) {
      if (i >= 0 && i < getSamplesCount()) {
        m_dataPoints[i].setXY(x, y);
        adjustInternalData();
        return this;
      }
      else {
        throw new IllegalArgumentException(
          "Index " + i + " is not appropriate for adjusting for this Function");
      }
    }

    /**
     * The <code>i</code>th sample pair will be set to <code>p.x,p.y</code>, if
     * <code>i</code> is an appropriate sample index for this function. Observe
     * however, that the samples might be reordered, i.e. it is not guaranted
     */
    public MMFunctionDefinedBySamples setSample(int i, Affine2DPoint p) {
      if (i >= 0 && i < getSamplesCount()) {
        m_dataPoints[i].setFromPoint(p);
        adjustInternalData();
        return this;
      }
      else {
        throw new IllegalArgumentException(
          "Index " + i + " is not appropriate for adjusting for this Function");
      }
    }

    /** Returns the minimum x value, this function is defined for. */
    public double getXminDouble() {
      return m_dataPoints[0].getXAsDouble();
    }

    /** Returns the minimum x value, this function is defined for. */
    public MNumber getXmin() {
      return m_dataPoints[0].getAffineCoordinatesOfPoint().getEntryRef(1);
    }
    
    /** Returns the maximum x value, this function is defined for. */
    public double getXmaxDouble() {
      return m_dataPoints[getSamplesCount() - 1].getXAsDouble();
    }

    /** Returns the maximum x value, this function is defined for. */
    public MNumber getXmax() {
      return m_dataPoints[getSamplesCount()
        - 1].getAffineCoordinatesOfPoint().getEntryRef(1);
    }

    /**
     * After calling this method, this instance
     * of <code>FunctionDefinedBySamples</code> will be defined by the map
     * points[i]_x --> points[i]_y, i.e. <code>points</code> determine the samples
     * for the function.
     */
    public void setFromPoints(Affine2DPoint[] points) {
      if (getSamplesCount() != points.length) {
        m_dataPoints = new Affine2DPoint[points.length];
        for (int i = 0; i < points.length; ++i) {
          m_dataPoints[i] = (Affine2DPoint) points[i].deepCopy();
        }
      }
      else {
        for (int i = 0; i < getSamplesCount(); i++)
          m_dataPoints[i].setFromPoint(points[i]);
      }
      adjustInternalData();
    }

    /**
     * In general this method should return the result f(x) of this
     * function for <i>x[i-1] <= x < x[i]</i> with
     * <i>x[i]</i> being the <code>i</code>th sample point. In this class, this
     * method simply returns y[i], independently of <i>x</i>. It should
     * be overwritten in subclasses (i.e. splines).
     */
    protected double specificEvaluate(double x, int i) {
      //observe index is based on fortran-indexing, m_yVals[] uses standard java-indexing,
      // i.e. index i means that x is element of [x[i-1],x[i]):
      return m_yVals[i - 1];
    }

    /**
     * In general this method should return the result f(x) of this
     * function for <i>x[i-1] <= x < x[i]</i> with
     * <i>x[i]</i> being the <code>i</code>th sample point. In this class, this
     * method simply returns y[i], independently of <i>x</i>. It should
     * be overwritten in subclasses (i.e. splines).
     */
    protected void specificEvaluate(MNumber xin, MNumber yout, int i) {
      yout.set(
        m_dataPoints[i - 1].getAffineCoordinatesOfPoint().getEntryRef(2));
    }

    /**
     * Returns a {@link net.mumie.mathletfactory.util.math.NumberMatrix} holding
     * the (affine) coordinates of the <code>i</code>th sample as it's
     * <code>i</code>th row.
     */
    public NumberMatrix exportSamplesAsColumnMatrix() {
      NumberMatrix samplesAsTable =
        new NumberMatrix(getNumberClass(), 2, getSamplesCount());
      for (int i = 0; i < getSamplesCount(); i++) {
        samplesAsTable.setRowVector(
          i + 1,
          m_dataPoints[i].getAffineCoordinatesOfPoint());
      }
      return samplesAsTable;
    }
  
    /** Returns a string representation of the samples. */
    public String toString() {
      return exportSamplesAsColumnMatrix().toString();
    }

    private class Affine2DPointComparator implements Comparator {
      public int compare(Object o1, Object o2) {
        Affine2DPoint p1 = (Affine2DPoint) o1;
        Affine2DPoint p2 = (Affine2DPoint) o2;
        return MathUtilLib.sign(p1.getXAsDouble() - p2.getXAsDouble());
      }
    }

  /** The number of vertices to be displayed for this function. */
	protected int m_vertices_count_in_drawable =
		MMOneChainInRIF.DEFAULT_VERTICES_COUNT;

  /** Whether the boundaries of the domain should be made visible by the display system. */
	protected boolean m_BoundaryVisibility;


	public int getDefaultTransformType() {
		return GeneralTransformer.ONE_CHAIN_1D_AFFINE_GRAPH_TRANSFORM;
	}

	public int getDefaultTransformTypeInCanvas() {
		return GeneralTransformer.ONE_CHAIN_1D_AFFINE_GRAPH_TRANSFORM;
	}

	public int getDefaultTransformTypeAsContainer() {
		return GeneralTransformer.NO_TRANSFORM_TYPE;
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

	public FunctionOverRIF getFunctionExpression(int index) {
		return this;
	}

	public FiniteBorelSet getBorelSet(int index) {
		return getBorelSet();
	}

	public int getExpressionCount() {
		return 1;
	}

	public int getIntervalCount(int index) {
		return getBorelSet().getIntervalCount();
	}

	public int getIntervalCountInComponent(int indexOfPart) {
		return getBorelSet(indexOfPart).getIntervalCount();
	}

  public boolean areBoundarysVisible(){
    return m_BoundaryVisibility;
  }
  
  public void setBoundarysVisible(boolean b){
    m_BoundaryVisibility = b;
  }
    
  public int getVerticesCount(){
    return m_vertices_count_in_drawable;
  }

  public void setVerticesCount(int i){
    m_vertices_count_in_drawable = i;
  }
  
  public void setBorelSet(FiniteBorelSet set) {
    m_borelSet = set;
  }
}
