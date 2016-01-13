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

import java.util.Map;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.grade.WorksheetBulkCorrector;
import net.mumie.cocoon.util.ParamUtil;
import net.mumie.cocoon.notions.Id;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.ProcessingException;

/**
 * Performs bulk correction and writes an report as XML
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @author Fritz Lehmann-Grube <a href="mailto:lehmannf@math.tu-berlin.de">lehmannf@math.tu-berlin.de</a>
 * @version <code>$Id: WorksheetBulkCorrectionGenerator.java,v 1.2 2007/07/11 15:38:45 grudzin Exp $</code>
 */

public class WorksheetBulkCorrectionGenerator extends ServiceableJapsGenerator
{
  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(WorksheetBulkCorrectionGenerator.class);

  /**
   * The worksheet to be corrected.
   */

  protected int worksheetId = Id.UNDEFINED;

  /**
   * The force flag
   */

  protected boolean force = false;

  /**
   * Creates a new <code>WorksheetBulkCorrectionGenerator</code> instance.
   */

  public WorksheetBulkCorrectionGenerator ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Recycles this <code>WorksheetBulkCorrectionGenerator</code>. Calls the superclass
   * <code>recycle</code> method and notifies the recycle to the instance status.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle";
    this.logDebug(METHOD_NAME + " 1/2");
    super.recycle();
    this.worksheetId = Id.UNDEFINED;
    force = false;
    this.instanceStatus.notifyRecycle();
    this.logDebug(METHOD_NAME + " 2/2");
  }

  /**
   * Disposes this <code>WorksheetBulkCorrectionGenerator</code>. Calls the superclass
   * <code>dispose</code> method and notifies the dispose to the instance status.
   */

  public void dispose ()
  {
    final String METHOD_NAME = "dispose";
    this.logDebug(METHOD_NAME + " 1/2");
    super.dispose();
    this.worksheetId = Id.UNDEFINED;
    force = false;
    this.instanceStatus.notifyDispose();
    this.logDebug(METHOD_NAME + " 2/2");
  }

  /**
   * Calls the superclass <code>setup</code> method and processes the parameters.
   */

  public void setup (SourceResolver resolver,
                     Map objectModel,
                     String source,
                     Parameters parameters)
    throws ProcessingException
  {
    final String METHOD_NAME = "setup";
    this.getLogger().debug(METHOD_NAME + " 1/3: Started");
    try
      {
        super.setup(resolver, objectModel, source, parameters);

	// worksheet id:
	if ( ParamUtil.checkIfSet(this.parameters, "worksheet") )
	    this.worksheetId = this.parameters.getParameterAsInteger("worksheet");
	else throw new ProcessingException("missing parameter: worksheet");

	// force flag:
	if ( ParamUtil.checkIfSet(this.parameters, "force") )
	  this.force = this.parameters.getParameterAsBoolean("force",false);

        this.getLogger().debug
          (METHOD_NAME + " 2/3:");

        this.getLogger().debug(METHOD_NAME + " 3/3: Done" +
           " worksheetId = " + worksheetId +
           " force = " + force);
      }
    catch (Exception exception)
      {
	throw new ProcessingException(exception);
      }
  }

  /**
   * Performs the bulk correction and writes the report.
   */

  public void generate ()
    throws ProcessingException
  {
    final String METHOD_NAME = "generate";
    this.logDebug(METHOD_NAME + " 1/2: started");
    WorksheetBulkCorrector bulkCorrector = null;
    try
      {
        bulkCorrector =
          (WorksheetBulkCorrector)this.serviceManager.lookup(WorksheetBulkCorrector.ROLE);
        bulkCorrector.bulkCorrect
          (this.worksheetId,
	   this.force,
           this.contentHandler,
           true /* ownDocument */ );
      }
    catch (Exception exception)
      {
        throw new ProcessingException(exception);
      }
    this.logDebug(METHOD_NAME + " 2/2: finished");
  }
}
