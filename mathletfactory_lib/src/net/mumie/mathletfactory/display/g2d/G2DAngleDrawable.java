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
 * Contains methods to draw an
 * {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DAngle}.
 *
 * @author Mrose
 * @mm.docstatus finished
 */
public class G2DAngleDrawable extends G2DDrawable {

	private Arc2D.Double m_angle;
	private double m_startingAngle, m_angularExtent;
	private double[] m_upperLeftCorner, m_widthAndHeight;
	private int m_closure;

	public void setPoints(
		double[] upperLeftCorner,
		double[] widthAndHeight,
		double startingAngle,
		double angularExtent,
		int closure) {
		m_widthAndHeight = widthAndHeight;
		m_upperLeftCorner = upperLeftCorner;
		m_startingAngle = startingAngle;
		m_angularExtent = angularExtent;
		m_closure = closure;
	}

	protected Shape getShape() {
		return m_angle;
	}

	public void render(DisplayProperties properties) {
		int closure;
		if (m_closure == 0) {
			closure = Arc2D.OPEN;
		}
		else if (m_closure == 1) {
			closure = Arc2D.CHORD;
		}
		else
			closure = Arc2D.PIE;
		m_angle =
			new Arc2D.Double(
				m_upperLeftCorner[0],
				m_upperLeftCorner[1],
				m_widthAndHeight[0],
				m_widthAndHeight[1],
				m_startingAngle,
				m_angularExtent,
				closure);
	}

	public boolean isAtScreenLocation(int xOnScreen, int yOnScreen) {
		return m_angle.contains(xOnScreen, yOnScreen);
	}
}
