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

package net.mumie.util.io;

import java.io.File;
import java.io.FileFilter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * <p>
 *   Caches the entries of a directory and all its descendant directories. This class may be
 *   useful to improve performance if you have to search large directory trees. It caches
 *   the entries of each directory of the tree in memory so searching is faster. The cache
 *   is updated automatically if necessary. If desired, the files in the cache (directories
 *   and non-directories) can be confined to a certain subset by a
 *   {@link FileFilter FileFilter}.
 * </p>
 * <p>
 *   Please note: Since the cache must be filled, you only profit from the speed improvement
 *   if you search the directory tree more then once.
 * </p>
 * <p>
 *   The cached entries may be obtained by the <code>getFiles</code> or
 *   <code>getUris</code> methods (see below). The first ones return the entries as
 *   {@link File File} objects, the latter ones as {@link URI URI} objects.
 * </p>
 * <p>
 *   The <code>CachedDirEntries</code> class models a directory tree by using itself
 *   recursively: Primarily, a <code>CachedDirEntries</code> instancs belongs to a single
 *   directory,  but contains references to other <code>CachedDirEntries</code> instances
 *   which correspond to its subdirectories, and so on.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: CachedDirEntries.java,v 1.4 2007/06/05 08:57:39 rasched Exp $</code>
 */

public class CachedDirEntries
{
  /**
   * The directory the entries of which are cached by this instance.
   */

  protected File dir;

  /**
   * The time this cache was last updated.
   */

  protected long lastUpdated;

  /**
   * The children of {@link #dir dir}.
   */

  protected File[] children;

  /**
   * Maps each directory in {@link #children children} to a <code>CachedDirEntries</code>
   * object.
   */

  protected Map cachedChildDirEntries = new Hashtable();

  /**
   * The file filter of this instance.
   */

  protected FileFilter fileFilter;

  /**
   * Returns the directory to which this instance corresponds. This is the directory the
   * entries of which are cached by this instance. 
   */

  public File getDir ()
  {
    return this.dir;
  }

  /**
   * Returns the time this cache was last updated.
   */

  public long getLastUpdated ()
  {
    return this.lastUpdated;
  }

  /**
   * Updates this cache. Works recursively, i.e., child caches are updated, too. If
   * <code>force</code> is <code>true</code>, the cache is updated even if the last
   * modification time of the directory is newer then the last update time of the cache. If
   * <code>force</code> is <code>false</code>, the cache is updated only if the last
   * modification time of the directory is not newer then the last update time of the
   * cache. The vale of the <code>force</code> flag is inherited in the recursion mentioned
   * above.
   */

  public void update (boolean force)
  {
    if ( force || ( this.lastUpdated < this.dir.lastModified() ) )
      {
	// Update children:
	this.children = this.dir.listFiles(this.fileFilter);

        // Update cachedChildDirEntries I: Remove entries for children that doesn't exist
	// any longer: 
	Set keySet = this.cachedChildDirEntries.keySet();
	File[] keys = (File[])keySet.toArray(new File[keySet.size()]);
	for (int i = 0; i < keys.length; i++)
	  {
	    boolean exists = false;
	    for (int k = 0; ( ! exists ) && ( k < this.children.length ); k++)
	      exists = ( this.children[k].isDirectory() && this.children[k].equals(keys[i]) );
	    if ( ! exists ) this.cachedChildDirEntries.remove(keys[i]);
	  }

        // Update cachedChildDirEntries II: Creating entries for new children:
	for (int k = 0; k < this.children.length; k++)
	  {
	    File child = this.children[k];
	    if ( child.isDirectory() )
	      {
		if ( ! this.cachedChildDirEntries.containsKey(child) )
		  this.cachedChildDirEntries.put
                    (child, new CachedDirEntries(child, this.fileFilter));
	      }
	  }

	// Set lastUpdated:
	this.lastUpdated = System.currentTimeMillis();
      }
    // Recursively update children:
    for (int k = 0; k < this.children.length; k++)
      {
	File child = this.children[k];
	if ( this.cachedChildDirEntries.containsKey(child) )
	  ((CachedDirEntries)this.cachedChildDirEntries.get(child)).update(force);
      }
  }

  /**
   * Same as {@link #update(boolean) update(false)}.
   */

  public void update ()
  {
    this.update(false);
  }

  /**
   * Auxiliary method. Adds the children of the directory of this instance to
   * <code>files</code>.
   */

  protected void enterFiles (List files)
  {
    for (int i = 0; i < this.children.length; i++)
      {
	File child = this.children[i];
	files.add(child);
	if ( this.cachedChildDirEntries.containsKey(child) )
	  ((CachedDirEntries)this.cachedChildDirEntries.get(child)).enterFiles(files);
      }
  }

  /**
   * Auxiliary method. Converts the children of the root directory to {@link java.net.URI URI}s
   * and adds them to <code>uris</code>.
   */

  protected void enterUris (List uris)
  {
    for (int i = 0; i < this.children.length; i++)
      {
	File child = this.children[i];
	URI uri = child.toURI();
	uris.add(uri);
	if ( this.cachedChildDirEntries.containsKey(child) )
	  ((CachedDirEntries)this.cachedChildDirEntries.get(child)).enterUris(uris);
      }
  }

  /**
   * Returns a list of all files in this cache and all descendant caches. This method is
   * synchronized.
   */

  public synchronized List getFiles (boolean force)
  {
    this.update(force);
    List files = new ArrayList();
    this.enterFiles(files);
    return files;
  }

  /**
   * Same as {@link #getFiles(boolean) getFiles(false)}.
   */

  public List getFiles ()
  {
    return this.getFiles(false);
  }

  /**
   * Returns a list of all URI's in this cache and all descendant caches. The elements of
   * the returned list are objects of type {@link java.net.URI URI}. This method is
   * synchronized.
   */

  public synchronized List getUris (boolean force)
  {
    this.update(force);
    List uris = new ArrayList();
    this.enterUris(uris);
    return uris;
  }

  /**
   * Same as {@link #getUris(boolean) getUris(false)}.
   */

  public List getUris ()
  {
    return this.getUris(false);
  }

  /**
   * Returns a <code>CachedDirEntries</code> for a child directory of the directory of this
   * instance.
   */

  public CachedDirEntries getChildTree (File childDir)
  {
    CachedDirEntries childTree = (CachedDirEntries)(this.cachedChildDirEntries.get(childDir));
    if ( childTree == null )
      throw new IllegalArgumentException
        ("getChildTree: can not find child directory: " + childDir);
    return childTree;
  }

  /**
   * Returns a <code>CachedDirEntries</code> for a child directory of the directory of this
   * instance.
   */

  public CachedDirEntries getChildTree (String childDirName)
  {
    return this.getChildTree(new File(this.dir, childDirName));
  }

  /**
   * Returns a <code>CachedDirEntries</code> that represents descentant directory of the
   * directory of this instance.
   */

  public CachedDirEntries getSubTree (String path)
  {
    CachedDirEntries tree = this;
    StringTokenizer pathTokenizer = new StringTokenizer(path, File.separator);
    while ( pathTokenizer.hasMoreTokens() )
      tree = tree.getChildTree(pathTokenizer.nextToken());
    return tree;
  }

  /**
   * Creates a new <code>CachedDirEntries</code>.
   */

  public CachedDirEntries (File dir, FileFilter fileFilter)
  {
    if ( ! dir.isDirectory() )
      throw new IllegalArgumentException("Not a directory: " + dir);
    this.dir = dir;
    this.fileFilter = fileFilter;
  }

  /**
   * Creates a new <code>CachedDirEntries</code>.
   */

  public CachedDirEntries (File dir)
  {
    this
      (dir,
       new FileFilter () { public boolean accept (File file) { return true; } });
  }

  /**
   * Creates a new <code>CachedDirEntries</code>.
   */

  public CachedDirEntries (String dirName)
  {
    this(new File(dirName));
  }
}
