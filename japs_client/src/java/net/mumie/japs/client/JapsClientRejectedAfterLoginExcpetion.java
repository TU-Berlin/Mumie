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

import java.net.HttpURLConnection;

/**
 * Indicates that the server has rejected the request after a successful login. This is a
 * strange behaviour which should not happen in normal operation. Nevertheless, it cannot be
 * excluded. This subclass of {@link JapsClientException JapsClientException} is provided to
 * give applications an opportunity to react appropriately.
 */

public class JapsClientRejectedAfterLoginExcpetion extends JapsClientException
{
  /**
   * Fixed text describing the cause of the exception. The text is:
   * <pre>"rejected after successful login"</pre>.
   */

  public static final String CAUSE = "rejected after successful login";

  /**
   * Creates a new instance with the specified connection. Calls a superclass contructor
   * like this:
   * <pre>{@link JapsClientException#<constructor>(HttpURLConnection, String) super(connection, CAUSE)}.
   */

  public JapsClientRejectedAfterLoginExcpetion (HttpURLConnection connection)
  {
    super(connection, CAUSE);
  }

}