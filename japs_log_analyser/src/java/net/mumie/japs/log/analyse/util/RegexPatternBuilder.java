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

package net.mumie.japs.log.analyse.util;

import java.util.regex.Pattern;

public class RegexPatternBuilder
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The pattern code.
   */

  protected String regex = null;

  /**
   * Wether the {@link Pattern#CANON_EQ CANON_EQ} flag is turned on.
   */

  protected boolean canonicalEquivalence = false;

  /**
   * Wether the {@link Pattern#CASE_INSENSITIVE CASE_INSENSITIVE} flag is turned on.
   */

  protected boolean caseInsensitive = false;

  /**
   * Wether the {@link Pattern#COMMENTS COMMENTS} flag is turned on.
   */

  protected boolean comments = false;

  /**
   * Wether the {@link Pattern#DOTALL DOTALL} flag is turned on.
   */

  protected boolean dotall = false;

  /**
   * Wether the {@link Pattern#MULTILINE MULTILINE} flag is turned on.
   */

  protected boolean multiline = false;

  /**
   * Wether the {@link Pattern#UNICODE_CASE UNICODE_CASE} flag is turned on.
   */

  protected boolean unicodeCase = false;

  /**
   * Wether the {@link Pattern#UNIX_LINES UNIX_LINES} flag is turned on.
   */

  protected boolean unixLines = false;

  // --------------------------------------------------------------------------------
  // Constructor
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>RegexPatternBuilder</code> with the specified regular expression.
   */

  public RegexPatternBuilder (String regex)
  {
    this.regex = regex;
  }

  /**
   * Creates a new <code>RegexPatternBuilder</code> with an uninitialized regular expression.
   */

  public RegexPatternBuilder ()
  {
    this.regex = null;
  }

  // --------------------------------------------------------------------------------
  // Settings (regex, flags)
  // --------------------------------------------------------------------------------

  /**
   * Sets the regular expression.
   */

  public void setRegex (String regex)
  {
    this.regex = regex;
  }

  /**
   * Sets the {@link Pattern#CANON_EQ CANON_EQ} flag.
   */

  public void setCanonicalEquivalence (boolean canonicalEquivalence)
  {
    this.canonicalEquivalence = canonicalEquivalence;
  }

  /**
   * Sets the {@link Pattern#CASE_INSENSITIVE CASE_INSENSITIVE} flag.
   */

  public void setCaseInsensitive (boolean caseInsensitive)
  {
    this.caseInsensitive = caseInsensitive;
  }

  /**
   * Sets the {@link Pattern#COMMENTS COMMENTS} flag.
   */

  public void setComments (boolean comments)
  {
    this.comments = comments;
  }

  /**
   * Sets the {@link Pattern#DOTALL DOTALL} flag.
   */

  public void setDotall (boolean dotall)
  {
    this.dotall = dotall;
  }

  /**
   * Sets the {@link Pattern#MULTILINE MULTILINE} flag.
   */

  public void setMultiline (boolean multiline)
  {
    this.multiline = multiline;
  }

  /**
   * Sets the {@link Pattern#UNICODE_CASE UNICODE_CASE} flag.
   */

  public void setUnicodeCase (boolean unicodeCase)
  {
    this.unicodeCase = unicodeCase;
  }

  /**
   * Sets the {@link Pattern#UNIX_LINES UNIX_LINES} flag.
   */

  public void setUnixLines (boolean unixLines)
  {
    this.unixLines = unixLines;
  }

  // --------------------------------------------------------------------------------
  // Creating and returning the pattern
  // --------------------------------------------------------------------------------

  /**
   * 
   */

  public Pattern buildPattern ()
  {
    int flags = 0;
    if ( this.canonicalEquivalence ) flags += Pattern.CANON_EQ;
    if ( this.caseInsensitive ) flags += Pattern.CASE_INSENSITIVE;
    if ( this.comments ) flags += Pattern.COMMENTS;
    if ( this.dotall ) flags += Pattern.DOTALL;
    if ( this.multiline ) flags += Pattern.MULTILINE;
    if ( this.unicodeCase ) flags += Pattern.UNICODE_CASE;
    if ( this.unixLines ) flags += Pattern.UNIX_LINES;
    return Pattern.compile(this.regex, flags);
  }
}