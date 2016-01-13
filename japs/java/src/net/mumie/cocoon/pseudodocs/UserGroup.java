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

package net.mumie.cocoon.pseudodocs;

import java.util.List;
import org.apache.avalon.framework.service.ServiceException;
import net.mumie.cocoon.documents.Document;
import net.mumie.cocoon.notions.DbColumn;

/**
 * Represents a user group.
 *
 * @author Uwe Sinha <a href="mailto:sinha@math.tu-berlin.de">sinha@math.tu-berlin.de</a>
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: UserGroup.java,v 1.4 2007/07/11 15:38:48 grudzin Exp $</code>
 */

public interface UserGroup extends PseudoDocument
{
  /**
   * Role of an implementing class as an Avalon service. Value is
   * <span class="string">"net.mumie.cocoon.pseudodocs.UserGroup"</span>.
   */

  public static final String ROLE = "net.mumie.cocoon.pseudodocs.UserGroup";

  /**
   * Returns <code>true</code> if users of this group are allowed to read the document with
   * type <code>type</code> and id <code>id</code>.
   */

  public boolean hasReadPermission (int docType, int id)
    throws ServiceException;

  /**
   * Returns <code>true</code> if users of this group are allowed to read
   * {@link net.mumie.cocoon.documents.Document Document}, otherwise <code>false</code>.
   */

  public boolean hasReadPermission (Document document)
    throws ServiceException;
  
  /**
   * Returns <code>true</code> if users of this group are allowed to check-in new versions
   * of an existing documents of a certain type and vc thread, otherwise <code>false</code>.
   *
   * @param docType the type of the document
   * @param vcThreadId the id of the vc thread
   * @exception ServiceException if an error occurs
   */

  public boolean hasWritePermission (int docType, int vcThreadId) 
    throws ServiceException;

  /**
   * Returns <code>true</code> if users of this group are allowed create new documents of a
   * given type, otherwise <code>false</code>.
   *
   * @param docType the (numerical) document type
   * @exception ServiceException if an error occurs
   * @exception IllegalArgumentException if <code>docType</code> refers to
   * a non-existent document type
   */

  public boolean hasCreatePermission (int docType)
    throws ServiceException, IllegalArgumentException;

  /**
   * Returns the id's of the members of this group, as an array.
   */

  public int[] getMembers ()
    throws ServiceException;

  /**
   * Returns the id's of the members of this group, as alist of <code>Integer</code>
   * objects.
   */

  public List getMembersAsList ()
    throws ServiceException;
}
