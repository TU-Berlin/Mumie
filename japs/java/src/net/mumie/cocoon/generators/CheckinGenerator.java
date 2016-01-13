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

import java.io.File;
import net.mumie.cocoon.checkin.CheckinHelper;
import net.mumie.cocoon.checkin.ZipCheckinRepository;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.util.LogUtil;
import net.mumie.cocoon.util.UploadHelper;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;

/**
 * Checks-in new documents and pseudo-documents.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: CheckinGenerator.java,v 1.5 2007/07/11 15:38:44 grudzin Exp $</code>
 */

public class CheckinGenerator extends ServiceableJapsGenerator
  implements Configurable 
{
  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(CheckinGenerator.class);

  /**
   * Whether uploaded files are deleted after use.
   */

  protected boolean deleteUploads = true;

  /**
   * Creates a new <code>CheckinGenerator</code> instance.
   */

  public CheckinGenerator ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Configures this instance.
   */

  public void configure (Configuration configuration)
    throws ConfigurationException
  {
    final String METHOD_NAME = "configure";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.deleteUploads = configuration.getChild("delete-uploads").getValueAsBoolean(true);
    this.logDebug
      (METHOD_NAME + " 2/2: Done. deleteUploads" + this.deleteUploads);
  }  

  /**
   * Recycles this <code>CheckinGenerator</code>.
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
   * Disposes this <code>CheckinGenerator</code>.
   */

  public void dispose ()
  {
    final String METHOD_NAME = "dispose";
    this.logDebug(METHOD_NAME + " 1/2");
    super.dispose();
    this.instanceStatus.notifyDispose();
    this.logDebug(METHOD_NAME + " 2/2");
  }

  /**
   * Returns a string that identificates this <code>CheckinGenerator</code> Instance. It has the
   * following form:<pre>
   *   "CheckinGenerator" +
   *   '#' + instanceId
   *   '(' + numberOfRecycles
   *   ')'</pre>
   * where <code>instanceId</code> is the instance id and
   * <code>numberOfRecycles</code> the number of recycles of this instance.
   */

  public String getIdentification ()
  {
    return
      "CheckinGenerator" +
      '#' + this.instanceStatus.getInstanceId() +
      '(' + this.instanceStatus.getNumberOfRecycles() +
      ')';
  }

  /**
   * Generates the XML. See class documentation for details.
   */

  public void generate ()
    throws ProcessingException 
  {
    final String METHOD_NAME = "generate";
    this.logDebug(METHOD_NAME + " 1/2: started");
    UploadHelper uploadHelper = null;
    ZipCheckinRepository checkinRepository = null;
    CheckinHelper checkinHelper = null;
    try
      {
        // Init services:
        uploadHelper =
          (UploadHelper)this.serviceManager.lookup(UploadHelper.ROLE);
        checkinRepository =
          (ZipCheckinRepository)this.serviceManager.lookup(ZipCheckinRepository.ROLE);
        checkinHelper =
          (CheckinHelper)this.serviceManager.lookup(CheckinHelper.ROLE);

        // Get the upload dir and uploaded file:
        File[] uploadedFiles =
          uploadHelper.upload(ObjectModelHelper.getRequest(this.objectModel));
        if ( uploadedFiles.length == 0 )
          throw new IllegalStateException("No upload dir");
        if ( uploadedFiles.length == 1 )
          throw new IllegalStateException("No uploaded file");
        if ( uploadedFiles.length > 2 )
          throw new IllegalStateException
            ("Too many uploaded files: " + LogUtil.arrayToString(uploadedFiles));
        File uploadDir = uploadedFiles[0];
        File uploadedFile = uploadedFiles[1];

        // Init checkin repository:
        checkinRepository.setup(uploadedFile);

        // Checkin:
        checkinHelper.checkin(checkinRepository);

        // Write report:
        checkinHelper.toSAX(this.contentHandler);

        // Delete uploads if necessary:
        if ( this.deleteUploads )
          {
            uploadedFile.delete();
            uploadDir.delete();
          }

        this.logDebug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
        throw new ProcessingException(this.getIdentification(), exception);
      }
    finally
      {
        if ( checkinHelper != null )
          this.serviceManager.release(checkinHelper);
        if ( checkinRepository != null )
          this.serviceManager.release(checkinRepository);
        if ( uploadHelper != null )
          this.serviceManager.release(uploadHelper);
      }
  }  
}
