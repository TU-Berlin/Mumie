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
 * {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DParabola}.
 *
 * @author Liu
 * @mm.docstatus finished
 */
public class G2DParabolaDrawable extends G2DDrawable{
	
	private double[] m_apex = new double[2];
	private double[] m_focal = new double[2];
	private double m_radian;
	private GeneralPath m_parabola;
	
	/** Sets the affine coordinates for the apex and the focal point.
	 *  It is invoked by the
     * {@link net.mumie.mathletfactory.transformer.g2d.Affine2DParabolaTransformer}
	 * @param    apex        a  <code>double[]</code>
     * @param    focal      a  <code>double[]</code>
	 */
	public void setPoints(double[] apex, double[] focal){
		m_apex = apex;
		m_focal= focal;
	}
	
	/**
	 * Sets the coordinates for the radian. It is invoked by the
     * {@link net.mumie.mathletfactory.transformer.g2d.Affine2DParabolaTransformer}
	 *
	 * @param    radian              a  <code>double</code>
	 *
	 */
	public void setRadian(double radian){
		m_radian = radian;
	}
	
	/**
	 * Creates the shape to be drawn in {@link #draw}.
	 * @see net.mumie.mathletfactory.display.CanvasDrawable#render()
	 */
	public void render(DisplayProperties properties){
		properties.setFilled(false);
		 m_parabola = (GeneralPath)createParabola();
	}
	
	protected Shape createParabola(){
		//double steigung = (m_focus[0]-m_center[0])/(m_center[1]-m_focus[1]);
		//y = x²/2p, p/2 = |m_focus[1] - m_center[1]|
		double dx = m_focal[0] - m_apex[0];
		double dy = m_focal[1] - m_apex[1];
		double p = 2 * Math.sqrt(dx*dx+dy*dy);
		//untere
		double x = 0, y = 0;
		int i  = 0;
		G2DPolygonDrawable left = new G2DPolygonDrawable();
		left.initLength(200);
		do{
			left.setPoint(i,x,y);
			i++;
			x -= 2;
			y = -x*x/(2*p);
		}while(i<200);
		left.setOpened();
		left.createShape();
		//obere
		x = 0;
		y = 0;
		i = 0;
		G2DPolygonDrawable right = new G2DPolygonDrawable();
		right.initLength(200);
		do{
			right.setPoint(i,x,y);
			i++;
			x += 2;
			y = -x*x/(2*p);
		}	while(i<200);
		 right.setOpened();
	     right.createShape();
		
		GeneralPath path = new GeneralPath();
		path.append(left.getShape(),false);
		path.append(right.getShape(),false);
		AffineTransform trafo = new AffineTransform();
		Shape result = trafo.createTransformedShape(path);
	
		trafo .setToRotation(-m_radian, m_apex[0], m_apex[1]);//AffineTransform.getRotateInstance(rotAngle, m_apex[0], m_apex[1]);
		trafo.translate(m_apex[0], m_apex[1]);
		
		return trafo.createTransformedShape(result);
	}
	
  /**
   * Returns the Java 2D-{@link java.awt.Shape}, that acts as graphical
   * representation of the corresponding mathematical
   * {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DParabola}.
   *
   * @return   a <code>Shape</code>
   *
   */
	protected Shape getShape() {
      return m_parabola;
    }
	
	/**
	 * @see net.mumie.mathletfactory.display.CanvasDrawable#isAtScreenLocation(int, int)
	 */
	public boolean isAtScreenLocation(int xOnScreen, int yOnScreen) {
    
    PathIterator it = m_parabola.getPathIterator(null, 1);
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

