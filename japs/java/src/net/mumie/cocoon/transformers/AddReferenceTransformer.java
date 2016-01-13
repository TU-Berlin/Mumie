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

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.util.ParamUtil;
import net.mumie.cocoon.util.Reference;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.source.impl.validity.TimeStampValidity;
import org.xml.sax.SAXException;

/**
 * <p>
 *   Adds a reference to the dynamic data section. Recognizes the following
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
 *       <td><code>from-doc-type</code></td>
 *       <td>The document type of the reference origin, as numerical code</td>
 *       <td rowspan="2">One of the both</td>
 *     </tr>
 *     <tr>
 *       <td><code>from-doc-type-name</code></td>
 *       <td>The document type of the reference origin, as string name</td>
 *     </tr>
 *     <tr>
 *       <td><code>to-doc-type</code></td>
 *       <td>The document type of the reference target, as numerical code</td>
 *       <td rowspan="2">One of the both</td>
 *     </tr>
 *     <tr>
 *       <td><code>to-doc-type-name</code></td>
 *       <td>The document type of the reference target, as string name</td>
 *     </tr>
 *     <tr>
 *       <td><code>id</code></td>
 *       <td>The id of the reference</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr>
 *       <td><code>store-name</code></td>
 *       <td>The name of the {@link XMLElement#STORE STORE} element that wrappes the user
 *       (s.b.) </td>
 *       <td>No. Defaults to <code>"ref"</code>.</td>
 *     </tr>
 *   </tbody>
 * </table>
 * <p>
 *   An example for the XML inserted by this transformer is the following:
 * </p>
 * <pre>
 * &lt;mumie:dynamic_data originator="net.mumie.cocoon.tramsformers.AdReferenceTransformer"&gt;
 *   &lt;mumie:store name="ref"&gt;
 *     &lt;mumie:ref id="63"&gt;
 *       &lt;mumie:from&gt;&lt;mumie:worksheet id="12"/&gt;&lt;/mumie:from&gt;
 *       &lt;mumie:to&gt;&lt;mumie:problem id="134"/&gt;&lt;/mumie:to&gt;
 *       &lt;mumie:ref_type id="0" name="component"/&gt;
 *       &lt;mumie:ref_attribute name="points" value="10"/&gt;
 *     &lt;/mumie:ref&gt;
 *   &lt;/mumie:store&gt;
 * &lt;/mumie:dynamic_data&gt;
 * </pre>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: AddReferenceTransformer.java,v 1.8 2007/07/11 15:38:51 grudzin Exp $</code>
 */

public class AddReferenceTransformer extends AbstractAddDynamicDataTransformer
  implements CacheableProcessingComponent 
{
  /**
   * The reference.
   */

  protected Reference reference = null;

  /**
   * Name of the {@link XMLElement#STORE STORE} element that wrappes the reference.
   */

  protected String storeName = null;

  /**
   * Calls the superclass <code>setup</code> method and initializes
   * {@link #reference reference} from the parameters.
   */

  public void setup (SourceResolver resolver,
                     Map objectModel,
                     String source,
                     Parameters parameters)
    throws ProcessingException, SAXException, IOException 
  {
    final String METHOD_NAME = "setup";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");
    super.setup(resolver, objectModel, source, parameters);
    try
      {
        int fromDocType =
          ParamUtil.getAsDocType(parameters, "from-doc-type", "from-doc-type-name");
        int toDocType =
          ParamUtil.getAsDocType(parameters, "to-doc-type", "to-doc-type-name");
        int refId = ParamUtil.getAsId(parameters, "id");
        this.storeName = ParamUtil.getAsString(parameters, "store-name", "ref");
        this.reference = (Reference)this.manager.lookup(Reference.ROLE);
        this.reference.setup(fromDocType,toDocType,refId);
        this.getLogger().debug
          (METHOD_NAME + " 2/2: Done." +
           " fromDocType = " + fromDocType +
           ", toDocType = " + toDocType +
           ", refId = " + refId +
           ", this.reference = " + this.reference +
           ", this.storeName = " + this.storeName);
      }
    catch (Exception exception)
      {
        throw new ProcessingException(exception);
      }
  }

  /**
   * Recycles this transformer.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");
    super.recycle();
    if ( this.reference != null )
      {
        this.manager.release(this.reference);
        this.reference = null;
      }
    this.storeName = null;
    this.getLogger().debug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Writes the content of the dynamic data element.
   */

  protected void dynamicDataElementContentToSAX ()
    throws SAXException
  {
    final String METHOD_NAME = "dynamicDataElementContentToSAX()";
    this.getLogger().debug
      (METHOD_NAME + " 1/2: " +
       "this.reference = " + this.reference +
       ", this.storeName = " + this.storeName);

    // Start STORE element:
    this.metaXMLElement.reset();
    this.metaXMLElement.setLocalName(XMLElement.STORE);
    this.metaXMLElement.addAttribute(XMLAttribute.NAME, this.storeName);
    this.metaXMLElement.startToSAX(this.contentHandler);

    // Send reference to SAX:
    this.reference.toSAX(this.contentHandler, false);

    // Close STORE element:
    this.metaXMLElement.reset();
    this.metaXMLElement.setLocalName(XMLElement.STORE);
    this.metaXMLElement.endToSAX(this.contentHandler);

    this.getLogger().debug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * <p>
   *   Generates the caching key. This is a string consisting of the following components,
   *   separated by dashes:
   * </p>
   * <ul>
   *   <li>the type of the reference origin document,</li>
   *   <li>the type of the reference target document,</li>
   *   <li>the id of the reference,</li>
   *   <li>the store name ({@link #storeName storeName}).</li>
   * </ul>
   * <p>
   *   Example:<pre>
   *     21-5-23-ref
   *   </pre>
   * </p>
   */

  public Serializable getKey()
  {
    final String METHOD_NAME = "getKey";
    this.getLogger().debug(METHOD_NAME + " 1/2");
    String key =
      this.reference.getFromDocType() + "-" +
      this.reference.getToDocType() + "-" +
      this.reference.getId() + "-" +
      this.storeName; 
    this.getLogger().debug(METHOD_NAME + " 2/2: key = " + key);
    return key;
  }

  /**
   * Creates the validity object.
   * Returns a {@link TimeStampValidity TimeStampValidity}
   * with the last modification time of the reference.
   */

  public SourceValidity getValidity()
  {
    final String METHOD_NAME = "getValidity";
    this.getLogger().debug(METHOD_NAME + " 1/2");
    try
      {
        long lastModified = this.reference.getLastModified().getTime();
        this.getLogger().debug(METHOD_NAME + " 2/2: lastModified = " + lastModified);
        return new TimeStampValidity(lastModified);
      }
    catch (Exception exception)
      {
	this.getLogger().warn(METHOD_NAME + ": Caught exception: " + exception);
      }
    return null;
  }
}
