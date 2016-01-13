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

package net.mumie.cocoon.generators;

import java.sql.ResultSet;
import java.text.DateFormat;
import java.util.Date;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.AnnType;
import net.mumie.cocoon.notions.TimeFormat;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.notions.XMLNamespace;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.util.ParamUtil;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.xml.GeneralXMLElement;

public class UserProblemDataIndexGenerator extends ServiceableJapsGenerator
{
  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus =
    new ServiceStatus(UserProblemDataIndexGenerator.class);

  /**
   * Helper tho write XML elements to SAX.
   */

  protected GeneralXMLElement xmlElement = new GeneralXMLElement
    (XMLNamespace.URI_GRADES, XMLNamespace.PREFIX_GRADES);

  /**
   * Creates a new <code>UserProblemDataIndexGenerator</code> instance.
   */

  public UserProblemDataIndexGenerator ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Recycles this instance. Essentially, this method only calls the superclass recycle
   * method. Besides this, the instance status id notified about the recycle, and log
   * messages are written.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    super.recycle();
    this.xmlElement.reset();
    this.instanceStatus.notifyRecycle();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Disposes this instance. Essentially, this method only calls the superclass dispose
   * method. Besides this, the instance status id notified about the dispose, and log
   * messages are written.
   */

  public void dispose ()
  {
    final String METHOD_NAME = "dispose";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    super.dispose();
    this.xmlElement.reset();
    this.instanceStatus.notifyDispose();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Generates the XML. See class documentation for details.
   */

  public void generate ()
    throws ProcessingException 
  {
    final String METHOD_NAME = "generate";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    DbHelper dbHelper = null;
    try
      {
        int userId = ParamUtil.getAsInt(this.parameters, "user");
        DateFormat dateFormat = ParamUtil.getAsDateFormat(parameters, "time-format", TimeFormat.DEFAULT); 
        dbHelper = (DbHelper)this.serviceManager.lookup(DbHelper.ROLE);
        ResultSet resultSet = dbHelper.queryUserProblemDataIndex(userId);

        // Start XML document:
        this.contentHandler.startDocument();

        // Start root element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.USER_PROBLEM_DATA_INDEX);
        this.xmlElement.addAttribute(XMLAttribute.USER_ID, userId);
        this.xmlElement.startToSAX(this.contentHandler);

        // Write the data:
        while ( resultSet.next() )
          {
            this.xmlElement.reset();
            this.xmlElement.setLocalName(XMLElement.USER_PROBLEM_DATA);
            this.xmlElement.addAttribute
              (XMLAttribute.COURSE_ID, resultSet.getInt(DbColumn.COURSE_ID));
            this.xmlElement.addAttribute
              (XMLAttribute.COURSE_NAME, resultSet.getString(DbColumn.COURSE_NAME));
            this.xmlElement.addAttribute
              (XMLAttribute.WORKSHEET_ID, resultSet.getInt(DbColumn.WORKSHEET_ID));
            this.xmlElement.addAttribute
              (XMLAttribute.WORKSHEET_LABEL, resultSet.getString(DbColumn.WORKSHEET_LABEL));
            this.xmlElement.addAttribute
              (XMLAttribute.PROBLEM_ID, resultSet.getInt(DbColumn.PROBLEM_ID));
            this.xmlElement.addAttribute
              (XMLAttribute.PROBLEM_LABEL, resultSet.getString(DbColumn.PROBLEM_LABEL));
            this.xmlElement.addAttribute
              (XMLAttribute.TYPE, getAnnTypeName(resultSet.getInt(DbColumn.ANN_TYPE)));
            Date lastModified = resultSet.getTimestamp(DbColumn.LAST_MODIFIED);
            this.xmlElement.addAttribute
              (XMLAttribute.LAST_MODIFIED, dateFormat.format(lastModified));
            this.xmlElement.addAttribute
              (XMLAttribute.LAST_MODIFIED_RAW, lastModified.getTime());
            float score = resultSet.getFloat(DbColumn.SCORE);
            if ( !resultSet.wasNull() )
              this.xmlElement.addAttribute(XMLAttribute.SCORE, score);
            this.xmlElement.toSAX(this.contentHandler);
          }

        // Close root element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.USER_PROBLEM_DATA_INDEX);
        this.xmlElement.endToSAX(this.contentHandler);

        // Close XML document:
        this.contentHandler.endDocument();

        this.logDebug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
        throw new ProcessingException(exception);
      }
    finally
      {
        if ( dbHelper != null ) this.serviceManager.release(dbHelper);
      }
  }

  /**
   * Returns the name of the annotation type with the specified code, or "unknown" if the
   * code does not represent a known annotation type.
   */

  protected static String getAnnTypeName (int code)
  {
    return
      (code >= AnnType.first && code <= AnnType.last
       ? AnnType.nameFor[code]
       : "unknown");
  }
}
