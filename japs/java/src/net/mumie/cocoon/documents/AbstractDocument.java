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

package net.mumie.cocoon.documents;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.Category;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.DbTable;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.MediaType;
import net.mumie.cocoon.notions.RefAttrib;
import net.mumie.cocoon.notions.RefType;
import net.mumie.cocoon.notions.TimeFormat;
import net.mumie.cocoon.notions.UseMode;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.notions.XMLNamespace;
import net.mumie.cocoon.pseudodocs.ELClass;
import net.mumie.cocoon.pseudodocs.UserGroup;
import net.mumie.cocoon.service.AbstractJapsServiceable;
import net.mumie.cocoon.util.LogUtil;
import net.mumie.cocoon.xml.FragmentSAXFilter;
import net.mumie.cocoon.xml.MetaXMLElement;
import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.xml.XMLizable;
import org.apache.excalibur.xml.sax.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Abstract base class for the type-specific document classes.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: AbstractDocument.java,v 1.111 2010/01/01 22:04:09 rassy Exp $</code>
 */

public abstract class AbstractDocument extends AbstractJapsServiceable 
  implements Document, Serviceable, Recyclable, Disposable, XMLizable
{
  // --------------------------------------------------------------------------------
  // Global variables
  // --------------------------------------------------------------------------------

  /**
   * The id of this document. May also be {@link Id#UNDEFINED UNDEFINED}, meaning  this
   * document has no id.
   */

  protected int id = Id.UNDEFINED;

  /**
   * The path of this document. Set only if needed, otherwise <code>null</code>.
   */

  protected String path = null;

  /**
   * Whether the path should be included in the XML. Set by
   * {@link #setWithPath setWithPath}
   */

  protected boolean withPath = false;

  /**
   * The local id of this document. May be <code>null</code>, meaning this document has no
   * local id.
   */

  protected String lid = null;

  /**
   * The type of this document.
   */

  protected int type = DocType.UNDEFINED;

  /**
   * The use mode of this document.
   */

  protected int useMode = UseMode.UNDEFINED;

  /**
   * The id of the reference pointing to this document, or {@link Id#UNDEFINED UNDEFINED} if
   * this object does not represent a document that is the target of a reference.
   */

  protected int refId = Id.UNDEFINED;

  /**
   * The the type of the document that is the origin of the reference pointing to this
   * document, or {@link DocType#UNDEFINED UNDEFINED} if this object does not represent a
   * document that is the target of a reference.
   */

  protected int fromDocType = DocType.UNDEFINED;

  /**
   * The database helper used by this object.
   */

  protected DbHelper dbHelper = null;

  /**
   * The date format used by default.
   */

  protected SimpleDateFormat dateFormat = new SimpleDateFormat(TimeFormat.DEFAULT);

  /**
   * Helper to write Meta XML elements to SAX.
   */

  protected MetaXMLElement xmlElement = new MetaXMLElement ();

  // --------------------------------------------------------------------------------
  // Livecycle methods
  // --------------------------------------------------------------------------------

  /**
   * Makes this object ready to represent another document.
   */

  public void reset ()
  {
    final String METHOD_NAME = "reset()";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.id = Id.UNDEFINED;
    this.path = null;
    this.withPath = false;
    this.lid = null;
    this.useMode = UseMode.UNDEFINED;
    this.refId = Id.UNDEFINED;
    this.fromDocType = DocType.UNDEFINED;
    this.xmlElement.reset();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Recycles this document object. In particular, calls the {@link #reset} method and
   * releases the {@link #dbHelper dbHelper} if not <code>null</code>.
   */

  public void recycle ()
  {
    this.reset();
    this.releaseDbHelper();
  }

  /**
   * Calls the {@link #reset} method and releases the {@link #dbHelper dbHelper} if not
   * <code>null</code>.
   */

  public void dispose ()
  {
    this.reset();
    this.releaseDbHelper();
  }

  // --------------------------------------------------------------------------------
  // Get / set methods
  // --------------------------------------------------------------------------------

  /**
   * Returns the type of the document. See 
   * {@link net.mumie.cocoon.notions.DocType} for
   * the possible types and their meanings.
   */

  public int getType ()
  {
    return this.type;
  }

  /**
   * Sets the id of the document. 
   *
   * @throws IllegalArgumentException if <code>id</code> is not suitable as an id
   * (e.g., if it is negative).
   */

  public void setId (int id)
    throws IllegalArgumentException
  {
    if ( id < 0 )
      throw new IllegalArgumentException("Invalid id value: " + id);
    this.id = id;
  }

  /**
   * @return the id of the document
   */

  public int getId ()
  {
    return this.id;
  }

  /**
   * Sets the local id of the document. 
   */

  public void setLid (String lid)
    //    throws IllegalArgumentException
  {
    this.lid = lid;
  }

  /**
   * @return the local id of the document
   */

  public String getLid ()
  {
    return this.lid;
  }

  /**
   * <p>
   *   Sets the use mode of the document. (The use mode specifies the way the document is
   *   used in the current context.) See {@link UseMode} for the possible use modes and
   *   their meanings.
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
    if ( (  useMode < UseMode.first ) || ( useMode > UseMode.last ) )
      throw new IllegalArgumentException("Use mode value out of range: " + useMode);
    this.useMode = useMode;
    if ( useMode == UseMode.CHECKOUT ) this.setWithPath(true);
  }

  /**
   * @return the use mode of the document.
   */

  public int getUseMode ()
  {
    return this.useMode;
  }

  /**
   * Sets whether the path should be included in the XML.
   */

  public void setWithPath (boolean withPath)
  {
    this.withPath = withPath;
  }

  /**
   * Returns whether the path should be included in the XML.
   */

  public boolean getWithPath ()
  {
    return this.withPath;
  }

  /**
   * Sets the id of the reference pointing to this document.
   */

  public void setRefId (int refId)
  {
    this.refId = refId;
  }

  /**
   * Returns the id of the reference pointing to this document, or
   * {@link Id#UNDEFINED UNDEFINED} if this object does not represent a document that is the
   * target of e reference. 
   */

  public int getRefId ()
  {
    return this.refId;
  }

  /**
   * Sets the type of the document that is the origin of the reference pointing to this
   * document.
   */

  public void setFromDocType (int fromDocType)
  {
    this.fromDocType = fromDocType;
  }

  /**
   * Returns the type of the document that is the origin of the reference pointing to this
   * document.
   */

  public int getFromDocType ()
  {
    return this.fromDocType;
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

  /**
   * Releases {@link #dbHelper}, the database helper of this object, provided it is not
   * <code>null</code>.
   */

  protected void releaseDbHelper ()
  {
    final String METHOD_NAME = "releaseDbHelper()";
    this.logDebug
      (METHOD_NAME + " 1/2: Started." +
       " this.dbHelper = " + LogUtil.identify(this.dbHelper));
    if ( this.dbHelper != null )
      {
        this.serviceManager.release(this.dbHelper);
        this.dbHelper = null;
      }
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // Checking
  // --------------------------------------------------------------------------------

  /**
   * Checks if the document type, id, and use mode are properly defined, and throws a
   * {@link ServiceException ServiceException} if not.
   */

  protected void check ()
    throws ServiceException
  {
    if ( this.type < 0 )
      throw new ServiceException("Invalid document type: " + this.type);
    if ( this.id < 0 )
      throw new ServiceException("Invalid document id: " + this.id);
    if ( this.useMode < 0 )
      throw new ServiceException("Invalid use mode: " + this.useMode);
  }

  // --------------------------------------------------------------------------------
  // Auxiliaries for the toSAX methods
  // --------------------------------------------------------------------------------

  /**
   * Returns the database columns this document needs if one of the
   * <code>toSAX</code> methods would be called.
   */

  public abstract String[] getDbColumns ()
    throws ServiceException;

  /**
   * Returns the result set needed in the toSAX method.
   */

  protected ResultSet getResultSet ()
    throws SQLException, ServiceException
  {
    if ( this.refId == Id.UNDEFINED )
      return this.dbHelper.queryData(this, this.getDbColumns());
    else
      return this.dbHelper.queryDataOfRefTarget
        (this.fromDocType, this.type, this.refId, this.getDbColumns());
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
   * Parses <code>stream</code> by means of a SAX parser. Calls from the parser to the
   * <code>startDocument</code> and <code>endDocument</code> methods are suppressed. This
   * is because it should be possible to insert <code>stream</code> as a part of a larger
   * XML document. <code>contentHandler</code> receieves the SAX events.
   */

  public void xmlCodeToSAX (InputStream stream,
                            ContentHandler contentHandler)
    throws SAXException
  {
    final String METHOD_NAME = "xmlCodeToSAX(...)";
    this.logDebug
      (METHOD_NAME + " 1/2: Started." +
       " stream = " + stream +
       ", contentHandler = " + contentHandler);
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

  /**
   * Writes the root element start tag. <code>contentHandler</code> is the content Handler
   * that recieves the SAX event.
   */

  protected void rootElementStartToSAX (ContentHandler contentHandler)
    throws SAXException
  {
    this.xmlElement.reset();
    this.xmlElement.setLocalName(XMLElement.DOC[this.type]);
    this.xmlElement.addAttribute(XMLAttribute.ID, this.id);
    if ( this.path != null )
      this.xmlElement.addAttribute(XMLAttribute.PATH, this.path);
    if ( this.lid != null )
      this.xmlElement.addAttribute(XMLAttribute.LID, this.lid);
    if ( this.refId != Id.UNDEFINED )
      this.xmlElement.addAttribute(XMLAttribute.REF_ID, this.refId);
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
    this.xmlElement.setLocalName(XMLElement.DOC[this.type]);
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
   * Writes the {@link XMLElement#QUALIFIED_NAME QUALIFIED_NAME} element.
   */

  protected void qualifiedNameToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        this.xmlElement.reset();
    this.xmlElement.setLocalName(XMLElement.QUALIFIED_NAME);
    this.xmlElement.setText(resultSet.getString(DbColumn.QUALIFIED_NAME));
    this.xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("qualifiedNameToSAX: " + this.xmlElement.statusToString(), exception);
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
   * Writes the {@link XMLElement#COPYRIGHT COPYRIGHT} element.
   */

  protected void copyrightToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.COPYRIGHT);
        this.xmlElement.setText(resultSet.getString(DbColumn.COPYRIGHT));
        this.xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("copyrightToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#VC_THREAD VC_THREAD} element.
   */

  protected void vcThreadToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.VC_THREAD);
        this.xmlElement.addAttribute(XMLAttribute.ID, resultSet.getString(DbColumn.VC_THREAD));
        this.xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("vcThreadToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#VERSION VERSION} element.
   */

  protected void versionToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.VERSION);
        this.xmlElement.addAttribute(XMLAttribute.VALUE, resultSet.getString(DbColumn.VERSION));
        this.xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("versionToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#DELETED DELETED} element.
   */

  protected void deletedToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.DELETED);
        this.xmlElement.addAttribute(XMLAttribute.VALUE, resultSet.getBoolean(DbColumn.DELETED));
        this.xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("deletedToSAX: " + this.xmlElement.statusToString(), exception);
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
   * Writes the {@link XMLElement#IS_WRAPPER IS_WRAPPER} element.
   */

  protected void isWrapperToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.IS_WRAPPER);
        this.xmlElement.addAttribute(XMLAttribute.VALUE, resultSet.getBoolean(DbColumn.IS_WRAPPER));
        this.xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("isWrapperToSAX: " + this.xmlElement.statusToString(), exception);
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
   * Writes the {@link XMLElement#TIMEFRAME_START TIMEFRAME_START} element.
   */

  protected void timeframeStartToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        this.timeToSAX
          (resultSet, contentHandler, DbColumn.TIMEFRAME_START, XMLElement.TIMEFRAME_START);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("timeframeStartToSAX: " + this.xmlElement.statusToString(), exception);
      }
  } 

  /**
   * Writes the {@link XMLElement#TIMEFRAME_END TIMEFRAME_END} element.
   */

  protected void timeframeEndToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        this.timeToSAX
          (resultSet, contentHandler, DbColumn.TIMEFRAME_END, XMLElement.TIMEFRAME_END);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("timeframeEndToSAX: " + this.xmlElement.statusToString(), exception);
      }
  } 

  /**
   * Writes the {@link XMLElement#CATEGORY CATEGORY} element.
   */

  protected void categoryToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        int categoryId = resultSet.getInt(DbColumn.CATEGORY);
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.CATEGORY);
        this.xmlElement.addAttribute(XMLAttribute.ID, categoryId);
        this.xmlElement.addAttribute(XMLAttribute.NAME, Category.nameFor[categoryId]);
        this.xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("categoryToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#WIDTH WIDTH} element.
   */

  protected void widthToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.WIDTH);
        this.xmlElement.addAttribute(XMLAttribute.VALUE, resultSet.getString(DbColumn.WIDTH));
        this.xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("widthToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#HEIGHT HEIGHT} element.
   */

  protected void heightToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.HEIGHT);
        this.xmlElement.addAttribute(XMLAttribute.VALUE, resultSet.getString(DbColumn.HEIGHT));
        this.xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("heightToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#DURATION DURATION} element.
   */

  protected void durationToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.DURATION);
        this.xmlElement.addAttribute
          (XMLAttribute.VALUE, resultSet.getString(DbColumn.DURATION));
        this.xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("durationToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#CONTAINED_IN CONTAINED_IN} element.
   */

  protected void containedInToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.CONTAINED_IN);
        this.xmlElement.addAttribute
          (XMLAttribute.ID, resultSet.getString(DbColumn.CONTAINED_IN));
        if ( this.withPath )
          this.xmlElement.addAttribute
            (XMLAttribute.PATH, resultSet.getString(DbColumn.SECTION_PATH));
        this.xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("containedInToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#CONTENT_TYPE CONTENT_TYPE} element.
   */

  protected void contentTypeToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.CONTENT_TYPE);
        int contentTypeId = resultSet.getInt(DbColumn.CONTENT_TYPE);
        this.xmlElement.addAttribute(XMLAttribute.ID, contentTypeId);
        this.xmlElement.addAttribute(XMLAttribute.VALUE, MediaType.nameFor[contentTypeId]);
        this.xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("contentTypeToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#CONTENT_LENGTH CONTENT_LENGTH} element.
   */

  protected void contentLengthToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.CONTENT_LENGTH);
        this.xmlElement.addAttribute
          (XMLAttribute.VALUE, resultSet.getString(DbColumn.CONTENT_LENGTH));
        this.xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("contentLengthToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#INFO_PAGE INFO_PAGE} element.
   */

  protected void infoPageToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException, SQLException
  {
    try
      {
        String value = resultSet.getString(DbColumn.INFO_PAGE);
        if ( value != null )
          {
            this.xmlElement.reset();
            this.xmlElement.setLocalName(XMLElement.INFO_PAGE);
            this.xmlElement.addAttribute(XMLAttribute.ID, value);
            this.xmlElement.toSAX(contentHandler);
          }
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("infoPageToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#THUMBNAIL THUMBNAIL} element.
   */

  protected void thumbnailToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException, SQLException
  {
    try
      {
        String value = resultSet.getString(DbColumn.THUMBNAIL);
        if ( value != null )
          {
            this.xmlElement.reset();
            this.xmlElement.setLocalName(XMLElement.THUMBNAIL);
            this.xmlElement.addAttribute(XMLAttribute.ID, value);
            this.xmlElement.toSAX(contentHandler);
          }
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("thumbnailToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#SUMMARY SUMMARY} element.
   */

  protected void summaryToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException, SQLException
  {
    try
      {
        String value = resultSet.getString(DbColumn.SUMMARY);
        if ( value != null )
          {
            this.xmlElement.reset();
            this.xmlElement.setLocalName(XMLElement.DOC[DocType.GENERIC_SUMMARY]);
            this.xmlElement.addAttribute(XMLAttribute.ID, value);
            this.xmlElement.toSAX(contentHandler);
          }
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("summaryToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#CORRECTOR CORRECTOR} element.
   */

  protected void correctorToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException, SQLException
  {
    try
      {
        String value = resultSet.getString(DbColumn.CORRECTOR);
        if ( value != null )
          {
            this.xmlElement.reset();
            this.xmlElement.setLocalName(XMLElement.CORRECTOR);
            this.xmlElement.addAttribute(XMLAttribute.ID, value);
            this.xmlElement.toSAX(contentHandler);
          }
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("correctorToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#CLASS CLASS} element.
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
        if ( !resultSet.wasNull () )
          {
            elClass = (ELClass)this.serviceManager.lookup(ELClass.ROLE);
            elClass.setId(classId);
            elClass.setUseMode(UseMode.COMPONENT);
            elClass.toSAX(contentHandler, false);
            // elClass.toSAX(resultSet, DB_PREFIX_CLASS, contentHandler, false);
            this.logDebug(METHOD_NAME + "2/2: Done");
          }
        else
          this.logDebug(METHOD_NAME + "2/2: No class");
          
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
   * Writes the {@link XMLElement#CUSTOM_METAINFO CUSTOM_METAINFO} element.
   */

  protected void customMetainfoToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.CUSTOM_METAINFO);
        this.xmlElement.startToSAX(contentHandler);
        this.xmlCodeToSAX
          (resultSet.getAsciiStream(DbColumn.CUSTOM_METAINFO),
           contentHandler);
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.CUSTOM_METAINFO);
        this.xmlElement.endToSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("customMetainfoToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#CONTENT CONTENT} element.
   */

  protected void contentToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.CONTENT);
        this.xmlElement.startToSAX(contentHandler);
        this.xmlCodeToSAX
          (resultSet.getAsciiStream(DbColumn.CONTENT),
           contentHandler);
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.CONTENT);
        this.xmlElement.endToSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("contentToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#CONTENT_CODE CONTENT_CODE} element.
   */

  protected void contentCodeToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.CONTENT_CODE);
        this.xmlElement.setText(resultSet.getString(DbColumn.CONTENT));
        this.xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("contenCodeToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

  /**
   * Assuming this document is a reference target, sends all attributes of the reference to
   * SAX.
   */

  protected void refAttribsToSAX (ResultSet resultSet,
                                  ContentHandler contentHandler,
                                  String calledFrom)
    throws SAXException
  {
    final String METHOD_NAME = "refAttribsToSAX";
    this.logDebug
      (METHOD_NAME + " 1/3: Started." +
       " calledFrom = " + calledFrom +
       ", this.id = " + this.id +
       ", this.refId = " + this.refId +
       ", this.fromDocType = " + this.fromDocType +
       ", this.type = " + this.type +
       ", resultSet = " + resultSet);
    try
      {
        int refType = resultSet.getInt(DbColumn.REF_TYPE);
        this.logDebug(METHOD_NAME + " 2/3: refType = " + refType);
        int count = 0;
        for (int refAttrib = RefAttrib.first; refAttrib <= RefAttrib.last; refAttrib++)
          {
            if ( RefAttrib.exists(refAttrib, this.fromDocType, this.type, refType) )
              {
                count++;
                String name = RefAttrib.nameFor[refAttrib];
                String value = resultSet.getString(name);
                this.logDebug
                  (METHOD_NAME + "1." + count + "/2:" +
                   " name = " + name + ", value = " + value);
                if ( value != null )
                  {
                    this.xmlElement.reset();
                    this.xmlElement.setLocalName(XMLElement.REF_ATTRIBUTE);
                    this.xmlElement.addAttribute(XMLAttribute.NAME, name);
                    this.xmlElement.addAttribute(XMLAttribute.VALUE, value);
                    this.xmlElement.toSAX(contentHandler);
                  }
              }
          }
        this.logDebug
          (METHOD_NAME + " 2/2: Done. " + count + " attributes processed");
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("refAttribsToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

  /**
   * Sends all references pointing to documents of a certain type to SAX.
   *
   * @param toDocType the type of the document the references point to.
   * @param refType the type of the references.
   * @param useMode the use mode given to the referenced documents (controls their XML
   * representations)
   * @param contentHandler recieves the SAX events.
   */

  protected void referencesToSAX (int toDocType,
                                  int refType,
                                  int useMode,
                                  ContentHandler contentHandler)
    throws SAXException
  {
    final String METHOD_NAME = "referencesToSAX";
    this.logDebug
      (METHOD_NAME + " 1/2: Started." +
       " toDocType = " + toDocType +
       ", refType = " + refType +
       ", useMode = " + useMode +
       ", this.withPath = " + this.withPath +
       ", contentHandler = " + contentHandler);
    ServiceSelector documentSelector = null;
    Document toDoc = null;
    try
      {
        documentSelector =
          (ServiceSelector) this.serviceManager.lookup(Document.ROLE + "Selector");
        toDoc = (Document) documentSelector.select(DocType.hintFor[toDocType]);
        toDoc.setUseMode(useMode);
        toDoc.setWithPath(this.withPath);
        String[] columns = toDoc.getDbColumns();
        this.ensureDbHelper();
        ResultSet resultSet =
          this.dbHelper.queryDataOfReferencedDocs(this, toDocType, refType, columns);
        if ( resultSet == null )
          {
            this.logDebug(METHOD_NAME + " 2/2: Done. resultSet = null");
            return;
          }
        while ( resultSet.next() )
          {
            toDoc.setId(resultSet.getInt(DbColumn.ID));
            toDoc.setLid(resultSet.getString(DbColumn.LID));
            toDoc.setRefId(resultSet.getInt(DbColumn.REF));
            toDoc.setFromDocType(this.type);
            toDoc.toSAX(resultSet, contentHandler, false);
          }
      }
    catch (Exception exception)
      {
        throw new SAXException(exception);
      }
    finally
      {
        if ( toDoc != null ) documentSelector.release(toDoc);
        if ( documentSelector != null ) this.serviceManager.release(documentSelector);
      }
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Sends all references pointing to documents of any type to SAX.
   *
   * @param refType the type of the references.
   * @param useMode the use mode given to the referenced documents (controls their XML
   *   representations)
   * @param contentHandler recieves the SAX events.
   */

  protected void referencesToSAX (int refType, int useMode, ContentHandler contentHandler)
    throws SAXException
  {
    for (int toDocType = DocType.first;  toDocType <= DocType.last; toDocType++)
      if ( DbTable.REF[this.type][toDocType] != null )
        referencesToSAX(toDocType, refType, useMode, contentHandler);
  }

  /**
   * Writes the {@link XMLElement#COMPONENTS COMPONENTS} element.
   */

  protected void componentsToSAX (ContentHandler contentHandler)
    throws SAXException, SQLException
  {
    this.xmlElement.reset();
    this.xmlElement.setLocalName(XMLElement.COMPONENTS);
    this.xmlElement.startToSAX(contentHandler);
    this.referencesToSAX(RefType.COMPONENT, UseMode.COMPONENT, contentHandler);
    this.xmlElement.endToSAX(contentHandler);
  }

  /**
   * Writes the {@link XMLElement#LINKS LINKS} element.
   */

  protected void linksToSAX (ContentHandler contentHandler)
    throws SAXException, SQLException
  {
    this.xmlElement.reset();
    this.xmlElement.setLocalName(XMLElement.LINKS);
    this.xmlElement.startToSAX(contentHandler);
    this.referencesToSAX(RefType.LINK, UseMode.LINK, contentHandler);
    this.xmlElement.endToSAX(contentHandler);
  }

  /**
   * Writes the {@link XMLElement#ATTACHABLE ATTACHABLE} element.
   */

  protected void attachableToSAX (ContentHandler contentHandler)
    throws SAXException, SQLException
  {
    this.xmlElement.reset();
    this.xmlElement.setLocalName(XMLElement.ATTACHABLE);
    this.xmlElement.startToSAX(contentHandler);
    this.referencesToSAX(RefType.ATTACHABLE, UseMode.LINK, contentHandler);
    this.xmlElement.endToSAX(contentHandler);
  }

  /**
   * Takes all references which (a) point from documents of the specified type to this document and
   * (b) have the specified reference type; and sends these references to SAX.
   *
   * @param fromDocType the type of the reference origins.
   * @param refType the type of the references.
   * @param useMode the use mode given to the referencing documents (controls their XML
   *   representations)
   * @param contentHandler recieves the SAX events.
   */

  protected void referencingDocsToSAX (int fromDocType,
                                       int refType,
                                       int useMode,
                                       ContentHandler contentHandler)
    throws SAXException
  {
    final String METHOD_NAME = "referencingDocsToSAX";
    this.logDebug
      (METHOD_NAME + " 1/2: Started." +
       " fromDocType = " + fromDocType +
       ", refType = " + refType +
       ", useMode = " + useMode +
       ", this.withPath = " + this.withPath +
       ", contentHandler = " + contentHandler);
    ServiceSelector documentSelector = null;
    Document toDoc = null;
    try
      {
        documentSelector =
          (ServiceSelector) this.serviceManager.lookup(Document.ROLE + "Selector");
        toDoc = (Document) documentSelector.select(DocType.hintFor[fromDocType]);
        toDoc.setUseMode(useMode);
        toDoc.setWithPath(this.withPath);
        String[] columns = toDoc.getDbColumns();
        this.ensureDbHelper();
        ResultSet resultSet =
          this.dbHelper.queryDataOfReferencingDocs(this.type, this.id, fromDocType, refType, columns);
        if ( resultSet == null )
          {
            this.logDebug(METHOD_NAME + " 2/2: Done. resultSet = null");
            return;
          }
        while ( resultSet.next() )
          {
            toDoc.setId(resultSet.getInt(DbColumn.ID));
            toDoc.setLid(resultSet.getString(DbColumn.LID));
            toDoc.setRefId(resultSet.getInt(DbColumn.REF));
            toDoc.setFromDocType(this.type);
            toDoc.toSAX(resultSet, contentHandler, false);
          }
      }
    catch (Exception exception)
      {
        throw new SAXException(exception);
      }
    finally
      {
        if ( toDoc != null ) documentSelector.release(toDoc);
        if ( documentSelector != null ) this.serviceManager.release(documentSelector);
      }
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Takes all references which point this document and have the specified reference type,
   * and sends them to SAX.
   *
   * @param refType the type of the references.
   * @param useMode the use mode given to the referencing documents (controls their XML
   *   representations)
   * @param contentHandler recieves the SAX events.
   */

  protected void referencingDocsToSAX (int refType, int useMode, ContentHandler contentHandler)
    throws SAXException
  {
    for (int fromDocType = DocType.first;  fromDocType <= DocType.last; fromDocType++)
      if ( DbTable.REF[fromDocType][this.type] != null )
        referencingDocsToSAX(fromDocType, refType, useMode, contentHandler);
  }

  /**
   * Takes all documents referencing this document as a component, and sends them to SAX.
   */

  protected void componentOfToSAX (ContentHandler contentHandler)
    throws SAXException, SQLException
  {
    this.xmlElement.reset();
    this.xmlElement.setLocalName(XMLElement.COMPONENT_OF);
    this.xmlElement.startToSAX(contentHandler);
    this.referencingDocsToSAX(RefType.COMPONENT, UseMode.COMPONENT, contentHandler);
    this.xmlElement.endToSAX(contentHandler);
  }

  /**
   * Takes all documents referencing this document as a link, and sends them to SAX.
   */

  protected void linkOfToSAX (ContentHandler contentHandler)
    throws SAXException, SQLException
  {
    this.xmlElement.reset();
    this.xmlElement.setLocalName(XMLElement.LINK_OF);
    this.xmlElement.startToSAX(contentHandler);
    this.referencingDocsToSAX(RefType.LINK, UseMode.COMPONENT, contentHandler);
    this.xmlElement.endToSAX(contentHandler);
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

  // --------------------------------------------------------------------------------
  // toSAX methods
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Sends this document as SAX events to <code>contentHandler</code>.
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
           " this.type = " + this.type +
           ", this.id = " + this.id +
           ", this.refId = " + this.refId +
           ", this.useMode = " + this.useMode +
           ", this.withPath = " + this.withPath +
           ", resultSet = " + resultSet +
           ", ownDocument = " + ownDocument);
        this.check();
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
   *   Sends this document as SAX events to <code>contentHandler</code>.
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
   *   Sends this document as SAX events to <code>contentHandler</code>.
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

  /**
   * Throws a {@link SQLException SQLException} saying that the document is not
   * representable as XML in the current use mode.
   */

  protected void signalNotRepresentableAsXML ()
    throws SAXException
  {
    throw new SAXException
      ("Document not representable as XML in current use mode (" + this.useMode + ")");
  }

  /**
   * <p>
   *  Core part of the toSAX method if the use mode is {@link UseMode#SERVE SERVE}.
   * </p>
   * <p>
   *   This implementation simply calls {@link #signalNotRepresentableAsXML}. Extending
   *   classes should overwrite this if necessary.
   * </p>
   */

  protected void toSAX_SERVE (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException, SQLException
  {
    this.signalNotRepresentableAsXML();
  }

  /**
   * <p>
   *  Core part of the toSAX method if the use mode is {@link UseMode#INFO INFO}.
   * </p>
   * <p>
   *   This implementation simply calls {@link #signalNotRepresentableAsXML}. Extending
   *   classes should overwrite this if necessary.
   * </p>
   */

  protected void toSAX_INFO (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException, SQLException
  {
    this.signalNotRepresentableAsXML();
  }

  /**
   * <p>
   *  Core part of the toSAX method if the use mode is {@link UseMode#CHECKOUT CHECKOUT}.
   * </p>
   * <p>
   *   This implementation simply calls {@link #signalNotRepresentableAsXML}. Extending
   *   classes should overwrite this if necessary.
   * </p>
   */

  protected void toSAX_CHECKOUT (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException, SQLException
  {
    this.signalNotRepresentableAsXML();
  }

  /**
   * <p>
   *  Core part of the toSAX method if the use mode is {@link UseMode#COMPONENT COMPONENT}.
   * </p>
   * <p>
   *   This implementation simply calls {@link #signalNotRepresentableAsXML}. Extending
   *   classes should overwrite this if necessary.
   * </p>
   */

  protected void toSAX_COMPONENT (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException, SQLException
  {
    this.signalNotRepresentableAsXML();
  }

  /**
   * <p>
   *  Core part of the toSAX method if the use mode is {@link UseMode#LINK LINK}.
   * </p>
   * <p>
   *   This implementation simply calls {@link #signalNotRepresentableAsXML}. Extending
   *   classes should overwrite this if necessary.
   * </p>
   */

  protected void toSAX_LINK (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException, SQLException
  {
    this.signalNotRepresentableAsXML();
  }

  // --------------------------------------------------------------------------------
  // toStream methods
  // --------------------------------------------------------------------------------

  /**
   * Auxiliary method. Reads all data from <code>inputStream</code> and writes them to
   * <code>outputStream</code>.
   */

  protected void redirectStream (InputStream inputStream, OutputStream outputStream)
    throws IOException
  {
    this.logDebug("redirectStream 1/2: inputStream = " + inputStream);

    byte[] buffer = new byte[1024];
    int amount = 0;
    while ( (amount = inputStream.read(buffer)) > 0 )
      outputStream.write(buffer, 0, amount);
    outputStream.flush();

    this.logDebug("redirectStream 2/2");
  }

  /**
   * Writes a representation of the document to <code>outputStream</code>.
   *
   * @throws java.io.IOException if something goes wrong.
   */

  public void toStream (OutputStream outputStream)
    throws IOException
  {
    this.logDebug("toStream 1/2: outputStream = " + outputStream);

    try
      {
        this.check();
        switch ( this.useMode )
          {
          case UseMode.SERVE : toStream_SERVE(outputStream); break;
          default : this.signalNotRepresentableAsStream();
          }
      }
    catch (Exception exception)
      {
        throw new IOException (exception.getMessage());
      }

    this.logDebug("toStream 2/2");
  }

  /**
   * Throws a {@link IOException IOException} saying that the document is not
   * representable as a stream in the current use mode.
   */

  protected void signalNotRepresentableAsStream ()
    throws IOException
  {
    throw new IOException
      ("Document not representable as stream in current use mode (" + this.useMode + ")");
  }

  /**
   * <p>
   *  Core part of the toStream method if the use mode is {@link UseMode#SERVE SERVE}.
   * </p>
   * <p>
   *   This implementation simply calls {@link #signalNotRepresentableAsStream()}. Extending
   *   classes should overwrite this if necessary.
   * </p>
   */

  protected void toStream_SERVE (OutputStream outputStream)
    throws SQLException, IOException
  {
    this.signalNotRepresentableAsStream();
  }

  // --------------------------------------------------------------------------------
  // Content type, content length, last modified
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Returns the <em>Internet Media Type</em> of the document's content. This should be a
   *   numerical key for the real media type. See {@link notions.MediaType MediaType} for
   *   more information.  
   * <p>
   *   It is allowed that the method returns <code>-1</code>, which means that the media
   *   type is unknown.
   * </p>
   *
   * @return the internet media type.
   *
   * @throws SQLException if something goes wrong while retrieving the media type from the
   *                      database
   */

  public int getContentType ()
    throws ServiceException, SQLException
  {
    this.logDebug("getContentType");
    this.ensureDbHelper();
    ResultSet resultSet = this.dbHelper.queryDatum(this, DbColumn.CONTENT_TYPE);
    if ( ! resultSet.next() )
      throw new SQLException("Empty content type ResultSet");
    return resultSet.getInt(DbColumn.CONTENT_TYPE);
  }

  /**
   * <p>
   *   Returns the size of the document in bytes. This is what should appear in the
   *   <code>Content-Length</code> header of the HTTP response.
   * </p>
   * <p>
   *   It is allowed that the method returns <code>-1</code>, which means that the size
   *   is unknown.
   * </p>
   *
   * @return the size of the document.
   *
   * @throws SQLException if something goes wrong while retrieving the media type from the
   *                      database
   */

  public long getContentLength ()
    throws ServiceException, SQLException
  {
    this.logDebug("getContentLength");
    this.ensureDbHelper();
    ResultSet resultSet = this.dbHelper.queryDatum(this, DbColumn.CONTENT_LENGTH);
    if ( ! resultSet.next() )
      throw new SQLException("Empty content length ResultSet");
    return resultSet.getLong(DbColumn.CONTENT_LENGTH);
  }

  /**
   * <p>
   *   Returns the time the document was created.
   * </p>
   * <p>
   *   It is allowed that the method returns <code>null</code>, which means that the 
   *   creation time is unknown.
   * </p>
   *
   * @return the creation time of the document.
   *
   * @throws SQLException if something goes wrong while retrieving the creation
   *                      time from the database
   */

  public Date getCreated ()
    throws ServiceException, SQLException
  {
    final String METHOD_NAME = "getCreated";
    this.logDebug(METHOD_NAME + ": 1/2: Started");
    this.ensureDbHelper();
    ResultSet resultSet = this.dbHelper.queryDatum(this, DbColumn.CREATED);
    if ( ! resultSet.next() )
      throw new SQLException
        (this.getIdentification() + ": " + METHOD_NAME + ": Empty ResultSet");
    Date created = resultSet.getTimestamp(DbColumn.CREATED);
    this.logDebug(METHOD_NAME + ": 2/2: Done. created = " + created);
    return created;
  }

  /**
   * <p>
   *   Returns the time the document was last modified.
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
    this.logDebug(METHOD_NAME + ": 1/2: Started");
    this.ensureDbHelper();
    ResultSet resultSet = this.dbHelper.queryDatum(this, DbColumn.LAST_MODIFIED);
    if ( ! resultSet.next() )
      throw new SQLException
        (this.getIdentification() + ": " + METHOD_NAME + ": Empty ResultSet");
    Date lastModified = resultSet.getTimestamp(DbColumn.LAST_MODIFIED);
    this.logDebug(METHOD_NAME + ": 2/2: Done. lastModified = " + lastModified);
    return lastModified;
  }

  // --------------------------------------------------------------------------------
  // Misc.
  // --------------------------------------------------------------------------------

  /**
   * Returns a string that identificates this instance. Used in log messages.
   */

  public String getIdentification ()
  {
    return this.toString();
  }

  /**
   * Copies an array of strings.
   */

  protected static String[] copy (String[] array)
  {
    String[] copy = new String[array.length];
    System.arraycopy(array, 0, copy, 0, array.length);
    return copy;
  }
}
