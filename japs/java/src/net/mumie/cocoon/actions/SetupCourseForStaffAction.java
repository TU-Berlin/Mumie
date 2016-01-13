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
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.SessionAttrib;
import net.mumie.cocoon.notions.UserGroupName;
import net.mumie.cocoon.pseudodocs.GeneralUser;
import net.mumie.cocoon.pseudodocs.SessionUser;
import net.mumie.cocoon.util.LogUtil;
import net.mumie.cocoon.util.ParamUtil;
import net.mumie.cocoon.util.UserProblemData;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.SourceResolver;

/**
 * <p>
 *   Prepares the pipeline for processing a course that is viewed by
 *   a staff member. Recognizes the following parameters: 
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
 *       <td><code>student</code></td>
 *       <td>Id of the student to view, or the keyword {@link #CLEAR CLEAR}.</td>
 *       <td>No. Defaults to the student stored in the {@link #STUDENT STUDENT} session attribute,
 *       or to no student if this attribute is not set.</td>
 *     </tr>
 *   </tbody>
 * </table>
 * <p>
 *   Sets the following sitemap parameters:
 * </p>
 * <table class="genuine indented" style="width:60em">
 *   <thead>
 *     <tr>
 *       <td>Name</td>
 *       <td>Description</td>
 *       <td>Always set</td>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td><code>user</code></td>
 *       <td>The id of the user whose problem data should be loaded (see below)</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr>
 *       <td><code>tutor-view</code></td>
 *       <td>Boolean. Whether tutor view is enabled (see below)</td>
 *       <td>No</td>
 *     </tr>
 *   </tbody>
 * </table>
 * <p>
 *   NOTE: This action presupposes that the user who owns the session ("session user") is a
 *   staff member. This is not checked. Rather, it is assumed that this action is placed
 *   inside another action ensuring that only staff members can reach this action.
 * </p>
 * <p>
 *   The user and the tutor view flag are determined slightly differently as in
 *   {@link SetupProblemForStaffAction SetupProblemForStaffAction}: Since courses have no timeframes,
 *   tutor view is enabled whenever the staff member has selected a student (in which case the
 *   user is the student). Otherwise, tutor view is nor enabled and the user is the staff member.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: SetupCourseForStaffAction.java,v 1.2 2007/07/11 15:38:40 grudzin Exp $</code>
 */

public class SetupCourseForStaffAction extends AbstractSetupForStaffAction
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
    this.getLogger().debug
      (METHOD_NAME + " 1/4: Started." +
       " Parameters: " + LogUtil.parametersToString(parameters));

    Map sitemapParams = new HashMap();
    SessionUser currentUser = null;

    try
      {
        // Setup currentUser:
        currentUser = (SessionUser)this.manager.lookup(SessionUser.ROLE);
        this.getLogger().debug
          (METHOD_NAME + " 2/4: currentUser = " + LogUtil.identify(currentUser));

        // Get the student:
        int studentId = this.getStudentId(currentUser, parameters, sitemapParams);

        // Some variables:
        boolean tutorView = ( studentId != Id.UNDEFINED );
        int userId = (tutorView ? studentId : currentUser.getId());
        this.getLogger().debug
          (METHOD_NAME + " 3/4:" +
           " tutorView = " + tutorView +
           ", userId = " + userId);

        // Set sitemap parameters:
        sitemapParams.put(USER, Integer.toString(userId));
        sitemapParams.put(TUTOR_VIEW, booleanToString(tutorView));

        this.getLogger().debug
          (METHOD_NAME + " 4/4: Done. sitemapParams = " + sitemapParams);

        return sitemapParams;
      }
    catch (Exception exception)
      {
        throw new ProcessingException(exception);
      }
    finally
      {
        if ( currentUser != null )
          this.manager.release(currentUser);
      }
  }
}
