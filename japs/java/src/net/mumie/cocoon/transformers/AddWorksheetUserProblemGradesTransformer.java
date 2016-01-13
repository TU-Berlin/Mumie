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

package net.mumie.cocoon.transformers;

import net.mumie.cocoon.grade.WorksheetUserProblemGrades;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.notions.XMLNamespace;
import net.mumie.cocoon.util.ParamUtil;
import net.mumie.cocoon.xml.MetaXMLElement;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.transformation.ServiceableTransformer;
import org.xml.sax.SAXException;

/**
 * <p>
 *   Adds the grades a certain user achieved in the problems of a certain worksheet to the
 *   dynamic data section. Recognizes the following parameters:
 * </p>
 * <table class="genuine indented" style="width:60em">
 *   <thead>
 *     <tr>
 *       <td>Name</td>
 *       <td>Description</td>
 *       <td>Required</td>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td><code>user</code></td>
 *       <td>Id of the user</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr>
 *       <td><code>worksheet</code></td>
 *       <td>Id the worksheet</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr>
 *       <td><code>store-name</code></td>
 *       <td>Name of the <code>"store"</code> XML element</td>
 *       <td>No. Default is <code>"user-worksheet-grades"</code>.</td>
 *     </tr>
 *   </tbody>
 * </table>
 * <p>
 *   This transformer relies on {@link WorksheetUserProblemGrades WorksheetUserProblemGrades}.
 * </p>
 *
 * @see WorksheetUserProblemGrades WorksheetUserProblemGrades
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: AddWorksheetUserProblemGradesTransformer.java,v 1.1 2008/02/26 12:06:04 rassy Exp $</code>
 */

public class AddWorksheetUserProblemGradesTransformer extends AbstractAddDynamicDataTransformer
{
  /**
   * Sends the content of the dynamic data element to SAX.
   */

  protected void dynamicDataElementContentToSAX ()
    throws SAXException
  {
    final String METHOD_NAME = "dynamicDataElementContentToSAX()";
    this.getLogger().debug(METHOD_NAME + " 1/2");

    WorksheetUserProblemGrades grades = null;

    try
      {
        int userId = ParamUtil.getAsId(this.parameters, "user");
        int worksheetId = ParamUtil.getAsId(this.parameters, "worksheet");
        String storeName = ParamUtil.getAsString
          (this.parameters,"store-name", "user-worksheet-grades");

        // Initialize grades:
        grades = (WorksheetUserProblemGrades)this.manager.lookup(WorksheetUserProblemGrades.ROLE);
        grades.setup(userId, worksheetId);

        // Start STORE element:
        this.metaXMLElement.reset();
        this.metaXMLElement.setLocalName(XMLElement.STORE);
        this.metaXMLElement.addAttribute(XMLAttribute.NAME, storeName);
        this.metaXMLElement.startToSAX(this.contentHandler);

        // Send grades to SAX:
        grades.toSAX(this.contentHandler, false);

        // Close STORE element:
        this.metaXMLElement.reset();
        this.metaXMLElement.setLocalName(XMLElement.STORE);
        this.metaXMLElement.endToSAX(this.contentHandler);

        this.getLogger().debug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
        throw new SAXException(exception);
      }
    finally
      {
        if ( grades != null )
          this.manager.release(grades);
      }
  }
}
