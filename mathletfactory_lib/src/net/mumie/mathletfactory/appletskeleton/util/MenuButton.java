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

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPopupMenu;

import net.mumie.mathletfactory.appletskeleton.util.theme.StyledButtonUI;

public class MenuButton extends JButton {

	private Image m_arrowImage, m_disabledArrowImage;
	private JPopupMenu m_popupMenu;
	
	public MenuButton(String label, Action[] actions) {
		super(label);
		initialize(actions);
	}
	
	public MenuButton(Action defaultAction, Action[] actions) {
		super(defaultAction);
		initialize(actions);
	}
	
	private void initialize(Action[] actions) {
		m_arrowImage = new ImageIcon(getClass().getResource("/resource/icon/canvas/down.png")).getImage();
		m_disabledArrowImage = GrayFilter.createDisabledImage(m_arrowImage);
		m_popupMenu = new JPopupMenu();
		for(int i = 0; i < actions.length; i++) {
			m_popupMenu.add(actions[i]);
		}
		
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if(!MenuButton.this.isEnabled())
					return;
				if(e.getX() > getWidth() - 15) {
					m_popupMenu.show(MenuButton.this, 0, getHeight());
					e.consume();
				}
			}
		});
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		Image img = isEnabled() ? m_arrowImage : m_disabledArrowImage;
		g.drawImage(img, getWidth() - 12, getHeight() / 2, getBackground(), null);
	}
	
  public void updateUI() {
    setUI(StyledButtonUI.createUI(this));
  }
}
