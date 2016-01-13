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
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.ServiceException;
import net.mumie.cocoon.util.LogUtil;
import net.mumie.cocoon.service.LookupNotifyable;
import net.mumie.cocoon.util.Identifyable;
import org.apache.avalon.framework.logger.Logger;

public class ServiceSelectorWrapper extends AbstractLogEnabled
  implements ServiceSelector
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The wrapped service selector.
   */

  protected ServiceSelector serviceSelector = null;

  /**
   * The identification string of the owner object.
   */

  protected String ownerLabel = null;

  // --------------------------------------------------------------------------------
  // Constructor
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>ServiceSelectorWrapper</code>.
   */

  public ServiceSelectorWrapper (String ownerLabel,
                                 Logger logger)
  {
    this.ownerLabel = ownerLabel;
    this.enableLogging(logger);
  }

  // --------------------------------------------------------------------------------
  // The wrapped selector.
  // --------------------------------------------------------------------------------

  /**
   * Sets the wrapped selector.
   */

  public void wrap (ServiceSelector serviceSelector)
  {
    this.serviceSelector = serviceSelector;
  }

  /**
   * <p>
   *   Makes a copy of the reference to the wrapped service selector, sets the reference to
   *   the wrapped service selector to <code>null</code>, and returns the copy.
   * </p>
   * <p>
   *   In other words: After a call to this method, the service selector wrapper does no
   *   longer wrap a service selector; and the call returns the previously wrapped selector.
   * </p>
   */

  public ServiceSelector unwrapServiceSelector ()
  {
    ServiceSelector serviceSelector = this.serviceSelector;
    this.serviceSelector = null;
    return serviceSelector;
  }

  /**
   * Returns true if the wrapped service selector is not null, otherwise false.
   */

  public boolean hasServiceSelector ()
  {
    return ( this.serviceSelector != null );
  }

  // --------------------------------------------------------------------------------
  // Logging methods
  // --------------------------------------------------------------------------------

  /**
   * Writes a debug log message. The message is prepended by
   * <code>"ServiceSelectorWrapper (owner = " + this.ownerLabel + "): "</code>.
   */

  protected void logDebug (String message)
  {
    this.getLogger().debug
      ("ServiceSelectorWrapper (owner = " + this.ownerLabel + "): " + message);
  }

  // --------------------------------------------------------------------------------
  // ServiceSelector methods
  // --------------------------------------------------------------------------------

  /**
   * Returns an instance of the service with the specified policy. The instance is obtained
   * by calling the respective method of the wrapped service selector. If the service class
   * implements the {@link LookupNotifyable LookupNotifyable} interface, the instance is
   * notified about the select. Note that selects are handled identically to lookups; i.e.,
   * by a call to {@link LookupNotifyable#notifyLookup notifyLookup}.
   */

  public Object select (Object policy)
    throws ServiceException 
  {
    final String METHOD_NAME = "select";
    this.logDebug(METHOD_NAME + " 1/2: Started. policy = " + policy);
    Object service = this.serviceSelector.select(policy);
    if ( service instanceof LookupNotifyable )
      ((LookupNotifyable)service).notifyLookup(ownerLabel);
    this.logDebug(METHOD_NAME + " 2/2: Done. service = " + getLabelFor(service));
    return service;
  }

  /**
   * Releases the specified service instance. This is done by calling the respective method
   * of the wrapped service selector. If the service class implements the
   * {@link LookupNotifyable  LookupNotifyable} interface, the instance is 
   * notified about the release call.
   */

  public void release (Object service)
  {
    final String METHOD_NAME = "release";
    this.logDebug(METHOD_NAME + " 1/2: Started. service = " + getLabelFor(service));
    this.serviceSelector.release(service);
    if ( service instanceof LookupNotifyable )
      ((LookupNotifyable)service).notifyRelease(ownerLabel);
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Calls the respective method of the wrapped service selector.
   */

  public boolean isSelectable (Object policy)
  {
    return this.serviceSelector.isSelectable(policy);
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
