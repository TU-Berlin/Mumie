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

package net.mumie.srv.entities.pseudodocs;

import net.mumie.srv.entities.AbstractPseudoDocument;
import net.mumie.srv.notions.UseMode;
import net.mumie.srv.service.ServiceStatus;
import net.mumie.srv.service.ServiceInstanceStatus;
import net.mumie.srv.notions.XMLElement;
import net.mumie.srv.notions.DbColumn;
import java.sql.ResultSet;
import org.xml.sax.SAXException;
import net.mumie.srv.notions.XMLAttribute;
import org.xml.sax.ContentHandler;

/**
 * Default implementation of {@link ELClass ELClass}.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: ELClassImpl.java,v 1.2 2009/05/28 23:40:33 rassy Exp $</code>
 */

public class ELClassImpl extends AbstractPseudoDocument
  implements ELClass
{
  // --------------------------------------------------------------------------------
  // h1: Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(ELClassImpl.class);

  /**
   * Database columns needed in use mode {@link UseMode#SERVE SERVE}.
   */

  protected static final String[] DB_COLUMNS_SERVE = new String[]
    {
			DbColumn.ID,
			DbColumn.SYNC_ID,
			DbColumn.SECTION_PATH,
			DbColumn.PURE_NAME,
			DbColumn.NAME,
			DbColumn.DESCRIPTION,
			DbColumn.SEMESTER,
			DbColumn.CREATED,
			DbColumn.LAST_MODIFIED,
    };

  /**
   * Database columns needed in use mode {@link UseMode#INFO INFO}.
   */

  protected static final String[] DB_COLUMNS_INFO = new String[]
    {
			DbColumn.ID,
			DbColumn.CONTAINED_IN,
			DbColumn.SECTION_PATH,
			DbColumn.PURE_NAME,
			DbColumn.SYNC_ID,
			DbColumn.NAME,
			DbColumn.DESCRIPTION,
			DbColumn.SEMESTER,
			DbColumn.CREATED,
			DbColumn.LAST_MODIFIED,
    };

  /**
   * Database columns needed in use mode {@link UseMode#COMPONENT COMPONENT}.
   */

  protected static final String[] DB_COLUMNS_COMPONENT = new String[]
    {
			DbColumn.ID,
			DbColumn.SYNC_ID,
			DbColumn.SECTION_PATH,
			DbColumn.PURE_NAME,
			DbColumn.NAME,
			DbColumn.DESCRIPTION,
    };

  /**
   * Database columns needed in use mode {@link UseMode#LINK LINK}.
   */

  protected static final String[] DB_COLUMNS_LINK = new String[]
    {
			DbColumn.ID,
			DbColumn.SECTION_PATH,
			DbColumn.PURE_NAME,
			DbColumn.NAME,
			DbColumn.DESCRIPTION,
    };
  
  // --------------------------------------------------------------------------------
  // h1: Lifecycle methods
  // --------------------------------------------------------------------------------

  /**
   * Creates a new instance.
   */

  public ELClassImpl ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
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

  // --------------------------------------------------------------------------------
  // h1: getDbColumns method
  // --------------------------------------------------------------------------------

  /**
   * Returns the database columns needed by the <code>toSAX</code> methods.
   */

  public String[] getDbColumns ()
  {
    switch ( this.useMode )
      {
      case UseMode.SERVE: return copyDbColums(DB_COLUMNS_SERVE, this.withPath);
      case UseMode.INFO: return copyDbColums(DB_COLUMNS_INFO, this.withPath);
      case UseMode.COMPONENT: return copyDbColums(DB_COLUMNS_COMPONENT, this.withPath);
      case UseMode.LINK: return copyDbColums(DB_COLUMNS_LINK, this.withPath);
      default: return null;
      }
  }

  // --------------------------------------------------------------------------------
  // h1: toSAX methods for specific data
  // --------------------------------------------------------------------------------

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Theme
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Writes the {@link XMLElement#THEME THEME} element.
   */

  protected void themeToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
	this.xmlElement.reset();
	this.xmlElement.setLocalName(XMLElement.THEME);
	this.xmlElement.addAttribute(XMLAttribute.ID, resultSet.getString(DbColumn.THEME));
	this.xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("themeToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Semesters
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Writes the semester of this class to SAX.
   */

  protected void semesterToSAX (ResultSet resultSet,
                                ContentHandler contentHandler)
    throws SAXException
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

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Lecturers
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Writes the lecturers of this class to SAX.
   */

  protected void lecturersToSAX (ContentHandler contentHandler)
    throws SAXException
  {
    final String METHOD_NAME = "lecturersToSAX";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    User lecturer = null;
    try
      {
        // Start LECTURERS element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.LECTURERS);
        this.xmlElement.startToSAX(contentHandler);

        // Setup lecturer object:
        lecturer = (User)this.serviceManager.lookup(User.ROLE);

        // Query data:
        lecturer.setUseMode(UseMode.COMPONENT);
        lecturer.setWithPath(false);
				this.ensureDbHelper();
        ResultSet resultSet =
          this.dbHelper.queryClassLecturers(this.id, lecturer.getDbColumns());

        // Write USER elements:
        while ( resultSet.next() )
          {
            lecturer.reset();
            lecturer.setId(resultSet.getInt(DbColumn.ID));
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
    throws SAXException
  {
    final String METHOD_NAME = "membersToSAX";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    User member = null;
    try
      {
        // Start MEMBERS element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.MEMBERS);
        this.xmlElement.startToSAX(contentHandler);

        // Setup member object:

        member = (User)this.serviceManager.lookup(User.ROLE);

        // Query data:
        member.setUseMode(UseMode.COMPONENT);
        this.ensureDbHelper();
        ResultSet resultSet = this.dbHelper.queryClassMembers(this.id, member.getDbColumns());

        // Write USER elements:
        while ( resultSet.next() )
          {
            member.reset();
            member.setId(resultSet.getInt(DbColumn.ID));
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

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Tutorials
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Writes the tutorials of this class to SAX.
   */

  protected void tutorialsToSAX (ContentHandler contentHandler)
    throws SAXException
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
        this.ensureDbHelper();
        ResultSet resultSet =
          this.dbHelper.queryClassTutorials(this.id, tutorial.getDbColumns());

        // Write TUTORIAL elements:
        while ( resultSet.next() )
          {
            tutorial.reset();
            tutorial.setId(resultSet.getInt(DbColumn.ID));
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

  // --------------------------------------------------------------------------------
  // h1: toSAX methods
  // --------------------------------------------------------------------------------

  /**
   * Called from {@link #toSAX} if {@link #useMode} is {@link UseMode.COMPONENT COMPONENT}.
   */

  protected void toSAX_COMPONENT (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception
  {
    final String METHOD_NAME = "toSAX_COMPONENT";
    this.logDebug(METHOD_NAME + "1/2: Started");
    this.rootElementStartToSAX(contentHandler);
    this.syncIdToSAX(resultSet, contentHandler);
		this.pureNameToSAX(resultSet, contentHandler);
    this.nameToSAX(resultSet, contentHandler);
    this.descriptionToSAX(resultSet, contentHandler);
    this.themeToSAX(resultSet, contentHandler);
    this.lecturersToSAX(contentHandler);
    this.rootElementEndToSAX(contentHandler);
    this.logDebug(METHOD_NAME + "2/2: Done");
  }

  /**
   * Called from {@link #toSAX} if {@link #useMode} is {@link UseMode.LINK LINK}.
   */

  protected void toSAX_LINK (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception
  {
    final String METHOD_NAME = "toSAX_LINK";
    this.logDebug(METHOD_NAME + "1/2: Started");
    this.rootElementStartToSAX(contentHandler);
		this.pureNameToSAX(resultSet, contentHandler);
    this.nameToSAX(resultSet, contentHandler);
    this.descriptionToSAX(resultSet, contentHandler);
    this.rootElementEndToSAX(contentHandler);
    this.logDebug(METHOD_NAME + "2/2: Done");
  }

  /**
   * Called from {@link #toSAX} if {@link #useMode} is {@link UseMode.SERVE SERVE}.
   */

  protected void toSAX_SERVE (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception
  {
    final String METHOD_NAME = "toSAX_SERVE";
    this.logDebug(METHOD_NAME + "1/2: Started");
    this.rootElementStartToSAX(contentHandler);
    this.syncIdToSAX(resultSet, contentHandler);
		this.pureNameToSAX(resultSet, contentHandler);
    this.nameToSAX(resultSet, contentHandler);
    this.descriptionToSAX(resultSet, contentHandler);
    this.themeToSAX(resultSet, contentHandler);
    this.createdToSAX(resultSet, contentHandler);
    this.lastModifiedToSAX(resultSet, contentHandler);
    this.semesterToSAX(resultSet, contentHandler);
    this.lecturersToSAX(contentHandler);
    this.tutorialsToSAX(contentHandler);
    this.membersToSAX(contentHandler);
    this.rootElementEndToSAX(contentHandler);
    this.logDebug(METHOD_NAME + "2/2: Done");
  }

  /**
   * Called from {@link #toSAX} if {@link #useMode} is {@link UseMode.INFO INFO}.
   */

  protected void toSAX_INFO (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception
  {
    final String METHOD_NAME = "toSAX_INFO";
    this.logDebug(METHOD_NAME + "1/2: Started");
    this.rootElementStartToSAX(contentHandler);
    this.syncIdToSAX(resultSet, contentHandler);
		this.pureNameToSAX(resultSet, contentHandler);
    this.nameToSAX(resultSet, contentHandler);
    this.descriptionToSAX(resultSet, contentHandler);
    this.themeToSAX(resultSet, contentHandler);
    this.createdToSAX(resultSet, contentHandler);
    this.lastModifiedToSAX(resultSet, contentHandler);
    this.semesterToSAX(resultSet, contentHandler);
    this.lecturersToSAX(contentHandler);
    this.tutorialsToSAX(contentHandler);
    this.membersToSAX(contentHandler);
    this.rootElementEndToSAX(contentHandler);
    this.logDebug(METHOD_NAME + "2/2: Done");
  }
}