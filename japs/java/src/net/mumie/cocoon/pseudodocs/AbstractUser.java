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

import java.util.List;
import java.util.Iterator;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.documents.Document;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.UserRole;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.notions.UseMode;
import net.mumie.cocoon.notions.Id;
import java.util.Date;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.util.PasswordEncryptor;

/**
 * @author Uwe Sinha <a href="mailto:sinha@math.tu-berlin.de">sinha@math.tu-berlin.de</a>
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: AbstractUser.java,v 1.34 2009/09/08 13:19:21 rassy Exp $</code>
 */

public abstract class AbstractUser extends AbstractPseudoDocument
  implements User
{
  // --------------------------------------------------------------------------------
  // Global constants and variables
  // --------------------------------------------------------------------------------

  /**
   * The role of this user. May also be {@link UserRole#UNDEFINED UNDEFINED} or
   * {@link UserRole#ANY ANY}, which means that the role is not defined or this object is
   * suited for any role, respectively.
   */

  protected int role = UserRole.UNDEFINED;

  /**
   * The password encryptor.
   */

  protected PasswordEncryptor encryptor = null;

  // --------------------------------------------------------------------------------
  // Cocoon / Avalon interfaces implementation
  // --------------------------------------------------------------------------------

  /**
   * Calls the superclass {@link AbstractPseudoDocument#reset reset} method and sets {@link
   * #role role} to {@link UserRole#UNDEFINED UNDEFINED}.
   */

  public void reset()
  {
    super.reset();
    this.role = UserRole.UNDEFINED;
  }

  /**
   * Calls the superclass {@link AbstractPseudoDocument#releaseResources releaseResources}
   * method and releases the encryptor if not <code>null</code>.
   */

  protected void releaseResources ()
  {
    if ( this.encryptor == null )
      {
        this.serviceManager.release(this.encryptor);
        this.encryptor = null;
      }
    super.releaseResources();
  }

  // --------------------------------------------------------------------------------
  // PasswordEncryptor methods
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Ensures that {@link #encryptor} is initialized. 
   * </p>
   * <p>
   *   If {@link #encryptor} is <code>null</code>, gets a password encryptor from the
   *   service manager ({@link #serviceManager}) and sets {@link #encryptor} to it. 
   * </p>
   * <p>
   *   If {@link #encryptor} is not <code>null</code>, does nothing.
   * </p>
   */

  protected void ensureEncryptor ()
    throws ServiceException
  {
    if ( this.encryptor != null ) return;
    final String METHOD_NAME = "ensureEncryptor()";
    this.getLogger().debug(METHOD_NAME + "1/2: Need a password encryptor");
    this.encryptor = (PasswordEncryptor)this.serviceManager.lookup(PasswordEncryptor.ROLE);
    this.getLogger().debug(METHOD_NAME + "2/2: this.encryptor = " + this.dbHelper);
  }

  // --------------------------------------------------------------------------------
  // Get and set data
  // --------------------------------------------------------------------------------

  /**
   * Returns the role of this user. May also return {@link UserRole#UNDEFINED UNDEFINED} or
   * {@link UserRole#ANY ANY}, which means that the role is not defined or this object is
   * suited for any role, respectively.
   */

  public int getRole ()
  {
    return this.role;
  }

  /**
   * Sets the role of this user. The new role may also be
   * {@link UserRole#UNDEFINED UNDEFINED} or {@link UserRole#ANY ANY} to indicate that the
   * role is not defined or that this object is suited for any role, respectively.
   */

  public void setRole (int role)
  {
    // TODO: check role
    this.role = role;
  }

  /**
   * Returns <code>true</code> if this user is controlled by an external service. This is
   * the case if, and only if, the user has a non-null synchronization id.
   */

  public boolean isExternallyControlled ()
    throws ServiceException
  {
    try
      {
        this.ensureDbHelper();
        ResultSet resultSet =
          this.dbHelper.queryPseudoDocDatum(this.type, this.getId(), DbColumn.SYNC_ID);
        if ( !resultSet.next() )
          throw new SQLException
            (this + ": Failed to check external control for user: " +
             this.getId() + ": no data");
        return ( resultSet.getString(DbColumn.SYNC_ID) != null );
        
      }
    catch (Exception exception)
      {
	throw new ServiceException(this + ": Wrapped exception:", exception);
      }
  }

  /**
   * <p>
   *   Returns <code>true</code> if <code>password</code> is the password of the user,
   *   otherwise <code>false</code>
   * </p>
   */

  public boolean checkPassword (String password)
     throws ServiceException
  {
    try
      {
        this.ensureEncryptor();
	this.ensureDbHelper();
        password = this.encryptor.encrypt(password);
        ResultSet resultSet =
          this.dbHelper.queryPseudoDocDatum(this.type, this.getId(), DbColumn.PASSWORD);
        if ( !resultSet.next() )
          throw new SQLException
            (this + ": Failed to check password for user: " + this.getId() + ": no data");
        return ( password.equals(resultSet.getString(DbColumn.PASSWORD)) );
      }
    catch (Exception exception)
      {
	throw new ServiceException(this + ": Wrapped exception:", exception);
      }
  }

  /**
   * Returns the id's of the user groups this user is a menber of.
   */

   public int[] getUserGroups ()
     throws ServiceException
  {
    List userGroupsAsList = this.getUserGroupsAsList();
    Integer[] userGroupsAsInegers = 
      (Integer[])userGroupsAsList.toArray(new Integer[userGroupsAsList.size()]);
    int[] userGroups = new int[userGroupsAsInegers.length];
    for (int i = 0; i < userGroups.length; i++)
      userGroups[i] = userGroupsAsInegers[i].intValue();
    return userGroups;
  }

  /**
   * Returns the id's of the user groups this user is a menber of, as a list of
   * {@link Integer} objects.
   */

   public List getUserGroupsAsList ()
     throws ServiceException
  {
    try
      {
	this.ensureDbHelper();
	return this.dbHelper.queryUserGroupsAsList(this.getId());
      }
    catch (Exception exception)
      {
	throw new ServiceException(this + ": Wrapped exception:", exception);
      }
  }

  /**
   * Returns the names of the user groups this user is a menber of.
   */

   public String[] getUserGroupNames ()
     throws ServiceException
  {
    List userGroupNamesAsList = this.getUserGroupNamesAsList();
    return
      (String[])(userGroupNamesAsList.toArray(new String[userGroupNamesAsList.size()]));
  }

  /**
   * Returns the names of the user groups this user is a menber of, as a list of
   * {@link String} objects.
   */

  public List getUserGroupNamesAsList ()
    throws ServiceException
  {
    try
      {
	this.ensureDbHelper();
	return this.dbHelper.queryUserGroupNamesAsList(this.getId());
      }
    catch (Exception exception)
      {
	throw new ServiceException(this + ": Wrapped exception:", exception);
      }
  }

  /**
   * Returns <code>true</code> if the user is allowed to read the pseudo-document with type
   * <code>type</code> and id <code>id</code>.
   */

  public boolean hasPseudoDocReadPermission (int docType, int docId)
    throws ServiceException
  {
    try
      {
	final String METHOD_NAME = "hasPseudoDocReadPermission(int type, int id)";
	this.getLogger().debug(METHOD_NAME + " 1/2: docType = " + docType + ", docId = " + docId);
	this.ensureDbHelper();
	boolean hasPermission = this.dbHelper.checkPseudoDocumentReadPermission(this.getId(), docType, docId);
	this.getLogger().debug(METHOD_NAME + " 2/2: hasPermission = " + hasPermission);
	return hasPermission;
      }
    catch (Throwable throwable)
      {
	throw new ServiceException
          (this + ": While checking read permission", throwable);
      }
  }

  /**
   * Returns <code>true</code> if the user is allowed to read the document with type
   * <code>type</code> and id <code>id</code>.
   */

  public boolean hasReadPermission (int docType, int docId)
    throws ServiceException
  {
    try
      {
	final String METHOD_NAME = "hasReadPermission(int type, int id)";
	this.getLogger().debug(METHOD_NAME + " 1/2: docType = " + docType + ", docId = " + docId);
	this.ensureDbHelper();
	boolean hasPermission = this.dbHelper.checkDocumentReadPermission(this.getId(), docType, docId);
	this.getLogger().debug(METHOD_NAME + " 2/2: hasPermission = " + hasPermission);
	return hasPermission;
      }
    catch (Throwable throwable)
      {
	throw new ServiceException
          (this + ": While checking read permission", throwable);
      }
  }

  /**
   * Returns <code>true</code> if the user is allowed to read
   * {@link net.mumie.cocoon.documents.Document Document}, otherwise <code>false</code>.
   */

  public boolean hasReadPermission (Document document)
    throws ServiceException
  {
    return this.hasReadPermission(document.getType(), document.getId());
  }

  /**
   * Returns <code>true</code> if the user is allowed to add a new version
   * of a document in a given version control thread, otherwise
   * <code>false</code>.
   * @param vcThread the id of the version control thread
   * @exception IllegalArgumentException if a negative VC thread ID was given
   * @exception ServiceException if any other error occurs
   */

  public  boolean hasWritePermission (int docType, int vcThread) 
    throws ServiceException
  {
    try
      {
        final String METHOD_NAME = "hasWritePermission(int)";
        this.getLogger().debug(METHOD_NAME + " 1/2: vcThread = " +  vcThread);
        if ( vcThread < 0 ) 
          throw new IllegalArgumentException(this + ": VC thread id may not be negative"); 
        this.ensureDbHelper();
        boolean hasPermission 
          = this.dbHelper.checkWritePermission(this.getId(),docType,vcThread);
        this.getLogger().debug(METHOD_NAME + " 2/2: hasPermission = " + hasPermission);
        return hasPermission;
      }
    catch (Exception exception)
      {
	throw new ServiceException(this + ": Wrapped exception:", exception);
      }
  }

  /**
   * Returns <code>true</code> if the user is allowed to add a new document
   * of the given type, otherwise <code>false</code>.
   *
   * @param docType the document type
   * @exception IllegalArgumentException if the id of the document type was out of range 
   * @exception ServiceException if any other error occurs
   * @see net.mumie.cocoon.notions.DocType
   */

  public boolean hasCreatePermission (int docType)
    throws ServiceException, IllegalArgumentException
  {
    try
      {
        final String METHOD_NAME = "hasCreatePermission(int)";
        this.getLogger().debug(METHOD_NAME + " 1/2:  docType = " +  docType);
        this.ensureDbHelper();
        boolean hasPermission 
          = this.dbHelper.checkCreatePermission(this.getId(), docType);
        this.getLogger().debug(METHOD_NAME + " 2/2: hasPermission = " + hasPermission);
        return hasPermission;
      }
    catch (Exception exception)
      {
	throw new ServiceException
          (this +  ": Wrapped exception:", exception);
      }
  }
  
  /**
   * Checks if this user is an administrator. In this very basic
   * implementation, there is only one administrator, identified by his/her
   * user id 0 (zero). 
   *
   * @return <code>true</code> if this user has the id 0,
   * <code>false</code> if not
   */

  public boolean isAdmin()
    throws ServiceException
  {
    return ( this.getId() == 0 ); 
  }

  /**
   * {@inheritDoc}
   */

  public boolean isGroupAdmin (int groupId)
    throws ServiceException
  {
    try
      {
        final String METHOD_NAME = "isGroupAdmin(int groupId)";
        this.getLogger().debug(METHOD_NAME + " 1/2: groupId = " + groupId);
        this.ensureDbHelper();
        ResultSet resultSet = this.dbHelper.queryUserGroupMember
          (groupId, this.getId(), new String[] {DbColumn.ADMIN}); 
        return (resultSet.next() && resultSet.getBoolean(DbColumn.ADMIN)); 
      }
    catch (Exception exception)
      {
        throw new ServiceException
          (this + ": Wrapped exception:", exception);
      }
  }

  /**
   * Returns the section this user may store sync data in, as the sections id.
   */

  public int getSyncHome ()
    throws ServiceException
  {
    try
      {
        final String METHOD_NAME = "getSyncHome()";
        this.getLogger().debug(METHOD_NAME + " 1/2");
        this.ensureDbHelper();
	int sync_home = this.dbHelper.getSyncHome(this.getId()); 
        this.getLogger().debug(METHOD_NAME + " 2/2 returning sync_home: " + sync_home);
        return sync_home;
      }
    catch (Exception exception)
      {
        throw new ServiceException
          (this + ": Wrapped exception:", exception);
      }
  }

  // --------------------------------------------------------------------------------
  // Auxiliaries for the toSAX methods
  // --------------------------------------------------------------------------------

  /**
   * Returns the database columns this pseudo-document needs if one of the
   * <code>toSAX</code> methods would be called. The colums are:
   * <ul>
   *   <li>
   *     In use mode {@link UseMode#COMPONENT COMPONENT}:
   *     <ul>
   *       <li>{@link DbColumn#ID ID}</li>
   *       <li>{@link DbColumn#LOGIN_NAME LOGIN_NAME}</li>
   *       <li>{@link DbColumn#FIRST_NAME FIRST_NAME}</li>
   *       <li>{@link DbColumn#SURNAME SURNAME}</li>
   *       <li>{@link DbColumn#DESCRIPTION DESCRIPTION}</li>
   *       <li>{@link DbColumn#EMAIL EMAIL}</li>
   *       <li>{@link DbColumn#THEME THEME}</li>
   *     </ul>
   *   </li>
   *   <li>
   *     In use mode {@link UseMode#LINK LINK}:
   *     <ul>
   *       <li>{@link DbColumn#ID ID}</li>
   *       <li>{@link DbColumn#LOGIN_NAME LOGIN_NAME}</li>
   *       <li>{@link DbColumn#FIRST_NAME FIRST_NAME}</li>
   *       <li>{@link DbColumn#SURNAME SURNAME}</li>
   *       <li>{@link DbColumn#DESCRIPTION DESCRIPTION}</li>
   *       <li>{@link DbColumn#EMAIL EMAIL}</li>
   *     </ul>
   *   </li>
   *   <li>
   *     In use mode {@link UseMode#SERVE SERVE}:
   *     <ul>
   *       <li>{@link DbColumn#ID ID}</li>
   *       <li>{@link DbColumn#SYNC_ID SYNC_ID}</li>
   *       <li>{@link DbColumn#LOGIN_NAME LOGIN_NAME}</li>
   *       <li>{@link DbColumn#FIRST_NAME FIRST_NAME}</li>
   *       <li>{@link DbColumn#SURNAME SURNAME}</li>
   *       <li>{@link DbColumn#DESCRIPTION DESCRIPTION}</li>
   *       <li>{@link DbColumn#EMAIL EMAIL}</li>
   *       <li>{@link DbColumn#THEME THEME}</li>
   *       <li>{@link DbColumn#CREATED CREATED}</li>
   *       <li>{@link DbColumn#LAST_MODIFIED}</li>
   *       <li>{@link DbColumn#LAST_LOGIN LAST_LOGIN}</li>
   *     </ul>
   *   </li>
   *   <li>
   *     In use mode {@link UseMode#INFO INFO}:
   *     <ul>
   *       <li>{@link DbColumn#ID ID}</li>
   *       <li>{@link DbColumn#SYNC_ID SYNC_ID}</li>
   *       <li>{@link DbColumn#LOGIN_NAME LOGIN_NAME}</li>
   *       <li>{@link DbColumn#FIRST_NAME FIRST_NAME}</li>
   *       <li>{@link DbColumn#SURNAME SURNAME}</li>
   *       <li>{@link DbColumn#DESCRIPTION DESCRIPTION}</li>
   *       <li>{@link DbColumn#EMAIL EMAIL}</li>
   *       <li>{@link DbColumn#THEME THEME}</li>
   *       <li>{@link DbColumn#CREATED CREATED}</li>
   *       <li>{@link DbColumn#LAST_MODIFIED}</li>
   *       <li>{@link DbColumn#LAST_LOGIN LAST_LOGIN}</li>
   *     </ul>
   *   </li>
   * </ul>
   */

  public String[] getDbColumns ()
  {
    if ( this.withPath )
      // could we have a loop through UseMode.first to UseMode.last and define the dbColumns by xml-configuration ?
      switch ( this.useMode )
	{
	case UseMode.COMPONENT :
	  return new String[]
	    {
	      DbColumn.ID,
	      DbColumn.SECTION_PATH,
	      DbColumn.PURE_NAME,
	      DbColumn.LOGIN_NAME,
	      DbColumn.FIRST_NAME,
	      DbColumn.SURNAME,
	      DbColumn.DESCRIPTION,
	      DbColumn.EMAIL,
	      DbColumn.THEME,
	      DbColumn.LANGUAGE,
	    };
	case UseMode.LINK :
	  return new String[]
	    {
	      DbColumn.ID,
	      DbColumn.SECTION_PATH,
	      DbColumn.PURE_NAME,
	      DbColumn.LOGIN_NAME,
	      DbColumn.FIRST_NAME,
	      DbColumn.SURNAME,
	      DbColumn.DESCRIPTION,
	      DbColumn.EMAIL,
	    };
	case UseMode.SERVE :
	  return new String[]
	    {
	      DbColumn.ID,
	      DbColumn.SYNC_ID,
	      DbColumn.SECTION_PATH,
	      DbColumn.PURE_NAME,
	      DbColumn.LOGIN_NAME,
	      DbColumn.FIRST_NAME,
	      DbColumn.SURNAME,
	      DbColumn.DESCRIPTION,
	      DbColumn.EMAIL,
	      DbColumn.THEME,
	      DbColumn.CREATED,
              DbColumn.LAST_MODIFIED,
	      DbColumn.LAST_LOGIN,
	    };
	case UseMode.INFO :
	  return new String[]
	    {
	      DbColumn.ID,
	      DbColumn.SYNC_ID,
	      DbColumn.CONTAINED_IN,
	      DbColumn.SECTION_PATH,
	      DbColumn.PURE_NAME,
	      DbColumn.LOGIN_NAME,
	      DbColumn.FIRST_NAME,
	      DbColumn.SURNAME,
	      DbColumn.DESCRIPTION,
	      DbColumn.EMAIL,
	      DbColumn.THEME,
	      DbColumn.LANGUAGE,
	      DbColumn.CREATED,
              DbColumn.LAST_MODIFIED,
	      DbColumn.LAST_LOGIN,
	    };
	case UseMode.CHECKOUT :
	  return new String[]
	    {
	      DbColumn.ID,
	      DbColumn.SECTION_PATH,
	      DbColumn.PURE_NAME,
	      DbColumn.SYNC_ID,
	      DbColumn.LOGIN_NAME,
	      DbColumn.FIRST_NAME,
	      DbColumn.SURNAME,
	      DbColumn.DESCRIPTION,
	      DbColumn.EMAIL,
	      DbColumn.THEME,
	      DbColumn.LANGUAGE,
	      DbColumn.CREATED,
              DbColumn.LAST_MODIFIED,
	      DbColumn.LAST_LOGIN,
	    };
	default:
	  throw new IllegalStateException
	    (this + ": Method \"getDbColumns\" not implemented for use mode: " + UseMode.nameFor[this.useMode]);
	}
    else
      switch ( this.useMode )
	{
	case UseMode.COMPONENT :
	  return new String[]
	    {
	      DbColumn.ID,
        DbColumn.PURE_NAME,
	      DbColumn.LOGIN_NAME,
	      DbColumn.FIRST_NAME,
	      DbColumn.SURNAME,
	      DbColumn.DESCRIPTION,
	      DbColumn.EMAIL,
	      DbColumn.THEME,
	      DbColumn.LANGUAGE,
	    };
	case UseMode.LINK :
	  return new String[]
	    {
	      DbColumn.ID,
	      DbColumn.LOGIN_NAME,
	      DbColumn.FIRST_NAME,
	      DbColumn.SURNAME,
	      DbColumn.DESCRIPTION,
	      DbColumn.EMAIL,
	    };
	case UseMode.SERVE :
	  return new String[]
	    {
	      DbColumn.ID,
	      DbColumn.SYNC_ID,
	      DbColumn.PURE_NAME,
	      DbColumn.LOGIN_NAME,
	      DbColumn.FIRST_NAME,
	      DbColumn.SURNAME,
	      DbColumn.DESCRIPTION,
	      DbColumn.EMAIL,
	      DbColumn.THEME,
	      DbColumn.LANGUAGE,
	      DbColumn.CREATED,
              DbColumn.LAST_MODIFIED,
	      DbColumn.LAST_LOGIN,
	    };
	case UseMode.INFO :
	  return new String[]
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
	      DbColumn.THEME,
	      DbColumn.LANGUAGE,
	      DbColumn.CREATED,
              DbColumn.LAST_MODIFIED,
	      DbColumn.LAST_LOGIN,
	    };
	default:
	  throw new IllegalStateException
	    (this + ": Method \"getDbColumns\" not implemented for use mode: " + UseMode.nameFor[this.useMode] + ", this.withPath: " + this.withPath);
	}
  }

  /**
   * Sets {@link #id id} and/or {@link #role} to the values found in
   * <code>resultSet</code> if {@link #id id} and/or {@link #role} is
   * {@link Id#AUTO Id.AUTO} or {@link UserRole#AUTO UserRole.AUTO}, respectively.
   */

  protected void autoSetup (ResultSet resultSet)
    throws SQLException
  {
    final String METHOD_NAME = "autoSetup(ResultSet resultSet)";
    this.getLogger().debug
      (METHOD_NAME + " 1/2: Started." + " this.role = " + this.role);
    super.autoSetup(resultSet);
    if ( this.role == UserRole.AUTO )
      this.role = resultSet.getInt(DbColumn.ROLE);
    this.getLogger().debug
      (METHOD_NAME + " 2/2: Done." + " this.role = " + this.role);
  }

  /**
   * Writes the {@link XMLElement#LOGIN_NAME LOGIN_NAME } element.
   */

  protected void loginNameToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException, SQLException
  {
    this.xmlElement.reset();
    this.xmlElement.setLocalName(XMLElement.LOGIN_NAME);
    this.xmlElement.setText(resultSet.getString(DbColumn.LOGIN_NAME));
    this.xmlElement.toSAX(contentHandler);
  }

  /**
   * Writes the {@link XMLElement#FIRST_NAME FIRST_NAME } element.
   */

  protected void firstNameToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException, SQLException
  {
    this.xmlElement.reset();
    this.xmlElement.setLocalName(XMLElement.FIRST_NAME);
    this.xmlElement.setText(resultSet.getString(DbColumn.FIRST_NAME));
    this.xmlElement.toSAX(contentHandler);
  }

  /**
   * Writes the {@link XMLElement#SURNAME SURNAME } element.
   */

  protected void surnameToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException, SQLException
  {
    this.xmlElement.reset();
    this.xmlElement.setLocalName(XMLElement.SURNAME);
    this.xmlElement.setText(resultSet.getString(DbColumn.SURNAME));
    this.xmlElement.toSAX(contentHandler);
  }

  /**
   * Writes the {@link XMLElement#EMAIL EMAIL } element.
   */

  protected void emailToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException, SQLException
  {
    this.xmlElement.reset();
    this.xmlElement.setLocalName(XMLElement.EMAIL);
    this.xmlElement.setText(resultSet.getString(DbColumn.EMAIL));
    this.xmlElement.toSAX(contentHandler);
  }

  /**
   * Writes the {@link XMLElement#THEME THEME} element.
   */

  protected void themeToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException, SQLException
  {
    this.xmlElement.reset();
    this.xmlElement.setLocalName(XMLElement.THEME);
    this.xmlElement.addAttribute(XMLAttribute.ID, resultSet.getString(DbColumn.THEME));
    this.xmlElement.toSAX(contentHandler);
  }

  /**
   * Writes the {@link XMLElement#LANGUAGE LANGUAGE} element.
   */

  protected void languageToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException, SQLException
  {
    this.xmlElement.reset();
    this.xmlElement.setLocalName(XMLElement.LANGUAGE);
    this.xmlElement.addAttribute(XMLAttribute.ID, resultSet.getString(DbColumn.LANGUAGE));
    this.xmlElement.toSAX(contentHandler);
  }

//   /**
//    * Writes the {@link XMLElement#CREATED CREATED} element.
//    */

//   protected void createdToSAX (ResultSet resultSet, ContentHandler contentHandler)
//     throws SAXException, SQLException
//   {
//     this.xmlElement.reset();
//     this.xmlElement.setLocalName(XMLElement.CREATED);
//     this.xmlElement.addAttribute(XMLAttribute.VALUE, resultSet.getDate(DbColumn.CREATED));
//     this.xmlElement.toSAX(contentHandler);
//   }

  /**
   * Writes the {@link XMLElement#LAST_LOGIN LAST_LOGIN} element.
   */

  protected void lastLoginToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException, SQLException
  {
    this.xmlElement.reset();
    this.xmlElement.setLocalName(XMLElement.LAST_LOGIN);
    this.xmlElement.addAttribute(XMLAttribute.VALUE, resultSet.getDate(DbColumn.LAST_LOGIN));
    this.xmlElement.toSAX(contentHandler);
  }

  /**
   * Writes the {@link XMLElement#ROLE ROLE} element.
   */

  protected void roleToSAX (ContentHandler contentHandler)
    throws SAXException, SQLException
  {
    this.xmlElement.reset();
    this.xmlElement.setLocalName(XMLElement.ROLE);
    this.xmlElement.addAttribute(XMLAttribute.ID, this.role);
    this.xmlElement.addAttribute(XMLAttribute.NAME, UserRole.nameFor[this.role]);
    this.xmlElement.toSAX(contentHandler);
  }

  /**
   * Writes the groups this user is a member of to SAX.
   */

  protected void userGroupsToSAX (ContentHandler contentHandler)
    throws SAXException, SQLException
  {
    final String METHOD_NAME = "userGroupsToSAX";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");
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
          this.dbHelper.queryUserGroups(this.getId(), userGroup.getDbColumns());

        // Write USER_GROUP elements:
        while ( resultSet.next() )
          {
            userGroup.reset();
            userGroup.setId(Id.AUTO);
            userGroup.setUseMode(UseMode.COMPONENT);
            userGroup.toSAX(resultSet, contentHandler, false);
          }

        // Close USER_GROUPS element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.USER_GROUPS);
        this.xmlElement.endToSAX(contentHandler);

        this.getLogger().debug(METHOD_NAME + "2/2: Done");
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

  /**
   * Writes the tutorials this user is a member of to SAX.
   */

  protected void tutorialsToSAX (ContentHandler contentHandler)
    throws SAXException, SQLException
  {
    final String METHOD_NAME = "tutorialsToSAX";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");
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
          this.dbHelper.queryTutorialsOfUser(this.getId(), tutorial.getDbColumns());

        // Write USER_GROUP elements:
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

        this.getLogger().debug(METHOD_NAME + "2/2: Done");
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
  // toSAX methods
  // --------------------------------------------------------------------------------

  /**
   * Called from {@link #toSAX} if {@link #useMode} is {@link UseMode.COMPONENT COMPONENT}.
   */

  protected void toSAX_COMPONENT (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception
  {
    final String METHOD_NAME = "toSAX_COMPONENT(ContentHandler contentHandler)";
    this.getLogger().debug(METHOD_NAME + "1/2: Started. resultSet = " + resultSet);

    this.startRootElement(contentHandler);
    if ( this.role > 0 ) this.roleToSAX(contentHandler);
    this.pureNameToSAX(resultSet, contentHandler);
    this.loginNameToSAX(resultSet, contentHandler);
    this.firstNameToSAX(resultSet, contentHandler);
    this.surnameToSAX(resultSet, contentHandler);
    this.emailToSAX(resultSet, contentHandler);
    this.themeToSAX(resultSet, contentHandler);
    this.languageToSAX(resultSet, contentHandler);
    this.closeRootElement(contentHandler);

    this.getLogger().debug(METHOD_NAME + "2/2: Done");
  }

  /**
   * Called from {@link #toSAX} if {@link #useMode} is {@link UseMode.LINK LINK}.
   */

  protected void toSAX_LINK (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception
  {
    final String METHOD_NAME = "toSAX_LINK(ContentHandler contentHandler)";
    this.getLogger().debug(METHOD_NAME + "1/2: Started. resultSet = " + resultSet);

    this.startRootElement(contentHandler);
    if ( this.role > 0 ) this.roleToSAX(contentHandler);
    this.loginNameToSAX(resultSet, contentHandler);
    this.firstNameToSAX(resultSet, contentHandler);
    this.surnameToSAX(resultSet, contentHandler);
    this.emailToSAX(resultSet, contentHandler);
    this.closeRootElement(contentHandler);

    this.getLogger().debug(METHOD_NAME + "2/2: Done");
  }

  /**
   * Called from {@link #toSAX} if {@link #useMode} is {@link UseMode.SERVE SERVE}.
   */

  protected void toSAX_SERVE (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception
  {
    final String METHOD_NAME = "toSAX_SERVE(ContentHandler contentHandler)";
    this.getLogger().debug(METHOD_NAME + "1/2: Started. resultSet = " + resultSet);

    this.startRootElement(contentHandler);
    if ( this.role > 0 ) this.roleToSAX(contentHandler);
    this.syncIdToSAX(resultSet, contentHandler);
    this.pureNameToSAX(resultSet, contentHandler);
    this.loginNameToSAX(resultSet, contentHandler);
    this.firstNameToSAX(resultSet, contentHandler);
    this.surnameToSAX(resultSet, contentHandler);
    this.emailToSAX(resultSet, contentHandler);
    this.themeToSAX(resultSet, contentHandler);
    this.languageToSAX(resultSet, contentHandler);
    this.createdToSAX(resultSet, contentHandler);
    this.lastModifiedToSAX(resultSet, contentHandler);
    this.lastLoginToSAX(resultSet, contentHandler);
    this.userGroupsToSAX(contentHandler);
    this.tutorialsToSAX(contentHandler);
    this.readPermissionsToSAX(contentHandler);
    this.writePermissionsToSAX(contentHandler);
    this.closeRootElement(contentHandler);

    this.getLogger().debug(METHOD_NAME + "2/2: Done");
  }

  /**
   * Called from {@link #toSAX} if {@link #useMode} is {@link UseMode.INFO INFO}.
   */

  protected void toSAX_INFO (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception
  {
    final String METHOD_NAME = "toSAX_INFO(ContentHandler contentHandler)";
    this.getLogger().debug(METHOD_NAME + "1/2: Started. resultSet = " + resultSet);

    this.startRootElement(contentHandler);
    if ( this.role > 0 ) this.roleToSAX(contentHandler);
    this.syncIdToSAX(resultSet, contentHandler);
    this.containedInToSAX(resultSet, contentHandler);
    this.pureNameToSAX(resultSet, contentHandler);
    this.loginNameToSAX(resultSet, contentHandler);
    this.firstNameToSAX(resultSet, contentHandler);
    this.surnameToSAX(resultSet, contentHandler);
    this.emailToSAX(resultSet, contentHandler);
    this.themeToSAX(resultSet, contentHandler);
    this.languageToSAX(resultSet, contentHandler);
    this.createdToSAX(resultSet, contentHandler);
    this.lastModifiedToSAX(resultSet, contentHandler);
    this.lastLoginToSAX(resultSet, contentHandler);
    this.userGroupsToSAX(contentHandler);
    this.tutorialsToSAX(contentHandler);
    this.readPermissionsToSAX(contentHandler);
    this.writePermissionsToSAX(contentHandler);
    this.closeRootElement(contentHandler);

    this.getLogger().debug(METHOD_NAME + "2/2: Done");
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
    if ( this.role > 0 ) this.roleToSAX(contentHandler);
    this.syncIdToSAX(resultSet, contentHandler);
    this.pureNameToSAX(resultSet, contentHandler);
    this.loginNameToSAX(resultSet, contentHandler);
    this.firstNameToSAX(resultSet, contentHandler);
    this.surnameToSAX(resultSet, contentHandler);
    this.emailToSAX(resultSet, contentHandler);
    this.themeToSAX(resultSet, contentHandler);
    this.languageToSAX(resultSet, contentHandler);
    this.createdToSAX(resultSet, contentHandler);
    this.lastModifiedToSAX(resultSet, contentHandler);
    this.lastLoginToSAX(resultSet, contentHandler);
    this.userGroupsToSAX(contentHandler);
    this.tutorialsToSAX(contentHandler);
    this.closeRootElement(contentHandler);

    this.getLogger().debug(METHOD_NAME + "2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // Constructor
  // --------------------------------------------------------------------------------

  /**
   * Creates an <code>AbstractUser</code> instance and sets {@link #type type} and
   * {@link #rootElementName rootElementName} to the appropriate values.
   */

  public AbstractUser ()
  {
    this.type = PseudoDocType.USER;
    this.rootElementName = XMLElement.USER;
  }
}
