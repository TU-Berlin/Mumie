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

package net.mumie.cocoon.session;

import java.util.Date;
import javax.servlet.http.HttpSession;
import net.mumie.cocoon.notions.SessionAttrib;
import net.mumie.cocoon.notions.UseMode;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.notions.XMLNamespace;
import net.mumie.cocoon.pseudodocs.GeneralUser;
import net.mumie.cocoon.service.AbstractJapsServiceable;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.xml.GeneralXMLElement;
import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Default implementation of {@link SessionXMLizer}.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: DefaultSessionXMLizer.java,v 1.9 2007/10/11 11:07:21 rassy Exp $</code>
 */

public class DefaultSessionXMLizer extends AbstractJapsServiceable 
  implements Poolable, SessionXMLizer
{
  // --------------------------------------------------------------------------------
  // Global constants and variables
  // --------------------------------------------------------------------------------

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(DefaultSessionXMLizer.class);

  /**
   * Helper to write XML elements as SAX events.
   */

  protected GeneralXMLElement xmlElement =
    new GeneralXMLElement(XMLNamespace.URI_SESSION, XMLNamespace.PREFIX_SESSION);

  // --------------------------------------------------------------------------------
  // Lifecycle methods
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>DefaultSessionXMLizer</code> instance.
   */

  public DefaultSessionXMLizer ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  // --------------------------------------------------------------------------------
  // To-SAX methods
  // --------------------------------------------------------------------------------

  /**
   * Sends the specified Http session as SAX events to the specified content handler. If
   * <code>ownDocument</code>  is <code>true</code>, the <code>startDocument</code> and
   * <code>endDocument</code> methods are called before resp. after, otherwise, they are
   * suppressed.
   */

  public void sessionToSAX (HttpSession session,
                            ContentHandler contentHandler,
                            boolean ownDocument)
    throws SAXException
  {
    final String METHOD_NAME = "sessionToSAX";
    this.getLogger().debug
      (METHOD_NAME + " 1/2: Started. ownDocument = " + ownDocument);

    // Get session data:
    String sessionId = null;
    long sessionCreated = -1;
    long sessionLastAccessed = -1;
    Integer sessionUserId = null;
    try
      {
        sessionId = session.getId();
        sessionCreated = session.getCreationTime();
        sessionLastAccessed = session.getLastAccessedTime();
        sessionUserId = (Integer)session.getAttribute(SessionAttrib.USER);
      }
    catch (IllegalStateException exception)
      {
        this.getLogger().warn
          (METHOD_NAME + " Failed to retrieve session data", exception);
        return;
      }

    // Write session data to SAX stream:
    GeneralUser user = null;
    try
      {
        // Start document if necessary:
        if ( ownDocument )
          contentHandler.startDocument();

        // Start root element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.SESSION);
        this.xmlElement.addAttribute(XMLAttribute.ID, sessionId);
        this.xmlElement.startToSAX(contentHandler);

        // Write CREATED element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.CREATED);
        this.xmlElement.addAttribute(XMLAttribute.VALUE, new Date(sessionCreated));
        this.xmlElement.addAttribute(XMLAttribute.RAW, sessionCreated);
        this.xmlElement.toSAX(contentHandler);

        // Write LAST_ACCESSED element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.LAST_ACCESSED);
        this.xmlElement.addAttribute(XMLAttribute.VALUE, new Date(sessionLastAccessed));
        this.xmlElement.addAttribute(XMLAttribute.RAW, sessionLastAccessed);
        this.xmlElement.toSAX(contentHandler);

        // Write owner element if necessary:
        if ( sessionUserId != null )
          {
            this.xmlElement.reset();
            this.xmlElement.setLocalName(XMLElement.OWNER);
            this.xmlElement.startToSAX(contentHandler);

            user = (GeneralUser)this.serviceManager.lookup(GeneralUser.ROLE);
            user.setId(sessionUserId.intValue());
            user.setUseMode(UseMode.COMPONENT);
            user.toSAX(contentHandler, false);

            this.xmlElement.reset();
            this.xmlElement.setLocalName(XMLElement.OWNER);
            this.xmlElement.endToSAX(contentHandler);
          }

        // Close root element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.SESSION);
        this.xmlElement.endToSAX(contentHandler);

        // Close document if necessary:
        if ( ownDocument )
          contentHandler.endDocument();

        this.getLogger().debug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
        throw new SAXException(METHOD_NAME + ": Caught exception", exception);
      }
    finally
      {
        if ( user != null )
          this.serviceManager.release(user);
      }
  }

  /**
   * <p>
   *   Sends an XML representation of all currently active Http sessions as SAX events to
   *   the specified content handler. If <code>ownDocument</code> is <code>true</code>, the
   *   <code>startDocument</code> and <code>endDocument</code> methods are called before
   *   resp. after, otherwise, they are suppressed.
   * </p>
   * <p>
   *   The currently active sessions are requested from {@link SessionList}, which class
   *   holds a static list of sessions. The latter class should be registered as a listener
   *   in web.xml to work properly.
   * </p>
   */

  public void sessionsToSAX (ContentHandler contentHandler,
                             boolean ownDocument)
    throws SAXException
  {
    final String METHOD_NAME = "sessionsToSAX";
    this.getLogger().debug
      (METHOD_NAME + " 1/2: Started. ownDocument = " + ownDocument);

    // Start document if necessary:
    if ( ownDocument )
      contentHandler.startDocument();

    // Start root element:
    this.xmlElement.reset();
    this.xmlElement.setLocalName(XMLElement.SESSIONS);
    this.xmlElement.addAttribute(XMLAttribute.TOTAL, SessionList.getSize());
    this.xmlElement.startToSAX(contentHandler);

    // Write all active sessions to SAX:
    HttpSession[] sessions = SessionList.getSessions();
    for (int i = 0; i < sessions.length; i++)
      this.sessionToSAX(sessions[i], contentHandler, false);
    
    // Close root element:
    this.xmlElement.reset();
    this.xmlElement.setLocalName(XMLElement.SESSIONS);
    this.xmlElement.endToSAX(contentHandler);

    // Close document if necessary:
    if ( ownDocument )
      contentHandler.endDocument();

    this.getLogger().debug(METHOD_NAME + " 2/2: Done");
  }
}
