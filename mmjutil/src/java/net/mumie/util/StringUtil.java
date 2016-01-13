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

import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Provides static methods to handle strings.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: StringUtil.java,v 1.6 2009/01/28 10:52:32 rassy Exp $</code>
 */

public class StringUtil
{
  // --------------------------------------------------------------------------------
  // Creating strings
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Creates a string of the specified length made of repetitions of the specified
   *   character.
   * </p>
   * <p>
   *   Example: <code>createString('.', 5)</code> returns <code>"....."</code>.
   * </p>
   */

  public static String createString (char character, int number)
  {
    char[] characters = new char[number];
    Arrays.fill(characters, character);
    return new String(characters);
  }

  /**
   * Creates a string of space characters (<code>' '</code>) with the specified length.
   */

  public static String createSpaceString (int length)
  {
    char[] chars = new char[length];
    Arrays.fill(chars, ' ');
    return new String(chars);
  }

  /**
   * Concatenates the specified strings with the specified separator.
   */

  public static String join (String[] strings, String separator)
  {
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < strings.length; i++)
      {
	if ( i > 0 ) buffer.append(separator);
	buffer.append(strings[i]);
      }
    return buffer.toString();
  }

  // --------------------------------------------------------------------------------
  // Whitepsaces
  // --------------------------------------------------------------------------------

  /**
   * Removes trailing whitespaces from the specified string.
   */

  public static String stripTrailingWhitespaces (String string)
  {
    int pos = string.length()-1;
    if ( pos == -1 )
      return string;
    while ( Character.isWhitespace(string.charAt(pos)) )
      pos--;
    return string.substring(0, pos+1);
  }

  // --------------------------------------------------------------------------------
  // Suffixes and prefixes
  // --------------------------------------------------------------------------------

  /**
   * Returns the suffix of the specified string. The suffix is the part of the string
   * starting with the last <code>'.'</code> character. The <code>'.'</code> character is
   * included in the suffix. If the string does not contain a <code>'.'</code> character,
   * returns <code>null</code>.
   */

  public static String getSuffix (String string)
  {
    int posOfDot = string.lastIndexOf('.');
    return (posOfDot != -1 ? string.substring(posOfDot) : null);
  }

  /**
   * Replaces the suffix of the specified string with the specified new suffix. The suffix
   * is the part beginning with the last <code>'.'</code> character. The <code>'.'</code>
   * character is included in the suffix. If the string does not contain a <code>'.'</code>
   * character, appends the new suffix to the string. 
   *
   * @param string the string with the suffix to be replaced
   * @param newSuffix the new suffix
   */

  public static String replaceSuffix (String string, String newSuffix)
  {
    int posOfDot = string.lastIndexOf('.');
    return (posOfDot != -1
            ? string.substring(0, posOfDot).concat(newSuffix)
            : string.concat(newSuffix));
  }

  /**
   * Replaces the specified old suffix by the specified new suffix in the specified
   * string. If the string does not end with the old suffix, appends the new suffix to the
   * string.
   *
   * @param string the string with the suffix to replace
   * @param oldSuffix the old suffix
   * @param newSuffix the new suffix
   */

  public static String replaceSuffix (String string, String oldSuffix, String newSuffix)
  {
    return (string.endsWith(oldSuffix)
	    ? string.substring(0, string.length() - oldSuffix.length()).concat(newSuffix)
	    : string.concat(newSuffix));
  }

  /**
   * Replaces the first suffix in the array <code>oldSuffixes</code> that matches the end of
   * the specified string by the specifed new suffix. If no suffix in <code>oldSuffixes</code>
   * matches, appends the new suffix to string.
   *
   * @param string the string with the suffix to replace
   * @param oldSuffixes list of old suffixes
   * @param newSuffix the new suffix
   */

  public static String replaceSuffix (String string, String[] oldSuffixes, String newSuffix)
  {
    for (int i = 0; i < oldSuffixes.length; i++)
      {
        String oldSuffix = oldSuffixes[i];
        if ( string.endsWith(oldSuffix) )
          return string.substring(0, string.length() - oldSuffix.length()).concat(newSuffix);
      }
    return string.concat(newSuffix);
  }

  /**
   * Replaces the specifed old pefix by the specified new prefix in the specified string. If
   * the string does not start with the old prefix, prepends the new prefix to the string.
   *
   * @param string the string with the prefix to replace
   * @param oldPrefix the old prefix
   * @param newPrefix the new prefix
   */

  public static String replacePrefix (String string, String oldPrefix, String newPrefix)
  {
    return (string.startsWith(oldPrefix)
	    ? newPrefix.concat(string.substring(oldPrefix.length()))
	    : newPrefix.concat(string));
  }

  // --------------------------------------------------------------------------------
  // Command line parsing
  // --------------------------------------------------------------------------------

  /**
   * Parses the specified command line and returns the tokens
   */

  public static List<String> parseCmdline (String cmdline)
    throws CmdlineParseException
  {
    boolean singleQuoted = false;
    boolean doubleQuoted = false;
    List<String> tokens = new ArrayList<String>();
    StringBuilder currentToken = null;
    char[] chars = cmdline.toCharArray();

    // Iterate through the characters of the string:
    for (int i = 0; i < chars.length; i++)
      {
        char c = chars[i];
        if ( Character.isSpaceChar(c) )
          {
            if ( singleQuoted || doubleQuoted  )
              {
                currentToken.append(c);
              }
            else if ( currentToken != null )
              {
                tokens.add(currentToken.toString());
                currentToken = null;
              }
          }
        else
          {
            if ( currentToken == null )
              currentToken = new StringBuilder();

            if ( c == '\'' )
              {
                if ( singleQuoted )
                  singleQuoted = false;
                else if ( doubleQuoted )
                  currentToken.append(c);
                else
                  singleQuoted = true;
              }
            else if ( c == '"' )
              {
                if ( doubleQuoted )
                  doubleQuoted = false;
                else if ( singleQuoted )
                  currentToken.append(c);
                else
                  doubleQuoted = true;
              }
            else if ( c == '\\' )
              {
                i++;
                if ( i < chars.length )
                  currentToken.append(chars[i]);
              }
            else
              currentToken.append(c);
          }
      }

    // Check if quotes are closed:
    if ( singleQuoted )
      throw new CmdlineParseException("Missing closing single quote");
    if ( doubleQuoted )
      throw new CmdlineParseException("Missing closing double quote");

    // Finish last token if necessary:
    if ( currentToken != null )
      tokens.add(currentToken.toString());

    return tokens;
  }

  /**
   * Performs parameter substitution in the specified command line.
   */

  public static String substProperties (String cmdline, Properties properties)
    throws CmdlineParseException
  {
    StringBuilder output = new StringBuilder();
    StringBuilder property = null;
    StringBuilder target = output;
    char[] chars = cmdline.toCharArray();
    for (int i = 0; i < chars.length; i++)
      {
        char c = chars[i];
        switch ( c )
          {
          case '$':
            if ( target == output && chars[i+1] == '{' )
              {
                property = new StringBuilder();
                target = property;
                i++;
              }
            else
              target.append(c);
            break;
          case '}':
            if ( target == property )
              {
                String value = properties.getProperty(property.toString());
                if ( value != null ) output.append(value);
                property = null;
                target = output;
              }
            else
              target.append(c);
            break;
          case '\\':
            target.append(c);
            if ( i < chars.length ) target.append(chars[++i]);
            break;
          default:
            target.append(c);
          }
      }
    if ( target == property )
      throw new CmdlineParseException("Missing closing '}'");
    return output.toString();
  }

  // --------------------------------------------------------------------------------
  // Name style conversion
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Converts compound names to the "upcase first character style". That means that the
   *   first characters of all components of the name except, perhaps, the first one, are
   *   changed to upper case and all component are concatenated. The components of the input
   *   name are expected to be separated by one or more characters specified by
   *   <code>separators</code>. If <code>startWithUpperCase</code> is <code>true</code>, the
   *   first character of the first component is capitalized as well, otherwise it is not.
   * </p>
   * <p>
   *   Example:<pre>
   *     toUpcaseFirstStyle("foo-bar-bazz", "-", true)</pre>
   *   returns <code>"FooBarBazz"</code>.
   * </p>
   * @param string the input string.
   * @param separators characters that separate the components of the input string
   * @param startWithUpperCase whether the result string should start with upper case
   */

  public static String toUpcaseFirstStyle (String string,
                                           String separators,
                                           boolean startWithUpperCase)
  {
    StringTokenizer tokenizer = new StringTokenizer(string, separators);
    StringBuffer buffer = new StringBuffer();
    boolean notFirst = false;
    while ( tokenizer.hasMoreTokens() )
      {
	String token = tokenizer.nextToken();
	if ( notFirst || startWithUpperCase ) token = upcaseFirstChar(token);
	buffer.append(token);
	notFirst = true;
      }
    return buffer.toString();
  }

  /**
   * <p>
   *   Converts compound names  to the "lower case with separtor style". That meas that all
   *   components of the name are converted to lower case and concatenated with a certain
   *   string, the separator. The input string is expected to be in the "upcase first
   *   character style", See {@link #toUpcaseFirstStyle toUpcaseFirstStyle} for an
   *   explanation of that term.
   * </p>
   * <p>
   *   Example 1:<pre>
   *     toLowerCaseWithSepStyle("FooBarBazz", "-")</pre>
   *   returns <code>"foo-bar-bazz"</code>.
   * </p>
   * <p>
   *   Example 1:<pre>
   *     toLowerCaseWithSepStyle("XSLStylesheet", "_")</pre>
   *   returns <code>"xsl_stylesheet"</code>.
   * </p>
   * @param string the input string.
   * @param separator the separator
   */

  public static String toLowerCaseWithSepStyle (String string, String separator)
  {
    char[] chars = string.toCharArray();
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < chars.length; i++)
      {
	if ( Character.isUpperCase(chars[i]) &&       // char is upper case
	     i > 0 &&                                 // 
	     ( Character.isLowerCase(chars[i-1]) ||
	       ( i < chars.length-1 && Character.isLowerCase(chars[i+1]) ) ) )
	  buffer.append(separator);
	buffer.append(Character.toLowerCase(chars[i]));
      }
    return buffer.toString();
  }

  // --------------------------------------------------------------------------------
  // Formatting text
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Formats the specified text according to a given width and left margin. The text is
   *   first broken up into words. Then lines are formed that have the specified left margin
   *   and do not exceed the specified width. Each sequence of whitespaces is replaced by a
   *   single space or newline character.
   * </p>
   * <p>
   *   Example: Consider <code>text</code> is:
   * </p>
   * <pre>
   *   <span class="highlight">This a test text to demonstate the formatTextLeft method in the StringUtil class.</span></pre>
   * <p>
   *   Then <code>formatLeft(text, 20, 2)</code> creates:
   * </p>
   * <pre>
   *   <span class="highlight">  This a test text to</span>
   *   <span class="highlight">  demonstate the</span>
   *   <span class="highlight">  formatTextLeft</span>
   *   <span class="highlight">  method in the</span>
   *   <span class="highlight">  StringUtil class.</span></pre>
   * <p>
   *   Since each sequence of whitespaces is replaced by a single space or newline character,
   *   we got the same result if <code>text</code> where
   * </p>
   * <pre>
   *   <span class="highlight">This a test text                 to demonstate </span>
   *   <span class="highlight">  </span>
   *   <span class="highlight">         the </span>
   *   <span class="highlight">  formatTextLeft method in the StringUtil class.</span>
   *   <span class="highlight">     </span></pre>
   *
   * @param text the string to format.
   * @param width the maximum width. This width is not exceeded.
   * @param margin the left margin (number of characters)
   */

  public static String formatTextLeft (String text, int width, int margin)
  {
    StringBuffer buffer = new StringBuffer();
    StringTokenizer tokenizer = new StringTokenizer(text);
    String marginString = createSpaceString(margin);
    buffer.append(marginString);
    int column = margin;
    boolean firstWord = true;
    while ( tokenizer.hasMoreTokens() )
      {
	String word = tokenizer.nextToken();
	if ( (column + word.length()) > width )
	  {
	    buffer.append("\n").append(marginString);
	    column = margin;
	  }
	else if ( ! firstWord )
	  {
	    buffer.append(" ");
	    column++;
	  }
	buffer.append(word);
	column += word.length();
	firstWord = false;
      }
    return buffer.toString();
  }

  /**
   * <p>
   *   Moves a block of text to the left. The specified string (<code>text</code>) is first
   *   broken up in lines. From the beginning of each line are then as many space
   *   characters deleted as <code>shift</code> specifies. If there are less then
   *   <code>shift</code> space characters at the beginning of a line, only the space
   *   characters up to but excluding the first non-space characters are deleted. "Space"
   *   always means the <code>' '</code> character.
   * </p>
   * <p>
   *   Example: Consider <code>text</code> is:
   * </p>
   * <pre>
   *   <span class="highlight">    This is  </span>
   *   <span class="highlight">      a test text</span></pre>
   * <p>
   *   Then <code>shiftLeft(text, 2)</code> creates:
   * </p>
   * <pre>
   *   <span class="highlight">  This is  </span>
   *   <span class="highlight">    a test text</span></pre>
   * <p>
   *   Whereas <code>shiftLeft(text, 5)</code> returns:
   * </p>
   * <pre>
   *   <span class="highlight">This is  </span> +
   *   <span class="highlight"> a test text</span></pre>
   * <p>
   *   If <code>shift</code> is 0 or negative, returns <code>text</code> unchanged.
   * </p>
   *
   * @throws StringUtilException if something goes wrong.
   */

  public static String shiftTextLeft (String text, int shift)
    throws StringUtilException
  {
    if ( shift <= 0 )
      return text;
    try
      {
        StringBuffer buffer = new StringBuffer();
        StringTokenizer tokenizer = new StringTokenizer(text, "\n\r\f", true);
        while ( tokenizer.hasMoreTokens() )
          {
            String token = tokenizer.nextToken();
            int pos = 0;
            while ( pos < shift && pos < token.length() && token.charAt(pos) == ' ' )
              pos++;
            if ( pos < token.length() )
              token = token.substring(pos);
            buffer.append(token);
          }
        return buffer.toString();
      }
    catch (Exception exception)
      {
        throw new StringUtilException
          ("shiftTextLeft: Problem with string: " + text, exception);
      }
  }

  /**
   * Moves a block of text to the right. The specified string (<code>text</code>) is first
   * broken up in lines. Each line is then prepended with a string made of as many space
   * characters (<code>' '</code>) as specified by <code>shift</code>. If <code>shift</code>
   * is 0 or negative, returns <code>text</code> unchanged.
   */

  public static String shiftTextRight (String text, int shift)
  {
    if ( shift <= 0 )
      return text;
    StringBuffer buffer = new StringBuffer();
    String shiftString = createSpaceString(shift);
    StringTokenizer tokenizer = new StringTokenizer(text, "\n\r\f", true);
    while ( tokenizer.hasMoreTokens() )
      buffer.append(shiftString + tokenizer.nextToken());
    return buffer.toString();
  }

  /**
   * Moves a block of text to the left or right. If <code>shift</code> is negative, this is
   * equivalent to {@link #shiftTextLeft shiftTextLeft(text, -shift)}. If <code>shift</code> is
   * positive, this is equivalent to {@link #shiftTextRight shiftTextRight(text, shift)}. If
   * <code>shift</code> is 0, the text is returned unchanged.
   *
   * @param text the text to shift
   * @param shift number of characters to shift
   *
   * @throws StringUtilException if something goes wrong
   */

  public static String shiftText (String text, int shift)
    throws StringUtilException
  {
    return (shift < 0 ? shiftTextLeft(text, -shift) : shiftTextRight(text, shift));
  }

  // --------------------------------------------------------------------------------
  // Misc.
  // --------------------------------------------------------------------------------

  /**
   * Returns <code>true</code> if the specified string is <code>null</code> or consists only
   * of whitepsaces. Otherwise, returns <code>false</code>.
   */

  protected static boolean isEmpty (String string)
  {
    return
      ( string == null || string.trim().length() == 0 );
  }

  /**
   * Returns <code>string</code> with the first character changed to upper case.
   */

  public static String upcaseFirstChar (String string)
  {
    char[] chars = string.toCharArray();
    chars[0] = Character.toUpperCase(chars[0]);
    return new String(chars);
  }

  /**
   * Returns true if <code>list</code> contains <code>word</code>, otherwise
   * false. <code>list</code> must be a list of tokens seperated by one of the characters in
   * <code>delimiters</code>.
   */

  public static boolean listContains (String list, String word, String delimiters)
  {
    StringTokenizer tokenizer = new StringTokenizer(list, delimiters);
    boolean contains = false;
     while ( ( ! contains ) && ( tokenizer.hasMoreTokens() ) )
       contains = ( tokenizer.nextToken().equals(word) );
    return contains;
  }

  /**
   * Returns true if <code>list</code> contains <code>word</code>, otherwise
   * false. <code>list</code> must be a list of tokens seperated by a blank, a tab, or a
   * newline. 
   */

  public static boolean listContains (String list, String word)
  {
    return listContains(list, word, ", \t\n");
  }

  /**
   * Returns true if the specified string is <code>"yes"</code> or <code>"true"</code>,
   * false if the specified string is <code>"no"</code> or <code>"false"</code>, and throws
   * an {@link IllegalArgumentException IllegalArgumentException} otherwise.
   */

  public static boolean stringToBoolean (String string)
  {
    if ( string == null )
      throw new IllegalArgumentException
        ("stringToBoolean: Input string is null");
    else if ( string.equals("yes") || string.equals("true") )
      return true;
    else if ( string.equals("no") || string.equals("false") )
      return false;
    else
      throw new IllegalArgumentException
        ("stringToBoolean: Illegal boolean specified: " + string + " (expected yes|no|true|false)");
  }

}
