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

package net.mumie.cocoon.notions;

public class Id
{
  /**
   * Indicates that the id is undefined.
   */

  public static final int UNDEFINED = -1;

  /**
   * If a variable has this value, then it means that this variable currently does not
   * contain the id.
   */

  public static final int DEFINED_ELSEWHERE = -2;

  /**
   * Indicates that the id should be set automatically.
   */

  public static final int AUTO = -3;

  /**
   * Checks if <code>id</code> is a possible id value, and throws an exception otherwise.
   */

  public static void checkId (int id)
    throws IllegalArgumentException
  {
    if ( id >= 0 ||
         id == UNDEFINED ||
         id == DEFINED_ELSEWHERE ||
         id == AUTO )
      return;
    else
      throw new IllegalArgumentException("Illegal id value: " + id);
  }

  /**
   * 
   */

  public static int parseId (String string)
    throws NumberFormatException, IllegalArgumentException
  {
    if ( string.equals("UNDEFINED") )
      return UNDEFINED;
    else if ( string.equals("DEFINED_ELSEWHERE") )
      return DEFINED_ELSEWHERE;
    else if ( string.equals("AUTO") )
      return AUTO;
    else
      {
        int id = Integer.parseInt(string);
        if ( id < 0 )
          throw new IllegalArgumentException("Illegal id value: " + id);
        return id;
      }
  }

  /**
   * 
   */

  public static String toString (int id)
    throws IllegalArgumentException
  {
    switch ( id )
      {
      case UNDEFINED:
        return "UNDEFINED";
      case DEFINED_ELSEWHERE:
        return "DEFINED_ELSEWHERE";
      case AUTO:
        return "AUTO";
      default:
        if ( id < 0 )
          throw new IllegalArgumentException("Illegal id value: " + id);
        return Integer.toString(id);
      }
  }
}
