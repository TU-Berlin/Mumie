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

import net.mumie.cocoon.util.CoursesAndTutorialsIndex;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.util.ParamUtil;
import org.apache.cocoon.ProcessingException;

/**
 * Generates a list of tutorials for a given class.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: CoursesAndTutorialsIndexGenerator.java,v 1.1 2007/09/24 15:41:48 rassy Exp $</code>
 */

public class CoursesAndTutorialsIndexGenerator extends ServiceableJapsGenerator
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus =
    new ServiceStatus(CoursesAndTutorialsIndexGenerator.class);

  // --------------------------------------------------------------------------------
  // Lifecycle related methods
  // --------------------------------------------------------------------------------

  /**
   * Creates a new instance.
   */

  public CoursesAndTutorialsIndexGenerator ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Recycles this instance. Actually, does nothing but calling the superclass recycle
   * method and notifying the instance status object about the recycling.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    super.recycle();
    this.instanceStatus.notifyRecycle();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Disposes this instance. Actually, does nothinmg but calling the superclass dispose
   * method and notifying the instance status object about the disposing.
   */

  public void dispose ()
  {
    final String METHOD_NAME = "dispose";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    super.dispose();
    this.instanceStatus.notifyDispose();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // Generate method
  // --------------------------------------------------------------------------------

  /**
   * Generates the index XML.
   */

  public void generate ()
    throws ProcessingException 
  {
    final String METHOD_NAME = "generate";
    this.logDebug(METHOD_NAME + " 1/2: started");
    CoursesAndTutorialsIndex index = null;
    try
      {
        index =
          (CoursesAndTutorialsIndex)this.serviceManager.lookup(CoursesAndTutorialsIndex.ROLE);
        int classId = ParamUtil.getAsId(this.parameters, "class");
        index.setup(classId);
        index.toSAX(this.contentHandler);
        this.logDebug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
	throw new ProcessingException(exception);
      }
    finally
      {
        if ( index != null )
          this.serviceManager.release(index);
      }
  }

  // --------------------------------------------------------------------------------
  // Identification method
  // --------------------------------------------------------------------------------

  /**
   * Returns a string that identifies this instance. It has the
   * following form:<pre>
   *   "CoursesAndTutorialsIndexGenerator" +
   *   '#' + instanceId</pre>
   * where <code>instanceId</code> is the instance id.
   */

  public String getIdentification ()
  {
    return
      "CoursesAndTutorialsIndexGenerator" +
      '#' + this.instanceStatus.getInstanceId();
  }
}