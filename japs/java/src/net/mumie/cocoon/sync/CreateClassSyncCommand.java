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
 * Creates a new class.
 *
 * <p>Arguments (mandatory in <strong>this face</strong>):</p>
 * <ul>
 *   <li><strong>sync-id</strong> &mdash; synchronization id of the new class</li>
 *   <li><strong>name</strong> &mdash; name of the new class</li>
 *   <li><strong>semester</strong> &mdash; sync id of the semester the new class belongs to</li>
 *   <li>description &mdash; description of the new class</li>
 *   <li>pure-name &mdash; pure name of the new class</li>
 *   <li>section &mdash; path of the section the new class is stored in</li>
 *   <li>lecturers &mdash; lecturers of the new class (comma- or space-separated list of sync ids)</li>
 * </ul>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: CreateClassSyncCommand.java,v 1.2 2009/02/09 15:21:26 rassy Exp $</code>
 */

public class CreateClassSyncCommand extends AbstractSyncCommand
{
  /**
   * Hint for this sync command.
   */

  public static final String HINT = "create-class";

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
        String semester = request.getParameter(RequestParam.SEMESTER);
        String description = request.getParameter(RequestParam.DESCRIPTION);
        String pureName = request.getParameter(RequestParam.PURE_NAME);
        String sectionPath = request.getParameter(RequestParam.SECTION);
        String lecturers = request.getParameter(RequestParam.LECTURERS);

	// Set pure name if not set by request parameter:
        if ( pureName == null || pureName.equals("") )
          pureName = this.suggestPureName("cls", name);

        // Set section path if not set by request parameter:
        if ( sectionPath == null || sectionPath.equals("") )
          sectionPath = this.getDefaultSectionPath(semester, dbHelper);

        this.getLogger().debug
          (METHOD_NAME + " 2/4:" +
           " syncId = " + syncId +
           ", name = " + name +
           ", semester = " + semester +
           ", description : " + description +
           ", pureName = " + pureName +
           ", sectionPath = " + sectionPath +
           ", lecturers = " + lecturers);

        // Get id of the section:
        int containedIn = dbHelper.getSectionIdForPath(sectionPath);

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
	if ( lecturers != null && !lecturers.equals("") )
	  {
            for (String lecturer : this.splitPattern.split(lecturers))
	      {
		// Set user as a lecturer of the class:
		dbHelper.addClassLecturer(syncId, lecturer);
		
		// Add user to lecturer group if necessary:
		if ( !dbHelper.checkUserGroupMembership(lecturer, UserGroupName.LECTURERS) )
		  dbHelper.addMemberToUserGroup(lecturer, UserGroupName.LECTURERS);
	      }
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

  /**
   * Returns the default section path. The default section path is
   * <code><var>semester_section_path</var> + "/classes"</code> where
   * <var>semester_section_path</var> is the path of the section the
   * smester is in.
   */

  protected String getDefaultSectionPath (String semester, DbHelper dbHelper)
    throws SQLException
  {
    ResultSet resultSet = dbHelper.queryPseudoDocDatumBySyncId
      (PseudoDocType.SEMESTER, semester, DbColumn.SECTION_PATH);
    if ( !resultSet.next() )
      throw new SQLException("Cannot find semester with sync id \"" + semester + "\"");
    return resultSet.getString(DbColumn.SECTION_PATH) + "/classes";
  }
}
