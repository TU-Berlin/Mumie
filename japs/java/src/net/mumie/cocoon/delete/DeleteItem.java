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

import net.mumie.cocoon.notions.Nature;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.content.ContentObjectItem;

public class DeleteItem extends ContentObjectItem 
{
  /**
   * Status flag indicating this content object has not been deleted yet.
   */

  public static final int INITIAL = 0;

  /**
   * Status flag indicating this content object has been deleted.
   */

  public static final int DELETED = 1;

  /**
   * Status flag indicating that the deletion of this content object failed.
   */

  public static final int FAILED = 2;

  /**
   * Error code indicating the deletion failed because of lacking permissions.
   */

  public static final int PERMISSION_DENIED = 3;

  /**
   * Error code indicating the deletion failed because the dection was not empty
   * (only with sections)
   */

  public static final int NOT_EMPTY = 4;

  /**
   * Status flag
   */
    
  protected int status = INITIAL;

  /**
   * Error code, or -1 of no error ocurred.
   */

  protected int error = -1;

  /**
   * Creates a new instance.
   */

  public DeleteItem (int nature, int type, int id)
  {
    super(nature, type, id);
  }

  /**
   * Creates a new instance.
   */

  public DeleteItem (String typeName, int id)
  {
    super(typeName, id);
  }

  /**
   * Sets the status
   */

  public final void setStatus (int status)
  {
    this.status = status;
  }

  /**
   * Sets the error code
   */

  public final void setError (int error)
  {
    this.error = error;
  }

  /**
   * Returns the status code.
   */

  public final int getStatus ()
  {
    return this.status;
  }

  /**
   * Returns the error code.
   */

  public final int getError ()
  {
    return this.error;
  }

  /**
   * Returns a descriptive string for the specifed status flag.
   */

  public final String statusToString (int status)
  {
    switch ( this.status )
      {
      case INITIAL: return "retrun";
      case DELETED: return "deleted";
      case FAILED:  return "failed";
      default: return "unknown";
      }
  }

  /**
   * Returns a descriptive string for the specifed error flag.
   */

  public final String errorToString (int error)
  {
    switch ( this.error )
      {
      case PERMISSION_DENIED: return "permission-denied";
      case NOT_EMPTY: return "not-emty";
      case -1:  return null;
      default: return "unknown";
      }
  }

  /**
   * Returns the status as a descriptive string.
   */

  public final String getStatusStr ()
  {
    return statusToString(this.status);
  }

  /**
   * Returns the error as a descriptive string.
   */

  public final String getErrorStr ()
  {
    return errorToString(this.error);
  }

  /**
   * Returns a string representation of this instance.
   */

  public String toString ()
  {
    return
      "DeleteItem(" +
      this.nature + "," +
      this.type + "," +
      this.id + "," +
      this.status + "," +
      this.error + ")";
  }
}