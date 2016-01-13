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

package net.mumie.coursecreator.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.mumie.coursecreator.CCController;

import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Attr;

/***
 * ELClassListWrapper.java
 *
 * Created: Wed Oct 12 11:51:01 2005
 **/
/**
 * Wraps the index of e-learning classes ("Lehrveranstaltungen") available
 * on the JAPS server.
 *
 * @author <a href="mailto:sinha@math.tu-berlin.de">Uwe Sinha</a>
 * @version $Revision: 1.4 $ ($Date: 2008/05/28 12:39:40 $)
 */

public class ELClassListWrapper {
	
	private ELClassListWrapper() throws ParserConfigurationException {
		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		fac.setIgnoringComments(true);
		fac.setNamespaceAware(true);
		fac.setValidating(false);
		this.domBuilder = fac.newDocumentBuilder();
		
	}
	private DocumentBuilder domBuilder = null; 
	
	private Document classList = null;
	
	private static ELClassListWrapper instance = null; 
	
	/**
	 * Obtain an instance of the <code>ELClassListWrapper</code>. There
	 * SHOULD be only one.
	 *
	 * @return a <code>ELClassListWrapper</code> (the only one), or
	 * <code>null</code> if initializing this class failed
	 * @exception ParserConfigurationException if no parser for the
	 * classList was available.
	 */
	public static synchronized ELClassListWrapper getInstance()
	throws ParserConfigurationException 
	{
		if(instance == null) {
			instance = new ELClassListWrapper();
		}
		
//		if (xpathError == null || xpathException == null || xpathMessage == null 
//		|| xpathExplanation == null) 		
//		{
//		System.out.println("In ELClassListWrapper#getInstance():");
//		System.out.println
//		("WARNING: Initialization of XPaths for parsing error responses failed." );
//		return null;    
//		} // end of if (xpathError == null || ...)
		
		return instance;
	}
	
	/**
	 * Construct a DOM {@link Document} from the XML read from
	 * <code>inst</code>. That XML MUST conform to the Checkin List XML
	 * specification (see class description).
	 *
	 * @param inst an <code>InputStream</code> to read XML from
	 */
	public void parse(InputStream inst) throws SAXException, IOException {
		this.classList = domBuilder.parse(inst);
	}
	
	public Map getELClasses() {
		LinkedHashMap map = new LinkedHashMap(); 
		try {
			babble("getELClasses(): constructing XPaths");  
			XPath docPath = new DOMXPath("/mumie:pseudo_documents/mumie:class"); 
			docPath.addNamespace
			(StringUtils.substringBefore(XMLConstants.PREFIX_META,":"),
					XMLConstants.NS_META);
//			XPath idPath = new DOMXPath("./@id");
//			XPath namePath = new DOMXPath("./name/*");
			XPath idPath = new DOMXPath("attribute::id");
			XPath pathPath = new DOMXPath("attribute::path");
			idPath.addNamespace
			(StringUtils.substringBefore(XMLConstants.PREFIX_META,":"),
					XMLConstants.NS_META);
			XPath namePath = new DOMXPath("child::mumie:name/child::text()");
			namePath.addNamespace
			(StringUtils.substringBefore(XMLConstants.PREFIX_META,":"),
					XMLConstants.NS_META);
			Element doc = classList.getDocumentElement();
			babble("getELClasses(): this is my document root: "
					+ doc.toString());
			List li = docPath.selectNodes(doc); 
			babble("getELClasses(): got back a list which " 
					+ (li == null ? "is null" 
							: "has " + li.size() + " element(s)"));
			for (Iterator lit = li.iterator(); lit.hasNext();) {
				// FIXME(2): the loop body might use a minor code cleanup
				babble("getELClasses(): looping through nodes...");
				Object node = lit.next();
				//Object theID = idPath.selectSingleNode(node);
				Object thePath = pathPath.selectSingleNode(node);
				Object theName = namePath.selectSingleNode(node);
				String theNameStr = (theName instanceof Text 
						? ((Text)theName).getNodeValue() : "---");
				//Integer theNewId = (theID instanceof Attr ? Integer.valueOf(((Attr)theID).getValue()) : new Integer(XMLConstants.UNDEFINED_ID));
				String theNewPath = (thePath instanceof Attr ? ((Attr)thePath).getValue() : "/");
				babble("getELClasses(): Will put (" + theNewPath //theID.toString() 
						+ ",\"" + theNameStr + "\") into the map.");
				map.put(theNewPath,theNameStr);
				babble("getELClasses(): ...end of one loop");
			} // end of for (Iterator lit = li.iterator(); lit.hasNext();)	    
		} catch (Exception ex) {
			CCController.dialogErrorOccured(
					"ELClassListWrapper: Exception", 
					"ELClassListWrapper: Exception: " + ex, 
					JOptionPane.ERROR_MESSAGE);
			
			return null; 
		} // end of try-catch
		return map;
	}
	void babble(String bab){
		if (false) System.out.println("ELClassListWrapper: "+bab);
	}
}// ELClassListWrapper
