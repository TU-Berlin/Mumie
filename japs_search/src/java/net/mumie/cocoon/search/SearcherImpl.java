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

import java.io.IOException;
import java.io.PipedInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.SearchField;
import net.mumie.cocoon.search.rdf.RdfUtils;
import net.mumie.cocoon.search.rdf.RdfUtilsImpl;

import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.lucene.analysis.Analyzer;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC_11;

/**
 * @author vieritz
 */
public class SearcherImpl
extends AbstractLogEnabled
implements Serviceable, Searcher, Recyclable  {

	/**
	 * External Avalon-component container
	 */
	private ServiceManager manager;
	
	/**
	 * Instance of Lucene<code>Analyzer</code>. Must be the same like this one which was used for indexing.  
	 */
	private Analyzer analyzer;
	
	/**
	 * <span class="string">Lucene</span>-<code>directory</code> object to store the index. 
	 */
	private Directory indexDir;
	
	/**
	 * <code>RdfUtilities</code> for creating RDF.
	 */
	private RdfUtils rdfUtils;
	
	/**
	 * Number of founded documents.
	 */
	private int hitsCounter = 0;
	
	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
	 */
	public void service(ServiceManager manager)
	throws ServiceException {
		final String METHOD_NAME = "service";
		this.manager = manager;
		this.getLogger().debug(METHOD_NAME + " : started");
		IndexConfiguration indexConfiguration = (IndexConfiguration) manager.lookup(IndexConfiguration.ROLE);
		if (indexConfiguration.existsIndex()) {
			try {
				this.indexDir = FSDirectory.getDirectory(indexConfiguration.getIndexDir(), false);
				setAnalyzer(indexConfiguration.getLang());
			}
			catch (IOException i) {
				throw new ServiceException(Searcher.ROLE, "Could not set Directory indexDir");
			}
		}
		else {
			throw new ServiceException(Searcher.ROLE, "Cannot find index");
		}
		if (indexConfiguration != null)
			this.manager.release(indexConfiguration);
		this.getLogger().debug(METHOD_NAME + " : finished");
	}
	
	/* (non-Javadoc)
	 * @see net.mumie.cocoon.search.Searcher#setup()
	 */
	public void setup() throws ServiceException {
		this.rdfUtils = new RdfUtilsImpl();
		this.rdfUtils.setup();
	}
	
	/* (non-Javadoc)
	 * @see org.apache.avalon.excalibur.pool.Recyclable#recycle()
	 */
	public void recycle() {
		this.rdfUtils = null;
		this.hitsCounter = 0;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see net.mumie.cocoon.search.Searcher#search(java.lang.String, java.lang.String)
	 */
	public Model search (String searchString, String searchField) {
		final String METHOD_NAME = "search";
		DbHelper dbHelper = null;
		
		try {
			dbHelper = (DbHelper) this.manager.lookup(DbHelper.ROLE);			
			Query query = QueryParser.parse(searchString, searchField, this.analyzer);
			this.getLogger().debug("Query: " + query.toString());
			IndexSearcher searcher = new IndexSearcher(this.indexDir);
			Hits hits = searcher.search(query);
			this.getLogger().debug(METHOD_NAME + " " + hits.length() + " hits found");
			
			if (hits.length() != 0) {
				this.hitsCounter = hits.length();
				Document doc;
				for (int i = 0; i < hits.length(); i++) {
					doc = hits.doc(i);
					Resource res = this.rdfUtils.addRessource(doc);
					
					ResultSet resultSet = dbHelper.queryDatum(
							Integer.parseInt(doc.get(SearchField.TYPE)), Integer.parseInt(doc.get(SearchField.ID)), DbColumn.DESCRIPTION);
					
					while (resultSet.next())
						res.addProperty(DC_11.description, resultSet.getString(DbColumn.DESCRIPTION));
				}
			}
		}
		catch (IOException i) {
			this.getLogger().error(METHOD_NAME + "IOException");
		}
		catch (SQLException s) {
			this.getLogger().error(METHOD_NAME + "SQLException");		
		}
		catch (ServiceException s) {
			this.getLogger().error(METHOD_NAME + "ServiceException");
		}
		catch (ParseException p) {
			this.getLogger().error(METHOD_NAME + "ParseException");
		}
		finally {
			if (dbHelper != null)
				this.manager.release(dbHelper);
		}
		return this.rdfUtils.getModel(); 
	}
	
	/**
	 * Set analyzer with language parameter.
	 * @param <span class="string">lang</span> can be 
	 * <span class="string">en</span> or <span class="string">de</span>.
	 */
	public void setAnalyzer (String lang) {
		if (lang.equals("en"))
			this.analyzer = new StandardAnalyzer();
		else if (lang.equals("de"))
			this.analyzer = new GermanEntityAnalyzer();
		else {
			this.analyzer = new StandardAnalyzer();
			this.getLogger().error("Cannot assign analyzer for indexing.");
		}
	}
	
	/* (non-Javadoc)
	 * @see net.mumie.cocoon.search.Indexer#getRdf()
	 */
	public void streamRdf(PipedInputStream pis) throws IOException {
		final String METHOD_NAME = "streamRdf";
		this.rdfUtils.setPos(pis, this.getLogger());
		this.getLogger().debug(METHOD_NAME + " starting streaming Thread");
		this.rdfUtils.start();
		this.getLogger().debug(METHOD_NAME + " streaming Thread started");	
	}
	
	/* (non-Javadoc)
	 * @see net.mumie.cocoon.search.Searcher#getNumberOfHits()
	 */
	public int getNumberOfHits() {
		return this.hitsCounter;
	}
	
}
