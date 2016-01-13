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

package net.mumie.cocoon.generators;

import java.io.IOException;
import java.io.PipedInputStream;
import java.util.Map;

import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.search.Searcher;
import net.mumie.cocoon.xml.FragmentSAXFilter;

import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.generation.ServiceableGenerator;
import org.apache.excalibur.xml.sax.SAXParser;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.hp.hpl.jena.rdf.model.Model;

public class SearchGenerator extends ServiceableGenerator {
	
	private final String HEAD_TITLE = "Ergebnisse der Suche";
	
	private final String PAGE_TITLE = HEAD_TITLE;	
	
	private final String SUMMARY_TITLE = "Zusammenfassung";
	
	private final String MISSING_INPUT = "Sucheingaben fehlen";
	
	private final String NO_RESULT = "Keine Treffer";	
	
	private final String QUANTITY = "Anzahl der Treffer";
	
	private final String LISTING_TITLE = "Trefferliste";
	
	private final String NEW_SEARCH = "Neue Suche";	
	
	/**
	 * Signalizes a simple search.
	 */
	private boolean isSimpleSearch = false;
	
	/**
	 * Signalizes a expert search.
	 */
	private boolean isExpertSearch = false;
	
	/**
	 * Search string for simple search.
	 */
	private String searchString;
	
	/**
	 * Search field for simple search.
	 */
	private String searchField;
	
	/**
	 * Search string for expert search.
	 */
	private String searchPhrase;
	
	public void setup(SourceResolver resolver, Map objectModel, String src, Parameters par)
	throws ProcessingException, SAXException, IOException{
		final String METHOD_NAME = "setup";
		this.getLogger().debug(METHOD_NAME + " started");
		super.setup(resolver, objectModel, src, par);
		
		this.isSimpleSearch = false;
		this.isExpertSearch = false;
		this.searchString = null;
		this.searchField = null;
		this.searchPhrase = null;	
		
		try {
			if (this.parameters.isParameter("search-phrase"))
				this.searchPhrase = this.parameters.getParameter("search-phrase").trim();
			this.getLogger().debug(METHOD_NAME + " searchPhrase: " + this.searchPhrase);
			
			if (this.parameters.isParameter("search-string"))
				this.searchString = this.parameters.getParameter("search-string").trim();
			this.getLogger().debug(METHOD_NAME + " searchString: " + this.searchString);
			
			if (this.parameters.isParameter("search-field"))
				this.searchField = this.parameters.getParameter("search-field").trim();
			if (this.searchField.equals(""))
				this.searchField = XMLElement.DESCRIPTION;
			this.getLogger().debug(METHOD_NAME + " searchField: " + this.searchField);
			
			// check first for expert search
			if (!this.searchPhrase.equals(""))
				this.isExpertSearch = true;
			else if (!this.searchString.equals(""))
				this.isSimpleSearch =  true;
		}
		catch (ParameterException e) {
			throw new ProcessingException(e);
		}
	}
	
	/**
	 * Inserts a complete HTML node
	 * @param elemName the name of the node
	 * @param attrs
	 * @param text included text
	 * @throws SAXException
	 */
	private void insertHtmlElement (String elemName, AttributesImpl attrs, String text) throws SAXException {
		this.contentHandler.startElement("", elemName, elemName, attrs);
		char[] chars = text.toCharArray();
		this.contentHandler.characters(chars, 0, chars.length);
		this.contentHandler.endElement("", elemName, elemName);
	}
	
	public void generate() throws IOException, SAXException, ProcessingException {
		final String METHOD_NAME = "generate";
		
		this.getLogger().debug(METHOD_NAME + " searching started");
		Searcher searcher = null;
		SAXParser parser = null;
		FragmentSAXFilter fragSaxFilter = null;
		try {
			searcher = (Searcher) this.manager.lookup(Searcher.ROLE);
			searcher.setup();
			Model model = null;
			
			if (this.isExpertSearch)
				model = searcher.search(this.searchPhrase, this.searchField);
			else if (this.isSimpleSearch)
				model = searcher.search(this.searchString, this.searchField);
			
			this.getLogger().debug(METHOD_NAME + " searching finished");
			
			this.contentHandler.startDocument();
			this.getLogger().debug(METHOD_NAME + " document started");
			
			AttributesImpl attrs = new AttributesImpl();
			attrs.clear();
			
			this.contentHandler.startElement("", "html", "html", attrs);
			this.contentHandler.startElement("", "head", "head", attrs);			
			insertHtmlElement("title",attrs, HEAD_TITLE);
			this.contentHandler.endElement("", "head", "head");
			this.contentHandler.startElement("", "body", "body", attrs);

			insertHtmlElement("h1",attrs, PAGE_TITLE);
			insertHtmlElement("h2",attrs, SUMMARY_TITLE);
			// the stylesheet is inserting the link target
			insertHtmlElement("a",attrs, NEW_SEARCH);			
			
			if (!this.isExpertSearch && !this.isSimpleSearch)
				insertHtmlElement("p",attrs, MISSING_INPUT);
			else if  (model == null)
				insertHtmlElement("p",attrs, NO_RESULT);
			else {
				insertHtmlElement("p",attrs, QUANTITY + ": " + new Integer(searcher.getNumberOfHits()).toString());
				insertHtmlElement("h2",attrs, LISTING_TITLE);
			}

			if (model!= null) {
				parser = (SAXParser) this.manager.lookup(SAXParser.ROLE);
				fragSaxFilter = (FragmentSAXFilter) this.manager.lookup(FragmentSAXFilter.ROLE);
				fragSaxFilter.setContentHandler(this.contentHandler);
				
				this.getLogger().debug(METHOD_NAME + " start parsing");				
				PipedInputStream pis = new PipedInputStream();
				searcher.streamRdf(pis);
				parser.parse(new InputSource(pis), fragSaxFilter);
				pis.close();
				this.getLogger().debug(METHOD_NAME + " finish parsing");					
			}
			
			this.contentHandler.endElement("", "html", "html");
			this.contentHandler.endDocument();
			this.getLogger().debug(METHOD_NAME + " document finished");
		}
		catch (ServiceException s) {
			throw new ProcessingException(s);
		}
		finally {
			if (searcher != null) this.manager.release(searcher);
			if (parser != null) this.manager.release(parser);
			if (fragSaxFilter != null) this.manager.release(fragSaxFilter);			
		}
		
		this.getLogger().debug(METHOD_NAME + " finished");
	}
}
