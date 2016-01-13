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
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.DbColumn;
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

public class AuthenticationGenerator extends ServiceableGenerator
{
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
   * The db columns the retrieve from the user table ({@link DbColumn.ID ID},
   * {@link DbColumn.THEME THEME}, and {@link DbColumn.LANGUAGE LANGUAGE}).
   */

  static final String[] USER_DATA_COLUMNS =
  {
    DbColumn.ID,
    DbColumn.THEME,
    DbColumn.LANGUAGE,
  };

  /**
   * Helper object to write XML elements to SAX.
   */

  protected GeneralXMLElement xmlElement = new GeneralXMLElement("", "");

  /**
   * Writes the XML answer required by the Cocoon authentication framework in the case the
   * user successfully logged in.
   */

  protected void successToSAX (Integer id,
                               Integer language,
                               Integer theme,
                               ContentHandler contentHandler)
    throws SAXException
  {
    // Start the XML decoument:
    this.contentHandler.startDocument();

    // Start the root element:
    this.xmlElement.reset();
    this.xmlElement.setLocalName(ROOT_ELEMENT);
    this.xmlElement.startToSAX(contentHandler);

    // Write ID element:
    this.xmlElement.reset();
    this.xmlElement.setLocalName(ID_ELEMENT);
    this.xmlElement.setText(id.toString());
    this.xmlElement.toSAX(contentHandler);

    // Start data element:
    this.xmlElement.reset();
    this.xmlElement.setLocalName(DATA_ELEMENT);
    this.xmlElement.startToSAX(contentHandler);

    // Write theme element:
    this.xmlElement.reset();
    this.xmlElement.setLocalName(THEME_ELEMENT);
    this.xmlElement.setText(theme.toString());
    this.xmlElement.toSAX(contentHandler);

    // Write language element:
    this.xmlElement.reset();
    this.xmlElement.setLocalName(LANGUAGE_ELEMENT);
    this.xmlElement.setText(language.toString());
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
    this.contentHandler.endDocument();
  }

  /**
   * Writes the XML answer required by the Cocoon authentication framework in the case the
   * login failed.
   */

  protected void failureToSAX (ContentHandler contentHandler)
    throws SAXException
  {
    // Start the XML decoument:
    this.contentHandler.startDocument();

    // Write an empty root element:
    this.xmlElement.reset();
    this.xmlElement.setLocalName(ROOT_ELEMENT);
    this.xmlElement.toSAX(contentHandler);

    // Close XML docuemnt:
    this.contentHandler.endDocument();
  }

  /**
   * Checks if login name and password are correct, generates the XML answer, sets session
   * attributes if necessary.
   */

  public void generate()
    throws SAXException, ProcessingException 
  {
    final String METHOD_NAME = "generate";
    this.getLogger().debug(METHOD_NAME + " 1/5: started");

    DbHelper dbHelper = null;
    PasswordEncryptor passwordEncryptor = null;
    SessionManager sessionManager = null;
    try 
      {
        // Get login name and password from the request:
	Request request = ObjectModelHelper.getRequest(objectModel);
	String loginName = request.getParameter(RequestParam.LOGIN_NAME);
	String password = request.getParameter(RequestParam.PASSWORD);
        passwordEncryptor =
          (PasswordEncryptor)this.manager.lookup(PasswordEncryptor.ROLE);
        String passwordEncrypted = passwordEncryptor.encrypt(password);
	this.getLogger().debug
          (METHOD_NAME + " 2/5:" +
           " loginName = " + loginName +
           ", passwordEncrypted = " + passwordEncrypted);

        // Lookup user in the database:
        dbHelper = (DbHelper)this.manager.lookup(DbHelper.ROLE);

	this.getLogger().debug
          (METHOD_NAME + " 3/5: dbHelper = " + LogUtil.identify(dbHelper));
	ResultSet userData =
          dbHelper.queryUserData(loginName, passwordEncrypted, USER_DATA_COLUMNS);

        if ( userData.next() ) // Login was successful
          {
            // Get id, language, and theme
            Integer id = new Integer(userData.getInt(DbColumn.ID));
            Integer language = new Integer(userData.getInt(DbColumn.LANGUAGE));
            Integer theme = new Integer(userData.getInt(DbColumn.THEME));
            this.getLogger().debug
              (METHOD_NAME + " 4/5: Login successful." +
               " id = " + id +
               ", language = " + language +
               ", theme = " + theme);

            // Set session attributes:
            sessionManager = (SessionManager)this.manager.lookup(SessionManager.ROLE);
            Session session = sessionManager.getSession(true);
            session.setAttribute(SessionAttrib.USER, id);
            session.setAttribute(SessionAttrib.LANGUAGE, language);
            session.setAttribute(SessionAttrib.THEME, theme);
            
            // Write XML answer:
            this.successToSAX(id, language, theme, this.contentHandler);
          }
        else // Login failed
          {
            this.getLogger().debug(METHOD_NAME + " 4/5: Login failed");

            // Write XML answer:
            this.failureToSAX(this.contentHandler);
          }
        this.getLogger().debug(METHOD_NAME + " 5/5: Done");
      } 
    catch (Exception exception) 
      {
	throw new SAXException(exception);
      }
    finally
      {
	if ( dbHelper != null ) this.manager.release(dbHelper);
        if ( passwordEncryptor != null ) this.manager.release(passwordEncryptor);
        if ( sessionManager != null ) this.manager.release(sessionManager);
      }
  }
}
