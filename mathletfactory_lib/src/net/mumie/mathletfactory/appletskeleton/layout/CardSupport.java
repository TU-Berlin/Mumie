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

package net.mumie.mathletfactory.appletskeleton.layout;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class CardSupport {

	private Vector m_cards = new Vector();

	private JComponent m_contentPane;
	private JPanel m_layoutPanel;
	private CardLayout m_layout;

	public CardSupport(BasicCard card) {
		m_layout = createLayout();
		m_layoutPanel = new JPanel(m_layout);
		m_contentPane = new JScrollPane(m_layoutPanel); 
		addSubCard(card);// add top level content as first sub card
	}
	
	public JComponent getContent() {
		return m_contentPane;
	}
	
	public void addSubCard(BasicCard card) {
		if(card.getContent() == null)
			return;
		m_cards.add(card);
		m_layout.addLayoutComponent(card.getContent(), Integer.toHexString(card.hashCode()));
	}
	
	public void showSubCard(int cardIndex) {
		showSubCard(getSubCard(cardIndex));
	}
	
	public void showSubCard(BasicCard card) {
		if(card == null)
			card = getSubCard(0); // return top level content
		m_layout.show(m_layoutPanel, Integer.toHexString(card.hashCode()));
	}
	
	public BasicCard getSubCard(int index) {
		return (BasicCard) m_cards.get(index);
	}
	
	public int getSubCardsCount() {
		return m_cards.size();
	}
	
	/*
	 * Original method uses biggest size of all cards for own size -> now 
	 * it uses the current visible component's size
	 */
	protected CardLayout createLayout() {
		return new CardLayout() {
			public Dimension preferredLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {
          for (int i = 0 ; i < parent.getComponentCount() ; i++) {
            Component comp = parent.getComponent(i);
            if(comp.isVisible()) {
              Insets insets = parent.getInsets();
              Dimension d = comp.getPreferredSize();
              return new Dimension(insets.left + insets.right + d.width + getHgap()*2,
                  insets.top + insets.bottom + d.height + getVgap()*2);
            }
          }
          return super.preferredLayoutSize(parent);
        }
			}
		};
	}
}
