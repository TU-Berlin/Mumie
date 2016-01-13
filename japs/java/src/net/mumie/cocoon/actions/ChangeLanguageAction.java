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
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.webapps.session.SessionManager;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.pseudodocs.SessionUser;
import net.mumie.cocoon.pseudodocs.User;
import net.mumie.cocoon.util.ParamUtil;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.SessionAttrib;
import java.util.HashMap;

/**
 * @author Marek Grudzinski <a href="mailto:grudzin@math.tu-berlin.de">grudzin@math.tu-berlin.de</a>
 * @version <code>$Id: ChangeLanguageAction.java,v 1.5 2009/01/28 13:16:54 rassy Exp $</code>
 */

public class ChangeLanguageAction extends ServiceableAction
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
    User user = null;
    SessionManager sessionManager = null;

    try
      {  
        // Init services:
        dbHelper = (DbHelper)this.manager.lookup(DbHelper.ROLE);
        user = (User)this.manager.lookup(SessionUser.ROLE);
        

        // User id:
        int userId = user.getId();

        // Get input data from parameters:
        int oldLanguageId = ParamUtil.getAsInt(parameters, "old-language");
        int newLanguageId = ParamUtil.getAsInt(parameters, "new-language");        

        String status = null;
        
        // Check if current language is equal to selected language:
        if ( oldLanguageId == newLanguageId )
          status = "already-set";
                
        else
          {
            //Set new password:
            dbHelper.updatePseudoDocDatum
              (PseudoDocType.USER, userId, DbColumn.LANGUAGE, newLanguageId);
            
            //Update session attribute "language"
            sessionManager = (SessionManager)this.manager.lookup(SessionManager.ROLE);
            Session session = sessionManager.getSession(true);
            session.setAttribute(SessionAttrib.LANGUAGE, newLanguageId);

            status = "language-changed";
          }

        Map result = new HashMap();
        result.put("status", status);

        this.getLogger().debug
          (METHOD_NAME + " 2/2: Done." +
           " userId = " + userId + ", status = " + status);

        return result;
      }
    catch (Exception exception)
      {
  throw new ProcessingException(exception);
      }
    finally
      {
        if ( user != null )
          this.manager.release(user);

        if ( dbHelper != null )
          this.manager.release(dbHelper);
        
        if ( sessionManager != null )
          this.manager.release(sessionManager);
      }
  }
}
