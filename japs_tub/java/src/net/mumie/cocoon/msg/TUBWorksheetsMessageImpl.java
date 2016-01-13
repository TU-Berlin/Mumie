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

package net.mumie.cocoon.msg;

import java.io.StringWriter;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.msg.AbstractPostMessage;
import net.mumie.cocoon.msg.MessageDestination;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.util.LogUtil;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.commons.httpclient.methods.PostMethod;

public class TUBWorksheetsMessageImpl extends AbstractPostMessage
  implements TUBWorksheetsMessage, Recyclable, Disposable
{
  // --------------------------------------------------------------------------------
  // h1: Global constants and variables
  // --------------------------------------------------------------------------------

  /**
   * The id of the worksheets.
   */

  protected int[] worksheetIds = null;

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus =
    new ServiceStatus(TUBWorksheetsMessageImpl.class);

  /**
   * 
   */

  public static final String COMA_CLASS_SYNC_ID = "coma-i-ws-08-09";

  // --------------------------------------------------------------------------------
  // h1: Lifecycle related methods
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>TUBWorksheetsMessageImpl</code> instance.
   */

  public TUBWorksheetsMessageImpl ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Initializes this instance
   */

  public void setup (int[] worksheetIds, String destinationName)
  {
    final String METHOD_NAME = "setup";
    this.logDebug
      (METHOD_NAME + " 1/2: Started." +
       " worksheetIds = " + LogUtil.arrayToString(worksheetIds) +
       ", destinationName = " + destinationName);
    this.worksheetIds = worksheetIds;
    this.destinationName = destinationName;
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Calls the super-class reset method and resets the user and worksheet ids
   * ({@link #userId userId} and {@link #worksheetId worksheetId}, respectively).
   */

  protected void reset ()
  {
    super.reset();
    this.worksheetIds = null;
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
  // h1: Sending
  // --------------------------------------------------------------------------------

  /**
   * Adds the contents of the grade to the message.
   */

  protected void init (PostMethod method, MessageDestination destination)
    throws Exception
  {
    final String METHOD_NAME = "init";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    DbHelper dbHelper = null;
    try
      {
        dbHelper = (DbHelper)this.serviceManager.lookup(DbHelper.ROLE);
        ResultSet resultSet = dbHelper.queryWorksheetContext(this.worksheetIds);
        StringWriter out = new StringWriter();
        Map<Integer,Integer> courseVCThreads = new HashMap<Integer,Integer>();
        int comaClassId = -1;
        out.write("<?xml version=\"1.0\" encoding=\"ASCII\"?>");
        out.write("<grades:worksheets xmlns:grades=\"http://www.mumie.net/xml-namespace/grades\">");
        while ( resultSet.next() )
          {
            Integer courseId = new Integer(resultSet.getInt(DbColumn.COURSE_ID));
            String label = resultSet.getString(DbColumn.LABEL);
            int points = resultSet.getInt(DbColumn.POINTS);

            int classId = resultSet.getInt(DbColumn.CLASS_ID);
            if ( comaClassId == -1 )
              comaClassId = classId;
            else if ( classId != comaClassId )
              throw new IllegalArgumentException
                ("Multiple classes: " + comaClassId + ", " + classId);
            
            if ( !label.trim().endsWith(".1") )
              {
                out.write
                  ("<grades:worksheet" +
                   " id=\"" + resultSet.getInt(DbColumn.ID) + "\"" +
                   " vc_thread_id=\"" + resultSet.getInt(DbColumn.VC_THREAD_ID) + "\"" +
                   " label=\"" + label + "\"" +
                   " points=\"" + points + "\"" +
                   " course_id=\"" + courseId + "\"" +
                   "/>");
                if ( !courseVCThreads.containsKey(courseId) )
                  courseVCThreads.put
                    (courseId, new Integer(resultSet.getInt(DbColumn.COURSE_VC_THREAD_ID)));
              }
          }

        for ( Map.Entry entry : courseVCThreads.entrySet() )
          {
            out.write
              ("<grades:course" +
               " id=\"" + entry.getKey() + "\"" +
               " vc_thread_id=\"" + entry.getValue() + "\"" +
               " class_id=\"" + comaClassId + "\"" +
               "/>");
          }

        out.write
          ("<grades:class" +
           " id=\"" + comaClassId + "\"" +
           " sync_id=\"" + COMA_CLASS_SYNC_ID + "\"" +
           "/>");

        out.write("</grades:worksheets>");
        method.addParameter("body", out.toString());
        this.logDebug(METHOD_NAME + " 2/2: Done");
      }
    finally
      {
        this.serviceManager.release(dbHelper);
      }
  }
}