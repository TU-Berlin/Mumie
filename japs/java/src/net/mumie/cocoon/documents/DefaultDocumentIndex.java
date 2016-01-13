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

package net.mumie.cocoon.documents;

import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.framework.service.Serviceable;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.UseMode;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.ServiceManager;
import net.mumie.cocoon.db.DbHelper;
import org.apache.avalon.excalibur.pool.Recyclable;
import net.mumie.cocoon.xml.MetaXMLElement;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.notions.Id;
import java.sql.ResultSet;
import org.apache.avalon.framework.service.ServiceException;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import net.mumie.cocoon.notions.DbColumn;

/**
 * Default implementation of {@link DocumentIndex}.
 */

public class DefaultDocumentIndex extends AbstractLoggable
  implements DocumentIndex, Serviceable, Recyclable 
{
  /**
   * The service manager used by this object.
   **/

  protected ServiceManager serviceManager;

  /**
   * The type of the documents in the index.
   */
  
  protected int type = DocType.UNDEFINED;

  /**
   * The use mode of the documents in the index.
   */

  protected int useMode = UseMode.COMPONENT;

  /**
   * Controlls whether on;y the latest versions or all documents are included in the index.
   */

  protected boolean onlyLatest = true;

  /**
   * Helper tho write Meta XML elements to SAX.
   */

  protected MetaXMLElement metaXMLElement = new MetaXMLElement();

  /**
   * Sets the service manager.
   **/

  public void service (ServiceManager serviceManager)
    throws ServiceException
  {
    final String METHOD_NAME = "service(ServiceManager serviceManager)";
    this.getLogger().debug(METHOD_NAME + " 1/1: serviceManager = " + serviceManager);
    this.serviceManager = serviceManager;
  }

  /**
   * Sets the type of the documents in the index.
   */

  public void setType (int type)
  {
    if ( !DocType.exists(type) )
      throw new IllegalArgumentException("Unknown document type: " + type);
    this.type = type;
  }

  /**
   * Sets the use mode of the documents in the index. Default is
   * {@link UseMode#COMPONENT COMPONENT}.
   */

  public void setUseMode (int useMode)
  {
    if ( !UseMode.exists(useMode) )
      throw new IllegalArgumentException("Unknown use mode: " + useMode);
    this.useMode = useMode;
  }

  /**
   * Sets whether only the latest versions of the documents should be included in the
   * index. Default is <code>true</code>.
   */

  public void setOnlyLatest (boolean onlyLatest)
  {
    this.onlyLatest = onlyLatest;
  }

  /**
   * Sends this index as SAX events to <code>contentHandler</code>. If
   * <code>ownDocument</code> is true, the <code>startDocument</code> and
   * <code>endDocument</code> method of <code>contentHandler</code> is called before
   * resp. after the XML is sent to SAX.
   */

  public void toSAX (ContentHandler contentHandler, boolean ownDocument)
    throws SAXException
  {
    final String METHOD_NAME = "toSAX(ContentHandler contentHandler, boolean ownDocument)";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started. ownDocument = " + ownDocument);
    DbHelper dbHelper = null;
    ServiceSelector documentSelector = null;
    Document document = null;
    try
      {
        // Setup db helper:
        dbHelper = (DbHelper)this.serviceManager.lookup(DbHelper.ROLE);

        // Setup document object:
        documentSelector =
	  (ServiceSelector)this.serviceManager.lookup(Document.ROLE + "Selector");
        document =
          (Document)documentSelector.select(DocType.hintFor[this.type]);

        // Query data:
        document.setUseMode(this.useMode);
        ResultSet resultSet =
          dbHelper.queryData(this.type, document.getDbColumns(), this.onlyLatest);

        // Start XML document if necessary:
        if ( ownDocument ) contentHandler.startDocument();

        // Start root element:
        this.metaXMLElement.reset();
        this.metaXMLElement.setLocalName(XMLElement.DOCUMENTS);
        this.metaXMLElement.addAttribute(XMLAttribute.TYPE, this.type);
        this.metaXMLElement.addAttribute
          (XMLAttribute.TYPE_NAME, DocType.nameFor[this.type]);
        this.metaXMLElement.startToSAX(contentHandler);

        // Write documents as XML:
        while ( resultSet.next() )
          {
            document.reset();
            document.setId(resultSet.getInt(DbColumn.ID));
            document.setUseMode(this.useMode);
            document.toSAX(resultSet, contentHandler, false);
          }

        // Close root element:
        this.metaXMLElement.reset();
        this.metaXMLElement.setLocalName(XMLElement.DOCUMENTS);
        this.metaXMLElement.endToSAX(contentHandler);

        // Close document if necessary:
        if ( ownDocument ) contentHandler.endDocument();

        this.getLogger().debug(METHOD_NAME + "2/2: Done");
      }
    catch (Exception exception)
      {
        throw new SAXException(exception);
      }
    finally
      {
        if ( document != null ) this.serviceManager.release(document);
        if ( documentSelector != null ) this.serviceManager.release(documentSelector);
        if ( dbHelper != null ) this.serviceManager.release(dbHelper);
      }
  }

  /**
   * Recycles this index.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle()";
    this.getLogger().debug(METHOD_NAME + "1/2: Started");
    this.type = DocType.UNDEFINED;
    this.useMode = UseMode.UNDEFINED;
    this.onlyLatest = true;
    this.metaXMLElement.reset();
    this.getLogger().debug(METHOD_NAME + "2/2: Done");
  }

  /**
   * Sends this index as SAX events to <code>contentHandler</code>.
   */

  public void toSAX (ContentHandler contentHandler)
    throws SAXException
  {
    this.toSAX(contentHandler, true);
  }
}
