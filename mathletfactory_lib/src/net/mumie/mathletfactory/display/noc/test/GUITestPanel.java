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

package net.mumie.mathletfactory.display.noc.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import net.mumie.mathletfactory.display.noc.MMPanel;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.util.MMString;
import net.mumie.mathletfactory.util.xml.ExerciseObjectIF;

public class GUITestPanel extends JPanel {

	private MMObjectIF m_mmobject;
	private MMPanel m_drawable;
	
	private Border m_focusedBorder = new LineBorder(Color.BLUE);
	private Border m_unfocusedBorder = new EmptyBorder(1, 1, 1, 1);
	private boolean m_selected = false;
	
	private boolean m_isEditable = true;
	private boolean m_isEdited = true;
	private boolean m_isSelectable = false;
	private boolean m_isPreferredWidth = true;
	private boolean m_isPreferredHeight = true;
	private Color m_objectColor, m_borderColor;
	private boolean m_isFontRenderedWithObjectColor = true;
	private GUITestControlPanel m_controller;
	private boolean m_hasErrors = false;
	
//	private String 
	
	GUITestPanel(MMObjectIF mmobject) {
		super();
		JComponent c = null;
		try {
			c = mmobject.getAsContainerContent();
		} catch(Throwable error) {
			System.err.println("Error caught in MM-Object " + mmobject.getClass().getName() + "...");
			c = createErrorComp(error);
		}
		initialize(mmobject, c);
	}

	GUITestPanel(MMObjectIF mmobject, int trafoType) {
		super();
		JComponent c = null;
		try {
			c = mmobject.getAsContainerContent(trafoType);
		} catch(Throwable error) {
			System.err.println("Error caught in MM-Object " + mmobject.getClass().getName() + "...");
			c = createErrorComp(error);
		}
		initialize(mmobject, c);
	}
	
	private JComponent createErrorComp(Throwable error) {
		JComponent c = new MMString(error.toString()).getAsContainerContent();
//		c.setBackground(Color.RED);
		c.setForeground(Color.RED);
		error.printStackTrace();
		m_hasErrors = true;
		return c;
	}
	
	private void initialize(MMObjectIF mmobject, JComponent drawable) {
		m_mmobject = mmobject;
		m_drawable = (MMPanel) drawable;
		
		loadDefaults();
		
//		setLayout(new BorderLayout());
		setFocusable(true);
		m_focusedBorder = new CompoundBorder(new TitledBorder(getShortClassName(mmobject.getClass())), new LineBorder(Color.BLUE));
		m_unfocusedBorder = new CompoundBorder(new TitledBorder(getShortClassName(mmobject.getClass())), new EmptyBorder(1, 1, 1, 1));
		setBorder(m_unfocusedBorder);
		add(m_drawable, BorderLayout.NORTH);
		
		addFocusListener(new MyFocusListener());
	}
	
	public boolean hasErrors() {
		return m_hasErrors;
	}
	
	private void loadDefaults() {
		m_objectColor = m_mmobject.getDisplayProperties().getObjectColor();
		m_borderColor = m_mmobject.getDisplayProperties().getBorderColor();
		
		m_mmobject.setEditable(m_isEditable);
		if(m_mmobject instanceof ExerciseObjectIF) {
			ExerciseObjectIF exObj = (ExerciseObjectIF) m_mmobject;
			exObj.setEdited(m_isEdited);
		}
		m_drawable.setSelectable(m_isSelectable);
		m_mmobject.getDisplayProperties().setObjectColor(m_objectColor);
		m_mmobject.getDisplayProperties().setFontRenderedWithObjectColor(m_isFontRenderedWithObjectColor);
		m_mmobject.getDisplayProperties().setBorderColor(m_borderColor);
		m_mmobject.render();
	}
	
	private String getShortClassName(Class aClass) {
    try {
      String className = aClass.getName();
      return className.substring(className.lastIndexOf('.') + 1);    	
    } catch(IndexOutOfBoundsException e) {// class is in default package
    	return aClass.getName();
    }
	}
	
	MMObjectIF getMMObject() {
		return m_mmobject;
	}
	
	ExerciseObjectIF getExerciseObject() {
		if(getMMObject() instanceof ExerciseObjectIF)
			return (ExerciseObjectIF) getMMObject();
		else
			return null;
	}
	
	boolean isPreferredWidth() {
		return m_isPreferredWidth;
	}
	
	void setPreferredWidth(boolean flag) {
		m_isPreferredWidth = flag;
		setPreferredSize();
	}
	
	private void setPreferredSize() {
		remove(m_drawable);
		if(m_isPreferredWidth && m_isPreferredHeight) {
			setLayout(new FlowLayout());
			add(m_drawable);
		} else if(m_isPreferredWidth) {
			setLayout(new BorderLayout());
			add(m_drawable, BorderLayout.WEST);
		} else if(m_isPreferredHeight) {
			setLayout(new BorderLayout());
			add(m_drawable, BorderLayout.NORTH);
		} else {
			setLayout(new BorderLayout());
			add(m_drawable, BorderLayout.CENTER);
		}
	}
	
	boolean isPreferredHeight() {
		return m_isPreferredHeight;
	}
	
	void setPreferredHeight(boolean flag) {
		m_isPreferredHeight = flag;
		setPreferredSize();
	}
	
	MMPanel getDrawable() {
		return m_drawable;
	}
	
	void setSelected(boolean flag) {
		m_selected = flag;
		if(flag)
			setBorder(m_focusedBorder);
		else
			setBorder(m_unfocusedBorder);
	}
	
	void setController(GUITestControlPanel controlPanel) {
		m_controller = controlPanel;
	}
		
	class MyFocusListener extends FocusAdapter {
		public void focusGained(FocusEvent e) {
			setBorder(m_focusedBorder);
			m_controller.select(GUITestPanel.this);
		}
		
		public void focusLost(FocusEvent e) {
			// focus lost to children?
			if(e.getOppositeComponent() != null && SwingUtilities.isDescendingFrom(e.getOppositeComponent(), GUITestPanel.this))
				return;
			if(m_selected == false)
				setBorder(m_unfocusedBorder);
		}
	}
}
