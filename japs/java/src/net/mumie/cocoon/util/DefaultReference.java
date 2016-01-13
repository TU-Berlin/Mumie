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

package net.mumie.cocoon.util;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.RefAttrib;
import net.mumie.cocoon.notions.RefType;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.xml.MetaXMLElement;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.apache.avalon.framework.activity.Disposable;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import java.io.PrintStream;
import net.mumie.cocoon.service.AbstractJapsServiceable;

/**
 * Default implementation of {@link Reference}.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: DefaultReference.java,v 1.10 2007/07/30 14:34:26 rassy Exp $</code>
 */

public class DefaultReference extends AbstractJapsServiceable
  implements Reference, Recyclable, Disposable
{
  // --------------------------------------------------------------------------------
  // Global constants and variables
  // --------------------------------------------------------------------------------

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(DefaultReference.class);

  /**
   * The db helper.
   */

  protected DbHelper dbHelper = null;

  /**
   * The type of the reference origin.
   */

  protected int fromDocType = DocType.UNDEFINED;

  /**
   * The type of the reference target.
   */

  protected int toDocType = DocType.UNDEFINED;

  /**
   * The id of the reference.
   */

  protected int id = Id.UNDEFINED;

  // --------------------------------------------------------------------------------
  // Lifecycle related methods
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>DefaultReference</code> instance. Increases the instance counter
   * by 1 and sets the instance id to the new value of the instance counter.
   */

  public DefaultReference ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Sets the document type of the reference origin, the document type of the reference
   * target, and the id of the reference.
   *
   * @param fromDocType the document type of the reference origin, as numerical code.
   * @param toDocType the document type of the reference target, as numerical code.
   * @param id the reference id.
   *
   * @throws IllegalArgumentException if <code>fromDocType</code> or <code>toDocType</code>
   * is not a valid document type codes, or if <code>id</code> is not a possible id value.
   */

  public void setup (int fromDocType, int toDocType, int id)
  {
    final String METHOD_NAME = "setup(int fromDocType, int toDocType, int id)";
    this.getLogger().debug
      (METHOD_NAME + " 1/2:" +
       " fromDocType = " + fromDocType +
       ", toDocType = " + toDocType +
       ", id = " + id);
    if ( id < 0 )
      throw new IllegalArgumentException("Invalid ref id: " + id);
    if ( !DocType.exists(fromDocType) )
      throw new IllegalArgumentException("Invalid type of ref origin: " + fromDocType);
    if ( !DocType.exists(toDocType) )
      throw new IllegalArgumentException("Invalid type of ref target: " + toDocType);
    this.fromDocType = fromDocType;
    this.toDocType = toDocType;
    this.id = id;
    this.logDebug(METHOD_NAME + " 2/2: Done"); 
  }

  /**
   * Resets the global variables to their initial values.
   */

  protected void resetVariables ()
  {
    this.fromDocType = DocType.UNDEFINED;
    this.toDocType = DocType.UNDEFINED;
    this.id = Id.UNDEFINED;
  }

  /**
   * Releases the global services. Currently, the only global service is the db helper.
   */

  protected void releaseServices ()
  {
    if ( this.dbHelper != null )
      {
        this.serviceManager.release(this.dbHelper);
        this.dbHelper = null;
      }
  }

  /**
   * Recycles this object.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.resetVariables();
    this.releaseServices();
    this.instanceStatus.notifyRecycle();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Disposes this object.
   */

  public void dispose ()
  {
    final String METHOD_NAME = "dispose";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.resetVariables();
    this.releaseServices();
    this.instanceStatus.notifyDispose();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // "Ensure" method for the db helper
  // --------------------------------------------------------------------------------

  /**
   * Ensures that the db helper is ready to use.
   */

  protected void ensureDbHelper ()
    throws ServiceException 
  {
    if ( this.dbHelper == null )
      this.dbHelper = (DbHelper)this.serviceManager.lookup(DbHelper.ROLE);
  }

  // --------------------------------------------------------------------------------
  // Get methods for "from-doc" type, "to-doc" type, id
  // --------------------------------------------------------------------------------

  /**
   * Returns the type of the reference origin document.
   */

  public int getFromDocType ()
  {
    return this.fromDocType;
  }

  /**
   * Returns the type of the reference target document.
   */

  public int getToDocType ()
  {
    return this.toDocType;
  }

  /**
   * Returns the id of this reference.
   */

  public int getId ()
  {
    return this.id;
  }

  // --------------------------------------------------------------------------------
  // Last modification time
  // --------------------------------------------------------------------------------

  /**
   * Returns the time this reference was last modified. This is the time the reference
   * target was checked in.
   *
   * @return the last modification time of this reference.
   *
   * @throws SQLException if something goes wrong while retrieving the last modification
   *                      time from the database
   */

  public Date getLastModified ()
    throws ServiceException, SQLException
  {
    final String METHOD_NAME = "getLastModified";
    this.logDebug(METHOD_NAME);
    this.ensureDbHelper();
    ResultSet resultSet = this.dbHelper.queryDatumOfRefTarget
      (this.fromDocType, this.toDocType, this.id, DbColumn.CREATED);
    if ( ! resultSet.next() )
      throw new SQLException("Empty ResultSet");
    return resultSet.getTimestamp(DbColumn.CREATED);
  }

  // --------------------------------------------------------------------------------
  // To-SAX methods
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Writes this reference as SAX events to <code>contentHandler</code>.
   * </p>
   * <p>
   *   If <code>ownDocument</code> is <code>true</code>, the <code>startDocument</code> and
   *   <code>endDocument</code> method of contentHandler is called before resp. after the
   *   XML is sent to SAX. 
   * </p>
   */

  public void toSAX (ContentHandler contentHandler, boolean ownDocument)
    throws SAXException
  {
    final String METHOD_NAME = "toSAX";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    try
      {
        this.ensureDbHelper();
        ResultSet resultSet =
          this.dbHelper.queryReference(this.fromDocType, this.toDocType, this.id);
	if ( !resultSet.next() )
	  throw new SAXException("No such reference:" 
				 + " from " + DocType.nameFor(this.fromDocType) 
				 + " to " + DocType.nameFor(this.fromDocType) 
				 + ", refId " + this.id);
        int refType = resultSet.getInt(DbColumn.REF_TYPE);
        
        MetaXMLElement xmlElement = new MetaXMLElement();

        // Start document if necessary:
        if ( ownDocument )
          contentHandler.startDocument();

        // Start REF element:
        xmlElement.reset();
        xmlElement.setLocalName(XMLElement.REF);
        xmlElement.addAttribute(XMLAttribute.ID, this.id);
        xmlElement.addAttribute(XMLAttribute.LID, resultSet.getString(DbColumn.LID));
        xmlElement.startToSAX(contentHandler);

        // Start FROM element:
        xmlElement.reset();
        xmlElement.setLocalName(XMLElement.FROM);
        xmlElement.startToSAX(contentHandler);

        // Write "from-document" element:
        xmlElement.reset();
        xmlElement.setLocalName(DocType.nameFor[this.fromDocType]);
        xmlElement.addAttribute(XMLAttribute.ID, resultSet.getInt(DbColumn.FROM_DOC));
        xmlElement.toSAX(contentHandler);

        // Close FROM element:
        xmlElement.reset();
        xmlElement.setLocalName(XMLElement.FROM);
        xmlElement.endToSAX(contentHandler);

        // Start TO element:
        xmlElement.reset();
        xmlElement.setLocalName(XMLElement.TO);
        xmlElement.startToSAX(contentHandler);

        // Write "to-document" element:
        xmlElement.reset();
        xmlElement.setLocalName(DocType.nameFor[this.toDocType]);
        xmlElement.addAttribute(XMLAttribute.ID, resultSet.getInt(DbColumn.TO_DOC));
        xmlElement.toSAX(contentHandler);

        // Close TO element:
        xmlElement.reset();
        xmlElement.setLocalName(XMLElement.TO);
        xmlElement.endToSAX(contentHandler);

        // Write REF_TYPE element:
        xmlElement.reset();
        xmlElement.setLocalName(XMLElement.REF_TYPE);
        xmlElement.addAttribute(XMLAttribute.ID, refType);
        xmlElement.addAttribute(XMLAttribute.NAME, RefType.nameFor[refType]);
        xmlElement.toSAX(contentHandler);

        // Write ref attribute elements:
        for (int refAttrib = RefAttrib.first; refAttrib <= RefAttrib.last; refAttrib++)
          {
            if ( RefAttrib.exists(refAttrib, fromDocType, toDocType, refType) )
              {
                String column = RefAttrib.dbColumnOf[refAttrib];
                xmlElement.reset();
                xmlElement.setLocalName(XMLElement.REF_ATTRIBUTE);
                xmlElement.addAttribute(XMLAttribute.NAME, RefAttrib.nameFor[refAttrib]);
                xmlElement.addAttribute(XMLAttribute.VALUE, resultSet.getString(column));
                xmlElement.toSAX(contentHandler);
              }
          }

        // Close REF element:
        xmlElement.reset();
        xmlElement.setLocalName(XMLElement.REF);
        xmlElement.endToSAX(contentHandler);

        // Close document if necessary:
        if ( ownDocument )
          contentHandler.endDocument();


        this.logDebug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
        throw new SAXException(exception);
      }
  }

  /**
   * <p>
   *   Writes this reference as SAX events to <code>contentHandler</code>.
   * </p>
   * <p>
   *   The <code>startDocument</code> and <code>endDocument</code> method of contentHandler
   *   is called before resp. after the XML is sent to SAX. 
   * </p>
   */

  public void toSAX (ContentHandler contentHandler)
    throws SAXException
  {
    this.toSAX(contentHandler, true);
  }

  // --------------------------------------------------------------------------------
  // Identification method
  // --------------------------------------------------------------------------------
  
  /**
   * Returns a string that identifies this instance. It has the
   * following form:<pre>
   *   "DefaultReference" +
   *   '#' + instanceId
   *   '(' + lifecycleStatus
   *   ',' + numberOfRecycles
   *   ',' + {@link #fromDocType fromDocType}
   *   ',' + {@link #toDocType toDocType}
   *   ',' + {@link #id id}
   *   ')'</pre>
   * where <code>instanceId</code> is the instance id, <code>lifecycleStatus</code> the
   * lifecycle status, and <code>numberOfRecycles</code> the number of recycles of this
   * instance.
   */

  public String getIdentification ()
  {
    return
      "DefaultReference" +
      '#' + this.instanceStatus.getInstanceId() +
      '(' + this.instanceStatus.getLifecycleStatus() +
      ',' + this.instanceStatus.getNumberOfRecycles() +
      ',' + this.fromDocType +
      ',' + this.toDocType +
      ',' + this.id +
      ')';
  }
}
