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
import org.xml.sax.ContentHandler;

/**
 * Corrects all problems of a given worksheet for all users.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: WorksheetBulkCorrector.java,v 1.4 2007/11/13 12:43:57 rassy Exp $</code>
 */

public interface WorksheetBulkCorrector
  extends BulkCorrector
{
  /**
   * Role as an Avalon service (<code>WorksheetBulkCorrector.class.getName()</code>).
   */

  public static String ROLE = WorksheetBulkCorrector.class.getName();

  /**
   * Carries out the bulk correction for the specified worksheet. If <code>force</code> is
   * true, problems are corrected even if the correction exists and is up-to-date. A
   * protocol of the correction is written in XML form to the specified content handler. If
   * <code>ownDocument</code> is false, the content handlers <code>startDocument</code> and
   * <code>endDocument</code> methods are not called before resp. after the XML is written.
   */

  public void bulkCorrect (int worksheetId,
                           boolean force,
                           ContentHandler contentHandler,
                           boolean ownDocument)
    throws BulkCorrectionException;
}