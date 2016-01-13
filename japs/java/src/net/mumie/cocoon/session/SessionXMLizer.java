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

package net.mumie.cocoon.session;

import javax.servlet.http.HttpSession;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * <p>
 *   Creates XML representations of sessions and outputs them as SAX events.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: SessionXMLizer.java,v 1.5 2007/07/11 15:38:49 grudzin Exp $</code>
 */

public interface SessionXMLizer
{
  /**
   * Role as an Avalon service (<code>SessionXMLizer.class.getName()</code>
   */

  public static final String ROLE = SessionXMLizer.class.getName();

  /**
   * Sends the specified Http session as SAX events to the specified content handler. If
   * <code>ownDocument</code>  is <code>true</code>, the <code>startDocument</code> and
   * <code>endDocument</code> methods are called before resp. after, otherwise, they are
   * suppressed.
   */

  public void sessionToSAX (HttpSession session,
                            ContentHandler contentHandler,
                            boolean ownDocument)
    throws SAXException;

  /**
   * Sends an XML representation of all currently active Http sessions as SAX events to the
   * specified content handler. If <code>ownDocument</code> is <code>true</code>, the
   * <code>startDocument</code> and <code>endDocument</code> methods are called before
   * resp. after, otherwise, they are suppressed.
   */

  public void sessionsToSAX (ContentHandler contentHandler,
                             boolean ownDocument)
    throws SAXException;
}  
