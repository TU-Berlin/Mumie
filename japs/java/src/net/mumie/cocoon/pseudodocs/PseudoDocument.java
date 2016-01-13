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

import org.apache.excalibur.xml.sax.XMLizable;
import org.apache.avalon.framework.service.ServiceException;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import java.sql.ResultSet;
import net.mumie.cocoon.notions.Id;
import java.util.Date;
import java.sql.SQLException;
import net.mumie.cocoon.util.Identifyable;

/**
 * Represents a pseudo document.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: PseudoDocument.java,v 1.16 2007/07/11 15:38:48 grudzin Exp $</code>
 */

public interface PseudoDocument
  extends XMLizable, Identifyable 
{
  /**
   * Role of an implementing class as an Avalon component
   * (<code>PseudoDocument.class.getName()</code>).
   */

  String ROLE = PseudoDocument.class.getName();

  /**
   * Returns the type of this pseudo-document, as numerical code.
   */

  public int getType ();

  /**
   * Returns the id of this pseudo-document.
   */

  public int getId ();

  /**
   * <p>
   *   Sets the id of this pseudo-document.
   * </p>
   * <p>
   *   The argument may also be {@link Id#DEFINED_ELSEWHERE DEFINED_ELSEWHERE}. This may be
   *   used in conjuncture which the
   *   {@link #toSAX(ResultSet,ContentHandler,boolean) toSAX(ResultSet,ContentHandler,boolean)}
   *   method. If the id is set to {@link Id#DEFINED_ELSEWHERE DEFINED_ELSEWHERE}, this
   *   method uses the id of the current row in the result set.
   * </p>
   */

  public void setId (int id)
    throws ServiceException;
  /**
   * Returns the use mode of this pseudo-document.
   */

  public int getUseMode ();

  /**
   * Sets the use mode of this pseudo-document.
   */

  public void setUseMode (int useMode)
    throws ServiceException;

  /**
   * Sets whether the path should be included in the XML.
   */

  public void setWithPath (boolean withPath);

  /**
   * Returns <code>true</code> if this pseudo-document is controlled by an external service
   * rather than from the JAPS platform. This is true for data actually located elsewhere
   * and represented in the database by "placeholders" only. Data of this kind is created
   * and changed by the synchronization mechanism.
   */

  public boolean isExternallyControlled ()
    throws ServiceException;

  /**
   * Returns the database columns this pseudo-document needs if one of the
   * <code>toSAX</code> methods would be called.
   */

  public String[] getDbColumns ();

  /**
   * Returns the specified data of the pseudo-document. The data are specified by column
   * names of the database table corresponding to this pseudo-document type.
   */

  public ResultSet getData (String[] dbColumns)
    throws ServiceException, SQLException;

  /**
   * <p>
   *   Returns the time this pseudo-document was last modified.
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
   * Makes this instance ready to represent another pseudo-document (of the same type).
   */

  public void reset();

  /**
   * <p>
   *   Sends this pseudo-document as SAX events to <code>contentHandler</code>.
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
   *   Sends this pseudo-document as SAX events to <code>contentHandler</code>.
   * </p>
   * <p>
   *   If <code>ownDocument</code> is true, the <code>startDocument</code> and
   *   <code>endDocument</code> method of <code>contentHandler</code> is called before
   *   resp. after the XML is sent to SAX.
   * </p>
   */

  public void toSAX (ContentHandler contentHandler, boolean ownDocument)
    throws SAXException;
}
