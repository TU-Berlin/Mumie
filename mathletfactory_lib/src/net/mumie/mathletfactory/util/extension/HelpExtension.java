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
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import net.mumie.mathletfactory.util.help.MumieHelp;
import net.mumie.mathletfactory.util.property.DefaultPropertyMap;
import net.mumie.mathletfactory.util.property.Property;
import net.mumie.mathletfactory.util.property.PropertyMapIF;
import net.mumie.mathletfactory.util.property.PropertySheet;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class HelpExtension {

	/** Map holding property maps containing id-url mappings for different languages. */
	private Map m_idMaps = new HashMap();
	
	private PropertyMapIF m_id2classMap = new DefaultPropertyMap();
	
	private MessagesExtensionMap m_messages;
		
//	public HelpExtension() {
//		this((Extension) null);
//	}
	
	public HelpExtension(Extension e) {
		m_messages = new MessagesExtensionMap(e);
	}
  
	public void addProperties(Node[] xmlNodes) {
		for(int i = 0; i < xmlNodes.length; i++) {
			addProperties(xmlNodes[i]);
		}
	}
	
	private void addProperties(Node xmlNode) {
		NodeList children = xmlNode.getChildNodes();
		for(int i = 0; i < children.getLength(); i++) {
			Node n = children.item(i);
			if(n.getNodeType() == Node.TEXT_NODE || n.getNodeType() == Node.COMMENT_NODE)
				continue;
			addPropertiesFromHelpSubnode(n);
		}
	}
	
	private void addPropertiesFromHelpSubnode(Node xmlNode) {
		if(xmlNode.getNodeName().equals("mf:help-id-map")) { // a map with id-url mappings
			String languages = XMLUtils.getAttribute(xmlNode, "languages");
			StringTokenizer tok = new StringTokenizer(languages, ",; ");
			while(tok.hasMoreTokens()) {
				PropertySheet.readPropertyNodes(xmlNode, getId2URLMap(tok.nextToken().trim()));
			}
		} else if(xmlNode.getNodeName().equals("mf:i18n")) { // a messages section
			m_messages.addMessages(xmlNode);
		} else if(xmlNode.getNodeName().equals("mf:help-class-map")) { // a map with id-class mappings
			// entries in XML are different for better readability! Name and value attributes are swapped
			// --> must swap name and value in every property
			// 1. create temp map
			PropertyMapIF class2idMap = new DefaultPropertyMap();
			// 2. read in XML data
			PropertySheet.readPropertyNodes(xmlNode, class2idMap);
			// 3. Swap names and values
			PropertyMapIF id2classMap = swapNamesAndValues(class2idMap);
			// 4. copy swapped properties to global map
			getId2ClassMap().copyPropertiesFrom(id2classMap);
		}
	}
	
	private PropertyMapIF swapNamesAndValues(PropertyMapIF map) {
		PropertyMapIF result = new DefaultPropertyMap();
		Property[] props = map.getProperties();
		for(int i = 0; i < props.length; i++) {
			String name = (String) props[i].getValue();
			String value = props[i].getName();
			result.setProperty(name, value);
		}
		return result;
	}
	
	public void copyFrom(HelpExtension help) {
		// copy id-url mappings for each language
		Set keys = help.m_idMaps.keySet();// keys are languages
		for(Iterator i = keys.iterator(); i.hasNext(); ) {
			String language = (String) i.next();
			this.getId2URLMap(language).copyPropertiesFrom(help.getId2URLMap(language));
		}
		// copy messages
		m_messages.copyFrom(help.getMessages());
		// copy id-class mappings
		m_id2classMap.copyPropertiesFrom(help.getId2ClassMap());
	}
	
	public void propagateValues(MumieHelp help) {
//		help.setIDMap(getId2URLMap(help.getLocale()));
	}
	
	public PropertyMapIF getId2URLMap(Locale locale) {
		return getId2URLMap(locale.getLanguage());
	}

	public PropertyMapIF getId2URLMap(String language) {
		PropertyMapIF map = (PropertyMapIF) m_idMaps.get(language);
		if(map == null) {
			map = new DefaultPropertyMap();
			m_idMaps.put(language, map);
		}
		return map;
	}

	public PropertyMapIF getId2ClassMap() {
		return m_id2classMap;
	}
	
  public MessagesExtensionMap getMessages() {
  	return m_messages;
  }
}
