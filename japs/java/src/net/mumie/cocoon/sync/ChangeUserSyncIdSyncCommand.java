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

import java.util.HashMap;
import java.util.Map;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.RequestParam;
import org.apache.cocoon.environment.Request;

/**
 * Represents the synchronization command <code>change-user-data</code>.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: ChangeUserSyncIdSyncCommand.java,v 1.1 2009/02/19 15:16:43 rassy Exp $</code>
 */

public class ChangeUserSyncIdSyncCommand extends AbstractSyncCommand
{
  /**
   * Hint for this sync command.
   */

  public static final String HINT = "change-user-sync-id";

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
        String oldSyncId = request.getParameter(RequestParam.OLD_SYNC_ID);
        String newSyncId = request.getParameter(RequestParam.NEW_SYNC_ID);

        this.getLogger().debug
          (METHOD_NAME + " 2/3:" +
           " oldSyncId = " + oldSyncId +
           " newSyncId = " + newSyncId);

        // Update user data in db:
        Map data = new HashMap();
        data.put(DbColumn.SYNC_ID, newSyncId);
        dbHelper.updatePseudoDocDataBySyncId(PseudoDocType.USER, oldSyncId, data);

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
