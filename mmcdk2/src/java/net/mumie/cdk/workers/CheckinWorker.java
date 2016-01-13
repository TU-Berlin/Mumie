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

package net.mumie.cdk.workers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import net.mumie.cdk.CdkHelper;
import net.mumie.cdk.io.CdkFile;
import net.mumie.japs.client.JapsClient;
import net.mumie.japs.client.JapsPath;
import net.mumie.japs.client.JapsResponseHeader;
import net.mumie.util.io.IOUtil;
import java.util.ArrayList;

public class CheckinWorker extends CdkWorker
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * Size of the i/o buffer, in bytes.
   */

  static final int IO_BUFFER_SIZE = 1024;

  /**
   * The internet media type of zip archives.
   */

  static final String ZIP_CONTENT_TYPE = "application/zip";

  /**
   * Name of the error file.
   */

  static final String ERROR_FILENAME = "checkin_error.txt";

  // --------------------------------------------------------------------------------
  // Creating the zip
  // --------------------------------------------------------------------------------

  /**
   * Writes the specified file to the specified zip input stream.
   */

  protected void writeToZip (ZipOutputStream zip, CdkFile file)
    throws Exception
  {
    this.msgln("Adding " + file);
    ZipEntry entry = new ZipEntry(file.getCdkPath());
    entry.setTime(file.lastModified());
    zip.putNextEntry(entry);
    FileInputStream in = new FileInputStream(file);
    byte[] buffer = new byte[IO_BUFFER_SIZE];
    int length = 0;
    while ( (length = in.read(buffer)) != -1 )
      zip.write(buffer, 0, length);
    zip.flush();
    in.close();
  }

  /**
   * Creates a checkin zip containing the (pseudo-)documents determined by the specified
   * master files and writes it to the specified file.
   */

  protected void createZip (CdkFile zipFile, List<CdkFile> masterFiles)
    throws Exception
  {
    this.msgln("Creating zip ...");
    ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(zipFile));
    List<CdkFile> addedSourceFiles = new ArrayList<CdkFile>();
    for (CdkFile masterFile : masterFiles)
      {
        // Add master file:
        this.writeToZip(zip, masterFile);

        // Add content file if necessary:
        CdkFile contentFile = masterFile.getContentFile();
        if ( contentFile != null )
          this.writeToZip(zip, contentFile);

        // Add source file if necessary:
        CdkFile sourceFile = masterFile.getSourceFile();
        if ( sourceFile != null && sourceFile.exists() )
          {
            if ( addedSourceFiles.contains(sourceFile) )
              this.msgln("Skipping " + sourceFile + " (was already added)");
            else
              {
                this.writeToZip(zip, sourceFile);
                addedSourceFiles.add(sourceFile);
              }
          }
      }
    zip.flush();
    zip.close();
    this.msgln("Creating zip done");
  }

  // --------------------------------------------------------------------------------
  // Uploading
  // --------------------------------------------------------------------------------

  /**
   * Uploads the specified zip file to the server with the specifed alias.
   */

  protected void updload (CdkFile zipFile, String alias)
    throws Exception
  {
    this.msgln("Uploading zip ...");

    // Get Japs client:
    JapsClient japsClient = CdkHelper.getSharedInstance().getJapsClient(alias);

    // Establish connection:
    HttpURLConnection connection =
      japsClient.post(JapsPath.CHECKIN, zipFile, ZIP_CONTENT_TYPE);

    // Check success:
    if ( connection == null )
      throw new IllegalStateException("Connection is null");
    String status = connection.getHeaderField(JapsResponseHeader.STATUS);
    boolean error = ( status != null && status.trim().equals("ERROR") );

    if ( error )
      {
        // Write response to error file:
        File errorFile = new File(this.directory, ERROR_FILENAME);
        write(connection.getInputStream(), new FileOutputStream(errorFile), true);

        // Throw exception:
        String description = connection.getHeaderField(JapsResponseHeader.ERROR);
        throw new CdkCheckinException("Upload failed: " + description);
      }
    else
      // Write response to stdout:
      write(connection.getInputStream(), this.out, false);

    this.msgln("Uploading zip done");
  }

  // --------------------------------------------------------------------------------
  // Checkin
  // --------------------------------------------------------------------------------

  /**
   * Checks-in the (pseudo-)documents with the specifed master files on the server with the
   * specified alias. If <code>deleteZip</code> is true, the zip file is deleted after
   * check-in. If <code>onlyZip</code> is true, only the zip file is created but the
   * documents are not checked-in.
   */

  public void checkin (List<CdkFile> masterFiles,
                       String alias,
                       boolean deleteZip,
                       boolean onlyZip)
    throws Exception
  {
    CdkFile zipFile = new CdkFile(new File(this.directory, "checkin.zip"));
    if ( zipFile.exists() ) IOUtil.deleteFile(zipFile);
    try
      {
        this.createZip(zipFile, masterFiles);
        if ( !onlyZip )
          this.updload(zipFile, alias);
      }
    finally
      {
        if ( deleteZip && zipFile.exists() )
          IOUtil.deleteFile(zipFile);
      }
  }

  // --------------------------------------------------------------------------------
  // Utilities
  // --------------------------------------------------------------------------------

  /**
   * Auxiliary method to write all content if the specified input stream to the specified
   * output stream. If <code>closeOut</code> is true, the output stream is closed after
   * writing (by calling the output steam's <code>close</code> method). Otherwise, it is not
   * closed by this method.
   */

  protected static void write (InputStream in, OutputStream out, boolean closeOut)
    throws Exception
  {
    byte[] buffer = new byte[IO_BUFFER_SIZE];
    int length;
    while ( (length = in.read(buffer)) != -1 )
      out.write(buffer, 0, length);
    out.flush();
    if ( closeOut ) out.close();
    in.close();
  }

}
