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

import java.io.File;
import java.io.InputStream;
import java.util.zip.ZipFile;

/**
 * A {@link CheckinRepository CheckinRepository} that gets its (pseudo-)documents from a zip
 * archive.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: ZipCheckinRepository.java,v 1.8 2009/06/16 16:06:23 rassy Exp $</code>
 */

public interface ZipCheckinRepository extends CheckinRepository
{
  /**
   * Role as an Avalon service (<code>ZipCheckinRepository.class.getName()</code>).
   */

  public static final String ROLE = ZipCheckinRepository.class.getName();

  /**
   * Initializes this checkin repository with the specified zip file.
   */

  public void setup (ZipFile zipFile)
    throws CheckinException;

  /**
   * Initializes this checkin repository with the specified file, which must be a zip
   * archive.
   */

  public void setup (File file)
    throws CheckinException;
}