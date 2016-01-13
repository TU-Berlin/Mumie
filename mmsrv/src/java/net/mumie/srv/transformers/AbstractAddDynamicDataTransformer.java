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

package net.mumie.srv.transformers;

import net.mumie.srv.notions.XMLAttribute;
import net.mumie.srv.notions.XMLElement;
import net.mumie.srv.notions.XMLNamespace;
import net.mumie.srv.xml.MetaXMLElement;
import org.apache.cocoon.transformation.ServiceableTransformer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: AbstractAddDynamicDataTransformer.java,v 1.1 2009/03/07 00:42:36 rassy Exp $</code>
 */

public abstract class AbstractAddDynamicDataTransformer extends ServiceableTransformer
{
  /**
   * Counts the nesting depth of elements.
   */

  protected int depth = 0;

  /**
   * Helper to write metainfo XML elements to SAX.
   */

  protected MetaXMLElement metaXMLElement = new MetaXMLElement();

  /**
   * Sends the start tag of the "dynamic_data" metainfo element to SAX.
   */

  protected void dynamicDataElementStartToSAX ()
    throws SAXException 
  {
    final String METHOD_NAME = "dynamicDataElementStartToSAX()";
    this.getLogger().debug(METHOD_NAME);
    this.metaXMLElement.reset();
    this.metaXMLElement.setLocalName(XMLElement.DYNAMIC_DATA);
    this.metaXMLElement.addAttribute(XMLAttribute.ORIGINATOR, this.getClass().getName());
    this.metaXMLElement.startToSAX(this.contentHandler);
  }

  /**
   * Sends the end tag of the "dynamic_data" metainfo element to SAX.
   */

  protected void dynamicDataElementEndToSAX ()
    throws SAXException 
  {
    final String METHOD_NAME = "dynamicDataElementEndToSAX()";
    this.getLogger().debug(METHOD_NAME);
    this.metaXMLElement.reset();
    this.metaXMLElement.setLocalName(XMLElement.DYNAMIC_DATA);
    this.metaXMLElement.endToSAX(this.contentHandler);
  }

  /**
   * Sends the content of the dynamic data element to SAX.
   */

  protected abstract void dynamicDataElementContentToSAX ()
    throws SAXException;

  /**
   * <p>
   *   Called from the <code>startElement</code> method when the root element starts. The
   *   parameters are those of the <code>startElement</code> method.
   * </p>
   */

  protected void processRootElementStart (String namespaceURI,
                                          String localName,
                                          String qualifiedName,
                                          Attributes attributes)
    throws SAXException
  {
    final String METHOD_NAME =
      "processRootElementStart(String namespaceURI, String localName," +
      " String qualifiedName, Attributes attributes)";

    this.getLogger().debug
      (METHOD_NAME + " 1/2:" + 
       " namespaceURI = " + namespaceURI +
       ", localName = " + localName +
       ", qualifiedName = " + qualifiedName +
       ", attributes = " + attributes);

    // Sending element start to SAX
    this.contentHandler.startElement(namespaceURI, localName, qualifiedName, attributes);

    this.getLogger().debug
      (METHOD_NAME + " 2/2");
  }

  /**
   * <p>
   *   Called from the <code>endElement</code> method when the root element starts. The
   *   parameters are those of the <code>startElement</code> method.
   * </p>
   * <p>
   *   Inserts the "dynamic_data" metainfo element before the root end tag.
   * </p>
   */

  protected void processRootElementEnd (String namespaceURI,
                                        String localName,
                                        String qualifiedName)
    throws SAXException
  {
    final String METHOD_NAME =
      "processRootElementEnd(String namespaceURI, String localName, String qualifiedName)";

    this.getLogger().debug
      (METHOD_NAME + " 1/2:" + 
       " namespaceURI = " + namespaceURI +
       ", localName = " + localName +
       ", qualifiedName = " + qualifiedName);

    this.dynamicDataElementStartToSAX();
    this.dynamicDataElementContentToSAX();
    this.dynamicDataElementEndToSAX();

    this.contentHandler.endElement(namespaceURI, localName, qualifiedName);

    this.getLogger().debug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * <p>
   *   Receives notification of the beginning of an element.
   * </p>
   */

  public void startElement (String namespaceURI,
                            String localName,
                            String qualifiedName,
                            Attributes attributes)
    throws SAXException
  {
    this.depth++;
    if ( this.contentHandler != null )
      {
        if ( this.depth == 1 )
          this.processRootElementStart(namespaceURI, localName, qualifiedName, attributes);
        else
          this.contentHandler.startElement(namespaceURI, localName, qualifiedName, attributes);
      }
  }

  /**
   * <p>
   *   Receives notification of the end of an element.
   * </p>
   */

  public void endElement (String namespaceURI,
                          String localName,
                          String qualifiedName)
    throws SAXException
  {
    if ( this.contentHandler != null )
      {
        if ( this.depth == 1 )
          this.processRootElementEnd(namespaceURI, localName, qualifiedName);
        else
          this.contentHandler.endElement(namespaceURI, localName, qualifiedName);
      }
    this.depth--;
  }

  /**
   * Resets the global variables to their initial states.
   */

  protected void resetVariables ()
  {
    this.depth = 0;
    this.metaXMLElement.reset();
  }

  /**
   * Releases the global services. This implementation does nothing. Extending classes may
   * overwrite this if they have global services.
   */

  protected void releaseServices ()
  {
    // No services to release
  }

  /**
   * Recycles this transformer.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");
    this.resetVariables();
    this.releaseServices();
    super.recycle();
    this.getLogger().debug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Disposes this transformer.
   */

  public void dispose ()
  {
    final String METHOD_NAME = "dispose";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");
    this.resetVariables();
    this.releaseServices();
    super.dispose();
    this.getLogger().debug(METHOD_NAME + " 2/2: Done");
  }

}
