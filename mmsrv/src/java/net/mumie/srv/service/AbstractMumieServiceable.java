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

package net.mumie.srv.service;

import net.mumie.srv.event.Event;
import net.mumie.srv.event.EventHandler;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.Serviceable;

public abstract class AbstractMumieServiceable extends AbstractMumieService
  implements Serviceable
{
  /**
   * The service manager of this instance.
   */

  protected ServiceManager serviceManager;

  /**
   * Sets the service of this instance.
   */

  public void service (ServiceManager serviceManager)
    throws ServiceException
  {
    final String METHOD_NAME = "service()";
    this.logDebug(METHOD_NAME + " 1/2: Started. serviceManager = " + serviceManager);
    String label = this.getShortClassName() + "#" + this.instanceStatus.getInstanceId();
    this.serviceManager = new ServiceManagerWrapper
      (serviceManager, label, this.getLogger().getChildLogger(label));
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Calls the event handler with the specified name for the specified event.
   */

  protected void callEventHandler (String handlerName, Event event)
  {
    final String METHOD_NAME = "callEventHandler";
    this.logDebug(METHOD_NAME + " 1/2: Started. handlerName = " + handlerName);
    ServiceSelector handlerSelector = null;
    EventHandler handler = null;
    try
      {
        handlerSelector =
          (ServiceSelector)this.serviceManager.lookup(EventHandler.ROLE + "Selector");
        handler = (EventHandler)handlerSelector.select(handlerName);
        handler.handle(event);
        this.logDebug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
        this.logError(METHOD_NAME + ": Calling handler failed", exception);
      }
    finally
      {
        if ( handlerSelector != null )
          {
            if ( handler != null )
              handlerSelector.release(handler);
            this.serviceManager.release(handlerSelector);
          }
      }
  }
}
