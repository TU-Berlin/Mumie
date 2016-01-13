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

package net.mumie.mathletfactory.util.exercise;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import net.mumie.mathletfactory.display.util.TextPanel;

public class SelectionPanel extends JPanel {
		
		public final static String EMPTY_CARD = "empty-card";
		public final static String CONTENT_CARD = "content-card";
		public final static String DISABLED_CARD = "disabled-card";
		
		public final static String DEFAULT_EMPTY_MESSAGE = " ";
		public final static String DEFAULT_DISABLED_MESSAGE = " ";
		
		private CardLayout m_cardLayout = new CardLayout();
		private JPanel m_contentPane, m_emptyPane, m_disabledPane; 
		private boolean m_isEmpty = true;
		private TextPanel m_emptyLabel, m_disabledLabel;
		private int m_width, m_height;
		
		public SelectionPanel() {
			this(-1, -1);
		}
		
		public SelectionPanel(int width, int height) {
			setLayout(m_cardLayout);
			setBorder(BorderFactory.createEtchedBorder());
			m_emptyLabel = new TextPanel(DEFAULT_EMPTY_MESSAGE);
			m_disabledLabel = new TextPanel(DEFAULT_DISABLED_MESSAGE);
			m_contentPane = new JPanel();
			m_emptyPane = new JPanel(new BorderLayout());
			m_emptyPane.add(m_emptyLabel, BorderLayout.CENTER);
			m_disabledPane = new JPanel();
			m_disabledPane.add(m_disabledLabel);
			super.addImpl(m_emptyPane, EMPTY_CARD, 0);
			super.addImpl(m_contentPane, CONTENT_CARD, 1);
			super.addImpl(m_disabledPane, DISABLED_CARD, 2);
			
			m_width = width;
			m_height = height;
		}
		
		public void setEmptyMessage(String message) {
			m_emptyLabel.setText(message);
		}
		
		public void setDisabledMessage(String message) {
			m_disabledLabel.setText(message);
		}
		
		public void showEmptyPane() {
			m_cardLayout.show(this, EMPTY_CARD);
		}

		public void showContentPane() {
			m_cardLayout.show(this, CONTENT_CARD);
		}

		public void showDisabledPane() {
			m_cardLayout.show(this, DISABLED_CARD);
		}

		public void fixSize(int width, int height) {
			m_width = width;
			m_height = height;
			revalidate();
		}
		
		public Dimension getPreferredSize() {
//			if(isVisible())
			if(m_width > 0 && m_height > 0)
				return new Dimension(m_width, m_height);
			else
				return super.getPreferredSize();//new Dimension(0, 0);//
		}
		
		protected void addImpl(Component comp, Object constraints, int index) {
			m_contentPane.add(comp, constraints, index);
			if(m_isEmpty) {
				m_cardLayout.show(this, CONTENT_CARD);
				m_isEmpty = false;
			}
			validate();
		}
		
		public void removeAll() {
			m_contentPane.removeAll();
			m_isEmpty = true;
			validate();
			m_cardLayout.show(this, EMPTY_CARD);
		}
		
		public void remove(int index) {
			m_contentPane.remove(index);
			if(m_contentPane.getComponentCount() == 0) {
				m_isEmpty = true;
				m_cardLayout.show(this, EMPTY_CARD);
			}
			validate();
		}
		
		public void remove(Component c) {
			m_contentPane.remove(c);
			if(m_contentPane.getComponentCount() == 0) {
				m_isEmpty = true;
				m_cardLayout.show(this, EMPTY_CARD);
			}
			validate();
		}
		
		public void setEnabled(boolean enable) {
			super.setEnabled(enable);
			if(enable) {
				if(m_contentPane.getComponentCount() == 0)
					m_cardLayout.show(this, EMPTY_CARD);
				else
					m_cardLayout.show(this, CONTENT_CARD);
			}
			else
				m_cardLayout.show(this, DISABLED_CARD);
		}
		
		public JPanel getContentPane() {
			return m_contentPane;
		}
	}