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

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;

/**
 * <p>
 *   A service that recieves notification of lookup, selection and release. This works only
 *   if the {@link ServiceManagerWrapper ServiceManagerWrapper} or
 *   {@link ServiceSelectorWrapper ServiceSelectorWrapper} is used as service manager
 *   resp. selector.
 * </p>
 * <p>
 *   <em>Lookup</em> means a {@link ServiceManager#lookup lookup} call that returns this
 *   instance. 
 *   <em>Select</em> means a {@link ServiceSelector#select select} call that returns this
 *   instance. 
 *   <em>Release</em> means a <code>release</code> call, either of a service
 *   {@link ServiceManager manager} or {@link ServiceSelector selector}, with this instance
 *   passed as argument.
 * </p>
 * <p>
 *   Notification is done by calling special notification methods declared in this
 *   interface. There are two such methods: {@link #notifyLookup notifyLookup} and
 *   {@link #notifyRelease notifyRelease}. The latter is called on releases, the first
 *   on lookups <em>and</em> selects. Thus, this interface does not distinguish between
 *   lookups and selects.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: LookupNotifyable.java,v 1.1 2008/09/09 21:55:09 rassy Exp $</code>
 */

public interface LookupNotifyable
{
  /**
   * Notifies this object that is has been looked up. That means that the object has just
   * been returned by the
   * {@link ServiceManager#lookup lookup} method of a
   * {@link ServiceManager ServiceManager}.
   */

  public void notifyLookup (String ownerLabel);

  /**
   * Notifies this object that is has been released. That means that the
   * {@link ServiceManager#release release} method of a
   * {@link ServiceManager ServiceManager} has been
   * called with this object as argument.
   */

  public void notifyRelease (String ownerLabel);
}
