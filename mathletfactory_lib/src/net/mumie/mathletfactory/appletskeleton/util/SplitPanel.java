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

package net.mumie.mathletfactory.appletskeleton.util;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

/**
 * @author Markus Gronau
 * 
 * This class is used to ...
 *  
 */
public class SplitPanel extends JPanel {

	private JSplitPane m_splitPane;
//	private int m_splitOrientation;

	private JPanel m_resizerPanel;
	private JScrollPane m_scrollPane;

	private boolean m_isAutoAdjusting = true;
	private boolean m_isUserAction = true;

	/**
	 *  
	 */
	public SplitPanel(
		final JComponent topComponent,
		final JComponent bottomComponent) {
		m_resizerPanel = new JPanel(new BorderLayout()) {
			public void setBounds(int x, int y, int width, int height) {
					m_isUserAction = false;
					super.setBounds(x, y, width, height);
			}
		};
		m_resizerPanel.add(bottomComponent, BorderLayout.CENTER);
		m_scrollPane = new JScrollPane(m_resizerPanel);
		m_scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		JPanel t = new JPanel(new BorderLayout());
		t.add(topComponent, BorderLayout.CENTER);
		m_splitPane =
			new JSplitPane(JSplitPane.VERTICAL_SPLIT, t, m_scrollPane) {
			public int getDividerLocation() {
				if(m_isAutoAdjusting) {
				  int height = getBottomComponent().getPreferredSize().height;
				  return m_splitPane.getHeight() - m_splitPane.getDividerSize() - height -5;
				}
				return super.getDividerLocation();
			}

			public void setBounds(int x, int y, int width, int height) {
			  m_isUserAction = false;
			  super.setBounds(x, y, width, height);
			}

			public void setDividerLocation(int location) {
				// user interacts with divider, set flag that enables manual divider
				// location setting
//			  System.out.println("user:" + m_isUserAction + ", autoOn:" + m_isAutoAdjusting);
			  if(m_isAutoAdjusting && m_isUserAction) {
			    m_isAutoAdjusting = false;
//			    System.out.println("auto off");
			  }
			  else {// if(m_isAutoAdjusting && !m_isUserAction) {
			    m_isUserAction = true;
			    if(m_scrollPane.getHeight() > m_splitPane.getBottomComponent().getPreferredSize().height) {
			      m_isAutoAdjusting = true;
//			      System.out.println("auto on");
			    }
			  }
				super.setDividerLocation(location);
        m_splitPane.getTopComponent().setSize(m_splitPane.getWidth(), getDividerLocation());
        //System.out.println(getTopComponent().getSize());
        m_splitPane.getTopComponent().repaint();
			}
		};
		m_splitPane.setResizeWeight(1);
		setLayout(new BorderLayout());
		add(m_splitPane, BorderLayout.CENTER);
	}

	public void setTopComponent(Component comp) {
		m_splitPane.setTopComponent(comp);
	}

	public void setBottomComponent(Component comp) {}
	
	public JSplitPane getJSplitPane() {
		return m_splitPane;
	}
	
	public JScrollPane getJScrollPane() {
		return m_scrollPane;
	}
}
