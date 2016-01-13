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

import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.mumie.srv.entities.pseudodocs.ELClass;
import net.mumie.srv.notions.Category;
import net.mumie.srv.notions.DbColumn;
import net.mumie.srv.notions.DbTable;
import net.mumie.srv.notions.EntityType;
import net.mumie.srv.notions.Id;
import net.mumie.srv.notions.MediaType;
import net.mumie.srv.notions.RefType;
import net.mumie.srv.notions.UseMode;
import net.mumie.srv.notions.XMLAttribute;
import net.mumie.srv.notions.XMLElement;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceSelector;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;


public abstract class AbstractDocument extends AbstractEntity
  implements Document
{
  // --------------------------------------------------------------------------------
  // h1: Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The local id of this document, or null if this document has no local id. This is used
   * only if this instance represents a reference target.
   */

  protected String lid = null;

  /**
   * The id of the reference pointing to this document, or {@link Id#UNDEFINED UNDEFINED} if
   * this instance does not represent a reference target.
   */

  protected int refId = Id.UNDEFINED;

  /**
   * The the type of the document that is the origin of the reference pointing to this
   * document, or {@link Entity#UNDEFINED UNDEFINED} if this instance does not represent a
   * reference target.
   */

  protected int refOriginType = EntityType.UNDEFINED;

  // --------------------------------------------------------------------------------
  // h1: Lifecycle methods
  // --------------------------------------------------------------------------------

  /**
   * Makes this instance ready to represent another document.
   */

  public void reset()
  {
    super.reset();
    this.lid = null;
    this.refId = Id.UNDEFINED;
    this.refOriginType = EntityType.UNDEFINED;
  }

  // --------------------------------------------------------------------------------
  // h1: Set methods
  // --------------------------------------------------------------------------------

  /**
   * Sets the local id of the document. This is used only if this instance represents a
   * reference target.
   */

  public void setLid (String lid)
  {
    this.lid = lid;
  }

  /**
   * Sets the id of the reference pointing to this document. This is used only if this
   * instance represents a reference target.
   */

  public void setRefId (int refId)
  {
    this.refId = refId;
  }

  /**
   * Sets the type of the document that is the origin of the reference pointing to this
   * document. This is used only if this instance represents a reference target.
   */

  public void setRefOriginType (int refOriginType)
  {
    this.refOriginType = refOriginType;
  }

  // --------------------------------------------------------------------------------
  // h1: Get methods
  // --------------------------------------------------------------------------------

  /**
   * Returns the local id of the document, or null if this document has no local id. This is
   * used only if this instance represents a reference target.
   */

  public String getLid ()
  {
    return this.lid;
  }

  /**
   * Returns the id of the reference pointing to this document, or
   * {@link Id#UNDEFINED UNDEFINED} if this instance does not represent a document that is
   * the target of e reference.
   */

  public int getRefId ()
  {
    return this.refId;
  }

  /**
   * Returns the type of the document that is the origin of the reference pointing to this
   * document, or {@link Entity#UNDEFINED UNDEFINED} if this instance does not represent a
   * reference target.
   */

  public int getRefOriginType ()
  {
    return this.refOriginType;
  }

  // --------------------------------------------------------------------------------
  // h1: DbHelper methods
  // --------------------------------------------------------------------------------

  // -- Nothing here; respective methods inherited from AbstractEntity --

  // --------------------------------------------------------------------------------
  // h1: Querying information about the entity
  // --------------------------------------------------------------------------------

  /**
   * Returns the <em>Internet Media Type</em> of the document's content. This should be a
   * numerical key for the real media type. See {@link MediaType MediaType} for more
   * information. It is allowed that the method returns <code>-1</code>, which means that
   * the media type is unknown.
   *
   * @return the internet media type.
   *
   * @throws SQLException if something goes wrong while retrieving the media type from the
   *   database
   */

  public int getContentType ()
    throws ServiceException, SQLException
  {
    this.logDebug("getContentType");
    this.ensureDbHelper();
    return this.dbHelper.queryEntityDatumAsInt(this.type, this.id, DbColumn.CONTENT_TYPE);
  }

  /**
   * Returns the size of the document in bytes. This is what should appear in the
   * <code>Content-Length</code> header of the HTTP response. It is allowed that the method
   * returns <code>-1</code>, which means that the size is unknown.
   *
   * @return the size of the document.
   *
   * @throws SQLException if something goes wrong while retrieving the media type from the
   *   database
   */

  public long getContentLength ()
    throws ServiceException, SQLException
  {
    this.logDebug("getContentLength");
    this.ensureDbHelper();
    return this.dbHelper.queryEntityDatumAsLong(this.type, this.id, DbColumn.CONTENT_LENGTH);
  }

  // --------------------------------------------------------------------------------
  // h1: Auxiliaries for the getDbColumn method
  // --------------------------------------------------------------------------------

  // -- Nothing here; respective method(s) inherited from AbstractEntity --

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
    if ( this.path != null )
      this.xmlElement.addAttribute(XMLAttribute.PATH, this.path);
    if ( this.lid != null )
      this.xmlElement.addAttribute(XMLAttribute.LID, this.lid);
    if ( this.refId != Id.UNDEFINED )
      this.xmlElement.addAttribute(XMLAttribute.REF_ID, this.refId);
    this.xmlElement.startToSAX(contentHandler);
  }

  // --------------------------------------------------------------------------------
  // h1: toSAX methods for specific data
  // --------------------------------------------------------------------------------

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Category
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Writes the {@link XMLElement#CATEGORY CATEGORY} element. If the category column is SQL
   * NULL, the element is suppressed.
   */

  protected void categoryToSAX (ResultSet resultSet,
                                ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        int categoryId = resultSet.getInt(DbColumn.CATEGORY);
        if ( !resultSet.wasNull() )
          {
            this.xmlElement.reset();
            this.xmlElement.setLocalName(XMLElement.CATEGORY);
            this.xmlElement.addAttribute(XMLAttribute.ID, categoryId);
            this.xmlElement.addAttribute(XMLAttribute.NAME, Category.nameFor(categoryId));
            this.xmlElement.toSAX(contentHandler);
          }
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("categoryToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Qualified name
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Writes the {@link XMLElement#QUALIFIED_NAME QUALIFIED_NAME} element.
   */

  protected void qualifiedNameToSAX (ResultSet resultSet,
                                         ContentHandler contentHandler)
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

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Copyright
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Writes the {@link XMLElement#COPYRIGHT COPYRIGHT} element.
   */

  protected void copyrightToSAX (ResultSet resultSet,
                                 ContentHandler contentHandler)
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

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: VC thread, version
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Writes the {@link XMLElement#VC_THREAD VC_THREAD} element.
   */

  protected void vcThreadToSAX (ResultSet resultSet,
                                ContentHandler contentHandler)
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

  protected void versionToSAX (ResultSet resultSet,
                               ContentHandler contentHandler)
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

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Summary
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Writes the {@link XMLElement#SUMMARY SUMMARY} element.
   */

  protected void summaryToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        String value = resultSet.getString(DbColumn.SUMMARY);
        if ( value != null )
          {
            this.xmlElement.reset();
            this.xmlElement.setLocalName(XMLElement.ofEntityType(EntityType.GENERIC_SUMMARY));
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

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Corrector
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Writes the {@link XMLElement#CORRECTOR CORRECTOR} element.
   */

  protected void correctorToSAX (ResultSet resultSet,
				 ContentHandler contentHandler)
    throws SAXException
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

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Class
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Writes the {@link XMLElement#CLASS CLASS} element.
   */

  protected void classToSAX (ResultSet resultSet,
                             ContentHandler contentHandler)
    throws SAXException
  {
    final String METHOD_NAME = "classToSAX";
    this.logDebug(METHOD_NAME + "1/2: Started");
    ELClass elClass = null;
    try
      {
        int classId = resultSet.getInt(DbColumn.CLASS);
        if ( !resultSet.wasNull() )
          {
            elClass = (ELClass)this.serviceManager.lookup(ELClass.ROLE);
            elClass.setId(classId);
            elClass.setUseMode(UseMode.COMPONENT);
            elClass.toSAX(contentHandler, false);
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

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Metainfos with time values
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  // ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
  // h3: Timeframe start/end
  // ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~

  /**
   * Writes the {@link XMLElement#TIMEFRAME_START TIMEFRAME_START} element.
   */

  protected  void timeframeStartToSAX (ResultSet resultSet,
                                       ContentHandler contentHandler)
    throws SAXException
  {
    timeToSAX
      (resultSet, contentHandler,
       DbColumn.TIMEFRAME_START, XMLElement.TIMEFRAME_START);
  } 

  /**
   * Writes the {@link XMLElement#TIMEFRAME_END TIMEFRAME_END} element.
   */

  protected  void timeframeEndToSAX (ResultSet resultSet,
                                     ContentHandler contentHandler)
    throws SAXException
  {
    timeToSAX
      (resultSet, contentHandler,
       DbColumn.TIMEFRAME_END, XMLElement.TIMEFRAME_END);
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Thumbnail, info page
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Writes the {@link XMLElement#THUMBNAIL THUMBNAIL} element. If the thumbnail column is SQL
   * NULL, the element is suppressed.
   */

  protected void thumbnailToSAX (ResultSet resultSet,
                                 ContentHandler contentHandler)
    throws SAXException
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
   * Writes the {@link XMLElement#INFO_PAGE INFO_PAGE} element. If the info_page column is SQL
   * NULL, the element is suppressed.
   */

  protected void infoPageToSAX (ResultSet resultSet,
                                ContentHandler contentHandler)
    throws SAXException
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

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Width, height, duration
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Writes the {@link XMLElement#WIDTH WIDTH} element.
   */

  protected void widthToSAX (ResultSet resultSet,
                             ContentHandler contentHandler)
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

  protected void heightToSAX (ResultSet resultSet,
                              ContentHandler contentHandler)
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

  protected void durationToSAX (ResultSet resultSet,
                                ContentHandler contentHandler)
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

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Content type, content length
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Writes the {@link XMLElement#CONTENT_TYPE CONTENT_TYPE} element.
   */

  protected void contentTypeToSAX (ResultSet resultSet,
                                   ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.CONTENT_TYPE);
        int contentTypeId = resultSet.getInt(DbColumn.CONTENT_TYPE);
        this.xmlElement.addAttribute(XMLAttribute.ID, contentTypeId);
        this.xmlElement.addAttribute(XMLAttribute.VALUE, MediaType.nameFor(contentTypeId));
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

  protected void contentLengthToSAX (ResultSet resultSet,
                                               ContentHandler contentHandler)
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

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Reference attributes
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Writes the {@link XMLElement#LABEL LABEL} element.
   */

  protected void labelToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.LABEL);
        this.xmlElement.setText(resultSet.getString(DbColumn.LABEL));
        this.xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("labelToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#POINTS POINTS} element.
   */

  protected void pointsToSAX (ResultSet resultSet,
			      ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.POINTS);
        this.xmlElement.addAttribute(XMLAttribute.VALUE, resultSet.getInt(DbColumn.POINTS));
        this.xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("pointsToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

  /**
   * Convienience method to write label and points to SAX, as far as they exist.
   */

  protected void refAttribsToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    if ( EntityType.refsHaveLabels(this.refOriginType, this.type) )
      this.labelToSAX(resultSet, contentHandler);

    if ( EntityType.refsHavePoints(this.refOriginType, this.type) )
      this.pointsToSAX(resultSet, contentHandler);
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: References
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Sends all references with a certain target document type to SAX.
   *
   * @param targetType the type of the document the references point to.
   * @param refType the type of the references.
   * @param useMode the use mode given to the referenced documents (controls their XML
   *   representations)
   * @param contentHandler recieves the SAX events.
   */

  protected void referencesToSAX (int targetType,
                                  int refType,
                                  int useMode,
                                  ContentHandler contentHandler)
    throws SAXException
  {
    final String METHOD_NAME = "referencesToSAX";
    this.logDebug
      (METHOD_NAME + " 1/2: Started." +
       " targetType = " + targetType +
       ", refType = " + refType +
       ", useMode = " + useMode +
       ", withPath = " + this.withPath);
    ServiceSelector documentSelector = null;
    Document target = null;
    try
      {
        documentSelector =
          (ServiceSelector)this.serviceManager.lookup(Document.ROLE + "Selector");
        target = (Document)documentSelector.select(EntityType.hintFor(targetType));
        target.setUseMode(useMode);
        target.setWithPath(this.withPath);
        String[] columns = target.getDbColumns();
        this.ensureDbHelper();
        ResultSet resultSet =
          this.dbHelper.queryReferencedDocs(this.type, this.id, targetType, refType, columns);
        if ( resultSet == null )
          {
            this.logDebug(METHOD_NAME + " 2/2: Done. resultSet = null");
            return;
          }
        while ( resultSet.next() )
          {
            target.setId(resultSet.getInt(DbColumn.ID));
            target.setLid(resultSet.getString(DbColumn.LID));
            target.setRefId(resultSet.getInt(DbColumn.REF));
            target.setRefOriginType(this.type);
            target.toSAX(resultSet, contentHandler, false);
          }
      }
    catch (Exception exception)
      {
        throw new SAXException(exception);
      }
    finally
      {
        if ( target != null ) documentSelector.release(target);
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
    for (int targetType : EntityType.docTypes())
      if ( DbTable.ofRefs(this.type, targetType) != null )
	referencesToSAX(targetType, refType, useMode, contentHandler);
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

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: XML data
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  // ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
  // h3: Content
  // ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~

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

  // --------------------------------------------------------------------------------
  // h1: Other auxiliaries for the toSAX methods
  // --------------------------------------------------------------------------------

  /**
   * Returns the result set needed in the toSAX method.
   */

  protected ResultSet getResultSet ()
    throws SQLException, ServiceException
  {
    if ( this.refId == Id.UNDEFINED )
      return this.dbHelper.queryEntity(this.type, this.id, this.getDbColumns());
    else
      return this.dbHelper.queryRefTarget
        (this.refOriginType, this.type, this.refId, this.getDbColumns());
  }

  // --------------------------------------------------------------------------------
  // h1: toStream methods for specified data
  // --------------------------------------------------------------------------------

  /**
   * Writes the content of this document to the specified output stream.
   */

  protected void contentToStream (OutputStream out)
    throws IOException, SQLException
  {
    final String METHOD_NAME = "contentToStream";
    this.getLogger().debug(METHOD_NAME + "1/2: Started");
    this.blobToStream(DbColumn.CONTENT, out);
    this.getLogger().debug(METHOD_NAME + "2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // h1: toStream methods
  // --------------------------------------------------------------------------------

  // -- Nothing here; respective methods are defined in subclasses and AbstractEntity --
}
