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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import net.mumie.cocoon.checkin.DOMMaster;
import net.mumie.cocoon.checkin.Master;
import net.mumie.util.xml.AbortingSAXErrorHandler;

/**
 * A cache for {@link Master Master} objects.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: MasterCache.java,v 1.8 2007/07/16 11:07:07 grudzin Exp $</code>
 */

public class MasterCache
{
  /**
   * The document builder of this instance.
   */

  protected DocumentBuilder documentBuilder;

  /**
   * A {@link Master Master} object together with a timestamp. Instances of this class
   * are the values of the cache map {@link #masterCache masterCache}. The timestamp
   * contains the creation time of the {@link Master Master} object, which is needed
   * for the up-to-date check of the {@link Master Master} object.
   */

  protected class CacheRecord
  {
    Master master;
    long created;
  }

  /**
   * The master cache. The keys of this map are {@link File File} objects, the values
   * {@link CacheRecord CacheRecord} objects. The former represent the source of the
   * masters, the latter the corresponding {@link Master Master} object together with a
   * timestamp specifying its creation time.
   */

  protected Map masterCache = new HashMap();

  /**
   * The maximum size of the cache. -1 means unlimited.
   */

  protected int masterCacheMaxSize = -1;

  /**
   * Returns the {@link Master Master} object corresponding to the specified file. 
   */

  public synchronized Master getMaster (File file)
    throws MasterCacheException
  {
    if ( !file.isAbsolute() )
      throw new MasterCacheException("Filename is not absolute: " + file);

    // TODO: Check if file is indeed a master file
    try
      {
        if ( this.masterCache.containsKey(file) )
          {
            // Get cache record:
            CacheRecord cacheRecord = (CacheRecord)this.masterCache.get(file);

            // If cached master object is outdated, recreate it:
            if ( cacheRecord.created < file.lastModified() )
              {
                cacheRecord.master =
                  new DOMMaster(this.documentBuilder.parse(file).getDocumentElement());
                cacheRecord.created = System.currentTimeMillis();
              }

            return cacheRecord.master;
          }
        else
          {
            // Create new cache record:
            CacheRecord cacheRecord = new CacheRecord();
            cacheRecord.master =
              new DOMMaster(this.documentBuilder.parse(file).getDocumentElement());
            cacheRecord.created = System.currentTimeMillis();

            // Add cache record to cache provided the maximum cache size is not exceeded:
            if ( this.masterCacheMaxSize != -1 &&
                 this.masterCache.size() < this.masterCacheMaxSize )
              this.masterCache.put(file, cacheRecord);

            return cacheRecord.master;
          }
      }
    catch (Exception exception)
      {
        throw new MasterCacheException
          ("Failed to create Master object from file: " + file, exception);
      }
  }

  /**
   * Removes the {@link Master Master} object corresponding to the specified file from the
   * cache. If no cached {@link Master Master} object object for the specified file exists,
   * does nothing.
   */

  public synchronized void removeMaster (File file)
    throws MasterCacheException
  {
    try
      {
        this.masterCache.remove(file);
      }
    catch (Exception exception)
      {
        throw new MasterCacheException
          ("Failed to remove Master from cache: " + file, exception);
      }
  }

  /**
   * Returns the maximum size of the cache. -1 means unlimited.
   */

  public int getMasterCacheMaxSize ()
  {
    return this.masterCacheMaxSize;
  }

  /**
   * Sets maximum size of the cache. -1 means unlimited.
   */

  public synchronized void setMasterCacheMaxSize (int masterCacheMaxSize)
  {
    this.masterCacheMaxSize = masterCacheMaxSize;
  }

  /**
   * Empties the cache.
   */

  public synchronized void clear ()
    throws MasterCacheException
  {
    try
      {
        this.masterCache.clear();
      }
    catch (Exception exception)
      {
        throw new MasterCacheException("Clearing cache failed", exception);
      }
  }

  /**
   * Returns the files of the cached masters.
   */

  public File[] list ()
    throws MasterCacheException
  {
    try
      {
        Set keys = this.masterCache.keySet();
        return (File[])(keys.toArray(new File[keys.size()]));
      }
    catch (Exception exception)
      {
        throw new MasterCacheException("Creating key list failed", exception);
      }
  }

  /**
   * Returns the creation time of the cached master object corresponding to the specified
   * master file, or -1 if the master file is not cached.
   */

  public long getCreationTime (File file)
  {
    return
      (this.masterCache.containsKey(file)
       ? ((CacheRecord)this.masterCache.get(file)).created
       : -1);
  }

  /**
   * Creates a new instance
   */

  public MasterCache ()
    throws MasterCacheException
  {
    try
      {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        this.documentBuilder = documentBuilderFactory.newDocumentBuilder();
        this.documentBuilder.setErrorHandler(new AbortingSAXErrorHandler());
      }
    catch (Throwable throwable)
      {
        throw new MasterCacheException("Failed to initialize master cache", throwable);
      }
  }
}
