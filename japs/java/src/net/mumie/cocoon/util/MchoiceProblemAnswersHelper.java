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

import org.apache.cocoon.environment.Request;

/**
 * <p>
 *   Utility to deal with answers to multiple choice questions.
 * </p>
 * <p>
 *   The main purpose of this interface is to provide a means to convert multiple choice
 *   answers from the <em>request parameter format</em> into the corresponding <em>XML
 *   format</em>. Both the request parameter format and the XML format are defined in the
 *   respective specification.
 * </p>
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: MchoiceProblemAnswersHelper.java,v 1.4 2007/07/11 15:38:53 grudzin Exp $</code>
 */

public interface MchoiceProblemAnswersHelper
{
  /**
   * Role of an implementing class as an Avalon service. Value is
   * <span class="string">"net.mumie.cocoon.util.MultipleChoiceAnswersHelper"</span>.
   */

  public static final String ROLE = "net.mumie.cocoon.util.MchoiceProblemAnswersHelper";

  /**
   * Common prefix of all request parameters that represent a multiple choice answer
   * (<code>"answer"</code>). 
   */

  static final String PREFIX = "answer";

  /**
   * Keyword for the "unique" answer type
   * (<code>"unique"</code>).
   */

  static final String UNIQUE = "unique";

  /**
   * Keyword for the "multiple" answer type
   * (<code>"multiple"</code>).
   */

  static final String MULTIPLE = "multiple";

  /**
   * Keyword for the "yesno" answer type
   * (<code>"yesno"</code>).
   */

  static final String YESNO = "yesno";

  /**
   * Value of request parameters corresponding to a "yesno" question with a positive answer
   * (<code>"yes"</code>). 
   */

  static final String YES = "yes";

  /**
   * Value of request parameters corresponding to a "yesno" question with a negative answer
   * (<code>"no"</code>). 
   */

  static final String NO = "no";

  /**
   * Value of request parameters corresponding to a selected "multiple" question
   * (<code>"selected"</code>).  
   */

  static final String SELECTED = "selected";

  /**
   * Value of a positive entry in the datasheet
   * (<code>"true"</code>).
   */

  static final String TRUE = "true";

  /**
   * Value of a negative entry in the datasheet
   * (<code>"false"</code>).
   */

  static final String FALSE = "false";

  /**
   * Reads the answers from the request parameters in <code>request</code> stores them in
   * <code>dataSheet</code>.
   */

  public void requestToDataSheet (Request request, CocoonEnabledDataSheet dataSheet)
    throws MchoiceProblemAnswersException;
}
