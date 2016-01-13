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

package net.mumie.cocoon.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import net.mumie.cocoon.service.AbstractJapsService;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.util.LogUtil;
import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.servlet.multipart.Part;

/**
 * <p>
 *   Default implementation of {@link UploadHelper UploadHelper}.
 * </p>
 * <p>
 *   This class needs one configuration value: the upload directory. It is specified in the
 *   <code>upload-dir</code> element of the configuration XML. The upload directory must be
 *   given with its <strong>absolute path</strong>. Example (<code>cocoon.xconf</code>
 *   excerpt):
 * </p>
 * <pre>
 *   &lt;upload-helper&gt;
 *     &lt;upload-dir&gt;/srv/tomcat/webapps/cocoon/WEB-INF/upload&lt;/upload-dir&gt;
 *   &lt;/upload-helper&gt;</pre>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: DefaultUploadHelper.java,v 1.6 2008/04/30 11:07:23 rassy Exp $</code>
 */

public class DefaultUploadHelper extends AbstractJapsService
  implements Configurable, Poolable, UploadHelper
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(DefaultUploadHelper.class);

  /**
   * The last upload id.
   */

  protected static long lastUploadId = 0;

  /**
   * The root directory for storing the uploads.
   */

  protected String rootUploadDirName;

  // --------------------------------------------------------------------------------
  // Lifecycle methods
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>DefaultUploadHelper</code>.
   */

  public DefaultUploadHelper ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Configures this upload helper. See class decription for details.
   */

  public void configure (Configuration configuration)
    throws ConfigurationException
  {
    final String METHOD_NAME = "configuration";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.rootUploadDirName = configuration.getChild("upload-dir").getValue().trim();
    this.logDebug
      (METHOD_NAME + " 2/2: Done." +
       " rootUploadDirName = " + this.rootUploadDirName);
  }

  // --------------------------------------------------------------------------------
  // Uploading
  // --------------------------------------------------------------------------------

  /**
   * Uploads the files determined by the specified request. All files are saved in a newly
   * created directory. The returned array contains this directory as the first entry. The
   * remaining entries contain the uploaded files.
   */

  public File[] upload (Request request)
    throws UploadException
  {
    try
      {
        final String METHOD_NAME = "upload";
        this.logDebug(METHOD_NAME + " 1/3: Started");

        // Get the upload id:
        long uploadId = getUploadId();

        // Create the upload directory:
        String uploadDirName =
          Long.toString(System.currentTimeMillis()) + "-" + Long.toString(uploadId);
        File uploadDir = new File(this.rootUploadDirName, uploadDirName);
        if ( uploadDir.exists() )
          throw new UploadException("Upload dir exists: " + uploadDir);
        uploadDir.mkdirs();
        this.logDebug(METHOD_NAME + " 2/3: uploadDir = " + uploadDir);

        // List of uploaded files (contains uploadDir as first element):
        List uploadedFilesList = new ArrayList();
        uploadedFilesList.add(uploadDir);

        // Process request params:
        int count = 0;
        Enumeration paramNames = request.getParameterNames();
        while ( paramNames.hasMoreElements() )
          {
            String paramName = (String)paramNames.nextElement();
            Object paramValue = request.get(paramName);
            if ( paramValue instanceof Part )
              {
                count++;
                Part part = (Part)paramValue;
                File uploadedFile = new File(uploadDir, part.getFileName());
                part.copyToFile(uploadedFile.getAbsolutePath());
                uploadedFilesList.add(uploadedFile);
                this.logDebug(METHOD_NAME + "2." + count + "/3: uploadedFile = " + uploadedFile);
              }
          }

        File[] uploadedFiles =
          (File[])uploadedFilesList.toArray(new File[uploadedFilesList.size()]);

        this.logDebug
          (METHOD_NAME + " Done. uploadedFiles = " + LogUtil.arrayToString(uploadedFiles));

        return uploadedFiles;
      }
    catch (Exception exception)
      {
        throw new UploadException(exception);
      }
  }

  /**
   * Same as {@link #upload(Request) upload(Request request)}, but checks if the number of
   * uploaded files equals the specified integer. If not, throws an exception.
   */

  public File[] upload (Request request, int reqNum)
    throws UploadException
  {
    File[] files = this.upload(request);
    int num = files.length-1;
    if ( num != reqNum )
      throw new UploadException("Expected " + reqNum + "uploaded files, but found: " + num);
    return files;
  }

  // --------------------------------------------------------------------------------
  // Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * Returns the next upload id.
   */

  protected static synchronized long getUploadId ()
  {
    if ( lastUploadId < Long.MAX_VALUE )
      lastUploadId++;
    else
      lastUploadId = 0;
    return lastUploadId;
  }

  // --------------------------------------------------------------------------------
  // Identification method
  // --------------------------------------------------------------------------------
  
  /**
   * Returns a string that identifies this instance. It has the
   * following form:<pre>
   *   "DefaultUploadHelper" +
   *   '#' + instanceId
   *   '(' + lifecycleStatus
   *   ')'</pre>
   * where <code>instanceId</code> is the instance id and <code>lifecycleStatus</code> the
   * lifecycle status of this instance.
   */

  public String getIdentification ()
  {
    return
      "DefaultUploadHelper" +
      '#' + this.instanceStatus.getInstanceId() +
      '(' + this.instanceStatus.getLifecycleStatus() +
      ')';
  }
}
