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

package net.mumie.cocoon.delete;

import net.mumie.cocoon.util.Identifyable;

/**
 * Provides methods to delete and undelete (pseudo)-documents.
 *
 * @version <code>$Id: DeleteHelper.java,v 1.5 2010/01/13 15:01:16 rassy Exp $</code>
 * @author Tilman Rassy <a href="mailto:tilman.rassy@integral-learning.de">tilman.rassy@integral-learning.de</a>
 */

public interface DeleteHelper
  extends Identifyable
{
  /**
   * Role as an Avalon service (<code>DeleteHelper.class.getName()</code>).
   */

  public static final String ROLE = DeleteHelper.class.getName();

  /**
   * Sets the user for which this instance does deletions. If not set, the currently
   * logged-in user is assumed.
   */

  public void setUser (int userId)
    throws DeletionException;

  /**
   * Tries to delete the document or pseudo-dcoument represented by the specified
   * {@link DeleteItem DeleteItem} object. Deletion is only possible if the user has write
   * permission on the (pseudo-)document, and, in case of a section, the section is
   * empty.
   */

  public void tryDelete (DeleteItem item)
    throws DeletionException;

  /**
   * Tries to undelete the document or pseudo-document represented by the specified
   * {@link DeleteItem DeleteItem} object. Undeletion is only possible if the user has write
   * permission on the (pseudo-)document.
   */

  public void tryUndelete (DeleteItem item)
    throws DeletionException;

  /**
   * Tries to recursively delete the section represented by the specified
   * {@link DeleteItem DeleteItem} object. Returns all affected (pseudo-)documents, i.e.,
   * all (pseudo-)documents the method tries to delete.
   */

  public DeleteItem[] tryDeleteSectionRecursively (DeleteItem secItem)
    throws DeletionException;

  /**
   * Tries to delete the (pseudo-)documents represented by the specified
   * {@link DeleteItem DeleteItem}s. If <code>recursive</code> is true, sections are tried
   * to delete recursively. Retutrns all affected (pseudo-)documents (if <code>recursive</code>
   * is turned on, this may be more than the specifed {@link DeleteItem DeleteItem}s).
   */

  public DeleteItem[] tryDeleteAll (DeleteItem[] items, boolean recursive)
    throws DeletionException;

  /**
   * Tries to undelete the (pseudo-)documents represented by the specified
   * {@link DeleteItem DeleteItem}s. Retutrns all affected (pseudo-)documents.
   */

  public DeleteItem[] tryUndeleteAll (DeleteItem[] items)
    throws DeletionException;
}