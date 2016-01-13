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

public interface ProblemContext
  extends Identifyable, XMLizable 
{
  /**
   * Role as an Avalon service (<code>ProblemContext.class.getName()</code>).
   */

  public static final String ROLE = ProblemContext.class.getName();

  /**
   * Name of the field containing the sum of max_points per worksheet (<code>"worksheet_max_points"</code>). 
   */

  static final String WORKSHEET_MAX_POINTS = "worksheet_max_points";

  /**
   * Sets the data fields and restrictions.
   */

  public void setup (String[] fields,
                     int[] classIds,
                     String[] classSyncIds,
                     int[] courseIds,
                     int[] worksheetIds,
                     String[] worksheetLids,
                     String[] worksheetLabels,
                     int[] worksheetCategoryIds,
                     String[] worksheetCategoryNames,
                     int[] problemIds,
                     int[] problemRefIds,
                     String[] problemLids,
                     String[] problemLabels,
                     int[] tutorialIds,
                     String[] tutorialSyncIds);

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
