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
import java.util.Map;
import java.util.HashMap;

import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.documents.Document;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.SessionAttrib;
import net.mumie.cocoon.util.ParamUtil;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.webapps.session.SessionManager;
import net.mumie.cocoon.notions.Id;

/**
 * <p>
 * Set the session attribute class. Expects one of the following paramters:
 * </p>
 * <table class="genuine indented" style="width:60em">
 * <thead>
 * <tr>
 * <td>Name</td>
 * <td>Description</td>
 * <td>Required</td>
 * </tr>
 * </thead> <tbody>
 * <tr>
 * <td><code>course</code></td>
 * <td>Id of the course</td>
 * <td>No</td>
 * </tr>
 * <tr>
 * <td><code>worksheet</code></td>
 * <td>Id of the {@link DocType#WORKSHEET WORKSHEET}.</td>
 * <td>No</td>
 * </tr>
 * <tr>
 * <td><code>class</code></td>
 * <td>Id of class</td>
 * <td>No</td>
 * </tr>
 * <tr>
 * <td><code>ref</code></td>
 * <td>Id of the {@link DocType#WORKSHEET WORKSHEET}&ndash;
 * {@link DocType#PROBLEM PROBLEM} reference. This specifies the
 * {@link DocType#WORKSHEET WORKSHEET}, too.</td>
 * <td>No</td>
 * </tr>
 * </tbody>
 * </table>
 * <p>
 * If the parameter class = clear or a parameter is wrong the session attribute
 * will be removed.
 * </p>
 * <p>
 * 
 * 
 * 
 */

public class UpdateClassAction extends ServiceableAction
{
  public Map act(Redirector redirector, SourceResolver resolver,
		 Map objectModel, String source, Parameters parameters)
    throws ProcessingException
  {

    // Some constants:
    final String CLASS = "class";
    final String COURSE = "course";
    final String WORKSHEET = "worksheet";
    final String REF = "ref";
    final String METHOD_NAME = "act";

    this.getLogger().debug(METHOD_NAME + " 1/3: Started");

    DbHelper dbHelper = null;
    SessionManager sessionManager = null;

    try
      {
	// Init db helper:
	dbHelper = (DbHelper) this.manager.lookup(DbHelper.ROLE);

	// Init session
	sessionManager = (SessionManager)this.manager.lookup(SessionManager.ROLE);
	Session session = sessionManager.getSession(true);

	// Init class id. The undefined value means: clear session attribute.
	Integer classId = null;
 
	// Set the class id accoring to the parameters:
	if ( ParamUtil.checkIfSet(parameters, CLASS) )
	  {
	    if ( !ParamUtil.getAsString(parameters, CLASS).equals("clear") )
	      classId = ParamUtil.getAsInt(parameters, CLASS);
	    else 
	      classId = Id.UNDEFINED;
	  }
	else if ( ParamUtil.checkIfSet(parameters, COURSE) )
	  {
	    int courseId = ParamUtil.getAsInt(parameters, COURSE);
	    ResultSet resultSet =
	      dbHelper.queryData(DocType.COURSE, courseId, new String[] {DbColumn.CLASS});
	    if ( !resultSet.next() )
	      throw new IllegalArgumentException("Course does not exist");
	    classId = resultSet.getInt(DbColumn.CLASS);
	    if ( resultSet.wasNull() )
	      classId = Id.UNDEFINED;
	  }
	else if ( ParamUtil.checkIfSet(parameters, WORKSHEET))
	  {
	    int worksheetId = ParamUtil.getAsInt(parameters, WORKSHEET);
	    classId = dbHelper.getClassForWorksheetById(worksheetId);
	  }
	else if ( ParamUtil.checkIfSet(parameters, REF))
	  {
	    int refId = ParamUtil.getAsInt(parameters, REF);
	    classId = dbHelper.getClassForProblemRefId(refId);
	  }

	if ( classId != null )
	  {
	    // Set or remove session attribute:
	    if ( classId == Id.UNDEFINED )
	      {
		session.removeAttribute(SessionAttrib.CLASS);
		this.getLogger().debug(
				       METHOD_NAME + " 2/3: session attribute \"class\" removed");
	      }
	    else
	      {
		session.setAttribute(SessionAttrib.CLASS, classId);
		this.getLogger().debug(METHOD_NAME + " 2/3: class = " + classId);
	      }
	  }
	else
	  this.getLogger().debug(METHOD_NAME + " 2/3: session attribute \"class\" not altered");
	
	this.getLogger().debug(METHOD_NAME + " 3/3: Done");
	return EMPTY_MAP;
      }

    catch (Exception exception)
      {
	throw new ProcessingException(exception);
      }
    finally
      {
	if ( dbHelper != null ) this.manager.release(dbHelper);
	if ( sessionManager != null ) this.manager.release(sessionManager);
      }

  }
}
