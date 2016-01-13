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

package net.mumie.cocoon.generators;

import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.UseMode;
import net.mumie.cocoon.search.UserSearcher;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.util.ParamUtil;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;

public class SearchUsersGenerator extends ServiceableJapsGenerator 
{
  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(SearchUsersGenerator.class);

  /**
   * Creates a new <code>SearchUsersGenerator</code> instance.
   */

  public SearchUsersGenerator ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Recycles this instance. Essentially, this method only calls the superclass recycle
   * method. Besides this, the instance status id notified about the recycle, and log
   * messages are written.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    super.recycle();
    this.instanceStatus.notifyRecycle();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Disposes this instance. Essentially, this method only calls the superclass dispose
   * method. Besides this, the instance status id notified about the dispose, and log
   * messages are written.
   */

  public void dispose ()
  {
    final String METHOD_NAME = "dispose";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    super.dispose();
    this.instanceStatus.notifyDispose();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Generates the XML. See class documentation for details.
   */

  public void generate ()
    throws ProcessingException 
  {
    final String METHOD_NAME = "generate";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");

    UserSearcher searcher = null;

    try
      {
        // Add request parameters to generator parameters:
        Request request = ObjectModelHelper.getRequest(objectModel);
        ParamUtil.addRequestParams(this.parameters, request, true);

        // Get search request data:
        int id = ParamUtil.getAsInt(this.parameters, "id", Id.UNDEFINED);
        String loginName = ParamUtil.getAsString(this.parameters, "login-name", null);
        String firstName = ParamUtil.getAsString(this.parameters, "first-name", null);
        String surname = ParamUtil.getAsString(this.parameters, "surname", null);

        // The form stage:
        int formStage = ParamUtil.getAsInt(parameters, "form-stage", 0);

        // Get use mode and "with-path" flag:
        int useMode = ParamUtil.getAsUseMode
          (this.parameters, "use-mode", "use-mode-name", UseMode.COMPONENT);
        boolean withPath = ParamUtil.getAsBoolean(this.parameters, "with-path", false);

        searcher = (UserSearcher)this.serviceManager.lookup(UserSearcher.ROLE);
        searcher.search
          (id, loginName, firstName, surname,
           (formStage == 0), useMode, withPath, this.contentHandler, true);

        this.getLogger().debug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
        throw new ProcessingException(exception);
      }
    finally
      {
        if ( searcher != null ) this.serviceManager.release(searcher);
      }
  }
}