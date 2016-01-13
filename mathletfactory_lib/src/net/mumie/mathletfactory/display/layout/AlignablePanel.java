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

package net.mumie.mathletfactory.display.layout;

import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;


public class AlignablePanel extends JPanel implements Alignable {

	private AlignerLayout m_layout;
	
	public AlignablePanel() {
		this(new AlignerLayout());
	}
	
	public AlignablePanel(int hgap) {
		this(new AlignerLayout(hgap));
	}
	
	public AlignablePanel(LayoutManager layout) {
		super(null);
		setLayout(layout);
    // baseline must be adjusted to new component's size
    addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        revalidate();
      }
    });
	}
	
	public void setHorizontalAlignment(int halign) {
		m_layout.setHorizontalAlignment(halign);
		revalidate();
	}

	public void setVerticalAlignment(int valign) {
		m_layout.setVerticalAlignment(valign);
		revalidate();
	}
	
  public double getBaseline() {
		if(m_layout == null)
			return 0;
		return m_layout.getBaseline();
  }
	
	public Dimension getPreferredSize() {
		if(m_layout == null)
			return new Dimension(0, 0);
		return m_layout.preferredLayoutSize(this);
	}
	
	public final void setLayout(LayoutManager mgr) {
		if(mgr == null)
			mgr = new AlignerLayout();
		if((mgr instanceof AlignerLayout) == false)
			throw new IllegalArgumentException("Layout manager must be an AlignerLayout!");
		m_layout = (AlignerLayout) mgr;
		super.setLayout(mgr);
	}
}
