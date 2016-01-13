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

import java.util.Hashtable;

/**
 * <p>
 *   A group of pools of reusable objects.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 *
 * @version <span class="file">$Id: ResourcePools.java,v 1.2 2006/11/07 00:14:28 rassy Exp $</span>
 */

public abstract class ResourcePools
{
  /**
   * Maps keys to the corresponding resource pools.
   */

  protected Hashtable pools;

  /**
   * Returns a newly created pool for key <code>key</code>.
   */

  protected abstract ResourcePool newResourcePool (Object key)
    throws ResourcePoolException;

  /**
   * Returns all keys.
   */

  public synchronized Object[] getKeys ()
  {
    return this.pools.keySet().toArray();
  }

  /**
   * Returns the key for <code>resource</code>
   */

  protected abstract Object getKeyOf (Object resource)
    throws ResourcePoolException;

  /**
   * Returns <code>true</code> if this <code>ResourcePools</code> object has a pool for
   * <code>key</code>.
   */

  public synchronized boolean hasPool (Object key)
  {
    return this.pools.containsKey(key);
  }

  /**
   * Returns the pool for <code>key</code>.
   */

  public synchronized ResourcePool getPool (Object key)
    throws ResourcePoolException
  {
    if ( ! this.hasPool(key) )
      this.pools.put(key, this.newResourcePool(key));
    return (ResourcePool)this.pools.get(key);
  }

  /**
   * Returns a resource for <code>key</code>.
   */

  public synchronized Object get (Object key)
    throws ResourcePoolException
  {
    return this.getPool(key).get();
  }

  /**
   * Puts <code>resource</code> into the corresponding pool.
   */

  public synchronized void put (Object resource)
    throws ResourcePoolException
  {
    this.getPool(this.getKeyOf(resource)).put(resource);
  }

  /**
   * Creates a new <code>ResourcePools</code>.
   */

  public ResourcePools ()
  {
    this.pools = new Hashtable();
  }
}
