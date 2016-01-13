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
import java.awt.geom.Arc2D;

import net.mumie.mathletfactory.display.DisplayProperties;

/**
 * This drawable draws an arc shape.in a
 * {@link net.mumie.mathletfactory.display.g2d.MMG2DCanvas}.
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */
public class G2DArcDrawable extends G2DDrawable {

	private double[] m_center = new double[2];

  /** Returns the center coordinates of the arc drawn. */
	public double[] getCenter() {
		return m_center;
	}

	private Shape m_arc = new Arc2D.Double();

	protected Shape getShape() {
		return m_arc;
	}

	public boolean isAtScreenLocation(int xOnScreen, int yOnScreen) {
		return false;
	}

	public void render(DisplayProperties properties) {
		Arc2DDisplayProperties arcProp = (Arc2DDisplayProperties) properties;
		getArc().setArcType(Arc2D.OPEN);
		double radius = arcProp.getArcRadius();
		double startingAngle = arcProp.getStartingAngle();
		double xDelta = arcProp.getXDelta();
		double angleExtension = arcProp.getAngleExtension();
		getArc().setArcByCenter(
			m_center[0]+xDelta,
			m_center[1],
			radius,
			startingAngle,
			angleExtension,
			Arc2D.OPEN);
	}

	private Arc2D.Double getArc() {
		return (Arc2D.Double) m_arc;
	}
}
