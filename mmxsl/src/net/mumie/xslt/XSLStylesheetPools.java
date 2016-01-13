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

package net.mumie.xslt;

import java.net.URI;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import org.xml.sax.SAXException;
import net.mumie.util.ResourcePool;
import net.mumie.util.ResourcePools;
import net.mumie.util.ResourcePoolException;

/**
 *
 * @author Tilman Rassy
 *
 * @version <span class="file">$Id: XSLStylesheetPools.java,v 1.5 2004/03/30 19:16:11 rassy Exp $</span>
 */

public class XSLStylesheetPools extends ResourcePools
{
  /**
   * The {@link javax.xml.transform.TransformerFactory TransformerFactory} that creates the
   * stylesheets of this pools.
   */

  protected TransformerFactory transformerFactory;

  /**
   * the {@link XSLStylesheetDependencyHelper XSLStylesheetDependencyHelper} used by this
   * pools.
   */

  protected XSLStylesheetDependencyHelper dependencyHelper;

  /**
   * Returns a newly created pool for key <code>key</code>.
   */

  protected ResourcePool newResourcePool (Object key)
    throws ResourcePoolException
  {
    try
      {
	return new XSLStylesheetPool(this.transformerFactory, this.dependencyHelper, (URI)key);
      }
    catch (Exception exception)
      {
	throw new ResourcePoolException(exception);
      }
  }

  /**
   * Returns the key for <code>resource</code>
   */

  protected Object getKeyOf (Object resource)
    throws ResourcePoolException
  {
    try
      {
	return ((PoolableXSLStylesheet)resource).getURI();
      }
    catch (Exception exception)
      {
	throw new ResourcePoolException(exception);
      }
  }

  /**
   * Creates a new <code>XSLStylesheetPools</code> object that uses
   * <code>transformerFactory</code> to create new stylesheets.
   */

  public XSLStylesheetPools (TransformerFactory transformerFactory,
			     XSLStylesheetDependencyHelper dependencyHelper)
    throws SAXException
  {
    final String METHOD_NAME = "<constructor>(TransformerFactory transformerFactory, " +
                               "XSLStylesheetDependencyHelper dependencyHelper)";
    this.classNameForLogging = "net.mumie.xslt.XSLStylesheetPools";
    this.logger = Logger.getLogger("net.mumie.xslt.XSLStylesheetPools");
    this.log(METHOD_NAME, "1/1: transformerFactory = " + transformerFactory +
	     ", dependencyHelper = " + dependencyHelper);
    this.dependencyHelper = dependencyHelper;
    this.transformerFactory = transformerFactory;
  }

  /**
   * Creates a new <code>XSLStylesheetPools</code>.
   */

  public XSLStylesheetPools ()
    throws TransformerFactoryConfigurationError, ParserConfigurationException, SAXException
  {
    this(TransformerFactory.newInstance(), new XSLStylesheetDependencyHelper());
  }
}
