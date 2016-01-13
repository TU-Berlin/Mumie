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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.avalon.framework.component.Component;

/**
 * Manages the configuration of an index. Use the implementation
 * to load, change and save the configuration.
 * @author vieritz
 */
public interface IndexConfiguration extends Component {

	  /**
	   * Role of implementing classes as an Avalon component. Value is
	   * <span class="string">"net.mumie.cocoon.search.IndexConfiguration"</span>.
	   */
	  public static final String ROLE = IndexConfiguration.class.getName();
	  
	  /**
	   * Directory where the index should be stored.
	   * @return the index directory
	   */
	  public File getIndexDir ();
	  
	  /**
	   * Language parameter for the <span class="string">Lucene</span>-<code>Analyzer</code>.
	   * Currently <span class="string">en</span> and 
	   * <span class="string">de</span> are supported.
	   * @return en for english and de for german.
	   */
	  public String getLang ();
	  
	  /**
	   * Flag for existing index directory and property file.
	   * This flag signalizes if an index is already created.
	   * Usually the properties file will found at 
	   * <span class="string">$COCOON_HOME/index.properties</span>.
	   * @return true if the properties file and the index directory exists.
	   */
	  public boolean existsIndex ();
	  
	  /**
	   * Sets one parameter and saves the configuration.
	   * @param Name
	   * @param Value
	   * @throws IOException in case of problems while storing the parameters.
	   */
	  public void setParameter (String Name, String Value)
	  throws IOException;
	
	  /**
	   * Sets parameters and saves the configuration.
	   * @param Name
	   * @param Value
	   * @throws IOException in case of problems while storing the parameters.
	   */
	  public void setParameters (HashMap map)
	  throws IOException;

}
