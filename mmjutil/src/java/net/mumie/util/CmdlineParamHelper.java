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

package net.mumie.util;

import java.util.Map;
import java.util.List;

/**
 * <p>
 *   Represents a list of command line parameters. Provides methods to iterate through the
 *   parameters, test for certain expressions, and to retrieve the respective tokens.
 * </p>
 * <p>
 *   Usage example:
 *   <pre>
 *     public static void main (String[] params)
 *       throws Exception
 *     {
 *       String dir = null;
 *       String configFile = null;
 *       Map input = new HashMap();
 *   
 *       CmdlineParamHelper paramHelper = new CmdlineParamHelper(params);
 *       while ( paramHelper.next() )
 *         {
 *           if ( paramHelper.checkOptionWithValue("--dir") )
 *             dir = paramHelper.getValue();
 *           else if ( paramHelper.checkOptionWithValue("--config-file") )
 *             configFile = paramHelper.getValue();
 *           else if ( paramHelper.checkOptionWithKeyValuePair("--input", "-i") )
 *             paramHelper.copyKeyValuePair(input);
 *           else
 *             throw new IllegalArgumentException
 *               ("Unknown parameter: " + paramHelper.getParam());
 *         }
 *   
 *       // ...
 *   
 *     }</pre>
 * </p>
 */

public class CmdlineParamHelper
{
  /**
   * The array holding all parameters of the command line.
   */

  protected String[] params = null;

  /**
   * Index of the current parameter.
   */

  protected int index = -1;

  /**
   * Index of the last parameter.
   */

  protected int maxIndex = -1;

  /**
   * The current parameter as a string.
   */

  protected String param = null;

  /**
   * The current parameter value, if any.
   */

  protected String value = null;

  /**
   * The current parameter key-value pair, if any.
   */

  protected KeyValuePair keyValuePair = null;

  /**
   * Goes to the next parameter. Returns <code>true</code> if a next parameter exists,
   * otherwise <code>false</code>.
   */

  public boolean next ()
  {
    if ( this.index < this.maxIndex )
      {
        this.index++;
        this.param = this.params[index];
        this.value = null;
        this.keyValuePair = null;
        return true;
      }
    else
      return false;
  }

  /**
   * Checks if the current parameter equals the specified string.
   */

  public boolean checkParam (String name)
  {
    return ( this.param.equals(name) );
  }

  /**
   * Checks if the current parameter equals one of the two specified strings.
   */

  public boolean checkParam (String name1, String name2)
  {
    return ( this.param.equals(name1) || this.param.equals(name2) );
  }

  /**
   * <p>
   *   Checks if the current parameter is an option with the specified name and has a
   *   value. A <em>parameter with value</em> is an expression of the form
   *   <pre>  --foo=bar</pre>
   *   or
   *   <pre>  --foo bar</pre>
   *   Here, <code>--foo</code> is the name of the parameter, and <code>bar</code> its
   *   value.
   * </p>
   * <p>
   *   If the current parameter has the specified name and has a value, the method returns
   *   <code>true</code>. The value may be retrieved by {@link #getValue() getValue}
   *   afterwards. If the current parameter has not the specified name, the method returns
   *   <code>false</code>. If the current parameter has the specified name, but no value,
   *   an exception is thrown.
   * </p>
   * <p>
   *   The <code>dashForbidden</code> flag controls whether a dash is forbidden at the
   *   beginning of the parameter value or not.
   * </p>
   */

  public boolean checkOptionWithValue (String name, boolean dashForbidden)
  { 
    if ( this.param.equals(name) )
      {
        if ( !this.next() || ( dashForbidden && this.param.startsWith("-") ) )
          throw new IllegalArgumentException("Missing value after " + name);
        this.value = this.param;
        return true;
      }
    else if ( this.param.startsWith(name + "=") )
      {
        this.value = this.param.substring((name + "=").length());
        return true;
      }
    else
      return false;
  }

  /**
   * Same as {@link #checkOptionWithValue(String,boolean) checkOptionWithValue(name, true)}.
   */

  public boolean checkOptionWithValue (String name)
  {
    return this.checkOptionWithValue(name, true);
  }

  /**
   * Checks if the current parameter is an option with one of the specified names and has a
   * value. Same as
   * <pre>
   *   return
   *    ( {@link #checkOptionWithValue(String,boolean) this.checkOptionWithValue(name1, dashForbidden)} ||
   *      {@link #checkOptionWithValue(String,boolean) this.checkOptionWithValue(name2, dashForbidden)} );</pre>
   * This method is especially useful if you want to test for two options which have the
   * same meaning. A typical case is a "long" option <code>--foo</code> and an equivalent
   * "short" option <code>-f</code>.
   */

  public boolean checkOptionWithValue (String name1, String name2, boolean dashForbidden)
  {
    return
      ( this.checkOptionWithValue(name1, dashForbidden) ||
        this.checkOptionWithValue(name2, dashForbidden) );
  }

  /**
   * Checks if the current parameter is an option with one of the specified names and has a
   * value. Same as
   * {@link #checkOptionWithValue(String,String,boolean) this.checkOptionWithValue(name1, name2, true)}.
   */

  public boolean checkOptionWithValue (String name1, String name2)
  {
    return this.checkOptionWithValue(name1, name2, true);
  }

  /**
   * <p>
   *   Checks if the current parameter is an option with the specified name and has a
   *   key-value pair. A <em>parameter with key-value pair</em> is an expression
   *    of the form
   *   <pre>  --foo bar=xyz</pre>
   *   Here, <code>--foo</code> is the name of the parameter, <code>bar</code> the key and
   *   <code>xyz</code> the value.
   * </p>
   * <p>
   *   If the current parameter has the specified name and has a key-value pair, the method
   *   returns <code>true</code>. The key-value-pair may be retrieved by
   *   {@link #getKeyValuePair() getKeyValuePair} or copied to a map by
   *   {@link #copyKeyValuePair(Map) copyKeyValuePair} afterwards. If the current parameter has
   *   not the specified name, the method returns <code>false</code>. If the current
   *   parameter has the specified name, but no key-value pair, an exception is thrown.
   * </p>
   */

  public boolean checkOptionWithKeyValuePair (String name)
  {
    if ( this.param.equals(name) )
      {
        if ( !this.next()  )
          throw new IllegalArgumentException("Missing key-value pair after " + name);
        this.keyValuePair = new KeyValuePair(this.param);
        return true;
      }
    else
      return false;
  }

  /**
   * Checks if the current parameter is an option with one of the specified names and has a
   * key-value pair. Same as
   * <pre>
   *   return
   *    ( {@link #checkOptionWithKeyValuePair(String) this.checkOptionWithKeyValuePair(name1)} ||
   *      {@link #checkOptionWithKeyValuePair(String) this.checkOptionWithKeyValuePair(name2)} );</pre>
   * This method is especially useful if you want to test for two options which have the
   * same meaning. A typical case is a "long" option <code>--foo</code> and an equivalent
   * "short" option <code>-f</code>.
   */

  public boolean checkOptionWithKeyValuePair (String name1, String name2)
  {
    return
      ( this.checkOptionWithKeyValuePair(name1) ||
        this.checkOptionWithKeyValuePair(name2) );
  }

  /**
   * Returns <code>true</code> if the current parameter does not start with a dash.
   */

  public boolean checkArgument ()
  {
    return ( !this.param.startsWith("-") );
  }

  /**
   * Returns the current parameter.
   */

  public String getParam ()
  {
    return this.param;
  }

  /**
   * Returns the current parameter value. This method can only be called after
   * {@link #checkOptionWithValue(String) checkOptionWithValue} was called and returned
   * <code>true</code>. Otherwise, an exception is thrown.
   */

  public String getValue ()
  {
    if ( this.value == null )
      throw new IllegalStateException("No value available");
    return this.value;
  }

  /**
   * Returns the current key-value pair. This method can only be called after
   * {@link #checkOptionWithKeyValuePair(String) checkOptionWithKeyValuePair} was called and
   * returned <code>true</code>. Otherwise, an exception is thrown.
   */

  public KeyValuePair getKeyValuePair ()
  {
    if ( this.keyValuePair == null )
      throw new IllegalStateException("No key-value pair available");
    return this.keyValuePair;
  }

  /**
   * Inserts the current key-value pair into the specified map. This method can only be
   * called after 
   * {@link #checkOptionWithKeyValuePair(String) checkOptionWithKeyValuePair} was called and
   * returned <code>true</code>. Otherwise, an exception is thrown.
   */

  public void copyKeyValuePair (Map map)
  {
    if ( this.keyValuePair == null )
      throw new IllegalStateException("No key-value pair available");
    map.put(this.keyValuePair.getKey(), this.keyValuePair.getValue());
  }

  /**
   * Creates a new <code>CmdlineParamHelper</code> for the specified parameters.
   */

  public CmdlineParamHelper (String[] params)
  {
    this.params = params;
    this.maxIndex = params.length - 1;
  }

  /**
   * Creates a new <code>CmdlineParamHelper</code> for the specified parameters.
   */

  public CmdlineParamHelper (List paramList)
  {
    this.params = (String[])paramList.toArray(new String[paramList.size()]);
    this.maxIndex = params.length - 1;
  }

}
