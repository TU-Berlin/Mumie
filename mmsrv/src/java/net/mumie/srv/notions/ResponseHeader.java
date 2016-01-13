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

package net.mumie.srv.notions;

public class ResponseHeader
{
  // ================================================================================
  // Autocoded start
  // ================================================================================

  
  /**
   * Length of the content in the entity body, in bytes
   */

  public static final String CONTENT_LENGTH = "Content-Length";

  /**
   * A status message. This is a non-standard header
   */

  public static final String STATUS = "X-Mumie-Status";

  /**
   * Description of an error. This is a non-standard header
   */

  public static final String ERROR = "X-Mumie-Error";


  // ================================================================================
  // Autocoded end
  // ================================================================================

  /**
   * Disabled constructor.
   */

  private ResponseHeader ()
    throws IllegalAccessException
  {
    throw new IllegalAccessException("ResponseHeader must not be instanciated");
  }
}