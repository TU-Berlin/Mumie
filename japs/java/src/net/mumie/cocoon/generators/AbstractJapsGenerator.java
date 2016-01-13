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

package net.mumie.cocoon.generators;

import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.util.Identifyable;
import org.apache.cocoon.generation.AbstractGenerator;

/**
 * Abstract base class for JAPS generators. Implements
 * {@link LookupNotifyable LookupNotifyable} and
 * {@link Identifyable Identifyable}.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: AbstractJapsGenerator.java,v 1.8 2008/04/30 11:04:12 rassy Exp $</code>
 */

public abstract class AbstractJapsGenerator extends AbstractGenerator
  implements Identifyable
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
   * Writes a debug log message. The message is prepended by
   * <code>this.getIdentification() + ": "</code>.
   */

  protected void logDebug (String message, Throwable throwable)
  {
    this.getLogger().debug(this.getIdentification() + ": " + message, throwable);
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

  protected void logError (String message, Throwable throwable)
  {
    this.getLogger().error(this.getIdentification() + ": " + message, throwable);
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
   * in the default implementation of {@link #getIdentification getIdentification}.
   */

  protected String getShortClassName ()
  {
    String className = this.getClass().getName();
    return className.substring(className.lastIndexOf('.') + 1);
    // Note: This is correct even if className contains no dot.
  }
}
