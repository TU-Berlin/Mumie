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

public class UserGroupToCheckin extends AbstractPseudoDocumentToCheckin
{
  /**
   * Role of this class as an Avalon service (<code>UserGroupToCheckin.class.getName()</code>
   */

  public static final String ROLE = UserGroupToCheckin.class.getName();

  /**
   * Hint for this class as an Avalon service (<code>"user"</code>).
   */

  public static final String HINT = "user_group";

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(UserGroupToCheckin.class);

  // --------------------------------------------------------------------------------
  // Lifecycle methods
  // --------------------------------------------------------------------------------
  
  /**
   * Creates a new instance.
   */

  public UserGroupToCheckin ()
  {
    this.nature = Nature.PSEUDO_DOCUMENT;
    this.type = PseudoDocType.USER_GROUP;
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
  // Checking-in members
  // --------------------------------------------------------------------------------

  /**
   * Adds this user to the user groups specified in the master.
   */

  protected void checkinMembers (DbHelper dbHelper)
    throws MasterException, CheckinException, SQLException
  {
    final String METHOD_NAME = "checkinUserGroupMembers";
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
        dbHelper.addMemberToUserGroup(memberId, this.id);
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
        this.createPermissionsToData(data);

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

        this.checkinMembers(dbHelper);
        this.checkinReadPermissions(dbHelper, defaults);

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
   * Inserts the create permissions into the specified map.
   */

  protected void createPermissionsToData (Map data)
    throws MasterException
  {
    int[] docTypes = this.master.getCreatePermissions();
    for (int i = 0; i < docTypes.length; i++)
      data.put(DbColumn.mayCreate[docTypes[i]], Boolean.TRUE);
  }

  // --------------------------------------------------------------------------------
  // Identification method 
  // --------------------------------------------------------------------------------

  /**
   * Returns a string that identificates this <code>UserGroupToCheckin</code> Instance. It has the
   * following form:<pre>
   *   "UserGroupToCheckin" +
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
      "UserGroupToCheckin" +
      '#' + this.instanceStatus.getInstanceId() +
      '(' + this.instanceStatus.getNumberOfRecycles() +
      ',' + this.id +
      ',' + this.path +
      ')';
  }
}
