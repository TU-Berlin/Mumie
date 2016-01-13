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

import java.util.Map;
import net.mumie.srv.notions.Id;
import org.w3c.dom.Element;

/**
 * <p>
 *   Represents the master of a document or pseudo-document.
 * </p>
 * <p>
 *   This interface provides access to the metainfos contained in the master file of a
 *   document or pseudo-document. It can be used with any types of documents and
 *   pseudo-documents. For each metainfo, a method <code>getXxxx()</code> exists
 *   where Xxxx denotes the metainfo. The return type depends on the metainfo. If a metainfo
 *   is requested that does not exist for the type of (pseudo-)document the master file
 *   belongs to, an exception is thrown (for example, if the <code>Master</code> object
 *   represents the master file of a "page" document, and {@link #getWidth getWidth} is
 *   called, an exception is thrown because pages do not have the "width" metainfo).
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: Master.java,v 1.3 2009/10/07 00:38:32 rassy Exp $</code>
 */

public interface Master
{
  /**
   * Returns the path, or <code>null</code> if no path is specified by this master.
   */

  public String getPath ()
    throws MasterException;

  /**
   * Returns the nature (document or pseudo-document).
   */

  public int getNature ()
    throws MasterException;

  /**
   * Returns the type.
   */

  public int getType ()
    throws MasterException;

  /**
   * Returns the type name.
   */

  public String getTypeName ()
    throws MasterException;

  /**
   * Returns the category. Throws an exception if the category can not be determined.
   */

  public int getCategory ()
    throws MasterException;

  /**
   * Returns the id, or {@link Id#UNDEFINED UNDEFINED} if no id is specified by this
   * master.
   */

  public int getId ()
    throws MasterException;

  /**
   * Returns the lid, or <code>null</code> if no lid is specified by this master.
   */

  public String getLid ()
    throws MasterException;

  /**
   * Only with theme references; returns the path of the generic document which is
   * implemented by the document represented by this master, or <code>null</code> if no
   * such path is set. 
   */

  public String getGenericDocPath ()
    throws MasterException;

  /**
   * Returns the name.
   */

  public String getName ()
    throws MasterException;

  /**
   * Returns the description, or <code>null</code> if none is set.
   */

  public String getDescription ()
    throws MasterException;

  /**
   * Returns the copyright note, or <code>null</code> if none is set.
   */

  public String getCopyright ()
    throws MasterException;

  /**
   * Returns the changelog note, or <code>null</code> if none is set.
   */

  public String getChangelog ()
    throws MasterException;

  /**
   * Returns the qualified name
   */

  public String getQualifiedName ()
    throws MasterException;

  /**
   * Returns the version, or -1 if not set.
   */

  public int getVersion ()
    throws MasterException;

  /**
   * Returns the vc thread, or {@link Id#UNDEFINED UNDEFINED} if not set.
   */

  public int getVCThread ()
    throws MasterException;

  /**
   * Returns the width. Throws an exception if the width can not be determined.
   */

  public int getWidth ()
    throws MasterException;

  /**
   * Returns the height. Throws an exception if the height can not be determined.
   */

  public int getHeight ()
    throws MasterException;

  /**
   * Returns the duration. Throws an exception if the duration can not be determined.
   */

  public int getDuration ()
    throws MasterException;

  /**
   * Returns the corrector path, or <code>null</code> none is set.
   */

  public String getCorrectorPath ()
    throws MasterException;

  /**
   * Returns the info page path, or <code>null</code> none is set.
   */

  public String getInfoPagePath ()
    throws MasterException;

  /**
   * Returns the thumbnail path, or <code>null</code> none is set.
   */

  public String getThumbnailPath ()
    throws MasterException;

  /**
   * Returns the summary path, or <code>null</code> none is set.
   */

  public String getSummaryPath ()
    throws MasterException;

  /**
   * Returns the semester path, or <code>null</code> none is set.
   */

  public String getSemesterPath ()
    throws MasterException;

  /**
   * Returns the tutor path, or <code>null</code> none is set.
   */

  public String getTutorPath ()
    throws MasterException;

  /**
   * Returns the <em>hide</em> flag, or null if not set.
   */

  public Boolean getHide ()
    throws MasterException;

  /**
   * Returns the <em>is_wrapper</em> flag of the document represented by the specified
   * master root element, or null if not set.
   */

  public Boolean getIsWrapper ()
    throws MasterException;

  /**
   * Returns the theme master, or null if not set.
   */

  public Master getTheme ()
    throws MasterException;

  /**
   * Returns the path of the e-learning class of the document represented by the specified
   * master root element, or <code>null</code> if none is set.
   */

  public String getELClassPath ()
    throws MasterException;

  /**
   * Returns the content type. Throws an excpetion if the content type can not be detected.
   */

  public int getContentType ()
    throws MasterException;

  /**
   * Returns the timeframe start. Throws an exception if the timeframe start can not be
   * determined. 
   */

  public long getTimeframeStart ()
    throws MasterException;

  /**
   * Returns the timeframe end. Throws an exception if the timeframe end can not be
   * determined. 
   */

  public long getTimeframeEnd ()
    throws MasterException;

  /**
   * Returns the login name.
   */

  public String getLoginName ()
    throws MasterException;

  /**
   * Returns the password.
   */

  public String getPassword ()
    throws MasterException;

  /**
   * Returns the first name.
   */

  public String getFirstName ()
    throws MasterException;

  /**
   * Returns the surname.
   */

  public String getSurname ()
    throws MasterException;

  /**
   * Returns the email.
   */

  public String getEmail ()
    throws MasterException;

  /**
   * Returns the code.
   */

  public String getCode ()
    throws MasterException;

  /**
   * Returns the components of the document represented by this master
   */

  public Master[] getComponents ()
    throws MasterException;

  /**
   * Returns the links in the document represented by this master
   */

  public Master[] getLinks ()
    throws MasterException;

  /**
   * Returns the members of the document represented by this master
   */

  public Master[] getMembers ()
    throws MasterException;

  /**
   * Returns the authors of the document represented by this master
   */

  public Master[] getAuthors ()
    throws MasterException;

  /**
   * Returns the GDIM entries of the document represented by this master
   */

  public GDIMEntry[] getGDIMEntries ()
    throws MasterException;

  /**
   * Returns the read permissions of the document represented by this master
   */

  public Master[] getReadPermissions ()
    throws MasterException;

  /**
   * Returns the write permissions of the document represented by this master
   */

  public Master[] getWritePermissions ()
    throws MasterException;

  /**
   * Returns the user groups of the user represented by this master
   */

  public Master[] getUserGroups ()
    throws MasterException;
 
  /**
   * Returns the label, or null if none is specified.
   */

  public String getLabel ()
    throws MasterException;

  /**
   * Returns the maximum points for a (generic) problem, or null if none are specified.
   */

  public Integer getPoints ()
    throws MasterException;

  /**
   * Returns the document types the user group represented by this master can
   * create new documents for.
   */

  public int[] getCreatePermissions ()
    throws MasterException;

  /**
   * Returns all path-like attribute values that reference another document or
   * pseudo-document.
   */

  public String[] getAllRefPaths ();
}
