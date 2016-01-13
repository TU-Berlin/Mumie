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

package net.mumie.cocoon.service;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceException;
import net.mumie.cocoon.util.LogUtil;
import net.mumie.cocoon.service.LookupNotifyable;
import net.mumie.cocoon.util.Identifyable;
import org.apache.avalon.framework.logger.Logger;

/**
 * <p>
 *   A special service manager for Japs services. Respects the {@link LookupNotifyable}
 *   interface. Wraps a "real" service manager.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: ServiceManagerWrapper.java,v 1.4 2007/07/11 15:38:49 grudzin Exp $</code>
 */

public class ServiceManagerWrapper extends AbstractLogEnabled
  implements ServiceManager
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The wrapped service manager.
   */

  protected ServiceManager serviceManager = null;

  /**
   * The identification string of the owner object.
   */

  protected String ownerLabel = null;

  // --------------------------------------------------------------------------------
  // Constructor
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>ServiceManagerWrapper</code>.
   */

  public ServiceManagerWrapper (ServiceManager serviceManager,
                                String ownerLabel,
                                Logger logger)
  {
    this.serviceManager = serviceManager;
    this.ownerLabel = ownerLabel;
    this.enableLogging(logger);
  }

  // --------------------------------------------------------------------------------
  // Logging methods
  // --------------------------------------------------------------------------------

  /**
   * Writes a debug log message. The message is prepended by
   * <code>"ServiceManagerWrapper (owner = " + this.ownerLabel + "): "</code>.
   */

  protected void logDebug (String message)
  {
    this.getLogger().debug
      ("ServiceManagerWrapper (owner = " + this.ownerLabel + "): " + message);
  }

  // --------------------------------------------------------------------------------
  // ServiceManager methods
  // --------------------------------------------------------------------------------

  /**
   * Returns an instance of the service with the specified key. The instance is obtained by
   * calling the respective method of the wrapped service manager. If the service class
   * implements the {@link LookupNotifyable  LookupNotifyable} interface, the instance is
   * notified about the lookup call.
   */

  public Object lookup (String key)
    throws ServiceException 
  {
    final String METHOD_NAME = "lookup";
    this.logDebug(METHOD_NAME + " 1/2: Started. key = " + key);
    Object service = this.serviceManager.lookup(key);
    if ( service instanceof LookupNotifyable )
      ((LookupNotifyable)service).notifyLookup(ownerLabel);
    this.logDebug(METHOD_NAME + " 2/2: Done. service = " + getLabelFor(service));
    return service;
  }

  /**
   * Releases the specified service instance. This is done by calling the respective method
   * of the wrapped service manager. If the service class implements the
   * {@link LookupNotifyable  LookupNotifyable} interface, the instance is 
   * notified about the release call.
   */

  public void release (Object service)
  {
    final String METHOD_NAME = "release";
    this.logDebug(METHOD_NAME + " 1/2: Started. service = " + getLabelFor(service));
    this.serviceManager.release(service);
    if ( service instanceof LookupNotifyable )
      ((LookupNotifyable)service).notifyRelease(ownerLabel);
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Calls the respective method of the wrapped service manager.
   */

  public boolean hasService (String key)
  {
    return this.serviceManager.hasService(key);
  }

  // --------------------------------------------------------------------------------
  // Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * 
   */

  protected static String getLabelFor (Object service)
  {
    return
      (service == null
       ? "null"
       : (service instanceof Identifyable
          ? ((Identifyable)service).getIdentification()
          : service.toString()));
  }
}
