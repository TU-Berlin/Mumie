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


public class SetupTrainingProblemAction extends ServiceableAction
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
      (METHOD_NAME + " 1/3: Started." +
       " Parameters: " + LogUtil.parametersToString(parameters));

    SessionUser currentUser = null;
    UserProblemData userProblemData = null;

    try
      {
        // Initialize services:
        currentUser = (SessionUser)this.manager.lookup(SessionUser.ROLE);
        userProblemData = (UserProblemData)this.manager.lookup(UserProblemData.ROLE);

        // Input parameters:
        int problemId = ParamUtil.getAsInt(parameters, "problem");
        int refId = ParamUtil.getAsInt(parameters, "ref");
        boolean clearData = ParamUtil.getAsBoolean(parameters, "clear-data", false);
        boolean correct = ParamUtil.getAsBoolean(parameters, "correct", false);

        // Id of the logged-in user:
        int userId = currentUser.getId();

        this.getLogger().debug
          (METHOD_NAME + " 2/3:" +
           " problemId = " + problemId +
           ", refId = " + refId +
           ", clearData = " + clearData +
           ", correct = " + correct +
           ", userId = " + userId);

        // Initialize output params:
        Map sitemapParams = new HashMap();

        // Set sitemap parameter "user":
        sitemapParams.put("user", Integer.toString(userId));

        // Setup userProblemData:
        userProblemData.setup(problemId, refId, userId);

        // Delete PPD if necessary:
        // (answers and corrections will be deleted, too, because they are outdated)
        if ( clearData ) userProblemData.clear();

        // Correct answers if necessary:
        if ( correct )
          {
            userProblemData.createCorrection(true);
            sitemapParams.put("corrected", "yes");
          }

        // Set the "include-correction" sitemap parameter:
        sitemapParams.put("include-correction", booleanToString(userProblemData.correctionExists()));

        this.getLogger().debug
          (METHOD_NAME + " 3/3: Done. sitemapParams = " + sitemapParams);

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

  /**
   * Auxiliary method; turns a boolean into the string "yes" or "no".
   */

  protected static String booleanToString (boolean value)
  {
    return (value ? "yes" : "no");
  }
}
