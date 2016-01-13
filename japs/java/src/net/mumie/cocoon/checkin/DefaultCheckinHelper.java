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

package net.mumie.cocoon.checkin;

import java.io.FileReader;
import java.sql.SQLException;
import java.util.Arrays;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.event.CheckinEvent;
import net.mumie.cocoon.notions.EventName;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.pseudodocs.SessionUser;
import net.mumie.cocoon.pseudodocs.User;
import net.mumie.cocoon.service.AbstractJapsServiceable;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceSelectorWrapper;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.util.LogUtil;
import net.mumie.cocoon.util.PasswordEncryptor;
import net.mumie.cocoon.xml.MetaXMLElement;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.excalibur.xml.dom.DOMParser;
import org.w3c.dom.Element;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DefaultCheckinHelper extends AbstractJapsServiceable
  implements CheckinHelper, Configurable, Recyclable, Disposable
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(DefaultCheckinHelper.class);

  /**
   * The content objects to check-in.
   */

  protected ContentObjectToCheckin[] toCheckIns = null;

  /**
   * Selector for {@link ContentObjectToCheckin ContentObjectToCheckin}'s.
   */

  protected ServiceSelectorWrapper toCheckinSelector = null;

  /**
   * A {@link DOMMaster DOMMaster} that contains the defaults for the checkin data.
   */

  protected DOMMaster defaults = null;

  /**
   * Comparator to sort {@link ContentObjectToCheckin ContentObjectToCheckin}'s.
   */

  protected CheckinComparator checkinComarator = new CheckinComparator();

  /**
   * Auxiliary to write XML elements.
   */

  protected MetaXMLElement xmlElement = new MetaXMLElement();

  // --------------------------------------------------------------------------------
  // Lifecycle related methods
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>DefaultCheckinHelper</code> instance.
   */

  public DefaultCheckinHelper ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Configures this instance.
   */

  public void configure (Configuration configuration)
    throws ConfigurationException
  {
    final String METHOD_NAME = "configuration";
    this.logDebug(METHOD_NAME + " 1/3: Started");
    String defaultsFilename = configuration.getChild("defaults-file").getValue().trim();
    this.logDebug
      (METHOD_NAME + " 2/3: defaultsFilename = " + defaultsFilename);
    DOMParser domParser = null;
    try
      {
        domParser = (DOMParser)this.serviceManager.lookup(DOMParser.ROLE);
        FileReader reader = new FileReader(defaultsFilename);
        Element rootElement =
          domParser.parseDocument(new InputSource(reader)).getDocumentElement();
        this.defaults = new DOMMaster(rootElement);
      }
    catch (Exception exception)
      {
        throw new ConfigurationException
          ("Error while configuring instance", exception);
      }
    finally
      {
        if ( domParser != null )
          this.serviceManager.release(domParser);
      }
    this.logDebug
      (METHOD_NAME + " 3/3: Done");
  }

  /**
   * Resets all global variables except thoses which are constant or represent a service to
   * their initial values.
   */

  protected void resetVariables ()
  {
    // Currently nothing to do
  }

  /**
   * Releases the services hold by this instance.
   */

  protected void releaseServices ()
  {
    final String METHOD_NAME = "releaseServices";
    this.logDebug(METHOD_NAME + " 1/2: Started");

    // Releasing toCheckIns:
    if ( this.toCheckIns != null )
      {
        if ( this.toCheckinSelector == null || !this.toCheckinSelector.hasServiceSelector() )
          this.logWarn("Can not release toCheckIns: no service selector");
        else
          {
            for (int i = 0; i < this.toCheckIns.length; i++)
              this.toCheckinSelector.release(this.toCheckIns[i]);
          }
        this.toCheckIns = null;
      }

    // Releasing toCheckinSelector:
    if ( this.toCheckinSelector != null && this.toCheckinSelector.hasServiceSelector() )
      {
        this.serviceManager.release(this.toCheckinSelector.unwrapServiceSelector());
      }

    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Recycles this instance.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.releaseServices();
    this.resetVariables();
    this.instanceStatus.notifyRecycle();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Disposes this instance.
   */

  public void dispose ()
  {
    final String METHOD_NAME = "dispose";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.releaseServices();
    this.resetVariables();
    this.defaults = null;
    this.instanceStatus.notifyDispose();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // "Ensure" method for the toCheckinSelector
  // --------------------------------------------------------------------------------

  /**
   * Initializes {@link #toCheckinSelector toCheckinSelector} if it is <code>null</code>.
   */

  protected void ensureToCheckinSelector ()
    throws ServiceException
  {
    // Get a service selector wrapper if necessary:
    if ( this.toCheckinSelector == null )
      {
        this.logDebug("ensureToCheckinSelector: Instanciating a ServiceSelectorWrapper");
        String label = "DefaultCheckinHelper#" + this.instanceStatus.getInstanceId();
        this.toCheckinSelector = new ServiceSelectorWrapper
          (label, this.getLogger().getChildLogger(label));
      }

    // If necessary, get a selector and wrap it:
    if ( !this.toCheckinSelector.hasServiceSelector() )
      this.toCheckinSelector.wrap
        ((ServiceSelector)this.serviceManager.lookup(ContentObjectToCheckin.ROLE + "Selector"));
  }

  // --------------------------------------------------------------------------------
  // Checkin
  // --------------------------------------------------------------------------------

  /**
   * Checks-in the documents and/or pseudo-documents in the specified checkin repository.
   */

  public void checkin (CheckinRepository checkinRepository)
    throws CheckinException
  {
    final String METHOD_NAME = "checkin";
    this.logDebug
      (METHOD_NAME + " 1/5: Started." +
       " checkinRepository = " + LogUtil.identify(checkinRepository));

    DbHelper dbHelper = null;
    User user = null;
    PasswordEncryptor encryptor = null;

    // Path of the currently processed content object, or null if none
    // (used for error messages).
    String currentPath = null;

    try
      {
        // Get master paths:
        String[] masterPaths = checkinRepository.getMasterPaths();
        this.logDebug
          (METHOD_NAME + " 2/5: masterPaths = " + LogUtil.arrayToString(masterPaths));

        // Build list of ContentObjectToCheckin's:
        this.toCheckIns = new ContentObjectToCheckin[masterPaths.length];
        this.ensureToCheckinSelector();
        for (int i = 0; i < this.toCheckIns.length; i++)
          {
            String masterPath = masterPaths[i];
            Master master = checkinRepository.getMaster(masterPath);
            Content content = checkinRepository.getContentForMaster(masterPath);
            Source source = checkinRepository.getSourceForMaster(masterPath);
            String typeName = master.getTypeName();
            toCheckIns[i] = (ContentObjectToCheckin)toCheckinSelector.select(typeName);
            toCheckIns[i].setup(master, content, source, masterPath);
            this.logDebug
              (METHOD_NAME + " 3." + i + "/5: Prepared:" +
               " toCheckIns[" + i +"] = " + LogUtil.identify(toCheckIns[i]));
          }

        // Sort list of ContentObjectToCheckin's:
        Arrays.sort(this.toCheckIns, this.checkinComarator);
    
        // Init dbHelper, user, and encryptor:
        dbHelper = (DbHelper)this.serviceManager.lookup(DbHelper.ROLE);
        user = (User)this.serviceManager.lookup(SessionUser.ROLE);
        encryptor = (PasswordEncryptor)this.serviceManager.lookup(PasswordEncryptor.ROLE);

        // Start transaction:
        dbHelper.beginTransaction(this, true);

        // Carry out the actual checkin:
        for (int stage = 1; stage <= 4; stage++)
          {
            for (int k = 0; k < toCheckIns.length; k++)
              {
                ContentObjectToCheckin toCheckIn = toCheckIns[k];
                currentPath = toCheckIn.getPath();
                toCheckIn.checkin(stage, dbHelper, user, encryptor, this.defaults);
                this.logDebug
                  (METHOD_NAME + " 4." + stage + "." + k + "/5: Checked in:" +
                   " stage = " + stage +
                   ", toCheckIns[" + k +"] = " + LogUtil.identify(toCheckIns[k]));
              }
          }

        // End transaction:
        dbHelper.endTransaction(this);

        // Call event handler:
        this.callEventHandler(EventName.CHECKIN, new CheckinEvent(toCheckIns));

        this.logDebug(METHOD_NAME + " 5/5: Done");
      }
    catch (Exception exception)
      {
        try
          {
            dbHelper.abortTransaction(this);
          }
        catch(SQLException e)
          {
            throw new CheckinException(this.getIdentification(), exception);
          }
        this.logError(this.getIdentification() + ": " + METHOD_NAME, exception);

        throw new CheckinException
          ((currentPath != null ? currentPath : "Checkin failed") +
           ": " +
           LogUtil.unwrapThrowable(exception).getMessage());
      }
    finally
      {
        if ( encryptor != null )
          this.serviceManager.release(encryptor);

        if ( user != null )
          this.serviceManager.release(user);

        if ( dbHelper != null )
          {
            try
              {
                if ( dbHelper.hasTransactionLocked() )
                  {
                    this.logDebug(METHOD_NAME + "WARNING: aborting transaction.");
                    dbHelper.abortTransaction(this);
                  }
                this.serviceManager.release(dbHelper);
              }
            catch (Exception exception)
              {
                throw new CheckinException
                  (this.getIdentification() + ": Abort transaction failed", exception);
              }
          }
      }
  }

  // --------------------------------------------------------------------------------
  // To-SAX methods
  // --------------------------------------------------------------------------------

  /**
   * Sends a checkin report in XML form as SAX evenys to the specified content handler. If
   * <code>ownDocument</code> is <code>true</code>, the <code>startDocument</code> and
   * <code>endDocument</code> methods of the content handler are called before
   * resp. after. Otherwise, these calles are suppressed.
   */

  public void toSAX (ContentHandler contentHandler, boolean ownDocument)
    throws SAXException 
  {
    final String METHOD_NAME = "toSAX";
    this.logDebug(METHOD_NAME + " 1/2: Started");

    if ( ownDocument )
      contentHandler.startDocument();

    // Start tag:
    this.xmlElement.reset();
    this.xmlElement.setLocalName(XMLElement.CHECKIN_REPORT);
    this.xmlElement.startToSAX(contentHandler);

    // Content (list of to-check-in's):
    if ( this.toCheckIns != null )
      {
        for (int i = 0; i < this.toCheckIns.length; i++)
          this.toCheckIns[i].toSAX(contentHandler, false);
      }

    // End tag:
    this.xmlElement.reset();
    this.xmlElement.setLocalName(XMLElement.CHECKIN_REPORT);
    this.xmlElement.endToSAX(contentHandler);

    if ( ownDocument )
      contentHandler.endDocument();
  }

  /**
   * Same as {@link #toSAX(ContentHandler,boolean) toSAX(contentHandler, true)}.
   */

  public void toSAX (ContentHandler contentHandler)
    throws SAXException
  {
    this.toSAX(contentHandler, true);
  }

  // --------------------------------------------------------------------------------
  // Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * Writes an XML representation of the specified exception as SAX events to the specified
   * content handler.
   */

  protected void exceptionToSAX (Exception exception,
                                 ContentHandler contentHandler)
    throws SAXException 
  {
    this.xmlElement.reset();
    this.xmlElement.setLocalName(XMLElement.ERROR);
    this.xmlElement.setText(LogUtil.unwrapThrowable(exception).getMessage());
    this.xmlElement.toSAX(contentHandler);
  }

  // --------------------------------------------------------------------------------
  // Identification method
  // --------------------------------------------------------------------------------
  
  /**
   * Returns a string that identifies this instance. It has the
   * following form:<pre>
   *   "DefaultCheckinHelper" +
   *   '#' + instanceId
   *   '(' + lifecycleStatus
   *   ')'</pre>
   * where <code>instanceId</code> is the instance id and <code>lifecycleStatus</code> the
   * lifecycle status of this instance.
   */

  public String getIdentification ()
  {
    return
      "DefaultCheckinHelper" +
      '#' + this.instanceStatus.getInstanceId() +
      '(' + this.instanceStatus.getLifecycleStatus() +
      ')';
  }
}
