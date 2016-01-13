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

import com.sonalb.net.http.cookie.CookieJar;
import com.sonalb.net.http.cookie.Client;
import java.util.Iterator;
import com.sonalb.net.http.cookie.Cookie;
import java.net.HttpURLConnection;

/**
 * <p>
 *   A Japs client that can handle cookies.
 * </p>
 * <p>
 *   This class uses the jCookie library of Sonal Bansal, see 
 *   <a href="http://jcookie.sourceforge.net">http://jcookie.sourceforge.net</a>.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: CookieEnabledJapsClient.java,v 1.2 2007/07/16 11:15:37 grudzin Exp $</code>
 */

public abstract class CookieEnabledJapsClient extends JapsClient
{
  /**
   * Helper to handle cookies.
   */

  protected Client cookieHandler = new Client();

  /**
   * The stored cookies.
   */

  protected CookieJar cookies = new CookieJar();

  /**
   * Adds those cookies to the request that should be resent to the server.
   */

  protected void addCookies (HttpURLConnection connection)
    throws JapsClientException
  {
    final String METHOD = "addCookies";
    this.cookieHandler.setCookies(connection, this.cookies);
    this.log(METHOD + " 1/1: " + connection.getRequestProperty("Cookie"));
  }

  /**
   * Adds the cookies found in the resopnse headers of <code>connection</code> to the stored
   * cookies.
   */

  protected void handleRecievedCookies (HttpURLConnection connection)
    throws JapsClientException
  {
    try
      {
        final String METHOD = "handleRecievedCookies";
        CookieJar recievedCookies = this.cookieHandler.getCookies(connection);
        this.log(METHOD + " 1/1: recievedCookies = " + recievedCookies);
        this.cookies.addAll(recievedCookies);
      }
    catch (Exception exception)
      {
        throw new JapsClientException(exception);
      }
  }

  /**
   * Creates a new <code>CookieEnabledJapsClient</code> with <code>urlPrefix</code> as
   * server url prefix.
   */

  public CookieEnabledJapsClient (String urlPrefix)
  {
    super(urlPrefix);
  }
}
