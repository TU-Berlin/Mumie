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
import java.util.Date;
import net.mumie.cocoon.util.Identifyable;
import org.apache.excalibur.xml.sax.XMLizable;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Represents a document or pseudo-document.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: ContentObject.java,v 1.2 2007/07/11 15:38:42 grudzin Exp $</code>
 */

public interface ContentObject
  extends XMLizable, Identifyable 
{
  /**
   * Role of an implementing class as an Avalon component
   * (<code>ContentObject.class.getName()</code>).
   */

  String ROLE = ContentObject.class.getName();

  /**
   * Returns the nature (document or pseudo-document) of this content object.
   */

  public int getNature ();

  /**
   * Returns the type of this content object, as numerical code.
   */

  public int getType ();

  /**
   * Returns the type of this content object, as string name.
   */

  public String getTypeName ();

  /**
   * Returns the id of this content object.
   */

  public int getId ();

  /**
   * Sets the id of this content object.
   */

  public void setId (int id);

  /**
   * Returns the use mode of this content object.
   */

  public int getUseMode ();

  /**
   * Sets the use mode of this content object.
   */

  public void setUseMode (int useMode);

  /**
   * Returns the database columns this content object needs if one of the
   * <code>toSAX</code> methods would be called.
   */

  public String[] getDbColumns ();

  /**
   * <p>
   *   Returns the time this content object was last modified.
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
    throws ContentObjectException;

  /**
   * Makes this instance ready to represent another content object.
   */

  public void reset()
    throws ContentObjectException;

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
    throws SAXException;

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
    throws SAXException;
}
