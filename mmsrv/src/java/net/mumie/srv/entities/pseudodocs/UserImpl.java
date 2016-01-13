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

import java.sql.ResultSet;
import net.mumie.srv.entities.AbstractPseudoDocument;
import net.mumie.srv.notions.DbColumn;
import net.mumie.srv.notions.SessionAttrib;
import net.mumie.srv.notions.UseMode;
import net.mumie.srv.notions.XMLAttribute;
import net.mumie.srv.notions.XMLElement;
import net.mumie.srv.service.ServiceInstanceStatus;
import net.mumie.srv.service.ServiceStatus;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.webapps.session.SessionManager;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Default implementation of {@link User User}.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: UserImpl.java,v 1.6 2009/11/28 00:26:03 rassy Exp $</code>
 */

public class UserImpl extends AbstractPseudoDocument
  implements User
{
  // --------------------------------------------------------------------------------
  // h1: Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(UserImpl.class);

  /**
   * Database columns needed in use mode {@link UseMode#SERVE SERVE}.
   */

  protected static final String[] DB_COLUMNS_SERVE = new String[]
    {
      DbColumn.ID,
      DbColumn.SYNC_ID,
      DbColumn.PURE_NAME,
      DbColumn.LOGIN_NAME,
      DbColumn.FIRST_NAME,
      DbColumn.SURNAME,
      DbColumn.DESCRIPTION,
      DbColumn.EMAIL,
      DbColumn.LANGUAGE,
      DbColumn.CREATED,
      DbColumn.LAST_MODIFIED,
      DbColumn.LAST_LOGIN,
    };

  /**
   * Database columns needed in use mode {@link UseMode#INFO INFO}.
   */

  protected static final String[] DB_COLUMNS_INFO = new String[]
    {
      DbColumn.ID,
      DbColumn.SYNC_ID,
      DbColumn.CONTAINED_IN,
      DbColumn.PURE_NAME,
      DbColumn.LOGIN_NAME,
      DbColumn.FIRST_NAME,
      DbColumn.SURNAME,
      DbColumn.DESCRIPTION,
      DbColumn.EMAIL,
      DbColumn.LANGUAGE,
      DbColumn.CREATED,
      DbColumn.LAST_MODIFIED,
      DbColumn.LAST_LOGIN,
    };

  /**
   * Database columns needed in use mode {@link UseMode#COMPONENT COMPONENT}.
   */

  protected static final String[] DB_COLUMNS_COMPONENT = new String[]
    {
      DbColumn.ID,
      DbColumn.PURE_NAME,
      DbColumn.LOGIN_NAME,
      DbColumn.FIRST_NAME,
      DbColumn.SURNAME,
      DbColumn.DESCRIPTION,
      DbColumn.EMAIL,
      DbColumn.LANGUAGE,
    };

  /**
   * Database columns needed in use mode {@link UseMode#LINK LINK}.
   */

  protected static final String[] DB_COLUMNS_LINK = new String[]
    {
      DbColumn.ID,
      DbColumn.LOGIN_NAME,
      DbColumn.FIRST_NAME,
      DbColumn.SURNAME,
      DbColumn.DESCRIPTION,
      DbColumn.EMAIL,
    };
  
  // --------------------------------------------------------------------------------
  // h1: Lifecycle methods
  // --------------------------------------------------------------------------------

  /**
   * Creates a new instance.
   */

  public UserImpl ()
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
  // h1: Set methods
  // --------------------------------------------------------------------------------

  /**
   * Sets the id of this user to the id of the user of the session.
   */

  public void setIdFromSession ()
    throws ProcessingException
  {
    final String METHOD_NAME = "setIdFromSession";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    SessionManager sessionManager = null;
    try
      {
        sessionManager = (SessionManager)this.serviceManager.lookup(SessionManager.ROLE);
        Session session = sessionManager.getSession(false);
        int id = ((Integer)session.getAttribute(SessionAttrib.USER)).intValue();
        this.setId(id);
        this.logDebug(METHOD_NAME + " 1/2: Done. id = " + id);
      }
    catch (Exception exception)
      {
        throw new ProcessingException(exception);
      }
    finally
      {
        if ( sessionManager != null )
          this.serviceManager.release(sessionManager);
      }
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
  // h2: Login name
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Writes the {@link XMLElement#LOGIN_NAME LOGIN_NAME } element.
   */

  protected void loginNameToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.LOGIN_NAME);
        this.xmlElement.setText(resultSet.getString(DbColumn.LOGIN_NAME));
        this.xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("loginNameToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: First name, surname
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Writes the {@link XMLElement#FIRST_NAME FIRST_NAME } element.
   */

  protected void firstNameToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.FIRST_NAME);
        this.xmlElement.setText(resultSet.getString(DbColumn.FIRST_NAME));
        this.xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("firstNameToSAX: " + this.xmlElement.statusToString(), exception);
      }
 } 

  /**
   * Writes the {@link XMLElement#SURNAME SURNAME } element.
   */

  protected void surnameToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.SURNAME);
        this.xmlElement.setText(resultSet.getString(DbColumn.SURNAME));
        this.xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("surnameToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Email
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Writes the {@link XMLElement#EMAIL EMAIL } element.
   */

  protected void emailToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.EMAIL);
        this.xmlElement.setText(resultSet.getString(DbColumn.EMAIL));
        this.xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("emailToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Language
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Writes the {@link XMLElement#LANGUAGE LANGUAGE} element.
   */

  protected void languageToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.LANGUAGE);
        this.xmlElement.addAttribute(XMLAttribute.ID, resultSet.getString(DbColumn.LANGUAGE));
        this.xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("languageToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Last login
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Writes the {@link XMLElement#LAST_LOGIN LAST_LOGIN} element.
   */

  protected void lastLoginToSAX (ResultSet resultSet,
                                 ContentHandler contentHandler)
    throws SAXException
  {
    timeToSAX(resultSet, contentHandler, DbColumn.LAST_LOGIN, XMLElement.LAST_LOGIN);
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: User groups
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Writes the groups this user is a member of to SAX.
   */

  protected void userGroupsToSAX (ContentHandler contentHandler)
    throws SAXException
  {
    final String METHOD_NAME = "userGroupsToSAX";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    UserGroup userGroup = null;
    try
      {
        // Start USER_GROUPS element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.USER_GROUPS);
        this.xmlElement.startToSAX(contentHandler);

        // Setup userGroup object:
        userGroup = (UserGroup)this.serviceManager.lookup(UserGroup.ROLE);

        // Query data:
        userGroup.setUseMode(UseMode.COMPONENT);
        this.ensureDbHelper();
        ResultSet resultSet =
          this.dbHelper.queryGroupsOfUser(this.id, userGroup.getDbColumns());

        // Write USER_GROUP elements:
        while ( resultSet.next() )
          {
            userGroup.reset();
            userGroup.setId(resultSet.getInt(DbColumn.ID));
            userGroup.setUseMode(UseMode.COMPONENT);
            userGroup.toSAX(resultSet, contentHandler, false);
          }

        // Close USER_GROUPS element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.USER_GROUPS);
        this.xmlElement.endToSAX(contentHandler);

        this.logDebug(METHOD_NAME + "2/2: Done");
      }
    catch (Exception exception)
      {
        throw new SAXException(exception);
      }
    finally
      {
        if ( userGroup != null ) this.serviceManager.release(userGroup);
      }
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Tutorials
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Writes the tutorials this user is a member of to SAX.
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
          this.dbHelper.queryTutorialsOfUser(this.id, tutorial.getDbColumns());

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
    this.pureNameToSAX(resultSet, contentHandler);
    this.loginNameToSAX(resultSet, contentHandler);
    this.firstNameToSAX(resultSet, contentHandler);
    this.surnameToSAX(resultSet, contentHandler);
    this.emailToSAX(resultSet, contentHandler);
    this.languageToSAX(resultSet, contentHandler);
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
    this.loginNameToSAX(resultSet, contentHandler);
    this.firstNameToSAX(resultSet, contentHandler);
    this.surnameToSAX(resultSet, contentHandler);
    this.emailToSAX(resultSet, contentHandler);
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
    this.loginNameToSAX(resultSet, contentHandler);
    this.firstNameToSAX(resultSet, contentHandler);
    this.surnameToSAX(resultSet, contentHandler);
    this.emailToSAX(resultSet, contentHandler);
    this.languageToSAX(resultSet, contentHandler);
    this.createdToSAX(resultSet, contentHandler);
    this.lastModifiedToSAX(resultSet, contentHandler);
    this.lastLoginToSAX(resultSet, contentHandler);
    this.userGroupsToSAX(contentHandler);
    this.tutorialsToSAX(contentHandler);
    this.readPermissionsToSAX(contentHandler);
    this.writePermissionsToSAX(contentHandler);
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
    this.containedInToSAX(resultSet, contentHandler);
    this.pureNameToSAX(resultSet, contentHandler);
    this.loginNameToSAX(resultSet, contentHandler);
    this.firstNameToSAX(resultSet, contentHandler);
    this.surnameToSAX(resultSet, contentHandler);
    this.emailToSAX(resultSet, contentHandler);
    this.languageToSAX(resultSet, contentHandler);
    this.createdToSAX(resultSet, contentHandler);
    this.lastModifiedToSAX(resultSet, contentHandler);
    this.lastLoginToSAX(resultSet, contentHandler);
    this.userGroupsToSAX(contentHandler);
    this.tutorialsToSAX(contentHandler);
    this.readPermissionsToSAX(contentHandler);
    this.writePermissionsToSAX(contentHandler);
    this.rootElementEndToSAX(contentHandler);
    this.logDebug(METHOD_NAME + "2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // h1: Requesting user properties
  // --------------------------------------------------------------------------------

  /**
   * Returns true if the user is allowed to create a new (pseudo-)document of the
   * given type, otherwise false. 
   *
   * @param type the (pseudo-)document type
   * @exception ProcessingException if something goes wrong
   */

  public boolean hasCreatePermission (int type)
    throws ProcessingException
  {
    try
      {
        final String METHOD_NAME = "hasCreatePermission";
        this.getLogger().debug(METHOD_NAME + " 1/2: Started. type = " +  type);
        this.ensureDbHelper();
        boolean hasPermission = this.dbHelper.checkCreatePermission(this.getId(), type);
        this.getLogger().debug(METHOD_NAME + " 2/2: Done. hasPermission = " + hasPermission);
        return hasPermission;
      }
    catch (Exception exception)
      {
        throw new ProcessingException(exception);
      }
  }

  /**
   * Returns true if the user is allowed to change the (pseudo-)document with the specific
   * type and id, otherwise false.
   *
   * @param type the (pseudo-)document type
   * @exception ProcessingException if something goes wrong
   */

  public boolean hasWritePermissionForVCThread (int type, int vcThreadId)
    throws ProcessingException
  {
    try
      {
        final String METHOD_NAME = "hasWritePermissionForVCThread";
        this.getLogger().debug(METHOD_NAME + " 1/2: Started. type = " +  type + ", vcThreadId = " + vcThreadId);
        this.ensureDbHelper();
        boolean hasPermission = this.dbHelper.checkWritePermissionForVCThread(this.getId(), type, vcThreadId);
        this.getLogger().debug(METHOD_NAME + " 2/2: Done. hasPermission = " + hasPermission);
        return hasPermission;
      }
    catch (Exception exception)
      {
        throw new ProcessingException(exception);
      }
  }

  /**
   * Returns true if the user is allowed to change the (pseudo-)document with the specific
   * type and id, otherwise false.
   *
   * @param type the (pseudo-)document type
   * @exception ProcessingException if something goes wrong
   */

  public boolean hasWritePermissionForEntity (int type, int id)
    throws ProcessingException
  {
    try
      {
        final String METHOD_NAME = "hasWritePermissionForEntity";
        this.getLogger().debug(METHOD_NAME + " 1/2: Started. type = " +  type + ", id = " + id);
        this.ensureDbHelper();
        boolean hasPermission = this.dbHelper.checkWritePermissionForEntity(this.getId(), type, id);
        this.getLogger().debug(METHOD_NAME + " 2/2: Done. hasPermission = " + hasPermission);
        return hasPermission;
      }
    catch (Exception exception)
      {
        throw new ProcessingException(exception);
      }
  }
}
