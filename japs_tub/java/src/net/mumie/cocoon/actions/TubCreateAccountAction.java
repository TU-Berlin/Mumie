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

package net.mumie.cocoon.actions;

import java.util.HashMap;
import java.util.Map;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.util.ParamUtil;
import net.mumie.cocoon.util.PasswordEncryptor;
import net.mumie.cocoon.util.UserUtil;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;
import net.mumie.cocoon.tub.TubLdapHelper;
import net.mumie.cocoon.tub.TubLdapUser;
import net.mumie.cocoon.util.UserInputException;
import net.mumie.cocoon.generators.TubAuthenticationGenerator;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.util.LogUtil;

public class TubCreateAccountAction extends ServiceableAction
{

  /**
   * See class description.
   */

  public Map act (Redirector redirector,
                  SourceResolver resolver,
                  Map objectModel,
                  String source, 
                  Parameters parameters)
    throws ProcessingException 
  {
    final String METHOD_NAME = "act";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");

    try
      {
        // Init sitemap parameters to return:
        Map sitemapParams = new HashMap();

        int formStage = ParamUtil.getAsInt(parameters, "form-stage", 0);
        sitemapParams.put("form-stage", Integer.toString(formStage));
        switch ( formStage )
          {
          case 0:
            // Do nothing, only inform sitemap to generate form:
            sitemapParams.put("mode", "form");
            break;
          case 1:
            // Create account:
            this.createAccount(parameters, sitemapParams);
            break;
          default:
            throw new IllegalArgumentException("Illegal form-stage value: " + formStage);
          }

        this.getLogger().debug(METHOD_NAME + " 2/2: Done");

        return sitemapParams;
      }
    catch (Exception exception)
      {
        throw new ProcessingException(exception);
      }
  }

  /**
   * 
   */

  protected void createAccount (Parameters params, Map sitemapParams)
    throws ProcessingException
  {
    final String METHOD_NAME = "createAccount";
    this.getLogger().debug(METHOD_NAME + " 1/3: Started");

    TubLdapHelper ldapHelper = null;
    DbHelper dbHelper = null;
    PasswordEncryptor encryptor = null;

    try
      {
        // Init services:
        ldapHelper = (TubLdapHelper)this.manager.lookup(TubLdapHelper.ROLE);
        dbHelper = (DbHelper)this.manager.lookup(DbHelper.ROLE);
        encryptor = (PasswordEncryptor)this.manager.lookup(PasswordEncryptor.ROLE);

        // Get input data:
        String loginName = ParamUtil.getAsString(params, "login-name", null);
        String firstName = ParamUtil.getAsString(params, "first-name", null);
        String surname = ParamUtil.getAsString(params, "surname", null);
        String email = ParamUtil.getAsString(params, "e-mail", null);
        String password = ParamUtil.getAsString(params, "password", null);
        int[] groupIds = ParamUtil.getAsIntArray(params, "user-groups", new int[0]);
        int[] tutorialIds = ParamUtil.getAsIntArray(params, "tutorials", new int[0]);

        this.getLogger().debug
          (METHOD_NAME + " 2/3:" +
           " loginName = " + loginName +
           ", firstName = " + firstName +
           ", surname = " + surname +
           ", email = " + email +
           ", groupIds = " + LogUtil.arrayToString(groupIds) +
           ", tutorialIds = " + LogUtil.arrayToString(tutorialIds));

        try
          {
            // Check data:
            if ( loginName == null )
              throw new UserInputException("login-name-missing");
            else if ( firstName == null )
              throw new UserInputException("first-name-missing");
            else if ( surname == null )
              throw new UserInputException("surname-missing");
            else if ( email == null )
              throw new UserInputException("email-missing");
            else if ( password == null )
              throw new UserInputException("password-missing");
            else if ( dbHelper.checkIfUserExists(loginName) )
              throw new UserInputException("user-already-exists");

            // Lookup user in LDAP:
            TubLdapUser ldapUser = ldapHelper.searchUser("uid=" + loginName);
            if ( ldapUser == null )
              throw new UserInputException("user-not-found-in-ldap");

            // Check password:
            if ( !ldapHelper.checkPassword(ldapUser.getUserDN(), password) )
              throw new UserInputException("password-check-failed");

            // Check if user has a tubPersonOM:
            if ( ldapUser.getTubPersonOM() == null )
              throw new UserInputException("no-tub-person-om");

            // Pure name and section id:
            String pureName = UserUtil.suggestPureName(loginName);
            int sectionId = dbHelper.getSectionIdForPath(UserUtil.USERS_SECTION_PATH);

            dbHelper.beginTransaction(this, true);

            // Create user:
            Map<String,Object> data = new HashMap<String,Object>();
            data.put(DbColumn.LOGIN_NAME, loginName);
            data.put(DbColumn.FIRST_NAME, firstName);
            data.put(DbColumn.SURNAME, surname);
            data.put(DbColumn.EMAIL, email);
            data.put(DbColumn.PASSWORD, encryptor.encrypt(password));
            data.put(DbColumn.PURE_NAME, pureName);
            data.put(DbColumn.CONTAINED_IN, new Integer(sectionId));
            data.put(DbColumn.SYNC_ID, ldapUser.getTubPersonOM());
            int userId = dbHelper.storeUserData(data);

            // Add user to groups if necessary:
            for (int groupId : groupIds)
              dbHelper.addMemberToUserGroup(userId, groupId);

            // Add user to tutorials if necessary:
            for (int tutorialId : tutorialIds)
              dbHelper.addMemberToTutorial(userId, tutorialId);

            dbHelper.endTransaction(this);

            sitemapParams.put("mode", "feedback");
            if ( ldapUser.getTubPersonOM() == null )
              sitemapParams.put("temp-account", "yes");
          }
        catch (UserInputException exception)
          {
            sitemapParams.put("error", exception.getMessage());
            sitemapParams.put("mode", "form");
          }

        this.getLogger().debug(METHOD_NAME + " 3/3: Done");
      }
    catch  (Exception exception)
      {
        throw new ProcessingException(exception);
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
              throw new ProcessingException(exception);
            }
          finally
            {
              this.manager.release(dbHelper);
            }

        if ( encryptor != null )
          this.manager.release(encryptor);

        if ( ldapHelper != null )
          this.manager.release(ldapHelper);
      }
  }

}