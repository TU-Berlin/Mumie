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

package net.mumie.srv.checkin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.mumie.srv.notions.FileRole;
import net.mumie.srv.notions.MediaType;
import net.mumie.srv.service.AbstractMumieService;
import net.mumie.srv.service.ServiceInstanceStatus;
import net.mumie.srv.service.ServiceStatus;
import net.mumie.srv.util.PathTokenizer;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.activity.Disposable;


public class SimpleEditableCheckinRepository extends AbstractMumieService
  implements Recyclable, Disposable, EditableCheckinRepository
{
  // --------------------------------------------------------------------------------
  // h1: Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus =
    new ServiceStatus(SimpleEditableCheckinRepository.class);

  /**
   * Suffixes (role plus type suffix) of master files.
   */

  protected static final String MASTER_SUFFIXES =
    FileRole.suffixOf(FileRole.MASTER) + "." + MediaType.suffixOf(MediaType.TEXT_XML);

  /**
   * Role suffix of content files:
   */

  protected static final String CONTENT_ROLE_SUFFIX = FileRole.suffixOf(FileRole.CONTENT);

  /**
   * Maps master paths to masters
   */

  protected Map<String,Master> masters = new HashMap<String,Master>();

  /**
   * Maps content paths to contents
   */

  protected Map<String,Content> contents = new HashMap<String,Content>();

  /**
   * Maps content paths without suffixes to the corresponding complete paths.
   */

  protected Map<String,String> contentPaths = new HashMap<String,String>();

  /**
   * The path tokenizer to tokenize checkin paths.
   */

  protected PathTokenizer pathTokenizer = new PathTokenizer();

  // --------------------------------------------------------------------------------
  // h1: Lifecycle methods
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>SimpleEditableCheckinRepository</code>.
   */

  public SimpleEditableCheckinRepository ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Recycles this instance.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.masters = new HashMap<String,Master>();
    this.contents = new HashMap<String,Content>();
    this.contentPaths = new HashMap<String,String>();
    this.instanceStatus.notifyRecycle();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Disposes this instance.
   */

  public void dispose ()
  {
    final String METHOD_NAME = "dispose";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.masters = null;
    this.contents = null;
    this.contentPaths = null;
    this.instanceStatus.notifyDispose();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // h1: Adding masters, contents
  // --------------------------------------------------------------------------------

  /**
   * Adds the specified master with the specified path to the checkin repository.
   *
   * @throws CheckinException if something goes wrong
   */

  public void addMaster (String path, Master master)
    throws CheckinException
  {
    String basePath = this.getMasterBasePath(path);
    this.masters.put(path, master);
  }

  /**
   * Adds the specified content with the specified path to the checkin repository.
   *
   * @throws CheckinException if something goes wrong
   */

  public void addContent (String path, Content content)
    throws CheckinException
  {
    String basePath = getContentBasePath(path);
    if ( this.contentPaths.containsKey(basePath) )
      {
        String previousPath = this.contentPaths.get(basePath);
        if ( !previousPath.equals(path) )
          throw new CheckinException
            ("Multiple content files for same document: " + path + ", " + previousPath);
      }
    this.contents.put(path, content);
    this.contentPaths.put(basePath, path);
  }

  // --------------------------------------------------------------------------------
  // h1: Checking if masters / contents exist
  // --------------------------------------------------------------------------------

  /**
   * Returns true if this repository contains a master with the specified path,
   * otherwise false.
   */

  public final boolean hasMaster (String masterPath)
  {
    return this.masters.containsKey(masterPath);
  }

  /**
   * Returns true if this repository contains a content with the specified path,
   * otherwise false.
   */

  public final boolean hasContent (String contentPath)
  {
    return this.contents.containsKey(contentPath);
  }

  // --------------------------------------------------------------------------------
  // h1: Getting masters, contents
  // --------------------------------------------------------------------------------

  /**
   * Returns the master for the specified master path. If it does not exist, throws
   * an exeption.
   */

  public Master getMaster (String masterPath)
    throws CheckinException
  {
    if ( !this.hasMaster(masterPath) )
      throw new CheckinException("No such master path : " + masterPath);
    return this.masters.get(masterPath);
  }

  /**
   * Returns the content for the specified content path. If it does not exist, throws
   * an exeption.
   */

  public Content getContent (String contentPath)
    throws CheckinException
  {
    if ( !this.hasContent(contentPath) )
      throw new CheckinException("No such content path : " + contentPath);
    return this.contents.get(contentPath);
  }

  // --------------------------------------------------------------------------------
  // h1: Getting all master paths.
  // --------------------------------------------------------------------------------

  /**
   * Returns all master paths in this checkin repository.
   */

  public String[] getMasterPaths ()
  {
    Set<String> keys = this.masters.keySet();
    return keys.toArray(new String[keys.size()]);
  }

  // --------------------------------------------------------------------------------
  // h1: Getting content for master
  // --------------------------------------------------------------------------------

  /**
   * Returns the content path for the specified master path, or <code>null</code> if it does
   * not exist.
   */

  public String getContentPath (String masterPath)
    throws CheckinException
  {
    String basePath = this.getMasterBasePath(masterPath);
    return (basePath != null ? this.contentPaths.get(basePath) : null);
  }

  /**
   * Returns the content for the master with the specified path, or <code>null</code> if
   * there is no content for that master.
   */

  public Content getContentForMaster (String masterPath)
    throws CheckinException
  {
    String contentPath = this.getContentPath(masterPath);
    return (contentPath != null ? this.contents.get(contentPath) : null);
  }

  // --------------------------------------------------------------------------------
  // h1: Removing masters, contents
  // --------------------------------------------------------------------------------

  /**
   * Removes the master with the specified path. If no such master exists, throws an
   * exception.
   *
   * @throws CheckinException if something goes wrong
   */

  public void removeMaster (String path)
    throws CheckinException
  {
    if ( !this.hasMaster(path) )
      throw new CheckinException("No such master path : " + path);
    this.masters.remove(path);
  }

  /**
   * Removes the content with the specified path. If no such content exists, throws an
   * exception.
   *
   * @throws CheckinException if something goes wrong
   */

  public void removeContent (String path)
    throws CheckinException
  {
    if ( !this.hasContent(path) )
      throw new CheckinException("No such content path : " + path);
    this.contents.remove(path);
    this.contentPaths.remove(getContentBasePath(path));
  }

  // --------------------------------------------------------------------------------
  // h1: Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * Returns the specified master path with the role and type suffixes stripped. If the path
   * is not a master checkin path, an exception is thrown.
   */

  protected String getMasterBasePath (String path)
    throws CheckinException
  {
    this.pathTokenizer.tokenize(path);
    if ( !pathTokenizer.getSuffixes().equals(MASTER_SUFFIXES) )
      throw new CheckinException("Not a master path: " + path);
    return this.pathTokenizer.getPathWithoutSuffixes();
  }

  /**
   * Returns the specified content path with the role and type suffixes stripped. If the path
   * is not a content checkin path, an exception is thrown.
   */

  protected String getContentBasePath (String path)
    throws CheckinException
  {
    this.pathTokenizer.tokenize(path);
    if ( !pathTokenizer.getRoleSuffix().equals(CONTENT_ROLE_SUFFIX) )
      throw new CheckinException("Not a content path: " + path);
    return this.pathTokenizer.getPathWithoutSuffixes();
  }
}