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

// Including the LoginDialogResult class for performing the login
require_once ('LoginDialogResult.class.php'); 
 
class JapsSynchronise extends JapsClient{
    
/*
* The account for login.
*/

protected $account = null;

/*
* The password for login.
*/

private $password = array();
 

/**
 * Constructor for JapsSynchronise
 * 
 * $serverPath - path to mumies cocoon directory
 * $url - path to the synchronisation method
 * $logFilename - Name of the logfile, not necessary
 * $dbValue - the array with the values to synchronise
 * 
 */
public function JapsSynchronise (){
    global $CFG;
    // calling the super classes constructor
    $logFilename = $CFG->syncLogfile;
    parent::__construct($CFG->syncServer, $logFilename);
     $this->setAccount($CFG->syncUser);
     for ($i = 0; $i < strlen($CFG->syncPassword); $i++){
      $password[] = substr ($CFG->syncPassword, $i, 1);
	 }
    $this->setPassword($password);
 }
 
  public function performLoginDialog ($afterFailure)
  {  
     if ( $this->account == null || $this->password == null )
       throw new Exception("Account and/or password null");
     return new LoginDialogResult($this->account, $this->password);
  }
  
   /**
   * Sets the account.
   */

  public function setAccount ($account)
  {
    if ( $account == null )
      //throw new IllegalStateException("Account null");
     echo "Account null";
    $this->account = $account;
  }

  /**
   * Sets the password.
   */

  public function setPassword ($password)
  {
    if ( $password == null )
      //throw new IllegalStateException("Password null");
     echo "Password null";
    $this->password = $password;
  }
}

?>
