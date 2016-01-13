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

package net.mumie.cocoon.sign;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipFile;
import net.mumie.cocoon.util.Identifyable;

public interface SignHelper extends Identifyable
{
  /**
   * Role of this class as an Avalon service (<code>SignHelper.class.getName()</code>).
   */

  public static final String ROLE = SignHelper.class.getName();

  /**
   * Creates a signature for the data provided by the specified input stream.
   */

  public byte[] sign (InputStream in)
    throws SignHelperException;

  /**
   * Creates a signature for certain data and writes the data and the signate to a zip
   * archive. The data is provided by the specified input stream. The zip archive is written
   * to the specified output stream. The archive contains two entries: The data and the
   * signature. The names of the two entries are given by {@link #DATA DATA} and
   * {@link #SIGNATURE SIGNATURE}, respectively.
   *
   * @param in the input strem from which the data to sign are read
   * @param out the output stream the zip archive is written to
   */

  public byte[] signAndZip (InputStream in, OutputStream out)
    throws SignHelperException;

  /**
   * Verifies the data from th specified input stream with respect to the specified
   * signature.
   */

  public boolean verify (byte[] sigBytes, InputStream in)
    throws SignHelperException;

  /**
   * Verifies the data/signature pair stored in the specified zip file. The zip file must
   * have the form of the zip archive described with {@link #signAndZip(in,out) signAndZip}.
   */

  public boolean verifyZip (ZipFile zipFile)
    throws SignHelperException;

  /**
   * Returns a string representation of the specified byte array.
   */

  public String bytesToString (byte[] bytes);

}
