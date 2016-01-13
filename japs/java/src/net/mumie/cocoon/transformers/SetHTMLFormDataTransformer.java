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

import java.util.HashMap;
import java.util.Map;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.cocoon.transformation.AbstractTransformer;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.source.impl.validity.NOPValidity;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import java.io.Serializable;

/**
 * <p>
 *   Set data of value attributes given as parameters for HTML input fields.
 * </p>
 *
 * @author Marek Grudzinski <a href="mailto:grudzin@math.tu-berlin.de">grudzin@math.tu-berlin.de</a>
 * @version <code>$Id: SetHTMLFormDataTransformer.java,v 1.2 2009/01/28 13:16:55 rassy Exp $</code>
 */

public class SetHTMLFormDataTransformer extends AbstractTransformer
  implements CacheableProcessingComponent 
{
  /**
   * The parameters as a <code>HashMap</code>
   */

  protected HashMap<String,String> paramsAsHashMap = new HashMap<String,String>();
  
  /**
   * Indicates if processing is currently inside a HTML form section.
   */
  
  protected boolean insideFormSection = false;

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
        String[] names = parameters.getNames();
        for (int i = 0; i < names.length; i++)
          this.paramsAsHashMap.put(names[i], parameters.getParameter(names[i]));
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
   * Handles an element start.
   */

  public void startElement (String namespaceURI,
                            String localName,
                            String qualifiedName,
                            Attributes attributes)
    throws SAXException
  {
    try
      {
        boolean startElementAlredySet = false;
        
        if ( "form".equals(localName) )
          {
            this.insideFormSection = true;  
          }
        if ( this.insideFormSection && "input".equals(localName) )
          {
            if ( this.isValidTextInput(attributes) )
              {
                AttributesImpl newAttributes = new AttributesImpl();
                for (int i = 0; i < attributes.getLength(); i++)
                  {
                    String attribNamespaceURI = attributes.getURI(i);
                    String attribLocalName = attributes.getLocalName(i);
                    String attribQualifiedName = attributes.getQName(i);
                    String attribType = attributes.getType(i);
                    String attribValue = attributes.getValue(i);
            
                    if ( attribLocalName.equals("name") && this.paramsAsHashMap.get(attribValue) != null )
                      {
                        newAttributes.addAttribute
                          (attribNamespaceURI,
                           "value",
                           "value",
                           attribType,
                           this.paramsAsHashMap.get(attribValue).toString());                        
                      }
            
                    newAttributes.addAttribute
                      (attribNamespaceURI,
                       attribLocalName,
                       attribQualifiedName,
                       attribType,
                       attribValue);                    
                  }                
                super.startElement(namespaceURI, localName, qualifiedName, newAttributes);
                startElementAlredySet = true;                
              }
          }        
        if ( !startElementAlredySet )
          {
            super.startElement(namespaceURI, localName, qualifiedName, attributes);
          }        
      }
    catch (Exception exception)
      {
        throw new SAXException("Wrapped exception", exception);
      }
  }
  
  /**
   * Handles an element end.
   */
  
  public void endElement (String namespaceURI,
                          String localName,
                          String qualifiedName,
                          Attributes attributes)
  throws SAXException
  {
    try
    {        
      if ( "form".equals(localName) )
        {
          this.insideFormSection = false;
        }
      super.endElement(namespaceURI, localName, qualifiedName);
    }
    catch (Exception exception)
    {
      throw new SAXException("Wrapped exception", exception);
    }
  }

  /**
   *  Check if this HTML input is from type <code>text<code> and
   *  if the name attribute match one of the given parameters
   */
  
  public boolean isValidTextInput (Attributes attributes)
  {
    boolean isValidTextInput = false;
    
    for (int i = 0; i < attributes.getLength(); i++)
      {
        if ( attributes.getLocalName(i).equals("type") && attributes.getValue(i).equals("text") )
          {    
            for (int j = 0; j < attributes.getLength(); j++)
              {
                if ( attributes.getLocalName(j).equals("name") && 
                     this.paramsAsHashMap.containsKey(attributes.getValue(j)) )
                  {
                    isValidTextInput = true;            
                    break;                    
                  }
              }
          }
      }
    
    return isValidTextInput;
  }
  
  /**
   * Recycles this transformer.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");
    super.recycle();
    this.paramsAsHashMap = new HashMap<String,String>();
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
