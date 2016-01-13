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
import java.awt.Graphics;

import javax.swing.Action;
import javax.swing.JButton;

import net.mumie.mathletfactory.display.layout.SimpleInsets;

public class StyledTextButton extends JButton {

	private String m_text;
	private ViewIF m_view;
	
	public StyledTextButton() {
		super();
	}
	
	public StyledTextButton(String text) {
		super(text);
	}
	
	public StyledTextButton(Action a) {
		super(a);
	}
	
	public Dimension getPreferredSize() {
		updateView();
		if(m_text == null || m_view == null)
			return super.getPreferredSize();
		return new SimpleInsets(getInsets()).getOutsideDim(m_view.getPreferredSize(getGraphics()));
	}
	
	public void setText(String text) {
		m_text = text;
		m_view = null;
		super.setText(null);
	}
	
	public String getStyledText() {
		return m_text;
	}
	
	private void updateView() {
		// nothing set
		if(m_text == null)
			return;
		// first view
		if(m_view == null)
			m_view = StyledTextView.createStyledTextView(m_text);
		m_view.setAttribute(ViewIF.FONT_ATTRIBUTE, getFont());
    if(isEnabled())
      m_view.setAttribute(ViewIF.FOREGROUND_ATTRIBUTE, getForeground());
    else
      m_view.setAttribute(ViewIF.FOREGROUND_ATTRIBUTE, Color.GRAY);
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		updateView();
		if(m_text == null || m_view == null)
			return;
		SimpleInsets insets = new SimpleInsets(getInsets());
		Dimension dim = insets.getInsideDim(getSize());
		m_view.setSize(dim);
  	if(getModel().isArmed() && getModel().isPressed())
  		g.translate(1, 1);// translate graphics context if button is being pressed
		m_view.paint(g, insets.left, insets.top, dim.width, dim.height);
	}
}
