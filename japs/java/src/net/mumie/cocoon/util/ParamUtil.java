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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import net.mumie.cocoon.notions.Category;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.UseMode;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.environment.Request;

public class ParamUtil
{
  // --------------------------------------------------------------------------------
  // Global variables and constants.
  // --------------------------------------------------------------------------------

  /**
   * The timframe keyword <code>"before"</code> as a constant.
   */

  public static final String BEFORE = "before";

  /**
   * The timframe keyword <code>"inside"</code> as a constant.
   */

  public static final String INSIDE = "inside";

  /**
   * The timframe keyword <code>"after"</code> as a constant.
   */

  public static final String AFTER = "after";

  // --------------------------------------------------------------------------------
  // Get infos about parameters
  // --------------------------------------------------------------------------------

  /**
   * Returns true if <code>parameters</code> contain a parameter with the
   * specified name, otherwise false. This method is slightly more strict then
   * {@link Parameters#isParameter isParameter} from {@link Parameters Parameters}: It
   * returns true if and only if the parameter exists <em>and</em> is not the
   * empty string.
   */

  public static boolean checkIfSet (Parameters parameters, String name)
    throws ParameterException
  {
    return
      ( parameters.isParameter(name) &&
	!parameters.getParameter(name).equals("") );
  }

  // --------------------------------------------------------------------------------
  // Get parameter values
  // --------------------------------------------------------------------------------

  /**
   * Returns the parameter with the specified name as a string. If the parameter does not
   * exist, throws an exception.  Note that the method {@link #checkIfSet checkIfSet} is
   * used to check whether the parameter exists.
   *
   * @param parameters the parameters object to look up for the specified parameter
   * @param name the name of the parameter
   *
   * @throws ParameterException if something goes wrong
   */

  public static String getAsString (Parameters parameters, String name)
    throws ParameterException
  {
    if ( !checkIfSet(parameters, name) )
      throw new ParameterException("Missing paramater: " + name);
    return parameters.getParameter(name);
  }

  /**
   * Returns the parameter with the specified name as a string. If the parameter does not
   * exist, returns the specified default value. Note that the method
   * {@link #checkIfSet checkIfSet} is used to check whether the parameter exists. The
   * default value may be null.
   *
   * @param parameters the parameters object to look up for the specified parameter
   * @param name the name of the parameter
   * @param defaultValue the defalut value (value to return if the parameter is not set)
   *
   * @throws ParameterException if something goes wrong
   * 
   */

  public static String getAsString (Parameters parameters,
                                    String name,
                                    String defaultValue)
    throws ParameterException
  {
    return
      (checkIfSet(parameters, name)
       ? parameters.getParameter(name)
       : defaultValue);
  }

  /**
   * <p>
   *   Returns the parameter with the specified name as a boolean, or a default value
   *   if the parameter is not set. This method differs from 
   *   {@link Parameters#getParameterAsBoolean(String,boolean) getParameterAsBoolean(String,boolean)}
   *   in two points:
   * <p>
   * <ol>
   *   <li>The Method {@link #checkIfSet checkIfSet} is used to check whether the parameter
   *   is set,</li> 
   *   <li>The values <code>"yes"</code> and <code>"no"</code> may be used instead of
   *   <code>"true"</code> and <code>"false"</code>, respectively, to describe the
   *   boolean.</li>
   * </ol>
   * 
   * @param parameters the parameters object to look up for the specified parameter
   * @param name the name of the parameter
   * @param defaultValue the defalut value (value to return if the parameter is not set)
   *
   * @throws ParameterException if something goes wrong
   */

  public static boolean getAsBoolean (Parameters parameters,
                                      String name,
                                      boolean defaultValue)
    throws ParameterException
  {
    return
      (checkIfSet(parameters, name)
       ? stringToBoolean(parameters.getParameter(name))
       : defaultValue);
  }

  /**
   * Returns the parameter with the specified name as an integer. If the parameter does not
   * exist, throws an exception.  Note that the method {@link #checkIfSet} is used to check
   * whether the parameter exists.
   *
   * @param parameters the parameters object to look up for the specified parameter
   * @param name the name of the parameter
   *
   * @throws ParameterException if something goes wrong
   */

  public static int getAsInt (Parameters parameters, String name)
    throws ParameterException
  {
    if ( !checkIfSet(parameters, name) )
      throw new ParameterException("Missing paramater: " + name);
    return parameters.getParameterAsInteger(name);
  }

  /**
   * Returns the parameter with the specified name as an integer. If the parameter does not
   * exist, returns the specified default value. Note that the method {@link #checkIfSet}
   * is used to check whether the parameter exists.
   *
   * @param parameters the parameters object to look up for the specified parameter
   * @param name the name of the parameter
   * @param defaultValue the defalut value (value to return if the parameter is not set)
   *
   * @throws ParameterException if something goes wrong
   * 
   */

  public static int getAsInt (Parameters parameters,
                              String name,
                              int defaultValue)
    throws ParameterException
  {
    return
      (checkIfSet(parameters, name)
       ? parameters.getParameterAsInteger(name)
       : defaultValue);
  }

  /**
   * Returns the parameter with the specified name as a 'long' value. If the parameter does
   * not exist, throws an exception.  Note that the method {@link #checkIfSet} is used to
   * check whether the parameter exists.
   *
   * @param parameters the parameters object to look up for the specified parameter
   * @param name the name of the parameter
   *
   * @throws ParameterException if something goes wrong
   */

  public static long getAsLong (Parameters parameters, String name)
    throws ParameterException
  {
    if ( !checkIfSet(parameters, name) )
      throw new ParameterException("Missing paramater: " + name);
    return parameters.getParameterAsLong(name);
  }

  /**
   * Returns the parameter with the specified name as a 'long' value. If the parameter does
   * not exist, returns the specified default value. Note that the method {@link
   * #checkIfSet} is used to check whether the parameter exists.
   *
   * @param parameters the parameters object to look up for the specified parameter
   * @param name the name of the parameter
   * @param defaultValue the defalut value (value to return if the parameter is not set)
   *
   * @throws ParameterException if something goes wrong
   * 
   */

  public static long getAsLong (Parameters parameters,
                              String name,
                              long defaultValue)
    throws ParameterException
  {
    return
      (checkIfSet(parameters, name)
       ? parameters.getParameterAsLong(name)
       : defaultValue);
  }

  /**
   * Returns the parameter with the specified name as a character. If the parameter does not
   * exist, throws an exception.  Note that the method {@link #checkIfSet} is used to check
   * whether the parameter exists.
   *
   * @param parameters the parameters object to look up for the specified parameter
   * @param name the name of the parameter
   *
   * @throws ParameterException if something goes wrong
   */

  public static char getAsChar (Parameters parameters, String name)
    throws ParameterException
  {
    if ( !checkIfSet(parameters, name) )
      throw new ParameterException("Missing paramater: " + name);
    return stringToChar(parameters.getParameter(name));
  }

  /**
   * Returns the parameter with the specified name as a character. If the parameter does not
   * exist, returns the specified default value. Note that the method {@link #checkIfSet}
   * is used to check whether the parameter exists.
   *
   * @param parameters the parameters object to look up for the specified parameter
   * @param name the name of the parameter
   * @param defaultValue the defalut value (value to return if the parameter is not set)
   *
   * @throws ParameterException if something goes wrong
   * 
   */

  public static char getAsChar (Parameters parameters,
                              String name,
                              char defaultValue)
    throws ParameterException
  {
    return
      (checkIfSet(parameters, name)
       ? stringToChar(parameters.getParameter(name))
       : defaultValue);
  }

  /**
   * Returns a parameter as an id. The parameter must exist and be a non-negative
   * integer. Otherwise, an exception is thrown.
   *
   * @param parameters the parameters object to look up for the specified parameter
   * @param name the name of the parameter
   *
   * @throws ParameterException if the parameter can not be found, is not convertable to an
   * integer, is negative, or something else went frong.
   */

  public static int getAsId (Parameters parameters,
                             String name)
    throws ParameterException
  {
    if ( checkIfSet(parameters, name) )
      {
        int id = parameters.getParameterAsInteger(name);
        if ( id < 0 )
          throw new ParameterException
            ("Invalid value of \"" + name + "\" parmater: " + id);
        return id;
      }
    else
      throw new ParameterException
        ("Missing paramater: \"" + name + "\"");
  }

  /**
   * <p>
   *   Returns a document type code from the parameters. Works as follows:
   * </p>
   * <ul>
   *   <li>If the parameter <code>codeParam</code> is set, its value is returned.</li>
   *   <li>If the parameter <code>codeParam</code> is not set, but the parameter
   *   <code>nameParam</code> is set, the value of the latter is expected to be a document
   *   type name. The name is converted to the corresponding code and returned.</li>
   *   <li>If neither the parameter <code>codeParam</code> nor the parameter
   *   <code>nameParam</code> is set, the behaviour is controlled by the
   *   <code>required</code> flag: If <code>required</code> is true, an
   *   exception is thrown, otherwise, <code>defaultValue</code> is returned.</li>
   * </ul>
   * <p>
   *   The value obtained from the parameters must be a valid document type code. The
   *   default value may be any integer, including
   *   {@link DocType#UNDEFINED DocType.UNDEFINED}.
   * </p>
   *
   * @param parameters the parameters object to look up for the document type
   * @param codeParam the parameter specifying the document type as numerical code 
   * @param nameParam the parameter specifying the document type as string name
   * @param defaultValue the default return value; used only if <code>required</code> is
   * false. 
   * @param required whether it is required that the parameter <code>codeParam</code> or the
   * parameter <code>nameParam</code> exists.
   *
   * @throws ParameterException if something goes wrong
   */

  public static int getAsDocType (Parameters parameters,
                                  String codeParam,
                                  String nameParam,
                                  int defaultValue,
                                  boolean required)
    throws ParameterException
  {
    int type = DocType.UNDEFINED;
    if ( checkIfSet(parameters, codeParam) )
      {
        type = parameters.getParameterAsInteger(codeParam);
        if ( !DocType.exists(type) )
          throw new ParameterException
            ("Parameter \"" + codeParam + "\": Unknown document type code: " + type);
      }
    else if ( checkIfSet(parameters, nameParam) )
      {
        String typeName = parameters.getParameter(nameParam);
        type = DocType.codeFor(typeName);
        if ( type == DocType.UNDEFINED )
          throw new ParameterException
            ("Parameter \"" + nameParam + "\": Unknown document type: " + typeName);
      }
    else if ( !required )
      {
        type = defaultValue;
      }
    else
      {
        throw new ParameterException
          ("Found neither parameter \"" + codeParam + "\" nor \"" + nameParam + "\"");
      }
    return type;
  }

  /**
   * <p>
   *   Returns a document type code from the parameters. Works as follows:
   * </p>
   * <ul>
   *   <li>If the parameter <code>codeParam</code> is set, its value is returned.</li>
   *   <li>If the parameter <code>codeParam</code> is not set, but the parameter
   *   <code>nameParam</code> is set, the value of the latter is expected to be a document
   *   type name. The name is converted to the corresponding code and returned.</li>
   *   <li>If neither the parameter <code>codeParam</code> nor the parameter
   *   <code>nameParam</code> is set, <code>defaultValue</code> is returned.</li>
   * </ul>
   * <p>
   *   The value obtained from the parameters must be a valid document type code. The
   *   default value may be any integer, including
   *   {@link DocType#UNDEFINED DocType.UNDEFINED}.
   * </p>
   * <p>
   *   Same as {@link #getAsDocType(Parameters,String,String,int,boolean) getAsDocType(parameters,codeParam,nameParam,defaultValue,false}.
   * </p>
   *
   * @param parameters the parameters object to look up for document type
   * @param codeParam the parameter specifying the document type as numerical code 
   * @param nameParam the parameter specifying the document type as string name
   * @param defaultValue the default return value
   *
   * @throws ParameterException if something goes wrong
   */

  public static int getAsDocType (Parameters parameters,
                                  String codeParam,
                                  String nameParam,
                                  int defaultValue)
    throws ParameterException
  {
    return getAsDocType(parameters, codeParam, nameParam, defaultValue, false);
  }

  /**
   * <p>
   *   Returns a document type code from the parameters. Works as follows:
   * </p>
   * <ul>
   *   <li>If the parameter <code>codeParam</code> is set, its value is returned.</li>
   *   <li>If the parameter <code>codeParam</code> is not set, but the parameter
   *   <code>nameParam</code> is set, the value of the latter is expected to be a document
   *   type name. The name is converted to the corresponding code and returned.</li>
   *   <li>If neither the parameter <code>codeParam</code> nor the parameter
   *   <code>nameParam</code> is set, an exception is thrown.</li>
   * </ul>
   * <p>
   *   Same as {@link #getAsDocType(Parameters,String,String,int,boolean) getAsDocType(parameters,codeParam,nameParam,defaultValue,false}.
   * </p>
   *
   * @param parameters the parameters object to look up for document type
   * @param codeParam the parameter specifying the document type as numerical code 
   * @param nameParam the parameter specifying the document type as string name
   *
   * @throws ParameterException if something goes wrong
   */

  public static int getAsDocType (Parameters parameters,
                                  String codeParam,
                                  String nameParam)
    throws ParameterException
  {
    return getAsDocType(parameters, codeParam, nameParam, -1, true);
  }

  /**
   * <p>
   *   Returns a pseudo-document type code from the parameters. Works as follows:
   * </p>
   * <ul>
   *   <li>If the parameter <code>codeParam</code> is set, its value is returned.</li>
   *   <li>If the parameter <code>codeParam</code> is not set, but the parameter
   *   <code>nameParam</code> is set, the value of the latter is expected to be a document
   *   type name. The name is converted to the corresponding code and returned.</li>
   *   <li>If neither the parameter <code>codeParam</code> nor the parameter
   *   <code>nameParam</code> is set, the behaviour is controlled by the
   *   <code>required</code> flag: If <code>required</code> is true, an
   *   exception is thrown, otherwise, <code>defaultValue</code> is returned.</li>
   * </ul>
   * <p>
   *   The value obtained from the parameters must be a valid document type code. The
   *   default value may be any integer, including
   *   {@link DocType#UNDEFINED DocType.UNDEFINED}.
   * </p>
   *
   * @param parameters the parameters object to look up for the pseudo-document type
   * @param codeParam the parameter specifying the pseudo-document type as numerical code 
   * @param nameParam the parameter specifying the pseudo-document type as string name
   * @param defaultValue the default return value; used only if <code>required</code> is
   * false. 
   * @param required whether it is required that the parameter <code>codeParam</code> or the
   * parameter <code>nameParam</code> exists.
   *
   * @throws ParameterException if something goes wrong
   */

  public static int getAsPseudoDocType (Parameters parameters,
                                        String codeParam,
                                        String nameParam,
                                        int defaultValue,
                                        boolean required)
    throws ParameterException
  {
    int type = PseudoDocType.UNDEFINED;
    if ( checkIfSet(parameters, codeParam) )
      {
        type = parameters.getParameterAsInteger(codeParam);
        if ( !PseudoDocType.exists(type) )
          throw new ParameterException
            ("Parameter \"" + codeParam + "\": Unknown pseudo-document type code: " + type);
      }
    else if ( checkIfSet(parameters, nameParam) )
      {
        String typeName = parameters.getParameter(nameParam);
        type = PseudoDocType.codeFor(typeName);
        if ( type == PseudoDocType.UNDEFINED )
          throw new ParameterException
            ("Parameter \"" + nameParam + "\": Unknown pseudo-document type: " + typeName);
      }
    else if ( !required )
      {
        type = defaultValue;
      }
    else
      {
        throw new ParameterException
          ("Found neither parameter \"" + codeParam + "\" nor \"" + nameParam + "\"");
      }
    return type;
  }

  /**
   * <p>
   *   Returns a pseudo-document type code from the parameters. Works as follows:
   * </p>
   * <ul>
   *   <li>If the parameter <code>codeParam</code> is set, its value is returned.</li>
   *   <li>If the parameter <code>codeParam</code> is not set, but the parameter
   *   <code>nameParam</code> is set, the value of the latter is expected to be a pseudo-document
   *   type name. The name is converted to the corresponding code and returned.</li>
   *   <li>If neither the parameter <code>codeParam</code> nor the parameter
   *   <code>nameParam</code> is set, <code>defaultValue</code> is returned.</li>
   * </ul>
   * <p>
   *   The value obtained from the parameters must be a valid pseudo-document type code. The
   *   default value may be any integer, including
   *   {@link DocType#UNDEFINED DocType.UNDEFINED}.
   * </p>
   * <p>
   *   Same as {@link #getAsDocType(Parameters,String,String,int,boolean) getAsDocType(parameters,codeParam,nameParam,defaultValue,false}.
   * </p>
   *
   * @param parameters the parameters object to look up for pseudo-document type
   * @param codeParam the parameter specifying the pseudo-document type as numerical code 
   * @param nameParam the parameter specifying the pseudo-document type as string name
   * @param defaultValue the default return value
   *
   * @throws ParameterException if something goes wrong
   */

  public static int getAsPseudoDocType (Parameters parameters,
                                        String codeParam,
                                        String nameParam,
                                        int defaultValue)
    throws ParameterException
  {
    return getAsPseudoDocType(parameters, codeParam, nameParam, defaultValue, false);
  }

  /**
   * <p>
   *   Returns a pseudo-document type code from the parameters. Works as follows:
   * </p>
   * <ul>
   *   <li>If the parameter <code>codeParam</code> is set, its value is returned.</li>
   *   <li>If the parameter <code>codeParam</code> is not set, but the parameter
   *   <code>nameParam</code> is set, the value of the latter is expected to be a pseudo-document
   *   type name. The name is converted to the corresponding code and returned.</li>
   *   <li>If neither the parameter <code>codeParam</code> nor the parameter
   *   <code>nameParam</code> is set, an exception is thrown.</li>
   * </ul>
   * <p>
   *   Same as {@link #getAsDocType(Parameters,String,String,int,boolean) getAsDocType(parameters,codeParam,nameParam,defaultValue,false}.
   * </p>
   *
   * @param parameters the parameters object to look up for pseudo-document type
   * @param codeParam the parameter specifying the pseudo-document type as numerical code 
   * @param nameParam the parameter specifying the pseudo-document type as string name
   *
   * @throws ParameterException if something goes wrong
   */

  public static int getAsPseudoDocType (Parameters parameters,
                                        String codeParam,
                                        String nameParam)
    throws ParameterException
  {
    return getAsPseudoDocType(parameters, codeParam, nameParam, -1, true);
  }

  /**
   * <p>
   *   Returns a use mode code from the parameters. Works as follows:
   * </p>
   * <ul>
   *   <li>If the parameter <code>codeParam</code> is set, its value is returned.</li>
   *   <li>If the parameter <code>codeParam</code> is not set, but the parameter
   *   <code>nameParam</code> is set, the value of the latter is expected to be a document
   *   type name. The name is converted to the corresponding code and returned.</li>
   *   <li>If neither the parameter <code>codeParam</code> nor the parameter
   *   <code>nameParam</code> is set, the behaviour is controlled by the
   *   <code>required</code> flag: If <code>required</code> is true, an
   *   exception is thrown, otherwise, <code>defaultValue</code> is returned.</li>
   * </ul>
   *
   * @param parameters the parameters object to look up for the use mode
   * @param codeParam the parameter specifying the use mode as numerical code 
   * @param nameParam the parameter specifying the use mode as string name
   * @param defaultValue the default return value; used only if <code>required</code> is
   * false. 
   * @param required whether it is required that the parameter <code>codeParam</code> or the
   * parameter <code>nameParam</code> exists.
   *
   * @throws ParameterException if something goes wrong
   */

  public static int getAsUseMode (Parameters parameters,
                                  String codeParam,
                                  String nameParam,
                                  int defaultValue,
                                  boolean required)
    throws ParameterException
  {
    int useMode = UseMode.UNDEFINED;
    if ( checkIfSet(parameters, codeParam) )
      {
        useMode = parameters.getParameterAsInteger(codeParam);
        if ( !UseMode.exists(useMode) )
          throw new ParameterException
            ("Parameter \"" + codeParam + "\": Unknown use mode code: " + useMode);
      }
    else if ( checkIfSet(parameters, nameParam) )
      {
        String useModeName = parameters.getParameter(nameParam);
        useMode = UseMode.codeFor(useModeName);
        if ( useMode == UseMode.UNDEFINED )
          throw new ParameterException
            ("Parameter \"" + nameParam + "\": Unknown use mode: " + useModeName);
      }
    else if ( !required )
      {
        useMode = defaultValue;
      }
    else
      {
        throw new ParameterException
          ("Found neither parameter \"" + codeParam + "\" nor \"" + nameParam + "\"");
      }
    return useMode;
  }

  /**
   * <p>
   *   Returns a use mode code from the parameters. Works as follows:
   * </p>
   * <ul>
   *   <li>If the parameter <code>codeParam</code> is set, its value is returned.</li>
   *   <li>If the parameter <code>codeParam</code> is not set, but the parameter
   *   <code>nameParam</code> is set, the value of the latter is expected to be a document
   *   type name. The name is converted to the corresponding code and returned.</li>
   *   <li>If neither the parameter <code>codeParam</code> nor the parameter
   *   <code>nameParam</code> is set, <code>defaultValue</code> is returned.</li>
   * </ul>
   *
   * @param parameters the parameters object to look up for the use mode
   * @param codeParam the parameter specifying the use mode as numerical code 
   * @param nameParam the parameter specifying the use mode as string name
   * @param defaultValue the default return value; used only if <code>required</code> is
   * false. 
   *
   * @throws ParameterException if something goes wrong
   */

  public static int getAsUseMode (Parameters parameters,
                                  String codeParam,
                                  String nameParam,
                                  int defaultValue)
    throws ParameterException
  {
    return getAsUseMode(parameters, codeParam, nameParam, defaultValue, false);
  }

  /**
   * <p>
   *   Returns a use mode code from the parameters. Works as follows:
   * </p>
   * <ul>
   *   <li>If the parameter <code>codeParam</code> is set, its value is returned.</li>
   *   <li>If the parameter <code>codeParam</code> is not set, but the parameter
   *   <code>nameParam</code> is set, the value of the latter is expected to be a document
   *   type name. The name is converted to the corresponding code and returned.</li>
   *   <li>If neither the parameter <code>codeParam</code> nor the parameter
   *   <code>nameParam</code> is set, an exception is thrown.</li>
   * </ul>
   *
   * @param parameters the parameters object to look up for the use mode
   * @param codeParam the parameter specifying the use mode as numerical code 
   * @param nameParam the parameter specifying the use mode as string name
   *
   * @throws ParameterException if something goes wrong
   */

  public static int getAsUseMode (Parameters parameters,
                                  String codeParam,
                                  String nameParam)
    throws ParameterException
  {
    return getAsUseMode(parameters, codeParam, nameParam, -1, false);
  }

  /**
   * <p>
   *   Returns a category code from the parameters. Works as follows:
   * </p>
   * <ul>
   *   <li>If the parameter <code>codeParam</code> is set, its value is returned.</li>
   *   <li>If the parameter <code>codeParam</code> is not set, but the parameter
   *   <code>nameParam</code> is set, the value of the latter is expected to be a document
   *   type name. The name is converted to the corresponding code and returned.</li>
   *   <li>If neither the parameter <code>codeParam</code> nor the parameter
   *   <code>nameParam</code> is set, the behaviour is controlled by the
   *   <code>required</code> flag: If <code>required</code> is true, an
   *   exception is thrown, otherwise, <code>defaultValue</code> is returned.</li>
   * </ul>
   *
   * @param parameters the parameters object to look up for the category
   * @param codeParam the parameter specifying the category as numerical code 
   * @param nameParam the parameter specifying the category as string name
   * @param defaultValue the default return value; used only if <code>required</code> is
   * false. 
   * @param required whether it is required that the parameter <code>codeParam</code> or the
   * parameter <code>nameParam</code> exists.
   *
   * @throws ParameterException if something goes wrong
   */

  public static int getAsCategory (Parameters parameters,
                                  String codeParam,
                                  String nameParam,
                                  int defaultValue,
                                  boolean required)
    throws ParameterException
  {
    int category = Category.UNDEFINED;
    if ( checkIfSet(parameters, codeParam) )
      {
        category = parameters.getParameterAsInteger(codeParam);
        if ( !Category.exists(category) )
          throw new ParameterException
            ("Parameter \"" + codeParam + "\": Unknown category code: " + category);
      }
    else if ( checkIfSet(parameters, nameParam) )
      {
        String categoryName = parameters.getParameter(nameParam);
        category = Category.codeFor(categoryName);
        if ( category == Category.UNDEFINED )
          throw new ParameterException
            ("Parameter \"" + nameParam + "\": Unknown category: " + categoryName);
      }
    else if ( !required )
      {
        category = defaultValue;
      }
    else
      {
        throw new ParameterException
          ("Found neither parameter \"" + codeParam + "\" nor \"" + nameParam + "\"");
      }
    return category;
  }

  /**
   * <p>
   *   Returns a category code from the parameters. Works as follows:
   * </p>
   * <ul>
   *   <li>If the parameter <code>codeParam</code> is set, its value is returned.</li>
   *   <li>If the parameter <code>codeParam</code> is not set, but the parameter
   *   <code>nameParam</code> is set, the value of the latter is expected to be a document
   *   type name. The name is converted to the corresponding code and returned.</li>
   *   <li>If neither the parameter <code>codeParam</code> nor the parameter
   *   <code>nameParam</code> is set, <code>defaultValue</code> is returned.</li>
   * </ul>
   *
   * @param parameters the parameters object to look up for the category
   * @param codeParam the parameter specifying the category as numerical code 
   * @param nameParam the parameter specifying the category as string name
   * @param defaultValue the default return value; used only if <code>required</code> is
   * false. 
   *
   * @throws ParameterException if something goes wrong
   */

  public static int getAsCategory (Parameters parameters,
                                  String codeParam,
                                  String nameParam,
                                  int defaultValue)
    throws ParameterException
  {
    return getAsCategory(parameters, codeParam, nameParam, defaultValue, false);
  }

  /**
   * <p>
   *   Returns a category code from the parameters. Works as follows:
   * </p>
   * <ul>
   *   <li>If the parameter <code>codeParam</code> is set, its value is returned.</li>
   *   <li>If the parameter <code>codeParam</code> is not set, but the parameter
   *   <code>nameParam</code> is set, the value of the latter is expected to be a document
   *   type name. The name is converted to the corresponding code and returned.</li>
   *   <li>If neither the parameter <code>codeParam</code> nor the parameter
   *   <code>nameParam</code> is set, an exception is thrown.</li>
   * </ul>
   *
   * @param parameters the parameters object to look up for the category
   * @param codeParam the parameter specifying the category as numerical code 
   * @param nameParam the parameter specifying the category as string name
   *
   * @throws ParameterException if something goes wrong
   */

  public static int getAsCategory (Parameters parameters,
                                  String codeParam,
                                  String nameParam)
    throws ParameterException
  {
    return getAsCategory(parameters, codeParam, nameParam, -1, false);
  }

  /**
   * Assumes that the specified parameter is a list of tokens separated by whitespaces or
   * commas and returns the first token as a string. If the parameter does not
   * exist or contains no tokens, throws an exception.
   *
   * @param parameters the parameters object to look up for the specified parameter
   * @param name the name of the parameter
   *
   * @throws ParameterException if something goes wrong
   */

  public static String getFirstTokenAsString (Parameters parameters, String name)
    throws ParameterException
  {
    if ( !checkIfSet(parameters, name) )
      throw new ParameterException("Missing paramater: " + name);
    String firstToken = getFirstToken(parameters.getParameter(name));
    if ( firstToken == null )
      throw new ParameterException("Void paramater: " + name);
    return firstToken;
  }

  /**
   * Assumes that the specified parameter is a list of tokens separated by whitespaces or
   * commas and returns the first token as a string. If the parameter does not
   * exist or contains no tokens, returns the specified default value.
   *
   * @param parameters the parameters object to look up for the specified parameter
   * @param name the name of the parameter
   * @param defaultValue the defalut value (value to return if the parameter is not set or
   * contains no tokens)
   *
   * @throws ParameterException if something goes wrong
   */

  public static String getFirstTokenAsString (Parameters parameters,
                                              String name,
                                              String defaultValue)
    throws ParameterException
  {
    if ( !checkIfSet(parameters, name) )
      return defaultValue;
    String firstToken = getFirstToken(parameters.getParameter(name));
    return (firstToken != null ? firstToken : defaultValue);
  }

  /**
   * Assumes that the specified parameter is a list of tokens separated by whitespaces or
   * commas and returns the first token as a integer. If the parameter does not
   * exist or contains no tokens, throws an exception.
   *
   * @param parameters the parameters object to look up for the specified parameter
   * @param name the name of the parameter
   *
   * @throws ParameterException if something goes wrong
   */

  public static int getFirstTokenAsInt (Parameters parameters, String name)
    throws ParameterException
  {
    if ( !checkIfSet(parameters, name) )
      throw new ParameterException("Missing paramater: " + name);
    String firstToken = getFirstToken(parameters.getParameter(name));
    if ( firstToken == null )
      throw new ParameterException("Void paramater: " + name);
    try
      {
        return Integer.parseInt(firstToken);
      }
    catch (NumberFormatException exception)
      {
        throw new ParameterException
          ("Can not convert first token of \"" + name + "\" to an int: " + firstToken,
           exception);
      }
  }

  /**
   * Assumes that the specified parameter is a list of tokens separated by whitespaces or
   * commas and returns the first token as an integer. If the parameter does not
   * exist or contains no tokens, returns the specified default value.
   *
   * @param parameters the parameters object to look up for the specified parameter
   * @param name the name of the parameter
   * @param defaultValue the defalut value (value to return if the parameter is not set or
   * contains no tokens)
   *
   * @throws ParameterException if something goes wrong
   */

  public static int getFirstTokenAsInt (Parameters parameters,
                                        String name,
                                        int defaultValue)
    throws ParameterException
  {
    if ( !checkIfSet(parameters, name) )
      return defaultValue;
    String firstToken = getFirstToken(parameters.getParameter(name));
    if ( firstToken == null )
      return defaultValue;
    try
      {
        return Integer.parseInt(firstToken);
      }
    catch (NumberFormatException exception)
      {
        throw new ParameterException
          ("Can not convert first token of \"" + name + "\" to an int: " + firstToken,
           exception);
      }
  }

  /**
   * <p>
   *   Returns a parameter with the specified name as an array of strings. The characters
   *   <code>','</code>, <code>' '</code>, <code>'\t'</code>, <code>'\n'</code>,
   *   <code>'\r'</code>, or <code>'\f'</code> separate the strings.
   * </p>
   * <p>
   *   If the parameter does not exist or is void, the following rule applies: if
   *   <code>required</code> is false, <code>defaultValues</code> is retruned;
   *   otherwise, an exception is thrown. <code>defaultValues</code> may be null.
   * </p>
   * 
   * @param parameters the parameters object to look up for the values.
   * @param name the name of the parameter
   * @param defaultValues the default return value; used only if <code>required</code> is
   *  false. 
   * @param required whether it is required that the parameter exists.
   *
   * @throws ParameterException if something goes wrong
   */

  public static String[] getAsStringArray (Parameters parameters,
                                           String name,
                                           String[] defaultValues,
                                           boolean required)
    throws ParameterException
  {
    if ( checkIfSet(parameters, name) )
      return tokenizeString(parameters.getParameter(name));
    else if ( !required )
      return defaultValues;
    else
      throw new ParameterException("Missing parameter \"" + name);
  }

  /**
   * <p>
   *   Returns a parameter with the specified name as an array of strings. The characters
   *   <code>','</code>, <code>' '</code>, <code>'\t'</code>, <code>'\n'</code>,
   *   <code>'\r'</code>, or <code>'\f'</code> separate the strings.
   * </p>
   * <p>
   *   If the parameter does not exist or is void, <code>defaultValue</code> is retruned.
   *   <code>defaultValues</code> may be null.
   * </p>
   * 
   * @param parameters the parameters object to look up for the values.
   * @param name the name of the parameter
   * @param defaultValues the default return value
   *
   * @throws ParameterException if something goes wrong
   */

  public static String[] getAsStringArray (Parameters parameters,
                                           String name,
                                           String[] defaultValues)
    throws ParameterException
  {
    return getAsStringArray(parameters, name, defaultValues, false);
  }

  /**
   * <p>
   *   Returns a parameter with the specified name as an array of strings. The characters
   *   <code>','</code>, <code>' '</code>, <code>'\t'</code>, <code>'\n'</code>,
   *   <code>'\r'</code>, or <code>'\f'</code> separate the strings.
   * </p>
   * <p>
   *   If the parameter does not exist or is void, an exception is thrown.
   * </p>
   * 
   * @param parameters the parameters object to look up for the values.
   * @param name the name of the parameter
   *
   * @throws ParameterException if something goes wrong
   */

  public static String[] getAsStringArray (Parameters parameters,
                                           String name)
    throws ParameterException
  {
    return getAsStringArray(parameters, name, null, true);
  }

  /**
   * <p>
   *   Returns a parameter with the specified name as an array of integers. The characters
   *   <code>','</code>, <code>' '</code>, <code>'\t'</code>, <code>'\n'</code>,
   *   <code>'\r'</code>, or <code>'\f'</code> separate the strings.
   * </p>
   * <p>
   *   If the parameter does not exist or is void, the following rule applies: if
   *   <code>required</code> is false, <code>defaultValues</code> is retruned;
   *   otherwise, an exception is thrown. <code>defaultValues</code> may be null.
   * </p>
   * 
   * @param parameters the parameters object to look up for the values.
   * @param name the name of the parameter
   * @param defaultValues the default return value; used only if <code>required</code> is
   * false. 
   * @param required whether it is required that the parameter exists.
   *
   * @throws ParameterException if something goes wrong
   */

  public static int[] getAsIntArray (Parameters parameters,
                                     String name,
                                     int[] defaultValues,
                                     boolean required)
    throws ParameterException
  {
    if ( checkIfSet(parameters, name) )
      {
        String[] tokens = tokenizeString(parameters.getParameter(name));
        int[] values = new int[tokens.length];
        for (int i = 0; i < values.length; i++)
          {
            try
              {
                values[i] = Integer.parseInt(tokens[i]);
              }
            catch (NumberFormatException exception)
              {
                throw new ParameterException
                  ("Can not convert token " + i + " of \"" + name + "\" to an int: " +
                   tokens[i], exception);
              }
          }
        return values;
      }
    else if ( !required )
      return defaultValues;
    else
      throw new ParameterException("Missing parameter \"" + name);
  }

  /**
   * <p>
   *   Returns a parameter with the specified name as an array of integers. The characters
   *   <code>','</code>, <code>' '</code>, <code>'\t'</code>, <code>'\n'</code>,
   *   <code>'\r'</code>, or <code>'\f'</code> separate the integers.
   * </p>
   * <p>
   *   If the parameter does not exist or is void, <code>defaultValue</code> is retruned.
   * </p>
   * 
   * @param parameters the parameters object to look up for the values.
   * @param name the name of the parameter
   * @param defaultValues the default return value
   *
   * @throws ParameterException if something goes wrong
   */

  public static int[] getAsIntArray (Parameters parameters,
                                     String name,
                                     int[] defaultValues)
    throws ParameterException
  {
    return getAsIntArray(parameters, name, defaultValues, false);
  }

  /**
   * <p>
   *   Returns a parameter with the specified name as an array of integers. The characters
   *   <code>','</code>, <code>' '</code>, <code>'\t'</code>, <code>'\n'</code>,
   *   <code>'\r'</code>, or <code>'\f'</code> separate the integers.
   * </p>
   * <p>
   *   If the parameter does not exist or is void, an exception is thrown.
   * </p>
   * 
   * @param parameters the parameters object to look up for the values.
   * @param name the name of the parameter
   *
   * @throws ParameterException if something goes wrong
   */

  public static int[] getAsIntArray (Parameters parameters,
                                     String name)
    throws ParameterException
  {
    return getAsIntArray(parameters, name, null, true);
  }

  /**
   * Returns the parameter with the specified name as a timeframe relation.
   */

  public static String getAsTimeframeRelation (Parameters parameters,
                                               String name)
    throws ParameterException
  {
    if ( !checkIfSet(parameters, name) )
      throw new ParameterException("Missing parameter: " + name);
    String timeframeRelation = parameters.getParameter(name);
    if ( ! ( timeframeRelation.equals(BEFORE) ||
             timeframeRelation.equals(INSIDE) ||
             timeframeRelation.equals(AFTER) ) )
      throw new ParameterException
        ("Illegal timeframe relation keyword: " + timeframeRelation);
    return timeframeRelation;
  }

  /**
   * Returns the parameter with the specified name in the specified parameters object as a
   * date format. If the paramter does not exist, the date format is creared according to
   * the specified default pattern.
   */

  public static DateFormat getAsDateFormat (Parameters parameters,
                                            String name,
                                            String defaultPattern)
    throws ParameterException
  {
    try
      {
        return new SimpleDateFormat(getAsString(parameters, name, defaultPattern));
      }
    catch (IllegalArgumentException exception)
      {
        throw new ParameterException("Failed to create date format: " + exception);
      }
  }

  // --------------------------------------------------------------------------------
  // Import from requet parameters
  // --------------------------------------------------------------------------------

  /**
   * Adds the request parameters from the specified {@link Request Request} object to the
   * specified {@link Parameters Parameters} object. If a parameter to add already exists in
   * the {@link Parameters Parameters} object, the <code>overwrite</code> flag decides what
   * to do: if true, the parameter will be overwritten, if false,
   * it will be preserved.
   */

  public static void addRequestParams (Parameters parameters,
                                       Request request,
                                       boolean overwrite)
    throws ParameterException
  {
    Enumeration requestParamNames = request.getParameterNames();
    while ( requestParamNames.hasMoreElements() )
      {
        String name = (String)requestParamNames.nextElement();
        if ( overwrite || !checkIfSet(parameters, name) )
          {
            String value = request.getParameter(name);
            parameters.setParameter(name, value);
          }
      }
  }

  /**
   * Adds the request parameters from the specified {@link Request Request} object to the
   * specified {@link Parameters Parameters} object. Already existing parameters will be
   * overwritten.
   */

  public static void addRequestParams (Parameters parameters,
                                       Request request)
    throws ParameterException
  {
    addRequestParams(parameters, request, true);
  }

  // --------------------------------------------------------------------------------
  // Utilities (public)
  // --------------------------------------------------------------------------------

  /**
   * Converts a string into a boolean. If the string is <code>"true"</code> or
   * <code>"yes"</code>, true is returned. If the string is
   * <code>"false"</code> or <code>"no"</code>, false is returned.  If the
   * string is any other value, an exception is thrown. Case does not matter.
   *
   * @param string the string to convert
   * @throws ParameterException if the string has any other value then <code>"true"</code>,
   * <code>"yes"</code>, <code>"false"</code>, or <code>"no"</code>.
   */

  public static boolean stringToBoolean (String string)
    throws ParameterException
  {
    if ( string.equalsIgnoreCase("true") ||
         string.equalsIgnoreCase("yes") )
      return true;
    else if ( string.equalsIgnoreCase("false") ||
              string.equalsIgnoreCase("no") )
      return false;
    else
      throw new ParameterException
        ("stringToBoolean: Cannot convert to boolean: " + string);
  }

  // --------------------------------------------------------------------------------
  // Auxiliaries (internal)
  // --------------------------------------------------------------------------------

  /**
   * Converts a string to a character. The string must contain exactly one character.
   * This character is returned. Otherwise, i.e., if the string has less or more
   * characters, an exception is thrown.
   */

  protected static char stringToChar (String string)
    throws ParameterException
  {
    if ( string.length() == 1 )
      return string.charAt(0);
    else
      throw new ParameterException
        ("stringToChar: String must contain exactly one character: " + string);
  }

  /**
   * Returns a list of tokens from a string. The characters <code>','</code>, <code>'
   * '</code>, <code>'\t'</code>, <code>'\n'</code>, <code>'\r'</code>, or <code>'\f'</code>
   * separate the tokens.
   */

  protected static String[] tokenizeString (String string)
  {
    List list = new ArrayList();
    StringTokenizer tokenizer = new StringTokenizer(string, ", \t\n\r\f");
    while ( tokenizer.hasMoreTokens() )
      list.add(tokenizer.nextToken());
    return (String[])list.toArray(new String[list.size()]);
  }

  /**
   * Assumes that the specified string is a list of tokens separated by whitespaces or
   * commas and returns the first token. If no token exists, returns <code>null</code>.
   */

  protected static String getFirstToken (String string)
  {
    StringTokenizer tokenizer = new StringTokenizer(string, " \t\n\r\f,");
    return (tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null);
  }

}
