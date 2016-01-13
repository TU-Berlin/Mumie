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

import java.sql.ResultSet;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.PseudoDocPath;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.service.AbstractJapsServiceable;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.xml.MetaXMLElement;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.activity.Disposable;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Default implementation of {@link ClassesAndCoursesIndex ClassesAndCoursesIndex}.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: DefaultClassesAndCoursesIndex.java,v 1.5 2009/12/11 11:53:00 linges Exp $</code>
 */

public class DefaultClassesAndCoursesIndex extends AbstractJapsServiceable
  implements Recyclable, Disposable, ClassesAndCoursesIndex
{
  // --------------------------------------------------------------------------------
  // Global constants and variables
  // --------------------------------------------------------------------------------

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus =
    new ServiceStatus(DefaultClassesAndCoursesIndex.class);

  /**
   * Helper tho write Meta XML elements to SAX.
   */

  protected MetaXMLElement xmlElement = new MetaXMLElement();

  // --------------------------------------------------------------------------------
  // Lifecycle related methods
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>DefaultClassesAndCoursesIndex</code> instance.
   */

  public DefaultClassesAndCoursesIndex ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Recycles this instance.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle";
    this.logDebug(METHOD_NAME + " 1/2: Started");
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
       
        int defaultClass = dbHelper.getPseudoDocIdForPath(PseudoDocType.CLASS, PseudoDocPath.DEFAULT_CLASS);
        
        // Query database:
        ResultSet resultSet = dbHelper.queryClassesAndCourses();
         
        // Start XML document if necessary:
        if ( ownDocument ) contentHandler.startDocument();

        // Start root element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.CLASSES_AND_COURSES);
        this.xmlElement.startToSAX(contentHandler);

        // Write list:
        while ( resultSet.next() )
          {
            if(resultSet.getInt(DbColumn.ID) == defaultClass) //ignore Default Class
              continue;
            
            String type = resultSet.getString(DbColumn.DOC_TYPE);

            // Start course resp. class element:
            this.xmlElement.reset();
            this.xmlElement.setLocalName(type);
            this.xmlElement.addAttribute(XMLAttribute.ID, resultSet.getInt(DbColumn.ID));
            this.xmlElement.startToSAX(contentHandler);

            // Name:
            this.xmlElement.reset();
            this.xmlElement.setLocalName(XMLElement.NAME);
            this.xmlElement.setText(resultSet.getString(DbColumn.NAME));
            this.xmlElement.toSAX(contentHandler);

            // Description:
            this.xmlElement.reset();
            this.xmlElement.setLocalName(XMLElement.DESCRIPTION);
            this.xmlElement.setText(resultSet.getString(DbColumn.DESCRIPTION));
            this.xmlElement.toSAX(contentHandler);

            if ( type.equals(DocType.nameFor(DocType.COURSE)) )
              {
                // Class:
                this.xmlElement.reset();
                this.xmlElement.setLocalName(PseudoDocType.nameFor(PseudoDocType.CLASS));
                this.xmlElement.addAttribute(XMLAttribute.ID, resultSet.getInt(DbColumn.CLASS));
                this.xmlElement.toSAX(contentHandler);
              }

            // Close course resp. class element:
            this.xmlElement.reset();
            this.xmlElement.setLocalName(type);
            this.xmlElement.endToSAX(contentHandler);
          }

        // Close root element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.CLASSES_AND_COURSES);
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
   *   "DefaultClassesAndCoursesIndex" +
   *   '#' + instanceId
   *   '(' + lifecycleStatus
   *   ')'</pre>
   * where <code>instanceId</code> is the instance id and <code>lifecycleStatus</code> the
   * lifecycle status instance.
   */

  public String getIdentification ()
  {
    return
      "DefaultClassesAndCoursesIndex" +
      '#' + this.instanceStatus.getInstanceId() +
      '(' + this.instanceStatus.getLifecycleStatus() +
      ')';
  }
}