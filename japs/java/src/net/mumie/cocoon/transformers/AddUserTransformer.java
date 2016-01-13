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
import java.io.Reader;
import java.io.Serializable;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.Map;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.UseMode;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.notions.XMLNamespace;
import net.mumie.cocoon.pseudodocs.GeneralUser;
import net.mumie.cocoon.pseudodocs.SessionUser;
import net.mumie.cocoon.pseudodocs.User;
import net.mumie.cocoon.util.ParamUtil;
import net.mumie.cocoon.xml.MetaXMLElement;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.transformation.ServiceableTransformer;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.source.impl.validity.TimeStampValidity;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * <p>
 *   Adds a user to the dynamic data section. Recognizes the following
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
 *       <td><code>id</code></td>
 *       <td>The id of the user</td>
 *       <td>No. Defaults to the id of the user owning the session.</td>
 *     </tr>
 *     <tr>
 *       <td><code>use-mode</code></td>
 *       <td>The use mode, as numerical code</td>
 *       <td rowspan="2">No. Defaults to {@link UseMode#INFO INFO}.</td>
 *     </tr>
 *     <tr>
 *       <td><code>use-mode-name</code></td>
 *       <td>The use mode, as string name</td>
 *     </tr>
 *     <tr>
 *       <td><code>store-name</code></td>
 *       <td>The name of the {@link XMLElement#STORE STORE} element that wrappes the user
 *       (s.b.) </td>
 *       <td>No. Defaults to <code>"user"</code>.</td>
 *     </tr>
 *   </tbody>
 * </table>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: AddUserTransformer.java,v 1.14 2008/08/28 13:35:42 rassy Exp $</code>
 */

public class AddUserTransformer extends AbstractAddDynamicDataTransformer
    implements CacheableProcessingComponent
{
  /**
   * The user object of this transformer.
   */

  protected User user = null;

  /**
   * Name of the {@link XMLElement#STORE STORE} element that wrappes the user.
   */

  protected String storeName = null;

  /**
   * Calls the superclass <code>setup</code> method and sets the gloabl variables.
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

        int id = ParamUtil.getFirstTokenAsInt(parameters, "id", Id.UNDEFINED);
        int useMode =
          ParamUtil.getAsUseMode(parameters, "use-mode", "use-mode-name", UseMode.INFO);
        this.storeName = ParamUtil.getAsString(parameters, "store-name", "user");

        this.getLogger().debug
          (METHOD_NAME + " 2/3:" + 
           " id = " + id +
           ", useMode = " + useMode);

        // Init the user object:
        if ( id == Id.UNDEFINED )
          this.user = (User)this.manager.lookup(SessionUser.ROLE);
        else
          {
            this.user = (User)this.manager.lookup(GeneralUser.ROLE);
            this.user.setId(id);
          }
        this.user.setUseMode(useMode);

        this.getLogger().debug
          (METHOD_NAME + " 3/3: Done." +
           " this.user = " + this.user +
           ", this.storeName = " + this.storeName);
      }
    catch (Exception exception)
      {
	throw new ProcessingException(exception);
      }
  }

  /**
   * Sends the content of the dynamic data element to SAX.
   */

  protected void dynamicDataElementContentToSAX ()
    throws SAXException
  {
    final String METHOD_NAME = "dynamicDataElementContentToSAX";
    this.getLogger().debug
      (METHOD_NAME + " 1/2: " +
       "this.user = " + this.user +
       ", this.storeName = " + this.storeName);

    // Start STORE element:
    this.metaXMLElement.reset();
    this.metaXMLElement.setLocalName(XMLElement.STORE);
    this.metaXMLElement.addAttribute(XMLAttribute.NAME, this.storeName);
    this.metaXMLElement.startToSAX(this.contentHandler);

    // Send user to SAX:
    this.user.toSAX(this.contentHandler, false);

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
   *   <li>the user id,</li>
   *   <li>the use mode, as numerical code,</li>
   *   <li>the store name ({@link #storeName storeName}).</li>
   * </ul>
   * <p>
   *   Example:<pre>
   *     23-0-user
   *   </pre>
   * </p>
   */

  public Serializable getKey()
  {
    final String METHOD_NAME = "getKey";
    this.getLogger().debug(METHOD_NAME + " 1/2");
    String key =
      this.user.getId() + "-" + this.user.getUseMode() + "-" + this.storeName; 
    this.getLogger().debug(METHOD_NAME + " 2/2: key = " + key);
    return key;
  }

  /**
   * Creates the validity object.
   * Returns a {@link TimeStampValidity TimeStampValidity}
   * with the last modification time of the user.
   */

  public SourceValidity getValidity()
  {
    final String METHOD_NAME = "getValidity";
    this.getLogger().debug(METHOD_NAME + " 1/2");
    try
      {
        long lastModified = this.user.getLastModified().getTime();
        this.getLogger().debug(METHOD_NAME + " 2/2: lastModified = " + lastModified);
        return new TimeStampValidity(lastModified);
      }
    catch (Exception exception)
      {
	this.getLogger().warn(METHOD_NAME + ": Caught exception: " + exception);
      }
    return null;
  }

  /**
   * Recycles this transformer.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");
    super.recycle();
    if ( this.user != null )
      {
        this.manager.release(this.user);
        this.user = null;
      }
    this.storeName = null;
    this.getLogger().debug(METHOD_NAME + " 2/2: Done");
  }
}
