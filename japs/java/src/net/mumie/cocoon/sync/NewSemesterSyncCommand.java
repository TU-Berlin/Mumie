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
import org.apache.cocoon.environment.Request;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.RequestParam;
import net.mumie.cocoon.notions.UserGroupName;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.pseudodocs.SessionUser;
import net.mumie.cocoon.pseudodocs.User;

/**
 * Represents the synchronization command <code>new-semester</code>.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: NewSemesterSyncCommand.java,v 1.11 2009/01/28 13:16:55 rassy Exp $</code>
 *
 * @deprecated use {@link CreateSemesterSyncCommand CreateSemesterSyncCommand instead}
 */

public class NewSemesterSyncCommand extends AbstractSyncCommand
{
  /**
   * Hint for this sync command.
   */

  public static final String HINT = "new-semester";

  /**
   * Executes this sync command.
   */

  public void execute (Request request)
    throws SyncException
  {
    final String METHOD_NAME = "execute";
    this.getLogger().debug(METHOD_NAME + " 1/3: Started");

    DbHelper dbHelper = null;
    User user = null;

    try
      {
        // Initialize db helper:
        dbHelper = (DbHelper)this.serviceManager.lookup(DbHelper.ROLE);

        // Initialize user:
        user = (User)this.serviceManager.lookup(SessionUser.ROLE);

        // Get data from request:
        String syncId = request.getParameter(RequestParam.SYNC_ID);
        String name = request.getParameter(RequestParam.NAME);
        String description = request.getParameter(RequestParam.DESCRIPTION);
        String pureName = request.getParameter(RequestParam.PURE_NAME);
        String sectionPureName = request.getParameter(RequestParam.SECTION_PURE_NAME);

        this.getLogger().debug
          (METHOD_NAME + " 2/3: " +
           " syncId = " + syncId +
           ", name = " + name +
           ", description = " + description +
           ", pureName = " + pureName +
           ", sectionPureName = " + sectionPureName);

	// Create pure name if not set by request parameter:
        if ( pureName == null || pureName.equals("") )
          pureName = this.suggestPureName("sem", name);

	// Create section pure name if not set by request parameter:
        if ( sectionPureName == null || sectionPureName.equals("") )
          sectionPureName = this.suggestPureName(null, name);

	// Get sync home:
	int syncHome = user.getSyncHome();
	if ( syncHome == Id.UNDEFINED )
          throw new SyncException("User has no sync home: " + user.getId());

        // Start transaction:
	dbHelper.beginTransaction(this, true);

        // Create semester section in the sync home:
        int semesterSectionId = this.createSection
          (dbHelper, sectionPureName, name, description, syncHome);
        this.setPermissions(dbHelper, PseudoDocType.SECTION, semesterSectionId);

        // Create the semester itself:
        int semesterId = dbHelper.storeSemesterData
          (syncId, pureName, semesterSectionId, name, description);
        this.setPermissions(dbHelper, PseudoDocType.SEMESTER, semesterId);

        // Create classes section:
        int classesSectionId = this.createSection
          (dbHelper, "classes", "Classes", null, semesterSectionId);
        this.setPermissions(dbHelper, PseudoDocType.SECTION, classesSectionId);

        // Create courses section:
        int coursesSectionId = this.createSection
          (dbHelper, "courses", "Courses", null, semesterSectionId);
        this.setPermissions(dbHelper, PseudoDocType.SECTION, coursesSectionId);

        // Create tutorials section:
        int tutorialsSectionId = this.createSection
          (dbHelper, "tutorials", "Tutorials", null, semesterSectionId);
        this.setPermissions(dbHelper, PseudoDocType.SECTION, tutorialsSectionId);

	dbHelper.endTransaction(this);

        this.getLogger().debug(METHOD_NAME + "3/3: Done. semesterId = " + semesterId);
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
        if ( user != null )
          this.serviceManager.release(user);
      }
  }

  /**
   * Creates a new section with the specified data using the specified db helper.
   */

  protected int createSection (DbHelper dbHelper,
                               String pureName,
                               String name,
                               String description,
                               int containedIn)
    throws Exception
  {
    // Prepare data:
    Map data = new HashMap();
    data.put(DbColumn.PURE_NAME, pureName);
    data.put(DbColumn.NAME, name);
    if ( description != null ) data.put(DbColumn.DESCRIPTION, description);
    data.put(DbColumn.CONTAINED_IN, containedIn);

    // Create section:
    int id = dbHelper.storePseudoDocData(PseudoDocType.SECTION, data);

    return id;
  }

  /**
   * Gives the following groups read permission for the pseudo-document with the specified
   * type and id using the specified db helper:
   * {@link UserGroupName#ADMINS ADMINS},
   * {@link UserGroupName#LECTURERS LECTURERS},
   * {@link UserGroupName#TUTORS TUTORS},
   * {@link UserGroupName#AUTHORS AUTHORS},
   * {@link UserGroupName#SYNCS SYNCS}.
   */

  protected void setPermissions (DbHelper dbHelper, int type, int id)
    throws Exception
  {
    String[] groupNames = new String[]
    {
      UserGroupName.ADMINS,
      UserGroupName.LECTURERS,
      UserGroupName.TUTORS,
      UserGroupName.AUTHORS,
      UserGroupName.SYNCS,
    };

    for (String groupName : groupNames)
      dbHelper.storePseudoDocumentReadPermission(type, id, groupName);
  }
}
