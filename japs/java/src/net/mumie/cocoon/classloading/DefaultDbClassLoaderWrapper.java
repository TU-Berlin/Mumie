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

package net.mumie.cocoon.classloading;

import net.mumie.cocoon.db.DbHelper;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.avalon.excalibur.pool.Recyclable;
import net.mumie.cocoon.util.Identifyable;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.util.LogUtil;
import org.apache.avalon.framework.activity.Disposable;
import net.mumie.cocoon.service.AbstractJapsServiceable;

/**
 * Default implementation of {@link DbClassLoaderWrapper}. Holds a {@link DbClassLoader}
 * which is instanciated in the {@link #service} medthod. The primary class loader of the
 * {@link DbClassLoader} instance is set to the class loader which loaded the class of this
 * object. The logger of the {@link DbClassLoader} instance is set to the logger of this
 * object. The {@link DbHelper} of the {@link DbClassLoader} instance is obtained from the
 * service manager of this object.
 */

public class DefaultDbClassLoaderWrapper extends AbstractJapsServiceable 
  implements DbClassLoaderWrapper, Recyclable, Disposable, Identifyable 
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus =
    new ServiceStatus(DefaultDbClassLoaderWrapper.class);

  /**
   * The wrapped {@link DbClassLoader}.
   */

  protected DbClassLoader dbClassLoader;

  // --------------------------------------------------------------------------------
  // Lifecycle related methods
  // --------------------------------------------------------------------------------

  /**
   * Creates a new instance.
   */

  public DefaultDbClassLoaderWrapper ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Releases the db helper hold by the wrapped db class loader, calls the db class loader's
   * {@link DbClassLoader#cleanUp cleanUp} method, and sets the db class
   * loader to <code>null</code>.
   */

  protected void discardDbClassLoader ()
  {
    final String METHOD_NAME = "discardDbClassLoader";
    this.logDebug
      (METHOD_NAME + " 1/2: Started." +
       " this.dbClassLoader = " + LogUtil.identify(this.dbClassLoader));
    DbHelper dbHelper = this.dbClassLoader.getDbHelper();
    if ( dbHelper != null )
      this.serviceManager.release(dbHelper);
    this.dbClassLoader.cleanUp();
    this.dbClassLoader = null;
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Recycles this instance.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.discardDbClassLoader();
    this.instanceStatus.notifyRecycle();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Disposes this instance.
   */

  public void dispose ()
  {
    final String METHOD_NAME = "dispose";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.discardDbClassLoader();
    this.instanceStatus.notifyDispose();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // Get wrapped db class loader
  // --------------------------------------------------------------------------------

  /**
   * Returns the wrapped {@link DbClassLoader}.
   */

  public DbClassLoader getDbClassLoader()
    throws DbClassLoaderException
  {
    final String METHOD_NAME = "getDbClassLoader";
    try
      {
        if ( this.dbClassLoader == null )
          {
            this.logDebug(METHOD_NAME + "1/2: Creating new DbClassLoader");
            DbHelper dbHelper = (DbHelper)serviceManager.lookup(DbHelper.ROLE);
            ClassLoader primaryClassLoader = this.getClass().getClassLoader();
            this.dbClassLoader = new DbClassLoader(dbHelper, primaryClassLoader, this.getLogger());
            this.logDebug
              (METHOD_NAME + " 2/2:" +
               " this.dbClassLoader = " + this.dbClassLoader +
               ", dbHelper = " + LogUtil.identify(dbHelper) +
               ", primaryClassLoader = " + primaryClassLoader.toString());
          }
        else
          {
            this.logDebug
              (METHOD_NAME + "1/1:" +
               " Returning existing db class loader." +
               " this.dbClassLoader = " + this.dbClassLoader);
          }
        return this.dbClassLoader;
      }
    catch (Exception exception)
      {
        throw new DbClassLoaderException
          (this.getIdentification() + ": " + METHOD_NAME +
           ": Caught exception", exception);
      }
  }

  // --------------------------------------------------------------------------------
  // Identification method
  // --------------------------------------------------------------------------------
  
  /**
   * Returns a string that identifies this instance. It has the
   * following form:<pre>
   *   "DefaultSessionUser" +
   *   '#' + instanceId
   *   '(' + lifecycleStatus
   *   ')'</pre>
   * where <code>instanceId</code> is the instance id, <code>lifecycleStatus</code> the
   * lifecycle status, and <code>numberOfRecycles</code> the number of recycles of this
   * instance.
   */

  public String getIdentification ()
  {
    return
      "DefaultDbClassLoaderWrapper" +
      '#' + this.instanceStatus.getInstanceId() +
      '(' + this.instanceStatus.getLifecycleStatus() +
      ',' + this.instanceStatus.getNumberOfRecycles() +
      ')';
  }
}
