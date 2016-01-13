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

package net.mumie.mathletfactory.util;

import java.util.Locale;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.mumie.mathletfactory.appletskeleton.system.MathletRuntime;

/**
 * This class can be used to load and read resources such as locale specific data (e.g. "messages") or icons. 
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public class ResourceManager {
	
	public final static Locale DEFAULT_LOCALE = new Locale("de");
//	final static String STATIC_MESSAGES_BASE_PATH = "/resource/i18n/Messages";
//	final static String KNOWN_PARAMETERS_SETTINGS_FILE = "/resource/settings/known_parameters.xml";
	
//	final static String FILE_ENDING = ".properties";
	
//	private static Locale m_locale = Locale.getDefault();
//	private static ResourceBundle m_staticMessages;
//	private ResourceBundle m_localMessages;
//	private String m_localBasePath;
//	private boolean m_localFileNotFound = false;
	
//  private static MumieLogger m_logger = MumieLogger.getLogger(ResourceManager.class);
//  private final static LogCategory MESSAGES = m_logger.getCategory("messages");
  
	// Constants for reading XML settings
	
//	private final static String NAME_ATTRIBUTE = "name";
//	
//	private final static String VALUES_ATTRIBUTE = "values";
//	
//	private final static String DESCRIPTION_ATTRIBUTE = "description";
//	
//  private static void loadStaticMessages() {
//  	m_staticMessages = getResourceBundle(STATIC_MESSAGES_BASE_PATH, m_locale);
//  	if(m_staticMessages == null) {
//  		m_logger.info("No global messages file found for locale \"" + m_locale + "\", switching to locale \"" + DEFAULT_LOCALE + "\".");
//  		m_staticMessages = getResourceBundle(STATIC_MESSAGES_BASE_PATH, DEFAULT_LOCALE);
//  		if(m_staticMessages == null)
//  			System.err.println("The global messages file could not be found!");
//  	}
//  }
  
//  /*
//   * Loads the local messages according the following search criteria:
//   * 1. the internally saved locale (may be Locale.getDefault() if not set explizitly);
//   * 2. the default locale stored as a class constant.
//   */
//  private void loadLocalMessages() {
//  	m_localMessages = getResourceBundle(m_localBasePath, m_locale);
//  	if(m_localMessages == null) {
//  		m_logger.info("No applet specific messages file found for locale \"" + m_locale + "\", switching to locale \"" + DEFAULT_LOCALE + "\".");
//  		m_localMessages = getResourceBundle(m_localBasePath, DEFAULT_LOCALE);
//  		if(m_localMessages == null && !m_localFileNotFound) {
//  			System.err.println("A local messages file could not be found!");
//  			m_localFileNotFound = true;
//  		}
//  	}
//  }
//  
//  /*
//   * Returns the <code>ResourceBundle</code> for the specified base path and locale or <code>null</code>
//   * if no such resource is found.
//   */
//  private static ResourceBundle getResourceBundle(String baseResourcePath, Locale locale) {
//  	InputStream is = findResource(baseResourcePath, locale, FILE_ENDING);
//  	return (is == null) ? null : loadBundle(is);
//  }
//  
//  /*
//   * Loads a resource bundle from the specified input stream using an instance of PropertyResourceBundle.
//   */
// private static ResourceBundle loadBundle(InputStream is) {
//  	try {
//  		return new PropertyResourceBundle(is);
//  	} catch(Throwable t) {
//  		return null;
//  	}  	
//  }
//  
//  /*
//   * Searches for files containing the base path and/or a language and/or a country code in their names
//   * based on the given locale.
//   * Search order:
//   * 1. file containing both a language and a country code;
//   * 2. file containing only a language code.
//   * If the given locale is null, the default resource is returned.
//   */
//  private static InputStream findResource(String baseResourcePath, Locale locale, String fileEnding) {
//  	if(locale == null)
//  		throw new NullPointerException("Locale must not be NULL !");
////  		return loadResource(baseResourcePath + fileEnding);
//  	InputStream is = loadResource(baseResourcePath + "_" + locale.getLanguage() + "_" + locale.getCountry() + fileEnding);
//  	if(is == null) {
//  		is = loadResource(baseResourcePath + "_" + locale.getLanguage() + fileEnding);
//  	}
//  	return is;
//  }
//  
//  /*
//   * Loads a resource by the system class loader.
//   */
//  private static InputStream loadResource(String path) {
//  	try {
//  		return ResourceManager.class.getResourceAsStream(path);
//  	} catch(Throwable t) {
//  		return null;
//  	}
//  }
//  
//  /**
//   * Sets the locale for this ResourceManager and reloads any locale specific data.
//   * Setting <code>null</code> will restore the default (system dependent) locale. 
//   */
//  public void setLocale(Locale locale) {
//  	if(locale.equals(m_locale))
//  		return;
//  	if(locale == null)
//  		locale = Locale.getDefault();
//  	m_locale = locale;
//  	m_localMessages = null;
//  	m_staticMessages = null;
//  }
//  
//  /**
//   * Sets the base path of the applet specific messages file.
//   */
//  public void setLocalMessagesFile(String basePath) {
//  	m_localBasePath = basePath;
//  	m_localFileNotFound = false;
//  }
//	
//	/**
//	 * Returns the applet specific localized message for the given key <code>key</code>
//	 * or <code>null</code> if no corresponding message is found.
//	 */
//	public String getLocalMessage(String key) {
//		if(key == null)
//			throw new NullPointerException("Message key must not be null!");
//		if(m_localFileNotFound)
//			return null;
//		if(m_localBasePath == null) {
//			System.err.println("No local messages file was set!");
//			return null;
//		}
//		if(m_localMessages == null)// loading for first time
//			loadLocalMessages();
//		if(m_localMessages == null)// file not found
//			return null;
//    try {
//      return m_localMessages.getString(key);
//    } catch (MissingResourceException e) {
//    	m_logger.log(MESSAGES, "No local message found for key: " + key);
//      return null;
//    }
//	}
	
	/**
	 * Returns the localized message for the given key <code>key</code>
	 * or <code>null</code> if no corresponding message is found in the list of global messages.
	 */
	public static String getMessage(String key) {
		if(MathletRuntime.isInitialized())
			return MathletRuntime.getRuntime().getMessage(key);
		return null;
	}
	
//	/**
//	 * Reads in the settings file containing all registered parameters with name, value and description.
//	 * 
//	 * @see java.applet.Applet#getParameterInfo()
//	 */
//	public static String[][] loadKnownParameters() throws SAXException, IOException {
//		Document doc = XMLUtils.createDocumentBuilder().parse(
//				loadResource(KNOWN_PARAMETERS_SETTINGS_FILE) );
//    Node known_parametersNode = XMLUtils.getNextNonTextNode(doc, 0);    
//    int index = 0;
//    Vector parameterList = new Vector();
//		while((index = XMLUtils.getNextNonTextNodeIndex(known_parametersNode, index)) > -1) {
//			Node parameterNode = known_parametersNode.getChildNodes().item(index);
//			// read in attributes
//			NamedNodeMap attributes = parameterNode.getAttributes();
//			String parameterName = attributes.getNamedItem(NAME_ATTRIBUTE).getNodeValue();
//			String parameterValues = attributes.getNamedItem(VALUES_ATTRIBUTE).getNodeValue();
//			String parameterDescription = attributes.getNamedItem(DESCRIPTION_ATTRIBUTE).getNodeValue();
//			parameterList.add(new String[] { parameterName, parameterValues, parameterDescription });
//			index++;
//		}
//		String[][] result = new String[parameterList.size()][3];
//		parameterList.copyInto(result);
//		return result;
//	}
	
	/**
	 * Loads and returns the icon specified by <code>pathName</code>,
	 * where the path can be the path inside a jar file.
	 */
	public static Icon getIcon(String pathName) {
		return new ImageIcon(ResourceManager.class.getResource(pathName));
	}
}
