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
import net.mumie.mathletfactory.appletskeleton.util.dialog.DialogAction;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Node;

public class DialogActionDefinition {

	private final int m_actionType;
	private final String m_actionName;
	private final boolean m_hideDialog;
	
	DialogActionDefinition(Node xmlNode) {
		String actionTypeString = XMLUtils.getAttribute(xmlNode, "type");
		if(actionTypeString.equalsIgnoreCase("answer_yes"))
			m_actionType = DialogAction.ANSWER_YES_ACTION;
		else if(actionTypeString.equalsIgnoreCase("answer_no"))
			m_actionType = DialogAction.ANSWER_NO_ACTION;
		else if(actionTypeString.equalsIgnoreCase("approve"))
			m_actionType = DialogAction.APPROVE_ACTION;
		else if(actionTypeString.equalsIgnoreCase("cancel"))
			m_actionType = DialogAction.CANCEL_ACTION;
		else if(actionTypeString.equalsIgnoreCase("close"))
			m_actionType = DialogAction.CLOSE_ACTION;
		else if(actionTypeString.equalsIgnoreCase("ignore"))
			m_actionType = DialogAction.IGNORE_ACTION;
		else if(actionTypeString.equalsIgnoreCase("retry"))
			m_actionType = DialogAction.RETRY_ACTION;
		else if(actionTypeString.equalsIgnoreCase("apply"))
			m_actionType = DialogAction.APPLY_ACTION;
		else if(actionTypeString.equalsIgnoreCase("none"))
			m_actionType = DialogAction.USER_DEFINED_ACTION;
		else
			throw new XMLParsingException("Not a valid dialog action node ! Missing attribute: \"type\"");
		
		m_actionName = XMLUtils.getAttribute(xmlNode, "name"); // may be null !
		
		String hideDialogString = XMLUtils.getAttribute(xmlNode, "hide"); // may be null !
		if(hideDialogString == null)
			m_hideDialog = true;
		else
			m_hideDialog = Boolean.valueOf(hideDialogString).booleanValue();
	}
	
	public int getActionType() {
		return m_actionType;
	}
	
	public String getActionName() {
		return m_actionName;
	}
	
	public boolean hideDialog() {
		return m_hideDialog;
	}
}
