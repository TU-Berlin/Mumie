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

import net.mumie.cocoon.grade.WorksheetUserBulkCorrector;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.pseudodocs.SessionUser;
import net.mumie.cocoon.pseudodocs.User;
import net.mumie.cocoon.util.LogUtil;
import net.mumie.cocoon.util.ParamUtil;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.SourceResolver;
import org.xml.sax.SAXException;

/**
 * <p>
 *   Carries out a bulk correction for all problems of a certain worksheet and
 *   user. Recognizes the following parameters: 
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
 *       <td><code>worksheet</code></td>
 *       <td>The id of the worksheet</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr>
 *       <td><code>user</code></td>
 *       <td>The id of the user</td>
 *       <td>No. Defaults to the id of the user who owns the session.</td>
 *     </tr>
 *     <tr>
 *       <td><code>force</code></td>
 *       <td>Whether corrections are recreated even if they are up-to-date. Boolean</td>
 *       <td>No. Defaults to <code>"false"</code>.</td>
 *     </tr>
 *     <tr>
 *       <td><code>store-name</code></td>
 *       <td>The name of the {@link XMLElement#STORE STORE} element that wraps the
 *       correction protocol.</td>
 *       <td>No. Defaults to <code>"bulk-correction"</code>.</td>
 *     </tr>
 *   </tbody>
 * </table>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: WorksheetUserBulkCorrectionTransformer.java,v 1.1 2007/08/16 14:27:22 rassy Exp $</code>
 */

public class WorksheetUserBulkCorrectionTransformer extends AbstractAddDynamicDataTransformer
{
  /**
   * Does the bulk correction and writes a protocol to the SAX stream.
   */

  protected void dynamicDataElementContentToSAX ()
    throws SAXException
  {
    final String METHOD_NAME = "dynamicDataElementContentToSAX()";
    this.getLogger().debug(METHOD_NAME + " 1/3: Started");

    WorksheetUserBulkCorrector bulkCorrector = null;
    User user = null;    

    try
      {
        // Init bulk corrector:
        bulkCorrector =
          (WorksheetUserBulkCorrector)this.manager.lookup(WorksheetUserBulkCorrector.ROLE);

        // Get user id:
        int userId;
        final String USER = "user";
        if ( ParamUtil.checkIfSet(this.parameters, USER) )
          userId = ParamUtil.getAsId(this.parameters, USER);
        else
          {
            user = (User)this.manager.lookup(SessionUser.ROLE);
            userId = user.getId();
          }

        // Get worksheet id:
        int worksheetId = ParamUtil.getAsId(this.parameters, "worksheet");

        // Get "force" flag:
        boolean force = ParamUtil.getAsBoolean(this.parameters, "force", false);

        // Get store name:
        String storeName = ParamUtil.getAsString(parameters, "store-name", "bulk-correction");

        this.getLogger().debug
          (METHOD_NAME + " 2/3: " +
           " user = " + LogUtil.identify(user) +
           ", userId = " + userId +
           ", worksheetId = " + worksheetId +
           ", force = " + force +
           ", storeName = " + storeName);

        // Start STORE element:
        this.metaXMLElement.reset();
        this.metaXMLElement.setLocalName(XMLElement.STORE);
        this.metaXMLElement.addAttribute(XMLAttribute.NAME, storeName);
        this.metaXMLElement.startToSAX(this.contentHandler);

        // Correct and send protocol to SAX:
        bulkCorrector.bulkCorrect(worksheetId, userId, force, this.contentHandler, false);

        // Close STORE element:
        this.metaXMLElement.reset();
        this.metaXMLElement.setLocalName(XMLElement.STORE);
        this.metaXMLElement.endToSAX(this.contentHandler);

        this.getLogger().debug(METHOD_NAME + " 3/3: Done");
      }
    catch (Exception exception)
      {
        throw new SAXException(exception);
      }
    finally
      {
        if ( bulkCorrector != null )
          this.manager.release(bulkCorrector);
        if ( user != null )
          this.manager.release(user);
      }
  }

}
