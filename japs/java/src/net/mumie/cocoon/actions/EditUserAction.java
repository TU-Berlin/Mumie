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
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.UserGroupName;
import net.mumie.cocoon.util.LogUtil;
import net.mumie.cocoon.util.ParamUtil;
import net.mumie.cocoon.util.PasswordEncryptor;
import net.mumie.cocoon.util.UserUtil;
import net.mumie.util.IntList;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;

/**
 * Creating/changing users. Intended to function in combination with a XHML form. The
 * actions knows about two stages of the form:
 *
 * <ul>
 *   <li>
 *     <em>Form stage 0:</em> No form data where sent yet; a new, initial form is about to
 *     be created. In this stage, the action only sets the sitemap parameters
 *     <code>mode</code> and <code>user</code> (see below).
 *   </li>
 *   <li>
 *     <em>Form stage 1:</em> Form data have been sent from the client. In this stage, the
 *     action processes the data, creating a new or modifying an existing user. After that,
 *     it sets the sitemap parameters <code>mode</code>, <code>user</code>,
 *     <code>performed-task</code> in case of sucess, and <code>error</code> in case of
 *     failure (see below).
 *   </li>
 * </ul>
 */

public class EditUserAction extends ServiceableAction
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
        // Add request parameters to action parameters:
        Request request = ObjectModelHelper.getRequest(objectModel);
        ParamUtil.addRequestParams(parameters, request, true);

        // Init sitemap parameters to return:
        Map sitemapParams = new HashMap();

        int formStage = ParamUtil.getAsInt(parameters, "form-stage", 0);
        switch ( formStage )
          {
          case 0:
            // Do nothing, only inform sitemap to generate form and set the user:
            int userId = ParamUtil.getAsInt(parameters, "id", -1);
            sitemapParams.put("mode", "form");
            sitemapParams.put("user", Integer.toString(userId));
            break;
          case 1:
            // Create or modify user:
            this.createOrModifyUser(parameters, sitemapParams);
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
   * Creates a new user or modifies an existing one. The data of the user (first name,
   * surname, etc.) are obtained from the request parameters of the specified request
   * object. The second argument, <code>sitemapParams</code>, is expected to be the map
   * eventually returned by the {@link #act act} method of the action. The id of the user is
   * added to the map with the key <code>user</code>. If a new user should be created, but
   * the creation failed because of an error, the value is {@link Id#UNDEFINED Id.UNDEFINED}.
   * If the data found in the request parameters are incomplete or incorrect (i.e., if
   * invalid characters were used in the password), a second entry is made in the map. The
   * key of the entry is the string <code>"error"</code>, and the value is a string which
   * indicates the error.
   */

  protected void createOrModifyUser (Parameters params, Map sitemapParams)
    throws ProcessingException
  {
    final String METHOD_NAME = "createOrModifyUser";
    this.getLogger().debug(METHOD_NAME + " 1/3: Started");

    DbHelper dbHelper = null;
    PasswordEncryptor encryptor = null;

    try 
      {
        // Init services:
        dbHelper = (DbHelper)this.manager.lookup(DbHelper.ROLE);
        encryptor = (PasswordEncryptor)this.manager.lookup(PasswordEncryptor.ROLE);

        // Id of the user:
        int userId = Id.UNDEFINED;

        // The first name of the user, or null if not specified.
        String firstName = null;

        // The surname of the user, or null if not specified.
        String surname = null;

        // The login name of the user, or null if not specified.
        String loginName = null;

        // The password of the user, or null if not specified.
        String password = null;

        // The repeated password of the user, or null if not specified.
        String repeatedPassword = null;

        // The sync id of the user, or null if not specified.
        String syncId = null;

        // The e-mail address of the user, or null if not specified.
        String email = null;

        // The description of the user, or null if not specified.
        String description = null;

        // The pure name of the user, or null if not specified.
        String pureName = null;

        // The sction path of the user, or null if not specified.
        String sectionPath = null;

        // The sync home of the user, or null if not specified.
        String syncHomePath = null;
    
        // List of groups the user is a member of.
        IntList groups = new IntList();
    
        // List of tutorials the user is a member of.
        IntList tutorials = new IntList();

        // List of tutorials the user is a tutor of.
        IntList tutorTutorials = new IntList();
    
        // List of classes the user is a lecturer of.
        IntList lecturerClasses = new IntList();
    
        // List of groups having read permission.
        IntList readAllowedGroups = new IntList();
    
        // List of groups having write permission.
        IntList writeAllowedGroups = new IntList();

        // Process parameters:
        for (String name : params.getNames())
          {
            String value = params.getParameter(name);
            if ( value.equals("") ) value = null;
            int intValue = -1;

            if ( name.equals("id") )
              userId = Integer.parseInt(value);
            else if ( name.equals("first-name") )
              firstName = value;
            else if ( name.equals("surname") )
              surname = value;
            else if ( name.equals("login-name") )
              loginName = value;
            else if ( name.equals("password") )
              password = value;
            else if ( name.equals("password-repeated") )
              repeatedPassword = value;
            else if ( name.equals("sync-id") )
              syncId = value;
            else if ( name.equals("email") )
              email = value;
            else if ( name.equals("description") )
              description = value;
            else if ( name.equals("pure-name") )
              pureName = value;
            else if ( name.equals("section") )
              sectionPath = value;
            else if ( name.equals("sync-home") )
              syncHomePath = value;
            else if ( (intValue = checkIntListParam(name, "member-of-group-", value)) != -1 )
              groups.add(intValue);
            else if ( (intValue = checkTutorialMembershipParam(name, "tutorial-for-class-", value)) != -1 )
              tutorials.add(intValue);
            else if ( (intValue = checkIntListParam(name, "tutor-of-tutorial-", value)) != -1 )
              tutorTutorials.add(intValue);
            else if ( (intValue = checkIntListParam(name, "lecturer-of-class-", value)) != -1 )
              lecturerClasses.add(intValue);
            else if ( (intValue = checkIntListParam(name, "read-permission-for-group-", value)) != -1 )
              readAllowedGroups.add(intValue);
            else if ( (intValue = checkIntListParam(name, "write-permission-for-group-", value)) != -1 )
              writeAllowedGroups.add(intValue);
          }

        // Indicates that a new user is created:
        boolean newUser = ( userId == Id.UNDEFINED );

        // Set pure name and section to the defaults if necessary:
        if ( newUser && pureName == null ) pureName = UserUtil.suggestPureName(loginName);
        if ( newUser && sectionPath == null ) sectionPath = UserUtil.USERS_SECTION_PATH;

        // Get section id if necessary:
        Integer sectionId =
          ( sectionPath != null
            ? new Integer(dbHelper.getSectionIdForPath(sectionPath))
            : null);

        // Get sync home section id if necessary:
        Integer syncHomeId =
          ( syncHomePath != null
            ? new Integer(dbHelper.getSectionIdForPath(syncHomePath))
            : null);

	this.getLogger().debug
	  (METHOD_NAME + " 2/3:" +
	   " userId = " + userId +
	   ", firstName = " + firstName +
	   ", surname = " + surname +
	   ", loginName = " + loginName +
	   ", password = " + LogUtil.hide(password) +
	   ", repeatedPassword = " + LogUtil.hide(repeatedPassword) +
	   ", syncId = " + syncId +
	   ", email = " + email +
	   ", description = " + description +
	   ", pureName = " + pureName +
	   ", sectionPath = " + sectionPath +
	   ", sectionId = " + sectionId +
	   ", syncHomePath = " + syncHomePath +
	   ", syncHomeId = " + syncHomeId +
	   ", groups = " + LogUtil.arrayToString(groups.toIntArray()) +
	   ", tutorials = " + LogUtil.arrayToString(tutorials.toIntArray()) +
	   ", tutorTutorials = " + LogUtil.arrayToString(tutorTutorials.toIntArray()) +
	   ", lecturerClasses = " + LogUtil.arrayToString(lecturerClasses.toIntArray()) +
	   ", readAllowedGroups = " + LogUtil.arrayToString(readAllowedGroups.toIntArray()) +
	   ", writeAllowedGroups = " + LogUtil.arrayToString(writeAllowedGroups.toIntArray()));

        // Check data:
        String error = null;
        if ( newUser && loginName == null )
          error = "login-name-missing";
        else if ( loginName != null && !UserUtil.checkLoginNameChars(loginName) )
          error = "invalid-characters-in-login-name";
        else if ( newUser && firstName == null )
          error = "first-name-missing";
        else if ( newUser && surname == null )
          error = "surname-missing";
        else if ( newUser && password == null && syncId == null )
          error = "password-missing";
        else if ( password != null && !UserUtil.checkPasswordChars(password) )
          error = "invalid-characters-in-password";
        else if ( password != null && password.length() < UserUtil.PASSWORD_MIN_LENGTH )
          error = "password-too-short";
        else if ( password != null && repeatedPassword == null )
          error = "repeated-password-missing";
        else if ( password != null && !password.equals(repeatedPassword) )
          error = "repeated-password-does-not-match";
        else if ( newUser && dbHelper.checkIfUserExists(loginName) )
          error = "user-already-exists";
        else if ( pureName != null && !UserUtil.checkPureName(pureName) )
          error = "invalid-pure-name";
        else if ( sectionId != null && sectionId.intValue() == -1 )
          error = "section-does-not-exist";
        else if ( syncHomeId != null && syncHomeId.intValue() == -1 )
          error = "sync-home-does-not-exist";
        else if ( newUser && dbHelper.checkIfUserPureNameExists(sectionId.intValue(), pureName) )
          error = "pure-name-already-exists";

        if ( error == null )
          {
            // Encrypt password if necessary:
            String passwordEncrypted =
              ( password != null
                ? encryptor.encrypt(password)
                : null);

            // Map for the user data:
            Map<String,Object> data = new HashMap<String,Object>();
            addIfNonNull(data, DbColumn.FIRST_NAME, firstName);
            addIfNonNull(data, DbColumn.SURNAME, surname);
            addIfNonNull(data, DbColumn.LOGIN_NAME, loginName);
            addIfNonNull(data, DbColumn.PASSWORD, passwordEncrypted);
            addIfNonNull(data, DbColumn.SYNC_ID, syncId);
            addIfNonNull(data, DbColumn.EMAIL, email);
            addIfNonNull(data, DbColumn.DESCRIPTION, description);
            addIfNonNull(data, DbColumn.PURE_NAME, pureName);
            addIfNonNull(data, DbColumn.CONTAINED_IN, sectionId);
            addIfNonNull(data, DbColumn.SYNC_HOME, syncHomeId);

            // Create or update user:
            if ( newUser )
              userId = dbHelper.storeUserData(data);
            else
              dbHelper.updateUserData(userId, data);

            // Group memberships:
            dbHelper.removeMemberFromAllUserGroups(userId);
            for (int groupId : groups.toIntArray())
              dbHelper.addMemberToUserGroup(userId, groupId);

            // Tutorial memberships:
            if ( dbHelper.checkUserGroupMembership(userId, UserGroupName.STUDENTS) )
              {
                dbHelper.removeMemberFromAllTutorials(userId);
                for (int tutorialId : tutorials.toIntArray())
                  dbHelper.addMemberToTutorial(userId, tutorialId);
              }

            if ( dbHelper.checkUserGroupMembership(userId, UserGroupName.TUTORS) )
              {
                // Tutor of tutorials:
                dbHelper.removeTutorFromAllTutorials(userId);
                for (int tutorTutorialId : tutorTutorials.toIntArray())
                  dbHelper.updatePseudoDocDatum
                    (PseudoDocType.TUTORIAL, tutorTutorialId, DbColumn.TUTOR, userId);
              }

            if ( dbHelper.checkUserGroupMembership(userId, UserGroupName.LECTURERS) )
              {
                // Lecturer of classes:
                dbHelper.removeLecturerFromAllClasses(userId);
                for (int lecturerClass : lecturerClasses.toIntArray())
                  dbHelper.addClassLecturer(lecturerClass, userId);
              }

            // Read permissions:
            dbHelper.removeAllReadPermissionsForPseudoDoc(PseudoDocType.USER, userId);
            for (int readAllowedGroup : readAllowedGroups)
              dbHelper.storePseudoDocumentReadPermission
                (PseudoDocType.USER, userId, readAllowedGroup);

            // Write permissions:
            dbHelper.removeAllWritePermissionsForPseudoDoc(PseudoDocType.USER, userId);
            for (int writeAllowedGroup : writeAllowedGroups)
              dbHelper.storePseudoDocWritePermission
                (PseudoDocType.USER, userId, writeAllowedGroup);

            sitemapParams.put("mode", "feedback");
            sitemapParams.put("performed-task", newUser ? "created-user" : "modified-user");
          }
	else
          {
            sitemapParams.put("mode", "form");
            sitemapParams.put("error", error);
          }

	sitemapParams.put("user", Integer.toString(userId));

	this.getLogger().debug(METHOD_NAME + " 3/3: Done");
      }
    catch (Exception exception)
      {
	throw new ProcessingException(exception);
      }
    finally
      {
	if ( dbHelper != null ) this.manager.release(dbHelper);
	if ( encryptor != null ) this.manager.release(encryptor);
      }
  }

  /**
   * Helper method to process parameters which may contribute a numerical value to an int
   * list. The names of the parameters this method is made for ahe the form
   * <code>prefix + <var>value</var></code>, the values are <code>"yes"</code> or
   * <code>"no"</code>. If the value is <code>"yes"</code>, the method returns
   * <var>value</var> as an int, otherwise it returns -1.
   */

  protected static int checkIntListParam (String paramName, String prefix, String paramValue)
  {
    if ( paramName.startsWith(prefix) )
      {
        String valueAsString = paramName.substring(prefix.length());
        int value = -1;
        try
          {
            value = Integer.parseInt(valueAsString);
          }
        catch (Exception exception)
          {
            throw new IllegalArgumentException
              ("Illegal parameter name: \"" + paramName + "\"" +
               " (expected \"prefix\" + integral number)");
          }
        if ( paramValue.equals("yes") || paramValue.equals("true") )
          return value;
        else if ( paramValue.equals("no") || paramValue.equals("false") )
          return -1;
        else
          throw new IllegalArgumentException
            ("Illegal value for parameter \"" + paramName + "\": " + paramValue +
             " (expected yes|no|true|false)");
      }
    return -1;
  }

  /**
   * Special helper method to process the
   * <code>tutorial-for-class-<var>id</var></code> parameters, where <var>id</var> is the id
   * of the teaching class. The value of such a parameter must by a tutorial id or the
   * string <code>"void"</code>. In the former case, the tutorial id is returned as an int,
   * in the latter case -1 is returned.
   */

  protected static int checkTutorialMembershipParam (String paramName, String prefix, String paramValue)
  {
    if ( paramName.startsWith(prefix) )
      {
        String classIdAsString = paramName.substring(prefix.length());
        try
          {
            Integer.parseInt(classIdAsString);
          }
        catch (Exception exception)
          {
            throw new IllegalArgumentException
              ("Illegal parameter name: \"" + paramName + "\"" +
               " (expected \"prefix\" + integral number)");
          }

        int value = -1;
        if ( !paramValue.equals("void") )
          {
            try
              {
                value = Integer.parseInt(paramValue);
              }
            catch (Exception exception)
              {
                throw new IllegalArgumentException
                  ("Illegal value for parameter \"" + paramName + "\": " + paramValue +
                   " (expected integral number or \"void\")");
              }
          }
        return value;
      }
    return -1;
  }

  /**
   * Auxiliary method; adds the specified key/value pair to the specified map if, and only
   * if, the value is not null.
   */

  protected static final void addIfNonNull (Map<String,Object> map, String key, Object value)
  {
    if ( value != null ) map.put(key, value);
  }

}
