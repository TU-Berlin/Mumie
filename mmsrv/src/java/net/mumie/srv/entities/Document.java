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

package net.mumie.srv.entities;

import org.apache.cocoon.ProcessingException;
import org.apache.avalon.framework.service.ServiceException;
import java.sql.SQLException;

public interface Document
  extends Entity
{
  /**
   * Role of an implementing class as an Avalon component. Value is
   * the full qualified class name.
   */

  public static final String ROLE = Document.class.getName();;

  /**
   * Sets the local id of this dcoument. Meaningful only if this instance represents
   * a reference target.
   */

  public void setLid (String lid);

  /**
   * Returns the local id of this dcoument. Meaningful only if this instance represents a
   * reference target.
   */

  public String getLid ();

  /**
   * Sets the id of the reference pointing to this document. Meaningful only if this
   * instance represents a reference target.
   */

  public void setRefId (int refId);

  /**
   * Returns the id of the reference pointing to this document. Meaningful only if this
   * instance represents a reference target.
   */

  public int getRefId ();

  /**
   * Sets the type of the document that is the origin of the reference pointing to this
   * document. This value is meaningful only if this instance represents a reference
   * target.
   */

  public void setRefOriginType (int refOriginType);

  /**
   * Returns the type of the document that is the origin of the reference pointing to this
   * document. This value is meaningful only if this instance represents a reference target.
   */

  public int getRefOriginType ();

  /**
   * Returns the <em>Internet Media Type</em> of the document's content. This should be a
   * numerical key for the real media type. See {@link MediaType MediaType} for more
   * information. It is allowed that the method returns <code>-1</code>, which means that
   * the media type is unknown.
   *
   * @return the internet media type.
   *
   * @throws SQLException if something goes wrong while retrieving the media type from the
   *   database
   */

  public int getContentType ()
    throws ServiceException, SQLException;

  /**
   * Returns the size of the document in bytes. This is what should appear in the
   * <code>Content-Length</code> header of the HTTP response. It is allowed that the method
   * returns <code>-1</code>, which means that the size is unknown.
   *
   * @return the size of the document.
   *
   * @throws SQLException if something goes wrong while retrieving the media type from the
   *   database
   */

  public long getContentLength ()
    throws ServiceException, SQLException;

}