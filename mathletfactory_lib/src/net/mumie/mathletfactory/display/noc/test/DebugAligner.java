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

package net.mumie.mathletfactory.display.noc.test;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import net.mumie.mathletfactory.display.layout.AlignablePanel;


public class DebugAligner extends AlignablePanel {

	private double m_ascent = 25;
	private double m_descent = 25;
	
	public DebugAligner() {
		super();
		addMouseMotionListener(new MouseMotionAdapter() {
			int minBorder = 10;
			Cursor DEFAULT_CURSOR = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
			Cursor d_CURSOR = Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
			public void mouseMoved(MouseEvent e) {
				int y = e.getPoint().y;
				int baseline = (int) getBaseline();
				if(Math.abs(baseline - y) < minBorder)// || y <= minBorder || y >= getHeight() - minBorder)
					setCursor(d_CURSOR);
				else
					setCursor(DEFAULT_CURSOR);
			}
			
	    public void mouseDragged(MouseEvent e) {
				int y = e.getPoint().y;
				int baseline = (int) getBaseline();
				if(Math.abs(baseline - y) < minBorder) {
		    	if(e.getPoint().y > minBorder)
		    		m_ascent = e.getPoint().y;
				}
	    	revalidate();
	    	repaint();
	    	getParent().validate();
	    	getParent().repaint();
	    }
		});
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		// calculate baseline
		double baseline = getBaseline();
		// draw ascent bounds
		g.setColor(Color.YELLOW);
		g.fillRect(0, (int) (baseline - getAscent()), getWidth(), (int) getAscent());
		// draw descent bounds
		g.setColor(Color.CYAN);
		g.fillRect(0, (int) baseline, getWidth(), (int) getDescent());
		// draw baseline
		g.setColor(Color.BLACK);
		g.drawLine(0, (int) baseline, getWidth(), (int) baseline);
		// draw border
		g.drawRect(0, 0, getWidth()-1, getHeight()-1);
	}
	
	public double getAscent() {
		return m_ascent;
	}
	
	public double getBaseline() {
		return getAscent();
	}
	
	public double getDescent() {
		return m_descent;
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(50, (int) (m_ascent + m_descent));
	}
}
