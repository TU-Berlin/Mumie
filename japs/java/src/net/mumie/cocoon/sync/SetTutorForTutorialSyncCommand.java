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

import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.RequestParam;
import net.mumie.cocoon.notions.UserGroupName;

import java.sql.ResultSet;
import java.util.StringTokenizer;
import org.apache.cocoon.environment.Request;


public class SetTutorForTutorialSyncCommand extends AbstractSyncCommand
{
  /**
   * Hint for this sync command.
   */

  public static final String HINT = "set-tutor-for-tutorial";

  public void execute(Request request) throws SyncException
  {

    final String METHOD_NAME = "execute";
    this.getLogger().debug(METHOD_NAME + "1/3: Started");
    DbHelper dbHelper = null;

    try
    {
      // Initialize db helper:
      dbHelper = (DbHelper) this.serviceManager.lookup(DbHelper.ROLE);

      // Get data from request:
      String tutorSyncId = request.getParameter(RequestParam.USER);
      String tutorialSyncId = request.getParameter(RequestParam.TUTORIAL);

      this.getLogger().debug(
          METHOD_NAME + " 2/3:" + " tutorialSyncId = " + tutorialSyncId
              + ", userSyncId = " + tutorSyncId);

      if ( tutorSyncId == null )
        throw new IllegalArgumentException("Missing user sync id");

      if ( tutorialSyncId == null )
        throw new IllegalArgumentException("Missing tutorial sync id");

      if ( ! dbHelper.checkUserGroupMembership(tutorSyncId,
          UserGroupName.TUTORS) )
        throw new IllegalArgumentException("User is not a tutor");

      //Check if Id's exist
      int tutorId = dbHelper.getIdForSyncId(PseudoDocType.USER, tutorSyncId);
      int tutorialId = dbHelper.getIdForSyncId(PseudoDocType.TUTORIAL,
          tutorialSyncId);
      
      //Check if User is already the tutor
      ResultSet resultSetTutor = dbHelper.queryTutorialDatum(tutorialId, "tutor");
      resultSetTutor.next();
      if(resultSetTutor.getInt("tutor") == tutorId)
        throw new IllegalArgumentException("User is already the tutor of this tutorial");
      
      // Update tutorial dat in the db:
      dbHelper.updateTutorialData
        (tutorialSyncId,
         null,
         null,
         tutorSyncId,
         null);
      
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
