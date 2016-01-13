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

package net.mumie.cocoon.generators;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.pseudodocs.SessionUser;
import net.mumie.cocoon.pseudodocs.User;
import net.mumie.cocoon.tub.TubLdapHelper;
import net.mumie.cocoon.tub.TubLdapUser;
import net.mumie.cocoon.tub.TubMumieUser;
import net.mumie.cocoon.tub.TubUtil;
import net.mumie.cocoon.util.ParamUtil;
import net.mumie.cocoon.util.PasswordEncryptor;
import net.mumie.cocoon.util.UserInputException;
import net.mumie.cocoon.xml.GeneralXMLElement;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.generation.ServiceableGenerator;

public class TubConvertAccountGenerator extends ServiceableGenerator
{
  /**
   * Name of the root element of the XML.
   */

  static final String ROOT_ELEMENT = "convert-account";

  /**
   * Name of the "status" attribute:
   */

  static final String STATUS = "status";

  /**
   * Name of the "new-user-existed" attribute:
   */

  static final String NEW_USER_EXISTED = "new-user-existed";

  /**
   * Name of the "logged-in-as-new-user" attribute:
   */

  static final String LOGGED_IN_AS_NEW_USER = "logged-in-as-new-user";

  /**
   * Name of the "error" attribute:
   */

  static final String ERROR = "error";

  /**
   * Helper object to write XML elements to SAX.
   */

  protected GeneralXMLElement xmlElement = new GeneralXMLElement("", "");

  /**
   * Generate method; calls {@link #startForm startForm} or
   * {@link #convertAccountAndFeedback convertAccountAndFeedback} depending on the form stage.
   */

  public void generate ()
    throws ProcessingException 
  {
    final String METHOD_NAME = "generate";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");

    try
      {
        int formStage = ParamUtil.getAsInt(this.parameters, "form-stage", 0);
        switch ( formStage )
          {
          case 0:
            this.startForm();
            break;
          case 1:
            this.convertAccountAndFeedback();
            break;
          default:
            throw new IllegalArgumentException("Illegal form stage value: " + formStage);
          }
        this.getLogger().debug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
        throw new ProcessingException(exception);
      }
  }

  /**
   * Converts the account and generates a short feedbak XML.
   */

  protected void convertAccountAndFeedback ()
    throws Exception
  {
    final String METHOD_NAME = "convertAccountAndFeedback";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");

    TubLdapHelper ldapHelper = null;
    DbHelper dbHelper = null;
    User user = null;
    PasswordEncryptor encryptor = null;

    try
      {  
        // Init services:
        dbHelper = (DbHelper)this.manager.lookup(DbHelper.ROLE);
        ldapHelper = (TubLdapHelper)this.manager.lookup(TubLdapHelper.ROLE);
        user = (User)this.manager.lookup(SessionUser.ROLE);
        encryptor = (PasswordEncryptor)this.manager.lookup(PasswordEncryptor.ROLE);

        // Get input data:
        String oldLoginName = ParamUtil.getAsString(this.parameters, "old-login-name", null);
        String oldPassword = ParamUtil.getAsString(this.parameters, "old-password", null);
        String newLoginName = ParamUtil.getAsString(this.parameters, "new-login-name", null);
        String newPassword = ParamUtil.getAsString(this.parameters, "new-password", null);

        try
          {
            // Check data:
            if ( oldLoginName == null )
              throw new UserInputException("old-login-name-missing");
            else if ( oldPassword == null )
              throw new UserInputException("old-password-missing");
            else if ( newLoginName == null )
              throw new UserInputException("new-login-name-missing");
            else if ( newPassword == null )
              throw new UserInputException("new-password-missing");

            int userId = Id.UNDEFINED;
            String oldSyncId = null;

            if ( dbHelper.checkIfUserHasCredentials(oldLoginName, true) )
              {
                // Lookup old user in Mumie db::
                ResultSet oldUserDbData = dbHelper.queryUserData
                  (oldLoginName, encryptor.encrypt(oldPassword), new String[] {DbColumn.ID});
                if ( !oldUserDbData.next() )
                  throw new UserInputException("old-password-check-failed");
                userId = oldUserDbData.getInt(DbColumn.ID);
              }
            else
              {
                // Lookup old user in LDAP:
                TubLdapUser oldLdapUser = ldapHelper.searchUser("uid=" + oldLoginName);
                if ( oldLdapUser == null )
                  throw new UserInputException("old-user-not-found-in-ldap");

                // Check old password:
                if ( !ldapHelper.checkPassword(oldLdapUser.getUserDN(), oldPassword) )
                  throw new UserInputException("old-password-check-failed");

                // Lookup id and sync id of old user in Mumie DB:
                ResultSet oldUserDbData = dbHelper.queryUserData
                  (oldLoginName, new String[] {DbColumn.ID, DbColumn.SYNC_ID});
                if ( !oldUserDbData.next() )
                  throw new UserInputException("old-user-not-found-in-mumie-db");
                userId = oldUserDbData.getInt(DbColumn.ID);
                oldSyncId = oldUserDbData.getString(DbColumn.SYNC_ID);
              }

            // Check if the old account is a temporary one:
            if ( oldSyncId != null && !TubUtil.checkIfTempAccount(oldSyncId) )
              throw new UserInputException("old-account-is-no-temp-account");

            // Lookup new user in LDAP:
            TubLdapUser newLdapUser = ldapHelper.searchUser("uid=" + newLoginName);
            if ( newLdapUser == null )
              throw new UserInputException("new-user-not-found-in-ldap");

            // Check new password:
            if ( !ldapHelper.checkPassword(newLdapUser.getUserDN(), newPassword) )
              throw new UserInputException("new-password-check-failed");

            // Get new sync id:
            String newSyncId = newLdapUser.getTubPersonOM();
            if ( newSyncId == null )
              throw new IllegalArgumentException("New sync id is null");

            // Check if the new account is not a temporary one:
            if ( TubUtil.checkIfTempAccount(newSyncId) )
              throw new UserInputException("new-account-is-temp-account");

	    boolean newUserExisted = false;
	    boolean loggedInAsNewUser = true;

            // Check if an account with the new sync id already exists in the Mumie DB:
            ResultSet newUserDbData = dbHelper.queryPseudoDocDatumBySyncId
              (PseudoDocType.USER, newSyncId, DbColumn.ID);
            if ( newUserDbData.next() )
              {
                // Case 1: New user already exists.

                int oldUserId = userId;
                int newUserId = newUserDbData.getInt(DbColumn.ID);

                // Check if the currently logged-in user is either the new or the old user
                if ( user.getId() != oldUserId && user.getId() != newUserId )
                  throw new UserInputException("user-is-not-logged-in-user");

                // Start transaction:
                dbHelper.beginTransaction(this, true);

                // Transfer all datasheets from old to new user:
                dbHelper.changeUserOfUserAnnotations
                  (oldUserId, newUserId, DocType.WORKSHEET, DocType.GENERIC_PROBLEM);

                // Get tutorials the old user is a member of:
                int[] tutorialIds = dbHelper.getTutorialIdsForMember(oldUserId);

                // Remove new user from all tutorials:
                dbHelper.removeMemberFromAllTutorials(newUserId);

                // Add new user to tutorials of old user:
                for (int tutorialId : tutorialIds)
                  dbHelper.addTutorialMember(tutorialId, newUserId);

                // Remove old user from all tutorials:
                dbHelper.removeMemberFromAllTutorials(oldUserId);

                // // Remove old user from all user groups:
                // dbHelper.removeMemberFromAllUserGroups(oldUserId);

                // End transaction:
                dbHelper.endTransaction(this);

		newUserExisted = true;
		loggedInAsNewUser = ( user.getId() == newUserId );
              }
            else
              {
                // Case : New user does not exist.

                // Check if the user specified by oldLoginName and oldPassword
                // is the currently logged-in user:
                if ( userId != user.getId() )
                  throw new UserInputException("user-is-not-logged-in-user");

                // Change sync id, login name and password of the old user so the
                // old user becomes the new user:
                Map newUserData = new HashMap();
                newUserData.put(DbColumn.SYNC_ID, newSyncId);
                newUserData.put(DbColumn.LOGIN_NAME, newLoginName);
                newUserData.put(DbColumn.PASSWORD, encryptor.encrypt(newPassword));
                dbHelper.updateUserData(userId, newUserData);
              }

            // Report success:
            this.contentHandler.startDocument();
            this.xmlElement.reset();
            this.xmlElement.setLocalName(ROOT_ELEMENT);
            this.xmlElement.addAttribute(STATUS, "ok");
            this.xmlElement.addAttribute(NEW_USER_EXISTED, newUserExisted);
            this.xmlElement.addAttribute(LOGGED_IN_AS_NEW_USER, loggedInAsNewUser);
            this.xmlElement.toSAX(this.contentHandler);
            this.contentHandler.endDocument();
          }
        catch (UserInputException userInputException)
          {
            // Report error:
            this.contentHandler.startDocument();
            this.xmlElement.reset();
            this.xmlElement.setLocalName(ROOT_ELEMENT);
            this.xmlElement.addAttribute(STATUS, "failed");
            this.xmlElement.addAttribute(ERROR, userInputException.getMessage());
            this.xmlElement.toSAX(this.contentHandler);
            this.contentHandler.endDocument();
          }
        catch (Exception exception)
          {
            throw exception;
          }

        this.getLogger().debug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
        this.getLogger().debug(METHOD_NAME + " 2/2: Failed", exception);
      }
    finally
      {
        if ( encryptor != null ) this.manager.release(encryptor);
        if ( user != null ) this.manager.release(user);
        if ( ldapHelper != null ) this.manager.release(ldapHelper);
        if ( dbHelper != null )
          try
            {
              if ( dbHelper.hasTransactionLocked(this) )
                dbHelper.abortTransaction(this);
            }
          catch(Exception exception)
            {
              throw new SQLException("Failed to abort transaction", exception);
            }
          finally
            {
              this.manager.release(dbHelper);
            }
      }
  }

  /**
   * Generates a short XML message saying that the (initial) form needs to be displayed.
   */

  protected void startForm ()
    throws Exception
  {
    final String METHOD_NAME = "startForm";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");
    this.contentHandler.startDocument();
    this.xmlElement.reset();
    this.xmlElement.setLocalName(ROOT_ELEMENT);
    this.xmlElement.addAttribute(STATUS, "form");
    this.xmlElement.toSAX(this.contentHandler);
    this.contentHandler.endDocument();
    this.getLogger().debug(METHOD_NAME + " 2/2: Done");
  }
}