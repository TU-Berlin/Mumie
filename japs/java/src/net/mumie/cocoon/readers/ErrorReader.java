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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import net.mumie.cocoon.notions.ResponseHeader;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.util.LogUtil;
import net.mumie.cocoon.util.ParamUtil;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Response;
import org.apache.cocoon.environment.SourceResolver;

/**
 * <p>
 *   Creates an error message in plain text format. The message is taken from the Throwable
 *   stored in the object model with the key
 *   {@link ObjectModelHelper#THROWABLE_OBJECT ObjectModelHelper.THROWABLE_OBJECT}. The
 *   message has the form
 *   <pre>
 *   ERROR
 *
 *     <var>description</var>
 *
 *   <var>stacktrace</var></pre>
 *
 *   where <var>description</var> and <var>stacktrace</var> are the description and the stack
 *   trace of the Throwable. To get the description, the Throwable is first "unwrapped",
 *   which means that the {@link Throwable#getCause getCause} reference is followed as far as
 *   possible, and then {@link Throwable#getMessage getMessage} is called from the last
 *   Throwable object obtained this way. 
 * </p>
 * <p>
 *   Two non-standard response headers are set by the reader: the first is
 *   {@link ResponseHeader#STATUS ResponseHeader.STATUS} and contains the string
 *   <code>"ERROR"</code>, the second is
 *   {@link ResponseHeader#STATUS ResponseHeader.ERROR}
 *   and contains <var>description</var>.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: ErrorReader.java,v 1.3 2007/07/11 15:38:48 grudzin Exp $</code>
 */

public class ErrorReader extends AbstractJapsReader
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(ErrorReader.class);

  // --------------------------------------------------------------------------------
  // Lifecycle related methods
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>ErrorReader</code> instance.
   */

  public ErrorReader ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Recycles this instance. Actually, does nothing but calling the superclass recycle
   * method and notifying the instance status object about the recycling.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    super.recycle();
    this.instanceStatus.notifyRecycle();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // Generate method
  // --------------------------------------------------------------------------------

  /**
   * Generates the response.
   */

  public void generate ()
    throws ProcessingException 
  {
    final String METHOD_NAME = "generate";
    this.logDebug(METHOD_NAME + "1/2: Started");

    try
      {
        // Get the Throwable describing the error:
        Throwable throwable =
          (Throwable)this.objectModel.get(ObjectModelHelper.THROWABLE_OBJECT);

        // Write the throwable to the logs:
        if ( throwable != null )
          this.logError("Reporting error", throwable);

        // Get a description of the error:
        String description =
          (throwable != null
           ? LogUtil.unwrapThrowable(throwable).getMessage()
           : "Unknown error (no throwable found)");

        // Get a StringWriter to compose the error message:
        StringWriter stringWriter = new StringWriter();

        // Wrap the StringWriter by a PrintWriter for convienience:
        PrintWriter writer = new PrintWriter(stringWriter);

        // Compose the message:
        writer.println("ERROR");
        writer.println();
        writer.println("  " + description);
        writer.println();
        if ( throwable != null )
          throwable.printStackTrace(writer);
        writer.flush();

        // Convert the message to a byte array:
        byte[] content = stringWriter.toString().getBytes();

        // Setup response object:
	Response response = ObjectModelHelper.getResponse(this.objectModel);
        response.setHeader(ResponseHeader.CONTENT_LENGTH, Integer.toString(content.length));
        response.setHeader(ResponseHeader.STATUS, "ERROR");
        response.setHeader(ResponseHeader.ERROR, description);

        // Write the message to the output:
        this.out.write(content);
      }
    catch (Exception exception)
      {
	throw new ProcessingException(exception);
      }
	
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Returns the mime type of the document (<code>"text/plain"</code>). 
   *
   * @return the mime type of the document. 
   */

  public String getMimeType ()
  {
    return "text/plain";
  }
  
  /**
   * Whether this <code>DocumentReader</code> wants to set the content length or not. Always
   * returns <code>false</code>.
   */

  public boolean shouldSetContentLength()
  {
    return false;
  }
}