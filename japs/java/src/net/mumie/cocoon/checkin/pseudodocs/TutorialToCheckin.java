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
import net.mumie.cocoon.checkin.Master;
import net.mumie.cocoon.checkin.MasterException;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.Nature;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.pseudodocs.User;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.util.PasswordEncryptionException;
import net.mumie.cocoon.util.PasswordEncryptor;
import org.w3c.dom.Element;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class TutorialToCheckin extends AbstractPseudoDocumentToCheckin
{
  /**
   * Role of this class as an Avalon service (<code>TutorialToCheckin.class.getName()</code>
   */

  public static final String ROLE = TutorialToCheckin.class.getName();

  /**
   * Hint for this class as an Avalon service (<code>"user"</code>).
   */

  public static final String HINT = "user_group";

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(TutorialToCheckin.class);

  // --------------------------------------------------------------------------------
  // Lifecycle methods
  // --------------------------------------------------------------------------------
  
  /**
   * Creates a new instance.
   */

  public TutorialToCheckin ()
  {
    this.nature = Nature.PSEUDO_DOCUMENT;
    this.type = PseudoDocType.TUTORIAL;
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
  // Checking-in tutor
  // --------------------------------------------------------------------------------

  /**
   * Checks-in the e-learning class
   */

  protected void checkinELClass (DbHelper dbHelper)
    throws MasterException, SQLException, CheckinException
  {
    final String METHOD_NAME = "checkinELClass";
    this.logDebug(METHOD_NAME + " 1/3: Started");

    String elClassPath = this.master.getELClassPath();
    if ( elClassPath == null )
      this.logDebug(METHOD_NAME + " 2/3: No e-learning class specified");
    else
      {
        ResultSet resultSet = dbHelper.queryPseudoDocDataByPath
          (PseudoDocType.CLASS, elClassPath, new String[] {DbColumn.ID});
        if ( !resultSet.next() )
          throw new CheckinException("No e-learning class found for path: " + elClassPath);
        int elClassId = resultSet.getInt(DbColumn.ID);
        this.logDebug(METHOD_NAME + " 2/3: elClassId = " + elClassId);
        dbHelper.updatePseudoDocDatum
          (PseudoDocType.TUTORIAL, this.id, DbColumn.CLASS, elClassId);
      }
    this.logDebug(METHOD_NAME + " 3/3: Done");
  }

  /**
   * Checks-in the tutor
   */

  protected void checkinTutor (DbHelper dbHelper)
    throws SQLException, MasterException, CheckinException
  {
    final String METHOD_NAME = "checkinTutor";
    this.logDebug(METHOD_NAME + " 1/3: Started");

    String tutorPath = this.master.getTutorPath();
    if ( tutorPath == null )
      this.logDebug(METHOD_NAME + " 2/3: No tutor specified");
    else
      {
        ResultSet resultSet = dbHelper.queryPseudoDocDataByPath
          (PseudoDocType.USER, tutorPath, new String[] {DbColumn.ID});
        if ( !resultSet.next() )
          throw new CheckinException("No Tutor found for path: " + tutorPath);
        int tutorId = resultSet.getInt(DbColumn.ID);
        this.logDebug(METHOD_NAME + " 2/3: tutorId = " + tutorId);
        dbHelper.updatePseudoDocDatum
          (this.type, this.id, DbColumn.TUTOR, tutorId);
      }
    this.logDebug(METHOD_NAME + " 3/3: Done");
  }

  // --------------------------------------------------------------------------------
  // Checking-in members
  // --------------------------------------------------------------------------------

  /**
   * Adds this user to the user groups specified in the master.
   */

  protected void checkinMembers (DbHelper dbHelper)
    throws MasterException, CheckinException, SQLException
  {
    final String METHOD_NAME = "checkinTutorialMembers";
    this.logDebug
      (METHOD_NAME + " 1/2: Started");
    Master[] members = this.master.getMembers();
    for (int i = 0; i < members.length; i++)
      {
        Master member = members[i];
        if ( member.getNature() != Nature.PSEUDO_DOCUMENT ||
             member.getType() != PseudoDocType.USER )
          throw new CheckinException
            ("Expected a user reference, but found: " + member.getTypeName());
        String memberPath = member.getPath();
	int memberId = this.getPseudoDocIdForPath
	  (PseudoDocType.USER, memberPath, this.path, dbHelper);
        dbHelper.addMemberToTutorial(memberId, this.id);
      }
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

        this.checkinELClass(dbHelper);
        this.checkinTutor(dbHelper);
        this.checkinMembers(dbHelper);

        this.logDebug(METHOD_NAME + " 2/2: Done");
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
   * Returns a string that identificates this <code>TutorialToCheckin</code> Instance. It has the
   * following form:<pre>
   *   "TutorialToCheckin" +
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
      "TutorialToCheckin" +
      '#' + this.instanceStatus.getInstanceId() +
      '(' + this.instanceStatus.getNumberOfRecycles() +
      ',' + this.id +
      ',' + this.path +
      ')';
  }
}
