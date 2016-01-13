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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import net.mumie.util.io.IOUtil;

/**
 * <p>
 *   Provides static methods for miscellaneous purposes. In particular, can be used as XSLT
 *   extension functions.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 *
 * @version <span class="file">$Id: Util.java,v 1.5 2008/04/28 23:58:47 rassy Exp $</span>
 *
 * @deprecated Use {@link StringUtil}, {@link ErrorUtil}, and
 *   {@link net.mumie.util.io.IOUtil IOUtil} instead.
 */

public class Util
{
  // ------------------------------------------------------------------------------------------
  // Global variables and constants
  // ------------------------------------------------------------------------------------------

  /**
   * Name of the class
   */

  final static String CLASS_NAME;

  /**
   * Size of IO buffers in bytes (<code>1024</code>).
   */

  final static int IO_BUFFER_SIZE = 1024;

  /**
   * String identifiers for the boolean value <code>true</code>. Used by
   * {@link #getBoolean(String) getBoolean}.
   */

  final static String[] TRUE_IDENTIFIERS = {"true", "yes", "on"};

  /**
   * String identifiers for the boolean value <code>false</code>. Used by
   * {@link #getBoolean(String) getBoolean}.
   */

  final static String[] FALSE_IDENTIFIERS = {null, "", "false", "no", "off"};

  // ------------------------------------------------------------------------------------------
  // Auxiliaries
  // ------------------------------------------------------------------------------------------

  /**
   * Helper method. Reads <code>inputStream</code> and returns its content as a string.
   */
  
  protected static String readStream (InputStream inputStream)
    throws IOException
  {
    InputStreamReader reader = new InputStreamReader(inputStream);
    char[] ioBuffer = new char[IO_BUFFER_SIZE];
    StringBuffer content = new StringBuffer();
    int number = -1;

    while ( (number = reader.read(ioBuffer)) != -1 )
      {
	content.append(ioBuffer, 0, number);
      }
	
    return content.toString();
  }

  // ------------------------------------------------------------------------------------------
  // Strings
  // ------------------------------------------------------------------------------------------

  /**
   * Returns <code>true</code> if <code>string1</code> and <code>string2</code> are equal or
   * both <code>null</code>; otherwise <code>false</code>. If <code>ingoreCase</code> is
   * <code>true</code>, case does not matter (i.e., testing for equality is done with
   * {@link java.lang.String#equalsIgnoreCase equalsIgnoreCase}, otherwise it matters (i.e.,
   * testing for equality is done with {@link java.lang.String#equals equals}.
   */

  public static boolean stringsAreEqual (String string1, String string2, boolean ignoreCase)
  {
    return
      ( string1 != null && string2 != null && 
	(ignoreCase ? string1.equalsIgnoreCase(string2) : string1.equals(string2)) ) ||
      ( string1 == null && string2 == null );
  }

  /**
   * Same as {@link #stringsAreEqual(String,String) stringsAreEqual(string1, string2, false)}.
   */

  public static boolean stringsAreEqual (String string1, String string2)
  {
    return stringsAreEqual(string1, string2, false);
  }

  /**
   * Same as {@link #stringsAreEqual(String,String) stringsAreEqual(string1, string2, true)}.
   */

  public static boolean stringsAreEqualCaseIgnored (String string1, String string2)
  {
    return stringsAreEqual(string1, string2, true);
  }

  /**
   * Removes leading and trailing whitespaces from <ode>string</code> and returns it.
   */

  public static String trim (String string)
  {
    return string.trim();
  }

  /**
   * Composes a string made of <code>number</number> repititions of <code>character</code>.
   */

  public static String multiplyChar (char character, int number)
  {
    char[] characters = new char[number];
    for (int i = 0; i < number; i++) characters[i] = character;
    return new String(characters);
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
   * <p>
   *   Converts compound names to the "upcase first character style". That means that the
   *   first characters of all components of the name except, perhaps, the first one, are
   *   changed to upper case and all component are concatenated. The components of the input
   *   name are expected to be separated by one or more characters in
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
   *
   * @deprecated use {@link StringUtil#toUpcaseFirstStyle StringUtil.toUpcaseFirstStyle}
   * instead. 
   */

  public static String toUpcaseFirstStyle (String string, String separators, boolean startWithUpperCase)
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
   *   Converts compound names in the "upcase first character style" (see
   *   {@link #toUpcaseFirstStyle toUpcaseFirstStyle}) to the "lower case with separtor
   *   style". That meas that all components of the name are converted to lower case and
   *   concatenated with a certain string, the separator. 
   * </p>
   * <p>
   *   Example 1:<pre>
   *     toLowerCaseWithSeparatorStyle("FooBarBazz", "-")</pre>
   *   returns <code>"foo-bar-bazz"</code>.
   * </p>
   * <p>
   *   Example 1:<pre>
   *     toLowerCaseWithSeparatorStyle("XSLStylesheet", "_")</pre>
   *   returns <code>"xsl_stylesheet"</code>.
   * </p>
   * @param string the input string.
   * @param separator the separator
   *
   * @deprecated use
   * {@link StringUtil#toLowerCaseWithSepStyle StringUtil.toLowerCaseWithSepStyle}
   * instead. 
   */

  public static String toLowerCaseWithSeparatorStyle (String string, String separator)
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

  /**
   * Formats a string as a block of text. Lines are wrapped according to a maximum width
   * and optionally indented.
   *
   * @param source The string to format.
   * @param width The maximum width. This width is not exceeded.
   * @param indent Number of blanks to indent.
   *
   * @deprecated Use {@link net.mumie.util.StringUtil#formatTextLeft StringUtil.formatTextLeft}
   * instead.
   */

  public static String formatLines (String source, int width, int indent)
  {
    StringBuffer target = new StringBuffer();
    StringTokenizer tokenizer = new StringTokenizer(source);
    String margin = multiplyChar(' ', indent);
    target.append(margin);
    int column = margin.length();
    boolean firstWord = true;
    while ( tokenizer.hasMoreTokens() )
      {
	String word = tokenizer.nextToken();
	if ( (column + word.length()) > width )
	  {
	    target.append("\n").append(margin);
	    column = margin.length();
	  }
	else if ( ! firstWord )
	  {
	    target.append(" ");
	    column++;
	  }
	target.append(word);
	column += word.length();
	firstWord = false;
      }
    return target.toString();
  }

  /**
   * Returns a truncated version of the string, where the removed part is marked by
   * <code>ellipse</code>.
   */

  public static String truncate (String string, int maxLength, boolean atBeginning,
				 String ellipse)
  {
    int length = string.length();
    int removedLength = length - maxLength - ellipse.length();
    if ( removedLength > 0 )
      {
	string = (atBeginning
		  ? ellipse + string.substring(removedLength)
		  : string.substring(0, length - removedLength) + ellipse);
      }
    return string;
  }

  /**
   * Same as {@link #truncate(String,int,boolean,String) truncate(string, maxLength, atBeginning, "[...]")}.
   */

  public static String truncate (String string, int maxLength, boolean atBeginning)
  {
    return truncate(string, maxLength, atBeginning, "[...]");
  }

  /**
   * Returns whether <code>string</code> ends with <code>suffix</code> or not.
   */

  public static boolean stringEndsWith (String string, String suffix)
  {
    return string.endsWith(suffix);
  }

  /**
   * Returns <code>true</code> is <code>string</code> is <code>null</code>, empty, or
   * consists only of whitespaces.
   */

  public static boolean nullOrVoid (String string)
  {
    return ( ( string == null ) || ( string.trim().length() == 0 ) );
  }

  /**
   * Returns the suffix of string, i.e., the part beginning with the last <code>'.'</code>
   * character.
   *
   * @deprecated use {@link StringUtil#getSuffix StringUtil.getSuffix} instead.
   */

  public static String getSuffix (String string)
  {
    int posOfDot = string.lastIndexOf('.');
    return (posOfDot != -1 ? string.substring(posOfDot) : null);
  }

  /**
   * Replaces the suffix of <code>string</code> by <code>newSuffix</code>. The suffix is the part
   * beginning with the last <code>'.'</code> character. If <code>string</code> does not
   * contain the <code>'.'</code> character, appends <code>newSuffix</code> to
   * <code>string</code>.
   *
   * @deprecated use
   * {@link StringUtil#replaceSuffix(String,String) StringUtil.replaceSuffix(string,newSuffix}
   * instead.
   */

  public static String replaceSuffix (String string, String newSuffix)
  {
    int posOfDot = string.lastIndexOf('.');
    return (posOfDot != -1
            ? string.substring(0, posOfDot).concat(newSuffix)
            : string.concat(newSuffix));
  }

  /**
   * Replaces suffix <code>oldSuffix</code> by <code>newSuffix</code> in
   * <code>string</code>. If <code>string</code> does not end with <code>oldSuffix</code>,
   * appends <code>newSuffix</code> to <code>string</code>. 
   *
   * @deprecated use
   * {@link StringUtil#replaceSuffix(String,String,String) StringUtil.replaceSuffix(string,oldSuffix,newSuffix}
   * instead.
   */

  public static String replaceSuffix (String string, String oldSuffix, String newSuffix)
  {
    return (string.endsWith(oldSuffix)
	    ? string.substring(0, string.length() - oldSuffix.length()).concat(newSuffix)
	    : string.concat(newSuffix));
  }

  /**
   * Replaces the first suffix in <code>oldSuffixes</code> that matches the end of
   * <code>string</code> by <code>newSuffix</code>. If no suffix in <code>oldSuffixes</code>
   * matches, appends <code>newSuffix</code> to <code>string</code>.
   *
   * @deprecated use
   * {@link StringUtil#replaceSuffix(String,String[],String) StringUtil.replaceSuffix(string,oldSuffixes,newSuffix}   */

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
   * Replaces prefix <code>oldPrefix</code> by <code>newPrefix</code> in <code>string</code>.
   *
   * @deprecated use
   * {@link StringUtil#replacePrefix(String,String,String) StringUtil.replacePrefix(string, oldPrefix, newPrefix}
   * instead.
   */

  public static String replacePrefix (String string, String oldPrefix, String newPrefix)
  {
    return (string.startsWith(oldPrefix)
	    ? newPrefix.concat(string.substring(oldPrefix.length()))
	    : string);
  }

  /**
   * Concatenates <code>strings</code> with <code>separator</code>.
   *
   * @deprecated use {@link StringUtil#join StringUtil.join} instead.
   */

  public static String concatenate (String[] strings, String separator)
  {
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < strings.length; i++)
      {
	if ( i > 0 ) buffer.append(separator);
	buffer.append(strings[i]);
      }
    return buffer.toString();
  }

  /**
   * Returns true if <code>list</code> contains <code>word</code>, otherwise
   * false. <code>list</code> must be a list of tokens seperated by one of the characters in
   * <code>delimiters</code>.
   *
   * @deprecated use
   * {@link StringUtil#listContains(String,String,String) StringUtil.listContains(String,String,String)}
   * instead.
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
   *
   * @deprecated use
   * {@link StringUtil#listContains(String,String) StringUtil.listContains(String,String)}
   * instead.
   */

  public static boolean listContains (String list, String word)
  {
    return listContains(list, word, ", \t\n");
  }

  /**
   * Similar to Perl's <code>grep</code> function. Returns all elements of
   * <code>strings</code> that match <code>pattern</code>, as an array
   */

  public static String[] getMatching (Pattern pattern, String[] strings)
  {
    List matchingStrings = new ArrayList();
    for (int i = 0; i < strings.length; i++)
      {
	if ( pattern.matcher(strings[i]).find() ) matchingStrings.add(strings[i]);
      }
    return (String[])(matchingStrings.toArray(new String[matchingStrings.size()]));
  }

  /**
   * Similar to Perl's <code>grep</code> function. Returns all elements of
   * <code>strings</code> that match <code>regexp</code>, as an array
   */

  public static String[] getMatching (String regexp, String[] strings)
    throws PatternSyntaxException
  {
    return getMatching(Pattern.compile(regexp), strings);
  }

  /**
   * Quotes characters <code>charsToQuote</code> in <code>string</code> by prepending a
   * backslash.
   */

  public static String quote (String string, String charsToQuote)
  {
    char[] chars = string.toCharArray();
    StringBuffer result = new StringBuffer();
    for (int i = 0; i < string.length(); i++)
      {
        if ( charsToQuote.indexOf(chars[i]) != -1 ) result.append('\\');
        result.append(chars[i]);
      }
    return result.toString();
  }

  // ------------------------------------------------------------------------------------------
  // Files
  // ------------------------------------------------------------------------------------------

  /**
   * <p>
   *   Concatenates two path fragments, taking care of leading/trailing path separators.
   *   <code>path1</code> and <code>path2</code> are the first resp. second fragment.
   *   <code>path1</code> may or may not end with a path separator. <code>path2</code>
   *   may or may not start with a path separator. In any case, the resulting string contains
   *   exactly one path separator between the parts specified by <code>path1</code> and
   *   <code>path2</code>. E.g., on Unix systems, regardless wether <code>path1</code> is
   * </p>
   * <quote>
   *   <span class="string">"foo"</span>  or  <span class="string">"foo/"</span>,
   * </quote>
   * <p>
   *   and <code>path2</code> is
   * </p>
   * <quote>
   *   <span class="string">"bar"</span>  or  <span class="string">"/bar"</span>,
   * </quote>
   * <p>
   *   the returned string is always
   * <quote>
   *   <span class="string">"foo/bar"</span>
   * </quote>
   */

  public static String concatenatePaths (String path1, String path2)
  {
    if ( ( path1 == null ) || ( path1.length() == 0 ) )
      return path2;
    else if ( path1.endsWith(File.separator) )
      {
	if ( path2.startsWith(File.separator) )
	  return path1.concat(path2.substring(1));
	else
	  return path1.concat(path2);
      }
    else
      {
	if ( path2.startsWith(File.separator) )
	  return path1.concat(path2);
	else
	  return path1.concat(File.separator).concat(path2);
      }
  }

  /**
   * @deprecated Use {@link #concatenatePaths concatenatePaths} instead.
   */

  public static String prependURLPrefix (String prefix, String restURL)
  {
    return concatenatePaths(prefix, restURL);
  }

  /**
   * Returns the path elemens of <code>filename</code>, as a list. The <em>path elements</em>
   * of <code>filename</code> are the parts separated by
   * {@link java.io.File#separator File.separator}.
   */

  public static List getPathElementList (String filename)
  {
    List pathElements = new ArrayList();
    StringTokenizer tokenizer = new StringTokenizer(filename, File.separator);
    while ( tokenizer.hasMoreTokens() ) pathElements.add(tokenizer.nextToken());
    return pathElements;
  }

  /**
   * Returns the path elemens of <code>filename</code>, as an array of strings. The <em>path
   * elements</em> of <code>filename</code> are the parts separated by
   * {@link java.io.File#separator File.separator}.
   */

  public static String[] getPathElements (String filename)
  {
    List pathElements = getPathElementList(filename);
    return (String[])(pathElements.toArray(new String[pathElements.size()]));
  }

  /**
   * Resolves <code>".."</code> and <code>"."</code> parts in <code>filename</code> and
   * returns the result. Example:
   * <pre>
   *   Util.normalizeFilename("foo/bar/../bazz")
   * </pre>
   * yields <code>"foo/bazz"</code>.
   */

  public static String normalizeFilename (String filename)
  {
    try
      {
	Stack parts = new Stack();
	StringTokenizer tokenizer = new StringTokenizer(filename, File.separator);
	while ( tokenizer.hasMoreTokens() )
	  {
	    String part = tokenizer.nextToken();
	    if ( part.equals("..") )
	      parts.pop();
	    else if ( part.equals(".") )
	      /* do nothing */ ;
	    else
	      parts.push(part);
	  }
	StringBuffer normalizedFilename = new StringBuffer();
	Iterator iterator = parts.iterator();
	boolean first = true;
	while ( iterator.hasNext() )
	  {
	    if ( !first || filename.startsWith(File.separator) )
	      normalizedFilename.append(File.separator);
	    normalizedFilename.append((String)iterator.next());
	    first = false;
	  }
	return normalizedFilename.toString();
      }
    catch (Exception exception)
      {
	throw new IllegalArgumentException
	  ("Can not normalize filename: " + filename + ": " + exception.toString());
      }
  }

  /**
   * Returns the <code>count</code>th ancestor of <code>pathname</code>
   */

  public static String getAncestorPathname (String pathname, int count)
  {
    if ( count < 0 )
      throw new IllegalArgumentException("getAncestorPathname: negative count value: " + count);
    String ancestorName = new String(pathname);
    for (int i = 0; ( i < count ) && ( ancestorName != null ); i++)
      ancestorName = (new File(ancestorName)).getParent();
    return ancestorName;
  }

  /**
   * Returns the parent of <code>pathname</code>
   */

  public static String getParentPathname (String pathname)
  {
    return getAncestorPathname(pathname, 1);
  }

  /**
   * Returns the extension of <code>filename</code>. This is the part of
   * <code>filename</code> after the last dot.
   */

  public static String getFilenameExtension (String filename)
  {
    int pos = filename.lastIndexOf(".");
    return (pos != -1 ? filename.substring(pos) : null);
  }

  /**
   * Returns the content of <code>file</code> as a string.
   *
   * @deprecated use repsective method in {@link IOUtil} instead.
   */

  public static String getFileContent (File file)
    throws IOException
  {
    FileReader fileReader = new FileReader(file);
    char[] ioBuffer = new char[1024];
    StringBuffer content = new StringBuffer();
    int number = -1;

    while ( (number = fileReader.read(ioBuffer)) != -1 )
      {
	content.append(ioBuffer, 0, number);
      }
	
    return content.toString();
  }

  /**
   * Returns the content of the file named <code>filename</code> as a string.
   *
   * @deprecated use repsective method in {@link IOUtil} instead.
   */

  public static String getFileContent (String filename)
    throws IOException
  {
    return getFileContent(new File(filename));
  }

  /**
   * Returns the content length of the file named <code>filename</code> as <code>long</code>.
   */

  public static long getFileContentLength (String filename)
    throws IOException
  {
    return (new File(filename)).length();
  }

  /**
   * Writes <code>string</code> to <code>file</code>.
   *
   * @deprecated use repsective method in {@link IOUtil} instead.
   */

  public static void writeToFile (File file, String string)
    throws IOException
  {
    FileWriter writer = new FileWriter(file);
    writer.write(string);
    writer.flush();
    writer.close();
  }

  /**
   * Writes <code>string</code> to file <code>filename</code>.
   *
   * @deprecated use repsective method in {@link IOUtil} instead.
   */

  public static void writeToFile (String filename, String string)
    throws IOException
  {
    writeToFile(new File(filename), string);
  }

  /**
   * Returns <code>true</code> if file <code>filename</code> exists, otherwise
   * <code>false</code>. 
   */

  public static boolean fileExists (String filename)
  {
    return (new File(filename)).exists();
  }

  /**
   * <p>
   *   Copies <code>source</code> to <code>destination</code>. If <code>destination</code>
   *   is a directory, <code>source</code> is copied to a new file inside that directory;
   *   otherwise, <code>destination</code> becomes the copy of <code>source</code>. If
   *   <code>createParents</code> is <code>true</code>, parent directories of
   *   <code>destination</code> are created if necessary.
   * </p>
   */

  public static void copyFile (File source, File destination, boolean createParents)
    throws IOException
  {
    // Input stream for the source:
    FileInputStream inputStream = new FileInputStream(source);

    // Setting target file:
    File target;
    if ( destination.isDirectory() )
      {
	String path = destination.getPath();
	String name = source.getName();
	target = new File
	  (path.endsWith(File.separator) ? path + name : path + File.separator + name);
      }
    else
      target = destination;

    // Checking parents of target file:
    File parent = target.getParentFile();
    if ( ( parent != null ) && ( ! parent.exists() ) )
      {
	if ( createParents )
	  parent.mkdirs();
	else
	  throw new IOException("copyFile: Parent directory does not exist: " + target);
      } 

    // Output stream for the target:
    FileOutputStream outputStream = new FileOutputStream(target);

    // Copying:
    byte[] ioBuffer = new byte[1024];
    int number = -1;
    while ( (number = inputStream.read(ioBuffer)) != -1 )
      outputStream.write(ioBuffer, 0, number);
    outputStream.flush();

    // Cleanup:
    inputStream.close();
    outputStream.close();
  }

  /**
   * Same as {@link #copyFile(File, File, boolean) copyFile(source, destination, false)}.
   */

  public static void copyFile (File source, File destination)
    throws IOException
  {
    copyFile(source, destination, false);
  }

  /**
   * <p>
   *
   *   Copies file <code>sourceName</code> (the source) to <code>destinationName</code>. If
   *   the latter names a directory, the source is copied to a new file with the same local
   *   name inside that directory; otherwise is is copied to a new file named
   *   <code>destinationName</code>.
   * </p>
   * <p>
   *   If <code>createParents</code> is <code>true</code>, parent directories of
   *   <code>destination</code> are created if necessary.
   * </p>
   * <p>
   *   Same as {@link #copyFile(File, File, boolean) copyFile(new File(sourceName), new File(destinationName), createParents)}.
   * </p>
   */

  public static void copyFile (String sourceName, String destinationName, boolean createParents)
    throws IOException
  {
    copyFile(new File(sourceName), new File(destinationName), createParents);
  }

  /**
   * Same as {@link #copyFile(File, File, boolean) copyFile(new File(sourceName), new File(destinationName), false)}.
   */

  public static void copyFile (String sourceName, String destinationName)
    throws IOException
  {
    copyFile(new File(sourceName), new File(destinationName), false);
  }

  /**
   * Same as {@link #copyFile(File, File, boolean) copyFile(new File(sourceName), new File(destinationName), getBoolean(createParents))}.
   */

  public static void copyFile (String sourceName, String destinationName, String createParents)
    throws IOException
  {
    copyFile(new File(sourceName), new File(destinationName), getBoolean(createParents));
  }

  /**
   * Deletes <code>file</code>. If fails, throws an {@link IOException}.
   */

  public static void deleteFile (File file)
    throws IOException
  {
    if ( !file.delete() )
      throw new IOException("Failed to delete file: " + file);
  }

  /**
   * Deletes <code>file</code> provided it exists. If fails, throws an {@link IOException}.
   */

  public static void deleteFileIfExists (File file)
    throws IOException
  {
    if ( file.exists() ) deleteFile(file);
  }

  /**
   * Deletes file <code>filename</code>. If fails, throws an {@link IOException}.
   */

  public static void deleteFile (String filename)
    throws IOException
  {
    deleteFile(new File(filename));
  }

  /**
   * Deletes file <code>filename</code> provided it exists. If fails, throws an
   * {@link IOException}.
   */

  public static void deleteFileIfExists (String filename)
    throws IOException
  {
    deleteFileIfExists(new File(filename));
  }

  /**
   * Returns the local name of file <code>filename</code> resides.
   */

  public static String getLocalFilename (String filename)
  {
    return (new File(filename)).getName();
  }

  /**
   * Return <code>true</code> if <code>file1</code> is older then <code>file2</code>,
   * otherwise <code>false</code>. <code>file1</code> is older then <code>file2</code> if
   * and oly if its last modification time is earlier than the last modification time of
   * <code>file2</code>.
   */

  public static boolean fileIsOlder (File file1, File file2)
  {
    return (file1.lastModified() < file2.lastModified());
  }

  /**
   * Return <code>true</code> if <code>filename1</code> is older then
   * <code>filename2</code>, otherwise <code>false</code>. <code>filename1</code> is older
   * then <code>filename2</code> if and oly if its last modification time is earlier than the
   * last modification time of <code>filename2</code>.
   */

  public static boolean fileIsOlder (String filename1, String filename2)
  {
    return fileIsOlder(new File(filename1), new File(filename2));
  }

  /**
   * Return <code>true</code> if <code>file1</code> is newer then <code>file2</code>,
   * otherwise <code>false</code>. <code>file1</code> is newer then <code>file2</code> if
   * and oly if its last modification time is later than the last modification time of
   * <code>file2</code>.
   */

  public static boolean fileIsNewer (File file1, File file2)
  {
    return (file1.lastModified() > file2.lastModified());
  }

  /**
   * Return <code>true</code> if <code>filename1</code> is newer then
   * <code>filename2</code>, otherwise <code>false</code>. <code>filename1</code> is newer
   * then <code>filename2</code> if and oly if its last modification time is later than the
   * last modification time of <code>filename2</code>.
   */

  public static boolean fileIsNewer (String filename1, String filename2)
  {
    return fileIsNewer(new File(filename1), new File(filename2));
  }

  // ------------------------------------------------------------------------------------------
  // IO
  // ------------------------------------------------------------------------------------------

  /**
   * Creates and returns a thread that reads from stream <code>from</code> and writes to
   * stream <code>to</code>.
   *
   * @deprecated use corresponding method in {@link net.mumie.util.io.IOUtil IOUtil} instead.
   */

  public static Thread createPipeThread (final InputStream from, final OutputStream to,
					 final int bufferSize, final boolean autoFlush)
  {
    return new Thread ()
      {
	public void run ()
	{
	  byte[] ioBuffer = new byte[bufferSize];
	  int number = -1;
	  try
	    {
	      while ( (number = from.read(ioBuffer)) != -1 )
		{
		  to.write(ioBuffer, 0, number);
		  if ( autoFlush ) to.flush();
		}
	      to.flush();
	    }
	  catch (Exception exception)
	    {
	      throw new RuntimeException(exception);
	    }
	}
      };
  }

  /**
   * Creates and returns a thread that reads from stream <code>from</code> and writes to
   * stream <code>to</code>.
   *
   * @deprecated use corresponding method in {@link net.mumie.util.io.IOUtil IOUtil} instead.
   */

  public static Thread createPipeThread (final InputStream from, final OutputStream to)
  {
    return createPipeThread(from, to, IO_BUFFER_SIZE, false); 
  }

  /**
   * Creates and returns a thread that reads from stream <code>from</code> and writes to
   * stream <code>to</code>.
   *
   * @deprecated
   */

  public static Thread createPipeThread (final Reader from, final Writer to)
  {
    return new Thread ()
      {
	public void run ()
	{
	  BufferedReader reader = new BufferedReader(from);
	  String line;
	  try
	    {
	      while ( (line = reader.readLine()) != null )
		{
		  to.write(line + "\n");
		  to.flush();
		}
	    }
	  catch (Exception exception)
	    {
	      throw new RuntimeException(exception);
	    }
	}
      };
  }

  // ------------------------------------------------------------------------------------------
  // Printed representations of variables
  // ------------------------------------------------------------------------------------------

  /**
   * Returns a string notation for <code>value</code>. This is <code>value.toString()</code>
   * if <code>value</code> is not <code>null</code>, and <code>fallback</code> otherwise.
   */

  public static String getNotation (Object value, String fallback)
  {
    return (value != null ? value.toString() : fallback);
  }

  /**
   * Returns a string notation for <code>value</code>. This is <code>value.toString()</code>
   * if <code>value</code> is not <code>null</code>, and <span class="string">"none"</span>
   * otherwise. 
   */

  public static String getNotation (Object value)
  {
    return getNotation(value, "-none-");
  }

  /**
   * Returns a string notation for <code>value</code>. This is
   * <code>Integer.toString(value)</code> if <code>value</code> is not <code>-1</code>, and
   * <code>fallback</code> otherwise. 
   */

  public static String getNotation (int value, String fallback)
  {
    return (value != -1 ? Integer.toString(value) : fallback);
  }

  /**
   * Returns a string notation for <code>value</code>. This is
   * <code>Integer.toString(value)</code> if <code>value</code> is not <code>-1</code>, and
   * <span class="string">"none"</span> otherwise. 
   */

  public static String getNotation (int value)
  {
    return getNotation(value, "-none-");
  }

  /**
   * Returns a string notation for <code>value</code>. This is <code>trueNotation</code>
   * if <code>value</code> is <code>true</code>, and <code>falseNotation</code> if
   * <code>value</code> is <code>false</code>.
   */

  public static String getNotation (boolean value, String trueNotation, String falseNotation)
  {
    return (value ? trueNotation : falseNotation);
  }

  /**
   * Returns a string notation for <code>value</code>. This is
   * <span class="string">"yes"</span> if <code>value</code> is <code>true</code>, and
   * <span class="string">"no"</span> if <code>value</code> is <code>false</code>.
   */

  public static String getNotation (boolean value)
  {
    return getNotation(value, "yes", "no");
  }

  /**
   * Returns a string notation for <code>values</code>. If <code>value</code> is not <code>null</code>, this is
   * <span class="string">{<var>value<sub>1</sub></var>, ..., <var>value<sub>n</sub></var>}</span>,
   * where <var>value<sub>k</sub></var> is <code>values[<var>k</var>].toString()</code>. Otherwise, this is
   * <code>fallback</code>.
   */

  public static String getNotation (Object[] values, String fallback)
  {
    if ( values != null )
      {
	StringBuffer notation = new StringBuffer("{");
	for (int i = 0; i < values.length; i++)
	  notation.append(i > 0 ? ", " : "").append(values[i].toString());
	return notation.append('}').toString();
      }
    else
      return fallback;
  }

  /**
   * Returns a string notation for <code>values</code>. If <code>value</code> is not
   * <code>null</code>, this is <span class="string">{<var>value<sub>1</sub></var>, ...,
   * <var>value<sub>n</sub></var></span>, where <var>value<sub>k</sub></var> is
   * <code>values[<var>k</var>].toString()</code>. Otherwise, this is <span
   * class="string">"-none-"</span>.
   */

  public static String getNotation (Object[] values)
  {
    return getNotation(values, "-none-");
  }

  /**
   * Auxiliary method to get a boolean from a string value. Returns <code>true</code> if
   * <code>value</code> is <span class="string">"true"</span>, <span
   * class="string">"yes"</span>, or <span class="string">"on"</span>. Returns
   * <code>false</code>  if <code>value</code> is <span class="string">"false"</span>, <span
   * class="string">"no"</span> or <span class="string">"off"</span>. Case is ignored.
   *
   * @throws IllegalArgumentException if <code>value</code> is not one of the strings
   *                                  listed above.
   */

  // ------------------------------------------------------------------------------------------
  // Misc.
  // ------------------------------------------------------------------------------------------

  /**
   * Returns <code>true</code> if <code>value</code> is in
   * {@link #TRUE_IDENTIFIERS TRUE_IDENTIFIERS} and <code>false</code> if <code>value</code> is in
   * {@link #FALSE_IDENTIFIERS FALSE_IDENTIFIERS}. Case does not matter.
   */

  public static boolean getBoolean (String value)
  {
    for (int i = 0; i < TRUE_IDENTIFIERS.length; i++)
      {
	if ( stringsAreEqualCaseIgnored(value,  TRUE_IDENTIFIERS[i]) )
	  return true;
      }
    for (int i = 0; i < FALSE_IDENTIFIERS.length; i++)
      {
	if ( stringsAreEqualCaseIgnored(value,  FALSE_IDENTIFIERS[i]) )
	  return false;
      }
    throw new IllegalArgumentException("Not a boolean-describing value: " + value);
  }

  /**
   * Adds an entry to a <code>Map</code> . <code>keyValuePair</code> must be of the form
   * <var>key</var><code>=</code><var>value</var>. 
   */

  public static void put (Map map, String keyValuePair)
  {
    int posOfSep = keyValuePair.indexOf('=');
    if ( posOfSep == -1 )
      throw new IllegalArgumentException("Invalid keu-value specification: " + keyValuePair);
    String key = keyValuePair.substring(0, posOfSep);
    String value = keyValuePair.substring(posOfSep+1);
    map.put(key, value);
  }

  // ------------------------------------------------------------------------------------------
  // Static initialization
  // ------------------------------------------------------------------------------------------

  static
  {
    CLASS_NAME = Util.class.getName();
  }
}
