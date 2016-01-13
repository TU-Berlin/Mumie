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

import java.util.Map;

import javax.swing.text.StyledEditorKit.BoldAction;

import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.UserGroupName;
import net.mumie.cocoon.pseudodocs.SessionUser;
import net.mumie.cocoon.pseudodocs.User;
import net.mumie.cocoon.util.ParamUtil;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;

/**
 * <p>
 *   Checks if the current user is a staff member of a given course or class. Recognizes the
 *   following parameters:
 * </p>
 * <table class="genuine indented" style="width:60em">
 *   <thead>
 *     <tr>
 *       <td>Name</td>
 *       <td>Description</td>
 *       <td>Required</td>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td><code>course</code></td>
 *       <td>Id of the course</td>
 *       <td rowspan="2">One of the both</td>
 *     </tr>
 *     <tr>
 *       <td><code>class</code></td>
 *       <td>Id of the class</td>
 *     </tr>
 *     <tr>
 *       <td><code>groups</code></td>
 *       <td>List of groups that are given permission. Default is admins,lecturers and tutors</td>
 *     </tr>
 *   </tbody>
 * </table>
 * <p>
 *   The current user is regarded a staff member if one of the following conditions hold true:
 * </p>
 * <ul>
 *   <li>The user is an administrator</li>
 *   <li>The user is a lecturer of the class</li>
 *   <li>The user is a tutor of a tutorial of the class</li>
 * </ul>
 * <p>
 *   If a course is specfied, the class above is the class the course is belongs to.
 * </p>
 * <p>
 *   The <code>act</code> method returns an empty <code>Map</code> if the current user is a
 *   staff member, otherwise it returns null.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: CheckIfStaffAction.java,v 1.3 2009/10/27 12:16:37 linges Exp $</code>
 */

public class CheckIfStaffAction extends ServiceableAction
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
    User user = null;

    final String COURSE = "course";
    final String CLASS = "class";
    final String GROUPS = "groups";
  

    try
      {
        // Init db helper and user:
        dbHelper = (DbHelper)this.manager.lookup(DbHelper.ROLE);
        user = (User)this.manager.lookup(SessionUser.ROLE);

        // Get user id and course resp. class id:
        int userId = user.getId();

        int courseId =
          (ParamUtil.checkIfSet(parameters, COURSE)
           ? ParamUtil.getAsId(parameters, COURSE)
           : Id.UNDEFINED);
        int classId =
          (ParamUtil.checkIfSet(parameters, CLASS)
           ? ParamUtil.getAsId(parameters, CLASS)
           : Id.UNDEFINED);
          
        
        boolean adminsAllowed = true;
        boolean tutorsAllowed = true;
        boolean lecturersAllowed = true;
        
        if (ParamUtil.checkIfSet(parameters, GROUPS)) 
        {
          String[] allowedGroupNames = ParamUtil.getAsStringArray(parameters, GROUPS);
          
          adminsAllowed = false;
          tutorsAllowed = false;
          lecturersAllowed = false;
          
          
          for (int i = 0; i < allowedGroupNames.length; i ++ )
          {
            String string = allowedGroupNames[i];
            if(string.equalsIgnoreCase(UserGroupName.ADMINS)) adminsAllowed = true;
            if(string.equalsIgnoreCase(UserGroupName.TUTORS)) tutorsAllowed = true;
            if(string.equalsIgnoreCase(UserGroupName.LECTURERS)) lecturersAllowed = true;
          }
          
        }

        
	if ( courseId != Id.UNDEFINED && classId != Id.UNDEFINED )
	  throw new IllegalArgumentException
	    ("If course is set, class must not be set either");

        // Check if user is a staff member:
        boolean isStaffMember = false;
        if ( courseId != Id.UNDEFINED )
          isStaffMember =
            ( (adminsAllowed && dbHelper.checkAdmin(userId)) ||
              (tutorsAllowed && dbHelper.checkTutorForCourse(userId, courseId)) ||
              (lecturersAllowed && dbHelper.checkLecturerForCourse(userId, courseId)) );
        else if ( classId != Id.UNDEFINED )
          isStaffMember =
            ( (adminsAllowed && dbHelper.checkAdmin(userId)) ||
              (tutorsAllowed && dbHelper.checkTutorForClass(userId, classId)) ||
              (lecturersAllowed && dbHelper.checkLecturerForClass(userId, classId)) );
        else
          throw new IllegalArgumentException
            ("Missing parameter: Neither \"" + COURSE + "\" nor \"" + CLASS + "\"" +
             " is specified");

        this.getLogger().debug
          (METHOD_NAME + " 1/2: Done. " +
           "courseId = " + courseId +
           "classId = " + classId +
           ", userId = " + userId +
           ", isStaffMember = " + isStaffMember);
        
        return (isStaffMember ? EMPTY_MAP : null);
      }
    catch  (Exception exception)
      {
	throw new ProcessingException(exception);
      }
    finally
      {
        if ( user != null )
          this.manager.release(user);

        if ( dbHelper != null )
          this.manager.release(dbHelper);
      }
  }
  
}