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

import java.util.HashMap;
import java.util.Map;

import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Node;

public class DialogExtensionMap {

	private Map m_dialogs = new HashMap();
	
	public DialogExtensionMap() {
		
	}
	
	public void addAll(DialogExtensionMap map) {
		m_dialogs.putAll(map.m_dialogs);
	}
	
	public DialogExtensionMap(Node[] xmlNodes) {
		addDialogDefinitions(xmlNodes);
	}
	
	public void addDialogDefinitions(Node[] xmlNodes) {
		for(int i = 0; i < xmlNodes.length; i++){
			Node[] dialogNodes = XMLUtils.getChildNodes(xmlNodes[i], "mf:dialog");
			for(int j = 0; j < dialogNodes.length; j++) {
				Node n = dialogNodes[j];
				if(n.getNodeType() == Node.TEXT_NODE || n.getNodeType() == Node.COMMENT_NODE)
					continue;
				addDialogDefinition(n);
			}
		}
	}
	
	public void addDialogDefinition(Node xmlNode) {
		addDialogDefinition(new DialogDefinition(xmlNode));
	}
	
	public void addDialogDefinition(DialogDefinition dialog) {
		DialogDefinition old = getDialogDefinition(dialog.getID());
		if(old != null)
			throw new IllegalArgumentException("Duplicate dialog ID: " + dialog.getID());
		m_dialogs.put(new Integer(dialog.getID()), dialog);
	}
	
	public DialogDefinition getDialogDefinition(int id) {
		return (DialogDefinition) m_dialogs.get(new Integer(id));
	}
}