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

package net.mumie.cocoon.delete;

import java.sql.ResultSet;
import java.util.ArrayList;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.pseudodocs.SessionUser;
import net.mumie.cocoon.pseudodocs.User;
import net.mumie.cocoon.service.AbstractJapsServiceable;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.util.LogUtil;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.activity.Disposable;
import net.mumie.cocoon.notions.Nature;
import org.apache.avalon.framework.service.ServiceException;
import java.util.List;

public class DeleteHelperImpl extends AbstractJapsServiceable
  implements Recyclable, Disposable, DeleteHelper
{

  // --------------------------------------------------------------------------------
  // h1: Global constants and variables
  // --------------------------------------------------------------------------------

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(DeleteHelperImpl.class);

  /**
   * Id of the user for which this instance does deletions and undeletions.
   */

  protected int userId = Id.UNDEFINED;

  /**
   * The db helper of this instance.
   */

  protected DbHelper dbHelper = null;

  // --------------------------------------------------------------------------------
  // h1: "Ensure" methods for the dbHelper and userId
  // --------------------------------------------------------------------------------

  /**
   * Ensures that the db helper is ready to use.
   */

  protected void ensureDbHelper ()
    throws ServiceException 
  {
    if ( this.dbHelper == null )
      this.dbHelper = (DbHelper)this.serviceManager.lookup(DbHelper.ROLE);
  }

  /**
   * Ensures the user id is properly set.
   */

  protected void ensureUserId ()
    throws ServiceException
  {
    if ( this.userId == Id.UNDEFINED )
      {
        User user = null;    
        try
          {
            user = (User)this.serviceManager.lookup(SessionUser.ROLE);
            this.userId = user.getId();
          }
        finally
          {
            if ( user != null ) this.serviceManager.release(user);
          }
      }
  }

  // --------------------------------------------------------------------------------
  // h1: Reset variables and releasing services
  // --------------------------------------------------------------------------------

  /**
   * Resets the user id ({@link #userId userId}).
   */

  protected void reset ()
  {
    this.userId = Id.UNDEFINED;
  }

  /**
   * Releases all services. Since the db helper is the only service needed by this class,
   * only the db helper is released.
   */

  protected void releaseServices ()
  {
    final String METHOD_NAME = "releaseServices";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    if ( this.dbHelper != null )
      {
        this.serviceManager.release(this.dbHelper);
        this.dbHelper = null;
      }
    this.logDebug(METHOD_NAME + " 1/2: Done");
  }

  // --------------------------------------------------------------------------------
  // h1: Lifecycle related methods
  // --------------------------------------------------------------------------------

  /**
   * Creates a new instance.
   */

  public DeleteHelperImpl ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Recycles this instance.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.reset();
    this.releaseServices();
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
    this.reset();
    this.releaseServices();
    this.instanceStatus.notifyDispose();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // h1: Specifying the user
  // --------------------------------------------------------------------------------

  /**
   * Sets the user for which this instance does deletions. If not set, the currently
   * logged-in user is assumed.
   */

  public void setUser (int userId)
  {
    this.userId = userId;
  }

  // --------------------------------------------------------------------------------
  // h1: Deleting / undeleting
  // --------------------------------------------------------------------------------

  /**
   * Tries to set the "deleted" flag of the (pseudo-)document represented by the specified
   * {@link DeleteItem DeleteItem} to the specified value. 
   */

  public void tryDelete (DeleteItem item, boolean deleted)
    throws DeletionException
  {
    final String METHOD_NAME = "tryDelete";
    this.logDebug
      (METHOD_NAME + " 1/2: Started. item = " + item.toString());
    final int nature = item.getNature();
    final int type = item.getType();
    final int id = item.getId();
    try
      {
        this.ensureUserId();
        this.ensureDbHelper();
        if ( nature == Nature.DOCUMENT )
          {
            if ( !this.dbHelper.checkWritePermissionById(this.userId, type, id) )
              {
                item.setStatus(DeleteItem.FAILED);
                item.setError(DeleteItem.PERMISSION_DENIED);
              }
            else
              this.dbHelper.updateDatum(type, id, DbColumn.DELETED, deleted);
          }
        else if ( nature == Nature.PSEUDO_DOCUMENT )
          {
            if ( !this.dbHelper.checkPseudoDocWritePermission(this.userId, type, id) )
              {
                item.setStatus(DeleteItem.FAILED);
                item.setError(DeleteItem.PERMISSION_DENIED);
              }
            else if ( item.getType() == PseudoDocType.SECTION && !this.checkSectionEmty(item) )
              {
                item.setStatus(DeleteItem.FAILED);
                item.setError(DeleteItem.NOT_EMPTY);
              }
            else
              this.dbHelper.updatePseudoDocDatum(type, id, DbColumn.DELETED, deleted);
          }
      }
    catch (Exception exception)
      {
        throw new DeletionException(exception);
      }
    this.logDebug(METHOD_NAME + " 2/2: Done. item = " + item.toString());
  }

  /**
   * Tries to delete the document or pseudo-dcoument represented by the specified
   * {@link DeleteItem DeleteItem} object. Deletion is only possible if the user has write
   * permission on the (pseudo-)document, and, in case of a section, the section is
   * empty.
   */

  public void tryDelete (DeleteItem item)
    throws DeletionException
  {
    this.tryDelete(item, true);
  }

  /**
   * Tries to undelete the document or pseudo-document represented by the specified
   * {@link DeleteItem DeleteItem} object. Undeletion is only possible if the user has write
   * permission on the (pseudo-)document.
   */

  public void tryUndelete (DeleteItem item)
    throws DeletionException
  {
    this.tryDelete(item, false);
  }

  /**
   * Auxiliary method to implement {@link #tryDeleteSectionRecursively(DeleteItem)}. Tries to
   * delete the section represented by the specified {@link DeleteItem DeleteItem} recursively.
   * All (pseudo-)documents tried to delete in this process are added to the specified list. 
   */

  protected boolean tryDeleteSectionRecursively (DeleteItem secItem, List<DeleteItem> items)
    throws DeletionException
  {
    try
      {
        ResultSet resultSet = this.dbHelper.queryContentObjectsInSection
          (secItem.getId(), new String[] {DbColumn.DOC_TYPE, DbColumn.ID}, true);
        boolean allDeleted = true;
        while ( resultSet.next() )
          {
            int id = resultSet.getInt(DbColumn.ID);
            String typeName = resultSet.getString(DbColumn.DOC_TYPE);
            DeleteItem item = new DeleteItem(typeName, id);
            if ( item.getNature() == Nature.PSEUDO_DOCUMENT && item.getType() == PseudoDocType.SECTION )
              {
                if (!this.tryDeleteSectionRecursively(item, items) ) allDeleted = false;
              }
            else
              {
                this.tryDelete(item);
                if ( item.getStatus() == DeleteItem.FAILED ) allDeleted = false;
                items.add(item);
              }
          }
        if ( allDeleted )
          this.tryDelete(secItem);
        else
          {
            secItem.setStatus(DeleteItem.FAILED);
            secItem.setStatus(DeleteItem.NOT_EMPTY);
          }
        items.add(secItem);
        return allDeleted;
      }
    catch (Exception exception)
      {
        throw new DeletionException(exception);
      }
  }

  /**
   * Tries to recursively delete the section represented by the specified
   * {@link DeleteItem DeleteItem} object. Returns all affected (pseudo-)documents, i.e.,
   * all (pseudo-)documents the method tries to delete.
   */

  public DeleteItem[] tryDeleteSectionRecursively (DeleteItem secItem)
    throws DeletionException
  {
    final String METHOD_NAME = "tryDeleteSectionRecursively";
    this.logDebug(METHOD_NAME + " 1/2: Started. sectionItem = " + secItem);
    List<DeleteItem> items = new ArrayList<DeleteItem>();
    this.tryDeleteSectionRecursively(secItem, items);
    DeleteItem[] result = items.toArray(new DeleteItem[items.size()]);
    this.logDebug
      (METHOD_NAME + " 2/2: Done. result = " + LogUtil.arrayToString(result));
    return result;
  }

  /**
   * Tries to delete the (pseudo-)documents represented by the specified
   * {@link DeleteItem DeleteItem}s. If <code>recursive</code> is true, sections are tried
   * to delete recursively. Retutrns all affected (pseudo-)documents (if <code>recursive</code>
   * is turned on, this may be more than the specifed {@link DeleteItem DeleteItem}s).
   */

  public DeleteItem[] tryDeleteAll (DeleteItem[] items, boolean recursive)
    throws DeletionException
  {
    final String METHOD_NAME = "tryDeleteAll";
    this.logDebug
      (METHOD_NAME + " 1/2: Started." +
       " items = " + LogUtil.arrayToString(items) +
       ", recursive = " + recursive);
    List<DeleteItem> doneItems = new ArrayList<DeleteItem>();
    for (DeleteItem item : items)
      {
        if ( item.getNature() == Nature.PSEUDO_DOCUMENT &&
             item.getType() == PseudoDocType.SECTION && recursive )
          this.tryDeleteSectionRecursively(item, doneItems);
        else
          {
            this.tryDelete(item);
            doneItems.add(item);
          }
      }
    DeleteItem[] result = doneItems.toArray(new DeleteItem[doneItems.size()]);
    this.logDebug
      (METHOD_NAME + " 2/2: Done. result = " + LogUtil.arrayToString(result));
    return result;
  }

  /**
   * Tries to undelete the (pseudo-)documents represented by the specified
   * {@link DeleteItem DeleteItem}s. Retutrns all affected (pseudo-)documents.
   */

  public DeleteItem[] tryUndeleteAll (DeleteItem[] items)
    throws DeletionException
  {
    final String METHOD_NAME = "tryUndeleteAll";
    this.logDebug(METHOD_NAME + " 1/2: Started. items = " + LogUtil.arrayToString(items));
    List<DeleteItem> doneItems = new ArrayList<DeleteItem>();
    for (DeleteItem item : items)
      {
        this.tryDelete(item);
        doneItems.add(item);
      }
    DeleteItem[] result = doneItems.toArray(new DeleteItem[doneItems.size()]);
    this.logDebug
      (METHOD_NAME + " 2/2: Done. result = " + LogUtil.arrayToString(result));
    return result;
  }

  // --------------------------------------------------------------------------------
  // h1: Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * Returns true if the section represented by the specified {@link DeleteItem DeleteItem}
   * is empty, otherwise false.
   */

  protected boolean checkSectionEmty (DeleteItem secItem)
    throws Exception
  {
    ResultSet resultSet = this.dbHelper.queryContentObjectsInSection
      (secItem.getId(), new String[] {DbColumn.ID}, true);
    return !resultSet.next();
  }

}