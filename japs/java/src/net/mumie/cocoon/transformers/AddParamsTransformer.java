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

package net.mumie.cocoon.transformers;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.notions.XMLNamespace;
import net.mumie.cocoon.xml.MetaXMLElement;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.transformation.ServiceableTransformer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import java.util.List;
import org.apache.cocoon.ProcessingException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.source.impl.validity.NOPValidity;
import java.io.Serializable;

/**
 * <p>
 *   Adds all parameters to the dynamic data section.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: AddParamsTransformer.java,v 1.7 2007/07/11 15:38:51 grudzin Exp $</code>
 */

public class AddParamsTransformer extends AbstractAddDynamicDataTransformer
  implements CacheableProcessingComponent 
{
  /**
   * The parameters as a <code>HashMap</code>
   */

  protected HashMap paramsAsHashMap = new HashMap();

  /**
   * Calls the superclass <code>setup</code> method and copies {@link #parameters} to
   * {@link #paramsAsHashMap}.
   */

  public void setup (SourceResolver resolver,
                     Map objectModel,
                     String source,
                     Parameters parameters)
    throws ProcessingException
  {
    final String METHOD_NAME = "setup";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");
    try
      {
        super.setup(resolver, objectModel, source, parameters);
        String[] names = this.parameters.getNames();
        for (int i = 0; i < names.length; i++)
          this.paramsAsHashMap.put(names[i], this.parameters.getParameter(names[i]));
      }
    catch (Exception exception)
      {
	throw new ProcessingException(exception);
      }
    this.getLogger().debug
      (METHOD_NAME + " 2/2: Finished." +
       " this.paramsAsHashMap = " + this.paramsAsHashMap);
  }

  /**
   * Sends the parameters as
   * {@link XMLElement#PARAM PARAM} elements to SAX.
   */

  protected void dynamicDataElementContentToSAX ()
    throws SAXException
  {
    final String METHOD_NAME = "dynamicDataElementContentToSAX()";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");
    try
      {
        Iterator iterator = this.paramsAsHashMap.entrySet().iterator();
        while ( iterator.hasNext() )
          {
            Map.Entry entry = (Map.Entry)iterator.next();
            this.metaXMLElement.reset();
            this.metaXMLElement.setLocalName(XMLElement.PARAM);
            this.metaXMLElement.addAttribute(XMLAttribute.NAME, (String)entry.getKey());
            this.metaXMLElement.addAttribute(XMLAttribute.VALUE, (String)entry.getValue());
            this.metaXMLElement.toSAX(this.contentHandler);
          }
      }
    catch (Exception exception)
      {
        throw new SAXException(exception);
      }
    this.getLogger().debug(METHOD_NAME + " 2/2: Finished");
  }

  /**
   * Recycles this transformer.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");
    super.recycle();
    this.paramsAsHashMap = new HashMap();
    this.getLogger().debug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * <p>
   *   Returns the validity object (caching implementation).
   * </p>
   * <p>
   *   Since the output of this transformer does not depend on external sources, its cached
   *   output is always valid. Thus, this method returns a {@link NOPValidity}. To be
   *   precise, the returned object is the static {@link NOPValidity} instance named
   *   {@link NOPValidity#SHARED_INSTANCE SHARED_INSTANCE} provided by the
   *   {@link NOPValidity} class.
   * </p>
   */

  public SourceValidity getValidity ()
  {
    return NOPValidity.SHARED_INSTANCE;
  }

  /**
   * <p>
   *   Returns the cache key.
   * </p>
   *
   * <p>
   *   The parameters, i.e., {@link #paramsAsHashMap}, are used as the key. Thus,
   *   {@link #paramsAsHashMap} is returned.
   * </p>
   */

  public Serializable getKey ()
  {
    return this.paramsAsHashMap;
  }
}
