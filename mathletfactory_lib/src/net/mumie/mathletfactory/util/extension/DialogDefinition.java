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

package net.mumie.mathletfactory.util.extension;

import net.mumie.mathletfactory.action.message.XMLParsingException;
import net.mumie.mathletfactory.appletskeleton.util.dialog.DefaultDialog;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Node;

public class DialogDefinition {

	private final int m_id;
	private final int m_type;
	private final String m_text;
	private final boolean m_isModal;
	private final Class m_class;
	private final String m_title;
	private final DialogActionDefinition[] m_actions;

	DialogDefinition(Node xmlNode) {
		String idString = XMLUtils.getAttribute(xmlNode, "id");
		if(idString == null)
			throw new XMLParsingException("Not a valid dialog node ! Missing attribute: \"id\"");
		m_id = Integer.parseInt(idString);
		
		String typeString = XMLUtils.getAttribute(xmlNode, "type");
		if(typeString == null)
			throw new XMLParsingException("Not a valid dialog node ! Missing attribute: \"type\"");
		m_type = DefaultDialog.parseDialogType(typeString);
		
		m_text = XMLUtils.getAttribute(xmlNode, "text");
		
		String modalityString = XMLUtils.getAttribute(xmlNode, "modal");
		if(modalityString == null)
			m_isModal = true; // default value
		else
			m_isModal = Boolean.valueOf(modalityString).booleanValue();
		
		String classString = XMLUtils.getAttribute(xmlNode, "class");
		if(classString == null)
			m_class = DefaultDialog.class; // default value
		else {
			try {
				m_class = Class.forName(classString);
			} catch (ClassNotFoundException e) {
				throw new XMLParsingException("Not a valid dialog class: \"" + classString + "\"");
			}
		}
		
		m_title = XMLUtils.getAttribute(xmlNode, "title");
		
		Node[] actionNodes = XMLUtils.getChildNodes(xmlNode, "mf:action");
		m_actions = new DialogActionDefinition[actionNodes.length];
		for(int i = 0; i < actionNodes.length; i++) {
			m_actions[i] = new DialogActionDefinition(actionNodes[i]);
		}
	}
	
	public int getType() {
		return m_type;
	}
	
	public int getID() {
		return m_id;
	}
	
	public String getText() {
		return m_text;
	}
	
	public boolean isModal() {
		return m_isModal;
	}
	
	public Class getDialogClass() {
		return m_class;
	}
	
	public String getTitle() {
		return m_title;
	}
	
	public DialogActionDefinition[] getActions() {
		return m_actions;
	}
}