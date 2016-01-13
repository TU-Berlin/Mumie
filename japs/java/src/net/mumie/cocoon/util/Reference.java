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

package net.mumie.cocoon.util;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.excalibur.xml.sax.XMLizable;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Represents a reference from one document to another. The reference can be written to XML
 * by means of the <code>toSAX</code> method inherited from {@link XMLizable XMLizable}.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: Reference.java,v 1.5 2007/07/11 15:38:54 grudzin Exp $</code>
 */

public interface Reference extends XMLizable, Identifyable
{
  /**
   * Role as an Avalon service (<code>"net.mumie.cocoon.util.Reference"</code>).
   */

  public static final String ROLE = Reference.class.getName();

  /**
   * Sets the document type of the reference origin, the document type of the reference
   * target, and the id of the reference.
   *
   * @param fromDocType the document type of the reference origin, as numerical code.
   * @param toDocType the document type of the reference target, as numerical code.
   * @param id the reference id.
   *
   * @throws IllegalArgumentException if <code>fromDocType</code> or <code>toDocType</code>
   * is not a valid document type codes, or if <code>id</code> is not a possible id value.
   */

  public void setup (int fromDocType, int toDocType, int id);

  /**
   * Returns the type of the reference origin document.
   */

  public int getFromDocType ();

  /**
   * Returns the type of the reference target document.
   */

  public int getToDocType ();

  /**
   * Returns the id of this reference.
   */

  public int getId ();

  /**
   * Returns the time this reference was last modified, or <code>-1</code> if the last
   * modification time can not be determined.
   */

  public Date getLastModified ()
    throws ServiceException, SQLException;

  /**
   * <p>
   *   Writes this reference as SAX events to <code>contentHandler</code>.
   * </p>
   * <p>
   *   If <code>ownDocument</code> is <code>true</code>, the <code>startDocument</code> and
   *   <code>endDocument</code> method of contentHandler is called before resp. after the
   *   XML is sent to SAX. 
   * </p>
   */

  public void toSAX (ContentHandler contentHandler, boolean ownDocument)
    throws SAXException;
}
