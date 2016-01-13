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
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.Nature;
import net.mumie.cocoon.pseudodocs.SessionUser;
import net.mumie.cocoon.pseudodocs.User;
import net.mumie.cocoon.util.ParamUtil;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;

/**
 * <p>
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
 *       <td><code>nature</code></td>
 *       <td>The nature of the object. Must be either <code>"document"</code> or
 *         <code>"pseudo-document"</code>.</td>
 *       <td>No. Defaults to <code>"document"</code>.</td>
 *     </tr>
 *     <tr>
 *       <td><code>type</code></td>
 *       <td>The type of the (pseudo-)document (numerical code)</td>
 *       <td rowspan="2" style="vertical-align:middle">Only one of the both</td>
 *     </tr>
 *     <tr>
 *       <td><code>type-name</code></td>
 *       <td>The type of the (pseudo-)document (string name)</td>
 *     </tr>
 *     <tr>
 *       <td><code>id</code></td>
 *       <td>The id of the (pseudo-)document</td>
 *       <td>Yes</td>
 *     </tr>
 *   </tbody>
 * </table>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: ProvideSectionAction.java,v 1.1 2008/08/27 14:21:52 rassy Exp $</code>
 */

public class ProvideSectionAction extends ServiceableAction
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

    DbHelper dbHelper = null;

    try
      {
        // Get nature:
        int nature = Nature.UNDEFINED;
        String natureName = ParamUtil.getAsString(parameters, "nature", "document");
        if ( natureName.equals("document") )
          nature = Nature.DOCUMENT;
        else if ( natureName.equals("pseudo-document") )
          nature = Nature.PSEUDO_DOCUMENT;
        else
          throw new ParameterException
            ("Illegal value for parameter \"nature\": " + natureName +
             " (expected \"document\"  or \"pseudo-document\")");

	// Get type:
        int type =
          (nature == Nature.DOCUMENT
           ? ParamUtil.getAsDocType(parameters, "type", "type-name")
           : ParamUtil.getAsPseudoDocType(parameters, "type", "type-name"));

	// Get id:
        int id = ParamUtil.getAsId(parameters, "id");

        // Init db helper:
        dbHelper = (DbHelper)this.manager.lookup(DbHelper.ROLE);

        // Get section id
        int sectionId =
          (nature == Nature.DOCUMENT
           ? dbHelper.getDatumAsInt(type, id, DbColumn.CONTAINED_IN)
           : dbHelper.getPseudoDocDatumAsInt(type, id, DbColumn.CONTAINED_IN));

        Map map = new HashMap();
        map.put("section", sectionId);

        return map;
      }
    catch  (Exception exception)
      {
	throw new ProcessingException(exception);
      }
    finally
      {
        if ( dbHelper != null )
          this.manager.release(dbHelper);
      }
  }
  
}