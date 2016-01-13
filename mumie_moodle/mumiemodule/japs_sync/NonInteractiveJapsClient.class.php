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
function __autoload($class_name)
  {
     require_once $class_name . '.class.php';
  }
/*
 * <p>
 *   A japs client that does not interactively ask for account and password.
 * </p>
 * <p>
 *   The account and password can be set by the {@link #setAccount setAccount},
 *   {@link #setPassword setPassword}, {@link #readAccountFromFile readAccountFromFile},
 *   and {@link #readPasswordFromFile readPasswordFromFile} methods. The latter two
 *   specify a file from which the account resp. the password is read.
 * </p>
 */

class NonInteractiveJapsClient extends JapsClient
{
  /*
   * The account for login.
   */

  protected $account = null;

  /*
   * The password for login.
   */

  private $password = array();

  /*
   * Returns account and password as a {@link LoginDialogResult LoginDialogResult}.
   */

  public function performLoginDialog ($afterFailure)
  {  
     if ( $this->account == null || $this->password == null )
       throw new Exception("Account and/or password null");
     return new LoginDialogResult($this->account, $this->password);
  }

  /*
   * <p>
   *   Reads <code>file</code> and returns the content as an array of characters.
   * </p>
   */

  protected function readFromFile ($file)
    //throws FileNotFoundException, IOException
  {
    $fileSize = filesize($file);
    if ( $fileSize > 2147483647 )
      throw new Exception("File too big: $file");
    $buffer = array();
    $reader = fopen($file, 'r');
    $i = 0;
    while (!feof($reader))
    {
      $buffer[$i] = fgetc($reader);
      $i++;
    }
    fclose($reader);
    if ( is_null($buffer))
      throw new Exception("Can not read init file");
    $chars = array();
    for ($j = 0; $j < count($buffer) -1; $j++)
      $chars[$j] = $buffer[$j];
      LoginDialogResult::clear_array(&$buffer);
    return $chars;
  }

  /*
   * Sets the account.
   */

  public function setAccount ($account)
  {
    if ( $account == null )
      //throw new IllegalStateException("Account null");
     echo "Account null";
    $this->account = $account;
  }

  /*
   * Sets the password.
   */

  public function setPassword ($password)
  {
    if ( $password == null )
      //throw new IllegalStateException("Password null");
     echo "Password null";
    $this->password = $password;
  }

  /*
   * Reads the account from a file. The entire content of the file is set to the account, so
   * avoid leading and trailing whitespaces and newlines in the file.
   *
   * @param file the file from where to read the account.
   */

  public function readAccountFromFile ($file)
    //throws FileNotFoundException, IOException
  {
    $this->account = implode('',$this->readFromFile($file));
  }

  /*
   * Reads the password from a file. The entire content of the file is set to the password, so
   * avoid leading and trailing whitespaces and newlines in the file.
   *
   * @param file the file from where to read the password.
   */

  public function readPasswordFromFile ($file)
    //throws FileNotFoundException, IOException
  {
    $this->password = $this->readFromFile($file);
  }

  /*
   * Creates a new <code>NonInteractiveJapsClient</code> that writes its log messages
   * to the stream <code>logStream</code>.
   */

  public function NonInteractiveJapsClient ()
  {  
     $numargs = func_num_args();
     if($numargs == 1)  //NonInteractiveJapsClient (urlPrefix)
     {
        $urlPrefix = func_get_arg(0);
        parent::__construct($urlPrefix);
     }
     if($numargs == 2)  //NonInteractiveJapsClient (urlPrefix, logFilename)
     {
        $urlPrefix = func_get_arg(0);
        $logFilename = func_get_arg(1);
        parent::__construct($urlPrefix, $logFilename);
     }
  }

  /*
   * <p>
   *   Clears the password of this japs client.
   * </p>
   * <p>
   *   You should call this when you do not need this japs client any longer.
   * </p>
   */

  public function clearPassword ()
  {
    if ( !empty($this->password))
      LoginDialogResult::clear_array($this->password);
  }
}
?>