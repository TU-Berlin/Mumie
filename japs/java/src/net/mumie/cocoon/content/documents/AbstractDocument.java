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

package net.mumie.cocoon.content.documents;

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
import net.mumie.cocoon.content.AbstractContentObject;
import net.mumie.cocoon.content.ContentObjectException;
import net.mumie.cocoon.content.ToSAXUtil;
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
import net.mumie.cocoon.service.AbstractJapsServiceable;
import net.mumie.cocoon.util.LogUtil;
import net.mumie.cocoon.xml.FragmentSAXFilter;
import net.mumie.cocoon.xml.MetaXMLElement;
import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
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
 * @version <code>$Id: AbstractDocument.java,v 1.3 2007/08/15 16:24:10 lehmannf Exp $</code>
 */

public abstract class AbstractDocument extends AbstractContentObject
  implements Document
{
  // --------------------------------------------------------------------------------
  // Global variables
  // --------------------------------------------------------------------------------

  /**
   * The local id of this document. May be <code>null</code>, meaning this document has no
   * local id.
   */

  protected String lid = null;

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
   * The date format used by default.
   */

  protected SimpleDateFormat dateFormat = new SimpleDateFormat(TimeFormat.DEFAULT);

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
    super.reset();
    this.lid = null;
    this.refId = Id.UNDEFINED;
    this.fromDocType = DocType.UNDEFINED;
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // Get / set methods
  // --------------------------------------------------------------------------------

  /**
   * Sets the local id of the document. 
   */

  public void setLid (String lid)
  {
    this.lid = lid;
  }

  /**
   * Returns the local id of the document
   */

  public String getLid ()
  {
    return this.lid;
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
  // Auxiliaries for the toSAX methods
  // --------------------------------------------------------------------------------

  /**
   * Returns the result set needed in the toSAX method.
   */

  protected ResultSet getResultSet ()
    throws SQLException
  {
    if ( this.refId == Id.UNDEFINED )
      return this.dbHelper.queryData(this.type, this.id, this.getDbColumns());
    else
      return this.dbHelper.queryDataOfRefTarget
        (this.fromDocType, this.type, this.refId, this.getDbColumns());
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
   * Writes the {@link XMLElement#QUALIFIED_NAME QUALIFIED_NAME} element.
   */

  protected void qualifiedNameToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    ToSAXUtil.qualifiedNameToSAX(resultSet, this.xmlElement, contentHandler);
  }

  /**
   * Writes the {@link XMLElement#COPYRIGHT COPYRIGHT} element.
   */

  protected void copyrightToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    ToSAXUtil.copyrightToSAX(resultSet, this.xmlElement, contentHandler);
  }

  /**
   * Writes the {@link XMLElement#VC_THREAD VC_THREAD} element.
   */

  protected void vcThreadToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    ToSAXUtil.vcThreadToSAX(resultSet, this.xmlElement, contentHandler);
  }

  /**
   * Writes the {@link XMLElement#VERSION VERSION} element.
   */

  protected void versionToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    ToSAXUtil.versionToSAX(resultSet, this.xmlElement, contentHandler);
  }

  /**
   * Writes the {@link XMLElement#TIMEFRAME_START TIMEFRAME_START} element.
   */

  protected void timeframeStartToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    ToSAXUtil.timeframeStartToSAX(resultSet, this.xmlElement, contentHandler);
  } 

  /**
   * Writes the {@link XMLElement#TIMEFRAME_END TIMEFRAME_END} element.
   */

  protected void timeframeEndToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    ToSAXUtil.timeframeEndToSAX(resultSet, this.xmlElement, contentHandler);
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
   * Writes the {@link XMLElement#WIDTH WIDTH} element.
   */

  protected void widthToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    ToSAXUtil.widthToSAX(resultSet, this.xmlElement, contentHandler);
  }

  /**
   * Writes the {@link XMLElement#HEIGHT HEIGHT} element.
   */

  protected void heightToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    ToSAXUtil.heightToSAX(resultSet, this.xmlElement, contentHandler);
  }

  /**
   * Writes the {@link XMLElement#DURATION DURATION} element.
   */

  protected void durationToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    ToSAXUtil.durationToSAX(resultSet, this.xmlElement, contentHandler);
  }

  /**
   * Writes the {@link XMLElement#CONTENT_TYPE CONTENT_TYPE} element.
   */

  protected void contentTypeToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    ToSAXUtil.contentTypeToSAX(resultSet, this.xmlElement, contentHandler);
  }

  /**
   * Writes the {@link XMLElement#CONTENT_LENGTH CONTENT_LENGTH} element.
   */

  protected void contentLengthToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    ToSAXUtil.contentLengthToSAX(resultSet, this.xmlElement, contentHandler);
  }

  /**
   * Writes the {@link XMLElement#CORRECTOR CORRECTOR} element.
   */

  protected void correctorToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException, SQLException
  {
    ToSAXUtil.correctorToSAX(resultSet, this.xmlElement, contentHandler);
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
    final String METHOD_NAME =
      "referencesToSAX (" + 
      "int toDocType, " +
      "int refType, " +
      "int useMode, " +
      "ContentHandler contentHandler)";
    this.logDebug
      (METHOD_NAME + " 1/2: Started." +
       " toDocType = " + toDocType +
       ", refType = " + refType +
       ", useMode = " + useMode +
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
        ResultSet resultSet = this.dbHelper.queryDataOfReferencedDocs
          (this.type, this.id, toDocType, refType, columns);
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
   * representations)
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
   * @throws ContentObjectException if something goes wrong while retrieving the media type
   *   from the database
   */

  public int getContentType ()
    throws ContentObjectException
  {
    try
      {
        this.logDebug("getContentType");
        this.ensureDbHelper();
        ResultSet resultSet =
          this.dbHelper.queryDatum(this.type, this.id, DbColumn.CONTENT_TYPE);
        if ( ! resultSet.next() )
          throw new SQLException("Empty content type ResultSet");
        return resultSet.getInt(DbColumn.CONTENT_TYPE);
      }
    catch (Exception exception)
      {
        throw new ContentObjectException(exception);
      }
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
   * @throws ContentObjectException if something goes wrong while retrieving the media type
   *   from the database
   */

  public long getContentLength ()
    throws ContentObjectException
  {
    try
      {
        this.logDebug("getContentLength");
        this.ensureDbHelper();
        ResultSet resultSet =
          this.dbHelper.queryDatum(this.type, this.id, DbColumn.CONTENT_LENGTH);
        if ( ! resultSet.next() )
          throw new SQLException("Empty content length ResultSet");
        return resultSet.getLong(DbColumn.CONTENT_LENGTH);
      }
    catch (Exception exception)
      {
        throw new ContentObjectException(exception);
      }
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
   * @throws ContentObjectException if something goes wrong while retrieving the creation
   *   time from the database
   */

  public Date getCreated ()
    throws ContentObjectException
  {
    try
      {
        final String METHOD_NAME = "getCreated";
        this.logDebug(METHOD_NAME + ": 1/2: Started");
        this.ensureDbHelper();
        ResultSet resultSet =
          this.dbHelper.queryDatum(this.type, this.id, DbColumn.CREATED);
        if ( ! resultSet.next() )
          throw new SQLException
            (this.getIdentification() + ": " + METHOD_NAME + ": Empty ResultSet");
        Date created = resultSet.getTimestamp(DbColumn.CREATED);
        this.logDebug(METHOD_NAME + ": 2/2: Done. created = " + created);
        return created;
      }
    catch (Exception exception)
      {
        throw new ContentObjectException(exception);
      }
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
   * @throws ContentObjectException if something goes wrong while retrieving the last
   *   modification time from the database
   */

  public Date getLastModified ()
    throws ContentObjectException 
  {
    try
      {
        final String METHOD_NAME = "getLastModified";
        this.logDebug(METHOD_NAME + ": 1/2: Started");
        this.ensureDbHelper();
        ResultSet resultSet =
          this.dbHelper.queryDatum(this.type, this.id, DbColumn.LAST_MODIFIED);
        if ( ! resultSet.next() )
          throw new SQLException
            (this.getIdentification() + ": " + METHOD_NAME + ": Empty ResultSet");
        Date lastModified = resultSet.getTimestamp(DbColumn.LAST_MODIFIED);
        this.logDebug(METHOD_NAME + ": 2/2: Done. lastModified = " + lastModified);
        return lastModified;
      }
    catch (Exception exception)
      {
        throw new ContentObjectException(exception);
      }
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
