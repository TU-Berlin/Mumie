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

package net.mumie.cocoon.content;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.Nature;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.UseMode;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.service.AbstractJapsServiceable;
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

/**
 * Abstract base class for content objects.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: AbstractContentObject.java,v 1.7 2008/09/08 08:48:25 rassy Exp $</code>
 */

public abstract class AbstractContentObject extends AbstractJapsServiceable 
  implements ContentObject, Serviceable, Recyclable, Disposable 
{
  // --------------------------------------------------------------------------------
  // Global variables
  // --------------------------------------------------------------------------------

  /**
   * The nature of this content object.
   */

  protected int nature;

  /**
   * The type of this content object.
   */

  protected int type;

  /**
   * The id of this content object.
   */

  protected int id = Id.UNDEFINED;

  /**
   * The path of this content object. Set only if needed, otherwise <code>null</code>.
   */

  protected String path = null;

  /**
   * Whether the path should be included in the XML. Set by
   * {@link #setWithPath setWithPath}
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
  // Livecycle methods
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Makes this instance ready to represent another content object.
   * </p>
   * <p>
   *   Resets {@link #id id}, {@link #path path}, {@link #withPath withPath},
   *   {@link #useMode} and {@link #xmlElement xmlElement}.
   * </p>
   * <p>
   *   {@link #nature nature} and {@link #type type} are not reset, since they are constant
   *   in most implementations.
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
   *   Recycles this content object.
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
   *   Disposes this content object.
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
   * Returns the nature (document or pseudo-document) of this content object.
   */

  public int getNature ()
  {
    return this.nature;
  }

  /**
   * Returns the type of this content object, as numerical code.
   */

  public int getType ()
  {
    return this.type;
  }

  /**
   * Returns the type of this content object, as string name.
   */

  public String getTypeName ()
  {
    return
      (this.nature == Nature.DOCUMENT
       ? DocType.nameFor(this.type)
       : PseudoDocType.nameFor(this.type));
  }

  /**
   * Returns the id of this content object.
   */

  public int getId ()
  {
    return this.id;
  }

  /**
   * Sets the id of this content object.
   */

  public void setId (int id)
  {
    this.id = id;
  }

  /**
   * Returns the use mode of this content object.
   */

  public int getUseMode ()
  {
    return this.useMode;
  }

  /**
   * <p>
   *   Sets the use mode of the content object. (The use mode specifies the way the
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
    throws IllegalArgumentException
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
   * Returns the result set needed in the toSAX method.
   */

  protected abstract ResultSet getResultSet ()
    throws SQLException;

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
    this.path =
      resultSet.getString(DbColumn.SECTION_PATH) + "/" +
      resultSet.getString(DbColumn.PURE_NAME);
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
    if ( this.path != null )
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
    throws SAXException
  {
    ToSAXUtil.nameToSAX(resultSet, this.xmlElement, contentHandler);
  }

  /**
   * Writes the {@link XMLElement#CATEGORY CATEGORY} element.
   */

  protected void categoryToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    ToSAXUtil.categoryToSAX(resultSet, this.xmlElement, contentHandler);
  }

  /**
   * Writes the {@link XMLElement#THUMBNAIL THUMBNAIL} element.
   */

  protected void thumbnailToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    ToSAXUtil.thumbnailToSAX(resultSet, this.xmlElement, contentHandler);
  }

  /**
   * Writes the {@link XMLElement#INFO_PAGE INFO_PAGE} element.
   */

  protected void infoPageToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    ToSAXUtil.infoPageToSAX(resultSet, this.xmlElement, contentHandler);
  }

  /**
   * Writes the {@link XMLElement#PATH PATH} element.
   */

  protected void pathToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    ToSAXUtil.pathToSAX(resultSet, this.xmlElement, contentHandler);
  }

  /**
   * Writes the {@link XMLElement#PURE_NAME PURE_NAME} element.
   */

  protected void pureNameToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    ToSAXUtil.pureNameToSAX(resultSet, this.xmlElement, contentHandler);
  }

  /**
   * Writes the {@link XMLElement#DESCRIPTION DESCRIPTION} element.
   */

  protected void descriptionToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    ToSAXUtil.descriptionToSAX(resultSet, this.xmlElement, contentHandler);
  }

  /**
   * Writes the {@link XMLElement#CONTAINED_IN CONTAINED_IN} element.
   */

  protected void containedInToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    ToSAXUtil.containedInToSAX(resultSet, this.xmlElement, contentHandler);
  }

  /**
   * Writes the {@link XMLElement#CREATED CREATED} element.
   */

  protected void createdToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    ToSAXUtil.createdToSAX(resultSet, this.xmlElement, contentHandler);
  }

  /**
   * Writes the {@link XMLElement#HIDE HIDE} element.
   */

  protected void hideToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    ToSAXUtil.hideToSAX(resultSet, this.xmlElement, contentHandler);
  }

  /**
   * Writes the {@link XMLElement#DELETED DELETED} element.
   */

  protected void deletedToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    ToSAXUtil.deletedToSAX(resultSet, this.xmlElement, contentHandler);
  }

  /**
   * Writes the {@link XMLElement#LAST_MODIFIED LAST_MODIFIED} element.
   */

  protected void lastModifiedToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    ToSAXUtil.lastModifiedToSAX(resultSet, this.xmlElement, contentHandler);
  }

  // --------------------------------------------------------------------------------
  // toSAX methods
  // --------------------------------------------------------------------------------

  /**
   * Called from {@link #toSAX} if {@link #useMode} is {@link UseMode#COMPONENT COMPONENT}.
   */

  protected void toSAX_COMPONENT (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception
  {
    throw new IllegalStateException
      ("Not representable as XML in use mode \"component\"");
  }

  /**
   * Called from {@link #toSAX} if {@link #useMode} is {@link UseMode#LINK LINK}.
   */

  protected void toSAX_LINK (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception
  {
    throw new IllegalStateException
      ("Not representable as XML in use mode \"link\"");
  }

  /**
   * Called from {@link #toSAX} if {@link #useMode} is {@link UseMode#SERVE SERVE}.
   */

  protected void toSAX_SERVE (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception
  {
    throw new IllegalStateException
      ("Not representable as XML in use mode \"serve\"");
  }

  /**
   * Called from {@link #toSAX} if {@link #useMode} is {@link UseMode#INFO INFO}.
   */

  protected void toSAX_INFO (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception
  {
    throw new IllegalStateException
      ("Not representable as XML in use mode \"info\"");
  }

  /**
   * Called from {@link #toSAX} if {@link #useMode} is {@link UseMode#INFO CHECKOUT}.
   */

  protected void toSAX_CHECKOUT (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception
  {
    throw new IllegalStateException
      ("Not representable as XML in use mode \"checkout\"");
  }

  /**
   * <p>
   *   Sends this content object as SAX events to <code>contentHandler</code>.
   * </p>
   * <p>
   *   Id <code>resultSet</code> is non-null, the method requests the necessary data from
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
        this.logDebug
          (METHOD_NAME + " 1/2:" + 
           " type = " + this.getType() +
           ", id = " + this.getId() +
           ", useMode = " + this.getUseMode() +
           ", resultSet = " + resultSet +
           ", ownDocument = " + ownDocument);
        if ( resultSet == null )
          {
            this.ensureDbHelper();
            resultSet = this.getResultSet();
            if ( !resultSet.next() )
              throw new SAXException("resultSet empty");
          }
        if ( this.withPath ) this.setPath(resultSet);
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
              ("Method " + METHOD_NAME + " not implemented for use mode: " + this.useMode);
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
   *   Sends this content object as SAX events to <code>contentHandler</code>.
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
   * Sends this content object as SAX events to <code>contentHandler</code>. If
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
  // getLastModified method
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Returns the time this content object was last modified.
   * </p>
   * <p>
   *   This implementation always returns <code>null</code>, which means that the last
   *   modification time is unknown.
   * </p>
   *
   * @return the last modification time of the document.
   *
   * @throws ContentObjectException if something goes wrong while retrieving the last
   *   modification time from the database
   */

  public Date getLastModified ()
    throws ContentObjectException
  {
    return null;
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
