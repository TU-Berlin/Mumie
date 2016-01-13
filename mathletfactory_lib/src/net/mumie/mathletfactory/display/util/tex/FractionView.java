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

package net.mumie.mathletfactory.display.util.tex;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import net.mumie.mathletfactory.display.util.AbstractView;
import net.mumie.mathletfactory.display.util.ViewIF;

public class FractionView extends AbstractView {

	private int m_marginToFraction = 1;
	
	public ViewIF copy() {
		return new FractionView();
	}
	
	public double getBaseline(Graphics g) {
		return m_margin.top + getChild(0).getPreferredSize(g).height + m_marginToFraction - 1 + g.getFontMetrics().getAscent()/2 -2;// workaround: "-2" is correction factor;
	}
	
	public Dimension getPreferredSize(Graphics context) {
		Dimension d1 = getChild(0).getPreferredSize(context);
		Dimension d2 = getChild(1).getPreferredSize(context);
		int width = Math.max(d1.width, d2.width) + m_margin.left + m_margin.right;
		int height = m_margin.top + d1.height + 2 * m_marginToFraction + d2.height + m_margin.bottom;
		return new Dimension(width, height);
	}

	public void paint(Graphics g, int x, int y, int width, int height) {
    Color oldColor = g.getColor();
    Font oldFont = g.getFont();
		g.setFont(getFont());
		g.setColor(getForeground());
		super.paint(g, x, y, width, height);
		int xPos = x + m_margin.left;
		int yPos = y + m_margin.top;
		int innerW = width - m_margin.left - m_margin.right;
		int innerH = height - m_margin.top - m_margin.bottom;
		Dimension d1 = getChild(0).getPreferredSize(g);
		Dimension d2 = getChild(1).getPreferredSize(g);
		int baseline = yPos + d1.height + m_marginToFraction;
		getChild(0).paint(g, xPos + (innerW - d1.width)/2, yPos, d1.width, d1.height);
		// draw base "line" on numerator bottom rather than on denominator top ("baseline-1")
		g.drawLine(xPos, baseline-1, xPos + innerW - 1, baseline-1);
		getChild(1).paint(g, xPos + (innerW - d2.width)/2, baseline + m_marginToFraction, d2.width, d2.height);
    g.setColor(oldColor);
    g.setFont(oldFont);
	}
}
