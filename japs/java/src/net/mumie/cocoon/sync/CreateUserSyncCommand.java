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
 * Creates a new user.
 *
 * <p>Arguments (mandatory in <strong>this face</strong>):</p>
 * <ul>
 *   <li><strong>sync-id</strong> &mdash; synchronization id of the new user</li>
 *   <li>surname &mdash; surname of the new user</li>
 *   <li>first-name &mdash; first-name of the new user</li>
 *   <li>login-name &mdash; login-name of the new user</li>
 *   <li>password-encrypted &mdash; encrypted password of the new user</li>
 *   <li>email &mdash; email of the new user</li>
 *   <li>pure-name &mdash; pure name of the new user</li>
 *   <li>section &mdash; path of the section the new user is stored in</li>
 *   <li>groups &mdash; groups the new user will be a member of (comma- or space-separated list of group names)</li>
 *   <li>if-exists</li> &mdash What happens if the user already exists. Allowed values are
 *     "fail" (an Excpetion is thrown), "overwrite" (the user is overwritten), and "ignore"
 *     (the sync command is ignored). The default is "fail".
 * </ul>
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: CreateUserSyncCommand.java,v 1.3 2009/02/27 23:19:30 rassy Exp $</code>
 */

public class CreateUserSyncCommand extends AbstractSyncCommand
{
  /**
   * Hint for this sync command.
   */

  public static final String HINT = "create-user";

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
        String surname = request.getParameter(RequestParam.SURNAME);
        String firstName = request.getParameter(RequestParam.FIRST_NAME);
        String loginName = request.getParameter(RequestParam.LOGIN_NAME);
        String passwordEncrypted = request.getParameter(RequestParam.PASSWORD_ENCRYPTED);
        String email = request.getParameter(RequestParam.EMAIL);
        String pureName = request.getParameter(RequestParam.PURE_NAME);
        String sectionPath = request.getParameter(RequestParam.SECTION);
        String matrNumber = request.getParameter(RequestParam.MATR_NUMBER);
        String groupNames = request.getParameter(RequestParam.GROUPS);
        int ifExists = getIfExistsMode(request.getParameter(RequestParam.IF_EXISTS));

        if ( syncId == null )
          throw new IllegalArgumentException("Missing sync id");

	// Set pure name if not set by request parameter:
        if ( pureName == null || pureName.equals("") )
          pureName = "usr_" + (loginName != null ? loginName : syncId);

        // Set section path if not set by request parameter:
        if ( sectionPath == null || sectionPath.equals("") )
          sectionPath = USERS_SECTION_PATH;

        this.getLogger().debug
          (METHOD_NAME + " 2/4:" +
           " syncId = " + syncId +
           ", surname = " + surname +
           ", firstName = " + firstName +
           ", loginName = " + loginName +
           ", passwordEncrypted : " + (passwordEncrypted == null ? "-null-" : "-not-null-") +
           ", email = " + email +
           ", pureName = " + pureName +
           ", sectionPath = " + sectionPath +
           ", matrNumber = " + matrNumber +
           ", groupNames = " + groupNames +
           ", ifExists = " + ifExists);

	int containedIn = dbHelper.getSectionIdForPath(sectionPath);

        // Start transaction:
	dbHelper.beginTransaction(this, true);

        // Check if user exists:
        ResultSet userData = dbHelper.queryPseudoDocDatumBySyncId
          (PseudoDocType.USER, syncId, DbColumn.ID);
        int userId = (userData.next() ? userData.getInt(DbColumn.ID) : Id.UNDEFINED);

	// Auxiliary flag which stores if the user has been created or modified:
	boolean createdOrModified = false;

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

	    createdOrModified = true;

            this.getLogger().debug(METHOD_NAME + "3/4: Created new user. userId = " + userId);
          }
        else if ( ifExists == IF_EXISTS_OVERWRITE )
          {
            // Update user data in db:
            dbHelper.updateUserData
              (syncId,
               loginName,
               passwordEncrypted,
               firstName,
               surname,
               matrNumber);

	    createdOrModified = true;

            this.getLogger().debug(METHOD_NAME + "3/4: Overwrote existing user");
          }
        else if ( ifExists == IF_EXISTS_IGNORE )
          {
            this.getLogger().debug(METHOD_NAME + "3/4: User exists; ignoring command");
          }
        else if ( ifExists == IF_EXISTS_FAIL )
          {
            throw new IllegalArgumentException("User already exists");
          }

	if ( createdOrModified )
	  {
	    // Set permissions:
	    for (String groupName : this.readAllowedGroups)
	      dbHelper.storePseudoDocumentReadPermission(PseudoDocType.USER, userId, groupName);

            // Add user to groups:
            if ( isNotVoid(groupNames) )
	      {
		dbHelper.removeMemberFromAllUserGroups(userId);
		if ( isNotVoid(groupNames) )
		  {
		    for (String groupName : this.splitPattern.split(groupNames))
		      dbHelper.addMemberToUserGroup(userId, groupName);
		  }		
	      }
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
}
