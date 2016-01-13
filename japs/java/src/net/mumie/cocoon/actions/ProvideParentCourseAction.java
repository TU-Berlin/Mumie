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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.DocType;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;
import net.mumie.cocoon.notions.RefType;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.util.ParamUtil;

/**
 * <p>
 *   Provides the id of the parent course for a given course section or
 *   worksheet (the "child"). Recognizes the following paramters:
 * </p>
 * <table class="genuine indented" style="width:60em">
 *   <thead>
 *     <tr>
 *       <td>Name</td>
 *       <td>Description</td>
 *       <td>Required</td>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td><code>child-type</code></td>
 *       <td>The document type of the child, as numerical code</td>
 *       <td rowspan="2">One of the both</td>
 *     </tr>
 *     <tr>
 *       <td><code>child-type-name</code></td>
 *       <td>The document type of the child, as string name</td>
 *     </tr>
 *     <tr>
 *       <td><code>child-id</code></td>
 *       <td>The id of the child</td>
 *       <td>Yes</td>
 *     </tr>
 *   </tbody>
 * </table>
 * <p>
 *   The document type specified by <code>child-type</code> or <code>child-type-name</code>
 *   must be {@link DocType.COURSE_SECTION COURSE_SECTION} or
 *   {@link DocType.WORKSHEET WORKSHEET}. Only one of
 *   <code>child-type</code> or <code>child-type-name</code> should be set. If both are set,
 *   <code>child-type</code> takes precedence.
 * </p>
 * <p>
 *   Sets the following sitemap parameters:
 * </p>
 * <table class="genuine indented" style="width:40em">
 *   <thead>
 *     <tr>
 *       <td>Name</td>
 *       <td>Description</td>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td><code>parent-id</code></td>
 *       <td>The Id of the parent course</td>
 *     </tr>
 *     <tr>
 *       <td><code>child-type</code></td>
 *       <td>The type of the child, as numerical code</td>
 *     </tr>
 *     <tr>
 *       <td><code>child-id</code></td>
 *       <td>The id of the child</td>
 *     </tr> 
 *     <tr>
 *       <td><code>parent-class-id</code></td>
 *       <td>The id of the parent class</td>
 *     </tr>
 *   </tbody>
 * </table>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: ProvideParentCourseAction.java,v 1.10 2009/06/15 13:55:35 linges Exp $</code>
 */

public class ProvideParentCourseAction extends ServiceableAction
  implements Recyclable
{
  /**
   * The db helper used by this action.
   */

  protected DbHelper dbHelper = null;

  /**
   * Returns the child document type ({@link DocType.COURSE_SECTION COURSE_SECTION} or
   * {@link DocType.WORKSHEET WORKSHEET}) from the parameters.
   *
   * @throws ParameterException if the type is neither
   * {@link DocType.COURSE_SECTION COURSE_SECTION} nor
   * {@link DocType.WORKSHEET WORKSHEET}, or if an error occurs while
   * accessing the parameters.
   */

  protected int getChildType (Parameters parameters)
    throws ParameterException
  {
    final String METHOD_NAME = "getChildType";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");

    final String CHILD_TYPE = "child-type";
    final String CHILD_TYPE_NAME = "child-type-name";

    // Get type from parameters:
    int type;
    if ( ParamUtil.checkIfSet(parameters, CHILD_TYPE) )
      {
        type = parameters.getParameterAsInteger(CHILD_TYPE);
        if ( !DocType.exists(type) )
          throw new ParameterException
            ("Invalid document type code: " + type);
      }
    else if ( ParamUtil.checkIfSet(parameters, CHILD_TYPE_NAME) )
      {
        String typeName = parameters.getParameter(CHILD_TYPE_NAME);
        type = DocType.codeFor(typeName);
        if ( type == DocType.UNDEFINED )
          throw new ParameterException
            ("Invalid document type name: " + typeName);
      }
    else
      throw new ParameterException("Missing document type");

    // Check if type is course_section or worksheet:
    if ( ! ( type == DocType.COURSE_SECTION ||
             type == DocType.WORKSHEET ) )
      throw new ParameterException
        ("Invalid document type: " + type + " (expected " +
         DocType.COURSE_SECTION + " or " + DocType.WORKSHEET + ")");

    this.getLogger().debug(METHOD_NAME + " 2/2: Done. type = " + type);
    return type;
  }

  /**
   * Returns the child id from the parameters.
   */

  protected int getChildId (Parameters parameters)
    throws ParameterException
  {
    final String METHOD_NAME = "getChildId";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");

    final String CHILD_ID = "child-id";

    int id;
    if ( ParamUtil.checkIfSet(parameters, CHILD_ID) )
      id = parameters.getParameterAsInteger(CHILD_ID);
    else
      throw new ParameterException(METHOD_NAME + ": Missing document id");

    this.getLogger().debug(METHOD_NAME + " 2/2: Done. id = " + id);
    return id;
  }

  /**
   * Retrieves the id of the course from the database.
   */

  protected ResultSet getParentData (int childType, int childId)
    throws SQLException, ProcessingException 
  {
    final String METHOD_NAME = "getParentData(int childType, int childId)";
    this.getLogger().debug
      (METHOD_NAME + " 1/3: Started." +
       " childType = " + childType +
       ", childId = " + childId);
    ResultSet resultSet = this.dbHelper.queryReferencingDocuments
      (childType,
       childId,
       DocType.COURSE,
       RefType.COMPONENT,
       new String[] {DbColumn.ID, DbColumn.CLASS},
       true);
    if ( !resultSet.next() )
      throw new ProcessingException
        ("No " + DocType.nameFor[DocType.COURSE] +
         " for " + DocType.nameFor[childType] +
         ": " + childId + " (result set empty)");

    this.getLogger().debug(METHOD_NAME + " 2/2: parentId = " + resultSet.getInt(DbColumn.ID) 
        + "classId = " + resultSet.getInt(DbColumn.CLASS));
    return resultSet;
  }

  /**
   * See class description.
   */

  public Map act (Redirector redirector, SourceResolver resolver, Map objectModel, String source, 
                  Parameters parameters)
    throws ProcessingException
  {
    final String METHOD_NAME = "act";
    this.getLogger().debug(METHOD_NAME + " 1/3: Started");
    try
      {
        // Init db helper:
        this.dbHelper = (DbHelper)this.manager.lookup(DbHelper.ROLE);

        // Get the type of the child:
        int childType = this.getChildType(parameters);

        // Get the id of the child:
        int childId = this.getChildId(parameters);
     
        ResultSet parentData = this.getParentData(childType, childId);
        
        // Get the parent id:
        int parentId = parentData.getInt(DbColumn.ID);
        
        // Get the class id
        int classId = parentData.getInt(DbColumn.CLASS);

        this.getLogger().debug
          (METHOD_NAME + " 2/3:" +
           " childType = " + childType +
           ", childId = " + childId +
           ", parentId = " + parentId);

        // Return sitemap parameters:
        Map sitemapParameters = new HashMap();
        sitemapParameters.put("child-type", Integer.toString(childId));
        sitemapParameters.put("child-id", Integer.toString(childId));
        sitemapParameters.put("parent-id", Integer.toString(parentId));
        sitemapParameters.put("parent-class-id", Integer.toString(classId));
        this.getLogger().debug(METHOD_NAME + " 3/3: Done");
        return sitemapParameters;
      }    
    catch  (Exception exception)
      {
	throw new ProcessingException(exception);
      }
  }

  /**
   * Recycles this action.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");
    if ( this.dbHelper != null )
      {
        this.manager.release(this.dbHelper);
        this.dbHelper = null;
      }
    this.getLogger().debug(METHOD_NAME + " 2/2: Done");
  }
}
