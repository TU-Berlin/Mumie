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

package net.mumie.japs.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Console;

/**
 * <p>
 *   A login dialog on the console.
 * </p>
 * <p>
 *   The dialog is performed when the constructor is called. First, a short message is
 *   printed, then the user is asked for the account, then for the password, then the
 *   constructor returns. The resulting <code>ConsoleLoginDialog</code> instance stores the
 *   account and password. They can be obtained as a {@link LoginDialogResult} object by the
 *   {@link #getResult getResult} method.
 * </p>
 * <p>
 *   The constructor has a boolean argument, <code>afterFailure</code>, that controls the
 *   message printed before the user is asked for account and password. If
 *   <code>afterFailure</code> is <code>false</code>, the message simply informs the user
 *   that now follows the login for Mumie. The wording of the message is given by the
 *   constant {@link #INFO INFO}. If <code>afterFailure</code> is <code>true</code>, the
 *   message says that the login has failed and must be repeated. The wording is given by
 *   the constant {@link #LOGIN_FAILED LOGIN_FAILED}. Thus, <code>afterFailure</code> should
 *   be set to <code>true</code> if this login dialog immediately follows a failed try to
 *   login, and otherwise to <code>false</code>.
 * </p>
 * <p>
 *   After a successful login, the console would look like the following:
 * </p>
 * <pre>
 *   Mumie Login
 *   Account: myloginname
 *   Password:</pre>
 * <p>
 *   The password is not echoed on the console, of course.
 * </p>
 * <p>
 *   After a failed login followed by a successful login, the console would look like:
 * </p>
 * <pre>
 *   Mumie Login
 *   Account: myloginname
 *   Password:
 *   Login failed - please try again
 *   Account: myloginname
 *   Password:</pre>
 * <p>
 *   NOTE: This class depends on native code contained in the library
 *   <code>ConsoleLoginDialog</code> (thus, on UNIX/LINUX systems, the filename of the
 *   libaray is <code>libConsoleLoginDialog.so</code>).
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: ConsoleLoginDialog.java,v 1.5 2007/07/16 11:15:37 grudzin Exp $</code>
 */

public class ConsoleLoginDialog
{
  /**
   * A short hint for the user that this is the login dialog for MUMIE-JAPS
   * (<code>"Mumie Login"</code>).
   */

  public static final String INFO = "Mumie Login";

  /**
   * Message that the login failed and must be repeated
   * (<code>"Login failed - please try again"</code>).
   */

  public static final String LOGIN_FAILED = "Login failed - please try again";

  /**
   * The account prompt (<code>"Account"</code>).
   */

  public static final String ACCOUNT_PROMPT = "Account: ";

  /**
   * The password prompt (<code>"Password"</code>).
   */

  public static final String PASSWORD_PROMPT = "Password: ";

  /**
   * The account read by this dialog, or <code>null</code> if the account has not been read
   * yet.
   */

  protected String account = null;

  /**
   * The password read by this dialog, or <code>null</code> if the password has not been
   * read yet.
   */

  private char[] password = null;

  /**
   * Prompts with <code>prompt</code>, reads the account and returns it.
   */

  protected String readAccount (String prompt)
    throws IOException 
  {
    System.out.print(prompt);
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    return reader.readLine();
  }

  /**
   * Prompts with <code>prompt</code>, reads the password and returns it.
   */

  protected char[] readPassword (String prompt)
    throws IOException
 {
   Console console = System.console();
   if ( console == null )
     throw new IOException("No console available");
   return console.readPassword("Password: ");
  }

  /**
   * <p>
   *   Creates a new <code>ConsoleLoginDialog</code> and performs the dialog.
   * </p>
   * <p>
   *   if <code>afterFailure</code> is <code>true</code>, the message {@link #LOGIN_FAILED}
   *   is printed before the dialog, otherwise the message {@link #INFO}. Thus, set
   *   <code>afterFailure</code> to <code>true</code> if this dialog immediately follows a
   *   failed try to login.
   * <p>
   */

  public ConsoleLoginDialog (boolean afterFailure)
    throws IOException 
  {
    System.out.println(afterFailure ? LOGIN_FAILED : INFO);
    this.account = this.readAccount(ACCOUNT_PROMPT);
    this.password = this.readPassword(PASSWORD_PROMPT);
  }

  /**
   * Same as {@link #ConsoleLoginDialog(boolean) ConsoleLoginDialog(false)}.
   */

  public ConsoleLoginDialog ()
    throws IOException 
  {
    this(false);
  }

  /**
   * Returns the result of this dialog.
   */

  public LoginDialogResult getResult ()
  {
    if ( this.account == null || this.password == null )
      throw new IllegalStateException("Account or password null");
    LoginDialogResult result = new LoginDialogResult(this.account, this.password);
    LoginDialogResult.clear(this.password);
    this.account = null;
    this.password = null;
    return result;
  }
}
