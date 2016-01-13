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

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * A component (in the sence of Avalon) that recieves SAX events as input and sends SAX
 * events as output. Extends {@link org.xml.sax.ContentHandler} to be able to act as a SAX
 * event reciever. Sends the output SAX events to another {@link org.xml.sax.ContentHandler}.
 *
 * @author Tilman Rassy
 *
 * @version <span class="file">$Id: SAXFilter.java,v 1.6 2007/07/11 15:38:54 grudzin Exp $</span>
 */

public interface SAXFilter extends ContentHandler
{
  /**
   * Role of an implementing class as an Avalon component. Value is
   * <span class="string">"net.mumie.cocoon.xml.SAXFilter"</span>.
   */

  String ROLE = "net.mumie.cocoon.xml.SAXFilter";


  /**
   * Sets the content handler to which to send the output SAX events.
   */

  public void setContentHandler (ContentHandler handler);
  
  
  /**
   * Enables or disables namespace adding.
   */

  public void setNamespaceAdding (boolean namespaceAddingEnabled)
    throws SAXException;
  
  
  /**
   * Returns whether namespace adding is in effect or not.
   */

  public boolean getNamespaceAdding ();
  
  
  /**
   * Sets the namespace URI and prefix.
   */

  public void setNamespace (String namespaceURI, String namespacePrefix);
}
