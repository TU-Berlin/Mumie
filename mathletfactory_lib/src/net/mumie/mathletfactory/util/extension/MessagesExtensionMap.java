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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import net.mumie.mathletfactory.action.message.TodoException;
import net.mumie.mathletfactory.action.message.XMLParsingException;
import net.mumie.mathletfactory.util.property.DefaultPropertyMap;
import net.mumie.mathletfactory.util.property.PropertyMapIF;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MessagesExtensionMap {

	class Message {
		
		private String m_language;
		private String m_key;
		private String m_value;
		
		Message(Message parent, Node xmlNode) {
			if(parent != null) {
				this.m_language = parent.m_language;
				this.m_key = parent.m_key;
				this.m_value = parent.m_value;
			}
			String language = XMLUtils.getAttribute(xmlNode, "language");
			if(language != null)
				m_language = language;
			String key = XMLUtils.getAttribute(xmlNode, "key");
			if(key != null)
				m_key = key;
			String value = XMLUtils.getAttribute(xmlNode, "value");
			if(value != null)
				m_value = value;
		}
		
		boolean isValid() {
			return m_language != null && m_key != null && m_value != null;
		}
	}
	
	private Map m_messages = new HashMap();
	
	private Extension m_masterExtension;
	
	public MessagesExtensionMap() {
		this((Extension) null);
	}
	
	public MessagesExtensionMap(Extension e) {
		m_masterExtension = e;
	}
	
	public MessagesExtensionMap(Node[] xmlNode) {
		addMessages(xmlNode);
	}
	
	public void addMessages(Node[] xmlNode) {
		for(int i = 0; i < xmlNode.length; i++) {
			Node n = xmlNode[i];
			if(n.getNodeType() == Node.TEXT_NODE || n.getNodeType() == Node.COMMENT_NODE)
				continue;
			addMessagesFromSubNode(n);
		}
	}
	
	public void addMessages(Node xmlNode) {
		addMessagesFromSubNode(xmlNode);
	}
	
	private void addMessagesFromSubNode(Node xmlNode) {
		NodeList children = xmlNode.getChildNodes();
		for(int i = 0; i < children.getLength(); i++) {
			Node n = children.item(i);
			if(n.getNodeType() == Node.TEXT_NODE || n.getNodeType() == Node.COMMENT_NODE)
				continue;
			addMessages(n, null);
		}
	}
	
	private void addMessages(Node xmlNode, Message parent) {
		if(xmlNode.getNodeName().equals("mf:messages")) { // a list
			Message listParent = new Message(parent, xmlNode);
			NodeList children = xmlNode.getChildNodes();
			for(int i = 0; i < children.getLength(); i++) {
				Node n = children.item(i);
				if(n.getNodeType() == Node.TEXT_NODE || n.getNodeType() == Node.COMMENT_NODE)
					continue;
				addMessages(n, listParent);
			}
		} else if(xmlNode.getNodeName().equals("mf:message-file")) { // a message file
			addMessageFile(xmlNode);
		} else if(xmlNode.getNodeName().equals("mf:message")) { // a single message
			Message m = new Message(parent, xmlNode);
			if(m.isValid()) {
				PropertyMapIF propMap = getMessages(m.m_language);
				propMap.setProperty(m.m_key, m.m_value);
			}
			else
				throw new XMLParsingException("Not a valid message !", xmlNode);
		} else
			throw new XMLParsingException("Only messages are allowed in this section but is: " + xmlNode.getNodeName());
	}
	
	private void addMessageFile(Node xmlNode) {
		String language = XMLUtils.getAttribute(xmlNode, "language");
		if(language == null)
			throw new XMLParsingException("Not a valid <message-file> node ! The \"language\" attribute must be set !", xmlNode);
		String file = XMLUtils.getAttribute(xmlNode, "file");
		String url = XMLUtils.getAttribute(xmlNode, "url");
		String mustExist = XMLUtils.getAttribute(xmlNode, "mustExist");
		SortedProperties props = new SortedProperties();
		try {
			InputStream in = null;
			if(file != null) {
				// use extension class for searching (if possible)
				if(m_masterExtension != null)
					in = m_masterExtension.getResourceAsStream(file);
				else
					in = getClass().getResourceAsStream(file);
				// ignore that file cannot be found if attribute "mustExist" is set to "false"
				if(in == null && mustExist != null && mustExist.equalsIgnoreCase("false"))
					return;
				if(in == null)
					throw new FileNotFoundException("Message file not found: " + file);
				props.loadSortedProperties(in);
			} else if(url != null) {
				throw new TodoException("Attribute \"url\" not yet implemented !");
			} else
				throw new XMLParsingException("Not a valid <message-file> node ! Either the \"file\" or \"url\" attribute must be set !", xmlNode);
		} catch(IOException e) {
			throw new XMLParsingException("Could not load message file !", e);
		}
		DefaultPropertyMap propMap = (DefaultPropertyMap) getMessages(language);
		propMap.copyPropertiesFrom(props.getSortedMap());
	}
	
	public PropertyMapIF getMessages(Locale locale) {
		return getMessages(locale.getLanguage());
	}
	
	public PropertyMapIF getMessages(String language) {
		DefaultPropertyMap propMap = (DefaultPropertyMap) m_messages.get(language);
		if(propMap == null) {
			propMap = new DefaultPropertyMap(true);
			m_messages.put(language, propMap);
		}
		return propMap;
	}
	
	public Map getAllMessages() {
		return m_messages;
	}
	
	public void clear() {
		m_messages.clear();
	}
	
	public void copyFrom(MessagesExtensionMap me) {
		Set keys = me.m_messages.keySet();// keys are languages
		for(Iterator i = keys.iterator(); i.hasNext(); ) {
			String language = (String) i.next();
			this.getMessages(language).copyPropertiesFrom(me.getMessages(language));
		}
	}
	
	class SortedProperties extends Properties {
		
		LinkedHashMap m_sortedMap = new LinkedHashMap();
		
		public void loadSortedProperties(InputStream inStream) throws IOException {
      BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "8859_1"));
    	while (true) {
        // read next line
        String line = reader.readLine();
        if (line == null) // no more lines to read
            break;
        // invoke super class for parsing
        super.load(new ByteArrayInputStream(line.getBytes()));
        // retrieve the only property
        Enumeration keys = super.keys();
        if(keys.hasMoreElements()) { // no property may exist (e.g. blank line)
        	String key = (String) keys.nextElement();
        	getSortedMap().put(key, super.getProperty(key));
        	super.clear();
        }
    	}
		}
		
		Map getSortedMap() {
			return m_sortedMap;
		}
	}
}
