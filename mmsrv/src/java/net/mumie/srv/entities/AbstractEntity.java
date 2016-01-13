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

package net.mumie.srv.entities;

import java.io.InputStream;
import java.sql.ResultSet;
import java.util.Date;
import net.mumie.srv.db.DbHelper;
import net.mumie.srv.entities.pseudodocs.UserGroup;
import net.mumie.srv.notions.Category;
import net.mumie.srv.notions.DbColumn;
import net.mumie.srv.notions.EntityType;
import net.mumie.srv.notions.Id;
import net.mumie.srv.notions.MediaType;
import net.mumie.srv.notions.UseMode;
import net.mumie.srv.notions.XMLAttribute;
import net.mumie.srv.notions.XMLElement;
import net.mumie.srv.service.AbstractMumieServiceable;
import net.mumie.srv.util.LogUtil;
import net.mumie.srv.xml.FragmentSAXFilter;
import net.mumie.srv.xml.MetaXMLElement;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.ProcessingException;
import org.apache.excalibur.xml.sax.SAXParser;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

public abstract class AbstractEntity extends AbstractMumieServiceable
  implements Entity, Serviceable, Recyclable, Disposable
{
  // --------------------------------------------------------------------------------
  // h1: Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The type of this entity.
   */

  protected int type;

  /**
   * The id of this entity.
   */

  protected int id = Id.UNDEFINED;

  /**
   * The path of this entity, or null if not set. This variable is used only if needed.
   */

  protected String path = null;

  /**
   * Whether the path should be included in the XML. Set by
   * {@link #setWithPath setWithPath}.
   */

  protected boolean withPath = false;

  /**
   * The use mode of this content object.
   */

  protected int useMode = UseMode.UNDEFINED;

  /**
   * The db helper of this content object.
   */

  protected DbHelper dbHelper = null;

  /**
   * Helper to write Meta XML elements to SAX.
   */

  protected MetaXMLElement xmlElement = new MetaXMLElement ();

  /**
   * The local name of the XML root element. <code>null</code> in this implementation, must
   * be set by extending classes.
   */

  protected String rootElementName = null;

  // --------------------------------------------------------------------------------
  // h1: Lifecycle methods
  // --------------------------------------------------------------------------------

  /**
   * Makes this instance ready to represent another entity. Resets {@link #id id},
   * {@link #path path}, {@link #withPath withPath}, {@link #useMode useMode} and
   * {@link #xmlElement xmlElement}. Note: {@link #type type} is not reset, since it is
   * constant in most implementations.
   */

  public void reset()
  {
    this.id = Id.UNDEFINED;
    this.path = null;
    this.withPath = false;
    this.useMode = UseMode.UNDEFINED;
    this.xmlElement.reset();
  }

  /**
   * <p>
   *   Releases all resources hold by this object.
   * </p>
   * <p>
   *   This implementation only releases the db helper. Extending classes may overwrite this
   *   to release other resources as well.
   * </p>
   */

  protected void releaseResources ()
  {
    if ( this.dbHelper != null ) 
      {
        this.serviceManager.release(this.dbHelper);
        this.dbHelper = null;
      }
  }

  /**
   * <p>
   *   Recycles this instance.
   * </p>
   * <p>
   *   Calls the {@link #reset reset} and {@link #releaseResources releaseResources}
   *   methods. 
   * </p>
   */

  public void recycle ()
  {
    this.reset();
    this.releaseResources();
  }

  /**
   * <p>
   *   Disposes this instance.
   * </p>
   * <p>
   *   Calls the {@link #reset reset} and {@link #releaseResources releaseResources}
   *   methods. 
   * </p>
   */

  public void dispose ()
  {
    this.reset();
    this.releaseResources();
  }

  // --------------------------------------------------------------------------------
  // h1: Set methods
  // --------------------------------------------------------------------------------

  /**
   * Sets the id of this entity.
   */

  public void setId (int id)
  {
    this.id = id;
  }

  /**
   * Sets the use mode of this instance.
   */

  public void setUseMode (int useMode)
  {
    this.useMode = useMode;
  }

  /**
   * Sets whether the path should be included in the XML.
   */

  public void setWithPath (boolean withPath)
  {
    this.withPath = withPath;
  }

  // --------------------------------------------------------------------------------
  // h1: Get methods
  // --------------------------------------------------------------------------------

  /**
   * Returns the type of this entity, or {@link EntityType#UNDEFINED EntityType.UNDEFINED}
   * if the type is not set.
   */

  public final int getType ()
  {
    return this.type;
  }

  /**
   * Returns the id of this entity, or {@link Id#UNDEFINED Id.UNDEFINED} if the id id not
   * set.
   */

  public final int getId ()
  {
    return this.id;
  }

  /**
   * Returns the use mode of this instance, or {@link UseMode#UNDEFINED UseMode.UNDEFINED}
   * if the use mode is not set.
   */

  public final int getUseMode ()
  {
    return this.useMode;
  }

  /**
   * Returns whether the path of the entity is included in the XML output.
   */

  public final boolean getWithPath ()
  {
    return this.withPath;
  }

  // --------------------------------------------------------------------------------
  // h1: DbHelper methods
  // --------------------------------------------------------------------------------

  /**
   * Ensures that {@link #dbHelper} is initialized. If {@link #dbHelper} is null, gets a db
   * helper from the service manager ({@link #serviceManager}) and sets {@link #dbHelper} to
   * it. If {@link #dbHelper} is not null, does nothing.
   */

  protected void ensureDbHelper ()
    throws ServiceException
  {
    final String METHOD_NAME = "ensureDbHelper";
    if ( this.dbHelper == null )
      {
        this.dbHelper = (DbHelper)this.serviceManager.lookup(DbHelper.ROLE);
        this.logDebug
          (METHOD_NAME + " 1/1: Done." +
           " this.dbHelper = " + LogUtil.identify(this.dbHelper));
      }
  }

  // --------------------------------------------------------------------------------
  // h1: Querying information about the entity
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Returns the time this entity was created.
   * </p>
   * <p>
   *   It is allowed that the method returns <code>null</code>, which means that the last
   *   modification time is unknown.
   * </p>
   *
   * @return the last modification time of the document.
   *
   * @throws ProcessingException if something goes wrong
   */

  public Date getCreated ()
    throws ProcessingException
  {
    final String METHOD_NAME = "getCreated";
    this.logDebug(METHOD_NAME + ": 1/2: Started");
    try
      {
        this.ensureDbHelper();
        Date created =
          this.dbHelper.queryEntityDatumAsTimestamp(this.type, this.id, DbColumn.LAST_MODIFIED);
        this.logDebug
          (METHOD_NAME + ": 2/2: Done." +
           " created = " + LogUtil.dateToString(created));
        return created;
      }
    catch (Exception exception)
      {
        throw new ProcessingException(exception);
      }
  }

  /**
   * <p>
   *   Returns the time this entity was last modified.
   * </p>
   * <p>
   *   It is allowed that the method returns <code>null</code>, which means that the last
   *   modification time is unknown.
   * </p>
   *
   * @return the last modification time of the document.
   *
   * @throws ProcessingException if something goes wrong
   */

  public Date getLastModified ()
    throws ProcessingException 
  {
    final String METHOD_NAME = "getLastModified";
    this.logDebug(METHOD_NAME + ": 1/2: Started");
    try
      {
        this.ensureDbHelper();
        Date lastModified =
          this.dbHelper.queryEntityDatumAsTimestamp(this.type, this.id, DbColumn.LAST_MODIFIED);
        this.logDebug
          (METHOD_NAME + ": 2/2: Done." +
           " lastModified = " + LogUtil.dateToString(lastModified));
        return lastModified;
      }
    catch (Exception exception)
      {
        throw new ProcessingException(exception);
      }
  }

  // --------------------------------------------------------------------------------
  // h1: Auxiliaries for the getDbColumn method
  // --------------------------------------------------------------------------------

  /**
   * Auxiliary method to implement {@link getDbColumn}. Returns a copy of the specified
   * array of column names. If <code>addPathColumn</code> is true,
   * {@link DbColumn.SECTION_PATH DbColumn.SECTION_PATH} is added to the copy.
   */

  protected static String[] copyDbColums (String[] srcColumns, boolean addPathColumn)
  {
    int length = srcColumns.length;
    if ( addPathColumn ) length++;
    String[] columns = new String[length];
    System.arraycopy(srcColumns, 0, columns, 0, length);
    if ( addPathColumn ) columns[length-1] = DbColumn.SECTION_PATH;
    return columns;
  }

  // --------------------------------------------------------------------------------
  // h1: toSAX methods for the root element
  // --------------------------------------------------------------------------------

  /**
   * Writes the root element start tag. <code>contentHandler</code> is the content Handler
   * that recieves the SAX event.
   */

  protected void rootElementStartToSAX (ContentHandler contentHandler)
    throws SAXException
  {
    this.xmlElement.reset();
    this.xmlElement.setLocalName(EntityType.xmlElementOf(this.type));
    this.xmlElement.addAttribute(XMLAttribute.ID, this.id);
    this.xmlElement.startToSAX(contentHandler);
  }

  /**
   * Writes the root element end tag. <code>contentHandler</code> is the content Handler
   * that recieves the SAX event.
   */

  protected void rootElementEndToSAX (ContentHandler contentHandler)
    throws SAXException
  {
    this.xmlElement.reset();
    this.xmlElement.setLocalName(EntityType.xmlElementOf(this.type));
    this.xmlElement.endToSAX(contentHandler);
  }

  // --------------------------------------------------------------------------------
  // h1: toSAX methods for specific data
  // --------------------------------------------------------------------------------

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Name, description
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Writes the {@link XMLElement#NAME NAME} element.
   */

  protected void nameToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.NAME);
        this.xmlElement.setText(resultSet.getString(DbColumn.NAME));
        this.xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("nameToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#DESCRIPTION DESCRIPTION} element.
   */

  protected void descriptionToSAX (ResultSet resultSet,
                                   ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.DESCRIPTION);
        this.xmlElement.setText(resultSet.getString(DbColumn.DESCRIPTION));
        this.xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("descriptionToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Pure name
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Writes the {@link XMLElement#PURE_NAME PURE_NAME} element.
   */

  protected void pureNameToSAX (ResultSet resultSet,
                                ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.PURE_NAME);
        this.xmlElement.setText(resultSet.getString(DbColumn.PURE_NAME));
        this.xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("pureNameToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Contained-in
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Writes the {@link XMLElement#CONTAINED_IN CONTAINED_IN} element.
   */

  protected void containedInToSAX (ResultSet resultSet,
                                   ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.CONTAINED_IN);
        this.xmlElement.addAttribute
          (XMLAttribute.ID, resultSet.getString(DbColumn.CONTAINED_IN));
        this.xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("containedInToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Hide, deleted
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Writes the {@link XMLElement#HIDE HIDE} element.
   */

  protected void hideToSAX (ResultSet resultSet,
			    ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.HIDE);
        this.xmlElement.addAttribute(XMLAttribute.VALUE, resultSet.getBoolean(DbColumn.HIDE));
        this.xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("hideToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#DELETED DELETED} element.
   */

  protected void deletedToSAX (ResultSet resultSet,
                               ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
	String deleted = ( resultSet.getBoolean(DbColumn.DELETED) ? "true" : "false");
	this.xmlElement.reset();
	this.xmlElement.setLocalName(XMLElement.DELETED);
	this.xmlElement.addAttribute(XMLAttribute.VALUE, deleted);
	this.xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("deletedToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Metainfos with time values
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  // ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
  // h3: Generic base method
  // ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~

  /**
   * Writes a time value to SAX.
   * @param resultSet result of the database query
   * @param contentHandler recieves the SAX events.
   * @param columnName the column to look up in <code>resultSet</code>
   * @param elementName local name of the XML element.
   */

  protected void timeToSAX (ResultSet resultSet,
                            ContentHandler contentHandler,
                            String columnName,
                            String elementName)
    throws SAXException
  {
    try
      {
        Date date = resultSet.getTimestamp(columnName);
	if ( date != null )
	  {
	    this.xmlElement.reset();
	    this.xmlElement.setLocalName(elementName);
	    this.xmlElement.addAttribute(XMLAttribute.VALUE, date);
	    this.xmlElement.addAttribute(XMLAttribute.RAW, date.getTime());
	    this.xmlElement.toSAX(contentHandler);
	  }
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("timeToSAX for column" + columnName + ": " +
           this.xmlElement.statusToString(), exception);
      }
  }

  // ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
  // h3: Created, last modified
  // ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~

  /**
   * Writes the {@link XMLElement#CREATED CREATED} element.
   */

  protected void createdToSAX (ResultSet resultSet,
                               ContentHandler contentHandler)
    throws SAXException
  {
    timeToSAX(resultSet, contentHandler, DbColumn.CREATED, XMLElement.CREATED);
  }

  /**
   * Writes the {@link XMLElement#LAST_MODIFIED LAST_MODIFIED} element.
   */

  protected void lastModifiedToSAX (ResultSet resultSet,
                                    ContentHandler contentHandler)
    throws SAXException
  {
    timeToSAX(resultSet, contentHandler, DbColumn.LAST_MODIFIED, XMLElement.LAST_MODIFIED);
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Permissions
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Writes the {@link XMLElement#READ_PERMISSIONS READ_PERMISSIONS} element.
   */

  protected void readPermissionsToSAX (ContentHandler contentHandler)
    throws SAXException
  {
    final String METHOD_NAME = "readPermissionsToSAX";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    UserGroup userGroup = null;
    try
      {
        userGroup = (UserGroup)this.serviceManager.lookup(UserGroup.ROLE);
        userGroup.setUseMode(UseMode.LINK);
        userGroup.setWithPath(this.withPath);
        String[] columns = userGroup.getDbColumns();

        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.READ_PERMISSIONS);
        this.xmlElement.startToSAX(contentHandler);

        this.ensureDbHelper();
        ResultSet resultSet = this.dbHelper.queryReadPermissions(this.type, this.id, columns);
        while ( resultSet.next() )
          {
            userGroup.reset();
            userGroup.setId(resultSet.getInt(DbColumn.ID));
            userGroup.setUseMode(UseMode.LINK);
            userGroup.setWithPath(this.withPath);
            userGroup.toSAX(resultSet, contentHandler, false);
          }

        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.READ_PERMISSIONS);
        this.xmlElement.endToSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException(exception);
      }
    finally
      {
        if ( userGroup != null ) this.serviceManager.release(userGroup);
      }
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Writes the {@link XMLElement#WRITE_PERMISSIONS WRITE_PERMISSIONS} element.
   */

  protected void writePermissionsToSAX (ContentHandler contentHandler)
    throws SAXException
  {
    final String METHOD_NAME = "writePermissionsToSAX";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    UserGroup userGroup = null;
    try
      {
        userGroup = (UserGroup)this.serviceManager.lookup(UserGroup.ROLE);
        userGroup.setUseMode(UseMode.LINK);
        userGroup.setWithPath(this.withPath);
        String[] columns = userGroup.getDbColumns();

        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.WRITE_PERMISSIONS);
        this.xmlElement.startToSAX(contentHandler);

        this.ensureDbHelper();
        ResultSet resultSet = this.dbHelper.queryWritePermissions(this.type, this.id, columns);
        while ( resultSet.next() )
          {
            userGroup.reset();
            userGroup.setId(resultSet.getInt(DbColumn.ID));
            userGroup.setUseMode(UseMode.LINK);
            userGroup.setWithPath(this.withPath);
            userGroup.toSAX(resultSet, contentHandler, false);
          }

        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.WRITE_PERMISSIONS);
        this.xmlElement.endToSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException(exception);
      }
    finally
      {
        if ( userGroup != null ) this.serviceManager.release(userGroup);
      }
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: XML data
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  // ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
  // h3: Auxiliary method to parse XML
  // ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~

  /**
   * Parses the specified input stream by means of a SAX parser and outputs the SAX
   * events to the specified content handler. The <code>startDocument</code> and
   * <code>endDocument</code> events are suppressed (i.e., this events are not passed 
   * to the content handler). This is because it should be possible to insert the XML
   * read from the stream as a part of a larger XML document.
   */

  protected void xmlCodeToSAX (InputStream stream,
			       ContentHandler contentHandler)
    throws SAXException
  {
    final String METHOD_NAME = "xmlCodeToSAX";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    SAXParser parser = null;
    FragmentSAXFilter fragmentSAXFilter = null;
    try
      {
	parser = (SAXParser)this.serviceManager.lookup(SAXParser.ROLE);
        fragmentSAXFilter =
          (FragmentSAXFilter)this.serviceManager.lookup(FragmentSAXFilter.ROLE);
        fragmentSAXFilter.setContentHandler(contentHandler);
	parser.parse(new InputSource(stream), fragmentSAXFilter);
      }
    catch (Exception exception)
      {
        throw new SAXException(exception);
      }
    finally
      {
	if (fragmentSAXFilter != null)
          this.serviceManager.release(fragmentSAXFilter);
	if (parser != null )
          this.serviceManager.release(parser);
      }
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // h1: Other auxiliaries for the toSAX methods
  // --------------------------------------------------------------------------------

  /**
   * Returns the result set needed in the toSAX method.
   */

  protected ResultSet getResultSet ()
    throws SQLException, ServiceException
  {
    return this.dbHelper.queryEntity(this.type, this.id, this.getDbColumns());
  }

  // --------------------------------------------------------------------------------
  // h1: toSAX methods
  // --------------------------------------------------------------------------------

  /**
   * Throws a {@link SAXException SAXException} saying that this entity is not representable
   * as XML in the current use mode.
   */

  protected void signalNotRepresentableAsXML ()
    throws SAXException
  {
    throw new SAXException
      ("Not representable as XML in use mode \"" + UseMode.nameFor(this.useMode) + "\"");
  }

  /**
   * Core part of the toSAX method if the use mode is {@link UseMode#SERVE SERVE}. This
   * implementation simply calls {@link #signalNotRepresentableAsXML}. Extending classes
   * should overwrite this if necessary.
   */

  protected void toSAX_SERVE (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception
  {
    this.signalNotRepresentableAsXML();
  }

  /**
   * Core part of the toSAX method if the use mode is {@link UseMode#INFO INFO}. This
   * implementation simply calls {@link #signalNotRepresentableAsXML}. Extending classes
   * should overwrite this if necessary.
   */

  protected void toSAX_INFO (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception
  {
    this.signalNotRepresentableAsXML();
  }

  /**
   * Core part of the toSAX method if the use mode is {@link UseMode#COMPONENT COMPONENT}.
   * This implementation simply calls {@link #signalNotRepresentableAsXML}. Extending
   * classes should overwrite this if necessary.
   */

  protected void toSAX_COMPONENT (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception
  {
    this.signalNotRepresentableAsXML();
  }

  /**
   * Core part of the toSAX method if the use mode is {@link UseMode#LINK LINK}. This
   * implementation simply calls {@link #signalNotRepresentableAsXML}. Extending classes
   * should overwrite this if necessary.
   */

  protected void toSAX_LINK (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception
  {
    this.signalNotRepresentableAsXML();
  }

  /**
   * Core part of the toSAX method if the use mode is {@link UseMode#CHECKOUT CHECKOUT}.
   * This implementation simply calls {@link #signalNotRepresentableAsXML}. Extending
   * classes should overwrite this if necessary.
   */

  protected void toSAX_CHECKOUT (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception
  {
    this.signalNotRepresentableAsXML();
  }

  /**
   * <p>
   *   Sends this entity as SAX events to the specified content handler. 
   * </p>
   * <p>
   *   If <code>resultSet</code> is non-null, the method requests the necessary data from
   *   the database; otherwise, it tries to extract them from <code>resultSet</code>. 
   * </p>
   * <p>
   *   If <code>ownDocument</code> is true, the <code>startDocument</code> and
   *   <code>endDocument</code> method of <code>contentHandler</code> is called before
   *   resp. after the XML is sent to SAX.
   * </p>
   */

  public void toSAX (ResultSet resultSet, ContentHandler contentHandler, boolean ownDocument)
    throws SAXException
  {
    try
      {
        final String METHOD_NAME = "toSAX";
        this.logDebug(METHOD_NAME + " 1/2: Started");
        if ( resultSet == null )
          {
            this.ensureDbHelper();
            resultSet = this.getResultSet();
            if ( !resultSet.next() )
              throw new SAXException("resultSet empty");
          }
        if ( this.withPath )
	  {
	    String sectionPath = resultSet.getString(DbColumn.SECTION_PATH);
	    String pureName = resultSet.getString(DbColumn.PURE_NAME);
	    this.path = (sectionPath.equals("") ? pureName : sectionPath + "/" + pureName);
	  }
        if ( ownDocument ) contentHandler.startDocument();
        switch ( this.useMode )
          {
          case UseMode.COMPONENT:
            this.toSAX_COMPONENT(resultSet, contentHandler); break;
          case UseMode.LINK:
            this.toSAX_LINK(resultSet, contentHandler); break;
          case UseMode.SERVE:
            this.toSAX_SERVE(resultSet, contentHandler); break;
          case UseMode.INFO :
            this.toSAX_INFO(resultSet, contentHandler); break;
          case UseMode.CHECKOUT:
            this.toSAX_CHECKOUT(resultSet, contentHandler); break;
          default:
            throw new SAXException
              ("Method \"toSAX\" not implemented for use mode: " + this.useMode);
          }
        if ( ownDocument ) contentHandler.endDocument();
        this.logDebug(METHOD_NAME + "2/2: Done");
      }
    catch (Exception exception)
      {
        throw new SAXException(exception);
      }
  }

  /**
   * <p>
   *   Sends this entity as SAX events to <code>contentHandler</code>.
   * </p>
   * <p>
   *   If <code>ownDocument</code> is true, the <code>startDocument</code> and
   *   <code>endDocument</code> method of <code>contentHandler</code> is called before
   *   resp. after the XML is sent to SAX.
   * </p>
   */

  public void toSAX (ContentHandler contentHandler, boolean ownDocument)
    throws SAXException
  {
    this.toSAX(null, contentHandler, ownDocument);
  }

  /**
   * <p>
   *   Sends this entity as SAX events to <code>contentHandler</code>.
   * </p>
   * <p>
   *   The <code>startDocument</code> and <code>endDocument</code> method of
   *   <code>contentHandler</code> is called before resp. after the XML is sent to SAX.
   * </p>
   */

  public void toSAX (ContentHandler contentHandler)
    throws SAXException
  {
    this.toSAX(null, contentHandler, true);
  }

  // --------------------------------------------------------------------------------
  // h1: toStream methods for specified data
  // --------------------------------------------------------------------------------

  // -- Nothing here; respective methods are defined in subclasses --

  // --------------------------------------------------------------------------------
  // h1: toStream methods
  // --------------------------------------------------------------------------------

  /**
   * Writes specified BLOB column to the specified output stream.
   */

  protected void blobToStream (String column, OutputStream out)
    throws IOException, SQLException
  {
    final String METHOD_NAME = "blobToStream";
    this.getLogger().debug(METHOD_NAME + "1/2: Started. column = " + column);
    this.dbHelper.queryAndWriteEntityBLOB
      (this.type, this.id, column, out);
    this.getLogger().debug(METHOD_NAME + "2/2: Done");
  }

  /**
   * Throws a {@link IOException IOException} saying that the entity is not
   * representable as a stream in the current use mode.
   */

  protected void signalNotRepresentableAsStream ()
    throws IOException
  {
    throw new IOException
      ("Not representable as stream in use mode \"" + UseMode.nameFor(this.useMode) + "\"");
  }

  /**
   * Core part of the toStream method if the use mode is {@link UseMode#SERVE SERVE}.
   * This implementation simply calls {@link #signalNotRepresentableAsStream()}. Extending
   * classes should overwrite this if necessary.
   */

  protected void toStream_SERVE (OutputStream out)
    throws SQLException, IOException
  {
    this.signalNotRepresentableAsStream();
  }

  /**
   * Core part of the toStream method if the use mode is {@link UseMode#INFO INFO}.
   * This implementation simply calls {@link #signalNotRepresentableAsStream()}. Extending
   * classes should overwrite this if necessary.
   */

  protected void toStream_INFO (OutputStream out)
    throws SQLException, IOException
  {
    this.signalNotRepresentableAsStream();
  }

  /**
   * Core part of the toStream method if the use mode is {@link UseMode#COMPONENT COMPONENT}.
   * This implementation simply calls {@link #signalNotRepresentableAsStream()}. Extending
   * classes should overwrite this if necessary.
   */

  protected void toStream_COMPONENT (OutputStream out)
    throws SQLException, IOException
  {
    this.signalNotRepresentableAsStream();
  }

  /**
   * Core part of the toStream method if the use mode is {@link UseMode#LINK LINK}.
   * This implementation simply calls {@link #signalNotRepresentableAsStream()}. Extending
   * classes should overwrite this if necessary.
   */

  protected void toStream_LINK (OutputStream out)
    throws SQLException, IOException
  {
    this.signalNotRepresentableAsStream();
  }

  /**
   * Core part of the toStream method if the use mode is {@link UseMode#CHECKOUT CHECKOUT}.
   * This implementation simply calls {@link #signalNotRepresentableAsStream()}. Extending
   * classes should overwrite this if necessary.
   */

  protected void toStream_CHECKOUT (OutputStream out)
    throws SQLException, IOException
  {
    this.signalNotRepresentableAsStream();
  }

  /**
   * Writes a representation of this entity to <code>out</code>.
   *
   * @throws java.io.IOException if something goes wrong.
   */

  public void toStream (OutputStream out)
    throws IOException
  {
    final String METHOD_NAME = "toStream";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    try
      {
	switch ( this.useMode )
	  {
	  case UseMode.COMPONENT : toStream_COMPONENT(out); break;
	  case UseMode.LINK : toStream_LINK(out); break;
	  case UseMode.SERVE : toStream_SERVE(out); break;
	  case UseMode.INFO : toStream_INFO(out); break;
	  case UseMode.CHECKOUT : toStream_CHECKOUT(out); break;
	  default : this.signalNotRepresentableAsStream();
	  }
	this.logDebug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
	throw new IOException (exception.getMessage());
      }
  }


}
