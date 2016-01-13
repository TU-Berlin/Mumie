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

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.service.ServiceException;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * @author vieritz
 */
public interface Searcher extends Component {
	
	/**
	 * Role of implementing classes as an Avalon component. Value is
	 * <span class="string">"net.mumie.cocoon.search.Indexer"</span>.
	 */
	String ROLE = Searcher.class.getName();
	
	/**
	 * Number of hits
	 */
	public int getNumberOfHits ();
	
	/**
	 * Searches the Lucene-index with given <code>searchString</code> at the given <code>searchfield</code>.
	 * @param searchString to search for
	 * @param searchField Field where to search for <span class="string">searchString</span>.
	 * @return RDF-model of hits
	 */
	public Model search (String searchString, String searchField);
	
	/**
	 * Starts a thread to stream the <span class="String">JENA-RDF-</span><code>Model</code>.
	 * @param <code>java.io.PipedInputStream</code>
	 * @throws IOException
	 */
	public void streamRdf(PipedInputStream pis) throws IOException;
	
	/**
	 * Sets up the searcher instance.
	 * @throws ServiceException
	 */
	public void setup () throws ServiceException;
	
}
