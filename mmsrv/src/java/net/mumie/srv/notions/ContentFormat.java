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

public class ContentFormat
{
  // --------------------------------------------------------------------------------
  // Global variables and constants, except autocoded 
  // --------------------------------------------------------------------------------

  /**
   * The undefined format.
   */

  public static final int UNDEFINED = -1;

  // --------------------------------------------------------------------------------
  // Auxiliaries 
  // --------------------------------------------------------------------------------

  /**
   * Returns the array index of the specified format code.
   */

  protected static final int indexOf (int code)
  {
    int index = -1;
    for (int i = 0; i < contentFormats.length && index == -1; i++)
      {
        if ( contentFormats[i] == code )
          index = i;
      }
    if ( index == -1 )
      throw new IllegalArgumentException("Invalid content format code: " + code);
    return index;
  }

  /**
   * Returns the array index of the format with the specified name.
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
      throw new IllegalArgumentException("Invalid content format name: " + name);
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
    return contentFormats[indexOf(name)];
  }

  // --------------------------------------------------------------------------------
  // Checking whether a code or name exists as a format
  // --------------------------------------------------------------------------------

  /**
   * Returns true if the specified integer exists as a format code;
   * otherwise returns false.
   */

  public static final boolean exists (int code)
  {
    for (int i = 0; i < contentFormats.length; i++)
      {
        if ( contentFormats[i] == code )
          return true;
      }
    return false;
  }

  /**
   * Returns true if the specified string exists as a format name;
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
  // Getting all formats
  // --------------------------------------------------------------------------------

  /**
   * Returns an array containing all formats. The array is is not backed by any
   * variables of this class, so changes to the array do not effect this class. 
   */

  public static final int[] allFormats ()
  {
    int[] contentFormatsCopy = new int[contentFormats.length];
    System.arraycopy(contentFormats, 0, contentFormatsCopy, 0, contentFormats.length);
    return contentFormatsCopy;
  }

  // --------------------------------------------------------------------------------
  // Formats of data entity types
  // --------------------------------------------------------------------------------

  /**
   * Returns the content format of the specified data entity type. Same as
   * {@link EntityType#contentFormatOf EntityType.contentFormatOf(type)}.
   */

  public static final int ofEntityType (int type)
  {
    return EntityType.contentFormatOf(type);
  }

  // ================================================================================
  // Autocoded methods start
  // ================================================================================

  
  /**
   * No content
   */

  public static final int NONE = 101;

  /**
   * Text content
   */

  public static final int TEXT = 102;

  /**
   * Binary content
   */

  public static final int BINARY = 103;

  /**
   * An array containing all content format codes.
   */

  private static final int[] contentFormats =
  {
    101,
    102,
    103,
  };

  /**
   * An array containing all content format names.
   */

  private static final String[] names =
  {
    "none",
    "text",
    "binary",
  };


  // ================================================================================
  // Autocoded methods end
  // ================================================================================

  /**
   * Disabled constructor.
   */

  private ContentFormat ()
    throws IllegalAccessException
  {
    throw new IllegalAccessException("Format must not be instanciated");
  }
}