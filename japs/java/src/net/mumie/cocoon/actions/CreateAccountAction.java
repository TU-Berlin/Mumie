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

public class CreateAccountAction extends ServiceableAction
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

    DbHelper dbHelper = null;
    PasswordEncryptor encryptor = null;

    try
      {
        // Init services:
        dbHelper = (DbHelper)this.manager.lookup(DbHelper.ROLE);
        encryptor = (PasswordEncryptor)this.manager.lookup(PasswordEncryptor.ROLE);

        // Get input data:
        String loginName = ParamUtil.getAsString(parameters, "login-name", null);
        String firstName = ParamUtil.getAsString(parameters, "first-name", null);
        String surname = ParamUtil.getAsString(parameters, "surname", null);
        String email = ParamUtil.getAsString(parameters, "e-mail", null);
        String password = ParamUtil.getAsString(parameters, "password", null);
        String passwordRetyped = ParamUtil.getAsString(parameters, "password-retyped", null);
        int[] groupIds = ParamUtil.getAsIntArray(parameters, "user-groups", new int[0]);
        int[] tutorialIds = ParamUtil.getAsIntArray(parameters, "tutorials", new int[0]);

        // Check data:
        String error = null;
        if ( loginName == null )
          error = "login-name-missing";
        else if ( !UserUtil.checkLoginNameChars(loginName) )
          error = "invalid-characters-in-login-name";
        else if ( firstName == null )
          error = "first-name-missing";
        else if ( surname == null )
          error = "surname-missing";
        else if ( email == null )
          error = "email-missing";
        else if ( password == null )
          error = "password-missing";
        else if ( !UserUtil.checkPasswordChars(password) )
          error = "invalid-characters-in-password";
        else if ( password.length() < UserUtil.PASSWORD_MIN_LENGTH )
          error = "password-too-short";
        else if ( passwordRetyped == null )
          error = "password-retyped-missing";
        else if ( !password.equals(passwordRetyped) )
          error = "retyped-passwords-does-not-match";
        else if ( dbHelper.checkIfUserExists(loginName) )
          error = "user-already-exists";

        // Pure name and section id:
        String pureName = "usr_" + loginName;
	int sectionId = dbHelper.getSectionIdForPath(UserUtil.USERS_SECTION_PATH);

        int userId = Id.UNDEFINED;
        if ( error == null )
          {
            // Create user
            Map<String,Object> data = new HashMap<String,Object>();
            data.put(DbColumn.LOGIN_NAME, loginName);
            data.put(DbColumn.FIRST_NAME, firstName);
            data.put(DbColumn.SURNAME, surname);
            data.put(DbColumn.EMAIL, email);
            data.put(DbColumn.PASSWORD, encryptor.encrypt(password));
            data.put(DbColumn.PURE_NAME, pureName);
            data.put(DbColumn.CONTAINED_IN, new Integer(sectionId));
            userId = dbHelper.storeUserData(data);

            // Add user to groups if necessary:
            for (int groupId : groupIds)
              dbHelper.addMemberToUserGroup(userId, groupId);

            // Add user to tutorials if necessary:
            for (int tutorialId : tutorialIds)
              dbHelper.addMemberToTutorial(userId, tutorialId);
          }

        // Sitemap parameters:
        Map params = new HashMap();
        params.put("case", (error == null ? "ok" : "failed"));
        params.put("error", (error == null ? "" : error));

        this.getLogger().debug(METHOD_NAME + " 2/2: Done. userId = " + userId);

        return params;
        
      }
    catch  (Exception exception)
      {
	throw new ProcessingException(exception);
      }
    finally
      {
        if ( encryptor != null )
          this.manager.release(encryptor);

        if ( dbHelper != null )
          this.manager.release(dbHelper);
      }
  }  
}