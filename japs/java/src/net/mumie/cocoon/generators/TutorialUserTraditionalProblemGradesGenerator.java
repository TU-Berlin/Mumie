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

import net.mumie.cocoon.grade.TutorialUserTraditionalProblemGrades;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.util.ParamUtil;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;

public class TutorialUserTraditionalProblemGradesGenerator extends ServiceableJapsGenerator
{
  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus =
    new ServiceStatus(TutorialUserTraditionalProblemGradesGenerator.class);

  /**
   * Creates a new <code>TutorialUserTraditionalProblemGradesGenerator</code> instance.
   */

  public TutorialUserTraditionalProblemGradesGenerator ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Recycles this instance. Essentially, this method only calls the superclass recycle
   * method. Besides this, the instance status id notified about the recycle, and log
   * messages are written.
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
   * Disposes this instance. Essentially, this method only calls the superclass dispose
   * method. Besides this, the instance status id notified about the dispose, and log
   * messages are written.
   */

  public void dispose ()
  {
    final String METHOD_NAME = "dispose";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    super.dispose();
    this.instanceStatus.notifyDispose();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Generates the XML. See class documentation for details.
   */

  public void generate ()
    throws ProcessingException 
  {
    final String METHOD_NAME = "generate";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    TutorialUserTraditionalProblemGrades grades = null;
    try
      {
        grades = (TutorialUserTraditionalProblemGrades)this.serviceManager.lookup
          (TutorialUserTraditionalProblemGrades.ROLE);
        int tutorialId = ParamUtil.getAsId(this.parameters, "tutorial");
        int courseId = ParamUtil.getAsId(this.parameters, "course");
        boolean store = ParamUtil.getAsBoolean(this.parameters, "store", false);
        grades.load(tutorialId, courseId);
        if ( store )
          {
            grades.addFormData(ObjectModelHelper.getRequest(this.objectModel));
            grades.store();
          }
        grades.toSAX(this.contentHandler);
        this.logDebug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
	throw new ProcessingException(exception);
      }
    finally
      {
        if ( grades != null )
          this.serviceManager.release(grades);
      }
  }
}