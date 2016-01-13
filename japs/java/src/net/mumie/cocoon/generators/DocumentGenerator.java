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

package net.mumie.cocoon.generators;

import java.io.Serializable;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import net.mumie.cocoon.documents.AbstractDocument;
import net.mumie.cocoon.documents.Document;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.UseMode;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceSelectorWrapper;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.util.LogUtil;
import net.mumie.cocoon.util.ParamUtil;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.generation.ServiceableGenerator;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.source.impl.validity.TimeStampValidity;

/**
 * <p>
 *   Generates a Mumie document. Recognizes the following parameters:
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
 *       <td>The type of the document (numerical code)</td>
 *       <td rowspan="2" style="vertical-align:middle">Only one of the both</td>
 *     </tr>
 *     <tr>
 *       <td><code>type-name</code></td>
 *       <td>The type of the document (string name)</td>
 *     </tr>
 *     <tr>
 *       <td><code>id</code></td>
 *       <td>The id of the document</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr>
 *       <td><code>use-mode</code></td>
 *       <td>The use mode (numerical code).</td>
 *       <td rowspan="2" style="vertical-align:middle">None or one of the both. Default is
 *           {@link UseMode#SERVE SERVE}.</td>
 *     </tr>
 *     <tr>
 *       <td><code>with-path</code></td>
 *       <td>Whether the document path is included in the XML. This parameter is ineffective
 *         when <code>use-mode</code> is {@link UseMode#SERVE CHECKOUT}, in which case paths
 *         are always included</td>
 *       <td>No. Default is <code>false</code></td>
 *     </tr>
 *     <tr>
 *       <td><code>use-mode-name</code></td>
 *       <td>The use mode (string name).</td>
 *     </tr>
 *     <tr>
 *       <td><code>add-content-namespace</code></td>
 *       <td>Add lacking namespaces in the content. Boolean.</td>
 *       <td>No. Default is <code>false</code>.</td>
 *     </tr>
 *     <tr>
 *       <td><code>ref</code></td>
 *       <td>Reference pointing to this document (id)</td>
 *       <td>No</td>
 *     </tr>
 *     <tr>
 *       <td><code>add-custom-meta-namespace</code></td>
 *       <td>Add lacking namespaces in the custom metainfo. Boolean.</td>
 *       <td>No. Default is <code>false</code>.</td>
 *     </tr>
 *   </tbody>
 * </table>
 * <p>
 *   NOTE 1: Only one of the parameters <code>type</code> and <code>type-name</code> should be
 *   set. If both are set, <code>type</code> takes precedence.
 * </p>
 * <p>
 *   NOTE 2: Only one of the parameters <code>use-mode</code> and <code>use-mode-name</code> should be
 *   set. If both are set, <code>use-mode</code> takes precedence.
 * </p>
 * <h2>Example</h2>
 * <p>
 *   Sitemap excerpt:
 * </p>
 * <pre>
 *   &lt;map:generators&gt;
 *     &lt;!-- ... --&gt;
 *     &lt;map:generator name="document"
 *                    src="net.mumie.cocoon.generators.DocumentGenerator"/&gt;
 *   &lt;/map:generators&gt;
 *
 *   &lt;!-- ... --&gt;
 *
 *   &lt;map:pipelines&gt;
 *
 *     &lt;map:pipeline&gt;
 *       &lt;map:match pattern="protected/document/type-name/&#42;/id/&#42;"&gt;
 *         &lt;map:generate type="document"&gt;
 *           &lt;map:parameter name="type-name" value="{1}"/&gt;
 *           &lt;map:parameter name="id" value="{2}"/&gt;
 *         &lt;/map:generate&gt;
 *         &lt;/map:serialize type="xml"&gt;
 *       &lt;/map:match&gt;
 *     &lt;/map:pipeline&gt;
 *
 *   &lt;/map:pipelines&gt;
 * </pre>
 * <p>
 *   Thus, a request with the URL<pre>
 *     <var>prefix</var>/protected/document/type-name/xsl_stylesheet/id/23</pre>
 *   would return the XSL stylesheet of id 23, where <var>prefix</var> stands for the
 *   Cocoon root URL.
 * </p>
 * <p>
 *   For document types, see {@link net.mumie.cocoon.notions.DocType DocType}.
 *   For use modes, see {@link net.mumie.cocoon.notions.UseMode UseMode}.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <span class="file">$Id: DocumentGenerator.java,v 1.47 2009/12/27 18:06:28 rassy Exp $</span>
 */

public class DocumentGenerator extends ServiceableJapsGenerator
  implements CacheableProcessingComponent
{
  /**
   * The class name.
   */

  public static final String CLASS_NAME = DocumentGenerator.class.getName();

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(DocumentGenerator.class);

  /**
   * The document selector.
   */

  protected ServiceSelectorWrapper documentSelector = null;

  /**
   * The document to generate.
   */

  protected Document document;

  /**
   * Creates a new <code>DocumentGenerator</code> instance.
   */

  public DocumentGenerator ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Calls the superclass <code>setup</code> method and processes the parameters and
   * initializes the {@link #document} object.
   */

  public void setup (SourceResolver resolver,
                     Map objectModel,
                     String source,
                     Parameters parameters)
    throws ProcessingException
  {
    final String METHOD_NAME = "setup";
    this.logDebug(METHOD_NAME + " 1/3: Started");

    try
      {
        // Ensure services are released:
        this.releaseServices();

        super.setup(resolver, objectModel, source, parameters);

        // Document type:
        int type = ParamUtil.getAsDocType(this.parameters, "type", "type-name");

        // Document id:
        int id = ParamUtil.getAsId(this.parameters, "id");

        // Use mode:
         int useMode =
          ParamUtil.getAsUseMode(this.parameters, "use-mode", "use-mode-name", UseMode.SERVE);

        // "With path" flag:
        boolean withPath =
          ParamUtil.getAsBoolean(this.parameters, "with-path", false);

        // Reference id:
        int refId = ParamUtil.getAsInt(this.parameters, "ref", Id.UNDEFINED);

        // Type of the reference origin:
        int fromDocType = ParamUtil.getAsDocType
          (this.parameters, "from-doc-type", "from-doc-type-name", DocType.UNDEFINED);

        this.logDebug
          (METHOD_NAME + " 2/3:" + 
           " type = " + type +
           ", id = " + id +
           ", useMode = " + useMode +
           ", withPath = " + withPath +
           ", refId = " + refId +
           ", fromDocType = " + fromDocType);

        // Get a service selector wrapper if necessary:
        if ( this.documentSelector == null )
          {
            this.logDebug(METHOD_NAME + " 2/3.1: Instanciating a ServiceSelectorWrapper");
            String label = "DocumentGenerator#" + this.instanceStatus.getInstanceId();
            this.documentSelector = new ServiceSelectorWrapper
              (label, this.getLogger().getChildLogger(label));
          }

        // Get a document selector and wrap it:
        this.documentSelector.wrap
          ((ServiceSelector)this.serviceManager.lookup(Document.ROLE + "Selector"));

        // Get a document from the pool:
        this.document = (Document)this.documentSelector.select(DocType.hintFor[type]);

        // Set id, useMode, etc.
        this.document.setId(id);
         this.document.setUseMode(useMode);
        this.document.setWithPath(withPath);
        if ( refId != Id.UNDEFINED ) this.document.setRefId(refId);
        if ( fromDocType != DocType.UNDEFINED ) this.document.setFromDocType(fromDocType);
        this.logDebug
          (METHOD_NAME + " 3/3: Done. this.document = " + LogUtil.identify(this.document));
      }
    catch (Exception exception)
      {
        throw new ProcessingException(exception);
      }
  }

  /**
   * Generates the XML. See class documentation for details.
   */

  public void generate ()
    throws ProcessingException 
  {
    final String METHOD_NAME = "generate";
    this.logDebug(METHOD_NAME + " 1/2: started");
    try
      {
        this.document.toSAX(this.contentHandler);
      }
    catch (Exception exception)
      {
        throw new ProcessingException(exception);
      }
    this.logDebug(METHOD_NAME + " 2/2: finished");
  }

  /**
   * Releases the global services.
   */

  protected void releaseServices ()
  {
    try
      {
        if ( this.documentSelector != null && this.documentSelector.hasServiceSelector() )
          {
            if ( this.document != null )
              {
                this.documentSelector.release(this.document);
                this.document = null;
              }
            this.serviceManager.release(this.documentSelector.unwrapServiceSelector());
            // this.documentSelector = null;  Should be unnecessary because the wrapped
            //                                service selector is set to null
          }
        else if ( this.document != null )
          {
            throw new IllegalStateException
              ("Document exists but document selector is null");
          }
      }
    catch (Exception exception)
      {
        this.logWarn("releaseServices: Caught exception: " + exception);
      }
  }

  /**
   * Recycles this <code>DocumentGenerator</code>.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.releaseServices();
    super.recycle();
    this.instanceStatus.notifyRecycle();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Disposes this <code>DocumentGenerator</code>.
   */

  public void dispose ()
  {
    final String METHOD_NAME = "dispose";
    this.logDebug(METHOD_NAME + " 1/2");
    this.releaseServices();
    super.dispose();
    this.instanceStatus.notifyDispose();
    this.logDebug(METHOD_NAME + " 2/2");
  }

  /**
   * <p>
   *   Generates the caching key. This is a string consisting of the following components,
   *   separated by dashes:
   * </p>
   * <ul>
   *   <li>the document type, as numerical code,</li>
   *   <li>the document id,</li>
   *   <li>the reference id, or <code>"!"</code> if not defined</li>
   *   <li>the document type of the reference origin, or <code>"!"</code> if not defined</li>
   *   <li>the use mode, as numerical code.</li>
   * </ul>
   * <p>
   *   Example:<pre>7-34-!-!-0-0-0</pre>
   * </p>
   */

  public Serializable getKey()
  {
    final String METHOD_NAME = "getKey";
    this.logDebug(METHOD_NAME + " 1/2");

    int refId = this.document.getRefId();
    int fromDocType = this.document.getFromDocType();

    StringBuffer keyBuffer = new StringBuffer();

    // Add type, id, ref id, use mode:
    keyBuffer
      .append(this.document.getType())
      .append('-')
      .append(this.document.getId())
      .append('-')
      .append(refId != Document.UNDEFINED ? Integer.toString(refId) : "!")
      .append('-')
      .append(fromDocType != DocType.UNDEFINED ? Integer.toString(fromDocType) : "!")
      .append('-')
      .append(this.document.getUseMode());

    String key = keyBuffer.toString();

    this.logDebug(METHOD_NAME + " 2/2: key = " + key);
    return key;
  }

  /**
   * Creates the validity object.
   * Returns a {@link org.apache.excalibur.source.impl.validity.TimeStampValidity}
   * with the last modification time of the document.
   */

  public SourceValidity getValidity()
  {
    final String METHOD_NAME = "getValidity";
    this.logDebug(METHOD_NAME + " 1/2");
    try
      {
        long lastModified = this.document.getLastModified().getTime();
        this.logDebug(METHOD_NAME + " 2/2: lastModified = " + lastModified);
        return new TimeStampValidity(lastModified);
      }
    catch (Exception exception)
      {
        this.getLogger().warn(METHOD_NAME + ": Caught exception: " + exception);
      }
    return null;
  }
}
