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

package net.mumie.cocoon.grade;

import java.sql.ResultSet;
import java.sql.SQLException;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.Category;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.notions.XMLNamespace;
import net.mumie.cocoon.service.AbstractJapsServiceable;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.xml.GeneralXMLElement;
import net.mumie.cocoon.xml.GeneralXMLElement;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.service.ServiceException;
import org.xml.sax.ContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXException;

/**
 *
 * 
 */

public class UserWorksheetGradeImpl extends AbstractJapsServiceable 
  implements UserWorksheetGrade, Recyclable, Disposable
{
  // --------------------------------------------------------------------------------
  // h1: Global constants and variables
  // --------------------------------------------------------------------------------

  /**
   * The id of the user.
   */

  protected int userId = Id.UNDEFINED;

  /**
   * The id of the worksheet.
   */

  protected int worksheetId = Id.UNDEFINED;

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus =
    new ServiceStatus(UserWorksheetGradeImpl.class);

  /**
   * Helper tho write Meta XML elements to SAX.
   */

  protected GeneralXMLElement xmlElement = new GeneralXMLElement
    (XMLNamespace.URI_GRADES, XMLNamespace.PREFIX_GRADES);

  // --------------------------------------------------------------------------------
  // h1: Lifecycle related methods
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>UserWorksheetGradeImpl</code> instance.
   */

  public UserWorksheetGradeImpl ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Initializes this instance to represent the specified user and worksheet.
   */

  public void setup (int userId, int worksheetId)
  {
    final String METHOD_NAME = "setup";
    this.logDebug
      (METHOD_NAME + " 1/2: Started. userId = " + userId + ", worksheetId = " + worksheetId);
    this.userId = userId;
    this.worksheetId = worksheetId;
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Resets the user and worksheet ids ({@link #userId userId} and
   * {@link #worksheetId worksheetId}, respectively) and the meta XML element writer
   * ({@link #xmlElement xmlElement}).
   */

  protected void reset ()
  {
    this.userId = Id.UNDEFINED;
    this.worksheetId = Id.UNDEFINED;
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
  // h1: To-SAX methods
  // --------------------------------------------------------------------------------

  /**
   * Writes the grades to the specified content handler. If <code>ownDocument</code> is
   * <code>true</code>, the <code>startDocument</code> and <code>endDocument</code> methods
   * are called before resp. after the XML is created. If <code>ownDocument</code> is false,
   * they are suppressed.
   */

  public void toSAX (ContentHandler contentHandler, boolean ownDocument)
    throws SAXException
  {
    final String METHOD_NAME = "toSAX";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    DbHelper dbHelper = null;
    try
      {
	// Init db helper:
	dbHelper = (DbHelper)this.serviceManager.lookup(DbHelper.ROLE);

        // Query user data:
        ResultSet userData = dbHelper.queryPseudoDocData
          (PseudoDocType.USER,
           userId,
           new String[]
            {DbColumn.FIRST_NAME,
             DbColumn.SURNAME,
             DbColumn.SYNC_ID});
        if ( !userData.next() )
          throw new IllegalArgumentException("User not found: " + userId);
        String userFirstName = userData.getString(DbColumn.FIRST_NAME);
        String userSurname = userData.getString(DbColumn.SURNAME);
        String userSyncId = userData.getString(DbColumn.SYNC_ID);

        // Query worksheet, course, and class data:
        ResultSet worksheetData = dbHelper.queryWorksheetContext(worksheetId);
        if ( !worksheetData.next() )
          throw new IllegalArgumentException("Worksheet not found: " + worksheetId);
        int worksheetVCthreadId = worksheetData.getInt(DbColumn.VC_THREAD_ID);
        int worksheetCategoryId = worksheetData.getInt(DbColumn.CATEGORY_ID);
        String worksheetCategory = Category.nameFor(worksheetCategoryId);
        String worksheetLabel = worksheetData.getString(DbColumn.LABEL);
        int points = worksheetData.getInt(DbColumn.POINTS);
        int courseId = worksheetData.getInt(DbColumn.COURSE_ID);
        int courseVCThreadId = worksheetData.getInt(DbColumn.COURSE_VC_THREAD_ID);
        int classId = worksheetData.getInt(DbColumn.CLASS_ID);
        // Class id is special because it may be SQL NULL, thus:
        if ( worksheetData.wasNull() ) classId = -1;
        String classSyncId =
          (classId != -1
           ? dbHelper.getPseudoDocDatumAsString(PseudoDocType.CLASS, classId, DbColumn.SYNC_ID)
           : null);

        // Query grade data:
        ResultSet gradeData = dbHelper.queryUserWorksheetGrade(userId, worksheetId);
        float result = (gradeData.next() ? gradeData.getFloat(DbColumn.GRADE) : 0);

        // Start XML document if necessary:
        if ( ownDocument ) contentHandler.startDocument();

        // Start root element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.USER_WORKSHEET_GRADES);
        this.xmlElement.startToSAX(contentHandler);

	// Write user_worksheet_grade element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.USER_WORKSHEET_GRADE);
        this.xmlElement.addAttribute(XMLAttribute.USER_ID, this.userId);
        this.xmlElement.addAttribute(XMLAttribute.WORKSHEET_ID, this.worksheetId);        
        this.xmlElement.addAttribute(XMLAttribute.RESULT, result);
        this.xmlElement.toSAX(contentHandler);

	// Write user element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.USER);
        this.xmlElement.addAttribute(XMLAttribute.ID, this.userId);
        this.xmlElement.addAttribute(XMLAttribute.SYNC_ID, userSyncId);
        this.xmlElement.addAttribute(XMLAttribute.FIRST_NAME, userFirstName);
        this.xmlElement.addAttribute(XMLAttribute.SURNAME, userSurname);
        this.xmlElement.toSAX(contentHandler);

	// Write worksheet element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.WORKSHEET);
        this.xmlElement.addAttribute(XMLAttribute.ID, this.worksheetId);
        this.xmlElement.addAttribute(XMLAttribute.VC_THREAD_ID, worksheetVCthreadId);
        this.xmlElement.addAttribute(XMLAttribute.CATEGORY, worksheetCategory);
        this.xmlElement.addAttribute(XMLAttribute.COURSE_ID, courseId);
        this.xmlElement.addAttribute(XMLAttribute.LABEL, worksheetLabel);
        this.xmlElement.addAttribute(XMLAttribute.POINTS, points);
        this.xmlElement.toSAX(contentHandler);

        // Write course element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.COURSE);
        this.xmlElement.addAttribute(XMLAttribute.ID, courseId);
        this.xmlElement.addAttribute(XMLAttribute.VC_THREAD_ID, courseVCThreadId);
        if ( classId != -1 )
          this.xmlElement.addAttribute(XMLAttribute.CLASS_ID, classId);
        this.xmlElement.toSAX(contentHandler);

        // If necessary, write class element:
        if ( classId != -1 )
          {
            this.xmlElement.reset();
            this.xmlElement.setLocalName(XMLElement.CLASS);
            this.xmlElement.addAttribute(XMLAttribute.ID, classId);
            this.xmlElement.addAttribute(XMLAttribute.SYNC_ID, classSyncId);
            this.xmlElement.toSAX(contentHandler);
          }

        // Close root element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.USER_WORKSHEET_GRADES);
        this.xmlElement.endToSAX(contentHandler);

        // Close XML document if necessary:
        if ( ownDocument ) contentHandler.endDocument();

        this.logDebug
          (METHOD_NAME + " 2/2: Done." +
           " userFirstName = " + userFirstName +
           ", userSurname = " + userSurname +
           ", userSyncId = " + userSyncId +
           ", worksheetVCthreadId = " + worksheetVCthreadId +
           ", worksheetCategoryId = " + worksheetCategoryId +
           ", worksheetCategory = " + worksheetCategory +
           ", worksheetLabel = " + worksheetLabel +
           ", points = " + points +
           ", courseId = " + courseId +
           ", courseVCThreadId = " + courseVCThreadId +
           ", classId = " + classId +
           ", classSyncId = " + classSyncId +
           ", result = " + result);
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
   * Writes the grades to the specified content handler. The <code>startDocument</code> and
   * <code>endDocument</code> methods are called before resp. after the XML is created.
   */

  public void toSAX (ContentHandler contentHandler)
    throws SAXException
  {
    this.toSAX(contentHandler, true);
  }
}