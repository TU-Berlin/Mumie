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

public class RefType
{
  // --------------------------------------------------------------------------------
  // Global variabled and constants, except autocoded 
  // --------------------------------------------------------------------------------

  /**
   * The undefined reference type.
   */

  public static final int UNDEFINED = -1;

  // --------------------------------------------------------------------------------
  // Auxiliaries 
  // --------------------------------------------------------------------------------

  /**
   * Returns the array index of the specified reference type code.
   */

  protected static final int indexOf (int code)
  {
    int index = -1;
    for (int i = 0; i < refTypes.length && index == -1; i++)
      {
        if ( refTypes[i] == code )
          index = i;
      }
    if ( index == -1 )
      throw new IllegalArgumentException("Invalid reference type code: " + code);
    return index;
  }

  /**
   * Returns the array index of the reference type with the specified name.
   */

  protected static final int indexOf (String name)
  {
    int index = -1;
    for (int i = 0; i < names.length && index == -1; i++)
      {
        if ( names[i].equals(name) )
          index = i;
      }
    if ( index == -1 )
      throw new IllegalArgumentException("Invalid reference type name: " + name);
    return index;
  }

  // --------------------------------------------------------------------------------
  // Getting codes for names and vice versa
  // --------------------------------------------------------------------------------

  /**
   * Returns the name for the (pseudo-)document type represented by the specified numerical
   * code.
   */

  public static final String nameFor (int code)
  {
    return names[indexOf(code)];
  }

  /**
   * Returns the numerical code for the specified (pseudo-)document type name.
   */

  public static final int codeFor (String name)
  {
    return refTypes[indexOf(name)];
  }

  // --------------------------------------------------------------------------------
  // Checking whether a code or name exists as a reference type
  // --------------------------------------------------------------------------------

  /**
   * Returns true if the specified integer exists as a reference type code;
   * otherwise returns false.
   */

  public static final boolean exists (int code)
  {
    for (int i = 0; i < refTypes.length; i++)
      {
        if ( refTypes[i] == code )
          return true;
      }
    return false;
  }

  /**
   * Returns true if the specified string exists as a reference type name;
   * otherwise returns false.
   */

  public static final boolean exists (String name)
  {
    for (int i = 0; i < names.length; i++)
      {
        if ( names[i].equals(name) )
          return true;
      }
    return false;
  }

  // --------------------------------------------------------------------------------
  // Getting all reference types
  // --------------------------------------------------------------------------------

  /**
   * Returns an array containing all reference types. The array is is not backed by any
   * variables of this class, so changes to the array do not effect this class. 
   */

  public static final int[] allRefTypes ()
  {
    int[] refTypesCopy = new int[refTypes.length];
    System.arraycopy(refTypes, 0, refTypesCopy, 0, refTypes.length);
    return refTypesCopy;
  }

  // ================================================================================
  // Autocoded methods start
  // ================================================================================

  
  /**
   * Used as a component
   */

  public static final int COMPONENT = 101;

  /**
   * Referenced as a link
   */

  public static final int LINK = 102;

  /**
   * An array containing all reference type codes.
   */

  private static final int[] refTypes =
  {
    101,
    102,
  };

  /**
   * An array containing all reference type names.
   */

  private static final String[] names =
  {
    "component",
    "link",
  };


  // ================================================================================
  // Autocoded methods end
  // ================================================================================

  /**
   * Disabled constructor.
   */

  private RefType ()
    throws IllegalAccessException
  {
    throw new IllegalAccessException("RefType must not be instanciated");
  }
}