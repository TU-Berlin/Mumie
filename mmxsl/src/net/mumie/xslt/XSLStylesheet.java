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

import java.io.File;
import java.io.StringWriter;
import java.io.FileNotFoundException;
import java.util.Properties;
import java.util.Map;
import java.util.Hashtable;
import java.util.Enumeration;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

/**
 *
 * @author Tilman Rassy
 *
 * @version <span class="file">$Id: XSLStylesheet.java,v 1.10 2004/03/30 19:16:11 rassy Exp $</span>
 */

public class XSLStylesheet
{
  /**
   * The <code>Templates</code> object wrapped by this <code>XSLStylesheet</code>.
   */

  protected Templates templates;

  /**
   * The system id of the stylesheet. This is the URL of its source, usually a filename.
   */

  protected String systemId;

  /**
   * The parameters to pass to the stylesheet.
   */

  protected Hashtable parameters = new Hashtable();

  /**
   * The <code>ErrorListener</code> to be used, or <code>null</code> if the default is to be
   * used.
   */

  protected ErrorListener errorListener = null;

  /**
   * Creates a new <code>XSLStylesheet</code>.
   */

  public XSLStylesheet (TransformerFactory transformerFactory, Source source)
    throws TransformerConfigurationException
  {
    this.systemId = source.getSystemId();
    this.templates = transformerFactory.newTemplates(source);
    this.errorListener = new SimpleErrorListener();
  }

  /**
   * Creates a new <code>XSLStylesheet</code>.
   */

  public XSLStylesheet (Source source)
    throws TransformerFactoryConfigurationError, TransformerConfigurationException
  {
    this(TransformerFactory.newInstance(), source);
  }

  /**
   * Creates a new <code>XSLStylesheet</code>.
   */

  public XSLStylesheet (TransformerFactory transformerFactory, File file)
    throws TransformerConfigurationException
  {
    this(transformerFactory, new StreamSource(file));
  }

  /**
   * Creates a new <code>XSLStylesheet</code>.
   */

  public XSLStylesheet (TransformerFactory transformerFactory, String systemId)
    throws TransformerConfigurationException
  {
    this(transformerFactory, new File(systemId));
  }

  /**
   * Creates a new <code>XSLStylesheet</code>.
   */

  public XSLStylesheet (File file)
    throws TransformerFactoryConfigurationError, TransformerConfigurationException
  {
    this(TransformerFactory.newInstance(), file);
  }

  /**
   * Creates a new <code>XSLStylesheet</code>.
   */

  public XSLStylesheet (String systemId)
    throws TransformerFactoryConfigurationError, TransformerConfigurationException
  {
    this(new File(systemId));
  }

  /**
   * Get method for {@link #systemId systemId}.
   */

  public String getSystemId ()
  {
    return this.systemId;
  }

  /**
   * Sets a stylesheet parameter.
   */

  public void setParameter (String name, Object value)
  {
    this.parameters.put(name, value);
  }

  /**
   * Removes all stylesheet parameters.
   */

  public void clearParameters ()
  {
    this.parameters.clear();
  }

  /**
   * Returns stylesheet parameter <code>name</code>. May also return <code>null</code>,
   * which indicates that no such parameter exists.
   */

  public Object getParameter (String name)
  {
    return this.parameters.get(name);
  }

  /**
   * Returns all parameter names.
   */

  public String[] getParameterNames ()
  {
    return (String[])(this.parameters.keySet().toArray(new String[this.parameters.size()]));
  }

  /**
   * Sets the parameters.
   */

  public void setParameters (Map parameters)
  {
    this.parameters = new Hashtable(parameters);
  }

  /**
   * Sets the error listener. <code>errorListener</code> may also be <code<null</code>,
   * which indicates that the default is to be used.
   */

  public void setErrorListener (ErrorListener errorListener)
  {
    this.errorListener = errorListener;
  }

  /**
   * Returns the error listener. May also return <code<null</code>, which indicates that the
   * default error listener is used. 
   */

  public ErrorListener getErrorListener ()
  {
    return this.errorListener;
  }

  /**
   * Returns the static properties of <code>xsl:output</code>.
   */

  public Properties getOutputProperties()
  {
    return this.templates.getOutputProperties();
  }

  /**
   * Transforms a source.
   */

  public void transform (Source source, Result result)
    throws TransformerException
  {
    // Getting a transformer
    Transformer transformer = this.templates.newTransformer();

    // Setting stylesheet parameters
    String[] parameterNames = this.getParameterNames();
    for (int i = 0; i < parameterNames.length; i++)
      transformer.setParameter(parameterNames[i], this.getParameter(parameterNames[i]));

    // Setting error listener (if necessary)
    if ( this.getErrorListener() != null )
      transformer.setErrorListener(this.getErrorListener());

    // Transforming
    transformer.transform(source, result);
  }

  /**
   * Transforms a source.
   */

  public void transform (String sourceSystemId, String resultSystemId)
    throws TransformerException
  {
    this.transform(new StreamSource(sourceSystemId), new StreamResult(resultSystemId));
  }

  /**
   * Transforms a source.
   */

  public void transform (String sourceSystemId)
    throws TransformerException
  {
    this.transform(new StreamSource(sourceSystemId), new StreamResult(System.out));
  }

  /**
   * Processes a source and returns the result as a string.
   */

  public String process (Source source)
    throws TransformerException
  {
    StringWriter stringWriter = new StringWriter();
    this.transform(source, new StreamResult(stringWriter));
    return stringWriter.toString();
  }

  /**
   * Processes a source and returns the result as a string.
   */

  public String process (String sourceSystemId)
    throws TransformerException
  {
    return this.process(new StreamSource(sourceSystemId));
  }

  /**
   * Returns a string representation of this <code>XSLStylesheet</code>.
   */

  public String toString ()
  {
    return this.getSystemId();
  }
}
