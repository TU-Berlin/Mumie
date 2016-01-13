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

package net.mumie.srv.service;

import java.io.PrintStream;

/**
 * <p>
 *   Represents the status of a single instance of a service class.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: ServiceInstanceStatus.java,v 1.1 2008/09/09 21:55:09 rassy Exp $</code>
 */

public class ServiceInstanceStatus implements Comparable 
{
  // --------------------------------------------------------------------------------
  // Global constants and variables
  // --------------------------------------------------------------------------------

  /**
   * The {@link #lifecycleStatus} variable should be set to this value when the contructor
   * is called. 
   */

  public static final int CREATED = 0;

  /**
   * The {@link #lifecycleStatus} variable should be set to this value when the
   * <code>start</code> method is called.
   */

  public static final int STARTED = 1;

  /**
   * The {@link #lifecycleStatus} variable should be set to this value when the
   * <code>recycle</code> method is called.
   */

  public static final int RECYCLED = 2;

  /**
   * The {@link #lifecycleStatus} variable should be set to this value when the
   * <code>stop</code> method is called.
   */

  public static final int STOPPED = 3;

  /**
   * The {@link #lifecycleStatus} variable should be set to this value when the
   * <code>dispose</code> method is called.
   */

  public static final int DISPOSED = 4;

  /**
   * The {@link #lifecycleStatus} variable should be set to this value when the
   * the service instance is looked up and the service class implements the
   * {@link LookupNotifyable LookupNotifyable} interface.
   */

  public static final int LOOKEDUP = 5;

  /**
   * The {@link #lifecycleStatus} variable should be set to this value when the
   * the service instance is released and the service class implements the
   * {@link LookupNotifyable LookupNotifyable} interface.
   */

  public static final int RELEASED = 6;

  /**
   * The lifecycle status.
   */

  protected int lifecycleStatus = -1;

  /**
   * Instance id.
   */

  protected long instanceId = -1;

  /**
   * Counts how often the <code>start</code> method has been called.
   */

  protected long numberOfStarts = 0;

  /**
   * Counts how often the <code>stop</code> method has been called.
   */

  protected long numberOfStops = 0;

  /**
   * Counts how often the service instance has been looked-up.
   */

  protected long numberOfLookups = 0;

  /**
   * Counts how often the service instance has been released.
   */

  protected long numberOfReleases = 0;

  /**
   * Counts how often the <code>recycle</code> method has been called.
   */

  protected long numberOfRecycles = 0;

  /**
   * Time of the first <code>start</code> method call.
   */

  protected long firstStartTime = -1;

  /**
   * Time of the last <code>stop</code> method call.
   */

  protected long lastStopTime = -1;
  
  /**
   * Time of the last <code>recycle</code> method call.
   */

  protected long lastRecycleTime = -1;
  
  /**
   * Time of the last lookup.
   */

  protected long lastLookupTime = -1;
  
  /**
   * Time of the last release.
   */

  protected long lastReleaseTime = -1;
  
  /**
   * Time of the <code>dispose</code> method call.
   */

  protected long disposeTime = -1;

  /**
   * <p>
   *   A string that identifies the "owner" of the tracked service instance. This is the
   *   object that obtained the service instance by a lookup call. The service class must
   *   implement the {@link LookupNotifyable LookupNotifyable} interface for this feature to
   *   work.
   * </p>
   * <p>
   *   If the tracked service instance has no owner, or if the service does not implement
   *   the {@link LookupNotifyable LookupNotifyable} interface, this variable is
   *   <code>null</code>.
   * </p>
   */

  protected String ownerLabel = null;

  /**
   * The service status associated with this instance status.
   */

  protected ServiceStatus serviceStatus = null;

  // --------------------------------------------------------------------------------
  // Methods to notify lifecycle method calls
  // --------------------------------------------------------------------------------

  /**
   * Notifies a constructor call. Increases the instance counter (in the service status) and
   * sets the instance id. 
   */

  public void notifyCreation ()
  {
    this.instanceId = this.serviceStatus.increaseNumberOfCreatedInstances();
    this.lifecycleStatus = CREATED;
  }

  /**
   * Notifies a <code>start</code> method call.
   */

  public void notifyStart ()
  {
    this.numberOfStarts++;
    if ( this.numberOfStarts == 1 )
      this.firstStartTime = System.currentTimeMillis();
    this.lifecycleStatus = STARTED;
    this.serviceStatus.increaseNumberOfStartedInstances();
  }

  /**
   * Notifies a <code>stop</code> method call.
   */

  public void notifyStop ()
  {
    this.numberOfStops++;
    this.lastStopTime = System.currentTimeMillis();
    this.lifecycleStatus = STOPPED;
    this.serviceStatus.increaseNumberOfStoppedInstances();
  }

  /**
   * Notifies a <code>dispose</code> method call.
   */

  public void notifyDispose ()
  {
    this.disposeTime = System.currentTimeMillis();
    this.lifecycleStatus = DISPOSED;
    this.serviceStatus.increaseNumberOfDisposedInstances();
  }

  /**
   * Notifies a <code>recycle</code> method call.
   */

  public void notifyRecycle ()
  {
    this.numberOfRecycles++;
    this.lastRecycleTime = System.currentTimeMillis();
    this.lifecycleStatus = RECYCLED;
    this.serviceStatus.increaseNumberOfRecycledInstances();
  }

  /**
   * Notifies a lookup.
   */

  public void notifyLookup (String ownerLabel)
  {
    this.numberOfLookups++;
    this.lastLookupTime = System.currentTimeMillis();
    this.lifecycleStatus = LOOKEDUP;
    this.ownerLabel = (ownerLabel != null ? ownerLabel : "unknown");
    this.serviceStatus.increaseNumberOfLookedupInstances();
  }

  /**
   * Notifies a release.
   */

  public void notifyRelease ()
  {
    this.numberOfReleases++;
    this.lastReleaseTime = System.currentTimeMillis();
    this.lifecycleStatus = RELEASED;
    this.ownerLabel = null;
    this.serviceStatus.increaseNumberOfReleasedInstances();
  }

  // --------------------------------------------------------------------------------
  // Get methods
  // --------------------------------------------------------------------------------

  /**
   * Returns the lifecycle status.
   */

  public int getLifecycleStatus ()
  {
    return this.lifecycleStatus;
  }

  /**
   * Returns the lifecycle status as a string name
   */

  public String getLifecycleStatusName ()
  {
    return getNameForLifecycleStatus(this.lifecycleStatus);
  }

  /**
   * Returns the instance id.
   */

  public long getInstanceId ()
  {
    return this.instanceId;
  }

  /**
   * Returns the number of starts.
   */

  public long getNumberOfStarts ()
  {
    return this.numberOfStarts;
  }

  /**
   * Returns the number of stops.
   */

  public long getNumberOfStops ()
  {
    return this.numberOfStops;
  }

  /**
   * Returns the number of recycles.
   */

  public long getNumberOfRecycles ()
  {
    return this.numberOfRecycles;
  }

  /**
   * Returns the number of lookups.
   */

  public long getNumberOfLookups ()
  {
    return this.numberOfLookups;
  }

  /**
   * Returns the number of releases.
   */

  public long getNumberOfReleases ()
  {
    return this.numberOfReleases;
  }

  /**
   * Returns the time of the first <code>start</code> method call.
   */

  public long getFirstStartTime ()
  {
    return this.firstStartTime;
  }

  /**
   * Returns the time of the last <code>stop</code> method call.
   */

  public long getLastStopTime ()
  {
    return this.lastStopTime;
  }

  /**
   * Returns the time of the last <code>recycle</code> method call.
   */

  public long getLastRecycleTime ()
  {
    return this.lastRecycleTime;
  }

  /**
   * Returns the time of the last lookup.
   */

  public long getLastLookupTime ()
  {
    return this.lastLookupTime;
  }

  /**
   * Returns the time of the last release.
   */

  public long getLastReleaseTime ()
  {
    return this.lastReleaseTime;
  }

  /**
   * Returns the time of the <code>dispose</code> method call.
   */

  public long getDisposeTime ()
  {
    return this.disposeTime;
  }

  /**
   * <p>
   *   Returns the owner label, a string that identifies the "owner" of the tracked service
   *   instance. This is the object that obtained the service instance by a lookup call. The
   *   service class must implement the {@link LookupNotifyable LookupNotifyable} interface
   *   for this feature to work.
   * </p>
   * <p>
   *   If the tracked service instance has no owner, or if the service does not implement
   *   the {@link LookupNotifyable LookupNotifyable} interface, this <code>null</code> is
   *   returned.
   * </p>
   */

  public String getOwnerLabel ()
  {
    return this.ownerLabel;
  }

  // --------------------------------------------------------------------------------
  // Constructor
  // --------------------------------------------------------------------------------

  /**
   * Creates a new instance status, sets the service status, and registers the instance
   * status with the service status.
   */

  public ServiceInstanceStatus (ServiceStatus serviceStatus)
  {
    this.serviceStatus = serviceStatus;
    this.serviceStatus.register(this);
  }

  // --------------------------------------------------------------------------------
  // Comparable implementation
  // --------------------------------------------------------------------------------

  /**
   * Returns 1, -1, or 0 depending on whether the instance id of this object is greater,
   * less, or equal to that of the specified object, respectively. The specified object must
   * be an <code>ServiceInstanceStatus</code>, otherwise, an <code>IllegalArgumentException</code>
   * is thrown.
   */

  public int compareTo (Object object)
  {
    if ( ! ( object instanceof ServiceInstanceStatus ) )
      throw new IllegalArgumentException
        ("Can not compare to object: " + object + " (Not an ServiceInstanceStatus)");
    ServiceInstanceStatus other = (ServiceInstanceStatus)object;
    return
      (this.instanceId > other.getInstanceId()
       ? 1
       : (this.instanceId < other.getInstanceId()
          ? -1
          : 0));
  }

  // --------------------------------------------------------------------------------
  // Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * Returns a string name for the specified lifecycle status.
   */

  public static String getNameForLifecycleStatus (int lifecycleStatus)
  {
    switch ( lifecycleStatus )
      {
      case CREATED: return "CREATED";
      case STARTED: return "STARTED";
      case RECYCLED: return "RECYCLED";
      case STOPPED: return "STOPPED";
      case DISPOSED: return "DISPOSED";
      case LOOKEDUP: return "LOOKEDUP";
      case RELEASED: return "RELEASED";
      default: return "UNKNOWN";
      }
  }

}
