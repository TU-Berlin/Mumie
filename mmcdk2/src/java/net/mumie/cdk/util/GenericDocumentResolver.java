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
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.mumie.cdk.CdkHelper;
import net.mumie.cdk.util.MasterCacheException;
import net.mumie.cocoon.checkin.GDIMEntry;
import net.mumie.cocoon.checkin.Master;
import net.mumie.cocoon.checkin.MasterException;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.LangCode;
import net.mumie.cocoon.notions.MediaType;
import net.mumie.cocoon.notions.Nature;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class GenericDocumentResolver
  implements Serializable
{
  // --------------------------------------------------------------------------------
  // Inner classes
  // --------------------------------------------------------------------------------

  /**
   * Represents a real path together with a timestamp. The latter contains the time the
   * instance was created. Objects of this class are the values of the cache map,
   * {@link #cache cache}.
   */

  public static class CacheRecord
  {
    /**
     * The path of the real document.
     */

    protected String pathOfReal;

    /**
     * Time when this instance was created.
     */

    protected long created;

    /**
     * Returns the path of the real document.
     */

    public final String getPathOfReal()
    {
      return this.pathOfReal;
    }

    /**
     * Returns the time this instance was created.
     */

    public long getCreated ()
    {
      return this.created;
    }

    /**
     * Creates a new instance for the specified real document path.
     */

    public CacheRecord (String pathOfReal)
    {
      this.pathOfReal = pathOfReal;
      this.created = System.currentTimeMillis();
    }
  }

  /**
   * Represents the files to check for resolving a generic document. The
   * {@link #getNextFile getNextFile} method iterates through all files comprised by the
   * <code>FilesToCheck</code> object. In fact, these are all existing master files, but the
   * order in which they are returned follows a certain heuristics which makes it likely
   * that the correct file is found quickly: First, it is assumed that the real document
   * implementing the given generic one is in the same directory as the latter. Thus, the
   * files of that directory are returned. Then then files of the parent directory are
   * returned, then the files of the parent's parent directory, and so on until the checkin
   * root directory is reached. This is called the "near by search mode". After that, all
   * master files are returned in no specific order. This called the "search all mode".
   */

  protected class FilesToCheck
  {
    /**
     * The cdk helper this instance is associated to.
     */

    protected CdkHelper cdkHelper;

    /**
     * Flag indicating if "near by search" is in progress.
     */

    protected boolean nearBySearch;

    /**
     * The currently scanned directory ("near by search" mode).
     */

    protected File dir;

    /**
     * The contents of the currently scanned directory ("near by search" mode).
     */

    protected File[] dirFiles;

    /**
     * Current position in the file array representing the contents of the currently scanned
     * directory ("near by search" mode).
     */

    protected int dirPos;

    /**
     * Iterator for the list of all files when "search all" is in progress.
     */

    protected Iterator allFilesIterator;

    /**
     * Returns the next file.
     */

    protected File getNextFile ()
    {
      if ( this.nearBySearch && this.dirPos == this.dirFiles.length-1 )
        {
          if ( this.dir.getPath().equals(this.cdkHelper.getCheckinRoot()) )
            {
              // Switch to "search all" mode:
              this.nearBySearch = false;
              this.allFilesIterator = this.cdkHelper.getCheckinFiles().iterator();
            }
          else
            {
              // Go to parent dir:
              this.dir = this.dir.getParentFile();
              this.dirFiles = this.dir.listFiles();
              this.dirPos = -1;
            }
        }

      return
        (this.nearBySearch
         ? this.dirFiles[++this.dirPos]
         : (this.allFilesIterator.hasNext()
            ? (File)this.allFilesIterator.next()
            : null));
    }

    /**
     * Creates a new instance.
     */

    public FilesToCheck (String pathOfGeneric, CdkHelper cdkHelper)
    {
      this.cdkHelper = cdkHelper;
      this.nearBySearch = true;
      this.dir = new File(cdkHelper.pathToAbsFilename(pathOfGeneric)).getParentFile();
      this.dirFiles = this.dir.listFiles();
      this.dirPos = -1;
    }
  }

  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The cdk helper this instance is used by.
   */

  protected CdkHelper cdkHelper;

  /**
   * Conatins the cached information. The keys are generic paths, the values are
   * {@link #CacheRecord} objects. Each <code>CacheRecord</code> contains the cached real
   * path for the generic path given by the key and a timestamp containing the time the
   * <code>CacheRecord</code> was created.
   */

  protected Map<String,CacheRecord> cache;

  /**
   * The maximum size of the cache. -1 means unlimited.
   */

  protected int cacheMaxSize;

  // --------------------------------------------------------------------------------
  // Resolving generic documents
  // --------------------------------------------------------------------------------

  /**
   * Finds the path of the real document which has the specified type and corresponds to the
   * generic document with the specified path.
   */

  public String findPathOfReal (int typeOfReal, String pathOfGeneric)
    throws MasterException, MasterCacheException
  {
    // Check if real path is cached:
    if ( this.cache.containsKey(pathOfGeneric) )
      {
        CacheRecord cacheRecord = this.cache.get(pathOfGeneric);
        String cachedPathOfReal = cacheRecord.getPathOfReal();

        // Check if cached value is up-to-date:
        File masterFileOfReal = new File(this.cdkHelper.pathToAbsFilename(cachedPathOfReal));
        if ( masterFileOfReal.exists() &&
             masterFileOfReal.lastModified() < cacheRecord.getCreated() )
          // Return cached path:
          return cachedPathOfReal;
        else
          // Remove outdated cache record:
          this.cache.remove(pathOfGeneric);
      }

    // Real path is not cached, or cached real path is outdated.
    // Find real path by crawling through all masters:
    String pathOfReal = null;
    String cdkThemePath = this.cdkHelper.getThemePath();
    String cdkLangCode = this.cdkHelper.getLangCode();
    int lastPriority = -1;
    FilesToCheck files = new FilesToCheck(pathOfGeneric, this.cdkHelper);
    File file;
    while ( lastPriority < 6 && (file = files.getNextFile()) != null )
      {
        String path = this.cdkHelper.absFilenameToPath(file.getPath());
        if ( this.cdkHelper.isMasterFilename(path) )
          {
            Master master = this.cdkHelper.getMasterCache().getMaster(file);
            if ( master.getNature() == Nature.DOCUMENT &&
                 master.getType() == typeOfReal )
              {
                GDIMEntry[] gdimEntries = master.getGDIMEntries();
                for (int i = 0; pathOfReal == null && i < gdimEntries.length; i++)
                  {
                    String pathToCompare = gdimEntries[i].getGenericDocPath();
                    pathToCompare = this.cdkHelper.resolvePathPrefix(pathToCompare, path);
                    if ( pathToCompare.equals(pathOfGeneric) )
                      {
                        // Get the language code:
                        String langCode =
                          this.cdkHelper.normalizeLangCode(gdimEntries[i].getLangCode());

                        // Get the theme path:
                        String themePath =
                          this.cdkHelper.normalizeThemePath(gdimEntries[i].getThemePath());

                        // Determine priority:
                        int priority = -1;
                        if ( langCode.equals(cdkLangCode) )
                          {
                            if ( themePath.equals(cdkThemePath) )
                              priority = 6;
                            else if ( themePath.equals(CdkHelper.DEFAULT_THEME_PATH) )
                              priority = 5;
                          }
                        else if ( langCode.equals(LangCode.NEUTRAL) )
                          {
                            if ( themePath.equals(cdkThemePath) )
                              priority = 4;
                            else if ( themePath.equals(CdkHelper.DEFAULT_THEME_PATH) )
                              priority = 3;
                          }
                        else if ( langCode.equals(LangCode.DEFAULT) )
                          {
                            if ( themePath.equals(cdkThemePath) )
                              priority = 2;
                            else if ( themePath.equals(CdkHelper.DEFAULT_THEME_PATH) )
                              priority = 1;
                          }

                        // Update pathOfReal if necessary:
                        if ( priority > lastPriority )
                          {
                            pathOfReal = path;
                            lastPriority = priority;
                          }
                      }
                  }
              }
          }
      }

    if ( pathOfReal != null )
      {
        // Cache the found real path:
        if ( this.cacheMaxSize == -1 || this.cache.size() < this.cacheMaxSize )
          this.cache.put(pathOfGeneric, new CacheRecord(pathOfReal));
      }

    return pathOfReal;
  }

  // --------------------------------------------------------------------------------
  // Controlling the cache
  // --------------------------------------------------------------------------------

  /**
   * Returns the maximum size of the cache. -1 means unlimited.
   */

  public int getCacheMaxSize ()
  {
    return this.cacheMaxSize;
  }

  /**
   * Sets maximum size of the cache. -1 means unlimited.
   */

  public void setCacheMaxSize (int cacheMaxSize)
  {
    if ( cacheMaxSize < -1 )
      throw new IllegalArgumentException
        ("Illegal value for maximum cache size: " + cacheMaxSize);
    this.cacheMaxSize = cacheMaxSize;
  }

  /**
   * Returns an array of all cached generic document paths.
   */

  public String[] listCache ()
  {
    synchronized (this.cache)
      {
        return this.cache.keySet().toArray(new String[this.cache.size()]);
      }
  }

  /**
   * Returns the cache record for the specified generic path.
   */

  public CacheRecord getCacheRecord (String pathOfGeneric)
  {
    return this.cache.get(pathOfGeneric);
  }

  /**
   * Clears the cache.
   */

  public void clearCache ()
  {
    this.cache.clear();
  }

  // --------------------------------------------------------------------------------
  // Constructor
  // --------------------------------------------------------------------------------

  /**
   * Creates a new instance for the specified cdk helper. The maximum size of the new cache
   * is set to the specified value.
   */

  public GenericDocumentResolver (CdkHelper cdkHelper, int cacheMaxSize)
  {
    this.cdkHelper = cdkHelper;
    if ( cacheMaxSize < -1 )
      throw new IllegalArgumentException("Invalid GDIM cache maximum size: " + cacheMaxSize);
    this.cacheMaxSize = cacheMaxSize;
    this.cache = Collections.synchronizedMap(new HashMap<String,CacheRecord>());
  }

}
