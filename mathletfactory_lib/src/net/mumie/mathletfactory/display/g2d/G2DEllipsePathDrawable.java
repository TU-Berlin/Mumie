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

package net.mumie.mathletfactory.display.g2d;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

import net.mumie.mathletfactory.display.DisplayProperties;

/**
 * Contains methods to draw an
 * {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DEllipse} in a
 * {@link net.mumie.mathletfactory.display.g2d.MMG2DCanvas}.
 * 
 * @author Mrose
 * @mm.docstatus finished
 */
public class G2DEllipsePathDrawable extends G2DDrawable {
  private double[] m_center = new double[2];
  private double[] m_upperLeft = new double[2];
  private double m_radian;
  private GeneralPath m_ellipse;
//  private AffineTransform rotation = new AffineTransform();
  
  /**
   * Sets the coordinates for the center point and the
   * upperleft point. It is invoked by the
   * {@link net.mumie.mathletfactory.transformer.g2d.Affine2DEllipseDefaultGraphics2DTransformer}
   *
   * @param    center         a  <code>double[]</code>
   * @param    upperLeft      a  <code>double[]</code>
   */
  public void setPoints(double[] center,
                        double[] upperLeft) {
    m_center = center;
    m_upperLeft = upperLeft;
  }
  
  /**
   * Sets the coordinates for the radian. It is invoked by the
   * {@link net.mumie.mathletfactory.transformer.g2d.Affine2DEllipseDefaultGraphics2DTransformer}
   *
   * @param    radian              a  <code>double
   */
  public void setRadian(double radian) {
    m_radian = radian;
  }
  
  /**
   * Specifies, how the ellipse has
   * to be laid out on the screen.
   *
   * @param properties the display properties of the ellipse
   */
  public void render(DisplayProperties properties) {
	  properties.setFilled(false);
    m_ellipse = (GeneralPath)createEllipse(properties);
  }
  
  private Shape createEllipse(DisplayProperties properties){
    double a = m_center[0]-m_upperLeft[0];
    double b = m_center[1]-m_upperLeft[1];
    //untere
    double x = -Math.abs(a), y = 0;
    int i  = 0;
    G2DPolygonDrawable down = new G2DPolygonDrawable();
    down.initLength((int) Math.abs(a)+1);
    
    do{
      down.setPoint(i,x,y);
      i++;
      x += 1;
      y = -Math.sqrt(b*b*(1-(x*x/(a*a))));
    }
    while(i<=(int) Math.abs(a));
    down.setOpened();
    
    //obere
    x = -Math.abs(a);
    y = 0;
    i = 0;
    G2DPolygonDrawable upper = new G2DPolygonDrawable();
    upper.initLength((int) Math.abs(a)+1);
    do{
      upper.setPoint(i,x,y);
      i++;
      x += 1;
      y = Math.sqrt(b*b*(1-(x*x/(a*a))));
    }
    while(i<=(int) Math.abs(a));
    upper.setOpened();
    
    x = Math.abs(a);
    y = 0;
    i = 0;
    G2DPolygonDrawable left = new G2DPolygonDrawable();
    left.initLength((int) Math.abs(a)+1);
    do{
      left.setPoint(i,x,y);
      i++;
      x -= 1;
      y = Math.sqrt(b*b*(1-(x*x/(a*a))));
    }while(i<=(int) Math.abs(a));
    left.setOpened();
    
    x = Math.abs(a);
    y = 0;
    i = 0;
    G2DPolygonDrawable right = new G2DPolygonDrawable();
    right.initLength((int) Math.abs(a)+1);
    do{
      right.setPoint(i,x,y);
      i++;
      x -= 1;
      y = -Math.sqrt(b*b*(1-(x*x/(a*a))));
    }while(i<=(int) Math.abs(a));
    right.setOpened();
    
    GeneralPath path = new GeneralPath();
    path.append(down.getShape(),false);
    path.append(upper.getShape(),false);
    path.append(left.getShape(),false);
    path.append(right.getShape(),false);
    AffineTransform trafo = new AffineTransform();
    Shape result = trafo.createTransformedShape(path);
    
    trafo .setToRotation(-m_radian, m_center[0], m_center[1]);//AffineTransform.getRotateInstance(rotAngle, m_apex[0], m_apex[1]);
    trafo.translate(m_center[0], m_center[1]);
    
    return trafo.createTransformedShape(result);
  }
  
  public boolean isAtScreenLocation(int xOnScreen, int yOnScreen) {
    PathIterator it = m_ellipse.getPathIterator(null, 1);
    float[] current = new float[6];
    Point2D start = null, stop = null;
    while (!it.isDone()) {
      int type = it.currentSegment(current);
      if (type == 0) {
        start = new Point2D.Float(current[0], current[1]);
      } else {
        stop = new Point2D.Float(current[0], current[1]);
        Line2D line = new Line2D.Float(start, stop);
        if (line.intersects(
              xOnScreen - PICKING_TOLERANCE, yOnScreen - PICKING_TOLERANCE,
              PICKING_TOLERANCE, PICKING_TOLERANCE)) {
          return true;
        }
        start = stop;
      }
      it.next();
    }
    return false;
  }
  
  /**
   * Returns the Java 2D-{@link java.awt.Shape}, that acts as graphical
   * representation of the corresponding mathematical
   * {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DEllipse}.
   *
   * @return   a <code>Shape</code>
   */
  protected Shape getShape() {
    return m_ellipse;
  }
}



