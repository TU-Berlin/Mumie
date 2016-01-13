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
import java.io.PipedInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.Category;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.SearchField;
import net.mumie.cocoon.notions.Status;
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
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC_11;

/**
 * A component (in the sence of Avalon) that implements the 
 * functionalities of indexing described in the interface 
 * {@link net.mumie.cocoon.search.Indexer}. It uses
 * {@link net.mumie.cocoon.search.rdf.RdfUtilsImpl} to
 * generate RDF-datas describing the documents and to stream
 * the resulting RDF-model.
 * @author Helmut Vieritz
 * @version <span class="file">$Id: IndexerImpl.java,v 1.6 2006/11/03 10:25:19 rassy Exp $</span>
 */

public class IndexerImpl
extends AbstractLogEnabled
implements Serviceable, Recyclable, Indexer {
	
	/**
	 * Flag for existing index.
	 */
	private boolean existsIndex;
	
	/**
	 * External Avalon-component container
	 */
	private ServiceManager manager;
	
	/**
	 * <code>RdfUtilities</code> for creating RDF.
	 */
	private RdfUtils rdfUtils;
	
	/**
	 * Path to index directory
	 */
	private File indexDirPath;
	
	/**
	 * <span class="string">Lucene</span>-<code>directory</code> object to store the index. 
	 */
	private Directory indexDir;
	
	/**
	 * Instance of Lucene-<code>Analyzer</code>.
	 */
	private Analyzer analyzer;
	
	/**
	 * Instance of mumie-<code>DbHelper</code>.
	 */
	private DbHelper dbHelper;
	
	/**
	 * Status of indexing.
	 * @see net.mumie.cocoon.search.Indexer#getStatus()
	 */
	private int status = 0;
	
	/**
	 * Status message.
	 * @see net.mumie.cocoon.search.Indexer#getStatusMessage()
	 */
	private String statusMessage = null;
	
	/**
	 * Number of currently indexed documents.
	 * @see net.mumie.cocoon.search.Indexer#getNumberOfIndexedDocuments()
	 */
	private int numberOfIndexedDocuments = 0;
	
	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.component.Composable#compose(org.apache.avalon.framework.component.ComponentManager)
	 */
	public void service(ServiceManager manager) throws ServiceException {
		final String METHOD_NAME = "service";
		this.manager = manager;

		IndexConfiguration indexConfiguration = (IndexConfiguration) manager.lookup(IndexConfiguration.ROLE);
		this.getLogger().debug(METHOD_NAME + "got IndexConfiguration");
		this.existsIndex = indexConfiguration.existsIndex();
		if (this.existsIndex) {
			this.indexDir = null;
			setIndexDirPath(indexConfiguration.getIndexDir());
			setAnalyzer(indexConfiguration.getLang());
		}
		if (indexConfiguration != null) this.manager.release(indexConfiguration);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see net.mumie.cocoon.search.Indexer#setup(java.io.File, java.lang.String)
	 */
	public void setup (File indexDirPath, String lang)
	throws IOException, ServiceException {
		final String METHOD_NAME = "setup";
		
		this.status = 0;
		this.statusMessage = "No warning or error has occured.";
		this.numberOfIndexedDocuments = 0;
		
		if (!indexDirPath.exists())
			indexDirPath.mkdir();			
		else {
			if (!indexDirPath.isDirectory())
				throw new IOException("Index directory path is already used from a file.");
		}
		setIndexDirPath(indexDirPath);
		setAnalyzer(lang);
		
		this.rdfUtils = new RdfUtilsImpl();
		this.rdfUtils.setup(this.indexDirPath);
		this.rdfUtils.setLogger(this.getLogger());

		this.existsIndex = false;
		this.getLogger().debug(METHOD_NAME + " parameter indexDirPath is set to: " + this.indexDirPath.getAbsolutePath());
		this.getLogger().debug(METHOD_NAME + " parameter analyzer is set to: " + this.analyzer.getClass().getName());
		
		//TODO this is not thread-safe
		IndexConfiguration indexConfiguration = (IndexConfiguration) this.manager.lookup(IndexConfiguration.ROLE);
		HashMap map = new HashMap();
		map.put("index-dir", this.indexDirPath.getAbsolutePath());
		map.put("lang", lang);
		indexConfiguration.setParameters(map);
		if (indexConfiguration != null) this.manager.release(indexConfiguration);
		
		this.dbHelper = (DbHelper) manager.lookup(DbHelper.ROLE);
	}
	
	/* (non-Javadoc)
	 * @see org.apache.avalon.excalibur.pool.Recyclable#recycle()
	 */
	public void recycle() {
		if (this.dbHelper != null) this.manager.release(this.dbHelper);
		this.rdfUtils = null;
		this.status = 0;
		this.statusMessage = null;
		this.numberOfIndexedDocuments = 0;
	}
	
	/**
	 * @return Returns the analyzer.
	 */
	public Analyzer getAnalyzer() {
		return analyzer;
	}
	
	/**
	 * @param analyzer The analyzer to set.
	 */
	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}
	
	/**
	 * Set analyzer with language parameter.
	 * @param <span class="string">lang</span> can be 
	 * <span class="string">en</span> or <span class="string">de</span>.
	 */
	public void setAnalyzer (String lang) {
		if (lang.equals("en"))
			this.analyzer = new StandardAnalyzer();
		else if (lang.equals("de")) {
			this.analyzer =  new GermanEntityAnalyzer();
		}
		else {
			this.analyzer = new StandardAnalyzer();
			this.getLogger().error("Cannot assign analyzer for indexing. Choose StandardAnalyzer.");
		}
	}
	
	/**
	 * @return Returns the indexDirPath.
	 */
	public File getIndexDirPath() {
		return indexDirPath;
	}
	
	/**
	 * @param indexDirPath The indexDirPath to set.
	 */
	public void setIndexDirPath(File indexDirPath) {
		this.indexDirPath = indexDirPath;
	}
	
	/**
	 * @return Returns the indexDir.
	 */
	public Directory getIndexDir() {
		return this.indexDir;
	}
	
	/**
	 * @return Returns the rdfUtils.
	 */
	public RdfUtils getRdfUtils() {
		return rdfUtils;
	}
	
	/* (non-Javadoc)
	 * @see net.mumie.cocoon.search.Indexer#getStatus()
	 */
	public int getStatus() {
		return this.status;
	}

	/* (non-Javadoc)
	 * @see net.mumie.cocoon.search.Indexer#getStatusMessage()
	 */
	public String getStatusMessage() {
		return this.statusMessage;
	}

	/* (non-Javadoc)
	 * @see net.mumie.cocoon.search.Indexer#getNumberOfIndexedDocuments()
	 */
	public int getNumberOfIndexedDocuments() {
		return this.numberOfIndexedDocuments;
	}
	
	/**
	 * Creates a new Lucene-<code>document</code> from <code>ResultSet</code> resultSet.
	 * @param resultSet
	 * @param type
	 * @return A Lucene-<code>document</code>.
	 * @throws SQLException
	 * @throws SAXException
	 * @throws IOException
	 */
	private Document createLuceneDocument (ResultSet resultSet, int type) 
	throws IOException, SAXException, SQLException {
		final String METHOD_NAME = "createLuceneDocument";
		MumieContentXmlHandler mumieContentXmlHandler = new MumieContentXmlHandler();
		Document doc = new Document();
		
		doc.add(Field.Keyword(SearchField.TYPE, new Integer(type).toString()));
		
		doc.add(Field.Text(SearchField.TYPE_AS_STRING, 
				DocType.nameFor(type)));
		
		try {
			doc.add(Field.UnIndexed(SearchField.UNIQUE_IDENTIFIER, getUniqueIdentifier(type, resultSet.getString(DbColumn.ID))));
			doc.add(Field.Keyword(SearchField.ID, resultSet.getString(DbColumn.ID)));
		}
		catch (SQLException s) {
			this.getLogger().error(METHOD_NAME + " exception: " + s);	
		}
		
		try {
			doc.add(Field.Text(SearchField.DESCRIPTION,resultSet.getString(DbColumn.DESCRIPTION)));
		}
		catch (SQLException s) {
			this.getLogger().warn(METHOD_NAME + " exception: " + s);	
		}
		
		try {
			doc.add(Field.Text(SearchField.NAME,resultSet.getString(DbColumn.NAME)));
		}
		catch (SQLException s) {
			this.getLogger().warn(METHOD_NAME + " exception: " + s);	
		}
		
		try {
			doc.add(Field.Text(SearchField.CHANGELOG,resultSet.getString(DbColumn.CHANGELOG)));
		}
		catch (SQLException s) {
			this.getLogger().warn(METHOD_NAME + " exception: " + s);	
		}
		
		try {
			if (!DocType.isGeneric(type))
				doc.add(Field.Text(SearchField.STATUS,
						Status.nameFor(resultSet.getInt(DbColumn.STATUS))));
		}
		catch (SQLException s) {
			this.getLogger().warn(METHOD_NAME + " exception: " + s);	
		}
		
		try {
			if (DocType.hasCategory[type])
				doc.add(Field.Text(SearchField.CATEGORY,
						Category.nameFor(resultSet.getInt(DbColumn.CATEGORY))));
		}
		catch (SQLException s) {
			this.getLogger().warn(METHOD_NAME + " exception: " + s);	
		}
		
		if (type == DocType.ELEMENT || type == DocType.SUBELEMENT) {
			Resource resource = this.rdfUtils.addRessource(resultSet, type);
			String[] columns = {DbColumn.ID, DbColumn.CONTENT};
			ResultSet contentSet = this.dbHelper.queryData(type, resultSet.getInt(DbColumn.ID), columns);
			while (contentSet.next())
				mumieContentXmlHandler.parse(new InputSource(contentSet.getAsciiStream(DbColumn.CONTENT)), doc, resource);
		}
		
		this.numberOfIndexedDocuments++;
		return doc;
	}
	
	/**
	 * Adds a set of meta-infos as Lucene-<code>Document</code>-objects to index.
	 * @param resultSet
	 * @param type
	 * @param writer
	 * @throws SAXException
	 * @throws SQLException
	 * @throws IOException
	 */
	public void addDocuments (ResultSet resultSet, int type, IndexWriter writer)
	throws SAXException, SQLException, IOException {
		final String METHOD_NAME = "addDocuments";
		if (this.indexDir == null)
			this.indexDir = FSDirectory.getDirectory(this.indexDirPath, false);
		if (!(resultSet == null)) {
			resultSet.beforeFirst();
			while (resultSet.next()) {
				Document doc = createLuceneDocument(resultSet, type);
				writer.addDocument(doc);
				this.rdfUtils.addRessource(resultSet, type);
			}
		}
		else
			this.getLogger().info(METHOD_NAME + " Empty ResultSet");
	}
	
	/*
	 *  (non-Javadoc)
	 * @see net.mumie.cocoon.search.Indexer#addDocument(int, int)
	 */
	public void addDocument (int type, int id) {
		final String METHOD_NAME = "addDocument";
		try {
			if (this.indexDir == null)
				this.indexDir = FSDirectory.getDirectory(this.indexDirPath, false);
			IndexWriter writer = new IndexWriter(this.indexDir, this.analyzer, false);
			addDocuments(this.dbHelper.queryData(type, id), type, writer);
			writer.optimize();
			writer.close();
		}
		catch (IOException i) {
			this.getLogger().error(METHOD_NAME + " exception: " + i);
		}
		catch (SQLException q) {
			this.getLogger().error(METHOD_NAME + " exception: " + q);
		}
		catch (SAXException s) {
			this.getLogger().error(METHOD_NAME + " exception: " + s);
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see net.mumie.cocoon.search.Indexer#updateDocument(int, int, int)
	 */
	public void updateDocument (int type, int id, int previousId) {
		final String METHOD_NAME = "updateDocument";
		if (this.existsIndex) {
			try {
				if (this.indexDir == null)
					this.indexDir = FSDirectory.getDirectory(this.indexDirPath, false);				
				this.rdfUtils.getModelFromFile();
				IndexReader reader = IndexReader.open(this.indexDir);
				deleteDocument(type, previousId, reader);
				reader.close();
				addDocument(type, id);
				this.rdfUtils.writeModelToFile();
			}
			catch (IOException i) {
				this.getLogger().error(METHOD_NAME + " exception: " + i);
			}
		}
		else
			this.getLogger().warn(METHOD_NAME + " Could not find index to change.");
	}
	
	/**
	 * Updates a set of meta-infos from same type as Lucene-<code>Document</code>-objects in index. 
	 * Update includes documents with same JAPS-<code>type</code> and same JAPS-<code>Id</code>.
	 * @throws IOException
	 * @throws SQLException
	 * @throws SAXException
	 */
	private void updateDocuments (ResultSet resultSet, int type)
	throws IOException, SAXException, SQLException {
		if (this.indexDir == null)
			this.indexDir = FSDirectory.getDirectory(this.indexDirPath, false);
		if (!(resultSet == null)) {
			this.rdfUtils.getModelFromFile();
			IndexReader reader = IndexReader.open(this.indexDir);
			resultSet.beforeFirst();
			while (resultSet.next())
				deleteDocument(type, resultSet.getInt(DbColumn.ID), reader);
			reader.close();
			IndexWriter writer = new IndexWriter(this.indexDir, this.analyzer, false);
			addDocuments(resultSet, type, writer);
			writer.optimize();
			writer.close();
			this.rdfUtils.writeModelToFile();
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see net.mumie.cocoon.search.Indexer#deleteDocument(int, int)
	 */
	public void deleteDocument (int type, int id) {
		final String METHOD_NAME = "deleteDocument";
		try {
			if (this.indexDir == null)
				this.indexDir = FSDirectory.getDirectory(this.indexDirPath, false);			
			IndexReader reader = IndexReader.open(this.indexDir);
			deleteDocument(type, id, reader);
			reader.close();
		}
		catch (IOException i) {
			this.getLogger().error(METHOD_NAME + " exception: " + i);
		}
	}
	
	/**
	 * Deletes one document from index.
	 * @param type
	 * @param id
	 * @param reader
	 * @throws IOException
	 * @throws SQLException
	 */
	private void deleteDocument (int type, int id, IndexReader reader)
	throws IOException {
		reader.delete(new Term(SearchField.UNIQUE_IDENTIFIER, getUniqueIdentifier(type, id)));
		this.rdfUtils.deleteResource(type, id);
	}
	
	/**
	 * Creates a new Index and adds all documents of different types to index.
	 * @param types
	 * @param onlyLatest
	 * @param writer
	 * @throws IOException
	 * @throws SQLException
	 * @throws SAXException
	 */
	public void indexTypes (int[] types) {
		final String METHOD_NAME = "indexTypes";
		this.getLogger().debug(METHOD_NAME + " started");
		try {
			if (this.indexDir == null)
				this.indexDir = FSDirectory.getDirectory(this.indexDirPath, true);			
			IndexWriter writer = new IndexWriter(this.indexDir, this.analyzer, true);
			for (int i = 0; i < types.length; i++) {
				try {
					ResultSet resultSet = this.dbHelper.queryDocuments(types[i], true);
					addDocuments(resultSet, types[i], writer);
				}
				catch (SQLException q) {
					this.getLogger().error(METHOD_NAME + " exception: " + q);
				}
				catch (SAXException s) {
					this.getLogger().error(METHOD_NAME + " exception: " + s);
				}
			}
			writer.optimize();
			writer.close();
			this.rdfUtils.writeModelToFile();
		} 
		catch (IOException i) {
			this.getLogger().error(METHOD_NAME + " exception: " + i); 
		}
	}
	
	/**
	 * Creates a unique identifier from type and id.
	 * @param type
	 * @param id
	 * @return
	 */
	private String getUniqueIdentifier (int type, int id) {
		String s = type + "_" + id;
		return s;
	}
	
	/**
	 * Creates a unique identifier from type and id.
	 * @param type
	 * @param id
	 * @return
	 */
	private String getUniqueIdentifier (int type, String id) {
		String s = type + "_" + id;
		return s;
	}
	
	/**
	 * Parses type from unique identifier.
	 * @param identifier
	 * @return
	 */
	private int getTypeFromUniqueIdentifier (String identifier) {
		int type = -1;
		String[] s = identifier.split("_");		
		if (s.length > 0 && s.length < 3)
			type = Integer.parseInt(s[0]);
		return type;
	}
	
	/**
	 * Parses id from unique identifier.
	 * @param identifier
	 * @return
	 */
	private int getIdFromUniqueIdentifier (String identifier) {
		int id = -1;
		String[] s = identifier.split("_");		
		if (s.length > 0 && s.length < 3)
			id = Integer.parseInt(s[1]);
		return id;
	}
	
	/* (non-Javadoc)
	 * @see net.mumie.cocoon.search.Indexer#getRdf()
	 */
	public void streamRdf(PipedInputStream pis) {
		final String METHOD_NAME = "streamRdf";
		try {
			this.rdfUtils.setPos(pis, this.getLogger());
		}
		catch (IOException i) {
			this.getLogger().error(METHOD_NAME + ": " + i.getMessage());
		}
		this.getLogger().debug(METHOD_NAME + " starting streaming Thread");
		this.rdfUtils.start();
		this.getLogger().debug(METHOD_NAME + " streaming Thread started");		
	}
	
	class MumieContentXmlHandler implements ContentHandler {
		
		/**
		 * <code>org.xml.sax.Locator</code>-instance
		 */
		private Locator locator = null;
		
		/**
		 * <code>Resource</code> to append a <code>Property</code>.
		 */
		private Resource resource;
		
		/**
		 * Lucene-<code>Document</code>
		 */
		private Document doc;
		
		/**
		 * SAX-XML reader
		 */
		private XMLReader reader;		
		
		/**
		 * Stores text nodes.
		 */
		private StringBuffer stringBuffer = null;
		
		/**
		 * Flag for \<defnotion\>-Element.
		 */
		private boolean isMMDefnotion = false;
		
		/**
		 * Flag for \<title\>-Element.
		 */
		private boolean isMMTitle = false;
		
		/**
		 * Flag for \<notion\>-Element.
		 */
		private boolean isMMNotion = false;
		
		/**
		 * Flag for \<paragraph\>-Element.
		 */
		private boolean isMMParagraph = false;
		
		/**
		 * Flag for \<math\>-Element.
		 */
		private boolean isMMMath = false;
		
		/**
		 * Constructor
		 * @throws SAXException
		 */
		private MumieContentXmlHandler()
		throws SAXException {
			this.reader = XMLReaderFactory.createXMLReader();
			this.reader.setContentHandler(this);
		}
		
		/**
		 * @return Returns the doc.
		 */
		Document getDoc() {
			return doc;
		}
		
		/**
		 * @param doc The doc to set.
		 */
		void setDoc(Document doc) {
			this.doc = doc;
		}
		
		/**
		 * @return Returns the locator.
		 */
		Locator getLocator() {
			return locator;
		}
		
		/**
		 * @param locator The locator to set.
		 */
		public void setDocumentLocator(Locator locator)	{
			this.locator = locator;
		}
		
		/**
		 * Parses Content-XML
		 * @param ips
		 * @param doc
		 * @param resource
		 * @throws IOException
		 * @throws SAXException
		 */
		void parse (InputSource ips, Document doc, Resource resource)
		throws IOException, SAXException {
			this.resource = resource;
			this.doc = doc;
			this.reader.parse(ips);
		}
		
		/*
		 *  (non-Javadoc)
		 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
		 */
		public void startElement(String nameSpaceURI, String localName, String qName, Attributes atts)
		throws SAXException {
			
			if (nameSpaceURI.equals(XmlNotions.CONTENT_NAMESPACE_URI)) {
				if (localName.equals(XmlNotions.DEFNOTION)) {
					this.isMMDefnotion = true;
				}
				else if (localName.equals(XmlNotions.TITLE)) {
					this.isMMTitle = true;
				}
				else if (localName.equals(XmlNotions.NOTION)) {
					this.isMMNotion = true;
				}
				else if (localName.equals(XmlNotions.PARAGRAPH)) {
					this.isMMParagraph = true;
					this.stringBuffer = new StringBuffer();
				}
				else if (localName.equals(XmlNotions.MATH)) {
					this.isMMMath = true;
				}			
			}
		}
		
		/*
		 *  (non-Javadoc)
		 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
		 */
		public void endElement(String nameSpaceURI, String localName, String qName)
		throws SAXException	{
			if (nameSpaceURI.equals(XmlNotions.CONTENT_NAMESPACE_URI))	{
				if (localName.equals(XmlNotions.DEFNOTION)) {
					this.isMMDefnotion = false;
				}
				else if (localName.equals(XmlNotions.TITLE)) {
					this.isMMTitle = false;
				}
				else if (localName.equals(XmlNotions.NOTION)) {
					this.isMMNotion = false;
				}
				else if (localName.equals(XmlNotions.PARAGRAPH)) {
					this.isMMParagraph = false;
					doc.add(Field.Text(XmlNotions.PARAGRAPH, this.stringBuffer.toString()));
				}
				else if (localName.equals(XmlNotions.MATH)) {
					this.isMMMath = false;
				}
			}
		}
		
		/*
		 *  (non-Javadoc)
		 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
		 */
		public void characters(char[] ch, int start, int length)
		throws SAXException	{
			String s = new String(ch, start, length);
			if (isMMDefnotion) {
				doc.add(Field.Text(XmlNotions.DEFNOTION, s));
				rdfUtils.addProperty(this.resource, DC_11.title, s);				
			}
			else if (isMMTitle) {
				doc.add(Field.Text(XmlNotions.TITLE, s));
				rdfUtils.addProperty(this.resource, DC_11.title, s);
			}
			else if (isMMNotion) {
				doc.add(Field.Text(XmlNotions.NOTION, s));
			}
			else if (isMMParagraph) {
				if (! this.isMMMath)
					this.stringBuffer.append(s);
			}	
		}
		
		/*
		 *  (non-Javadoc)
		 * @see org.xml.sax.ContentHandler#startDocument()
		 */
		public void startDocument()
		throws SAXException	{
		}
		
		/*
		 *  (non-Javadoc)
		 * @see org.xml.sax.ContentHandler#endDocument()
		 */
		public void endDocument()
		throws SAXException	{
		}
		
		/*
		 *  (non-Javadoc)
		 * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)
		 */
		public void startPrefixMapping(String prefix, String uri)
		throws SAXException	{
		}
		
		/*
		 *  (non-Javadoc)
		 * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
		 */
		public void endPrefixMapping(String prefix)
		throws SAXException	{
		}
		
		/*
		 *  (non-Javadoc)
		 * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
		 */
		public void ignorableWhitespace(char[] ch, int start, int length)
		throws SAXException	{
		}
		
		/*
		 *  (non-Javadoc)
		 * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String)
		 */
		public void processingInstruction(String target, String data)
		throws SAXException	{
		}
		
		/*
		 *  (non-Javadoc)
		 * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
		 */
		public void skippedEntity(String name)
		throws SAXException	{
		}
	}

}
