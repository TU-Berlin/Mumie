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

package net.mumie.cocoon.search.rdf;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.avalon.framework.logger.Logger;
import org.apache.lucene.document.Document;

import net.mumie.cocoon.notions.Category;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.SearchField;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DC_11;

public class RdfUtilsImpl extends Thread 
implements RdfUtils {
	
	/**
	 * File name to store index RDF-data.
	 */
	public final static String RDF_FILE_NAME = "index.rdf";
	
	/**
	 * Jena-<code>com.hp.hpl.jena.rdf.model.Model</code>.
	 */
	public Model model;
	
	/**
	 * File to store RDF data.
	 */
	private File rdfFile;
	
	/**
	 * <code>PipedOutputStream</code> to write the RDF-<code>com.hp.hpl.jena.rdf.model.Model</code> out.
	 */
	private PipedOutputStream pos;
	
	private Logger logger;
	
	/**
	 * setup for use without RDF-file. 
	 */
	public void setup () {
		this.rdfFile = null;
		this.model = ModelFactory.createDefaultModel();
		this.logger = null;
	}
	
	/**
	 * setup for use with RDF-file.
	 * @param rdfFilePath path to RDF-file.
	 * @param loadRdfFile Flag if an existing file should be loaded.
	 */
	public void setup(File rdfFilePath, boolean loadRdfFile) 
	throws FileNotFoundException {
		this.rdfFile = new File(rdfFilePath + File.separator + RDF_FILE_NAME);
		this.model = ModelFactory.createDefaultModel();
		if (loadRdfFile) {
			this.model.read(new BufferedInputStream(new FileInputStream(this.rdfFile)), null);
		}
		this.logger = null;
	}
	
	/**
	 * setup for use with RDF-file.
	 * @param rdfFilePath path to RDF-file.
	 * @param loadRdfFile Flag if an existing file should be loaded.
	 * @throws FileNotFoundException
	 */
	public void setup(String rdfFilePathName, boolean loadRdfFile)
	throws FileNotFoundException {
		setup(new File(rdfFilePathName), loadRdfFile);
	}
	
	/**
	 * setup for use with RDF-file.
	 * An existing file not be loaded.
	 * @param rdfFilePath path to RDF-file.
	 * @throws FileNotFoundException
	 */
	public void setup(File rdfFilePath) throws FileNotFoundException {
		setup(rdfFilePath, false);
	}	
	
	/**
	 * setup for use with RDF-file.
	 * An existing file not be loaded.
	 * @param rdfFilePath path to RDF-file.
	 * @throws FileNotFoundException
	 */
	public void setup(String rdfFilePathName) throws FileNotFoundException {
		setup(new File(rdfFilePathName), false);
	}
	
	/**
	 * Set logger for logging. This will changed one day when class becomes component.
	 * @param logger
	 */
	public void setLogger (Logger logger) {
		//TODO change class to avalon component
		this.logger = logger;
	}

	/* (non-Javadoc)
	 * @see net.mumie.cocoon.search.rdf.RdfUtils#getModel()
	 */
	public Model getModel() {
		return this.model;
	}
		
	/**
	 * Adds a <code>Resource<code> to <code>com.hp.hpl.jena.rdf.model.Model</code>.
	 * It will add only the first entry of the <code>ResultSet</code>.
	 * @param resultSet
	 * @param type
	 * @return A <span class=string">JENA-RDF</span>-<code>com.hp.hpl.jena.rdf.model.Model</code> <code>Resource</span>
	 * or <code>null</code> if there is an error.
	 */
	public Resource addRessource (ResultSet resultSet, int type) {
		final String METHOD_NAME = "addRessource";
		Resource res = null;
		try {
			res = this.model.createResource(createResourceName(type, resultSet.getInt(DbColumn.ID)));
			res.addProperty(DC_11.description, resultSet.getString(DbColumn.DESCRIPTION));
			res.addProperty(DC_11.identifier, resultSet.getString(DbColumn.ID));
			res.addProperty(DC_11.coverage, DocType.nameFor[type]);
			if (DocType.hasCategory[type])
				res.addProperty(DC_11.type, Category.nameFor(resultSet.getInt(DbColumn.CATEGORY)));
		}
		catch (SQLException s) {
			if (this.logger != null)
				this.logger.warn(METHOD_NAME + ": " + s.getMessage());
		}
		return res;
	}
	
	/**
	 * Adds a <code>Resource<code> to <code>com.hp.hpl.jena.rdf.model.Model</code>.
	 * @param doc <code>org.apache.lucene.document.Document</code>
	 * @return A <span class=string">JENA-RDF</span>-<code>com.hp.hpl.jena.rdf.model.Model</code> <code>Resource</span>.
	 */
	public Resource addRessource (Document doc) {
		Resource res = this.model.createResource(createResourceName(Integer.parseInt(doc.get(SearchField.TYPE)), Integer.parseInt(doc.get(SearchField.ID))));
		res.addProperty(DC_11.identifier, doc.get(SearchField.ID));
		int type = Integer.parseInt(doc.get(SearchField.TYPE));
		String typeName = DocType.nameFor[type];
		// Mumie type <--> DC coverage
		// Mumie category <--> DC type
		res.addProperty(DC_11.coverage, typeName);
		if (DocType.hasCategory[type])
			res.addProperty(DC_11.type, doc.get(SearchField.CATEGORY));
		res.addProperty(DC_11.title, doc.get(SearchField.NAME));
		return res;
	}
	
	/**
	 * Deletes a RDF <code>Resource</code>.
	 * @param type
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 */
	public void deleteResource (int type, int id) {
		String rn = createResourceName(type, id);
		Model newModel = ModelFactory.createDefaultModel();
		ResIterator resIterator = this.model.listSubjectsWithProperty(DC_11.identifier);
		while (resIterator.hasNext()) { 
			Resource res = resIterator.nextResource();
			if (!res.hasURI(rn)) {
				Resource res1 = newModel.createResource(res.getURI());
				StmtIterator stmIterator = res.listProperties();
				while (stmIterator.hasNext()) {
					Statement st = stmIterator.nextStatement();
					res1.addProperty(st.getPredicate(),st.getString());
				}
			}
		}
		this.model = newModel;
	}
	
	/**
	 * Creates the name <code>String</code> of a rersource
	 * @param type
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 */
	private String createResourceName (int type, int id) {
		return RdfNotions.MUMIE_RDF_PREFIX + DocType.nameFor[type] + "/"+ id;
	}
	
	/**
	 * Adds one <code>Property</code> to <code>Resource</code>.
	 * @param res
	 * @param property
	 * @param value
	 * @return
	 */
	public Resource addProperty (Resource res, Property property, String value) {
		res.addProperty(property, value);
		return res;
	}
	
	/**
	 * Writes out the RDF file.
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	public Model writeModelToFile ()
	throws FileNotFoundException {
		OutputStream ops = new BufferedOutputStream(new FileOutputStream(this.rdfFile));
		this.model.write(ops);
		return this.model;
	}
	
	/**
	 * Loads RDF-statements to a <code>Model</code> object.
	 * @return A JENA <code>Model</code> object.
	 * @throws FileNotFoundException
	 */
	public Model getModelFromFile()
	throws FileNotFoundException {
		Model model = ModelFactory.createDefaultModel();
		model.read(new BufferedInputStream(new FileInputStream(this.rdfFile)), null);
		return model;
	}
	
	/**
	 * Sets the <code>PipedInputStream</code> to write the RDF-<code>Model</code> therein.
	 * @param pis
	 * @throws IOException
	 */
	public void setPos (PipedInputStream pis, Logger logger) throws IOException {
		this.pos = new PipedOutputStream(pis);
		this.logger = logger;
	}
	
	/**
	 * Method to stream the RDF-<code>Model</code> with a PipedStream.
	 */
	public void run () {
		final String METHOD_NAME = "run";
		this.logger.debug(METHOD_NAME + ": starting streaming");
		this.model.write(pos);
		this.logger.debug(METHOD_NAME + ": streaming finished");		
		try {
			this.pos.flush();
			this.pos.close();
		} catch (IOException e) {
			this.logger.error(METHOD_NAME + ": " + e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see net.mumie.cocoon.search.rdf.RdfUtils#setPis(java.io.PipedInputStream)
	 */
	public void setPos(PipedInputStream pis) throws IOException {
		this.pos = new PipedOutputStream(pis);		
	}

}
