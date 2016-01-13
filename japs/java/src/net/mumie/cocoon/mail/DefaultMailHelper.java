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

package net.mumie.cocoon.mail;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.IllegalWriteException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import net.mumie.cocoon.service.AbstractJapsService;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * <p>
 *   Default implementation of {@link MailHelper}.
 * </p>
 * <p>
 *   This class must be configured in cocoon.xonf to work properly. The configuration XML is
 *   "flat" in the sense that it consists of elements with text nodes only. The following
 *   elements are recognized:
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
 *       <td><code>smtp-host</code></td>
 *       <td>Name of the SMTP server</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr>
 *       <td><code>from-address</code></td>
 *       <td>Default e-mail address of the sender ("from" address)</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr>
 *       <td><code>from-name</code></td>
 *       <td>Default name attached to the "from" address</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr>
 *       <td><code>reply-address</code></td>
 *       <td>Default e-mail address where replies should go to ("reply" address)</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr>
 *       <td><code>reply-name</code></td>
 *       <td>Default name attached to the "reply" address</td>
 *       <td>Yes</td>
 *     </tr>
 *   </tbody>
 * </table>
 * <p>
 *   Example configuration:
 * </p>
 * <pre>
 *   &lt;mail-helper&gt;
 *     &lt;smtp-host&gt;mail.provider.org&lt;/smtp-host&gt;
 *     &lt;from-address&gt;admin@mumie.net&lt;/from-address&gt;
 *     &lt;from-name&gt;Japs Administration&lt;/from-name&gt;
 *     &lt;reply-address&gt;admin@mumie.net&lt;/reply-address&gt;
 *     &lt;reply-name&gt;Japs Administration&lt;/reply-name&gt;
 *   &lt;/mail-helper&gt;
 * </pre>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: DefaultMailHelper.java,v 1.8 2007/07/11 15:38:46 grudzin Exp $</code>
 */

public class DefaultMailHelper extends AbstractJapsService
  implements Configurable, Poolable, MailHelper
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(DefaultMailHelper.class);

  /**
   * The string <code>"smtp"</code> as a constant.
   */

  protected static final String SMTP = "smtp";

  /**
   * The SMTP host for outgoing mail
   */

  protected String smtpHost = null;

  /**
   * The name to attach to the "from" address
   */

  protected String fromName = null;

  /**
   * The "from" address
   */

  protected String fromAddress = null;

  /**
   * The name to attach to the reply address
   */

  protected String replyName = null;

  /**
   * The reply address
   */

  protected String replyAddress = null;

  /**
   * The mail session
   */

  protected Session session = null;

  // --------------------------------------------------------------------------------
  // Lifecycle methods
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>DefaultMailHelper</code>.
   */

  public DefaultMailHelper ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Configures this mail helper. See class decription for details.
   */

  public void configure (Configuration configuration)
    throws ConfigurationException
  {
    final String METHOD_NAME = "configure";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.smtpHost = configuration.getChild("smtp-host").getValue().trim();
    this.fromName = configuration.getChild("from-name").getValue().trim();
    this.fromAddress = configuration.getChild("from-address").getValue().trim();
    this.replyName = configuration.getChild("reply-name").getValue().trim();
    this.replyAddress = configuration.getChild("reply-address").getValue().trim();
    this.logDebug
      (METHOD_NAME + " 2/2: Done." +
       " smtpHost = " + this.smtpHost +
       ", fromName = " + this.fromName +
       ", fromAddress = " + this.fromAddress +
       ", replyName = " + this.replyName +
       ", replyAddress = " + this.replyAddress);
  }

  // --------------------------------------------------------------------------------
  // Mail session
  // --------------------------------------------------------------------------------

  /**
   * Initializes {@link #session} if it is <code>null</code>.
   */

  protected void ensureSession ()
  {
    final String METHOD_NAME = "ensureSession";
    if ( this.session == null )
      {
        this.logDebug(METHOD_NAME + " 1/2: Creating new session");
        Properties properties = new Properties();
        properties.put("mail.smtp.host", this.smtpHost);
        this.session = Session.getInstance(properties, null);
        this.logDebug(METHOD_NAME + " 2/2: Done. session = " + this.session);
      }
  }

  // --------------------------------------------------------------------------------
  // Creating messages
  // --------------------------------------------------------------------------------

  /**
   * Sets some message defaults.
   */

  protected void setMessageDefaults (Message message)
    throws MessagingException, IllegalWriteException, IllegalStateException,
           UnsupportedEncodingException 
  {
    Address from = new InternetAddress(this.fromAddress, this.fromName);
    Address replyTo = new InternetAddress(this.replyAddress, this.replyName);
    message.setFrom(from);
    message.setReplyTo(new Address[] {replyTo});
  }

  /**
   * Returns a new MIME message.
   */

  public MimeMessage newMimeMessage ()
    throws MailException
  {
    try
      {
        this.ensureSession();
        MimeMessage message = new MimeMessage(this.session);
        this.setMessageDefaults(message);
        return message;
      }
    catch (Exception exception)
      {
        throw new MailException(exception);
      }
  }

  // --------------------------------------------------------------------------------
  // Sending messages
  // --------------------------------------------------------------------------------

  /**
   * Sends the specified message.
   */

  public void sendMessage (Message message)
    throws MailException
  {
    try
      {
        final String METHOD_NAME = "sendMessage";
        this.logDebug(METHOD_NAME + " 1/2: Started. message = " + message);
        this.ensureSession();
        message.saveChanges();
        Transport transport = this.session.getTransport(SMTP);
        transport.connect();
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
        this.logDebug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
        throw new MailException(exception);
      }
  }

  // --------------------------------------------------------------------------------
  // Identification method
  // --------------------------------------------------------------------------------
  
  /**
   * Returns a string that identifies this instance. It has the
   * following form:<pre>
   *   "DefaultMailHelper" +
   *   '#' + instanceId
   *   '(' + lifecycleStatus
   *   ')'</pre>
   * where <code>instanceId</code> is the instance id and <code>lifecycleStatus</code> the
   * lifecycle status of this instance.
   */

  public String getIdentification ()
  {
    return
      "DefaultMailHelper" +
      '#' + this.instanceStatus.getInstanceId() +
      '(' + this.instanceStatus.getLifecycleStatus() +
      ')';
  }
}
