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

/**
 * Indicates a problem with an answer to a multiple choice question.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: MchoiceProblemAnswersException.java,v 1.4 2007/07/11 15:38:53 grudzin Exp $</code>
 */

public class MchoiceProblemAnswersException extends Exception
{
  /**
   * Creates a new <code>MchoiceProblemAnswersException</code> with message
   * <code>description</code>.
   */

  public MchoiceProblemAnswersException (String description)
  {
    super(description);
  }

  /**
   * Creates a new <code>MchoiceProblemAnswersException</code> that wraps
   * <code>throwable</code>.
   */

  public MchoiceProblemAnswersException (Throwable throwable)
  {
    super(throwable);
  }

  /**
   * Creates a new <code>MchoiceProblemAnswersException</code> with message
   * <code>description</code> that wraps <code>throwable</code>.
   */

  public MchoiceProblemAnswersException (String description, Throwable throwable)
  {
    super(description, throwable);
  }
}
