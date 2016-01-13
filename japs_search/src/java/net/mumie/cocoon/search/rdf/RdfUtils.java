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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PipedInputStream;
import java.sql.ResultSet;

import org.apache.avalon.framework.logger.Logger;
import org.apache.lucene.document.Document;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Combines all functionalities to generate and stream RDF data 
 * used by the search engine. 
 * @author vieritz
 */
public interface RdfUtils {
	
	/**
	 * Role of implementing classes as an Avalon component. Value is
	 * <span class="string">"net.mumie.cocoon.search.Indexer"</span>.
	 */
	public static final String ROLE = RdfUtils.class.getName();
	
	/**
	 * setup for use without RDF-file. 
	 */
	public void setup ();
	
	/**
	 * setup for use with RDF-file.
	 * @param rdfFilePath path to RDF-file.
	 * @param loadRdfFile Flag if an existing file should be loaded.
	 * @throws FileNotFoundException
	 */
	public void setup(File rdfFilePath, boolean loadRdfFile)
	throws FileNotFoundException;
	
	/**
	 * setup for use with RDF-file.
	 * @param rdfFilePath path to RDF-file.
	 * @param loadRdfFile Flag if an existing file should be loaded.
	 * @throws FileNotFoundException
	 */
	public void setup(String rdfFilePathName, boolean loadRdfFile)
	throws FileNotFoundException;
	
	/**
	 * setup for use with RDF-file.
	 * An existing file not be loaded.
	 * @param rdfFilePath path to RDF-file.
	 * @throws FileNotFoundException
	 */
	public void setup(File rdfFilePath) throws FileNotFoundException;	
	
	/**
	 * setup for use with RDF-file.
	 * An existing file not be loaded.
	 * @param rdfFilePath path to RDF-file.
	 * @throws FileNotFoundException
	 */
	public void setup(String rdfFilePathName) throws FileNotFoundException;
	
	/**
	 * Adds a <code>Resource<code> to <code>com.hp.hpl.jena.rdf.model.Model</code>.
	 * It will add only the first entry of the <code>ResultSet</code>.
	 * @param resultSet
	 * @param type
	 * @return A <span class=string">JENA-RDF</span>-<code>com.hp.hpl.jena.rdf.model.Model</code> <code>Resource</span>
	 * or <code>null</code> if there is an error.
	 */
	public abstract Resource addRessource(ResultSet resultSet, int type);

	/**
	 * Adds a <code>Resource<code> to <code>com.hp.hpl.jena.rdf.model.Model</code>.
	 * @param doc <code>org.apache.lucene.document.Document</code>
	 * @return A <span class=string">JENA-RDF</span>-<code>com.hp.hpl.jena.rdf.model.Model</code> <code>Resource</span>.
	 */
	public abstract Resource addRessource(Document doc);

	/**
	 * Deletes a RDF <code>Resource</code>.
	 * @param type
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 */
	public abstract void deleteResource(int type, int id);

	/**
	 * Adds one <code>Property</code> to <code>Resource</code>.
	 * @param res
	 * @param property
	 * @param value
	 * @return
	 */
	public abstract Resource addProperty(Resource res, Property property,
			String value);

	/**
	 * Sets the <code>PipedOutputStream</code> to write the RDF-<code>Model</code> thereout.
	 * @param pis
	 * @throws IOException
	 * @throws IOException
	 */
	public abstract void setPos(PipedInputStream pis) throws IOException;
	
	/**
	 * Sets the <code>PipedOutputStream</code> to write the RDF-<code>Model</code> thereout.
	 * @param pis
	 * @param logger
	 * @throws IOException
	 */
	public abstract void setPos(PipedInputStream pis, Logger logger)
	throws IOException;	
	
	/**
	 * Writes out the RDF file.
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	public Model writeModelToFile ()
	throws FileNotFoundException;
	
	/**
	 * Loads RDF-statements to a <code>Model</code> object.
	 * @return A JENA <code>Model</code> object.
	 * @throws FileNotFoundException
	 */
	public Model getModelFromFile()
	throws FileNotFoundException;
	
	/**
	 * @return A JENA <code>Model</code> object.
	 */
	public Model getModel ();

	/**
	 * Method to stream the RDF-<code>Model</code> by a PipedStream.
	 */
	public abstract void start();
	
	/**
	 * Sets logger until class becomes Avalon component
	 * @param logger
	 */
	public void setLogger (Logger logger);
}
