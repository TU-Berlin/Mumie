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

package net.mumie.mathletfactory.transformer.g2d;

import net.mumie.mathletfactory.action.message.TodoException;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.PolygonDisplayProperties;
import net.mumie.mathletfactory.display.g2d.G2DBracketDrawable;
import net.mumie.mathletfactory.display.g2d.G2DPolygonDrawable;
import net.mumie.mathletfactory.math.analysis.function.FunctionOverRIF;
import net.mumie.mathletfactory.math.number.NumberTypeDependentIF;
import net.mumie.mathletfactory.math.set.Interval;
import net.mumie.mathletfactory.math.util.FunctionRenderLib;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.analysis.function.MMOneChainInRIF;
import net.mumie.mathletfactory.transformer.Canvas2DObjectTransformer;

/**
 * <code>OneChainInRTransformer</code> is the common transformer class (see
 * {@link net.mumie.mathletfactory.transformer.GeneralTransformer} for more
 * general information about transformers) able to work for any
 * {@link net.mumie.mathletfactory.mmobject.MMObjectIF}
 *  that (directly or by inheritance)
 * implements
 * {@link net.mumie.mathletfactory.mmobject.analysis.function.MMOneChainInRIF}.
 * <br>
 * Due to the specification of <code>MMOneChainInRIF</code> the master object
 * contains some functions defined on domains of type <code>BorelSet</code>, i.
 * e. f_i: B_i -- > R. These functions will be visualised as their graph in 
 * R^2, this transformer will render 2d space curves of type (t,f(t)) with t 
 * being element of a borel set.
 * <br>
 * Internally these curves will be further devided with respect to the
 * decomposition of the underlying <code>BorelSet</code> into it's defining
 * intervals. Each of these 2d space curves will be rendered as {@link net.
 * mumie.mathletfactory.display.g2d.G2DPolygonDrawable} with it's number
 * of vertices equal to
 * {@link net.mumie.mathletfactory.mmobject.analysis.function.MMOneChainInRIF#getVerticesCount()}.
 * <br>
 * Further on the transformer may probably split the above mentioned intervals
 * due to the visibility of the 2d space curve on the actual
 * <code>MMCanvas</code>.
 * <br>
 *
 * @author vossbeck
 * @mm.docstatus finished
 */
public class OneChainInRTransformer extends Canvas2DObjectTransformer {

  /* used for rendering purposes:
   * The part on which the Chain-Components will be evaluated is increased by
   * tmin --> tmin-m_relOffSet*(tmax-tmin)
   * tmax --> tmax+m_relOffSet*(tmax-tmin)
   */
  private final double m_relOffSet = 0.1;

  /**
   * wx, wy store the world draw coordinates of the discretised part due to a
   * single function part f of the MMOneChainInRIF on a single interval of the
   * BorelDomain of f. using a world2Screen() method (wx,wy) will be traversed
   * to be the vertex coordinates of the corresponding
   * G2DPolygonDrawable.
   */
  private double[] wxAll, wyAll;

  private int m_allIntervalCount;
  /**
   * Holds the number of vertices that shall be used for discretisation.
   */
  private int m_verticesCount;

  /**
   * Stores the number of expressions of the <code>MMOneChainInRIF</code> this
   * transformer is working for. In the synchronizeMath2Screen() method the
   * transformer can by this decide whether to reinitialize its datafields or
   * not.
   */
  private int m_componentsInChainCount;

  /**
   * Stores the number of intervals in the <code>BorelSet</code> for each
   * expression of the <code>MMOneChainInRIF</code> (i.e. the length of this
   * array is equal to m_partsInChainCount.
   */
  private int[] m_intervalCountInComponent;

  /**
   * Holds the lower and upper bounds of the "renderable" intervals (i.e. 
   * where function evaluation results in visible points on the screen
   */
  private double[][] m_splitIntervalLimits;
  /** holds the number of vertices for discretization per renderable interval*/
  private int[] m_splitVerticesCount;

  /**
   * The <code>IndicatorFunction</code> is implemented as an inner class of
   * <code>OneChainInRTransformer</code>. It is needed by an external
   * method call in class
   * {@link net.mumie.mathletfactory.util.math.FunctionRenderLib}
   * during the visualising algorithm. Briefly spoken, it will determine if the
   * pair (t,f(t)) is visible in the canvas or not, but see
   * {@link IndicatorFunction} for more information.
   */
  private IndicatorFunction m_indicatorFunction;

  /**
   * Overrides the implementation of
   * {@link net.mumie.mathletfactory.transformer.GeneralTransformer#initialize
   * (net.mumie.mathletfactory.mmobject.MMObjectIF)}
   * due to the fact that any
   * <code>MMOneChainTransformer</code> can only work for instances of
   * {@link net.mumie.mathletfactory.mmobject.analysis.function.MMOneChainInRIF}.
   *
   * @see net.mumie.mathletfactory.transformer.GeneralTransformer#initialize(net.mumie.mathletfactory.mmobject.MMObjectIF)
   */
  public void initialize(MMObjectIF masterObject) {
    if (masterObject instanceof MMOneChainInRIF) {
      super.initialize(masterObject);
      m_indicatorFunction = new IndicatorFunction();
      initAllDataFields();
    } else
      throw new IllegalArgumentException(
        getClass().getName()
          + "::init(): can only work for instances of MMOneChainInRIF, but I got "
          + masterObject.getClass().getName());
  }

  /**
   * This method returns the master object (see
   * {@link net.mumie.mathletfactory.transformer.GeneralTransformer#getMaster()}
   * cast to the greatest common interface possible. All <code>MMObject</code>s
   * this transformer is working for must implement the interface
   * <code>MMOneChainInRIF</code>.
   */
  private MMOneChainInRIF getRealMasterObject() {
    return (MMOneChainInRIF) getMaster();
  }

  /**
   * Keep in mind that this method must always be called, when there is a 
   * change either in the number of parts in the current 
   * <code>MMOneChainInRIF</code> or in the number of intervals in the 
   * BorelSet of an already existing expression.
   */
  private void initAllDataFields() {
    MMOneChainInRIF master = getRealMasterObject();
    m_verticesCount = master.getVerticesCount();
    m_allIntervalCount = master.getAllIntervalCount();
    m_componentsInChainCount = master.getComponentsCount();
    m_intervalCountInComponent = new int[m_componentsInChainCount];
    for (int i = 0; i < m_componentsInChainCount; i++) {
      m_intervalCountInComponent[i] = master.getIntervalCountInComponent(i);
    }
    m_activeDrawable = new G2DPolygonDrawable(m_verticesCount * m_allIntervalCount);
    m_additionalDrawables = new G2DBracketDrawable[m_allIntervalCount * 2];
    m_additionalProperties = new DisplayProperties[m_allIntervalCount * 2];
    for (int i = 0; i < m_allIntervalCount * 2; i++){    
      m_additionalDrawables[i] = new G2DBracketDrawable();
      m_additionalProperties[i] = new PolygonDisplayProperties();
    }
    wxAll = new double[m_verticesCount * m_allIntervalCount];
    wyAll = new double[m_verticesCount * m_allIntervalCount];
    m_splitIntervalLimits = new double[m_verticesCount][2];
    m_splitVerticesCount = new int[m_verticesCount];
  }

  public void synchronizeWorld2Screen() {
    // bring the fields m_ll*, m_ur* up-to-date
    // only working if the canvas and screen are not
    // rotated versus each other.
    adjustWorldBounds();
    double xWorldMin = m_llInWorldDraw[0];
    double xWorldMax = m_urInWorldDraw[0];
    double yWorldMin = m_llInWorldDraw[1];
    double yWorldMax = m_urInWorldDraw[1];

    /* indicator function will be used later to retrieve the visible parts of 
       the curve*/
    m_indicatorFunction.setBounds(xWorldMin, xWorldMax, yWorldMin, yWorldMax);

    int indexInPolygonDrawable = 0;

    for (int expr = 0; expr < m_componentsInChainCount; expr++) {

      for (int i = 0; i < m_intervalCountInComponent[expr]; i++) {

        Interval currentInterval = getRealMasterObject().getInterval(expr, i);
        double lowerBound = currentInterval.getLowerBoundaryVal().getDouble();
        double upperBound = currentInterval.getUpperBoundaryVal().getDouble();

        // adjust bounds by a "dx" for open interval bounds
        if(currentInterval.getLowerBoundaryType() == Interval.OPEN)
          lowerBound += (m_urInWorldDraw[0] - m_llInWorldDraw[0])/10000; 
        if(currentInterval.getUpperBoundaryType() == Interval.OPEN)
          upperBound -= (m_urInWorldDraw[0] - m_llInWorldDraw[0])/10000; 

        FunctionOverRIF f = getRealMasterObject().getEvaluateExpressionInComponent(i);
        m_indicatorFunction.setFunction(f);

        double tminSecure =
          Math.max(xWorldMin - m_relOffSet * Math.abs(xWorldMax - xWorldMin), lowerBound);
        double tmaxSecure =
          Math.min(xWorldMax + m_relOffSet * Math.abs(xWorldMax - xWorldMin), upperBound);

        double deltaXInit = (tmaxSecure - tminSecure) / (m_verticesCount - 1);

        /*
         * now compute the separation of the parameter interval into
        * subintervals, in which all evaluated vertices will be visible on 
        * the screen.
         */
        int splitCount =
          FunctionRenderLib.findIntervals(
            m_indicatorFunction,
            tminSecure,
            tmaxSecure,
            m_verticesCount,
            m_splitIntervalLimits);

        /*
         * we currently assume splitCount >= 1:
         */

        FunctionRenderLib.computeVertexCountForSplits(
          m_verticesCount,
          splitCount,
          m_splitIntervalLimits,
          m_splitVerticesCount);

        int startIndex = indexInPolygonDrawable;
        for (int splitIndex = 0; splitIndex < splitCount; splitIndex++) {

          int nV = m_splitVerticesCount[splitIndex];
          double tmin = m_splitIntervalLimits[splitIndex][0];
          double tmax = m_splitIntervalLimits[splitIndex][1];
          double dT = (tmax - tmin) / (nV - 1);

          for (int j = 0; j < nV; j++) {
            /*
             * Observe, that two terms depend on the loop index j:
             * - the position in the arrays wx,wy
             * - the parameter t where the function must be evaluated
             */
            int currentIndexInArray = startIndex + j;
            double tj = tmin + j * dT;
            wxAll[currentIndexInArray] = tj;
            wyAll[currentIndexInArray] = f.evaluate(tj);
            double wxc = wxAll[currentIndexInArray];
            double wyc = wyAll[currentIndexInArray];
            /*
             * for j==0 we can assume wyc to be at finite because it results 
            * from the splitting routine, i.e. f.evaluate(tmin) is at least 
            * a finite value (generally not visible on the screen).
             *
             * Unfortunately, as a result of the re-discretization, we cannot 
            * exclude, that f(tj) might become "infinity" or "NaN". In this 
            * case we use the former point coordinates.
             */
            if (Double.isNaN(wyc) || Double.isInfinite(wyc)) {
              if(currentIndexInArray == 0){              
                wxAll[currentIndexInArray] = 0; // any better solution?
                wyAll[currentIndexInArray] = 0;
              } else {
                wxAll[currentIndexInArray] = wxAll[currentIndexInArray - 1];
                wyAll[currentIndexInArray] = wyAll[currentIndexInArray - 1];
              }
            }
            ((G2DPolygonDrawable) m_activeDrawable).setValid(currentIndexInArray, true);
          } // end of evaluation/discretization loop
           ((G2DPolygonDrawable) m_activeDrawable).setValid(startIndex, false);
          /* adjust the vertices values of the PolygonDrawable due to this 
          interval */
          startIndex += nV;
        } //end of splitting loop
        /* do not connect vertices belonging to different domain intervals in 
        the chain*/
         ((G2DPolygonDrawable) m_activeDrawable).setValid(indexInPolygonDrawable, false);
        indexInPolygonDrawable += m_verticesCount;
      } // end of intervals-per-expression loop
    } // end of expression loop
    world2Screen(
      wxAll,
      wyAll,
      ((G2DPolygonDrawable) m_activeDrawable).getXValsRef(),
      ((G2DPolygonDrawable) m_activeDrawable).getYValsRef());
      synchronizeIntervalBoundaryPoints();
  }

  public void synchronizeMath2Screen() {
    if (getRealMasterObject().getVerticesCount() != m_verticesCount
      || getRealMasterObject().getComponentsCount() != m_componentsInChainCount) {
      /*
       * If the number of expressions or the number of vertices in the
       * MMOneChain has changed, a total re-initialisation of the data fields 
       * is necessary.
       */
      initAllDataFields();
    } else {
      for (int i = 0; i < m_componentsInChainCount; i++) {
        if (getRealMasterObject().getIntervalCountInComponent(i)
          != m_intervalCountInComponent[i]) {
          initAllDataFields();
          break;
        }
      }
    }
    synchronizeWorld2Screen();
  }

  private void synchronizeIntervalBoundaryPoints() {
    int boundaryPointIndex = 0;
    double[] tmp = new double[2];
    G2DBracketDrawable p;
    for (int expr = 0; expr < m_componentsInChainCount; expr++) {

      for (int i = 0; i < m_intervalCountInComponent[expr]; i++) {

        Interval currentInterval = getRealMasterObject().getInterval(expr, i);
        double lowerBound = currentInterval.getLowerBoundaryVal().getDouble();
        double upperBound = currentInterval.getUpperBoundaryVal().getDouble();

        // adjust bounds by a "dx" for open interval bounds
        if(currentInterval.getLowerBoundaryType() == Interval.OPEN)
          lowerBound += (m_urInWorldDraw[0] - m_llInWorldDraw[0])/10000; 
        if(currentInterval.getUpperBoundaryType() == Interval.OPEN)
          upperBound -= (m_urInWorldDraw[0] - m_llInWorldDraw[0])/10000; 

        // render left bracket drawable
        p = (G2DBracketDrawable) m_additionalDrawables[boundaryPointIndex];
        p.setType(G2DBracketDrawable.LEFT);
        if (getRealMasterObject().getInterval(expr, i).isLowerClosed())
          p.setForm(G2DBracketDrawable.SQUARE);
        else 
          p.setForm(G2DBracketDrawable.ROUND);
        p.setVisible(getRealMasterObject().areBoundarysVisible());
        
        tmp[1] = getRealMasterObject().getEvaluateExpressionInComponent(expr).evaluate(lowerBound);
        if(tmp[1] > m_urInWorldDraw[1] || tmp[1] < m_llInWorldDraw[1])
          tmp[1] = 0;

        tmp[0] = lowerBound;
        world2Screen(tmp, p.getPoint());
        boundaryPointIndex++;

        // render right bracket drawable
        p = (G2DBracketDrawable) m_additionalDrawables[boundaryPointIndex];
        p.setType(G2DBracketDrawable.RIGHT);
        if (getRealMasterObject().getInterval(expr, i).isLowerClosed())
          p.setForm(G2DBracketDrawable.SQUARE);
        else 
          p.setForm(G2DBracketDrawable.ROUND);
        p.setVisible(getRealMasterObject().areBoundarysVisible()); 
        tmp[1] = getRealMasterObject().getEvaluateExpressionInComponent(expr).evaluate(upperBound);
        if(tmp[1] > m_urInWorldDraw[1] || tmp[1] < m_llInWorldDraw[1])
          tmp[1] = 0;
          
        tmp[0] = upperBound;
        world2Screen(tmp, p.getPoint());
        boundaryPointIndex++;
      }
    }
  }

  public void getScreenPointFromMath(
    NumberTypeDependentIF entity,
    double[] javaScreenCoordinates) {
    throw new TodoException();
  }

  public void getMathObjectFromScreen(
    double[] javaScreenCoordinates,
    NumberTypeDependentIF mathObject) {
    throw new TodoException();
  }

  /**
   * This inner class is used within the algorithm that determines the split of
   * the interval I with t --> (t,f(t)) (t in I) into subintervals I_k with
   * (t, f(t)) is visible on the screen for t in I_k
   */
  private class IndicatorFunction implements FunctionRenderLib.IntegerFunctionOverRIF {
    private double m_xmin, m_xmax, m_ymin, m_ymax;
    private FunctionOverRIF m_function;

    public void setFunction(FunctionOverRIF f) {
      m_function = f;
    }

    public void setBounds(double xmin, double xmax, double ymin, double ymax) {
      m_xmin = xmin;
      m_xmax = xmax;
      m_ymin = ymin;
      m_ymax = ymax;
    }

    public int evaluate(double tin) {
      double yt = m_function.evaluate(tin);
      // only test for y-value necessary because xmin<=xin<=xmax in all function calls:
      if (Double.isNaN(yt) || Double.isInfinite(yt))
        return 0;
      // from now we may expect xin, fx to be finite:
      if (tin >= m_xmin && tin <= m_xmax && yt >= m_ymin && yt <= m_ymax)
        return -1;
      return 1;
    }
  }
}
