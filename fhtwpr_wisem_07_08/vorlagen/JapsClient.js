
function JapsClient (passedUrlPrefix)
{
  // --------------------------------------------------------------------------------
  // Global variables
  // --------------------------------------------------------------------------------

  /**
   * The value of the "user-agent" header.
   */

  var USER_AGENT_VALUE = "JapsClient/JS";

  /**
   * Indicates a successful login.
   */

  var LOGIN_SUCCESSFUL = 0;

  /**
   * Indicates a failed login.
   */

  var LOGIN_FAILED = 1;

  /**
   * Indicates that the uses has canceled the login dialog.
   */

  var LOGIN_CANCELED = 2;

  /**
   * The request parameter containing the account (<code>"name"</code>).
   */

  var ACCOUNT_PARAM = "name";

  /**
   * The request parameter containing the password (<code>"password"</code>).
   */

  var PASSWORD_PARAM = "password";

  /**
   * The request parameter containing the resource (<code>"resource"</code>).
   */

  var RESOURCE_PARAM = "resource";

  /**
   * The url path of the resource requested in the login request
   * (<code>"protected/auth/login-successful"</code>).
   */

  var RESOURCE_VALUE = "protected/auth/login-successful";

  /**
   * Unlimited number of login tries. Value: <code>-1</code>
   */

  var UNLIMITED = -1;

  /**
   * Response header indicating whether login os required
   */

  var LOGIN_REQUIRED_HEADER = "X-Mumie-Login-Required";

  /**
   * Path for login (<code>"public/auth/login"</code>). This is the path where to send the
   * login form data.
   */

  var LOGIN_PATH = "public/auth/login";

  /**
   * Path for logout (<code>"public/auth/logout"</code>).
   */

  var LOGOUT_PATH = "public/auth/logout";

  /**
   * The url prefix of the Japs server.
   */

  var urlPrefix = passedUrlPrefix;

  /**
   * Maximum number of login tries. A value of 1 means the number is not limited.
   * Default: -1.
   */

  var maxLoginTries = -1;

  // --------------------------------------------------------------------------------
  // HTTP Get and Post requests
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Opens a connection for the specified path using the HTTP Get method. Returns the
   *   XMLHttpRequest object representing the connection, or null in special cases
   *   (see below).
   * </p>
   * <p>
   *   If the user is not * logged-in and <code>loginIfNecessary</code> id true, the
   *   user is given an opportunity * to login. Login is done by means of the
   *   <code>login</code> method. If the login * succeded, the request is repeated. If
   *   the server requires login again (which should not happen in normal operation),
   *   an exception is thrown. If the login failed, an * exception is thrown either.
   *   If the login was canceled by the user, no exception is thrown and <code>null</code>
   *   is returned.
   * </p>
   * 
   * @param  path
   *    Part of the URL after the prefix
   *
   * @param  params
   *     Request parameters, given as an associative array (key/value pairs).
   *     May be omitted or null.
   *
   * @param  handler
   *     Event handler for state changes of the XMLHttpRequest object. May be
   *     omitted or null. If specified, the request is asynchronous; otherwise,
   *     it is synchronous.
   *
   * @param loginIfNecessary
   *     Whether the user is given an opportunity to login if the server requires
   *     so. May be omitted or null; default is <code>true<code>.
   */

  this.get = function (path, params, handler, loginIfNecessary)
    {
      // TODO
    };

  /**
   * <p>
   *   Opens a connection for the specified path using the HTTP Post method. Returns the
   *   XMLHttpRequest object representing the connection, or null in special cases
   *   (see below).
   * </p>
   * <p>
   *   If the user is not * logged-in and <code>loginIfNecessary</code> id true, the
   *   user is given an opportunity * to login. Login is done by means of the
   *   <code>login</code> method. If the login * succeded, the request is repeated. If
   *   the server requires login again (which should not happen in normal operation),
   *   an exception is thrown. If the login failed, an * exception is thrown either.
   *   If the login was canceled by the user, no exception is thrown and <code>null</code>
   *   is returned.
   * </p>
   * 
   * @param  path
   *    Part of the URL after the prefix
   *
   * @param  params
   *     Request parameters, given as an associative array (key/value pairs).
   *     May be omitted or null.
   *
   * @param  handler
   *     Event handler for state changes of the XMLHttpRequest object. May be
   *     omitted or null. If specified, the request is asynchronous; otherwise,
   *     it is synchronous.
   *
   * @param loginIfNecessary
   *     Whether the user is given an opportunity to login if the server requires
   *     so. May be omitted or null; default is <code>true<code>.
   */

  this.post = function (path, params, handler, loginIfNecessary)
    {
      // TODO
    };

  // --------------------------------------------------------------------------------
  // Utilities
  // --------------------------------------------------------------------------------

  /**
   * Returns <code>true</code> if login is required for <code>connection</code>, otherwise
   * <code>false</code>. Whether login is required is decided by checking the non-standard
   * response header <code>X-Mumie-Login-Required</code>. The following rules apply:
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
   *    Otherwise an error is signaled.
   *  </li>
   * </ul>
   */

  var checkLoginRequired = function (connection)
    {
      var value = connection.getResponseHeader(LOGIN_REQUIRED_HEADER);
      if ( !value )
        return false;
      else if ( value == "yes" || value == "true" )
        return true;
      else if ( value == "no" || value == "false" )
        return false;
      else
        // TODO: Signal error
     };

  /**
   * Performs a login dialog. Returns an associative array conatining account and
   * password, or null if the user has canceled the dialog. <code>afterFailure</code>
   * should be set to <code>true</code> if the dialog immediately follows a failed try
   * to login.
   */

  var performLoginDialog = function (afterFailure)
    {
      // TODO
    };

  /**
   * <p>
   *   Tries to login and returns the login status. This method behaves differently
   *   depending on whether the parameters <code>account</code> and <code>password</code>
   *   are specified or not:
   * </p>
   * <p>
   *   If <code>account</code> and <code>password</code> are both specified, the
   *   method tries to login with that data. In case of success, it returns
   *   <code>LOGIN_SUCCESSFUL</code>, otherwise <code>LOGIN_FAILED</code>.
   * </p>
   *   Otherwise, <code>performLoginDialog</code> is called to query account and
   *   password. Provided the user has not canceled the login dialog, the method
   *   tries to login with the requested data afterwards. If the login fails, the
   *   procedure is repeated until login is successful, the maximum number of login
   *   tries (<code>maxLoginTries</code>) is reached, or the user cancels the login
   *   dialog.
   * <p>
   */

  this.login = function (account, password)
    {
      if ( account && password )
        {
          // Set request parameters:
          var params = new Array ();
          params[ACCOUNT_PARAM] = account;
          params[PASSWORD_PARAM] = password;
          params[RESOURCE_PARAM] = this.urlPrefix + RESOURCE_VALUE;

          //  post request:
          var connection = this.post(LOGIN_PATH, params, null, false);

          // Login status:
          var status =
            (this.checkLoginRequired(connection)
             ? LOGIN_FAILED
             : LOGIN_SUCCESSFUL);

          return status;
        }
      else
        {
          var count = 1;
          var status = -1;
          while ( status != LOGIN_SUCCESSFUL &&
                  status != LOGIN_CANCELED &&
                  ( count <= this.maxLoginTries || this.maxLoginTries == UNLIMITED ) )
            {
              var result = this.performLoginDialog( status == LOGIN_FAILED );
              status =
                (result == null
                 ? LOGIN_CANCELED :
                 this.login(result[ACCOUNT_PARAM], result[PASSWORD_PARAM]));
              count++;
            }
          return status;
        }
    };
}
