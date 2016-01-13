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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Implementation of {@link Content Content} which revieved the content from a string
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: StringContent.java,v 1.3 2009/06/16 16:06:23 rassy Exp $</code>
 */

public class StringContent implements Content
{
  /**
   * The content as an aray of bytes.
   */

  protected final byte[] bytes;

  /**
   * Creates a new instance from the specified string and with the specified charset.
   */

  public StringContent (String string, Charset charset)
  {
    if ( string == null )
      throw new IllegalArgumentException("String is null");
    this.bytes = (charset == null ? string.getBytes() : string.getBytes(charset));
  }

  /**
   * Creates a new instance from the specified string and with the default charset.
   */

  public StringContent (String string)
  {
    this(string, null);
  }

  /**
   * Returns an input stream from which the content can be read.
   */

  public InputStream getInputStream ()
  {
    return new ByteArrayInputStream(this.bytes);
  }

  /**
   * Returns the length of the content in bytes, or -1 if the length can not be determined.
   */

  public long getLength ()
  {
    return this.bytes.length;
  }
}