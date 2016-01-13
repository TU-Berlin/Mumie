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

import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.util.Map;
import java.util.StringTokenizer;

import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.search.Indexer;
import net.mumie.cocoon.xml.FragmentSAXFilter;

import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;

import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.Context;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.generation.ServiceableGenerator;
import org.apache.excalibur.xml.sax.SAXParser;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class IndexGenerator extends ServiceableGenerator {
	
	private final String HEAD_TITLE = "Ergebnisse der Indexierung";
	
	private final String PAGE_TITLE = HEAD_TITLE;	
	
	private final String SUMMARY_TITLE = "Zusammenfassung";
	
	private final String STATUS = "Statusmeldung";
	
	private final String QUANTITY = "Anzahl der indexierten Dokumente";
	
	private final String LISTING_TITLE = "Indexierte Dokumente";
	
	/**
	 * Language-parameter. Specifying the Lucene-analyzer.
	 */
	private String lang;
	
	/**
	 * Document-types for indexing as comma-separated strings. Specified as request-parameter.
	 */
	private final String TYPE_NAMES = "type-names";
	
	/**
	 * Document-types for indexing as separated integers. Specified as request-parameter.
	 * Not yet in use.
	 */
	private final String TYPE_NAME_LIST = "type-name-list";
	
	/**
	 * Document-types for indexing as integers
	 */
	private int[] types;
	
	/**
	 * The directory to store the index files.
	 */
	private File indexDir;
	
	/**
	 * Parses <code>string</code>, which shoud be a comma or whitespace separated list of
	 * document types, and returns the types as an array of numerical codes. If
	 * <code>convertNamesToCodes</code> is <code>true</code>, the method expects the types as
	 * string names and converts them to numerical codes.
	 */
	protected int[] parseTypes (String string, boolean convertNamesToCodes)
	throws ProcessingException {
		StringTokenizer tokenizer = new StringTokenizer(string, ", \t\n");
		int[] types = new int[tokenizer.countTokens()];
		for (int i = 0; i < types.length; i++) {
			String token = tokenizer.nextToken();
			int type = (convertNamesToCodes ? DocType.codeFor(token) : Integer.parseInt(token));
			if (!DocType.exists(type) )
				throw new ProcessingException("Failed to parse document types (token = \"" + token + "\"):  Unknown document type code: " + type);
			types[i] = type;
		}
		return types;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.apache.cocoon.sitemap.SitemapModelComponent
	 * 		#setup(org.apache.cocoon.environment.SourceResolver, java.util.Map, 
	 * 		java.lang.String, org.apache.avalon.framework.parameters.Parameters)
	 */
	public void setup(SourceResolver resolver, Map objectModel, String src, Parameters par)
	throws SAXException, IOException, ProcessingException {
		final String METHOD_NAME = "setup";
		super.setup(resolver, objectModel, src, par);
		
		try {
			//set index directory
			String indexDirName = this.parameters.getParameter("index-dirname","WEB-INF/index");
			Context context = ObjectModelHelper.getContext(objectModel);
			String realPath = context.getRealPath("/");
			
			if(realPath != null) {
				this.indexDir = new File(realPath + indexDirName);
				if(!this.indexDir.exists())
					this.indexDir.mkdir();
				this.getLogger().debug(METHOD_NAME + " indexDir=" + this.indexDir);
			}
			
			this.lang = parameters.getParameter("lang","en");
			
			//set types for indexing
			if (this.parameters.isParameter(TYPE_NAMES) && ! this.parameters.getParameter(TYPE_NAMES).trim().equals(""))
				this.types = this.parseTypes(this.parameters.getParameter(TYPE_NAMES), true);
			else 
				throw new ProcessingException("Missing \"" + TYPE_NAMES + "\" parameter");
		}
		catch (ParameterException p) {
			throw new ProcessingException(p);
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
	
	/*
	 *  (non-Javadoc)
	 * @see org.apache.cocoon.generation.Generator#generate()
	 */
	public void generate() throws IOException, ProcessingException, SAXException {
		final String METHOD_NAME = "generate";
		
		Indexer indexer = null;
		SAXParser parser = null;
		FragmentSAXFilter fragSaxFilter = null;
		try {
			indexer = (Indexer) this.manager.lookup(Indexer.ROLE);
			this.getLogger().debug(METHOD_NAME + " Indexer class name: " + 
					indexer.getClass().getName() + "... starting indexing");
			indexer.setup(this.indexDir, this.lang);
			indexer.indexTypes(this.types);
			this.getLogger().debug(METHOD_NAME + " indexing finished");
			
			AttributesImpl attrs = new AttributesImpl();
			attrs.clear();
			
			this.contentHandler.startDocument();
			this.getLogger().debug(METHOD_NAME + " document started");
			
			int status = indexer.getStatus();
			
			this.contentHandler.startElement("", "html", "html", attrs);
			this.contentHandler.startElement("", "head", "head", attrs);			
			insertHtmlElement("title",attrs, HEAD_TITLE);
			this.contentHandler.endElement("", "head", "head");
			this.contentHandler.startElement("", "body", "body", attrs);

			insertHtmlElement("h1",attrs, PAGE_TITLE);
			insertHtmlElement("h2",attrs, SUMMARY_TITLE);
			insertHtmlElement("p",attrs, STATUS + ": " + indexer.getStatusMessage());
			if (status == 0 ||  status == 1) {
				insertHtmlElement("p",attrs, QUANTITY + ": " + new Integer(indexer.getNumberOfIndexedDocuments()).toString());
				insertHtmlElement("h2",attrs, LISTING_TITLE);				
			}
			// generates output RDF
			if (status == 0 || status == 1) {
				parser = (SAXParser) this.manager.lookup(SAXParser.ROLE);
				this.getLogger().debug(METHOD_NAME + " parser class name: " + parser.getClass().getName());
				fragSaxFilter = (FragmentSAXFilter) this.manager.lookup(FragmentSAXFilter.ROLE);
				this.getLogger().debug(METHOD_NAME + " fragSaxFilter class name: " + fragSaxFilter.getClass().getName());				
				fragSaxFilter.setContentHandler(this.contentHandler);
				
				this.getLogger().debug(METHOD_NAME + " start parsing");
				PipedInputStream pis = new PipedInputStream();
				this.getLogger().debug(METHOD_NAME + " PipedInputStream class name: " + pis.getClass().getName());
				indexer.streamRdf(pis);
				this.getLogger().debug(METHOD_NAME + " streaming started");
				parser.parse(new InputSource(pis), fragSaxFilter);
				this.getLogger().debug(METHOD_NAME + " finish parsing");				
				pis.close();
				this.getLogger().debug(METHOD_NAME + " PipedInputStream closed");
			}

			this.contentHandler.endElement("", "body", "body");			
			this.contentHandler.endElement("", "html", "html");
			this.contentHandler.endDocument();
			this.getLogger().debug(METHOD_NAME + " document finished");
		}
		catch (ServiceException s) {
			throw new ProcessingException(s);
		}
		finally {
			if (indexer != null) this.manager.release(indexer);
			if (parser != null) this.manager.release(parser);
			if (fragSaxFilter != null) this.manager.release(fragSaxFilter);
		}
	}
}
