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

package net.mumie.cdk.util;

import net.mumie.cdk.CdkConfigParam;
import net.mumie.cdk.CdkHelper;
import net.mumie.japs.client.CookieEnabledJapsClient;
import net.mumie.japs.client.JapsClientException;
import net.mumie.japs.client.LoginDialogResult;
import net.mumie.japs.client.SwingLoginDialog;
import net.mumie.util.logging.SimpleLogger;

public class CdkJapsClient extends CookieEnabledJapsClient
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The server alias.
   */

  protected String alias = null;

  /**
   * The account for login.
   */

  protected String account = null;

  /**
   * The password for login.
   */

  private char[] password = null;

  // --------------------------------------------------------------------------------
  // Logging
  // --------------------------------------------------------------------------------

  /**
   * Writes a log message. Same as
   * <pre>SimpleLogger.getSharedInstance().log("Jvmd: " + message, throwable);</pre>
   */

  protected void log (String message)
  {
    SimpleLogger.getSharedInstance().log("JapsClient (" + this.alias + "): " + message);
  }

  // --------------------------------------------------------------------------------
  // Account and password
  // --------------------------------------------------------------------------------

  /**
   * Sets password.
   */

  public void setPassword (char[] password)
  {
    this.password = password;
  }

  /**
   * Returns true if the password is set, otherwise false.
   */

  public boolean passwordSet ()
  {
    return ( this.password != null );
  }

  /**
   * Returns the account.
   */

  public String getAccount ()
  {
    return this.account;
  }

  /**
   * Returns account and password as a {@link LoginDialogResult LoginDialogResult}.
   */

  public LoginDialogResult performLoginDialog (boolean afterFailure)
    throws JapsClientException
  {
    if ( this.password == null )
      {
        if ( !CdkHelper.toBoolean(System.getProperty(CdkConfigParam.GRAPHICAL_LOGIN, "true")) )
          throw new JapsClientException
            ("No password stored for alias \"" + this.alias + "\"");
        SwingLoginDialog loginDialog = new SwingLoginDialog
          (null,
           "Mumie Login",
           "Login for " + this.alias,
           this.account,
           false,
           afterFailure);
        return loginDialog.getResult();
      }
    else
      {
        if ( afterFailure )
          throw new JapsClientException
            ("Failed to login for alias \"" + this.alias + "\" (wrong password?)");
        return new LoginDialogResult(this.account, this.password);
      }
  }

  /**
   * Logs out and clears the password.
   */

  public void logout ()
    throws JapsClientException 
  {
    super.logout();
    this.password = null;
  }

  // --------------------------------------------------------------------------------
  // Constructor
  // --------------------------------------------------------------------------------

  /**
   * 
   */

  public CdkJapsClient (String alias, String urlPrefix, String account)
  {
    super(urlPrefix);
    this.alias = alias;
    this.account = account;
  }
}
