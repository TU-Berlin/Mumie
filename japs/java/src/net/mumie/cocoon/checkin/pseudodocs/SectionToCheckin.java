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

package net.mumie.cocoon.checkin.pseudodocs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import net.mumie.cocoon.checkin.CheckinException;
import net.mumie.cocoon.checkin.Content;
import net.mumie.cocoon.checkin.Master;
import net.mumie.cocoon.checkin.MasterException;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.Nature;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.pseudodocs.User;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.util.PasswordEncryptor;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class SectionToCheckin extends AbstractPseudoDocumentToCheckin
{
  /**
   * Role of this class as an Avalon service (<code>SectionToCheckin.class.getName()</code>
   */

  public static final String ROLE = SectionToCheckin.class.getName();

  /**
   * Hint for this class as an Avalon service (<code>"section"</code>).
   */

  public static final String HINT = "section";

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(SectionToCheckin.class);

  // --------------------------------------------------------------------------------
  // Lifecycle methods
  // --------------------------------------------------------------------------------
  
  /**
   * Creates a new instance.
   */

  public SectionToCheckin ()
  {
    this.nature = Nature.PSEUDO_DOCUMENT;
    this.type = PseudoDocType.SECTION;
    this.resetVariables();
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Recycles this instance. Resets the global variables and increases the recycle counters.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle()";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.resetVariables();
    this.instanceStatus.notifyRecycle();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Disposes this instance. Resets the global variables.
   */

  public void dispose ()
  {
    final String METHOD_NAME = "dispose()";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.resetVariables();
    this.instanceStatus.notifyDispose();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // Utilities
  // --------------------------------------------------------------------------------

  /**
   * Returns the id of the section this content object is contained in.
   */

  protected int getContainedIn (DbHelper dbHelper)
    throws SQLException 
  {
    final String METHOD_NAME = "getContainedIn";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.pathTokenizer.tokenize(this.path);
    this.pathTokenizer.tokenize(this.pathTokenizer.getSectionPath());
    String parentSectionPath = this.pathTokenizer.getSectionPath();
    int parentSectionId = dbHelper.getSectionIdForPath(parentSectionPath);
    this.logDebug(METHOD_NAME + " 2/2: Done. parentSectionId = " + parentSectionId);
    return parentSectionId;
  }

  // --------------------------------------------------------------------------------
  // Utilities to prepare the map with the checkin data
  // --------------------------------------------------------------------------------

  /**
   * Inserts the pure name into the specified map.
   */

  protected void pureNameToData (Map data)
    throws MasterException
  {
    this.pathTokenizer.tokenize(this.path);
    this.pathTokenizer.tokenize(this.pathTokenizer.getSectionPath());
    data.put(DbColumn.PURE_NAME, this.pathTokenizer.getPureName());
  }

  // --------------------------------------------------------------------------------
  // Checking existence
  // --------------------------------------------------------------------------------

  /**
   * Returns the id of the section with this path in the database, or
   * {@link Id#UNDEFINED Id.UNDEFINED} if there is no such section.
   */

  protected int getIdOfExisting (DbHelper dbHelper)
    throws SQLException, CheckinException
  {
    final String METHOD_NAME = "getIdOfExisting";
    this.logDebug(METHOD_NAME + " 1/2: Started");

    // Get the path of this section. - Note: this.path is the path of the master file of
    // this section, e.g., foo/bar/.meta.xml. But what we need below is the path of the
    // section, e.g. foo/bar.
    this.pathTokenizer.tokenize(this.path);
    String sectionPath = this.pathTokenizer.getSectionPath();

    ResultSet resultSet = dbHelper.queryPseudoDocDataByPath
      (this.type, sectionPath, new String[] {DbColumn.ID});
    int id = (resultSet.next() ? resultSet.getInt(DbColumn.ID) : Id.UNDEFINED);
    this.logDebug(METHOD_NAME + " 2/2: Done, id = " + id);
    return id;
  }

  // --------------------------------------------------------------------------------
  // Checkin
  // --------------------------------------------------------------------------------

  /**
   * Checkin stage 1. Checks-in all non-referential data, sets the id.
   */

  protected void checkin_1 (DbHelper dbHelper,
                            User user,
                            PasswordEncryptor encryptor,
                            Master defaults)
    throws CheckinException
  {
    try
      {
        final String METHOD_NAME = "checkin_1";
        this.logDebug(METHOD_NAME + " 1/2: Started");

        Map data = new HashMap();
        this.pureNameToData(data);
        this.nameToData(data);
        this.descriptionToData(data);
	this.hideToData(data);

        this.storeOrUpdateData(dbHelper, data);
        
        this.logDebug(METHOD_NAME + " 2/2: Done. this.id = " + this.id);
      }
    catch (Exception exception)
      {
        throw new CheckinException(this.getIdentification(), exception);
      }
  }

  // --------------------------------------------------------------------------------
  // Identification method 
  // --------------------------------------------------------------------------------

  /**
   * Returns a string that identificates this <code>SectionToCheckin</code> Instance. It has the
   * following form:<pre>
   *   "SectionToCheckin" +
   *   '#' + instanceId
   *   '(' + numberOfRecycles
   *   ',' + {@link #id id}
   *   ',' + {@link #path path}
   *   ')'</pre>
   * where <code>instanceId</code> is the instance id and
   * <code>numberOfRecycles</code> the number of recycles of this instance.
   */

  public String getIdentification ()
  {
    return
      "SectionToCheckin" +
      '#' + this.instanceStatus.getInstanceId() +
      '(' + this.instanceStatus.getNumberOfRecycles() +
      ',' + this.id +
      ',' + this.path +
      ')';
  }
}
