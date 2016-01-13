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

public class UnsetLecturerForClassSyncCommand extends AbstractSyncCommand
{
  /**
   * Hint for this sync command.
   */

  public static final String HINT = "unset-lecturer-for-class";

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
      String lecturerSyncId = request.getParameter(RequestParam.USER);
      String classSyncId = request.getParameter(RequestParam.CLASS);

      this.getLogger().debug(
          METHOD_NAME + " 2/3:" + " tutorialSyncId = " + classSyncId
              + ", userSyncId = " + lecturerSyncId);

      if ( lecturerSyncId == null )
        throw new IllegalArgumentException("Missing user sync id");

      if ( classSyncId == null )
        throw new IllegalArgumentException("Missing class sync id");

      // Check if Id's exist
      int lecturerId = dbHelper.getIdForSyncId(PseudoDocType.USER,
          lecturerSyncId);
      int classId = dbHelper.getIdForSyncId(PseudoDocType.CLASS, classSyncId);

      if ( ! dbHelper.checkUserGroupMembership(lecturerSyncId,
          UserGroupName.LECTURERS) )
        throw new IllegalArgumentException("User is not a lecturer");

      // Check if User is the lecturer of the class
       ResultSet resultSetLecturer = dbHelper.queryClassLecturers(classId,
       new String[] { "id" });
            
      boolean isLecturerOfClass = false;
      
      while ( resultSetLecturer.next() )
      {

        if ( resultSetLecturer.getInt("id") == lecturerId )
        {
          isLecturerOfClass = true;
          break;
        }

      }
      
      if(!isLecturerOfClass)
        throw new IllegalArgumentException(
          "User is not lecturer of the class");
      
      
      dbHelper.removeClassLecturer(classSyncId, lecturerSyncId);

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
