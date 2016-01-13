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

package net.mumie.mathletfactory.transformer.g2d;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import net.mumie.mathletfactory.appletskeleton.util.MumieTheme;
import net.mumie.mathletfactory.display.CanvasDrawable;
import net.mumie.mathletfactory.display.g2d.G2DRectDrawable;
import net.mumie.mathletfactory.display.g2d.MMG2DCanvas;
import net.mumie.mathletfactory.mmobject.util.MMImage;
import net.mumie.mathletfactory.transformer.Affine2DDefaultTransformer;

/**
 * Transformer for {@link net.mumie.mathletfactory.mmobject.util.MMImage}.
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public class Image2DTransformer extends Affine2DDefaultTransformer {

	private double[] m_coords = new double[2];
	private double[] m_size = new double[2];
	private double[] m_screenCoords = new double[2];
	private double[] m_screenSize = new double[2];
	private double m_heightTranslationValueForNonStatic = 0;

	public Image2DTransformer() {
		m_allDrawables = new CanvasDrawable[] { new G2DRectDrawable()};
		m_activeDrawable = m_allDrawables[0];
	}
	
	public void synchronizeMath2Screen() {
		synchronizeWorld2Screen();
	}

	public void synchronizeWorld2Screen() {
		if(getRealMaster().getRealImage() == null)
			return;
		m_heightTranslationValueForNonStatic = 0;
		if (getRealMaster().isStatic()) {
			m_screenCoords = (double[]) getRealMaster().getCoordinates().clone();
			m_screenSize = (double[]) getRealMaster().getSize().clone();
			if(getRealMaster().getSize()[0] == -1)
				m_screenSize[0] = getRealMaster().getRealImage().getWidth();
			if(m_screenSize[1] == -1)
				m_screenSize[1] = getRealMaster().getRealImage().getHeight();
		} else {
			m_coords = (double[]) getRealMaster().getCoordinates().clone();
			getWorld2Screen().applyTo(m_coords, m_screenCoords);
			m_size = (double[]) getRealMaster().getSize().clone();
			if(m_size[0] == -1) {
				m_screenSize[0] = getRealMaster().getRealImage().getWidth();
			} else {
				m_screenSize[0] = m_size[0];
			}
			if(m_size[1] == -1) {
				m_screenSize[1] = -getRealMaster().getRealImage().getHeight();
			} else {
				// minus because image coords and math-coords are different in y-axis
				m_screenSize[1] = -m_size[1];
			}
			getWorld2Screen().applyDeformationPartTo(m_screenSize);
			m_heightTranslationValueForNonStatic = m_screenSize[1];
		}
	}
	
	public void draw() {
		super.draw();
		MMG2DCanvas canvas2d = (MMG2DCanvas) getMasterAsCanvasObject().getCanvas();
		BufferedImage img = getRealMaster().getRealImage();
		Graphics2D g2 = canvas2d.getGraphics2D();
		if (g2 == null)
			return;
//		if (getRealMaster().isRepeating()) {
//			Paint oldPaint = g2.getPaint();
//			g2.setPaint(new TexturePaint(img, new Rectangle2D.Double(
//					m_screenCoords[0], m_screenCoords[1]
//							- m_heightTranslationValueForNonStatic, img.getWidth(), img
//							.getHeight())));
//			g2.fillRect((int) m_screenCoords[0],
//					(int) (m_screenCoords[1] - m_heightTranslationValueForNonStatic),
//					(int) m_screenSize[0], (int) m_screenSize[1]);
//			g2.setPaint(oldPaint);
//		} else {
			// g2.drawString("Srekimg", 10,10);
//			img.flush();
			g2.drawImage(img, (int) m_screenCoords[0],
					(int) (m_screenCoords[1] - m_heightTranslationValueForNonStatic),
					(int) m_screenSize[0], (int) m_screenSize[1], MumieTheme
							.getCanvasBackground(), null);
//		}
	}

	private MMImage getRealMaster() {
		return (MMImage) getMaster();
	}
}
