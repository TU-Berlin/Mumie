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

import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import net.mumie.mathletfactory.appletskeleton.UpperMiddleLowerCanvasApplet;
import net.mumie.mathletfactory.display.g2d.MMG2DCanvas;

/**
 * Abstract skeleton class for a 2D-Applet with 3
 * {@link net.mumie.mathletfactory.display.MM2DCanvas MM2DCanvas}
 * and one
 * {@link net.mumie.mathletfactory.appletskeleton.util.ControlPanel ControlPanel}
 * aligned in a <i>column</i>.
 *
 * @author Gronau
 * @mm.docstatus finished

 */
public abstract class UpperMiddleLowerG2DCanvasApplet
	extends UpperMiddleLowerCanvasApplet {

	/** The world coordinates that are at the center of the canvas. */
	private double m_worldCenterX = 0, m_worldCenterY = 0;

	/**
	 * The width and height in world coordinates that are displayed by the
	 * canvas.
	 */
	private double m_worldWidth = 1, m_worldHeight = 1;

  protected String m_upperLabel, m_middleLabel, m_lowerLabel;

	public void init() {
		super.init();
		m_upperCanvas = new MMG2DCanvas(MMG2DCanvas.NO_TOOLBAR);
		m_upperCanvas.setBorder(
			new TitledBorder(new EmptyBorder(0, 0, 0, 0), m_upperLabel));
		m_middleCanvas = new MMG2DCanvas(MMG2DCanvas.NO_TOOLBAR);
		m_middleCanvas.setBorder(
			new TitledBorder(new EmptyBorder(0, 0, 0, 0), m_middleLabel));
		m_lowerCanvas = new MMG2DCanvas(MMG2DCanvas.NO_TOOLBAR);
		m_lowerCanvas.setBorder(
			new TitledBorder(new EmptyBorder(0, 0, 0, 0), m_lowerLabel));

		m_canvasPane.add(m_upperCanvas);
		m_canvasPane.add(m_middleCanvas);
		m_canvasPane.add(m_lowerCanvas);

		getCanvasParameters();
	}

	/** parse parameters that set the world dimensions. */
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

	public void adjustWorld2Screen() {
		MMG2DCanvas[] canvases =
			new MMG2DCanvas[] {
				getUpperCanvas2D(),
				getMiddleCanvas2D(),
				getLowerCanvas2D()};
		for (int i = 0; i < 3; i++) {
			canvases[i].getW2STransformationHandler().setWorldCentreCoordinates(
				m_worldCenterX,
				m_worldCenterY);
			if (m_worldWidth == m_worldHeight) {
				//System.out.println("width is "+getWorldWidth()+", height is
				// "+getWorldHeight());
				canvases[i].getW2STransformationHandler().setUniformWorldDim(
					m_worldWidth);
			} else
				canvases[i].getW2STransformationHandler().setWorldDim(
					m_worldWidth,
					m_worldHeight);
		}
	}

	public MMG2DCanvas getUpperCanvas2D() {
		return (MMG2DCanvas)getUpperCanvas();
	}

	public MMG2DCanvas getMiddleCanvas2D() {
		return (MMG2DCanvas)getMiddleCanvas();
	}

	public MMG2DCanvas getLowerCanvas2D() {
		return (MMG2DCanvas)getLowerCanvas();
	}

	public String getLowerLabel() {
		return m_lowerLabel;
	}

	public String getMiddleLabel() {
		return m_middleLabel;
	}

	public String getUpperLabel() {
		return m_upperLabel;
	}

	/**
   * Sets the label for the lower canvas.
	 * @param string
	 */
	public void setLowerLabel(String string) {
		m_lowerLabel = string;
		m_lowerCanvas.setBorder(
			new TitledBorder(new EmptyBorder(0, 0, 0, 0), m_lowerLabel));
	}

	/**
   * Sets the label for the middle canvas.
	 * @param string
	 */
	public void setMiddleLabel(String string) {
		m_middleLabel = string;
		m_middleCanvas.setBorder(
			new TitledBorder(new EmptyBorder(0, 0, 0, 0), m_middleLabel));
	}

	/**
   * Sets the label for the upper canvas.
	 * @param string
	 */
	public void setUpperLabel(String string) {
		m_upperLabel = string;
		m_upperCanvas.setBorder(
			new TitledBorder(new EmptyBorder(0, 0, 0, 0), m_upperLabel));
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
		adjustWorld2Screen();
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
		adjustWorld2Screen();
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
		adjustWorld2Screen();
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
		adjustWorld2Screen();
	}
}
