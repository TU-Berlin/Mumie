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

import java.util.regex.Pattern;
import net.mumie.japs.log.analyse.LogOutputProcessingException;
import net.mumie.japs.log.analyse.util.RegexPatternBuilder;

/**
 * <p>
 *   Filters by a regular expression. Only those log records whose "description" field
 *   contains a match for the regular expression are passed through. Recognizes the following
 *   parameters:
 * </p>
 * <table class="genuine">
 *   <tr>
 *     <th>Name</th><th>Description</th><th>Required</th>
 *   </tr>
 *   <tr>
 *     <td>
 *       <code>regex</code>
 *     </td>
 *     <td>
 *       The regular expression. See {@link Pattern Pattern} for a description of the syntax.
 *     </td>
 *     <td>
 *       Yes
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>
 *       <code>canon-eq</code>
 *     </td>
 *     <td>
 *       Whether the "canonical equivalence" flag should be turned on. Boolean.
 *       See {@link Pattern#CANON_EQ Pattern.CANON_EQ} for the effect of this flag.
 *     </td>
 *     <td>
 *       No. Default is <code>"false"</code>
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>
 *       <code>case-insensitive</code>
 *     </td>
 *     <td>
 *       Whether the "case insensitive" flag should be turned on. Boolean. 
 *       See {@link Pattern#CASE_INSENSITIVE Pattern.CASE_INSENSITIVE} for the effect of
 *       this flag.
 *     </td>
 *     <td>
 *       No. Default is <code>"false"</code>
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>
 *       <code>comments</code>
 *     </td>
 *     <td>
 *       Whether the "comments" flag should be turned on. Boolean.
 *       See {@link Pattern#COMMENTS Pattern.COMMENTS} for the effect of this flag.
 *     </td>
 *     <td>
 *       No. Default is <code>"false"</code>
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>
 *       <code>dotall</code>
 *     </td>
 *     <td>
 *       Whether the "dotall" flag should be turned on. Boolean.
 *       See {@link Pattern#DOTALL Pattern.DOTALL} for the effect of this flag.
 *     </td>
 *     <td>
 *       No. Default is <code>"false"</code>
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>
 *       <code>multiline</code>
 *     </td>
 *     <td>
 *       Whether the "multiline" flag should be turned on. Boolean.
 *       See {@link Pattern#MULTILINE Pattern.MULTILINE} for the effect of this flag.
 *     </td>
 *     <td>
 *       No. Default is <code>"false"</code>
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>
 *       <code>unicode-case</code>
 *     </td>
 *     <td>
 *       Whether the "unicode case" flag should be turned on. Boolean.
 *       See {@link Pattern#UNICODE_CASE Pattern.UNICODE_CASE} for the effect of this flag.
 *     </td>
 *     <td>
 *       No. Default is <code>"false"</code>
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>
 *       <code>unix-lines</code>
 *     </td>
 *     <td>
 *       Whether the "unix lines" flag should be turned on. Boolean.
 *       See {@link Pattern#UNIX_LINES Pattern.UNIX_LINES} for the effect of this flag.
 *     </td>
 *     <td>
 *       No. Default is <code>"false"</code>
 *     </td>
 *   </tr>
 * </table>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: RegexLogOutputFilter.java,v 1.4 2006/01/15 20:22:59 rassy Exp $</code>
 */

public class RegexLogOutputFilter extends DefaultLogOutputFilter
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The pattern.
   */

  protected Pattern pattern = null;

  /**
   * Helper to build the pattern.
   */

  protected RegexPatternBuilder patternBuilder = new RegexPatternBuilder();

  /**
   * Internal auxiliary variable, <code>true</code> if the last regex test was successful,
   * <code>false</code> otherwise.
   */

  protected boolean matched = false;

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
  // Implementation of the handler methods
  // --------------------------------------------------------------------------------

  /**
   * Handles a formatted log line.
   */

  public void handleFormattedLine (String sourceName,
                                   int lineNumber,
                                   String priority,
                                   String time,
                                   String category,
                                   String uri,
                                   String thread,
                                   String className,
                                   String message,
                                   String highlight)
    throws LogOutputProcessingException
  {
    if ( this.withMessage && this.checkIfMatches(message) )
      this.handler.handleFormattedLine
        (sourceName,
         lineNumber,
         priority,
         time,
         category,
         uri,
         thread,
         className,
         message,
         highlight);
  }

  /**
   * Handles a unformatted log line.
   */

  public void handleUnformattedLine (String sourceName,
                                     int lineNumber,
                                     String line)
    throws LogOutputProcessingException
  {
    if ( this.withMessage && this.matched )
      this.handler.handleUnformattedLine(sourceName, lineNumber, line);
  }

  // --------------------------------------------------------------------------------
  // Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   *
   */

  public boolean checkIfMatches (String string)
  {
    if ( this.pattern == null )
      this.pattern = this.patternBuilder.buildPattern();
    this.matched = this.pattern.matcher(string).find();
    return this.matched;
  }
}