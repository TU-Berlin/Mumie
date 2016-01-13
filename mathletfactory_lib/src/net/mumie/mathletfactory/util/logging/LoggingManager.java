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

package net.mumie.mathletfactory.util.logging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

/**
 * This class acts as a manager class for the MathletFactory Logging Framework.
 * It loads the categories to be logged from the client system and creates the necessary logger objects.
 * This class is used internally by the logging framework and does not expose any methods nor properties
 * to external code.
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public class LoggingManager {
	
	public final static String LOGGING_FILE = ".mumie-logging.properties";
	
	private final static String LOGGING_FILE_HEADER = "Mumie Logging File";

	private final static String LOGGABLE_CATEGORIES_FILE_LOCATION = "/resource/settings/loggable_categories.properties";

	private static LoggingManager m_instance;
		
	private boolean m_isActive = false;
	
	private Hashtable m_loggers = new Hashtable();
	
	private Hashtable m_categories = new Hashtable();
	
	static {
		m_instance = new LoggingManager();
		// load file
  	try {
  		Properties loggableCategoryProps = new Properties();
  		loggableCategoryProps.load(LoggingManager.class.getResourceAsStream(LOGGABLE_CATEGORIES_FILE_LOCATION));
  		for(Iterator i = loggableCategoryProps.keySet().iterator(); i.hasNext(); ) {
  			String categoryName = (String) i.next();
  			String scopeList = loggableCategoryProps.getProperty(categoryName);
  			String[] scopes = scopeList.split(",");
  			for(int j = 0; j < scopes.length; j++) {
  				String scope = scopes[j].trim();
  				m_instance.getLogger(scope, false).addLoggableCategory(m_instance.getCategory(categoryName, true));
  			}
  		}
  		Properties loggerProps = null;
   		String userHome = System.getProperty("user.home");
  		if(userHome != null) {
	  		File loggingFile = new File(userHome, LOGGING_FILE);
	  		if(loggingFile.canRead()) {
	  			loggerProps = new Properties();
	  			loggerProps.load(new FileInputStream(loggingFile));
	  		}
  		}
  		// test if logging file exists in eclipse workspace
  		if(loggerProps == null) {
  			InputStream in = LoggingManager.class.getResourceAsStream("/logging.properties");
  			if(in != null) {
	  			loggerProps = new Properties();
	  			loggerProps.load(in);
  			}
  		}
  		
  		if(loggerProps != null) {
	  		// logging file found -> enable logging
	  		m_instance.m_isActive = true;
	  		System.out.println("LoggingManager: logging enabled\n");
	  		
	  		// parse logging file
	  		for(Iterator i = loggerProps.keySet().iterator(); i.hasNext(); ) {
	  			String scope = (String) i.next();
	  			String categoriesList = loggerProps.getProperty(scope);
	  			String[] categoryNames = categoriesList.split(",");
	  			for(int j = 0; j < categoryNames.length; j++) {
	  				String categoryName = categoryNames[j].trim();
	  				m_instance.getLogger(scope, false).addActiveCategory(m_instance.getCategory(categoryName, true));
	  			}
	  		}
	  		m_instance.enableLoggers(true);
  		}
  	}
  	 // do nothing on these exceptions -> applet doesn't fit the requirements
  	catch(SecurityException ace) {}
  	catch(NullPointerException npe) {npe.printStackTrace();}
  	catch (IOException ioe) {ioe.printStackTrace();}
  	// TODO: printStackTrace entfernen in final version
	}
	
	void save() {
 		String userHome = System.getProperty("user.home");
		if(userHome == null)
			throw new RuntimeException("Cannot read user home!");
		if(isActive()) {
			Properties props = new Properties();
			for(Iterator i = m_loggers.keySet().iterator(); i.hasNext(); ) {
				String loggerName = (String) i.next();
				MumieLogger logger = (MumieLogger) m_loggers.get(loggerName);
				LogCategory[] activeCategories = logger.getOwnActiveCategories();
				if(activeCategories.length == 0)
					continue;
				String categoriesList = "";
				for(int j = 0; j < activeCategories.length; j++) {
					categoriesList += activeCategories[j];
					if(j < activeCategories.length -1)
						categoriesList += ", ";
				}
				props.setProperty(loggerName, categoriesList);
			}
			try {
				props.store(new FileOutputStream(new File(userHome, LOGGING_FILE)), LOGGING_FILE_HEADER);
			} catch (IOException e) {
				System.err.println("Logging file could not be written:");
				e.printStackTrace();
			}
		} else {
			File loggingFile = new File(userHome, LOGGING_FILE);
			if(loggingFile.exists())
				loggingFile.delete();
		}
	}
	
	synchronized MumieLogger getLogger(String name) {
		return getLogger(name, true);
	}
	
	synchronized MumieLogger getLogger(String name, boolean initialize) {
		if(name == null)
			return null;
		MumieLogger logger = (MumieLogger) m_loggers.get(name);
		if(logger == null) {
			log("Creating logger for name " + name);
			logger = new MumieLogger(name);
			m_loggers.put(name, logger);			
		}
		if(initialize)
			logger.setActive(true);
		return logger;
	}
	
	synchronized LogCategory getCategory(String name) {
		return getCategory(name, false);
	}
	
	private synchronized LogCategory getCategory(String name, boolean create) {
		LogCategory category = (LogCategory) m_categories.get(name);
		if(category == null && create) {
			log("Creating category for name " + name);
			category = new LogCategory(name);
			m_categories.put(name, category);
		}
		return category;
	}
		
	private void enableLoggers(boolean flag) {
		log("Updating loggers");
		for(Iterator i = m_loggers.keySet().iterator(); i.hasNext(); ) {
			String loggerName = (String) i.next();
			MumieLogger logger = (MumieLogger) m_loggers.get(loggerName);
			if(logger.isRoot())
				logger.setActive(flag);
		}
	}
	
	static LoggingManager getLoggingManager() {
		return m_instance;
	}
	
	boolean isActive() {
		return m_isActive;
	}
	
	void setActive(boolean flag) {
		m_isActive = flag;
	}
	
	static String getShortClassName(Class c) {
		String cName = c.getName();
		if(cName.indexOf(".") > -1)
			return cName.substring(cName.lastIndexOf(".") + 1);
		return cName;
	}
	
	private static void log(String message) {
//		System.out.println(message);
	}
}