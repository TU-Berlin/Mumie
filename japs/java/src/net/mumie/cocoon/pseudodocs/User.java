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
import net.mumie.cocoon.notions.UseMode;
import net.mumie.cocoon.notions.UserRole;

/**
 * Represents a user.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @author Uwe Sinha <a href="mailto:sinha@math.tu-berlin.de">sinha@math.tu-berlin.de</a>
 * @version <code>$Id: User.java,v 1.15 2007/09/06 14:16:45 lehmannf Exp $</code>
 */

public interface User extends PseudoDocument
{
  /**
   * Role of an implementing class as an Avalon service. Value is
   * <span class="string">"net.mumie.cocoon.pseudodocs.User"</span>.
   */

  public static final String ROLE = "net.mumie.cocoon.pseudodocs.User";

  /**
   * Constant to be used as a key for the datum representing the user's
   * login name (<code>"login-name"</code>).
   */

  public static final String LOGIN_NAME_DATA_KEY = "login-name"; 

  /**
   * Constant to be used as a key for the datum representing the user's
   * first name (<code>"first-name"</code>).
   */

  public static final String FIRST_NAME_DATA_KEY = "first-name"; 

  /**
   * Constant to be used as a key for the datum representing the user's
   * surname (<code>"surname"</code>).
   */

  public static final String SURNAME_DATA_KEY = "surname"; 
  
  /** 
   * Constant to be used as a key for the datum representing the user's
   * e-mail address (<code>"email"</code>).
   */

  public static final String EMAIL_DATA_KEY = "email"; 

  /** 
   * Constant to be used as a key for the datum representing the user's
   * password (<code>"password"</code>).
   */

  public static final String PASSWORD_DATA_KEY = "password"; 

  /**
   * Constant to be used as a key for the datum representing the user's
   * preferred theme (<code>"theme"</code>).
   */

  public static final String THEME_DATA_KEY = "theme"; 

  /**
   * Constant to be used as a key for the datum representing a
   * description about the user (<code>"description"</code>).
   */

  public static final String DESCRIPTION_DATA_KEY = "description"; 
  
  /**
   * Constant to be used as a key for the datum representing customized
   * meta information about the user (<code>"custom-meta"</code>).
   */

  public static final String CUSTOM_METAINFO_DATA_KEY = "custom-metainfo"; 
  
  /**
   * Constant to be used as a key for the datum representing the time
   * the user logged in last time (<code>"last-login"</code>).
   */

  public static final String LAST_LOGIN_DATA_KEY = "last-login"; 

  /**
   * Constant to be used as a key for the datum representing the time
   * the user created in the database (<code>"created"</code>).
   */

  public static final String CREATED_DATA_KEY = "created";

  /**
   * As return value of {@link DbHelper#checkAuthStatus(String loginName)}
   *  indicates, that no user with this @loginName exists in the database.
   */

  public static final int AUTHSTATUS_NONE = 0;

  /**
   * As return value of {@link DbHelper#checkAuthStatus(String loginName)}
   *  indicates, that a user with this @loginName exists in the database
   *  and the user's password is not NULL.
   */

  public static final int AUTHSTATUS_MUMIE = 1;

  /**
   * As return value of {@link DbHelper#checkAuthStatus(String loginName)}
   *  indicates, that a user with this @loginName exists in the database
   *  but the user's password is NULL.
   */

  public static final int AUTHSTATUS_EXTERNAL = 2;

  /**
   * Returns the role of this user. May also return {@link UserRole#UNDEFINED UNDEFINED} or
   * {@link UserRole#ANY ANY}, which means that the role is not defined or this object is
   * suited for any role, respectively.
   */

  public int getRole ()
    throws ServiceException;

  /**
   * Sets the role of this user. The new role may also be
   * {@link UserRole#UNDEFINED UNDEFINED} or {@link UserRole#ANY ANY} to indicate that the
   * role is not defined or that this object is suited for any role, respectively.
   */

  public void setRole (int userRole)
    throws ServiceException;

  /**
   * <p>
   *   Returns <code>true</code> if the specified string is the password of the user,
   *   otherwise <code>false</code>
   * </p>
   */

  public boolean checkPassword (String password)
    throws ServiceException;

  /**
   * Returns <code>true</code> if the user is allowed to read the pseudo-document with type
   * <code>type</code> and id <code>id</code>.
   */

  public boolean hasPseudoDocReadPermission (int type, int id)
    throws ServiceException;

  /**
   * Returns <code>true</code> if the user is allowed to read the document with type
   * <code>type</code> and id <code>id</code>.
   */

  public boolean hasReadPermission (int type, int id)
    throws ServiceException;

  /**
   * Returns <code>true</code> if the user is allowed to read
   * {@link net.mumie.cocoon.documents.Document Document}, otherwise <code>false</code>.
   */

  public boolean hasReadPermission (Document document)
    throws ServiceException;
  
  /**
   * Checks if a user is allowed to check in a new version of an existing
   * document. I.e., checks if this user is member of a group that has write
   * permission on a given VC thread of given type. 
   *
   * @param docType the vc_threads documents type
   * @param vcThread the id of the VC thread identifying the document
   * @return <code>true</code> if the user is member of a group that has a
   * write permission on VC thread <code>vcThread</code>;
   * <code>false</code> if not
   * @exception ServiceException if an error occurs
   * @exception IllegalArgumentException if there is no VC thread with the
   * Id <code>vcThread</code>
   */

  public boolean hasWritePermission (int docType,int vcThread) 
    throws ServiceException, IllegalArgumentException; 

  /**
   * Checks if a user is allowed to check in a new document of a given
   * type. 
   *
   * @param docType the (numerical) document type
   * @return <code>true</code> if the user is member of a group that has
   * write permission on document type <code>docType</code>;
   * <code>false</code> if not
   * @exception ServiceException if an error occurs
   * @exception IllegalArgumentException if <code>docType</code> refers to
   * a non-existent document type
   * @see net.mumie.cocoon.notions.DocType
   */

  public boolean hasCreatePermission (int docType)
    throws ServiceException, IllegalArgumentException; 

  /**
   * Checks if this user is an administrator.
   */

  public boolean isAdmin ()
    throws ServiceException;

  /**
   * Checks if this user is an administrator of group <code>groupId</code>. 
   *
   * @param groupId the numerical id of the group in question
   * @return <code>true</code> if this user is an administrator,
   * <code>false</code> if he/she is not (or if he/she is not even a member
   * of the group in question)
   * @exception ServiceException if the id of this user could not be
   * determined (see {@link getId()})
   */

  public boolean isGroupAdmin (int groupId)
    throws ServiceException;

  /**
   * Returns the id's of the user groups this user is a member of.
   */

  public int[] getUserGroups ()
    throws ServiceException;

  /**
   * Returns the id's of the user groups this user is a member of, as a list of
   * {@link Integer} objects.
   */

  public List getUserGroupsAsList ()
    throws ServiceException;

  /**
   * Returns the names of the user groups this user is a menber of.
   */

   public String[] getUserGroupNames ()
     throws ServiceException;

  /**
   * Returns the names of the user groups this user is a menber of, as a list of
   * {@link String} objects.
   */

  public List getUserGroupNamesAsList ()
    throws ServiceException;

  /**
   * Returns the section this user may store sync data in, as the sections id.
   */

  public int getSyncHome ()
    throws ServiceException;

}
