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

package net.mumie.cdk.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents the metatags of a document.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: JavaMetatags.java,v 1.9 2008/09/01 21:49:36 rassy Exp $</code>
 */

public class JavaMetatags
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The map containing the metatags.
   */

  protected Map<String, List<String>> metatags;

  // --------------------------------------------------------------------------------
  // Constructor
  // --------------------------------------------------------------------------------

  /**
   * Build a new instance wrapping the specified data.
   */

  public JavaMetatags (Map<String, List<String>> metatags)
  {
    this.metatags = metatags;
  }

  // --------------------------------------------------------------------------------
  // Accessing metatags
  // --------------------------------------------------------------------------------

  /**
   * Returns the metatag with the specified name as a list of strings. The
   * <code>required</code> flag controls what happens in the case the metatag does not
   * exist: if true, an exception is thrown, otherwise, the method returns an empty list.
   */

  public List<String> getAsList (String name, boolean required)
    throws JavaMetatagException
  {
    List<String> value = this.metatags.get(name);
    if ( value == null )
      {
        if ( required )
          throw new JavaMetatagException("Missing metatag \"" + name + "\"");
        else
          value = new ArrayList<String>();
      }
    return value;
  }

  /**
   * <p>
   *   Returns the metatag with the specified name as a list of strings. If the metatag does
   *   not exist, the method returns null.
   * </p>
   * <p>
   *   Same as {@link #getAsList(String,boolean) getAsList(name, false)}.
   * </p>
   */

  public List<String> getAsList (String name)
    throws JavaMetatagException
  {
    return this.getAsList(name, false);
  }

  /**
   * Returns the metatag with the specified name as a string. This method assumes that the
   * value of the metatag is unique. If the metatag has multiple values, an exception is
   * thrown. The <code>required</code> flag controls what happens in the case the metatag
   * does not exist: if true, an exception is thrown, otherwise, <code>defaultValue</code>
   * is returned. It is allowed that <code>defaultValue</code> is null.
   */

  public String getAsString (String name, boolean required, String defaultValue)
    throws JavaMetatagException
  {
    List<String> value = this.getAsList(name, required);
    switch ( value.size() )
      {
      case 0:
        if ( required )
          throw new JavaMetatagException("Missing metatag \"" + name + "\"");
        else
          return defaultValue;
      case 1:
        return value.get(0);
      default:
        throw new JavaMetatagException
          ("Multiple values for unique metatag \"" + name + "\": " + listToString(value));
      }
  }

  /**
   * <p>
   *   Returns the metatag with the specified name as a string. This method assumes that the 
   *   value of the metatag is unique. If the metatag has multiple values, an exception is
   *   thrown. If the metatag does not exist, <code>defaultValue</code> is returned.
   *   It is allowed that <code>defaultValue</code> is null.
   * </p>
   * <p>
   *   Same as
   *   {@link #getAsString(String,boolean,String) getAsString(name, false, defaultValue)}.
   * </p>
   */

  public String getAsString (String name, String defaultValue)
    throws JavaMetatagException
  {
    return this.getAsString(name, false, defaultValue);
  }

  /**
   * <p>
   *   Returns the metatag with the specified name as a string. This method assumes that the 
   *   value of the metatag is unique. If the metatag has multiple values, an exception is
   *   thrown. If the metatag does not exist, an exception is thrown.
   * </p>
   * <p>
   *   Same as
   *   {@link #getAsString(String,boolean,String) getAsString(name, true, null)}.
   * </p>
   */

  public String getAsString (String name)
    throws JavaMetatagException
  {
    return this.getAsString(name, true, null);
  }

  /**
   * Returns the metatag with the specified name as a boolean. This method assumes that the
   * value of the metatag is unique. If the metatag has multiple values, an exception is
   * thrown. The <code>required</code> flag controls what happens in the case the metatag
   * does not exist: if true, an exception is thrown, otherwise, <code>defaultValue</code>
   * is returned. The string value of the metatag must be <code>"true", "false",
   * "yes",</code> or <code>"no"</code>. If any other value is found, an exception is thrown.
   */

  public boolean getAsBoolean (String name, boolean required, boolean defaultValue)
    throws JavaMetatagException
  {
    String stringValue = this.getAsString(name, required, null);
    if ( stringValue == null )
      return defaultValue;
    else if ( stringValue.equals("true") || stringValue.equals("yes") )
      return true;
    else if ( stringValue.equals("false") || stringValue.equals("no") )
      return false;
    else
      throw new JavaMetatagException
        ("Invalid value for metatag \"" + name + "\": " + stringValue +
         " (must be true|false|yes|no)");
  }

  /**
   * <p>
   *   Returns the metatag with the specified name as a boolean. This method assumes that
   *   the value of the metatag is unique. If the metatag has multiple values, an exception
   *   is thrown. If the metatag does not exist, <code>defaultValue</code> is returned. The
   *   string value of the metatag must be <code>"true", "false", "yes",</code> or
   *   <code>"no"</code>. If any other value is found, an exception is thrown. 
   * </p>
   * <p>
   *   Same as
   *   {@link #getAsBoolean(String,boolean,boolean) getAsBoolean(name, false, defaultValue)}.
   * </p>
   */

  public boolean getAsBoolean (String name, boolean defaultValue)
    throws JavaMetatagException
  {
    return this.getAsBoolean(name, false, defaultValue);
  }

  /**
   * <p>
   *   Returns the metatag with the specified name as a boolean. This method assumes that
   *   the value of the metatag is unique. If the metatag has multiple values, an exception
   *   is thrown. If the metatag does not exist, an exception is thrown. The string value of
   *   the metatag must be <code>"true", "false", "yes",</code> or <code>"no"</code>. If any
   *   other value is found, an exception is thrown.
   * </p>
   * <p>
   *   Same as
   *   {@link #getAsBoolean(String,boolean,boolean) getAsBoolean(name, true, false)}.
   * </p>
   */

  public boolean getAsBoolean (String name)
    throws JavaMetatagException
  {
    return this.getAsBoolean(name, true, false);
  }

  /**
   * Returns the metatag with the specified name as an integer. This method assumes that the
   * value of the metatag is unique. If the metatag has multiple values, an exception is
   * thrown. The <code>required</code> flag controls what happens in the case the metatag
   * does not exist: if true, an exception is thrown, otherwise, <code>defaultValue</code>
   * is returned. If the string value of the metatag can not be parsed as an integer, an
   * exception is thrown. If <code>nonNegative</code> is true, it is checked wether the
   * value is non-negative, and an exception is thrown if it is not.
   */

  public int getAsInt (String name, boolean required, boolean nonNegative,
                          int defaultValue)
    throws JavaMetatagException
  {
    String stringValue = this.getAsString(name, required, null);

    if ( stringValue == null )
      return defaultValue;

    int value = defaultValue;
    try
      {
        value = Integer.parseInt(stringValue);
      }
    catch (Exception exception)
      {
        throw new JavaMetatagException
          ("Failed convert value of metatag \"" + name + "\" to an integer: " + value,
           exception);
      }
    if ( nonNegative && value < 0 )
        throw new JavaMetatagException
          ("Invalid value for metatag \"" + name + "\": " + value +
           " ( must be non-negative)");

    return value;
  }

  /**
   * Returns the metatag with the specified name as an integer. This method assumes that the
   * value of the metatag is unique. If the metatag has multiple values, an exception is
   * thrown. If the metatag does not exist, <code>defaultValue</code> is returned. If the
   * string value of the metatag can not be parsed as an integer, an exception is thrown. If
   * <code>nonNegative</code> is true, it is checked wether the value is non-negative, and
   * an exception is thrown if it is not.
   * <p>
   *   Same as
   *   {@link #getAsInt(String,boolean,boolean,int) getAsInt(name, false, nonNegative, defaultValue)}.
   * </p>
   */

  public int getAsInt (String name, boolean nonNegative, int defaultValue)
    throws JavaMetatagException
  {
    return this.getAsInt(name, false, nonNegative, defaultValue);
  }

  /**
   * Returns the metatag with the specified name as an integer. This method assumes that the
   * value of the metatag is unique. If the metatag has multiple values, an exception is
   * thrown. If the metatag does not exist, an exception is thrown. If the string value of
   * the metatag can not be parsed as an integer, an exception is thrown. If
   * <code>nonNegative</code> is true, it is checked wether the value is non-negative, and
   * an exception is thrown if it is not.
   * <p>
   *   Same as
   *   {@link #getAsInt(String,boolean,boolean,int) getAsInt(name, false, nonNegative, defaultValue)}.
   * </p>
   */

  public int getAsInt (String name, boolean nonNegative)
    throws JavaMetatagException
  {
    return this.getAsInt(name, true, nonNegative, -1);
  }

  // --------------------------------------------------------------------------------
  // Getting all metatag names
  // --------------------------------------------------------------------------------

  /**
   * Returns the names of all metatags which exist in this object.
   */

  public String[] getNames ()
  {
    return this.metatags.keySet().toArray(new String[this.metatags.size()]);
  }

  // --------------------------------------------------------------------------------
  // Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * Concatenates the strings in the specified list, seperating them by blancs.
   */

  protected static String listToString (List<String> list)
  {
    StringBuffer buffer = new StringBuffer();
    for (String item : list)
      {
        if ( buffer.length() > 0 ) buffer.append(" ");
        buffer.append(item);
      }
    return buffer.toString();
  }
}
