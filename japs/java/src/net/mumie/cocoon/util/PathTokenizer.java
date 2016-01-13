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

import java.io.File;

/**
 * Splits checkin paths into its parts.
 *
 * <p>
 *   General usage:<pre>
 *     PathTokenizer tokenizer = new PathTokenizer();
 *
 *     // ...
 *
 *     tokenizer.tokenize(path);
 *     String pureName = tokenizer.getPureName();
 *     String suffixes = tokenizer.getSuffixes();
 *
 *     // ...
 *
 *     tokenizer.tokenize(anotherPath);
 *     String pureName2 = tokenizer.getPureName();
 *     String suffixes2 = tokenizer.getSuffixes();</pre>
 *
 *   Note that you can use a single instance repeatedly. The {@link #tokenize tokenize}
 *   method parses the path and also resets the instance.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: PathTokenizer.java,v 1.7 2009/12/11 09:20:06 rassy Exp $</code>
 */

public class PathTokenizer
{
  /**
   * Separator for path items.
   */

  public static final String PATH_ITEM_SEPARATOR = "/";

  /**
   * The prefix keyword found in the last {@link #tokenize tokenize} call.
   */

  protected String prefixKeyword = null;

  /**
   * The section path found in the last {@link #tokenize tokenize} call.
   */

  protected String sectionPath = null;

  /**
   * The pure name found in the last {@link #tokenize tokenize} call.
   */

  protected String pureName = null;

  /**
   * The role suffix found in the last {@link #tokenize tokenize} call.
   */

  protected String roleSuffix = null;

  /**
   * The type suffix found in the last {@link #tokenize tokenize} call.
   */

  protected String typeSuffix = null;

  /**
   * The suffixes found in the last {@link #tokenize tokenize} call.
   */

  protected String suffixes = null;

  /**
   * The path without the prefix keyword.
   */

  protected String pathWithoutPrefixKeyword = null;

  /**
   * The path without the suffixes.
   */

  protected String pathWithoutSuffixes = null;

  /**
   * Auxiliary method, returns the specified string if its length is >0, otherwise
   * <code>null</code>.
   */

  protected static String strOrNull (String string)
  {
    return (string.length() > 0 ? string : null);
  }

  /**
   * Tokenizes the specified path.
   */

  public void tokenize (String path)
  {
    String restPath = path;

    // Remove trailing file separator:
    if ( restPath.endsWith(PATH_ITEM_SEPARATOR) )
      restPath = restPath.substring(0, restPath.length() - PATH_ITEM_SEPARATOR.length());

    // Strip prefix keyword:
    int colon = restPath.indexOf(':');
    if ( colon == -1 )
      {
        this.prefixKeyword = null;
      }
    else
      {
        this.prefixKeyword = strOrNull(restPath.substring(0, colon+1));
        restPath = restPath.substring(colon+1);
      }

    this.pathWithoutPrefixKeyword = strOrNull(restPath);

    // Strip section path:
    int lastFileSep = restPath.lastIndexOf(PATH_ITEM_SEPARATOR);
    if ( lastFileSep == -1 )
      {
        this.sectionPath = null;
      }
    else
      {
        this.sectionPath = strOrNull(restPath.substring(0, lastFileSep));
        restPath = restPath.substring(lastFileSep+1);
      }

    // Parse the remaining path:
    int lastDot =  restPath.lastIndexOf('.');
    int nextToLastDot = (lastDot > 0 ? restPath.lastIndexOf('.', lastDot-1) : -1);
    if ( lastDot == -1 && nextToLastDot == -1 )
      {
        this.pureName = strOrNull(restPath);
        this.roleSuffix = null;
        this.typeSuffix = null;
        this.suffixes = null;
      }
    else if ( lastDot != -1 && nextToLastDot == -1 )
      {
        this.pureName = strOrNull(restPath.substring(0, lastDot));
        this.roleSuffix = strOrNull(restPath.substring(lastDot+1));
        this.typeSuffix = null;
        this.suffixes = this.roleSuffix;
      }
    else if ( lastDot != -1 && nextToLastDot != -1 )
      {
        this.pureName = strOrNull(restPath.substring(0, nextToLastDot));
        this.roleSuffix = strOrNull(restPath.substring(nextToLastDot+1, lastDot));
        this.typeSuffix = strOrNull(restPath.substring(lastDot+1));
        this.suffixes = strOrNull(restPath.substring(nextToLastDot+1));
      }
    else
      throw new IllegalArgumentException("Invalid path: " + path);


    this.pathWithoutSuffixes =
      (this.suffixes != null
       ? strOrNull(path.substring(0, path.length() - this.suffixes.length() - 1))
       : path);
  }

  /**
   * Returns the prefic keyword found in the last {@link #tokenize tokenize} call.
   */

  public String getPrefixKeyword ()
  {
    return this.prefixKeyword;
  }

  /**
   * Returns the section path found in the last {@link #tokenize tokenize} call.
   */

  public String getSectionPath ()
  {
    return this.sectionPath;
  }

  /**
   * Returns the pure name found in the last {@link #tokenize tokenize} call.
   */

  public String getPureName ()
  {
    return this.pureName;
  }

  /**
   * Returns the role suffix found in the last {@link #tokenize tokenize} call.
   */

  public String getRoleSuffix ()
  {
    return this.roleSuffix;
  }

  /**
   * Returns the type suffix found in the last {@link #tokenize tokenize} call.
   */

  public String getTypeSuffix ()
  {
    return this.typeSuffix;
  }

  /**
   * Returns the suffixes found in the last {@link #tokenize tokenize} call.
   */

  public String getSuffixes ()
  {
    return this.suffixes;
  }

  /**
   * Returns the path without the prefix keyword.
   * 
   */

  public String getPathWithoutPrefixKeyword ()
  {
    return this.pathWithoutPrefixKeyword;
  }

  /**
   * Returns the path without the prefix keyword.
   */

  public String getPathWithoutSuffixes ()
  {
    return this.pathWithoutSuffixes;
  }

  /**
   * Turns backslashes and slashes into {@link #PATH_ITEM_SEPARATOR PATH_ITEM_SEPARATOR}s.
   */

  public static String normalizeSeparators (String path)
  {
    StringBuilder buffer = new StringBuilder();
    for (char c : path.toCharArray())
      {
        if ( c == '\\' || c == '/' )
          buffer.append(PATH_ITEM_SEPARATOR);
        else
          buffer.append(c);
      }
    return buffer.toString();
  }

}
