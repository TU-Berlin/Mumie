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

package net.mumie.mathletfactory.mmobject.util;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.number.MInteger;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.mmobject.MMDefaultCanvasObject;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 * This class is the "MM" implementation of images which can be drawn in a canvas and a container.
 * The type of an image affects its coordinates and its size: static images are independant of the canvas'
 * current perspective and zoom whereas non-static images are "embedded" into the mathematical "world".  
 * Therefore coordinates and sizes for static images have to be in pixel values and for non-static images in
 * "world" values. 
 * Static images can be zoomable and movable onto the canvas. 
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public class MMImage extends MMDefaultCanvasObject {

	private BufferedImage m_img;
	
	private double[] m_coords = new double[2];
	private double[] m_size = new double[2];
	private double[] m_screenCoords = new double[2];
	private double[] m_screenSize = new double[2];
	private NumberMatrix m_grayToneMatrix;

	private boolean m_isStatic = false;
//	private boolean m_isRepeating = false;
//	private boolean m_alwaysAutoscale = false;

	/**
	 * Constructs a new (empty) <code>MMImage</code> that will be drawn at (0,0) in screen coordinates (static image)
	 * with the images's original size in pixels.
	 */
	public MMImage() {
		this(-1, -1);
	}
	
	/**
	 * Constructs a new (empty) <code>MMImage</code> that will be drawn at (0,0) whether in absolute screen coordinates (static image)
	 * or in relative "world" coordinates (non static image) with the images's original size in pixels.
	 */
	public MMImage(boolean isStatic) {
		this(-1, -1, -1, -1, isStatic);
	}
	
	/**
	 * Constructs a new (empty) <code>MMImage</code> that will be drawn at (0,0) in screen coordinates (static image)
	 * with the specified size in pixels. 
	 * This image will be static (nor zoomable, nor movable) onto the canvas. 
	 */
	public MMImage(
		double width,
		double height) {
		this(0, 0, width, height, true);
	}
	
	/**
	 * Constructs a new (empty) <code>MMImage</code> that will be drawn at (x,y) whether in absolute screen coordinates (static image)
	 * or in relative "world" coordinates (non static image) with the specified size whether in pixels or in "world" sizes. 
	 */
	public MMImage(double x, double y, double width,
			double height, boolean isStatic) {
		m_isStatic = isStatic;
		setCoordinates(x, y);
		setSize(width, height);
	}

//	/**
//	 * Loads the image from the specified location.
//	 */
//	public void setRealImage(String loc) {
//		m_img = Graphics2DUtils.loadImageFromFile(loc);
//		render();
//		invokeUpdaters();
//	}
	
	private void setImageContent(BufferedImage bi) {
		m_img = bi;
		render();
		invokeUpdaters();
	}

	/**
	 * Sets the content of the given image. May be null.
	 */
	public void setRealImage(BufferedImage bi) {
			m_img = bi;
			m_grayToneMatrix = null;
		render();
		invokeUpdaters();
//		if(m_alwaysAutoscale)
//			((MM2DCanvas)getCanvas()).autoScale(true);
	}
	
	/**
	 * Returns the real image. May be null.
	 */
	public BufferedImage getRealImage() {
		return m_img;
	}
	
	/**
	 * Returns if the internal stored image is empty, i.e. not yet loaded.
	 */
	public boolean isEmpty() {
		return getRealImage() == null;// || !isGrayScaled();
	}
	
	/**
	 * Returns if the image is in gray tones.
	 */
	public boolean isGrayScaled() {
		return m_grayToneMatrix != null;
	}

	/**
	 * Returns for static images the absolute coordinates of the top left corner 
	 * or (for non static images) relative coordinates of the image's lef.
	 */
	public double[] getCoordinates() {
		if (isStatic())
			return m_screenCoords;
		else
			return m_coords;
	}

	/**
	 * Sets for static images the absolute (screen) coordinates of the image's top left corner 
	 * where in the other case (for non static images) the values are treated as relative "world" coordinates 
	 * and are used to calculate the screen coordinates.
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
	 * Values for static images describe directly the screen size where in the other case
	 * (non static images) the screen size is calculated.
	 */
	public void setSize(double width, double height) {
		if (isStatic()) {
//			if(m_img != null && width == -1)
//				m_screenSize[0] = m_img.getWidth();
//			else
				m_screenSize[0] = width;
//			if (m_img != null && height == -1)
//				m_screenSize[1] = m_img.getHeight();
//			else
				m_screenSize[1] = height;
		} else {
//			if (width == 0)
//				m_size[0] = 0;
//			else
				m_size[0] = width;
//			if (height == 0)
//				m_size[1] = 0;
//			else
				m_size[1] = height;
		}
	}

	/**
	 * Returns the size of this image. Depending on the image type, this method
	 * returns the absolute screen size (static image) or the image's relative "world size" (non static image).
	 */
	public double[] getSize() {
		if (isStatic())
			return m_screenSize;
		else
			return m_size;
	}
	
	/**
	 * Returns if this image is static.
	 */
	public boolean isStatic() {
		return m_isStatic;
	}

//	public boolean isRepeating() {
//		return m_isRepeating;
//	}
	
//	/**
//	 * Sets if the canvas should be autoscaled after changes of this image.
//	 */
//	public void setAlwaysAutoscale(boolean autoscale) {
//		m_alwaysAutoscale = autoscale;
//	}
//	
//	/**
//	 * Returns if the canvas should be autoscaled after changes of this image.
//	 */
//	public boolean isAlwaysAutoscale() {
//		return m_alwaysAutoscale;
//	}

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
	 * Returns a new {@link NumberMatrix} describing the image's pixels with gray tones.
	 * Each pixel defines an entry in the matrix. Values from 0 to 255 are used for the brightness.
	 * The "edited"-flags (if they exist, i.e. if they were set from another matrix) are copied to the matrix. 
	 */
	public NumberMatrix getGrayToneMatrix() {
		if(m_grayToneMatrix != null)
			return m_grayToneMatrix;
		NumberMatrix m = new NumberMatrix(MInteger.class, getRealImage().getWidth(), getRealImage().getHeight());
		getGrayToneMatrix(m);
		return m;
	}
	
	/**
	 * Converts the image's colors into gray tones and stores the result in the given {@link NumberMatrix}.
	 * Each pixel defines an entry in the matrix. Values from 0 to 255 are used for the brightness.
	 * The "edited"-flags (if they exist, i.e. if they were set from another matrix) are copied to the matrix. 
	 */
	public void getGrayToneMatrix(NumberMatrix m) {
		if(m.getRowCount() != getRealImage().getHeight() || m.getColumnCount() != getRealImage().getWidth())
			throw new IllegalArgumentException("Gray tone matrix must have same dimensions as this image!");
		if(m_grayToneMatrix != null) {
			m.copyFrom(m_grayToneMatrix, true);
			return;
		}
		for(int r = 0; r < m.getRowCount(); r++) {
			for(int c = 0; c < m.getColumnCount(); c++) {
				int rgb = getRealImage().getRGB(c, r);
				int red = (rgb >> 16) & 0xFF;
				int green = (rgb >> 8) & 0xFF;
				int blue = (rgb >> 0) & 0xFF;
				int gray = (red + green + blue)/3;
				m.setEntry(r+1, c+1, gray);
			}
		}
	}
	
	/**
	 * Sets this image's content to the values of the given matrix by interpreting
	 * the numbers as gray tone values. This image will have the dimensions of the matrix.
	 * Each entry in the matrix defines a pixel. Values from -255 to 255 can be used for the brightness.
	 */
	public void setGrayToneMatrix(NumberMatrix m) {
		BufferedImage img = new BufferedImage(m.getColumnCount(), m.getRowCount(), BufferedImage.TYPE_INT_ARGB);
		for(int r = 1; r <= m.getRowCount(); r++) {
			for(int c = 1; c <= m.getColumnCount(); c++) {
				// alpha = 255 (opaque)
      	MNumber entry = (MNumber) m.getEntryRef(r, c);
				int value = (int) m.getEntry(r, c).getDouble();
				int rgb = 0;
      	if( ! entry.isEdited()) {
      		rgb = 0xff000000 | ((255 & 0xFF) << 16) | ((175 & 0xFF) << 8)  | ((175 & 0xFF) << 0); // rosa for non edited numbers
      	} else if(value > 510) { // green for numbers > 510
      		rgb = 0xff000000 | ((0 & 0xFF) << 16) | ((255 & 0xFF) << 8)  | ((0 & 0xFF) << 0);
      	} else if(value > 255) { // 255: white -> green: 510
      		int i = Math.abs(value - 510);
      		rgb = 0xff000000 | ((i & 0xFF) << 16) | ((255 & 0xFF) << 8)  | ((i & 0xFF) << 0);
      	} else if(value >= 0) { // 0: black -> white: 255
      		rgb = 0xff000000 | ((value & 0xFF) << 16) | ((value & 0xFF) << 8)  | ((value & 0xFF) << 0);
      	} else if(value < -255) { // red for numbers < -255
      		rgb = 0xff000000 | ((255 & 0xFF) << 16) | ((0 & 0xFF) << 8)  | ((0 & 0xFF) << 0);
      	} else { // -255: red -> 0: black
      		int i = Math.abs(value);
      		rgb = 0xff000000 | ((i & 0xFF) << 16) | ((0 & 0xFF) << 8)  | ((0 & 0xFF) << 0);
      	}
				img.setRGB(c-1, r-1, rgb);
			}
		}
		m_grayToneMatrix = new NumberMatrix(MInteger.class, m.getColumnCount(), m.getRowCount());
		m_grayToneMatrix.copyFrom(m, true);
		setImageContent(img);
	}
	
	/**
	 * Returns the PSNR (Peak Signal to Noise Ratio) of this and <code>anImage</code>.
	 * Both images must have the same size.
	 */
  public MNumber PSNR (MMImage anImage) {
  	return PSNR(this, anImage);
  }

  /**
	 * Returns the PSNR (Peak Signal to Noise Ratio) of 2 images.
	 * The gray tone matrix of each image is used for the calculations.
	 * Both images must have the same size.
	 */
  public static MNumber PSNR (MMImage image1, MMImage image2){
  	return PSNR(image1.getGrayToneMatrix(), image2.getGrayToneMatrix());
  }

  /**
	 * Returns the PSNR (Peak Signal to Noise Ratio) of 2 image matrices.
	 * The gray tone matrix of each image is used for the calculations.
	 * Both matrices must have the same shape.
	 */
  public static MNumber PSNR (NumberMatrix matrix1, NumberMatrix matrix2){
  	if(!matrix1.hasSameShape(matrix2))
  		throw new IllegalArgumentException("Both matrices must have the same shape!");
    int C = matrix1.getColumnCount();
    int R = matrix1.getRowCount();
    double logArg = (255.0 * Math.sqrt(R*C)) / (NumberMatrix.distance(matrix1, matrix2).getDouble());
  	MNumber psnr = NumberFactory.newInstance(matrix1.getNumberClass());
    psnr.setDouble(20 * Math.log(logArg)/Math.log(10));
    return psnr;
  }

	/**
	 * Returns the ratio between width and height.
	 * Calculation: height = width * ratio (i.e. y = x * r)
	 */
	public double getDimensionRatio() {
		return getRealImage().getHeight() / getRealImage().getWidth();
	}
	
	/**
	 * Returns the image's width.
	 */
	public int getWidth() {
		return (int) getSize()[0];
	}
	
	/**
	 * Returns the image's height.
	 */
	public int getHeight() {
		return (int) getSize()[1];
	}

	public int getDefaultTransformType() {
		return GeneralTransformer.IMAGE_TRANSFORM;
	}

	public Class getNumberClass() {
		return MInteger.class;
	}

  public Rectangle2D getWorldBoundingBox() {
  	if(m_img == null || isStatic())
  		return null;
    Rectangle2D result = new Rectangle2D.Double();
  	double[] size = { getRealImage().getWidth(), getRealImage().getHeight() };//new double[2];
//    	((Canvas2DObjectTransformer)getCanvasTransformer()).getScreen2World().applyDeformationPartTo(size);
  	if(m_size[0] != -1)
  		size[0] = m_size[0];
  	if(m_size[1] != -1)
  		size[1] = m_size[1];
//    	System.out.println("width="+size[0]);
//    	System.out.println("height="+size[1]);
    result.setRect(getCoordinates()[0], getCoordinates()[1], size[0], size[1]);    	
    return result;
  }
}
