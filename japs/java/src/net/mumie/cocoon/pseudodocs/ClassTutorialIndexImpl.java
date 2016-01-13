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
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.UseMode;
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

public class ClassTutorialIndexImpl extends AbstractJapsServiceable
  implements Recyclable, Disposable, ClassTutorialIndex
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

  private static ServiceStatus serviceStatus =
    new ServiceStatus(ClassTutorialIndexImpl.class);

  /**
   * Helper tho write Meta XML elements to SAX.
   */

  protected MetaXMLElement xmlElement = new MetaXMLElement();

  // --------------------------------------------------------------------------------
  // Lifecycle related methods
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>ClassTutorialIndexImpl</code> instance.
   */

  public ClassTutorialIndexImpl ()
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
    this.logDebug
      (METHOD_NAME + " 1/2: Started. classId = " + classId);
    this.classId = classId;
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Resets the class id ({@link #classId classId}) and the meta XML element writer
   * ({@link #xmlElement xmlElement}).
   */

  protected void reset ()
  {
    this.classId = Id.UNDEFINED;
    this.xmlElement.reset();
  }

  /**
   * Recycles this instance.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.reset();
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
    this.reset();
    this.instanceStatus.notifyDispose();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // To-SAX methods
  // --------------------------------------------------------------------------------

  /**
   * Writes the tutorial index to the specified content handler. If <code>ownDocument</code>
   * is <code>true</code>, the <code>startDocument</code> and <code>endDocument</code>
   * methods are called before resp. after the XML is created. If <code>ownDocument</code>
   * is false, they are suppressed.
   */

  public void toSAX (ContentHandler contentHandler, boolean ownDocument)
    throws SAXException
  {
    final String METHOD_NAME = "toSAX";
    this.logDebug(METHOD_NAME + " 1/2: Started");

    DbHelper dbHelper = null;
    Tutorial tutorial = null;
    try
      {
        // Setup dbHelper and tutorial objects:
        dbHelper = (DbHelper)this.serviceManager.lookup(DbHelper.ROLE);
        tutorial = (Tutorial)this.serviceManager.lookup(Tutorial.ROLE);

        // Get e-learning class name:
        String className = dbHelper.getPseudoDocDatumAsString
          (PseudoDocType.CLASS, this.classId, DbColumn.NAME);

        // Start XML document if necessary:
        if ( ownDocument ) contentHandler.startDocument();

        // Start root element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.CLASS_TUTORIALS);
        this.xmlElement.addAttribute(XMLAttribute.CLASS_ID, this.classId);
        this.xmlElement.addAttribute(XMLAttribute.CLASS_NAME, className);
        this.xmlElement.startToSAX(contentHandler);

        // Query data:
        tutorial.setUseMode(UseMode.COMPONENT);
        ResultSet resultSet =
          dbHelper.queryClassTutorials(this.classId, tutorial.getDbColumns());

        // Write TUTORIAL elements:
        while ( resultSet.next() )
          {
            tutorial.reset();
            tutorial.setId(resultSet.getInt(DbColumn.ID));
            tutorial.setUseMode(UseMode.COMPONENT);
            tutorial.toSAX(resultSet, contentHandler, false);
          }

        // Close root element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.CLASS_TUTORIALS);
        this.xmlElement.endToSAX(contentHandler);

        // Close XML document if necessary:
        if ( ownDocument ) contentHandler.endDocument();

        this.logDebug(METHOD_NAME + "2/2: Done");
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("Failed to output tutorials for e-learning class " + classId + " as XML", exception);
      }
    finally
      {
        if ( tutorial != null ) this.serviceManager.release(tutorial);
        if ( dbHelper != null ) this.serviceManager.release(dbHelper);
      }

    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Same as {@link #toSAX(ContentHandler,boolean) toSAX(contentHandler, true)}.
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
   *   "ClassTutorialIndexImpl" +
   *   '#' + instanceId
   *   '(' + numberOfRecycles
   *   ',' + {@link #classId classId}
   *   ')'</pre>
   * where <code>instanceId</code> is the instance id and
   * <code>numberOfRecycles</code> the number of recycles of this instance.
   */

  public String getIdentification ()
  {
    return
      "ClassTutorialIndexImpl" +
      '#' + this.instanceStatus.getInstanceId() +
      '(' + this.instanceStatus.getNumberOfRecycles() +
      ',' + this.classId +
      ')';
  }
}
