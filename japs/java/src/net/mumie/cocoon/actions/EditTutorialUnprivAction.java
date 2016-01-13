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

package net.mumie.cocoon.actions;

import java.util.HashMap;
import java.util.Map;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.UserGroupName;
import net.mumie.cocoon.util.LogUtil;
import net.mumie.cocoon.util.ParamUtil;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import net.mumie.cocoon.util.PathTokenizer;
import net.mumie.cocoon.util.TutorialUtil;
import net.mumie.cocoon.util.DocUtil;
import net.mumie.cocoon.util.UserInputException;

public class EditTutorialUnprivAction extends ServiceableAction
{
  /**
   * Groups which have read permission on tutorials by default.
   */

  private String[] readAllowedGroupNames = new String[]
  {
    UserGroupName.ADMINS,
    UserGroupName.LECTURERS,
    UserGroupName.TUTORS,
  };

  /**
   * Groups which have write permission on tutorials by default.
   */

  private String[] writeAllowedGroupNames = new String[]
  {
    UserGroupName.ADMINS,
  };


  /**
   * The path tokenizer used by this instance.
   */

  protected PathTokenizer pathTokenizer = new PathTokenizer();

  /**
   * See class description.
   */

  public Map act (Redirector redirector,
                  SourceResolver resolver,
                  Map objectModel,
                  String source, 
                  Parameters parameters)
    throws ProcessingException 
  {
    final String METHOD_NAME = "act";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");

    try
      {
        // Add request parameters to action parameters:
        Request request = ObjectModelHelper.getRequest(objectModel);
        ParamUtil.addRequestParams(parameters, request, true);

        // Init sitemap parameters to return:
        Map sitemapParams = new HashMap();

        int formStage = ParamUtil.getAsInt(parameters, "form-stage", 0);
        switch ( formStage )
          {
          case 0:
            // Do nothing, only inform sitemap to generate form and set the tutorial:
            int tutorialId = ParamUtil.getAsInt(parameters, "id", -1);
            sitemapParams.put("mode", "form");
            sitemapParams.put("tutorial", Integer.toString(tutorialId));
            break;
          case 1:
            // Create or modify tutorial:
            this.createOrModifyTutorial(parameters, sitemapParams);
            break;
          default:
            throw new IllegalArgumentException("Illegal form-stage value: " + formStage);
          }

        this.getLogger().debug(METHOD_NAME + " 2/2: Done");

        return sitemapParams;
      }
    catch (Exception exception)
      {
        throw new ProcessingException(exception);
      }
  }

  /**
   * 
   */

  protected void createOrModifyTutorial (Parameters params, Map sitemapParams)
    throws ProcessingException
  {
    final String METHOD_NAME = "createOrModifyTutorial";
    this.getLogger().debug(METHOD_NAME + " 1/3: Started");

    DbHelper dbHelper = null;

    try 
      {
        // Init services:
        dbHelper = (DbHelper)this.manager.lookup(DbHelper.ROLE);

        // Get data from parameters:
        int tutorialId = ParamUtil.getAsInt(params, "id", Id.UNDEFINED);
        String name = ParamUtil.getAsString(params, "name", null);
        String description = ParamUtil.getAsString(params, "description", null);
        int classId = ParamUtil.getAsId(params, "class");
        int tutorId = ParamUtil.getAsInt(params, "tutor", Id.UNDEFINED);
        String capacityStr = ParamUtil.getAsString(params, "capacity", "unlimited");

        // Indicates that a new tutorial is created:
        boolean newTutorial = ( tutorialId == Id.UNDEFINED );

        // If an existing tutorial is modified, the class must remain the same:
        if ( !newTutorial &&
             dbHelper.getPseudoDocDatumAsInt(PseudoDocType.TUTORIAL, tutorialId, DbColumn.CLASS)
             != classId )
          throw new IllegalArgumentException("Tutorial does not belong to the specified class");

        this.getLogger().debug
          (METHOD_NAME + " 2/3:" +
           " tutorialId = " + tutorialId +
           ", name = " + name +
           ", description = " + description +
           ", classId = " + classId +
           ", tutorId = " + tutorId +
           ", capacityStr = " + capacityStr);

        try
          {
            // Check if name is set:
            if ( name == null )
              throw new UserInputException("name-missing");

            // Create pure name:
            String pureName = (name != null ? DocUtil.nameToPureName("tut", name) : "");

            // Get section:
            String sectionPath = TutorialUtil.getDefaultSectionPath(classId, dbHelper, this.pathTokenizer);
            Integer sectionId = dbHelper.getSectionIdForPath(sectionPath);

            // Get capacity:
            int capacity = -1;
            if ( !capacityStr.equals("unlimited") )
              {
                try
                  {
                    capacity = Integer.parseInt(capacityStr);
                  }
                catch (NumberFormatException exception)
                  {
                    throw new UserInputException("invalid-capacity-value", exception);
                  }
                if ( capacity < 0 )
                  throw new UserInputException("negative-capacity");
              }

            // Map for the tutorial data:
            Map<String,Object> data = new HashMap<String,Object>();
            data.put(DbColumn.NAME, name);
            if ( description != null ) data.put(DbColumn.DESCRIPTION, description);
            data.put(DbColumn.CLASS, new Integer(classId));
            if ( tutorId != Id.UNDEFINED ) data.put(DbColumn.TUTOR, new Integer(tutorId));
            data.put(DbColumn.PURE_NAME, pureName);
            data.put(DbColumn.CONTAINED_IN, new Integer(sectionId));
            if ( capacity != -1 ) data.put(DbColumn.CAPACITY, new Integer(capacity));

            // Create or update tutorial:
            if ( newTutorial )
              tutorialId = dbHelper.storePseudoDocData(PseudoDocType.TUTORIAL, data);
            else
              dbHelper.updatePseudoDocData(PseudoDocType.TUTORIAL, tutorialId, data);

            // Read permissions:
            dbHelper.removeAllReadPermissionsForPseudoDoc(PseudoDocType.TUTORIAL, tutorialId);
            for (String readAllowedGroupName : readAllowedGroupNames)
              dbHelper.storePseudoDocumentReadPermission
                (PseudoDocType.TUTORIAL, tutorialId, readAllowedGroupName);

            // Write permissions:
            dbHelper.removeAllWritePermissionsForPseudoDoc(PseudoDocType.TUTORIAL, tutorialId);
            for (String writeAllowedGroupName : writeAllowedGroupNames)
              dbHelper.storePseudoDocWritePermission
                (PseudoDocType.TUTORIAL, tutorialId, writeAllowedGroupName);

            sitemapParams.put("mode", "feedback");
            sitemapParams.put("performed-task", newTutorial ? "created-tutorial" : "modified-tutorial");
          }
        catch (UserInputException exception)
          {
            sitemapParams.put("error", exception.getMessage());
            sitemapParams.put("mode", "form");
          }

        sitemapParams.put("tutorial", Integer.toString(tutorialId));

        this.getLogger().debug(METHOD_NAME + " 3/3: Done");
      }
    catch (Exception exception)
      {
        throw new ProcessingException(exception);
      }
    finally
      {
        if ( dbHelper != null ) this.manager.release(dbHelper);
      }
  }
}