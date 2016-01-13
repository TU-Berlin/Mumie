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

package net.mumie.cocoon.search;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

/**
 * @author vieritz
 */
public class IndexConfigurationImpl
extends AbstractLogEnabled
implements IndexConfiguration , Initializable, Poolable {
	
	/**
	 * Name for property file.
	 */
	public static final String PROPERTIES_FILE_NAME = "index.properties";
	
	/**
	 * Comment in the head of property file.
	 */
	public final static String PROPERTIES_FILE_COMMENT = 
		"properties file for JAPS-index - in case of any error " +
		"the current index will be lost and has to be generated new.";
	
	/**
	 * property file
	 */
	private File propertiesFile;
	
	/**
	 * Instance of <code>java.util.Properties</code>.
	 */
	private Properties properties;
	
	/**
	 * Directory for <span class="string">JAPS</span>-index.
	 */
	private File indexDir;
	
	/**
	 * Flag for existing property file and index directory.
	 */
	private boolean indexExists;
	
	/**
	 * String (en|de) to set the language for indexing.
	 */
	private String lang;
	
	/*
	 *  (non-Javadoc)
	 * @see org.apache.avalon.framework.activity.Initializable#initialize()
	 */
	public void initialize () {
		final String METHOD_NAME = "initialize";
		this.propertiesFile = new File(System.getProperty("java.io.tmpdir", "tmp") + File.separator + PROPERTIES_FILE_NAME);
		this.getLogger().debug(METHOD_NAME + " parameter 'propertiesFileName' is set to: " + this.propertiesFile.getAbsolutePath());
		
		this.properties = new Properties();		
		this.indexExists = (this.propertiesFile.exists() && this.propertiesFile.isFile()) ? true : false;
		if (this.indexExists) {
			try {
				this.properties.load(new BufferedInputStream(new FileInputStream(this.propertiesFile)));
			} catch (FileNotFoundException e) {
				this.indexExists = false;
				this.getLogger().error(METHOD_NAME + "Cannot find file: " + this.propertiesFile.getAbsolutePath());
			} catch (IOException e) {
				this.indexExists = false;
				this.getLogger().error(METHOD_NAME + "Cannot load properties from file: " + this.propertiesFile.getAbsolutePath());
			}
			this.lang = this.properties.getProperty("lang","en");
			this.indexDir = new File(this.properties.getProperty("index-dir"));
			if (!this.indexDir.exists() || !this.indexDir.isDirectory()) {
				this.indexExists = false;
				this.indexDir = null;
			}	
		}
	}
	
	/* (non-Javadoc)
	 * @see net.mumie.cocoon.search.IndexConfiguration#getIndexDirPath()
	 */
	public File getIndexDir () {
		return this.indexDir;
	}

	/* (non-Javadoc)
	 * @see net.mumie.cocoon.search.IndexConfiguration#getLang()
	 */
	public String getLang() {
		return this.lang;
	}

	/* (non-Javadoc)
	 * @see net.mumie.cocoon.search.IndexConfiguration#indexExists()
	 */
	public boolean existsIndex() {
		return indexExists;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see net.mumie.cocoon.search.IndexConfiguration#setParameter(java.lang.String, java.lang.String)
	 */
	public void setParameter (String name, String value) throws IOException {
		this.properties.setProperty(name, value);
		this.properties.store(new BufferedOutputStream(new FileOutputStream(this.propertiesFile)), PROPERTIES_FILE_COMMENT);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see net.mumie.cocoon.search.IndexConfiguration#setParameter(java.util.HashMap)
	 */
	public void setParameters (HashMap map) throws IOException {
		Iterator it = map.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			this.properties.setProperty(key, (String) map.get(key));
		}
		this.properties.store(new BufferedOutputStream(new FileOutputStream(this.propertiesFile)), PROPERTIES_FILE_COMMENT);
	}	

}
