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

import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;

/**
 * <p>
 *   A japs client that does not interactively ask for account and password.
 * </p>
 * <p>
 *   The account and password can be set by the {@link #setAccount setAccount},
 *   {@link #setPassword setPassword}, {@link #readAccountFromFile readAccountFromFile},
 *   and {@link #readPasswordFromFile readPasswordFromFile} methods. The latter two
 *   specify a file from which the account resp. the password is read.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: NonInteractiveJapsClient.java,v 1.4 2007/07/16 11:15:37 grudzin Exp $</code>
 */

public class NonInteractiveJapsClient extends LoggingCookieEnabledJapsClient
{
  /**
   * The account for login.
   */

  protected String account = null;

  /**
   * The password for login.
   */

  private char[] password = null;

  /**
   * Returns account and password as a {@link LoginDialogResult LoginDialogResult}.
   */

  public LoginDialogResult performLoginDialog (boolean afterFailure)
  {
    if ( this.account == null || this.password == null )
      throw new IllegalStateException("Account and/or password null");
    return new LoginDialogResult(this.account, this.password);
  }

  /**
   * <p>
   *   Reads <code>file</code> and returns the content as an array of characters.
   * </p>
   */

  protected char[] readFromFile (File file)
    throws FileNotFoundException, IOException
  {
    long fileSize = file.length();
    if ( fileSize > Integer.MAX_VALUE )
      throw new IOException("File too big: " + file);
    char[] buffer = new char[(int)fileSize];
    Reader reader = new FileReader(file);
    int size = reader.read(buffer);
    if ( size == -1 )
      throw new IOException("Can not read init file");
    char[] chars = new char[size];
    for (int i = 0; i < chars.length; i++)
      chars[i] = buffer[i];
    LoginDialogResult.clear(buffer);
    return chars;
  }

  /**
   * Sets the account.
   */

  public void setAccount (String account)
  {
    if ( account == null )
      throw new IllegalStateException("Account null");
    this.account = account;
  }

  /**
   * Sets the password.
   */

  public void setPassword (char[] password)
  {
    if ( password == null )
      throw new IllegalStateException("Password null");
    this.password = password;
  }

  /**
   * Reads the account from a file. The entire content of the file is set to the account, so
   * avoid leading and trailing whitespaces and newlines in the file.
   *
   * @param file the file from where to read the account.
   */

  public void readAccountFromFile (File file)
    throws FileNotFoundException, IOException
  {
    this.account = new String(this.readFromFile(file));
  }

  /**
   * Reads the password from a file. The entire content of the file is set to the password, so
   * avoid leading and trailing whitespaces and newlines in the file.
   *
   * @param file the file from where to read the password.
   */

  public void readPasswordFromFile (File file)
    throws FileNotFoundException, IOException
  {
    this.password = this.readFromFile(file);
  }

  /**
   * Creates a new <code>NonInteractiveJapsClient</code> that writes its log messages
   * to the stream <code>logStream</code>.
   */

  public NonInteractiveJapsClient (String urlPrefix, PrintStream logStream)
  {
    super(urlPrefix, logStream);
  }

  /**
   * Creates a new <code>NonInteractiveJapsClient</code> that writes its log messages
   * to the file <code>logFile</code>.
   */

  public NonInteractiveJapsClient (String urlPrefix, File logFile)
    throws FileNotFoundException 
  {
    super(urlPrefix, logFile);
  }

  /**
   * Creates a new <code>NonInteractiveJapsClient</code> that writes its log messages
   * to the file with the name <code>logFilename</code>.
   */

  public NonInteractiveJapsClient (String urlPrefix, String logFilename)
    throws FileNotFoundException 
  {
    super(urlPrefix, logFilename);
  }

  /**
   * <p>
   *   Clears the password of this japs client.
   * </p>
   * <p>
   *   You should call this when you do not need this japs client any longer.
   * </p>
   */

  public void clearPassword ()
  {
    if ( this.password != null )
      LoginDialogResult.clear(this.password);
  }
}
