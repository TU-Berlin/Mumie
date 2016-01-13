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

import net.mumie.srv.util.Identifyable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

public abstract class AbstractMumieService extends AbstractLogEnabled
  implements LookupNotifyable, Identifyable
{
  /**
   * The status object of this instance.
   */

  protected ServiceInstanceStatus instanceStatus = null;

  /**
   * Writes a debug log message. The message is prepended by
   * <code>this.getIdentification() + ": "</code>.
   */

  protected void logDebug (String message)
  {
    this.getLogger().debug(this.getIdentification() + ": " + message);
  }

  /**
   * Writes a warn log message. The message is prepended by
   * <code>this.getIdentification() + ": "</code>.
   */

  protected void logWarn (String message)
  {
    this.getLogger().warn(this.getIdentification() + ": " + message);
  }

  /**
   * Writes a warn log message. The message is prepended by
   * <code>this.getIdentification() + ": "</code>.
   */

  protected void logWarn (String message, Throwable throwable)
  {
    this.getLogger().warn(this.getIdentification() + ": " + message, throwable);
  }

  /**
   * Writes an error log message. The message is prepended by
   * <code>this.getIdentification() + ": "</code>.
   */

  protected void logError (String message)
  {
    this.getLogger().error(this.getIdentification() + ": " + message);
  }

  /**
   * Writes an error log message. The message is prepended by
   * <code>this.getIdentification() + ": "</code>.
   */

  protected void logError (String message, Throwable throwable)
  {
    this.getLogger().error(this.getIdentification() + ": " + message, throwable);
  }

  /**
   * Notifies this instance that it has been looked-up.
   */

  public void notifyLookup (String ownerLabel)
  {
    final String METHOD_NAME = "notifyLookup";
    this.logDebug(METHOD_NAME + " 1/2: Started. ownerLabel = " + ownerLabel);
    this.instanceStatus.notifyLookup(ownerLabel);
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Notifies this instance that it has been released.
   */

  public void notifyRelease (String ownerLabel)
  {
    final String METHOD_NAME = "notifyRelease";
    this.logDebug(METHOD_NAME + " 1/2: Started. ownerLabel = " + ownerLabel);
    this.instanceStatus.notifyRelease();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Returns the short (non-qualified) name of the class plus <code>"#"</code> plus the
   * instance id.
   */

  public String getIdentification ()
  {
    return this.getShortClassName() + "#" + this.instanceStatus.getInstanceId();
  }

  /**
   * Auxiliary method, returns the short (non-qualified) name of the class. This is needed
   * in the default implementation of {@link #getIdentification getIdentification}. Also
   * used in {@link AbstractJapsServiceable AbstractJapsServiceable} to generate the owner
   * label.
   */

  protected String getShortClassName ()
  {
    String className = this.getClass().getName();
    return className.substring(className.lastIndexOf('.') + 1);
    // Note: This is correct even if className contains no dot.
  }
}
