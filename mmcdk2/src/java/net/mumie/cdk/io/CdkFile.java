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

package net.mumie.cdk.io;

import java.io.File;
import net.mumie.cdk.CdkException;
import net.mumie.cdk.CdkHelper;
import net.mumie.cdk.CdkInitException;
import net.mumie.cdk.util.MasterCacheException;
import net.mumie.cocoon.checkin.Master;
import net.mumie.cocoon.checkin.MasterException;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.FileRole;
import net.mumie.cocoon.notions.MediaType;
import net.mumie.cocoon.notions.Nature;
import net.mumie.util.io.IOUtil;

/**
 * <p>
 *   An extension of {@link File File} for Mumie-specific filenames. Provides the following
 *   extra-functionalities:
 * </p>
 * <ul>
 *   <li>
 *     Methods to get Mumie-specific filename parts (suffixes, path relative to checkin
 *     root)
 *   </li>
 *   <li>
 *     Methods to convert this Mumie filename to Mumie filenames of a different kind;
 *     e.g., a master filenames to content filenames.
 *   </li>
 *   <li>
 *     Methods to check for Mumie specific file types ("is it a master file?", "is it a
 *     content file?", etc.).
 *   </li>
 *   <li>
 *     A method to get a {@link Master Master} object (master files only).
 *   </li>
 * </ul>
 * <p>
 *   A <code>CdkFile</code> is always absolute, which means that
 *   {@link File#isAbsolute isAbsolute} always yields true. If a <code>CdkFile</code> is
 *   created from a non-absolute filename, the filename is interpreted relative to the
 *   checkin root directory (cf. contructors below).
 * </p>
 * <p>
 *   Objects of this type internally use the global {@link CdkHelper} instance.
 * </p>
 *
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: CdkFile.java,v 1.18 2008/08/30 23:30:53 rassy Exp $</code>
 */

public class CdkFile extends File
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * Suffix of temporary XML files
   * (<code>"tmp." + </code>{@link MediaType#TEXT_XML_SUFFIX MediaType.TEXT_XML_SUFFIX}).
   */

  public static final String TMP_XML_SUFFIX = "tmp." + MediaType.TEXT_XML_SUFFIX;

  /**
   * Suffix of dynamic data files
   * (<code>"dyndata." + </code>{@link MediaType#TEXT_XML_SUFFIX MediaType.TEXT_XML_SUFFIX}).
   */

  public static final String DYNDATA_SUFFIX = "dyndata." + MediaType.TEXT_XML_SUFFIX;

  /**
   * Suffix of datasheet files
   * (<code>"datasheet." + </code>{@link MediaType#TEXT_XML_SUFFIX MediaType.TEXT_XML_SUFFIX}).
   */

  public static final String DATASHEET_SUFFIX = "datasheet." + MediaType.TEXT_XML_SUFFIX;

  /**
   *
   */

  public static final String ANSWERS_SUFFIX = "answers." + MediaType.TEXT_XML_SUFFIX;

  /**
   *
   */

  public static final String CORRECTION_SUFFIX = "correction." + MediaType.TEXT_XML_SUFFIX;

  /**
   * The pure name
   */

  protected String pureName = null;

  /**
   * The path.
   */

  protected String cdkPath = null;

  /**
   * The path without suffixes.
   */

  protected String cdkPurePath = null;

  /**
   * The role suffix.
   */

  protected String roleSuffix = null;

  /**
   * The type suffix.
   */

  protected String typeSuffix = null;

  /**
   * The total suffix
   */

  protected String suffix = null;

  // --------------------------------------------------------------------------------
  // Constructors
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>CdkFile</code> from the specified filename. If the latter is
   * not absloute, it is interpreted relative to the checkin root directory.
   */

  public CdkFile (String filename)
    throws CdkInitException
  {
    super(IOUtil.normalizeFilename(makeAbsolute(filename)));

    // Set cdk path:
    this.cdkPath =
      CdkHelper.getSharedInstance().absFilenameToPath(this.getAbsolutePath());

    // Set cdk pure path and suffixes:
    int lastDot =  this.cdkPath.lastIndexOf('.');
    int nextToLastDot = (lastDot > 0 ? this.cdkPath.lastIndexOf('.', lastDot-1) : -1);
    if ( lastDot != -1 && nextToLastDot != -1 )
      {
        this.cdkPurePath = this.cdkPath.substring(0, nextToLastDot);
        this.roleSuffix = this.cdkPath.substring(nextToLastDot+1, lastDot);
        this.typeSuffix = this.cdkPath.substring(lastDot+1);
        this.suffix = this.cdkPath.substring(nextToLastDot+1);
      }
    else if ( lastDot != -1 )
      {
        this.cdkPurePath = this.cdkPath.substring(0, lastDot);
        this.roleSuffix = null;
        this.typeSuffix = this.cdkPath.substring(lastDot+1);
        this.suffix = this.typeSuffix;
      }
    else
      {
        this.cdkPurePath = this.cdkPath;
        this.roleSuffix = null;
        this.typeSuffix = null;
        this.suffix = null;
      }

    // Set pure name:
    int lastSep = this.cdkPurePath.lastIndexOf(File.separatorChar);
    this.pureName =
      (lastSep != -1
       ? this.cdkPurePath.substring(lastSep+1)
       : this.cdkPurePath);
  }

  /**
   * Creates a new <code>CdkFile</code> from the specified {@link File File} object. If the
   * latter is not absloute, it is interpreted relative to the checkin root directory.
   */

  public CdkFile (File file)
    throws CdkInitException
  {
    this(file.getPath());
  }

  // --------------------------------------------------------------------------------
  // Retrieving file components
  // --------------------------------------------------------------------------------

  /**
   * Returns the pure name.
   */

  public String getPureName ()
  {
    return this.pureName;
  }

  /**
   * Returns the path relative to the checkin root directory.
   */

  public String getCdkPath ()
  {
    return this.cdkPath;
  }

  /**
   * Returns the path without suffixes relative to the checkin root directory.
   */

  public String getCdkPurePath ()
  {
    return this.cdkPurePath;
  }

  /**
   * Returns the role suffix.
   */

  public String getRoleSuffix ()
  {
    return this.roleSuffix;
  }

  /**
   * Returns the Type suffix.
   */

  public String getTypeSuffix ()
  {
    return this.typeSuffix;
  }

  /**
   * Returns the total suffix.
   */

  public String getSuffix ()
  {
    return this.suffix;
  }

  // --------------------------------------------------------------------------------
  // Conversion into other MUMIE files
  // --------------------------------------------------------------------------------

  /**
   * If this is a master file of a non-generic document, returns the corresponding content
   * file. If this is a master file of a pseudo-document or generic document, returns null.
   * If this is not a master file, throws an exception.
   */

  public CdkFile getContentFile ()
    throws CdkFileException
  {
    try
      {
        String contentPath =
          CdkHelper.getSharedInstance().masterToContentPath(this.getCdkPath());
        return (contentPath != null ? new CdkFile(contentPath) : null);
      }
    catch (Exception exception)
      {
        throw new CdkFileException(exception);
      }
  }

  /**
   * If this is a master file of a non-generic document, returns the corresponding preview
   * file. If this is a master file of a pseudo-document or generic document, returns null.
   * If this is not a master file, throws an exception.
   */

  public CdkFile getPreviewFile ()
    throws CdkFileException
  {
    try
      {
        String previewPath =
          CdkHelper.getSharedInstance().masterToPreviewPath(this.getCdkPath());
        return (previewPath != null ? new CdkFile(previewPath) : null);
      }
    catch (Exception exception)
      {
        throw new CdkFileException(exception);
      }
  }

  /**
   * If this is a master file of a non-generic document for which a source suffix is
   * defined, returns the corresponding source file. If this is a master file of a
   * non-generic document for which no source suffix is defined, or if this is a master file
   * of a pseudo-document or generic document, returns null. If this is not a master file,
   * throws an exception.
   */

  public CdkFile getSourceFile ()
    throws CdkFileException
  {
    try
      {
        String sourcePath =
          CdkHelper.getSharedInstance().masterToSourcePath(this.getCdkPath());
        return (sourcePath != null ? new CdkFile(sourcePath) : null);
      }
    catch (Exception exception)
      {
        throw new CdkFileException(exception);
      }
  }

  /**
   * Replaces the suffix by the specified string and returns the new file.
   */

  public CdkFile replaceSuffix (String newSuffixes)
    throws CdkInitException
  {
    return new CdkFile(this.cdkPurePath + "." + newSuffixes);
  }

  /**
   * Returns the master file corresponding to this file.
   */

  public CdkFile getMasterFile ()
    throws CdkInitException
  {
    return this.replaceSuffix(CdkHelper.MASTER_SUFFIX);
  }

  /**
   * If this is the master file of a document, returns the master file of the corresponding
   * "real" document. If this is already a real (i.e., non-generic) document, returns a new
   * <code>CdkFile</code> instance representing the same file as this one. If this is a
   * generic document, resolves it and returns the corresponding real one. If this is the
   * master file of a pseudo-document, or not a master file at all, throws an exception.
   */

  public CdkFile getMasterFileOfReal ()
    throws MasterException, MasterCacheException, CdkInitException, CdkException
  {
    return new CdkFile(CdkHelper.getSharedInstance().getPathOfReal(this.cdkPath));
  }

  // --------------------------------------------------------------------------------
  // File role/type checks
  // --------------------------------------------------------------------------------

  /**
   * Returns true if this is a master file, otherwise false.
   */

  public boolean isMasterFile ()
  {
    return
      ( this.roleSuffix != null &&
        this.typeSuffix != null &&
        this.roleSuffix.equals(FileRole.MASTER_SUFFIX) &&
        this.typeSuffix.equals(MediaType.TEXT_XML_SUFFIX) );
  }

  /**
   * Returns true if this is a content file, otherwise false.
   */

  public boolean isContentFile ()
  {
    return ( this.roleSuffix != null && 
             this.roleSuffix.equals(FileRole.CONTENT_SUFFIX) );
  }

  /**
   * Returns true if this is a preview file, otherwise false.
   */

  public boolean isPreviewFile ()
  {
    return ( this.roleSuffix != null && 
             this.roleSuffix.equals(FileRole.PREVIEW_SUFFIX) );
  }

  /**
   * Returns true if this is a source file, otherwise false.
   */

  public boolean isSourceFile ()
  {
    return ( this.roleSuffix != null && 
             this.roleSuffix.equals(FileRole.SOURCE_SUFFIX) );
  }

  /**
   * Returns true if this is the master file of a generic document, otherwise false.
   */

  public boolean isMasterOfGeneric ()
    throws MasterException, MasterCacheException, CdkInitException
  {
    if ( this.isMasterFile() )
      {
        Master master = this.getMaster();
        return ( master.getNature() == Nature.DOCUMENT && DocType.isGeneric(master.getType()) );
      }
    else
      return false;
  }

  /**
   * 
   */

  public int getFileRole ()
  {
    if ( this.isMasterFile() )
      return FileRole.MASTER;
    else if ( this.isContentFile() )
      return FileRole.CONTENT;
    else if ( this.isSourceFile() )
      return FileRole.SOURCE;
    else if ( this.isPreviewFile() )
      return FileRole.PREVIEW;
    else
      return FileRole.UNDEFINED;
  }

  // --------------------------------------------------------------------------------
  // Getting a Master object
  // --------------------------------------------------------------------------------

  /**
   * If this is a master file, returns a {@link Master Master} object corresponding to this
   * file. Otherwise, an exception is thrown.
   */

  public Master getMaster ()
    throws CdkInitException, MasterCacheException 
  {
    if ( !this.isMasterFile() )
      throw new IllegalStateException("Not a master file: " + this);
    return CdkHelper.getSharedInstance().getMasterCache().getMaster(this);
  }

  // --------------------------------------------------------------------------------
  // Directory contents
  // --------------------------------------------------------------------------------

  /**
   * If this is a directory, returns its contents as <code>CdkFile</code>s. Otherwise,
   * returns null.
   */

  public CdkFile[] listCdkFiles ()
    throws CdkInitException
  {
    File[] files = this.listFiles();
    if ( files == null )
      return null;
    CdkFile[] cdkFiles = new CdkFile[files.length];
    for (int i = 0; i < files.length; i++)
      cdkFiles[i] = new CdkFile(files[i]);
    return cdkFiles;
  }

  // --------------------------------------------------------------------------------
  // toString method
  // --------------------------------------------------------------------------------

  /**
   * Returns the path relative to the checkin root.
   */

  public String toString()
  {
    return this.cdkPath;
  }

  // --------------------------------------------------------------------------------
  // Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * Makes the specified filename absolute if necessary. If the filename is already
   * absolute, it is simply returned. Otherwise, the filename is interpreted as relative
   * to the checkin root, and is made absolute by calling the
   * {@link CdkHelper#pathToAbsFilename pathToAbsFilename} method of the shared CdkHelper
   * instance.
   */

  protected static String makeAbsolute (String filename)
    throws CdkInitException
  {
    return
      ((new File(filename)).isAbsolute()
       ? filename
       : CdkHelper.getSharedInstance().pathToAbsFilename(filename));
  }
}
