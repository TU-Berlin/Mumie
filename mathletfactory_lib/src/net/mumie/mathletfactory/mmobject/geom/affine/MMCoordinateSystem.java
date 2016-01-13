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

package net.mumie.mathletfactory.mmobject.geom.affine;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import net.mumie.mathletfactory.action.message.TodoException;
import net.mumie.mathletfactory.display.LineDisplayProperties;
import net.mumie.mathletfactory.display.g2d.GridLineDescriptor;
import net.mumie.mathletfactory.math.util.MathUtilLib;
import net.mumie.mathletfactory.mmobject.MMDefaultCanvasObject;
import net.mumie.mathletfactory.transformer.GeneralTransformer;
import net.mumie.mathletfactory.transformer.g2d.Cartesian2DCoordinateSystemTransformer;
import net.mumie.mathletfactory.util.TickMark;

/**
 * This class represents a configurable 2D coordinate system.
 *
 * @author amsel, Paehler
 * @mm.docstatus finished
 */
public class MMCoordinateSystem extends MMDefaultCanvasObject {

  /** The constants for displaying the x-axis. */
  public final static int DISPLAY_X = 0x1;

  /** The constants for displaying the y-axis. */
  public final static int DISPLAY_Y = 0x2;

  /** The constants for displaying lines parallel to the the x-axis. */
  public final static int DISPLAY_GRIDLINES_X = 0x4;

  /** The constants for displaying lines parallel to the the y-axis. */
  public final static int DISPLAY_GRIDLINES_Y = 0x8;

  /** The flags for displaying the coordinate system. */
  private int m_displayStyle;

  private double m_centerX, m_centerY;

  private ArrayList xGridLineDescriptors = new ArrayList();
  private ArrayList yGridLineDescriptors = new ArrayList();

  private String m_xAxisLabel = "x", m_yAxisLabel = "y";

  /**
   * Default table for the choice of gridline distance. Posssible values are
   * 2, 2.5, 5, 10 and tenfolds of it.
   */
  public static final TickMark[] STANDARD_TABLE =
    new TickMark[] {
      new TickMark(MathUtilLib.log10(2), null),
      new TickMark(MathUtilLib.log10(2.5), null),
      new TickMark(MathUtilLib.log10(5), null),
      new TickMark(MathUtilLib.log10(10), null)
    };

  /**
   * Alternate table for gridline distance especially for trigonometric
   * functions. Possible values are PI/2, PI and tenfolds of it.
   */
  public static final TickMark[] TRIGONOMETRIC_TABLE =
    new TickMark[] {
      new TickMark(MathUtilLib.log10(Math.PI/2), TickMark.PI + "/2"),
      new TickMark(MathUtilLib.log10(Math.PI), TickMark.PI)
    };

  /**
   * Alternate table when whole numbers are displayed. Possible values are
   * 2,5,10 and tenfolds of it
   */
  public static final TickMark[] Z_TABLE =
    new TickMark[] {
      new TickMark(MathUtilLib.log10(2), null),
      new TickMark(MathUtilLib.log10(5), null),
      new TickMark(MathUtilLib.log10(10), null)
    };

  /**
   * Alternate table for purists :-). 1 and tenfolds of it are the only values
   * allowed
   */
  public static final TickMark[] SMALL_TABLE =
    new TickMark[] {
      new TickMark(MathUtilLib.log10(1), null)
    };

  private TickMark[] log10tableX = STANDARD_TABLE;

  private TickMark[] log10tableY = STANDARD_TABLE;

  private double minTickDistance = 50;

  /**
   *  Creates the coordinates system with the given display options (see constants) and
   *  center coordinates
   */
  public MMCoordinateSystem(int displayStyle, double centerX, double centerY){
    m_displayStyle = displayStyle;
    setDisplayProperties(new LineDisplayProperties(LineDisplayProperties.DEFAULT));
    m_centerX = centerX;
    m_centerY = centerY;
    getDisplayProperties().setFilled(false);
  }

  /** Creates a coordinate system centered at (0,0) with gridlines in both directions. */
  public MMCoordinateSystem() {
    this(DISPLAY_X | DISPLAY_Y | DISPLAY_GRIDLINES_X | DISPLAY_GRIDLINES_Y, 0,0);
  }


  public int getDefaultTransformType() {
    return GeneralTransformer.AFFINE_DEFAULT_TRANSFORM;
  }

  public int getDefaultTransformTypeInCanvas() {
    return GeneralTransformer.AFFINE_DEFAULT_TRANSFORM;
  }

  /** Adds the given gridline descriptor for the x axis. */
  public void addXGridLineDescriptor(GridLineDescriptor gridLine) {
    xGridLineDescriptors.add(gridLine);
  }

  /** Removes the given gridline descriptor for the x axis. */
  public void removeXGridLineDescriptor(GridLineDescriptor gridLine) {
    xGridLineDescriptors.remove(gridLine);
  }

  /** Adds the given gridline descriptor for the y axis. */
  public void addYGridLineDescriptor(GridLineDescriptor gridLine) {
    yGridLineDescriptors.add(gridLine);
  }

  /** Removes the given gridline descriptor for the y axis. */
  public void removeYGridLineDescriptor(GridLineDescriptor gridLine) {
    yGridLineDescriptors.remove(gridLine);
  }

  /** Returns all x gridline descriptors. */
  public ArrayList getXGridLineDescriptors() {
    return xGridLineDescriptors;
  }

  /** Returns all y gridline descriptors. */
  public ArrayList getYGridLineDescriptors() {
    return yGridLineDescriptors;
  }

	public Class getNumberClass() {
		throw new TodoException();
	}


  public Rectangle2D getWorldBoundingBox() {
    return null;
  }

  /** Returns the current logtable used for X-axis */
  public TickMark[] getLog10tableX() {
    return log10tableX;
  }

  /** Returns the current logtable used for Y-axis */
  public TickMark[] getLog10tableY() {
    return log10tableY;
  }

  /** Returns the current minimum distance between two grid lines */
  public double getMinTickDistance() {
    return minTickDistance;
  }

  /**
   * Set a table of log10 values to be used for choosing the distance between
   * grid lines on x-axis. One may use the predefined double array in this class
   * or give a own array. The array must contain values that satisfies the following
   * conditions:
   * <ul>
   * <li>the array must contain at least one value
   * <li>the values int the array have to be in ascending order
   * <li>the values must be in log10(1) &lt; x &let; log10(10);
   * </ul>
   */
  public void setLog10tableX(TickMark[] ds) {
    log10tableX = ds;
  }

  /**
   * Set a table of log10 values to be used for choosing the distance between
   * grid lines on y-axis. See {@link #setLog10tableX(TickMark[])} for details
   */
  public void setLog10tableY(TickMark[] ds) {
    log10tableY = ds;
  }

  /**
   * Set a table of log10 values to be used for choosing the distance between
   * grid lines on  both axis. See {@link #setLog10tableX(TickMark[])} for details
   */
  public void setLog10table(TickMark[] ds) {
    setLog10tableX(ds);
    setLog10tableY(ds);
  }

  /**
   * Sets the minimum distance between two gridlines on the screen.
   * @param d
   */
  public void setMinTickDistance(double d) {
    minTickDistance = d;
  }

  /** Returns the flags for displaying. See constant values for meaning. */
  public int getDisplayStyle() {
    return m_displayStyle;
  }

  /** Sets the flags for displaying. See constant values for meaning. */
  public void setDisplayStyle(int i) {
    m_displayStyle = i;
    if(getCanvas() != null)
      ((Cartesian2DCoordinateSystemTransformer)getDisplayTransformerMap().get(getCanvas())).setDisplayStyle();
  }

  /** Returns the x coordinate of the center. */
  public double getCenterX() {
    return m_centerX;
  }

  /** Returns the y coordinate of the center. */
  public double getCenterY() {
    return m_centerY;
  }

  /** Sets the x coordinate of the center. */
  public void setCenterX(double d) {
    m_centerX = d;
    ((Cartesian2DCoordinateSystemTransformer)getDisplayTransformerMap().get(getCanvas())).setDisplayStyle();
  }

  /** Sets the y coordinate of the center. */
  public void setCenterY(double d) {
    m_centerY = d;
    ((Cartesian2DCoordinateSystemTransformer)getDisplayTransformerMap().get(getCanvas())).setDisplayStyle();
  }

  /**
   * Returns the label that is displayed at the visible end of the positive X-Axis.
   */
  public String getXAxisLabel() {
    return m_xAxisLabel;
  }

  /**
   * Sets the label that is displayed at the visible end of the positive X-Axis.
   */
  public void setXAxisLabel(String string) {
    m_xAxisLabel = string;
  }

  /**
   * Returns the label that is displayed at the visible end of the positive X-Axis.
   */
  public String getYAxisLabel() {
    return m_yAxisLabel;
  }

  /**
   * Sets the label that is displayed at the visible end of the positive X-Axis.
   */
  public void setYAxisLabel(String string) {
    m_yAxisLabel = string;
  }

}
