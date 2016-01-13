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
import net.mumie.cocoon.clienttime.ClientTime;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.AnnType;
import net.mumie.cocoon.notions.Category;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.RefType;
import net.mumie.cocoon.notions.TimeFormat;
import net.mumie.cocoon.notions.UserGroupName;
import net.mumie.cocoon.notions.UserRole;
import net.mumie.cocoon.notions.WorksheetState;
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
 *   Prepares a pipeline for processing a selftest worksheet. Recognizes the following parameters:
 * </p>
 * <table class="genuine indented">
 *   <thead>
 *     <tr>
 *       <td>Name</td>
 *       <td>Description</td>
 *       <td>Required</td>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td>
 *         <code>worksheet</code>
 *       </td>
 *       <td>
 *         Id of the worksheet
 *       </td>
 *       <td>
 *         Yes
 *       </td>
 *     </tr>
 *     <tr>
 *       <td>
 *         <code>new-state</code>
 *       </td>
 *       <td>
 *         New state of the worksheet
 *       </td>
 *       <td>
 *         No
 *       </td>
 *     </tr>
 *   </tbody>
 * </table>
 * <p>
 *   Sets the following sitemap parameters:
 * </p>
 * <table class="genuine indented">
 *   <thead>
 *     <tr>
 *       <td>Name</td>
 *       <td>Description</td>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td>
 *         <code>category</code>
 *       </td>
 *       <td>
 *         Category of the worksheet (as name)
 *       </td>
 *     </tr>
 *     <tr>
 *       <td>
 *         <code>course</code>
 *       </td>
 *       <td>
 *         Id of the course the worksheet belongs to
 *       </td>
 *     </tr>
 *     <tr>
 *       <td>
 *         <code>label</code>
 *       </td>
 *       <td>
 *         Label of the worksheet
 *       </td>
 *     </tr>
 *     <tr>
 *       <td>
 *         <code>summary</code>
 *       </td>
 *       <td>
 *         Id of the worksheet summary (generic summary)
 *       </td>
 *     </tr>
 *     <tr>
 *       <td>
 *         <code>state</code>
 *       </td>
 *       <td>
 *         State of the worksheet
 *       </td>
 *     </tr>
 *     <tr>
 *       <td>
 *         <code>correct</code>
 *       </td>
 *       <td>
 *         Whether the worksheet must be corrected (boolean)
 *       </td>
 *     </tr>
 *     <tr>
 *       <td>
 *         <code>is-course-member</code>
 *       </td>
 *       <td>
 *         Whether the current user is a member of the course
 *       </td>
 *     </tr>
 *     <tr>
 *       <td>
 *         <code>is-staff-member</code>
 *       </td>
 *       <td>
 *         Whether the current user is a staff member
 *       </td>
 *     </tr>
 *     <tr>
 *       <td><code>in-feedback-state</code></td>
 *       <td>Whether the worksheet is in the state <code>"feedback"</code>. Possible
 *       values: <code>"yes", "no"</code> (self-explanatory).</td>
 *     </tr>
 *     <tr>
 *       <td>
 *         <code>case</code>
 *       </td>
 *       <td>Distinguishes different cases when a selftest worksheet is requested (see below
 *         for details)</td>
 *     </tr>
 *     <tr>
 *       <td><code>timeframe-start</code></td>
 *       <td>Date when the timeframe of the worksheet beginns.</td>
 *     </tr>
 *     <tr>
 *       <td><code>timeframe-end</code></td>
 *       <td>Date when the timeframe of the worksheet ends.</td>
 *     </tr>
 *     <tr>
 *       <td><code>timeframe-start-raw</code></td>
 *       <td>Date when the timeframe of the worksheet begins, as milliseconds since Jan 01
 *       1970, 00:00.</td>  
 *     </tr>
 *     <tr>
 *       <td><code>timeframe-end-raw</code></td>
 *       <td>Date when the timeframe of the worksheet ends, as milliseconds since Jan 01
 *       1970, 00:00.</td>  
 *     </tr>
 *     <tr>
 *       <td><code>timeframe-relation</code></td>
 *       <td>Whether the current time is before, inside, or after the timeframe of the
 *       worksheet. Possible values: <code>"before", "inside", "after"</code>
 *       (self-explanatory).</td>
 *     </tr>
 *     <tr>
 *       <td><code>after-timeframe</code></td>
 *       <td>Whether the current time is after the timeframe end. Possible values:
 *       <code>"yes", "no"</code> (self-explanatory).</td>
 *     </tr>
 *     <tr>
 *        <td><code>theme</code></td>
 *        <td>Id of the theme</td>
 *     </tr>
 *   </tbody>
 * </table>
 * <p>
 *   Here are the pssible values of the <code>case</code> parameter and their meanings:
 * </p>
 * <table class="genuine indented">
 *   <thead>
 *     <tr>
 *       <td style="width:20em">Value</td>
 *       <td style="width:30em">Meaning</td>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td><code>"staff-or-student-inside-timeframe"</code></td>
 *       <td>User is a staff member, or user is a member of the course and current time
 *         is inside timeframe</td>
 *     </tr>
 *     <tr>
 *       <td><code>"student-before-timeframe"</code></td>
 *       <td>User a member of the course and current time
 *         is before timeframe</td>
 *     </tr>
 *     <tr>
 *       <td><code>"student-after-timeframe"</code></td>
 *       <td>User a member of the course and current time
 *         is after timeframe</td>
 *     </tr>
 *     <tr>
 *       <td><code>"other"</code></td>
 *       <td>User is neither a staff nor a course member</td>
 *     </tr>
 *   </tbody>
 * </table>
 * <p>
 *   This action executes the steps which are necessary when a selftest worksheet is about to
 *   be displayed. The steps are:
 * </p>
 * <ul>
 *   <li>
 *     It may happen that the current response is the response after the user has
 *     changed the worksheet state. The action assumes that this is the case if, and only if,
 *     the <code>new-state</code> parameter has been passed to it. By this means, the action
 *     detects whether the state has changed and what are the old and the new states.
 *   </li>
 *   <li>
 *     If the state has changed to <code>work</code>, the PPDs, answers, and corrections for
 *     all problems of the worksheet must be deletet. This is done by the action.
 *   <li>
 *     If the state has changed to <code>feedback</code>, all problems of the worksheet must be
 *     corrected. This is not done by the action itself; rather, the action sets the sitemap
 *     parameter <code>correct</code> to <code>"yes"</code> in this case (in all other cases,
 *     the parameter is set to <code>"no"</code>). The actual correction should be carried out
 *     by the respective transformer, which should be included in the sitemap if the
 *     <code>correct</code> parameter has the value <code>"yes"</code>.
 *   </li>
 * </ul>
 * 
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: PrepareSelftestAction.java,v 1.18 2009/10/09 01:09:09 rassy Exp $</code>
 */

public class PrepareSelftestAction extends ServiceableAction
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
    final String WORKSHEET = "worksheet";
    final String REF = "ref";
    final String TIME_FORMAT = "time-format";
    final String WORK = "work";
    final String FEEDBACK = "feedback";
    final String NEW_STATE = "new-state";
    final String BEFORE = "before";
    final String INSIDE = "inside";
    final String AFTER = "after";
    final String YES = "yes";
    final String NO = "no";

    DbHelper dbHelper = null;
    User user = null;    
    ClientTime clientTime = null;

    try
      {
        // Init services:
        dbHelper = (DbHelper)this.manager.lookup(DbHelper.ROLE);
        user = (User)this.manager.lookup(SessionUser.ROLE);
        clientTime = (ClientTime)this.manager.lookup(ClientTime.ROLE);

        // User id:
        int userId = user.getId();

        // Worksheet id:
        int worksheetId = ParamUtil.getAsId(parameters, "worksheet");

        // Get worksheet data:
        ResultSet worksheetData = dbHelper.queryWorksheetData(worksheetId);
        if ( ! worksheetData.next() )
          throw new ProcessingException
            ("No worksheet data found (result set empty)");

        // Get the worksheet category:
        String worksheetCategory = Category.nameFor(worksheetData.getInt(DbColumn.CATEGORY));

        // Get the course id:
        int courseId = worksheetData.getInt(DbColumn.COURSE_ID);

        // Get the class id:
        int classId = worksheetData.getInt(DbColumn.CLASS_ID);
        
        // Get the worksheet label:
        String label = worksheetData.getString(DbColumn.LABEL);
        
        // Get the worksheet summary id:
        int summaryId = worksheetData.getInt(DbColumn.SUMMARY);

        // Get the "is-course-member" flag:
        boolean isCourseMember =
          dbHelper.checkMembership(user.getId(), DocType.COURSE, courseId);

        // Check if user is a staff member:
        boolean isStaffMember = 
          ( dbHelper.checkAdmin(user.getId()) ||
            dbHelper.checkTutorForCourse(user.getId(), courseId) ||
            dbHelper.checkLecturerForCourse(user.getId(), courseId) );

        // Get the timeframe start/end dates:
        Date timeframeStartDate = worksheetData.getTimestamp(DbColumn.TIMEFRAME_START);
        Date timeframeEndDate = worksheetData.getTimestamp(DbColumn.TIMEFRAME_END);

        // Get the "timeframe-start-raw" and "timeframe-end-raw" values:
        long timeframeStartRaw = timeframeStartDate.getTime();
        long timeframeEndRaw = timeframeEndDate.getTime();

        // Get the "timeframe-start" and "timeframe-end" values:
        DateFormat dateFormat =
          ParamUtil.getAsDateFormat(parameters, TIME_FORMAT, TimeFormat.DEFAULT); 
        String timeframeStart = dateFormat.format(timeframeStartDate);
        String timeframeEnd = dateFormat.format(timeframeEndDate);

        // Get the "timeframe-relation" value:
        String timeframeRelation;
        Date now = new Date(clientTime.getTime());
        if ( now.before(timeframeStartDate) )
          timeframeRelation = BEFORE;
        else if ( now.after(timeframeEndDate) )
          timeframeRelation = AFTER;
        else
          timeframeRelation = INSIDE;

        //Get theme id
        String theme = "0";
          theme = Integer.toString(dbHelper.getThemeForWorksheetById(worksheetId));

        // Get worksheet state:
        int state = dbHelper.getWorksheetState(worksheetId, userId);
        if ( state == WorksheetState.UNDEFINED )
          {
            int annType = AnnType.PROBLEM_ANSWERS;
            // Worksheet has no state yet
            state = WorksheetState.WORK;
            dbHelper.storeWorksheetState(worksheetId, userId, annType, state);
          }
        String stateName = WorksheetState.nameFor[state];
        String oldStateName = stateName;

        // Default for new state (null means no state change):
        String newStateName = null;

        // If timeframe is over, but state is still 'work' and user is not staff,
        // set new state to 'feedback'
        if ( timeframeRelation == AFTER && state == WorksheetState.WORK && !isStaffMember )
          newStateName = WorksheetState.nameFor[WorksheetState.FEEDBACK];
        else
          // Get new state name from request paramter, if any:
          newStateName = ParamUtil.getAsString(parameters, NEW_STATE, null);

        // Set default for the "correct" flag:
        boolean correct = false;

        if ( newStateName != null )
          {
            int newState = WorksheetState.codeFor(newStateName);

            // Update state in the database:
            dbHelper.updateWorksheetState(worksheetId, userId, newState);

            // Delete PPD, answers, and corrections if necessary:
            if ( newState == WorksheetState.WORK )
              dbHelper.deleteUserAnnotationsByRefOrigin
                (userId,
                 DocType.WORKSHEET,
                 DocType.GENERIC_PROBLEM,
                 worksheetId,
                 null);

            // Set "correct" flag is necessary:
            correct = ( newState == WorksheetState.FEEDBACK );

            // Update state:
            state = newState;
            stateName = newStateName;
          }

        // The inFeedbackState flag:
        boolean inFeedbackState = ( state == WorksheetState.FEEDBACK );

        // Compose "case" parameter value:
        String theCase;
        if ( isStaffMember )
          theCase = "staff-or-student-inside-timeframe";
        else if ( isCourseMember )
          {
            if ( timeframeRelation.equals(BEFORE) )
              theCase = "student-before-timeframe";
            else if ( timeframeRelation.equals(INSIDE) )
              theCase = "staff-or-student-inside-timeframe";
            else 
              theCase = "student-after-timeframe";
          }
        else
          theCase = "other";

        this.getLogger().debug
          (METHOD_NAME + " 2/3:" +
           " worksheetId = " + worksheetId +
           ", worksheetCategory = " + worksheetCategory +
           ", courseId = " + courseId +
           ", classId = " + classId +
           ", label = " + label +
           ", summaryId = " + summaryId +
           ", stateName = " + stateName +
           ", oldStateName = " + oldStateName +
           ", inFeedbackState = " + inFeedbackState +
           ", correct = " + correct +
           ", isCourseMember = " + isCourseMember +
           ", isStaffMember = " + isStaffMember +
           ", theCase = " + theCase +
           ", timeframeStartRaw = " + timeframeStartRaw +
           ", timeframeEndRaw = " + timeframeEndRaw +
           ", timeframeStart = " + timeframeStart +
           ", timeframeEnd = " + timeframeEnd +
           ", theme = " + theme +
           ", timeframeRelation = " + timeframeRelation);
        
        // Return sitemap parameters:
        Map sitemapParameters = new HashMap();
        sitemapParameters.put("worksheet", Integer.toString(worksheetId));
        sitemapParameters.put("category", worksheetCategory);
        sitemapParameters.put("course", Integer.toString(courseId));
        sitemapParameters.put("class", Integer.toString(classId));
        sitemapParameters.put("label", label);
        sitemapParameters.put("summary", Integer.toString(summaryId));
        sitemapParameters.put("state", stateName);
        sitemapParameters.put("in-feedback-state", booleanToString(inFeedbackState));
        sitemapParameters.put("correct", booleanToString(correct));
        sitemapParameters.put("is-course-member", booleanToString(isCourseMember));
        sitemapParameters.put("is-staff-member", booleanToString(isStaffMember));
        sitemapParameters.put("case", theCase);
        sitemapParameters.put("timeframe-start-raw", Long.toString(timeframeStartRaw));
        sitemapParameters.put("timeframe-end-raw", Long.toString(timeframeEndRaw));
        sitemapParameters.put("timeframe-start", timeframeStart);
        sitemapParameters.put("timeframe-end", timeframeEnd);
        sitemapParameters.put("timeframe-relation", timeframeRelation);
        sitemapParameters.put("theme", theme);
        this.getLogger().debug(METHOD_NAME + " 3/3: Done");
        return sitemapParameters;
      }
    catch  (Exception exception)
      {
	throw new ProcessingException(exception);
      }
    finally
      {
        if ( clientTime != null )
          this.manager.release(clientTime);

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
