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
import net.mumie.cocoon.notions.DocPath;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.util.ParamUtil;
import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;

/**
 * <p>
 *   Provides the id of the worksheet/generic-problem reference for a given real (!) problem and the
 *   default worksheet. Recognizes the following paramters:
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
 *       <td><code>problem-id</code></td>
 *       <td>The id of the real problem</td>
 *       <td>Yes</td>
 *     </tr>
 *   </tbody>
 * </table>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: ProvideDefaultWorksheetRefAction.java,v 1.1 2009/11/14 00:13:41 rassy Exp $</code>
 */

public class ProvideDefaultWorksheetRefAction extends ServiceableAction
  implements Poolable 
{

  /**
   * See class description.
   */

  public Map act (Redirector redirector, SourceResolver resolver, Map objectModel, String source, 
                  Parameters parameters)
    throws ProcessingException
  {
    final String METHOD_NAME = "act";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");
    DbHelper dbHelper = null;
    try
      {
        dbHelper = (DbHelper)this.manager.lookup(DbHelper.ROLE);
        int problemId = ParamUtil.getAsInt(parameters, "problem-id");
        int worksheetId = dbHelper.getIdForPath(DocType.WORKSHEET, DocPath.DEFAULT_WORKSHEET);
        int refId = dbHelper.getProblemRef(problemId, worksheetId);
        Map result = new HashMap();
        result.put("ref", Integer.toString(refId));
        this.getLogger().debug(METHOD_NAME + " 2/2: Done. refId = " + refId);
        return result;
      }
    catch  (Exception exception)
      {
        throw new ProcessingException(exception);
      }
    finally
      {
        if ( dbHelper != null ) this.manager.release(dbHelper);
      }
  }
}