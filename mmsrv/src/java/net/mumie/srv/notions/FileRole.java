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

public class FileRole
{
  // --------------------------------------------------------------------------------
  // Global variabled and constants, except autocoded 
  // --------------------------------------------------------------------------------

  /**
   * The undefined file role.
   */

  public static final int UNDEFINED = -1;

  // --------------------------------------------------------------------------------
  // Auxiliaries 
  // --------------------------------------------------------------------------------

  /**
   * Returns the array index of the specified file role code.
   */

  protected static final int indexOf (int code)
  {
    int index = -1;
    for (int i = 0; i < fileRoles.length && index == -1; i++)
      {
        if ( fileRoles[i] == code )
          index = i;
      }
    if ( index == -1 )
      throw new IllegalArgumentException("Invalid file role code: " + code);
    return index;
  }

  /**
   * Returns the array index of the file role with the specified name.
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
      throw new IllegalArgumentException("Invalid file role name: " + name);
    return index;
  }

  /**
   * Returns the array index of the file role with the specified suffix.
   */

  protected static final int indexOfSuffix (String suffix)
  {
    int index = -1;
    for (int i = 0; i < suffixes.length && index == -1; i++)
      {
        if ( suffixes[i].equals(suffix) )
          index = i;
      }
    if ( index == -1 )
      throw new IllegalArgumentException("Invalid file role suffix: " + suffix);
    return index;
  }

  // --------------------------------------------------------------------------------
  // Getting codes for names and vice versa
  // --------------------------------------------------------------------------------

  /**
   * Returns the name for the file role represented by the specified numerical
   * code.
   */

  public static final String nameFor (int code)
  {
    return names[indexOf(code)];
  }

  /**
   * Returns the numerical code for the specified file role name.
   */

  public static final int codeFor (String name)
  {
    return fileRoles[indexOf(name)];
  }

  /**
   * Returns the numerical code of the file role for the specified suffix.
   */

  public static final int codeForSuffix (String suffix)
  {
    return fileRoles[indexOfSuffix(suffix)];
  }

  // --------------------------------------------------------------------------------
  // Getting suffixes
  // --------------------------------------------------------------------------------

  /**
   * Returns the suffix for the file role represented by the specified numerical
   * code.
   */

  public static final String suffixOf (int code)
  {
    return suffixes[indexOf(code)];
  } 

  /**
   * Returns the suffix for the specified file role name.
   */

  public static final String suffixOf (String name)
  {
    return suffixes[indexOf(name)];
  } 

  // --------------------------------------------------------------------------------
  // Checking whether a code or name exists as a file role
  // --------------------------------------------------------------------------------

  /**
   * Returns true if the specified integer exists as a file role code;
   * otherwise returns false.
   */

  public static final boolean exists (int code)
  {
    for (int i = 0; i < fileRoles.length; i++)
      {
        if ( fileRoles[i] == code )
          return true;
      }
    return false;
  }

  /**
   * Returns true if the specified string exists as a file role name;
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
  // Getting all file roles
  // --------------------------------------------------------------------------------

  /**
   * Returns an array containing all file roles. The array is is not backed by any
   * variables of this class, so changes to the array do not effect this class. 
   */

  public static final int[] allFileRoles ()
  {
    int[] fileRolesCopy = new int[fileRoles.length];
    System.arraycopy(fileRoles, 0, fileRolesCopy, 0, fileRoles.length);
    return fileRolesCopy;
  }

  // ================================================================================
  // Autocoded methods start
  // ================================================================================

  
  /**
   * Role as a master file
   */

  public static final int MASTER = 101;

  /**
   * Role as a content file
   */

  public static final int CONTENT = 102;

  /**
   * Role as a source file
   */

  public static final int SOURCE = 103;

  /**
   * Role as a preview file
   */

  public static final int PREVIEW = 104;

  /**
   * An array containing all file role codes.
   */

  private static final int[] fileRoles =
  {
    101,
    102,
    103,
    104,
  };

  /**
   * An array containing all file role names.
   */

  private static final String[] names =
  {
    "master",
    "content",
    "source",
    "preview",
  };

  /**
   * An array containing all file role suffixes.
   */

  private static final String[] suffixes =
  {
    "meta",
    "content",
    "src",
    "preview",
  };


  // ================================================================================
  // Autocoded methods end
  // ================================================================================

  /**
   * Disabled constructor.
   */

  private FileRole ()
    throws IllegalAccessException
  {
    throw new IllegalAccessException("FileRole must not be instanciated");
  }
}