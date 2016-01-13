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

import java.awt.Component;

import javax.swing.JComponent;

import net.mumie.mathletfactory.appletskeleton.util.ControlPanel;
import net.mumie.mathletfactory.mmobject.MMObjectIF;

/**
 * This class represents a text card for symbolic representations by using an instance
 * of {@link ControlPanel}. The ControlPanel's context methods can be easily reached by 
 * subclassing this class. Sub classes may use the predefined base methods to manage
 * programming code into sections:
 * 
 * @see BasicCard#createObjects()
 * @see BasicCard#addObjects()
 * @see BasicCard#defineDependencies()
 * @see BasicCard#reset()
 * @see BasicCard#selectElement(String)
 * @see BasicCard#collectAnswers()
 * @see BasicCard#destroy()
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public class TextCard extends BasicCard {

	private ControlPanel m_controlPanel;
	
	public TextCard() {
		m_controlPanel = new ControlPanel();
	}
	
	public JComponent getContent() {
		return m_controlPanel;
	}
	
	public ControlPanel getControlPanel() {
		return m_controlPanel;
	}
	
	protected void addControl(Component c) {
		m_controlPanel.add(c);
	}
	
	protected void addControl(MMObjectIF obj) {
		m_controlPanel.add(obj.getAsContainerContent());
	}
	
	protected void addControl(MMObjectIF obj, int transformType) {
		m_controlPanel.add(obj.getAsContainerContent(transformType));
	}
}
