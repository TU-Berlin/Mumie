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
import net.mumie.cocoon.notions.Category;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.DocPath;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.PseudoDocType;
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
 *   Provides information about a worksheet as sitemap paramters. Recognizes the
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
 *       <td><code>worksheet</code></td>
 *       <td>Id of the {@link DocType#WORKSHEET WORKSHEET}.</td>
 *       <td rowspan="2">One of the both</td>
 *     </tr>
 *     <tr>
 *       <td><code>ref</code></td>
 *       <td>Id of the 
 *       {@link DocType#WORKSHEET WORKSHEET}&ndash;{@link DocType#PROBLEM PROBLEM}
 *       reference. This specifies the {@link DocType#WORKSHEET WORKSHEET},
 *       too.</td>
 *     </tr>
 *     <tr>
 *       <td><code>user</code></td>
 *       <td>Id of the user to which the informations should refer.</td>
 *       <td>No. Defaults to the user who owns the session.</td>
 *     </tr>
 *     <tr>
 *       <td><code>time-format</code></td>
 *       <td>The time format of the <code>timeframe-start</code> and
 *       <code>timeframe-end</code> sitemap parameter values (s.b.).</td>
 *       <td>No. Defualts to {@link TimeFormat#DEFAULT DEFAULT}.</td>
 *     </tr>
 *   </tbody>
 * </table>
 * <p>
 *   Note that the worksheet in turn specifies the (newest) course it is in, since
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
 *       <td><code>worksheet</code></td>
 *       <td>Id of the worksheet</td>
 *     </tr>
 *     <tr>
 *       <td><code>category</code></td>
 *       <td>Category of the worksheet</td>
 *     </tr>
 *     <tr>
 *       <td><code>course</code></td>
 *       <td>Id of the course</td>
 *     </tr>
 *     <tr>
 *       <td><code>label</code></td>
 *       <td>Label of the worksheet in the course</td>
 *     </tr>
 *     <tr>
 *       <td><code>summary</code></td>
 *       <td>Id of the worksheet summary</td>
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
 *       <td>Distinguishes different cases when a worksheet or problem is requested (see
 *       below for details)</td>
 *     </tr>
 *     <tr>
 *       <td><code>case-store-answers</code></td>
 *       <td>Distinguishes different cases when answers to a problem are to be stored
 *       (see below for details)</td>
 *     </tr>
 *     <tr>
 *       <td><code>case-view-correction</code></td>
 *       <td>Distinguishes different cases when a problem correction is requested
 *       (see below for details)</td>
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
 *     <tr>
 *        <td><code>is-default-course</code></td>
 *        <td>Whether course is default course</td>
 *     </tr>
 *     
 *   </tbody>
 * </table>
 * <p>
 *   Essentially, there are three different situations where this action may be applied:
 * </p>
 * <ol>
 *   <li>When a worksheet or problem is requested</li>
 *   <li>When answers to a problem are stored</li>
 *   <li>When the correctrion of a problem is requestet </li>
 * </ol>
 * <p>
 *   In each of this situations, several cases are possible which require conditional
 *   processing in the sitemap. To this end, the action provides three case parameters, one
 *   for each situation. The paramters are:
 * </p>
 * <dl>
 *   <dt><code>case</code></dt>
 *   <dd>
 *     For situation 1. Possible values and meanings:
 *     <table class="genuine indented">
 *       <thead>
 *         <tr>
 *           <td style="width:20em">Value</td>
 *           <td style="width:30em">Meaning</td>
 *         </tr>
 *       </thead>
 *       <tbody>
 *         <tr>
 *           <td><code>"staff"</code></td>
 *           <td>User is a staff member</td>
 *         </tr>
 *         <tr>
 *           <td><code>"student-before-timeframe"</code></td>
 *           <td>User is a course member and current time is before timeframe</td>
 *         </tr>
 *         <tr>
 *           <td><code>"student-not-before-timeframe"</code></td>
 *           <td>User is a course member and current time is inside or after timeframe</td>
 *         </tr>
 *         <tr>
 *           <td><code>"other"</code></td>
 *           <td>User is not a course member</td>
 *         </tr>
 *       </tbody>
 *     </table>
 *   </dd>
 *   <dt><code>case-store-answers</code></dt>
 *   <dd>
 *     For situation 2. Possible values and meanings:
 *     <table class="genuine indented">
 *       <thead>
 *         <tr>
 *           <td style="width:20em">Value</td>
 *           <td style="width:30em">Meaning</td>
 *         </tr>
 *       </thead>
 *       <tbody>
 *         <tr>
 *           <td><code>"staff"</code></td>
 *           <td>User is a staff member</td>
 *         </tr>
 *         <tr>
 *           <td><code>"student-before-timeframe"</code></td>
 *           <td>User is a course member and current time is before timeframe</td>
 *         </tr>
 *         <tr>
 *           <td><code>"student-inside-timeframe"</code></td>
 *           <td>User is a course member and current time is inside timframe</td>
 *         </tr>
 *         <tr>
 *           <td><code>"student-after-timeframe"</code></td>
 *           <td>User is a course member and current time is after timframe</td>
 *         </tr>
 *         <tr>
 *           <td><code>"other"</code></td>
 *           <td>User is not a course member</td>
 *         </tr>
 *       </tbody>
 *     </table>
 *   </dd>
 *   <dt><code>case-view-correction</code></dt>
 *   <dd>
 *     For situation 3. Possible values and meanings:
 *     <table class="genuine indented">
 *       <thead>
 *         <tr>
 *           <td style="width:20em">Value</td>
 *           <td style="width:30em">Meaning</td>
 *         </tr>
 *       </thead>
 *       <tbody>
 *         <tr>
 *           <td><code>"staff"</code></td>
 *           <td>User is a staff member</td>
 *         </tr>
 *         <tr>
 *           <td><code>"student-after-timeframe"</code></td>
 *           <td>User is a course member and current time is after timframe</td>
 *         </tr>
 *         <tr>
 *           <td><code>"student-not-after-timeframe-selftest"</code></td>
 *           <td>User is a course member and current time is before or inside timframe and worksheet-category is selftest</td>
 *         </tr>
 *         <tr>
 *           <td><code>"student-not-after-timeframe"</code></td>
 *           <td>User is a course member and current time is before or inside timframe</td>
 *         </tr>
 *         <tr>
 *           <td><code>"other"</code></td>
 *           <td>User is not a course member</td>
 *       </tbody>
 *     </table>
 *   </dd>
 * </dl>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: ProvideWorksheetInfosAction.java,v 1.28 2009/12/11 12:26:47 linges Exp $</code>
 */

public class ProvideWorksheetInfosAction extends ServiceableAction
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
    this.getLogger().debug(METHOD_NAME + " 1/3: Started");

    // Some constants:
    final String USER = "user";
    final String WORKSHEET = "worksheet";
    final String REF = "ref";
    final String TIME_FORMAT = "time-format";
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

        // Init client time object:
        clientTime = (ClientTime)this.manager.lookup(ClientTime.ROLE);

        // Get worksheet data:
        ResultSet worksheetData;
        if ( ParamUtil.checkIfSet(parameters, WORKSHEET) )
          worksheetData = dbHelper.queryWorksheetData
            (ParamUtil.getAsId(parameters, WORKSHEET));
        else if ( ParamUtil.checkIfSet(parameters, REF) )  
          worksheetData = dbHelper.queryWorksheetDataByProblemRef
            (ParamUtil.getAsId(parameters, REF));
        else
          throw new IllegalArgumentException
            ("Missing parameters: " + WORKSHEET + " or " + REF +
             " (at least one must be present)");
        
        if ( ! worksheetData.next() )
          throw new ProcessingException
            ("No worksheet data found (result set empty)");
        
        // Get the worksheet id:
        int worksheetId = worksheetData.getInt(DbColumn.ID);

        // Get the worksheet category:
        int worksheetCategory = worksheetData.getInt(DbColumn.CATEGORY);

        // Get the course id:
        int courseId = worksheetData.getInt(DbColumn.COURSE_ID);
        
        //is Default Course:
        
        boolean isDefaultCourse = (courseId == dbHelper.getIdForPath(DocType.COURSE, DocPath.DEFAULT_COURSE));
        
        // Get the worksheet label:
        String label = worksheetData.getString(DbColumn.LABEL);
        
        // Get the worksheet summary id:
        int summaryId = worksheetData.getInt(DbColumn.SUMMARY);

        // Get the "is-course-member" flag:
        boolean isCourseMember =
          dbHelper.checkMembership(user.getId(), DocType.COURSE, courseId);

        // Get the theme:
        int themeId = dbHelper.getThemeForWorksheetById(worksheetId);

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

        // Compose "case" parameter value:
        String theCase;
        if ( isStaffMember )
          theCase = "staff";
        else if ( isCourseMember )
          {
            if ( timeframeRelation.equals(BEFORE) )
              theCase = "student-before-timeframe";
            else 
              theCase = "student-not-before-timeframe";
          }
        else
          theCase = "other";

        // Compose "case-store-answers" parameter value:
        String caseStoreAnswers;
        if ( isStaffMember )
          caseStoreAnswers = "staff";
        else if ( isCourseMember )
          {
            if ( timeframeRelation.equals(BEFORE) )
              caseStoreAnswers = "student-before-timeframe";
            else if ( timeframeRelation.equals(INSIDE) )
              caseStoreAnswers = "student-inside-timeframe";
            else
              caseStoreAnswers = "student-after-timeframe";
          }
        else
          caseStoreAnswers = "other";

        // Compose "case-view-correction" parameter value:
        String caseViewCorrection;
        if ( isStaffMember )
          caseViewCorrection = "staff";
        else if ( isCourseMember )
          {
            if ( timeframeRelation.equals(AFTER) )
              caseViewCorrection = "student-after-timeframe";
            else
              {        
              
                if ( worksheetCategory == Category.TRAINING )
                {
                  caseViewCorrection = "student-not-after-timeframe-training";
                }
                
                else if ( worksheetCategory == Category.SELFTEST )
                  {
                    caseViewCorrection = "student-not-after-timeframe-selftest";
                  }
                

                else
                  caseViewCorrection = "student-not-after-timeframe";
              }
          }     
        else
          caseViewCorrection = "other";

        // Get the "after-timeframe" flag:
        boolean afterTimeframe = timeframeRelation.equals(AFTER);
                                         
        this.getLogger().debug
          (METHOD_NAME + " 2/3:" +
           " worksheetId = " + worksheetId +
           " worksheetCategory = " + worksheetCategory +
           ", courseId = " + courseId +
           ", label = " + label +
           ", summaryId = " + summaryId +
           ", isCourseMember = " + isCourseMember +
           ", isStaffMember = " + isStaffMember +
           ", theCase = " + theCase +
           ", caseStoreAnswers = " + caseStoreAnswers +
           ", caseViewCorrection = " + caseViewCorrection +
           ", timeframeStartRaw = " + timeframeStartRaw +
           ", timeframeEndRaw = " + timeframeEndRaw +
           ", timeframeStart = " + timeframeStart +
           ", timeframeEnd = " + timeframeEnd +
           ", timeframeRelation = " + timeframeRelation +
           ", theme = " + themeId +
           ", afterTimeframe = " + afterTimeframe);
        
        // Return sitemap parameters:
        Map sitemapParameters = new HashMap();
        sitemapParameters.put("worksheet", Integer.toString(worksheetId));
        sitemapParameters.put("category", Category.nameFor(worksheetCategory));
        sitemapParameters.put("course", Integer.toString(courseId));
        sitemapParameters.put("label", label);
        sitemapParameters.put("summary", Integer.toString(summaryId));
        sitemapParameters.put("is-course-member", booleanToString(isCourseMember));
        sitemapParameters.put("is-staff-member", booleanToString(isStaffMember));
        sitemapParameters.put("case", theCase);
        sitemapParameters.put("case-store-answers", caseStoreAnswers);
        sitemapParameters.put("case-view-correction", caseViewCorrection);
        sitemapParameters.put("timeframe-start-raw", Long.toString(timeframeStartRaw));
        sitemapParameters.put("timeframe-end-raw", Long.toString(timeframeEndRaw));
        sitemapParameters.put("timeframe-start", timeframeStart);
        sitemapParameters.put("timeframe-end", timeframeEnd);
        sitemapParameters.put("timeframe-relation", timeframeRelation);
        sitemapParameters.put("after-timeframe", booleanToString(afterTimeframe));
        sitemapParameters.put("theme", Integer.toString(themeId));
        sitemapParameters.put("is-default-course", Boolean.toString(isDefaultCourse));
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
