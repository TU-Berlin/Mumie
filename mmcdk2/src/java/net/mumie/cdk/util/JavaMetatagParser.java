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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.mumie.util.XMLCharacters;
import net.mumie.util.io.IOUtil;

/**
 * A parser for extracting metainfos from Java source files. With a few exceptions,
 * metainfos are specified as Javadoc tags. See the respective specification for
 * details. The central method is {@link #parse parse}, which parses a source file and
 * returns the metainfos as a {@link JavaMetatags JavaMetatags} object.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: JavaMetatagParser.java,v 1.7 2009/11/10 14:53:27 linges Exp $</code>
 */

public class JavaMetatagParser
{
  /**
   * Pattern to parse the Java source.
   */

  protected Pattern parseSourcePattern = null;

  /**
   * Pattern to strip the asterisks and slashes that start/terminate a (javadoc) comment, as
   * well as asteriks at the beginning of each line.
   */

  protected Pattern stripCommentPattern = null;

  /**
   * Pattern to parse a Javadoc comment to get the tags from it.
   */

  protected Pattern parseCommentPattern = null;

  /**
   * Adds the entry with the specified name and value to the specified result map.
   */

  protected void addEntry (String name, String value, Map<String, List<String>> result)
    throws JavaMetatagParseException
  {
    try
      {
        value = XMLCharacters.entitiesToNumcodes(value.trim());
        if ( !result.containsKey(name) )
          result.put(name, new ArrayList<String>());
        result.get(name).add(value);
      }
    catch (Exception exception)
      {
        throw new JavaMetatagParseException(exception);
      }
  }

  /**
   * Strips the asterisks and slashes that start/terminate a (javadoc) comment, as
   * well as asteriks at the beginning of each line.
   */

  protected String stripComment (String comment)
  {
    if ( this.stripCommentPattern == null )
      this.stripCommentPattern = Pattern.compile
	(
	 "\\A/\\*\\*[ \t]*|"  + // javadoc comment starter
	 "[ \t]*\\*/\\z|"     + // javadoc comment terminator
	 "^[ \t]*\\*[ \t]*",    // asterisk at beginning of line
	 Pattern.MULTILINE
	 );
    return this.stripCommentPattern.matcher(comment).replaceAll("").trim();
  }

  /**
   * Parses the specified comment and adds the tags found to the specified result.
   */

  protected void parseComment (String comment, Map<String, List<String>> result)
    throws JavaMetatagParseException
  {
    try
      {
        if ( this.parseCommentPattern == null )
          this.parseCommentPattern = Pattern.compile
            (
             "\\G(?:"
             + "^(@[a-zA-z0-9.]+)|" // 1 | tag name
             + "(@)|"               // 2 | "@" character
             + "([^@]+)"            // 3 | non-"@" characters
             + ")",
             Pattern.MULTILINE
             );
        final int TAG_NAME = 1;
        final int AT_CHAR = 2;
        final int NON_AT_CHARS = 3;
        final int NUM_GROUPS = 3;
        Matcher matcher = this.parseCommentPattern.matcher(comment);
        String tagName = null;
        StringBuffer tagValue = null;
        while ( matcher.find() )
          {
            String group = null;
            int groupType = 0;
            while ( group == null && groupType <= NUM_GROUPS )
              group = matcher.group(++groupType);
            switch ( groupType )
              {
              case TAG_NAME:
                if ( tagName != null )
                  {
                    this.addEntry(tagName, tagValue.toString(), result);
                    tagName = null;
                    tagValue = null;
                  }
                tagName = group;
                tagValue = new StringBuffer();
                break;
              default:
                if ( tagName != null ) tagValue.append(group);
              }
          }
        if ( tagName != null )
          this.addEntry(tagName, tagValue.toString(), result);
      }
    catch (Exception exception)
      {
        throw new JavaMetatagParseException(exception);
      }
  }

  /**
   * Parses the specified Java source file and returns the metatags found in it.
   */

  public JavaMetatags parse (File sourceFile)
    throws JavaMetatagParseException, IOException 
  {
    String source = IOUtil.readFile(sourceFile);
    if ( this.parseSourcePattern == null )
      {
        this.parseSourcePattern = Pattern.compile
          (
            "\\G(?:"
            + "(\\s+)|"                            // 1 | whitespaces
            + "package\\s+([a-zA-Z0-9.]+);|"       // 2 | package name
            + "(import\\s+[a-zA-Z0-9.*]+;)|"        // 3 | import declaration
            + "(//.*?$)|"                          // 4 | one-line comment
            + "(/\\*\\*.*?\\*/)|"                  // 5 | javadoc comment
            + "(/\\*.*?\\*/)|"                     // 6 | multiline comment
            + "public\\s+class\\s+([a-zA-Z0-9_]+)" // 7 | start of code
            + ")",
            Pattern.DOTALL
          );
      }

    final int WHITESPACE = 1;
    final int PACKAGE_NAME = 2;
    final int IMPORT_DECLARATION = 3;
    final int SINGLE_LINE_COMMENT = 4;
    final int JAVADOC_COMMENT = 5;
    final int MULTILINE_COMMENT = 6;
    final int CLASS_START = 7;

    final int NUM_GROUPS = 7;

    Matcher matcher = this.parseSourcePattern.matcher(source);
    int pos = 0;
    String classDoc = null;
    String packageName = null;
    String shortName = null;
    
    while ( ( shortName == null ) && matcher.find() )
      {
        String all = matcher.group(0);
        if ( all == null )
          throw new JavaMetatagParseException
            (sourceFile + ", position " + pos + ": Can not identify token");
        String group = null;
        int groupType = 0;
        while ( group == null && groupType <= NUM_GROUPS )
          group = matcher.group(++groupType);
        pos = matcher.start(0);
        switch ( groupType )
          {
          case PACKAGE_NAME: packageName = group; break;
          case JAVADOC_COMMENT: classDoc = this.stripComment(group); break;
          case CLASS_START: shortName = group; break;
          }
      }

    if ( classDoc == null )
      throw new JavaMetatagParseException("No class documentation found");
    if ( shortName == null )
      throw new JavaMetatagParseException("No class name found");

    String qualifiedName = (packageName != null ? packageName + "." + shortName : shortName);

    Map<String, List<String>> result = new HashMap<String, List<String>>();
    this.addEntry(JavaMetatag.FALLBACK_NAME, shortName, result);
    this.addEntry(JavaMetatag.QUALIFIED_NAME, qualifiedName, result);
    this.parseComment(classDoc, result);

    return new JavaMetatags(result);
  }
}
