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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.pseudodocs.SessionUser;
import net.mumie.cocoon.util.LogUtil;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.webapps.session.SessionManager;

/**
 * <p>
 *   Checks if the current user is in certain groups. Recognizes the following parameters:
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
 *       <td><code>user-groups</code></td>
 *       <td>Comma or whitespace separated list of user group ids</td>
 *       <td>Yes</td>
 *     </tr>
 *   </tbody>
 * </table>
 * <p>
 *   If the user is a member of at least one of the groups specified by the
 *   <code>user-groups</code> parameter, the {@link #act act} method returns a
 *   non-<code>null</code> Map object; otherwise, it returns <code>null</code>. The Map
 *   returned in the first case contains a single parameter named <code>"is-member"</code>
 *   with the value <code>"yes"</code>
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: CheckUserGroupsAction.java,v 1.13 2007/12/20 16:41:38 grudzin Exp $</code>
 */

public class CheckUserGroupsAction extends ServiceableAction
{
  /**
   * Parses <code>string</code>, which should be a comma or whitespace separated list of ids,
   * and returns the ids as an array of <code>Integer</code> objects.
   */

  protected Integer[] parseIds (String string)
  {
    StringTokenizer tokenizer = new StringTokenizer(string, ", \\t\\n");
    Integer[] ids = new Integer[tokenizer.countTokens()];
    for (int i = 0; i < ids.length; i++)
      ids[i] = new Integer(tokenizer.nextToken());
    return ids;
  }

  /*
   * See class description.
   */

  public Map act (Redirector redirector, SourceResolver resolver, Map objectModel, String source, 
                  Parameters parameters)
    throws ProcessingException 
  {
    final String METHOD_NAME = "act";
    this.getLogger().debug(METHOD_NAME + " 1/3: Started");
    SessionUser user = null;
    try
      {
	String userGroupsParameter = parameters.getParameter("user-groups");
	this.getLogger().debug
          (METHOD_NAME + " 2/3: userGroupsParameter = " + userGroupsParameter);
	Integer[] allowdUserGroupIds = this.parseIds(userGroupsParameter);
	user = (SessionUser)this.manager.lookup(SessionUser.ROLE);
	List userGroupIds = (List)user.getUserGroupsAsList();
	boolean isMember = false;
	for (int i = 0; !isMember && i < allowdUserGroupIds.length; i++)
	  isMember = userGroupIds.contains(allowdUserGroupIds[i]);
	this.getLogger().debug
          (METHOD_NAME + " 3/3: Done." +
           " user = " + LogUtil.identify(user) +
           ", isMember = " + isMember);
	if ( isMember )
	  {
	    Map sitemapParameters = new HashMap();
	    sitemapParameters.put("is-member", "yes");
	    return sitemapParameters;
	  }
	else
	  return null;
      }
    catch (Exception exception)
      {
	throw new ProcessingException(exception);
      }
    finally
      {
	if ( user != null ) this.manager.release(user);
      }
  }
}
