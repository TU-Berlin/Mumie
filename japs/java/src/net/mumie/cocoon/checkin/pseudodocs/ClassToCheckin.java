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
import net.mumie.cocoon.notions.Nature;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.pseudodocs.User;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.util.PasswordEncryptor;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class ClassToCheckin extends AbstractPseudoDocumentToCheckin
{
  /**
   * Role of this class as an Avalon service (<code>ClassToCheckin.class.getName()</code>
   */

  public static final String ROLE = ClassToCheckin.class.getName();

  /**
   * Hint for this class as an Avalon service (<code>"class"</code>).
   */

  public static final String HINT = "class";

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(ClassToCheckin.class);

  // --------------------------------------------------------------------------------
  // Lifecycle methods
  // --------------------------------------------------------------------------------
  
  /**
   * Creates a new instance.
   */

  public ClassToCheckin ()
  {
    this.nature = Nature.PSEUDO_DOCUMENT;
    this.type = PseudoDocType.CLASS;
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

  /**
   * Gives this class the theme specified in the master.
   */

  protected void checkinTheme (DbHelper dbHelper)
    throws MasterException, CheckinException, SQLException
  {
    final String METHOD_NAME = "checkinTheme";
    this.logDebug
      (METHOD_NAME + " 1/2: Started");
    Master theme = this.master.getTheme();
    if ( theme != null )
      {
	this.logDebug
	  (METHOD_NAME + " found Theme Master");
	String themePath = theme.getPath();
	int themeId = this.getPseudoDocIdForPath
	  (PseudoDocType.THEME, themePath, this.path, dbHelper);
	dbHelper.updatePseudoDocDatum(this.type,this.id,DbColumn.THEME,themeId);
      }
    else
      this.logDebug
	(METHOD_NAME + " no Theme Master found");
    this.logDebug(METHOD_NAME + " 2/2: Done");
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

        this.storeOrUpdateData(dbHelper, data);
        
        this.logDebug(METHOD_NAME + " 2/2: Done. this.id = " + this.id);
      }
    catch (Exception exception)
      {
        throw new CheckinException(this.getIdentification(), exception);
      }
  }

  /**
   * Checkin stage 3. Checks-in all referential data.
   */

  protected void checkin_3 (DbHelper dbHelper,
                            User user,
                            PasswordEncryptor encryptor,
                            Master defaults)
    throws CheckinException
  {
    try
      {
        final String METHOD_NAME = "checkin_3";
        this.logDebug(METHOD_NAME + " 1/2: Started");

        this.checkinSemester(dbHelper);
        this.checkinTheme(dbHelper);
        this.checkinReadPermissions(dbHelper, defaults);
        this.checkinWritePermissions(dbHelper, defaults);
        this.logDebug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
        throw new CheckinException(this.getIdentification(), exception);
      }
  }

  // --------------------------------------------------------------------------------
  // Checking-in semester
  // --------------------------------------------------------------------------------

  /**
   * Checks-in the semester
   */

  protected void checkinSemester (DbHelper dbHelper)
    throws SQLException, MasterException, CheckinException
  {
    final String METHOD_NAME = "checkinSemester";
    this.logDebug(METHOD_NAME + " 1/3: Started");

    String semesterPath = this.master.getSemesterPath();
    if ( semesterPath == null )
      this.logDebug(METHOD_NAME + " 2/3: No semester specified");
    else
      {
        ResultSet resultSet = dbHelper.queryPseudoDocDataByPath
          (PseudoDocType.SEMESTER, semesterPath, new String[] {DbColumn.ID});
        if ( !resultSet.next() )
          throw new CheckinException("No Semester found for path: " + semesterPath);
        int semesterId = resultSet.getInt(DbColumn.ID);
        this.logDebug(METHOD_NAME + " 2/3: semesterId = " + semesterId);
        dbHelper.updatePseudoDocDatum
          (this.type, this.id, DbColumn.SEMESTER, semesterId);
      }
    this.logDebug(METHOD_NAME + " 3/3: Done");
  }

  // --------------------------------------------------------------------------------
  // Identification method 
  // --------------------------------------------------------------------------------

  /**
   * Returns a string that identificates this <code>ClassToCheckin</code> Instance. It has the
   * following form:<pre>
   *   "ClassToCheckin" +
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
      "ClassToCheckin" +
      '#' + this.instanceStatus.getInstanceId() +
      '(' + this.instanceStatus.getNumberOfRecycles() +
      ',' + this.id +
      ',' + this.path +
      ')';
  }
}
