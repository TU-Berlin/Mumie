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
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.html.HTML;

import net.mumie.mathletfactory.action.message.TodoException;

public class HTMLView extends AbstractView {

	private class DefaultComponent extends JComponent {
		public Font getFont() {
			return HTMLView.this.getFont();
		}
		
		public Color getForeground() {
			return HTMLView.this.getForeground();
		}
	}
	
	private View m_view;
	private JComponent m_comp;
	private String m_text;
	
	public HTMLView(String text) {
		this(text, null);
	}
	
	public HTMLView(String text, JComponent c) {
		m_text = text;
		if(c == null)
			c = new DefaultComponent();
		m_comp = c;
	}
	
	public void updateView() {
		if(m_view != null)
			createView(true);
	}
	
	private void createView(boolean force) {
		if(force || m_view == null)
			m_view = BasicHTML.createHTMLView(m_comp, m_text);
	}
	
	public ViewIF copy() {
		throw new TodoException();
	}

	public Dimension getPreferredSize(Graphics context) {
		createView(false);
    Dimension dim = new Dimension((int) m_view.getPreferredSpan(View.X_AXIS), (int) m_view.getPreferredSpan(View.Y_AXIS));
    Insets insets = getInsets();
    // workaround: enlarge width for plain font styles
    int textLength = m_text.toLowerCase().indexOf("<html>") > -1 ? m_text.length() - 6 : m_text.length();
    dim.width += Math.ceil(getFont().getStyle() == Font.PLAIN ? textLength * 0.1 : 0);
    dim.width += insets.left + insets.right;
    dim.height += insets.top + insets.bottom;
		return dim;
	}

	public void paint(Graphics g, int x, int y, int width, int height) {
		createView(false);
    Insets insets = getInsets();
    Rectangle rect = new Rectangle(getPreferredSize(g));
		int innerW = rect.width + insets.left + insets.right;
		rect.x = x + (width - innerW) / 2 + insets.left + 1;// "+1" is mainly for single characters in HTML
		rect.y = y + insets.top;
		m_view.paint(g, rect);
	}
	
	public double getBaseline(Graphics context) {
		createView(false);
		Rectangle rect = new Rectangle(getPreferredSize(context));
		return getBaseline(m_view.getView(0), rect);
	}
	
   private int getBaseline(View view, Shape bounds) {
     if (view.getViewCount() == 0) {
         return -1;
     }
     AttributeSet attributes = view.getElement().getAttributes();
     Object name = null;
     if (attributes != null) {
         name = attributes.getAttribute(StyleConstants.NameAttribute);
     }
     int index = 0;
     if (name == HTML.Tag.HTML && view.getViewCount() > 1) {
         // For html on widgets the header is not visible, skip it.
         index++;
     }
     bounds = view.getChildAllocation(index, bounds);
     if (bounds == null) {
         return -1;
     }
     View child = view.getView(index);
     if (view instanceof javax.swing.text.ParagraphView) {
         Rectangle rect;
         if (bounds instanceof Rectangle) {
             rect = (Rectangle)bounds;
         }
         else {
             rect = bounds.getBounds();
         }
         return rect.y + (int)(rect.height *
                               child.getAlignment(View.Y_AXIS));
     }
     return getBaseline(child, bounds);
   }
}
