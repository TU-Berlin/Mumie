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

package net.mumie.util;

import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.transform.Transformer;

/**
 * <p>
 *   A pool of reusable objects.
 * </p>
 * <p>
 *   The usage of a <code>ResourcePool</code> is simple: to get an object, use the
 *   {@link #get get} method; to return it to the pool, use the {@link #put put}
 *   method. The remaining public methods control the maximum pool size
 *   ({@link #setMaxSize setMaxSize}), refresh the pool ({@link #refresh refresh}),
 *   empty the pool ({@link #clear clear}), or return status information.
 * </p>
 * <p>
 *   This is an abstract class. Extending classes must at least overwrite
 *   {@link #newResource newResource}. This method creates a new pooled object. If the
 *   pooled objects are created from a "source" in any sense, it might be necessary to
 *   overwrite {@link #getLastModificationTimeOfSource getLastModificationTimeOfSource},
 *   too. This method returns the last modification time of the source. The pool uses this
 *   value to decide whether a pooled object is still up-to-date or not. There are two
 *   further methods an extending class might overwrite:
 *   {@link #beginPutHook beginPutHook} and {@link #endGetHook endGetHook}. They are
 *   called immediately before an object enters/leaves the pool, respectively. Their purpose
 *   is to give an opportunity to cleanup/setup the object on this occasions.
 * </p>
 * <h4>Example for extending</h4>
 * <p>
 *   Say you want to pool XSL {@link Transformer Transformer} objects build from a certain
 *   XSL document stored in a file. The corresponding class extending
 *   <code>ResourcePool</code> might look like the following:
 * </p>
 * <pre>
 *   public class TransformerPool extends ResourcePool
 *   {
 *     protected File source;
 *   
 *     protected TransformerFactory transformerFactory;
 *   
 *     public TransformerPool (File source)
 *       throws TransformerFactoryConfigurationError
 *     {
 *       this.source = source;
 *       this.transformerFactory = TransformerFactory.newInstance();
 *     }
 *   
 *     protected Object newResource ()
 *          throws ResourcePoolException
 *     {
 *       try
 *         {
 *           return this.transformerFactory.newTransformer(new StreamSource(this.source));
 *         }
 *       catch (Exception exception)
 *         {
 *           throw new ResourcePoolException(exception);
 *         }
 *     }
 *     
 *     protected long getLastModificationTimeOfSource ()
 *     {
 *       return this.source.lastModified();
 *     }
 *   
 *     protected void beginPutHook (Object resource)
 *     {
 *       ((Transformer)resource).clearParameters();
 *     }
 *   }
 * </pre>
 * <p>
 *   In this example, the pooled objects are created from a source, i.e., the XSL
 *   file. Thus, we overwrote the 
 *   {@link #getLastModificationTimeOfSource getLastModificationTimeOfSource} method in
 *   addition to the {@link #newResource newResource} method. Furthermore, we overwrote
 *   {@link #beginPutHook beginPutHook} to remove any parameters from the transformer befor
 *   it is returned to the pool (in the real world, the {@link #beginPutHook beginPutHook}
 *   method should take into account other things like output properties and error listener,
 *   too, but in the current context this is not important).
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 *
 * @version <span class="file">$Id: ResourcePool.java,v 1.3 2006/11/07 00:14:28 rassy Exp $</span>
 */

public abstract class ResourcePool
{
  // --------------------------------------------------------------------------------
  // Gloabl variables and constants
  // --------------------------------------------------------------------------------

  /**
   * Contains the pooled resource objects.
   */

  protected ArrayList pool;

  /**
   * The maximal size of the pool. (I.e., the maximal number of resources in it.)
   */

  protected int maxSize = 20;

  /**
   * Number of newly created resources. (For status information.)
   */

  protected long counterNew = 0;

  /**
   * Number of resources returned to the pool. (For status information.)
   */

  protected long counterPut = 0;

  /**
   * Number of resources delivered to a user. (For status information.)
   */

  protected long counterGet = 0;

  /**
   * Number removed resources. (For status information.)
   */

  protected long counterRemove = 0;

  // --------------------------------------------------------------------------------
  // Resource get / put
  // --------------------------------------------------------------------------------

  /**
   * Returns a resource from the pool.
   */

  public synchronized Object get ()
    throws ResourcePoolException
  {
    this.refresh();
    Object resource;
    if ( this.pool.isEmpty() )
      {
	resource = this.newResource();
	this.counterNew++;
      }
    else
      {
	resource = this.pool.get(0);
	this.pool.remove(resource);
      }
    this.endGetHook(resource);
    this.counterGet++;
    return resource;
  }

  /**
   * Puts <code>resource</code> into the pool.
   */

  public synchronized void put (Object resource)
    throws ResourcePoolException
  {
    final String METHOD_NAME = "put(Objct resource)";
    this.beginPutHook(resource);
    if ( this.pool.size() <= this.maxSize )
      {
	this.pool.add(resource);
	this.counterPut++;
      }
  }

  // --------------------------------------------------------------------------------
  // Resource creation
  // --------------------------------------------------------------------------------

  /**
   * Returns a newly created resource.
   */

  protected abstract Object newResource ()
    throws ResourcePoolException;

  // --------------------------------------------------------------------------------
  // Creation / modification times
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Returns the creation time of <code>resource</code>. Can also return <code>-1</code> to
   *   indicate that the creation time is unknown.
   * </p>
   * <p>
   *   This implementation always returns <code>-1</code>.
   * </p>
   */

  protected long getCreationTimeOf (Object resource)
    throws ResourcePoolException
  {
    return -1;
  }

  /**
   * <p>
   *   Returns the last modification time of the source of the pooled resources. Can also
   *   return <code>-1</code> to indicate that the source never changes or there is no source
   *   for the pooled resources.
   * </p>
   * <p>
   *   This implementation always returns <code>-1</code>.
   * </p>
   */

  protected long getLastModificationTimeOfSource ()
    throws ResourcePoolException
  {
    return -1;
  }

  // --------------------------------------------------------------------------------
  // Hooks
  // --------------------------------------------------------------------------------

  /**
   * Called before a resource is returned to the pool.
   */

  protected void beginPutHook (Object resource)
    throws ResourcePoolException
  {
  }

  /**
   * Called before a resource is handed over to the user.
   */

  protected void endGetHook (Object resource)
    throws ResourcePoolException
  {
  }

  // --------------------------------------------------------------------------------
  // Refresh and clear
  // --------------------------------------------------------------------------------

  /**
   * Removes outdated resources from the pool.
   */

  public synchronized void refresh ()
    throws ResourcePoolException
  {
    long lastModified = this.getLastModificationTimeOfSource();
    if ( lastModified != -1 )
      {
	Iterator iterator = this.pool.iterator();
	int counter = 0;
	while ( iterator.hasNext() )
	  {
	    Object resource = iterator.next();
	    long creationTime = this.getCreationTimeOf(resource);
	    if ( this.getCreationTimeOf(resource) <= lastModified )
	      {
		// this.pool.remove(resource); // Caused ConcurrentModificationException's
		iterator.remove();
		counter++;
		this.counterRemove++;
	      }
	  }
      }
  }

  /**
   * Removes all resources from the pool.
   */

  public synchronized void clear ()
  {
    this.pool.clear();
  }

  // --------------------------------------------------------------------------------
  // Maximum pool size
  // --------------------------------------------------------------------------------

  /**
   * Get method for {@link #maxSize maxSize}.
   */

  public int getMaxSize ()
  {
    return this.maxSize;
  }

  /**
   * Set method for {@link #maxSize maxSize}.
   */

  public void setMaxSize (int maxSize)
  {
    this.maxSize = maxSize;
  }

  // --------------------------------------------------------------------------------
  // Getting status information
  // --------------------------------------------------------------------------------

  /**
   * Returns the current size of the pool. (I.e., the current number of resources in it.)
   */

  public int getSize ()
  {
    return this.pool.size();
  }

  /**
   * Get method for {@link #counterNew counterNew}.
   */

  public long getCounterNew ()
  {
    return this.counterNew;
  }

  /**
   * Get method for {@link #counterPut counterPut}.
   */

  public long getCounterPut ()
  {
    return this.counterPut;
  }

  /**
   * Get method for {@link #counterGet counterGet}.
   */

  public long getCounterGet ()
  {
    return this.counterGet;
  }

  /**
   * Get method for {@link #counterRemove counterRemove}.
   */

  public long getCounterRemove ()
  {
    return this.counterRemove;
  }

  /**
   * Creates a new <code>ResourcePool</code>.
   */

  public ResourcePool ()
  {
    this.pool = new ArrayList();
  }
}
