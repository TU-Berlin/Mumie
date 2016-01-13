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

public class UserToCheckin extends AbstractPseudoDocumentToCheckin
{
  /**
   * Role of this class as an Avalon service (<code>UserToCheckin.class.getName()</code>
   */

  public static final String ROLE = UserToCheckin.class.getName();

  /**
   * Hint for this class as an Avalon service (<code>"user"</code>).
   */

  public static final String HINT = "user";

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(UserToCheckin.class);

  // --------------------------------------------------------------------------------
  // Lifecycle methods
  // --------------------------------------------------------------------------------
  
  /**
   * Creates a new instance.
   */

  public UserToCheckin ()
  {
    this.nature = Nature.PSEUDO_DOCUMENT;
    this.type = PseudoDocType.USER;
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
  // Checking-in user groups
  // --------------------------------------------------------------------------------

  /**
   * Adds this user to the user groups specified in the master.
   */

  protected void checkinUserGroupMemberships (DbHelper dbHelper)
    throws MasterException, CheckinException, SQLException
  {
    final String METHOD_NAME = "checkinUserGroupMemberships";
    this.logDebug(METHOD_NAME + " 1/2: Started");

    // If this is a re-checkin, we clear all group memberships:
    dbHelper.removeMemberFromAllUserGroups(this.id);

    // (New) group memberships:
    Master[] userGroups = this.master.getUserGroups();
    for (int i = 0; i < userGroups.length; i++)
      {
        Master userGroup = userGroups[i];
        if ( userGroup.getNature() != Nature.PSEUDO_DOCUMENT ||
             userGroup.getType() != PseudoDocType.USER_GROUP )
          throw new CheckinException
            ("Expected a user group reference, but found: " + userGroup.getTypeName());
        String userGroupPath = userGroup.getPath();
        int userGroupId = this.getPseudoDocIdForPath
          (PseudoDocType.USER_GROUP, userGroupPath, this.path, dbHelper);
        dbHelper.addMemberToUserGroup(this.id, userGroupId);
      }

    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Gives this user the theme specified in the master.
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
        this.descriptionToData(data);
        this.loginNameToData(data);
        this.passwordToData(data, encryptor);
        this.firstNameToData(data);
        this.surnameToData(data);
        this.emailToData(data);

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

	super.checkin_3(dbHelper,user,encryptor,defaults);
        this.checkinUserGroupMemberships(dbHelper);
        this.checkinTheme(dbHelper);

        this.logDebug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
        throw new CheckinException(this.getIdentification(), exception);
      }
  }

  // --------------------------------------------------------------------------------
  // Utilities to prepare the map with the checkin data
  // --------------------------------------------------------------------------------

  /**
   * Inserts the login name into the specified map.
   */

  protected void loginNameToData (Map data)
    throws MasterException
  {
    data.put(DbColumn.LOGIN_NAME, this.master.getLoginName());
  }

  /**
   * Inserts the password into the specified map. The password is encrypted by the specified
   * encryptor.
   */

  protected void passwordToData (Map data, PasswordEncryptor encryptor)
    throws MasterException, PasswordEncryptionException 
  {
    data.put(DbColumn.PASSWORD, encryptor.encrypt(this.master.getPassword()));
  }

  /**
   * Inserts the first name into the specified map.
   */

  protected void firstNameToData (Map data)
    throws MasterException
  {
    data.put(DbColumn.FIRST_NAME, this.master.getFirstName());
  }

  /**
   * Inserts the surname into the specified map.
   */

  protected void surnameToData (Map data)
    throws MasterException
  {
    data.put(DbColumn.SURNAME, this.master.getSurname());
  }

  /**
   * Inserts the email into the specified map.
   */

  protected void emailToData (Map data)
    throws MasterException
  {
    data.put(DbColumn.EMAIL, this.master.getEmail());
  }

  // --------------------------------------------------------------------------------
  // Identification method 
  // --------------------------------------------------------------------------------

  /**
   * Returns a string that identificates this <code>UserToCheckin</code> Instance. It has the
   * following form:<pre>
   *   "UserToCheckin" +
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
      "UserToCheckin" +
      '#' + this.instanceStatus.getInstanceId() +
      '(' + this.instanceStatus.getNumberOfRecycles() +
      ',' + this.id +
      ',' + this.path +
      ')';
  }
}
