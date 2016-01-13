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

package net.mumie.cocoon.msg;

import java.util.Properties;
import javax.xml.transform.OutputKeys;
import net.mumie.cocoon.httpclient.SimpleHttpClient;
import net.mumie.cocoon.service.AbstractJapsServiceable;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.xml.XMLUtils;
import org.apache.commons.httpclient.DefaultMethodRetryHandler;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.excalibur.xml.sax.XMLizable;

public abstract class AbstractPostMessage extends AbstractJapsServiceable
  implements Message
{
  // --------------------------------------------------------------------------------
  // h1: Global constants and variables
  // --------------------------------------------------------------------------------

  /**
   * The destination name
   */

  protected String destinationName;

  // --------------------------------------------------------------------------------
  // h1: Lifecycle related methods
  // --------------------------------------------------------------------------------

  /**
   * Clears the destination name.
   */
  
  protected void reset ()
  {
    this.destinationName = null;
  }

  // --------------------------------------------------------------------------------
  // h1: Sending
  // --------------------------------------------------------------------------------

  /**
   * Sends this message.
   */

  public boolean send ()
  {
    final String METHOD_NAME = "send";
    this.logDebug(METHOD_NAME + " 1/3: Started");
    boolean success = true;

    MessageDestinationTable destinationTable = null;
    SimpleHttpClient httpClient = null;

    try
      {
        // Init services:
        destinationTable =
          (MessageDestinationTable)this.serviceManager.lookup(MessageDestinationTable.ROLE);
        httpClient =
          (SimpleHttpClient)this.serviceManager.lookup(SimpleHttpClient.ROLE);

        // Get destination:
        MessageDestination destination = destinationTable.getDestination(this.destinationName);

        // Create and setup a Http Post method object:
        PostMethod method = new PostMethod(destination.getURL());
        DefaultMethodRetryHandler retryHandler = new DefaultMethodRetryHandler();
        retryHandler.setRequestSentRetryEnabled(false);
        retryHandler.setRetryCount(3);
        method.setMethodRetryHandler(retryHandler);

        // Set login name and password:
        method.addParameter("name", destination.getLoginName());
        method.addParameter("password", destination.getPassword());

        // Specific setup:
        this.init(method, destination);

        // Execute method:
        try
          {
            if ( httpClient.executeMethod(method) != HttpStatus.SC_OK )
              {
                this.logError
                  (METHOD_NAME + ": Failed to send message: " + method.getStatusLine());
                success = false;
              }
            String response = new String(method.getResponseBody());
            if ( response.trim().startsWith("ERROR") )
              {
                this.logError
                  (METHOD_NAME + ": Failed to send message: " + response);
                success = false;
              }
            this.logDebug(METHOD_NAME + " 2/3: response = " + response);
          }
        finally
          {
            method.releaseConnection();
          }
      }
    catch (Exception exception)
      {
        this.logError(METHOD_NAME, exception);
        success = false;
      }
    finally
      {
        if ( httpClient != null )
          this.serviceManager.release(httpClient);
        if ( destinationTable != null )
          this.serviceManager.release(destinationTable);
      }

    this.logDebug(METHOD_NAME + " 3/3: Done. success = " + success);
    return success;
  }

  /**
   * Called by {@link #send send} immediately before the message is sent. This implementation
   * does nothing. Extending classes may overwrite this method.
   */

  protected void init (PostMethod method, MessageDestination destination)
    throws Exception
  {
    // Does nothing.
  }

  // --------------------------------------------------------------------------------
  // h1: Utilities
  // --------------------------------------------------------------------------------

  /**
   * Returns the XML code of a {@link XMLizable XMLizable} object as a string.
   */

  protected String getXMLCode (XMLizable xmlizable)
    throws ProcessingException
  {
    Properties outputProperties = new Properties();
    outputProperties.setProperty(OutputKeys.ENCODING, "ASCII");
    return XMLUtils.serialize(xmlizable, outputProperties);
  }
}
