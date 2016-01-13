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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.SessionAttrib;
import net.mumie.cocoon.notions.Theme;
import net.mumie.cocoon.pseudodocs.SessionUser;
import net.mumie.cocoon.pseudodocs.User;
import net.mumie.cocoon.util.ParamUtil;

import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.webapps.session.SessionManager;
/**
 * <p>
 *   Sets session parameter class and theme
 * </p>
 * <table class="genuine indented" style="width:60em">
 *   <thead>
 *     <tr>
 *       <td>Name</td>
 *       <td>Description</td>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td><code>class</code></td>
 *       <td>Id of the class</td>
 *     </tr>
 *     <tr>
 *       <td><code>ref</code></td>
 *       <td>Id of the reference</td>
 *     </tr>
 *     <tr>
 *       <td><code>course</code></td>
 *       <td>Id of the course</td>
 *     </tr>   
 *    
 *    </tbody>
 * </table>
 *
 * 
 * 
 */
public class SetupSessionAction extends ServiceableAction
{

  // Some constants:
  private final String CLASS = "class";
  private final String COURSE = "course";
  private final String REF = "ref";
  private final String URL = "url";

  public Map act(Redirector redirector, SourceResolver resolver,
      Map objectModel, String source, Parameters parameters)
      throws ProcessingException
  {

    final String METHOD_NAME = "act";
    
    this.getLogger().debug(METHOD_NAME + " 1/6: Started");

    DbHelper dbHelper = null;
    SessionManager sessionManager = null;
    User user = null;
    try
    {
      
      if(checkUrlforSync(parameters))
          return null;
      
      // Init db helper:
      dbHelper = (DbHelper) this.manager.lookup(DbHelper.ROLE);

      // Init session
      sessionManager = (SessionManager) this.manager
          .lookup(SessionManager.ROLE);
      Session session = sessionManager.getSession(true);

      user = (User) this.manager.lookup(SessionUser.ROLE);

      // Get user id
      int userId = user.getId();
      int classId = readClassId(parameters, dbHelper);
      
      if (classId != Id.UNDEFINED)
      {

          // Check if user is class member or staff
          if ( ! dbHelper.checkClassMembership(userId, classId) && ! dbHelper.checkAdmin(userId) &&
              ! dbHelper.checkTutorForClass(userId, classId) && ! dbHelper.checkLecturerForClass(userId, classId))
            throw new IllegalArgumentException("User is not class member");

          int theme = dbHelper.getPseudoDocDatumAsInt(PseudoDocType.CLASS, classId, DbColumn.THEME);
          
           // Set session attribute
          session.setAttribute(SessionAttrib.CLASS, classId);
          session.setAttribute(SessionAttrib.THEME, theme);
          this.getLogger().debug(METHOD_NAME + " 5/6: set session att. class = " + classId + " theme = " + theme);


      }
      else if (checkUrl(parameters))
      {
        session.removeAttribute(SessionAttrib.CLASS);
        session.setAttribute(SessionAttrib.THEME, Theme.DEFAULT);
        this.getLogger().debug(METHOD_NAME+ " 5/6: session attribute \"class\" removed and set default theme ");
      }

      this.getLogger().debug(METHOD_NAME + " 6/6: Done");

      return null;
    }
    catch (Exception exception)
    {
      throw new ProcessingException(exception);
    }
    finally
    {
      if ( dbHelper != null )
        this.manager.release(dbHelper);
      if ( sessionManager != null )
        this.manager.release(sessionManager);
      if ( user != null )
        this.manager.release(user);
    }

  }

  private int readClassId(Parameters parameters, DbHelper dbHelper)
      throws ParameterException, SQLException
  {
    final String METHOD_NAME = "readClassId";
    
    int classId = Id.UNDEFINED;
    // Set the class id accoring to the parameters:
    if ( ParamUtil.checkIfSet(parameters, CLASS))
    {
        classId = ParamUtil.getAsInt(parameters, CLASS);
    }
    else if ( ParamUtil.checkIfSet(parameters, COURSE)
        && classId != Id.UNDEFINED )
    {
      int courseId = ParamUtil.getAsInt(parameters, COURSE);
      ResultSet resultSet = dbHelper.queryData(DocType.COURSE, courseId,
          new String[] { DbColumn.CLASS });
      if ( ! resultSet.next() )
        throw new IllegalArgumentException("Course does not exist");
      classId = resultSet.getInt(DbColumn.CLASS);
      if ( resultSet.wasNull() )
        classId = Id.UNDEFINED;
    }

    else if ( ParamUtil.checkIfSet(parameters, REF) && classId != Id.UNDEFINED )
    {
      int refId = ParamUtil.getAsInt(parameters, REF);
      classId = dbHelper.getClassForProblemRefId(refId);
    }

    this.getLogger().debug(METHOD_NAME + " 4/6: classId = " + classId);

    return classId;
  }

  /*
   * Checks the url whether the class and theme has to be set. Returns true if
   * classId has to be reset
   */
  private boolean checkUrl(Parameters parameters) throws ParameterException
  {

    final String METHOD_NAME = "checkUrl";
    
    String url = ParamUtil.getAsString(parameters, URL);
    boolean hasToBeReset = true;

    hasToBeReset = url.startsWith("alias/") ||
                   url.startsWith("sync/") ||
                   url.startsWith("mmbrowser/") ||
                   url.contains("/classes-and-courses-index") ||
                   url.startsWith("info/") ||
                   url.contains("/generic_page/");

    this.getLogger()
        .debug(
            METHOD_NAME + " 3/6: url = " + url + " hasToBeCheck ="
                + hasToBeReset);
    return hasToBeReset;
  }

  /*
   * Checks the url is a sync url, if it is, this action does nothing.
   */
  private boolean checkUrlforSync(Parameters parameters) throws ParameterException
  {

    final String METHOD_NAME = "checkUrlforSync";
    
    String url = ParamUtil.getAsString(parameters, URL);
    boolean isSync = false;

    isSync = url.startsWith("sync/");

    this.getLogger()
        .debug(
            METHOD_NAME + " 2/6: url = " + url + " isSync ="
                + isSync);
    return isSync;
  }
  
}
