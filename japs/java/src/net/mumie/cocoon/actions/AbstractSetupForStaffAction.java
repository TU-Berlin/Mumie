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
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: AbstractSetupForStaffAction.java,v 1.5 2009/01/28 13:16:54 rassy Exp $</code>
 */

public abstract class AbstractSetupForStaffAction extends ServiceableAction
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The request and session parameter <code>"student"</code> as a constant.
   */

  protected static final String STUDENT = "student";

  /**
   * The keyword <code>"clear"</code> as a constant.
   */

  protected static final String CLEAR = "clear";

  /**
   * The parameter <code>"user"</code> as a constant.
   */

  protected static final String USER = "user";

  /**
   * The parameter <code>"timeframe-relation"</code> as a constant.
   */

  protected static final String TIMEFRAME_RELATION = "timeframe-relation";

  /**
   * The parameter <code>"correct"</code> as a constant.
   */

  protected static final String CORRECT = "correct";

  /**
   * The parameter <code>"points"</code> as a constant.
   */

  protected static final String POINTS = "points";

  /**
   * The parameter <code>"clear-data"</code> as a constant.
   */

  protected static final String CLEAR_DATA = "clear-data";

  /**
   * the parameter <code>"problem"</code> as a constant.
   */

  protected static final String PROBLEM = "problem";

  /**
   * the parameter <code>"ref"</code> as a constant.
   */

  protected static final String REF = "ref";

  /**
   * The sitemap paramter <code>"student-selection-failed"</code> as a constant.
   */

  protected static final String STUDENT_SELECTION_FAILED = "student-selection-failed";

  /**
   * The sitemap paramter <code>"student-selection-error-message"</code> as a constant.
   */

  protected static final String STUDENT_SELECTION_ERROR_MESSAGE =
    "student-selection-error-message";

  /**
   * The sitemap paramter <code>"setting-points-failed"</code> as a constant.
   */

  protected static final String SETTING_POINTS_FAILED = "setting-points-failed";

  /**
   * The sitemap paramter <code>"setting-points-error-message"</code> as a constant.
   */

  protected static final String SETTING_POINTS_ERROR_MESSAGE =
    "setting-points-error-message";

  /**
   * The sitemap paramter <code>"tutor-view"</code> as a constant.
   */

  protected static final String TUTOR_VIEW = "tutor-view";

  /**
   * The sitemap paramter <code>"include-correction"</code> as a constant.
   */

  protected static final String INCLUDE_CORRECTION = "include-correction";

  /**
   * The sitemap paramter <code>"corrected"</code> as a constant.
   */

  protected static final String CORRECTED = "corrected";

  // --------------------------------------------------------------------------------
  // Auxiliary methods
  // --------------------------------------------------------------------------------

  /**
   * 
   */

  protected int getStudentId (SessionUser currentUser,
                              Parameters parameters,
                              Map sitemapParams)
    throws Exception
  {
    final String METHOD_NAME = "getStudentId";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");

    int studentId = this.getStudentIdFromSession(currentUser, SessionAttrib.STUDENT);
    String studentParam = ParamUtil.getAsString(parameters, STUDENT, null);
    if ( studentParam != null )
      {
        if ( studentParam.equals(CLEAR) )
          {
            // User wants to unselect the student:
            currentUser.setSessionAttribute(STUDENT, null);
            studentId = Id.UNDEFINED;
          }
        else
          {
            // User wants to select a new student:
            try
              {
                studentId = this.getNewStudentId(studentParam);
                currentUser.setSessionAttribute
                  (SessionAttrib.STUDENT, new Integer(studentId));
              }
            catch (StudentSelectionException exception)
              {
                sitemapParams.put
                  (STUDENT_SELECTION_FAILED, booleanToString(true));
                sitemapParams.put
                  (STUDENT_SELECTION_ERROR_MESSAGE,
                   LogUtil.unwrapThrowable(exception).getMessage());
                this.getLogger().warn
                  (METHOD_NAME + ": Caught exception: " + exception);
              }
          }
      }
    this.getLogger().debug(METHOD_NAME + " 2/2: studentId = " + studentId);
    return studentId;
  }

  /**
   * 
   */

  protected int getStudentIdFromSession (SessionUser currentUser, String attribName)
    throws Exception
  {
    final String METHOD_NAME = "getStudentIdFromSession";
    this.getLogger().debug
      (METHOD_NAME + " 1/2: Started." +
       " attribName = " + attribName);
    Integer sessionAttribValue =
      (Integer)currentUser.getSessionAttribute(attribName);
    int studentId =
      (sessionAttribValue != null
           ? sessionAttribValue.intValue()
       : Id.UNDEFINED);
    this.getLogger().debug
      (METHOD_NAME + " 2/2: Done. studentId = " + studentId);
    return studentId;
  }

  /**
   * Returns the id of the newly selected student from the specified parameter value.
   */

  protected int getNewStudentId (String value)
    throws StudentSelectionException
  {
    int newStudentId;

    // Convert value to an integer:
    try
      {
        newStudentId = Integer.parseInt(value.trim());
      }
    catch (NumberFormatException exception)
      {
        throw new StudentSelectionException
          ("Invalid student id: " + value + " (must be a number)");
      }

    // Check if newStudentId is non-negative:
    if ( newStudentId < 0 )
      throw new StudentSelectionException
        ("Invalid student id: " + newStudentId + " (must not be negative)");

    // Check if newStudentId points to an existing student:
    GeneralUser newStudent = null;
    try
      {
        newStudent = (GeneralUser)this.manager.lookup(GeneralUser.ROLE);
        newStudent.setId(newStudentId);
        if ( !newStudent.getUserGroupNamesAsList().contains(UserGroupName.STUDENTS) )
          throw new StudentSelectionException
            ("User does not exist or is not a student: " + newStudentId);
      }
    catch (Exception exception)
      {
        throw new StudentSelectionException(exception);
      }
    finally
      {
        if ( newStudent != null )
          this.manager.release(newStudent);
      }

    return newStudentId;
  }

  /**
   * Returns the new score from the corresponding parameter value and the maximum point number.
   */

  protected float getNewScore (String pointsParam, int maxPoints)
    throws SettingPointsException
  {
    final String METHOD_NAME = "getNewScore";
    this.getLogger().debug
      (METHOD_NAME + " 1/2: Started." +
       " pointsParam = " + pointsParam +
       ", maxPoints = " + maxPoints);

    float newPoints;

    // Convert value to a float:
    try
      {
        newPoints = Float.parseFloat(pointsParam.trim());
      }
    catch (NumberFormatException exception)
      {
        throw new SettingPointsException
          ("Invalid points value: " + pointsParam + " (must be a number)");
      }

    // Check if newPoints is a proper number:
    if ( Float.isNaN(newPoints) || Float.isInfinite(newPoints) )
      throw new SettingPointsException
        ("Invalid points value: " + pointsParam +  " (must be a number)");

    // Check if newPoints is non-negative:
    if ( newPoints < 0 )
      throw new SettingPointsException
        ("Invalid points value: " + newPoints + " (must not be negative)");

    // Check if newPoints <= maxPoints:
    if ( newPoints > maxPoints )
      throw new SettingPointsException
        ("Invalid points value: " + newPoints +
         " (must be less then or equal to the maximum point number)");

    float newScore = (maxPoints > 0 ? newPoints / (float)maxPoints : 0);

    this.getLogger().debug
      (METHOD_NAME + " 2/2: Done." +
       " newPoints = " + newPoints +
       ", newScore = " + newScore);

    return newScore;
  }

  /**
   * Auxiliary method; turns a boolean into the string "yes" or "no".
   */

  protected static String booleanToString (boolean value)
  {
    return (value ? "yes" : "no");
  }
}
