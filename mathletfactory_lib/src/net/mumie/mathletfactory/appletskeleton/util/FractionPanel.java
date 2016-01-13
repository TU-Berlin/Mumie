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

package net.mumie.mathletfactory.appletskeleton.util;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.LayoutManager;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;

/**
 * This class can be used to display 2 SWING components as a "fraction" (i.e. in a column with
 * a fraction bar between them). The color and the width of the fraction bar and the vertical gap 
 * (i.e. the free space between a component and the fraction bar) can be adjusted.
 * 
 * @see JComponent#setForeground(java.awt.Color)
 * @see #setFractionWidth(int)
 * @see #setVerticalGap(int)
 * 
 * @author Gronau
 * @mm.docstatus finished
 */
public class FractionPanel extends JPanel {
	
	private int m_verticalGap = 1;
	private int m_fractionWidth = 1;
	
	/**
	 * Creates a new fraction panel for the 2 given components.
	 * 
	 * @param topComponent the component on the top
	 * @param bottomComponent the component on the bottom
	 */
	public FractionPanel(JComponent topComponent, JComponent bottomComponent) {
		super(null);
		super.setBorder(new FractionBorder());
		add(topComponent);
		add(bottomComponent);
		updateLayout();
	}
	
	private void updateLayout() {
		super.setLayout(new GridLayout(2, 1, 0, m_fractionWidth + 2 * m_verticalGap));
	}
	
	/**
	 * Sets the width of the fraction bar.
	 * Default is 1.
	 */
	public void setFractionWidth(int width) {
		m_fractionWidth = width;
		updateLayout();
	}
	
	/**
	 * Sets the vertical gap (i.e. the free space between a component and the fraction bar).
	 * Default is 1.
	 */
	public void setVerticalGap(int gap) {
		m_verticalGap = gap;
		updateLayout();
	}
	
	/**
	 * Must not be called.
	 */
	public void setLayout(LayoutManager lm) {
		if(lm != null)
			throw new IllegalArgumentException("Cannot change the layout of a FractionPanel !");
	}
	
	/**
	 * Must not be called.
	 */
	public void setBorder(Border border) {
		if(border != null)
			throw new IllegalArgumentException("Cannot change the border of a FractionPanel !");
	}
	
	/**
	 * This class can be used to draw a fraction bar with custom width and vertical gap.
	 */
	class FractionBorder extends AbstractBorder {
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			if(getComponentCount() != 2)
				return;
			height = getComponent(0).getHeight() + m_verticalGap;
			g.fillRect(0, height, width, m_fractionWidth);
		}			
	}
}
