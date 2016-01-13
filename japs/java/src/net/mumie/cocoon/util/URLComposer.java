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

/**
 * Helper class to compose Japs URLs.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * <code>$Id: URLComposer.java,v 1.6 2007/07/11 15:38:54 grudzin Exp $</code>
 */

public class URLComposer
{
  /**
   * The mode <code>"view"</code> as a constant.
   */

  public static final String MODE_VIEW = "view";

  /**
   * The mode <code>"info"</code> as a constant.
   */

  public static final String MODE_INFO = "info";

  /**
   * The mode <code>"info-raw"</code> as a constant.
   */

  public static final String MODE_INFO_RAW = "info-raw";

  /**
   * The mode <code>"info-course-browser"</code> as a constant.
   */

  public static final String MODE_INFO_COURSE_BROWSER = "info-course-browser";

  /**
   * The mode <code>"checkout"</code> as a constant.
   */

  public static final String MODE_CHECKOUT = "checkout";

  /**
   * The mode <code>"mutipage"</code> as a constant.
   */

  public static final String MODE_MUTIPAGE = "mutipage";

  /**
   * The mode <code>"content"</code> as a constant.
   */

  public static final String MODE_CONTENT = "content";

  /**
   * Returns the (relative) URL for the document with type <code>type</code> (numerical
   * code) and id <code>id</code> in mode <code>mode</code>.
   */

  public static String composeURLforDocument (String mode, int type, int id)
  {
    return "protected/" + mode + "/document/type-code/" + type + "/id/" + id;
  }

  /**
   * Returns the (relative) URL for the document with type <code>type</code> and id
   * <code>id</code> with modifier {@link #MODE_VIEW}.
   */

  public static String composeURLforDocument (int type, int id)
  {
    return composeURLforDocument(MODE_VIEW, type, id);
  }

  /**
   * Returns the (relative) URL for the document with type <code>typeName</code> (string
   * name) and id <code>id</code> in mode <code>mode</code>.
   */

  public static String composeURLforDocument (String mode, String typeName, int id)
  {
    return "protected/" + mode + "/document/type-name/" + typeName + "/id/" + id;
  }

  /**
   * Returns the (relative) URL for the document with type <code>typeName</code> (string
   * name) and id <code>id</code> in mode {@link #MODE_VIEW}.
   */

  public static String composeURLforDocument (String typeName, int id)
  {
    return composeURLforDocument(MODE_VIEW, typeName, id);
  }

  /**
   * Returns the (relative) URL for the document with type <code>typeName</code> (string
   * name) and id qualified name <code>qualifiedName</code> in mode <code>mode</code>.
   */

  public static String composeURLforDocument (String mode, String typeName, String qualifiedName)
  {
    return "protected/" + mode + "/document/type-name/" + typeName + "/qname/" + qualifiedName;
  }

  /**
   * Returns the (relative) URL for the document with type <code>typeName</code> (string
   * name) and id qualified name <code>qualifiedName</code> in mode {@link #MODE_VIEW}.
   */

  public static String composeURLforDocument (String typeName, String qualifiedName)
  {
    return composeURLforDocument(MODE_VIEW, typeName, qualifiedName);
  }

  /**
   * Returns the (relative) URL for the document with type <code>type</code> (numerical
   * code) and qualified name <code>qualifiedName</code> in mode <code>mode</code>.
   */

  public static String composeURLforDocument (String mode, int type, String qualifiedName)
  {
    return "protected/" + mode + "/document/type-code/" + type + "/qname/" + qualifiedName;
  }

  /**
   * Returns the (relative) URL for the document with type <code>type</code> (numerical
   * code) and qualified name <code>qualifiedName</code> in mode{@link #MODE_VIEW}.
   */

  public static String composeURLforDocument (int type, String qualifiedName)
  {
    return composeURLforDocument(MODE_VIEW, type, qualifiedName);
  }
}
