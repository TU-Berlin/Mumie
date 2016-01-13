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

import java.sql.SQLException;
import net.mumie.cocoon.util.Identifyable;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.Request;
import org.apache.excalibur.xml.sax.XMLizable;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @author Fritz Lehmann-Grube
 *   <a href="mailto:lehmannf@math.tu-berlin.de">lehmannf@math.tu-berlin.de</a>
 * @version <code>$Id: TutorialUserTraditionalProblemGrades.java,v 1.1 2008/04/28 12:47:23 rassy Exp $</code>
 * 
 */

public interface TutorialUserTraditionalProblemGrades
  extends XMLizable, Identifyable
{
  /**
   * Role as an Avalon service (<code>TutorialUserTraditionalProblemGrades.class.getName()</code>).
   */

  public static final String ROLE = TutorialUserTraditionalProblemGrades.class.getName();

  /**
   * Loads the data for the specified tutorial and course
   */

  public void load (int tutorialId, int courseId)
    throws ProcessingException, ServiceException, SQLException;

  /**
   * Adds the form data found in the specified request to the grades.
   */

  public void addFormData (Request request)
    throws ProcessingException;

  /**
   * Stores the grades in the database.
   */

  public void store ()
    throws ServiceException, SQLException;

  /**
   * Writes the grades to the specified content handler. If <code>ownDocument</code> is
   * <code>true</code>, the <code>startDocument</code> and <code>endDocument</code> methods
   * are called before resp. after the XML is created. If <code>ownDocument</code> is false,
   * they are suppressed.
   */

  public void toSAX (ContentHandler contentHandler, boolean ownDocument)
    throws SAXException;
}
