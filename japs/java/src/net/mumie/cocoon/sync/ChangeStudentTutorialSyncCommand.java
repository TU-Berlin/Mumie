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

/**
 * Removes a student from one tutorial and adds her/him to another one.
 *
 * <p>Arguments (mandatory in <strong>this face</strong>):</p>
 * <ul>
 *   <li><strong>user</strong> &mdash; synchronization id of the user</li>
 *   <li><strong>old-tutorial</strong> &mdash; synchronization id of the old tutorial</li>
 *   <li><strong>new-tutorial</strong> &mdash; synchronization id of the new tutorial</li>
 * </ul>
 * 
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: ChangeStudentTutorialSyncCommand.java,v 1.3 2009/05/15 13:09:57 linges Exp $</code>
 */

public class ChangeStudentTutorialSyncCommand extends AbstractSyncCommand
{
  /**
   * Hint for this sync command.
   */

  public static final String HINT = "change-student-tutorial";

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
        String oldTutorialSyncId = request.getParameter(RequestParam.OLD_TUTORIAL);
        String newTutorialSyncId = request.getParameter(RequestParam.NEW_TUTORIAL);
        String userSyncId = request.getParameter(RequestParam.USER);

        this.getLogger().debug
          (METHOD_NAME + " 2/3:" +
           " oldTutorialSyncId = " + oldTutorialSyncId +
           " newTutorialSyncId = " + newTutorialSyncId +
           ", userSyncId = " + userSyncId);

        //Check if Id's exist
        dbHelper.getIdForSyncId(PseudoDocType.TUTORIAL, oldTutorialSyncId);
        dbHelper.getIdForSyncId(PseudoDocType.TUTORIAL, newTutorialSyncId);
      
        
        if(!dbHelper.checkTutorialMembership(userSyncId, oldTutorialSyncId))
          throw new IllegalArgumentException("user not in old-tutorial");
        
        if(dbHelper.checkTutorialMembership(userSyncId, newTutorialSyncId))
          throw new IllegalArgumentException("user is already in new-tutorial");
        
	dbHelper.beginTransaction(this, true);

        // Remove user from old tutorial:
        dbHelper.removeTutorialMember(oldTutorialSyncId, userSyncId);

        // Add user to new tutorial:
        dbHelper.addTutorialMember(newTutorialSyncId, userSyncId);

	dbHelper.endTransaction(this);

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
