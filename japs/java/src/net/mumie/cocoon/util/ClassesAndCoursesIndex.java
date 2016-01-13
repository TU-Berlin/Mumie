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

import org.apache.excalibur.xml.sax.XMLizable;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * List containing all e-learning classes and all (latest) courses.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: ClassesAndCoursesIndex.java,v 1.3 2007/07/11 15:38:52 grudzin Exp $</code>
 */

public interface ClassesAndCoursesIndex
  extends XMLizable, Identifyable
{
  /**
   * Role as an Avalon service
   * (<code>"ClassesAndCoursesIndex.class.getName()</code>).
   */

  public static final String ROLE = ClassesAndCoursesIndex.class.getName();

  /**
   * Sends the list as SAX events to <code>contentHandler</code>. If
   * <code>ownDocument</code> is true, the <code>startDocument</code> and
   * <code>endDocument</code> method of <code>contentHandler</code> is called before
   * resp. after the XML is sent to SAX.
   */

  public void toSAX (ContentHandler contentHandler, boolean ownDocument)
    throws SAXException;
}
