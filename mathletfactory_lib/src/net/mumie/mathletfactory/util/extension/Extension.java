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

import java.io.InputStream;
import java.net.URL;

import net.mumie.mathletfactory.action.message.XMLParsingException;
import net.mumie.mathletfactory.appletskeleton.MathletContext;
import net.mumie.mathletfactory.appletskeleton.system.MathletRuntime;
import net.mumie.mathletfactory.util.Version;
import net.mumie.mathletfactory.util.logging.LogCategory;
import net.mumie.mathletfactory.util.logging.MumieLogger;
import net.mumie.mathletfactory.util.property.DefaultPropertyMap;
import net.mumie.mathletfactory.util.property.PropertyMapIF;
import net.mumie.mathletfactory.util.property.PropertySheet;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Extension {
	
	public final static String MATHLET_EXTENSION_NAME = "mathlet";
	
	private final static String EXTENSION_PROPERTIES_FILE_NAME = "properties.xml";
	
	private final static String DEFAULT_MATHLET_PROPERTIES_LOCATION = "/resource/settings/default_mathlet_properties.xml";
	
	public static PropertySheet loadPropertySheet(String extensionName) {
		String extensionFileName = "/" + extensionName + "/" + EXTENSION_PROPERTIES_FILE_NAME;
		LOGGER.log(EXTENSIONS_CATEGORY, "Loading property file for extension \"" + extensionName + "\" from \"" + extensionFileName + "\"...");
		InputStream extensionFileStream = Extension.class.getResourceAsStream(extensionFileName);
		if(extensionFileStream == null) {
			LOGGER.log(EXTENSIONS_CATEGORY, "Property sheet not found: \"" + extensionFileName + "\"");
			return null;
		}
		return new PropertySheet(extensionFileStream);
	}
	
	public static PropertyMapIF loadParameters(String extensionName) {
		PropertySheet propSheet = loadPropertySheet(extensionName);
		if(propSheet == null)
			return null;
		return propSheet.getProperties(PropertySheet.PARAMETER_SECTION);
	}
	
	public static Extension forName(String extensionName, PropertyMapIF parameters) throws ExtensionException {
		PropertySheet propSheet = loadPropertySheet(extensionName);
		if(propSheet == null) {
			LOGGER.log(EXTENSIONS_CATEGORY, "Extension not found in class path: \"" + extensionName + "\"");
			return null;
		}
		LOGGER.log(EXTENSIONS_CATEGORY, "Creating lib extension \"" + extensionName + "\"");
		return new Extension(extensionName, propSheet, parameters, "/" + extensionName);
	}
	
	public static PropertySheet loadPropertySheet(MathletContext mathlet) {
		String extensionFileName = getMathletExtensionDirectory(mathlet) + "/" + EXTENSION_PROPERTIES_FILE_NAME;
		PropertySheet propSheet = null;
		InputStream extensionFileStream = Extension.class.getResourceAsStream(extensionFileName);
		if(extensionFileStream != null)
			propSheet = new PropertySheet(extensionFileStream);
		// use default mathlet properties if no extension property file is available
		else
			propSheet = new PropertySheet(Extension.class.getResourceAsStream(DEFAULT_MATHLET_PROPERTIES_LOCATION));
		return propSheet;
	}
	
	public static PropertyMapIF loadParameters(MathletContext mathlet) {
		PropertySheet propSheet = loadPropertySheet(mathlet);
		if(propSheet == null)
			return null;
		return propSheet.getProperties(PropertySheet.PARAMETER_SECTION);
	}
	
	public static Extension forMathlet(MathletContext mathlet, PropertyMapIF parameters) throws ExtensionException {
		PropertySheet propSheet = loadPropertySheet(mathlet);
		LOGGER.log(EXTENSIONS_CATEGORY, "Creating mathlet extension \"" + mathlet.getShortName() + "\"");
		return new Extension(MATHLET_EXTENSION_NAME, propSheet, parameters, getMathletExtensionDirectory(mathlet));
	}
	
	public static String getMathletExtensionDirectory(MathletContext mathlet) {
		return "/" + mathlet.getAppletClass().getName().replace('.', '/')  + ".files";
	}

	private final static MumieLogger LOGGER = MumieLogger.getLogger(Extension.class);
	
	private final static LogCategory EXTENSIONS_CATEGORY = LOGGER.getCategory("extension.main-extensions");

	private String m_name;
	
	private PropertySheet m_properties;
	
	private String m_basePath;
	
	private PropertyMapIF m_parameters;
	
	private PropertyMapIF m_parameterDefinitions;
	
//	private PropertyMapIF m_loggingProperties;
	
	private MessagesExtensionMap m_messagesMap;
	
	private ScreenTypeExtensionMap m_screenTypeMap;
	
	private DialogExtensionMap m_dialogs;
	
	private HelpExtension m_help;
	
	private ComplianceExtensionMap m_complianceExtensions;
	
	private PropertyMapIF m_themeProperties;
	
	private Version m_complianceLevel;
	
	/**
	 * @param parameters map containing parameters; may be null
	 */
	Extension(String name, PropertySheet properties, PropertyMapIF parameters, String basePath) throws ExtensionException {
		m_name = name;
		m_properties = properties;
		m_parameters = parameters; 
		m_basePath = basePath;
		m_parameterDefinitions = new DefaultPropertyMap();
//		m_loggingProperties = new DefaultPropertyMap();
		m_messagesMap = new MessagesExtensionMap(this);
		m_screenTypeMap = new ScreenTypeExtensionMap();
		m_dialogs = new DialogExtensionMap();
		m_help = new HelpExtension(this);
		m_complianceExtensions = new ComplianceExtensionMap(this);
		m_themeProperties = new DefaultPropertyMap();
		try {
			loadValues();
		} catch(UnsupportedExtensionException unsupportedError) {
			unsupportedError.setExtensionName(name);
			throw unsupportedError;
		} catch(Throwable t) {
			throw new DefectiveExtensionException(t, name);
		}
	}
	
	private void loadValues() {
//		Node[] loggingNodes = m_properties.getLoggingNodes();
//		for(int i = 0; i < loggingNodes.length; i++) {
//	  	PropertySheet.readPropertyNodes(loggingNodes[i], m_loggingProperties);
//		}
//		
		if(m_parameters == null)
			m_parameters = m_properties.getProperties(PropertySheet.PARAMETER_SECTION);
		
		Node[] parameterDefNodes = m_properties.getParameterDefinitionNodes();
		for(int i = 0; i < parameterDefNodes.length; i++) {
	  	Node parametersNode = parameterDefNodes[i];
	  	NodeList nodes = parametersNode.getChildNodes();
	  	for(int j = 0; j < nodes.getLength(); j++) {
	  		Node n = nodes.item(j);
	  		if(n.getNodeType() == Node.TEXT_NODE || n.getNodeType() == Node.COMMENT_NODE)
	  			continue;
	  		if(n.getNodeName().equals("mf:parameter")) {
	  			ParameterDefinition def = new ParameterDefinition(n);
	  			m_parameterDefinitions.setProperty(def.getName(), def);
	   		} else
	  			throw new DefectiveExtensionException(new XMLParsingException("Only parameter definitions can be parsed in this section !", n));	
	  	}
		}
		
  	Node[] i18nNodes = m_properties.geti18nNodes();
  	m_messagesMap.addMessages(i18nNodes);

  	Node[] screenTypeNodes = m_properties.getScreenTypeNodes();
  	m_screenTypeMap.addScreenTypeExtensions(screenTypeNodes);
  	
  	Node[] dialogNodes = m_properties.getDialogDefinitionNodes();
  	m_dialogs.addDialogDefinitions(dialogNodes);
  	  	
  	Node[] helpNodes = m_properties.getHelpNodes();
  	m_help.addProperties(helpNodes);

		Node[] themeNodes = m_properties.getThemeNodes();
		for(int i = 0; i < themeNodes.length; i++) {
	  	PropertySheet.readPropertyNodes(themeNodes[i], m_themeProperties);
		}
		
  	Node[] complianceNodes = m_properties.getComplianceNodes();
  	m_complianceExtensions.addExtensions(complianceNodes);
  	
  	// read in parameters
  	m_complianceLevel = (Version) getParameters().getProperty("compliance-level", MathletRuntime.DEFAULT_COMPLIANCE_LEVEL);
	}
	
	public PropertyMapIF getParameters() {
		return m_parameters;
	}
	
	public PropertyMapIF getRegisteredParameters() {
		return m_parameterDefinitions;
	}
	
//	public PropertyMapIF getLoggingProperties() {
//		return m_loggingProperties;
//	}
	
	public ScreenTypeExtensionMap getScreenTypes() {
  	return m_screenTypeMap;
	}
	
	public DialogExtensionMap getDialogs() {
  	return m_dialogs;
	}
	
  public MessagesExtensionMap getMessages() {
  	return m_messagesMap;
  }
  
  public HelpExtension getHelpInformation() {
  	return m_help;
  }
  
  public PropertyMapIF getThemeProperties() {
  	return m_themeProperties;
  }
  
  public ComplianceExtensionMap getComplianceExtensions() {
  	return m_complianceExtensions;
  }
  
	public String getName() {
		return m_name;
	}
	
	public String getBasePath() {
		return m_basePath;
	}
	
	public Version getComplianceLevel() {
		return m_complianceLevel;
	}
	
	public URL getResource(String name) {
		URL result = null;
		if(getBasePath() != null)
			result = getClass().getResource(getBasePath() + "/" + name);
		if(result == null)
			result = getClass().getResource(name);
		return result;
	}
	
	public InputStream getResourceAsStream(String name) {
		InputStream result = null;
		if(getBasePath() != null)
			result = getClass().getResourceAsStream(getBasePath() + "/" + name);
		if(result == null)
			result = getClass().getResourceAsStream(name);
		return result;
	}
}
