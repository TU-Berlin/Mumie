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

package net.mumie.cdk;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import net.mumie.cdk.util.CdkJapsClient;
import net.mumie.cdk.util.GenericDocumentResolver;
import net.mumie.cdk.util.MasterCache;
import net.mumie.cdk.util.MasterCacheException;
import net.mumie.cocoon.checkin.GDIMEntry;
import net.mumie.cocoon.checkin.Master;
import net.mumie.cocoon.checkin.MasterException;
import net.mumie.cocoon.notions.DataFormat;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.FileRole;
import net.mumie.cocoon.notions.LangCode;
import net.mumie.cocoon.notions.MediaType;
import net.mumie.cocoon.notions.Nature;
import net.mumie.cocoon.util.PathTokenizer;
import net.mumie.util.StringUtil;
import net.mumie.util.io.CachedDirEntries;

public class CdkHelper
{
  // ------------------------------------------------------------------------------------------
  // Global constants and variables
  // ------------------------------------------------------------------------------------------

  /**
   * Full suffix of master files (<code>".meta.xml"</code>).
   */

  public static final String MASTER_SUFFIX =
    FileRole.MASTER_SUFFIX + "." + MediaType.TEXT_XML_SUFFIX;

  /**
   * Path of the default theme (<code>"system/themes/thm_default.meta.xml"</code>).
   */

  public static final String DEFAULT_THEME_PATH =
    "system/themes/thm_default." + MASTER_SUFFIX;

  /**
   * The checkin root directory.
   */

  protected String checkinRoot;

  /**
   * The master cache.
   */

  protected MasterCache masterCache;

  /**
   * The cached directory entries.
   */

  protected CachedDirEntries cachedDirEntries;

  /**
   * The generic document resolver.
   */

  protected GenericDocumentResolver genericDocumentResolver;

  /**
   * Holds the Japs clients for the different hosts.
   */

  protected Map<String,CdkJapsClient> japsClients =
    Collections.synchronizedMap(new HashMap<String,CdkJapsClient>());

  /**
   * A static <code>CdkHelper</code> instance.
   */

  protected static CdkHelper sharedInstance = null;

  // ------------------------------------------------------------------------------------------
  // Get the checkin root
  // ------------------------------------------------------------------------------------------

  /**
   * Returns the checkin root directory.
   */

  public String getCheckinRoot ()
  {
    return this.checkinRoot;
  }

  // ------------------------------------------------------------------------------------------
  // Get theme and language
  // ------------------------------------------------------------------------------------------

  /**
   * Returns the path of the theme for which the previews are created.
   */

  public String getThemePath ()
  {
    return this.normalizeThemePath(System.getProperty(CdkConfigParam.THEME_PATH));
  }

  /**
   * Retruns the code of the language for which the previews are created.
   */

  public String getLangCode ()
  {
    return this.normalizeLangCode(System.getProperty(CdkConfigParam.LANG_CODE));
  }

  /**
   * 
   */

  public String normalizeThemePath (String themePath)
  {
    if ( themePath == null || themePath.equals("") )
      themePath = DEFAULT_THEME_PATH;
    return themePath;
  }

  /**
   * 
   */

  public String normalizeLangCode (String langCode)
  {
    if ( langCode == null || langCode.equals("") )
      langCode = LangCode.DEFAULT;
    return langCode;
  }

  // ------------------------------------------------------------------------------------------
  // Language codes
  // ------------------------------------------------------------------------------------------

  /**
   * Returns hte language codes known to the system. The language codes are read from the
   * system property {@link {CdkConfigParam#LANG_CODES CdkConfigParam.LANG_CODES}.
   */

  public String[] getLangCodes ()
  {
    StringTokenizer tokenizer =
      new StringTokenizer(System.getProperty(CdkConfigParam.LANG_CODES, ""), ", \t\n\r\f");

    String[] langCodes = new String[tokenizer.countTokens()];
    int i = -1;
    while ( tokenizer.hasMoreTokens() )
      langCodes[++i] = tokenizer.nextToken();

    return langCodes;
  }

  /**
   * Strips the language code from a name (usually a filename). If the specified string ends
   * with <code>"_" + <var>code</var></code>, where <code><var>code</var></code> is one of
   * the language codes as returned by {@link #getLangCodes getLangCodes}, <code>"_" +
   * <var>code</var></code> is removed and the resulting string is returned. Otherwise, the
   * string is returned unchanged.
   */

  public String stripLangCode (String name)
  {
    for (String langCode : this.getLangCodes())
      {
        if ( name.endsWith("_" + langCode) )
          return name.substring(0, name.length() - langCode.length() - 1);
      }
    return name;
  }

  // ------------------------------------------------------------------------------------------
  // Filename checks
  // ------------------------------------------------------------------------------------------

  /**
   * Returns <code>true</code> if the specified filename is a master filename, otherwise
   * <code>false</code>.
   */

  public boolean isMasterFilename (String filename)
  {
    return filename.endsWith("." + MASTER_SUFFIX);
  }

  /**
   * Auxiliary method. Checks if the role suffix of the specified filename is equal to the
   * specified role suffix.
   */

  protected boolean hasRoleSuffix (String filename, String roleSuffix)
  {
    // Get last dot:
    int lastDot = filename.lastIndexOf('.');

    // If no last dot found, return false:
    if ( lastDot == -1 )
      return false;

    // If last dot is the only character, return false:
    if ( lastDot == 0 )
      return false;

    // Get next-to-last dot:
    int nextToLastDot = filename.lastIndexOf('.', lastDot-1);


    // If no next-to-last dot found, return false:
    if ( nextToLastDot == -1 )
      return false;

    // Check if substring from next-to-last to last dot equals role suffix:
    return filename.substring(nextToLastDot+1, lastDot).equals(roleSuffix);
  }

  /**
   * Returns <code>true</code> if the specified filename is a content filename, otherwise
   * <code>false</code>.
   */

  public boolean isContentFilename (String filename)
  {
    return this.hasRoleSuffix(filename, FileRole.CONTENT_SUFFIX);
  }

  // ------------------------------------------------------------------------------------------
  // Filename conversions
  // ------------------------------------------------------------------------------------------

  /**
   * Converts the specified absolute filename to the corresponding checkin path.
   */

  public String absFilenameToPath (String absFilename)
    throws IllegalArgumentException
  {
    if ( absFilename.equals(checkinRoot) )
      return "";
    else if ( absFilename.startsWith(checkinRoot + File.separator) )
      return absFilename.substring(checkinRoot.length()+1);
    else
      throw new IllegalArgumentException
        ("Filename does not start with checkin root: " + absFilename);
  }

  /**
   * Converts the specified checkin path to the corresponding absolute filename.
   */

  public String pathToAbsFilename (String path)
  {
    // Remove leading separator (on Unix: "/"), if any:
    if ( path.startsWith(File.separator) )
      path = path.substring(File.separator.length());
    return this.checkinRoot + File.separator + path;
  }

  /**
   * Returns the content path for the specified master path, or null if the master path
   * represents a pseudo-document or generic document.
   */

  public String masterToContentPath (String masterPath)
    throws IllegalArgumentException, MasterException, MasterCacheException 
  {
    if ( !this.isMasterFilename(masterPath) )
      throw new IllegalArgumentException("Not a master path: " + masterPath);
    File masterFile = new File(this.pathToAbsFilename(masterPath));
    Master master = this.masterCache.getMaster(masterFile);

    if ( master.getNature() != Nature.DOCUMENT || DocType.isGeneric(master.getType()) )
      return null;
    String contentSuffix =
      FileRole.CONTENT_SUFFIX + "." +  this.getContentTypeSuffix(master);
    return StringUtil.replaceSuffix(masterPath, MASTER_SUFFIX, contentSuffix);
  }

  /**
   * Returns the preview path for the specified master path, or null if the master path
   * represents a pseudo-document or generic document.
   */

  public String masterToPreviewPath (String masterPath)
    throws IllegalArgumentException, MasterException, MasterCacheException
  {
    if ( !this.isMasterFilename(masterPath) )
      throw new IllegalArgumentException("Not a master path: " + masterPath);
    File masterFile = new File(this.pathToAbsFilename(masterPath));
    Master master = this.masterCache.getMaster(masterFile);
    if ( master.getNature() != Nature.DOCUMENT || DocType.isGeneric(master.getType()) )
      return null;
    String roleSuffix = 
      (DataFormat.ofDocType(master.getType()) == DataFormat.TEXT
       ? FileRole.PREVIEW_SUFFIX
       : FileRole.CONTENT_SUFFIX);
    String previewSuffix = roleSuffix + "." + this.getPreviewTypeSuffix(master);
    return StringUtil.replaceSuffix(masterPath, MASTER_SUFFIX, previewSuffix);
  }

  /**
   * Returns the source path for the specified master path, or null if the master path
   * represents a pseudo-document or generic document or if no source suffix is defined for
   * the corresponding document type.
   */

  public String masterToSourcePath (String masterPath)
    throws IllegalArgumentException, MasterException, MasterCacheException 
  {
    if ( !this.isMasterFilename(masterPath) )
      throw new IllegalArgumentException("Not a master path: " + masterPath);
    File masterFile = new File(this.pathToAbsFilename(masterPath));
    Master master = this.masterCache.getMaster(masterFile);
    if ( master.getNature() != Nature.DOCUMENT || DocType.isGeneric(master.getType()) )
      return null;
    String sourceTypeSuffix = this.getSourceTypeSuffix(master);
    if ( sourceTypeSuffix != null )
      {
        String purePath =
          masterPath.substring(0, masterPath.length() - MASTER_SUFFIX.length() - 1);
        purePath = this.stripLangCode(purePath);
        String sourceSuffix = FileRole.SOURCE_SUFFIX + "." +  sourceTypeSuffix;
        return purePath + "." + sourceSuffix;
      }
    else
      return null;
  }

  /**
   * Auxiliary method, returns the type suffix of the preview file corresponding to the
   * specified master. If the system property
   * {@link CdkConfigParam#PREVIEW_TYPE_SUFFIX PREVIEW_TYPE_SUFFIX}<code> + "." +
   * <var>type_name</var></code> is set, where <code><var>type_name</var></code> is the name
   * of a document type, the value of that system property is returned. Otherwise, the
   * suffix associated with the content type of the document is returned. The latter is
   * obtained by
   * {@link MediaType#suffixOf MediaType.suffixOf}<code>[master.getContentType()]</code>.
   */

  protected String getPreviewTypeSuffix (Master master)
    throws MasterException
  {
    String suffix =
      System.getProperty(CdkConfigParam.PREVIEW_TYPE_SUFFIX + "." + master.getTypeName());
    if ( suffix == null )
      suffix = MediaType.suffixOf[master.getContentType()];
    return suffix;
  }

  /**
   * Auxiliary method, returns the type suffix of the content file corresponding to the
   * specified master. If the system property
   * {@link CdkConfigParam#CONTENT_TYPE_SUFFIX CONTENT_TYPE_SUFFIX}<code> + "." +
   * <var>type_name</var></code> is set, where <code><var>type_name</var></code> is the name
   * of a document type, the value of that system property is returned. Otherwise, the
   * suffix associated with the content type of the document is returned. The latter is
   * obtained by
   * {@link MediaType#suffixOf MediaType.suffixOf}<code>[master.getContentType()]</code>.
   */

  protected String getContentTypeSuffix (Master master)
    throws MasterException
  {
    String suffix =
      System.getProperty(CdkConfigParam.CONTENT_TYPE_SUFFIX + "." + master.getTypeName());
    if ( suffix == null )
      suffix = MediaType.suffixOf[master.getContentType()];
    return suffix;
  }

  /**
   * Auxiliary method, returns the type suffix of the source file corresponding to the
   * specified master. If the system property
   * {@link CdkConfigParam#SOURCE_TYPE_SUFFIX SOURCE_TYPE_SUFFIX}<code> + "." +
   * <var>type_name</var></code> is set, where <code><var>type_name</var></code> is the name
   * of a document type, the value of that system property is returned. Otherwise, null is
   * returned.
   */

  protected String getSourceTypeSuffix (Master master)
    throws MasterException
  {
    String suffix =
      System.getProperty(CdkConfigParam.SOURCE_TYPE_SUFFIX + "." + master.getTypeName());
    return suffix;
  }

  // ------------------------------------------------------------------------------------------
  // Filename resolving
  // ------------------------------------------------------------------------------------------

  /**
   * Resolves the prefix keywords of the specified path with respect to the specified
   * context path.
   */

  public String resolvePathPrefix (String path, String contextPath)
    throws IllegalArgumentException
  {
    String resolvedPath;
    PathTokenizer pathTokenizer = new PathTokenizer();
    pathTokenizer.tokenize(path);
    String prefixKeyword = pathTokenizer.getPrefixKeyword();
    String pathWithoutPrefixKeyword = pathTokenizer.getPathWithoutPrefixKeyword();
    if ( prefixKeyword == null )
      resolvedPath = pathWithoutPrefixKeyword;
    else
      {
        pathTokenizer.tokenize(contextPath);
        if ( pathTokenizer.getPrefixKeyword() != null )
          throw new IllegalArgumentException
            ("Context path must not begin with a prefix keyword: " + contextPath);
        if ( prefixKeyword.equals("current:") )
          {
            String contextSectionPath = pathTokenizer.getSectionPath();
            resolvedPath =
              (contextSectionPath == null
               ? pathWithoutPrefixKeyword
               : contextSectionPath + File.separator + pathWithoutPrefixKeyword);
          }
        else if ( prefixKeyword.equals("parent:") )
          {
            String contextSectionPath = pathTokenizer.getSectionPath();
            if ( contextSectionPath == null )
              throw new IllegalArgumentException
                ("Context path has no parent: " + contextPath);
            pathTokenizer.tokenize(contextSectionPath);
            String parentSectionPath = pathTokenizer.getSectionPath();
            resolvedPath =
              (parentSectionPath == null
               ? pathWithoutPrefixKeyword
               : parentSectionPath + File.separator + pathWithoutPrefixKeyword);
          }
        else
          throw new IllegalArgumentException
            ("Unknown prefix keyword: " + prefixKeyword);
      }
    return resolvedPath;
  }

  // ------------------------------------------------------------------------------------------
  // Cached dir entries
  // ------------------------------------------------------------------------------------------

  /**
   * Returns all files in the checkin tree.
   */

  public List getCheckinFiles ()
  {
    return this.cachedDirEntries.getFiles();
  }

  // ------------------------------------------------------------------------------------------
  // Master object cache
  // ------------------------------------------------------------------------------------------

  /**
   * Returns the {@link Master Master} object cache.
   */

  public MasterCache getMasterCache ()
  {
    return this.masterCache;
  }

  // ------------------------------------------------------------------------------------------
  // Resolving generic documents
  // ------------------------------------------------------------------------------------------

  /**
   * <p>
   *   Returns the path of the real document corresponding to the generic document with
   *   the specified path.
   * </p>
   * <p>
   *   If the real document can not be found, the method returns <code>null</code>.
   * </p>
   *
   * @param pathOfGeneric path of the generic document to resolve
   *
   * @throws CdkException if <code>pathOfGeneric</code> does not point to a generic
   * document
   * @throws MasterCacheException if the {@link Master Master} object of the generic
   * document can not be accessed
   */

  public String resolvePathOfGeneric (String pathOfGeneric)
    throws CdkException, MasterException, MasterCacheException
  {
    // Check if inputPath really points to a generic document, and determine the
    // type the corresponding real document must have:
    File masterFileOfGeneric = new File(this.pathToAbsFilename(pathOfGeneric));
    Master masterOfGeneric = this.masterCache.getMaster(masterFileOfGeneric);
    if ( masterOfGeneric.getNature() != Nature.DOCUMENT )
      throw new CdkException("Not a document: " + pathOfGeneric);
    int typeOfGeneric = masterOfGeneric.getType();
    int typeOfReal = DocType.realOf(typeOfGeneric);
    if ( typeOfReal == DocType.UNDEFINED )
      throw new CdkException("Not a generic document: " + pathOfGeneric);

    // Find the real document:
    return this.genericDocumentResolver.findPathOfReal(typeOfReal, pathOfGeneric);
  }

  /**
   * <p>
   *   Returns the path of the "real" document corresponding to a given document. Does the
   *   following:
   * </p>
   * <ol>
   *   <li>
   *     If <code>docPath</code> points to a real (i.e., non-generic) document, returns
   *     <code>docPath</code>.
   *   </li>
   *   <li>
   *     If <code>docPath</code> points to a generic document, resolves it to the
   *     corresponding real one and returns the latter
   *   </li>
   * </ol>
   * <p>
   *   If the real document can not be found in the second case, throws an exception.
   * </p>
   *
   * @throws CdkException if something goes wrong.
   */

  public String getPathOfReal (String docPath)
    throws CdkException, MasterException, MasterCacheException
  {
    final String METHOD = "getPathOfReal";
    if ( docPath == null )
      throw new CdkException(METHOD + ": Document path null");
    if ( !this.isMasterFilename(docPath) )
      throw new IllegalArgumentException("Not a master path: " + docPath);
    File masterFile = new File(this.pathToAbsFilename(docPath));
    Master master = this.masterCache.getMaster(masterFile);
    if ( master.getNature() != Nature.DOCUMENT )
      throw new CdkException(METHOD+ ": Not a document: " + docPath);
    int type = master.getType();
    if ( DocType.isGeneric(type) )
      {
        String pathOfReal =
          this.genericDocumentResolver.findPathOfReal(DocType.realOf(type), docPath);
        if ( pathOfReal == null )
          throw new CdkException
            (METHOD+ ": Can not find real document for: " + docPath);
        return pathOfReal;
      }
    else
      return docPath;
  }

  /**
   * Returns the generic document resolver.
   */

  public GenericDocumentResolver getGenericDocumentResolver ()
  {
    return this.genericDocumentResolver;
  }

  // ------------------------------------------------------------------------------------------
  // Japs client
  // ------------------------------------------------------------------------------------------

  /**
   * Creates a new Japs client with the specified alias, URL prefix, and account. If a Japs
   * client with the same alias already exists, an exception is thrown.
   */

  public void createJapsClient (String alias, String urlPrefix, String account)
  {
    if ( this.japsClients.containsKey(alias) )
      throw new IllegalArgumentException
        ("createJapsClient: Alias already exists: " + alias);
    this.japsClients.put(alias, new CdkJapsClient(alias, urlPrefix, account));
  }

  /**
   * Returns the japs client for the specifed alias.
   */

  public CdkJapsClient getJapsClient (String alias)
  {
    if ( !this.japsClients.containsKey(alias) )
      throw new IllegalArgumentException
        ("getJapsClient: no client for that alias: " + alias);
    return this.japsClients.get(alias);
  }

  /**
   * Returns a list of all currently defined aliases.
   */

  public List<String> getAccountAliases ()
  {
    List<String> list = new ArrayList<String>();
    list.addAll(this.japsClients.keySet());
    return list;
  }

  // ------------------------------------------------------------------------------------------
  // Constructors
  // ------------------------------------------------------------------------------------------

  /**
   * Creates a new instance with the specified checkin root.
   */

  public CdkHelper (String checkinRoot)
    throws CdkInitException
  {
    this.init(checkinRoot);
  }

  /**
   * Creates a new instance with the checkin root specified in the system property
   * {@link CdkConfigParam#CHECKIN_ROOT CdkConfigParam.CHECKIN_ROOT}.
   */

  public CdkHelper ()
    throws CdkInitException
  {
    String checkinRoot = System.getProperty(CdkConfigParam.CHECKIN_ROOT);
    if ( checkinRoot == null )
      throw new CdkInitException
        ("System property " + CdkConfigParam.CHECKIN_ROOT + " not set");
    this.init(checkinRoot);
  }

  /**
   * Auxiliary method. Used by the constructors to initialize the new instance.
   */

  protected void init (String checkinRoot)
    throws CdkInitException
  {
    try
      {
        if ( !(new File(checkinRoot)).isAbsolute() )
          throw new IllegalArgumentException("Checkin root is not absolute: " + checkinRoot);
        this.checkinRoot = checkinRoot;
        this.masterCache = new MasterCache();
        this.cachedDirEntries = new CachedDirEntries(this.checkinRoot);
        this.genericDocumentResolver = new GenericDocumentResolver(this, -1);
      }
    catch (Exception exception)
      {
        throw new CdkInitException(exception);
      }
  }

  // ------------------------------------------------------------------------------------------
  // Static utility methods
  // ------------------------------------------------------------------------------------------

  /**
   * Prints the specified string to the specified stream, quotes XML special characters and
   * characters above 127.
   */

  public static void printXMLText (String string, PrintStream out)
  {
    for (char c : string.toCharArray())
      {
        if ( c == '<' )
          out.print("&lt;");
        else if ( c == '>' )
          out.print("&gt;");
        else if ( c == '&' )
          out.print("&amp;");
        else if ( c < 128 )
          out.print(c);
        else
          out.print("&#" + (int)c + ";");
      }
  }

  /**
   * Quotes XML special characters and characters above 127 in the specified string.
   */

  public static String quoteXML (String string)
  {
    StringBuilder buffer = new StringBuilder();
    for (char c : string.toCharArray())
      {
        if ( c == '<' )
          buffer.append("&lt;");
        else if ( c == '>' )
          buffer.append("&gt;");
        else if ( c == '&' )
          buffer.append("&amp;");
        else if ( c < 128 )
          buffer.append(c);
        else
          buffer.append("&#" + (int)c + ";");
      }
    return buffer.toString();
  }

  /**
   * Returns a boolean from a string. If the string equals "true" or "yes", true is
   * returned. Otherwise, if the string is "false" or "no", false is returned. Otherwise, an
   * exception is thrown.
   */

  public static boolean toBoolean (String string)
  {
    if ( string.equals("false") || string.equals("no") )
      return false;
    else if ( string.equals("true") || string.equals("yes") )
      return true;
    else
      throw new IllegalArgumentException("Invalid boolean specifier: \"" + string + "\"");
  }

  // ------------------------------------------------------------------------------------------
  // Shared static instance
  // ------------------------------------------------------------------------------------------

  /**
   * Returns a static <code>CdkHelper</code> instance.
   */

  public static synchronized CdkHelper getSharedInstance ()
    throws CdkInitException
  {
    if ( sharedInstance == null )
      sharedInstance = new CdkHelper();
    return sharedInstance;
  }
}
