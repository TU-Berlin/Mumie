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
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import net.mumie.mathletfactory.display.CanvasDrawable;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.LineDisplayProperties;
import net.mumie.mathletfactory.math.geom.affine.Affine2DPoint;

/**
 * This drawable is reponsible for drawing line segments onto a canvas.
 * 
 * The appearance of the line can be influenced via 
 * {@link net.mumie.mathletfactory.display.LineDisplayProperties}. When using
 * {@link net.mumie.mathletfactory.display.DisplayProperties} missing properties
 * for line are substituted by defaults.
 * 
 * @author Lars Amsel
 * @mm.docstatus finished
 * @todo keep watching the bad path bug in Java2D
 * @see G2DDrawable
 */
public class G2DLineDrawable extends G2DDrawable {
  private double m_xStart;
  private double m_yStart;
  private double m_xEnd;
  private double m_yEnd;
  
  //private LineDisplayProperties m_properties = LineDisplayProperties.VECTOR_DEFAULT;
  
  private Shape m_line;
    
  public void render(DisplayProperties properties) {
    m_line = createLine(properties);
  }

  /**
   * Caculates the 2D euclidean distance between start end endpoint. 
   * @return double the line length
   */
  protected double lineLength() {
    return Math.sqrt(
        Math.pow(m_xEnd - m_xStart, 2) +
        Math.pow(m_yEnd - m_yStart, 2));
  }
  
  /**
   * Returns the path that is stored in properties for the starting edge of a
   * line. If properties isn't a (subclass of) 
   * {@link net.mumie.mathletfactory.LineDisplayProperties} the default from
   * LineDisplayProperties is returned.  
   * 
   * @param properties the properties for lookup
   * @return GeneralPath a path describing the start edge of the line.
   */
  protected GeneralPath getArrowAtStart(DisplayProperties properties) {
    if (properties instanceof LineDisplayProperties)
      return ((LineDisplayProperties)properties).getArrowAtStart();
    return LineDisplayProperties.DEFAULT.getArrowAtStart();
  }
  
  /**
   * Returns the Path that is stored in porperties for the end edge of a
   * line. If properties isn't a (subclass of) 
   * {@link net.mumie.mathletfactory.LineDisplayProperties} the default from
   * LineDisplayProperties is returned.  
   * 
   * @param properties the properties for lookup
   * @return GeneralPath a path describing the end edge of the line.
   */
  protected GeneralPath getArrowAtEnd(DisplayProperties properties) {
    if (properties instanceof LineDisplayProperties)
      return ((LineDisplayProperties)properties).getArrowAtEnd();
    return LineDisplayProperties.DEFAULT.getArrowAtEnd();
  }
  
  /**
   * Looks up properties for the width of the line to draw. If it isn't a
   * {@link net.mumie.mathletfactory.LineDisplayProperties} default is 
   * substituted.
   * @param properties properties object for look up
   * @return double line length stored in properties or default value.
   */
  protected double getLineWidth(DisplayProperties properties) {
    if (properties instanceof LineDisplayProperties)
      return ((LineDisplayProperties)properties).getLineWidth();
    return LineDisplayProperties.DEFAULT.getLineWidth();
  }
  
  /**
   * Create a shape to display a line.
   * 
   * The shape is merged from two lines and the GeneralPathes returned from 
   * {@link getArrowAtEnd} and {@link getArrowAtStart}.
   *   
   * @param properties
   * @return Shape
   */
  protected Shape createLine(DisplayProperties properties) {
  	// bug fix for bad path exception!
  	if(m_xStart == m_xEnd && m_yStart == m_yEnd)
  		return new GeneralPath();
    GeneralPath start = getArrowAtStart(properties);
    GeneralPath end = getArrowAtEnd(properties);
    
    double dx = m_xEnd - m_xStart;
    double dy = m_yEnd - m_yStart;
    double scale = getLineWidth(properties) / 10.0;
    double lineLength = Math.sqrt(Math.pow(dx, 2)+ Math.pow(dy, 2)) / scale;
    double segmentLength = Math.max(0, lineLength - start.getBounds2D().getHeight() - end.getBounds2D().getHeight());
    
    if(scale == 0) //empty shape
      return new GeneralPath();
    AffineTransform trafo = null;
    GeneralPath path = (GeneralPath)start.clone();
    trafo = AffineTransform.getRotateInstance(Math.PI, (start.getBounds2D().getCenterX() + end.getBounds().getCenterX()) / 2, 0);
    trafo.translate(0, -(segmentLength + start.getBounds2D().getHeight() + end.getBounds2D().getHeight()));
    
    if (segmentLength == 0) {
      path.append(trafo.createTransformedShape(end), true);
    } else {

      Line2D line1 = new Line2D.Double(
          start.getBounds2D().getCenterX() - 5,
          start.getBounds2D().getHeight(),
          start.getBounds2D().getCenterX() - 5,
          start.getBounds2D().getHeight() + segmentLength);
          
      Line2D line2 = new Line2D.Double(
          start.getBounds2D().getCenterX() + 5,
          start.getBounds2D().getHeight() + segmentLength,
          start.getBounds2D().getCenterX() + 5,
          start.getBounds2D().getHeight());

      path.append(line1, true);
      path.append(trafo.createTransformedShape(end), true);
      path.append(line2, true);
    }
    path.closePath();

    if (lineLength < (start.getBounds2D().getHeight() + end.getBounds2D().getHeight())) {
      scale *= lineLength / (start.getBounds2D().getHeight() + end.getBounds2D().getHeight());
    }
    trafo = AffineTransform.getTranslateInstance(-start.getBounds().getCenterX() * scale, 0);
    trafo.scale(scale, scale);
    Shape result = trafo.createTransformedShape(path);
    
    double rotAngle = Math.atan(dy/dx);
    if(dx >= 0 && dy >=0) {
      rotAngle -= Math.PI / 2;
    }
    else if(dx <= 0 && dy >=0) {
      rotAngle -= Math.PI * 3.0/2.0;
    }
    else if(dx >= 0 && dy <=0) {
      rotAngle -= Math.PI / 2;
    }
    else {
      rotAngle -= Math.PI * 3.0/2.0;
    }
    
    trafo = AffineTransform.getRotateInstance(rotAngle, m_xStart, m_yStart);
    trafo.translate(m_xStart, m_yStart);    
    
    return trafo.createTransformedShape(result);
  }
  
  /**
   * Rotates <code>shape</code> around the startpoint. The angle is determined 
   * by the value of start and endpoint of the drawable.
   *   
   * @param shape the shape to be rotated
   * @return Shape rotated shape
   */
  protected Shape rotateLine(GeneralPath shape) {
    double dx = m_xEnd - m_xStart;
    double dy = m_yEnd - m_yStart;
    double rotAngle = Math.atan(dy/dx);
    if(dx >= 0 && dy >=0) {
      rotAngle -= Math.PI / 2;
    }
    else if(dx <= 0 && dy >=0) {
      rotAngle -= Math.PI * 3.0/2.0;
    }
    else if(dx >= 0 && dy <=0) {
      rotAngle -= Math.PI / 2;
    }
    else {
      rotAngle -= Math.PI * 3.0/2.0;
    }
    
    AffineTransform rot = AffineTransform.getRotateInstance(rotAngle, m_xStart, m_yStart);
    return shape.createTransformedShape(rot);
  }
  
  /**
   * Calculates the bound that a gradient should have so that the line gets
   * a poor 3D effect. Maybe this method should be deprecated because the 
   * result is not satisfactory.
   */
  protected Rectangle2D getGradientBounds() {
    Shape shape = getShape();
    double size = Math.max(shape.getBounds2D().getWidth(), shape.getBounds2D().getHeight())/2.0;
    Rectangle2D result = shape.getBounds2D();
    result.setFrameFromCenter(
        shape.getBounds2D().getCenterX(),
        shape.getBounds2D().getCenterY(),
        shape.getBounds2D().getCenterX() - size, shape.getBounds2D().getCenterY() - size);
    return result;
  }
  
  /**
   * Returns the current shape. The shape is never updated automaticly neigher
   * by changing the coordinates nor by modifying the properties. 
   */
  protected Shape getShape() {
    return m_line;
  }
    
   /**
    * Tests whether or not the shape interscets a screen location. The tolerance
    * ist determined by {@link G2DDrawable#PICKING} definied in 
    * G2DDrawable.   
    */
  public boolean isAtScreenLocation(int xOnScreen, int yOnScreen) {
    Rectangle2D rect = new Rectangle2D.Double();
    rect.setFrameFromCenter(xOnScreen, yOnScreen, xOnScreen - PICKING_TOLERANCE, yOnScreen - PICKING_TOLERANCE);
    return m_line.intersects(rect);
  }
  
  /**
   * Returns the upright bounding box of the cuirrent shape.
   * @return Rectangle
   */
  public Rectangle getBoundingBox() {
    return m_line.getBounds();
  }

  /**
   * Changes the current coordinates. The shape ist not updated by this mehtod.
   * 
   * @param initialPoint
   * @param endPoint
   * @return CanvasDrawable
   */
  public CanvasDrawable setPoints(Affine2DPoint initialPoint, Affine2DPoint endPoint) {
    // it's better to use projective coordinates, because we want to reduce
    // "new" calls:
    double xStart = initialPoint.getProjectiveCoordinatesOfPoint().getEntryRef(1).getDouble();
    double yStart = initialPoint.getProjectiveCoordinatesOfPoint().getEntryRef(2).getDouble();
    double xEnd = endPoint.getProjectiveCoordinatesOfPoint().getEntryRef(1).getDouble();
    double yEnd = endPoint.getProjectiveCoordinatesOfPoint().getEntryRef(2).getDouble();
    return setPoints(xStart, yStart, xEnd, yEnd);
  }

  /**
   * Changes the current coordinates. The shape ist not updated by this method.
   * 
   * @param initialPoint
   * @param endPoint
   * @return CanvasDrawable
   */
  public CanvasDrawable setPoints(double x1, double y1, double x2, double y2) {
    m_xStart = x1;
    m_yStart = y1;
    m_xEnd = x2;
    m_yEnd = y2;
    return this;
  }
  
}

