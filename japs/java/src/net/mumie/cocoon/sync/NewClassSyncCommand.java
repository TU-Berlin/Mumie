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
import org.apache.cocoon.environment.Request;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.RequestParam;
import net.mumie.cocoon.notions.UserGroupName;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.DbColumn;
import java.util.StringTokenizer;

/**
 * Represents the synchronization command <code>new-class</code>.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: NewClassSyncCommand.java,v 1.12 2009/01/28 13:16:55 rassy Exp $</code>
 *
 * @deprecated use {@link CreateClassSyncCommand CreateClassSyncCommand instead}
 */

public class NewClassSyncCommand extends AbstractSyncCommand
{
  /**
   * Hint for this sync command.
   */

  public static final String HINT = "new-class";

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
        String syncId = request.getParameter(RequestParam.SYNC_ID);
        String name = request.getParameter(RequestParam.NAME);
        String description = request.getParameter(RequestParam.DESCRIPTION);
        String semester = request.getParameter(RequestParam.SEMESTER);
        String lecturers = request.getParameter(RequestParam.LECTURERS);
        String pureName = request.getParameter(RequestParam.PURE_NAME);
        String courses = request.getParameter(RequestParam.COURSES);

	// Create pure name if not set by request parameter:
        if ( pureName == null || pureName.equals("") )
          pureName = this.suggestPureName("cls", name);

        // Get section:
        int containedIn = dbHelper.getSubSectionForSemesterSyncId(semester, "classes");

        this.getLogger().debug
          (METHOD_NAME + " 2/4:" +
           " syncId = " + syncId +
           ", pureName = " + pureName +
           ", containedIn = " + containedIn +
           ", name = " + name +
           ", description : " + description +
           ", semester = " + semester +
           ", lecturers = " + lecturers);

	dbHelper.beginTransaction(this, true);

        // Create new class in the db:
        int classId = dbHelper.storeClassData
          (syncId,
           pureName,
           containedIn,
           name,
           description,
           semester);
        this.getLogger().debug(METHOD_NAME + " 3/4: classId = " + classId);

        // Set permissions
	dbHelper.storePseudoDocumentReadPermission
          (PseudoDocType.CLASS, classId, UserGroupName.ADMINS);
	dbHelper.storePseudoDocumentReadPermission
          (PseudoDocType.CLASS, classId, UserGroupName.LECTURERS);
	dbHelper.storePseudoDocumentReadPermission
          (PseudoDocType.CLASS, classId, UserGroupName.SYNCS);
	dbHelper.storePseudoDocumentReadPermission
          (PseudoDocType.CLASS, classId, UserGroupName.TUTORS);
	dbHelper.storePseudoDocumentReadPermission
          (PseudoDocType.CLASS, classId, UserGroupName.STUDENTS);
	dbHelper.storePseudoDocumentReadPermission
          (PseudoDocType.CLASS, classId, UserGroupName.AUTHORS);

        // Set lecturers if necessary:
	if ( lecturers != null && lecturers.length() != 0 )
	  {
	    StringTokenizer tokenizer = new StringTokenizer(lecturers, ", \t\n\r\f");
	    while ( tokenizer.hasMoreTokens() )
	      {
		String lecturerSyncId = tokenizer.nextToken();
		
		// Set user as a lecturer of the class:
		dbHelper.addClassLecturer(syncId, lecturerSyncId);
		
		// Add user to lecturer group if necessary:
		if ( !dbHelper.checkUserGroupMembership(lecturerSyncId, UserGroupName.LECTURERS) )
		  dbHelper.addMemberToUserGroup(lecturerSyncId, UserGroupName.LECTURERS);
	      }
	  }

        // Assign this class to courses if necessary:
        if ( courses != null && courses.length() != 0 )
          {
	    StringTokenizer tokenizer = new StringTokenizer(courses, ", \t\n\r\f");
            int[] courseIds = new int[tokenizer.countTokens()];
            for (int i = 0; i < courseIds.length; i++)
              courseIds[i] = Integer.parseInt(tokenizer.nextToken());
            dbHelper.setClassForCourses(syncId, courseIds);
          }

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
