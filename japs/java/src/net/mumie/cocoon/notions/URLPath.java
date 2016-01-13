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

package net.mumie.cocoon.notions;

/**
 * <p>
 *   Provides static helper methods to compose URL's for documents.
 * </p>
 * <p>
 *   A Mumie document URL has the form
 *   <pre>
 *     <var>accessModifier</var>/<var>[modifier</var>/<var>]</var>document/<var>docTypeName</var>/id/<var>id</var>
 *   </pre>
 *   or
 *   <pre>
 *     <var>accessModifier</var>/<var>[modifier</var>/<var>]</var>document/<var>docTypeName</var>/qname/<var>qualifiedName</var>
 *   </pre>
 *   usually prepended by a prefix containing the protocoll, the host, perhaps the port, and
 *   usually and a path prefix (e.g., <code>cocoon</code>). The first URL is called the <em>id
 *   form</em>, the second the <em>qualified name form</em>. The placeholders
 *   have the following meaning: 
 * </p>
 * <p>
 * <table class="genuine indented">
 *   <thead>
 *     <tr>
 *       <td>Placeholder</td>
 *       <td>Meaning</td>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td><var>accessModifier</var></td>
 *       <td>May be <code>protected</code> or <code>public</code>. The first means only
 *       logged-in users can see the document, the latter means all users can see the
 *       document. Default is <code>protected</code>.</td>
 *     </tr>
 *     <tr>
 *       <td><var>modifier</var></td>
 *       <td>Optional keyword to retrieve special kinds of output. E.g.,
 *       <code>info-raw</code> can stand for the raw XML form of the document in use
 *       mode <code>info</code>. It's up to the sitemap to interpret the modifier
 *       correctly. If not set, the rendered form of the document in use mode
 *       <code>serve</code> is retrieved. Not det by default.</td>
 *     </tr>
 *     <tr>
 *       <td><var>docTypeName</var></td>
 *       <td>The document type, as string name
 *           (cf. {@link net.mumie.cocoon.notions.DocType DocType}).</td>
 *     </tr>
 *     <tr>
 *       <td><var>id</var></td>
 *       <td>The id of the document</td>
 *     </tr>
 *     <tr>
 *       <td><var>qualifiedName</var></td>
 *       <td>The qualified name of the document.</td>
 *     </tr>
 *   </tbody>
 * </table>
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: URLPath.java,v 1.5 2007/07/11 15:38:47 grudzin Exp $</code>
 */

public class URLPath
{
  /**
   * Returns the (relative) URL of a document.
   *
   * @param accessModifier the access modifier. See above table, entry
   * <var>accessModifier</var>. May also by <code>null</code> to indicate that the default
   * is to be used. 
   * @param modifier the modifier. See above table, entry <var>modifier</var>. May also be
   * <code>null</code> to indicate that no modifier is to be set.
   * @param docTypeName the document type, as string name.
   * @param idOrQualifiedName contains the id or the qualified name.
   * @param byId whether the id form or the qulified name form of the URL is
   * composed. <code>true</code> means the first option, <code>false</code> the second. 
   */

  public static String forDoc (String accessModifier, String modifier, String docTypeName,
			String idOrQualifiedName, boolean byId)
  {
    StringBuffer path = new StringBuffer();
    path.append(accessModifier != null ? accessModifier : "protected").append('/');
    if ( modifier != null ) path.append(modifier).append("/");
    path
      .append("document/")
      .append(docTypeName)
      .append(byId ? "/id/" : "/qname/")
      .append(idOrQualifiedName);
    return path.toString();
  }

  /**
   * Returns the (relative) URL of a document in the id form.
   *
   * @param accessModifier the access modifier. See above table, entry <var>accessModifier</var>. May also by <code>null</code> to indicate that the default is to be used.
   * @param modifier the modifier. See above table, entry <var>modifier</var>. May also be <code>null</code> to indicate that no modifier is to be set.
   * @param docTypeName the document type, as string name.
   * @param id the id of the document.
   */

  public static String forDoc (String accessModifier, String modifier, String docTypeName, String id)
  {
    return URLPath.forDoc(accessModifier, modifier, docTypeName, id, true);
  }

  /**
   * Returns the (relative) URL of a document in the id form with access modifier
   * <code>protected</code>. For the latter term, see above table, entry
   * <var>accessModifier</var>. 
   *
   * @param modifier the modifier. See above table, entry <var>modifier</var>. May also be <code>null</code> to indicate that no modifier is to be set.
   * @param docTypeName the document type, as string name.
   * @param id the id of the document.
   */

  public static String forDoc (String modifier, String docTypeName, String id)
  {
    return URLPath.forDoc(null, modifier, docTypeName, id);
  }

  /**
   * Returns the (relative) URL of a document in the id form with access modifier
   * <code>protected</code> and no modifier. For the latter term, see above table, entries
   * <var>accessModifier</var> and <var>modifier</var>. 
   *
   * @param docTypeName the document type, as string name.
   * @param id the id of the document.
   */

  public static String forDoc (String docTypeName, String id)
  {
    return URLPath.forDoc(null, null, docTypeName, id);
  }

  /**
   * Returns the (relative) URL of the document specified by
   * <code>accessModifier</code>, <code>modifier</code>, <code>docTypeName</code>, and
   * <code>id</code>.
   */

  public static String forDoc (String accessModifier, String modifier, String docTypeName,
			int id)
  {
    return URLPath.forDoc(accessModifier, modifier, docTypeName, Integer.toString(id), true);
  }

  /**
   * Returns the (relative) URL of the document specified by
   * <code>modifier</code>, <code>docTypeName</code>, and
   * <code>id</code>.
   */

  public static String forDoc (String modifier, String docTypeName, int id)
  {
    return URLPath.forDoc(null, modifier, docTypeName, id);
  }

  /**
   * Returns the (relative) URL of the document specified by
   * <code>docTypeName</code> and <code>id</code>.
   */

  public static String forDoc (String docTypeName, int id)
  {
    return URLPath.forDoc(null, null, docTypeName, id);
  }

  /**
   * Returns the (relative) URL of the document specified by
   * <code>accessModifier</code>, <code>modifier</code>, <code>docTypeName</code>, and
   * <code>qualifiedName</code>.
   */

  public static String forDocByQualifiedName (String accessModifier, String modifier,
					      String docTypeName, String qualifiedName)
  {
    return URLPath.forDoc(accessModifier, modifier, docTypeName, qualifiedName, false);
  }

  /**
   * Returns the (relative) URL of the document specified by
   * <code>modifier</code>, <code>docTypeName</code>, and
   * <code>qualifiedName</code>.
   */

  public static String forDocByQualifiedName (String modifier, String docTypeName,
					      String qualifiedName)
  {
    return URLPath.forDocByQualifiedName(null, modifier, docTypeName, qualifiedName);
  }

  /**
   * Returns the (relative) URL of the document specified by
   * <code>docTypeName</code> and <code>qualifiedName</code>.
   */

  public static String forDocByQualifiedName (String docTypeName, String qualifiedName)
  {
    return URLPath.forDocByQualifiedName(null, null, docTypeName, qualifiedName);
  }

  /**
   * Returns the (relative) URL of the document specified by
   * <code>accessModifier</code>, <code>modifier</code>, <code>docType</code>, and
   * <code>id</code>.
   */

  public static String forDoc (String accessModifier, String modifier, int docType, String id)
  {
    return URLPath.forDoc(accessModifier, modifier, DocType.nameFor(docType), id, true);
  }

  /**
   * Returns the (relative) URL of the document specified by
   * <code>modifier</code>, <code>docType</code>, and
   * <code>id</code>.
   */

  public static String forDoc (String modifier, int docType, String id)
  {
    return URLPath.forDoc(null, modifier, docType, id);
  }

  /**
   * Returns the (relative) URL of the document specified by
   * <code>docType</code> and <code>id</code>.
   */

  public static String forDoc (int docType, String id)
  {
    return URLPath.forDoc(null, null, docType, id);
  }

  /**
   * Returns the (relative) URL of the document specified by
   * <code>accessModifier</code>, <code>modifier</code>, <code>docType</code>, and
   * <code>id</code>.
   */

  public static String forDoc (String accessModifier, String modifier, int docType, int id)
  {
    return URLPath.forDoc(accessModifier, modifier, DocType.nameFor(docType), id);
  }

  /**
   * Returns the (relative) URL of the document specified by
   * <code>modifier</code>, <code>docType</code>, and
   * <code>id</code>.
   */

  public static String forDoc (String modifier, int docType, int id)
  {
    return URLPath.forDoc(null, modifier, docType, id);
  }

  /**
   * Returns the (relative) URL of the document specified by
   * <code>docType</code> and <code>id</code>.
   */

  public static String forDoc (int docType, int id)
  {
    return URLPath.forDoc(null, null, docType, id);
  }

}
