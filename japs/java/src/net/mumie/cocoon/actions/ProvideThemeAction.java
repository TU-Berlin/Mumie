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

import java.util.Map;
import java.util.HashMap;

import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.SessionAttrib;
import net.mumie.cocoon.util.ParamUtil;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.webapps.session.SessionManager;
import net.mumie.cocoon.notions.Theme;


/**
 * <p>
 *   Provides theme concerning a class as sitemap paramters. Works with session attribute class
 *   Default theme is 0
 *   
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
 *       <td><code>theme</code></td>
 *       <td>Id of the theme</td>
 *     </tr>
 *    </tbody>
 * </table>
 *
 * 
 * 
 */
public class ProvideThemeAction extends ServiceableAction
{

  public Map act(Redirector redirector, SourceResolver resolver,
      Map objectModel, String source, Parameters parameters)
      throws ProcessingException
  {
    
    // Some constants:
    final String METHOD_NAME = "act";
    
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");
    
    
    DbHelper dbHelper = null;
    SessionManager sessionManager = null;

    try
    {
      // Init db helper:
      dbHelper = (DbHelper) this.manager.lookup(DbHelper.ROLE);
      int theme = Theme.DEFAULT;
      
      //Init session
      sessionManager = (SessionManager)this.manager.lookup(SessionManager.ROLE);
      Session session = sessionManager.getSession(true);


      if(session.getAttribute(SessionAttrib.CLASS) != null) 
      {
        Integer classId = (Integer) session.getAttribute(SessionAttrib.CLASS);
        this.getLogger().debug(METHOD_NAME + "class = " + classId);
        theme = dbHelper.getPseudoDocDatumAsInt(PseudoDocType.CLASS,
             classId, DbColumn.THEME); 
      }
      
      Map sitemapParameters = new HashMap();
      sitemapParameters.put("theme", theme);
      this.getLogger().debug
      (METHOD_NAME + " 2/2: theme = " + theme);
      return sitemapParameters;
    }

    catch (Exception exception)
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
