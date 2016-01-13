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

import net.mumie.mathletfactory.appletskeleton.SingleCanvasApplet;
import net.mumie.mathletfactory.display.g2d.MMG2DCanvas;

/**
 * Abstract skeleton class for a 2D-Applet with 1
 * {@link net.mumie.mathletfactory.display.MM2DCanvas MM2DCanvas} and one
 * {@link net.mumie.mathletfactory.appletskeleton.util.ControlPanel ControlPanel}
 * one under the other.
 *
 * @author Gronau
 * @mm.docstatus finished
 */

public abstract class SingleG2DCanvasApplet extends SingleCanvasApplet {

	/** The world coordinates that are at the center of the canvas. */
	private double m_worldCenterX = 0, m_worldCenterY = 0;

	/**
	 * The width and height in world coordinates that are displayed by the
	 * canvas.
	 */
	private double m_worldWidth = 1, m_worldHeight = 1;

	/** Adds canvas components and parses parameters. */
	public void init() {
		super.init();
		m_canvas = new MMG2DCanvas();
		getCanvasPane().add(m_canvas, BorderLayout.CENTER);
		getCanvasParameters();
//    m_viewButtonMenu.setCanvas(m_canvas);
	}

	/** Parses parameters that set the world dimensions. */
	public void getCanvasParameters() {
		try {
			if (getParameter("worldWidth") != null)
				m_worldWidth = 1.2 * Double.parseDouble(getParameter("worldWidth"));
			if (getParameter("worldHeight") != null)
				m_worldHeight = 1.2 * Double.parseDouble(getParameter("worldHeight"));
			if (getParameter("worldCenterX") != null)
				m_worldCenterX = Double.parseDouble(getParameter("worldCenterX"));
			if (getParameter("worldCenterY") != null)
				m_worldCenterY = Double.parseDouble(getParameter("worldCenterY"));
		} catch (Exception e) {}
		adjustWorld2Screen();
	}

	/** Sets the world to screen map according to the given dimensions. */
	public void adjustWorld2Screen() {
		getCanvas2D().getW2STransformationHandler().setWorldCentreCoordinates(
			m_worldCenterX,
			m_worldCenterY);
		if (m_worldWidth == m_worldHeight) {
			//System.out.println("width is "+getWorldWidth()+", height is
			// "+getWorldHeight());
			getCanvas2D().getW2STransformationHandler().setUniformWorldDim(
				m_worldWidth);
		} else
			getCanvas2D().getW2STransformationHandler().setWorldDim(
				m_worldWidth,
				m_worldHeight);
	}

	/** Calls super method and resets the history of the canvas. */
	public void reset() {
		super.reset();
		getCanvasParameters();
    getCanvas2D().rescale();
		getCanvas2D().getWorld2Screen().getHistory().clear();
		getCanvas2D().getWorld2Screen().addSnapshotToHistory();
		getCanvas2D().renderScene();
		getCanvas2D().repaint();
	}

	public MMG2DCanvas getCanvas2D() {
		return (MMG2DCanvas)getCanvas();
	}

	/**
	 * Returns the x world coordinate that is at the center of the canvas.
	 */
	public double getWorldCenterX() {
		return m_worldCenterX;
	}

	/**
	 * Sets the x world coordinate that is at the center of the canvas.
	 */
	public void setWorldCenterX(double d) {
		m_worldCenterX = d;
	}

	/**
	 * Returns the y world coordinate that is at the center of the canvas.
	 */
	public double getWorldCenterY() {
		return m_worldCenterY;
	}

	/**
	 * Sets the y world coordinate that is at the center of the canvas.
	 */
	public void setWorldCenterY(double d) {
		m_worldCenterY = d;
	}

	/**
	 * Returns the width in world coordinates that is displayed by the canvas.
	 */
	public double getWorldWidth() {
		return m_worldWidth;
	}

	/**
	 * Sets the width in world coordinates that is displayed by the canvas.
	 */
	public void setWorldWidth(double d) {
		m_worldWidth = d;
	}

	/**
	 * Returns the height in world coordinates that is displayed by the canvas.
	 */
	public double getWorldHeight() {
		return m_worldHeight;
	}

	/**
	 * Sets the height in world coordinates that is displayed by the canvas.
	 */
	public void setWorldHeight(double d) {
		m_worldHeight = d;
	}
}
