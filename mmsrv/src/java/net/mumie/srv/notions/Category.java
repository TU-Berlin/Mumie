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

public class Category
{
  // --------------------------------------------------------------------------------
  // Global variables and constants, except autocoded 
  // --------------------------------------------------------------------------------

  /**
   * The undefined category.
   */

  public static final int UNDEFINED = -1;

  // --------------------------------------------------------------------------------
  // Auxiliaries 
  // --------------------------------------------------------------------------------

  /**
   * Returns the array index of the specified category code.
   */

  protected static final int indexOf (int code)
  {
    int index = -1;
    for (int i = 0; i < categories.length && index == -1; i++)
      {
        if ( categories[i] == code )
          index = i;
      }
    if ( index == -1 )
      throw new IllegalArgumentException("Invalid category code: " + code);
    return index;
  }

  /**
   * Returns the array index of the category with the specified name.
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
      throw new IllegalArgumentException("Invalid category name: " + name);
    return index;
  }

  // --------------------------------------------------------------------------------
  // Getting codes for names and vice versa
  // --------------------------------------------------------------------------------

  /**
   * Returns the name of the category represented by the specified numerical code.
   */

  public static final String nameFor (int code)
  {
    return names[indexOf(code)];
  }

  /**
   * Returns the numerical code for the specified category name.
   */

  public static final int codeFor (String name)
  {
    return categories[indexOf(name)];
  }

  // --------------------------------------------------------------------------------
  // Checking whether a code or name exists as a category
  // --------------------------------------------------------------------------------

  /**
   * Returns true if the specified integer exists as a category code;
   * otherwise returns false.
   */

  public static final boolean exists (int code)
  {
    for (int i = 0; i < categories.length; i++)
      {
        if ( categories[i] == code )
          return true;
      }
    return false;
  }

  /**
   * Returns true if the specified string exists as a category name;
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
  // Getting all categories
  // --------------------------------------------------------------------------------

  /**
   * Returns an array containing all categorys. The array is is not backed by any
   * variables of this class, so changes to the array do not effect this class. 
   */

  public static final int[] allCategries ()
  {
    int[] categoriesCopy = new int[categories.length];
    System.arraycopy(categories, 0, categoriesCopy, 0, categories.length);
    return categoriesCopy;
  }

  // ================================================================================
  // Autocoded methods start
  // ================================================================================

  
  /**
   * A motivation
   */

  public static final int MOTIVATION = 101;

  /**
   * A definition
   */

  public static final int DEFINITION = 201;

  /**
   * A theorem
   */

  public static final int THEOREM = 202;

  /**
   * An application
   */

  public static final int APPLICATION = 203;

  /**
   * An algorithm
   */

  public static final int ALGORITHM = 204;

  /**
   * A lemma
   */

  public static final int LEMMA = 205;

  /**
   * A proof
   */

  public static final int PROOF = 301;

  /**
   * A deduction
   */

  public static final int DEDUCTION = 302;

  /**
   * A remark
   */

  public static final int REMARK = 303;

  /**
   * An example
   */

  public static final int EXAMPLE = 304;

  /**
   * A visualization
   */

  public static final int VISUALIZATION = 305;

  /**
   * Historical information
   */

  public static final int HISTORY = 306;

  /**
   * A problem consisting of multiple choice questions
   */

  public static final int MCHOICE = 401;

  /**
   * A problem that is to be solved with an applet
   */

  public static final int APPLET = 402;

  /**
   * A problem without corrector
   */

  public static final int TRADITIONAL = 403;

  /**
   * A pretest worksheet
   */

  public static final int PRETEST = 501;

  /**
   * A prelearning worksheet
   */

  public static final int PRELEARN = 502;

  /**
   * A homework worksheet
   */

  public static final int HOMEWORK = 503;

  /**
   * A selftest worksheet
   */

  public static final int SELFTEST = 504;

  /**
   * A corrector for problems
   */

  public static final int CORRECTOR = 601;

  /**
   * Inner class of a corrector for problems
   */

  public static final int CORRECTOR_INNER_CLASS = 602;

  /**
   * An array containing all category codes.
   */

  private static final int[] categories =
  {
    101,
    201,
    202,
    203,
    204,
    205,
    301,
    302,
    303,
    304,
    305,
    306,
    401,
    402,
    403,
    501,
    502,
    503,
    504,
    601,
    602,
  };

  /**
   * An array containing all category names.
   */

  private static final String[] names =
  {
    "motivation",
    "definition",
    "theorem",
    "application",
    "algorithm",
    "lemma",
    "proof",
    "deduction",
    "remark",
    "example",
    "visualization",
    "history",
    "mchoice",
    "applet",
    "traditional",
    "pretest",
    "prelearn",
    "homework",
    "selftest",
    "corrector",
    "corrector_inner_class",
  };


  // ================================================================================
  // Autocoded methods end
  // ================================================================================

  /**
   * Disabled constructor.
   */

  private Category ()
    throws IllegalAccessException
  {
    throw new IllegalAccessException("Category must not be instanciated");
  }
}