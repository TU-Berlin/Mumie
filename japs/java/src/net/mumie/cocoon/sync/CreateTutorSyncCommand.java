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
 * Creates a new tutor.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: CreateTutorSyncCommand.java,v 1.2 2009/05/15 12:49:27 linges Exp $</code>
 */

public class CreateTutorSyncCommand extends AbstractSyncCommand
{
  /**
   * Hint for this sync command.
   */

  public static final String HINT = "create-tutor";

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
        String tutorialSyncIds = request.getParameter(RequestParam.TUTORIALS);
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
           ", tutorialSyncIds = " + tutorialSyncIds +
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

	    // Add user to tutor group:
	    dbHelper.addMemberToUserGroup(userId, UserGroupName.TUTORS);

	    // Set user as tutor for specified tutorials:
	    if(tutorialSyncIds != null )
	    {
  	    dbHelper.removeTutorFromAllTutorials(userId);
  	    for (String tutorialSyncId : this.splitPattern.split(tutorialSyncIds))
  	    {
          //Check if Id's exist
          dbHelper.getIdForSyncId(PseudoDocType.TUTORIAL, tutorialSyncId);
          
  	      dbHelper.updatePseudoDocDatumBySyncId
  		(PseudoDocType.TUTORIAL, tutorialSyncId, DbColumn.TUTOR, userId);
  	      
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
