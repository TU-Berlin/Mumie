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
 *   Prepares the pipeline for processing a problem in context "homework" that is viewed by
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
 *       <td><code>problem</code></td>
 *       <td>Id of the &ndash; real &ndash; problem</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr>
 *       <td><code>ref</code></td>
 *       <td>The id of worksheet - generic_problem reference</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr>
 *       <td><code>student</code></td>
 *       <td>Id of the student to view, or the keyword {@link #CLEAR CLEAR}.</td>
 *       <td>No. Defaults to the student stored in the {@link #STUDENT STUDENT} session attribute,
 *       or to no student if this attribute is not set.</td>
 *     </tr>
 *     <tr>
 *       <td><code>timeframe-relation</code></td>
 *       <td>The timeframe relation, given as the keyword <code>"before"</code>,
 *       <code>inside</code>, or <code>"after"</code></td>
 *       <td>Yes (however, this parameter is accessed only if tutor mode is enabled; see
 *       below)</td>
 *     </tr>
 *     <tr>
 *       <td><code>clear-data</code></td>
 *       <td>Boolean. Whether the PPD of the staff member should be cleared</td>
 *       <td>No. Default is <code>"no"</code></td>
 *     </tr>
 *     <tr>
 *       <td><code>correct</code></td>
 *       <td>Boolean. Whether the answers of the staff member shoud be corrected</td>
 *       <td>No. Default is <code>"no"</code></td>
 *     </tr>
 *     <tr>
 *       <td><code>points</code></td>
 *       <td>Number of points to assign for this problem</td>
 *       <td>No</td>
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
 *     <tr>
 *       <td><code>student-selection-failed</code></td>
 *       <td>Boolean. Whether the selection of a student failed (see below)</td>
 *       <td>No</td>
 *     </tr>
 *     <tr>
 *       <td><code>student-selection-error-message</code></td>
 *       <td>Error message in case the selection of a student failed (see below)</td>
 *       <td>No</td>
 *     </tr>
 *     <tr>
 *       <td><code>setting-points-failed</code></td>
 *       <td>Boolean. Whether the setting of the point number failed (see below)</td>
 *       <td>No</td>
 *     </tr>
 *     <tr>
 *       <td><code>setting-points-error-message</code></td>
 *       <td>Error message in case the setting of the point number failed (see below)</td>
 *       <td>No</td>
 *     </tr>
 *     <tr>
 *       <td><code>include-correction</code></td>
 *       <td>Boolean. Whether the correction should be added to the XML (see below)</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr>
 *       <td><code>corrected</code></td>
 *       <td>Boolean. Whether the answers of the user haev been corrected</td>
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
 *   The action does the following:
 * </p>
 *   <h4>1. Determination of the user whose data (PPD, answers) should be combined with
 *   the problem. Determination of the tutor view flag</h4> 
 * <p>
 *   The user is either the session user and thus necessarily a staff member (cf. NOTE
 *   above), or a student selected by the staff member. In the latter case, we say that
 *   <em>tutor view</em> is enabled. Tutor view can only be enabled after the timeframe of
 *   the worksheet. This is checked by means of the <code>timeframe-relation</code>
 *   parameter. If this parameter has a value other than <code>"after"</code>, tutor view is
 *   not enabled even if a student is selected. 
 * </p>
 * <p>
 *   Thus, if <code>timeframe-relation</code> is not <code>"after"</code>, the user is
 *   always the staff member and tutor view is always disabled. If
 *   <code>timeframe-relation</code> is <code>"after"</code>, the user and the tutor view
 *   flag are determined as follows:
 * </p>
 * <ul>
 *   <li>
 *     If the <code>"student"</code> parameter is set and is a non-negative number, the user
 *     is set to the student with that id, and tutor view is enabled. If the user is not a
 *     student, an exception is thrown. The user id is stored in the session attribute
 *     <code>"student"</code> of the staff member for later use (see next item).
 *   </li>
 *   <li>
 *     If the <code>"student"</code> parameter is not set, but the <code>"student"</code>
 *     session attribute is set, the user is set to student with the latter id (this id
 *     always points to a student), and tutor view is enabled..
 *   </li>
 *   <li>
 *     If neiter the <code>"student"</code> parameter nor the <code>"student"</code> session
 *     attribute is set, the user is set to the staff member, and tutor view is not enabled.
 *   </li>
 *   <li>
 *     If the <code>"student"</code> parameter is set and is the string
 *     <code>"clear"</code>, the <code>"student"</code> session attribute is deleted if set,
 *     the user is set to the staff member, and tutor view is not enabled..
 *   </li>
 * </ul>
 * <h4>2. Deletion of PPD (optional, staff only)</h4>
 * <p>
 *   If the <code>"clear-data"</code> parameter is set and is <code>true</code>, the
 *   personalized problem data of the user are deleted. In that case, the answers and
 *   correction, if any, are deleted as well, because they are outdated. This feature is
 *   allowed only if the user is the staff member; otherwise, an exception is thrown. The
 *   feature exists to give the staff members an opportunity to solve the same problem with
 *   different personalized data.
 * </p>
 * <h4>3. Correction (optional, staff only)</h4>
 * <p>
 *   If the <code>"correct"</code> parameter is set and is <code>true</code>, the answers of
 *   the user are corrected. This feature is allowed only if the user is the staff member;
 *   otherwise, an exception is thrown. The feature exists to give the staff members an
 *   opportunity to test the correction without waiting for the timframe to end.
 * </p>
 * <h4>4. Setting the point number (optional)</h4>
 * <p>
 *   If the <code>"points"</code> parameter is set, and is a non-negative number less than or
 *   equal to the maximum point number, the number of points the user gets for this problem
 *   is set to this value. This feature can not be used together with deletion of PPD or
 *   correction.
 * </p>
 * <h4>5. Determination whether the correction should be added to the XML</h4>
 * <p>
 *   Usually, the pipeline this action is a part of also contains a transfomer that adds the
 *   corresponding problem datasheet to the XML. The action sets a boolean sitemap
 *   parameter, <code>"include-correction"</code>, which says whether the correction must be
 *   included into this datasheet. The value of the parameter is obtained as follows: If
 *   tutor view is enabled, the parameter is set to <code>"yes"</code> if and only if the
 *   timeframe is over (i.e., timeframe relation is <code>"after"</code>). If tutor view is
 *   not enabled, the parameter is set to <code>"yes"</code> if and only if a correction
 *   exists.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: SetupProblemForStaffAction.java,v 1.19 2007/07/11 15:38:40 grudzin Exp $</code>
 */

public class SetupProblemForStaffAction extends AbstractSetupForStaffAction
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
      (METHOD_NAME + " 1/5: Started." +
       " Parameters: " + LogUtil.parametersToString(parameters));

    Map sitemapParams = new HashMap();
    SessionUser currentUser = null;
    UserProblemData userProblemData = null;

    try
      {
        // Setup currentUser:
        currentUser = (SessionUser)this.manager.lookup(SessionUser.ROLE);
        this.getLogger().debug
          (METHOD_NAME + " 2/5: currentUser = " + LogUtil.identify(currentUser));

        // Get the student:
        int studentId = this.getStudentId(currentUser, parameters, sitemapParams);

        // Some auxiliary variables:
        boolean afterTimeframe = ParamUtil
          .getAsTimeframeRelation(parameters, TIMEFRAME_RELATION).equals(ParamUtil.AFTER);
        boolean tutorView = ( afterTimeframe && studentId != Id.UNDEFINED );
        int userId = (tutorView ? studentId : currentUser.getId());
        boolean clearData = ParamUtil.getAsBoolean(parameters, CLEAR_DATA, false);
        boolean correct = ParamUtil.getAsBoolean(parameters, CORRECT, false);
        String pointsParam = ParamUtil.getAsString(parameters, POINTS, null);
        this.getLogger().debug
          (METHOD_NAME + " 4/5:" +
           " afterTimeframe = " + afterTimeframe +
           ", tutorView = " + tutorView +
           ", userId = " + userId +
           ", clearData = " + clearData +
           ", correct = " + correct +
           ", pointsParam = " + pointsParam);

        // Some checks:
        if ( tutorView && clearData )
          throw new IllegalStateException
            ("Data deletion not allowed in tutor view");
        if ( tutorView && correct )
          throw new IllegalStateException
            ("Forced correction not allowed in tutor view");
        if ( pointsParam != null && clearData )
          throw new IllegalStateException
            ("Data deletion not allowed when points are set");
        if ( pointsParam != null && correct )
          throw new IllegalStateException
            ("Forced correction not allowed when points are set");

        // Set sitemap parameters USER and TUTOR_VIEW:
        sitemapParams.put(USER, Integer.toString(userId));
        sitemapParams.put(TUTOR_VIEW, booleanToString(tutorView));

        // Setup userProblemData:
        userProblemData = (UserProblemData)this.manager.lookup(UserProblemData.ROLE);
        userProblemData.setup
          (ParamUtil.getAsInt(parameters, PROBLEM), ParamUtil.getAsInt(parameters, REF), userId);

        // Setup depending on tutor view:
        if ( tutorView )
          {
            // Set the "include-correction" sitemap parameter:
            sitemapParams.put(INCLUDE_CORRECTION, booleanToString(afterTimeframe));
          }
        else
          {
            // Delete PPD if necessary:
            // (answers and corrections will be deleted, too, because they are outdated)
            if ( clearData ) userProblemData.clear();

            // Correct answers if necessary:
            if ( correct )
              {
                userProblemData.createCorrection(true);
                sitemapParams.put(CORRECTED, "yes");
              }

            // Set the "include-correction" sitemap parameter:
            sitemapParams.put
              (INCLUDE_CORRECTION, booleanToString(userProblemData.correctionExists()));
          }

        // Set score if necessary:
        if ( pointsParam != null )
          {
            try
              {
                userProblemData.setScore
                  (this.getNewScore(pointsParam, userProblemData.getMaxPoints()));
              }
            catch (SettingPointsException exception)
              {
                sitemapParams.put
                  (SETTING_POINTS_FAILED, booleanToString(true));
                sitemapParams.put
                  (SETTING_POINTS_ERROR_MESSAGE,
                   LogUtil.unwrapThrowable(exception).getMessage());
                this.getLogger().warn
                  (METHOD_NAME + ": Caught exception: " + exception);
              }
          }

        this.getLogger().debug
          (METHOD_NAME + " 5/5: Done. sitemapParams = " + sitemapParams);

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
        if ( userProblemData != null )
          this.manager.release(userProblemData);
      }
  }
}
