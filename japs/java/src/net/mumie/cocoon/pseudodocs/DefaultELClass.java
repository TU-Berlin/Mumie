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

package net.mumie.cocoon.pseudodocs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.UseMode;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import org.apache.avalon.framework.service.ServiceException;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import net.mumie.cocoon.service.LookupNotifyable;

/**
 * Default implementation of {@link ELClass}.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: DefaultELClass.java,v 1.20 2009/05/22 10:29:03 linges Exp $</code>
 */

public class DefaultELClass extends ExternallyControlledPseudoDocument
  implements ELClass, LookupNotifyable 
{
  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(DefaultELClass.class);

  /**
   * Returns the database columns this pseudo-document needs if one of the
   * <code>toSAX</code> methods would be called. The colums are:
   * <ul>
   *   <li>
   *     In use mode {@link UseMode#COMPONENT COMPONENT}:
   *     <ul>
   *       <li>{@link DbColumn#ID ID}</li>
   *       <li>{@link DbColumn#SYNC_ID SYNC_ID}</li>
   *       <li>{@link DbColumn#NAME NAME}</li>
   *       <li>{@link DbColumn#DESCRIPTION DESCRIPTION}</li>
   *       <li>{@link DbColumn#THEME THEME}</li>
   *     </ul>
   *   </li>
   *   <li>
   *     In use mode {@link UseMode#LINK LINK}:
   *     <ul>
   *       <li>{@link DbColumn#ID ID}</li>
   *       <li>{@link DbColumn#NAME NAME}</li>
   *       <li>{@link DbColumn#DESCRIPTION DESCRIPTION}</li>
   *     </ul>
   *   </li>
   *   <li>
   *     In use mode {@link UseMode#SERVE SERVE}:
   *     <ul>
   *       <li>{@link DbColumn#ID ID}</li>
   *       <li>{@link DbColumn#SYNC_ID SYNC_ID}</li>
   *       <li>{@link DbColumn#NAME NAME}</li>
   *       <li>{@link DbColumn#DESCRIPTION DESCRIPTION}</li>
   *       <li>{@link DbColumn#SEMESTER SEMESTER}</li>
   *       <li>{@link DbColumn#THEME THEME}</li>
   *       <li>{@link DbColumn#CREATED CREATED}</li>
   *       <li>{@link DbColumn#LAST_MODIFIED LAST_MODIFIED}</li>
   *     </ul>
   *   </li>
   *   <li>
   *     In use mode {@link UseMode#INFO INFO}:
   *     <ul>
   *       <li>{@link DbColumn#ID ID}</li>
   *       <li>{@link DbColumn#SYNC_ID SYNC_ID}</li>
   *       <li>{@link DbColumn#NAME NAME}</li>
   *       <li>{@link DbColumn#DESCRIPTION DESCRIPTION}</li>
   *       <li>{@link DbColumn#SEMESTER SEMESTER}</li>
   *       <li>{@link DbColumn#THEME THEME}</li>
   *       <li>{@link DbColumn#CREATED CREATED}</li>
   *       <li>{@link DbColumn#LAST_MODIFIED LAST_MODIFIED}</li>
   *     </ul>
   *   </li>
   * </ul>
   */

  public String[] getDbColumns ()
  {
    if ( this.withPath )
      switch ( this.useMode )
	{
	case UseMode.COMPONENT :
	  return new String []
	    {
	      DbColumn.ID,
	      DbColumn.SYNC_ID,
	      DbColumn.SECTION_PATH,
	      DbColumn.PURE_NAME,
	      DbColumn.NAME,
	      DbColumn.DESCRIPTION,
	      DbColumn.THEME,
	    };
	case UseMode.LINK :
	  return new String []
	    {
	      DbColumn.ID,
	      DbColumn.SECTION_PATH,
	      DbColumn.PURE_NAME,
	      DbColumn.NAME,
	      DbColumn.DESCRIPTION,
	    };
	case UseMode.SERVE :
	  return new String []
	    {
	      DbColumn.ID,
	      DbColumn.SYNC_ID,
	      DbColumn.SECTION_PATH,
	      DbColumn.PURE_NAME,
	      DbColumn.NAME,
	      DbColumn.DESCRIPTION,
	      DbColumn.SEMESTER,
	      DbColumn.THEME,
	      DbColumn.CREATED,
	      DbColumn.LAST_MODIFIED,
	    };
	case UseMode.INFO :
	  return new String []
	    {
	      DbColumn.ID,
	      DbColumn.CONTAINED_IN,
	      DbColumn.SECTION_PATH,
	      DbColumn.PURE_NAME,
	      DbColumn.SYNC_ID,
	      DbColumn.NAME,
	      DbColumn.DESCRIPTION,
	      DbColumn.SEMESTER,
	      DbColumn.THEME,
	      DbColumn.CREATED,
	      DbColumn.LAST_MODIFIED,
	    };
	case UseMode.CHECKOUT :
	  return new String[]
	    {
	      DbColumn.ID,
	      DbColumn.SECTION_PATH,
	      DbColumn.PURE_NAME,
	      DbColumn.SYNC_ID,
	      DbColumn.DESCRIPTION,
	      DbColumn.SEMESTER,
	      DbColumn.THEME,
	      DbColumn.CREATED,
	      DbColumn.LAST_MODIFIED,
	    };
	default:
	  throw new IllegalStateException
	    (this + ": Method \"getDbColumns\" not implemented for use mode: " + UseMode.nameFor[this.useMode]);
	}
    else // not! this.withPath
      switch ( this.useMode )
	{
	case UseMode.COMPONENT :
	  return new String []
	    {
	      DbColumn.ID,
	      DbColumn.SYNC_ID,
	      DbColumn.NAME,
	      DbColumn.DESCRIPTION,
	    };
	case UseMode.LINK :
	  return new String []
	    {
	      DbColumn.ID,
	      DbColumn.NAME,
	      DbColumn.DESCRIPTION,
	    };
	case UseMode.SERVE :
	  return new String []
	    {
	      DbColumn.ID,
	      DbColumn.SYNC_ID,
	      DbColumn.NAME,
	      DbColumn.DESCRIPTION,
	      DbColumn.SEMESTER,
	      DbColumn.CREATED,
	      DbColumn.LAST_MODIFIED,
	    };
	case UseMode.INFO :
	  return new String []
	    {
	      DbColumn.ID,
	      DbColumn.SYNC_ID,
	      DbColumn.CONTAINED_IN,
	      DbColumn.PURE_NAME,
	      DbColumn.NAME,
	      DbColumn.DESCRIPTION,
	      DbColumn.SEMESTER,
	      DbColumn.CREATED,
	      DbColumn.LAST_MODIFIED,
	    };
	default:
	  throw new IllegalStateException
	    (this + ": Method \"getDbColumns\" not implemented for use mode: " + UseMode.nameFor[this.useMode] + ", this.withPath: " + this.withPath);
	}
  }

  /**
   * Writes the semester of this class to SAX.
   */

  protected void semesterToSAX (ResultSet resultSet,
                                ContentHandler contentHandler)
    throws SAXException, SQLException
  {
    final String METHOD_NAME = "semesterToSAX";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    Semester semester = null;
    try
      {
        int semesterId = resultSet.getInt(DbColumn.SEMESTER);
        semester = (Semester)this.serviceManager.lookup(Semester.ROLE);
        semester.setId(semesterId);
        semester.setUseMode(UseMode.COMPONENT);
        semester.toSAX(contentHandler, false);
      }
    catch (Exception exception)
      {
        throw new SAXException(exception);
      }
    finally
      {
        if ( semester != null ) this.serviceManager.release(semester);
      }
  }

  /**
   * Writes the lecturers of this class to SAX.
   */

  protected void lecturersToSAX (ContentHandler contentHandler)
    throws SAXException, SQLException
  {
    final String METHOD_NAME = "lecturersToSAX";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    GeneralUser lecturer = null;
    try
      {
        // Start LECTURERS element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.LECTURERS);
        this.xmlElement.startToSAX(contentHandler);

        // Setup lecturer object:
        lecturer = (GeneralUser)this.serviceManager.lookup(GeneralUser.ROLE);

        // Query data:
        lecturer.setUseMode(UseMode.COMPONENT);
        lecturer.setWithPath(false);
	ensureDbHelper();
        ResultSet resultSet =
          this.dbHelper.queryClassLecturers(this.id, lecturer.getDbColumns());

        // Write USER elements:
        while ( resultSet.next() )
          {
            lecturer.reset();
            lecturer.setId(Id.AUTO);
            lecturer.setUseMode(UseMode.COMPONENT);
            lecturer.toSAX(resultSet, contentHandler, false);
          }

        // Close LECTURERS element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.LECTURERS);
        this.xmlElement.endToSAX(contentHandler);

        this.logDebug(METHOD_NAME + "2/2: Done");
      }
    catch (Exception exception)
      {
        throw new SAXException(exception);
      }
    finally
      {
        if ( lecturer != null ) this.serviceManager.release(lecturer);
      }
  }

  /**
   * Writes the members of this class to SAX.
   */

  protected void membersToSAX (ContentHandler contentHandler)
    throws SAXException, SQLException
  {
    final String METHOD_NAME = "membersToSAX";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    GeneralUser member = null;
    try
      {
        // Start MEMBERS element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.MEMBERS);
        this.xmlElement.startToSAX(contentHandler);

        // Setup member object:
        member = (GeneralUser)this.serviceManager.lookup(GeneralUser.ROLE);

        // Query data:
        member.setUseMode(UseMode.COMPONENT);
        ResultSet resultSet =
          this.dbHelper.queryClassMembers(this.id, member.getDbColumns());

        // Write USER elements:
        while ( resultSet.next() )
          {
            member.reset();
            member.setId(Id.AUTO);
            member.setUseMode(UseMode.COMPONENT);
            member.toSAX(resultSet, contentHandler, false);
          }

        // Close MEMBERS element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.MEMBERS);
        this.xmlElement.endToSAX(contentHandler);

        this.logDebug(METHOD_NAME + "2/2: Done");
      }
    catch (Exception exception)
      {
        throw new SAXException(exception);
      }
    finally
      {
        if ( member != null ) this.serviceManager.release(member);
      }
  }

  /**
   * Writes the tutorials of this class to SAX.
   */

  protected void tutorialsToSAX (ContentHandler contentHandler)
    throws SAXException, SQLException
  {
    final String METHOD_NAME = "tutorialsToSAX";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    Tutorial tutorial = null;
    try
      {
        // Start TUTORIALS element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.TUTORIALS);
        this.xmlElement.startToSAX(contentHandler);

        // Setup tutorial object:
        tutorial = (Tutorial)this.serviceManager.lookup(Tutorial.ROLE);

        // Query data:
        tutorial.setUseMode(UseMode.COMPONENT);
        ResultSet resultSet =
          this.dbHelper.queryClassTutorials(this.id, tutorial.getDbColumns());

        // Write TUTORIAL elements:
        while ( resultSet.next() )
          {
            tutorial.reset();
            tutorial.setId(Id.AUTO);
            tutorial.setUseMode(UseMode.COMPONENT);
            tutorial.toSAX(resultSet, contentHandler, false);
          }

        // Close TUTORIALS element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.TUTORIALS);
        this.xmlElement.endToSAX(contentHandler);

        this.logDebug(METHOD_NAME + "2/2: Done");
      }
    catch (Exception exception)
      {
        throw new SAXException(exception);
      }
    finally
      {
        if ( tutorial != null ) this.serviceManager.release(tutorial);
      }
  }

  /**
   * Called from {@link #toSAX} if {@link #useMode} is {@link UseMode#COMPONENT COMPONENT}.
   */

  protected void toSAX_COMPONENT (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception
  {
    final String METHOD_NAME = "toSAX_COMPONENT(ContentHandler contentHandler)";
    this.logDebug(METHOD_NAME + "1/2: Started. resultSet = " + resultSet);
    this.startRootElement(contentHandler);
    this.syncIdToSAX(resultSet, contentHandler);
    if ( this.withPath )
      {
	this.pureNameToSAX(resultSet, contentHandler);
      }
    this.nameToSAX(resultSet, contentHandler);
    this.descriptionToSAX(resultSet, contentHandler);
    this.lecturersToSAX(contentHandler);
    this.closeRootElement(contentHandler);
    this.logDebug(METHOD_NAME + "2/2: Done");
  }

  /**
   * Called from {@link #toSAX} if {@link #useMode} is {@link UseMode#LINK LINK}.
   */

  protected void toSAX_LINK (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception
  {
    final String METHOD_NAME = "toSAX_LINK(ContentHandler contentHandler)";
    this.logDebug(METHOD_NAME + "1/2: Started. resultSet = " + resultSet);
    this.startRootElement(contentHandler);
    if ( this.withPath )
      {
	this.pureNameToSAX(resultSet, contentHandler);
      }
    this.nameToSAX(resultSet, contentHandler);
    this.descriptionToSAX(resultSet, contentHandler);
    this.closeRootElement(contentHandler);
    this.logDebug(METHOD_NAME + "2/2: Done");
  }

  /**
   * Called from {@link #toSAX} if {@link #useMode} is {@link UseMode#SERVE SERVE}.
   */

  protected void toSAX_SERVE (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception
  {
    final String METHOD_NAME = "toSAX_SERVE(ContentHandler contentHandler)";
    this.logDebug(METHOD_NAME + "1/2: Started. resultSet = " + resultSet);
    this.startRootElement(contentHandler);
    this.syncIdToSAX(resultSet, contentHandler);
    if ( this.withPath )
      {
	this.pureNameToSAX(resultSet, contentHandler);
      }
    this.nameToSAX(resultSet, contentHandler);
    this.descriptionToSAX(resultSet, contentHandler);
    this.createdToSAX(resultSet, contentHandler);
    this.lastModifiedToSAX(resultSet, contentHandler);
    this.semesterToSAX(resultSet, contentHandler);
    this.lecturersToSAX(contentHandler);
    this.tutorialsToSAX(contentHandler);
    this.membersToSAX(contentHandler);
    this.closeRootElement(contentHandler);
    this.logDebug(METHOD_NAME + "2/2: Done");
  }

  /**
   * Called from {@link #toSAX} if {@link #useMode} is {@link UseMode#INFO INFO}.
   */

  protected void toSAX_INFO (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception
  {
    final String METHOD_NAME = "toSAX_INFO(ContentHandler contentHandler)";
    this.logDebug(METHOD_NAME + "1/2: Started. resultSet = " + resultSet);
    this.startRootElement(contentHandler);
    this.syncIdToSAX(resultSet, contentHandler);
    if ( this.withPath )
      {
	this.pureNameToSAX(resultSet, contentHandler);
      }
    this.nameToSAX(resultSet, contentHandler);
    this.descriptionToSAX(resultSet, contentHandler);
    this.createdToSAX(resultSet, contentHandler);
    this.lastModifiedToSAX(resultSet, contentHandler);
    this.semesterToSAX(resultSet, contentHandler);
    this.lecturersToSAX(contentHandler);
    this.tutorialsToSAX(contentHandler);
    this.membersToSAX(contentHandler);
    this.closeRootElement(contentHandler);
    this.logDebug(METHOD_NAME + "2/2: Done");
  }

  /**
   * Called from {@link #toSAX} if {@link #useMode} is {@link UseMode.INFO CHECKOUT}.
   */

  protected void toSAX_CHECKOUT (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception
  {
    final String METHOD_NAME = "toSAX_CHECKOUT(ContentHandler contentHandler)";
    this.getLogger().debug(METHOD_NAME + "1/2: Started. resultSet = " + resultSet);

    this.startRootElement(contentHandler);
    this.syncIdToSAX(resultSet, contentHandler);
    this.pureNameToSAX(resultSet, contentHandler);
    this.semesterToSAX(resultSet, contentHandler);

    this.closeRootElement(contentHandler);
    this.getLogger().debug(METHOD_NAME + "2/2: Done");
  }

  /**
   * Returns a string that identificates this instance. It has the
   * following form:<pre>
   *   "DefaultELClass" +
   *   '#' + instanceId
   *   '(' + numberOfRecycles
   *   ',' + {@link #id id}
   *   ',' + {@link #useMode useMode}
   *   ')'</pre>
   * where <code>instanceId</code> is the instance id and
   * <code>numberOfRecycles</code> the number of recycles of this instance.
   */

  public String getIdentification ()
  {
    return
      "DefaultELClass" +
      '#' + this.instanceStatus.getInstanceId() +
      '(' + this.instanceStatus.getNumberOfRecycles() +
      ',' + this.id +
      ',' + this.useMode +
      ')';
  }

  /**
   * Creates a <code>DefaultClass</code> instance and sets {@link #type type} and
   * {@link #rootElementName rootElementName} to the appropriate values.
   */

  public DefaultELClass ()
  {
    this.type = PseudoDocType.CLASS;
    this.rootElementName = XMLElement.CLASS;
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Notifies this instance that it has been looked-up.
   */

  public void notifyLookup (String ownerLabel)
  {
    final String METHOD_NAME = "notifyLookup";
    this.logDebug(METHOD_NAME + " 1/2: Started. ownerLabel = " + ownerLabel);
    this.instanceStatus.notifyLookup(ownerLabel);
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Notifies this instance that it has been released.
   */

  public void notifyRelease (String ownerLabel)
  {
    final String METHOD_NAME = "notifyRelease";
    this.logDebug(METHOD_NAME + " 1/2: Started. ownerLabel = " + ownerLabel);
    this.instanceStatus.notifyRelease();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Recycles this instance.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle()";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    super.recycle();
    this.instanceStatus.notifyRecycle();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Disposes this instance.
   */

  public void dispose ()
  {
    final String METHOD_NAME = "dispose()";
    this.logDebug(METHOD_NAME + "1/2: Started");
    super.dispose();
    this.instanceStatus.notifyDispose();
    this.logDebug(METHOD_NAME + "2/2: Done");
  }
}
