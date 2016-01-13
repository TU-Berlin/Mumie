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

package net.mumie.cocoon.httpclient;

import java.io.IOException;
import net.mumie.cocoon.service.AbstractJapsService;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;

/**
 * Default implementation of {@link SimpleHttpClient SimpleHttpClient}. This implementation
 * wrappes a thread-safe {@link HttpClient HttpClient} instance.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: SimpleHttpClientImpl.java,v 1.2 2008/10/08 11:19:40 rassy Exp $</code>
 */

public class SimpleHttpClientImpl extends AbstractJapsService
  implements SimpleHttpClient, ThreadSafe
{
  // --------------------------------------------------------------------------------
  // h1: Global constants and variables
  // --------------------------------------------------------------------------------

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(SimpleHttpClientImpl.class);

  /**
   * The wrapped {@link HttpClient HttpClient} instance.
   */

  protected final HttpClient httpClient;

  // --------------------------------------------------------------------------------
  // h1: Lifecycle related methods
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>SimpleHttpClientImpl</code>.
   */

  public SimpleHttpClientImpl ()
  {
    MultiThreadedHttpConnectionManager connectionManager =
      new MultiThreadedHttpConnectionManager();
    this.httpClient = new HttpClient(connectionManager);
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  // --------------------------------------------------------------------------------
  // h1: Executing methods
  // --------------------------------------------------------------------------------

  /**
   * Executes the specified method.
   */

  public int executeMethod (HttpMethod method)
    throws IOException, HttpException
  {
    final String METHOD_NAME = "executeMethod";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    int responseCode = this.httpClient.executeMethod(method);
    this.logDebug(METHOD_NAME + " 2/2: Done. responseCode = " + responseCode);
    return responseCode;
  }

}