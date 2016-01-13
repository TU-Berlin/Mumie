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

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Pattern;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.RequestParam;
import net.mumie.cocoon.notions.SessionAttrib;
import net.mumie.cocoon.util.LogUtil;
import net.mumie.cocoon.util.PasswordEncryptor;
import net.mumie.cocoon.xml.GeneralXMLElement;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.generation.ServiceableGenerator;
import org.apache.cocoon.webapps.session.SessionManager;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import net.mumie.cocoon.tub.TubLdapHelper;
import net.mumie.cocoon.tub.TubLdapUser;
import net.mumie.cocoon.tub.TubMumieUser;

public class TubAuthenticationGenerator extends ServiceableGenerator
{
  // --------------------------------------------------------------------------------
  // h1: Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * Name of the root element of the XML answer required by the Cocoon authentication
   * framework (<code>"authentication"</code>).
   */

  static final String ROOT_ELEMENT = "authentication";

  /**
   * Name of the XML element containing the user id (<code>"ID"</code>).
   */

  static final String ID_ELEMENT = "ID";

  /**
   * Name of the XML element containing additional data (<code>"data"</code>).
   */

  static final String DATA_ELEMENT = "data";

  /**
   * Name of the XML element containing the theme id (<code>"theme"</code>).
   */

  static final String THEME_ELEMENT = "theme";

  /**
   * Name of the XML element containing the language id (<code>"language"</code>).
   */

  static final String LANGUAGE_ELEMENT = "language";

  /**
   * Helper object to write XML elements to SAX.
   */

  protected GeneralXMLElement xmlElement = new GeneralXMLElement("", "");

  /**
   * Pattern to split a comma-separated string.
   */

  protected Pattern splitPattern = Pattern.compile(",");

  // --------------------------------------------------------------------------------
  // h1: Checking user
  // --------------------------------------------------------------------------------

  /**
   * Checks the login name and password. In case of success, returns a
   * {@link #TubMumieUser TubMumieUser} object with the data of the user. Otherwise, returns null.
   */

  protected TubMumieUser checkCredentials ()
    throws ProcessingException
  {
    final String METHOD_NAME = "checkCredentials";
    this.getLogger().debug(METHOD_NAME + " 1/3: Started");

    TubMumieUser user = null;
    TubLdapHelper ldapHelper = null;
    DbHelper dbHelper = null;
    PasswordEncryptor encryptor = null;

    try 
      {
        // Get login name and password from the request:
        Request request = ObjectModelHelper.getRequest(objectModel);
        String loginName = request.getParameter(RequestParam.LOGIN_NAME);
        String password = request.getParameter(RequestParam.PASSWORD);
        if ( checkIfVoid(loginName) )
          throw new ProcessingException("Void login name");
        if ( checkIfVoid(password) )
          throw new ProcessingException("Void password");

        // Initialize services:
        ldapHelper = (TubLdapHelper)this.manager.lookup(TubLdapHelper.ROLE);
        dbHelper = (DbHelper)this.manager.lookup(DbHelper.ROLE);
        encryptor = (PasswordEncryptor)this.manager.lookup(PasswordEncryptor.ROLE);

        if ( dbHelper.checkIfUserHasCredentials(loginName, true) )
          {
             // Internal login
            this.getLogger().debug
              (METHOD_NAME + " 2/3: Internal login. loginName = " + loginName);

            ResultSet data = dbHelper.queryUserData
              (loginName, encryptor.encrypt(password), TubMumieUser.getDbColumns());

            if ( !data.next() )
              throw new ProcessingException("Wrong password for user: " + loginName);

            user = new TubMumieUser(data);
          }
        else
          {
            try
              {
                 // LDAP login
                this.getLogger().debug
                  (METHOD_NAME + " 2/3: LDAP login. loginName = " + loginName);

                // Lookup user in LDAP:
                TubLdapUser ldapUser = ldapHelper.searchUser("uid=" + loginName);
                if ( ldapUser == null )
                  throw new ProcessingException("User not found in LDAP: " + loginName);

                // Check password:
                if ( !ldapHelper.checkPassword(ldapUser.getUserDN(), password) )
                  throw new ProcessingException("Password check failed for user: " + loginName);

                // Check if user is registered in the Mumie db:
                String tubPersonOM = ldapUser.getTubPersonOM();
                if ( tubPersonOM == null )
                  throw new ProcessingException("User has no tubPersonOM: " + loginName);
                ResultSet data = dbHelper.queryPseudoDocDataBySyncId
                  (PseudoDocType.USER, tubPersonOM, TubMumieUser.getDbColumns());
                if ( !data.next() )
                  throw new ProcessingException
                    ("LDAP user not found in Mumie db: " + ldapUser);
                user = new TubMumieUser(data);

                // Cache credentials:
                Map credentials = new HashMap();
                credentials.put(DbColumn.LOGIN_NAME, loginName);
                credentials.put(DbColumn.PASSWORD, encryptor.encrypt(password));
                dbHelper.updateUserData(user.getId(), credentials);
              }
            catch (Exception exception)
              {
                // LDAP login failed
                this.getLogger().error("LDAP login failed: " + exception.getMessage());
                this.getLogger().debug("LDAP login failed", exception);

                // Try cached credentials:
                if ( dbHelper.checkIfUserHasCredentials(loginName, false) )
                  {
                    this.getLogger().debug
                      (METHOD_NAME + " 2.1/3:" +
                       " Login via cached credentials. loginName = " + loginName);

                    ResultSet data = dbHelper.queryUserData
                      (loginName, encryptor.encrypt(password), TubMumieUser.getDbColumns());

                    if ( !data.next() )
                      throw new ProcessingException("Wrong password for user: " + loginName);

                    user = new TubMumieUser(data);
                  }
                else
                  throw new ProcessingException
                    ("No cached credentials for user: " + loginName);
              }
          }

        this.getLogger().debug(METHOD_NAME + " 3/3: Login successful. user = " + user);
      }
    catch (Exception exception)
      {
        this.getLogger().debug(METHOD_NAME + " 3/3: Failed", exception);
      }
    finally
      {
        if ( ldapHelper != null ) this.manager.release(ldapHelper);
        if ( dbHelper != null ) this.manager.release(dbHelper);
        if ( encryptor != null ) this.manager.release(encryptor);
      }

    return user;
  }

  // --------------------------------------------------------------------------------
  // h1: To-session methods
  // --------------------------------------------------------------------------------

  /**
   * Sets the ids of the user, the users theme, and the users language as session
   * attributes.
   */

  protected void toSession (TubMumieUser user)
    throws ProcessingException
  {
    final String METHOD_NAME = "toSession";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");

    SessionManager sessionManager = null;
    try
      {
        sessionManager = (SessionManager)this.manager.lookup(SessionManager.ROLE);
        Session session = sessionManager.getSession(true);
        session.setAttribute(SessionAttrib.USER, new Integer(user.getId()));
        session.setAttribute(SessionAttrib.LANGUAGE, new Integer(user.getLanguageId()));
        session.setAttribute(SessionAttrib.THEME, new Integer(user.getThemeId()));
        this.getLogger().debug(METHOD_NAME + " 1/2: Done");
      }
    catch (Exception exception) 
      {
        throw new ProcessingException(exception);
      }
    finally
      {
        if ( sessionManager != null ) this.manager.release(sessionManager);
      }
  }

  // --------------------------------------------------------------------------------
  // h1: To-SAX methods
  // --------------------------------------------------------------------------------

  /**
   * Writes the XML answer required by the Cocoon authentication framework in the case the
   * user successfully logged in.
   */

  protected void successToSAX (TubMumieUser user, ContentHandler contentHandler)
    throws SAXException
  {
    // Start the XML decoument:
    contentHandler.startDocument();

    // Start the root element:
    this.xmlElement.reset();
    this.xmlElement.setLocalName(ROOT_ELEMENT);
    this.xmlElement.startToSAX(contentHandler);

    // Write ID element:
    this.xmlElement.reset();
    this.xmlElement.setLocalName(ID_ELEMENT);
    this.xmlElement.setText(Integer.toString(user.getId()));
    this.xmlElement.toSAX(contentHandler);

    // Start data element:
    this.xmlElement.reset();
    this.xmlElement.setLocalName(DATA_ELEMENT);
    this.xmlElement.startToSAX(contentHandler);

    // Write theme element:
    this.xmlElement.reset();
    this.xmlElement.setLocalName(THEME_ELEMENT);
    this.xmlElement.setText(Integer.toString(user.getThemeId()));
    this.xmlElement.toSAX(contentHandler);

    // Write language element:
    this.xmlElement.reset();
    this.xmlElement.setLocalName(LANGUAGE_ELEMENT);
    this.xmlElement.setText(Integer.toString(user.getLanguageId()));
    this.xmlElement.toSAX(contentHandler);

    // Close data element:
    this.xmlElement.reset();
    this.xmlElement.setLocalName(DATA_ELEMENT);
    this.xmlElement.endToSAX(contentHandler);

    // Close root element:
    this.xmlElement.reset();
    this.xmlElement.setLocalName(ROOT_ELEMENT);
    this.xmlElement.endToSAX(contentHandler);

    // Close XML docuemnt:
    contentHandler.endDocument();
  }

  /**
   * Writes the XML answer required by the Cocoon authentication framework in the case the
   * login failed.
   */

  protected void failureToSAX (ContentHandler contentHandler)
    throws SAXException
  {
    // Start the XML decoument:
    contentHandler.startDocument();

    // Write an empty root element:
    this.xmlElement.reset();
    this.xmlElement.setLocalName(ROOT_ELEMENT);
    this.xmlElement.toSAX(contentHandler);

    // Close XML docuemnt:
    contentHandler.endDocument();
  }

  // --------------------------------------------------------------------------------
  // h1: Generate method
  // --------------------------------------------------------------------------------

  /**
   * Checks if login name and password are correct, generates the XML answer, sets session
   * attributes if necessary.
   */

  public void generate()
    throws SAXException, ProcessingException 
  {
    final String METHOD_NAME = "generate";
    this.getLogger().debug(METHOD_NAME + " 1/5: started");

    TubMumieUser user = this.checkCredentials();

    if ( user != null ) // Login successful
      {
        this.toSession(user);
        this.successToSAX(user, this.contentHandler);
      }
    else // Login failed
      {
        this.failureToSAX(this.contentHandler);
      }
  }

  // --------------------------------------------------------------------------------
  // h1: Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * 
   */

  protected static final boolean checkIfVoid (String string)
  {
    return ( string == null || string.trim().length() == 0 );
  }

}
