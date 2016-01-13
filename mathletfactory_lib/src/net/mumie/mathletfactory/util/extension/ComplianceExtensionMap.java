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

import net.mumie.mathletfactory.action.message.XMLParsingException;
import net.mumie.mathletfactory.util.Version;
import net.mumie.mathletfactory.util.logging.LogCategory;
import net.mumie.mathletfactory.util.logging.MumieLogger;
import net.mumie.mathletfactory.util.property.PropertySheet;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Node;

public class ComplianceExtensionMap {

	private final static MumieLogger LOGGER = MumieLogger.getLogger(ComplianceExtensionMap.class);
	
	private final static LogCategory COMPLIANCE_EXTENSIONS_CATEGORY = LOGGER.getCategory("extension.compliance-extensions");

	private Map m_extensions = new HashMap();
	
	private Extension m_masterExtension;
	
	public ComplianceExtensionMap() {
	}
	
	public ComplianceExtensionMap(Extension masterExtension) {
		m_masterExtension = masterExtension;
	}
	
	public void addExtensions(Node[] xmlNodes) {
		for(int i = 0; i < xmlNodes.length; i++) {
			Node n = xmlNodes[i];
			if(n.getNodeType() == Node.TEXT_NODE || n.getNodeType() == Node.COMMENT_NODE)
				continue;
			addExtension(n);
		}
	}
	
	private Extension addExtension(Node xmlNode) {
		try {
			String level = XMLUtils.getAttribute(xmlNode, "level");
			if(level == null)
				throw new DefectiveExtensionException(new XMLParsingException("LEVEL attribute must be set in every compliance settings node !"));
			Extension e = getComplianceExtension(level);
			if(e == null && m_masterExtension != null) {
				LOGGER.log(COMPLIANCE_EXTENSIONS_CATEGORY, "Creating compliance settings from extension \"" + m_masterExtension.getName() + "\" for level \"" + level + "\"");
				e = new Extension(m_masterExtension.getName() + "#" + level, PropertySheet.createSubSheet(xmlNode), null, m_masterExtension.getBasePath());
				m_extensions.put(level, e);
			}
			return e;
		} catch(Exception exc) {
			throw new DefectiveExtensionException(new XMLParsingException("Could not load extension from XML !", exc));
		}
	}
	
	public void clear() {
		m_extensions.clear();
	}
	
	public Extension getComplianceExtension(Version level) {
		return getComplianceExtension(level.toString(true, false));
	}
	
	private Extension getComplianceExtension(String level) {
		return (Extension) m_extensions.get(level);
	}
}
