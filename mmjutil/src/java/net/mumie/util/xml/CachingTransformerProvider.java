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

package net.mumie.util.xml;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import net.mumie.util.io.DependencyUpdateException;
import net.mumie.util.logging.SimpleLogger;
import org.xml.sax.SAXException;
import java.util.List;
import java.util.ArrayList;

/**
 * <p>
 *   Factory-like class providing XML transformers and templates, caching the templates.
 * </p>
 * <p>
 *   The most important method from the user side is {@link #getTransformer getTransformer},
 *   which returns a {@link Transformer Transformer} instance for a given URI. The latter
 *   must point to the source of the transformer. Currently, only local files are supported
 *   as sources; thus, the URI must point to a local XSL stylesheet file. The
 *   {@link Transformer Transformer} instance is created from a corresponding
 *   {@link Templates Templates} object. The {@link Templates Templates} objects are
 *   cached. Thus, if a transformer is requested multiple times for the same URI, the {@link
 *   Templates Templates} object is created only once and then re-used.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: CachingTransformerProvider.java,v 1.7 2007/06/04 00:13:22 rassy Exp $</code>
 */

public class CachingTransformerProvider
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The transformer factory of this instance.
   */

  protected TransformerFactory transformerFactory;

  /**
   * The error listener of this instance, or null if no error listener is specified. If
   * non-null, this error listener is passed to the factory as well as to each transformer.
   */

  protected ErrorListener errorListener;

  /**
   * The dependency helper of this object.
   */

  protected XSLStylesheetDependencyHelper dependencyHelper;

  /**
   * A {@link Templates Templates} object together with a timestamp. Instances of this class
   * are the values of the cache map {@link #templatesCache templatesCache}. The timestamp
   * contains the creation time of the {@link Templates Templates} object, which is needed
   * for the up-to-date check of the {@link Templates Templates} object.
   */

  protected class CacheRecord
  {
    Templates templates;
    long created;
  }

  /**
   * The templates cache. The keys of this map are {@link URI URI} objects, the values
   * {@link CacheRecord CacheRecord} objects. The former represent the XSL
   * stylesheet source, the latter the corresponding {@link Templates Templates} object
   * together with a timestamp specifying its creation time.
   */

  protected Map<URI,CacheRecord> templatesCache = new HashMap<URI,CacheRecord>();

  /**
   * The maximum size of the cache. -1 means unlimited.
   */

  protected int templatesCacheMaxSize = -1;

  /**
   * A static <code>CachingTransformerProvider</code> instance.
   */

  protected static CachingTransformerProvider sharedInstance = null;

  /**
   * The logger of this instance, or null if logging is disabled.
   */

  protected SimpleLogger logger = null;

  // --------------------------------------------------------------------------------
  // Logging
  // --------------------------------------------------------------------------------

  /**
   * Writes a log message provided {@link #logger logger} is not null. In the latter case
   * ({@link #logger logger} not null), the effect is the same as
   * <pre>this.logger.log("Jvmd: " + message, throwable);</pre>
   */

  protected final void log (String message, Throwable throwable)
  {
    if ( this.logger != null )
      this.logger.log("CachingTransformerProvider: " + message, throwable);
  }

  /**
   * Writes a log message. Same as
   * <pre>this.log(message, null);</pre>
   */

  protected final void log (String message)
  {
    this.log(message, null);
  }

  // --------------------------------------------------------------------------------
  // Templates
  // --------------------------------------------------------------------------------

  /**
   * Creates a new {@link Templates Templates} object corresponding to the specified URI.
   */

  protected Templates newTemplates (URI uri)
    throws TransformerConfigurationException 
  {
    return this.transformerFactory.newTemplates(new StreamSource(new File(uri)));
  }

  /**
   * Returns the {@link Templates Templates} object corresponding to the specified URI. 
   */

  public synchronized Templates getTemplates (URI uri)
    throws TransformerConfigurationException, DependencyUpdateException
  {
    if ( this.templatesCache.containsKey(uri) )
      {
        // Get cache record:
        CacheRecord cacheRecord = (CacheRecord)this.templatesCache.get(uri);

        // If cached templates object is outdated, recreate it:
        if ( cacheRecord.created < this.dependencyHelper.getAbsoluteLastModified(uri) )
          {
            cacheRecord.templates = this.newTemplates(uri);
            cacheRecord.created = System.currentTimeMillis();
            this.log(uri + ", outdated, recreated");
          }
        else
          this.log(uri + ", from cache");

        return cacheRecord.templates;
      }
    else
      {
        // Create new cache record:
        CacheRecord cacheRecord = new CacheRecord();
        cacheRecord.templates = this.newTemplates(uri);
        cacheRecord.created = System.currentTimeMillis();

        // Add cache record to cache provided the maximum cache size is not exceeded:
        if ( this.templatesCacheMaxSize == -1 ||
             this.templatesCache.size() < this.templatesCacheMaxSize )
          {
            this.templatesCache.put(uri, cacheRecord);
            this.log(uri + ", created, cached");
          }
        else
          this.log(uri + ", created, not cached (max. cache size reached)");

        return cacheRecord.templates;
      }
  }

  // --------------------------------------------------------------------------------
  // Transformer
  // --------------------------------------------------------------------------------

  /**
   * Returns the {@link Transformer Transformer} object corresponding to the specified URI. 
   */

  public Transformer getTransformer (URI uri)
    throws TransformerConfigurationException, DependencyUpdateException
  {
    Transformer transformer = this.getTemplates(uri).newTransformer();
    if ( this.errorListener != null )
      transformer.setErrorListener(this.errorListener);
    return transformer;
  }

  /**
   * Returns the {@link Transformer Transformer} object corresponding to the URI specified
   * in the <code>xml-stylesheet</code> processing instruction in the XML document given
   * by <code>source</code>. The arguments of this method have the same meaning as those
   * of {@link TransformerFactory#getAssociatedStylesheet getAssociatedStylesheet} in class
   * {@link TransformerFactory TransformerFactory}. If there is no
   * <code>xml-stylesheet</code> processing instruction in the XML document, returns
   * <code>null</code>.
   */

  public synchronized Transformer getAssociatedTransformer (Source source,
                                                            String media,
                                                            String title,
                                                            String charset)
    throws TransformerConfigurationException, DependencyUpdateException,
           URISyntaxException
  {
    Source transformerSource =
      this.transformerFactory.getAssociatedStylesheet(source, media, title, charset);
    return
      (transformerSource != null
       ? this.getTransformer(new URI(transformerSource.getSystemId()))
       : null);
  }

  // --------------------------------------------------------------------------------
  // Get/set maximum cache size
  // --------------------------------------------------------------------------------

  /**
   * Returns the maximum size of the cache. -1 means unlimited.
   */

  public int getTemplatesCacheMaxSize ()
  {
    return this.templatesCacheMaxSize;
  }

  /**
   * Sets maximum size of the cache. -1 means unlimited.
   */

  public synchronized void setTemplatesCacheMaxSize (int templatesCacheMaxSize)
  {
    if ( templatesCacheMaxSize < -1 )
      throw new IllegalArgumentException
        ("Illegal value for maximum cache size: " + templatesCacheMaxSize);
    this.templatesCacheMaxSize = templatesCacheMaxSize;
  }

  // --------------------------------------------------------------------------------
  // Status information
  // --------------------------------------------------------------------------------

  /**
   * 
   */

  public synchronized List<URI> listCache ()
  {
    List<URI> list = new ArrayList<URI>();
    list.addAll(this.templatesCache.keySet());
    return list;
  }

  /**
   * 
   */

  public long getCreationTime (URI uri)
  {
    if ( !this.templatesCache.containsKey(uri) )
      throw new IllegalArgumentException
        ("getCreationTime: No such cache entry: " + uri);
    return ((CacheRecord)this.templatesCache.get(uri)).created;
  }

  // --------------------------------------------------------------------------------
  // Constructors, shared static instance
  // --------------------------------------------------------------------------------

  /**
   * Creates a new instance with the specified logger and error listener.
   */

  public CachingTransformerProvider (SimpleLogger logger, ErrorListener errorListener)
    throws SAXException, ParserConfigurationException 
  {
    this.transformerFactory = TransformerFactory.newInstance();
    this.dependencyHelper = new XSLStylesheetDependencyHelper();
    this.logger = logger;
    this.errorListener = errorListener;
    if ( this.errorListener != null )
      this.transformerFactory.setErrorListener(this.errorListener);
  }

  /**
   * Creates a new instance with no logger (logging disabled)nd the default error listener.
   */

  public CachingTransformerProvider ()
    throws SAXException, ParserConfigurationException 
  {
    this(null, null);
  }

  /**
   * Returns a static <code>CachingTransformerProvider</code> instance. 
   */

  public static synchronized CachingTransformerProvider getSharedInstance ()
    throws SAXException, ParserConfigurationException 
  {
    if ( sharedInstance == null )
      {
        SimpleLogger logger = SimpleLogger.getSharedInstance();
        ErrorListener errorListener = new AbortingXSLErrorListener();
        sharedInstance = new CachingTransformerProvider(logger, errorListener);
      }
    return sharedInstance;
  }
}
