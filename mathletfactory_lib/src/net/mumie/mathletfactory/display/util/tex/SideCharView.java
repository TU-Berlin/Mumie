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

public class SideCharView extends AbstractView {

	public final static int DET_TYPE = 0;
	
	public final static int ABS_TYPE = 1;
		
	private final static String[][] TYPE_CHARS = {
			{"|", "|"}, // DET_TYPE
			{"||", "||"} // ABS_TYPE
		};
	
	private final static int[][] CHAR_OFFSETS = {
			{0, 0}, // DET_TYPE
			{0, 0} // ABS_TYPE
		};

	private final int m_type;
	private final String[] m_arrowString;
	private final int[] m_charXOffsets;
	
	public SideCharView(int type) {
		m_type = type;
		m_arrowString = TYPE_CHARS[type];
		m_charXOffsets = CHAR_OFFSETS[type];
	}
	
	public ViewIF copy() {
		return new SideCharView(m_type);
	}

	public double getBaseline(Graphics g) {
		return getChild(0).getBaseline(g) + getChild(0).getInsets().top;
	}
	
	public Dimension getPreferredSize(Graphics context) {
		Dimension result = getChild(0).getPreferredSize(context);
		result.width += context.getFontMetrics(getFont()).getStringBounds(m_arrowString[0], context).getWidth() + m_charXOffsets[0];
		result.width += context.getFontMetrics(getFont()).getStringBounds(m_arrowString[1], context).getWidth() + m_charXOffsets[1];
		result.width += m_margin.left + m_margin.right;
		result.height += m_margin.top + m_margin.bottom;
		return result;
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
		Rectangle2D rect = g.getFontMetrics(getFont()).getStringBounds(m_arrowString[0], g);
		// paint child at first to lay out baseline
		Dimension d = getChild(0).getPreferredSize(g);
		getChild(0).paint(g, (int) (xPos + rect.getWidth() + m_charXOffsets[0]), yPos, d.width, d.height);
		// paint left char
		g.drawString(m_arrowString[0], xPos, (int) (yPos + getChild(0).getBaseline(g)));
		// paint right char
		rect = g.getFontMetrics(getFont()).getStringBounds(m_arrowString[1], g);
		g.drawString(m_arrowString[1], (int) (xPos + innerW + m_margin.left - rect.getWidth()), (int) (yPos + getChild(0).getBaseline(g)));
    g.setColor(oldColor);
    g.setFont(oldFont);
	}
}
