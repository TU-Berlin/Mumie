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
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.notions.XMLNamespace;
import net.mumie.cocoon.xml.MetaXMLElement;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.transformation.ServiceableTransformer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.apache.cocoon.ProcessingException;
import org.apache.avalon.framework.parameters.Parameters;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import org.apache.avalon.framework.parameters.ParameterException;
import net.mumie.cocoon.notions.TimeFormat;
import net.mumie.cocoon.util.ParamUtil;
import net.mumie.cocoon.clienttime.ClientTime;

/**
 * <p>
 *   Adds the current time to the dynamic data section. Recognizes the following
 *   parameter: 
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
 *       <td><code>time-format</code></td>
 *       <td>The pattern that describes the format of the <code>current_time</code> parameter
 *       in the dynamic data section (s.b.). For the syntax of the pattern, see
 *       {@link SimpleDateFormat SimpleDateFormat}.</td>
 *       <td>No. Defaults to {@link TimeFormat#DEFAULT TimeFormat.DEFAULT}</td>
 *     </tr>
 *     <tr>
 *       <td><code>use-client-time</code></td>
 *       <td>Whether the client time offset is added to the current time.</td>
 *       <td>No. Defaults to false</td>
 *     </tr>
 *   </tbody>
 * </table>
 * <p>
 *   Sets the following parameters in the dynamic data section:
 * </p>
 * <table class="genuine indented" style="width:60em">
 *   <thead>
 *     <tr>
 *       <td>Name</td>
 *       <td>Description</td>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td><code>current_time_is_client_time</code></td>
 *       <td>Whether the time is client time (as opposed to server time). "true" or "false".</td>
 *     </tr>
 *     <tr>
 *       <td><code>current_time_format</code></td>
 *       <td>The pattern that describes the format of the <code>current_time</code>
 *       parameter. If the <code>time-format</code> transformer parameter is set, this is
 *       equal to that parameter, otherwise to its default, i.e.,
 *       {@link TimeFormat#DEFAULT TimeFormat.DEFAULT}. For the syntax of the pattern, see
 *       {@link SimpleDateFormat SimpleDateFormat}.</td>
 *     </tr>
 *     <tr>
 *       <td><code>current_time</code></td>
 *       <td>The current time, formatted according to <code>current_time_format</code>.</td>
 *     </tr>
 *     <tr>
 *       <td><code>current_time_raw</code></td>
 *       <td>The current time, as the number of milliseconds since January 1, 1970 00:00:00
 *       GMT.</td>
 *     </tr>
 *   </tbody>
 * </table>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: AddCurrentTimeTransformer.java,v 1.6 2009/10/02 22:24:53 rassy Exp $</code>
 */

public class AddCurrentTimeTransformer extends AbstractAddDynamicDataTransformer
{
  /**
   * Writes the time to the dynamic data section.
   */

  protected void dynamicDataElementContentToSAX ()
    throws SAXException
  {
    final String METHOD_NAME = "dynamicDataElementContentToSAX()";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");
    ClientTime clientTime = null;

    try
      {
        String currentTimeFormat = ParamUtil.getAsString(this.parameters, "time-format", TimeFormat.DEFAULT);
        DateFormat dateFormat = new SimpleDateFormat(currentTimeFormat);
        dateFormat.setLenient(false);

        boolean useClientTime = ParamUtil.getAsBoolean(this.parameters, "use-client-time", false);

        long currentTimeRaw = -1;
        if ( useClientTime )
          {
            clientTime = (ClientTime)this.manager.lookup(ClientTime.ROLE);
            currentTimeRaw = clientTime.getTime();
          }
        else
          currentTimeRaw = System.currentTimeMillis();

        String currentTime = dateFormat.format(new Date(currentTimeRaw));

        this.getLogger().debug
          (METHOD_NAME + " 2/3:" +
           " useClientTime = " + useClientTime +
           ", currentTimeRaw = " + currentTimeRaw +
           ", currentTimeFormat = " + currentTimeFormat +
           ", currentTime = " + currentTime);

        this.metaXMLElement.reset();
        this.metaXMLElement.setLocalName(XMLElement.PARAM);
        this.metaXMLElement.addAttribute(XMLAttribute.NAME, "current_time_is_client_time");
        this.metaXMLElement.addAttribute(XMLAttribute.VALUE, useClientTime);
        this.metaXMLElement.toSAX(this.contentHandler);

        this.metaXMLElement.reset();
        this.metaXMLElement.setLocalName(XMLElement.PARAM);
        this.metaXMLElement.addAttribute(XMLAttribute.NAME, "current_time_raw");
        this.metaXMLElement.addAttribute(XMLAttribute.VALUE, currentTimeRaw);
        this.metaXMLElement.toSAX(this.contentHandler);

        this.metaXMLElement.reset();
        this.metaXMLElement.setLocalName(XMLElement.PARAM);
        this.metaXMLElement.addAttribute(XMLAttribute.NAME, "current_time_format");
        this.metaXMLElement.addAttribute(XMLAttribute.VALUE, currentTimeFormat);
        this.metaXMLElement.toSAX(this.contentHandler);

        this.metaXMLElement.reset();
        this.metaXMLElement.setLocalName(XMLElement.PARAM);
        this.metaXMLElement.addAttribute(XMLAttribute.NAME, "current_time");
        this.metaXMLElement.addAttribute(XMLAttribute.VALUE, currentTime);
        this.metaXMLElement.toSAX(this.contentHandler);

        this.getLogger().debug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
        throw new SAXException(exception);
      }
    finally
      {
        if ( clientTime != null ) this.manager.release(clientTime);
      }
  }
}
