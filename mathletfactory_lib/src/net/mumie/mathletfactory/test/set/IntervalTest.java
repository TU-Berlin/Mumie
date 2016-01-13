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

package net.mumie.mathletfactory.test.set;

import java.awt.Color;
import java.awt.Font;
import java.util.logging.Level;

import net.mumie.mathletfactory.action.handler.Affine2DMouseTranslateHandler;
import net.mumie.mathletfactory.action.updater.DependencyAdapter;
import net.mumie.mathletfactory.appletskeleton.g2d.SingleG2DCanvasApplet;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.PointDisplayProperties;
import net.mumie.mathletfactory.display.PolygonDisplayProperties;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.set.FiniteBorelSet;
import net.mumie.mathletfactory.math.set.Interval;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.analysis.function.MMFunctionDefByOp;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPoint;
import net.mumie.mathletfactory.mmobject.geom.affine.MMCoordinateSystem;
import net.mumie.mathletfactory.mmobject.number.MMDouble;
import net.mumie.mathletfactory.mmobject.set.MMInterval;
import net.mumie.mathletfactory.util.BasicApplicationFrame;

public class IntervalTest extends SingleG2DCanvasApplet {

  private Class m_numberClass = MDouble.class;
  private final String m_initialExpression = "sin(x)";
  private final double m_initLowerBound = -2 * Math.PI;
  private final double m_initUpperBound = 2 * Math.PI;
  private final double m_initialCanvasDim =
    (m_initUpperBound - m_initLowerBound);
  private MMFunctionDefByOp m_function;
  private MMAffine2DPoint m_lower, m_upper;
  private MMInterval m_defInterval;
  private MMDouble xPos = new MMDouble();
  private MMDouble yPos = new MMDouble();

  public void init() {
    super.init();

    setTitle("MMInterval and MMDouble Test");
    setLoggerLevel(Level.WARNING);
    getCanvas2D().addObject(new MMCoordinateSystem());
    createObjects();

    initializeObjects();
    defineDependencies();
    getCanvas2D().addObject(m_defInterval);
    getCanvas2D().addObject(m_function);
    getCanvas2D().addObject(m_lower);
    getCanvas2D().addObject(m_upper);
    addMMObjectAsContainerContent(m_function);
    insertLineBreak();
    addText("x " + '\u2208');
    addMMObjectAsContainerContent(m_defInterval);
    insertLineBreak();
    addText("f(");
    addMMObjectAsContainerContent(xPos);
    addText(") = ");
    addMMObjectAsContainerContent(yPos);
    addResetButton();

  }

  private void createObjects() {
    m_function = new MMFunctionDefByOp(m_numberClass, m_initialExpression);
    m_function.setEditable(true);
    DisplayProperties props = m_function.getDisplayProperties();
    props.setFont(new Font("Serif", Font.PLAIN, 18));
    m_function.setDisplayProperties(props);

    Affine2DMouseTranslateHandler amth =
      new Affine2DMouseTranslateHandler(getCanvas2D());
    amth.setinXOnly(true);
    m_lower = new MMAffine2DPoint(m_numberClass);
    m_lower.addHandler(amth);
    m_upper = new MMAffine2DPoint(m_numberClass);
    m_upper.addHandler(amth);
    m_defInterval =
      new MMInterval(
        MMDouble.class,
        m_initLowerBound,
        Interval.CLOSED,
        m_initUpperBound,
        Interval.OPEN, 
        Interval.SQUARE);
  }

  public void initializeObjects() {
    xPos.setEditable(true);
    m_function.setOperation(m_initialExpression);
    m_function.setVerticesCount(500);
    m_function.setBoundarysVisible(false);
    PolygonDisplayProperties prop = new PolygonDisplayProperties();
    prop.setObjectColor(Color.blue);
    prop.setBorderColor(Color.blue);
    prop.setAntiAliasing(false);
    m_function.setDisplayProperties(prop);
    PointDisplayProperties pointProp = new PointDisplayProperties();
    pointProp.setObjectColor(Color.red);
    pointProp.setPointRadius(3);
    pointProp.setAntiAliasing(false);
    m_lower.setXY(m_initLowerBound, 0);
    m_upper.setXY(m_initUpperBound, 0);
    m_lower.setDisplayProperties(pointProp);
    m_upper.setDisplayProperties(pointProp);
    getCanvas2D().getW2STransformationHandler().setUniformWorldDim(
      m_initialCanvasDim);
    m_defInterval.setEditable(true);
    DisplayProperties props = m_defInterval.getDisplayProperties();
    props.setFont(new Font("Serif", Font.PLAIN, 18));
    props.setAntiAliasing(false);
    m_defInterval.setDisplayProperties(props);

    m_defInterval.setBoundary(
      m_initLowerBound,
      Interval.CLOSED,
      m_initUpperBound,
      Interval.OPEN, 
      Interval.SQUARE);
  }

  private void defineDependencies() {
    m_function
      .dependsOn(new MMInterval[] { m_defInterval }, new DependencyAdapter() {
      public void doUpdate() {
        m_function.setBorelSet(new FiniteBorelSet(m_defInterval));
      }
    });
    m_defInterval.dependsOn(m_lower, new DependencyAdapter() {
      public void doUpdate() {
        m_defInterval.setLowerBoundary(
          m_lower.getAffineCoordinatesOfPoint().getEntry(1),
          Interval.CLOSED);
      }
    });
    m_defInterval.dependsOn(m_upper, new DependencyAdapter() {
      public void doUpdate() {
        m_defInterval.setUpperBoundary(
          m_upper.getAffineCoordinatesOfPoint().getEntry(1),
          Interval.OPEN);
      }
    });
    yPos
      .dependsOn(
        new MMObjectIF[] { xPos, m_function },
        new DependencyAdapter() {
      public void doUpdate() {
        yPos.setDouble(m_function.evaluate(xPos.getDouble()));
      }
    });

    m_lower
      .dependsOn(new MMInterval[] { m_defInterval }, new DependencyAdapter() {
      public void doUpdate() {
        m_lower.setVisible(true);
        m_lower.setXY(m_defInterval.getLowerBoundaryVal().getDouble(), 0);
        if (m_defInterval.getLowerBoundaryVal().isInfinity()) {
          m_lower.setVisible(false);
        }
      }
    });

    m_upper
      .dependsOn(new MMInterval[] { m_defInterval }, new DependencyAdapter() {
      public void doUpdate() {
        m_upper.setVisible(true);
        m_upper.setXY(m_defInterval.getUpperBoundaryVal().getDouble(), 0);
        if (m_defInterval.getUpperBoundaryVal().isInfinity()) {
          m_upper.setVisible(false);
        }
      }
    });
  }

  public void reset() {
    initializeObjects();
    defineDependencies();
    m_lower.invokeUpdaters();
    m_upper.invokeUpdaters();
    m_defInterval.invokeUpdaters();
    m_defInterval.render();
    getCanvas2D().renderScene();
    getCanvas2D().repaint();
  }

  /**
   * Starting point if applet runs as an application.
   */
  public static void main(String[] args) {
    IntervalTest myApplet = new IntervalTest();
    BasicApplicationFrame f = new BasicApplicationFrame(myApplet, 600);
    myApplet.init();
    myApplet.start();
    f.pack();
    f.setVisible(true);
  }
}
