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
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.pseudodocs.SessionUser;
import net.mumie.cocoon.util.LogUtil;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.webapps.session.SessionManager;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * <p>
 *   Checks if the user of the current session is allowed to see a certain document. Expects
 *   the following paramters:
 * </p>
 * <table class="genuine indented">
 *   <thead>
 *     <tr>
 *       <td>Name</td>
 *       <td colspan="2">Description</td>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td><code>type</code></td>
 *       <td>The type of the document, as numerical code</td>
 *       <td rowspan="2" style="vertical-align:middle">Only one of the both</td>
 *     </tr>
 *     <tr>
 *       <td><code>type-name</code></td>
 *       <td>The type of the document, as string name</td>
 *     </tr>
 *     <tr>
 *       <td><code>id</code></td>
 *       <td colspan="2">The id of the document.</td>
 *     </tr>
 *   </tbody>
 * </table>
 * <p>
 *   NOTE: Only one of the parameters <code>type</code> and <code>type-name</code> should be
 *   set. If both are set, <code>type</code> takes precedence.
 * </p>
 * <p>
 *   If the user has permission to see the document, the {@link #act act} method returns a 
 *   non-<code>null</code> <code>Map</code> object; otherwise, it returns
 *   <code>null</code>. Thus, put the code to be executed in the first case inside the
 *   <code>map:act</code> element  and the code to be executed in the second case (usually a
 *   redirect to an error page) after the <code>map:act</code> element (cf. Cocoon actions
 *   documentation). 
 * </p>
 * <p>
 *   The <code>Map</code> returned in the first case (user has permission) contains a single
 *   paramter <code>has-read-permission</code> which the value
 *   <code>"yes"</code>. However, in most cases you will not need this parameter, but only the
 *   logic that is imposed by the action's {@link #act act} method in returning
 *   non-<code>null</code> or <code>null</code>.
 * </p>
 * <p>
 *   Example (sitemap excerpt):
 * </p>
 * <pre>
 *   &lt;map:actions&gt;
 *     &lt;!-- ... --&gt;
 *     &lt;map:action name="check-read-permission"
 *                 src="net.mumie.cocoon.actions.CheckReadPermissionAction"/&gt;
 *   &lt;/map:actions&gt;
 * 
 *     &lt;!-- ... --&gt;
 * 
 *   &lt;map:pipelines&gt;
 * 
 *     &lt;map:pipeline&gt;
 *       &lt;map:match pattern="url-for-document-42-of-type-5"&gt;
 *         &lt;map:act type="check-read-permission"&gt;
 *           &lt;map:parameter name="document-type" value="5"/&gt;
 *           &lt;map:parameter name="id" value="42"/&gt;
 *           &lt;!-- code 1 --&gt;
 *         &lt;/map:act&gt;
 *         &lt;!-- code 2 --&gt;
 *       &lt;/map:match&gt;
 *     &lt;/map:pipeline&gt;
 * 
 *   &lt;/map:pipelines&gt;
 * </pre>
 * <p>
 *   If the user has the permission to see the document with type <code>5</code> and id
 *   <code>42</code>, code 1 is executed; otherwise, code 2 is executed.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: CheckReadPermissionAction.java,v 1.13 2007/12/20 16:41:38 grudzin Exp $</code>
 */


public class CheckReadPermissionAction extends ServiceableAction
{
  /**
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
	// Reading parameters:
	String typeParameter = parameters.getParameter("type", null);
	String typeNameParameter = parameters.getParameter("type-name", null);
	String idParameter = parameters.getParameter("id");
	this.getLogger().debug
	  (METHOD_NAME + " 2/3:" +
           " typeParameter = " + typeParameter +
           ", typeNameParameter = " + typeNameParameter +
	   ", idParameter = " + idParameter);
	// Getting document type as numerical code:
	int type;
	if ( typeParameter != null )
	  {
	    type = Integer.parseInt(typeParameter);
	    if ( !DocType.exists(type) )
	      throw new ProcessingException("Unknown document type code: " + type);
	    if ( typeNameParameter != null )
	      this.getLogger().warn
                (METHOD_NAME + ": Superfluous parameter \"type-name\". Ignored");
	  }
	else if ( typeNameParameter != null )
	  {
	    type = DocType.codeFor(typeNameParameter);
	    if ( type == DocType.UNDEFINED )
	      throw new ProcessingException("Unknown document type: " + typeNameParameter);
	  }
	else
	  throw new ProcessingException("Missing \"type\" or \"type-name\" parameter");
	// Document id:
	int id = Integer.parseInt(idParameter);
	// Get user and check permission:
	user = (SessionUser)this.manager.lookup(SessionUser.ROLE);
	boolean hasReadPermission = user.hasReadPermission(type, id);
	this.getLogger().debug
          (METHOD_NAME + " 3/3: Done." +
           " user = " + LogUtil.identify(user) +
           ", hasReadPermission = " + hasReadPermission);
	if ( hasReadPermission )
	  {
	    Map sitemapParameters = new HashMap();
	    sitemapParameters.put("has-read-permission", "yes");
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
	if ( user != null )
          this.manager.release(user);
      }
  }
}
