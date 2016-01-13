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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.DocType;
import org.apache.avalon.framework.logger.Logger;
import net.mumie.cocoon.util.Identifyable;

/**
 * <p>
 *   A class loader that loads classes from the Japs database.
 * </p>
 * <p>
 *   A <code>DbClassLoader</code> co-operates with another class loader, the so-called
 *   <em>primary class loader</em>, which is responsible for loading classes not stored in
 *   the database.
 * </p>
 * <p>
 *   The central method is
 *   {@link #loadClass(String,boolean) loadClass(String className, boolean resolve)}.
 *   It returns a class for a given fully qualified name. The method works as follows:
 * </p>
 * <ul>
 *   <li> First, the primary class loader is asked to load the class. If it succedes, the
 *     found class is returned.</li>
 *   <li> If the primary class loader failes, the method checks if this class loader has
 *     already loaded the class, and if the loaded class is still up-to-date. If both
 *     conditions hold true, the already loaded class is returned.</li>
 *   <li> Otherwise, the class is loaded from the database.
 * </ul>
 * <p>
 *   The up-to-date check is done by comparing the id of the already loaded class with the
 *   id of the class in the database. The class is up-to-date if and only if the both id's
 *   coincide.
 * </p>
 * <p>
 *   The <code>DbClassLoader</code> can not be used as an Avalon service directly, because
 *   it can not be made into an interface. But there exists an interface that works as a
 *   <code>DbClassLoader</code> wrapper and is suitable as an Avalon service:
 *   {@link DbClassLoaderWrapper}. Typical code using a <code>DbClassLoader</code> would
 *   thus be like the following (<code>manager</code> is a 
 *   {@link org.apache.avalon.framework.service.ServiceManager ServiceManager}):
 * </p>
 * <pre>
 *    DbClassLoaderWrapper dbClassLoaderWrapper = null;
 *    try
 *      {
 *        dbClassLoaderWrapper =
 *          (DbClassLoaderWrapper)this.manager.lookup(DbClassLoaderWrapper.ROLE);
 *        DbClassLoader dbClassLoader = dbClassLoaderWrapper.getDbClassLoader();
 * 
 *        // Use dbClassLoader here
 * 
 *      }
 *    finally
 *      {
 *        if ( dbClassLoaderWrapper != null )
 *          this.manager.release(dbClassLoaderWrapper);
 *      }
 * </pre>
 * <p>
 *   Besides the primary class loader, a <code>DbClassLoader</code> needs a {@link DbHelper}
 *   and a {@link Logger}. All the three objects are passed to the constructor of the
 *   <code>DbClassLoader</code>. Since the latter is usually called from the
 *   {@link DbClassLoaderWrapper}, the wrapper is responsible for providing the
 *   <code>DbClassLoader</code> with the three objects.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: DbClassLoader.java,v 1.4 2007/07/11 15:38:42 grudzin Exp $</code>
 */

public class DbClassLoader extends ClassLoader
  implements Identifyable 
{
  // --------------------------------------------------------------------------------
  // Global constants and variables
  // --------------------------------------------------------------------------------

  /**
   * Counts the instances created so far.
   */

  private static long numberOfCreatedInstances = 0;

  /**
   * Counts the currently "enabled" instances. This are instances for which
   * {@link #shutdown shutdown} has not been called yet.
   */

  private static long numberOfEnabledInstances = 0;

  /**
   * Instance id of this object.
   */

  private final long instanceId;

  /**
   * The db helper of this class loader.
   */

  protected DbHelper dbHelper = null;

  /**
   * The logger of this class loader.
   */

  protected Logger logger = null;

  /**
   * The primary class loader. 
   */

  protected ClassLoader primaryClassLoader = null;

  /**
   * Maps loaded classes (specified by their fully qualified names) to their id's.
   */

  protected Map loadedClassIds = new HashMap();

  // --------------------------------------------------------------------------------
  // Logging methods
  // --------------------------------------------------------------------------------

  /**
   * Writes a debug log message. The message is prepended by
   * <code>this.getIdentification() + ": "</code>.
   */

  protected void logDebug (String message)
  {
    this.logger.debug(this.getIdentification() + ": " + message);
  }

  // --------------------------------------------------------------------------------
  // Lifecycle methods
  // --------------------------------------------------------------------------------

  /**
   * Returns the number of instances.
   */

  public static long getNumberOfCreatedInstances ()
  {
    return numberOfCreatedInstances;
  }

  /**
   * <p>
   *  Increases the number-of-created-instances counter and the number-of-enabled-instances
   *  counter by 1 and returns the new value of the former one. This method is called in the
   *  constructor, and the returned value becomes the instance id.
   * </p>
   * <p>
   *   The counters are implemented as a static variables of type <code>long</code>. If the
   *   old counter value is {@link Long#MAX_VALUE Long.MAX_VALUE}, the counter is reset to
   *   -1 before being increased.
   * </p>
   */

  protected static synchronized long notifyInstanceCreation ()
  {
    if ( numberOfCreatedInstances == Integer.MAX_VALUE )
      numberOfCreatedInstances = -1;
    numberOfCreatedInstances++;
    if ( numberOfEnabledInstances == Integer.MAX_VALUE )
      numberOfEnabledInstances = -1;
    numberOfEnabledInstances++;
    return numberOfCreatedInstances;
  }

  /**
   * <p>
   *   Decreases the number-of-enabled-instances counter by 1. This method is called in the
   *   {@link #shutdown shutdown} method.
   * </p>
   */

  protected static synchronized void notifyShutdown ()
  {
    numberOfEnabledInstances--;
  }

  /**
   * Creates a new <code>DbClassLoader</code> and supplies it with the specified db helper,
   * primary class loader and logger. If <code>primaryClassLoader</code> is
   * <code>null</code>, the primary class loader is set to the parent class loader of this
   * instance. 
   */

  DbClassLoader (DbHelper dbHelper,
                 ClassLoader primaryClassLoader,
                 Logger logger)
  {
    this.instanceId = notifyInstanceCreation();
    this.dbHelper = dbHelper;
    this.logger = logger;
    this.primaryClassLoader =
      (primaryClassLoader == null
       ? this.getParent()
       : primaryClassLoader);
  }

  /**
   * Sets the db helper, primary class loader, and logger to <code>null</code>. After a
   * call to this method, the db class loader con not be used any longer.
   */

  void cleanUp ()
  {
    this.dbHelper = null;
    this.logger = null;
    this.primaryClassLoader = null;
    notifyShutdown();
  }

  // --------------------------------------------------------------------------------
  // Access to db helper and primary class loader
  // --------------------------------------------------------------------------------

  /**
   * Returns the db helper of this instance.
   */

  DbHelper getDbHelper ()
  {
    return this.dbHelper;
  }

  /**
   * Returns the primary class loader of this instance.
   */

  ClassLoader getPrimaryClassLoader ()
  {
    return this.primaryClassLoader;
  }

  // --------------------------------------------------------------------------------
  // Class loading methods
  // --------------------------------------------------------------------------------

  /**
   * Returns the id of the newest version of the class with qualified name
   * <code>className</code>.
   */

  protected int getClassId (String className)
    throws SQLException 
  {
    final String METHOD_NAME = "getClassId";
    ResultSet resultSet = this.dbHelper.queryDatumByQualifiedName
      (DocType.JAVA_CLASS, className, DbColumn.ID, true);
    if ( !resultSet.next() )
      throw new SQLException
        (this.getIdentification() + ": " + METHOD_NAME +
         ": Class not found in database (result set empty): "+ className);
    int id = resultSet.getInt(DbColumn.ID);
    return id;
  }

  /**
   * Reads the content of the {@link DocType#JAVA_CLASS JAVA_CLASS} document with the given
   * id from the database and returns it.
   * @param id the id of the {@link DocType#JAVA_CLASS JAVA_CLASS} document.
   */

  protected byte[] getClassContent (int id)
    throws SQLException, IOException
  {
    final String METHOD_NAME = "getClassContent";
    this.logDebug(METHOD_NAME + " 1/2: Started. id = " + id);
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    this.dbHelper.queryAndWriteBlobDatum
      (DocType.JAVA_CLASS, id, DbColumn.CONTENT, stream);
    byte[] classContent = stream.toByteArray();
    this.logDebug
      (METHOD_NAME + " 2/2: Done. Read " + classContent.length + " bytes");
    return classContent;
  }

  /**
   * <p>
   *   Loads a class.
   * </p>
   * <p>
   *   First, the primary class loader is asked to load the class. If that fails, the class
   *   is loaded from the database. If the class has been loaded before (from the database)
   *   and is still up-to-date, the loaded class is returned, Otherwise, the class is read
   *   from the database.
   * </p>
   * <p>
   *   If <code>resolve</code> is <code>true</code>, the class will be resolved.
   * </p>
   */

  protected synchronized Class loadClass (String className, boolean resolve)
    throws ClassNotFoundException
  {
    final String METHOD_NAME = "loadClass(String className, boolean resolve)";
    String classNameLog = "className = " + className;
    this.logDebug
      (METHOD_NAME + " 1/4: " + classNameLog + ", resolve = " + resolve);
    try
      {
        Class clazz = null;

        // Let the primary class loader try to load the class:
        if ( this.primaryClassLoader != null )
          {
            try
              {
                this.logDebug
                  (METHOD_NAME + " 2/4: " + classNameLog +
                   ". Let primary classloader try to load class");
                clazz = this.primaryClassLoader.loadClass(className);
                this.logDebug
                  (METHOD_NAME + " 3/4: " + classNameLog +
                   ". Loaded by primary classloader: clazz = " + clazz);
              }
            catch (ClassNotFoundException exception)
              {
                this.logDebug
                  (METHOD_NAME + " 3/4: " + classNameLog +
                   ". Primary classloader failed to load class (" + exception + ")");
                clazz = null;
              }
          }

        if ( clazz == null ) // Primary class loader failed to load class
          {
            this.logDebug
              (METHOD_NAME + " 3.1/4: " + classNameLog +
               ". Trying to load class from database");

            // Check if the class is already loaded:
            clazz = this.findLoadedClass(className);

            if ( clazz != null ) // Class has already been loaded
              {
                // Get id of loaded class:
                Integer loadedClassId = (Integer)this.loadedClassIds.get(className);
                if ( loadedClassId == null )
                  throw new IllegalStateException
                    ("No id for already loaded class: " + className);
                this.logDebug
                  (METHOD_NAME + " 3.2/4: " + classNameLog +
                   ". Already loaded. loadedClassId = " + loadedClassId);
              }
            else // Class not loaded yet
              {
                this.logDebug
                  (METHOD_NAME + " 3.2/4: " + classNameLog + ". Class not loaded yet");

                // Get the id of the class to load:
                int id = this.getClassId(className);
                this.logDebug
                  (METHOD_NAME + " 3.3/4: " + classNameLog + ", id = " + id);

                // Load class from database:
                byte[] classContent = this.getClassContent(id);
                clazz = this.defineClass(className, classContent, 0, classContent.length);
                this.loadedClassIds.put(className, new Integer(id));
              }
          }

        if ( resolve ) this.resolveClass(clazz);

        this.logDebug
          (METHOD_NAME + " 4/4: " + classNameLog + ". Done. clazz = " + clazz);
        return clazz;
      }
    catch (Throwable throwable)
      {
	throw new ClassNotFoundException
          ("DbClassLoader: " + METHOD_NAME + ": " +
           "Failed to read class from database: " + className +
           ". Caught throwable: " + throwable, throwable);
      }
  }

  /**
   * Same as {@link #loadClass(String,boolean) loadClass(className, false)}.
   */

  public Class loadClass (String className)
    throws ClassNotFoundException
  {
    return this.loadClass(className, false);
  }

  /**
   * Returns a copy of {@link #loadedClassIds}.
   */

  public synchronized Map getloadedClassIdsCopy ()
  {
    Map loadedClassIdsCopy = new HashMap();
    Iterator iterator = this.loadedClassIds.entrySet().iterator();
    while ( iterator.hasNext() )
      {
        Map.Entry entry = (Map.Entry)iterator.next();
        loadedClassIdsCopy.put((String)entry.getKey(), (Integer)entry.getValue());
      }
    return loadedClassIdsCopy;
  }

  // --------------------------------------------------------------------------------
  // Identification method
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Returns a string that identifies this <code>DbClassLoader</code>. Has the form
   *   <pre>  "DbClassLoader#" + <var>instanceId</var></pre>
   *   where <var>instanceId</var> is the instance id.
   * </p>
   */

  public String getIdentification ()
  {
    return
      "DbClassLoader" +
      '#' + this.instanceId;
  }
}
