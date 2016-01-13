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

package net.mumie.cocoon.sync;

import org.apache.cocoon.environment.Request;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.RequestParam;
import net.mumie.cocoon.notions.UserGroupName;
import net.mumie.cocoon.notions.DbColumn;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class UpdateUserSyncCommand extends AbstractSyncCommand
{
  /**
   * Hint for this sync command.
   */

  public static final String HINT = "update-user";

  /**
   * Executes this sync command.
   */

  public void execute (Request request)
    throws SyncException
  {
    final String METHOD_NAME = "execute";
    this.getLogger().debug(METHOD_NAME + "1/3: Started");
    DbHelper dbHelper = null;
    try
      {
        // Initialize db helper:
        dbHelper = (DbHelper)this.serviceManager.lookup(DbHelper.ROLE);

        // Get data from request:
        String syncId = request.getParameter(RequestParam.SYNC_ID);
        String loginName = request.getParameter(RequestParam.LOGIN_NAME);
        String passwordEncrypted = request.getParameter(RequestParam.PASSWORD_ENCRYPTED);
        String firstName = request.getParameter(RequestParam.FIRST_NAME);
        String surname = request.getParameter(RequestParam.SURNAME);
        String matrNumber = request.getParameter(RequestParam.MATR_NUMBER);
        String sectionPath = request.getParameter(RequestParam.SECTION);
        String pureName = request.getParameter(RequestParam.PURE_NAME);
        String groupNames = request.getParameter(RequestParam.GROUPS);
        String lang = request.getParameter(RequestParam.LANG);
        
        this.getLogger().debug
          (METHOD_NAME + " 2/3:" +
           " syncId = " + syncId +
           ", loginName = " + loginName +
           ", passwordEncrypted : " + (passwordEncrypted == null ? "-null-" : "-not-null-") +
           ", firstName = " + firstName +
           ", surname = " + surname +
           ", matrNumber = " + matrNumber + 
           ", sectionPath = " + sectionPath + 
           ", pureName = " + pureName +
           ", Groups = " + groupNames);
        
        if ( syncId == null )
          throw new IllegalArgumentException("Missing user sync id");
        
        //Check if Id's exist
        int userId = dbHelper.getIdForSyncId(PseudoDocType.USER, syncId);
        
        // Update user data in db:
        if(loginName != null || passwordEncrypted != null || 
            firstName != null || surname != null  || matrNumber != null)
          dbHelper.updateUserData
            (syncId,
             loginName,
             passwordEncrypted,
             firstName,
             surname,
             matrNumber);
        
        //Set pureName
        if ( pureName != null )
        {
          Map data = new HashMap();
          data.put(DbColumn.PURE_NAME, pureName);
          dbHelper.updatePseudoDocDataBySyncId(PseudoDocType.USER, syncId, data);

        }
        // Set section:
        if ( sectionPath != null ) 
        {
          int containedIn = dbHelper.getSectionIdForPath(sectionPath);
          
          Map data = new HashMap();
          data.put(DbColumn.CONTAINED_IN, containedIn);
          dbHelper.updatePseudoDocDataBySyncId(PseudoDocType.USER, syncId, data);
        }
        
        //Set language id
        if ( lang != null ) 
        {
          int langId = dbHelper.queryLangIdByCode(lang);
                    
          Map data = new HashMap();
          data.put(DbColumn.LANGUAGE, langId);
          dbHelper.updatePseudoDocDataBySyncId(PseudoDocType.USER, syncId, data);
        }
        
        if(groupNames !=  null)
        {
          for (String groupName : this.splitPattern.split(groupNames))
            dbHelper.addMemberToUserGroup(syncId, groupName);
        }
        
        
        
        this.getLogger().debug(METHOD_NAME + "3/3: Done");
      }
    catch (Exception exception)
      {
        throw new SyncException(exception);
      }
    finally
    {
      if ( dbHelper != null )
        try
        {
          if ( dbHelper.hasTransactionLocked(this) )
            dbHelper.abortTransaction(this);
        }
        catch (Exception exception)
        {
          throw new SyncException(exception);
        }
        finally
        {
          this.serviceManager.release(dbHelper);
        }
    }
  }

}
