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

package net.mumie.cocoon.xml;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.apache.cocoon.xml.AbstractXMLPipe;

/**
 * A {@link SAXFilter} that ignores start and end document events, and passes all other
 * events unchanged to the content handler.
 *
 * @author Tilman Rassy
 *
 * @version <span class="file">$Id: SubdocumentSAXFilter.java,v 1.8 2007/07/11 15:38:54 grudzin Exp $</span>
 */

public class SubdocumentSAXFilter extends AbstractXMLPipe
  implements SAXFilter
{
  /**
   * Hint of this component (for component selector). Value is
   * <span class="string">"subdocument"</span>.
   */

  public static String HINT = "subdocument";

  /**
   * Whether namespace adding is enabled or not.
   */

  protected boolean namespaceAddingEnabled = false;

  /**
   * The namespace URI.
   */

  protected String namespaceURI = null;

  /**
   * The namespace prefix.
   */

  protected String namespacePrefix = null;

  /**
   * Checks if the fallback namespace URI and prefix are non-empty.
   */

  protected void checkNamespace ()
    throws SAXException
  {
    if ( ( this.namespaceURI == null ) || ( this.namespaceURI.equals("") ) )
      throw new SAXException("Namespace URI null or empty");
    if ( ( this.namespacePrefix == null ) || ( this.namespacePrefix.equals("") ) )
      throw new SAXException("Namespace prefix null or empty");
  }

  /**
   * Enables or disables namespace adding.
   */

  public void setNamespaceAdding (boolean namespaceAddingEnabled)
    throws SAXException
  {
    if ( namespaceAddingEnabled ) this.checkNamespace();
    this.namespaceAddingEnabled = namespaceAddingEnabled;
  }

  /**
   * Returns whether namespace adding is in effect or not.
   */

  public boolean getNamespaceAdding ()
  {
    return this.namespaceAddingEnabled;
  }

  /**
   * Sets the namespace URI and prefix.
   */


  public void setNamespace (String namespaceURI, String namespacePrefix)
  {
    this.namespaceURI = namespaceURI;
    this.namespacePrefix = namespacePrefix;
  }

  /**
   * Starts the subdocument.
   */

  public void startDocument()
    throws SAXException 
  {
    this.getLogger().debug("startDocument:" +
			   " this.namespaceAddingEnabled = " + this.namespaceAddingEnabled +
			   ",this.namespaceURI  = " + this.namespaceURI +
			   ",this.namespacePrefix = " + this.namespacePrefix);
    if ( this.namespaceAddingEnabled )
      this.contentHandler.startPrefixMapping(this.namespacePrefix, this.namespaceURI);
  }

  /**
   * Closes the subdocument.
   */

  public void endDocument()
    throws SAXException 
  {
    this.getLogger().debug("endDocument");
    if ( this.namespaceAddingEnabled )
      this.contentHandler.endPrefixMapping(this.namespacePrefix);
  }

  /**
   * Starts an element.
   */

  public void startElement(String namespaceURI, String localName, String qualifiedName,
			   Attributes attributes)
    throws SAXException 
  {
    if ( ( this.namespaceAddingEnabled ) &&
	 ( ( namespaceURI == null ) || ( namespaceURI.equals("") ) ) )
      namespaceURI = this.namespaceURI;
    this.contentHandler.startElement(namespaceURI, localName, qualifiedName, attributes);
  }

  /**
   * Closes an element.
   */

  public void endElement(String namespaceURI, String localName, String qualifiedName)
    throws SAXException 
  {
    if ( ( this.namespaceAddingEnabled ) &&
	 ( ( namespaceURI == null ) || ( namespaceURI.equals("") ) ) )
      namespaceURI = this.namespaceURI;
    this.contentHandler.endElement(namespaceURI, localName, qualifiedName);
  }

  /**
   * Recycles this component
   */

  public void recycle ()
  {
    super.recycle();
    this.namespaceAddingEnabled = false;
    this.namespaceURI = null;
    this.namespacePrefix = null;
  }
}
