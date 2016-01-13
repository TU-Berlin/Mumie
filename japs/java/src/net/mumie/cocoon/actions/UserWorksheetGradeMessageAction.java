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
import net.mumie.cocoon.grade.UserWorksheetGradeMessage;
import net.mumie.cocoon.util.ParamUtil;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;

/**
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: UserWorksheetGradeMessageAction.java,v 1.2 2008/10/22 10:10:00 rassy Exp $</code>
 */

public class UserWorksheetGradeMessageAction extends ServiceableAction
{
  public Map act (Redirector redirector,
                  SourceResolver sourceResolver,
                  Map objectModel, String source, 
                  Parameters parameters)
    throws ProcessingException 
  {
    final String METHOD_NAME = "act";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");
    UserWorksheetGradeMessage message = null;

    try
      {
        // Get data:
        int userId = ParamUtil.getAsId(parameters, "user");
        int worksheetId = ParamUtil.getAsId(parameters, "worksheet");
        String destinationName = ParamUtil.getAsString(parameters, "destination");

        // Setup message object:
        message =
          (UserWorksheetGradeMessage)this.manager.lookup(UserWorksheetGradeMessage.ROLE);
        message.setup(userId, worksheetId, destinationName);

        // Send message:
        boolean success = message.send();

        // Compose sitemap parameters:
	Map sitemapParams = new HashMap();
	sitemapParams.put("message-status", (success ? "Ok" : "Failed"));

        this.getLogger().debug(METHOD_NAME + " 2/2: Done. success = " + success);

	return sitemapParams;
      }
    catch (Exception exception)
      {
	throw new ProcessingException(exception);
      }
    finally
      {
	if ( message != null ) this.manager.release(message);
      }
  }
}
