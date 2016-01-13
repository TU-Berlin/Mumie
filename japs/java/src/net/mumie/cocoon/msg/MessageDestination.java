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

package net.mumie.cocoon.msg;

/**
 * Comprises the URL, login name and password of a message destination.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: MessageDestination.java,v 1.1 2008/09/23 22:57:52 rassy Exp $</code>
 */

public class MessageDestination
{
  // --------------------------------------------------------------------------------
  // h1: Global constants and variables
  // --------------------------------------------------------------------------------

  /**
   * The URL.
   */

  protected final String url;

  /**
   * The login name, or null if no login is required.
   */

  protected final String loginName;

  /**
   * The password, or null if no login is required.
   */

  protected final String password;

  // --------------------------------------------------------------------------------
  // h1: Constructor
  // --------------------------------------------------------------------------------

  /**
   * Creates a new instance with the specified data.
   */

  public MessageDestination (String url, String loginName, String password)
  {
    this.url = url;
    this.loginName = loginName;
    this.password = password;
  }

  // --------------------------------------------------------------------------------
  // h1: Get methods.
  // --------------------------------------------------------------------------------

  /**
   * Returns the url.
   */

  public final String getURL ()
  {
    return this.url;
  }

  /**
   * Returns the login name, or null if no login is required.
   */

  public final String getLoginName ()
  {
    return this.loginName;
  }

  /**
   * Returns the password, or null if no login is required.
   */

  public final String getPassword ()
  {
    return this.password;
  }
}
