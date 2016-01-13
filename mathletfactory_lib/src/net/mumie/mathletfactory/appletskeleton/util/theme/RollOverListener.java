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

package net.mumie.mathletfactory.appletskeleton.util.theme;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;

public class RollOverListener implements MouseListener {

	private static JComponent m_mouseOverComponent;
	
	public boolean isMouseOver(JComponent c) {
		return m_mouseOverComponent == c && c != null && c.isVisible();
	}
	
	public static boolean hasListener(JComponent c) {
  	MouseListener[] listeners = c.getMouseListeners();
  	for(int i = 0; i < listeners.length; i++) {
  		if(listeners[i] instanceof RollOverListener)
  			return true;
  	}
  	return false;
  }
  
	public void mouseExited(MouseEvent e) {
		if(m_mouseOverComponent != null) {
			m_mouseOverComponent = null;
			e.getComponent().repaint();
		}
	}

	public void mouseEntered(MouseEvent e) {
		m_mouseOverComponent = (JComponent) e.getComponent();
		if(m_mouseOverComponent.isEnabled() == false)
			m_mouseOverComponent = null;
		else
			e.getComponent().repaint();
	}

	public void mouseReleased(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {}

	public void mouseClicked(MouseEvent e) {}
}
