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
import java.awt.geom.Rectangle2D;

import net.mumie.mathletfactory.display.util.AbstractView;
import net.mumie.mathletfactory.display.util.ViewIF;

public class TopCharView extends AbstractView {

	public final static int VECTOR_TYPE = 0;
	
	public final static int DOT_TYPE = 1;
	
	public final static int DDOT_TYPE = 2;
	
	public final static int TILDE_TYPE = 3;
	
	private final static String[] TYPE_CHARS = {
			"\u2192", // VECTOR_TYPE
			".", // DOT_TYPE
			"..", // DDOT_TYPE
			"~" // TILDE_TYPE
		};
	
	private final static int[][] CHAR_OFFSETS = {
		{-1, 2}, // VECTOR_TYPE
		{2, 0}, // DOT_TYPE
		{0, 0}, // DDOT_TYPE
		{-1, 2} // TILDE_TYPE
	};

	private final static int[] CHAR_HEIGHTS = {
		6, // VECTOR_TYPE
		4, // DOT_TYPE
		4, // DDOT_TYPE
		6 // TILDE_TYPE
	};

	private final int m_type;
	private final String m_charString;
	private final int m_charYOffset, m_charXOffset;
	private final int m_charHeight;
	
	public TopCharView(int type) {
		m_type = type;
		m_charString = TYPE_CHARS[type];
		m_charXOffset = CHAR_OFFSETS[type][0];
		m_charYOffset = CHAR_OFFSETS[type][1];
		m_charHeight = CHAR_HEIGHTS[type];
	}
	
	public ViewIF copy() {
		return new TopCharView(m_type);
	}

	public double getBaseline(Graphics g) {
		return m_charHeight + getChild(0).getBaseline(g) + getChild(0).getInsets().top;
	}
	
	public Dimension getPreferredSize(Graphics context) {
		Dimension result = new Dimension(m_margin.left + m_margin.right, m_margin.top + m_margin.bottom);
		Dimension childDim = getChild(0).getPreferredSize(context);
		result.height += m_charHeight + childDim.height;
		result.width += Math.max(childDim.width, context.getFontMetrics().getStringBounds(m_charString, context).getWidth());
		return result;
	}

	public void paint(Graphics g, int x, int y, int width, int height) {
    Color oldColor = g.getColor();
    Font oldFont = g.getFont();
		g.setColor(getForeground());
    g.setFont(getFont());
		super.paint(g, x, y, width, height);
		int xPos = x + m_margin.left;
		int yPos = y + m_margin.top;
		// paint child at first to lay out baseline
		Dimension d = getChild(0).getPreferredSize(g);
		getChild(0).paint(g, xPos, (int) (yPos + m_charHeight), d.width, d.height);
		// paint top char
		Rectangle2D rect = g.getFontMetrics(getFont()).getStringBounds(m_charString, g);
		g.drawString(m_charString, xPos + getChild(0).getInsets().left + m_charXOffset, (int) (yPos + rect.getHeight()/2 + m_charYOffset));
    g.setColor(oldColor);
    g.setFont(oldFont);
	}
}
