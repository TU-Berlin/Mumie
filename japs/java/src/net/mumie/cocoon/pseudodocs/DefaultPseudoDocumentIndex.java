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

package net.mumie.cocoon.pseudodocs;

import java.sql.ResultSet;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.UseMode;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.xml.MetaXMLElement;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.Serviceable;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import net.mumie.cocoon.service.AbstractJapsServiceable;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.service.ServiceInstanceStatus;

/**
 * Default implementation of {@link PseudoDocumentIndex}.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: DefaultPseudoDocumentIndex.java,v 1.7 2007/07/11 15:38:47 grudzin Exp $</code>
 */

public class DefaultPseudoDocumentIndex extends AbstractJapsServiceable 
  implements PseudoDocumentIndex, Serviceable, Recyclable 
{
  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(DefaultPseudoDocumentIndex.class);

  /**
   * The type of the pseudo-documents in the index.
   */

  protected int type = PseudoDocType.UNDEFINED;

  /**
   * The use mode of the pseudo-documents in the index.
   */

  protected int useMode = UseMode.COMPONENT;

  /**
   * Helper tho write Meta XML elements to SAX.
   */

  protected MetaXMLElement metaXMLElement = new MetaXMLElement();

  /**
   * Creates a new instance.
   */

  public DefaultPseudoDocumentIndex ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Sets the type of the pseudo-documents in the index.
   */

  public void setType (int type)
  {
    if ( !PseudoDocType.exists(type) )
      throw new IllegalArgumentException("Unknown pseudo-document type: " + type);
    this.type = type;
  }

  /**
   * Sets the use mode of the pseudo-documents in the index. Default is
   * {@link UseMode#COMPONENT COMPONENT}.
   */

  public void setUseMode (int useMode)
  {
    if ( !UseMode.exists(useMode) )
      throw new IllegalArgumentException("Unknown use mode: " + useMode);
    this.useMode = useMode;
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
    this.getLogger().debug(METHOD_NAME + " 1/2: Started. ownDocument = " + ownDocument + " use-mode Name is: " + UseMode.nameFor[this.useMode]);
    DbHelper dbHelper = null;
    ServiceSelector pseudoDocumentSelector = null;
    PseudoDocument pseudoDocument = null;
    try
      {
        // Setup db helper:
        dbHelper = (DbHelper)this.serviceManager.lookup(DbHelper.ROLE);

        // Setup pseudo-document object:
        pseudoDocumentSelector =
	  (ServiceSelector)this.serviceManager.lookup(PseudoDocument.ROLE + "Selector");
        pseudoDocument =
          (PseudoDocument)pseudoDocumentSelector.select(PseudoDocType.hintFor[this.type]);
        pseudoDocument.setUseMode(this.useMode);
	pseudoDocument.setWithPath(true);

        // Query data:
        ResultSet resultSet =
          dbHelper.queryPseudoDocData(this.type, pseudoDocument.getDbColumns());

        // Start XML pseudoDocument if necessary:
        if ( ownDocument ) contentHandler.startDocument();

        // Start root element:
        this.metaXMLElement.reset();
        this.metaXMLElement.setLocalName(XMLElement.PSEUDO_DOCUMENTS);
        this.metaXMLElement.addAttribute(XMLAttribute.TYPE, this.type);
        this.metaXMLElement.addAttribute
          (XMLAttribute.TYPE_NAME, PseudoDocType.nameFor[this.type]);
        this.metaXMLElement.startToSAX(contentHandler);

        // Write pseudo-documents as XML:
        while ( resultSet.next() )
          {
            pseudoDocument.reset();
            pseudoDocument.setId(Id.AUTO);
	    pseudoDocument.setUseMode(this.useMode);
	    pseudoDocument.setWithPath(true);
            pseudoDocument.toSAX(resultSet, contentHandler, false);
          }

        // Close root element:
        this.metaXMLElement.reset();
        this.metaXMLElement.setLocalName(XMLElement.PSEUDO_DOCUMENTS);
        this.metaXMLElement.endToSAX(contentHandler);

        // Close pseudoDocument if necessary:
        if ( ownDocument ) contentHandler.endDocument();

        this.getLogger().debug(METHOD_NAME + "2/2: Done");
      }
    catch (Exception exception)
      {
        throw new SAXException(exception);
      }
    finally
      {
        if ( pseudoDocument != null ) this.serviceManager.release(pseudoDocument);
        if ( pseudoDocumentSelector != null ) this.serviceManager.release(pseudoDocumentSelector);
        if ( dbHelper != null ) this.serviceManager.release(dbHelper);
      }
  }

  /**
   * Sends this index as SAX events to <code>contentHandler</code>.
   */

  public void toSAX (ContentHandler contentHandler)
    throws SAXException
  {
    this.toSAX(contentHandler, true);
  }

  /**
   * Recycles this index.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle()";
    this.getLogger().debug(METHOD_NAME + "1/2: Started");
    this.type = PseudoDocType.UNDEFINED;
    this.useMode = UseMode.COMPONENT;
    this.metaXMLElement.reset();
    this.getLogger().debug(METHOD_NAME + "2/2: Done");
  }
}
