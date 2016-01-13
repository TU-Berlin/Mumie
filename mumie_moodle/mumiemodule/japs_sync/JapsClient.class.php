<?php

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

?>
<?php



abstract class JapsClient
{  

  // --------------------------------------------------------------------------------
  // Static constants
  // --------------------------------------------------------------------------------

  /**
   * Name of the response header indicating that the user must login
   * (<code>"X-Mumie-Login-Required"</code>).
   */
   const LOGIN_REQUIRED_HEADER = "X-Mumie-Login-Required";

  /**
   * Indicates a successful login.
   */
   const LOGIN_SUCCESSFUL = 0;

  /**
   * Indicates a failed login.
   */
   const LOGIN_FAILED = 1;
  
  /**
   * Indicates that the uses has canceled the login dialog.
   */
   const LOGIN_CANCELED = 2;
  
  /**
   * The url path for login (<code>"login"</code>). Together with {@link #urlPrefix}, this
   * is the url where to send the login form data.
   */
   const LOGIN_PATH = "public/auth/login";
  
  /**
   * The request parameter containing the account (<code>"name"</code>).
   */
   const ACCOUNT_PARAM = "name";
  
  /**
   * The request parameter containing the password (<code>"password"</code>).
   */
   const PASSWORD_PARAM = "password";
  
  /**
   * The request parameter containing the resource (<code>"resource"</code>).
   */
   const RESOURCE_PARAM = "resource";
  
  /**
   * The url path of the resource requested in the login request
   * (<code>"protected/auth/login-successful"</code>).
   */
   const RESOURCE_VALUE = "protected/auth/login-successful";
  
  /**
   * Unlimited number of login tries. Value: <code>-1</code>
   */
   const UNLIMITED = -1;
  
  /**
   * Content type suited for text files (<code>"text/plain"</code>).
   */
   const TEXT_CONTENT_TYPE = "text/plain";
  
  /**
   * Content type suited for binary files (<code>"application/octet-stream"</code>).
   */
   const BINARY_CONTENT_TYPE = "application/octet-stream";
  
  /**
   * Default content type for file uploads ({@link #BINARY_CONTENT_TYPE}).
   */
   const DEFAULT_CONTENT_TYPE = "BINARY_CONTENT_TYPE";

  /**
   * The common prefix of the log messages (<code>"JapsClient"</code>).
   */
   const LOG_PREFIX = "JapsClient";

  /**
   * The default timestamp pattern (<code>"yyyy-MM-dd HH:mm:ss S"</code>)
   */
   const DEFAULT_TIMESTAMP_PATTERN = "Y-m-d H:i:s";


  // --------------------------------------------------------------------------------
  // Global variables
  // --------------------------------------------------------------------------------

  /** 
   * The url prefix of the Japs server.
   */
   protected $urlPrefix = "";

  /**
   * The url prefix of the Japs server without Port.
   */
   protected $urlPrefixWithoutPort = "";

  /**
   * The Port for the connection. Default: Port 80.
   */
   protected $urlPort = 80;

  /**
   * Contains the String "cocoon/" to add to the path
   */
   protected $cocoon = "";                       
  
  /**
   * Maximum number of login tries. A value of {@link #UNLIMITED} (<code>-1</code>) means the
   * number is not limited. Default: {@link #UNLIMITED}.
   */
   protected $maxLoginTries = self::UNLIMITED;
  
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
   protected $contentTypes = array();

  /**
   * Save the filename, when a logfile is specified.
   */
   protected $logFile = "";

  /**
   * Save received cookies.
   */
   protected $cookies = array();

  // --------------------------------------------------------------------------------
  // Constructor
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Creates a new Japs client with <code>urlPrefix</code> as server url prefix and <code>filename</code> as a logfile.
   * </p>
   * <p>
   *   Extending classes should call this in their constructors.
   * </p>
   */

  public function JapsClient ($urlPrefix, $logFile = "")
  { 
    // URL prefix:
    if ($urlPrefix == "" || $urlPrefix == "/" )
    {  throw new Exception("Url prefix null or void");  }
    $lastIndex = strlen($urlPrefix) -1;
    if ( substr($urlPrefix,$lastIndex,$lastIndex) == "/" )
    {  $urlPrefix = substr($urlPrefix,0,$lastIndex);  }
    $urlPrefixArray = explode("/",$urlPrefix);
    if(!strpos($urlPrefixArray[2],":")) 
    {  $this->urlPrefixWithoutPort = $urlPrefixArray[2];  }
    elseif(strpos($urlPrefixArray[2],":"))
    {  $helparr = explode(":",$urlPrefixArray[2]);
       $this->urlPrefixWithoutPort = $helparr[0];
       $this->urlPort = $helparr[1]; 
    } 
    if(array_key_exists(3,$urlPrefixArray))
    {  $this->cocoon = $urlPrefixArray[3].'/';  }

    $this->urlPrefix = $urlPrefix;

    $this->logFile = $logFile;
    if(file_exists($this->logFile))
    {  unlink($this->logFile);  }
       


    // Content types table:
    $this->contentTypes["txt"] = "text/plain";
    $this->contentTypes["xml"] = "text/xml";
    $this->contentTypes["xhtml"] = "text/xml";
    $this->contentTypes["xsl"] = "text/xsl";
    $this->contentTypes["html"] = "text/html";
    $this->contentTypes["css"] = "text/css";
    $this->contentTypes["tex"] = "text/tex";
    $this->contentTypes["jpg"] = "image/jpeg";
    $this->contentTypes["png"] = "image/png";
    $this->contentTypes["zip"] = "application/zip";
    $this->contentTypes["jar"] = "application/x-java-archive";
  }


  // --------------------------------------------------------------------------------
  // Hooks for cookie handling
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   If necessary, reads the cookies revieved from the server and stores them in cookies.
   * </p>
   */
   protected function handleRecievedCookies($connect) //throws JapsClientException
   {
      if(!array_key_exists('cookies',$connect['head']))
      {
         //echo "kein Cookie zum speichern vorhanden";
      }
      else
      {
         $this->cookies = $connect['head']['cookies'];
      }
   }

  /**
   * <p>
   *   If necessary, sets the cookies that should be sent back to the server.
   * </p>
   */

   protected function addCookies () //throws JapsClientException
   { 
      $cookieString = "";
   
      if(empty($this->cookies))
      {  
         return false;
      }
      else
      {
         foreach($this->cookies as $cookie)
         {
            if(is_array($cookie))
            {
               if(!array_key_exists('name',$cookie) || !array_key_exists('value',$cookie))
               { echo "Bad Cookie saved!!";  }
               else
               {
                  $cookieString .= urlencode($cookie['name']).'='.urlencode($cookie['value']).';';
               } 
            }
         } 
      }
      return $cookieString;
  }
   
  // --------------------------------------------------------------------------------
  // HTTP Get and Post requests
  // --------------------------------------------------------------------------------

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
/*========================================*\
                                    GET
\*========================================*/

  public function get($path, $params, $loginIfNecessary)
    //throws JapsClientException, IOException, MalformedURLException
  {
     $numargs = func_num_args();
     if($numargs == 1)   //get($path)
     {  
        $path = func_get_arg(0);
        return $this->get($path,$params = array());
     }
     elseif($numargs == 2)  //get($path, $params)
     {
        $path = func_get_arg(0);
        $params = func_get_arg(1);
        return $this->get($path,$params, true);
     }
     elseif($numargs == 3)  //get($path, $params, $loginIfNecessary)
     {  
        $path = func_get_arg(0);
        $params = func_get_arg(1);
        $loginIfNecessary = func_get_arg(2);

        $newpath = $this->cocoon.$path; // add "cocoon/" to path

        $METHOD = "get(String,Array,Boolean)";
        $this->logg($METHOD." 1/2: path = ".$newpath.", params = ".$this->paramsToString($params).", loginIfNecessary = ");
        $this->logg($loginIfNecessary ? 'true<br/>' : 'false<br/>');
    
        // Prepare connection:
       $connection = new HttpURLConnection($this->urlPrefixWithoutPort, $this->urlPort,false,"JapsClient/PHP");
       // Prepare params
       $params = $this->url_Encode($params);
       // Connect and add Cookies:
       $connect = $connection->get($newpath,$params,$this->addCookies());
       //$this->logg(var_dump($connect));
       // Check response code:
       if( $connect["head"]["code"] >= 400)  // Error
       {  echo "Could not connect";
           return; //sonst wird login-Abschnitt ausgefuehrt!!!
       }

       // If necessary, handle cookies:
       $this->handleRecievedCookies($connect);

       // Redirect if necessary:
       if (  $connect["head"]["code"] >= 300 && $connect["head"]["code"] < 400) // Redirected
       {  
          // Follow redirect (by Http Get):
          $location = $connect["head"]["location"]["uri"];
          $params = $connect["head"]["location"]["parameters"];
          $connect = $this->get($this->getPath($location), $params, false);
        
          // Check response code again; fail if not OK:
          if( $connect["head"]["code"] != 200)  // Code 200: HTTP_OK?
          {  echo "Could not connect";
             return; //sonst wird login-Abschnitt ausgefuehrt!!!
          }
       }

       // Login if necessary:
       if ( $loginIfNecessary && $this->checkLoginRequired($connect))
       {   
          $loginStatus = $this->login();
        
          switch ( $loginStatus )
          {  
             case self::LOGIN_SUCCESSFUL:
             {
                $connect = $this->get($path, $params, false);
                if ($this->checkLoginRequired($connect))
                  echo "rejected after successful login";
                break;
             }
             case self::LOGIN_FAILED:
               echo "login failed";
               break;
             case self::LOGIN_CANCELED:
               $connection = null;
               echo "LOGIN_CANCELED";
               break;
             default:
               echo "Bug: unexpected login status: ".$loginStatus;
          }
       }

       $this->logg("$METHOD 2/2: path = $newpath, connection = ");
       $this->logg($connection);
       $this->logg('<br/>');
       return $connect;
  }
}

/*========================================*\
                                    POST
\*========================================*/

  public function post()
    //throws JapsClientException, IOException, MalformedURLException
  {
     $numargs = func_num_args();
     if($numargs == 2)  //post($path, $params)
     {
        $path = func_get_arg(0);
        $params = func_get_arg(1);
        return $this->post($path,$params, true);
     }
     elseif($numargs == 3)  //post($path, $params, $loginIfNecessary)
     {  
        $path = func_get_arg(0);
        $params = func_get_arg(1);
        $loginIfNecessary = func_get_arg(2);

        $newpath = $this->cocoon.$path; // add "cocoon/" to path

        $METHOD = "post(String,Array,Boolean)";
        $this->logg($METHOD." 1/2: path = ".$newpath.", params = ".$this->paramsToString($params).", loginIfNecessary = ");
        $this->logg($loginIfNecessary ? 'true<br/>' : 'false<br/>');

        // Prepare connection:
        $connection = new HttpURLConnection($this->urlPrefixWithoutPort, $this->urlPort,false,"JapsClient/PHP");
        // Prepare params:
        $params = $this->url_Encode($params);
        // Connect and add Cookies:
        $connect = $connection->post($newpath,$params,$this->addCookies()); 
        //$this->logg(var_dump($connect));
        // Check response code:
        /*if( $connect["head"]["code"] == 200)  // No Error
        {
          return; //sonst wird login-Abschnitt ausgefuehrt!!!
        }*/
        if( $connect["head"]["code"] >= 400)  // Error
        {
          echo "Could not connect";
          return; //sonst wird login-Abschnitt ausgefuehrt!!!
        }

        // If necessary, handle cookies:
        $this->handleRecievedCookies($connect);

        // Redirect if necessary:
        if(  $connect["head"]["code"] >= 300 && $connect["head"]["code"] < 400) // Redirected
        {  
           // Follow redirect (by Http Get):
           $location = $connect["head"]["location"]["uri"];
           $params2 = $connect["head"]["location"]["parameters"];
           $connect = $this->get($this->getPath($location), $params2, false);

           // Check response code again; fail if not OK:
           if( $connect["head"]["code"] != 200)  // Code 200: HTTP_OK?
           {  echo "Could not connect";
              return; //sonst wird login-Abschnitt ausgefuehrt!!!
           }
        }

        // Login if necessary:
        if ( $loginIfNecessary && $this->checkLoginRequired($connect))
        {  
           $loginStatus = $this->login();
        
           switch ( $loginStatus )
           {
              case self::LOGIN_SUCCESSFUL:
              {
                 $connect = $this->post($path, $params, false);
                 if($this->checkLoginRequired($connect))
                   echo "rejected after successful login";
                 break;
              }
              case self::LOGIN_FAILED:
                echo "login failed";
                break;
              case self::LOGIN_CANCELED:
                $connection = "";
                echo "LOGIN_CANCELED";
                break;
              default:
                echo "Bug: unexpected login status: ".$loginStatus;
           }
        }  

        $this->logg($METHOD." 2/2: path = $newpath, connection = ");
        $this->logg($connection);
        return $connect;
   }
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
   *   requires login again (which should not happen in normal operation), an excehttp://www.tutorials.de/forum/php-tutorials/226367-http-ohne-curl-version-1-1-a.htmlption is
   *   thrown. If the login failed, an exception is thrown. If the login was canceled by the
   *   user, no exception is thrown and <code>null</code> is returned.
   * </p>
   */

/*========================================*\
                    POST-FOR-FILE-UPLOAD
\*========================================*/

  public function postForFileUpload ()
    //throws JapsClientException, IOException, FileNotFoundException, MalformedURLException
  {
     $numargs = func_num_args();
     if($numargs == 2)  //postForFileUpload ($path, $filename)
     {
        $path = func_get_arg(0);
        $filename = func_get_arg(1);
        $fileContentType = $this->guessContentType($filename);
        if($fileContentType == null)
          $fileContentType = self::DEFAULT_CONTENT_TYPE;

        return $this->postForFileUpload($path, $filename, $fileContentType);
        
     }
     elseif($numargs == 3)  //postForFileUpload ($path, $filename, $fileContentType)
     {
        $path = func_get_arg(0);
        $filename = func_get_arg(1);
        $fileContentType = func_get_arg(2);
        return $this->postForFileUpload($path, $filename, $fileContentType, true);
     }
     elseif($numargs == 4)  //postForFileUpload ($path, $filename, $fileContentType, $loginIfNecessary)
     {
        $path = func_get_arg(0);
        $filename = func_get_arg(1);
        $fileContentType = func_get_arg(2);
        $loginIfNecessary = func_get_arg(3);

        $newpath = $this->cocoon.$path; // add "cocoon/" to path

        $METHOD = "post(String,File,String,Boolean)";
        $this->logg($METHOD." 1/2: path = ".$newpath.", file = ".$filename.", fileContentType = $fileContentType, loginIfNecessary = ");
        $this->logg($loginIfNecessary ? 'true<br/>' : 'false<br/>');

        // Prepare connection:
        $connection = new HttpURLConnection($this->urlPrefixWithoutPort, $this->urlPort,false,"JapsClient/PHP");
        // Connect and add Cookies:
        $connect = $connection->post($newpath, false, $this->addCookies(), "file=$filename", $fileContentType);
        
        // Check response code:
        if( $connect["head"]["code"] >= 400)  // Error
        {
          echo "Could not connect";
          return; //sonst wird login-Abschnitt ausgefuehrt!!!
        }

        // If necessary, handle cookies:
        $this->handleRecievedCookies($connect);

        // Redirect if necessary:
        if(  $connect["head"]["code"] >= 300 && $connect["head"]["code"] < 400) // Redirected
        {  
           // Follow redirect (by Http Get):
           $location = $connect["head"]["location"]["uri"];
           $connect = $this->get($this->getPath($location), $params = array(), false);

           // Check response code again; fail if not OK:
           if( $connect["head"]["code"] != 200)  // Code 200: HTTP_OK?
           {  echo "Could not connect";
              return; //sonst wird login-Abschnitt ausgefuehrt!!!
           }
        }

        // Login if necessary:
        if ( $loginIfNecessary && $this->checkLoginRequired($connect))
        {  
           $loginStatus = $this->login();
        
           switch ( $loginStatus )
           {
              case self::LOGIN_SUCCESSFUL:
              {
                 $connect = $this->postForFileUpload($path, $filename, $fileContentType, false);
                 if($this->checkLoginRequired($connect))
                   echo "rejected after successful login";
                 break;
              }
              case self::LOGIN_FAILED:
                echo "login failed";
                break;
              case self::LOGIN_CANCELED:
                $connection = "";
                echo "LOGIN_CANCELED";
                break;
              default:
                echo "Bug: unexpected login status: ".$loginStatus;
           }
        }  

        $this->logg($METHOD." 2/2: path = $newpath, connection = ");
        $this->logg($connection);
        return $connect;
   }
}

  // --------------------------------------------------------------------------------
  // Login
  // --------------------------------------------------------------------------------

  /*
   * Performs a login dialog. Returns a {@link LoginDialogResult} object, or
   * <code>null</code> if the user has canceled the dialog. <code>afterFailure</code>
   * should be set to <code>true</code> if the dialog immediately follows a failed try to
   * login.
   */

  public abstract function performLoginDialog ($afterFailure);
    //throws JapsClientException;
  
  /*
   * Tries to login with <code>account</code> and <code>password</code>. If succeeded,
   * returns {@link #LOGIN_SUCCESSFUL}, otherwise {@link #LOGIN_FAILED}.
   */
/*
  /*
   * Tries to login with the account and password given by <code>result</code>. If succeeded,
   * returns {@link #LOGIN_SUCCESSFUL}, otherwise {@link #LOGIN_FAILED}.
   */

  /*
   * Tries to login. If the login fails, tries again and again, but at most
   * {@link #maxLoginTries} times. Returns {@link #LOGIN_SUCCESSFUL} if the last login try
   * succeeded, {@link #LOGIN_FAILED} if the last login try failed, and
   * {@link #LOGIN_CANCELED} if the user has canceled the login dialog.
   */

  public function login ()
    //throws JapsClientException
  {
    $numargs = func_num_args();
    if ($numargs == 0)
    { 
      $METHOD = "login()";
      $this->logg($METHOD." 1/2 <br/>");

      $count = 1; 
      $status = -1; 
      
      while( $status != self::LOGIN_SUCCESSFUL && $status != self::LOGIN_CANCELED && ( $count <= $this->maxLoginTries || $this->maxLoginTries == self::UNLIMITED ) )
      { 
        $result = $this->performLoginDialog( $status == self::LOGIN_FAILED );
        $status = (is_null($result) ? self::LOGIN_CANCELED : $this->login($result));
        $count++;
      }
    }
    elseif ($numargs == 1)
    { //login(LoginDialogResult result)
      
      $result = func_get_arg(0);
      return $this->login($result->getAccount(), $result->getPassword());
    }

    elseif ($numargs == 2)
    { 
      //login(account,password)
      $account = func_get_arg(0);
      $password = func_get_arg(1);
      $params = array();

      $METHOD = "login(String,char[])";
      $this->logg("$METHOD 1/2: account = $account, password = ".$this->hidePassword($password)."<br/>");

    //try
      //{
        // Set request parameters:
        $params[self::ACCOUNT_PARAM] = $account;
        $params[self::PASSWORD_PARAM] = $password;
        $params[self::RESOURCE_PARAM] = $this->composeURL(self::RESOURCE_VALUE);
        
        // Do post request:
        $connect = $this->post(self::LOGIN_PATH, $params, false);

        // Login status: 
        $status =  ($this->checkLoginRequired($connect) ? self::LOGIN_FAILED : self::LOGIN_SUCCESSFUL);
    }
    else
    {  echo "Too many arguments";  }

    $this->logg("$METHOD 2/2: status = $status <br/>");
    return $status;
     
  }

  // --------------------------------------------------------------------------------
  // Maximal number of login tries
  // --------------------------------------------------------------------------------

  /*
   * Returns the maximum number of login tries. A value of {@link #UNLIMITED}
   * (<code>-1</code>) means the number is not limited. Default: {@link #UNLIMITED}.
   */

  function getMaxLoginTries ()
  {
     return $this->maxLoginTries;
  }

  /*
   * Sets the maximum number of login tries. A value of {@link #UNLIMITED} (<code>-1</code>)
   * means the number of tries is not limited. Default: {@link #UNLIMITED}.
   * @param maxLoginTries The new maximum number of login tries.
   * @throws IllegalArgumentException If <code>maxLoginTries</code> is not positive and not
   * equal to {@link #UNLIMITED}.
   */

  function setMaxLoginTries ($maxLoginTries)
  {
     if ( $maxLoginTries <= 0 && maxLoginTries != "UNLIMITED" )
     {  throw new Exception("Invalid value for maximum number of login tries: ".$this->maxLoginTries);  }
     $this->maxLoginTries = $maxLoginTries;
  }

  // --------------------------------------------------------------------------------
  // URLs
  // --------------------------------------------------------------------------------

  /*
   * Returns the prefix of the Japs server.
   */

  function getUrlPrefix()
  {
     return $this->urlPrefix;
  }

  /*
   * Returns the (absolute) URL for a given (relative) path.
   */

  function composeURL($path)
  {
     if ( substr($path,0,0) != "/" )
     {  $path = "/".$path;  }
     return $this->urlPrefix.$path;
  }

  /*
   * Returns the (relative) path for a given (absolute) URL.
   */

  function getPath($url)
  {
     if ( $url == $this->urlPrefix)
     {  return "";  }
     $start = $this->urlPrefix."/";
     if ( strpos($url,$start) != 0 )
     {  throw new Exception("Improper url: ".url." (wrong prefix)"); }
     $start_len = strlen($start);
     return substr($url,$start_len);
  }

  // --------------------------------------------------------------------------------
  // Content types
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

   function guessContentType($filename)
   {
     if(!$pos = strrpos($filename,"."))
       return null;
     $suffix = substr($filename,$pos+1);
     return $this->getContentTypeForSuffix($suffix);
   }

  /**
   * Returns the content type for <code>suffix</code>. The suffix is looked up in
   * {@link #contentTypes}. If no entry for the suffix is found, <code>null</code> is
   * returned.
   */

  function getContentTypeForSuffix($suffix)
  {
     return $this->contentTypes[$suffix];
  }

  /**
   * Sets the content type for <code>suffix</code>.
   */

  function setContentTypeForSuffix($suffix, $contentType)
  {
     $this->contentTypes[$suffix] = $contentType;
  }

  /**
   * Removes the content type for <code>suffix</code>.
   */

  function removeContentTypeForSuffix($suffix,$contentType)
  {
     $this->contentTypes[$suffix] = "";
  }

  /**
   * Returns all suffixes for which content types are specified.
   */

  function getContentTypeSuffixes()
  {
     return $this->contentTypes;
  }

  // --------------------------------------------------------------------------------
  // Utilities to create the request
  // --------------------------------------------------------------------------------

  /**
   * Url-encodes the entries of <code>params</code> and returns the result as a string.
   */
   protected function url_Encode($params)
   {
      if( $params == null)
        return null;
      if(is_string($params))
      {  return $params;  }
      $query_string = array();
      foreach ($params as $key => $value)
      {
        if(is_array($value))
        {
           $string = "";
           foreach($value as $helpvalue)
           {  $string .= $helpvalue;
           }
           $query_string[] = urlencode($key) . '=' . urlencode($string);
        }
        else
        {
           $query_string[] = urlencode($key) . '=' . urlencode($value);
        }
      }
      return implode('&', $query_string);
    }

  // --------------------------------------------------------------------------------
  // Logging
  // --------------------------------------------------------------------------------

  /*
   * <p>$fp
   *   Writes a log message.
   * </p>
   * <p>
   *   This implementation does nothing. Extending classes can overwrite this to implement a
   *   logging mechanism.
   * </p>
   */

  function logg($message)
  {
     if($this->logFile != "")
     {
        if($fp = fopen($this->logFile, 'a'))
        {
           fputs($fp, self::LOG_PREFIX.': '.date(self::DEFAULT_TIMESTAMP_PATTERN).' => ');
           fputs($fp, $message);
           fputs($fp, "\r\n");
        }
        fclose($fp);
     }
  }


  // --------------------------------------------------------------------------------
  // Analysing the response
  // --------------------------------------------------------------------------------

  /*
   * Returns <code>true</code> if login is required for <code>connection</code>, otherwise
   * <code>false</code>.
   */

  protected function checkLoginRequired($connect)
  {
     $raw = $connect['head']['raw'];
     if(!strpos($raw,self::LOGIN_REQUIRED_HEADER.':'))
     {  return false;  }
     else
     {
       $start = strpos($raw,self::LOGIN_REQUIRED_HEADER.':') + 24;
       $value = trim(substr($raw,$start,3));
       if ($value == "")
         return false;
       else if ($value == "yes" || $value == "true")
         return true;
       else if ($value == "no" || $value == "false")
         return false;
       else
         echo "Unknown JapsResponseHeader.LOGIN_REQUIRED - login_required: ".$value;
     }
  }

 // --------------------------------------------------------------------------------
  // Utilities for log messages
  // --------------------------------------------------------------------------------

  /**
   * Convenience method to replace a password (or another confidential piece of text) by a
   * sequence of asterisks. Used for log messages.
   */

  protected function hidePassword ($password)
  {
     $chars = "";
     for ($i = 0; $i < count($password); $i++)
     {  $chars .= '*';  }
    
     return $chars;
  }

  /**
   * Convenience method to convert a numeric status code into a self-explanatory
   * string. Used for log messages.
   */

  protected function loginStatusToString ($loginStatus)
  {
    switch ( $loginStatus )
      {
         case self::LOGIN_SUCCESSFUL:
           return "LOGIN_SUCCESSFUL";
         case self::LOGIN_FAILED:
           return "LOGIN_FAILED";
         case self::LOGIN_CANCELED:
           return "LOGIN_CANCELED";
         default:
           return "[unknown status code: " + $loginStatus + "]";
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

  protected function paramsToString ($params)
  {
     if ( $params == null )
       return null;
     if (is_string($params))
       return $params;
     $buffer = "{";
     foreach ($params as $key => $value)
     {
        if(is_array($value))
        {
           $string = $this->hidePassword($value);
           $buffer .= $key.'='.$string;
        }
        else
        {
           $buffer .= $key.'='.$value;
        }
        $buffer .= ",";
     }
     $buffer = trim($buffer,',');
     $buffer .= "}";
     return $buffer;
  }


}
?>