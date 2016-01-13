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

package net.mumie.mathletfactory.util.property;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;

import net.mumie.mathletfactory.action.message.XMLParsingException;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PropertySheet {

  /**
   * The property sheet namespace
   * (<code>"http://www.mumie.net/xml-namespace/data-sheet"</code>). 
   */
  public final static String NAMESPACE = "http://www.mumie.net/xml-namespace/mf-property-sheet";
  
  /**
   * Local name of the property sheet root element (<code>"mf:property_sheet"</code>).
   */
  public final static String ROOT_ELEMENT = "mf:property_sheet";
  
  public final static int PARAMETER_SECTION = 0;
  
  public final static int PARAMETER_DEFINITION_SECTION = 1;
  
  public final static int SCREEN_TYPE_SECTION = 2;
  
  public final static int I18N_SECTION = 3;
  
  public final static int HELP_SECTION = 4;
  
  public final static int COMPLIANCE_SECTION = 5;
  
  public final static int THEME_SECTION = 6;
  
  /** The root element of this property sheet. */
  protected Element m_rootElement = null;
  
	public PropertySheet() {
		setRootElement(createRootElement(createDocument(createDocumentBuilder())));
	}
	
	public PropertySheet(DocumentBuilder documentBuilder) {
		setRootElement(createRootElement(createDocument(documentBuilder)));
	}
	
	public PropertySheet(Document document) {
		setRootElement(createRootElement(document));
	}
	
	public PropertySheet(Element element) {
		setRootElement(element);
	}
	
	public PropertySheet(InputStream inputStream) {
		try {
			setRootElement(createRootElement(createDocumentBuilder().parse(inputStream)));
		} catch (Exception e) {
			throw new XMLParsingException("Cannot create property sheet", e);
		}
	}
	
	private DocumentBuilder createDocumentBuilder() {
		return XMLUtils.createDocumentBuilder();
	}
	
	private Document createDocument(DocumentBuilder documentBuilder) {
    if (documentBuilder == null)
      throw new NullPointerException("Document builder must not be null !");
    if (!documentBuilder.isNamespaceAware())
      throw new IllegalArgumentException("Document builder must be namespace aware !");
    Document document =
      documentBuilder.getDOMImplementation().createDocument(NAMESPACE, ROOT_ELEMENT, null);
    return document;
	}
	
  private Element createRootElement(Document document) {
  	Element rootElement = document.getDocumentElement();
    rootElement.setAttribute("xmlns", NAMESPACE);
    return rootElement;
  }
  
  private void setRootElement(Element rootElement) {
    if (rootElement == null)
      throw new NullPointerException("Root element must not be null !");
    if (!rootElement.getNodeName().equals(ROOT_ELEMENT))
      throw new IllegalArgumentException("Not a property sheet root element: " + rootElement);
  	m_rootElement = rootElement;
  }
  
  public Document getDocument() {
  	return getRootElement().getOwnerDocument();
  }
  
  public Element getRootElement() {
  	return m_rootElement;
  }
  
  public Node[] getParameterNodes() {
  	return XMLUtils.getChildNodes(getRootElement(), "mf:runtime-settings");
  }
  
  public Node[] getParameterDefinitionNodes() {
  	return XMLUtils.getChildNodes(getRootElement(), "mf:parameters");
  }
  
//  public Node[] getLoggingNodes() {
//  	return XMLUtils.getChildNodes(getRootElement(), "mf:logging");
//  }
  
  public Node[] getScreenTypeNodes() {
  	return XMLUtils.getChildNodes(getRootElement(), "mf:screen-type");
  }
  
  public Node[] getDialogDefinitionNodes() {
  	return XMLUtils.getChildNodes(getRootElement(), "mf:dialogs");
  }
  
  public Node[] geti18nNodes() {
  	return XMLUtils.getChildNodes(getRootElement(), "mf:i18n");
  }
  
  public Node[] getHelpNodes() {
  	return XMLUtils.getChildNodes(getRootElement(), "mf:help");
  }
  
  public Node[] getComplianceNodes() {
  	return XMLUtils.getChildNodes(getRootElement(), "mf:compliance-settings");
  }
  
  public Node[] getThemeNodes() {
  	return XMLUtils.getChildNodes(getRootElement(), "mf:theme-properties");
  }
  
  public Node[] getSectionNodes(int section) {
  	switch(section) {
  	case PARAMETER_SECTION:
  		return getParameterNodes();
  	case PARAMETER_DEFINITION_SECTION:
  		return getParameterDefinitionNodes();
  	case SCREEN_TYPE_SECTION:
  		return getScreenTypeNodes();
  	case I18N_SECTION:
  		return geti18nNodes();
  	case HELP_SECTION:
  		return getHelpNodes();
  	case COMPLIANCE_SECTION:
  		return getComplianceNodes();
  	case THEME_SECTION:
  		return getThemeNodes();
  	}
		throw new IllegalArgumentException("Illegal property sheet section: " + section);
  }
  
  public PropertyMapIF getProperties(int section) {
  	PropertyMapIF result = new DefaultPropertyMap();
		Node[] nodes = getSectionNodes(section);
		for(int i = 0; i < nodes.length; i++) {
	  	PropertySheet.readPropertyNodes(nodes[i], result);
		}
		return result;
  }
  
  public static void readPropertyNodes(Node parentNode, PropertyMapIF map) {
  	NodeList nodes = parentNode.getChildNodes();
  	for(int i = 0; i < nodes.getLength(); i++) {
  		Node n = nodes.item(i);
  		if(n.getNodeType() == Node.TEXT_NODE || n.getNodeType() == Node.COMMENT_NODE)
  			continue;
  		if(n.getNodeName().equals("mf:property")) {
  			Property p = new Property(n);
  			if(map instanceof DefaultPropertyMap)
  				((DefaultPropertyMap) map).setProperty(p);
  			else
  				map.setProperty(p.getName(), p.getValue());
  		} else
  			throw new XMLParsingException("Only properties can be parsed in this section !", n);	
  	}
  }
  
  /**
   * Creates a new property sheet with a root node containing all child nodes of <code>newRootNode</code>.
   * Note that the root node will be different in the newly created property sheet.
   */
  public static PropertySheet createSubSheet(Node newRootNode) {
  	PropertySheet result = new PropertySheet();
  	NodeList children = newRootNode.getChildNodes();
  	for(int i = 0; i < children.getLength(); i++) {
  		result.getRootElement().appendChild(
  				result.getRootElement().getOwnerDocument().importNode(children.item(i), true));
  	}
  	return result;
  }
}
