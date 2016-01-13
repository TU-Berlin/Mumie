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
import net.mumie.cocoon.notions.WorksheetState;
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
 *   Prepares the pipeline for processing a problem in context "selftest" that is viewed by
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
 *       <td><code>clear-data</code></td>
 *       <td>Boolean. Whether the PPD of the staff member should be cleared</td>
 *       <td>No. Default is <code>"no"</code></td>
 *     </tr>
 *     <tr>
 *       <td><code>correct</code></td>
 *       <td>Boolean. Whether the answers of the staff member shoud be corrected</td>
 *       <td>No. Default is <code>"no"</code></td>
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
 * <h4>1. Deletion of PPD (optional)</h4>
 * <p>
 *   If the <code>"clear-data"</code> parameter is set and is <code>true</code>, the
 *   personalized problem data of the user are deleted. In that case, the answers and
 *   correction, if any, are deleted as well, because they are outdated. This feature is
 *   allowed only if the user is the staff member; otherwise, an exception is thrown. The
 *   feature exists to give the staff members an opportunity to solve the same problem with
 *   different personalized data.
 * </p>
 * <h4>2. Correction (optional)</h4>
 * <p>
 *   If the <code>"correct"</code> parameter is set and is <code>true</code>, the answers of
 *   the user are corrected. This feature is allowed only if the user is the staff member;
 *   otherwise, an exception is thrown. The feature exists to give the staff members an
 *   opportunity to test the correction without waiting for the timframe to end.
 * </p>
 * <h4>3. Determination whether the correction should be added to the XML</h4>
 * <p>
 *   Usually, the pipeline this action is a part of also contains a transfomer that adds the
 *   corresponding problem datasheet to the XML. The action sets a boolean sitemap
 *   parameter, <code>"include-correction"</code>, which says whether the correction must be
 *   included into this datasheet. The parameter is set to <code>"yes"</code> if a correction
 *   exists, otherwise to <code>"no"</code>.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: SetupSelftestProblemForStaffAction.java,v 1.1 2007/09/10 19:26:51 rassy Exp $</code>
 */

public class SetupSelftestProblemForStaffAction extends AbstractSetupForStaffAction
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
      (METHOD_NAME + " 1/2: Started." +
       " Parameters: " + LogUtil.parametersToString(parameters));

    Map sitemapParams = new HashMap();
    SessionUser user = null;
    UserProblemData userProblemData = null;

    try
      {
        // Setup user:
        user = (SessionUser)this.manager.lookup(SessionUser.ROLE);

        // Setup userProblemData:
        userProblemData = (UserProblemData)this.manager.lookup(UserProblemData.ROLE);
        userProblemData.setup
          (ParamUtil.getAsInt(parameters, PROBLEM), ParamUtil.getAsInt(parameters, REF), user.getId());

        boolean clearData = ParamUtil.getAsBoolean(parameters, CLEAR_DATA, false);
        boolean correct = ParamUtil.getAsBoolean(parameters, CORRECT, false);

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
        boolean includeCorrection = userProblemData.correctionExists();
        sitemapParams.put(INCLUDE_CORRECTION, booleanToString(includeCorrection));

        this.getLogger().debug
          (METHOD_NAME + " 2/2: Done. sitemapParams = " + sitemapParams);

        return sitemapParams;
      }
    catch (Exception exception)
      {
        throw new ProcessingException(exception);
      }
    finally
      {
        if ( userProblemData != null )
          this.manager.release(userProblemData);
        if ( user != null )
          this.manager.release(user);
      }
  }
}
