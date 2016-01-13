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
 * <p>
 *   Corrects a large number of problems. This is only a marker interface; it does not
 *   define any methods. Nevertheless, bulk correction follows a common pattern:
 * </p>
 * <p>
 *   First, a list of problem/user pairs is requested from the database. The corresponding
 *   result set must contain the following colums:
 *   <ol>
 *     <li>{@link DbColumn#PROBLEM_REF_ID PROBLEM_REF_ID} (id of the
 *       worksheet/generic_probelm reference)</li>
 *     <li>{@link DbColumn#USER_ID USER_ID}</li>
 *     <li>{@link DbColumn#PROBLEM_ID PROBLEM_ID} (id of the generic problem)</li>
 *   </ol>
 *   Then, it is iterated through the result set, and for each problem/user pair the
 *   correction is made. A protocol of the corrections is written in XML form to a content
 *   handler. The protocol lists the processed problem/user pairs and the status of each
 *   correction. The status may be <code>ok</code> or <code>failed</code>. In the latter
 *   case, the throwable that caused the correction to fail is included in the
 *   protocol. Here is an example of the XML:
 *   <pre>
 *   &lt;mumie:correction_reports&gt;
 *     &lt;mumie:correction_report problem_id="23" user_id="67" ref_id="316" status="ok"/&gt;
 *     &lt;mumie:correction_report problem_id="26" user_id="67" ref_id="323" status="ok"/&gt;
 *     &lt;mumie:correction_report problem_id="27" user_id="67" ref_id="329" status="failed"&gt;
 *       &lt;mumie:error&gt;
 *         &lt;!-- Throwable that caused the error --&gt;
 *       &lt;/mumie:error&gt;
 *     &lt;/mumie:correction_report&gt;
 *   &lt;/mumie:correction_reports&gt;</pre>
 *   The root elements may contain attributes specifying the realm of the bulk correction
 *   (e.g., the tutorial and worksheet from which the problem/user pairs are taken).
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: BulkCorrector.java,v 1.1 2007/11/14 12:20:07 rassy Exp $</code>
 */

public interface BulkCorrector
  extends Identifyable
{
}