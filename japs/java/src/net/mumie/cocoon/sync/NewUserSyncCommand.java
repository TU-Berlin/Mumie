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

package net.mumie.cocoon.sync;

import java.sql.ResultSet;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.RequestParam;
import net.mumie.cocoon.notions.UserGroupName;
import net.mumie.cocoon.util.LogUtil;
import org.apache.cocoon.environment.Request;

/**
 * Represents the synchronization command <code>new-user</code>.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: NewUserSyncCommand.java,v 1.18 2009/02/09 15:21:27 rassy Exp $</code>
 */

public class NewUserSyncCommand extends AbstractSyncCommand
{
  /**
   * Hint for this sync command.
   */

  public static final String HINT = "new-user";

  /**
   * Path of the section where the user is created.
   */

  public static final String USERS_SECTION_PATH = "org/users";

  /**
   * Indicates that the command should fail if the user already exists.
   */

  public static final int FAIL = 0;

  /**
   * Indicates that the command should overwrite the user if user already exists.
   */

  public static final int OVERWRITE = 1;

  /**
   * Indicates that the command should be ignored if user already exists.
   */

  public static final int IGNORE = 2;

  /**
   * Executes this sync command.
   */

  public void execute (Request request)
    throws SyncException
  {
    final String METHOD_NAME = "execute";
    this.getLogger().debug(METHOD_NAME + "1/4: Started");
    DbHelper dbHelper = null;
    
    try
      {
        // Initialize db helper:
        dbHelper = (DbHelper)this.serviceManager.lookup(DbHelper.ROLE);

        // Get data from request:
        String syncId = request.getParameter(RequestParam.SYNC_ID);
        String loginName = request.getParameter(RequestParam.LOGIN_NAME);
        String passwordEncrypted = request.getParameter(RequestParam.PASSWORD_ENCRYPTED);
        String firstName = request.getParameter(RequestParam.FIRST_NAME);
        String surname = request.getParameter(RequestParam.SURNAME);
        String matrNumber = request.getParameter(RequestParam.MATR_NUMBER);
        int ifExists = getIfExistsMode(request.getParameter("if-exists"));

        if ( syncId == null )
          throw new IllegalArgumentException("Missing sync id");

        // Create pure name:
        String pureName = "usr_" + (loginName != null ? loginName : syncId);

        this.getLogger().debug
          (METHOD_NAME + " 2/4:" +
           " syncId = " + syncId +
           ", loginName = " + loginName +
           ", passwordEncrypted : " + (passwordEncrypted == null ? "-null-" : "-not-null-") +
           ", firstName = " + firstName +
           " (" + LogUtil.stringCharsToString(firstName, 16) + ")" +
           ", surname = " + surname +
           " (" + LogUtil.stringCharsToString(surname, 16) + ")" +
           ", matrNumber = " + matrNumber +
           ", pureName = " + pureName);

	int containedIn = dbHelper.getSectionIdForPath(USERS_SECTION_PATH);

        // Start transaction:
	dbHelper.beginTransaction(this, true);

        // Check if user exists:
        ResultSet userData = dbHelper.queryPseudoDocDatumBySyncId
          (PseudoDocType.USER, syncId, DbColumn.ID);
        int userId = (userData.next() ? userData.getInt(DbColumn.ID) : Id.UNDEFINED);

        if ( userId == Id.UNDEFINED ) // User does not exist
          {
            // Create new user in the db:
            userId = dbHelper.storeUserData
              (syncId,
               pureName,
               containedIn,
               loginName,
               passwordEncrypted,
               firstName,
               surname,
               matrNumber);

            // Set permissions:
            setPermissions(dbHelper, userId);

            this.getLogger().debug(METHOD_NAME + "3/4: Created new user. userId = " + userId);
          }
        else if ( ifExists == OVERWRITE )
          {
            // Update user data in db:
            dbHelper.updateUserData
              (syncId,
               loginName,
               passwordEncrypted,
               firstName,
               surname,
               matrNumber);

            // Set permissions:
            setPermissions(dbHelper, userId);

            this.getLogger().debug(METHOD_NAME + "3/4: Overwrote existing user");
          }
        else if ( ifExists == IGNORE )
          {
            this.getLogger().debug(METHOD_NAME + "3/4: User exists; ignoring command");
          }
        else if ( ifExists == FAIL )
          {
            throw new IllegalArgumentException("User already exists");
          }

        // End transaction:
	dbHelper.endTransaction(this);

        this.getLogger().debug(METHOD_NAME + "4/4: Done");
      }
    catch (Exception exception)
      {
        throw new SyncException(exception);
      }
    finally
      {
        if ( dbHelper != null )
	  try
	    {
	      if ( dbHelper.hasTransactionLocked(this) )
		dbHelper.abortTransaction(this);
	    }
	  catch(Exception exception)
	    {
	      throw new SyncException(exception);
	    }
	  finally
	    {
	      this.serviceManager.release(dbHelper);
	    }
      }
  }

  /**
   * Obtains the "if exists mode" from the specified keyword.
   */

  protected int getIfExistsMode (String keyword)
  {
    if ( keyword == null || keyword.equals("fail") )
      return FAIL;
    else if ( keyword.equals("overwrite") )
      return OVERWRITE;
    else if ( keyword.equals("ignore") )
      return IGNORE;
    else
      throw new IllegalArgumentException("Illegal value for \"if-exists\": " + keyword);
  }

  /**
   * Sets the read/write permissions for the user.
   */

  protected static void setPermissions (DbHelper dbHelper, int userId)
    throws Exception
  {
    dbHelper.storePseudoDocumentReadPermission
      (PseudoDocType.USER, userId, UserGroupName.ADMINS);
    dbHelper.storePseudoDocumentReadPermission
      (PseudoDocType.USER, userId, UserGroupName.LECTURERS);
    dbHelper.storePseudoDocumentReadPermission
      (PseudoDocType.USER, userId, UserGroupName.SYNCS);
    dbHelper.storePseudoDocumentReadPermission
      (PseudoDocType.USER, userId, UserGroupName.TUTORS);
    dbHelper.storePseudoDocumentReadPermission
      (PseudoDocType.USER, userId, UserGroupName.STUDENTS);
    dbHelper.storePseudoDocumentReadPermission
      (PseudoDocType.USER, userId, UserGroupName.AUTHORS);
  }
}
