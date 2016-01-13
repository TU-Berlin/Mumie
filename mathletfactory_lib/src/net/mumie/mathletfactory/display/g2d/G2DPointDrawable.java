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
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;

import net.mumie.mathletfactory.display.CanvasDrawable;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.PointDisplayProperties;
import net.mumie.mathletfactory.math.geom.affine.Affine2DPoint;

/**
 * Contains methods to draw a point, (e.g. given by an 
 * {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPoint}) in a
 * {@link net.mumie.mathletfactory.display.g2d.MMG2DCanvas}.
 * 
 * @author Amsel
 * @mm.docstatus finished
 */
public class G2DPointDrawable extends G2DDrawable {

	private final float[] m_xy = new float[2];

	private RectangularShape m_shape = new Ellipse2D.Float();

	public CanvasDrawable setPoint(Affine2DPoint screenPointInPixel) {
		setPoint(screenPointInPixel.getProjectiveCoordinatesOfPoint().getEntryRef(1).getDouble(),
      screenPointInPixel.getProjectiveCoordinatesOfPoint().getEntryRef(2).getDouble());
		return this;
	}

	public CanvasDrawable setPoint(double[] javaScreenCoordinates) {
    setPoint(javaScreenCoordinates[0],javaScreenCoordinates[1]);
		return this;
	}

	public CanvasDrawable setPoint(double xOnScreen, double yOnScreen) {
		m_xy[0] = (float)xOnScreen;
		m_xy[1] = (float)yOnScreen;
		return this;
	}

	public double[] getPointCoordsRef() {
		return new double[] {m_xy[0], m_xy[1]};
	}

	protected Shape getShape() {
		return m_shape;
	}

	protected int getPointRadius(DisplayProperties properties) {

		if (properties instanceof PointDisplayProperties)
			return ((PointDisplayProperties) properties).getPointRadius();

		return PointDisplayProperties.DEFAULT.getPointRadius();
	}

	public void render(DisplayProperties properties) {
		// this is the Graphics2D construct for Ellipse2D,
		// you have to describe the surrounding rectangle, starting with the lower
		// left corner:
		int pointRadius = getPointRadius(properties);

		m_shape.setFrame(
			m_xy[0] - pointRadius,
			m_xy[1] - pointRadius,
			2 * pointRadius,
			2 * pointRadius);
	}

	public boolean isAtScreenLocation(int xOnScreen, int yOnScreen) {
		if ((PICKING_TOLERANCE < m_shape.getWidth())
			&& (PICKING_TOLERANCE < m_shape.getHeight())) {
			return m_shape.contains(xOnScreen, yOnScreen);
		}

		return m_shape.intersects(
			xOnScreen - PICKING_TOLERANCE / 2,
			yOnScreen - PICKING_TOLERANCE / 2,
			PICKING_TOLERANCE,
			PICKING_TOLERANCE);
	}

	public Rectangle getBoundingBox(DisplayProperties properties) {
		Rectangle result = m_shape.getBounds();
		if (properties.hasShadow()) {
			Point2D offset = properties.getShadowOffset();

			result.setFrame(
				result.getX(),
				result.getY(),
				result.getWidth() + Math.abs(offset.getX()),
				result.getHeight() + Math.abs(offset.getY()));

			if (offset.getX() < 0)
				result.setLocation((int) (result.getX() + offset.getX()), 0);
			if (offset.getY() < 0)
				result.setLocation(0, (int) (result.getY() + (int) offset.getY()));
		}
		return result;
	}

}
