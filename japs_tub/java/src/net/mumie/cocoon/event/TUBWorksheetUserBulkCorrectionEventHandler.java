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
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.grade.UserWorksheetGradeMessage;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.UserGroupName;
import net.mumie.cocoon.service.AbstractJapsServiceable;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.thread.ThreadSafe;

public class TUBWorksheetUserBulkCorrectionEventHandler extends AbstractJapsServiceable
  implements ThreadSafe, Configurable, EventHandler
{
  // --------------------------------------------------------------------------------
  // h1: Global constants and variables
  // --------------------------------------------------------------------------------

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus =
    new ServiceStatus(TUBWorksheetUserBulkCorrectionEventHandler.class);

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

  public TUBWorksheetUserBulkCorrectionEventHandler ()
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

  public void handle (Event rawEvent)
  {
    final String METHOD_NAME = "handle";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    DbHelper dbHelper = null;
    UserWorksheetGradeMessage message = null;
    try
      {
        WorksheetUserBulkCorrectionEvent event =
          (WorksheetUserBulkCorrectionEvent)rawEvent;

        int worksheetId = event.getWorksheetId();
        int userId = event.getUserId();

        dbHelper = (DbHelper)this.serviceManager.lookup(DbHelper.ROLE);
        ResultSet worksheetData = dbHelper.queryWorksheetContext(worksheetId);
        if ( !worksheetData.next() )
          throw new IllegalArgumentException("Worksheet not found in database: " + worksheetId);
        int classId = worksheetData.getInt(DbColumn.CLASS_ID);
        if ( worksheetData.wasNull() || classId != this.comaClassId )
          this.logDebug(METHOD_NAME + " 2/2: Done. Skipped message: Not the CoMa class");
        else if ( worksheetData.getString(DbColumn.LABEL).trim().endsWith(".1") )
          this.logDebug(METHOD_NAME + " 2/2: Done. Skipped message: Demo worksheet");
        else if ( !dbHelper.checkUserGroupMembership(userId, UserGroupName.STUDENTS) )
          this.logDebug(METHOD_NAME + " 2/2: Done. Skipped message: User is not a student");
        else if ( dbHelper.getPseudoDocDatumAsString
                  (PseudoDocType.USER, userId, DbColumn.SYNC_ID) == null )
          this.logDebug(METHOD_NAME + " 2/2: Done. Skipped message: User has no sync id");
        else
          {
            message = (UserWorksheetGradeMessage)this.serviceManager.lookup
              (UserWorksheetGradeMessage.ROLE);
            message.setup(userId, worksheetId, "coma/user-problem-grade");
            boolean success = message.send();
            this.logDebug(METHOD_NAME + " 2/2: Done. Sent message. success = " + success);
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

}