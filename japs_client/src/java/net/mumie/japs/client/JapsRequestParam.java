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
 * Provides static constants for frequently used Japs request parameters.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: JapsRequestParam.java,v 1.6 2007/07/16 11:15:37 grudzin Exp $</code>
 */

public class JapsRequestParam
{
  /**
   * <code>"ref"</code> &ndash; a reference id. For example, needed by problem applets to
   * specify the problem element and the course subsection it is in.
   */

  public static final String REF = "ref";

  /**
   * Same as {@link #REF}.
   */

  public static final String PROBLEM_REF = REF;

  /**
   * <code>"content"</code> &ndash; the body of some data sent to Japs. For example, the
   * answer data sheet of a problem applet.
   */

  public static final String CONTENT = "content";

  /**
   * <code>"course"</code> &ndash; a course id..
   */

  public static final String COURSE = "course";

  /**
   * <code>"worksheet"</code> &ndash; a worksheet id..
   */

  public static final String WORKSHEET = "worksheet";

  /**
   * <code>"problem"</code> &ndash; a problem id..
   */

  public static final String PROBLEM = "problem";

  /**
   * <code>"sendReceipt"</code> &ndash; whether the server should send a receipt. Used when
   * a problem answer is saved.
   */

  public static final String SEND_RECEIPT = "sendReceipt";

  /**
   * <code>"report"</code> &ndash; a quality feedback report.
   */

  public static final String REPORT = "report";

  /**
   * <code>"subject"</code> &ndash; a quality feedback subject.
   */

  public static final String SUBJECT = "subject";

  /**
   * Private constructor to prevent instantiation.
   */

  private JapsRequestParam ()
  {
    throw new RuntimeException("Must not be instantiated: " + JapsRequestParam.class);
  }
}
