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

package net.mumie.srv.entities.pseudodocs;

import net.mumie.srv.entities.PseudoDocument;
import org.apache.cocoon.ProcessingException;

/**
 * Represents a user.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: User.java,v 1.3 2009/11/10 00:02:23 rassy Exp $</code>
 */

public interface User extends PseudoDocument
{
  /**
   * Role of an implementing class as an Avalon service (<code>User.class.getName()</code>)
   */

  public static final String ROLE = User.class.getName();

  /**
   * Sets the id of this user to the id of the user of the session.
   */

  public void setIdFromSession ()
    throws ProcessingException;

  /**
   * Returns true if the user is allowed to create a new (pseudo-)document of the
   * given type, otherwise false. 
   *
   * @param type the (pseudo-)document type
   * @exception ProcessingException if something goes wrong
   */

  public boolean hasCreatePermission (int type)
    throws ProcessingException;

  /**
   * Returns true if the user is allowed to change the (pseudo-)document with the specific
   * type and id, otherwise false.
   *
   * @param type the (pseudo-)document type
   * @exception ProcessingException if something goes wrong
   */

  public boolean hasWritePermissionForVCThread (int type, int vcThreadId)
    throws ProcessingException;

  /**
   * Returns true if the user is allowed to change the (pseudo-)document with the specific
   * type and id, otherwise false.
   *
   * @param type the (pseudo-)document type
   * @exception ProcessingException if something goes wrong
   */

  public boolean hasWritePermissionForEntity (int type, int id)
    throws ProcessingException;
}
