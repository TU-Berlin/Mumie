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

/*
 * Created on 09.12.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.mumie.mathletfactory.display.util;

import java.awt.Dimension;

/**
 * @author gronau
 * @mm.docstatus outstanding
 */
public class TextPanel extends ViewPanel {

  private String m_text = new String();

	/** The preferred height of this TextPanel. */
	private int m_preferredHeight = -1;

	/** The preferred width of this TextPanel. */
	private int m_preferredWidth = -1;

  /**
   *
   */
  public TextPanel() {
    this("");
  }

  public TextPanel(String text) {
    super(StyledTextView.createStyledTextView(text));
  }

  public void setText(String text) {
    if(text == null)
      text = "";
    if(m_text.equals(text))
    	return;
    m_text = text;
    setView(StyledTextView.createStyledTextView(text));
  }

  public String getText() {
    return m_text;
  }

	public Dimension getPreferredSize() {
		Dimension superD = super.getPreferredSize();
		Dimension returnD = new Dimension(superD);
		if(m_preferredWidth != -1)
			returnD.width = m_preferredWidth;
		if(m_preferredHeight != -1)
			returnD.height = m_preferredHeight;
		return returnD;
	}

	/**
	 * Sets the preferred height of this TextPanel.
	 * Setting <code>-1</code> will restore the default (automatic) size.
	 */
	public void setHeight(int height) {
		m_preferredHeight = height;
		revalidate();
	}

	/**
	 * Sets the preferred width of this TextPanel.
	 * Setting <code>-1</code> will restore the default (automatic) size.
	 */
	public void setWidth(int width) {
		m_preferredWidth = width;
		revalidate();
	}
	
	public void setFontStyle(int fontStyle) {
		getView().setAttribute(ViewIF.FONT_STYLE_ATTRIBUTE, new Integer(fontStyle));
		setFont(getView().getFont());
	}
	
	public void setHorizontalTextAlignment(int alignment) {
		getView().setAttribute(ViewIF.HORIZONTAL_ALIGNMENT_ATTRIBUTE, new Integer(alignment));
		repaint();
	}
}
