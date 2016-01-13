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

package net.mumie.mathletfactory.display.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JComponent;

import net.mumie.mathletfactory.display.layout.Alignable;
import net.mumie.mathletfactory.display.layout.SimpleInsets;

public class ViewPanel extends JComponent implements Alignable {

	private ViewIF m_view;
	
	public ViewPanel(ViewIF view) {
		if(view == null)
			throw new NullPointerException("View must not be null !");
		m_view = view;
		setFocusable(false);
		// use component listener to track changes of panel's size (needed for updating line breaks)
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				Insets insets = getInsets();
				int newWidth = getSize().width - insets.left - insets.right;
				if(getView().getMaximumSize().width != newWidth) {
					getView().setMaximumSize(new Dimension(newWidth, -1));
					revalidate();
				}
			}
		});
	}
	
	protected void setView(ViewIF view) {
		// copy current values for font and foreground to new view
    // TODO: bug: overwrites view attributes with theme properties --> setting here default values instead?
		view.setAttribute(ViewIF.FONT_ATTRIBUTE, getFont());
		view.setAttribute(ViewIF.FOREGROUND_ATTRIBUTE, getForeground());
		// store old maximum size if available
		if(m_view != null && m_view.getMaximumSize() != null)
			view.setMaximumSize(m_view.getMaximumSize());
		m_view = view;
		revalidate();
	}
	
	protected ViewIF getView() {
		return m_view;
	}
	
	public double getBaseline() {
		return m_view.getBaseline(getGraphics());
	}
	
	public Insets getInsets() {
		return new SimpleInsets(super.getInsets(), m_view.getInsets());
	}

	public Dimension getPreferredSize() {
		Dimension dim = new Dimension(0, 0);
		if(m_view == null || getGraphics() == null)
			return dim;
		dim.setSize(m_view.getPreferredSize(getGraphics()));
  	Insets insets = super.getInsets();
		dim.width += insets.left + insets.right;
		dim.height += insets.top + insets.bottom;
		return dim;
	}
	
	public Graphics getGraphics() {
		Graphics g = super.getGraphics();
		if(g != null)
			updateGraphics(g);
		return g;
	}
	
	private void updateGraphics(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
	}
	
	protected void paintComponent(Graphics g) {
		updateGraphics(g);
  	Graphics2D g2 = (Graphics2D) g.create();
  	Insets insets = super.getInsets();
		int innerW = getWidth() - insets.left - insets.right;
		int innerH = getHeight() - insets.top - insets.bottom;
		m_view.setSize(new Dimension(innerW, innerH));
		m_view.paint(g2, insets.left, insets.top, innerW, innerH);
		g2.dispose();
	}
	
	public void setFont(Font f) {
		super.setFont(f);
		if(getView() != null)
			getView().setAttribute(ViewIF.FONT_ATTRIBUTE, f);
    revalidate();
	}
	
	public Font getFont() {
		return getView().getFont();
	}
	
	public void setForeground(Color c) {
		super.setForeground(c);
		if(getView() != null)
			getView().setAttribute(ViewIF.FOREGROUND_ATTRIBUTE, c);
    repaint();
	}
	
	public Color getForeground() {
		return getView().getForeground();
	}
}
