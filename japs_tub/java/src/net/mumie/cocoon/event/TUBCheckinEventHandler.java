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

package net.mumie.cocoon.event;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.msg.TUBWorksheetsMessage;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.Nature;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.RefType;
import net.mumie.cocoon.notions.UserGroupName;
import net.mumie.cocoon.service.AbstractJapsServiceable;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.util.LogUtil;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.thread.ThreadSafe;

public class TUBCheckinEventHandler extends AbstractJapsServiceable
  implements ThreadSafe, Configurable, EventHandler
{
  // --------------------------------------------------------------------------------
  // h1: Global constants and variables
  // --------------------------------------------------------------------------------

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus =
    new ServiceStatus(TUBCheckinEventHandler.class);

  /**
   * The id of the CoMa class.
   */

  protected int comaClassId;

  // --------------------------------------------------------------------------------
  // h1: Lifecycle related methods
  // --------------------------------------------------------------------------------

  /**
   * Creates a new instance.
   */

  public TUBCheckinEventHandler ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Configures this instance.
   */

  public void configure (Configuration configuration)
    throws ConfigurationException
  {
    final String METHOD_NAME = "configure";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    try
      {
        this.comaClassId = Integer.parseInt(configuration.getChild("coma-class-id").getValue());
        this.logDebug(METHOD_NAME + " 2/2: Done. comaClassId = " + comaClassId);
      }
    catch (Exception exception)
      {
        throw new ConfigurationException("Wrapped exception", exception);
      }
  }

  // --------------------------------------------------------------------------------
  // h1: Event handling
  // --------------------------------------------------------------------------------

  /**
   * Handles the event.
   */

  public void handle (Event event)
  {
    final String METHOD_NAME = "handle";
    this.logDebug(METHOD_NAME + " 1/3: Started");

    DbHelper dbHelper = null;
    TUBWorksheetsMessage message = null;

    try
      {
        // Init DbHelper:
        dbHelper = (DbHelper)this.serviceManager.lookup(DbHelper.ROLE);

        // Get checked-in courses:
        List<Integer> courseIdList = new LinkedList<Integer>();
        for (CheckinEvent.Item item : ((CheckinEvent)event).getItems())
          {
            if ( item.getNature() == Nature.DOCUMENT && item.getType() == DocType.COURSE )
              courseIdList.add(item.getId());
          }

        if ( courseIdList.isEmpty() )
          {
            this.logDebug(METHOD_NAME + " 2/3: No courses have been checked in");
            this.logDebug(METHOD_NAME + " 3/3: Done");
          }
        else
          {
            int[] courseIds = listToArray(courseIdList);

            // Find CoMa course(s):
            List<Integer> comaCourseIdList = new LinkedList<Integer>();
            ResultSet courseData = dbHelper.queryData
              (DocType.COURSE, courseIds, new String[] {DbColumn.ID, DbColumn.CLASS});
            while ( courseData.next() )
              {
                if ( courseData.getInt(DbColumn.CLASS) == this.comaClassId )
                  comaCourseIdList.add(courseData.getInt(DbColumn.ID));
              }
            int[] comaCourseIds = listToArray(comaCourseIdList);

            // Get corresponding worksheets:
            List<Integer> comaWorksheetIdList = new LinkedList<Integer>();
            for (int comaCourseId : comaCourseIds)
              {
                ResultSet worksheetData = dbHelper.queryDataOfReferencedDocs
                  (DocType.COURSE, comaCourseId,
                   DocType.WORKSHEET,
                   RefType.COMPONENT,
                   new String[] {DbColumn.ID});
                while ( worksheetData.next() )
                  comaWorksheetIdList.add(worksheetData.getInt(DbColumn.ID));
              }
            int[] comaWorksheetIds = listToArray(comaWorksheetIdList);

            this.logDebug
              (METHOD_NAME + "2/3:" +
               " courseIds = " + LogUtil.arrayToString(courseIds) +
               ", comaCourseIds = " + LogUtil.arrayToString(comaCourseIds) +
               ", comaWorksheetIds = " + LogUtil.arrayToString(comaWorksheetIds));

            message = (TUBWorksheetsMessage)this.serviceManager.lookup(TUBWorksheetsMessage.ROLE);
            message.setup(comaWorksheetIds, "coma/worksheets");
            boolean success = message.send();
            this.logDebug(METHOD_NAME + " 3/3: Done. Sent message. success = " + success);
          }
      }
    catch (Exception exception)
      {
        this.logError("Event handling failed", exception);
      }
    finally
      {
        if ( dbHelper != null ) this.serviceManager.release(dbHelper);
        if ( message != null ) this.serviceManager.release(message);
      }
  }

  // --------------------------------------------------------------------------------
  // h1: Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * 
   */

  protected static int[] listToArray (List<Integer> list)
  {
    int[] array = new int[list.size()];
    int i = -1;
    for (Integer value : list)
      array[++i] = value.intValue();
    return array;
  }

}