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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.RefType;
import net.mumie.cocoon.notions.TimeFormat;
import net.mumie.cocoon.notions.UserGroupName;
import net.mumie.cocoon.notions.UserRole;
import net.mumie.cocoon.pseudodocs.GeneralUser;
import net.mumie.cocoon.pseudodocs.SessionUser;
import net.mumie.cocoon.pseudodocs.User;
import net.mumie.cocoon.util.LogUtil;
import net.mumie.cocoon.util.ParamUtil;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;

/**
 * <p>
 *   Provides information concerning a course section as sitemap paramters. Expects the
 *   following paramters:
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
 *       <td><code>course-section</code></td>
 *       <td>Id of the course section</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr>
 *       <td><code>user</code></td>
 *       <td>Id of the user to which the informations should refer.</td>
 *       <td>No. Defaults to the user who owns the session.</td>
 *     </tr>
 *     <tr>
 *       <td><code>default-summary</code></td>
 *       <td>Id of the summary if the course section does not specify a summary itself</td>
 *       <td>Yes</td>
 *     </tr>
 *   </tbody>
 * </table>
 * <p>
 *   Note that the course section in turn specifies the (newest) course it is in, since
 *   there can only be one such course.
 * </p>
 * <p>
 *   Sets the following sitemap parameters:
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
 *       <td><code>course</code></td>
 *       <td>Id of the course</td>
 *     </tr>
 *     <tr>
 *       <td><code>label</code></td>
 *       <td>Label of the course section in the course</td>
 *     </tr>
 *     <tr>
 *       <td><code>summary</code></td>
 *       <td>Id of the course section summary</td>
 *     </tr>
 *     <tr>
 *       <td><code>is-course-member</code></td>
 *       <td>Whether the user who sent the request is a member of the course. Possible
 *       values: <code>"yes", "no"</code> (self-explanatory).</td>
 *     </tr>
 *     <tr>
 *       <td><code>is-staff-member</code></td>
 *       <td>Whether the user who sent the request is a staff member. Possible
 *       values: <code>"yes", "no"</code> (self-explanatory).</td>
 *     </tr>
 *     <tr>
 *       <td><code>case</code></td>
 *       <td>Distinguishes the following three cases: (1) User is a staff member, (2) user
 *       is not a staff member. Possible values: <code>"staff"</code> (case 1),
 *       <code>"student"</code> (case 2).</td>
 *     </tr>
 *   </tbody>
 * </table>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: ProvideCourseSectionInfosAction.java,v 1.8 2008/03/04 11:43:11 rassy Exp $</code>
 */

public class ProvideCourseSectionInfosAction extends ServiceableAction
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

    // Some constants:
    final String USER = "user";
    final String COURSE_SECTION = "course-section";
    final String DEAFULT_SUMMARY = "default-summary";

    DbHelper dbHelper = null;
    User user = null;    

    try
      {
        // Init db helper:
        dbHelper = (DbHelper)this.manager.lookup(DbHelper.ROLE);

        // Init user:
        if ( ParamUtil.checkIfSet(parameters, USER) )
          {
            user = (User)this.manager.lookup(GeneralUser.ROLE);
            user.setId(ParamUtil.getAsId(parameters, USER));
          }
        else
          user = (User)this.manager.lookup(SessionUser.ROLE);

        // Get courseSection data:
        ResultSet courseSectionData = dbHelper.queryCourseSectionData
          (ParamUtil.getAsId(parameters, COURSE_SECTION));

        if ( ! courseSectionData.next() )
          throw new ProcessingException
            ("No course section data found (result set empty)");

        // Get the course id:
        int courseId = courseSectionData.getInt(DbColumn.COURSE_ID);
        
        // Get the course section label:
        String label = courseSectionData.getString(DbColumn.LABEL);
        
        // Get the course section summary id:
        int summaryId = courseSectionData.getInt(DbColumn.SUMMARY);
        if ( courseSectionData.wasNull() )
          summaryId = ParamUtil.getAsId(parameters, DEAFULT_SUMMARY);

        // Get the "is-course-member" flag:
        boolean isCourseMember =
          dbHelper.checkMembership(user.getId(), DocType.COURSE, courseId);

        // Check if user is a staff member:
        boolean isStaffMember = 
          ( dbHelper.checkAdmin(user.getId()) ||
            dbHelper.checkTutorForCourse(user.getId(), courseId) ||
            dbHelper.checkLecturerForCourse(user.getId(), courseId) );

        // Compose "case" parameter value:
        String theCase;
        if ( isStaffMember )
          theCase = "staff";
        else
          theCase = "student";
                                         
        this.getLogger().debug
          (METHOD_NAME + " 2/:2" +
           " courseId = " + courseId +
           ", label = " + label +
           ", summaryId = " + summaryId +
           ", isCourseMember = " + isCourseMember +
           ", isStaffMember = " + isStaffMember +
           ", case = " + theCase);
        
        // Return sitemap parameters:
        Map sitemapParameters = new HashMap();
        sitemapParameters.put("course", Integer.toString(courseId));
        sitemapParameters.put("label", label);
        sitemapParameters.put("summary", Integer.toString(summaryId));
        sitemapParameters.put("is-course-member", booleanToString(isCourseMember));
        sitemapParameters.put("is-staff-member", booleanToString(isStaffMember));
        sitemapParameters.put("case", theCase);
        this.getLogger().debug(METHOD_NAME + " 3/3: Done");
        return sitemapParameters;
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

  /**
   * Auxiliary method; turns a boolean into the string "yes" or "no".
   */

  protected static String booleanToString (boolean value)
  {
    return (value ? "yes" : "no");
  }
}
