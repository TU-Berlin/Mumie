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

import java.io.Serializable;
import java.util.Map;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.UseMode;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.pseudodocs.PseudoDocument;
import net.mumie.cocoon.util.ParamUtil;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.source.impl.validity.TimeStampValidity;
import org.xml.sax.SAXException;

/**
 * <p>
 *   Adds a Mumie pseudo-document to the dynamic data section. Recognizes the following
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
 *       <td><code>type</code></td>
 *       <td>The type of the pseudo-document (numerical code)</td>
 *       <td rowspan="2" style="vertical-align:middle">Only one of the both</td>
 *     </tr>
 *     <tr>
 *       <td><code>type-name</code></td>
 *       <td>The type of the pseudo-document (string name)</td>
 *     </tr>
 *     <tr>
 *       <td><code>id</code></td>
 *       <td>The id of the pseudo-document</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr>
 *       <td><code>use-mode</code></td>
 *       <td>The use mode (numerical code).</td>
 *       <td rowspan="2" style="vertical-align:middle">None or one of the both. Default is
 *           {@link net.mumie.cocoon.notions.UseMode#INFO UseMode.INFO}.</td>
 *     </tr>
 *     <tr>
 *       <td><code>use-mode-name</code></td>
 *       <td>The use mode (string name).</td>
 *     </tr>
 *     <tr>
 *       <td><code>with-path</code></td>
 *       <td>Whether the pseudo-document path is included in the XML. This parameter is
 *         ineffective when <code>use-mode</code> is {@link UseMode#SERVE CHECKOUT}, in which
 *         case paths are always included</td>
 *       <td>No. Default is <code>false</code></td>
 *     </tr>
 *     <tr>
 *       <td><code>store-name</code></td>
 *       <td>The name of the {@link XMLElement#STORE STORE} element that wrappes the user
 *       (s.b.) </td>
 *       <td>No. Defaults to name of the pseudo-document type</code>.</td>
 *     </tr>
 *   </tbody>
 * </table>
 * <p>
 *   NOTE 1: Only one of the parameters <code>type</code> and <code>type-name</code> should be
 *   set. If both are set, <code>type</code> takes precedence.
 * </p>
 * <p>
 *   NOTE 2: Only one of the parameters <code>use-mode</code> and <code>use-mode-name</code>
 *   should be set. If both are set, <code>use-mode</code> takes precedence.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: AddPseudoDocumentTransformer.java,v 1.1 2008/08/27 14:21:52 rassy Exp $</code>
 */

public class AddPseudoDocumentTransformer extends AbstractAddDynamicDataTransformer
  implements CacheableProcessingComponent
{
  /**
   * The pseudo-document to generate.
   */

  protected PseudoDocument pseudoDocument = null;

  /**
   * Name of the {@link XMLElement#STORE STORE} element that wrappes the user.
   */

  protected String storeName = null;

  /**
   * The service selector to select pseudo-documents.
   */

  ServiceSelector pseudoDocumentSelector = null;

  /**
   * Calls the superclass <code>setup</code> method and processes the parameters and
   * initializes {@link #PseudoDocument}.
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

	// Type:
        int type = ParamUtil.getAsPseudoDocType(this.parameters, "type", "type-name");

	// Id:
        int id = this.parameters.getParameterAsInteger("id");

	// Use mode:
 	int useMode =
          ParamUtil.getAsUseMode(this.parameters, "use-mode", "use-mode-name", UseMode.INFO);

        // "With path" flag:
        boolean withPath =
          ParamUtil.getAsBoolean(this.parameters, "with-path", false);

        this.getLogger().debug
          (METHOD_NAME + " 2/3:" + 
           " type = " + type +
           ", id = " + id +
           ", useMode = " + useMode +
           ", withPath = " + withPath);

	// Get a pseudo-document from the pool:
	this.pseudoDocumentSelector =
	  (ServiceSelector) this.manager.lookup(PseudoDocument.ROLE + "Selector");
	this.pseudoDocument =
          (PseudoDocument)pseudoDocumentSelector.select(PseudoDocType.hintFor[type]);

        // Set id and useMode:
        this.pseudoDocument.setId(id);
 	this.pseudoDocument.setUseMode(useMode);
        this.pseudoDocument.setWithPath(withPath);

        this.storeName =
          ParamUtil.getAsString(parameters, "store-name", PseudoDocType.nameFor(type));

        this.getLogger().debug (METHOD_NAME + " 3/3: Finished");
      }
    catch (Exception exception)
      {
	throw new ProcessingException(exception);
      }
  }

  /**
   * Sends the content of the dynamic data element to SAX.
   */

  public void dynamicDataElementContentToSAX ()
    throws SAXException
  {
    final String METHOD_NAME = "dynamicDataElementContentToSAX";
    this.getLogger().debug(METHOD_NAME + " 1/2: started");

    // Start STORE element:
    this.metaXMLElement.reset();
    this.metaXMLElement.setLocalName(XMLElement.STORE);
    this.metaXMLElement.addAttribute(XMLAttribute.NAME, this.storeName);
    this.metaXMLElement.startToSAX(this.contentHandler);

    // Sent pseudo-document to SAX:
    this.pseudoDocument.toSAX(this.contentHandler, false);

    // Close STORE element:
    this.metaXMLElement.reset();
    this.metaXMLElement.setLocalName(XMLElement.STORE);
    this.metaXMLElement.endToSAX(this.contentHandler);

    this.getLogger().debug(METHOD_NAME + " 2/2: finished");
  }

  /**
   * Releases the global services.
   */

  protected void releaseServices ()
  {
    final String METHOD_NAME = "releaseServices";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");
    try
      {
	this.pseudoDocumentSelector.release(this.pseudoDocument);
	this.pseudoDocument = null;
	this.manager.release(this.pseudoDocumentSelector);
	this.pseudoDocumentSelector = null;
      }
    catch (Exception exception)
      {
	this.getLogger().warn(METHOD_NAME + ": Caught exception: " + exception);
      }
    this.getLogger().debug(METHOD_NAME + " 1/2: Done");
  }

  /**
   * Recycles this <code>PseudoDocumentGenerator</code>.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle";
    this.getLogger().debug(METHOD_NAME + " 1/2");
    this.releaseServices();
    super.recycle();
    this.getLogger().debug(METHOD_NAME + " 2/2");
  }

  /**
   * Disposes this <code>PseudoDocumentGenerator</code>.
   */

  public void dispose ()
  {
    final String METHOD_NAME = "dispose";
    this.getLogger().debug(METHOD_NAME + " 1/2");
    this.releaseServices();
    super.dispose();
    this.getLogger().debug(METHOD_NAME + " 2/2");
  }

  /**
   * <p>
   *   Generates the caching key. This is a string consisting of the following components,
   *   separated by dashes:
   * </p>
   * <ul>
   *   <li>the pseudo-document type, as numerical code,</li>
   *   <li>the pseudo-document id,</li>
   *   <li>the use mode, as numerical code,</li>
   * </ul>
   * <p>
   *   Example:<pre>4-23-0</pre>
   * </p>
   */

  public Serializable getKey()
  {
    final String METHOD_NAME = "getKey";
    this.getLogger().debug(METHOD_NAME + " 1/2");
    String key =
      this.pseudoDocument.getType() + "-" +
      this.pseudoDocument.getId() + "-" +
      this.pseudoDocument.getUseMode();
    this.getLogger().debug(METHOD_NAME + " 2/2: key = " + key);
    return key;
  }

  /**
   * Creates the validity object.
   * Returns a {@link org.apache.excalibur.source.impl.validity.TimeStampValidity}
   * with the last modification time of the pseudoDocument.
   */

  public SourceValidity getValidity()
  {
    final String METHOD_NAME = "getValidity";
    this.getLogger().debug(METHOD_NAME + " 1/2");
    try
      {
        long lastModified = this.pseudoDocument.getLastModified().getTime();
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
