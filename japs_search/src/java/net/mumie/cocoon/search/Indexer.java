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

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.service.ServiceException;


/**
 * @author vieritz
 */
public interface Indexer extends Component {
	
	/**
	 * Role of implementing classes as an Avalon component. Value is
	 * <span class="string">"net.mumie.cocoon.search.Indexer"</span>.
	 */
	public static final String ROLE = Indexer.class.getName();
	
	/**
	 * Signalizes if any message to report occurs.
	 * 0: normal
	 * 1: info
	 * 2: error
	 */
	public int getStatus ();
	
	/**
	 * Message to report.
	 */
	public String getStatusMessage ();
	
	/**
	 * Number of indexed elements
	 */
	public int getNumberOfIndexedDocuments ();
	
	/**
	 * Creates a new index with all Mumie documents of type <span class="string">docTypes</span>.
	 * @param docTypes
	 */
	public void indexTypes (int[] docTypes);
	
	/**
	 * Adds one Mumie document to index. 
	 * @param docType Mumie document type
	 * @param id Mumie document ID
	 */
	public void addDocument (int docType, int id);
	
	/**
	 * Updates one Mumie document in the index.
	 * @param docType
	 * @param id
	 * @param previousId
	 */
	public void updateDocument (int docType, int id, int previousId);
	
	/**
	 * Deletes one Mumie document from index.
	 * @param docType
	 * @param id
	 */
	public void deleteDocument (int docType, int id);
	
	/**
	 * Sets the index parameters externally.
	 * Use it to create a new index.
	 * @param indexDirPath Path to index directory relative from $COCOON_HOME.
	 * @param lang <span class="string">en</span> or <span class="string">de</span>.
	 * @throws IOException
	 * @throws ServiceException
	 */
	public void setup (File indexDir, String lang)
	throws IOException, ServiceException;
	
	/**
	 * Starts a thread to stream the <span class="String">JENA-RDF-</span><code>Model</code>.
	 * @param <code>java.io.PipedInputStream</code>
	 */
	public void streamRdf(PipedInputStream pis);
	
}
