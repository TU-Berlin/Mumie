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

package net.mumie.japs.log.analyse.parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.mumie.japs.log.analyse.AbstractLogAnalyserComponent;
import net.mumie.japs.log.analyse.LogOutputProcessingException;
import net.mumie.japs.log.analyse.source.LogOutputSource;
import net.mumie.japs.log.analyse.util.RegexPatternBuilder;

/**
 * <p>
 *   Default implementation of {@link LogOutputParser LogOutputParser}. Parses the log
 *   output lines by applying a certain regular expression to them. If a line matches, it is
 *   regarded as a formatted log entry, and the fields are extracted from it (cf.
 *   {@link LogOutputParser LogOutputParser}). If a line does not match, it is regarded as a
 *   continuation of the message fields of the last formatted log entry.
 * </p>
 * <p>
 *   By default, the regular expression is suitable for standard Cocoon log messages. If log
 *   messages of a different format are to be parsed, the regular expression and its flags
 *   (see below) can be changed by paramaters. However, the regular expression always must
 *   have seven capturing groups (i.e., <code>(...)</code> contructs, cf.
 *   {@link Pattern Pattern}); and the groups must correspond to the following fields (cf.
 *   {@link LogOutputParser LogOutputParser}): 
 * </p>
 * <table class="genuine">
 *   <tr>
 *     <th>Group</th><th>Field</th>
 *   </tr>
 *   <tr>
 *     <td class="center">1</td><td>Priority</td>
 *   </tr>
 *   <tr>
 *     <td class="center">2</td><td>Time</td>
 *   </tr>
 *   <tr>
 *     <td class="center">3</td><td>Category</td>
 *   </tr>
 *   <tr>
 *     <td class="center">4</td><td>URI</td>
 *   </tr>
 *   <tr>
 *     <td class="center">5</td><td>Thread</td>
 *   </tr>
 *   <tr>
 *     <td class="center">6</td><td>Class name</td>
 *   </tr>
 *   <tr>
 *     <td class="center">7</td><td>Message</td>
 *   </tr>
 * </table>
 * <p>
 *   The <code>DefaultLogOutputParser</code> recognizes the following parameters:
 * </p>
 * <table class="genuine">
 *   <tr>
 *     <th>Name</th><th>Corresponding field</th>
 *   </tr>
 *   <tr>
 *     <td>
 *       <code>regex</code>
 *     </td>
 *     <td>
 *       The regular expression
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>
 *       <code>canon-eq</code>
 *     </td>
 *     <td>
 *       Whether the {@link Pattern#CANON_EQ CANON_EQ} flag is turned on.
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>
 *       <code>case-insensitive</code>
 *     </td>
 *     <td>
 *       Whether the {@link Pattern#CASE_INSENSITIVE CASE_INSENSITIVE} flag is turned on.
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>
 *       <code>comments</code>
 *     </td>
 *     <td>
 *       Whether the {@link Pattern#COMMENTS COMMENTS} flag is turned on.
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>
 *       <code>dotall</code>
 *     </td>
 *     <td>
 *       Whether the {@link Pattern#DOTALL DOTALL} flag is turned on.
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>
 *       <code>multiline</code>
 *     </td>
 *     <td>
 *       Whether the {@link Pattern#MULTILINE MULTILINE} flag is turned on.
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>
 *       <code>unicode-case</code>
 *     </td>
 *     <td>
 *       Whether the {@link Pattern#UNICODE_CASE UNICODE_CASE} flag is turned on.
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>
 *       <code>unix-lines</code>
 *     </td>
 *     <td>
 *       Whether the {@link Pattern#UNIX_LINES UNIX_LINES} flag is turned on.
 *     </td>
 *   </tr>
 * </table>
 * <p>
 *   The default regular expression is:
 *   <pre>  ^(\S+)\s+(\S+&nbsp;\S+)\s+\[(\S+)\]\s+\((\S+)\)\s+([^/]+)/(\S+):\s+(.*)</pre>
 *   By default, the flags above are not turned on (more precicely, they are not turned on
 *   or off explicitely; rather, the default settings made by {@link Pattern Pattern} are
 *   used).
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: DefaultLogOutputParser.java,v 1.5 2007/06/10 19:15:07 rassy Exp $</code>
 */

public class DefaultLogOutputParser extends AbstractLogOutputParser
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * Utility for building the pattern.
   */

  protected RegexPatternBuilder patternBuilder = new RegexPatternBuilder
    ("^(\\S+)\\s+(\\S+ \\S+)\\s+\\[(\\S+)\\]\\s+\\((\\S+)\\)\\s+([^/]+)/(\\S+):\\s+(.*)");

  /**
   * Capturing group that contains the priority.
   */

  protected int priorityGroup = 1;

  /**
   * Capturing group that contains the time.
   */

  protected int timeGroup = 2;

  /**
   * Capturing group that contains the category.
   */

  protected int categoryGroup = 3;

  /**
   * Capturing group that contains the uri.
   */

  protected int uriGroup = 4;

  /**
   * Capturing group that contains the thread.
   */

  protected int threadGroup = 5;

  /**
   * Capturing group that contains the class.
   */

  protected int classGroup = 6;

  /**
   * Capturing group that contains the message.
   */

  protected int messageGroup = 7;

  // --------------------------------------------------------------------------------
  // Parameterization
  // --------------------------------------------------------------------------------

  /**
   * Sets a parameter.
   */

  public void setParameter (String name, String value)
    throws LogOutputProcessingException
  {
    if ( name.equals("regex") )
      this.patternBuilder.setRegex(value);
    else if ( name.equals("canon-eq") )
      this.patternBuilder.setCanonicalEquivalence(stringToBoolean(value));
    else if ( name.equals("case-insensitive") )
      this.patternBuilder.setCaseInsensitive(stringToBoolean(value));
    else if ( name.equals("comments") )
      this.patternBuilder.setComments(stringToBoolean(value));
    else if ( name.equals("dotall") )
      this.patternBuilder.setDotall(stringToBoolean(value));
    else if ( name.equals("multiline") )
      this.patternBuilder.setMultiline(stringToBoolean(value));
    else if ( name.equals("unicode-case") )
      this.patternBuilder.setUnicodeCase(stringToBoolean(value));
    else if ( name.equals("unix-lines") )
      this.patternBuilder.setUnixLines(stringToBoolean(value));
    else
      throw new LogOutputProcessingException("Unknown parameter: " + name);
  }

  // --------------------------------------------------------------------------------
  // Parsing
  // --------------------------------------------------------------------------------

  /**
   * Notifies the handler that analysing starts.
   */

  public void notifyStart ()
    throws LogOutputProcessingException
  {
    this.handler.handleStart
      (true,
       true,
       ( this.priorityGroup != -1 ),
       ( this.timeGroup != -1 ),
       ( this.categoryGroup != -1 ),
       ( this.uriGroup != -1 ),
       ( this.threadGroup != -1 ),
       ( this.classGroup != -1 ),
       ( this.messageGroup != -1 ));
  }

  /**
   * Parses the specified source and sends the parse results to the handler.
   */

  public void parse (LogOutputSource source)
    throws LogOutputProcessingException
  {
    Pattern pattern = this.patternBuilder.buildPattern();
    BufferedReader reader = source.getReader();
    String sourceName = source.getName();
    String line = null;
    int lineNumber = 0;
    try
      {
        while ( (line = reader.readLine()) != null )
          {
            lineNumber++;
            Matcher matcher = pattern.matcher(line);
            if ( matcher.matches() )
              {
                this.handler.handleFormattedLine
                  (sourceName,
                   lineNumber,
                   getGroup(matcher, this.priorityGroup),
                   getGroup(matcher, this.timeGroup),
                   getGroup(matcher, this.categoryGroup),
                   getGroup(matcher, this.uriGroup),
                   getGroup(matcher, this.threadGroup),
                   getGroup(matcher, this.classGroup),
                   getGroup(matcher, this.messageGroup),
                   null);
              }
            else
              {
                this.handler.handleUnformattedLine
                  (sourceName, lineNumber, line);
              }
          }
      }
    catch(IOException exception)
      {
        throw new LogOutputProcessingException(exception);
      }
  }

  /**
   * Notifies the handler that analysing ends.
   */

  public void notifyEnd ()
    throws LogOutputProcessingException
  {
    this.handler.handleEnd();
  }

  // --------------------------------------------------------------------------------
  // Auxiliaries.
  // --------------------------------------------------------------------------------

  /**
   * Auxiliary method, returns the specified group for the specified matcher, or
   * <code>null</code> if the group is -1.
   */

  protected static final String getGroup (Matcher matcher, int group)
  {
    return (group != -1 ? matcher.group(group) : null);
  }

}
