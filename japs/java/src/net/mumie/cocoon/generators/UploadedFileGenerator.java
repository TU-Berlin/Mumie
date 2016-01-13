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
import java.io.FileReader;
import java.util.Map;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.util.LogUtil;
import net.mumie.cocoon.util.ParamUtil;
import net.mumie.cocoon.util.UploadHelper;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.excalibur.xml.sax.SAXParser;
import org.xml.sax.InputSource;

/**
 * <p>
 *   Generates SAX events from an uploaded file. Recognizes the following parameter:
 * </p>
 * <table class="genuine indented" style="width:60em">
 *   <thead>
 *     <tr>
 *       <td>Name</td>
 *       <td>Description</td>
 *       <td>Required</td>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td><code>delete-uploads</code></td>
 *       <td>
 *         Whether the uploaded file and the upload directory are deleted after the file
 *         has been parsed. Boolean.
 *       </td>
 *       <td>No, default: <code>true</code></td>
 *     </tr>
 *   <tbody>
 * <table>
 * <p>
 *   The generator expects that a single upload file comes with the request. This file must
 *   be XML. The generator parses the XML and sends the resulting SAX events to the
 *   pipeline.
 * </p>
 * <p>
 *   If no uploaded file exists or if more then one uploaded file exists an exception is
 *   thrown.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: UploadedFileGenerator.java,v 1.3 2007/07/11 15:38:45 grudzin Exp $</code>
 */

public class UploadedFileGenerator extends ServiceableJapsGenerator
{
  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(UploadedFileGenerator.class);

  /**
   * Creates a new <code>UploadedFileGenerator</code> instance.
   */

  public UploadedFileGenerator ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Recycles this <code>UploadedFileGenerator</code>.
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
   * Disposes this <code>UploadedFileGenerator</code>.
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
   * Returns a string that identificates this <code>UploadedFileGenerator</code> Instance. It
   * has the 
   * following form:<pre>
   *   "UploadedFileGenerator" +
   *   '#' + instanceId
   *   '(' + numberOfRecycles
   *   ')'</pre>
   * where <code>instanceId</code> is the instance id and
   * <code>numberOfRecycles</code> the number of recycles of this instance.
   */

  public String getIdentification ()
  {
    return
      "UploadedFileGenerator" +
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
    this.logDebug(METHOD_NAME + " 1/3: Started");
    UploadHelper uploadHelper = null;
    SAXParser parser = null;
    try
      {
        // Init services:
        uploadHelper =
          (UploadHelper)this.serviceManager.lookup(UploadHelper.ROLE);
	parser = (SAXParser)this.serviceManager.lookup(SAXParser.ROLE);

        // Get the upload dir and uploaded file:
        File[] uploadedFiles =
          uploadHelper.upload(ObjectModelHelper.getRequest(this.objectModel));
        if ( uploadedFiles.length == 0 )
          throw new IllegalStateException("No upload dir");
        if ( uploadedFiles.length == 1 )
          throw new IllegalStateException("No uploaded file");
        if ( uploadedFiles.length > 2 )
          throw new IllegalStateException
            ("To many uploaded files: " + LogUtil.arrayToString(uploadedFiles));
        File uploadDir = uploadedFiles[0];
        File uploadedFile = uploadedFiles[1];

        this.logDebug
          (METHOD_NAME + " 2/3: " +
           "uploadDir = " + uploadDir +
           ", uploadedFile = " + uploadedFile);

        // Send uploadedFile to SAX:
        parser.parse(new InputSource(new FileReader(uploadedFile)), this.contentHandler);

        // Delete uploads if necessary:
        if ( ParamUtil.getAsBoolean(this.parameters, "delete-uploads", true) )
          {
            uploadedFile.delete();
            uploadDir.delete();
          }

        this.logDebug(METHOD_NAME + " 3/3: Done");
      }
    catch (Exception exception)
      {
        throw new ProcessingException(this.getIdentification(), exception);
      }
    finally
      {
	if (parser != null )
          this.serviceManager.release(parser);
        if ( uploadHelper != null )
          this.serviceManager.release(uploadHelper);
      }
  }  
}
