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

package net.mumie.japs.log.analyse.filter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.mumie.japs.log.analyse.LogOutputProcessingException;

/**
 * <p>
 *   Eleminates certain fields from the log records. Which fields are elminated and which
 *   are preserved is controlled by the parameters. Each parameter corresponds to a
 *   field. The parameter value is a boolean. True means the field is preserved, false means
 *   the field is eleminated. The boolean value true may be specified by the strings "true"
 *   or "yes", the value false by the strings "false" or "no". The following parameters
 *   exist:
 * </p>
 * <table class="genuine">
 *   <tr>
 *     <th>Name</th><th>Corresponding field</th>
 *   </tr>
 *   <tr>
 *     <td>
 *       <code>source-name</code>
 *     </td>
 *     <td>
 *       The log file which contains the record
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>
 *       <code>line-number</code>
 *     </td>
 *     <td>
 *       Line number in the log file
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>
 *       <code>priority</code>
 *     </td>
 *     <td>
 *       Priority
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>
 *       <code>category</code>
 *     </td>
 *     <td>
 *       Category
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>
 *       <code>uri</code>
 *     </td>
 *     <td>
 *       URI
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>
 *       <code>thread</code>
 *     </td>
 *     <td>
 *       Thread
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>
 *       <code>class</code>
 *     </td>
 *     <td>
 *       Name of the Java class that issued the log record
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>
 *       <code>message</code>
 *     </td>
 *     <td>
 *       The log message
 *     </td>
 *   </tr>
 * </table>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: FieldLogOutputFilter.java,v 1.3 2007/06/10 00:43:40 rassy Exp $</code>
 */

public class FieldLogOutputFilter extends DefaultLogOutputFilter
{
  // --------------------------------------------------------------------------------
  // Global variables and constants.
  // --------------------------------------------------------------------------------

  /**
   * Possible value of a flag setting (<code>withXxxSetting</code> variable); meaning that
   * the flag should be set to <code>false</code>.
   */

  protected static final int EXCLUDE = 0;

  /**
   * Possible value of a flag setting (<code>withXxxSetting</code> variable); meaning that
   * the flag should be set to <code>true</code>.
   */

  protected static final int INCLUDE = 1;

  /**
   * Possible value of a flag setting (<code>withXxxSetting</code> variable); meaning that
   * the flag should be inherited from the previous component.
   */

  protected static final int INHERIT = 2;

  /**
   * Setting of the source name flag.
   */

  protected int withSourceNameSetting = INHERIT;

  /**
   * Setting of the line number flag.
   */

  protected int withLineNumberSetting = INHERIT;

  /**
   * Setting of the priority flag.
   */

  protected int withPrioritySetting = INHERIT;

  /**
   * Setting of the time flag.
   */

  protected int withTimeSetting = INHERIT;

  /**
   * Setting of the category flag.
   */

  protected int withCategorySetting = INHERIT;

  /**
   * Setting of the uri flag.
   */

  protected int withUriSetting = INHERIT;

  /**
   * Setting of the class name flag.
   */

  protected int withClassNameSetting = INHERIT;

  /**
   * Setting of the thread flag.
   */

  protected int withThreadSetting = INHERIT;

  /**
   * Setting of the message flag.
   */

  protected int withMessageSetting = INHERIT;

  /**
   * Sets a parameter.
   */

  public void setParameter (String name, String value)
    throws LogOutputProcessingException
  {
    if ( name.equals("source-name") )
      this.withSourceNameSetting = stringToSetting(value);
    else if ( name.equals("line-number") )
      this.withLineNumberSetting = stringToSetting(value);
    else if ( name.equals("priority") )
      this.withPrioritySetting = stringToSetting(value);
    else if ( name.equals("time") )
      this.withTimeSetting = stringToSetting(value);
    else if ( name.equals("category") )
      this.withCategorySetting = stringToSetting(value);
    else if ( name.equals("uri") )
      this.withUriSetting = stringToSetting(value);
    else if ( name.equals("thread") )
      this.withThreadSetting = stringToSetting(value);
    else if ( name.equals("class") )
      this.withClassNameSetting = stringToSetting(value);
    else if ( name.equals("message") )
      this.withMessageSetting = stringToSetting(value);
    else
      throw new LogOutputProcessingException("Unknown parameter: " + name);
  }

  /**
   * Calls the <code>handleStart</code> method of the super-class and of the handler that
   * recieves the filtered ouput, both with the arguments passed to this method.
   */

  public void handleStart (boolean withSourceName,
                           boolean withLineNumber,
                           boolean withPriority,
                           boolean withTime,
                           boolean withCategory,
                           boolean withUri,
                           boolean withThread,
                           boolean withClassName,
                           boolean withMessage)
    throws LogOutputProcessingException 
  {
    this.withSourceName = settingToBoolean(this.withSourceNameSetting, withSourceName);
    this.withLineNumber = settingToBoolean(this.withLineNumberSetting, withLineNumber);
    this.withPriority = settingToBoolean(this.withPrioritySetting, withPriority);
    this.withTime = settingToBoolean(this.withTimeSetting, withTime);
    this.withCategory = settingToBoolean(this.withCategorySetting, withCategory);
    this.withUri = settingToBoolean(this.withUriSetting, withUri);
    this.withClassName = settingToBoolean(this.withClassNameSetting, withClassName);
    this.withThread = settingToBoolean(this.withThreadSetting, withThread);
    this.withMessage = settingToBoolean(this.withMessageSetting, withMessage);

    this.handler.handleStart
      (this.withSourceName,
       this.withLineNumber,
       this.withPriority,
       this.withTime,
       this.withCategory,
       this.withUri,
       this.withThread,
       this.withClassName,
       this.withMessage);
  }

  /**
   * 
   */

  protected static final int stringToSetting (String value)
    throws LogOutputProcessingException
  {
    return (stringToBoolean(value) ? INCLUDE : EXCLUDE);
  }

  /**
   * 
   */

  protected static final boolean settingToBoolean (int setting, boolean parentValue)
  {
    switch ( setting )
      {
      case EXCLUDE: return false;
      case INCLUDE: return true;
      case INHERIT: return parentValue;
      default:
        throw new IllegalStateException("Unknown setting: " + setting);
      }
  }

}
