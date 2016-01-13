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

package net.mumie.cocoon.pseudodocs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.UseMode;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.util.LogUtil;
import net.mumie.cocoon.util.PasswordEncryptionException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.cocoon.ProcessingException;

public class DefaultGeneralUser extends AbstractUser
  implements GeneralUser
{
  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(DefaultGeneralUser.class);

  /**
   * All data keys.
   */

  protected final String[] dataKeys =
  {
    User.LOGIN_NAME_DATA_KEY,
    User.FIRST_NAME_DATA_KEY,
    User.SURNAME_DATA_KEY,
    User.EMAIL_DATA_KEY,
    User.PASSWORD_DATA_KEY,
    User.THEME_DATA_KEY,
    User.DESCRIPTION_DATA_KEY,
    User.CUSTOM_METAINFO_DATA_KEY,
  };

  /**
   * Maps data keys to db column names.
   */

  protected final Map keysVsDbColumns;

  /**
   * An internal buffer to store the data before written to the database.
   */

  protected Map data = new HashMap();

  /**
   * <p>
   *   Makes this instance ready to represent another user.
   * </p>
   * <p>
   *   Calls the superclass reset method and clears {@link #data}.
   * </p>
   */

  public void reset ()
  {
    this.data.clear();
    super.reset();
  }

  /**
   * Recycles this instance.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle()";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    super.recycle();
    this.instanceStatus.notifyRecycle();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Disposes this instance.
   */

  public void dispose ()
  {
    final String METHOD_NAME = "dispose()";
    this.logDebug(METHOD_NAME + "1/2: Started");
    super.dispose();
    this.instanceStatus.notifyDispose();
    this.logDebug(METHOD_NAME + "2/2: Done");
  }

  /**
   * Returns <code>true</code> if <code>key</code> is a valid data key, otherwise
   * <code>false</code>.
   */

  public boolean checkIfDataKey (String key)
  {
    boolean found = false;
    for (int i = 0; !found && i < this.dataKeys.length; i++)
      found = key.equals(this.dataKeys[i]);
    return found;
  }

  /**
   * Sets a piece of data in the data buffer.
   */

  public void setDatum (String key, String value)
    throws ServiceException
  {
    final String METHOD_NAME = "set(String key, String value)";
    this.logDebug
      (METHOD_NAME + "1/2:" +
       " key = " + key +
       ", value = " + (key.equals(PASSWORD_DATA_KEY) ? LogUtil.hide(value) : value));
    if ( !this.checkIfDataKey(key) )
      throw new IllegalArgumentException
        (LogUtil.identify(this) + ": Invalid data key: " + key);
    this.data.put(key, value);
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Returns a piece of data from the buffer.
   */

  public Object getDatum (String key)
    throws ServiceException
  {
    final String METHOD_NAME = "get(String key)";
    this.logDebug(METHOD_NAME + "1/2: key = " + key);
    if ( !this.checkIfDataKey(key) )
      throw new IllegalArgumentException
        (LogUtil.identify(this) + ": Invalid data key: " + key);
    Object value = this.data.get(key);
    this.logDebug(METHOD_NAME + " 2/2: value = " + value);
    return value;
  }

  /**
   * Returns a map containing the assignments of database columns to buffer values.
   */

  protected Map getDbColumnsVsValues ()
    throws ServiceException, PasswordEncryptionException 
  {
    Map dbColumnsVsValues = new HashMap ();
    Iterator iterator = this.data.entrySet().iterator();
    while ( iterator.hasNext() )
      {
        Map.Entry entry = (Map.Entry)iterator.next();
        String key = (String)entry.getKey();
        String value = (String)entry.getValue();
        if ( key.equals(PASSWORD_DATA_KEY) )
          {
            this.ensureEncryptor();
            value = this.encryptor.encrypt(value);
          }
        String column = (String)this.keysVsDbColumns.get(key);
        dbColumnsVsValues.put(column, value);
      }
    return dbColumnsVsValues;
  }

  /**
   * <p>
   *   Updates this user in the database.
   * </p>
   */

  public void update ()
    throws ProcessingException
  {
    try
      {
        final String METHOD_NAME = "update()";
        this.logDebug(METHOD_NAME + " 1/2: Started");
        if ( this.isExternallyControlled() )
          throw new ProcessingException
            (LogUtil.identify(this) + ": User is externally controlled");
        this.ensureDbHelper();
        this.dbHelper.updateUserData(this.getId(), this.getDbColumnsVsValues());
        this.logDebug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
        throw new ProcessingException(exception);
      }
  }

  /**
   * <p>
   *   Creates this user in the database.
   * </p>
   */

  public void create ()
    throws ProcessingException
  {
    try
      {
        final String METHOD_NAME = "create()";
        this.logDebug(METHOD_NAME + " 1/2: Started");
        this.ensureDbHelper();
        this.id = this.dbHelper.storeUserData(this.getDbColumnsVsValues());
        this.logDebug(METHOD_NAME + " 2/2: Done. this.id = " + this.id);
      }
    catch (Exception exception)
      {
        throw new ProcessingException(exception);
      }
  }
  
  /**
   * Returns a string that identificates this instance. It has the
   * following form:<pre>
   *   "DefaultSessionUser" +
   *   '#' + instanceId
   *   '(' + numberOfRecycles
   *   ',' + {@link #id id}
   *   ',' + {@link #useMode useMode}
   *   ')'</pre>
   * where <code>instanceId</code> is the instance id and
   * <code>numberOfRecycles</code> the number of recycles of this instance.
   */

  public String getIdentification ()
  {
    return
      "DefaultGeneralUser" +
      '#' + this.instanceStatus.getInstanceId() +
      '(' + this.instanceStatus.getNumberOfRecycles() +
      ',' + this.id +
      ',' + this.useMode +
      ',' + this.data +
      ')';
  }

  /**
   * Default constructor. Creates an <code>DefaultGeneralUser</code> instance, calls the
   * superclass constructor, increases the instance counter and sets the instance id. 
   */

  public DefaultGeneralUser ()
  {
    super();
    this.keysVsDbColumns = new HashMap();
    this.keysVsDbColumns.put(User.LOGIN_NAME_DATA_KEY, DbColumn.LOGIN_NAME);
    this.keysVsDbColumns.put(User.FIRST_NAME_DATA_KEY, DbColumn.FIRST_NAME);
    this.keysVsDbColumns.put(User.SURNAME_DATA_KEY, DbColumn.SURNAME);
    this.keysVsDbColumns.put(User.EMAIL_DATA_KEY, DbColumn.EMAIL);
    this.keysVsDbColumns.put(User.PASSWORD_DATA_KEY, DbColumn.PASSWORD);
    this.keysVsDbColumns.put(User.THEME_DATA_KEY, DbColumn.THEME);
    this.keysVsDbColumns.put(User.DESCRIPTION_DATA_KEY, DbColumn.DESCRIPTION);
    this.keysVsDbColumns.put(User.CUSTOM_METAINFO_DATA_KEY, DbColumn.CUSTOM_METAINFO);
    this.keysVsDbColumns.put(User.LAST_LOGIN_DATA_KEY, DbColumn.LAST_LOGIN);
    this.keysVsDbColumns.put(User.CREATED_DATA_KEY, DbColumn.CREATED);
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }
}
