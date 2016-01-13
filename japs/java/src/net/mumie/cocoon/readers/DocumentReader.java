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
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceSelectorWrapper;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.util.LogUtil;
import net.mumie.cocoon.util.ParamUtil;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Response;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.reading.ServiceableReader;
import org.xml.sax.SAXException;

/**
 * <p>
 *   Reader for a Mumie document in use mode "serve". Expects two parameters:
 * </p>
 * <table class="genuine indented">
 *   <thead>
 *     <tr>
 *       <td>Name</td>
 *       <td colspan="2">Description</td>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td><code>type</code></td>
 *       <td>The type of the document (numerical code)</td>
 *       <td rowspan="2" style="vertical-align:middle">Only one of the both</td>
 *     </tr>
 *     <tr>
 *       <td><code>type-name</code></td>
 *       <td>The type of the document (string name)</td>
 *     </tr>
 *     <tr>
 *       <td><code>id</code></td>
 *       <td>The id of the document</td>
 *     </tr>
 *   </tbody>
 * </table>
 * <p>
 *   Example (sitemap excerpt):
 * </p>
 * <pre>
 *   &lt;map:readers&gt;
 *     &lt;!-- ... --&gt;
 *     &lt;map:reader name="document"
 *                    src="net.mumie.cocoon.readers.DocumentReader"/&gt;
 *   &lt;/map:readers&gt;
 *
 *   &lt;!-- ... --&gt;
 *
 *   &lt;map:pipelines&gt;
 *
 *     &lt;map:pipeline&gt;
 *       &lt;map:match pattern="document/&#42;/&#42;"&gt;
 *         &lt;map:read type="document"&gt;
 *           &lt;map:parameter name="type" value="{1}"/&gt;
 *           &lt;map:parameter name="id" value="{2}"/&gt;
 *         &lt;/map:read&gt;
 *       &lt;/map:match&gt;
 *     &lt;/map:pipeline&gt;
 *
 *   &lt;/map:pipelines&gt;
 * </pre>
 * <p>
 *   Thus, a request with the URL <span class="string">"$PREFIX/document/3/67"</span> would
 *   return the document of type 3 with id 67, where <span class="string">"$PREFIX"</span>
 *   stands for the Cocoon root URL.
 * </p>
 * <p>
 *   For document types, see {@link net.mumie.cocoon.notions.DocType DocType}.
 *   For use modes, see {@link net.mumie.cocoon.notions.UseMode UseMode}.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: DocumentReader.java,v 1.19 2007/10/31 17:18:28 rassy Exp $</code>
 */

public class DocumentReader extends ServiceableJapsReader 
{
  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(DocumentReader.class);

  /**
   * The document to serve to the client.
   */

  protected Document document = null;

  /**
   * The service selector used to select documents
   */

  ServiceSelectorWrapper documentSelector = null;

  /**
   * Creates a new <code>DocumentReader</code> instance.
   */

  public DocumentReader ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Sets up this <code>DocumentReader</code>. Calls the
   * <code>setup</code> method of the super class, then
   * initializes the document.
   */

  public void setup (SourceResolver sourceResolver, Map objectModel, String source,
		     Parameters parameters)
    throws ProcessingException, SAXException, IOException
  {
    final String METHOD_NAME = "setup";
    this.logDebug(METHOD_NAME + " 1/3: started");

    try
      {
        // Ensure services are released:
        this.releaseServices();

        super.setup(sourceResolver, objectModel, source, parameters);
    
	// Document type:
        int type = ParamUtil.getAsDocType(this.parameters, "type", "type-name");

	// Document id:
        int id = ParamUtil.getAsId(this.parameters, "id");

	this.logDebug(METHOD_NAME + " 2/3: type = " + type + ", id = " + id);

        // Get a document selector wrapper if necessary:
        if ( this.documentSelector == null )
          {
            this.logDebug(METHOD_NAME + " 2/3.1: Instanciating a ServiceSelectorWrapper");
            String label = "DocumentReader#" + this.instanceStatus.getInstanceId();
            this.documentSelector = new ServiceSelectorWrapper
              (label, this.getLogger().getChildLogger(label));
          }

        // Get a document selector and wrap it:
	this.documentSelector.wrap
	  ((ServiceSelector)this.serviceManager.lookup(Document.ROLE + "Selector"));

	// Get a document from the pool:
	this.document = (Document)this.documentSelector.select(DocType.hintFor[type]);

        // Set id and use mode:
        this.document.setId(id);
	this.document.setUseMode(UseMode.SERVE);

	this.logDebug
          (METHOD_NAME + " 4/5: Done. this.document = " + LogUtil.identify(this.document));
      }
    catch (Exception exception)
      {
	throw new ProcessingException(exception);
      }

    this.logDebug(METHOD_NAME + " 5/5: finished");
  }

  /**
   * <p>
   *   Returns the mime type of the document.
   * </p>
   * <p>
   *   It is also possible that the method returns <code>null</code>, meaning the
   *   mime type cannot be determined. 
   * </p>
   * 
   * @return the mime type of the document, or <code>null</code> if the mime type cannot be
   *         determined. 
   */

  public String getMimeType ()
  {
    final String METHOD_NAME = "getMimeType";
    this.logDebug(METHOD_NAME + "1/2: Started");

    String mimeType = null;
    try
      {
	int contentType = this.document.getContentType();
	if ( contentType >= 0 ) mimeType = MediaType.nameFor[contentType];
      }
    catch (Exception exception)
      {
	this.logWarn(METHOD_NAME + "Failed to retrieve mime type", exception);
      }

    this.logDebug(METHOD_NAME + " 2/2: Done. mimeType = " + mimeType);
    return mimeType;
  }

  /**
   * <p>
   *   Returns the time when the document was last modified. The value is the number of
   *   milliseconds since January 1, 1970, 00:00:00 GMT (see method 
   *   {@link java.util.Date#getTime getTime} in class {@link java.util.Date}, which this
   *   method uses to optain the value).
   * </p>
   * <p>
   *   It is also possible that the method returns <code>0</code>, meaning the last
   *   modification time cannot be determined.
   * </p>
   * 
   * @return the last modification time of the document, or <code>0</code> if that time
   *         cannot be determined.
   */

  public long getLastModified()
  {
    final String METHOD_NAME = "getLastModified";
    this.logDebug(METHOD_NAME + " 1/2: Started");

    long time = 0;
    try
      {
	Date date = this.document.getLastModified();
	if ( date != null ) time = date.getTime();
      }
    catch (Exception exception)
      {
	this.logWarn(METHOD_NAME + ": Failed to retrieve last modification time", exception);
      }
    this.logDebug(METHOD_NAME + " 2/2: Done. time = " + time);
    return time;
  }
  
  /**
   * Whether this <code>DocumentReader</code> wants to set the content length or not. Always
   * returns <code>false</code>.
   */

  public boolean shouldSetContentLength()
  {
    return false;
  }

  /**
   * Generates the response.
   */

  public void generate ()
    throws ProcessingException 
  {
    final String METHOD_NAME = "generate";
    this.logDebug(METHOD_NAME + "1/3: Started");

    try
      {
	Response response = ObjectModelHelper.getResponse(this.objectModel);
	long contentLength = this.document.getContentLength();
	
	this.logDebug("generate 2/3: contentLength = " + contentLength);
	
	if ( contentLength != -1 )
	  response.setHeader("Content-Length", Long.toString(contentLength));
	
	this.document.toStream(this.out);
	this.out.flush();
      }
    catch (Exception exception)
      {
	throw new ProcessingException(exception);
      }
	
    this.logDebug(METHOD_NAME + " 3/3: Done");
  }

  /**
   * Releases the global services.
   */

  protected void releaseServices ()
  {
    try
      {
        if ( this.documentSelector != null && this.documentSelector.hasServiceSelector() )
          {
            if ( this.document != null )
              {
                this.documentSelector.release(this.document);
                this.document = null;
              }
            this.serviceManager.release(this.documentSelector.unwrapServiceSelector());
            // this.documentSelector = null;  Should be unnecessary because the wrapped
            //                                service selector is set to null
          }
        else if ( this.document != null )
          {
            throw new IllegalStateException
              ("Document exists but document selector is null");
          }
      }
    catch (Exception exception)
      {
	this.logWarn("releaseServices: Caught exception: " + exception);
      }
  }

  /**
   * Recycles this <code>DocumentReader</code>.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.releaseServices();
    super.recycle();
    this.instanceStatus.notifyRecycle();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Disposes this <code>DocumentReader</code>.
   */

  public void dispose ()
  {
    final String METHOD_NAME = "dispose";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.releaseServices();
    super.dispose();
    this.instanceStatus.notifyDispose();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }
}
