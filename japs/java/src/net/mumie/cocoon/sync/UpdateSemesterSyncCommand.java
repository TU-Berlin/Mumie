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

import java.sql.ResultSet;
import java.util.StringTokenizer;
import org.apache.cocoon.environment.Request;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.RequestParam;
import net.mumie.cocoon.notions.UserGroupName;
import net.mumie.cocoon.notions.DbColumn;
import java.util.HashMap;
import java.util.Map;

public class UpdateSemesterSyncCommand extends AbstractSyncCommand
{
  /**
   * Hint for this sync command.
   */

  public static final String HINT = "update-semester";

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
        String name = request.getParameter(RequestParam.NAME);
        String description = request.getParameter(RequestParam.DESCRIPTION);
        String sectionPath = request.getParameter(RequestParam.SECTION);
        String pureName = request.getParameter(RequestParam.PURE_NAME);
        
        this.getLogger().debug
          (METHOD_NAME + " 2/3:" +
           " syncId = " + syncId +
           ", name = " + name +
           ", description : " + description
           + ", sectionPath = " + sectionPath + ", pureName = " + pureName);

        if ( syncId == null )
          throw new IllegalArgumentException("Missing semester sync id");
        
        //Check if Id's exist
        dbHelper.getIdForSyncId(PseudoDocType.SEMESTER, syncId);
        
        // Update semester data in the db:
        dbHelper.updateSemesterData
          (syncId,
           name,
           description);
        //Set pureName
        if ( pureName != null )
        {
          Map data = new HashMap();
          data.put(DbColumn.PURE_NAME, pureName);
          dbHelper.updatePseudoDocDataBySyncId(PseudoDocType.SEMESTER, syncId, data);

        }
        // Set section:
        if ( sectionPath != null ) 
        {
          int containedIn = dbHelper.getSectionIdForPath(sectionPath);
          
          Map data = new HashMap();
          data.put(DbColumn.CONTAINED_IN, containedIn);
          dbHelper.updatePseudoDocDataBySyncId(PseudoDocType.SEMESTER, syncId, data);
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
          this.serviceManager.release(dbHelper);
      }
  }

}

