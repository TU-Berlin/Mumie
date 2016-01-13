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

package net.mumie.mathletfactory.display.noc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.border.EmptyBorder;

import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.util.MMImage;
import net.mumie.mathletfactory.transformer.ContainerObjectTransformer;

/**
 * This class is the container drawable for {@link net.mumie.mathletfactory.mmobject.util.MMImage MMImages}.
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public class MMImagePanel extends MMPanel {
	
	public MMImagePanel(MMObjectIF masterObject, ContainerObjectTransformer transformer) {
		super(masterObject, transformer, new ImageRenderer());
		addMouseListener(new CompoundListener(this));
    // disable label for images
    setLabelVisible(false);
	}
	
	public void render() {
		super.render();
		getImageRenderer().setImage(getRealMaster().getRealImage());
		repaint();
	}
	
	private ImageRenderer getImageRenderer() {
		return (ImageRenderer) getViewerComponent();
	}
	
	/**
	 * Overrides the super implementation for adding the border width
	 * to ensure that the image is displayed correctly.
	 */
	public void setWidth(int width) {
		getImageRenderer().setWidth(width);
	}

	/**
	 * Overrides the super implementation for adding the border height
	 * to ensure that the image is displayed correctly.
	 */
	public void setHeight(int height) {
		getImageRenderer().setHeight(height);
	}
	
	protected Dimension getPreferredPanelSize() {
		Dimension result = getImageRenderer().getPreferredSize();
		Insets insets = getInsets();
		result.width += insets.left + insets.right;
		result.height += insets.top + insets.bottom;
		return result;
	}
	
	public MMImage getRealMaster() {
		return (MMImage) getMaster();
	}
}

class ImageRenderer extends JComponent {
	
	// the black border, that will be drawn around the image
	private final static int INNER_BORDER_SIZE = 1;

	private int m_width, m_height;
	
	private Image m_image;
	
	ImageRenderer() {
		super();
		setBorder(new EmptyBorder(1, 1, 1, 1));// border is drawn explizitly
	}
	
	public void setImage(Image img) {
		m_image = img;
	}
	
	public Image getImage() {
		return m_image;
	}
	
	public void setHeight(int height) {
		m_height = height;
	}
	
	public void setWidth(int width) {
		m_width = width;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// no image set
//		Insets insets = getInsets();
		if(getImage() == null) {
			int maxWidth = getWidth() - getBorderWidth();
			int maxHeight = getHeight() - getBorderHeight();
//				g.setColor(getBackground());
//				g.fillRect(getLeftBorderSize(), getTopBorderSize(), maxWidth, maxHeight);
			if(m_width == -1 || m_height == -1)
				return;
			int width = m_width;
			int height = m_height;
			if(width > maxWidth || height > maxHeight) {
				double ratio = (double)maxWidth / (double)width;
				if(height * ratio > maxHeight)
					ratio = (double)maxHeight / (double)height;
			  width = (int) (width * ratio);
			  height = (int) (height * ratio);
			}
			int xPos = (int) Math.abs(width - maxWidth) / 2 + getLeftBorderSize();
			int yPos = (int) Math.abs(height - maxHeight) / 2 + getTopBorderSize();
			g.setColor(Color.BLACK);
			g.drawRect(xPos, yPos, width, height);
			g.setColor(Color.WHITE);
			g.fillRect(xPos+1, yPos+1, width-1, height-1);
		} else { // image is set
			// the image's real size 
			double orgWidth = getImage().getWidth(null);
			double orgHeight = getImage().getHeight(null);
			// scale image if it is too big for this panel to be completely visible
			int newWidth = (int) orgWidth;
			int newHeight = (int) orgHeight;
			if(orgWidth > m_width || orgHeight > m_height) {
				double ratio = (double)m_width / (double)orgWidth;
				if(orgHeight * ratio > m_height)
					ratio = (double)m_height / (double)orgHeight;
			  newWidth = (int) (orgWidth * ratio);
			  newHeight = (int) (orgHeight * ratio);
			}
			// needed if this panel is bigger than the image
			int stillAvailableWidth = getWidth() - (m_width + getBorderWidth());
			int stillAvailableHeight = getHeight() - (m_height + getBorderHeight());
			// starting points of image and border
			int xPos = (int) Math.abs(m_width - newWidth) / 2 + stillAvailableWidth / 2;// + getLeftBorderSize()
			int yPos = (int) Math.abs(m_height - newHeight) / 2 + stillAvailableHeight / 2;// + getTopBorderSize()
			// draw the image
			g.drawImage(getImage(), xPos + INNER_BORDER_SIZE, yPos + INNER_BORDER_SIZE, newWidth, newHeight, getBackground(), null);
			// draw the border
			g.setColor(Color.BLACK);
			g.drawRect(xPos, yPos, newWidth + INNER_BORDER_SIZE, newHeight + INNER_BORDER_SIZE);
		}
	}
	
	protected Dimension getPreferredSize(Dimension preferredSize) {
		Dimension result = new Dimension(preferredSize);
    if(m_width != -1)
    	result.width = m_width + 2 * INNER_BORDER_SIZE;
    if(m_height != -1)
    	result.height = m_height + 2 * INNER_BORDER_SIZE;
		return result;
	}

	public final Dimension getPreferredSize() {
		return getPreferredSize(super.getPreferredSize());
  }

  /**
   * Ensures that this component has always its preferred size
   * if the layout manager this component is handled by also respects
   * the minimum size.
   * 
   * @see JComponent#getMinimumSize()
   */
  public Dimension getMinimumSize() {
  	return getPreferredSize();
  }
  
  /**
   * Ensures that this component has always its preferred size
   * if the layout manager this component is handled by also respects
   * the maximum size (e.g. {@link javax.swing.BoxLayout}).
   * 
   * @see JComponent#getMaximumSize()
   */
  public Dimension getMaximumSize() {
  	return getPreferredSize();
  }
  
	int getLeftBorderSize() {
		if(getBorder() != null)
			return getBorder().getBorderInsets(this).left;
		else
			return 1;
	}
	
	int getTopBorderSize() {
		if(getBorder() != null)
			return getBorder().getBorderInsets(this).top;
		else
			return 1;
	}
	
	int getBorderWidth() {
		if(getBorder() != null)
			return getBorder().getBorderInsets(this).left + getBorder().getBorderInsets(this).right;
		else
			return 2;
	}
	
	int getBorderHeight() {
		if(getBorder() != null)
			return getBorder().getBorderInsets(this).top + getBorder().getBorderInsets(this).bottom;
		else
			return 2;
	}
	
}

