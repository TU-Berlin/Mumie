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

/**
 * Default implementation of {@link Tutorial}.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: DefaultTutorial.java,v 1.21 2009/03/05 10:42:42 mumie Exp $</code>
 */

public class DefaultTutorial extends ExternallyControlledPseudoDocument
  implements Tutorial
{
  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(DefaultTutorial.class);

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
   *       <li>{@link DbColumn#CLASS CLASS}</li>
   *       <li>{@link DbColumn#TUTOR TUTOR}</li>
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
   *       <li>{@link DbColumn#CLASS CLASS}</li>
   *       <li>{@link DbColumn#TUTOR TUTOR}</li>
   *       <li>{@link DbColumn#DESCRIPTION DESCRIPTION}</li>
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
   *       <li>{@link DbColumn#CLASS CLASS}</li>
   *       <li>{@link DbColumn#TUTOR TUTOR}</li>
   *       <li>{@link DbColumn#DESCRIPTION DESCRIPTION}</li>
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
	      DbColumn.SECTION_PATH,
	      DbColumn.PURE_NAME,
	      DbColumn.SYNC_ID,
	      DbColumn.NAME,
	      DbColumn.DESCRIPTION,
	      DbColumn.CLASS,
	      DbColumn.TUTOR,
	      DbColumn.CAPACITY,
	      DbColumn.OCCUPANCY,
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
	      DbColumn.CLASS,
	      DbColumn.TUTOR,
	      DbColumn.CAPACITY,
	      DbColumn.OCCUPANCY,
	      DbColumn.DESCRIPTION,
	      DbColumn.CREATED,
	      DbColumn.LAST_MODIFIED,
	    };
	case UseMode.INFO :
	  return new String []
	    {
	      DbColumn.ID,
	      DbColumn.SECTION_PATH,
	      DbColumn.PURE_NAME,
	      DbColumn.CONTAINED_IN,
	      DbColumn.SYNC_ID,
	      DbColumn.NAME,
	      DbColumn.CLASS,
	      DbColumn.TUTOR,
	      DbColumn.CAPACITY,
	      DbColumn.OCCUPANCY,
	      DbColumn.DESCRIPTION,
	      DbColumn.CREATED,
	      DbColumn.LAST_MODIFIED,
	    };
	case UseMode.CHECKOUT :
	  // TODO: "copy(...)" the columns
	  return new String[]
	    {
	      DbColumn.ID,
	      DbColumn.SECTION_PATH,
	      DbColumn.PURE_NAME,
	      DbColumn.SYNC_ID,
	      DbColumn.NAME,
	      DbColumn.CLASS,
	      DbColumn.TUTOR,
	      DbColumn.CAPACITY,
	      DbColumn.OCCUPANCY,
	      DbColumn.DESCRIPTION,
	      DbColumn.CREATED,
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
	  return new String []
	    {
	      DbColumn.ID,
	      DbColumn.SYNC_ID,
	      DbColumn.NAME,
              DbColumn.PURE_NAME,
	      DbColumn.DESCRIPTION,
	      DbColumn.CLASS,
	      DbColumn.TUTOR,
	      DbColumn.CAPACITY,
	      DbColumn.OCCUPANCY,
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
	      DbColumn.PURE_NAME,
	      DbColumn.NAME,
	      DbColumn.CLASS,
	      DbColumn.TUTOR,
	      DbColumn.CAPACITY,
	      DbColumn.OCCUPANCY,
	      DbColumn.DESCRIPTION,
	      DbColumn.CREATED,
	      DbColumn.LAST_MODIFIED,
	    };
	case UseMode.INFO :
	  return new String []
	    {
	      DbColumn.ID,
	      DbColumn.SECTION_PATH,
	      DbColumn.PURE_NAME,
	      DbColumn.CONTAINED_IN,
	      DbColumn.SYNC_ID,
	      DbColumn.NAME,
	      DbColumn.CLASS,
	      DbColumn.CAPACITY,
	      DbColumn.OCCUPANCY,
	      DbColumn.TUTOR,
	      DbColumn.DESCRIPTION,
	      DbColumn.CREATED,
	      DbColumn.LAST_MODIFIED,
	    };
	default:
	  throw new IllegalStateException
	    (this + ": Method \"getDbColumns\" not implemented for use mode: " + UseMode.nameFor[this.useMode] + ", this.withPath: " + this.withPath);
	}
  }

  /**
   * Writes the {@link XMLElement#CAPACITY CAPACITY} element with the section's id.
   */

  protected void capacityToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException, SQLException
  {
    try
      {
	int capacity = resultSet.getInt(DbColumn.CAPACITY);
	if ( !resultSet.wasNull() )
	  {
	    this.xmlElement.reset();
	    this.xmlElement.setLocalName(XMLElement.CAPACITY);
	    this.xmlElement.addAttribute(XMLAttribute.VALUE, capacity);
	    this.xmlElement.toSAX(contentHandler);
	  }
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("containedInToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#OCCUPANCY OCCUPANCY} element with the section's id.
   */

  protected void occupancyToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException, SQLException
  {
    try
      {
	this.xmlElement.reset();
	this.xmlElement.setLocalName(XMLElement.OCCUPANCY);
	this.xmlElement.addAttribute(XMLAttribute.VALUE, resultSet.getInt(DbColumn.OCCUPANCY));
	this.xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("containedInToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the <code>mumie:class</code> element.
   */

  protected void classToSAX (ResultSet resultSet,
                             ContentHandler contentHandler)
    throws SAXException, SQLException
  {
    final String METHOD_NAME =
      "classToSAX((ResultSet resultSet, ContentHandler contentHandler)";
    this.logDebug(METHOD_NAME + "1/2: Started");
    ELClass elClass = null;
    try
      {
        int classId = resultSet.getInt(DbColumn.CLASS);
        elClass = (ELClass)this.serviceManager.lookup(ELClass.ROLE);
        elClass.setId(classId);
        elClass.setUseMode(UseMode.COMPONENT);
        elClass.toSAX(contentHandler, false);
        // elClass.toSAX(resultSet, DB_PREFIX_CLASS, contentHandler, false);
        this.logDebug(METHOD_NAME + "2/2: Done");
      }
    catch (Exception exception)
      {
        throw new SAXException(exception);
      }
    finally
      {
        if ( elClass != null )
          this.serviceManager.release(elClass);
      }
  }

  /**
   * Writes the <code>mumie:tutor</code> element.
   */

  protected void tutorToSAX (ResultSet resultSet,
                             ContentHandler contentHandler)
    throws SAXException, SQLException
  {
    final String METHOD_NAME =
      "tutorToSAX((ResultSet resultSet, ContentHandler contentHandler)";
    this.logDebug(METHOD_NAME + "1/2: Started");
    User tutor = null;
    try
      {
        // Start the mumie:tutor element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.TUTOR);
        this.xmlElement.startToSAX(contentHandler);

        // Write XML representation of the tutor:
        int tutorId = resultSet.getInt(DbColumn.TUTOR);
        tutor = (User)this.serviceManager.lookup(GeneralUser.ROLE);
        tutor.setId(tutorId);
        tutor.setUseMode(UseMode.COMPONENT);
        tutor.toSAX(contentHandler, false);
        // tutor.toSAX(resultSet, DB_PREFIX_TUTOR, contentHandler, false);

        // Close the mumie:tutor element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.TUTOR);
        this.xmlElement.endToSAX(contentHandler);

        this.logDebug(METHOD_NAME + "2/2: Done");
      }
    catch (Exception exception)
      {
        throw new SAXException(exception);
      }
    finally
      {
        if ( tutor != null )
          this.serviceManager.release(tutor);
      }
  }

  /**
   * Writes the members this tutorial to SAX.
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
          this.dbHelper.queryTutorialMembers(this.id, member.getDbColumns());

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
   * Called from {@link #toSAX} if {@link #useMode} is {@link UseMode#COMPONENT COMPONENT}.
   */

  protected void toSAX_COMPONENT (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception
  {
    final String METHOD_NAME = "toSAX_COMPONENT(ContentHandler contentHandler)";
    this.logDebug(METHOD_NAME + "1/2: Started. resultSet = " + resultSet);
    this.startRootElement(contentHandler);
    this.syncIdToSAX(resultSet, contentHandler);
    this.nameToSAX(resultSet, contentHandler);
    this.pureNameToSAX(resultSet, contentHandler);
    this.descriptionToSAX(resultSet, contentHandler);
    this.classToSAX(resultSet, contentHandler);
    this.tutorToSAX(resultSet, contentHandler);
    this.capacityToSAX(resultSet, contentHandler);
    this.occupancyToSAX(resultSet, contentHandler);
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
    this.syncIdToSAX(resultSet, contentHandler);
    this.pureNameToSAX(resultSet, contentHandler);
    this.nameToSAX(resultSet, contentHandler);
    this.descriptionToSAX(resultSet, contentHandler);
    this.createdToSAX(resultSet, contentHandler);
    this.lastModifiedToSAX(resultSet, contentHandler);
    this.classToSAX(resultSet, contentHandler);
    this.tutorToSAX(resultSet, contentHandler);
    this.capacityToSAX(resultSet, contentHandler);
    this.occupancyToSAX(resultSet, contentHandler);
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
    this.containedInToSAX(resultSet, contentHandler);
    this.nameToSAX(resultSet, contentHandler);
    this.descriptionToSAX(resultSet, contentHandler);
    this.createdToSAX(resultSet, contentHandler);
    this.lastModifiedToSAX(resultSet, contentHandler);
    this.classToSAX(resultSet, contentHandler);
    this.tutorToSAX(resultSet, contentHandler);
    this.capacityToSAX(resultSet, contentHandler);
    this.occupancyToSAX(resultSet, contentHandler);
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
   * Returns a string that identificates this instance. It has the
   * following form:<pre>
   *   "DefaultTutorial" +
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
      "DefaultTutorial" +
      '#' + this.instanceStatus.getInstanceId() +
      '(' + this.instanceStatus.getNumberOfRecycles() +
      ',' + this.id +
      ',' + this.useMode +
      ')';
  }

  /**
   * Creates a <code>DefaultTutorial</code> instance and sets {@link #type type} and
   * {@link #rootElementName rootElementName} to the appropriate values.
   */

  public DefaultTutorial ()
  {
    this.type = PseudoDocType.TUTORIAL;
    this.rootElementName = XMLElement.TUTORIAL;
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
