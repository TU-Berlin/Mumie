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
import java.util.Map;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.UseMode;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.util.LogUtil;
import net.mumie.cocoon.xml.MetaXMLElement;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import net.mumie.cocoon.service.AbstractJapsServiceable;

/**
 * Abstract base class for pseudo documents
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: AbstractPseudoDocument.java,v 1.34 2008/11/18 16:18:07 rassy Exp $</code>
 */

public abstract class AbstractPseudoDocument extends AbstractJapsServiceable 
  implements PseudoDocument, Serviceable, Recyclable, Disposable 
{
  // --------------------------------------------------------------------------------
  // Global variables
  // --------------------------------------------------------------------------------

  /**
   * The id of this pseudo-document.
   */

  protected int id = Id.UNDEFINED;

  /**
   * The path of this pseudo-document. Set only if needed, otherwise <code>null</code>.
   */

  protected String path = null;

  /**
   * Whether the path should be included in the XML. Set by
   * {@link #setWithPath setWithPath}
   */

  protected boolean withPath = false;

  /**
   * The type of this pseudo-document.
   */

  protected int type = PseudoDocType.UNDEFINED;

  /**
   * The use mode of this pseudo-document.
   */

  protected int useMode = UseMode.UNDEFINED;

  /**
   * The db helper of this pseudo-document.
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
  // Livecycle methods
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Makes this instance ready to represent another pseudo-document.
   * </p>
   * <p>
   *   Sets {@link #id} to {@link Id#UNDEFINED Id.UNDEFINED} and
   *   {@link #useMode} to {@link UseMode#UNDEFINED UseMode.UNDEFINED}.
   * </p>
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
   *   Recycles this pseudo-document.
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
   *   Disposes this pseudo-document.
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
  // Get and set data
  // --------------------------------------------------------------------------------

  /**
   * Get method for {@link #type type}.
   */

  public int getType ()
  {
    return this.type;
  }

  /**
   * Get method for {@link #id id}.
   */

  public int getId ()
  {
    return this.id;
  }

  /**
   * Set method for {@link #id id}.
   */

  public void setId (int id)
    throws ServiceException
  {
    this.id = id;
  }

  /**
   * Get method for {@link #useMode useMode}.
   */

  public int getUseMode ()
  {
    return this.useMode;
  }

  /**
   * <p>
   *   Sets the use mode of the pseudo-document. (The use mode specifies the way the
   *   document is used in the current context.) See {@link UseMode} for the possible use
   *   modes and their meanings.
   * </p>
   * <p>
   *   If the use mode is set to {@link UseMode#CHECKOUT CHECKOUT}, the with-path flag ist
   *   set to <code>true</code> by calling {@link #setWithPath setWithPath(true)}.
   * </p>
   *
   * @throws IllegalArgumentException if <code>useMode</code> is not in the range of
   * possible use modes.
   */

  public void setUseMode (int useMode)
    throws ServiceException
  {
    if ( !UseMode.exists(useMode) )
      throw new IllegalArgumentException(this + ": Unknown use mode: " + useMode);
    this.useMode = useMode;
    if ( useMode == UseMode.CHECKOUT ) this.setWithPath(true);
  }

  /**
   * Sets whether the path should be included in the XML.
   */

  public void setWithPath (boolean withPath)
  {
    this.withPath = withPath;
  }

  /**
   * <p>
   *   Returns <code>true</code> if this pseudo-document is controlled by an external
   *   service.
   * </p>
   * <p>
   *   This implementation always returns <code>false</code>. Extending classes should
   *   overwrite this if necessary.
   * </p>
   */

  public boolean isExternallyControlled ()
    throws ServiceException
  {
    return false;
  }

  // --------------------------------------------------------------------------------
  // DbHelper methods
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Ensures that {@link #dbHelper} is initialized. 
   * </p>
   * <p>
   *   If {@link #dbHelper} is <code>null</code>, gets a db helper from the service manager
   *   ({@link #serviceManager}) and sets {@link #dbHelper} to it. 
   * </p>
   * <p>
   *   If {@link #dbHelper} is not <code>null</code>, does nothing.
   * </p>
   */

  protected void ensureDbHelper ()
    throws ServiceException
  {
    final String METHOD_NAME = "ensureDbHelper";
    if ( this.dbHelper != null )
      {
        this.logDebug
          (METHOD_NAME + " 1/1: Already have a db helper." +
           " this.dbHelper = " + LogUtil.identify(this.dbHelper));
        return;
      }
    this.logDebug(METHOD_NAME + " 1/2: Need a db helper");
    this.dbHelper = (DbHelper)this.serviceManager.lookup(DbHelper.ROLE);
    this.logDebug
      (METHOD_NAME + " 2/2:" +
       " this.dbHelper = " + LogUtil.identify(this.dbHelper));
  }

  // --------------------------------------------------------------------------------
  // Auxiliaries for the toSAX methods
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Called in the toSAX methods to give an opportunity to set variables of this
   *   pseudo-document to values found in <code>resultSet</code>.
   * </p>
   * <p>
   *   This implementation sets {@link #id id} to the value found in the column
   *   {@link DbColumn#ID ID} of the current row of <code>resultSet</code>. Extending
   *   classes may overwrite this. to perform additional setups.
   * </p>
   */

  protected void autoSetup (ResultSet resultSet)
    throws SQLException
  {
    final String METHOD_NAME = "autoSetup(ResultSet resultSet)";
    this.logDebug
      (METHOD_NAME + " 1/2: Started. this.id = " + this.id + ", this.path = " + this.path);
    if ( resultSet == null )
      throw new IllegalArgumentException("resultSet null");
    if ( this.id == Id.AUTO )
      this.id = resultSet.getInt(DbColumn.ID);
    if ( this.withPath )
      this.setPath(resultSet);
    this.logDebug
      (METHOD_NAME + " 2/2: Done. this.id = " + this.id + ", this.path = " + this.path);
  }

  /**
   * Returns the result set needed in the toSAX method.
   */

  protected ResultSet getResultSet ()
    throws SQLException, ServiceException
  {
    return this.dbHelper.queryPseudoDocData(this.type, this.getId(), this.getDbColumns());
  }

  /**
   * Sets the global variable {@link #path path}. The value is composed from the columns
   * {@link DbColumn#SECTION_PATH DbColumn.SECTION_PATH} and
   * {@link DbColumn#PURE_NAME DbColumn.PURE_NAME} of the specified result set.
   */

  protected void setPath (ResultSet resultSet)
    throws SQLException
  {
    final String METHOD_NAME = "setPath";
    this.logDebug(METHOD_NAME + "1/2: Started");
    String sectionPath = resultSet.getString(DbColumn.SECTION_PATH);
    String pureName = resultSet.getString(DbColumn.PURE_NAME);
    this.path = (sectionPath.equals("") ? pureName : sectionPath + "/" + pureName);
    this.logDebug(METHOD_NAME + "2/2: Done. this.path = " + this.path);
  }


  /**
   * Starts the XML root element.
   */

  protected void startRootElement (ContentHandler contentHandler)
    throws SAXException, SQLException, ServiceException
  {
    this.xmlElement.reset();
    this.xmlElement.setLocalName(this.rootElementName);
    this.xmlElement.addAttribute(XMLAttribute.ID, this.getId());
    if ( this.withPath && this.path != null )
      this.xmlElement.addAttribute(XMLAttribute.PATH, this.path);
    this.xmlElement.startToSAX(contentHandler);
  }

  /**
   * Closes the XML root element.
   */

  protected void closeRootElement (ContentHandler contentHandler)
    throws SAXException, SQLException
  {
    this.xmlElement.reset();
    this.xmlElement.setLocalName(this.rootElementName);
    this.xmlElement.endToSAX(contentHandler);
  }

  /**
   * Writes the {@link XMLElement#NAME NAME} element.
   */

  protected void nameToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException, SQLException
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
   * Writes the {@link XMLElement#PURE_NAME PURE_NAME} element.
   */

  protected void pureNameToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException, SQLException
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

  /**
   * Writes the {@link XMLElement#CONTAINED_IN CONTAINED_IN} element with the section's id.
   */

  protected void containedInToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException, SQLException
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

  /**
   * Writes the {@link XMLElement#DESCRIPTION DESCRIPTION} element.
   */

  protected void descriptionToSAX (ResultSet resultSet, ContentHandler contentHandler)
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

  /**
   * Writes the {@link XMLElement#HIDE HIDE} element.
   */

  protected void hideToSAX (ResultSet resultSet, ContentHandler contentHandler)
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
   * Writes the {@link XMLElement#SYNC_ID SYNC_ID} element.
   */

  protected void syncIdToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.SYNC_ID);
        this.xmlElement.setText(resultSet.getString(DbColumn.SYNC_ID));
        this.xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("syncIdToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes a time value to SAX.
   * @param resultSet result of the database query
   * @param contentHandler recieves the SAX events.
   * @param column the column to look up in <code>resultSet</code>
   * @param element local name of the XML element.
   */

  protected void timeToSAX (ResultSet resultSet,
                            ContentHandler contentHandler,
                            String column,
                            String element)
    throws SAXException, SQLException
  {
    Date date = resultSet.getTimestamp(column);

    this.xmlElement.reset();
    this.xmlElement.setLocalName(element);
    this.xmlElement.addAttribute(XMLAttribute.VALUE, date);
    this.xmlElement.addAttribute(XMLAttribute.RAW, date.getTime());
    this.xmlElement.toSAX(contentHandler);
  }

  /**
   * Writes the {@link XMLElement#CREATED CREATED} element.
   */

  protected void createdToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException, SQLException
  {
    try
      {
        this.timeToSAX
          (resultSet, contentHandler, DbColumn.CREATED, XMLElement.CREATED);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("createdToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#LAST_MODIFIED LAST_MODIFIED} element.
   */

  protected void lastModifiedToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        this.timeToSAX
          (resultSet, contentHandler, DbColumn.LAST_MODIFIED, XMLElement.LAST_MODIFIED);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("lastModifiedToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

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
        ResultSet resultSet = this.dbHelper.queryPseudoDocReadPermissions(this.type, this.id, columns);
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
        ResultSet resultSet = this.dbHelper.queryPseudoDocWritePermissions(this.type, this.id, columns);
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

  // --------------------------------------------------------------------------------
  // toSAX methods
  // --------------------------------------------------------------------------------

  /**
   * Called from {@link #toSAX} if {@link #useMode} is {@link UseMode#COMPONENT COMPONENT}.
   */

  protected abstract void toSAX_COMPONENT (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception;

  /**
   * Called from {@link #toSAX} if {@link #useMode} is {@link UseMode#LINK LINK}.
   */

  protected abstract void toSAX_LINK (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception;

  /**
   * Called from {@link #toSAX} if {@link #useMode} is {@link UseMode#SERVE SERVE}.
   */

  protected abstract void toSAX_SERVE (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception;

  /**
   * Called from {@link #toSAX} if {@link #useMode} is {@link UseMode#INFO INFO}.
   */

  protected abstract void toSAX_INFO (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception;

  /**
   * Called from {@link #toSAX} if {@link #useMode} is {@link UseMode#INFO CHECKOUT}.
   */

  protected abstract void toSAX_CHECKOUT (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception;

  /**
   * {@inheritDoc}
   */

  public void toSAX (ResultSet resultSet, ContentHandler contentHandler, boolean ownDocument)
    throws SAXException
  {
    try
      {
        final String METHOD_NAME =
          "toSAX(ResultSet resultSet, ContentHandler contentHandler, boolean ownDocument)";
        this.logDebug
          (METHOD_NAME + " 1/2:" + 
           " this.type = " + this.type +
           ", this.getId() = " + this.getId() +
           ", this.useMode = " + this.useMode +
           ", resultSet = " + resultSet +
           ", ownDocument = " + ownDocument);
        if ( resultSet == null )
          {
            this.ensureDbHelper();
            resultSet = this.getResultSet();
            if ( !resultSet.next() )
              throw new SAXException("resultSet empty");
          }
        this.autoSetup(resultSet);
        if ( ownDocument ) contentHandler.startDocument();
        switch ( this.useMode )
          {
          case UseMode.COMPONENT : toSAX_COMPONENT(resultSet, contentHandler); break;
          case UseMode.LINK : toSAX_LINK(resultSet, contentHandler); break;
          case UseMode.SERVE : toSAX_SERVE(resultSet, contentHandler); break;
          case UseMode.INFO : toSAX_INFO(resultSet, contentHandler); break;
          case UseMode.CHECKOUT : toSAX_CHECKOUT(resultSet, contentHandler); break;
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
   * {@inheritDoc}
   */

  public void toSAX (ContentHandler contentHandler, boolean ownDocument)
    throws SAXException
  {
    this.toSAX(null, contentHandler, ownDocument);
  }

  /**
   * Sends this pseudo-document as SAX events to <code>contentHandler</code>. If
   * {@link #useMode} is {@link UseMode#SERVE SERVE} or {@link UseMode#INFO INFO}, the
   * <code>startDocument</code> and <code>endDocument</code> method of
   * <code>contentHandler</code> is called before resp. after the XML is sent to SAX.
   */

  public void toSAX (ContentHandler contentHandler)
    throws SAXException
  {
    boolean ownDocument =
      ( this.useMode == UseMode.SERVE || this.useMode == UseMode.INFO );
    this.toSAX(contentHandler, ownDocument);
  }

  // --------------------------------------------------------------------------------
  // getData method
  // --------------------------------------------------------------------------------

  /**
   * Returns the specified data of the pseudo-document. The data are specified by column
   * names of the database table corresponding to this pseudo-document type.
   */

  public ResultSet getData (String[] dbColumns)
    throws ServiceException, SQLException
  {
    final String METHOD_NAME = "getData";
    this.getLogger().debug
      (METHOD_NAME + " 1/2: Started." +
       " dbColumns = " + LogUtil.arrayToString(dbColumns));
    this.ensureDbHelper();
    return this.dbHelper.queryPseudoDocData(this.type, this.getId(), dbColumns);
  }

  // --------------------------------------------------------------------------------
  // getLastModified method
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Returns the time the pseudo-document was last modified.
   * </p>
   * <p>
   *   It is allowed that the method returns <code>null</code>, which means that the last
   *   modification time is unknown.
   * </p>
   *
   * @return the last modification time of the document.
   *
   * @throws SQLException if something goes wrong while retrieving the last modification
   *                      time from the database
   */

  public Date getLastModified ()
    throws ServiceException, SQLException
  {
    final String METHOD_NAME = "getLastModified";
    this.logDebug(METHOD_NAME);
    this.ensureDbHelper();
    ResultSet resultSet =
      this.dbHelper.queryPseudoDocDatum(this.type, this.getId(), DbColumn.LAST_MODIFIED);
    if ( ! resultSet.next() )
      throw new SQLException("Empty ResultSet");
    return resultSet.getTimestamp(DbColumn.LAST_MODIFIED);
  }

  // --------------------------------------------------------------------------------
  // getIdentification method
  // --------------------------------------------------------------------------------

  /**
   * Returns a string that identificates this instance. Used in log messages.
   */

  public String getIdentification ()
  {
    return this.toString();
  }
}
