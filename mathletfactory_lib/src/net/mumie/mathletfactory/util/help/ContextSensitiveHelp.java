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

package net.mumie.mathletfactory.util.help;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.help.CSH;
import javax.help.HelpSet;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.mumie.mathletfactory.display.MMCanvas;
import net.mumie.mathletfactory.display.noc.MMEditablePanel;
import net.mumie.mathletfactory.display.noc.MMPanel;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;
import net.mumie.mathletfactory.util.property.DefaultPropertyMap;
import net.mumie.mathletfactory.util.property.PropertyMapIF;

public class ContextSensitiveHelp implements CSH.Manager {

//	private final static String CLASS_ID_MAP_FILE = "/resource/help/class_ID_Map.properties";

	final static String INDEX_FLAG = MumieHelpSet.INDEX_ID;

	final static String EDITING_FLAG = "handling.input";

	HelpSet m_helpSet;
	
	private PropertyMapIF m_id2classMap = new DefaultPropertyMap();

	ContextSensitiveHelp(HelpSet hs) {
		this.m_helpSet = hs;
	}
	
	void copyClassMap(PropertyMapIF map) {
		m_id2classMap.copyPropertiesFrom(map);
	}

	public HelpSet getHelpSet(Object comp, AWTEvent e) {
		return m_helpSet;
	}

	public String getHelpIDString(Object comp, AWTEvent e) {
		MouseEvent mouseEvent = null;
		if (e instanceof MouseEvent)
			mouseEvent = (MouseEvent) e;
		else if (e instanceof ActionEvent) {
		} // do nothing
		else {
			System.err.println("No help action defined for " + e.getClass());
			return null;
		}
		if (comp instanceof MMCanvas && mouseEvent != null)
			return getHelpIDFromCanvas((MMCanvas) comp, mouseEvent);
		else if (comp instanceof JTextField) {
			Component component = (Component) comp;
			if (component.getParent() != null && component.getParent().getParent() instanceof MMEditablePanel)
				return EDITING_FLAG;
			else
				return null;
		} else if (comp instanceof MMPanel)
			return getHelpIDFromMaster((MMPanel) comp);
		else
			return getHelpIDFromClass(comp.getClass());// hs.getHomeID().getIDString();
	}

	private String getHelpIDFromClass(Class c) {
		String id = (String) m_id2classMap.getProperty(c.getName());
		if (id == null && c.getSuperclass() != null)
			return getHelpIDFromClass(c.getSuperclass());
		return id;
	}

	private String getHelpIDFromMaster(MMPanel panel) {
		return getHelpIDFromClass(panel.getMaster().getClass());
	}

	private String getHelpIDFromCanvas(MMCanvas canvas, MouseEvent event) {
		event = SwingUtilities.convertMouseEvent((Component) event.getSource(),
				event, canvas.getDrawingBoard());
		for (int i = 0; i < canvas.getObjectCount(); i++) {
			MMCanvasObjectIF obj = canvas.getObject(i);
			if (obj.isAtScreenLocation(event.getX(), event.getY())) {
				String id = getHelpIDFromClass(obj.getClass());
				if(id != null)
					return id;
			}
		}
		return getHelpIDFromClass(canvas.getClass());
	}
}
