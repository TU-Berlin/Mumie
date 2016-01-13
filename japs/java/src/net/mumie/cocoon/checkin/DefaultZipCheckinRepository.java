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

package net.mumie.cocoon.checkin;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.mumie.cocoon.notions.FileRole;
import net.mumie.cocoon.notions.MediaType;
import net.mumie.cocoon.service.AbstractJapsServiceable;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.util.PathTokenizer;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.excalibur.xml.dom.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * Default implementation of {@link ZipCheckinRepository ZipCheckinRepository}.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: DefaultZipCheckinRepository.java,v 1.11 2009/12/14 09:48:34 rassy Exp $</code>
 */

public class DefaultZipCheckinRepository extends AbstractJapsServiceable
  implements Recyclable, Disposable, ZipCheckinRepository
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus =
    new ServiceStatus(DefaultZipCheckinRepository.class);

  /**
   * The zip archive wrapped by this object.
   */

  protected ZipFile zipFile;

  /**
   * Maps master paths to content paths.
   */

  protected Map mastersVsContents;

  /**
   * Maps master paths to source paths.
   */

  protected Map mastersVsSources;

  /**
   * Maps the (normaized) paths to the original raw paths.
   */

  protected Map pathsVsRawPaths;

  /**
   * The path tokenizer to tokenize checkin paths.
   */

  protected PathTokenizer pathTokenizer = new PathTokenizer();

  /**
   * The DOM parser of this object.
   */

  protected DOMParser domParser = null;

  // --------------------------------------------------------------------------------
  // Lifecycle methods
  // --------------------------------------------------------------------------------

  /**
   * Resets the global variables {@link #zipFile zipFile} and
   * {@link #mastersVsContents mastersVsContents}
   */

  protected void resetVariables ()
  {
    this.zipFile = null;
    this.mastersVsContents = null;
    this.mastersVsSources = null;
    this.pathsVsRawPaths = null;
  }

  /**
   * Releases {@link #domParser domParser}.
   */

  protected void releaseServices ()
  {
    if ( this.domParser != null )
      this.serviceManager.release(this.domParser);
  }

  /**
   * Creates a new <code>DefaultZipCheckinRepository</code>.
   */

  public DefaultZipCheckinRepository ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.resetVariables();
    this.instanceStatus.notifyCreation();
  }

  /**
   * Initializes this checkin repository with the specified zip file.
   */

  public void setup (ZipFile zipFile)
    throws CheckinException
  {
    final String METHOD_NAME = "setup";
    this.zipFile = zipFile;
    this.initFileTables();
    this.logDebug(METHOD_NAME + " 1/1: zipFile = " + zipFile);
  }

  /**
   * Initializes this checkin repository with the specified file, which must be a zip
   * archive.
   */

  public void setup (File file)
    throws CheckinException
  {
    try
      {
        this.setup(new ZipFile(file));
      }
    catch (Exception exception)
      {
        throw new CheckinException
          (this.getIdentification(), exception);
      }
  }

  /**
   * Recycles this checkin repository.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.resetVariables();
    this.releaseServices();
    this.instanceStatus.notifyRecycle();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Disposes this checkin repository.
   */

  public void dispose ()
  {
    final String METHOD_NAME = "dispose";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.resetVariables();
    this.releaseServices();
    this.instanceStatus.notifyDispose();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * Fills {@link #mastersVsContents mastersVsContents} and
   * {@link #mastersVsSources mastersVsSources} with the respective values.
   */

  protected void initFileTables ()
    throws CheckinException
  {
    try
      {
        // Suffixes as constants, for convenience:
        final String MASTER = FileRole.suffixOf(FileRole.MASTER);
        final String CONTENT = FileRole.suffixOf(FileRole.CONTENT);
        final String SOURCE = FileRole.suffixOf(FileRole.SOURCE);
        final String XML = MediaType.suffixOf(MediaType.TEXT_XML);

        this.pathsVsRawPaths = new HashMap();

        // Maps to store master and content paths temporarily. Keys are the paths without
        // suffixes, values the paths itself.
        Map masterPaths = new HashMap();
        Map contentPaths = new HashMap();
        Map sourcePaths = new HashMap();

        // Process the zip entry names:
        Enumeration enumeration = this.zipFile.entries();
        while ( enumeration.hasMoreElements() )
          {
            ZipEntry entry = (ZipEntry)enumeration.nextElement();
            if ( !entry.isDirectory() )
              {
                String rawPath = entry.getName();
                String path = PathTokenizer.normalizeSeparators(rawPath);
                this.pathsVsRawPaths.put(path, rawPath);
                this.pathTokenizer.tokenize(path);
                String pathWithoutSuffixes = this.pathTokenizer.getPathWithoutSuffixes();
                String roleSuffix = this.pathTokenizer.getRoleSuffix();
                String typeSuffix = this.pathTokenizer.getTypeSuffix();
                if ( roleSuffix == null )
                  throw new CheckinException("Invalid path: " + path);
                else if ( roleSuffix.equals(MASTER) )
                  {
                    if ( typeSuffix == null || !typeSuffix.equals(XML) )
                      throw new CheckinException("Invalid master path: " + path);
                    masterPaths.put(pathWithoutSuffixes, path);
                  }
                else if ( roleSuffix.equals(CONTENT) )
                  {
                    if ( contentPaths.containsKey(pathWithoutSuffixes) )
                      throw new CheckinException
                        ("Multiple content files for same document: " +
                         (String)contentPaths.get(pathWithoutSuffixes) + ", " + path);
                    contentPaths.put(pathWithoutSuffixes, path);
                  }
                else if ( roleSuffix.equals(SOURCE) )
                  {
                    if ( sourcePaths.containsKey(pathWithoutSuffixes) )
                      throw new CheckinException
                        ("Multiple primary source files for same document: " +
                         (String)sourcePaths.get(pathWithoutSuffixes) + ", " + path);
                    sourcePaths.put(pathWithoutSuffixes, path);
                  }
                else
                  throw new CheckinException
                    ("Invalid master path: " + path +
                     ": Unknown role suffix: " + roleSuffix);
              }
          }

        // Create mastersVsContents and mastersVsSources from the information in masterPaths
        // and contentPaths:
        this.mastersVsContents = new HashMap();
        this.mastersVsSources = new HashMap();
        Iterator iterator = masterPaths.entrySet().iterator();
        while ( iterator.hasNext() )
          {
            Map.Entry entry = (Map.Entry)iterator.next();
            String pathWithoutSuffixes = (String)entry.getKey();
            String masterPath = (String)entry.getValue();
            String contentPath = (String)contentPaths.get(pathWithoutSuffixes);
            String sourcePath = (String)sourcePaths.get(pathWithoutSuffixes);
            this.mastersVsContents.put(masterPath, contentPath);
            this.mastersVsSources.put(masterPath, sourcePath);
          }
      }
    catch (Exception exception)
      {
        throw new CheckinException(exception);
      }
  }

  /**
   * Ensures that the DOM parser is ready to use.
   */

  protected void ensureDOMParser ()
    throws ServiceException
  {
    if ( this.domParser == null )
      this.domParser = (DOMParser)this.serviceManager.lookup(DOMParser.ROLE);
  }

  /**
   * Returns the raw path for the specified normaizes path.
   */

  protected String getRawPath (String path)
    throws CheckinException
  {
    String rawPath = (String)this.pathsVsRawPaths.get(path);
    if ( rawPath == null )
      throw new CheckinException("No such path: " + path);
    return rawPath;
  }

  /**
   * Returns the zip entry for the specified path. Throws an exception if the entry does not
   * exist.
   */

  protected ZipEntry getZipEntry (String path)
    throws CheckinException, IllegalStateException
  {
    return this.zipFile.getEntry(this.getRawPath(path));
  }

  // --------------------------------------------------------------------------------
  // Accessing the checkin data
  // --------------------------------------------------------------------------------

  /**
   * Returns an input stream to read the entry with the specified path.
   */

  public InputStream getInputStream (String path)
    throws CheckinException
  {
    try
      {
        return this.zipFile.getInputStream(this.getZipEntry(path));
      }
    catch (Exception exception)
      {
        throw new CheckinException(this.getIdentification(), exception);
      }
  }

  /**
   * Returns the size of the master, content, or source with the specified path, or -1 if
   * not known.
   */

  public long getSize (String path)
    throws CheckinException
  {
    try
      {
        return this.getZipEntry(path).getSize();
      }
    catch (Exception exception)
      {
        throw new CheckinException(this.getIdentification(), exception);
      }
  }

  /**
   * Returns all master paths.
   */

  public String[] getMasterPaths ()
    throws CheckinException
  {
    return
      (String[])
      this.mastersVsContents
      .keySet()
      .toArray(new String[this.mastersVsContents.size()]);
  }

  /**
   * Returns the content path for the specified master path, or <code>null</code> if it does
   * not exist.
   */

  public String getContentPath (String masterPath)
    throws CheckinException
  {
    return (String)this.mastersVsContents.get(masterPath);
  }

  /**
   * Returns the source path for the specified master path, or <code>null</code> if it does
   * not exist.
   */

  public String getSourcePath (String masterPath)
    throws CheckinException
  {
    return (String)this.mastersVsSources.get(masterPath);
  }

  /**
   * Returns the master for the specified master Path.
   */

  public Master getMaster (String masterPath)
    throws CheckinException
  {
    try
      {
        final String METHOD_NAME = "getMaster";
        this.logDebug(METHOD_NAME + " 1/2: Started. masterPath = " + masterPath);

        // Check if masterPath is a valid and existing master path:
        if ( !this.mastersVsContents.containsKey(masterPath) )
          throw new IllegalArgumentException("No such master path: " + masterPath);

        // Require DOMParser
        this.ensureDOMParser();

        // Get the DOM tree:
        Document domDocument =
          this.domParser.parseDocument(new InputSource(this.getInputStream(masterPath)));

        // Create the master:
        Master master = new DOMMaster(domDocument.getDocumentElement());

        this.logDebug(METHOD_NAME + " 2/2: Done");
        return master;
      }
    catch (Exception exception)
      {
        throw new CheckinException(this.getIdentification(), exception);
      }
  }

  /**
   * Returns the content for the specified content path.
   */

  public Content getContent (String contentPath)
    throws CheckinException
  {
    try
      {
        final String METHOD_NAME = "getMaster";
        this.logDebug(METHOD_NAME + " 1/2: Started. contentPath = " + contentPath);

        // Check if contentPath is a valid and existing content path:
        if ( this.mastersVsContents.containsValue(contentPath) )
          throw new IllegalArgumentException("No such content path: " + contentPath);

        // Get input stream and size:
        InputStream inputStream = this.getInputStream(contentPath);
        long length = this.getSize(contentPath);

        // Create the content object:
        Content content = new StreamContent(inputStream, length);

        this.logDebug(METHOD_NAME + " 2/2: Done");
        return content;
      }
    catch (Exception exception)
      {
        throw new CheckinException(this.getIdentification(), exception);
      }
  }

  /**
   * Returns the source for the specified source path.
   */

  public Source getSource (String sourcePath)
    throws CheckinException
  {
    try
      {
        final String METHOD_NAME = "getMaster";
        this.logDebug(METHOD_NAME + " 1/2: Started. sourcePath = " + sourcePath);

        // Check if sourcePath is a valid and existing source path:
        if ( this.mastersVsSources.containsValue(sourcePath) )
          throw new IllegalArgumentException("No such source path: " + sourcePath);

        // Get input stream and size:
        InputStream inputStream = this.getInputStream(sourcePath);
        long length = this.getSize(sourcePath);

        // Create the source object:
        Source source = new StreamSource(inputStream, length);

        this.logDebug(METHOD_NAME + " 2/2: Done");
        return source;
      }
    catch (Exception exception)
      {
        throw new CheckinException(this.getIdentification(), exception);
      }
  }

  /**
   * Returns the content for the master with the specified path, or <code>null</code> if
   * there is no content for that master.
   */

  public Content getContentForMaster (String masterPath)
    throws CheckinException
  {
    try
      {
        final String METHOD_NAME = "getContentForMaster";
        this.logDebug(METHOD_NAME + " 1/2: Started. masterPath = " + masterPath);

        Content content = null;

        // Check if masterPath is a valid and existing master path:
        if ( !this.mastersVsContents.containsKey(masterPath) )
          throw new IllegalArgumentException("No such master path: " + masterPath);

        // Get content path:
        String contentPath = (String)this.mastersVsContents.get(masterPath);

        if ( contentPath != null )
          {
            // Get input stream and size:
            InputStream inputStream = this.getInputStream(contentPath);
            long length = this.getSize(contentPath);

            // Create the content object:
            content = new StreamContent(inputStream, length);
          }

        this.logDebug(METHOD_NAME + " 2/2: Done");
        return content;
      }
    catch (Exception exception)
      {
        throw new CheckinException(this.getIdentification(), exception);
      }
  }

  /**
   * Returns the source for the master with the specified path, or <code>null</code> if
   * there is no source for that master.
   */

  public Source getSourceForMaster (String masterPath)
    throws CheckinException
  {
    try
      {
        final String METHOD_NAME = "getSourceForMaster";
        this.logDebug(METHOD_NAME + " 1/2: Started. masterPath = " + masterPath);

        Source source = null;

        // Check if masterPath is a valid and existing master path:
        if ( !this.mastersVsSources.containsKey(masterPath) )
          throw new IllegalArgumentException("No such master path: " + masterPath);

        // Get source path:
        String sourcePath = (String)this.mastersVsSources.get(masterPath);

        if ( sourcePath != null )
          {
            // Get input stream and size:
            InputStream inputStream = this.getInputStream(sourcePath);
            long length = this.getSize(sourcePath);

            // Create the source object:
            source = new StreamSource(inputStream, length);
          }

        this.logDebug(METHOD_NAME + " 2/2: Done");
        return source;
      }
    catch (Exception exception)
      {
        throw new CheckinException(this.getIdentification(), exception);
      }
  }

  // --------------------------------------------------------------------------------
  // Identification method
  // --------------------------------------------------------------------------------
  
  /**
   * Returns a string that identifies this instance. It has the
   * following form:<pre>
   *   "DefaultZipCheckinRepository" +
   *   '#' + instanceId
   *   '(' + lifecycleStatus
   *   ')'</pre>
   * where <code>instanceId</code> is the instance id and <code>lifecycleStatus</code> the
   * lifecycle status of this instance.
   */

  public String getIdentification ()
  {
    return
      "DefaultZipCheckinRepository" +
      '#' + this.instanceStatus.getInstanceId() +
      '(' + this.instanceStatus.getLifecycleStatus() +
      ')';
  }
}
