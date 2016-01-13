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

import net.mumie.cocoon.grade.UserProblemGrades;
import net.mumie.cocoon.notions.Category;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.util.ParamUtil;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;

/**
 * Implementation of {@link UserProblemGradesGenerator}.
 *
 * @author Marek Grudzinski <a href="mailto:grudzin@math.tu-berlin.de">grudzin@math.tu-berlin.de</a>
 * @version <code>$Id: UserProblemGradesGenerator.java,v 1.1 2007/10/05 11:56:34 grudzin Exp $</code>
 */
public class UserProblemGradesGenerator extends ServiceableJapsGenerator
{
  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(UserProblemGradesGenerator.class);

  /**
   * Creates a new <code>TutorialUserProblemGradesGenerator</code> instance.
   */

  public UserProblemGradesGenerator ()
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
    UserProblemGrades grades = null;
    try
      {
        grades = (UserProblemGrades)this.serviceManager.lookup(UserProblemGrades.ROLE);
        int userId = ParamUtil.getAsId(this.parameters, "user");
        int courseId = ParamUtil.getAsId(this.parameters, "course");
        int[] problemCategories = getCategories(this.parameters, "problem-category-names");
        int[] worksheetCategories = getCategories(this.parameters, "worksheet-category-names");
        grades.setup(userId, courseId, problemCategories, worksheetCategories);
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
  
  /**
   * Auxiliary method: returns the paramter with the specified name in the specified
   * parameters object as an array of category codes. If the parameter does not exist, is
   * void, or equal to <code>"*"</code>, returns null. Otherwise, splits the parameter value
   * into category names, converts the names into codes, and returns the codes as an array.
   */

  protected static int[] getCategories (Parameters parameters, String name)
    throws Exception
  {
    String[] categoryNames = ParamUtil.getAsStringArray(parameters, name, null);
    if ( categoryNames == null ||
         ( categoryNames.length == 1 && categoryNames[0].equals("*") ) )
      return null;
    int[] categories = new int[categoryNames.length];
    for (int i = 0; i < categories.length; i++)
      {
        int category = Category.codeFor(categoryNames[i]);
        if ( category == Category.UNDEFINED )
          throw new IllegalArgumentException("Unknown category: " + categoryNames[i]);
        categories[i] = category;
      }
    return categories;
  }
}
