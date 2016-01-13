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
import java.util.HashMap;
import java.util.Map;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.util.ParamUtil;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;

/**
 * <p>
 *   Determines the id of the latest version of a document for a vc thread. Recognizes
 *   the following parameters: 
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
 *       <td><code>type</code></td>
 *       <td>The type of the document as numerical code</td>
 *       <td rowspan="2">One of the both</td>
 *     </tr>
 *     <tr>
 *       <td><code>tyep-name</code></td>
 *       <td>The type of the document as a name</td>
 *     </tr>
 *     <tr>
 *       <td><code>vc-thread</code></td>
 *       <td>The id of the vc thread</td>
 *       <td>Yes</td>
 *     </tr>
 *   </tbody>
 * </table>
 * <p>
 *   NOTE: Only one of the parameters <code>type</code> and <code>type-name</code> must be
 *   set. If both are set, <code>type</code> takes precedence.
 * </p>
 * <p>
 *   Sets the following sitemap parameter:
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
 *       <td><code>id</code></td>
 *       <td>The id of the document with the latest version in the vc thread.</td>
 *     </tr>
 *   </tbody>
 * </table>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: ResolveVCThreadAction.java,v 1.1 2007/12/13 13:28:01 rassy Exp $</code>
 */

public class ResolveVCThreadAction extends ServiceableAction
{
  public Map act (Redirector redirector,
                  SourceResolver sourceResolver,
                  Map objectModel, String source, 
                  Parameters parameters)
    throws ProcessingException 
  {
    final String METHOD_NAME = "act";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");
    DbHelper dbHelper = null;
    try
      {
        // Init db helpder:
        dbHelper = (DbHelper)this.manager.lookup(DbHelper.ROLE);

        // Get input data:
        int type = ParamUtil.getAsDocType(parameters, "type", "type-name");
        int vcThreadId = ParamUtil.getAsId(parameters, "vc-thread");

        // Get document id:
        ResultSet resultSet = dbHelper.queryDocumentDataForVCThread
          (type, vcThreadId, new String[] {DbColumn.ID}, true);
        if ( !resultSet.next() )
          throw new SQLException
            ("Can not find document for type " + type + " and vc thread " + vcThreadId);
        int id = resultSet.getInt(DbColumn.ID);

        // Compose sitemap parameters:
	Map sitemapParams = new HashMap();
	sitemapParams.put("id", Integer.toString(id));

        this.getLogger().debug(METHOD_NAME + " 2/2: Done. id = " + id);

	return sitemapParams;
      }
    catch (Exception exception)
      {
	throw new ProcessingException(exception);
      }
    finally
      {
	if ( dbHelper != null ) this.manager.release(dbHelper);
      }
  }
}
