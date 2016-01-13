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

package net.mumie.cocoon.actions;

import java.util.HashMap;
import java.util.Map;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import net.mumie.cocoon.mail.MailHelper;
import net.mumie.cocoon.util.LogUtil;
import net.mumie.cocoon.util.ParamUtil;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;

public class QFAppletAction extends ServiceableAction
  implements Configurable
{
  // --------------------------------------------------------------------------------
  // Global constants and variables
  // --------------------------------------------------------------------------------

  /**
   * The address where the reports are sent to.
   */

  Address toAddress = null;

  // --------------------------------------------------------------------------------
  // Lifecycle related methods
  // --------------------------------------------------------------------------------

  /**
   * Configures this instance. Simply sets the "to" address.
   */

  public void configure (Configuration configuration)
    throws ConfigurationException 
  {
    final String METHOD_NAME = "configuration";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");
    try
      {
        String toName = configuration.getChild("to-name").getValue();
        String toAddressRaw = configuration.getChild("to-address").getValue();
        this.toAddress = new InternetAddress(toAddressRaw, toName);
        this.getLogger().debug
          (METHOD_NAME + " 2/2: Done" +
           " toName = " + toName +
           ", toAddressRaw = " + toAddressRaw);
      }
    catch (Exception exception)
      {
        throw new ConfigurationException("Wrapped exception", exception);
      }
  }

  // --------------------------------------------------------------------------------
  // Act method
  // --------------------------------------------------------------------------------

  /**
   * See class description.
   */

  public Map act (Redirector redirector,
                  SourceResolver resolver,
                  Map objectModel,
                  String source, 
                  Parameters parameters)
    throws ProcessingException
  {
    final String METHOD_NAME = "act";
    this.getLogger().debug(METHOD_NAME + " 1/3: Started");
    MailHelper mailHelper = null;
    try
      {
        // Get subject and report:
        String subject = ParamUtil.getAsString(parameters, "subject");
        String report = ParamUtil.getAsString(parameters, "report");

        this.getLogger().debug
          (METHOD_NAME + " 2/3:" +
           " subject = " + subject +
           ", report.length() = " + report.length());

        // Init mail helper:
        mailHelper = (MailHelper)this.manager.lookup(MailHelper.ROLE);

        // Compose message:
        MimeMessage message = mailHelper.newMimeMessage();
        message.setSubject(subject);
        message.setText(report);
        message.setRecipient(Message.RecipientType.TO, this.toAddress);

        // Send message:
        mailHelper.sendMessage(message);

        // Create map to return:
        Map map = new HashMap();
        map.put("status", "OK");

        this.getLogger().debug(METHOD_NAME + " 3/3: Done");

        return map;
      }
    catch (Exception exception)
      {
        throw new ProcessingException(exception);
      }
    finally
      {
        if ( mailHelper != null )
          this.manager.release(mailHelper);
      }
  }
}
