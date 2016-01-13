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

package net.mumie.cocoon.readers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import net.mumie.cocoon.documents.Document;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.MediaType;
import net.mumie.cocoon.notions.UseMode;
import net.mumie.cocoon.util.ParamUtil;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Response;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.reading.AbstractReader;
import org.apache.cocoon.reading.ServiceableReader;
import org.xml.sax.SAXException;

/**
 * <p>
 *   Simple reader that produces a text/plain page from a string. Recognizes the following
 *   parameters: 
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
 *       <td><code>string</code></td>
 *       <td>The string from which to read the text of the response</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr>
 *       <td><code>add-newline</code></td>
 *       <td>Whether a newline is appended to the text. Boolean.</td>
 *       <td>No. Default is <code>"yes"</code></td>
 *     </tr>
 *   </tbody>
 * </table>
 * <p>
 *   Example (sitemap excerpt):
 * </p>
 * <pre>
 *   &lt;map:readers&gt;
 *     &lt;!-- ... --&gt;
 *     &lt;map:reader name="string"
                      src="net.mumie.cocoon.readers.StringReader"/&gt;
 *   &lt;/map:readers&gt;
 *
 *   &lt;!-- ... --&gt;
 *
 *   &lt;map:pipelines&gt;
 *
 *     &lt;map:pipeline&gt;
 *       &lt;map:match pattern="hello-world"&gt;
 *         &lt;map:read type="string"&gt;
 *           &lt;map:parameter name="string" value="Hello World!"/&gt;
 *         &lt;/map:read&gt;
 *       &lt;/map:match&gt;
 *     &lt;/map:pipeline&gt;
 *
 *   &lt;/map:pipelines&gt;
 * </pre>
 * <p>
 *  Shows <code>"Hello World!"</code> in the browser.
 * 
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <span class="file">$Id: StringReader.java,v 1.8 2007/11/15 13:43:16 rassy Exp $</span>
 */

public class StringReader extends AbstractReader 
{
  /**
   * The newline string as a constant. This is read from the system property
   * <code>"line.separator"</code>.
   */

  protected static final byte[] NEWLINE = System.getProperty("line.separator").getBytes();

  /**
   * <p>
   *   Returns the mime type of the document ( <code>"text/plain"</code>). 
   * </p>
   * @return the mime type of the document. 
   */

  public String getMimeType ()
  {
    this.getLogger().debug("getMimeType 1/1");
    return "text/plain";
  }

  /**
   * Generates the response.
   */

  public void generate ()
    throws ProcessingException 
  {
    final String METHOD_NAME = "generate";
    this.getLogger().debug(METHOD_NAME + " 1/3: Started");

    try
      {
        // Get data from parameters:
        String string = ParamUtil.getAsString(this.parameters, "string");
        boolean addNewline = ParamUtil.getAsBoolean(this.parameters, "add-newline", true);

        this.getLogger().debug
          (METHOD_NAME + " 2/3:" +
           " string = " + string +
           ", addNewline = " + addNewline);

	byte[] content = string.getBytes();
	long contentLength = content.length;

	Response response = ObjectModelHelper.getResponse(this.objectModel);

	if ( contentLength != -1 )
	  response.setHeader("Content-Length", Long.toString(contentLength));

	this.out.write(content);
        if ( addNewline ) this.out.write(NEWLINE);
	this.out.flush();
      }
    catch (Exception exception)
      {
	throw new ProcessingException(exception);
      }
	
    this.getLogger().debug(METHOD_NAME + " 3/3: Done");
  }
}
