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

package net.mumie.japs.client;

/**
 * Provides static constants for Japs-specific response headers.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: JapsResponseHeader.java,v 1.4 2007/07/16 11:15:37 grudzin Exp $</code>
 */

public class JapsResponseHeader
{
  /**
   * Common prefix of all response headers defined in this class (<code>"X-MUMIE-"</code>).
   */

  protected static final String PREFIX = "X-Mumie-";

  /**
   * Indicates that the user must login (<code>"X-Mumie-Login-Required"</code>).
   */

  public static final String LOGIN_REQUIRED = PREFIX + "Login-Required";

  /**
   * Contains a status message (<code>"X-Mumie-Status"</code>).
   */

  public static final String STATUS = PREFIX + "Status";

  /**
   * Contains a recommended filename (<code>"X-Mumie-Filename"</code>).
   */

  public static final String FILENAME = PREFIX + "Filename";

  /**
   * Contains the description of an error occurred (<code>"X-Mumie-Error"</code>).
   */

  public static final String ERROR = PREFIX + "Error";

  /**
   * Private contructor to prevent instantiation.
   */

  private JapsResponseHeader ()
  {
    throw new RuntimeException("Must not be instantiated: " + JapsRequestParam.class);
  }
}
