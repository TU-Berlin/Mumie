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
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.RequestParam;
import net.mumie.cocoon.notions.UserGroupName;
import net.mumie.cocoon.util.PathTokenizer;
import org.apache.cocoon.environment.Request;
import net.mumie.cocoon.util.TutorialUtil;

/**
 * Creates a new tutorial.
 *
 * <p>Arguments (mandatory in <strong>this face</strong>):</p>
 * <ul>
 *   <li><strong>sync-id</strong> &mdash; synchronization id of the new tutorial</li>
 *   <li><strong>name</strong> &mdash; name of the new tutorial</li>
 *   <li><strong>class</strong> &mdash; sync id of the class the new tutorial belongs to</li>
 *   <li>description &mdash; description of the new tutorial</li>
 *   <li>pure-name &mdash; pure name of the new tutorial</li>
 *   <li>section &mdash; path of the section the new tutorial is stored in</li>
 *   <li>tutor &mdash; sync id of the tutor of new tutorial</li>
 * </ul>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: CreateTutorialSyncCommand.java,v 1.4 2009/05/15 12:14:07 linges Exp $</code>
 */

public class CreateTutorialSyncCommand extends AbstractSyncCommand
{
  /**
   * Hint for this sync command.
   */

  public static final String HINT = "create-tutorial";

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
        String classSyncId = request.getParameter(RequestParam.CLASS);
        String description = request.getParameter(RequestParam.DESCRIPTION);
        String pureName = request.getParameter(RequestParam.PURE_NAME);
        String sectionPath = request.getParameter(RequestParam.SECTION);
        String tutorSyncId = request.getParameter(RequestParam.TUTOR);
        
        if ( syncId == null )
          throw new IllegalArgumentException("Missing tutorial sync id");
        
        if ( name == null )
          throw new IllegalArgumentException("Missing name");
        
        if ( classSyncId == null )
          throw new IllegalArgumentException("Missing class sync id");
        
        if ( tutorSyncId == null )
          throw new IllegalArgumentException("Missing tutor sync id");
        
        // Check if Id's exist
        dbHelper.getIdForSyncId(PseudoDocType.CLASS, classSyncId);
        dbHelper.getIdForSyncId(PseudoDocType.USER, tutorSyncId);
        
        // Set pure name if not set by request parameter:
        if ( pureName == null || pureName.equals("") )
          pureName = this.suggestPureName("tut", name);

        // Set section path if not set by request parameter:
        if ( sectionPath == null || sectionPath.equals("") )
          sectionPath = TutorialUtil.getDefaultSectionPath(classSyncId, dbHelper, this.pathTokenizer);

        this.getLogger().debug
          (METHOD_NAME + " 2/4:" +
           " syncId = " + syncId +
           ", name = " + name +
           ", classSyncId = " + classSyncId +
           ", description : " + description +
           ", pureName = " + pureName +
           ", sectionPath = " + sectionPath +
           ", tutorSyncId = " + tutorSyncId);




        
        // Get id of the section:
        int containedIn = dbHelper.getSectionIdForPath(sectionPath);

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
}
