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
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import net.mumie.cdk.CdkConfigParam;
import net.mumie.cdk.CdkHelper;
import net.mumie.cdk.CdkInitException;
import net.mumie.cdk.io.CdkFile;
import net.mumie.cdk.util.MasterCacheException;
import net.mumie.cocoon.checkin.Master;
import net.mumie.cocoon.checkin.MasterException;
import net.mumie.cocoon.notions.DataFormat;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.Nature;
import net.mumie.util.StringUtil;
import net.mumie.util.io.IOUtil;
import net.mumie.util.xml.AbortingXSLErrorListener;
import net.mumie.util.xml.CachingTransformerProvider;

/**
 * Creates previews of documents.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: PreviewCreator.java,v 1.8 2007/07/16 11:07:07 grudzin Exp $</code>
 */

public class PreviewCreator extends CdkWorker
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The error listener for XSL transformations.
   */

  protected AbortingXSLErrorListener xslErrorListener = new AbortingXSLErrorListener();

  // --------------------------------------------------------------------------------
  // Creating previews
  // --------------------------------------------------------------------------------

  /**
   * Creates a preview.
   *
   * @param masterFile the master file of the document for which the preview is created
   * @param force if true, the preview is created even if it exists and is up-to-date;
   *   if false, the preview is created only if it does not exist or is outdated.
   * @param deleteTmp if true, the temporary XML file (<code>foo.tmp.xml</code>) is deleted
   *   after the preview has been made; if false, it is not deleted.
   * @param onlyTmp if true, only the temporary XML file is created, not the actual preview;
   *   if false, both the temporary XML file and the actual preview are created.
   */

  public void createPreview (CdkFile masterFile,
                             boolean force,
                             boolean deleteTmp,
                             boolean onlyTmp)
    throws Exception
  {
    this.checkStop();

    if ( !masterFile.isMasterFile() )
      throw new IllegalArgumentException("Not a master file: " + masterFile);

    try
      {
        // Get nature and type:
        Master master = masterFile.getMaster();
        int nature = master.getNature();
        int type = master.getType();

        // If this is not a non-generic document with text content, skip:
        if ( !( nature == Nature.DOCUMENT
                && !DocType.isGeneric(type)
                && DataFormat.ofDocType(type) == DataFormat.TEXT ) )
          {
            return;
          }

        this.msg(masterFile + ": ");

        // File objects needed:
        CdkFile contentFile = masterFile.getContentFile();
        CdkFile tmpXMLFile = masterFile.replaceSuffix(CdkFile.TMP_XML_SUFFIX);
        CdkFile dyndataFile = masterFile.replaceSuffix(CdkFile.DYNDATA_SUFFIX);
        CdkFile datasheetFile = masterFile.replaceSuffix(CdkFile.DATASHEET_SUFFIX);
        CdkFile previewFile = masterFile.getPreviewFile();

        // Get transformerProvider:
        CachingTransformerProvider transformerProvider = 
          CachingTransformerProvider.getSharedInstance();

        // (Re)build temp file if necessary:
        if ( force
             || IOUtil.checkIfOutdated(tmpXMLFile, contentFile)
             || IOUtil.checkIfOutdated(tmpXMLFile, masterFile) )
          {
            // Get transformer:
            Transformer transformer =
              transformerProvider.getTransformer(this.getMasterToTempURI());

            // Set error listener:
            transformer.setErrorListener(this.xslErrorListener);

            // Set parameters:
            transformer.setParameter("master-path", masterFile.getCdkPath());
            transformer.setParameter("content-abs-filename", contentFile.getAbsolutePath());
            if ( dyndataFile.exists() )
              transformer.setParameter("dyndata-abs-filename", dyndataFile.getAbsolutePath());
            if ( datasheetFile.exists() )
              transformer.setParameter("datasheet-abs-filename", datasheetFile.getAbsolutePath());

            // Transform:
            transformer.transform(new StreamSource(masterFile), new StreamResult(tmpXMLFile));

            this.msg("created tmp");
          }
        else
          this.msg("tmp up-to-date");

        if ( !onlyTmp )
          {
            // (re)build preview if necessary:
            if ( force || IOUtil.checkIfOutdated(previewFile, tmpXMLFile) )
              {
                // Get transformer:
                Transformer transformer =
                  transformerProvider.getTransformer(this.getTempToPreviewURI(masterFile));

                // Set error listener:
                transformer.setErrorListener(this.xslErrorListener);

                // Set parameters:
                transformer.setParameter("xsl-mmcdk.master-path", masterFile.getCdkPath());

                // Transform:
                transformer.transform(new StreamSource(tmpXMLFile), new StreamResult(previewFile));

                this.msg(", created preview");
              }
            else
              this.msg(", preview up-to-date");

            // Remove temp file if necessary:
            if ( deleteTmp  )
              {
                IOUtil.deleteFile(tmpXMLFile);
              }
          }

        this.msgln();
      }
    catch (Exception exception)
      {
        this.msgln();
        throw exception;
      }
  }

  /**
   * <p>
   *   Creates several previews. For of the specified files, the following rules apply:
   *   <ul>
   *     <li>
   *       If the file is a master file, the corresponding preview is created.
   *     </li>
   *     <li>
   *       Otherwise, if the file is a directory and <code>recursive</code> or
   *       <code>firstLevel</code> is true, the method is applied to the files in the
   *       directory. All arguments except <code>files</code> and <code>firstLevel</code>
   *       are inherited. The latter is set to false.
   *     </li>
   *     <li>
   *       Otherwise, if <code>firstLevel</code> is true, an exception is thrown.
   *     </li>
   *     <li>
   *       Otherwise the file is ignored.
   *     </li>
   *   </ul>
   *   The parameters <code>force</code>, <code>deleteTmp</code>, and <code>onlyTmp</code>
   *   have the same meaning as with {@link #createPreview createPreview}.
   * </p>
   * <p>
   *   The main purpose of this method is to implement the public method with the same name,
   *   {@link #createPreviews(CdkFile[],boolean,boolean,boolean,boolean) createPreviews}.
   *   The latter is defined as
   *
   *   <pre>  createPreviews(files, force, deleteTmp, onlyTmp, recursive, true)</pre>
   *
   *   By the rules above, it is guaranteed that the public method can be applied to any
   *   files and behaves in an intuitive way: for master files, the previews are created;
   *   for directories, all master files in the directory are processed; subdirectories are
   *   processed recursivly if the <code>recursivly</code> flag is set.
   * </p>
  */

  protected void createPreviews (CdkFile[] files,
                                 boolean force,
                                 boolean deleteTmp,
                                 boolean onlyTmp,
                                 boolean recursive,
                                 boolean firstLevel)
    throws Exception
  {
    for (CdkFile file : files)
      {
        if ( file.isMasterFile() )
          this.createPreview(file, force, deleteTmp, onlyTmp);
        else if ( file.isDirectory() && ( recursive || firstLevel ) )
          this.createPreviews
            (file.listCdkFiles(), force, deleteTmp, onlyTmp, recursive, false);
        else if ( firstLevel )
          throw new IllegalArgumentException("Not a master file: " + file);
      }
  }

  /**
   * Creates several previews. The specified files are treated as follows: For master files,
   * the previews are created; for directories, all master files in the directory are
   * processed. Subdirectories are processed recursivly if the <code>recursivly</code> flag
   * is set. The parameters <code>force</code>, <code>deleteTmp</code>, and
   * <code>onlyTmp</code> have the same meaning as with
   * {@link #createPreview createPreview}.
   *
   * @see #createPreviews(CdkFile[],boolean,boolean,boolean,boolean,boolean)
   *   createPreviews(files, force, deleteTmp, onlyTmp, recursive, firstLevel)
   */

  public void createPreviews (CdkFile[] files,
                              boolean force,
                              boolean deleteTmp,
                              boolean onlyTmp,
                              boolean recursive)
    throws Exception
  {
    this.createPreviews(files, force, deleteTmp, onlyTmp, recursive, true);
  }

  // --------------------------------------------------------------------------------
  // Auxiliaries to get the stylesheet URIs
  // --------------------------------------------------------------------------------

  /**
   * Returns the URI of the XSL stylesheet that transforms the master plus content file to
   * the temporary XML file. The URI is retrieved from the system property
   * {@link CdkConfigParam#MASTER_TO_TMP_URI MASTER_TO_TMP_URI}.
   */

  protected URI getMasterToTempURI ()
    throws URISyntaxException
  {
    String param = CdkConfigParam.MASTER_TO_TMP_URI;
    String value = System.getProperty(param);
    if ( value == null )
      throw new IllegalStateException ("System property not set: " + param);
    return new URI(value);
  }

  /**
   * Returns the URI of the XSL stylesheet for the specified master file that transforms
   * the temporary XML file to the preview file.  The URI is retrieved from the system property
   * {@link CdkConfigParam#TMP_TO_PREVIEW_URI TMP_TO_PREVIEW_URI}<code>"." + docTypeName</code>,
   * where <code>dockType</code> is obtained by <code>masterFile.getMaster().getTypeName()</code>.
   */

  protected URI getTempToPreviewURI (CdkFile masterFile)
    throws URISyntaxException, MasterException, MasterCacheException, CdkInitException 
  {
    String docTypeName = masterFile.getMaster().getTypeName();
    String param = CdkConfigParam.TMP_TO_PREVIEW_URI + "." + docTypeName;
    String value = System.getProperty(param);
    if ( value == null )
      throw new IllegalStateException ("System property not set: " + param);
    return new URI(value);
  }
}
