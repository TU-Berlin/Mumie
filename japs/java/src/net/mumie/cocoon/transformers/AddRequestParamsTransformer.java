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
 *   Adds request parameters to the dynamic data section. Recognizes the following
 *   parameters: 
 * </p>
 * <table class="genuine indented" style="width:60em">
 *   <thead>
 *     <tr>
 *       <td>Name</td>
 *       <td>Description</td>
 *       <td>Required</td>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td><code>include</code></td>
 *       <td>Comma or space separated list of the request parameters to add.</td>
 *       <td>No</td>
 *     </tr>
 *     <tr>
 *       <td><code>exclude</code></td>
 *       <td>Comma or space separated list of the request parameters not to add.</td>
 *       <td>No</td>
 *     </tr>
 *   </tbody>
 * </table>
 * <p>
 *   The following rules apply for these Parameters:
 * </p>
 * <ol>
 *   <li>
 *     If both <code>include</code> and <code>exclude</code> are lacking, all request
 *     parameters are added.
 *   </li>
 *   <li>
 *     If only <code>include</code> is given and <code>exclude</code> is lacking, only the
 *     request parameters mentioned in <code>include</code> are added.
 *   </li>
 *   <li>
 *     If only <code>exclude</code> is given and <code>include</code> is lacking, all except
 *     the request parameters mentioned in <code>exclude</code> are added.
 *   </li>
 *   <li>
 *     If both <code>include</code> and <code>exclude</code> are given, all request
 *     parameters mentioned in <code>include</code> and not mentioned in
 *     <code>exclude</code> are added.
 *   </li>
 * </ol>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: AddRequestParamsTransformer.java,v 1.7 2007/07/11 15:38:51 grudzin Exp $</code>
 */

public class AddRequestParamsTransformer extends AbstractAddDynamicDataTransformer
  implements CacheableProcessingComponent 
{
  /**
   * The request parameters to add.
   */

  protected HashMap requestParams = new HashMap();

  /**
   * Auxiliary method to process the "include" and "exclude" parameters. Parses a string and
   * returns the tokens as a list. The tokens are the parts of the string separated by
   * commas or whitespaces.
   */

  protected List parseList (String string)
  {
    if ( string == null )
      return null;
    StringTokenizer tokenizer = new StringTokenizer(string);
    List list = new ArrayList();
    while ( tokenizer.hasMoreTokens() )
      list.add(tokenizer.nextToken());
    return list;
  }

  /**
   * Calls the superclass <code>setup</code> method and sets up the list of request
   * parameters to add.
   */

  public void setup (SourceResolver resolver,
                     Map objectModel,
                     String source,
                     Parameters parameters)
    throws ProcessingException
  {
    final String METHOD_NAME = "setup";
    this.getLogger().debug(METHOD_NAME + " 1/3: Started");
    try
      {
        super.setup(resolver, objectModel, source, parameters);

        // Parameter names:
        final String INCLUDE = "include";
        final String EXCLUDE = "exclude";

        // Include/exclude list:
        List includesParamNames = this.parseList(parameters.getParameter(INCLUDE, null));
        List excludesParamNames = this.parseList(parameters.getParameter(EXCLUDE, null));

        // Get the parameters to add (this.requestParams):
        Request request = ObjectModelHelper.getRequest(objectModel);
        Enumeration paramNames = request.getParameterNames();
        while ( paramNames.hasMoreElements() )
          {
            String paramName = (String)paramNames.nextElement();
             if ( ( includesParamNames == null || includesParamNames.contains(paramName) ) &&
                  ( excludesParamNames == null || !excludesParamNames.contains(paramName) ) )
               {
                 String paramValue = request.getParameter(paramName);
                 this.requestParams.put(paramName, paramValue);
               }
          }

        this.getLogger().debug
          (METHOD_NAME + " 2/3:" + 
           " includesParamNames = " + includesParamNames +
           ", excludesParamNames = " + excludesParamNames +
           ", this.requestParams = " + this.requestParams);
      }
    catch (Exception exception)
      {
	throw new ProcessingException(exception);
      }
    this.getLogger().debug (METHOD_NAME + " 3/3: Finished");
  }

  /**
   * Sends the request parameters {@link #requestParams requestParams} as
   * {@link XMLElement#PARAM PARAM} elements to SAX.
   */

  protected void dynamicDataElementContentToSAX ()
    throws SAXException
  {
    final String METHOD_NAME = "dynamicDataElementContentToSAX()";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");
    try
      {
        Iterator iterator = this.requestParams.entrySet().iterator();
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
    this.requestParams = new HashMap();
  }

  /**
   * <p>
   *   Returns the validity object (caching implementation).
   * </p>
   * <p>
   *   Since the output of this transformer does not depend on external sources, its cached
   *   output is always valid. Thus, this method returns a {@link NOPValidity}. To be
   *   precise, the returned object is the static {@link NOPValidity} instance named
   *   {@link NOPValidity#SHARED_INSTANCE SHARED_INSTANCE} provided by the {@link
   *   NOPValidity} class.
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
   *   The request parameters, i.e., {@link #requestParams}, are used as the key. Thus,
   *   {@link #requestParams} is returned.
   * </p>
   */

  public Serializable getKey ()
  {
    return this.requestParams;
  }
}
