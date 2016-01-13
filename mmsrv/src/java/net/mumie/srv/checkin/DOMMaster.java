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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A {@link Master} implementation that wraps a DOM tree of the master file. The methods
 * to obtain the metainfos are implemented by means of the corresponding methods in
 * {@link MasterUtil MasterUtil}.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: DOMMaster.java,v 1.2 2009/10/07 00:38:32 rassy Exp $</code>
 */

public class DOMMaster
  implements Master
{
  // --------------------------------------------------------------------------------
  // Gloabl variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The root element of this master.
   */

  protected Element rootElement;

  // --------------------------------------------------------------------------------
  // Constructors
  // --------------------------------------------------------------------------------

  /**
   * Creates a new instance from the DOM tree starting with the specified element.
   */

  public DOMMaster (Element rootElement)
  {
    this.rootElement = rootElement;
  }

  /**
   * 
   */

  public DOMMaster (Document document)
  {
    this.rootElement = document.getDocumentElement();
  }

  // --------------------------------------------------------------------------------
  // Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * Converts the specified master root elements to {@link Master} objects, and returns the
   * latter as an array.
   */

  protected static Master[] toMasterArray (Element[] elements)
  {
    Master[] masters = new DOMMaster[elements.length];
    for (int i=0; i < elements.length; i++)
      masters[i] = new DOMMaster(elements[i]);
    return masters;
  }

  /**
   * Converts the specified elements to {@link GDIMEntry} objects, and returns the
   * latter as an array.
   */

  protected static GDIMEntry[] toGDIMEntryArray (Element[] elements)
  {
    GDIMEntry[] gdimEntries = new DOMGDIMEntry[elements.length];
    for (int i=0; i < elements.length; i++)
      gdimEntries[i] = new DOMGDIMEntry(elements[i]);
    return gdimEntries;
  }

  // --------------------------------------------------------------------------------
  // Simple "get" methods
  // --------------------------------------------------------------------------------

  /**
   * Returns the path, or <code>null</code> if no path is specified by this master.
   */

  public String getPath ()
    throws MasterException
  {
    return MasterUtil.getPath(this.rootElement);
  }

  /**
   * Returns the nature (document or pseudo-document).
   */

  public int getNature ()
    throws MasterException
  {
    return MasterUtil.getNature(this.rootElement);
  }

  /**
   * Returns the type.
   */

  public int getType ()
    throws MasterException
  {
    return MasterUtil.getType(this.rootElement);
  }

  /**
   * Returns the type name.
   */

  public String getTypeName ()
    throws MasterException
  {
    return MasterUtil.getTypeName(this.rootElement);
  }

  /**
   * Returns the category. Throws an exception if the category can not be determined.
   */

  public int getCategory ()
    throws MasterException
  {
    return MasterUtil.getCategory(this.rootElement);
  }

  /**
   * Returns the id, or {@link Id#UNDEFINED UNDEFINED} if no id is specified by this
   * master.
   */

  public int getId ()
    throws MasterException
  {
    return MasterUtil.getId(this.rootElement);
  }

  /**
   * Returns the lid, or <code>null</code> if no lid is specified by this master.
   */

  public String getLid ()
    throws MasterException
  {
    return MasterUtil.getLid(this.rootElement);
  }

  /**
   * Only with theme references; returns the path of the generic document which is
   * implemented by the document represented by this master, or <code>null</code> if no
   * such path is set. 
   */

  public String getGenericDocPath ()
    throws MasterException
  {
    return MasterUtil.getGenericDocPath(this.rootElement);
  }

  /**
   * Returns the name.
   */

  public String getName ()
    throws MasterException
  {
    return MasterUtil.getName(this.rootElement);
  }

  /**
   * Returns the description, or <code>null</code> if none is set.
   */

  public String getDescription ()
    throws MasterException
  {
    return MasterUtil.getDescription(this.rootElement);
  }

  /**
   * Returns the copyright note, or <code>null</code> if none is set.
   */

  public String getCopyright ()
    throws MasterException
  {
    return MasterUtil.getCopyright(this.rootElement);
  }

  /**
   * Returns the changelog note, or <code>null</code> if none is set.
   */

  public String getChangelog ()
    throws MasterException
  {
    return MasterUtil.getChangelog(this.rootElement);
  }

  /**
   * Returns the qualified name
   */

  public String getQualifiedName ()
    throws MasterException
  {
    return MasterUtil.getQualifiedName(this.rootElement);
  }

  /**
   * Returns the version, or -1 if not set.
   */

  public int getVersion ()
    throws MasterException
  {
    return MasterUtil.getVersion(this.rootElement);
  }

  /**
   * Returns the vc thread, or {@link Id#UNDEFINED UNDEFINED} if not set.
   */

  public int getVCThread ()
    throws MasterException
  {
    return MasterUtil.getVCThread(this.rootElement);
  }

  /**
   * Returns the <em>hide</em> flag, or null if not set.
   */

  public Boolean getHide ()
    throws MasterException
  {
    return MasterUtil.getHide(this.rootElement);
  }

  /**
   * Returns the <em>is_wrapper</em> flag, or null if not set.
   */

  public Boolean getIsWrapper ()
    throws MasterException
  {
    return MasterUtil.getIsWrapper(this.rootElement);
  }

  /**
   * Returns the theme, or null if not set.
   */

  public Master getTheme ()
    throws MasterException
  {
    return MasterUtil.getTheme(this.rootElement);
  }

  /**
   * Returns the width. Throws an exception if the width can not be determined.
   */

  public int getWidth ()
    throws MasterException
  {
    return MasterUtil.getWidth(this.rootElement);
  }

  /**
   * Returns the height. Throws an exception if the height can not be determined.
   */

  public int getHeight ()
    throws MasterException
  {
    return MasterUtil.getHeight(this.rootElement);
  }

  /**
   * Returns the duration. Throws an exception if the duration can not be determined.
   */

  public int getDuration ()
    throws MasterException
  {
    return MasterUtil.getDuration(this.rootElement);
  }

  /**
   * Returns the corrector path, or <code>null</code> none is set.
   */

  public String getCorrectorPath ()
    throws MasterException
  {
    return MasterUtil.getCorrectorPath(this.rootElement);
  }

  /**
   * Returns the info page path, or <code>null</code> none is set.
   */

  public String getInfoPagePath ()
    throws MasterException
  {
    return MasterUtil.getInfoPagePath(this.rootElement);
  }

  /**
   * Returns the thumbnail path, or <code>null</code> none is set.
   */

  public String getThumbnailPath ()
    throws MasterException
  {
    return MasterUtil.getThumbnailPath(this.rootElement);
  }

  /**
   * Returns the summary path, or <code>null</code> none is set.
   */

  public String getSummaryPath ()
    throws MasterException
  {
    return MasterUtil.getSummaryPath(this.rootElement);
  }

  /**
   * Returns the semester path, or <code>null</code> none is set.
   */

  public String getSemesterPath ()
    throws MasterException
  {
    return MasterUtil.getSemesterPath(this.rootElement);
  }

  /**
   * Returns the tutor path, or <code>null</code> none is set.
   */

  public String getTutorPath ()
    throws MasterException
  {
    return MasterUtil.getTutorPath(this.rootElement);
  }

  /**
   * Returns the path of the e-learning class of the document represented by the specified
   * master root element, or <code>null</code> if none is set.
   */

  public String getELClassPath ()
    throws MasterException
  {
    return MasterUtil.getELClassPath(this.rootElement);
  }

  /**
   * Returns the content type. Throws an excpetion if the content type can not be detected.
   */

  public int getContentType ()
    throws MasterException
  {
    return MasterUtil.getContentType(this.rootElement);
  }

  /**
   * Returns the timeframe start. Throws an exception if the timeframe start can not be
   * determined. 
   */

  public long getTimeframeStart ()
    throws MasterException
  {
    return MasterUtil.getTimeframeStart(this.rootElement);
  }

  /**
   * Returns the timeframe end. Throws an exception if the timeframe end can not be
   * determined. 
   */

  public long getTimeframeEnd ()
    throws MasterException
  {
    return MasterUtil.getTimeframeEnd(this.rootElement);
  }

  /**
   * Returns the login name.
   */

  public String getLoginName ()
    throws MasterException
  {
    return MasterUtil.getLoginName(this.rootElement);
  }

  /**
   * Returns the password.
   */

  public String getPassword ()
    throws MasterException
  {
    return MasterUtil.getPassword(this.rootElement);
  }

  /**
   * Returns the first name.
   */

  public String getFirstName ()
    throws MasterException
  {
    return MasterUtil.getFirstName(this.rootElement);
  }

  /**
   * Returns the surname.
   */

  public String getSurname ()
    throws MasterException
  {
    return MasterUtil.getSurname(this.rootElement);
  }

  /**
   * Returns the email.
   */

  public String getEmail ()
    throws MasterException
  {
    return MasterUtil.getEmail(this.rootElement);
  }

  /**
   * Returns the language code.
   */

  public String getCode ()
    throws MasterException
  {
    return MasterUtil.getCode(this.rootElement);
  }

  /**
   * Returns the label, or null if none are specified.
   */

  public String getLabel ()
    throws MasterException
  {
    return MasterUtil.getLabel(this.rootElement);
  }

  /**
   * Returns the maximum points for a (generic) problem, or null if none are specified.
   */

  public Integer getPoints ()
    throws MasterException
  {
    return MasterUtil.getPoints(this.rootElement);
  }

  // --------------------------------------------------------------------------------
  // Complex "get" methods
  // --------------------------------------------------------------------------------

  /**
   * Returns the components of the document represented by this master
   */

  public Master[] getComponents ()
    throws MasterException
  {
    return toMasterArray(MasterUtil.getComponents(this.rootElement));
  }

  /**
   * Returns the links in the document represented by this master
   */

  public Master[] getLinks ()
    throws MasterException
  {
    return toMasterArray(MasterUtil.getLinks(this.rootElement));
  }

  /**
   * Returns the members of the user group represented by this master
   */

  public Master[] getMembers ()
    throws MasterException
  {
    return toMasterArray(MasterUtil.getMembers(this.rootElement));
  }

  /**
   * Returns the authors of the document represented by this master
   */

  public Master[] getAuthors ()
    throws MasterException
  {
    return toMasterArray(MasterUtil.getAuthors(this.rootElement));
  }

  /**
   * Returns the GDIM entries of the document represented by this master
   */

  public GDIMEntry[] getGDIMEntries ()
    throws MasterException
  {
    return toGDIMEntryArray(MasterUtil.getGDIMEntries(this.rootElement));
  }

  /**
   * Returns the read permissions of the document represented by this master
   */

  public Master[] getReadPermissions ()
    throws MasterException
  {
    return toMasterArray(MasterUtil.getReadPermissions(this.rootElement));
  }

  /**
   * Returns the write permissions of the document represented by this master
   */

  public Master[] getWritePermissions ()
    throws MasterException
  {
    return toMasterArray(MasterUtil.getWritePermissions(this.rootElement));
  }

  /**
   * Returns the user groups of the user represented by this master
   */

  public Master[] getUserGroups ()
    throws MasterException
  {
    return toMasterArray(MasterUtil.getUserGroups(this.rootElement));
  }

  /**
   * Returns the document types the user group represented by this master can
   * create new documents for.
   */

  public int[] getCreatePermissions ()
    throws MasterException
  {
    return MasterUtil.getCreatePermissions(this.rootElement);
  }

  // --------------------------------------------------------------------------------
  // Getting all referenced (pseudo-)documents
  // --------------------------------------------------------------------------------

  /**
   * Returns all path-like attribute values that reference another document or
   * pseudo-document.
   */

  public String[] getAllRefPaths ()
  {
    return MasterUtil.getAllRefPaths(this.rootElement);
  }
}
