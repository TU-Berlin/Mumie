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
import java.net.URI;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerConfigurationException;

/**
 *
 * @author Tilman Rassy
 *
 * @version <span class="file">$Id: PoolableXSLStylesheet.java,v 1.2 2004/03/30 19:16:11 rassy Exp $</span>
 */

public class PoolableXSLStylesheet extends XSLStylesheet
{

  /**
   * The URI of the source of this stylesheet.
   */

  protected URI uri;

  /**
   * The time this XSLStylesheet object was created. Given as milliseconds since midnight,
   * January 1, 1970 UTC.
   */

  protected long creationTime;

  /**
   * Get method for {@link #uri uri}.
   */

  public URI getURI ()
  {
    return this.uri;
  }

  /**
   * Get method for {@link #creationTime creationTime}.
   */

  public long getCreationTime ()
  {
    return this.creationTime;
  }

  /**
   * Creates a new <code>PoolableXSLStylesheet</code>.
   */

  public PoolableXSLStylesheet (TransformerFactory transformerFactory, URI uri)
    throws TransformerConfigurationException
  {
    super(transformerFactory, new File(uri));
    this.uri = uri;
    this.creationTime = System.currentTimeMillis();
  }
}
