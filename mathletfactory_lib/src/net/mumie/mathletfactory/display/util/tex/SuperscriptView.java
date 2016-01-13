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

public class SuperscriptView extends AbstractView {

	private int m_subScriptChildIndex = -1, m_superScriptChildIndex = -1;
	
	public ViewIF copy() {
		throw new RuntimeException("Method must no be called !");
	}
	
	public void setSubScript(boolean flag) {
		if(hasSuperScript())
			m_subScriptChildIndex = 2;
		else
			m_subScriptChildIndex = 1;
	}

	public void setSuperScript(boolean flag) {
		if(hasSubScript())
			m_superScriptChildIndex = 2;
		else
			m_superScriptChildIndex = 1;
	}
	
	private boolean hasSuperScript() {
		return getSuperScriptIndex() > -1;
	}
	
	private boolean hasSubScript() {
		return getSubScriptIndex() > -1;
	}
	
	private int getSubScriptIndex() {
		return m_subScriptChildIndex;
	}

	private int getSuperScriptIndex() {
		return m_superScriptChildIndex;
	}

	public double getBaseline(Graphics g) {
		ViewIF baseChild = getChild(0);
		double baseline = 0;
		if(hasSuperScript()) {
			ViewIF subChild = getChild(getSuperScriptIndex());
			Dimension subDim = subChild.getPreferredSize(g);
			baseline = m_margin.top + subDim.height/2 + baseChild.getBaseline(g);
		} else {
			baseline = m_margin.top + baseChild.getBaseline(g);
		}
		return baseline;
	}
	
	public Dimension getPreferredSize(Graphics context) {
		Dimension dim = getChild(0).getPreferredSize(context);
		int width = 0;
		if(hasSubScript()) {
			Dimension subDim = getChild(getSubScriptIndex()).getPreferredSize(context);
			width = Math.max(subDim.width, width);
			dim.height += subDim.height / 2;
		}
		if(hasSuperScript()) {
			Dimension subDim = getChild(getSuperScriptIndex()).getPreferredSize(context);
			width = Math.max(subDim.width, width);
			dim.height += subDim.height / 2;
		}
		dim.width += m_margin.left + m_margin.right + width;
		dim.height += m_margin.top + m_margin.bottom;
		return dim;
	}

	public void paint(Graphics g, int x, int y, int width, int height) {
    Color oldColor = g.getColor();
    Font oldFont = g.getFont();
		g.setFont(getFont());
		g.setColor(getForeground());
		super.paint(g, x, y, width, height);
		Dimension dim = getPreferredSize(g);
		int innerW = width - m_margin.left - m_margin.right;
		int xPos = x + m_margin.left + (innerW - dim.width) / 2;
		int yPos = y + m_margin.top;
		ViewIF baseChild = getChild(0);
		Dimension baseDim = baseChild.getPreferredSize(g);
		if(hasSuperScript()) {
			ViewIF subChild = getChild(getSuperScriptIndex());
			Dimension subDim = subChild.getPreferredSize(g);
			subChild.paint(g, xPos + baseDim.width, yPos, subDim.width, subDim.height);
			baseChild.paint(g, xPos, yPos + subDim.height/2, baseDim.width, baseDim.height);
		} else {
			baseChild.paint(g, xPos, yPos, baseDim.width, baseDim.height);
		}
		if(hasSubScript()) {
			ViewIF subChild = getChild(getSubScriptIndex());
			Dimension subDim = subChild.getPreferredSize(g);
			int innerH = height - m_margin.top - m_margin.bottom;
			subChild.paint(g, xPos + baseDim.width, yPos + innerH - subDim.height, subDim.width, subDim.height);
		}
    g.setColor(oldColor);
    g.setFont(oldFont);
	}
}
