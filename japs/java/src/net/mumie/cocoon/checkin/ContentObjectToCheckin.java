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

package net.mumie.cocoon.checkin;

import java.sql.SQLException;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.Nature;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.pseudodocs.User;
import net.mumie.cocoon.util.Identifyable;
import net.mumie.cocoon.util.PasswordEncryptor;
import org.apache.excalibur.xml.sax.XMLizable;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public interface ContentObjectToCheckin
  extends Identifyable, XMLizable 
{
  /**
   * Role as an Avalon service (<code>ContentObjectToCheckin.class.getName()</code>).
   */

  public static final String ROLE = ContentObjectToCheckin.class.getName();

  /**
   * Initializes this content object with the specified master, content, source, and
   * path. If the content object has no content (generic documents, pseudo-documents), the
   * content must be <code>null</code>. If the content object has no (primary) source, the
   * source must be <code>null</code>.
   */

  public void setup (Master master, Content content, Source source, String path)
    throws CheckinException;

  /**
   * Returns the nature of this content object. The return value is either
   * {@link Nature#DOCUMENT Nature.DOCUMENT} (for a proper document) or
   * {@link Nature#PSEUDO_DOCUMENT Nature.PSEUDO_DOCUMENT} (for a pseudo-document).
   */

  public int getNature ();

  /**
   * Returns the type of this content object, as numerical code. If the content object is a
   * proper document, this is one of the type constants defined in
   * {@link DocType DocType}. If the content object is a pseudo document, this is one of the
   * type constants defined in {@link PseudoDocType PseudoDocType}.
   */

  public int getType ();

  /**
   * Returns the id of this content object, or {@link Id#UNDEFINED Id.UNDEFINED} if the id
   * is not known yet.
   */

  public int getId ();

  /**
   * Returns the path of this content object.
   */

  public String getPath ();

  /**
   * Checks-in this content object in the specified stage using the specified db helper. The
   * <code>user</code> object should represent the user who initiated the checkin. It can be
   * used to check permissions. The <code>encryptor</code> may be used to encrypt
   * confidential data, e.g., passwords.
   */

  public void checkin (int stage,
                       DbHelper dbHelper,
                       User user,
                       PasswordEncryptor encryptor,
                       Master defaults)
    throws CheckinException;

  /**
   * Writes an XML representation of this object as SAX events to the specified content
   * handler. If <code>ownDocument</code> is <code>false</code>, the
   * <code>startDocument</code> and <code>endDocument</code> calls are suppressed.
   */

  public void toSAX (ContentHandler contentHandler, boolean ownDocument)
    throws SAXException;
}
