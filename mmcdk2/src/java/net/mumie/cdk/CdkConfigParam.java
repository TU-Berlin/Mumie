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

public final class CdkConfigParam
{
  /**
   * Common prefix of all parameters (<code>"net.mumie.cdk"</code>).
   */

  public static final String PREFIX = "net.mumie.cdk";

  /**
   * Full qualified name of the CdkHelper implementation.
   * (<code>PREFIX + ".cdkHelperImpl"</code>).
   */

  public static final String CDK_HELPER_IMPL = PREFIX + ".cdkHelperImpl";

  /**
   * The home directory of mmcdk
   * (<code>PREFIX + ".cdkHome"</code>).
   */

  public static final String CDK_HOME = PREFIX + ".cdkHome";

  /**
   * The checkin root directory
   * (<code>PREFIX + ".checkinRoot"</code>).
   */

  public static final String CHECKIN_ROOT = PREFIX + ".checkinRoot";

  /**
   * The common install prefix of all Mumie packages.
   */

  public static final String INSTALL_PREFIX = PREFIX + ".installPrefix";

  /**
   * The path of the theme for which the previews are created (<code>PREFIX +
   * ".themePath"</code>). If this parameter is not set, the default theme is assumed.
   * set, 
   */

  public static final String THEME_PATH = PREFIX + ".themePath";

  /**
   * The code of the language for which the previews are created (<code>PREFIX +
   * ".langCode"</code>). If this parameter is not set, the default language is assumed.
   */

  public static final String LANG_CODE = PREFIX + ".langCode";

  /**
   * The URI of the XSL stylesheet transforming master plus content files to temporary XML
   * files.
   * (<code>PREFIX + ".masterToTmpURI"</code>).
   */

  public static final String MASTER_TO_TMP_URI = PREFIX + ".masterToTmpURI";

  /**
   * The URI of the XSL stylesheet transforming temporary XML files to preview files
   * (<code>PREFIX + ".tmpToPreviewURI"</code>). This constant contains only the first part
   * of the parameter. To get the entire parameter, <code>"." + <var>type_name</var></code>
   * must be appended, where <code><var>type_name</var></code> is the name of a document
   * type. Thus, there are different parameters for different document types. This is
   * because the stylesheets are specific for the document types.
   */

  public static final String TMP_TO_PREVIEW_URI = PREFIX + ".tmpToPreviewURI";

  /**
   * The type suffix for preview files (<code>PREFIX + ".previewTypeSuffix"</code>). This
   * constant contains only the first part of the parameter. To get the entire parameter,
   * <code>"." + <var>type_name</var></code> must be appended, where
   * <code><var>type_name</var></code> is the name of a document type. Thus, there are
   * different parameters for different document types. This is because the suffixes are
   * specific for the document types. 
   */

  public static final String PREVIEW_TYPE_SUFFIX = PREFIX + ".previewTypeSuffix";

  /**
   * The type suffix for content files (<code>PREFIX + ".contentTypeSuffix"</code>). This
   * constant contains only the first part of the parameter. To get the entire parameter,
   * <code>"." + <var>type_name</var></code> must be appended, where
   * <code><var>type_name</var></code> is the name of a document type. Thus, there are
   * different parameters for different document types. This is because the suffixes are
   * specific for the document types. 
   */

  public static final String CONTENT_TYPE_SUFFIX = PREFIX + ".contentTypeSuffix";

  /**
   * The type suffix for source files (<code>PREFIX + ".sourceTypeSuffix"</code>). This
   * constant contains only the first part of the parameter. To get the entire parameter,
   * <code>"." + <var>type_name</var></code> must be appended, where
   * <code><var>type_name</var></code> is the name of a document type. Thus, there are
   * different parameters for different document types. This is because the suffixes are
   * specific for the document types. 
   */

  public static final String SOURCE_TYPE_SUFFIX = PREFIX + ".sourceTypeSuffix";

  /**
   * The command to call the javac compiler (<code>PREFIX + ".javacCmd"</code>). Can be a
   * sole command name or a command name with path. Must not have any parameters. The
   * default is <code>"javac"</code>.
   */

  public static final String JAVAC_CMD = PREFIX + ".javacCmd";

  /**
   * The command to call the jar tool (<code>PREFIX + ".jarCmd"</code>). Can be a sole
   * command name or a command name with path. Must not have any parameters. The default is
   * <code>"jar"</code>. 
   */

  public static final String JAR_CMD = PREFIX + ".jarCmd";

  /**
   * The command to call the jarsigner (<code>PREFIX + ".jarsignerCmd"</code>). Can be a
   * sole command name or a command name with path. Must not have any parameters. The
   * default is <code>"jarsigner"</code>. 
   */

  public static final String JARSIGNER_CMD = PREFIX + ".jarsignerCmd";

  /**
   * The keystore for jar signing
   * (<code>PREFIX + ".jarsignKeystore"</code>).
   */

  public static final String JARSIGN_KEYSTORE = PREFIX + ".jarsignKeystore";

  /**
   * The keystore password for jar signing
   * (<code>PREFIX + ".jarsignKeystorePassword"</code>).
   */

  public static final String JARSIGN_KEYSTORE_PASSWORD = PREFIX + ".jarsignKeystorePassword";

  /**
   * The key alias for jar signing
   * (<code>PREFIX + ".jarsignKeyAlias"</code>).
   * 
   */

  public static final String JARSIGN_KEY_ALIAS = PREFIX + ".jarsignKeyAlias";

  /**
   * List of language codes
   * (<code>PREFIX + ".langCodes"</code>).
   * 
   */

  public static final String LANG_CODES = PREFIX + ".langCodes";

  /**
   * Class name of the password encryptor
   * (<code>PREFIX + ".passwordEncryptor"</code>).
   */

  public static final String PASSWORD_ENCRYPTOR = PREFIX + ".passwordEncryptor";

  /**
   * Default copyright note (<code>PREFIX + ".copyright"</code>).
   */

  public static final String COPYRIGHT = PREFIX + ".copyright";

  /**
   * Whether graphical login is enabled (<code>PREFIX + ".graphicalLogin"</code>).
   */

  public static final String GRAPHICAL_LOGIN = PREFIX + ".graphicalLogin";

  /**
	 * Regular expression specifying directory names which do not refer to a section.
   */

  public static final String NON_SEC_DIR_NAME_REGEX = PREFIX + ".nonSecDirNameRegex";

  /**
   * Disabled constructor.
   */

  private CdkConfigParam ()
  {
    throw new IllegalStateException("Class must not be instanciated");
  }
}
