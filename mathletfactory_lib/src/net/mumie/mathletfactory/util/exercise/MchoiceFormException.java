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

package net.mumie.mathletfactory.util.exercise;

/**
 * Indicates a problem with multiple choice form.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: MchoiceFormException.java,v 1.2 2007/07/16 11:40:16 grudzin Exp $</code>
 */

public class MchoiceFormException extends Exception
{
  /**
   * Creates a new <code>MchoiceFormException</code> with message
   * <code>description</code>.
   */

  public MchoiceFormException (String description)
  {
    super(description);
  }

  /**
   * Creates a new <code>MchoiceFormException</code> that wraps
   * <code>throwable</code>.
   */

  public MchoiceFormException (Throwable throwable)
  {
    super(throwable);
  }

  /**
   * Creates a new <code>MchoiceFormException</code> with message
   * <code>description</code> that wraps <code>throwable</code>.
   */

  public MchoiceFormException (String description, Throwable throwable)
  {
    super(description, throwable);
  }
}
