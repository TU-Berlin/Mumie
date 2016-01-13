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

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

/**
 * A utility class for handling hardcopies.
 * 
 * @author Amsel 
 * @mm.docstatus finished
 */

public class Graphics2DUtils {

	/** Makes a hardcopy image from the given component. */
	public static BufferedImage acquireScreenshot(Component component)
		throws AWTException {
		Rectangle rect = component.getBounds();
		Insets insets = null;
		if (component instanceof Window) {
			insets = ((Window)component).getInsets();
			rect.width -= insets.left + insets.right;
			rect.height -= insets.top + insets.bottom;
		}

		BufferedImage image =
			new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_RGB);
		Graphics gr = image.getGraphics();

		if (insets != null) {
			gr.translate(-insets.left, -insets.top);
		}
		component.paintAll(gr);
		gr.dispose();
		return image;

	}

	/**
	 * Loads an image from the classpath, i.e. the image is included in the library.
	 */
	public static BufferedImage loadImageFromClasspath(String path) {
		try {
		  InputStream in = Graphics2DUtils.class.getResourceAsStream(path);
			if (in == null) {
				System.err.println("The image \"" + path + "\" cannot be found!");
				return null;
			}
			BufferedInputStream bin = new BufferedInputStream(in);
			return ImageIO.read(bin);
		} catch (IOException ioex) {
			System.err.println(ioex.toString());
			return null;
		}
	}
	
	/**
	 * Loads an image with the given file name.
	 */
	public static BufferedImage loadImageFromFile(String filepath) {
		return loadImageFromFile(new File(filepath));
	}

	/**
	 * Loads an image from the given File object.
	 */
	public static BufferedImage loadImageFromFile(File path) {
		try {
			return ImageIO.read(path);
		} catch (IOException ioex) {
			System.err.println(ioex.toString());
			return null;
		}
	}

	
	/**
	 * Creates a new buffered image with the same
	 * {@link java.awt.image.ColorModel}, size and alpha settings as its source
	 * image. It does not copies its pixel colors.
	 */
	public static BufferedImage createCompatibleImage(BufferedImage src) {
		return new BufferedImage(
			src.getColorModel(),
			src.getColorModel().createCompatibleWritableRaster(
				src.getWidth(),
				src.getHeight()),
			src.getColorModel().isAlphaPremultiplied(),
			null);
	}

	/**
	 * Creates a new buffered image with the same
	 * {@link java.awt.image.ColorModel} and alpha settings as its source
	 * image. It does not copies its pixel colors.
	 */
	public static BufferedImage createCompatibleImage(
		BufferedImage src,
		int width,
		int height) {
		return new BufferedImage(
			src.getColorModel(),
			src.getColorModel().createCompatibleWritableRaster(width, height),
			src.getColorModel().isAlphaPremultiplied(),
			null);
	}
	
	/**
	 * Creates and returns a new buffered grayscaled version of the given image.
	 * The newly created image has the same size and color model as the given image.
	 */
	public static BufferedImage createGrayscaledImage(BufferedImage img) {
//		ColorSpace srcSpace = img.getColorModel().getColorSpace();
//		ColorSpace destSpace = new ICC_ColorSpace(ICC_Profile.getInstance(ColorSpace.CS_GRAY));
//		ColorConvertOp op = new ColorConvertOp(srcSpace, destSpace, null);
//		BufferedImage grayImage = op.filter(img, null);
//		BufferedImage result = createCompatibleImage(img);
//		Graphics2D g = result.createGraphics();
////		g.fillRect(0, 0, img.getWidth(), img.getHeight());
//		g.drawRenderedImage(grayImage, null);
//		g.dispose();
//		return result;
		RGBImageFilter filter = new RGBImageFilter() {
			public int filterRGB(int x, int y, int rgb) {
				int red = (rgb >> 16) & 0xFF;
				int green = (rgb >> 8) & 0xFF;
				int blue = (rgb >> 0) & 0xFF;
				int gray = (red + green + blue)/3;
				// alpha = 255 (opaque)
				// red, green, blue = gray
				int grayRGB = 0xff000000 | ((gray & 0xFF) << 16) | ((gray & 0xFF) << 8)  | ((gray & 0xFF) << 0);
				return grayRGB;
			}
		};
		ImageProducer prod = new FilteredImageSource(img.getSource(), filter);
		Image disabledImage = Toolkit.getDefaultToolkit().createImage(prod);
		BufferedImage destImage = Graphics2DUtils.createCompatibleImage(img);
		Graphics2D g = destImage.createGraphics();
		g.drawImage(disabledImage, 0, 0, null);
		g.dispose();
		return destImage;
	}
	
	/**
	 * Creates and returns a new buffered image filled with the given image.
	 * This method allways draws as much of the given image as possible.
	 * The new image will be a shrinked and croped version of the given one.
	 * 
	 * @param width the width of the created image
   * @param height the height of the created image
   * @param img the image which will be drawn onto the created image
	 */
	public static BufferedImage createFilledImage(int width, int height, BufferedImage img) {
		double orgWidth = img.getWidth();
		double orgHeight = img.getHeight();
//		if(orgWidth < width || orgHeight < height)
//			throw new IllegalArgumentException("Image is too small and cannot be shrinked to fill rectangle!");
		double ratio = (double)width / (double)orgWidth;
		if(orgHeight * ratio < height)
			ratio = (double)height / (double)orgHeight;
		int newWidth = (int) (orgWidth * ratio);
		int newHeight = (int) (orgHeight * ratio);
//		System.out.println("New width: " + newWidth + ", new height: " + newHeight);
		// better results in shrinking as scale filters
		Image scaledImage = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
//	Image scaledImage = Toolkit.getDefaultToolkit().createImage(
//	new FilteredImageSource(
//			image.getImage().getSource(), 
//			new AreaAveragingScaleFilter(width, height)));
		Image cropedImage = Toolkit.getDefaultToolkit().createImage(
				new FilteredImageSource(
						scaledImage.getSource(), 
						new CropImageFilter(0, 0, width, height)));
		BufferedImage result = Graphics2DUtils.createCompatibleImage(img, width, height);
		Graphics2D g2 = result.createGraphics();
		g2.drawImage(cropedImage, 0, 0, null);
		g2.dispose();
		return result;
	}

	/** Checks, whether JMF (=Java Media Framework) is available. */
	public static boolean isJMFAvailable() {
		try {
			Class.forName("javax.media.Manager");
		} catch (ClassNotFoundException e) {
			return false;
		}
		return true;
	}
}
