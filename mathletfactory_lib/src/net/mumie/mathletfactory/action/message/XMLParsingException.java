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

package net.mumie.mathletfactory.action.message;

import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Node;

/**
 * An exception that indicates, that an error occured while trying to parse
 * XML/MathML encoded content. This exception serves as base class for all
 * xml parsing errors.
 *
 * @author gronau
 * @mm.docstatus finished
 */
public class XMLParsingException extends RuntimeException {

  public final static String DEFAULT_MESSAGE = "An error occurred while trying to parse XML content";
  
  private Node m_errorNode;

    /** Creates this exception with the a default message. */
    public XMLParsingException() {
      super(DEFAULT_MESSAGE);
    }
    
    /** Creates this exception with the given message. */
    public XMLParsingException(String message) {
      super(message);
    }
    
    /** Creates this exception for the given XML node and with the given message. */
    public XMLParsingException(String message, Node XMLnode) {
      super(message + "; node content: " + XMLUtils.nodeToString(XMLnode, true));
    }

    /** Creates this exception for the given XML node and the given throwable. */
    public XMLParsingException(String message, Throwable cause) {
      super(message, cause);
    }
    
    /** Creates this exception for the given XML node. */
    public XMLParsingException(Node XMLnode) {
      super(DEFAULT_MESSAGE + "; node content: " + XMLUtils.nodeToString(XMLnode, true));
      m_errorNode = XMLnode;
    }

    /** Creates this exception for the given XML node and the given throwable. */
    public XMLParsingException(Node mathMLnode, Throwable cause) {
      super(DEFAULT_MESSAGE + ": \n" + XMLUtils.nodeToString(mathMLnode, true), cause);
    }
    
    /**
     * Returns the XML node which caused this exception.
     */
    public Node getErrorNode() {
    	return m_errorNode;
    }
}
