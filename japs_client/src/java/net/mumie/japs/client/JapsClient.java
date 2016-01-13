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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import net.mumie.japs.client.util.URLUTF8Encoder;

/**
 * <p>
 *   A client of the Mumie Japs server.
 * </p>
 * <p>
 *   By means of a <code>JapsClient</code>, applications and applets can easily get or send
 *   data from or to the server without bothering about login and authentication.
 * </p>
 * <p>
 *   Extending classes must overwrite the {@link #performLoginDialog performLoginDialog}
 *   method, which returns the account and password of the user as a
 *   {@link LoginDialogResult LoginDialogResult} object. If logging is desired, extending
 *   classes should also overwrite the {@link #log log} method, because the {@link #log log}
 *   method of this class does nothing.
 * </p>
 * <p>
 *   The main feature of this class are the various <code>get</code> and <code>post</code>
 *   methods. They send GET resp. POST requests to the Japs server. If necessary, the
 *   <code>get</code> and <code>post</code> methods login the user by calling the
 *   {@link #login() login} method (some of them allow to switch off this by a flag). Thus,
 *   if you are a programmer and use a <code>JapsClient</code> in your code, you do not
 *   need to do the login explicitely.
 * </p>
 * <p>
 *   All <code>get</code> and <code>post</code> methods return a {@link HttpURLConnection}
 *   that can be used to process the response to the request, but it is also possible that
 *   the methods return <code>null</code>. The latter occurs if and only if the user was
 *   asked to login and has cancelled the login dialog. In that case, no response exist.
 * </p>
 * <h3>Examples</h3>
 * <p>
 *   In the following, <code>japsClient</code> always denotes a japs client instance.
 * </p>
 * <p>
 *   <strong>1. <code>Get</code> without parameters.</strong>
 *   We use the {@link #get(String)} form of the <code>get</code> method. After calling it,
 *   we check if the return value was <code>null</code> (user has cancelled login dialog) or
 *   not <code>null</code> (response exists). The code would follow the
 *   following pattern:
 * </p>
 * <pre>
 *   HttpURLConnection connection = japsClient.get("protected/foo/bar");
 *   if ( connection == null )
 *     {
 *       // User has cancelled login dialog
 *       // Handle this case here
 *     }
 *   else
 *     {
 *       // Process response
 *     }</pre>
 * <p>
 *   <strong>2. <code>Get</code> with parameters.</strong>
 *   We use the {@link #get(String,Map)} form of the <code>get</code> method. Before
 *   calling it, we compose a {@link HashMap} that contains the request parameter. The rest
 *   ist similar to example 1:
 * </p>
 * <pre>
 *   HashMap params = new HashMap();
 *   params.put("name1", "value1");
 *   params.put("name2", "value2");
 *   HttpURLConnection connection = japsClient.get("protected/foo/bar", params);
 *   if ( connection == null )
 *     {
 *       // User has cancelled login dialog
 *       // Handle this case here
 *     }
 *   else
 *     {
 *       // Process response
 *     }</pre>
 * <p>
 *   <strong>3. <code>Post</code> with parameters.</strong>
 *   We use the {@link #post(String,Map)} form of the <code>post</code> method. The rest is
 *   the same as in example 2:
 * </p>
 * <pre>
 *   HashMap params = new HashMap();
 *   params.put("name1", "value1");
 *   params.put("name2", "value2");
 *   HttpURLConnection connection = japsClient.post("protected/foo/bar", params);
 *   if ( connection == null )
 *     {
 *       // User has cancelled login dialog
 *       // Handle this case here
 *     }
 *   else
 *     {
 *       // Process response
 *     }</pre>
 * <p>
 *   <strong>4. File upload with <code>Post</code>.</strong>
 *   We use the {@link #post(String,File,String)} form of the <code>post</code> method. The
 *   third argument is the MIME type of the file. The code looks as follows:
 * </p>
 * <pre>
 *   HttpURLConnection connection =
 *     japsClient.post("protected/foo/bar", new File("myfile"), "text/plain");
 *   if ( connection == null )
 *     {
 *       // User has cancelled login dialog
 *       // Handle this case here
 *     }
 *   else
 *     {
 *       // Process response
 *     }</pre>
 * <p>
 *   There is also a {@link #post(String,File)} form of the <code>post</code> method which
 *   guesses the MIME type of the file by its name suffix.
 * </p>
 * <p>
 *   (End of examples)
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Revision: 1.22 $</code>
 */

public abstract class JapsClient
{
  // --------------------------------------------------------------------------------
  // h1:  Static constants
  // --------------------------------------------------------------------------------

  /**
   * The user agent as which the JapsClient should identify itself
   * (<code>"JapsClient/Java"</code>).
   */

  public static final String USER_AGENT_VALUE = "JapsClient/Java";

  /**
   * Indicates a successful login.
   */

  public static final int LOGIN_SUCCESSFUL = 0;

  /**
   * Indicates a failed login.
   */

  public static final int LOGIN_FAILED = 1;

  /**
   * Indicates that the uses has canceled the login dialog.
   */

  public static final int LOGIN_CANCELED = 2;

  /**
   * The request parameter containing the account (<code>"name"</code>).
   */

  public static final String ACCOUNT_PARAM = "login-name";

  /**
   * The request parameter containing the password (<code>"password"</code>).
   */

  public static final String PASSWORD_PARAM = "password";

  /**
   * The request parameter containing the resource (<code>"resource"</code>).
   */

  public static final String RESOURCE_PARAM = "resource";

  /**
   * The url path of the resource requested in the login request
   * (<code>"protected/auth/login-successful"</code>).
   */

  public static final String RESOURCE_VALUE = "protected/auth/login-successful";

  /**
   * Unlimited number of login tries. Value: <code>-1</code>
   */

  public static final int UNLIMITED = -1;

  /**
   * Set of characters from which boundary delimiters for multipart post requests are
   * build (digits and letters, i.e., <code>'0'&hellip;'9', 'a'&hellip;'z',
   * 'A'&hellip;'Z'</code>).
   */

  public static final String MULTPART_BOUNDARY_CHARSPACE =
    "0123456789" + "abcdefghijklmnopqrstuvw" + "ABCDEFGHIJKLMNOPQRSTUVW";

  /**
   * Minimum size of boundary delimiters (<code>10</code>).
   */

  public static final int MULTPART_BOUNDARY_MIN_SIZE = 10;

  /**
   * Maximum size of boundary delimiters (<code>70</code>). This is ascertained in the MIME
   * specification, see <a href="http://www.ietf.org/rfc/rfc2046.txt">RFC 2046</a>,
   * sec. 5.1.
   */

  public static final int MULTPART_BOUNDARY_MAX_SIZE = 70;

  /**
   * The maximum number of tries to create a boundary delimiter for a given file
   * (<code>100</code>).
   */

  public static final int MULTPART_BOUNDARY_MAX_TRIES = 100;

  /**
   * Content type suited for text files (<code>"text/plain"</code>).
   */

  public static final String TEXT_CONTENT_TYPE = "text/plain";

  /**
   * Content type suited for binary files (<code>"application/octet-stream"</code>).
   */

  public static final String BINARY_CONTENT_TYPE = "application/octet-stream";

  /**
   * Default content type for file uploads ({@link #BINARY_CONTENT_TYPE}).
   */

  public static final String DEFAULT_CONTENT_TYPE = BINARY_CONTENT_TYPE;

  // --------------------------------------------------------------------------------
  // h1:  Global variables
  // --------------------------------------------------------------------------------

  /**
   * The url prefix of the Japs server.
   */

  protected String urlPrefix = null;

  /**
   * The URL encoder used to encode text.
   */

  protected URLUTF8Encoder urlEncoder = new URLUTF8Encoder();

  /**
   * Maximum number of login tries. A value of {@link #UNLIMITED} (<code>-1</code>) means the
   * number is not limited. Default: {@link #UNLIMITED}.
   */

  protected int maxLoginTries = UNLIMITED;

  /**
   * <p>
   *   Stores content types (i.e., MIME types) for several file suffixes. The default is as
   *   follows: 
   * </p>
   * <table class="genuine">
   *   <thead>
   *     <tr><td>Suffix</td><td>Content Type</td></tr>
   *   </thead>
   *   <tbody>
   *     <tr><td><code>txt</code></td><td><code>text/plain</code></td></tr>
   *     <tr><td><code>xml</code></td><td><code>text/xml</code></td></tr>
   *     <tr><td><code>xhtml</code></td><td><code>text/xml</code></td></tr>
   *     <tr><td><code>xsl</code></td><td><code>text/xsl</code></td></tr>
   *     <tr><td><code>html</code></td><td><code>text/html</code></td></tr>
   *     <tr><td><code>css</code></td><td><code>text/css</code></td></tr>
   *     <tr><td><code>tex</code></td><td><code>text/tex</code></td></tr>
   *     <tr><td><code>jpg</code></td><td><code>image/jpeg</code></td></tr>
   *     <tr><td><code>png</code></td><td><code>image/png</code></td></tr>
   *     <tr><td><code>zip</code></td><td><code>application/zip</code></td></tr>
   *     <tr><td><code>jar</code></td><td><code>application/x-java-archive</code></td></tr>
   *   </tbody>
   * </table>
   */

  protected Properties contentTypes = new Properties();

  /**
   * Helper class to create a string of ascii characters via an output stream
   */

  protected static class AsciiStringOutputStream extends OutputStream
  {
    /**
     * Collects the characters written to this stream.
     */

    protected StringBuffer buffer = new StringBuffer();

    /**
     * Appends <code>i</code> (casted to <code>char</code>) to {@link #buffer}. Throws an
     * exception if <code>i</code> does not represent an ascii character (e.g., is larger
     * then 255).
     */

    public void write (int i)
      throws IOException
    {
      char c = (char)i;
      if ( i > 255 )
        throw new IOException("Not an ascii character: " + c);
      buffer.append(c);
    }

    /**
     * Returns the content written to this stream as a string.
     */

    public String toString ()
    {
      return this.buffer.toString();
    }
  }

  // --------------------------------------------------------------------------------
  // h1:  Constructor
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Creates a new Japs client with <code>urlPrefix</code> as server url prefix.
   * </p>
   * <p>
   *   Extending classes should call this in their constructors.
   * </p>
   */

  public JapsClient (String urlPrefix)
  {
    // URL prefix:
    if ( urlPrefix == null || urlPrefix.equals("") || urlPrefix.equals("/") )
      throw new IllegalArgumentException("Url prefix null or void");
    int lastIndex = urlPrefix.length() - 1;
    if ( urlPrefix.charAt(lastIndex) == '/' )
      urlPrefix = urlPrefix.substring(0, lastIndex);
    this.urlPrefix = urlPrefix;

    // Content types table:
    this.contentTypes.setProperty("txt", "text/plain");
    this.contentTypes.setProperty("xml", "text/xml");
    this.contentTypes.setProperty("xhtml", "text/xml");
    this.contentTypes.setProperty("xsl", "text/xsl");
    this.contentTypes.setProperty("html", "text/html");
    this.contentTypes.setProperty("css", "text/css");
    this.contentTypes.setProperty("tex", "text/tex");
    this.contentTypes.setProperty("jpg", "image/jpeg");
    this.contentTypes.setProperty("png", "image/png");
    this.contentTypes.setProperty("zip", "application/zip");
    this.contentTypes.setProperty("jar", "application/x-java-archive");
  }

  // --------------------------------------------------------------------------------
  // h1:  Hooks for cookie handling
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   If necessary, reads the cookies revieved from the server and stores them.
   * </p>
   * <p>
   *   This implementation does nothing. Extending classes may overwrite this to implement
   *   cookie handling. 
   * </p>
   */

  protected void handleRecievedCookies (HttpURLConnection connection)
    throws JapsClientException
  {
    // Does npthing
  }

  /**
   * <p>
   *   If necessary, sets the cookies that should be sent back to the server.
   * </p>
   * <p>
   *   This implementation does nothing. Extending classes may overwrite this to implement
   *   cookie handling. 
   * </p>
   */

  protected void addCookies (HttpURLConnection connection)
    throws JapsClientException
  {
    // Does npthing
  }

  // --------------------------------------------------------------------------------
  // h1:  HTTP requests
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Returns a connection for <code>path</code> via the specified HTTP method. The Java
   *   methods for the Http GET and HEAD methods are reduced to this.
   * </p>
   * <p>
   *   The entries of <code>params</code> are added as request parameters to the URL. They
   *   are automatically url-encoded. If <code>params</code> is <code>null</code> or empty,
   *   no request parameters are added.
   * </p>
   * <p>
   *   If <code>loginIfNecessary</code> is <code>true</code>, the user is logged-in if the
   *   server requires so. This is done by {@link #login()} (which normally allowes several
   *   tries to log-in). If the login succeded, the request is repeated. If the server
   *   requires login again (which should not happen in normal operation), an exception is
   *   thrown. If the login failed, an exception is thrown. If the login was canceled by the
   *   user, no exception is thrown and <code>null</code> is returned.
   * </p>
   */

  protected HttpURLConnection request (String method, String path, Map params, boolean loginIfNecessary)
    throws JapsClientException, IOException, MalformedURLException
  {
    final String METHOD = "request";
    this.log(METHOD + " 1/2:" +
             " method = " + method +
             ", path = " + path +
             ", params = " + this.paramsToString(params) +
             ", loginIfNecessary = " + loginIfNecessary);

    // Add params to path if necessary:
    String pathPlusParams = new String(path);
    if ( params != null && !params.isEmpty() )
      pathPlusParams += '?' + this.urlEncode(params);

    // Prepare connection:
    HttpURLConnection connection =
      (HttpURLConnection)this.composeURL(pathPlusParams).openConnection();
    connection.setRequestMethod(method);
    connection.setRequestProperty("User-Agent", USER_AGENT_VALUE);

    // If necessary, add cookies:
    this.addCookies(connection);

    // Connect:
    connection.connect();

    // Check response code:
    if ( connection.getResponseCode() !=  HttpURLConnection.HTTP_OK )
      throw new JapsClientException(connection);

    // If necessary, handle cookies:
    this.handleRecievedCookies(connection);

    // Login if necessary:
    if ( loginIfNecessary && this.checkLoginRequired(connection) )
      {
        int loginStatus = this.login();
        switch ( loginStatus )
          {
          case LOGIN_SUCCESSFUL:
            connection = this.get(path, params, false);
            if ( this.checkLoginRequired(connection) )
              throw new JapsClientRejectedAfterLoginExcpetion(connection);
            break;
          case LOGIN_FAILED:
            throw new JapsClientException
              (connection, "login failed");
          case LOGIN_CANCELED:
            connection = null;
            break;
          default:
            throw new IllegalStateException
              ("Bug: unexpected login status: " + loginStatus);
          }
      }

    this.log(METHOD + " 2/2: connection = " + connection);
    return connection;
  }

  /**
   * <p>
   *   Returns a connection for <code>path</code> via the HTTP Get method.
   * </p>
   * <p>
   *   The entries of <code>params</code> are added as request parameters to the URL. They
   *   are automatically url-encoded. If <code>params</code> is <code>null</code> or empty,
   *   no request parameters are added.
   * </p>
   * <p>
   *   If <code>loginIfNecessary</code> is <code>true</code>, the user is logged-in if the
   *   server requires so. This is done by {@link #login()} (which normally allowes several
   *   tries to log-in). If the login succeded, the request is repeated. If the server
   *   requires login again (which should not happen in normal operation), an exception is
   *   thrown. If the login failed, an exception is thrown. If the login was canceled by the
   *   user, no exception is thrown and <code>null</code> is returned.
   * </p>
   */

  public HttpURLConnection get (String path, Map params, boolean loginIfNecessary)
    throws JapsClientException, IOException, MalformedURLException
  {
    return this.request("GET", path, params, loginIfNecessary);
  }

  /**
   * <p>
   *   Returns a connection for <code>path</code> via the HTTP Get method.
   * </p>
   * <p>
   *   The entries of <code>params</code> are added as request parameters to the URL. They
   *   are automatically url-encoded. If <code>params</code> is <code>null</code> or empty,
   *   no request parameters are added.
   * </p>
   * <p>
   *   The user is logged-in if the server requires so. This is done by {@link #login()}
   *   (which normally allowes several tries to log-in). If the login succeded, the request
   *   is repeated. If the server requires login again (which should not happen in normal
   *   operation), an exception is thrown. If the login failed, an exception is thrown. If
   *   the login was canceled by the user, no exception is thrown and <code>null</code> is
   *   returned.
   * </p>
   * <p>
   *   Same as {@link #get(String,Map,boolean) get(path, params, true)};
   * </p>
   */

  public HttpURLConnection get (String path, Map params)
    throws JapsClientException, IOException, MalformedURLException
  {
    return this.get(path, params, true);
  }

  /**
   * <p>
   *   Returns a connection for <code>path</code> via the HTTP Get method.
   * </p>
   * <p>
   *   The user is logged-in if the server requires so. This is done by {@link #login()}
   *   (which normally allowes several tries to log-in). If the login succeded, the request
   *   is repeated. If the server requires login again (which should not happen in normal
   *   operation), an exception is thrown. If the login failed, an exception is thrown. If
   *   the login was canceled by the user, no exception is thrown and <code>null</code> is
   *   returned.
   * </p>
   * <p>
   *   Same as {@link #get(String,Map) get(path, null)};
   * </p>
   */

  public HttpURLConnection get (String path)
    throws JapsClientException, IOException, MalformedURLException
  {
    return this.get(path, null);
  }

  /**
   * <p>
   *   Returns a connection for <code>path</code> via the HTTP Head method.
   * </p>
   * <p>
   *   The entries of <code>params</code> are added as request parameters to the URL. They
   *   are automatically url-encoded. If <code>params</code> is <code>null</code> or empty,
   *   no request parameters are added.
   * </p>
   * <p>
   *   If <code>loginIfNecessary</code> is <code>true</code>, the user is logged-in if the
   *   server requires so. This is done by {@link #login()} (which normally allowes several
   *   tries to log-in). If the login succeded, the request is repeated. If the server
   *   requires login again (which should not happen in normal operation), an exception is
   *   thrown. If the login failed, an exception is thrown. If the login was canceled by the
   *   user, no exception is thrown and <code>null</code> is returned.
   * </p>
   */

  public HttpURLConnection head (String path, Map params, boolean loginIfNecessary)
    throws JapsClientException, IOException, MalformedURLException
  {
    return this.request("HEAD", path, params, loginIfNecessary);
  }

  /**
   * <p>
   *   Returns a connection for <code>path</code> via the HTTP Head method.
   * </p>
   * <p>
   *   The entries of <code>params</code> are added as request parameters to the URL. They
   *   are automatically url-encoded. If <code>params</code> is <code>null</code> or empty,
   *   no request parameters are added.
   * </p>
   * <p>
   *   The user is logged-in if the server requires so. This is done by {@link #login()}
   *   (which normally allowes several tries to log-in). If the login succeded, the request
   *   is repeated. If the server requires login again (which should not happen in normal
   *   operation), an exception is thrown. If the login failed, an exception is thrown. If
   *   the login was canceled by the user, no exception is thrown and <code>null</code> is
   *   returned.
   * </p>
   * <p>
   *   Same as {@link #head(String,Map,boolean) head(path, params, true)};
   * </p>
   */

  public HttpURLConnection head (String path, Map params)
    throws JapsClientException, IOException, MalformedURLException
  {
    return this.head(path, params, true);
  }

  /**
   * <p>
   *   Returns a connection for <code>path</code> via the HTTP Head method.
   * </p>
   * <p>
   *   The user is logged-in if the server requires so. This is done by {@link #login()}
   *   (which normally allowes several tries to log-in). If the login succeded, the request
   *   is repeated. If the server requires login again (which should not happen in normal
   *   operation), an exception is thrown. If the login failed, an exception is thrown. If
   *   the login was canceled by the user, no exception is thrown and <code>null</code> is
   *   returned.
   * </p>
   * <p>
   *   Same as {@link #head(String,Map) head(path, null)};
   * </p>
   */

  public HttpURLConnection head (String path)
    throws JapsClientException, IOException, MalformedURLException
  {
    return this.head(path, null);
  }

  /**
   * <p>
   *   Returns a connection for <code>path</code> via the HTTP Post method. Can be used to
   *   send data to the server in the manner browsers send form data.
   * </p>
   * <p>
   *   The entries of <code>params</code> are written to the entity body of the request in
   *   the url-encoded format. If <code>params</code> is <code>null</code> or empty, this is
   *   suppressed.
   * </p>
   * <p>
   *   If <code>loginIfNecessary</code> is <code>true</code>, the user is logged-in if the
   *   server requires so. This is done by {@link #login()} (which normally allowes several
   *   tries to log-in). If the login succeded, the request is repeated. If the server
   *   requires login again (which should not happen in normal operation), an exception is
   *   thrown. If the login failed, an exception is thrown. If the login was canceled by the
   *   user, no exception is thrown and <code>null</code> is returned.
   * </p>
   */

  public HttpURLConnection post (String path, Map params, boolean loginIfNecessary)
    throws JapsClientException, IOException, MalformedURLException
  {
    final String METHOD = "post(String,Map,boolean)";
    this.log(METHOD + " 1/2: path = " + path +
             ", params = " + this.paramsToString(params) +
             ", loginIfNecessary = " + loginIfNecessary);

    // Prepare connection:
    HttpURLConnection connection
      = (HttpURLConnection)this.composeURL(path).openConnection();
    connection.setRequestMethod("POST");
    connection.setRequestProperty("User-Agent", USER_AGENT_VALUE);
    connection.setInstanceFollowRedirects(false);
    connection.setDoOutput(true);

    // If necessary, add cookies:
    this.addCookies(connection);
 
    // Add parameters:
    this.addEntityBody(params, connection);

    // Connect:
    connection.connect();

    // Check response code:
    int responseCode = connection.getResponseCode();
    if ( responseCode >= 400 ) // Error
      throw new JapsClientException(connection);

    // If necessary, handle cookies:
    this.handleRecievedCookies(connection);

    // Redirect if necessary:
    if ( responseCode >= 300 ) // Redirected
      {
        // Follow redirect (by Http Get):
        String location = connection.getHeaderField("Location");
        connection = this.get(this.getPath(location), null, false);

        // Check response code again; fail if not OK:
        if ( connection.getResponseCode() !=  HttpURLConnection.HTTP_OK )
          throw new JapsClientException(connection);
      }

    // Login if necessary:
    if ( loginIfNecessary && this.checkLoginRequired(connection) )
      {
        int loginStatus = this.login();
        switch ( loginStatus )
          {
          case LOGIN_SUCCESSFUL:
            connection = this.post(path, params, false);
            if ( this.checkLoginRequired(connection) )
              throw new JapsClientException
                (connection, "rejected after successful login");
            break;
          case LOGIN_FAILED:
            throw new JapsClientException
              (connection, "login failed");
          case LOGIN_CANCELED:
            connection = null;
            break;
          default:
            throw new IllegalStateException
              ("Bug: unexpected login status: " + loginStatus);
          }
      }

    this.log(METHOD + " 2/2: connection = " + connection);
    return connection;
  }

  /**
   * <p>
   *   Returns a connection for <code>path</code> via the HTTP Post method.
   * </p>
   * <p>
   *   The entries of <code>params</code> are written to the entity body of the request in
   *   the url-encoded format. If <code>params</code> is <code>null</code> or empty, this is
   *   suppressed.
   * </p>
   * <p>
   *   The user is logged-in if the server requires so. This is done by {@link #login()}
   *   (which normally allowes several tries to log-in). If the login succeded, the request
   *   is repeated. If the server requires login again (which should not happen in normal
   *   operation), an exception is thrown. If the login failed, an exception is thrown. If
   *   the login was canceled by the user, no exception is thrown and <code>null</code> is
   *   returned.
   * </p>
   * <p>
   *   Same as {@link #post(String,Map,boolean) post(path, params, true)}.
   * </p>
   */

  public HttpURLConnection post (String path, Map params)
    throws JapsClientException, IOException, MalformedURLException
  {
    return this.post(path, params, true);
  }

  /**
   * <p>
   *   Returns a connection for <code>path</code> via the HTTP Post method. Can be used for
   *   file uploads. See
   *   <a href="http://www.ietf.org/rfc/rfc1867.txt">RFC 1867: Form-based File Upload in HTML</a>
   *   and <a href="http://www.ietf.org/rfc/rfc2046.txt">RFC 2046: MIME Part Two: Media Types</a>
   *   for details.
   * </p>
   * <p>
   *   The content of <code>file</code> is written to the entity body in the
   *   multipart/form-data format. If <code>file</code> is <code>null</code>, this is
   *   suppressed. <code>fileContentType</code> should be the MIME type of
   *   <code>file</code>.
   * </p>
   * <p>
   *   If <code>loginIfNecessary</code> is <code>true</code>, the user is logged-in if the
   *   server requires so. This is done by {@link #login()} (which normally allowes several
   *   tries to log-in). If the login succeded, the request is repeated. If the server
   *   requires login again (which should not happen in normal operation), an exception is
   *   thrown. If the login failed, an exception is thrown. If the login was canceled by the
   *   user, no exception is thrown and <code>null</code> is returned.
   * </p>
   */

  public HttpURLConnection post (String path, File file, String fileContentType,
                                 boolean loginIfNecessary)
    throws JapsClientException, IOException, FileNotFoundException, MalformedURLException
  {
    final String METHOD = "post(String,File,boolean)";
    this.log(METHOD + " 1/2: path = " + path +
             ", file = " + file +
             ", loginIfNecessary = " + loginIfNecessary);

    // Prepare connection:
    HttpURLConnection connection
      = (HttpURLConnection)this.composeURL(path).openConnection();
    connection.setRequestMethod("POST");
    connection.setRequestProperty("User-Agent", USER_AGENT_VALUE);
    connection.setInstanceFollowRedirects(false);
    connection.setDoOutput(true);

    // If necessary, add cookies:
    this.addCookies(connection);

    // Add file:
    if ( file != null )
      this.addEntityBody(file, fileContentType, connection);

    // Connect:
    connection.connect();

    // Check response code:
    int responseCode = connection.getResponseCode();
    if ( responseCode >= 400 ) // Error
      throw new JapsClientException(connection);

    // If necessary, handle cookies:
    this.handleRecievedCookies(connection);

    // Redirect if necessary:
    if ( responseCode >= 300 ) // Redirected
      {
        // Follow redirect (by Http Get):
        String location = connection.getHeaderField("Location");
        connection = this.get(this.getPath(location), null, false);

        // Check response code again; fail if not OK:
        if ( connection.getResponseCode() !=  HttpURLConnection.HTTP_OK )
          throw new JapsClientException(connection);
      }

    // Login if necessary:
    if ( loginIfNecessary && this.checkLoginRequired(connection) )
      {
        int loginStatus = this.login();
        switch ( loginStatus )
          {
          case LOGIN_SUCCESSFUL:
            connection = this.post(path, file, fileContentType, false);
            if ( this.checkLoginRequired(connection) )
              throw new JapsClientException
                (connection, "rejected after successful login");
            break;
          case LOGIN_FAILED:
            throw new JapsClientException
              (connection, "login failed");
          case LOGIN_CANCELED:
            connection = null;
            break;
          default:
            throw new IllegalStateException
              ("Bug: unexpected login status: " + loginStatus);
          }
      }

    this.log(METHOD + " 2/2: connection = " + connection);
    return connection;
  }

  /**
   * <p>
   *   Returns a connection for <code>path</code> via the HTTP Post method. Can be used for
   *   file uploads. See
   *   <a href="http://www.ietf.org/rfc/rfc1867.txt">RFC 1867: Form-based File Upload in HTML</a>
   *   and <a href="http://www.ietf.org/rfc/rfc2046.txt">RFC 2046: MIME Part Two: Media Types</a>
   *   for details.
   * </p>
   * <p>
   *   The content of <code>file</code> is written to the entity body in the
   *   multipart/form-data format. If <code>file</code> is <code>null</code>, this is
   *   suppressed. <code>fileContentType</code> should be the MIME type of
   *   <code>file</code>.
   * </p>
   * <p>
   *   The user is logged-in if the server requires so. This is done by {@link #login()}
   *   (which normally allowes several tries to log-in). If the login succeded, the request
   *   is repeated. If the server requires login again (which should not happen in normal
   *   operation), an exception is thrown. If the login failed, an exception is thrown. If
   *   the login was canceled by the user, no exception is thrown and <code>null</code> is
   *   returned.
   * </p>
   */

  public HttpURLConnection post (String path, File file, String fileContentType)
    throws JapsClientException, IOException, FileNotFoundException, MalformedURLException
  {
    return this.post(path, file, fileContentType, true);
  }

  /**
   * <p>
   *   Returns a connection for <code>path</code> via the HTTP Post method. Can be used for
   *   file uploads. See
   *   <a href="http://www.ietf.org/rfc/rfc1867.txt">RFC 1867: Form-based File Upload in HTML</a>
   *   and <a href="http://www.ietf.org/rfc/rfc2046.txt">RFC 2046: MIME Part Two: Media Types</a>
   *   for details.
   * </p>
   * <p>
   *   The content of <code>file</code> is written to the entity body in the
   *   multipart/form-data format. If <code>file</code> is <code>null</code>, this is
   *   suppressed.
   * </p>
   * <p>
   *   The MIME type of the file is guessed by {@link #guessContentType(File)}. If that
   *   failes, the MINE type is set to {@link #DEFAULT_CONTENT_TYPE}.
   * </p>
   * <p>
   *   The user is logged-in if the server requires so. This is done by {@link #login()}
   *   (which normally allowes several tries to log-in). If the login succeded, the request
   *   is repeated. If the server requires login again (which should not happen in normal
   *   operation), an exception is thrown. If the login failed, an exception is thrown. If
   *   the login was canceled by the user, no exception is thrown and <code>null</code> is
   *   returned.
   * </p>
   */

  public HttpURLConnection post (String path, File file)
    throws JapsClientException, IOException, FileNotFoundException, MalformedURLException
  {
    String fileContentType = this.guessContentType(file);
    if ( fileContentType == null )
      fileContentType = DEFAULT_CONTENT_TYPE;
    return this.post(path, file, fileContentType);
  }

  // --------------------------------------------------------------------------------
  // h1:  Login
  // --------------------------------------------------------------------------------

  /**
   * Performs a login dialog. Returns a {@link LoginDialogResult} object, or
   * <code>null</code> if the user has canceled the dialog. <code>afterFailure</code>
   * should be set to <code>true</code> if the dialog immediately follows a failed try to
   * login.
   */

  public abstract LoginDialogResult performLoginDialog (boolean afterFailure)
    throws JapsClientException;

  /**
   * Tries to login with <code>account</code> and <code>password</code>. If succeeded,
   * returns {@link #LOGIN_SUCCESSFUL}, otherwise {@link #LOGIN_FAILED}.
   */

  public int login (String account, char[] password)
    throws JapsClientException
  {
    final String METHOD = "login(String,char[])";
    this.log(METHOD + " 1/2: account = " + account +
             ", password = " + this.hidePassword(password));
    try
      {
        // Set request parameters:
        Map params = new HashMap();
        params.put(ACCOUNT_PARAM, account);
        params.put(PASSWORD_PARAM, password);
        params.put(RESOURCE_PARAM, this.composeURL(RESOURCE_VALUE).toString());

        // Do post request:
        HttpURLConnection connection = this.post(JapsPath.LOGIN, params, false);

        // Login status:
        int status =
          (this.checkLoginRequired(connection)
           ? LOGIN_FAILED
           : LOGIN_SUCCESSFUL);

        this.log(METHOD + " 2/2: status = " + this.loginStatusToString(status));
        
        return status;
      }
    catch (Exception exception)
      {
        throw new JapsClientException(exception);
      }
    finally
      {
        // Nulling password for security reasons;
        LoginDialogResult.clear(password);
      }
  }

  /**
   * Tries to login with the account and password given by <code>result</code>. If succeeded,
   * returns {@link #LOGIN_SUCCESSFUL}, otherwise {@link #LOGIN_FAILED}.
   */

  public int login (LoginDialogResult result)
    throws JapsClientException
  {
    return this.login(result.getAccount(), result.getPassword());
  }

  /**
   * Tries to login. If the login fails, tries again and again, but at most
   * {@link #maxLoginTries} times. Returns {@link #LOGIN_SUCCESSFUL} if the last login try
   * succeeded, {@link #LOGIN_FAILED} if the last login try failed, and
   * {@link #LOGIN_CANCELED} if the user has canceled the login dialog.
   */

  public int login ()
    throws JapsClientException
  {
    final String METHOD = "login()";
    this.log(METHOD + " 1/2");
    int count = 1;
    int status = -1;
    while ( status != LOGIN_SUCCESSFUL &&
            status != LOGIN_CANCELED &&
            ( count <= this.maxLoginTries || this.maxLoginTries == UNLIMITED ) )
      {
        LoginDialogResult result = this.performLoginDialog( status == LOGIN_FAILED );
        status = (result == null ? LOGIN_CANCELED : this.login(result));
        count++;
      }
    this.log(METHOD + " 2/2: status = " + this.loginStatusToString(status));
    return status;
  }

  // --------------------------------------------------------------------------------
  // h1:  Logout
  // --------------------------------------------------------------------------------

  /**
   * Logs out. Unlike the <code>login</code> methods, this method throws an exception if
   * the logout failes.
   */

  public void logout ()
    throws JapsClientException
  {
    final String METHOD = "logout()";
    this.log(METHOD + " 1/2");
    try
      {
        HttpURLConnection connection = this.get(JapsPath.LOGOUT);
        String error = connection.getHeaderField(JapsResponseHeader.ERROR);
        if ( error != null )
          throw new JapsClientException("Logout failed: " + error);
        this.log(METHOD + " 2/2");
      }
    catch (Exception exception)
      {
        throw new JapsClientException(exception);
      }
  }

  // --------------------------------------------------------------------------------
  // h1:  Maximal number of login tries
  // --------------------------------------------------------------------------------

  /**
   * Returns the maximum number of login tries. A value of {@link #UNLIMITED}
   * (<code>-1</code>) means the number is not limited. Default: {@link #UNLIMITED}.
   */

  public int getMaxLoginTries ()
  {
    return this.maxLoginTries;
  }

  /**
   * Sets the maximum number of login tries. A value of {@link #UNLIMITED} (<code>-1</code>)
   * means the number of tries is not limited. Default: {@link #UNLIMITED}.
   * @param maxLoginTries The new maximum number of login tries.
   * @throws IllegalArgumentException If <code>maxLoginTries</code> is not positive and not
   * equal to {@link #UNLIMITED}.
   */

  public void setMaxLoginTries (int maxLoginTries)
  {
    if ( maxLoginTries <= 0 && maxLoginTries != UNLIMITED )
      throw new IllegalArgumentException
        ("Invalid value for maximum number of login tries: " + maxLoginTries);
    this.maxLoginTries = maxLoginTries;
  }

  // --------------------------------------------------------------------------------
  // h1:  URLs
  // --------------------------------------------------------------------------------

  /**
   * Returns the prefix of the Japs server.
   */

  public String getUrlPrefix ()
  {
    return this.urlPrefix;
  }

  /**
   * Returns the (absolute) URL for a given (relative) path.
   */

  public URL composeURL (String path)
    throws MalformedURLException
  {
    return new URL(this.urlPrefix + (path.charAt(0) != '/' ? "/" : "") + path);
  }

  /**
   * Returns the (relative) path for a given (absolute) URL.
   */

  public String getPath (String url)
  {
    if ( url.equals(this.urlPrefix) )
      return "";
    String start = this.urlPrefix + '/';
    if ( !url.startsWith(start) )
      throw new IllegalArgumentException("Improper url: " + url + " (wrong prefix)");
    return url.substring(start.length());
  }

  // --------------------------------------------------------------------------------
  // h1:  Content types
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Returns the content type of <code>filename</code>. The content type is determined
   *   from the suffix of <code>filename</code>. The suffix is looked up in
   *   {@link #contentTypes}, and the corresponding value is returned.
   * </p>
   * <p>
   *   If <code>filename</code> has no suffix, or if no entry for the suffix is found in 
   *   {@link #contentTypes}, <code>null</code> is returned.
   * </p>
   */

    public String guessContentType (String filename)
  {
    int pos = filename.lastIndexOf('.');
    if ( pos == -1 || pos == filename.length()-1 )
      return null;
    String suffix = filename.substring(pos+1);
    return this.contentTypes.getProperty(suffix);
  }

  /**
   * <p>
   *   Returns the content type of <code>file</code>. The content type is determined from
   *   the suffix of <code>file</code>. The suffix is looked up in {@link #contentTypes},
   *   and the corresponding value is returned.
   * </p>
   * <p>
   *   If <code>file</code> has no suffix, or if no entry for the suffix is found in 
   *   {@link #contentTypes}, <code>null</code> is returned.
   * </p>
   */

  public String guessContentType (File file)
  {
    return this.guessContentType(file.getName());
  }

  /**
   * Returns the content type for <code>suffix</code>. The suffix is looked up in
   * {@link #contentTypes}. If no entry for the suffix is found, <code>null</code> is
   * returned.
   */

  public String getContentTypeForSuffix (String suffix)
  {
    return this.contentTypes.getProperty(suffix);
  }

  /**
   * Sets the content type for <code>suffix</code>.
   */

  public void setContentTypeForSuffix (String suffix, String contentType)
  {
    this.contentTypes.setProperty(suffix, contentType);
  }

  /**
   * Removes the content type for <code>suffix</code>.
   */

  public void removeContentTypeForSuffix (String suffix, String contentType)
  {
    this.contentTypes.remove(suffix);
  }

  /**
   * Returns all suffixes for which content types are specified.
   */

  public Enumeration getContentTypeSuffixes ()
  {
    return this.contentTypes.propertyNames();
  }

  // --------------------------------------------------------------------------------
  // h1:  Logging
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Writes a log message.
   * </p>
   * <p>
   *   This implementation does nothing. Extending classes can overwrite this to implement a
   *   logging mechanism.
   * </p>
   */

  protected void log (String message)
  {
    // Does nothing.
  }

  // --------------------------------------------------------------------------------
  // h1:  Utilities to create the request
  // --------------------------------------------------------------------------------

  /**
   * Url-encodes the entries of <code>params</code> and writes them to the stream
   * <code>out</code>.
   */

  protected void printUrlEncoded (Map params, PrintStream out)
  {
    Iterator iterator = params.entrySet().iterator();
    boolean separate = false;
    while ( iterator.hasNext() )
      {
        Map.Entry param = (Map.Entry)iterator.next();
        String name = (String)param.getKey();
        Object value = param.getValue();
        if ( separate ) out.print('&');
        out.print(this.urlEncoder.encode(name));
        out.print('=');
        if ( value instanceof String )
          out.print(this.urlEncoder.encode((String)value));
        else if ( value instanceof char[] )
          {
            char[] chars = (char[])value;
            for (int i = 0; i < chars.length; i++)
              out.print(this.urlEncoder.encode(chars[i]));
          }
        else
          throw new IllegalArgumentException
            ("Value of parameter \"" + name + "\" is neither String nor char[]: " + value);
        separate = true;
      }
    out.flush();
  }

  /**
   * Prints the content of <code>file</code> to the stream <code>out</code>.
   */

  protected void print (File file, PrintStream out)
    throws FileNotFoundException,IOException
  {
    FileInputStream in = new FileInputStream(file);
    byte[] buffer = new byte[1024];
    int count;
    while ( (count = in.read(buffer)) != -1 )
      out.write(buffer, 0, count);
    out.flush();
    in.close();
  }

  /**
   * <p>
   *   Url-encodes the entries of <code>params</code> and writes them to the entity body of
   *   <code>connection</code>. The <code>"Content-Type"</code> header of
   *   <code>connection</code>is set to <code>"application/x-www-form-urlencoded"</code>.
   * </p>
   */

  protected void addEntityBody (Map params, HttpURLConnection connection)
    throws IOException
  {
    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    PrintStream entityBody = new PrintStream(connection.getOutputStream());
    this.printUrlEncoded(params, entityBody);
    entityBody.close();
  }

  /**
   * Writes the content of <code>file</code> to the entity body of <code>connection</code>
   * in the multipart/form-data format. The <code>"Content-Type"</code> header of
   * <code>connection</code>is set to <code>"multipart/form-data"</code>.
   * <code>fileContentType</code> should be the MIME type of <code>file</code>.
   */

  protected void addEntityBody (File file, String fileContentType, HttpURLConnection connection)
    throws FileNotFoundException, IOException
  {
    String boundary = this.createMultipartBoundary(file);
    connection.setRequestProperty
      ("Content-Type","multipart/form-data; boundary=" + boundary);
    PrintStream entityBody = new PrintStream(connection.getOutputStream());
    entityBody.print
      ("--" + boundary + "\r\n" +
       "Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"\r\n" +
       "Content-Type: " + fileContentType + "\r\n\r\n");
    this.print(file, entityBody);
    entityBody.print("\r\n--" + boundary + "--\r\n");
    entityBody.flush();
    entityBody.close();
  }

  /**
   * Url-encodes the entries of <code>params</code> and returns the result as a string.
   */

  protected String urlEncode (Map params)
    throws IOException
  {
    AsciiStringOutputStream asciiOut = new AsciiStringOutputStream();
    this.printUrlEncoded(params, new PrintStream(asciiOut));
    return asciiOut.toString();
  }

  // --------------------------------------------------------------------------------
  // h1:  Utilities to create the multipart boundaries
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Creates a multipart boundary delimiter suited for <code>file</code>.
   * </p>
   */

  protected String createMultipartBoundary (File file)
    throws FileNotFoundException, IOException
  {
    final String METHOD = "createMultipartBoundary";
    this.log(METHOD + " 1/2: file = " + file);
    String boundary = null;
    int length = MULTPART_BOUNDARY_MIN_SIZE-1;
    int tries = 0;
    while ( ++tries <= MULTPART_BOUNDARY_MAX_TRIES && boundary == null )
      {
        if ( length < MULTPART_BOUNDARY_MAX_SIZE ) length++;
        boundary = this.createRandomString(MULTPART_BOUNDARY_CHARSPACE, length);
        if ( this.checkContained(file, this.stringToByteArray(boundary)) )
          boundary = null;
      }
    this.log(METHOD + " 2/2: boundary = " + boundary + ", tries = " + tries);
    return boundary;
  }

  /**
   * <p>
   *   Checks if a certain byte sequence is contained in a given file.
   * </p>
   * <p>
   *   Auxiliary method for composing boundaries for multipart post requests.
   * </p>
   */

  protected boolean checkContained (File file, byte[] byteArray)
    throws FileNotFoundException, IOException
  {
    if ( byteArray.length > file.length() )
      return false;
    byte[] region = new byte[byteArray.length];
    Arrays.fill(region, (byte)0);
    FileInputStream in = new FileInputStream(file);
    boolean contained = false;
    long count = 0;
    int next;
    while ( !contained && (next = in.read()) != -1 )
      {
        count++;
        this.shift(region, (byte)next);
        contained = ( count >= byteArray.length && this.checkEqual(region, byteArray) );
      }
    return contained;
  }

  /**
   * <p>
   *   Shifts the elements of <code>byteArray</code> one position to the left, and sets the 
   *   last element to <code>b</code>.
   * </p>
   * <p>
   *   Auxiliary method, needed in {@link #checkContained}.
   * </p>
   */

  protected void shift (byte[] byteArray, byte b)
  {
    int last = byteArray.length-1;
    for (int i = 0; i < last; i++)
      byteArray[i] = byteArray[i+1];
    byteArray[last] = b;
  }

  /**
   * <p>
   *   Returns <code>true</code> if <code>byteArray1</code> and <code>byteArray2</code> are
   *   equal, <code>false</code> otherwise.
   * </p>
   * <p>
   *   Auxiliary method, needed in {@link #checkContained}.
   * </p>
   */

  protected boolean checkEqual (byte[] byteArray1, byte[] byteArray2)
  {
    boolean equal = true;
    for (int k = 0; k < byteArray1.length && equal; k++)
      equal = ( byteArray1[k] == byteArray2[k] );
    return equal;
  }

  /**
   * <p>
   *   Creates a random string. The string is made of the the characters in
   *   <code>chars</code>. Its length is <code>length</code>.
   * </p>
   * <p>
   *   Auxiliary method for composing boundaries for multipart post requests.
   * </p>
   */

  protected String createRandomString (String chars, int length)
  {
    Random random = new Random();
    StringBuffer buffer = new StringBuffer();
    int maxPos = chars.length()-1;
    for (int i = 0; i < length; i++)
      {
        int pos = random.nextInt(maxPos);
        buffer.append(chars.charAt(pos));
      }
    return buffer.toString();
  }

  /**
   * <p>
   *   Converts <code>string</code> into an array of bytes.
   * </p>
   */

  protected byte[] stringToByteArray (String string)
  {
    byte[] byteArray = new byte[string.length()];
    for (int i = 0; i < string.length(); i++)
      {
        int value = (int)string.charAt(i);
        if ( value > 255 )
          throw new IllegalArgumentException
            ("Illegal charcter: " + value + "(position " + i + ")");
        byteArray[i] = (byte)value;
      }
    return byteArray;
  }

  // --------------------------------------------------------------------------------
  // h1:  Analysing the response
  // --------------------------------------------------------------------------------

  /**
   * Checks the server resonse code and throws an exception if not <code>200</code>
   * (<code>"OK"</code>).
   */

  protected void checkResponseCode (HttpURLConnection connection)
    throws JapsClientException, IOException 
  {
    int responseCode = connection.getResponseCode();
    if ( responseCode != HttpURLConnection.HTTP_OK )
      {
        String responseMessage = connection.getResponseMessage();
        throw new JapsClientException
          ("Failed to connect to: " + connection.getURL() +
           " (" + responseCode +
           (responseMessage != null ? " " + responseMessage : "") +
           ")");
      }
  }

  /**
   * Returns <code>true</code> if login is required for <code>connection</code>, otherwise
   * <code>false</code>. Whether login is required is decided by checking the non-standard
   * response header
   * {@link JapsResponseHeader#LOGIN_REQUIRED JapsResponseHeader.LOGIN_REQUIRED}.
   * The following rules apply:
   * <ul>
   *  <li>
   *    If the header does not exist, <code>false</code> is returned.
   *  </li>
   *  <li>
   *    Otherwise, if the header value is <code>"yes"</code> or <code>"true"</code>,
   *    <code>true</code> is returned.
   *  </li>
   *  <li>
   *    Otherwise, if the header value is <code>"no"</code> or <code>"false"</code>,
   *    <code>false</code> is returned.
   *  </li>
   *  <li>
   *    Otherwise an exception is thrown.
   *  </li>
   * </ul>
   */

  protected boolean checkLoginRequired (HttpURLConnection connection)
    throws JapsClientException
  {
    String value = connection.getHeaderField(JapsResponseHeader.LOGIN_REQUIRED);
    if ( value == null )
      return false;
    else if ( value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("true") )
      return true;
    else if ( value.equalsIgnoreCase("no") || value.equalsIgnoreCase("false") )
      return false;
    else
      throw new JapsClientException
        ("Unknown " + JapsResponseHeader.LOGIN_REQUIRED + "value: " + value);
  }

  // --------------------------------------------------------------------------------
  // h1:  Utilities for log messages
  // --------------------------------------------------------------------------------

  /**
   * Convenience method to replace a password (or another confidential piece of text) by a
   * sequence of asterisks. Used for log messages.
   */

  protected String hidePassword (char[] password)
  {
    char[] chars = new char[password.length];
    Arrays.fill(chars, '*');
    return new String(chars);
  }

  /**
   * Convenience method to convert a numeric status code into a self-explanatory
   * string. Used for log messages.
   */

  protected String loginStatusToString (int loginStatus)
  {
    switch ( loginStatus )
      {
      case LOGIN_SUCCESSFUL:
        return "LOGIN_SUCCESSFUL";
      case LOGIN_FAILED:
        return "LOGIN_FAILED";
      case LOGIN_CANCELED:
        return "LOGIN_CANCELED";
      default:
        return "[unknown status code: " + loginStatus + "]";
      }
  }

  /**
   * <p>
   *   Convenience method to convert parameters to an informative string. Used for log
   *   messages.
   * </p>
   * <p>
   *   Map values that are of type <code>char[]</code> are regarded as confidential and
   *   protected by the {@link #hidePassword} method.
   * </p>
   */

  protected String paramsToString (Map params)
  {
    if ( params == null )
      return null;
    StringBuffer buffer = new StringBuffer("{");
    Iterator iterator = params.entrySet().iterator();
    boolean separate = false;
    while ( iterator.hasNext() )
      {
        if ( separate ) buffer.append(", ");
        Map.Entry param = (Map.Entry)iterator.next();
        buffer.append(param.getKey().toString()).append('=');
        Object value = param.getValue();
        buffer.append
          (value instanceof char[]
           ? hidePassword((char[])value)
           : value.toString());
        separate = true;
      }
    buffer.append('}');
    return buffer.toString();
  }
}
