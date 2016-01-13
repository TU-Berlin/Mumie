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

package net.mumie.japs.log.analyse;

public abstract class AbstractLogAnalyserComponent
  implements LogAnalyserComponent
{
  /**
   * Does nothing.
   */

  public void setParameter (String name, String value)
    throws LogOutputProcessingException
  {}

  /**
   * Auxiliary method to process parameter values; converts a string into a boolean. If the
   * string is <code>"true"</code> or <code>"yes"</code>, returns <code>true</code>, if the
   * string is <code>"false"</code> or <code>"no"</code>, returns <code>no</code>, if the
   * string ist something else, thorws a
   * {@link LogOutputProcessingException LogOutputProcessingException}.
   */

  protected static boolean stringToBoolean (String string)
    throws LogOutputProcessingException
  {
    if ( string.equals("true") || string.equals("yes") )
      return true;
    else if ( string.equals("false") || string.equals("no") )
      return false;
    else
      throw new LogOutputProcessingException("Illegal boolean specifier: " + string);
  }
}