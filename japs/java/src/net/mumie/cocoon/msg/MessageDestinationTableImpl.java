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

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import net.mumie.cocoon.service.AbstractJapsServiceable;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.ProcessingException;
import org.apache.excalibur.xml.sax.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Default implementation of {@link MessageDestinationTable MessageDestinationTable}.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: MessageDestinationTableImpl.java,v 1.4 2009/10/02 22:24:18 rassy Exp $</code>
 */

public class MessageDestinationTableImpl extends AbstractJapsServiceable
  implements MessageDestinationTable, ThreadSafe, Configurable
{
  // --------------------------------------------------------------------------------
  // h1: Inner class: SAX handler for reading the config file
  // --------------------------------------------------------------------------------

  /**
   * A SAX content handler for parsing the config file.
   */

  public static final class SAXHandler extends DefaultHandler
  {
    /**
     * The result of the parsing. Maps destination names to
     * {@link MessageDestination MessageDestination} objects.
     */

    protected Map<String,MessageDestination> destinations = null;

    /**
     * Handles the start of the document.
     */

    public void startDocument ()
      throws SAXException
    {
      this.destinations = new HashMap<String,MessageDestination>();
    }

    /**
     * Handles the start of an element.
     */

    public void startElement (String namespaceURI,
                              String localName,
                              String qualifiedName,
                              Attributes attributes)
      throws SAXException
    {
      if ( localName.equals("destination") )
        {
          String name = attributes.getValue("name");
          String url = attributes.getValue("url");
          String loginName = attributes.getValue("login");
          String password = attributes.getValue("password");

          if ( name == null )
            throw new SAXException("Missing destination name");

          if ( url == null )
            throw new SAXException("Missing URL for destination \"" + name + "\"");

          if ( this.destinations.containsKey(name) )
            throw new SAXException("Destination \"" + name + "\" already defined");
          
          this.destinations.put
            (name, new MessageDestination(url, loginName, password));
        }
    }

    /**
     * Clears the parse result.
     */

    public void clear ()
    {
      this.destinations = null;
    }

    /**
     * Returns the parse result.
     */

    public Map<String,MessageDestination> getDestinations ()
    {
      return this.destinations;
    }
  }

  // --------------------------------------------------------------------------------
  // h1: Global constants and variables
  // --------------------------------------------------------------------------------

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus =
    new ServiceStatus(MessageDestinationTableImpl.class);

  /**
   * The config file (XML file containing the destinations).
   */

  protected File file = null;

  /**
   * Last time when the config file was read.
   */

  protected long timestamp = -1;

  /**
   * Maps destination names to {@link MessageDestination MessageDestination} objects.
   */

  protected Map<String,MessageDestination> destinations = null;

  /**
   * SAX handler to parse the config file.
   */

  protected SAXHandler saxHandler = new SAXHandler ();

  // --------------------------------------------------------------------------------
  // h1: Lifecycle related methods
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>MessageDestinationTableImpl</code>.
   */

  public MessageDestinationTableImpl ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Configures this instance. See class decription for details.
   */

  public void configure (Configuration configuration)
    throws ConfigurationException
  {
    final String METHOD_NAME = "configure";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    try
      {
        String filename = configuration.getChild("filename").getValue().trim();
        this.file = new File(filename);
        this.logDebug(METHOD_NAME + " 2/2: Done. filename = " + filename);
      }
    catch (Exception exception)
      {
        throw new ConfigurationException("Wrapped exception", exception);
      }
  }

  // --------------------------------------------------------------------------------
  // h1: Reading the config file
  // --------------------------------------------------------------------------------

  /**
   * Reads the config file.
   */

  protected void readConfigFile ()
    throws ProcessingException 
  {
    final String METHOD_NAME = "readConfigFile";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    SAXParser parser = null;
    try
      {
        // Init parser:
        parser = (SAXParser)this.serviceManager.lookup(SAXParser.ROLE);
        parser.parse(new InputSource(new FileReader(this.file)), this.saxHandler);
        this.timestamp = System.currentTimeMillis();
        this.destinations = this.saxHandler.getDestinations();
        this.saxHandler.clear();
        this.logDebug(METHOD_NAME + " 2/2: Started");
      }
    catch (Exception exception)
      {
        throw new ProcessingException(this.getIdentification(), exception);
      }
    finally
      {
        if (parser != null )
          this.serviceManager.release(parser);
      }
  }

  // --------------------------------------------------------------------------------
  // h1: Accessing the destinations
  // --------------------------------------------------------------------------------

  /**
   * Returns the destination for the specified name.
   */

  public MessageDestination getDestination (String name)
    throws Exception
  {
    if ( this.destinations == null || this.timestamp < this.file.lastModified() )
      this.readConfigFile();

    if ( !this.destinations.containsKey(name) )
      throw new IllegalArgumentException("Unknown message destination name: \"" + name + "\"");

    return this.destinations.get(name);
  }
}
