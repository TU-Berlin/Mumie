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
import java.util.logging.Level;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;
import net.mumie.util.ResourcePool;
import net.mumie.util.ResourcePoolException;
import net.mumie.util.ResourceDependencyUpdateException;

/**
 *
 * @author Tilman Rassy
 *
 * @version <span class="file">$Id: XSLStylesheetPool.java,v 1.5 2004/03/30 19:16:11 rassy Exp $</span>
 */

public class XSLStylesheetPool extends ResourcePool
{
  /**
   * The source of the pooled stylesheet.
   */

  protected URI uri;

  /**
   * The {@link javax.xml.transform.TransformerFactory TransformerFactory} that creates the
   * stylesheets of this pool.
   */

  protected TransformerFactory transformerFactory;

  /**
   * the {@link XSLStylesheetDependencyHelper XSLStylesheetDependencyHelper} used by this
   * pool.
   */

  protected XSLStylesheetDependencyHelper dependencyHelper;

  /**
   * Get method for {@link #uri uri}.
   */

  public URI getURI ()
  {
    return this.uri;
  }

  /**
   * Returns the creation time of <code>resource</code>.
   */

  protected long getCreationTimeOf (Object resource)
  {
    return ((PoolableXSLStylesheet)resource).getCreationTime();
  }

  /**
   * Returns the last modification time of the stylesheet source.
   */

  protected long getLastModificationTimeOfSource ()
    throws ResourcePoolException
  {
    try
      {
	return this.dependencyHelper.getAbsoluteLastModified(this.uri);
      }
    catch (Exception exception)
      {
	throw new ResourcePoolException(exception);
      }
  }

  /**
   * Called before a resource is handed over to the user.
   */

  protected void endGetHook (Object resource)
    throws ResourcePoolException
  {
    this.log("endGetHook(java.lang.Object)", "1/1: resource = " + resource);
  }

  /**
   * Called before a resource is returned to the pool.
   */

  protected void beginPutHook (Object resource)
    throws ResourcePoolException
  {
    final String METHOD_NAME = "beginPutHook(java.lang.Object)";
    this.log(METHOD_NAME, "1/2: resource = " + resource);
    try
      {
	PoolableXSLStylesheet stylesheet = (PoolableXSLStylesheet)resource;
	if ( ! stylesheet.getURI().equals(this.uri) )
	  throw new IllegalArgumentException
	    ("Stylesheet uri does not match pool uri: Stylesheet uri = " + stylesheet.getURI()
	     + ", pool uri = " + this.uri);
	stylesheet.clearParameters();
      }
    catch (Exception exception)
      {
	ResourcePoolException resourcePoolException = new ResourcePoolException(exception);
	this.log(Level.SEVERE, METHOD_NAME, "1.1/2", resourcePoolException);
	throw resourcePoolException;
      }
    this.log(METHOD_NAME, "2/2");
  }

  /**
   * Returns a newly created stylesheet.
   */

  protected Object newResource ()
    throws ResourcePoolException
  {
    try
      {
	return new PoolableXSLStylesheet(this.transformerFactory, this.uri);
      }
    catch (Exception exception)
      {
	throw new ResourcePoolException(exception);
      }
  }

  /**
   * Returns the prefix for log messages.
   */

  protected String getLogMessagePrefix ()
  {
    return this.uri + " pool " + this.hashCode() + Integer.toHexString(this.hashCode()) + ": ";
  }

  /**
   * Creates a new <code>XSLStylesheetPool</code>.
   */

  public XSLStylesheetPool (TransformerFactory transformerFactory,
			    XSLStylesheetDependencyHelper dependencyHelper,
			    URI uri)
  {
    final String METHOD_NAME = "<constructor>(TransformerFactory transformerFactory, " +
                               "XSLStylesheetDependencyHelper dependencyHelper, URI uri)";
    this.classNameForLogging = "net.mumie.xslt.XSLStylesheetPool";
    this.logger = Logger.getLogger("net.mumie.xslt.XSLStylesheetPool");
    this.log(METHOD_NAME, "1/1: transformerFactory = " + transformerFactory +
	     ", dependencyHelper = " + dependencyHelper + ", uri = " + uri);
    this.transformerFactory = transformerFactory;
    this.dependencyHelper = dependencyHelper;
    this.uri = uri;
  }

  /**
   * Creates a new <code>XSLStylesheetPool</code>.
   */

  public XSLStylesheetPool (URI uri)
    throws TransformerFactoryConfigurationError, ParserConfigurationException, SAXException
  {
    this(TransformerFactory.newInstance(), new XSLStylesheetDependencyHelper(), uri);
  }
}
