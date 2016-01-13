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

import net.mumie.mathletfactory.display.DisplayProperties;

/**
 * Display properties for 2D Arcs.
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */
public class Arc2DDisplayProperties extends DisplayProperties {
	
	private double m_startingAngle;
	
	private double m_angleExtension;
	
	private double m_radius;

	private double m_XDelta;
	
  /** Constructs the display properties with starting angle 0, angle extension 90 and radius 3. */
	public Arc2DDisplayProperties(){
		m_startingAngle = 0;
		m_angleExtension = 90;
		m_radius = 3;
	}
	
  /** Sets the radius of the arc. Default is 3. */
	public void setArcRadius(double radius){
		m_radius = radius;
	}
	
  /** Returns the radius of the arc. */
	public double getArcRadius(){
		return m_radius;
	}

	/** Returns the angle extension of this arc in degrees. */
	public double getAngleExtension() {
		return m_angleExtension;
	}

  /** Sets the angle extension of this arc in degrees. */
	public void setAngleExtension(double d) {
		m_angleExtension = d;
	}

  /** Returns the starting angle of this arc in degrees. */
	public double getStartingAngle() {
		return m_startingAngle;
	}

  /** Sets the starting angle of this arc in degrees. */
	public void setStartingAngle(double d) {
		m_startingAngle = d;
	}

	/**	 Returns the x translation of this arc. */
	public double getXDelta() {
		return m_XDelta;
	}

  /**  Sets the x translation of this arc. */
	public void setXDelta(double d) {
		m_XDelta = d;
	}
}
