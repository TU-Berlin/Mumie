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
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.xml.MetaXMLElement;
import org.apache.avalon.excalibur.pool.Poolable;
import org.xml.sax.ContentHandler;

/**
 * Default implementation of {@link WorksheetTutorialBulkCorrector}.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: WorksheetTutorialBulkCorrectorImpl.java,v 1.6 2008/09/24 15:13:12 rassy Exp $</code>
 * @deprecated Use instead {@link net.mumie.cocoon.action.WorksheetTutorialBulkCorrectorAction}.
 */

public class WorksheetTutorialBulkCorrectorImpl extends AbstractBulkCorrector
  implements WorksheetTutorialBulkCorrector, Poolable
{
  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus =
    new ServiceStatus(WorksheetTutorialBulkCorrectorImpl.class);

  /**
   * The worksheet id.
   */

  protected int worksheetId = Id.UNDEFINED;

  /**
   * The tutorial id.
   */

  protected int tutorialId = Id.UNDEFINED;

  /**
   * Gets the needed data from the database.
   */

  protected ResultSet queryInputData (DbHelper dbHelper)
      throws SQLException
  {
    return dbHelper.queryWorksheetTutorialBulkCorrectionInputData
      (this.worksheetId, this.tutorialId);
  }

  /**
   * Sets the worksheet id and the tutorial id as attributes.
   */

  protected void addRootElementAttribs (MetaXMLElement xmlElement)
  {
    xmlElement.addAttribute(XMLAttribute.WORKSHEET_ID, this.worksheetId);
    xmlElement.addAttribute(XMLAttribute.TUTORIAL_ID, this.tutorialId);
  }


  /**
   * Carries out the bulk correction for the specified worksheet and tutorial. If
   * <code>force</code> is true, problems are corrected even if the correction exists and is
   * up-to-date. A protocol of the correction is written in XML form to the specified
   * content handler. If <code>ownDocument</code> is false, the content handlers
   * <code>startDocument</code> and <code>endDocument</code> methods are not called before
   * resp. after the XML is written.
   */

  public void bulkCorrect (int worksheetId,
                           int tutorialId,
                           boolean force,
                           ContentHandler contentHandler,
                           boolean ownDocument)
    throws BulkCorrectionException
  {
    try
      {
        final String METHOD_NAME = "bulkCorrect";
        this.logDebug
          (METHOD_NAME + " 1/2: Started." +
           " worksheetId = " + worksheetId +
           " tutorialId = " + tutorialId +
           ", force + " + force);
        this.worksheetId = worksheetId;
        this.tutorialId = tutorialId;
        this.doCorrection(force, contentHandler, ownDocument);
        this.logDebug(METHOD_NAME + " 2/2: Done");
      }
    finally
      {
        this.worksheetId = Id.UNDEFINED;
        this.tutorialId = Id.UNDEFINED;
      }
  }

  /**
   * Creates a new <code>WorksheetTutorialBulkCorrectorImpl</code> instance.
   */

  public WorksheetTutorialBulkCorrectorImpl ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }
  /**
   * Returns a string that identifies this instance. It has the
   * following form:<pre>
   *   "WorksheetTutorialBulkCorrectorImpl" +
   *   '#' + instanceId
   *   '(' + lifecycleStatus
   *   ')'</pre>
   * where <code>instanceId</code> is the instance id and <code>lifecycleStatus</code> the
   * lifecycle status.
   */

  public String getIdentification ()
  {
    return
      "WorksheetTutorialBulkCorrectorImpl" +
      '#' + this.instanceStatus.getInstanceId() +
      '(' + this.instanceStatus.getLifecycleStatus() +
      ')';
  }
}
