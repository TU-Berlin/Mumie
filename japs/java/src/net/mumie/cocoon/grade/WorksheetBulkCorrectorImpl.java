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
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import org.apache.avalon.excalibur.pool.Poolable;
import org.xml.sax.ContentHandler;

/**
 * Default implementation of {@link WorksheetBulkCorrector}.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: WorksheetBulkCorrectorImpl.java,v 1.9 2008/09/24 15:13:12 rassy Exp $</code>
 */

public class WorksheetBulkCorrectorImpl extends AbstractBulkCorrector
  implements WorksheetBulkCorrector, Poolable 
{
  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(WorksheetBulkCorrectorImpl.class);

  /**
   * The worksheet id.
   */

  protected int worksheetId = Id.UNDEFINED;

  /**
   * Gets the needed data from the database.
   */

  protected ResultSet queryInputData (DbHelper dbHelper)
      throws SQLException
  {
    return dbHelper.queryWorksheetBulkCorrectionInputData(this.worksheetId);
  }

  /**
   * Carries out the bulk correction for the specified worksheet. If <code>force</code> is
   * true, problems are corrected even if the correction exists and is up-to-date. A
   * protocol of the correction is written in XML form to the specified content handler. If
   * <code>ownDocument</code> is false, the content handlers <code>startDocument</code> and
   * <code>endDocument</code> methods are not called before resp. after the XML is written.
   */

  public void bulkCorrect (int worksheetId,
                           boolean force,
                           ContentHandler contentHandler,
                           boolean ownDocument)
    throws BulkCorrectionException
  {
    try
      {
        final String METHOD_NAME = "bulkCorrect";
        this.logDebug
          (METHOD_NAME + " 1/2: Started. worksheetId = " + worksheetId + ", force + " + force);
        this.worksheetId = worksheetId;
        this.doCorrection(force, contentHandler, ownDocument);
        this.logDebug(METHOD_NAME + " 2/2: Done");
      }
    finally
      {
        this.worksheetId = Id.UNDEFINED;
      }
  }

  /**
   * Creates a new <code>WorksheetBulkCorrectorImpl</code> instance.
   */

  public WorksheetBulkCorrectorImpl ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Returns a string that identifies this instance. It has the
   * following form:<pre>
   *   "WorksheetBulkCorrectorImpl" +
   *   '#' + instanceId
   *   '(' + lifecycleStatus
   *   ')'</pre>
   * where <code>instanceId</code> is the instance id and <code>lifecycleStatus</code> the
   * lifecycle status.
   */

  public String getIdentification ()
  {
    return
      "WorksheetBulkCorrectorImpl" +
      '#' + this.instanceStatus.getInstanceId() +
      '(' + this.instanceStatus.getLifecycleStatus() +
      ')';
  }
}
