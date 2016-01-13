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

package net.mumie.cocoon.tub;

/**
 * Comprises the data of a LDAP user.
 */

public class TubLdapUser
{
  /**
   * The distinguished name of the user
   */

  protected final String userDN;

  /**
   * The <em>tubPersonOM</em> entry of the user (used as sync id).
   */

  protected final String tubPersonOM;

  /**
   * Returns the distinguished name of the user
   */

  public final String getUserDN ()
  {
    return this.userDN;
  }

  /**
   * Returns the <em>tubPersonOM</em> entry of the user (used as sync id).
   */

  public final String getTubPersonOM ()
  {
    return this.tubPersonOM;
  }

  /**
   * Creates a new instance with the specified values.
   */

  public TubLdapUser (String userDN, String tubPersonOM)
  {
    this.userDN = userDN;
    this.tubPersonOM = tubPersonOM;
  }

  /**
   * Returns true if the specified object is a <code>TubLdapUser</code> instance with
   * the same userDN and tubPersonOM, otherwise false.
   */

  public boolean equals (Object object)
  {
    if ( object instanceof TubLdapUser )
      {
	TubLdapUser result = (TubLdapUser)object;
	return
	  ( result.getUserDN().equals(this.userDN) &&
	    result.getTubPersonOM().equals(this.tubPersonOM) );
      }
    else
      return false;
  }

  /**
   * Returns a string representation of this object
   */

  public String toString ()
  {
    return "userDN: " + this.userDN + " tubPersonOM: " + this.tubPersonOM;
  }
}