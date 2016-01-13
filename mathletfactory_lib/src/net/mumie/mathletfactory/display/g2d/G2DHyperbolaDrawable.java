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
 * Contains methods to draw a
 * {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DHyperbola} in a
 * {@link net.mumie.mathletfactory.display.g2d.MMG2DCanvas}.
 *
 * @author Liu
 * @mm.docstatus finished
 */
public class G2DHyperbolaDrawable extends G2DPolygonDrawable{
  private double[] m_center = new double[2];
  private double[] m_upperLeft = new double[2];
  private double m_radian;
  private GeneralPath m_hyperbola;
  
  /**
   * Sets the coordinates for the center point and the
   * upperleft point. It is invoked by the
   * {@link net.mumie.mathletfactory.transformer.g2d.Affine2DHyperbolaTransformer}
   *
   * @param    center         a  <code>double[]</code>
   * @param    upperLeft      a  <code>double[]</code>
   *
   */
  public void setPoints(double[] center, double[] upperLeft){
    m_center = center;
    m_upperLeft = upperLeft;
  }
   
  /**
   * Sets the coordinates for the radian. It is invoked by the
   * {@link net.mumie.mathletfactory.transformer.g2d.Affine2DHyperbolaTransformer}
   *
   * @param    radian              a  <code>double</code>
   *
   */
  public void setRadian(double radian) {
      m_radian = radian;
  }
  
    /**
	 * Creates the shape to be drawn in {@link #draw}.
	 * @see net.mumie.mathletfactory.display.CanvasDrawable#render()
	 */
  public void render(DisplayProperties properties){
	  properties.setFilled(false);
     m_hyperbola = (GeneralPath)createHyperbola();
  }
  
  
  private  G2DPolygonDrawable createPart(int i,int k){
  //i,k koennen nur 1 oder -1 sein, sind nur fuer das Vorzeichen
    double x = i*Math.abs(m_upperLeft[0]-m_center[0]), y = 0;
    int step = 0;
    double a = Math.abs(m_upperLeft[0]-m_center[0]);
    double b= Math.abs(m_upperLeft[1]-m_center[1]);
    G2DPolygonDrawable part = new G2DPolygonDrawable();
    part.initLength(200);
    do{
      part.setPoint(step,x,y);
      step++;
      x=x+i*2;
      y = k*Math.sqrt(b*b*(x*x/(a*a)-1));
    }while(step<200);
    part.setOpened();
    part.createShape();
    return part;
  }
  
  protected Shape createHyperbola(){
    G2DPolygonDrawable downleft = createPart(-1,-1);
    G2DPolygonDrawable upperleft = createPart(-1,1);
    G2DPolygonDrawable upperright = createPart(1,1);
    G2DPolygonDrawable downright = createPart(1,-1);
    
    AffineTransform trafo= new AffineTransform();
    GeneralPath path = new GeneralPath();
    path.append(downleft.getShape(),false);
    path.append(upperleft.getShape(),false);
    path.append(upperright.getShape(),false);
    path.append(downright.getShape(),false);
    Shape result = trafo.createTransformedShape(path);
    
    trafo.setToRotation(-m_radian,m_center[0],m_center[1]);
    trafo.translate(m_center[0], m_center[1]);
    
    return trafo.createTransformedShape(result);
  }
  
    
  /**
   * Returns the Java 2D-{@link java.awt.Shape}, that acts as graphical
   * representation of the corresponding mathematical
   * {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DHyperbola}.
   *
   * @return   a <code>Shape</code>
   *
   */
  protected Shape getShape() {
      return m_hyperbola ;
    }
  
  
	
    /**
	 * @see net.mumie.mathletfactory.display.CanvasDrawable#isAtScreenLocation(int, int)
	 */
  public boolean isAtScreenLocation(int xOnScreen, int yOnScreen) {
      
      PathIterator it = m_hyperbola.getPathIterator(null, 1);
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
}

