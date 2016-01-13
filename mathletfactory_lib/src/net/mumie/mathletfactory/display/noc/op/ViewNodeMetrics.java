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

package net.mumie.mathletfactory.display.noc.op;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 *  This class contains the metric data of a
 *  {@link net.mumie.mathletfactory.display.noc.op.node.ViewNode}.
 *  This data contains the position of the baseline (the line over which a String
 *  without descending characters would be rendered), the ascent bounds (ranging
 *  in height from baseline to the uppermost pixels drawn and in width from the
 *  most left pixel to the most right pixel drawn).
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */

public class ViewNodeMetrics
{
  private Rectangle2D m_ascentBounds = new Rectangle2D.Double();
  private Rectangle2D m_descentBounds = new Rectangle2D.Double();
  private Rectangle2D m_bounds = new Rectangle2D.Double();
  
  public ViewNodeMetrics(){
  }
  
  /** Copy constructor. */
  public ViewNodeMetrics(ViewNodeMetrics metrics){
    setFrom(metrics);
  }
  
  public void setFrom(ViewNodeMetrics metrics){
    m_ascentBounds.setRect(metrics.getAscentBounds());
    m_descentBounds.setRect(metrics.getDescentBounds());
    m_bounds.setRect(metrics.getBounds());
  }
  
  public void setAscentBounds(Rectangle2D rect){
    m_ascentBounds = rect;
  }
  
  /**
   *  Sets the "ascent" of the metrics, i.e. the bounds of the part above the
   *  baseline. It is characterized as a rectangle with
   *  (x,y) as the upper left corner and with dimensions width and height.
   */
  public void setAscentBounds(double x, double y, double width, double height){
    m_ascentBounds.setRect(x,y,width, height);
  }

  /**
   *  Sets the "descent" of the metrics, i.e. the bounds of the part below the
   *  baseline. It is specified by the given rectangle.
   */
  public void setDescentBounds(Rectangle2D rect){
    m_descentBounds = rect;
  }
  
  /**
     *  Sets the "descent" of the metrics, i.e. the bounds of the part below the
     *  baseline. It is specified as a rectangle with (x,y) as the upper left 
     *  corner and with dimensions width and height.
     */
  public void setDescentBounds(double x, double y, double width, double height){
    m_descentBounds.setRect(x,y,width, height);
  }
  
  /**
   *  Returns the ascent bounds set by the corresponding view node as a rectangle.
   *  @see #setAscentBounds
   */
  public Rectangle2D getAscentBounds(){
    return m_ascentBounds;
  }
  
  /**
   *  Returns the descent bounds set by the corresponding view node as a rectangle.
  *  @see #setDescentBounds
   */
  public Rectangle2D getDescentBounds(){
    return m_descentBounds;
  }
  
  /**
   *  Returns the smallest Rectangle containing the union of the ascent and
   *  descent bounds.
   */
  public Rectangle2D getBounds(){
    m_bounds.setRect(m_ascentBounds.getX(), m_ascentBounds.getY(),
                     Math.max(m_ascentBounds.getWidth(), m_descentBounds.getWidth())
                       ,m_ascentBounds.getHeight() + m_descentBounds.getHeight());
    return m_bounds;
  }
  
  /** Returns the overall height of the bounding box. */
  public double getHeight(){
    return getBounds().getHeight();
  }
  
  /** Returns the overall width of the bounding box. */
  public double getWidth(){
    return getBounds().getWidth();
  }
  
  /** Returns the y position of the baseline for these metrics. */
  public float getBaseline(){
    return (float)m_ascentBounds.getMaxY();
  }
  
  public String toString(){
    return getBounds().toString();
  }
  
  /**
   *  Draws the metrics onto the given graphics with coloring the ascent black
   *  and the desecent red. For debugging purposes.
   */
  public void draw(Graphics2D g2){
    g2.draw(getAscentBounds());
    Color oldColor = g2.getColor();
    g2.setColor(Color.RED);
    g2.draw(getDescentBounds());
    g2.setColor(oldColor);
  }
}


