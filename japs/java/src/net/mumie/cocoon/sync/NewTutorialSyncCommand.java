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
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.RequestParam;
import net.mumie.cocoon.notions.UserGroupName;
import net.mumie.cocoon.util.PathTokenizer;
import org.apache.cocoon.environment.Request;

/**
 * Represents the synchronization command <code>new-tutorial</code>.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: NewTutorialSyncCommand.java,v 1.15 2009/02/09 15:21:26 rassy Exp $</code>
 *
 * @deprecated use {@link CreateTutorialSyncCommand CreateTutorialSyncCommand instead}
 */

public class NewTutorialSyncCommand extends AbstractSyncCommand
{
  /**
   * Hint for this sync command.
   */

  public static final String HINT = "new-tutorial";

  /**
   * The path tokenizer of this instance.
   */

  protected PathTokenizer pathTokenizer = new PathTokenizer();

  /**
   * Executes this sync command.
   */

  public void execute (Request request)
    throws SyncException
  {
    final String METHOD_NAME = "execute";
    this.getLogger().debug(METHOD_NAME + "1/4: Started");
    DbHelper dbHelper = null;
    try
      {
        // Initialize db helper:
        dbHelper = (DbHelper)this.serviceManager.lookup(DbHelper.ROLE);

        // Get data from request:
        String syncId = request.getParameter(RequestParam.SYNC_ID);
        String name = request.getParameter(RequestParam.NAME);
        String description = request.getParameter(RequestParam.DESCRIPTION);
        String tutorSyncId = request.getParameter(RequestParam.TUTOR);
        String classSyncId = request.getParameter(RequestParam.CLASS);
        String pureName = request.getParameter(RequestParam.PURE_NAME);

	// Create pure name if not set by request parameter:
        if ( pureName == null || pureName.equals("") )
          pureName = this.suggestPureName("tut", name);

        // Get section:
        int classId = dbHelper.getIdForSyncId(PseudoDocType.CLASS, classSyncId);
        ResultSet classData = dbHelper.queryPseudoDocData
          (PseudoDocType.CLASS, classId, new String[] {DbColumn.SECTION_PATH});
        if ( !classData.next() )
          throw new SyncException("Can not find class for id: " + classId);
        String classSectionPath = classData.getString(DbColumn.SECTION_PATH);
        String containedInPath = this.suggestPath(classSectionPath);
        ResultSet sectionData = dbHelper.queryPseudoDocDataByPath
          (PseudoDocType.SECTION, containedInPath, new String[] {DbColumn.ID});
        if ( !sectionData.next() )
          throw new SyncException("Section does not exist: " + containedInPath);
        int containedIn = sectionData.getInt(DbColumn.ID);

        this.getLogger().debug
          (METHOD_NAME + " 2/4:" +
           " syncId = " + syncId +
           ", pureName = " + pureName +
           ", containedInPath = " + containedInPath +
           ", containedIn = " + containedIn +
           ", name = " + name +
           ", description : " + description +
           ", tutorSyncId = " + tutorSyncId +
           ", classSyncId = " + classSyncId);

        // Start transaction:
	dbHelper.beginTransaction(this, true);

        // Create new tutorial in the db:
        int tutorialId = dbHelper.storeTutorialData
          (syncId,
           pureName,
           containedIn,
           name,
           description,
           tutorSyncId,
           classSyncId);

        this.getLogger().debug(METHOD_NAME + "3/4: tutorialId = " + tutorialId);

        // Set permissions:
	dbHelper.storePseudoDocumentReadPermission
          (PseudoDocType.TUTORIAL, tutorialId, UserGroupName.ADMINS);
	dbHelper.storePseudoDocumentReadPermission
          (PseudoDocType.TUTORIAL, tutorialId, UserGroupName.LECTURERS);
	dbHelper.storePseudoDocumentReadPermission
          (PseudoDocType.TUTORIAL, tutorialId, UserGroupName.SYNCS);
	dbHelper.storePseudoDocumentReadPermission
          (PseudoDocType.TUTORIAL, tutorialId, UserGroupName.TUTORS);
	dbHelper.storePseudoDocumentReadPermission
          (PseudoDocType.TUTORIAL, tutorialId, UserGroupName.STUDENTS);
	dbHelper.storePseudoDocumentReadPermission
          (PseudoDocType.TUTORIAL, tutorialId, UserGroupName.AUTHORS);

        // Add tutor to tutors group if necessary:
        if ( !dbHelper.checkUserGroupMembership(tutorSyncId, UserGroupName.TUTORS) )
          dbHelper.addMemberToUserGroup(tutorSyncId, UserGroupName.TUTORS);

        // End transaction:
	dbHelper.endTransaction(this);

        this.getLogger().debug(METHOD_NAME + "4/4: Done");
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
   * 
   */

  protected String suggestPath (String classSectionPath)
  {
    this.pathTokenizer.tokenize(classSectionPath);
    String pureName = this.pathTokenizer.getPureName();
    if ( !pureName.equals("classes") )
      throw new IllegalArgumentException
        ("Class section pure name does not meet the standard: \"" + pureName + "\"" +
         " (should be \"classes\")");
    String sectionPath = this.pathTokenizer.getSectionPath();
    return sectionPath + "/tutorials";
  }
}
