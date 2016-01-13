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
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.UseMode;
import net.mumie.cocoon.util.Identifyable;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.cocoon.xml.XMLizable;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Represents a document.
 *
 * @author Tilman Rassy
 *
 * @version <span class="file">$Id: Document.java,v 1.29 2007/07/11 15:38:44 grudzin Exp $</span>
 */

public interface Document extends XMLizable, Identifyable
{
  /**
   * Role of an implementing class as an Avalon component. Value is
   * <span class="string">"net.mumie.cocoon.documents.Document"</span>.
   */

  String ROLE = "net.mumie.cocoon.documents.Document";

  /**
   * Value indicating that a variable has not been defined.
   */

  int UNDEFINED = -1;

  /**
   * Returns the type of the document. See
   * {@link net.mumie.cocoon.notions.DocType} for
   * the possible types and their meanings.
   */

  public int getType ();

  /**
   * Sets the id of the document. 
   *
   * @throws IllegalArgumentException if <code>id</code> is not suitable as an id
   * (e.g., if it is negative).
   */

  public void setId (int id)
    throws IllegalArgumentException;

  /**
   * Returns the id of the document. May also return {@link #UNDEFINED} to indicate that the
   * id of the document is not specified.
   *
   * @return the id of the document
   */

  public int getId ();

  /**
   * Sets the local id of the document. 
   *
   * @throws IllegalArgumentException if <code>lid</code> is not suitable as an local id.
   */

  public void setLid (String lid)
    throws IllegalArgumentException;

  /**
   * Returns the local id of the document
   *
   * @return the local id of the document
   */

  public String getLid ();

  /**
   * Sets the use mode of the document. (The use mode specifies the
   * way the document is used in the current context.) See
   * {@link net.mumie.cocoon.notions.UseMode} for the possible use
   * modes and their meanings. 
   *
   * @throws IllegalArgumentException if <code>useMode</code> is not in the range of
   * possible use modes.
   */

  public void setUseMode (int useMode)
    throws IllegalArgumentException;

  /**
   * Returns the use mode of the document.
   *
   * @return the use mode of the document.
   */

  public int getUseMode ();

  /**
   * Sets whether the path should be included in the XML.
   */

  public void setWithPath (boolean withPath);

  /**
   * Returns whether the path should be included in the XML.
   */

  public boolean getWithPath ();

  /**
   * <p>
   *   Sets the id of the reference pointing to this document.
   * </p>
   * <p>
   *   This id is meaningful only if this object represents a document that is referenced by
   *   another document, e.g., if the use mode is {@link UseMode#COMPONENT UseMode.COMPONENT}
   *   or {@link UseMode#LINK UseMode.LINK}.
   * </p>
   */

  public void setRefId (int refId);

  /**
   * <p>
   *   Returns the id of the reference pointing to this document.
   * </p>
   * <p>
   *   This value is meaningful only if this object represents a document that is referenced
   *   by another document, e.g., if the use mode is
   *   {@link UseMode#COMPONENT UseMode.COMPONENT} or {@link UseMode#LINK UseMode.LINK}.
   *   Otherwise, this method should return <code>-1</code>.
   * </p>
   */

  public int getRefId ();

  /**
   * <p>
   *   Sets the type of the document that is the origin of the reference pointing to this 
   *   document.
   * </p>
   * <p>
   *   This value is meaningful only if this object represents a document that is referenced by
   *   another document, e.g., if the use mode is {@link UseMode#COMPONENT UseMode.COMPONENT}
   *   or {@link UseMode#LINK UseMode.LINK}.
   * </p>
   */

  public void setFromDocType (int fromDocType);

  /**
   * <p>
   *   Returns the type of the document that is the origin of the reference pointing to this 
   *   document.
   * </p>
   * <p>
   *   This value is meaningful only if this object represents a document that is referenced by
   *   another document, e.g., if the use mode is {@link UseMode#COMPONENT UseMode.COMPONENT}
   *   or {@link UseMode#LINK UseMode.LINK}.
   *   Otherwise, this method should return <code>-1</code>.
   * </p>
   */

  public int getFromDocType ();

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
    throws ServiceException, SQLException;

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
    throws ServiceException, SQLException;

  /**
   * <p>
   *   Returns the creation time of the document
   * </p>
   * <p>
   *   The method may return <code>null</code>, which means
   *   that the creation time is unknown.
   * </p>
   *
   * @return the creation date, or, if that's impossible to be looked up,
   * the date of last modification
   * @see #getLastModified()
   */    

  public Date getCreated ()
    throws ServiceException, SQLException;

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
    throws ServiceException, SQLException;

  /**
   * Writes a representation of the document to <code>outputStream</code>.
   *
   * @throws java.io.IOException if something goes wrong.
   */

  public void toStream (OutputStream outputStream)
    throws IOException;

  /**
   * Returns the database columns this document needs if one of the
   * <code>toSAX</code> methods would be called.
   */

  public String[] getDbColumns ()
    throws ServiceException;

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
    throws SAXException;

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
    throws SAXException;

  /**
   * Makes this object ready to represent another document
   *
   * @throws org.apache.avalon.framework.service.ServiceException if something
   *         goes wrong.
   */

  public void reset ()
    throws ServiceException;
}
