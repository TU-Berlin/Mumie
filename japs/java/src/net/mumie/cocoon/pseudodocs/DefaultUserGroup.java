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
import java.util.Iterator;
import java.util.List;
import net.mumie.cocoon.documents.Document;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.UseMode;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import org.apache.avalon.framework.service.ServiceException;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class DefaultUserGroup extends AbstractPseudoDocument
  implements UserGroup
{
  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(DefaultUserGroup.class);

  /**
   * Returns all columns of the user group table.
   */

  protected String[] getAllDbColumns ()
  {
    String[] columns = new String[7 + DocType.last - DocType.first + 1];
    int i = -1;
    columns[++i] = DbColumn.ID;
    columns[++i] = DbColumn.SECTION_PATH;
    columns[++i] = DbColumn.PURE_NAME;
    columns[++i] = DbColumn.CONTAINED_IN;
    columns[++i] = DbColumn.NAME;
    columns[++i] = DbColumn.DESCRIPTION;
    columns[++i] = DbColumn.LAST_MODIFIED;
    for (int docType = DocType.first; docType <= DocType.last; docType++)
      columns[++i] = DbColumn.mayCreate[docType];
    return columns;
  }

  /**
   * {@inheritDoc}
   */

  public String[] getDbColumns ()
  { 
    if ( this.withPath )
      switch ( this.useMode )
	{
	case UseMode.COMPONENT : 
	  return new String[]
	    {
	      DbColumn.ID,
	      DbColumn.SECTION_PATH,
	      DbColumn.PURE_NAME,
	      DbColumn.NAME,
	      DbColumn.DESCRIPTION,
	    };
	case UseMode.LINK :
	  return new String[]
	    {
	      DbColumn.ID,
	      DbColumn.SECTION_PATH,
	      DbColumn.PURE_NAME,
	      DbColumn.NAME,
	      DbColumn.DESCRIPTION,
	    };
	case UseMode.SERVE : 
	  return new String[]
	    {
	      DbColumn.ID,
	      DbColumn.SECTION_PATH,
	      DbColumn.PURE_NAME,
	      DbColumn.NAME,
	      DbColumn.DESCRIPTION,
	      DbColumn.LAST_MODIFIED,
	    };
	case UseMode.INFO :
	  return this.getAllDbColumns();
	case UseMode.CHECKOUT :
	  return new String[]
	    {
	      DbColumn.ID,
	      DbColumn.SECTION_PATH,
	      DbColumn.PURE_NAME,
	      DbColumn.NAME,
	      DbColumn.DESCRIPTION,
	      DbColumn.LAST_MODIFIED,
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
	      DbColumn.NAME,
        DbColumn.PURE_NAME,
	      DbColumn.DESCRIPTION,
	    };
	case UseMode.LINK :
	  return new String[]
	    {
	      DbColumn.ID,
	      DbColumn.NAME,
	      DbColumn.DESCRIPTION,
	    };
	case UseMode.SERVE : 
	  return new String[]
	    {
	      DbColumn.ID,
	      DbColumn.PURE_NAME,
	      DbColumn.NAME,
	      DbColumn.DESCRIPTION,
	      DbColumn.LAST_MODIFIED,
	    };
	case UseMode.INFO :
	  return this.getAllDbColumns();
	default:
	  throw new IllegalStateException
	    (this + ": Method \"getDbColumns\" not implemented for use mode: " + UseMode.nameFor[this.useMode] + ", this.withPath: " + this.withPath);
	}
  }

  /**
   * Writes the members this user group to SAX.
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
          this.dbHelper.queryUserGroupMembers(this.id, member.getDbColumns());

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

  /**
   * Sends the create permissions of this group to SAX.
   */

  protected void createPermissionsToSAX (ResultSet resultSet,
                                        ContentHandler contentHandler)
    throws SQLException, SAXException
  {
    // Start CREATE_PERMISSIONS element:
    this.xmlElement.reset();
    this.xmlElement.setLocalName(XMLElement.CREATE_PERMISSIONS);
    this.xmlElement.startToSAX(contentHandler);

    // Write DOCUMENT_TYPE elements:
    for (int docType = DocType.first; docType <= DocType.last; docType++)
      {
        if ( resultSet.getBoolean(DbColumn.mayCreate[docType]) )
          {
            this.xmlElement.reset();
            this.xmlElement.setLocalName(XMLElement.DOCUMENT_TYPE);
            this.xmlElement.addAttribute(XMLAttribute.NAME, DocType.nameFor[docType]);
            this.xmlElement.toSAX(contentHandler);
          }
      }

    // Close CREATE_PERMISSIONS element:
    this.xmlElement.reset();
    this.xmlElement.setLocalName(XMLElement.CREATE_PERMISSIONS);
    this.xmlElement.endToSAX(contentHandler);
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
    this.nameToSAX(resultSet, contentHandler);
    this.pureNameToSAX(resultSet, contentHandler);
    this.descriptionToSAX(resultSet, contentHandler);
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
    this.pureNameToSAX(resultSet, contentHandler);
    this.nameToSAX(resultSet, contentHandler);
    this.descriptionToSAX(resultSet, contentHandler);
    this.lastModifiedToSAX(resultSet, contentHandler);
    this.createPermissionsToSAX(resultSet, contentHandler);
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
    this.containedInToSAX(resultSet, contentHandler);
    this.pureNameToSAX(resultSet, contentHandler);
    this.nameToSAX(resultSet, contentHandler);
    this.descriptionToSAX(resultSet, contentHandler);
    this.lastModifiedToSAX(resultSet, contentHandler);
    this.createPermissionsToSAX(resultSet, contentHandler);
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
    throw new Exception("Not yet implemented.");
    /* from AbstractUser:
       final String METHOD_NAME = "toSAX_CHECKOUT(ContentHandler contentHandler)";
       this.getLogger().debug(METHOD_NAME + "1/2: Started. resultSet = " + resultSet);

       this.startRootElement(contentHandler);
       if ( this.role > 0 ) this.roleToSAX(contentHandler);
       this.syncIdToSAX(resultSet, contentHandler);
       this.loginNameToSAX(resultSet, contentHandler);
       this.firstNameToSAX(resultSet, contentHandler);
       this.surnameToSAX(resultSet, contentHandler);
       this.emailToSAX(resultSet, contentHandler);
       this.themeToSAX(resultSet, contentHandler);
       this.createdToSAX(resultSet, contentHandler);
       this.lastLoginToSAX(resultSet, contentHandler);
       this.userGroupsToSAX(contentHandler);
       this.closeRootElement(contentHandler);

       this.getLogger().debug(METHOD_NAME + "2/2: Done");
    */
  }

  /**
   * {@inheritDoc}
   */

  public boolean hasReadPermission (int docType, int id)
    throws ServiceException
  {
    try
      {
        final String METHOD_NAME = "hasReadPermission(int docType, int id)";
        this.logDebug
          (METHOD_NAME + " 1/1:" +
           " docType = " + docType +
           ", id = " + id);
        this.ensureDbHelper();
        return
          this.dbHelper.checkUserGroupReadPermission(this.id, docType, id);
      }
    catch (Exception exception)
      {
        throw new ServiceException(this + ": Wrapped exception: ", exception);
      }
  }

  /**
   * {@inheritDoc}
   */

  public boolean hasReadPermission (Document document)
    throws ServiceException
  {
    return this.hasReadPermission(document.getType(), document.getId());
  }

  /**
   * {@inheritDoc}
   */

  public boolean hasWritePermission (int docType, int vcThreadId)
    throws ServiceException
  {
    try
      {
        final String METHOD_NAME = "hasWritePermission(int docType)";
        this.logDebug(METHOD_NAME + " 1/1: docType = " + docType);
        this.ensureDbHelper();
        return
          this.dbHelper.checkUserGroupWritePermission(this.id, docType, vcThreadId);
      }
    catch (Exception exception)
      {
        throw new ServiceException(this + ": Wrapped exception: ", exception);
      }
  }

  /**
   * {@inheritDoc}
   */

  public boolean hasCreatePermission (int docType)
    throws ServiceException
  {
    try
      {
        this.ensureDbHelper();
        return
          this.dbHelper.checkUserGroupCreatePermission(this.id, docType);
      }
    catch (Exception exception)
      {
        throw new ServiceException(this + ": Wrapped exception: ", exception);
      }
  }

  /**
   * {@inheritDoc}
   */

  public int[] getMembers ()
    throws ServiceException
  {
    List membersAsList = this.getMembersAsList();
    int[] members = new int[membersAsList.size()];
    Iterator iterator = membersAsList.iterator();
    int i = -1;
    while ( iterator.hasNext() )
      members[++i] = ((Integer)iterator.next()).intValue();
    return members;
  }

  /**
   * {@inheritDoc}
   */

  public List getMembersAsList ()
    throws ServiceException
  {
    try
      {
        this.ensureDbHelper();
        return this.dbHelper.queryUserGroupMembersAsList(this.id);
      }
    catch (Exception exception)
      {
        throw new ServiceException(this + ": Wrapped exception: ", exception);
      }
  }

  /**
   * Returns a string that identificates this instance. It has the
   * following form:<pre>
   *   "DefaultUserGroup" +
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
      "DefaultUserGroup" +
      '#' + this.instanceStatus.getInstanceId() +
      '(' + this.instanceStatus.getNumberOfRecycles() +
      ',' + this.id +
      ',' + this.useMode +
      ')';
  }

  /**
   * Creates a <code>DefaultUserGroup</code> instance and sets {@link #type type} and
   * {@link #rootElementName rootElementName} to the appropriate values.
   */

  public DefaultUserGroup ()
  {
    this.type = PseudoDocType.USER_GROUP;
    this.rootElementName = XMLElement.USER_GROUP;
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
}
