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

package net.mumie.cocoon.checkin;

import java.io.InputStream;
import net.mumie.cocoon.util.Identifyable;

/**
 * Represents a bundle of (pseudo-)documents to check in.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: CheckinRepository.java,v 1.9 2009/06/16 16:06:23 rassy Exp $</code>
 */

public interface CheckinRepository
  extends Identifyable
{
  /**
   * Role as an Avalon service (<code>CheckinRepository.class.getName()</code>).
   */

  public static final String ROLE = CheckinRepository.class.getName();

//   /**
//    * Returns an input stream to read the entry with the specified path.
//    */

//   public InputStream getInputStream (String path)
//     throws CheckinException;

//   /**
//    * Returns the size of the the master, content, or source with the specified path, or -1
//    * if not known.
//    */

//   public long getSize (String path)
//     throws CheckinException;

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
   * Returns the source path for the specified master path, or <code>null</code> if it does
   * not exist.
   */

  public String getSourcePath (String masterPath)
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
   * Returns the source for the specified source path.
   */

  public Source getSource (String sourcePath)
    throws CheckinException;

  /**
   * Returns the content for the master with the specified path, or <code>null</code> if
   * there is no content for that master.
   */

  public Content getContentForMaster (String masterPath)
    throws CheckinException;

  /**
   * Returns the source for the master with the specified path, or <code>null</code> if
   * there is no source for that master.
   */

  public Source getSourceForMaster (String masterPath)
    throws CheckinException;
}
