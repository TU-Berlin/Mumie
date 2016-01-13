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
import java.sql.SQLException;
import java.util.regex.Pattern;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.RequestParam;
import net.mumie.cocoon.notions.UserGroupName;
import org.apache.cocoon.environment.Request;

/**
 * Removes a user from one or more groups
 *
 * <p>Arguments (mandatory in <strong>this face</strong>):</p>
 * <ul>
 *   <li><strong>user</strong> &mdash; synchronization id of the user</li>
 *   <li><strong>groups</strong> &mdash; names of the groups, as comma- or space-separated list</li>
 * </ul>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: RemoveUserFromGroupsSyncCommand.java,v 1.2 2009/12/27 18:06:28 rassy Exp $</code>
 */

public class RemoveUserFromGroupsSyncCommand extends AbstractSyncCommand
{
  /**
   * Hint for this sync command.
   */

  public static final String HINT = "remove-user-from-groups";

  /**
   * Executes this sync command.
   */

  public void execute (Request request)
    throws SyncException
  {
    final String METHOD_NAME = "execute";
    this.getLogger().debug(METHOD_NAME + " 1/4: Started");
    DbHelper dbHelper = null;
    try
      {
        // Initialize db helper:
        dbHelper = (DbHelper)this.serviceManager.lookup(DbHelper.ROLE);

        // Get data from request:
        String userSyncId = request.getParameter(RequestParam.USER);
        String groupNames = request.getParameter(RequestParam.GROUPS);

        this.getLogger().debug
          (METHOD_NAME + " 2/4:" +
           " userSyncId = " + userSyncId +
           ", groupNames = " + groupNames);

        dbHelper.beginTransaction(this, true);

        for (String groupName : this.splitPattern.split(groupNames))
          dbHelper.removeMemberFromUserGroup(userSyncId, groupName);

        dbHelper.endTransaction(this);

        this.getLogger().debug(METHOD_NAME + " 4/4: Done");
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
          catch(Exception exception)
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
