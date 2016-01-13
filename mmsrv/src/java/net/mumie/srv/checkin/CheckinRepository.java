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

package net.mumie.srv.checkin;

import java.io.InputStream;
import net.mumie.srv.util.Identifyable;

/**
 * Represents a bundle of (pseudo-)documents to check in.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: CheckinRepository.java,v 1.2 2009/10/05 22:37:59 rassy Exp $</code>
 */

public interface CheckinRepository
  extends Identifyable
{
  /**
   * Role as an Avalon service (<code>CheckinRepository.class.getName()</code>).
   */

  public static final String ROLE = CheckinRepository.class.getName();

  /**
   * Returns all master paths in this checkin repository.
   */

  public String[] getMasterPaths ()
    throws CheckinException;

  /**
   * Returns the content path for the specified master path, or <code>null</code> if it does
   * not exist.
   */

  public String getContentPath (String masterPath)
    throws CheckinException;

  /**
   * Returns the master for the specified master Path.
   */

  public Master getMaster (String masterPath)
    throws CheckinException;

  /**
   * Returns the content for the specified content path.
   */

  public Content getContent (String contentPath)
    throws CheckinException;

  /**
   * Returns the content for the master with the specified path, or <code>null</code> if
   * there is no content for that master.
   */

  public Content getContentForMaster (String masterPath)
    throws CheckinException;
}
