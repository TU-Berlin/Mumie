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
import java.net.URI;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Hashtable;

/**
 *
 * <p>
 *   Helper class to keep track of the dependencies of resources.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: DependencyHelper.java,v 1.1 2006/09/20 23:20:19 rassy Exp $</code>
 */

public abstract class DependencyHelper
{
  /**
   * <p>
   *   Represents the value of a given URI in the dependency map
   *   ({@link #dependencyMap}). Simply a comination of two fields:
   * </p>
   * <ul>
   *   <li><code>dependencies</code> - List of URIs from which the resource with the given
   *   URI depends.</li> 
   *   <li><code>lastChecked</code> - Time when the given URI was last checked.</li>
   * </ul>
   */

  protected class DependencyMapValue
  {
    List dependencies;
    long lastChecked;
  }

  /**
   * Describes the dependencies of resources. Each key is a
   * {@link java.io.URI URI} object, each value a
   * {@link DependencyMapValue DependencyMapValue} object. The
   * former specifies the resource, the latter is a comination of a list of
   * {@link java.io.URI URIs} specifying resources the former resource depends upon and a
   * timestamp when the former resource was last checked.
   */

  protected Hashtable dependencyMap;

  /**
   * Finds the direct dependencies of the resource specified by <code>uri</code>.
   */

  protected abstract List updateDependencies (URI uri)
    throws DependencyUpdateException;

  /**
   * Returns <code>true</code> if the cached dependencies of <code>uri</code> need to be
   * updated.
   */

  protected boolean dependenciesNeedUpdate (URI uri)
  {
    if ( this.dependencyMap.containsKey(uri) )
      {
	DependencyMapValue data = (DependencyMapValue)this.dependencyMap.get(uri);
	return ( data.lastChecked <= (new File(uri)).lastModified() );
      }
    else
      return true;
  }

  /**
   * Returns the dependencies of the resource specified by <code>uri</code>. If the
   * dependencies are outdated or do not exist yet, they are (re)created.
   */

  public List getDependencies (URI uri)
    throws DependencyUpdateException
  {
    if ( this.dependenciesNeedUpdate(uri) )
      {
	DependencyMapValue data = new DependencyMapValue();
	data.lastChecked = System.currentTimeMillis();
	data.dependencies = this.updateDependencies(uri);
	this.dependencyMap.put(uri, data);
      }
    return ((DependencyMapValue)this.dependencyMap.get(uri)).dependencies;
  }

  /**
   * Recursively adds the dependencies of <code>uri</code> to <code>list</code>. This means
   * that not only the dependencies of <code>uri</code> itself are added, but also the
   * dependencies of the resource <code>uri</code> depends from, and their dependencies,
   * and so on.
   */

  protected void addDependencies (URI uri, List list)
    throws DependencyUpdateException
  {
    List dependencies = this.getDependencies(uri);
    Iterator iterator = dependencies.iterator();
    while ( iterator.hasNext() )
      {
	URI requiredURI = (URI)iterator.next();
	if ( ! list.contains(requiredURI) )
	  {
	    list.add(requiredURI);
	    this.addDependencies(requiredURI, list);
	  }
      }
  }

  /**
   * Returns the full dependencies of the resource specified by <code>uri</code>. The
   * <em>full dependencies</em> are: <code>uri</code> itself, the dependencies of
   * <code>uri</code>, the dependencies of the dependencies, and so on.
   */

  public List getFullDependencies (URI uri)
    throws DependencyUpdateException
  {
    List dependencies = new ArrayList();
    dependencies.add(uri);
    this.addDependencies(uri, dependencies);
    return dependencies;
  }

  /**
   * Returns the absolute last modification time of the resource specified be
   * <code>uri</code>. This is the latest modification time of any of the full dependencies
   * of <code>uri</code>. See {@link #getFullDependencies getFullDependencies} for the term
   * "full dependencies".
   */

  public long getAbsoluteLastModified (URI uri)
    throws DependencyUpdateException
  {
    List dependencies = this.getFullDependencies(uri);
    long absoluteLastModified = -1;
    Iterator iterator = dependencies.iterator();
    while ( iterator.hasNext() )
      {
	long lastModified = (new File((URI)iterator.next())).lastModified();
	if ( lastModified > absoluteLastModified )
	  absoluteLastModified = lastModified;
      }
    return absoluteLastModified;
  }

  /**
   * Returns the last time the dependencies of the specified URI have been checked, or -1 if
   * the specified URI is not contained in the dependency map.
   */

  public long getLastChecked (URI uri)
  {
    return
      (this.dependencyMap.containsKey(uri)
       ? ((DependencyMapValue)this.dependencyMap.get(uri)).lastChecked
       : -1);
  }

  /**
   * Returns a list of all URIs the dependencies of which are currently cached by this
   * dependency helper.
   */

  public List getURIs ()
  {
    List uris = new ArrayList ();
    Iterator iterator = this.dependencyMap.keySet().iterator();
    while ( iterator.hasNext() )
      uris.add(iterator.next());
    return uris;
  }

  /**
   * Creates a new <code>DependencyHelper</code> object.
   */

  protected DependencyHelper ()
  {
    this.dependencyMap = new Hashtable();
  }
}
