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

package net.mumie.cocoon.util;

import java.io.File;
import org.apache.cocoon.environment.Request;

/**
 * Helper class to handle uploads.
 *  
 * <p>
 *   The only method of this interface is {@link #upload upload}. It gets a
 *   {@link Request Request} object as parameter and returns an array of
 *   {@link File File} objectss. The method uploads the files determined by the request and
 *   saves them in a newly created directory. The returned array contains this directory as
 *   the first entry. The remaining entries contain the uploaded files.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: UploadHelper.java,v 1.6 2008/04/30 11:07:41 rassy Exp $</code>
 */

public interface UploadHelper
  extends Identifyable
{
  /**
   * Role as an Avalon service (<code>UploadHelper.class.getName()</code>).
   */

  public static final String ROLE = UploadHelper.class.getName();

  /**
   * Uploads the files determined by the specified request. All files are saved in a newly
   * created directory. The returned array contains this directory as the first entry. The
   * remaining entries contain the uploaded files.
   */

  public File[] upload (Request request)
    throws UploadException;

  /**
   * Same as {@link #upload(Request) upload(Request request)}, but checks if the number of
   * uploaded files equals the specified integer. If not, throws an exception.
   */

  public File[] upload (Request request, int reqNum)
    throws UploadException;
}
