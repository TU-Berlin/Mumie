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
import java.text.SimpleDateFormat;
import java.util.Map;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.TimeFormat;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.pseudodocs.SessionUser;
import net.mumie.cocoon.util.ParamUtil;
import net.mumie.cocoon.util.UserProblemData;
import net.mumie.cocoon.xml.MetaXMLElement;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.excalibur.source.SourceValidity;
import org.xml.sax.SAXException;

/**
 * <p>
 *   Adds the data sheet for a problem to the dynamic data section. Recognizes the following
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
 *       <td><code>problem</code></td>
 *       <td>Id of the &ndash; real &ndash; problem</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr>
 *       <td><code>ref</code></td>
 *       <td>Id of the worksheet - generic_problem reference.</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr>
 *       <td><code>user</code></td>
 *       <td>Id of the user to which the data sheet belongs.</td>
 *       <td>No. Default is the user owning the session.</td>
 *     </tr>
 *     <tr>
 *       <td><code>with-common-data</code></td>
 *       <td><code>"yes"</code> or <code>"no"</code>. Specifies whether the
 *       common data should be included.</td>
 *       <td>No. Default is <code>"no"</code>.</td>
 *     </tr>
 *     <tr>
 *       <td><code>with-personalized-data</code></td>
 *       <td><code>"yes"</code> or <code>"no"</code>. Specifies whether the
 *       personalized data should be included.</td>
 *       <td>No. Default is <code>"yes"</code>.</td>
 *     </tr>
 *     <tr>
 *       <td><code>with-answers</code></td>
 *       <td><code>"yes"</code> or <code>"no"</code>. Specifies whether the
 *       answers should be included.</td>
 *       <td>No. Default is <code>"yes"</code>.</td>
 *     </tr>
 *     <tr>
 *       <td><code>with-correction</code></td>
 *       <td><code>"yes"</code> or <code>"no"</code>. Specifies whether the
 *       correction/marking should be included.</td>
 *       <td>No. Default is <code>"no"</code>.</td>
 *     </tr>
 *     <tr>
 *       <td><code>resolve-ppd</code></td>
 *       <td><code>"yes"</code> or <code>"no"</code>. Specifies whether references to
 *       personalized data in the common data should be resolved or not.</td>
 *       <td>No. Default is <code>"yes"</code>.</td>
 *     </tr>
 *     <tr>
 *       <td><code>remove-solutions</code></td>
 *       <td><code>"yes"</code> or <code>"no"</code>. Specifies whether the
 *         <code>common/solution</code> part is removed or not.</td>
 *       <td>No. Default is <code>"no"</code>.</td>
 *     </tr>
 *     <tr>
 *       <td><code>wrap-by-meta</code></td>
 *       <td><code>"yes"</code> or <code>"no"</code>. Specifies whether the datasheet should
 *       be wrapped by XML from the metainfo namespace.</td>
 *       <td>No. Default is <code>"yes"</code>.</td>
 *     </tr>
 *     <tr>
 *       <td><code>time-format</code></td>
 *       <td>A time format string as described with the
 *       {@link SimpleDateFormat SimpleDateFormat} class. Controlles the output of the
 *       last modification times</td>
 *       <td>No. Default is {@link TimeFormat#DEFAULT TimeFormat.DEFAULT}.</td>
 *     </tr>
 *   </tbody>
 * </table>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: AddProblemDataSheetTransformer.java,v 1.19 2008/05/15 12:11:52 rassy Exp $</code>
 */

public class AddProblemDataSheetTransformer extends AbstractAddDynamicDataTransformer
  implements CacheableProcessingComponent 
{
  /**
   * The problem data.
   */

  protected UserProblemData userProblemData = null;

  /**
   * Calls the superclass <code>setup</code> method and initializes
   * {@link #userProblemData userProblemData}.
   */

  public void setup (SourceResolver resolver,
                     Map objectModel,
                     String source,
                     Parameters parameters)
    throws ProcessingException
  {
    try
      {
        final String METHOD_NAME = "setup";
        this.getLogger().debug(METHOD_NAME + " 1/2: Started");
        super.setup(resolver, objectModel, source, parameters);

        // Init UserProblemData instance:
        this.userProblemData =
          (UserProblemData)this.manager.lookup(UserProblemData.ROLE);

        // Setup userProblemData: 
        this.userProblemData.setup
          (ParamUtil.getAsInt(parameters, "problem"),
           ParamUtil.getAsInt(parameters, "ref"),
           ParamUtil.getAsInt(parameters, "user", this.getSessionUserId()));

        // Configure output of userProblemData:
        this.userProblemData.configureOutput
          (ParamUtil.getAsBoolean(parameters, "with-common-data", false),
           ParamUtil.getAsBoolean(parameters, "with-personalized-data", true),
           ParamUtil.getAsBoolean(parameters, "with-answers", true),
           ParamUtil.getAsBoolean(parameters, "with-correction", false),
           ParamUtil.getAsBoolean(parameters, "resolve-ppd", true),
           ParamUtil.getAsBoolean(parameters, "remove-solutions", false),
           ParamUtil.getAsBoolean(parameters, "wrap-by-meta", true),
           ParamUtil.getAsString(parameters, "time-format", null));

        this.getLogger().debug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
	throw new ProcessingException(exception);
      }
  }

  /**
   * Returns the id of the user owning the session.
   */

  protected int getSessionUserId ()
    throws ProcessingException 
  {
    final String METHOD_NAME = "getSessionUserId";
    SessionUser user = null;
    try
      {
        user = (SessionUser)this.manager.lookup(SessionUser.ROLE);
        int userId = user.getId();
        this.getLogger().debug(METHOD_NAME + " 1/1: userId = " + userId);
        return userId;
      }
    catch (Exception exception)
      {
	throw new ProcessingException(exception);
      }
    finally
      {
        if ( user != null ) this.manager.release(user);
      }
  }

  /**
   * Sends the content of the dynamic data element to SAX.
   */

  protected void dynamicDataElementContentToSAX ()
    throws SAXException
  {
    final String METHOD_NAME = "dynamicDataElementContentToSAX";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");

    try
      {
        // Start STORE element:
        this.metaXMLElement.reset();
        this.metaXMLElement.setLocalName(XMLElement.STORE);
        this.metaXMLElement.addAttribute(XMLAttribute.NAME, "data");
        this.metaXMLElement.startToSAX(this.contentHandler);

        // Send problem data to SAX:
        this.userProblemData.toSAX(this.contentHandler, false);

        // Close STORE element:
        this.metaXMLElement.reset();
        this.metaXMLElement.setLocalName(XMLElement.STORE);
        this.metaXMLElement.endToSAX(this.contentHandler);

        this.getLogger().debug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
        throw new SAXException(exception);
      }
  }

  /**
   * Releases the global services of this <code>AddProblemDataSheetTransformer</code>.
   */

  protected void releaseServices ()
  {
    final String METHOD_NAME = "releaseServices";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");
    if ( this.userProblemData != null )
      {
        this.manager.release(this.userProblemData);
        this.userProblemData = null;
      }
    this.getLogger().debug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Returns the validity object.
   */

  public SourceValidity getValidity ()
  {
    return this.userProblemData.getValidity();
  }

  /**
   * Creates the caching key.
   */

  public Serializable getKey()
  {
    return this.userProblemData.getKey();
  }
}
