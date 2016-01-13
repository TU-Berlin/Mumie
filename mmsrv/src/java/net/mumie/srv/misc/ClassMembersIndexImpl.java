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

package net.mumie.srv.misc;

import java.sql.ResultSet;
import net.mumie.srv.db.DbHelper;
import net.mumie.srv.notions.DbColumn;
import net.mumie.srv.notions.Id;
import net.mumie.srv.notions.EntityType;
import net.mumie.srv.notions.XMLAttribute;
import net.mumie.srv.notions.XMLElement;
import net.mumie.srv.notions.XMLNamespace;
import net.mumie.srv.notions.XMLNamespacePrefix;
import net.mumie.srv.service.AbstractMumieServiceable;
import net.mumie.srv.service.ServiceInstanceStatus;
import net.mumie.srv.service.ServiceStatus;
import net.mumie.srv.xml.GeneralXMLElement;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.activity.Disposable;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Default implementation of {@link ClassMembersIndex ClassMembersIndex}.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: ClassMembersIndexImpl.java,v 1.1 2009/05/20 22:50:49 rassy Exp $</code>
 */

public class ClassMembersIndexImpl extends AbstractMumieServiceable
  implements Recyclable, Disposable, ClassMembersIndex
{
  // --------------------------------------------------------------------------------
  // Global constants and variables
  // --------------------------------------------------------------------------------

  /**
   * The id of the e-learning class.
   */

  protected int classId = Id.UNDEFINED;

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(ClassMembersIndexImpl.class);

  /**
   * Helper tho write XML elements to SAX.
   */

  protected GeneralXMLElement xmlElement = new GeneralXMLElement
    (XMLNamespace.DATA_RECORDS, XMLNamespacePrefix.DATA_RECORDS);

  // --------------------------------------------------------------------------------
  // Lifecycle related methods
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>ClassMembersIndexImpl</code> instance.
   */

  public ClassMembersIndexImpl ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Initializes this instance to represent the specified e-learning class.
   */

  public void setup (int classId)
  {
    final String METHOD_NAME = "setup";
    this.logDebug(METHOD_NAME + " 1/2: Started. classId = " + classId);
    this.classId = classId;
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Recycles this instance.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.classId = Id.UNDEFINED;
    this.xmlElement.reset();
    this.instanceStatus.notifyRecycle();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Disposes this instance.
   */

  public void dispose ()
  {
    final String METHOD_NAME = "dispose";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.classId = Id.UNDEFINED;
    this.instanceStatus.notifyDispose();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // To-SAX methods
  // --------------------------------------------------------------------------------

  /**
   * Sends the list as SAX events to the specified content handler.  If
   * <code>ownDocument</code> is true, the <code>startDocument</code> and
   * <code>endDocument</code> method of <code>contentHandler</code> is called before
   * resp. after the XML is sent to SAX.
   */

  public void toSAX (ContentHandler contentHandler, boolean ownDocument)
    throws SAXException
  {
    final String METHOD_NAME = "toSAX";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started. ownDocument = " + ownDocument);
    DbHelper dbHelper = null;
    try
      {
        // Setup db helper:
        dbHelper = (DbHelper)this.serviceManager.lookup(DbHelper.ROLE);

        // Get e-learning class name:
        String className = dbHelper.queryEntityDatumAsString
          (EntityType.CLASS, this.classId, DbColumn.NAME);

        // Query database:
        ResultSet resultSet = dbHelper.queryClassMemberIndex(this.classId);

        // Start XML document if necessary:
        if ( ownDocument ) contentHandler.startDocument();

        // Start root element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.CLASS_MEMBERS);
        this.xmlElement.addAttribute(XMLAttribute.CLASS_ID, this.classId);
        this.xmlElement.addAttribute(XMLAttribute.CLASS_NAME, className);
        this.xmlElement.startToSAX(contentHandler);

        // Write list:
        String elementName = XMLElement.ofEntityType(EntityType.USER);
        while ( resultSet.next() )
          {
            this.xmlElement.reset();
            this.xmlElement.setLocalName(elementName);
            this.xmlElement.addAttribute
              (XMLAttribute.ID, resultSet.getInt(DbColumn.ID));
            this.xmlElement.addAttribute
              (XMLAttribute.FIRST_NAME, resultSet.getString(DbColumn.FIRST_NAME));
            this.xmlElement.addAttribute
              (XMLAttribute.SURNAME, resultSet.getString(DbColumn.SURNAME));
            this.xmlElement.addAttribute
              (XMLAttribute.TUTORIAL_ID, resultSet.getInt(DbColumn.TUTORIAL_ID));
            this.xmlElement.addAttribute
              (XMLAttribute.TUTORIAL_NAME, resultSet.getString(DbColumn.TUTORIAL_NAME));
            this.xmlElement.addAttribute
              (XMLAttribute.TUTOR_ID, resultSet.getInt(DbColumn.TUTOR_ID));
            this.xmlElement.addAttribute
              (XMLAttribute.TUTOR_FIRST_NAME, resultSet.getString(DbColumn.TUTOR_FIRST_NAME));
            this.xmlElement.addAttribute
              (XMLAttribute.TUTOR_SURNAME, resultSet.getString(DbColumn.TUTOR_SURNAME));
            this.xmlElement.toSAX(contentHandler);
          }

        // Close root element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.CLASS_MEMBERS);
        this.xmlElement.endToSAX(contentHandler);

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
        if ( dbHelper != null )
          this.serviceManager.release(dbHelper);
      }
  }

  /**
   * Sends the list as SAX events to the specified content handler. Same as
   * {@link #toSAX(ContentHandler,boolean) toSAX(contentHandler, true)}.
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
   *   "ClassMembersIndexImpl" +
   *   '#' + instanceId
   *   '(' + lifecycleStatus
   *   ',' + {@link #classId classId}
   *   ')'</pre>
   * where <code>instanceId</code> is the instance id and <code>lifecycleStatus</code> the
   * lifecycle status instance.
   */

  public String getIdentification ()
  {
    return
      "ClassMembersIndexImpl" +
      '#' + this.instanceStatus.getInstanceId() +
      '(' + this.instanceStatus.getLifecycleStatus() +
      ',' + this.classId +
      ')';
  }
}