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
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.mumie.cocoon.notions.Category;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.MediaType;
import net.mumie.cocoon.notions.Nature;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.RefAttrib;
import net.mumie.cocoon.notions.UserRole;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.notions.XMLElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>
 *   Static utilities to deal with masters.
 * </p>
 * <p>
 *   The main purpose of this class is to provide methods to extract metainfos from the DOM
 *   tree of a master file. For each metainfo, a method
 *   <code>getXxxx(Element&nbsp;masterRootElement)</code> exists where Xxxx denotes the 
 *   metainfo and <code>masterRootElement</code> is the root element of the DOM tree
 *   representing the master file. The return type depends on the metainfo. 
 * </p>
 * <p>
 *   There are two variants of such methods: those extracting simple data, and those
 *   extracting complex data. <em>Simple data</em> consist of only one value, whereas
 *   <em>complex data</em> consist of a list, map, or array of values.
 * </p>
 * <p>
 *    Here is a list of the methods of the first variant:
 * </p>
 * <table class="genuine" style="margin-left:1em">
 *   <thead>
 *     <tr>
 *       <td>Method</td>
 *       <td>Metainfo value</td>
 *       <td>Return type</td>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td>{@link #getNature getNature}</td>
 *       <td>Whether the document is a "normal" one or a pseudo-document (actually, this
 *       is not a real metainfo)</td> 
 *       <td><code>int</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getTypeName getTypeName}</td>
 *       <td>Document or pseudo-document type name</td>
 *       <td><code>String</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getDocType getDocType}</td>
 *       <td>Document type (numerical code)</td>
 *       <td><code>int</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getPseudoDocType getPseudoDocType}</td>
 *       <td>Pseudo-document type (numerical code)</td>
 *       <td><code>int</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getType getType}</td>
 *       <td>Document or pseudo-document type (numerical code)</td>
 *       <td><code>int</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getCategory getCategory}</td>
 *       <td>Category (numerical code resp. id)</td>
 *       <td><code>int</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getId getId}</td>
 *       <td>Id</td>
 *       <td><code>int</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getLid getLid}</td>
 *       <td>Local id</td>
 *       <td><code>String</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getPath getPath}</td>
 *       <td>Path</td>
 *       <td><code>String</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getOldPath getOldPath}</td>
 *       <td>Old path</td>
 *       <td><code>String</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getGenericDocId getGenericDocId}</td>
 *       <td>Id of the implemented generic document</td>
 *       <td><code>int</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getGenericDocPath getGenericDocPath}</td>
 *       <td>Path of the implemented generic document</td>
 *       <td><code>String</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getName getName}</td>
 *       <td>Name</td>
 *       <td><code>String</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getDescription getDescription}</td>
 *       <td>Description</td>
 *       <td><code>String</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getCopyright getCopyright}</td>
 *       <td>Copyright note</td>
 *       <td><code>String</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getChangelog getChangelog}</td>
 *       <td>Changelog info</td>
 *       <td><code>String</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getQualifiedName getQualifiedName}</td>
 *       <td>Qualified name</td>
 *       <td><code>String</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getVersion getVersion}</td>
 *       <td>Version</td>
 *       <td><code>int</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getVCThread getVCThread}</td>
 *       <td>Version control thread (id)</td>
 *       <td><code>int</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getWidth getWidth}</td>
 *       <td>Width</td>
 *       <td><code>int</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getHeight getHeight}</td>
 *       <td>Height</td>
 *       <td><code>int</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getDuration getDuration}</td>
 *       <td>Duration</td>
 *       <td><code>int</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getCorrectorPath getCorrectorPath}</td>
 *       <td>Path to corrector (for problems - a document of type java_class)</td>
 *       <td><code>String</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getInfoPagePath getInfoPagePath}</td>
 *       <td>Path to info page (a document of type page)</td>
 *       <td><code>String</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getSummaryPath getSummaryPath}</td>
 *       <td>Path to summary (a document of type page)</td>
 *       <td><code>String</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getSemesterPath getSemesterPath}</td>
 *       <td>Path to semester (for elearning classes - a pseudo-document of type semester)</td>
 *       <td><code>String</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getTutorPath getTutorPath}</td>
 *       <td>Path to tutor (for tutorials - a pseudo-document of type user)</td>
 *       <td><code>String</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getELClassPath getELClassPath}</td>
 *       <td>Path to e-learning class (for courses and tutorials)</td>
 *       <td><code>String</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getContentType getContentType}</td>
 *       <td>Content type (id)</td>
 *       <td><code>int</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getSourceType getSourceType}</td>
 *       <td>Source type (id)</td>
 *       <td><code>int</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getCreated getCreated}</td>
 *       <td>Creation time</td>
 *       <td><code>long</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getLastModified getLastModified}</td>
 *       <td>Last modification time</td>
 *       <td><code>long</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getTimeframeStart getTimeframeStart}</td>
 *       <td>Timeframe start time</td>
 *       <td><code>long</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getTimeframeEnd getTimeframeEnd}</td>
 *       <td>Timeframe end time</td>
 *       <td><code>long</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getLoginName getLoginName}</td>
 *       <td>Login name (of a user)</td>
 *       <td><code>String</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getPassword getPassword}</td>
 *       <td>Password (of a user)</td>
 *       <td><code>String</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getFirstName getFirstName}</td>
 *       <td>First name (of a user)</td>
 *       <td><code>String</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getSurname getSurname}</td>
 *       <td>Surname (of a user)</td>
 *       <td><code>String</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getEmail getEmail}</td>
 *       <td>Email (of a user)</td>
 *       <td><code>String</code></td>
 *     </tr>
 *   <tbody>
 * </table>
 * <p>
 *   Here is a list of the methods of the second variant:
 * </p>
 * <table class="genuine" style="margin-left:1em">
 *   <thead>
 *     <tr>
 *       <td>Method</td>
 *       <td>Metainfo values</td>
 *       <td>Return type</td>
 *     </tr>
 *   </thead>
 *     <tr>
 *       <td>{@link #getComponents getComponents}</td>
 *       <td>The components</td>
 *       <td><code>Element[]</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getLinks getLinks}</td>
 *       <td>The links</td>
 *       <td><code>Element[]</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getAttachables getAttachables}</td>
 *       <td>The attachables</td>
 *       <td><code>Element[]</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getMembers getMembers}</td>
 *       <td>The members</td>
 *       <td><code>Element[]</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getAuthors getAuthors}</td>
 *       <td>The authors</td>
 *       <td><code>Element[]</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getThemes getThemes}</td>
 *       <td>The themes</td>
 *       <td><code>Element[]</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getReadPermissions getReadPermissions}</td>
 *       <td>The read permissions</td>
 *       <td><code>Element[]</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getWritePermissions getWritePermissions}</td>
 *       <td>The write permissions</td>
 *       <td><code>Element[]</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getUserGroups getUserGroups}</td>
 *       <td>The user groups (of a user)</td>
 *       <td><code>Element[]</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getRefAttribs getRefAttribs}</td>
 *       <td>The reference attributes</td> 
 *       <td><code>Map</code></td>
 *     </tr>
 *     <tr>
 *       <td>{@link #getCreatePermissions getCreatePermissions}</td>
 *       <td>The create permisssion s (of a user group)</td> 
 *       <td><code>int[]</code></td>
 *     </tr>
 *   <tbody>
 * </table>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: MasterUtil.java,v 1.37 2009/06/16 16:06:23 rassy Exp $</code>
 */

public class MasterUtil
{
  // --------------------------------------------------------------------------------
  // Getting simple data (single values)
  // --------------------------------------------------------------------------------

  /**
   * Returns {@link Nature#DOCUMENT Nature.DOCUMENT} if the master given by the specified
   * root element belongs to a "proper" document, and
   *{@link Nature#PSEUDO_DOCUMENT Nature.PSEUDO_DOCUMENT} if it belongs to a
   *pseudo-document.
   */

  public static int getNature (Element masterRootElement)
    throws MasterException
  {
    String typeName = masterRootElement.getLocalName();
    if ( DocType.codeFor(typeName) != DocType.UNDEFINED )
      return Nature.DOCUMENT;
    else if ( PseudoDocType.codeFor(typeName) != PseudoDocType.UNDEFINED )
      return Nature.PSEUDO_DOCUMENT;
    else
      throw new MasterException
        ("Unknown (pseudo-)document type name: " + typeName);
  }

  /**
   * Returns the type of the document or pseudo-document represented by the specified master
   * root element, as string name.
   */

  public static String getTypeName (Element masterRootElement)
    throws MasterException
  {
    return masterRootElement.getLocalName();
  }

  /**
   * Returns the type of the document represented by the specified master root element.
   */

  public static int getDocType (Element masterRootElement)
    throws MasterException
  {
    String typeName = masterRootElement.getLocalName();
    int type = DocType.codeFor(typeName);
    if ( type == DocType.UNDEFINED )
      throw new MasterException
        (masterRootElement, "Unknown document type name: " + typeName);
    return type;
  }

  /**
   * Returns the type of the pseudo-document represented by the specified master root
   * element.
   */

  public static int getPseudoDocType (Element masterRootElement)
    throws MasterException
  {
    String typeName = masterRootElement.getLocalName();
    int type = PseudoDocType.codeFor(typeName);
    if ( type == PseudoDocType.UNDEFINED )
      throw new MasterException
        (masterRootElement, "Unknown pseudo-document type name: " + typeName);
    return type;
  }

  /**
   * Returns the type of the document or pseudo-document represented by the specified master
   * root element.
   */

  public static int getType (Element masterRootElement)
    throws MasterException
  {
    String typeName = masterRootElement.getLocalName();
    int type = DocType.codeFor(typeName);
    if ( type != DocType.UNDEFINED )
      return type;
    else
      {
        type = PseudoDocType.codeFor(typeName);
        if ( type != PseudoDocType.UNDEFINED )
          return type;
        else
          throw new MasterException
            ("Unknown (pseudo-)document type name: " + typeName);
      }
  }

  /**
   * Returns the category of the (pseudo-)document represented by the specified master root
   * element. Throws an exception if the category can not be determined.
   */

  public static int getCategory (Element masterRootElement)
    throws MasterException
  {
    String categoryName =
      getAttribAsString(masterRootElement, XMLElement.CATEGORY, XMLAttribute.NAME, true);
    return Category.codeFor(categoryName);
  }

  /**
   * Returns the id of the (pseudo-)document represented by the specified master root
   * element, or {@link Id#UNDEFINED UNDEFINED} if the (pseudo-)document has no id.
   */

  public static int getId (Element masterRootElement)
    throws MasterException
  {
    return getAttribAsId(masterRootElement, XMLAttribute.ID, false);
  }

  /**
   * Returns the lid of the (pseudo-)document represented by the specified master root
   * element, or <code>null</code> if the (pseudo-)document has no lid.
   */

  public static String getLid (Element masterRootElement)
    throws MasterException
  {
    return getAttribAsString(masterRootElement, XMLAttribute.LID, false);
  }

  /**
   * Returns the path of the (pseudo-)document represented by the specified master root
   * element, or <code>null</code> if the (pseudo-)document has no path.
   */

  public static String getPath (Element masterRootElement)
    throws MasterException
  {
    String path = getAttribAsString(masterRootElement, XMLAttribute.PATH, false);

    // Support for legacy "url" attribute:
    if ( path == null )
      path = getAttribAsString(masterRootElement, XMLAttribute.URL, false);
    
    return path;
  }

  /**
   * Returns the path of the (pseudo-)document represented by the specified master root
   * element, or <code>null</code> if the (pseudo-)document has no path.
   */

  public static String getOldPath (Element masterRootElement)
    throws MasterException
  {
    String oldPath = getAttribAsString(masterRootElement, XMLAttribute.OLD_PATH, false);

    // Support for legacy attribute naming style:
    if ( oldPath == null )
      oldPath = getAttribAsString(masterRootElement, XMLAttribute.OLD_URL, false);
    
    return oldPath;
  }

  /**
   * Returns the name of the (pseudo-)document represented by the specified master root
   * element. Throws an exception if the name can not be determined.
   */

  public static String getName (Element masterRootElement)
    throws MasterException
  {
    return getTextNodesAsString(masterRootElement, XMLElement.NAME, true);
  }

  /**
   * Returns the description of the (pseudo-)document represented by the specified master
   * root element, or <code>null</code> if no description exists.
   */

  public static String getDescription (Element masterRootElement)
    throws MasterException
  {
    return getTextNodesAsString(masterRootElement, XMLElement.DESCRIPTION, false);
  }

  /**
   * Returns the copyright of the (pseudo-)document represented by the specified master
   * root element, or <code>null</code> if not set.
   */

  public static String getCopyright (Element masterRootElement)
    throws MasterException
  {
    return getTextNodesAsString(masterRootElement, XMLElement.COPYRIGHT, false);
  }

  /**
   * Returns the changelog of the (pseudo-)document represented by the specified master
   * root element, or <code>null</code> if not set.
   */

  public static String getChangelog (Element masterRootElement)
    throws MasterException
  {
    return getTextNodesAsString(masterRootElement, XMLElement.CHANGELOG, false);
  }

  /**
   * Returns the qualified of the (pseudo-)document represented by the specified master
   * root element. Throws an exception if the qualified name can not be found.
   */

  public static String getQualifiedName (Element masterRootElement)
    throws MasterException
  {
    return getTextNodesAsString(masterRootElement, XMLElement.QUALIFIED_NAME, true);
  }

  /**
   * Returns the version of the document represented by the specified master root element,
   * or -1 if not set.
   */

  public static int getVersion (Element masterRootElement)
    throws MasterException
  {
    return getAttribAsNonNegativeInt
      (masterRootElement, XMLElement.VERSION, XMLAttribute.VALUE, false);
  }

  /**
   * Returns the id of the vc thread of the document represented by the specified master
   * root element, or {@link Id#UNDEFINED UNDEFINED} if not set.
   */

  public static int getVCThread (Element masterRootElement)
    throws MasterException
  {
    return getAttribAsId
      (masterRootElement, XMLElement.VC_THREAD, XMLAttribute.ID, false);
  }

  /**
   * Returns the <em>hide</em> flag of the (pseudo-)document represented by the specified
   * master root element, or null if not set.
   */

  public static Boolean getHide (Element masterRootElement)
    throws MasterException
  {
    return getAttribAsBooleanObject
      (masterRootElement, XMLElement.HIDE, XMLAttribute.VALUE, false);
  }

  /**
   * Returns the <em>is_wrapper</em> flag of the document represented by the specified
   * master root element, or null if not set.
   */

  public static Boolean getIsWrapper (Element masterRootElement)
    throws MasterException
  {
    return getAttribAsBooleanObject
      (masterRootElement, XMLElement.IS_WRAPPER, XMLAttribute.VALUE, false);
  }

  /**
   * Returns the width of the document represented by the specified master root
   * element. Throws an exception if the width can not be determined.
   */

  public static int getWidth (Element masterRootElement)
    throws MasterException
  {
    return getAttribAsInt(masterRootElement, XMLElement.WIDTH, XMLAttribute.VALUE);
  }

  /**
   * Returns the height of the document represented by the specified master root
   * element. Throws an exception if the height can not be determined.
   */

  public static int getHeight (Element masterRootElement)
    throws MasterException
  {
    return getAttribAsInt(masterRootElement, XMLElement.HEIGHT, XMLAttribute.VALUE);
  }

  /**
   * Returns the duration of the document represented by the specified master root
   * element. Throws an exception if the duration can not be determined.
   */

  public static int getDuration (Element masterRootElement)
    throws MasterException
  {
    return getAttribAsInt(masterRootElement, XMLElement.DURATION, XMLAttribute.VALUE);
  }

  /**
   * Returns the path of the corrector of the document represented by the specified master root
   * element, or <code>null</code> if none is set.
   */

  public static String getCorrectorPath (Element masterRootElement)
    throws MasterException
  {
    String path =
      getAttribAsString(masterRootElement, XMLElement.CORRECTOR, XMLAttribute.PATH, false);

    // Support for legacy "url" attribute:
    if ( path == null )
      path =
        getAttribAsString(masterRootElement, XMLElement.CORRECTOR, XMLAttribute.URL, false);

    return path;
  }

  /**
   * Returns the path of the info page of the document represented by the specified master root
   * element, or <code>null</code> if none is set.
   */

  public static String getInfoPagePath (Element masterRootElement)
    throws MasterException
  {
    String path =
      getAttribAsString(masterRootElement, XMLElement.INFO_PAGE, XMLAttribute.PATH, false);

    // Support for legacy "url" attribute:
    if ( path == null )
      path =
        getAttribAsString(masterRootElement, XMLElement.INFO_PAGE, XMLAttribute.URL, false);

    return path;
  }

  /**
   * Returns the path of the thumbnail of the document represented by the specified master root
   * element, or <code>null</code> if none is set.
   */

  public static String getThumbnailPath (Element masterRootElement)
    throws MasterException
  {
    String path =
      getAttribAsString(masterRootElement, XMLElement.THUMBNAIL, XMLAttribute.PATH, false);

    return path;
  }

  /**
   * Returns the path of the summary of the document represented by the specified master root
   * element, or <code>null</code> if none is set.
   */

  public static String getSummaryPath (Element masterRootElement)
    throws MasterException
  {
    String path =
      getAttribAsString(masterRootElement, XMLElement.DOC[DocType.GENERIC_SUMMARY], XMLAttribute.PATH, false);

    // Support for legacy "url" attribute:
    if ( path == null )
      path =
        getAttribAsString(masterRootElement, XMLElement.DOC[DocType.GENERIC_SUMMARY], XMLAttribute.URL, false);

    return path;
  }

  /**
   * Returns the path of the semester of the document represented by the specified master root
   * element, or <code>null</code> if none is set.
   */

  public static String getSemesterPath (Element masterRootElement)
    throws MasterException
  {
    String path =
      getAttribAsString(masterRootElement, XMLElement.SEMESTER, XMLAttribute.PATH, false);

    // Support for legacy "url" attribute:
    if ( path == null )
      path =
        getAttribAsString(masterRootElement, XMLElement.SEMESTER, XMLAttribute.URL, false);

    return path;
  }

  /**
   * Returns the path of the tutor of the document represented by the specified master root
   * element, or <code>null</code> if none is set.
   */

  public static String getTutorPath (Element masterRootElement)
    throws MasterException
  {
    String path = null;
    Element tutorElement = getChildElement(masterRootElement, XMLElement.TUTOR, false);
    if ( tutorElement != null )
      {
        path = getAttribAsString(tutorElement, XMLElement.USER, XMLAttribute.PATH, false);

        // Support for legacy "url" attribute:
        if ( path == null )
          path = getAttribAsString(tutorElement, XMLElement.USER, XMLAttribute.URL, false);
      }
    return path;
  }

  /**
   * Returns the path of the e-learning class of the pseudo-document represented by the
   * specified master root element, or <code>null</code> if none is set.
   */

  public static String getELClassPath (Element masterRootElement)
    throws MasterException
  {
    String path =
      getAttribAsString(masterRootElement, XMLElement.CLASS, XMLAttribute.PATH, false);

    // Support for legacy "url" attribute:
    if ( path == null )
      path =
        getAttribAsString(masterRootElement, XMLElement.CLASS, XMLAttribute.URL, false);

    return path;
  }

  /**
   * Returns the path of the sync home of the user represented by the specified master root
   * element, or <code>null</code> if none is set.
   */

  public static String getSyncHomePath (Element masterRootElement)
    throws MasterException
  {
    String path = null;
    Element syncHomeElement = getChildElement(masterRootElement, XMLElement.SYNC_HOME, false);
    if ( syncHomeElement != null )
      path = getAttribAsString(syncHomeElement, XMLElement.SECTION, XMLAttribute.PATH, true);
    return path;
  }

  /**
   * Returns the content type of the (pseudo-)document represented by the specified master root
   * element. Throws an excpetion if the content type can not be detected.
   */

  public static int getContentType (Element masterRootElement)
    throws MasterException
  {
    Element contentTypeElement =
      getChildElement(masterRootElement, XMLElement.CONTENT_TYPE, true);
    String majorTypeName =
      getAttribAsString(contentTypeElement, XMLAttribute.TYPE, false);
    if ( majorTypeName != null )
      {
        String subtypeName =
          getAttribAsString(contentTypeElement, XMLAttribute.SUBTYPE, true);
        String typeName = majorTypeName + "/" + subtypeName;
        int type = MediaType.codeFor(typeName);
        if ( type == MediaType.UNDEFINED )
          throw new MasterException
            (contentTypeElement, "Unknown media type name: " + typeName);
        return type;
      }
    else
      return getAttribAsId(contentTypeElement, XMLAttribute.ID, true);
  }

  /**
   * Returns the source type of the (pseudo-)document represented by the specified master root
   * element. Throws an excpetion if the source type can not be detected.
   */

  public static int getSourceType (Element masterRootElement)
    throws MasterException
  {
    Element sourceTypeElement =
      getChildElement(masterRootElement, XMLElement.SOURCE_TYPE, true);
    String majorTypeName =
      getAttribAsString(sourceTypeElement, XMLAttribute.TYPE, false);
    if ( majorTypeName != null )
      {
        String subtypeName =
          getAttribAsString(sourceTypeElement, XMLAttribute.SUBTYPE, true);
        String typeName = majorTypeName + "/" + subtypeName;
        int type = MediaType.codeFor(typeName);
        if ( type == MediaType.UNDEFINED )
          throw new MasterException
            (sourceTypeElement, "Unknown media type name: " + typeName);
        return type;
      }
    else
      return getAttribAsId(sourceTypeElement, XMLAttribute.ID, true);
  }

  /**
   * Returns the creation time of the document represented by the specified master
   * root element, or -1 if not set.
   */

  public static long getCreated (Element masterRootElement)
    throws MasterException
  {
    return getAttribAsTime
      (masterRootElement, XMLElement.CREATED,
       XMLAttribute.VALUE, XMLAttribute.FORMAT, XMLAttribute.RAW,
       false);
  }

  /**
   * Returns the last modification time of the document represented by the specified master
   * root element, or -1 if not set.
   */

  public static long getLastModified (Element masterRootElement)
    throws MasterException
  {
    return getAttribAsTime
      (masterRootElement, XMLElement.LAST_MODIFIED,
       XMLAttribute.VALUE, XMLAttribute.FORMAT, XMLAttribute.RAW,
       false);
  }

  /**
   * Returns the timeframe start of the document represented by the specified master
   * root element. Throws an exception if the timeframe start can not be determined.
   */

  public static long getTimeframeStart (Element masterRootElement)
    throws MasterException
  {
    return getAttribAsTime
      (masterRootElement, XMLElement.TIMEFRAME_START,
       XMLAttribute.VALUE, XMLAttribute.FORMAT, XMLAttribute.RAW,
       true);
  }

  /**
   * Returns the timeframe end of the document represented by the specified master
   * root element. Throws an exception if the timeframe end can not be determined.
   */

  public static long getTimeframeEnd (Element masterRootElement)
    throws MasterException
  {
    return getAttribAsTime
      (masterRootElement, XMLElement.TIMEFRAME_END,
       XMLAttribute.VALUE, XMLAttribute.FORMAT, XMLAttribute.RAW,
       true);
  }

  /**
   * Returns the login name of the user represented by the specified master root
   * element. Throws an exception if the login name can not be determined.
   */

  public static String getLoginName (Element masterRootElement)
    throws MasterException
  {
    return getTextNodesAsString(masterRootElement, XMLElement.LOGIN_NAME, true);
  }

  /**
   * Returns the password of the user represented by the specified master root
   * element. Throws an exception if the password can not be determined.
   */

  public static String getPassword (Element masterRootElement)
    throws MasterException
  {
    return getTextNodesAsString(masterRootElement, XMLElement.PASSWORD, true);
  }

  /**
   * Returns the first name of the user represented by the specified master root
   * element, or <code>null</code> if the first name is not set.
   */

  public static String getFirstName (Element masterRootElement)
    throws MasterException
  {
    return getTextNodesAsString(masterRootElement, XMLElement.FIRST_NAME, false);
  }

  /**
   * Returns the surname of the user represented by the specified master root
   * element, or <code>null</code> if the surname is not set.
   */

  public static String getSurname (Element masterRootElement)
    throws MasterException
  {
    return getTextNodesAsString(masterRootElement, XMLElement.SURNAME, false);
  }

  /**
   * Returns the email of the user represented by the specified master root
   * element, or <code>null</code> if the email is not set.
   */

  public static String getEmail (Element masterRootElement)
    throws MasterException
  {
    return getTextNodesAsString(masterRootElement, XMLElement.EMAIL, false);
  }

  /**
   * Returns the code of the language represented by the specified master root
   * element, or <code>null</code> if the code is not set.
   */

  public static String getCode (Element masterRootElement)
    throws MasterException
  {
    return getTextNodesAsString(masterRootElement, XMLElement.CODE, false);
  }

  /**
   * Returns the author-role of the user represented by the specified master root
   * element, or <code>null</code> if the role is not set.
   * DEPRECATED
   */

  //  public static int getAuthorRole (Element masterRootElement)
  //    throws MasterException
  //  {
  //    return UserRole.codeFor(getAttribAsString(masterRootElement, XMLAttribute.ROLE, false));
  //  }

  // --------------------------------------------------------------------------------
  // Getting complex data (multiple values)
  // --------------------------------------------------------------------------------

  /**
   * Returns the components of the document represented by the specified master root
   * element.
   */

  public static Element[] getComponents (Element masterRootElement)
    throws MasterException
  {
    return getChildElements(masterRootElement, XMLElement.COMPONENTS);
  }

  /**
   * Returns the links of the document represented by the specified master root
   * element.
   */

  public static Element[] getLinks (Element masterRootElement)
    throws MasterException
  {
    return getChildElements(masterRootElement, XMLElement.LINKS);
  }

  /**
   * Returns the attachables of the document represented by the specified master root
   * element.
   * <p>
   *   If no 'attachables' elements exist, returns an array of length 0.
   * </p>
   *
   * @throws MasterException if something goes wrong
   */

  public static Element[] getAttachables (Element masterRootElement)
    throws MasterException
  {
    return getChildElements(masterRootElement, XMLElement.ATTACHABLE);
  }

  /**
   * Returns the members of the user group represented by the specified master root
   * element.
   */

  public static Element[] getMembers (Element masterRootElement)
    throws MasterException
  {
    return getChildElements(masterRootElement, XMLElement.MEMBERS);
  }

  /**
   * Returns the authors of the document represented by the specified master root
   * element.
   */

  public static Element[] getAuthors (Element masterRootElement)
    throws MasterException
  {
    return getChildElements(masterRootElement, XMLElement.AUTHORS);
  }

  /**
   * Returns the themes of the document represented by the specified master root
   * element.
   * @deprecated The themes element is deprecated in favour of the gdim_entries element.
   */

  public static Element[] getThemes (Element masterRootElement)
    throws MasterException
  {
    return getChildElements(masterRootElement, XMLElement.THEMES);
  }

  /**
   * Returns the GDIM entries of the document represented by the specified master root
   * element.
   */

  public static Element[] getGDIMEntries (Element masterRootElement)
    throws MasterException
  {
    return getChildElements(masterRootElement, XMLElement.GDIM_ENTRIES);
  }

  /**
   * Returns the read permissions of the document represented by the specified master root
   * element.
   */

  public static Element[] getReadPermissions (Element masterRootElement)
    throws MasterException
  {
    Element[] readPermissions = getChildElements(masterRootElement, XMLElement.READ_PERMISSIONS);
    return readPermissions;
  }

  /**
   * Returns the write permissions of the document represented by the specified master root
   * element.
   */

  public static Element[] getWritePermissions (Element masterRootElement)
    throws MasterException
  {
    return getChildElements(masterRootElement, XMLElement.WRITE_PERMISSIONS);
  }

  /**
   * Returns the user groups of the user represented by the specified master root
   * element.
   */

  public static Element[] getUserGroups (Element masterRootElement)
    throws MasterException
  {
    return getChildElements(masterRootElement, XMLElement.USER_GROUPS);
  }

  /**
   * Returns the reference attributes of the document represented by the specified master
   * root element. The values of the attributes are converted to objects with the
   * appropriate runtime types automatically.
   */

  public static Map getRefAttribs (Element masterRootElement)
    throws MasterException
  {
    NodeList childNodes = masterRootElement.getChildNodes();
    Map attribs = new HashMap ();
    for (int i = 0; i < childNodes.getLength(); i++)
      {
        Node childNode = childNodes.item(i);
        if ( childNode.getNodeType() == Node.ELEMENT_NODE &&
             childNode.getLocalName().equals(XMLElement.REF_ATTRIBUTE) )
          {
            Element childElement = (Element)childNode;
            String name = childElement.getAttribute(XMLAttribute.NAME);
            String valueAsString = childElement.getAttribute(XMLAttribute.VALUE);
            String javaType = RefAttrib.javaTypeOf[RefAttrib.codeFor(name)];
            attribs.put(name, stringToType(valueAsString, javaType));
          }
      }
    return attribs;
  }

  /**
   * Returns the document types the user group represented by the specified master can
   * create new documents for.
   */

  public static int[] getCreatePermissions (Element masterRootElement)
    throws MasterException
  {
    Element[] elements =
      getChildElements(masterRootElement, XMLElement.CREATE_PERMISSIONS);
    int[] docTypes = new int[elements.length];
    for (int i = 0; i < elements.length; i++)
      {
        String elementName = elements[i].getLocalName();
        if ( !elementName.equals(XMLElement.DOCUMENT_TYPE) )
          throw new MasterException
            ("Illegal create permissions element: " + elementName);
        String docTypeName = getAttribAsString(elements[i], XMLAttribute.NAME, true);
        int docType = DocType.codeFor(docTypeName);
        if ( docType == DocType.UNDEFINED )
          throw new MasterException
            ("Unknown document type in create permissions: " + docTypeName);
        docTypes[i] = docType;
      }
    return docTypes;
  }

  // --------------------------------------------------------------------------------
  // Getting GDIM data
  // --------------------------------------------------------------------------------

  /**
   * Only with GDIM entries; returns the id of the generic document, or
   * {@link Id#UNDEFINED UNDEFINED} if no such id is set.
   */

  public static int getGenericDocId (Element gdimEntryElement)
    throws MasterException
  {
    return getAttribAsId(gdimEntryElement, XMLAttribute.GENERIC_DOC_ID, false);
  }

  /**
   * Only with GDIM entries; returns the path of the generic document, or <code>null</code>
   * if no such path is set.
   */

  public static String getGenericDocPath (Element gdimEntryElement)
    throws MasterException
  {
    String genericDocPath =
      getAttribAsString(gdimEntryElement, XMLAttribute.GENERIC_DOC_PATH, false);

    // Support for legacy "generic_doc_url" attribute:
    if ( genericDocPath == null )
      genericDocPath =
        getAttribAsString(gdimEntryElement, XMLAttribute.GENERIC_DOC_URL, false);

    return genericDocPath;
  }

  /**
   * Only with GDIM entries; returns the theme path, or <code>null</code> if no theme path
   * is set.
   */

  public static String getThemePath (Element gdimEntryElement)
    throws MasterException
  {
    return getAttribAsString(gdimEntryElement, XMLAttribute.THEME_PATH, false);
  }

  /**
   * Only with GDIM entries; returns the language path.
   */

  public static String getLang (Element gdimEntryElement)
    throws MasterException
  {
    return getAttribAsString(gdimEntryElement, XMLAttribute.LANG, false);
  }

  /**
   * Only with GDIM entries; returns the language code.
   */

  public static String getLangCode (Element gdimEntryElement)
    throws MasterException
  {
    return getAttribAsString(gdimEntryElement, XMLAttribute.LANG_CODE, false);
  }

  /**
   * returns the theme master.
   */

  public static Master getTheme (Element masterRootElement)
    throws MasterException
  {
    Element themeElement = getChildElement(masterRootElement, XMLElement.THEME, false);
    if ( themeElement != null )
      return new DOMMaster(themeElement);
    else
      return null;
  }

  // --------------------------------------------------------------------------------
  // Getting all referenced (pseudo-)documents
  // --------------------------------------------------------------------------------

  /**
   * Returns all path-like attribute values that reference another document or
   * pseudo-document.
   */

  public static String[] getAllRefPaths (Element masterRootElement)
  {
    final String[] ATTRIBS = new String[]
      {XMLAttribute.PATH, XMLAttribute.GENERIC_DOC_PATH, XMLAttribute.THEME_PATH};
    List<String> paths = new ArrayList<String>();
    NodeList elements = masterRootElement.getElementsByTagName("*");
    for (int i = 0; i < elements.getLength(); i++)
      {
        Element element = (Element)elements.item(i);
        for (int k = 0; k < ATTRIBS.length; k++)
          {
            String path = element.getAttribute(ATTRIBS[k]);
            if ( !path.equals("") && !paths.contains(path) )
              paths.add(path);
          }
      }
    return paths.toArray(new String[paths.size()]);
  }

  // --------------------------------------------------------------------------------
  // Base methods to extract metainfos
  // --------------------------------------------------------------------------------

  /**
   * Returns the element that is (a) a child of the specified element, and (b) has the
   * specified name. If several childs with the specified name exist, the first is
   * returned. The <code>required</code> flag controls what happens if no child with the
   * specified name exists: if <code>required</code> is <code>true</code>, an exception is
   * thrown, if <code>required</code> is <code>false</code>, <code>null</code> is returned.
   *
   * @throws MasterException if something goes wrong
   */

  protected static Element getChildElement (Element element,
                                            String childElementName,
                                            boolean required)
    throws MasterException
  {
    NodeList nodeList = element.getChildNodes();
    Element childElement = null;
    for (int i = 0; i < nodeList.getLength() && childElement == null; i++)
      {
        Node node = nodeList.item(i);
        if ( node.getNodeType() == Node.ELEMENT_NODE &&
             node.getLocalName().equals(childElementName) )
          childElement = (Element)node;
      }
    if ( childElement == null && required )
      throw new MasterException
        (element, "Missing child element: " + childElementName);
    return childElement;
  }

  /**
   * Returns an attribute of the specified element as a string. The <code>required</code>
   * flag controls what happens if the attribute does not exist or is empty: if
   * <code>required</code> is <code>true</code>, an exception is thrown, if
   * <code>required</code> is <code>false</code>, <code>null</code> is returned.
   *
   * @param element the element
   * @param attribName the name of the attribute
   * @param required whether the attribute must exist and be non-empty.
   *
   * @throws MasterException if something goes wrong
   */

  protected static String getAttribAsString (Element element,
                                             String attribName,
                                             boolean required)
    throws MasterException
  {
    String value = element.getAttribute(attribName);
    if ( value.equals("") )
      {
        if ( required )
          throw new MasterException
            (element, "Missing or void attribute: " + attribName);
        else
          value = null;
      }
    return value;
  }

  /**
   * Returns an attribute of a child element of the specified element. The
   * <code>required</code> flag controls what happens if the attribute does not exist or is
   * empty: if <code>required</code> is <code>true</code>, an exception is thrown, if
   * <code>required</code> is <code>false</code>, <code>null</code> is returned. The case
   * that the child element does not exist is treated the same way as the case that the
   * attribute does not exist.
   *
   * @param element the element
   * @param childElementName the name of the child element
   * @param attribName the name of the attribute
   * @param required whether the attribute must exist and be non-empty.
   *
   * @throws MasterException if something goes wrong
   */

  protected static String getAttribAsString (Element element,
                                             String childElementName,
                                             String attribName,
                                             boolean required)
    throws MasterException
  {
    Element childElement =
      getChildElement(element, childElementName, required);
    return
      ( childElement != null
        ? getAttribAsString(childElement, attribName, true)
        : null);
  }

  /**
   * <p>
   *   Returns the text content of the specified element as a string. Requires that the
   *   element has text content only. If subelements are detected, an exception is
   *   thrown. However, it is allowed that the content contains comments and processing
   *   instructions [in fact, the current implementation allows all node types except
   *   elements as childs].
   * </p>
   * <p>
   *   The <code>required</code> flag controls what happens if the text content does not
   *   exist or is empty: if <code>required</code> is <code>true</code>, an exception is
   *   thrown, if <code>required</code> is <code>false</code>, <code>null</code> is
   *   returned.
   * </p>
   *
   * @param element the element
   * @param required whether the text content must exist and be non-empty.
   *
   * @throws MasterException if something goes wrong
   */

  protected static String getTextNodesAsString (Element element,
                                                boolean required)
    throws MasterException
  {
    NodeList nodeList = element.getChildNodes();
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < nodeList.getLength(); i++)
      {
        Node node = nodeList.item(i);
        switch ( node.getNodeType() )
          {
          case Node.TEXT_NODE:
            buffer.append(node.getNodeValue());
            break;
          case Node.ELEMENT_NODE:
            throw new MasterException
              (element,
               "No subelements allowd, but found element: " + node.getLocalName());
          }
      }
    String value = buffer.toString();
    if ( required && value.equals("") )
      throw new MasterException
        (element, "Missing text content");
    return value;
  }

  /**
   * <p>
   *   Returns the text content of a child element of the specified element as a
   *   string. Requires that the element has text content only. If subelements are detected,
   *   an exception is thrown. However, it is allowed that the content contains comments and
   *   processing instructions [in fact, the current implementation allows all node types
   *   except elements as childs].
   * </p>
   * <p>
   *   The <code>required</code> flag controls what happens if the text content does not
   *   exist, is empty, or the child element does not exist: if <code>required</code> is
   *   <code>true</code>, an exception is thrown, if <code>required</code> is
   *   <code>false</code>, <code>null</code> is returned.
   * </p>
   * <p>
   *   This method is used to get the metainfos that reside in text childs of subelements of
   *   the root element, for example:
   * </p>
   * <pre>
   *   &lt;mumie:name&gt;Start Page&lt;/mumie:name&gt;
   *
   *   &lt;mumie:description&gt;
   *     Page to display immediately after login.
   *   &lt;/mumie:description&gt;
   *
   *   &lt;mumie:copyright&gt;
   *     Copyright (C) 2006, Berlin University of Technology
   *   &lt;/mumie:copyright&gt;
   * </pre>
   * <p>
   *   The public <code>getXxx</code> methods that return such data (cf. "See also" below)
   *   are implemented by means of this method.
   * </p>
   *
   * @param element the element
   * @param childElementName the name of the child element
   * @param required whether the text content must exist and be non-empty.
   *
   * @throws MasterException if something goes wrong
   *
   * @see #getName getName
   * @see #getDescription getDescription
   * @see #getCopyright getCopyright
   * @see #getQualifiedName getQualifiedName
   */

  protected static String getTextNodesAsString (Element element,
                                                String childElementName,
                                                boolean required)
    throws MasterException
  {
    Element childElement =
      getChildElement(element, childElementName, required);
    return
      (childElement != null
       ? getTextNodesAsString(childElement, required)
       : null);
  }

  /**
   * Returns an attribute of the specified element as an {@link Integer Integer} object. The
   * <code>required</code> flag controls what happens if the attribute does not exist or is
   * empty: if <code>required</code> is <code>true</code>, an exception is thrown, if
   * <code>required</code> is <code>false</code>, <code>null</code> is returned.
   *
   * @param element the element
   * @param attribName the name of the attribute
   * @param required whether the attribute must exist and be non-empty.
   *
   * @throws MasterException if something goes wrong
   */

  protected static Integer getAttribAsIntObject (Element element,
                                                 String attribName,
                                                 boolean required)
    throws MasterException
  {
    String value = getAttribAsString(element, attribName, required);
    if ( value == null )
      return null;
    try
      {
        return new Integer(value);
      }
    catch (Exception exception)
      {
        throw new MasterException
          (element, "Failed to convert attribute to an integer: " + attribName);
      }
  }

  /**
   * Returns an attribute of a child element of the specified element as an
   * {@link Integer Integer} object. The <code>required</code> flag controls what happens if
   * the attribute does not exist, is empty, or the child element does not exist: if
   * <code>required</code> is <code>true</code>, an exception is thrown, if
   * <code>required</code> is <code>false</code>, <code>null</code> is returned.
   *
   * @param element the element
   * @param childElementName the name of the child element
   * @param attribName the name of the attribute
   * @param required whether the attribute must exist and be non-empty.
   *
   * @throws MasterException if something goes wrong
   */

  protected static Integer getAttribAsIntObject (Element element,
                                                 String childElementName,
                                                 String attribName,
                                                 boolean required)
    throws MasterException
  {
    Element childElement =
      getChildElement(element, childElementName, required);
    return
      (childElement != null
       ? getAttribAsIntObject(childElement, attribName, true)
       : null);
  }

  /**
   * Returns an attribute of the specified element as an <code>int</code>. If the attribute
   * does not exist or is empty, an exception is thrown.
   *
   * @param element the element
   * @param attribName the name of the attribute
   *
   * @throws MasterException if something goes wrong
   */

  protected static int getAttribAsInt (Element element,
                                       String attribName)
    throws MasterException
  {
    return getAttribAsIntObject(element, attribName, true).intValue();
  }

  /**
   * Returns an attribute of a child element of the specified element as an
   * <code>int</code>. If the attribute does not exist, is empty, or the child element does
   * not exist, an exception is thrown.
   *
   * @param element the element
   * @param attribName the name of the attribute
   *
   * @throws MasterException if something goes wrong
   */

  protected static int getAttribAsInt (Element element,
                                       String childElementName,
                                       String attribName)
    throws MasterException
  {
    return getAttribAsIntObject(element, childElementName, attribName, true).intValue();
  }

  /**
   * Returns an attribute of the specified element as a non-negative <code>int</code>
   * value. If the attribute value represents a negative integer, an exception is thrown. If
   * the attribute does not exist or is empty, an exception is thrown.
   *
   * @param element the element
   * @param attribName the name of the attribute
   *
   * @throws MasterException if something goes wrong
   */

  protected static int getAttribAsNonNegativeInt (Element element,
                                                  String attribName,
                                                  boolean required)
    throws MasterException
  {
    Integer valueObject = getAttribAsIntObject(element, attribName, required);
    if ( valueObject == null )
      return -1;
    int value = valueObject.intValue();
    if ( value < 0 )
      throw new MasterException
        (element, "Attribute " + attribName + ": Negative value: " + value);
    return value;
  }

  /**
   * Returns an attribute of the specified element as a non-negative <code>int</code>
   * value. If the attribute value represents a negative integer, an exception is thrown.
   * The <code>required</code> flag controls what happens if the attribute does not exist or
   * is empty: if <code>required</code> is <code>true</code>, an exception is thrown, if
   * <code>required</code> is <code>false</code>, -1 is returned.
   *
   * @param element the element
   * @param attribName the name of the attribute
   *
   * @throws MasterException if something goes wrong
   *
   */

  protected static int getAttribAsNonNegativeInt (Element element,
                                                  String childElementName,
                                                  String attribName,
                                                  boolean required)
    throws MasterException
  {
    Element childElement = getChildElement(element,childElementName, required);
    return
      (childElement != null
       ? getAttribAsNonNegativeInt(childElement, attribName, required)
       : -1);
  }

  /**
   * Returns an attribute of the specified element as an {@link Long Long} object. The
   * <code>required</code> flag controls what happens if the attribute does not exist or is
   * empty: if <code>required</code> is <code>true</code>, an exception is thrown, if
   * <code>required</code> is <code>false</code>, <code>null</code> is returned.
   *
   * @param element the element
   * @param attribName the name of the attribute
   * @param required whether the attribute must exist and be non-empty.
   *
   * @throws MasterException if something goes wrong
   */

  protected static Long getAttribAsLongObject (Element element,
                                               String attribName,
                                               boolean required)
    throws MasterException
  {
    String value = getAttribAsString(element, attribName, required);
    if ( value == null )
      return null;
    try
      {
        return new Long(value);
      }
    catch (Exception exception)
      {
        throw new MasterException
          (element, "Failed to convert attribute to an long: " + attribName);
      }
  }

  /**
   * Returns an attribute of the specified element as an id. The <code>required</code> flag
   * controls what happens if the attribute does not exist or is empty: if
   * <code>required</code> is <code>true</code>, an exception is thrown, if
   * <code>required</code> is <code>false</code>, {@link Id#UNDEFINED UNDEFINED} is
   * returned.
   *
   * @param element the element
   * @param attribName the name of the attribute
   * @param required whether the attribute must exist and be non-empty.
   *
   * @throws MasterException if something goes wrong
   */

  protected static int getAttribAsId (Element element,
                                      String attribName,
                                      boolean required)
    throws MasterException
  {
    Integer value = getAttribAsIntObject(element, attribName, required);
    if ( value == null )
      return Id.UNDEFINED;
    int id = value.intValue();
    if ( id < 0 )
      throw new MasterException
        (element,
         "Attribute: " + attribName + ": Invalid id value: " + id);
    return id;
  }

  /**
   * Returns an attribute of ta child element of the specified element as an id. The
   * <code>required</code> flag controls what happens if the attribute does not exist, is
   * empty, or the child element does not exist: if <code>required</code> is
   * <code>true</code>, an exception is thrown, if <code>required</code> is
   * <code>false</code>, {@link Id#UNDEFINED UNDEFINED} is returned.
   *
   * @param element the element
   * @param attribName the name of the attribute
   * @param required whether the attribute must exist and be non-empty [this includes the
   * existence of the child element]
   *
   * @throws MasterException if something goes wrong
   */

  protected static int getAttribAsId (Element element,
                                      String childElementName,
                                      String attribName,
                                      boolean required)
    throws MasterException
  {
    Element childElement = getChildElement(element, childElementName, required);
    return
      (childElement != null
       ? getAttribAsId(childElement, attribName, required)
       : Id.UNDEFINED);
  }

  /**
   * Returns an attribute of the specified element as a {@link Boolean Boolean} object. The
   * <code>required</code> flag controls what happens if the attribute does not exist or is
   * empty: if <code>required</code> is <code>true</code>, an exception is thrown, if
   * <code>required</code> is <code>false</code>, <code>null</code> is returned.
   *
   * @param element the element
   * @param attribName the name of the attribute
   * @param required whether the attribute must exist and be non-empty.
   *
   * @throws MasterException if something goes wrong
   */

  protected static Boolean getAttribAsBooleanObject (Element element,
						     String attribName,
						     boolean required)
    throws MasterException
  {
    String value = getAttribAsString(element, attribName, required);
    if ( value == null )
      return null;
    try
      {
        return stringToBoolean(value);
      }
    catch (Exception exception)
      {
        throw new MasterException
          (element, "Failed to convert attribute to boolean: " + attribName, exception);
      }
  }

  /**
   * Returns an attribute of a child element of the specified element as a
   * {@link Boolean Boolean} object. The <code>required</code> flag controls what happens if
   * the attribute does not exist, is empty, or the child element does not exist: if
   * <code>required</code> is <code>true</code>, an exception is thrown, if
   * <code>required</code> is <code>false</code>, <code>null</code> is returned.
   *
   * @param element the element
   * @param childElementName the name of the child element
   * @param attribName the name of the attribute
   * @param required whether the attribute must exist and be non-empty.
   *
   * @throws MasterException if something goes wrong
   */

  protected static Boolean getAttribAsBooleanObject (Element element,
						     String childElementName,
						     String attribName,
						     boolean required)
    throws MasterException
  {
    Element childElement =
      getChildElement(element, childElementName, required);
    return
      (childElement != null
       ? getAttribAsBooleanObject(childElement, attribName, true)
       : null);
  }

  /**
   * <p>
   *  Returns an attribute of the specified element as a time.
   * </p>
   * <p>
   *  The time value is controlled by three attributes, usually named <code>value</code>,
   *  <code>format</code>, and <code>raw</code>. They contain the formatted time, the
   *  format, and the unformatted time, respectively. The <em>formatted time</em> is a human
   *  readable string like <code>2006-01-30 12:42:34 456</code>. The <em>format</em> is the
   *  corresponding pattern as described with {@link SimpleDateFormat SimpleDateFormat}, for
   *  example <code>YYYY-MM-dd HH:mm:ss SSS</code>. The <em>unformatted time</em> is the
   *  number of milliseconds since 00:00, Jan 1, 1970 UTC. If the <code>raw</code> attribute
   *  exists and is non empty, its value is returned. Otherwise, the time is retrieved from
   *  the two other attributes, where the <code>format</code> attribute is used to parse the
   *  <code>value</code> attribute. The time is returned unformatted, i.e., number of
   *  milliseconds since 00:00, Jan 1, 1970 UTC.
   * </p>
   * <p>
   *   The <code>required</code> flag controls what happens if neither the <code>raw</code>
   *   nor the <code>value</code> attribute exists and is non-empty: If
   *   <code>required</code> is <code>true</code>, an exception is thrown; if
   *   <code>required</code> is <code>false</code>, -1 is returned. [Please note: If
   *   <code>required</code> is <code>true</code> and the <code>raw</code> attribute does
   *   not exist, the <code>format</code> attribute must exist in addition to the
   *   <code>value</code> attribute for the latter to be able to be parsed.]
   * </p>
   *
   * @param element the element
   * @param valueAttribName name of the attribute containing the formatted time
   * @param formatAttribName name of the attribute containing the time format
   * @param rawAttribName name of the attribute containing the unformatted time
   * @param whether either the the <code>value</code> or the <code>raw</code> attribute must
   * exist.
   *
   * @throws MasterException is something goes wrong.
   */

  protected static long getAttribAsTime (Element element,
                                         String valueAttribName,
                                         String formatAttribName,
                                         String rawAttribName,
                                         boolean required)
    throws MasterException
  {
    Long raw = getAttribAsLongObject(element, rawAttribName, false);
    if ( raw != null )
      return raw.longValue();
    String value = getAttribAsString(element, valueAttribName, false);
    if ( value == null )
      {
        if ( required )
          throw new MasterException
            (element, "None of the following attributes found: " +
             rawAttribName + ", " + valueAttribName);
        else
          return -1;
      }
    String formatPattern = getAttribAsString(element, formatAttribName, true);
    try
      {
        DateFormat format = new SimpleDateFormat(formatPattern);
        format.setLenient(false);
        return format.parse(value).getTime();
      }
    catch (Exception exception)
      {
        throw new MasterException(element, "Error while parsing time", exception);
      }
  }

  /**
   * <p>
   *  Returns an attribute of a child element of the specified element as a time.
   * </p>
   * <p>
   *  The time value is controlled by three attributes, usually named <code>value</code>,
   *  <code>format</code>, and <code>raw</code>. They contain the formatted time, the
   *  format, and the unformatted time, respectively. The <em>formatted time</em> is a human
   *  readable string like <code>2006-01-30 12:42:34 456</code>. The <em>format</em> is the
   *  corresponding pattern as described with {@link SimpleDateFormat SimpleDateFormat}, for
   *  example <code>YYYY-MM-dd HH:mm:ss SSS</code>. The <em>unformatted time</em> is the
   *  number of milliseconds since 00:00, Jan 1, 1970 UTC. If the <code>raw</code> attribute
   *  exists and is non empty, its value is returned. Otherwise, the time is retrieved from
   *  the two other attributes, where the <code>format</code> attribute is used to parse the
   *  <code>value</code> attribute. The time is returned unformatted, i.e., number of
   *  milliseconds since 00:00, Jan 1, 1970 UTC.
   * </p>
   * <p>
   *   The <code>required</code> flag controls what happens if neither the <code>raw</code>
   *   nor the <code>value</code> attribute exist and are non-empty, or if the child element
   *   does not exist: If <code>required</code> is <code>true</code>, an exception is
   *   thrown; if <code>required</code> is <code>false</code>, -1 is returned. [Please note:
   *   If <code>required</code> is <code>true</code> and the <code>raw</code> attribute does
   *   not exist, the <code>format</code> attribute must exist in addition to the
   *   <code>value</code> attribute for the latter to be able to be parsed.]
   * </p>
   *
   * @param element the element
   * @param childElementName name of the child element.
   * @param valueAttribName name of the attribute containing the formatted time
   * @param formatAttribName name of the attribute containing the time format
   * @param rawAttribName name of the attribute containing the unformatted time
   * @param whether either the the <code>value</code> or the <code>raw</code> attribute must
   * exist.
   *
   * @throws MasterException is something goes wrong.
   */

  protected static long getAttribAsTime (Element element,
                                         String childElementName,
                                         String valueAttribName,
                                         String formatAttribName,
                                         String rawAttribName,
                                         boolean required)
    throws MasterException
  {
    Element childElement = getChildElement(element, childElementName, required);
    return
      (childElement != null
       ? getAttribAsTime
           (childElement, valueAttribName, formatAttribName, rawAttribName, required)
       : -1);
  }

  /**
   * <p>
   *   Returns the child elements of the specified element as an array of
   *   {@link Element Element}s.
   * </p>
   * <p>
   *   If no child elements exist, returns an array of length 0.
   * </p>
   *
   * @throws MasterException if something goes wrong
   */

  protected static Element[] getChildElements (Element element)
    throws MasterException
  {
    NodeList childNodes = element.getChildNodes();
    List childElementsList = new ArrayList();
    for (int i = 0; i < childNodes.getLength(); i++)
      {
        Node childNode = childNodes.item(i);
        if ( childNode.getNodeType() == Node.ELEMENT_NODE )
          childElementsList.add((Element)childNode);
      }
    int size = childElementsList.size();
    Element[] childElements = (Element[])childElementsList.toArray(new Element[size]);
    return childElements;
  }

  /**
   * <p>
   *   Returns the child elements of a child element of the specified element as an array of
   *   {@link Element Element}s.
   * </p>
   * <p>
   *   If the child element of the specified element does not exist, or if it has no child
   *   elements, the method returns an array of length 0.
   * </p>
   */

  protected static Element[] getChildElements (Element element,
                                               String childElementName)
    throws MasterException
  {
    Element childElement = getChildElement(element, childElementName, false);
    return
      (childElement != null
       ? getChildElements(childElement)
       : new Element[0]);
  }

  // --------------------------------------------------------------------------------
  // Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * Returns the first element in the spacified node list that has a local name equal to the
   * spacified name. If no such element exists, returns <code>null</code>.
   */

  protected static Element selectElement (NodeList nodeList, String name)
  {
    Element element = null;
    for (int i = 0; i < nodeList.getLength() && element == null; i++)
      {
        Node node = nodeList.item(i);
        if ( node.getNodeType() == Node.ELEMENT_NODE &&
             node.getLocalName().equals(name) )
          element = (Element)node;
      }
    return element;
  }

  /**
   * Converts the specified string to an object with the specified runtime type.
   */

  protected static Object stringToType (String string, String type)
    throws MasterException
  {
    try
      {
        if ( type.equals("java.lang.Integer") )
          return new Integer(string);
        else if ( type.equals("java.lang.Long") )
          return new Long(string);
        else if ( type.equals("java.lang.Float") )
          return new Float(string);
        else if ( type.equals("java.lang.Double") )
          return new Double(string);
        else if ( type.equals("java.lang.Boolean") )
          {
            if ( string.equals("true") || string.equals("yes") )
              return Boolean.TRUE;
            else if ( string.equals("false") || string.equals("no") )
              return Boolean.FALSE;
            else
              throw new IllegalArgumentException("Unknown boolean value: " + string);
          }
        else if ( type.equals("java.lang.String") )
          return string;
        else if ( type.equals("java.sql.Timestamp") )
          return new Timestamp(Long.parseLong(string));
        else if ( type.equals("java.lang.Real") )
          return Long.parseLong(string);
        else
          throw new IllegalArgumentException
            ("No rule to convert a string to type: " + type);
      }
    catch (Exception exception)
      {
        throw new MasterException
          ("Unable to convert \"" + string + "\" to " + type, exception);
      }
  }

  /**
   * Transforms a path in its platform-specific form. Replaces all occurrences of
   * <code>'/'</code> and <code>'\'</code> in the specified path by the platform-dependent
   * separator char, i.e., {@link File#separatorChar File.separatorChar}.
   */

  protected static String nativePath (String path)
  {
    char[] chars = path.toCharArray();
    for (int i = 0; i < chars.length; i++)
      if ( chars[i] == '/' || chars[i] == '\\' )
        chars[i] = File.separatorChar;
    return new String(chars);
  }

  /**
   * Converts the specified string to a {@link Boolean Boolean} object.
   */

  protected static Boolean stringToBoolean (String string)
  {
    if ( string.equals("true") || string.equals("yes") )
      return Boolean.TRUE;
    else if ( string.equals("false") || string.equals("no") )
      return Boolean.FALSE;
    else
      throw new IllegalArgumentException
	("Illegal boolean specifier: \"" + string + "\" " +
	 " (expected true|yes|false|no)");
  }

}
