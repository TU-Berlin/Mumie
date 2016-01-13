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

package net.mumie.cocoon.generators;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import net.mumie.cocoon.checkin.CheckinHelper;
import net.mumie.cocoon.checkin.Content;
import net.mumie.cocoon.checkin.EditableCheckinRepository;
import net.mumie.cocoon.checkin.EditableMaster;
import net.mumie.cocoon.checkin.Master;
import net.mumie.cocoon.checkin.SimpleEditableMaster;
import net.mumie.cocoon.checkin.StringContent;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.Category;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.FileRole;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.MediaType;
import net.mumie.cocoon.notions.Nature;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.TimeFormat;
import net.mumie.cocoon.notions.UseMode;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.util.ParamUtil;
import net.mumie.cocoon.util.PathTokenizer;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;

/**
 * Creates a wrapper course with a wrapper worksheet for a given (generic) problem.
 * Recognizes the following parameters, which may be set as request parameters or in the
 * sitemap:
 * <table class="genuine indented">
 *   <thead>
 *     <tr>
 *       <td>Name</td>
 *       <td>Description</td>
 *       <td>Required</td>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td style="width:12em"><code>generic-problem-id</code></td>
 *       <td>Id of the generic problem</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr>
 *       <td><code>class-id</code></td>
 *       <td>Id of the teaching class the course is assigned to</td>
 *       <td rowspan="2" style="vertical-align:middle">One of the both</td>
 *     </tr>
 *     <tr>
 *       <td><code>class-sync-id</code></td>
 *       <td>The sync id of the teaching class the course is assigned to</td>
 *     </tr>
 *     <tr>
 *       <td><code>timeframe-start</code></td>
 *       <td>Start of the worksheet timeframe (format s.b.)</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr>
 *       <td><code>timeframe-end</code></td>
 *       <td>End of the worksheet timeframe (format s.b.)</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr>
 *       <td><code>timeframe-format</code></td>
 *       <td>Format of <em>timeframe-start</em> and <em>timeframe-end</em></td>
 *       <td>No. Default is {@link TimeFormat#DEFAULT TimeFormat.DEFAULT}</td>
 *     </tr>
 *     <tr>
 *       <td><code>points</code></td>
 *       <td>Maximum number of points for the problem</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr>
 *       <td><code>category</code></td>
 *       <td>Category of the worksheet, as numerical code</td>
 *       <td rowspan="2" style="vertical-align:middle">None or one of the both.
 *         Default is {@link Category#HOMEWORK HOMEWORK}</td>
 *     </tr>
 *     <tr>
 *       <td><code>category-name</code></td>
 *       <td>Category of the worksheet, as string name</td>
 *     </tr>
 *     <tr>
 *       <td><code>name</code></td>
 *       <td>Name of both the course and the worksheet</td>
 *       <td>No. Default is the empty string</td>
 *     </tr>
 *     <tr>
 *       <td><code>description</code></td>
 *       <td>Description of both the course and the worksheet</td>
 *       <td>No. Default is the empty string</td>
 *     </tr>
 *     <tr>
 *       <td><code>section</code></td>
 *       <td>Path of the section where the course and worksheet are stored</td>
 *       <td>No. Default is <code><var>parent</var> + "/courses/problem_wrappers"</code>
 *         where <var>parent</var> is the parent of the section of the teaching class</td>
 *     </tr>
 *     <tr>
 *       <td><code>pure-name-base</code></td>
 *       <td>Pure name of the course and worksheet without the type indicators
 *         <code>"wks_"</code> resp. <code>"crs_"</code></td>
 *       <td>No. Default is thr pure name of the generic problem with the type indicator
 *        <code>"g_prb_"</code> removed</td>
 *     </tr>
 *   </tbody>
 * </table>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: CreateProblemWrapperGenerator.java,v 1.4 2009/10/02 22:21:08 rassy Exp $</code>
 */

public class CreateProblemWrapperGenerator extends ServiceableJapsGenerator 
{
  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus =
    new ServiceStatus(CreateProblemWrapperGenerator.class);

  /**
   * Full suffix (role and type) of master files, including leading dot.
   */

  protected static final String MASTER_SUFFIX =
    "." + FileRole.MASTER_SUFFIX + "." + MediaType.TEXT_XML_SUFFIX;

  /**
   * Full suffix (role and type) of XML content files, including leading dot.
   */

  protected static final String CONTENT_SUFFIX =
    "." + FileRole.CONTENT_SUFFIX + "." + MediaType.TEXT_XML_SUFFIX;

  /**
   * Lid of the wrapper worksheet in the course.
   */

  protected static final String WORKSHEET_LID = "wks";

  /**
   * Lid of the generic problem in the worksheet.
   */

  protected static final String GENERIC_PROBLEM_LID = "prb";

  /**
   * The path tokenizer of this instance.
   */

  protected PathTokenizer pathTokenizer = new PathTokenizer();

  /**
   * Creates a new <code>CreateProblemWrapperGenerator</code> instance.
   */

  public CreateProblemWrapperGenerator ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Recycles this instance. Essentially, this method only calls the superclass recycle
   * method. Besides this, the instance status id notified about the recycle, and log
   * messages are written.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    super.recycle();
    this.instanceStatus.notifyRecycle();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Disposes this instance. Essentially, this method only calls the superclass dispose
   * method. Besides this, the instance status id notified about the dispose, and log
   * messages are written.
   */

  public void dispose ()
  {
    final String METHOD_NAME = "dispose";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    super.dispose();
    this.instanceStatus.notifyDispose();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Generates the XML. See class documentation for details.
   */

  public void generate ()
    throws ProcessingException 
  {
    final String METHOD_NAME = "generate";
    this.logDebug(METHOD_NAME + " 1/3: Started");

    DbHelper dbHelper = null;
    EditableCheckinRepository checkinRepository = null;
    CheckinHelper checkinHelper = null;

    try
      {
        // Init services:
        dbHelper =
          (DbHelper)this.serviceManager.lookup(DbHelper.ROLE);
        checkinRepository =
          (EditableCheckinRepository)this.serviceManager.lookup(EditableCheckinRepository.ROLE);
        checkinHelper =
          (CheckinHelper)this.serviceManager.lookup(CheckinHelper.ROLE);

        // Add request parameters to generator parameters:
        Request request = ObjectModelHelper.getRequest(objectModel);
        ParamUtil.addRequestParams(this.parameters, request, true);

        // Get input data:
        int genericProblemId = ParamUtil.getAsId(this.parameters, "generic-problem-id");
        int classId = ParamUtil.getAsInt(this.parameters, "class-id", Id.UNDEFINED);
        String classSyncId = ParamUtil.getAsString(this.parameters, "class-sync-id", null);
        DateFormat dateFormat = ParamUtil.getAsDateFormat
          (this.parameters, "time-format", TimeFormat.DEFAULT);
        String timeframeStartStr = ParamUtil.getAsString(this.parameters, "timeframe-start");
        String timeframeEndStr = ParamUtil.getAsString(this.parameters, "timeframe-end");
        long timeframeStart = dateFormat.parse(timeframeStartStr).getTime();
        long timeframeEnd = dateFormat.parse(timeframeEndStr).getTime();
        int points = ParamUtil.getAsInt(this.parameters, "points");
        int worksheetCategory = ParamUtil.getAsCategory
          (this.parameters, "category", "category-name", Category.HOMEWORK);
        String name = ParamUtil.getAsString(this.parameters, "name", "");
        String description = ParamUtil.getAsString(this.parameters, "description", "");
        String sectionPath = ParamUtil.getAsString(this.parameters, "section", null);
        String pureNameBase = ParamUtil.getAsString(this.parameters, "pure-name-base", null);

        // Get pure name and section path of the generic Problem:
        ResultSet genericProblemData = this.getGenericProblemData(genericProblemId, dbHelper);
        String genericProblemPureName = genericProblemData.getString(DbColumn.PURE_NAME);
        String genericProblemSectionPath = genericProblemData.getString(DbColumn.SECTION_PATH);

        // Get class id from class sync id if necessary:
        if ( classId == Id.UNDEFINED )
          {
            if ( classSyncId == null )
              throw new IllegalArgumentException("No class specified");
            classId = this.getClassId(classSyncId, dbHelper);
          }

        // Set section path if necessary:
        if ( sectionPath == null )
          {
            String classSectionPath = this.getClassSectionPath(classId, dbHelper);
            this.pathTokenizer.tokenize(classSectionPath);
            sectionPath = this.pathTokenizer.getSectionPath() + "/courses/problem_wrappers";
          }

        // Set pure name base if necessary:
        if ( pureNameBase == null )
          {
            pureNameBase = genericProblemPureName;
            if ( pureNameBase.startsWith("g_prb_") )
              pureNameBase = pureNameBase.substring("g_prb_".length());
          }

        this.logDebug
          (METHOD_NAME + " 2/3:" +
           " genericProblemId = " + genericProblemId +
           ", genericProblemPureName = " + genericProblemPureName +
           ", genericProblemSectionPath = " + genericProblemSectionPath +
           ", classId = " + classId +
           ", classSyncId = " + classSyncId +
           ", timeframeStartStr = " + timeframeStartStr +
           ", timeframeEndStr = " + timeframeEndStr +
           ", points = " + points +
           ", worksheetCategory = " + worksheetCategory +
           ", name = " + name +
           ", description = " + description +
           ", sectionPath = " + sectionPath +
           ", pureNameBase = " + pureNameBase);

        // Setup section master:
        EditableMaster sectionMaster = null;
        if ( !this.sectionExists(sectionPath, dbHelper) )
          {
            sectionMaster = new SimpleEditableMaster();
            sectionMaster.setNature(Nature.PSEUDO_DOCUMENT);
            sectionMaster.setType(PseudoDocType.SECTION);
            sectionMaster.setName("Problem wrappers");
            sectionMaster.setDescription("Wrapper courses for problems");
          }

        // Workshee and course paths:
        String worksheetPath = sectionPath + "/" + "wks_" + pureNameBase + "_wrapper";
        String coursePath = sectionPath + "/" + "crs_" + pureNameBase + "_wrapper";

        // Setup worksheet master:
        EditableMaster worksheetMaster = new SimpleEditableMaster();
        worksheetMaster.setNature(Nature.DOCUMENT);
        worksheetMaster.setType(DocType.WORKSHEET);
        worksheetMaster.setIsWrapper(true);
        worksheetMaster.setCategory(worksheetCategory);
        worksheetMaster.setName(name);
        worksheetMaster.setDescription(description);
        worksheetMaster.setTimeframeStart(timeframeStart);
        worksheetMaster.setTimeframeEnd(timeframeEnd);
        worksheetMaster.setContentType(MediaType.TEXT_XML);

        // Setup worksheet content:
        Content worksheetContent = new StringContent
          ("<?xml version=\"1.0\" encoding=\"ASCII\"?>" +
           "<crs:worksheet" +
           " xmlns:crs=\"http://www.mumie.net/xml-namespace/document/content/worksheet\"" +
           " arrange=\"list\"" +
           " category=\"" + Category.nameFor(worksheetCategory) + "\">" +
           "<crs:list>" +
           "<crs:generic_problem lid=\"" + GENERIC_PROBLEM_LID + "\"/>" +
           "</crs:list>" +
           "</crs:worksheet>");

        // Setup course master:
        EditableMaster courseMaster = new SimpleEditableMaster();
        courseMaster.setNature(Nature.DOCUMENT);
        courseMaster.setType(DocType.COURSE);
        courseMaster.setIsWrapper(true);
        courseMaster.setName(name);
        courseMaster.setDescription(description);
        courseMaster.setContentType(MediaType.TEXT_XML);

        // Setup course content:
        Content courseContent = new StringContent
          ("<?xml version=\"1.0\" encoding=\"ASCII\"?>" +
           "<crs:course" +
           " xmlns:crs=\"http://www.mumie.net/xml-namespace/document/content/course\"" +
           " arrange=\"list\">" +
           "<crs:list>" +
           "<crs:worksheet lid=\"" + WORKSHEET_LID + "\"/>" +
           "</crs:list>" +
           "</crs:course>");

        // Setup course->worksheet reference target:
        EditableMaster worksheetComponent = new SimpleEditableMaster();
        worksheetComponent.setNature(Nature.DOCUMENT);
        worksheetComponent.setType(DocType.WORKSHEET);
        worksheetComponent.setPath(worksheetPath + MASTER_SUFFIX);
        worksheetComponent.setLid(WORKSHEET_LID);

        // Setup worksheet->generic_document reference target:
        EditableMaster genericProblemComponent = new SimpleEditableMaster();
        genericProblemComponent.setNature(Nature.DOCUMENT);
        genericProblemComponent.setType(DocType.GENERIC_PROBLEM);
        genericProblemComponent.setPath
          (genericProblemSectionPath + "/" + genericProblemPureName + MASTER_SUFFIX);
        genericProblemComponent.setLid(GENERIC_PROBLEM_LID);

        // Add references to masters:
        courseMaster.addComponent(worksheetComponent);
        worksheetMaster.addComponent(genericProblemComponent);

        // Setup checkin repository:
        if ( sectionMaster != null )
          checkinRepository.addMaster(sectionPath + "/" + MASTER_SUFFIX, sectionMaster);
        checkinRepository.addMaster(coursePath + MASTER_SUFFIX, courseMaster);
        checkinRepository.addContent(coursePath + CONTENT_SUFFIX, courseContent);
        checkinRepository.addMaster(worksheetPath + MASTER_SUFFIX, worksheetMaster);
        checkinRepository.addContent(worksheetPath + CONTENT_SUFFIX, worksheetContent);

        // Checkin:
        checkinHelper.checkin(checkinRepository);

        // Write report:
        checkinHelper.toSAX(this.contentHandler);

        this.getLogger().debug(METHOD_NAME + " 3/3: Done");
      }
    catch (Exception exception)
      {
        throw new ProcessingException(exception);
      }
    finally
      {
        if ( checkinHelper != null ) this.serviceManager.release(checkinHelper);
        if ( checkinRepository != null ) this.serviceManager.release(checkinRepository);
        if ( dbHelper != null ) this.serviceManager.release(dbHelper);
      }
  }

  /**
   * Returns the id of the class with the specified sync id.
   */

  protected int getClassId (String syncId, DbHelper dbHelper)
    throws SQLException
  {
    ResultSet resultSet = dbHelper.queryPseudoDocDatumBySyncId
      (PseudoDocType.CLASS, syncId, DbColumn.ID);
    if ( !resultSet.next() )
      throw new SQLException("Cannot find class with sync id \"" + syncId + "\"");
    return resultSet.getInt(DbColumn.ID);
  }

  /**
   * Returns a result set with the pure name and the section path of the generic problem
   * with the specified id.
   */

  protected ResultSet getGenericProblemData  (int id, DbHelper dbHelper)
    throws Exception
  {
    ResultSet resultSet = dbHelper.queryData
      (DocType.GENERIC_PROBLEM, id,
       new String[] {DbColumn.PURE_NAME, DbColumn.SECTION_PATH});
    if ( !resultSet.next() )
      throw new SQLException("Cannot find generic problem with id " + id);
    return resultSet;
  }

  /**
   * Returns the section path for the specified class.
   */

  protected String getClassSectionPath (int classId, DbHelper dbHelper)
    throws Exception
  {
    ResultSet resultSet = dbHelper.queryPseudoDocDatum
      (PseudoDocType.CLASS, classId, DbColumn.SECTION_PATH);
    if ( !resultSet.next() )
      throw new SQLException("Cannot find class with id " + classId);
    return resultSet.getString(DbColumn.SECTION_PATH);
  }

  /**
   * Returns true if the section with the specified path exists, false otherwise.
   */

  protected boolean sectionExists (String path, DbHelper dbHelper)
    throws SQLException
  {
    ResultSet resultSet = dbHelper.queryPseudoDocDataByPath
      (PseudoDocType.SECTION, path, new String[] {DbColumn.ID});
    return resultSet.next();
  }

  /**
   * Returns a string that identificates this <code>CreateProblemWrapperGenerator</code>
   * instance. It has the following form:<pre>
   *   "CreateProblemWrapperGenerator" +
   *   '#' + instanceId
   *   '(' + numberOfRecycles
   *   ')'</pre>
   * where <code>instanceId</code> is the instance id and
   * <code>numberOfRecycles</code> the number of recycles of this instance.
   */

  public String getIdentification ()
  {
    return
      "CreateProblemWrapperGenerator" +
      '#' + this.instanceStatus.getInstanceId() +
      '(' + this.instanceStatus.getNumberOfRecycles() +
      ')';
  }
}