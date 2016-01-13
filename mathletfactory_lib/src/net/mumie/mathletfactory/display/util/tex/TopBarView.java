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

public class TopBarView extends AbstractView {

	private int m_marginToBar = 1;
	
	public ViewIF copy() {
		return new TopBarView();
	}
	
	public double getBaseline(Graphics g) {
		return m_margin.top + m_marginToBar - 1 + g.getFontMetrics().getAscent();
	}
	
	public Dimension getPreferredSize(Graphics context) {
		Dimension d = getChild(0).getPreferredSize(context);
		d.height = d.height + m_marginToBar + 1;
		return d;
	}

	public void paint(Graphics g, int x, int y, int width, int height) {
    Color oldColor = g.getColor();
    Font oldFont = g.getFont();
		g.setFont(getFont());
		g.setColor(getForeground());
		super.paint(g, x, y, width, height);
		int xPos = x + m_margin.left;
		int yPos = y + m_margin.top;
		Dimension d = getChild(0).getPreferredSize(g);
		getChild(0).paint(g, xPos, yPos, d.width, yPos+d.height);
		g.drawLine(xPos, yPos+m_marginToBar, xPos + width - 1, yPos+m_marginToBar);
    g.setColor(oldColor);
    g.setFont(oldFont);
	}
}
