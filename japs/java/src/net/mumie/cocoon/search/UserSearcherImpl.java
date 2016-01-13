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

package net.mumie.cocoon.search;
import java.sql.ResultSet;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.pseudodocs.GeneralUser;
import net.mumie.cocoon.pseudodocs.User;
import net.mumie.cocoon.service.AbstractJapsServiceable;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.util.ParamUtil;
import net.mumie.cocoon.xml.MetaXMLElement;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.cocoon.ProcessingException;
import org.xml.sax.ContentHandler;

public class UserSearcherImpl extends AbstractJapsServiceable
  implements Recyclable, Disposable, UserSearcher
{
  // --------------------------------------------------------------------------------
  // h1: Global constants and variables
  // --------------------------------------------------------------------------------

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(UserSearcherImpl.class);

  /**
   * Helper to create XML elements
   */

  protected MetaXMLElement metaXMLElement = new MetaXMLElement();

  // --------------------------------------------------------------------------------
  // h1: Lifecycle related methods
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>UserSearcherImpl</code> instance.
   */

  public UserSearcherImpl ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Resets this instnace. Does only one thing: Resets the meta XML element writer
   * ({@link #xmlElement xmlElement}).
   */

  protected void reset ()
  {
    this.metaXMLElement.reset();
  }

  /**
   * Recycles this instance. Calls the {@link #reset reset} method.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.reset();
    this.instanceStatus.notifyRecycle();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Disposes this instance. Only calls the {@link #reset reset} method.
   */

  public void dispose ()
  {
    final String METHOD_NAME = "dispose";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.reset();
    this.instanceStatus.notifyDispose();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // h1: Searching
  // --------------------------------------------------------------------------------

  /**
   * Searches for user(s) and writes the result as XML to the specified content handler.
   */

  public void search (int id, String loginName, String firstName, String surname,
                      boolean requestOnly,
                      int useMode,
                      boolean withPath,
                      ContentHandler contentHandler,
                      boolean ownDocument)
    throws ProcessingException
  {
    final String METHOD_NAME = "generate";
    this.logDebug
      (METHOD_NAME + " 1/2: Started." +
       " id = " + id +
       ", loginName = " + loginName +
       ", firstName = " + firstName +
       ", surname = " + surname +
       ", requestOnly = " + requestOnly +
       ", useMode = " + useMode +
       ", withPath = " + withPath +
       ", ownDocument = " + ownDocument);

    DbHelper dbHelper = null;
    User user = null;

    try
      {
        // Start document if necessary:
	if ( ownDocument ) contentHandler.startDocument();

        // Start root element:
        this.metaXMLElement.reset();
        this.metaXMLElement.setLocalName(XMLElement.USER_SEARCH);
        this.metaXMLElement.startToSAX(contentHandler);

        // Start request element:
        this.metaXMLElement.reset();
        this.metaXMLElement.setLocalName(XMLElement.REQUEST);
        this.metaXMLElement.startToSAX(contentHandler);

        // Write id element if necessary:
        if ( id != Id.UNDEFINED )
          {
            this.metaXMLElement.reset();
            this.metaXMLElement.setLocalName(XMLElement.ID);
            this.metaXMLElement.setText(id);
            this.metaXMLElement.toSAX(contentHandler);
          }

        // Write login name element if necessary:
        if ( loginName != null )
          {
            this.metaXMLElement.reset();
            this.metaXMLElement.setLocalName(XMLElement.LOGIN_NAME);
            this.metaXMLElement.setText(loginName);
            this.metaXMLElement.toSAX(contentHandler);
          }

        // Write first name element if necessary:
        if ( firstName != null )
          {
            this.metaXMLElement.reset();
            this.metaXMLElement.setLocalName(XMLElement.FIRST_NAME);
            this.metaXMLElement.setText(firstName);
            this.metaXMLElement.toSAX(contentHandler);
          }

        // Write surname element if necessary:
        if ( surname != null )
          {
            this.metaXMLElement.reset();
            this.metaXMLElement.setLocalName(XMLElement.SURNAME);
            this.metaXMLElement.setText(surname);
            this.metaXMLElement.toSAX(contentHandler);
          }

        // Close request element:
        this.metaXMLElement.reset();
        this.metaXMLElement.setLocalName(XMLElement.REQUEST);
        this.metaXMLElement.endToSAX(contentHandler);

        if ( !requestOnly )
          {
            // Setup db helper and user object:
            dbHelper = (DbHelper)this.serviceManager.lookup(DbHelper.ROLE);
            user = (User)this.serviceManager.lookup(GeneralUser.ROLE);

            // Search:
            user.setUseMode(useMode);
            user.setWithPath(withPath);
            ResultSet resultSet =
              dbHelper.searchUsers(id, loginName, firstName, surname, user.getDbColumns());

            // Start result element:
            this.metaXMLElement.reset();
            this.metaXMLElement.setLocalName(XMLElement.RESULT);
            this.metaXMLElement.startToSAX(contentHandler);

            // Write search result to SAX:
            while ( resultSet.next() )
              {
                user.reset();
                user.setId(resultSet.getInt(DbColumn.ID));
                user.setUseMode(useMode);
                user.setWithPath(withPath);
                user.toSAX(resultSet, contentHandler, false);
              }

            // Close result element:
            this.metaXMLElement.reset();
            this.metaXMLElement.setLocalName(XMLElement.RESULT);
            this.metaXMLElement.endToSAX(contentHandler);
          }

        // Close root element:
        this.metaXMLElement.reset();
        this.metaXMLElement.setLocalName(XMLElement.PSEUDO_DOCUMENTS);
        this.metaXMLElement.endToSAX(contentHandler);

        // Close document:
        contentHandler.endDocument();

        this.logDebug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
        throw new ProcessingException(exception);
      }
    finally
      {
        if ( user != null ) this.serviceManager.release(user);
        if ( dbHelper != null ) this.serviceManager.release(dbHelper);
      }
  }
}