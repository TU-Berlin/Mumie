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

/**
 * <p>
 *   Represents a pair consisting of an account name and password.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: LoginDialogResult.java,v 1.3 2007/07/16 11:15:37 grudzin Exp $</code>
 */

public class LoginDialogResult
{
  /**
   * The account.
   */

  protected String account = null;

  /**
   * The password.
   */

  private char[] password = null;

  /**
   * Returns the the account. (Cf. {@link #account account}.)
   */

  public String getAccount ()
  {
    return this.account;
  }

  /**
   * Returns the password.
   */

  public char[] getPassword ()
  {
    char[] password = new char[this.password.length];
    System.arraycopy(this.password, 0, password, 0, this.password.length);
    return password;
  }

  /**
   * Returns the password as a string.
   */

  public String getPasswordAsString ()
  {
    return new String(this.password);
  }

  /**
   * Replaces all characters in <code>chars</code> by the null character.
   */

  public static void clear (char[] chars)
  {
    for (int i = 0; i < chars.length; i++) chars[i] = 0;
  }

  /**
   * Erases the data stored in this object.
   */

  public void clear ()
  {
    this.account = null;
    if ( this.password != null )
      {
        clear(this.password);
        this.password = null;
      }
  }

  /**
   * Creates a new <code>LoginDialogResult</code> object.
   */

  public LoginDialogResult (String account, char[] password)
  {
    this.account = account;
    this.password = new char[password.length];
    System.arraycopy(password, 0, this.password, 0, password.length);
  }

}
