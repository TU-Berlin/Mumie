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
import net.mumie.cocoon.sync.SyncCommand;
import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.avalon.framework.parameters.Parameters;

/**
 * <p>
 *   Executes a synchronization command. Recognizes the following parameters: 
 * </p>
 * <table class="genuine indented" style="width:40em">
 *   <thead>
 *     <tr>
 *       <td>Name</td>
 *       <td>Description</td>
 *       <td>Required</td>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td><code>command</code></td>
 *       <td>The name of the command.</td>
 *       <td>Yes</td>
 *     </tr>
 *   </tbody>
 * </table>
 * <p>
 *    The {@link #act act} method always returns a non-null <code>Map</code> with a single
 *    entry. The name of the entry is <code>"status"</code>, the value is <code>"OK"</code>
 *    if the command was successful and <code>"ERROR: <var>description</var>"</code> if it
 *    failed, where <var>description</var> is a string describing the cause of the failure.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: SyncAction.java,v 1.5 2007/07/11 15:38:40 grudzin Exp $</code>
 */

public class SyncAction extends ServiceableAction
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
    this.getLogger().debug(METHOD_NAME + " 1/3: Started");
    ServiceSelector commandSelector = null;
    SyncCommand command = null;
    String status = "OK";
    try
      {
        String commandName = parameters.getParameter("command");
        this.getLogger().debug(METHOD_NAME + " 2/3: commandName = " + commandName);
        commandSelector = (ServiceSelector)this.manager.lookup(SyncCommand.ROLE + "Selector");
        command = (SyncCommand)commandSelector.select(commandName);
        command.execute(ObjectModelHelper.getRequest(objectModel));
      }
    catch (Exception exception)
      {
        status = "ERROR: " + this.unwrappThrowable(exception).toString();
        this.getLogger().error("Caught exception", exception);
      }
    finally
      {
        if ( commandSelector != null )
          {
            if ( command != null )
              commandSelector.release(command);
            this.manager.release(commandSelector);
          }
      }
    Map params = new HashMap();
    params.put("status", status);
    this.getLogger().debug(METHOD_NAME + " 3/3: Done. status = " + status);
    return params;
  }

  /**
   * Returns the "root" of <code>throwable</code>. This is the Throwable that
   * initially caused the problem.
   */

  protected Throwable unwrappThrowable (Throwable throwable)
  {
    Throwable cause;
    while ( true )
      {
	cause = throwable.getCause();
	if ( cause == null ) break;
	throwable = cause;
      }
    return throwable;
  }
}
