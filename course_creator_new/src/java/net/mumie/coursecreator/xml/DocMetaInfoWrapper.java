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


import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.mumie.coursecreator.CCController;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

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
 * @version $Revision: 1.3 $ ($Date: 2009/03/30 12:36:28 $)
 */

public class DocMetaInfoWrapper {
	
	private DocMetaInfoWrapper() throws ParserConfigurationException {
		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		fac.setIgnoringComments(true);
		fac.setNamespaceAware(false);
		fac.setValidating(false);
		this.domBuilder = fac.newDocumentBuilder();
		
	}
	private DocumentBuilder domBuilder = null; 
	

	
	private static DocMetaInfoWrapper instance = null; 
	
	public static synchronized DocMetaInfoWrapper getInstance()
	throws ParserConfigurationException{
		if(instance == null) instance = new DocMetaInfoWrapper();
		return instance;
	}
	
	public void parse(InputStream inst) {
		Document doc = null;
		babble("WWWWWWWWWWWWWw Start ..");
		
		try {
			doc = domBuilder.parse(inst);
		} catch (SAXException e) {
			CCController.dialogErrorOccured(
					"DocMetaInfoWrapper: SAXException", 
					"DocMetaInfoWrapper: SAXException: " + e, 
					JOptionPane.ERROR_MESSAGE);
			
			return;
		} catch (IOException e) {
			CCController.dialogErrorOccured(
					"DocMetaInfoWrapper: IOException", 
					"DocMetaInfoWrapper: IOException: " + e, 
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		babble("WWWWWWWWWWWWWw ...stop doc: "+doc);
		
		Element root = doc.getDocumentElement();
		babble("root.getChildNodes: "+root.getChildNodes());
		babble("Attributes: "+root.getAttributes());
		babble("root.getNodeName(): "+root.getNodeName());
		
	}
	

	
	
	void babble(String bab){
		if (true) System.out.println("DocMetaInfoWrapper: "+bab);
	}
}// DocMetaInfoWrapper
