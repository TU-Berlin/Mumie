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

package net.mumie.cocoon.generators;

import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.xml.MetaXMLElement;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.generation.AbstractGenerator;

public class VoidUserGenerator extends AbstractJapsGenerator 
{
  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(VoidUserGenerator.class);

  /**
   * Helper to create XML elements
   */

  protected MetaXMLElement xmlElement = new MetaXMLElement();

  /**
   * Creates a new <code>VoidUserGenerator</code> instance.
   */

  public VoidUserGenerator ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Recycles this instance. Essentially, this method only calls the superclass recycle
   * method. Besides this, {@link #xmlElement} is reset, the instance status is notified
   * about the recycle, and log messages are written.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.xmlElement.reset();
    super.recycle();
    this.instanceStatus.notifyRecycle();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Generates the XML. See class documentation for details.
   */

  public void generate ()
    throws ProcessingException 
  {
    final String METHOD_NAME = "generate";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");
    try
      {
	contentHandler.startDocument();
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.USER);
        this.xmlElement.toSAX(contentHandler);
        this.getLogger().debug(METHOD_NAME + " 2/2: Done");
	contentHandler.endDocument();
      }
    catch (Exception exception)
      {
        throw new ProcessingException(exception);
      }
  }
}