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

package net.mumie.mathletfactory.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import net.mumie.mathletfactory.appletskeleton.util.MumieTheme;
import net.mumie.mathletfactory.display.g2d.MMG2DCanvas;

/**
 * This class is used to render an arbitrary image in a <code>MMG2DCanvas()</code>.
 * 
 * 
 * The attribute <code>isStatic</code> declares that the image uses whether
 * screen or world coordinates (i.e. counting from the top left corner of the
 * canvas or going from canvas origin to positive infinity). Translating or
 * scaling the canvas's view has therefore no effect on static images. Only
 * non-static images are "embedded" in the world.
 * 
 * The attribute <code>isRepeating</code> declares that the image will
 * whether be repeated (at original size) over the defined width and height, or
 * not.
 * 
 * The attributes can be combined.
 * 
 * @author Markus Gronau 
 * @mm.docstatus finished
 */
public class CanvasImage {

	private static Logger logger = Logger.getLogger(CanvasImage.class.getName());

	private BufferedImage m_img;
	private double[] m_coords = new double[2];
	private double[] m_size = new double[2];
	private double[] m_screenCoords = new double[2];
	private double[] m_screenSize = new double[2];
	private String m_imageFileLocation;
	private MMG2DCanvas m_canvas;

	private boolean m_isStatic = false;
	private boolean m_isRepeating = false;

	/**
	 * Constructs a new CanvasImage that will be drawn at (0,0) in canvas
	 * coordinates. The source will be loaded from <code>location</code>.
	 */
	public CanvasImage(String location) {
		this(location, 0, 0, 0, 0, false, false);
	}

	/**
	 * Constructs a new CanvasImage that will be drawn at (x,y) in canvas
	 * coordinates. The source will be loaded from <code>location</code>.
	 */
	public CanvasImage(String location, double x, double y) {
		this(location, x, y, 0, 0, false, false);
	}

	/**
	 * Constructs a new CanvasImage that will be drawn at (x,y) in canvas
	 * coordinates. It will be resized to fit the specified size. Setting the
	 * width and height to zero will restore the images original size. The source
	 * will be loaded from <code>location</code>.
	 */
	public CanvasImage(
		String location,
		double x,
		double y,
		double width,
		double height) {
		this(location, x, y, width, height, false, false);
	}

	/**
	 * Constructs a new CanvasImage that will be drawn at (x,y) in
	 * canvas-coordinates. Setting the width and height to zero will restore the
	 * image's original size. The source will be loaded from <code>location</code>.
	 *  
	 */
	public CanvasImage(
		String location,
		double x,
		double y,
		double width,
		double height,
		boolean isStatic,
		boolean isRepeating) {
		setImage(location);
		m_isStatic = isStatic;
		m_isRepeating = isRepeating;
		setCoordinates(x, y);
		setSize(width, height);
	}
	
	public CanvasImage() {
		this((BufferedImage)null, 0, 0, 0, 0, false, false);
	}
	
	public CanvasImage(BufferedImage img) {
		this(img, 0, 0, 0, 0, false, false);
	}

	public CanvasImage(
		BufferedImage img,
		double x,
		double y,
		double width,
		double height,
		boolean isStatic,
		boolean isRepeating) {
		setImage(img);
		m_isStatic = isStatic;
		m_isRepeating = isRepeating;
		setCoordinates(x, y);
		setSize(width, height);
	}

	/**
	 * Loads and sets the image.
	 */
	public void setImage(String loc) {
		m_imageFileLocation = loc;
		m_img = Graphics2DUtils.loadImageFromClasspath(loc);
	}

	/**
	 * Sets the real image. 
	 */
	public void setImage(BufferedImage bi) {
		if (bi == null) {
			logger.warning("The image cannot be read from the buffer!");
			return;
		}
		m_img = bi;
	}

	/**
	 * Returns the real image.
	 */
	public BufferedImage getImage() {
		return m_img;
	}

	/**
	 * Returns the file location from where the image is loaded.
	 * 
	 * @return the file location or null if this CanvasImage was initialized with
	 *         a BufferedImage instead of a file location
	 */
	public String getImageFileLocation() {
		return m_imageFileLocation;
	}

	/**
	 * Returns wheather the absolute or relative coordinates of the picture.
	 */
	public double[] getCoordinates() {
		if (isStatic())
			return m_screenCoords;
		else
			return m_coords;
	}

	/**
	 * Sets the absolute or relative coordinates of the image's left bottom corner. 
	 */
	public void setCoordinates(double x, double y) {
		if (isStatic()) {
			m_screenCoords[0] = x;
			m_screenCoords[1] = y;
		} else {
			m_coords[0] = x;
			m_coords[1] = y;
		}
	}

	/**
	 * Sets the size of this image. The sizes will be stored as absolute or
	 * relative values depending on the images type (static or not).
	 */
	public void setSize(double width, double height) {
		if (isStatic()) {
			if (width == 0)
				m_screenSize[0] = m_img.getWidth();
			if (height == 0)
				m_screenSize[1] = m_img.getHeight();
		} else {
			if (width == 0)
				m_size[0] = 0;
			else
				m_size[0] = width;
			if (height == 0)
				m_size[1] = 0;
			else
				m_size[1] = height;
		}
	}

	public double[] getSize() {
		if (isStatic())
			return m_screenSize;
		else
			return m_size;
	}

	public boolean isStatic() {
		return m_isStatic;
	}

	public boolean isRepeating() {
		return m_isRepeating;
	}

	/**
	 * Draws the image onto the canvas. If the image is null (i.e. the image file
	 * could not be found) or the canvas is null, the method does nothing.
	 */
	public void drawImage() {
		if (m_img == null || m_canvas == null)
			return;
		
		double heightTranslationValueForNonStatic = 0;
		if (!isStatic()) {
			m_canvas.getWorld2Screen().applyTo(
				m_coords[0],
				m_coords[1],
				m_screenCoords);
			if (m_size[0] == 0 || m_size[1] == 0) {
				m_screenSize[0] = m_img.getWidth();
				m_screenSize[1] = m_img.getHeight();
			} else {
				m_screenSize[0] = m_size[0];
				// minus because image coords and math-coords are different in y-axis
				m_screenSize[1] = -m_size[1];
				m_canvas.getWorld2Screen().applyDeformationPartTo(m_screenSize);
			}
			heightTranslationValueForNonStatic = m_screenSize[1];
		}
		Graphics2D g2 = m_canvas.getGraphics2D();
		if (isRepeating()) {
			if(g2 == null)
				return;
			Paint oldPaint = g2.getPaint();
			g2.setPaint(
				new TexturePaint(
					m_img,
					new Rectangle2D.Double(
						m_screenCoords[0],
						m_screenCoords[1] - heightTranslationValueForNonStatic,
						m_img.getWidth(),
						m_img.getHeight())));
			g2.fillRect(
				(int)m_screenCoords[0],
				(int) (m_screenCoords[1] - heightTranslationValueForNonStatic),
				(int)m_screenSize[0],
				(int)m_screenSize[1]);
			g2.setPaint(oldPaint);
		} else {
			if(g2 == null)
				return;
//			g2.drawString("Srekimg", 10,10);
			m_img.flush();
			g2.drawImage(
				m_img,
				(int)m_screenCoords[0],
				(int) (m_screenCoords[1] - heightTranslationValueForNonStatic),
				(int)m_screenSize[0],
				(int)m_screenSize[1],
				MumieTheme.getCanvasBackground(),
				null);
		}
		m_canvas.repaint();
	}
	
	/**
	 * Sets the color of the pixel with the specified (image) coordinates.
	 */
	public void setPixelColor(Color c, int x, int y) {
		m_img.setRGB(x, y, c.getRGB());
	}
	
	/**
	 * Returns the color of the pixel with the specified (image) coordinates.
	 */
	public Color getPixelColor(int x, int y) {
		return new Color(m_img.getRGB(x, y));
	}
	
	/**
	 * Returns the ratio between width and height.
	 * Calculation: height = width * ratio (i.e. y = x * r)
	 */
	public double getDimensionRatio() {
		return m_img.getHeight() / m_img.getWidth();
	}
		
	/**
	 * Sets the canvas in which this CanvasImage will be drawn.
	 * Must be called before the image will be drawn the first time.
	 */
	public void setCanvas(MMG2DCanvas canvas) {
		m_canvas = canvas;
	}
}
