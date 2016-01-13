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

package net.mumie.cocoon.grade;

import net.mumie.cocoon.util.Identifyable;
import net.mumie.cocoon.util.JapsFile;

import org.apache.excalibur.xml.sax.XMLizable;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Represents the grades for a given user in all problems in a given worksheet. The grades
 * may be obtained in XML form by calling the {@link #toSAX toSAX} method. The XML has the
 * following form (example): <pre>
  &lt;mumie:user_worksheet_grades worksheet_id="34"&gt;
    &lt;mumie:problem_grade problem_id="412"
                         category="applet"
                         label="1"
                         points="6"
                         status="correction"
                         score="0.4"
                         result="2.4"/&gt;
    &lt;mumie:problem_grade problem_id="412"
                         category="applet"
                         label="2"
                         points="6"
                         status="correction"
                         score="1.0"
                         result="6"/&gt;
    &lt;mumie:problem_grade problem_id="419"
                         category="applet"
                         label="3"
                         points="6"
                         status="void"/&gt;
    &lt;mumie:problem_grade problem_id="420"
                         category="applet"
                         label="4"
                         points="6"
                         status="answers"/&gt;
    &lt;mumie:total_grade points="24"
                       result="8.4"/&gt;
  &lt;/mumie:user_worksheet_grades&gt;</pre>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @author Fritz Lehmann-Grube
 *   <a href="mailto:lehmannf@math.tu-berlin.de">lehmannf@math.tu-berlin.de</a>
 * @version <code>$Id: UserWorksheetGrades.java,v 1.9 2008/03/05 12:39:38 grudzin Exp $</code>
 * 
 * @deprecated Use {@link WorksheetUserProblemGrades} instead.
 */

public interface UserWorksheetGrades
  extends XMLizable, Identifyable
{
  /**
   * Role as an Avalon service (<code>UserWorksheetGrades.class.getName()</code>).
   */

  public static final String ROLE = UserWorksheetGrades.class.getName();

  /**
   * Initializes this instance to represent the specified user/worksheet pair.
   */

  public void setup (int userId, int worksheetId);

  /**
   * Writes the grades to the specified content handler. If <code>ownDocument</code> is
   * <code>true</code>, the <code>startDocument</code> and <code>endDocument</code> methods
   * are called before resp. after the XML is created. If <code>ownDocument</code> is false,
   * they are suppressed.
   */

  public void toSAX (ContentHandler contentHandler, boolean ownDocument)
    throws SAXException;
}
