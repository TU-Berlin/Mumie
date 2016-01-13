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

package net.mumie.coursecreator.graph.cells;

import net.mumie.coursecreator.graph.NavGraph;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

/**
 * represents Components with Documents from Japs
 * @author <a href="mailto:vrichter@math.tu-berlin.de">Verena Richter</a>
 * @author <a href="mailto:binder@math.tu-berlin.de">Jens Binder</a>
 * @version $Id: ComponentCell.java,v 1.9 2007/08/21 17:10:03 lehmannf Exp $
 *
 */
public interface ComponentCell {

	
	// lid
	String getLid();
	void setLid(String lid);
	
	//docType
	void setDocType(int docType);
	int getDocType();

	//category
	void setCategory(int category);
	int getCategory();

	//label
	void setLabel(String label);
	String getLabel();
	
	NavGraph getSubGraph();
	void setSubGraph(NavGraph subGraph);
	
	boolean navDownPossible();
    
	String getElementPath();
	void setElementPath(String path);
  	
	String getCellDescription();
	/**
	 * produces the Element for the MetaXMLFile
	 * something like:
	 *  <code>
	 *	<mumie:course_section lid="1" url="theoriegraph_1.meta.xml">
     *     <mumie:ref_attribute name="label" value=""/>
     *   </mumie:course_section>
	 *	<code>
	 *	not as String but as Element
	 * @param doc : the DOM-Document 
	 */
	public Element getMetaXML(Document doc);
} 