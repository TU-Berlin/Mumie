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
import java.util.HashMap;
import java.util.Map;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.DbTable;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.util.GenericDocumentResolver;
import net.mumie.cocoon.util.ParamUtil;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;
import net.mumie.cocoon.notions.Theme;

/**
 * <p>
 *   This action determines the "real" counterpart of a generic document. It expects the
 *   following parameters: 
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
 *       <td><code>type-of-generic</code></td>
 *       <td>The type of the generic document as numerical code</td>
 *       <td rowspan="2">One of the both</td>
 *     </tr>
 *     <tr>
 *       <td><code>name-of-generic</code></td>
 *       <td>The type of the document as string name</td>
 *     </tr>
 *     <tr>
 *       <td><code>id-of-generic</code></td>
 *       <td>The id of the generic document</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr>
 *       <td><code>language</code></td>
 *       <td>The language (specified as id) for which the real document is requested.</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr>
 *       <td><code>theme</code></td>
 *       <td>The theme (specified as id) for which the real document is requested.</td>
 *       <td>No. Default is {@link Theme#DEFAULT Theme.DEFAULT}</td>
 *     </tr>
 *   </tbody>
 * </table>
 * <p>
 *   NOTE: Only one of the parameters <code>type-of-generic</code> and
 *   <code>name-of-generic</code> must be set. If both are set, <code>type-of-generic</code>
 *   takes precedence.
 * </p>
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
 *       <td><code>type-of-real</code></td>
 *       <td>The type of the real document as numerical code</td>
 *     </tr>
 *     <tr>
 *       <td><code>name-of-real</code></td>
 *       <td>The type of the document as string name</td>
 *     </tr>
 *     <tr>
 *       <td><code>id-of-real</code></td>
 *       <td>The id of the real document</td>
 *     </tr>
 *   </tbody>
 * </table>
 * <p>
 *   Example (sitemap excerpt):
 * </p>
 * <pre>
 *   &lt;map:actions&gt;
 *     &lt;!-- ... --&gt;
 *     &lt;map:action name="resolve-generic-document"
 *                 src="net.mumie.cocoon.actions.ResolveGenericDocumentAction"/&gt;
 *   &lt;/map:actions&gt;
 *
 *   &lt;!-- ... --&gt;
 *
 *   &lt;map:pipelines&gt;
 *
 *     &lt;map:pipeline&gt;
 *       &lt;map:match pattern="document/generic_&#42;/id/&#42;"&gt;
 *         &lt;map:act type="resolve-generic-document"&gt;
 *           &lt;map:parameter name="name-of-generic" value="generic_{1}"/&gt;
 *           &lt;map:parameter name="id-of-generic" value="{2}"/&gt;
 *           &lt;map:parameter name="language" value="{session-attr:language}"/&gt;
 *           &lt;map:parameter name="theme" value="{session-attr:theme}"/&gt;
 *           &lt;map:redirect-to uri="document/{name-of-real}/id/{id-of-real}"/&gt;
 *         &lt;/map:act&gt;
 *       &lt;/map:match&gt;
 *     &lt;/map:pipeline&gt;
 *
 *   &lt;/map:pipelines&gt;
 * </pre>
 * <p>
 *   Consider a request with the URL
 *   <span class="string">"$PREFIX/document/generic_image/id/194"</span>, where <span
 *   class="string">"$PREFIX"</span> stands for the Cocoon root URL. The action will look up
 *   the "real" image for the generic image with id 194 in the theme with the id 0. Then,
 *   the request will be redirected to an URL pointing to the real image.
 * </p>
 * <p>
 *   For document types, see {@link net.mumie.cocoon.notions.DocType DocType}.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: ResolveGenericDocumentAction.java,v 1.22 2009/06/18 13:15:45 rassy Exp $</code>
 */

public class ResolveGenericDocumentAction extends ServiceableAction
{
  public Map act (Redirector redirector,
                  SourceResolver sourceResolver,
                  Map objectModel, String source, 
                  Parameters parameters)
    throws ProcessingException 
  {
    final String METHOD_NAME = "act";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");
    GenericDocumentResolver genDocResolver = null;
    try
      {
        // Init genDocResolver:
        genDocResolver =
          (GenericDocumentResolver)this.manager.lookup(GenericDocumentResolver.ROLE);

        // Get input data:
        int typeOfGeneric =
          ParamUtil.getAsDocType(parameters, "type-of-generic", "name-of-generic");
        int idOfGeneric = ParamUtil.getAsId(parameters, "id-of-generic");
        int languageId = ParamUtil.getAsId(parameters, "language");
        int themeId = ParamUtil.getAsInt(parameters, "theme", Theme.DEFAULT);

        // Get id of real document:
        int idOfReal =
          genDocResolver.resolve(typeOfGeneric, idOfGeneric, languageId, themeId);

        // Get type of real document, as numerical code and as string name:
	int typeOfReal = DocType.realOf[typeOfGeneric];
	String nameOfReal = DocType.nameFor[typeOfReal];

        // Compose sitemap parameters:
	Map sitemapParams = new HashMap();
	sitemapParams.put("type-of-real", Integer.toString(typeOfReal));
	sitemapParams.put("name-of-real", nameOfReal);
	sitemapParams.put("id-of-real", Integer.toString(idOfReal));

        this.getLogger().debug
          (METHOD_NAME + " 2/2: Done." +
           " typeOfReal = " + typeOfReal +
           ", nameOfReal = " + nameOfReal +
           ", idOfReal = " + idOfReal);

	return sitemapParams;
      }
    catch (Exception exception)
      {
	throw new ProcessingException(exception);
      }
    finally
      {
	if ( genDocResolver != null ) this.manager.release(genDocResolver);
      }
  }
}
