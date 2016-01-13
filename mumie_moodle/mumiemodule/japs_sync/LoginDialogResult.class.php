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
/**
 * <p>
 *   Represents a pair consisting of an account name and password.
 * </p>
*/

class LoginDialogResult
{
  /**
   * The account.
   */

  protected $account = null;

  /**
   * The password.
   */

  private $password = array();

  /**
   * Returns the the account. (Cf. {@link #account account}.)
   */

  public function getAccount ()
  {
    return $this->account;
  }

  /**
   * Returns the password.
   */

  public function getPassword ()
  {
    $password = array();
    foreach($this->password as $key => $value)
    {
      $password[$key] = $value;
    }

    return $password;
  }

  /**
   * Returns the password as a string.
   */

  public function getPasswordAsString ()
  {
    $password = "";
    foreach($this->password as $value)
    {
      $password .= $value;
    }
    return $password;
  }

  /**
   * Replaces all characters in <code>chars</code> by the null character.
   */

  public static function clear_array ($chars)
  { 
    for ($i = 0; $i < count($chars); $i++)
    {  $chars[$i] = 0;  }
  }

  /**
   * Erases the data stored in this object.
   */

  public function clear ()
  { 
    $this->account = null;
    if ( !empty($this->password))
      {
        $this->clear_array(&$this->password);
        $this->password = array();
      }
  }

  /**
   * Creates a new <code>LoginDialogResult</code> object.
   */

  public function LoginDialogResult ($account = "", $password = array())
  {
    $this->account = $account;
    $this->password = array();
    foreach($password as $key => $value)
    {
      $this->password[$key] = $value;
    }
  }

}
?>