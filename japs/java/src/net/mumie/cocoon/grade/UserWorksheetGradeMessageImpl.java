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

import net.mumie.cocoon.msg.AbstractPostMessage;
import net.mumie.cocoon.msg.MessageDestination;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.util.LogUtil;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.activity.Disposable;

public class UserWorksheetGradeMessageImpl extends AbstractPostMessage
  implements UserWorksheetGradeMessage, Recyclable, Disposable
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
    new ServiceStatus(UserWorksheetGradeMessageImpl.class);

  // --------------------------------------------------------------------------------
  // h1: Lifecycle related methods
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>UserWorksheetGradeMessageImpl</code> instance.
   */

  public UserWorksheetGradeMessageImpl ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Initializes this instance
   * @param userId id of the user
   * @param worksheetId id of the worksheet
   * @param destinationName the destination name
   */

  public void setup (int userId, int worksheetId, String destinationName)
  {
    final String METHOD_NAME = "setup";
    this.logDebug
      (METHOD_NAME + " 1/2: Started." +
       " userId = " + userId +
       ", worksheetId = " + worksheetId +
       ", destinationName = " + destinationName);
    this.userId = userId;
    this.worksheetId = worksheetId;
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
    this.userId = Id.UNDEFINED;
    this.worksheetId = Id.UNDEFINED;
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
    UserWorksheetGrade grade = null;
    try
      {
        grade = (UserWorksheetGrade)this.serviceManager.lookup(UserWorksheetGrade.ROLE);
	grade.setup(this.userId, this.worksheetId);
        method.addParameter("body", getXMLCode(grade));
        this.logDebug(METHOD_NAME + " 2/2: Done");
      }
    finally
      {
        this.serviceManager.release(grade);
      }
  }
}