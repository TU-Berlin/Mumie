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
import net.mumie.cocoon.session.SessionList;
import net.mumie.cocoon.util.LogUtil;
import net.mumie.cocoon.util.ParamUtil;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Response;
import org.apache.cocoon.environment.SourceResolver;

/**
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: SessionCountReader.java,v 1.1 2007/10/11 11:27:48 rassy Exp $</code>
 */

public class SessionCountReader extends AbstractJapsReader
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(SessionCountReader.class);

  // --------------------------------------------------------------------------------
  // Lifecycle related methods
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>SessionCountReader</code> instance.
   */

  public SessionCountReader ()
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
        byte[] content = Integer.toString(SessionList.getSize()).getBytes();
	Response response = ObjectModelHelper.getResponse(this.objectModel);
        response.setHeader(ResponseHeader.CONTENT_LENGTH, Integer.toString(content.length));
        this.out.write(content);
        this.out.flush();
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
   * Whether this <code>SessionCountReader</code> wants to set the content length or not. Always
   * returns <code>false</code>.
   */

  public boolean shouldSetContentLength()
  {
    return false;
  }
}