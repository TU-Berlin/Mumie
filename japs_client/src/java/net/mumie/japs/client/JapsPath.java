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
 * Provides static constants for frequently used Japs paths.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: JapsPath.java,v 1.13 2007/11/02 15:55:10 rassy Exp $</code>
 */

public class JapsPath
{
  /**
   * Path for login (<code>"public/auth/login"</code>). This is the path where to send the
   * login form data.
   */

  public static final String LOGIN = "public/auth/login";

  /**
   * Path for logout (<code>"public/auth/logout"</code>).
   */

  public static final String LOGOUT = "public/auth/logout";

  /**
   * Path for checkin (<code>"protected/checkin/checkin"</code>).
   */

  public static final String CHECKIN = "protected/checkin/checkin";

  /**
   * Path under which the data sheet  for a problem may be downloaded
   * (<code>"protected/homework/problem-data"</code>). 
   */

  public static final String PROBLEM_DATA = 
    "protected/data/problem";

  /**
   * Path under which the answer data sheet of a problem may be uploaded
   * (<code>"protected/homework/store-problem-answers"</code>). 
   */

  public static final String STORE_PROBLEM_ANSWERS =
    "protected/store/problem-answers";

  /**
   * Path to which applet quality feedback reports are sent
   * (<code>"protected/qf/applet"</code>).
   */

  public static final String QF_APPLET = "protected/qf/applet";

  /**
   * Private contructor to prevent instantiation.
   */

  private JapsPath ()
  {
    throw new RuntimeException("Must not be instantiated: " + JapsPath.class);
  }
}
