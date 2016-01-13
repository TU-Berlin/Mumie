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

package net.mumie.mathletfactory.appletskeleton.g2d;

import java.awt.BorderLayout;

import net.mumie.mathletfactory.appletskeleton.SideBySideCanvasApplet;
import net.mumie.mathletfactory.display.g2d.MMG2DCanvas;

/**
 * Abstract skeleton class for a 2D-Applet with 2
 * {@link net.mumie.mathletfactory.display.MM2DCanvas MM2DCanvas} aligned in a <i>row</i>
 * and one
 * {@link net.mumie.mathletfactory.appletskeleton.util.ControlPanel ControlPanel}
 * below.
 * 
 * @author Gronau 
 * @mm.docstatus finished
 */
public abstract class SideBySideG2DCanvasApplet
	extends SideBySideCanvasApplet {

	/** The world coordinates that are at the center of the left canvas. */
	private double m_worldCenterXLeft = 0, m_worldCenterYLeft = 0;

	/** The world coordinates that are at the center of the right canvas. */
	private double m_worldCenterXRight = 0, m_worldCenterYRight = 0;

	/**
	 * The width and height in world coordinates that are displayed by the left
	 * canvas.
	 */
	private double m_worldWidthLeft = 1, m_worldHeightLeft = 1;

	/**
	 * The width and height in world coordinates that are displayed by the left
	 * canvas.
	 */
	private double m_worldWidthRight = 1, m_worldHeightRight = 1;

	/** Adds canvas components and parses parameters. */
	public void init() {
		super.init();
		m_leftCanvas = new MMG2DCanvas();
		m_rightCanvas = new MMG2DCanvas();
		m_leftCanvasPane.add(m_leftCanvas, BorderLayout.CENTER);
		m_rightCanvasPane.add(m_rightCanvas, BorderLayout.CENTER);
		getCanvasParameters();
	}

	/** parse parameters that set the world dimensions. */
	public void getCanvasParameters() {
		try {
			if (getParameter("worldWidthLeft") != null)
				m_worldWidthLeft =
					1.2 * Double.parseDouble(getParameter("worldWidthLeft"));
			if (getParameter("worldHeightLeft") != null)
				m_worldHeightLeft =
					1.2 * Double.parseDouble(getParameter("worldHeightLeft"));
			if (getParameter("worldCenterXLeft") != null)
				m_worldCenterXLeft =
					Double.parseDouble(getParameter("worldCenterXLeft"));
			if (getParameter("worldCenterYLeft") != null)
				m_worldCenterYLeft =
					Double.parseDouble(getParameter("worldCenterYLeft"));
			if (getParameter("worldWidthRight") != null)
				m_worldWidthRight =
					1.2 * Double.parseDouble(getParameter("worldWidthRight"));
			if (getParameter("worldHeightRight") != null)
				m_worldHeightRight =
					1.2 * Double.parseDouble(getParameter("worldHeightRight"));
			if (getParameter("worldCenterXRight") != null)
				m_worldCenterXRight =
					Double.parseDouble(getParameter("worldCenterXRight"));
			if (getParameter("worldCenterYRight") != null)
				m_worldCenterYRight =
					Double.parseDouble(getParameter("worldCenterYRight"));
		} catch (Exception e) {}
		adjustWorld2Screen();
	}

	/** Sets the world to screen maps according to the given dimensions. */
	public void adjustWorld2Screen() {
		getLeftCanvas2D().getW2STransformationHandler().setWorldCentreCoordinates(
			m_worldCenterXLeft,
			m_worldCenterYLeft);
		if (m_worldWidthLeft == m_worldHeightLeft) {
			//System.out.println("width is "+getWorldWidth()+", height is
			// "+getWorldHeight());
			getLeftCanvas2D().getW2STransformationHandler().setUniformWorldDim(
				m_worldWidthLeft);
		} else
			getLeftCanvas2D().getW2STransformationHandler().setWorldDim(
				m_worldWidthLeft,
				m_worldHeightLeft);
		getRightCanvas2D().getW2STransformationHandler().setWorldCentreCoordinates(
			m_worldCenterXRight,
			m_worldCenterYRight);
		if (m_worldWidthRight == m_worldHeightRight) {
			//System.out.println("width is "+getWorldWidth()+", height is
			// "+getWorldHeight());
			getRightCanvas2D().getW2STransformationHandler().setUniformWorldDim(
				m_worldWidthRight);
		} else
			getRightCanvas2D().getW2STransformationHandler().setWorldDim(
				m_worldWidthRight,
				m_worldHeightRight);
	}

	/** Calls super method and resets the history of the canvases. */
	public void reset() {
		super.reset();
		getCanvasParameters();
    getLeftCanvas2D().rescale();
    getRightCanvas2D().rescale();
		getLeftCanvas2D().getWorld2Screen().getHistory().clear();
		getLeftCanvas2D().getWorld2Screen().addSnapshotToHistory();
		getRightCanvas2D().getWorld2Screen().getHistory().clear();
		getRightCanvas2D().getWorld2Screen().addSnapshotToHistory();
		getLeftCanvas2D().renderScene();
		getRightCanvas2D().renderScene();
		getLeftCanvas2D().repaint();
		getRightCanvas2D().repaint();
	}

	public MMG2DCanvas getLeftCanvas2D() {
		return (MMG2DCanvas)getLeftCanvas();
	}

	public MMG2DCanvas getRightCanvas2D() {
		return (MMG2DCanvas)getRightCanvas();
	}

	/**
	 * Returns the x world coordinate that is at the center of the left canvas.
	 */
	public double getWorldCenterXLeft() {
		return m_worldCenterXLeft;
	}

	/**
	 * Returns the x world coordinate that is at the center of the right canvas.
	 */
	public double getWorldCenterXRight() {
		return m_worldCenterXRight;
	}

	/**
	 * Returns the y world coordinate that is at the center of the right canvas.
	 */
	public double getWorldCenterYLeft() {
		return m_worldCenterYLeft;
	}

	/**
	 * Returns the y world coordinate that is at the center of the right canvas.
	 */
	public double getWorldCenterYRight() {
		return m_worldCenterYRight;
	}

	/**
	 * Returns the height in world coordinates that is displayed by the left
	 * canvas.
	 */
	public double getWorldHeightLeft() {
		return m_worldHeightLeft;
	}

	/**
	 * Returns the height in world coordinates that is displayed by the right
	 * canvas.
	 */
	public double getWorldHeightRight() {
		return m_worldHeightRight;
	}

	/**
	 * Returns the width in world coordinates that is displayed by the left
	 * canvas.
	 */
	public double getWorldWidthLeft() {
		return m_worldWidthLeft;
	}

	/**
	 * Returns the width in world coordinates that is displayed by the right
	 * canvas.
	 */
	public double getWorldWidthRight() {
		return m_worldWidthRight;
	}

	/**
	 * Sets the x world coordinate that is at the center of the left canvas.
	 */
	public void setWorldCenterXLeft(double d) {
		m_worldCenterXLeft = d;
	}

	/**
	 * Sets the x world coordinate that is at the center of the right canvas.
	 */
	public void setWorldCenterXRight(double d) {
		m_worldCenterXRight = d;
	}

	/**
	 * Sets the y world coordinate that is at the center of the left canvas.
	 */
	public void setWorldCenterYLeft(double d) {
		m_worldCenterYLeft = d;
	}

	/**
	 * Sets the y world coordinate that is at the center of the right canvas.
	 */
	public void setWorldCenterYRight(double d) {
		m_worldCenterYRight = d;
	}

	/**
	 * Sets the height in world coordinates that is displayed by the left canvas.
	 */
	public void setWorldHeightLeft(double d) {
		m_worldHeightLeft = d;
	}

	/**
	 * Sets the height in world coordinates that is displayed by the right
	 * canvas.
	 */
	public void setWorldHeightRight(double d) {
		m_worldHeightRight = d;
	}

	/**
	 * Sets the width in world coordinates that is displayed by the left canvas.
	 */
	public void setWorldWidthLeft(double d) {
		m_worldWidthLeft = d;
	}

	/**
	 * Sets the width in world coordinates that is displayed by the right canvas.
	 */
	public void setWorldWidthRight(double d) {
		m_worldWidthRight = d;
	}
}
