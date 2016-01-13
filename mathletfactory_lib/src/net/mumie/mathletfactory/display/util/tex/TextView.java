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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Vector;

import net.mumie.mathletfactory.display.util.AbstractView;
import net.mumie.mathletfactory.display.util.ViewIF;
import net.mumie.mathletfactory.util.logging.LogCategory;
import net.mumie.mathletfactory.util.logging.MumieLogger;

public class TextView extends AbstractView {

	private final static MumieLogger LOGGER = MumieLogger.getLogger(TextView.class);
	private final static LogCategory DRAW_BASELINE = LOGGER.getCategory("ui.draw-baseline");

	private String m_text;
	
	public TextView(String text) {
		m_text = text;
	}
	
	public ViewIF copy() {
		return new TextView(m_text);
	}
	
	public String getText() {
		return m_text;
	}
	
	public double getBaseline(Graphics context) {
		return context.getFontMetrics(getFont()).getAscent();
	}
	
	public Dimension getPreferredSize(Graphics context) {
		Dimension result = context.getFontMetrics(getFont()).getStringBounds(m_text, context).getBounds().getSize();
		result.width += getInsets().left + getInsets().right;
		result.height += getInsets().top + getInsets().bottom;
		return result;
	}

	public void paint(Graphics g, int x, int y, int width, int height) {
    Color oldColor = g.getColor();
    Font oldFont = g.getFont();
		g.setFont(getFont());
		g.setColor(getForeground());
		super.paint(g, x, y, width, height);
		Dimension dim = getPreferredSize(g);
		int innerW = width - getInsets().left - getInsets().right;
		int innerH = height - getInsets().top - getInsets().bottom;
		int xPos = x + getInsets().left + (innerW - dim.width) / 2;
		int yPos = y + getInsets().top + (innerH - dim.height) / 2;
		int baseline = yPos + g.getFontMetrics(getFont()).getAscent();
		g.drawString(m_text, xPos, baseline);
		if(LOGGER.isActiveCategory(DRAW_BASELINE))
			g.drawLine(xPos, baseline, dim.width, baseline);
    g.setColor(oldColor);
    g.setFont(oldFont);
	}
	
	public String[] getSubStrings(Graphics context, int firstSubWidth, int furtherSubWidth) {
		FontMetrics metrics = context.getFontMetrics(context.getFont());
		Vector result = new Vector();
		int maxWidth = firstSubWidth;
		int blankIndex, lastBegin = 0, lastEnd = 0;
		String lastSubstring = null;
		while( (blankIndex = m_text.indexOf(' ', lastEnd)) != -1) {
			String subString = m_text.substring(lastBegin, blankIndex).trim();
			Dimension size = metrics.getStringBounds(subString, context).getBounds().getSize();
			if(size.width > maxWidth) {
//				System.out.println("Too large: " + subString);
				if(lastSubstring != null) {
					result.add(lastSubstring);// append sub string from last loop (if available)
//					System.out.println("Adding1 " + lastSubstring);
					subString = m_text.substring(lastBegin + lastSubstring.length(), blankIndex).trim();
					lastBegin = lastBegin + lastSubstring.length() + 1;
				}
			}
			lastSubstring = subString;
			lastEnd = Math.min(blankIndex + 1, m_text.length()); // move behind blank character
			maxWidth = furtherSubWidth;
		}
		if(lastSubstring != null) {
			String lastWord = m_text.substring(lastEnd);
			String fullText = lastSubstring + " " + lastWord;
			Dimension size = metrics.getStringBounds(fullText, context).getBounds().getSize();
			if(size.width > maxWidth) {
//				System.out.println("Too large: " + fullText);
//				System.out.println("Adding2 " + lastSubstring);
//				System.out.println("Adding2 " + lastWord);
				result.add(lastSubstring);
				result.add(lastWord);
			} else {
				result.add(fullText);// append sub string from last loop (if available)
//				System.out.println("Adding3 " + fullText);
			}
		} else {
			result.add(m_text.substring(lastEnd)); // add last word
//			System.out.println("Adding4 " + m_text.substring(lastEnd));
		}
		String[] r = new String[result.size()];
//		for(int i = 0; i < result.size(); i++)
//			System.out.println(result.get(i));
		return (String[]) result.toArray(r);
	}
}
