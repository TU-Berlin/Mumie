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

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import net.mumie.cocoon.notions.Category;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.MediaType;
import net.mumie.cocoon.notions.Nature;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.util.IntList;

/**
 * This class is the base implementation of an editable master document. It can
 * be used to store and retrieve all available meta information, although the
 * mapping of specific meta information to single document types must be done in
 * sub classes.
 * 
 * @author Markus Gronau <a
 *         href="mailto:gronau@math.tu-berlin.de">gronau@math.tu-berlin.de</a>
 * @version <code>$Id$</code>
 */
public class SimpleEditableMaster implements EditableMaster
{
  // --------------------------------------------------------------------------------
  // h1: Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The path, or null if not set.
   */

  protected String path = null;

  /**
   * Old path, or null if not set.
   */

  protected String oldPath = null;

  /**
   * The nature (document or pseudo-document), or {@link Nature#UNDEFINED} if not set. 
   */

  protected int nature = Nature.UNDEFINED;

  /**
   * The type, or -1 if not set.
   */

  protected int type = -1;

  /**
   * The id, or {@link Id#UNDEFINED UNDEFINED} if not set.
   */

  protected int id = Id.UNDEFINED;

  /**
   * The category, or {@link Category#UNDEFINED} if not set.
   */

  protected int category = Category.UNDEFINED;

  /**
   * The local id, or <code>null</code> if nit set.
   */

  protected String lid = null;

  /**
   * The id of the generic document which is implemented by the document represented by this
   * master, or {@link Id#UNDEFINED UNDEFINED} if not set (only with theme references).
   */

  protected int genericDocId = Id.UNDEFINED;

  /**
   * The path of the generic document which is implemented by the document represented by
   * this master, or <code>null</code> if not set (only with theme references).
   */

  protected String genericDocPath = null;

  /**
   * The name, or null if not set.
   */

  protected String name = null;

  /**
   * The description, or null if not set.
   */

  protected String description = null;

  /**
   * Copyright note, or null if not set.
   */

  protected String copyright = null;

  /**
   * Changelog information, or null if not set.
   */

  protected String changelog = null;

  /**
   * Qualified name, or null if not set.
   */

  protected String qualifiedName = null;

  /**
   * The version, or -1 if not set.
   */

  protected int version = -1;

  /**
   * Id of the vc thread, or {@link Id#UNDEFINED UNDEFINED} if not set.
   */

  protected int vcThread = Id.UNDEFINED;

  /**
   * The width, or -1 if noet set.
   */

  protected int width = -1;

  /**
   * The height, or -1 if not set.
   */

  protected int height = -1;

  /**
   * The duration, or -1 if not set.
   */

  protected int duration = -1;

  /**
   * Corrector path, or null if not set.
   */

  protected String correctorPath = null;

  /**
   * Info page path, or null if not set.
   */

  protected String infoPagePath = null;

  /**
   * Thumbnail path, or null if not set.
   */

  protected String thumbnailPath = null;

  /**
   * Summary path, or null if not set.
   */

  protected String summaryPath = null;

  /**
   * Semester path, or null if not set.
   */

  protected String semesterPath = null;

  /**
   * Tutor path, or null if not set.
   */

  protected String tutorPath = null;

  /**
   * Path of the teaching class, or null if not set.
   */

  protected String elClassPath = null;

  /**
   * The <em>hide</em> flag, or null if not set.
   */

  protected Boolean hide = null;

  /**
   * The <em>is_wrapper</em> flag, or null if not set.
   */

  protected Boolean isWrapper = null;

  /**
   * The theme master, or null if not set.
   */

  protected Master theme = null;

  /**
   * The content type, or {@link MediaType#UNDEFINED} if not set.
   */

  protected int contentType = MediaType.UNDEFINED;

  /**
   * The source type, or {@link MediaType#UNDEFINED} if not set.
   */

  protected int sourceType = MediaType.UNDEFINED;

  /**
   * Timeframe start, or -1 if not set.
   */

  protected long timeframeStart = -1;

  /**
   * Timeframe end, or -1 if not set.
   */

  protected long timeframeEnd = -1;

  /**
   * Login name, or null if not set (only with users).
   */

  protected String loginName = null;

  /**
   * Password, or null if not set (only with users).
   */

  protected String password = null;

  /**
   * First name, or null if not set (only with users).
   */

  protected String firstName = null;

  /**
   * Surname, or null if not set (only with users).
   */

  protected String surname = null;

  /**
   * Email, or null if not set (only with users).
   */

  protected String email = null;

  /**
   * Code, or null if not set.
   */

  protected String code = null;

  /**
   * The components.
   */

  protected Vector<Master> components = null;

  /**
   * The links.
   */

  protected Vector<Master> links = null;

  /**
   * The attachables.
   */

  protected Vector<Master> attachables = null;

  /**
   * The members.
   */

  protected Vector<Master> members = null;

  /**
   * The authors.
   */

  protected Vector<Master> authors = null;

  /**
   * The GDIM entries.
   */

  protected Vector<GDIMEntry> gdimEntries = null;

  /**
   * The themes. DEPRECATED
   */

  protected Vector<Master> themes = null;

  /**
   * The read permissions.
   */

  protected Vector<Master> readPermissions = null;

  /**
   * The write permissions.
   */

  protected Vector<Master> writePermissions = null;

  /**
   * The user groups (only with users).
   */

  protected Vector<Master> userGroups = null;
 
  /**
   * The reference attributes, or null if not set.
   */

  protected Map refAttribs = null;

  /**
   * The create permissions, or null if not set.
   */

  protected IntList createPermissions = null;

  // --------------------------------------------------------------------------------
  // h1: Get / set / add methods
  // --------------------------------------------------------------------------------

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Path
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the path, or null if not set.
   */

  public String getPath ()
  {
    return this.path;
  }

  /**
   * Sets the path.
   */

  public void setPath (String path)
  {
    this.path = path;
  }

  /**
   * Returns the old path, or null if not set.
   */

  public String getOldPath ()
  {
    return this.oldPath;
  }

  /**
   * Sets the old path.
   */

  public void setOldPath (String path)
  {
    this.oldPath = oldPath;
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Nature
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the nature, or {@link Nature#UNDEFINED} if not set.
   */

  public int getNature()
  {
    return this.nature;
  }

  /**
   * Sets the nature.
   */

  public void setNature(int nature) {
    this.nature = nature;
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Type
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the type, or -1 if not set.
   */

  public int getType ()
  {
    return this.type;
  }

  /**
   * Sets the type.
   */

  public void setType (int type)
  {
    this.type = type;
  }

  /**
   * Returns the type name, or null if the type and/or nature are not set.
   */

  public String getTypeName ()
  {
    String typeName = null;
    if ( this.type != -1 )
      {
        if ( this.nature == Nature.DOCUMENT)
          typeName = DocType.nameFor(this.type);
        else if ( this.nature == Nature.PSEUDO_DOCUMENT )
          typeName = PseudoDocType.nameFor(this.type);
      }
    return typeName;
  }

  /**
   * Sets the type according to the specific name. The nature is set as well.
   */

  public void setTypeName (String typeName)
    throws MasterException
  {
    int type = -1;
    if ( (type = DocType.codeFor(typeName)) != DocType.UNDEFINED )
      {
        this.type = type;
        this.nature = Nature.DOCUMENT;
      }
    else if ( (type = PseudoDocType.codeFor(typeName)) != PseudoDocType.UNDEFINED )
      {
        this.type = type;
        this.nature = Nature.PSEUDO_DOCUMENT;
      }
    else
      throw new MasterException("Unknown (pseudo-)document type name :" + typeName);
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Category
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the category, or {@link Category#UNDEFINED} if not set.
   */

  public int getCategory()
  {
    return this.category;
  }

  /**
   * Sets the category.
   */

  public void setCategory (int category)
  {
    this.category = category;
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Id
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the id, or {@link Id#UNDEFINED} if not set.
   */

  public int getId()
  {
    return this.id;
  }

  /**
   * Sets the id.
   */

  public void setId (int id)
  {
    this.id = id;
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Version
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the version, or -1 if not set.
   */

  public int getVersion ()
  {
    return this.version;
  }

  /**
   * Sets the version.
   */

  public void setVersion (int version)
  {
    this.version = version;
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: VC Thread
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the vc thread, or {@link Id#UNDEFINED} if not set.
   */

  public int getVCThread()
  {
    return this.vcThread;
  }

  /**
   * Sets the vc thread.
   */

  public void setVCThread (int vcThread)
  {
    this.vcThread = vcThread;
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Generic doc id
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Only with theme references; returns the id of the generic document which is implemented
   * by the document represented by this master, or {@link Id#UNDEFINED UNDEFINED} if no
   * such id is set. 
   */

  public int getGenericDocId()
  {
    return this.genericDocId;
  }

  /**
   * Only with theme references; sets the id of the generic document which is implemented
   * by the document represented by this master. 
   */

  public void setGenericDocId (int genericDocId)
  {
    this.genericDocId = genericDocId;
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Generic doc path
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Only with theme references; returns the path of the generic document which is
   * implemented by the document represented by this master, or null if no such path is set.
   */

  public String getGenericDocPath()
  {
    return this.genericDocPath;
  }

  /**
   * Only with theme references; sets the path of the generic document which is implemented
   * by the document represented by this master. 
   */

  public void setGenericDocPath (String genericDocPath)
  {
    this.genericDocPath = genericDocPath;
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Lid
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the lid, ot null if not set.
   */

  public String getLid()
  {
    return this.lid;
  }

  /**
   * Sets the lid.
   */

  public void setLid (String lid)
  {
    this.lid = lid;
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Name
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the name, ot null if not set.
   */

  public String getName()
  {
    return this.name;
  }

  /**
   * Sets the name.
   */

  public void setName (String name)
  {
    this.name = name;
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Description
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the description, ot null if not set.
   */

  public String getDescription()
  {
    return this.description;
  }

  /**
   * Sets the description.
   */

  public void setDescription (String description)
  {
    this.description = description;
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Copyright
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the copyright note, ot null if not set.
   */

  public String getCopyright()
  {
    return this.copyright;
  }

  /**
   * Sets the copyright note.
   */

  public void setCopyright (String copyright)
  {
    this.copyright = copyright;
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Changelog
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the changelog note, ot null if not set.
   */

  public String getChangelog()
  {
    return this.changelog;
  }

  /**
   * Sets the changelog note.
   */

  public void setChangelog (String changelog)
  {
    this.changelog = changelog;
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Qualified name
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the qualified name, ot null if not set.
   */

  public String getQualifiedName()
  {
    return this.qualifiedName;
  }

  /**
   * Sets the qualified name.
   */

  public void setQualifiedName (String qualifiedName)
  {
    this.qualifiedName = qualifiedName;
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Corrector path
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the corrector path, or null if not set.
   */

  public String getCorrectorPath() {
    return this.correctorPath;
  }

  /**
   * Sets the corrector path.
   */

  public void setCorrectorPath (String correctorPath)
  {
    this.correctorPath = correctorPath;
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Width
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the with, or -1 if not set.
   */

  public int getWidth()
  {
    return this.width;
  }

  /**
   * Sets the width.
   */

  public void setWidth (int width)
  {
    this.width = width;
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Height
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the with, or -1 if not set.
   */

  public int getHeight()
  {
    return this.height;
  }

  /**
   * Sets the height.
   */

  public void setHeight (int height)
  {
    this.height = height;
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Duration
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the with, or -1 if not set.
   */

  public int getDuration()
  {
    return this.duration;
  }

  /**
   * Sets the duration.
   */

  public void setDuration (int duration)
  {
    this.duration = duration;
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Content type
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the content type, or {@link MediaType#UNDEFINED} if not set.
   */

  public int getContentType()
  {
    return this.contentType;
  }

  /**
   * Sets the content type
   */

  public void setContentType (int contentType)
  {
    this.contentType = contentType;
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Source type
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the source type, or {@link MediaType#UNDEFINED} if not set.
   */

  public int getSourceType()
  {
    return this.sourceType;
  }

  /**
   * Sets the source type
   */

  public void setSourceType (int sourceType)
  {
    this.sourceType = sourceType;
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Info page
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the path of the info page, or null if not set.
   */

  public String getInfoPagePath()
  {
    return this.infoPagePath;
  }

  /**
   * sets the path of the info page.
   */

  public void setInfoPagePath (String infoPagePath)
  {
    this.infoPagePath = infoPagePath;
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Thumbnail
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the path of the thumbnail, or null if not set.
   */

  public String getThumbnailPath()
  {
    return this.thumbnailPath;
  }

  /**
   * sets the path of the thumbnail.
   */

  public void setThumbnailPath (String thumbnailPath)
  {
    this.thumbnailPath = thumbnailPath;
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Timeframe
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the timeframe start. Throws an exception if the timeframe start can not be
   * determined. 
   */

  public long getTimeframeStart ()
    throws MasterException
  {
    if ( this.timeframeStart == -1 )
      throw new MasterException("Timeframe start not set");
    return this.timeframeStart;
  }

  /**
   * Returns the timeframe end. Throws an exception if the timeframe end can not be
   * determined. 
   */

  public long getTimeframeEnd ()
    throws MasterException
  {
    if ( this.timeframeEnd == -1 )
      throw new MasterException("Timeframe end not set");
    return this.timeframeEnd;
  }

  /**
   * Sets the timeframe start.
   */

  public void setTimeframeStart (long timeframeStart)
  {
    this.timeframeStart = timeframeStart;
  }

  /**
   * Sets the timeframe end.
   */

  public void setTimeframeEnd (long timeframeEnd)
  {
    this.timeframeEnd = timeframeEnd;
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Class
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the path of the teaching class, or null if not set.
   */

  public String getELClassPath()
  {
    return this.elClassPath;
  }

  /**
   * sets the path of the teaching class.
   */

  public void setELClassPath (String elClassPath)
  {
    this.elClassPath = elClassPath;
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Summary
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the path of the summary, or null if not set.
   */

  public String getSummaryPath()
  {
    return this.summaryPath;
  }

  /**
   * sets the path of the summary.
   */

  public void setSummaryPath (String summaryPath)
  {
    this.summaryPath = summaryPath;
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Semester
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the path of the semester, or null if not set.
   */

  public String getSemesterPath()
  {
    return this.semesterPath;
  }

  /**
   * sets the path of the semester.
   */

  public void setSemesterPath (String semesterPath)
  {
    this.semesterPath = semesterPath;
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Tutor
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the path of the tutor, or null if not set.
   */

  public String getTutorPath()
  {
    return this.tutorPath;
  }

  /**
   * sets the path of the tutor.
   */

  public void setTutorPath (String tutorPath)
  {
    this.tutorPath = tutorPath;
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Hide
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the <em>hide</em> flag, or null if not set.
   */

  public Boolean getHide ()
  {
    return this.hide;
  }

  /**
   * Controls the <em>hide</em> flag.
   */

  public void setHide (Boolean hide)
  {
    this.hide = hide;
  }

  /**
   * Controls the <em>hide</em> flag.
   */

  public void setHide (boolean hide)
  {
    this.hide = new Boolean(hide);
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Is Wrapper
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the <em>is_wrapper</em> flag, or null if not set.
   */

  public Boolean getIsWrapper ()
  {
    return this.isWrapper;
  }

  /**
   * Controls the <em>is_wrapper</em> flag.
   */

  public void setIsWrapper (Boolean isWrapper)
  {
    this.isWrapper = isWrapper;
  }

  /**
   * Controls the <em>is_wrapper</em> flag.
   */

  public void setIsWrapper (boolean isWrapper)
  {
    this.isWrapper = new Boolean(isWrapper);
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Code
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the code, ot null if not set.
   */

  public String getCode()
  {
    return this.code;
  }

  /**
   * Sets the code.
   */

  public void setCode (String code)
  {
    this.code = code;
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Login name (users only)
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the login name.
   */

  public String getLoginName()
  {
    return this.loginName;
  }

  /**
   * Sets the login name.
   */

  public void setLoginName (String loginName)
  {
    this.loginName = loginName;
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Password (users only)
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the password.
   */

  public String getPassword()
  {
    return this.password;
  }

  /**
   * Sets the password.
   */

  public void setPassword (String password)
  {
    this.password = password;
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: First name (users only)
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the first name.
   */

  public String getFirstName()
  {
    return this.firstName;
  }

  /**
   * Sets the first name.
   */

  public void setFirstName (String firstName)
  {
    this.firstName = firstName;
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Surname (users only)
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the surname.
   */

  public String getSurname()
  {
    return this.surname;
  }

  /**
   * Sets the surname.
   */

  public void setSurname (String surname)
  {
    this.surname = surname;
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Email (users only)
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the email.
   */

  public String getEmail()
  {
    return this.email;
  }

  /**
   * Sets the email.
   */

  public void setEmail (String email)
  {
    this.email = email;
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Theme (users only)
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the theme master, or null if not set.
   */

  public Master getTheme()
  {
    return this.theme;
  }

  /**
   * Sets the theme master.
   */

  public void setTheme (Master theme)
  {
    this.theme = theme;
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Attachables
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the attachables.
   */

  public Master[] getAttachables ()
  {
    if ( this.attachables == null ) this.attachables = new Vector<Master>();
    return (Master[])this.attachables.toArray(new Master[this.attachables.size()]);
  }

  /**
   * Sets the attachables.
   */

  public void setAttachables (Master[] attachables)
  {
    this.attachables = new Vector<Master>();
    for (int i = 0; i < attachables.length; i++)
      this.attachables.add(attachables[i]);
  }

  /**
   * Adds an attachable.
   */

  public void addAttachable (Master attachable)
  {
    if ( this.attachables == null ) this.attachables = new Vector<Master>();
    this.attachables.add(attachable);
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Authors
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the authors.
   */

  public Master[] getAuthors ()
  {
    if ( this.authors == null ) this.authors = new Vector<Master>();
    return (Master[])this.authors.toArray(new Master[this.authors.size()]);
  }

  /**
   * Sets the authors.
   */

  public void setAuthors (Master[] authors)
  {
    this.authors = new Vector<Master>();
    for (int i = 0; i < authors.length; i++)
      this.authors.add(authors[i]);
  }

  /**
   * Adds an author.
   */

  public void addAuthor (Master author)
  {
    if ( this.authors == null ) this.authors = new Vector<Master>();
    this.authors.add(author);
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Components
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the components.
   */

  public Master[] getComponents ()
  {
    if ( this.components == null ) this.components = new Vector<Master>();
    return (Master[])this.components.toArray(new Master[this.components.size()]);
  }

  /**
   * Sets the components.
   */

  public void setComponents (Master[] components)
  {
    this.components = new Vector<Master>();
    for (int i = 0; i < components.length; i++)
      this.components.add(components[i]);
  }

  /**
   * Adds a component.
   */

  public void addComponent (Master component)
  {
    if ( this.components == null ) this.components = new Vector<Master>();
    this.components.add(component);
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Links
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the links.
   */

  public Master[] getLinks ()
  {
    if ( this.links == null ) this.links = new Vector<Master>();
    return (Master[])this.links.toArray(new Master[this.links.size()]);
  }

  /**
   * Sets the links.
   */

  public void setLinks (Master[] links)
  {
    this.links = new Vector<Master>();
    for (int i = 0; i < links.length; i++)
      this.links.add(links[i]);
  }

  /**
   * Adds a link.
   */

  public void addLink (Master link)
  {
    if ( this.links == null ) this.links = new Vector<Master>();
    this.links.add(link);
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Members
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the members.
   */

  public Master[] getMembers ()
  {
    if ( this.members == null ) this.members = new Vector<Master>();
    return (Master[])this.members.toArray(new Master[this.members.size()]);
  }

  /**
   * Sets the members.
   */

  public void setMembers (Master[] members)
  {
    this.members = new Vector<Master>();
    for (int i = 0; i < members.length; i++)
      this.members.add(members[i]);
  }

  /**
   * Adds an member.
   */

  public void addMember (Master member)
  {
    if ( this.members == null ) this.members = new Vector<Master>();
    this.members.add(member);
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Themes DEPRECATED
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the themes.
   */

  public Master[] getThemes ()
  {
    if ( this.themes == null ) this.themes = new Vector<Master>();
    return (Master[])this.themes.toArray(new Master[this.themes.size()]);
  }

  /**
   * Sets the themes.
   */

  public void setThemes (Master[] themes)
  {
    this.themes = new Vector<Master>();
    for (int i = 0; i < themes.length; i++)
      this.themes.add(themes[i]);
  }

  /**
   * Adds a theme.
   */

  public void addTheme (Master theme)
  {
    if ( this.themes == null ) this.themes = new Vector<Master>();
    this.themes.add(theme);
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: GDIM entries
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the GDIM entries of the document represented by this master
   */

  public GDIMEntry[] getGDIMEntries()
  {
    if ( this.gdimEntries == null ) this.gdimEntries = new Vector<GDIMEntry>();
    return (GDIMEntry[])this.gdimEntries.toArray(new GDIMEntry[this.gdimEntries.size()]);
  }

  /**
   * Sets the GDIM entries of the document represented by this master.
   */

  public void setGDIMEntries (GDIMEntry[] gdimEntries)
  {
    this.gdimEntries = new Vector<GDIMEntry>();
    for (int i = 0; i < gdimEntries.length; i++)
      this.gdimEntries.add(gdimEntries[i]);
  }

  /**
   * Adds a GDIM entry.
   */

  public void addDIMEntry (GDIMEntry gdimEntry)
  {
    if ( this.gdimEntries == null ) this.gdimEntries = new Vector<GDIMEntry>();
    this.gdimEntries.add(gdimEntry);
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Read permissions
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the read permissions.
   */

  public Master[] getReadPermissions ()
  {
    if ( this.readPermissions == null ) this.readPermissions = new Vector<Master>();
    return (Master[])this.readPermissions.toArray(new Master[this.readPermissions.size()]);
  }

  /**
   * Sets the read permissions.
   */

  public void setReadPermissions (Master[] readPermissions)
  {
    this.readPermissions = new Vector<Master>();
    for (int i = 0; i < readPermissions.length; i++)
      this.readPermissions.add(readPermissions[i]);
  }

  /**
   * Adds a read permission.
   */

  public void addReadPermission (Master readPermission)
  {
    if ( this.readPermissions == null ) this.readPermissions = new Vector<Master>();
    this.readPermissions.add(readPermission);
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Write permissions
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the write permissions.
   */

  public Master[] getWritePermissions ()
  {
    if ( this.writePermissions == null ) this.writePermissions = new Vector<Master>();
    return (Master[])this.writePermissions.toArray(new Master[this.writePermissions.size()]);
  }

  /**
   * Sets the write permissions.
   */

  public void setWritePermissions (Master[] writePermissions)
  {
    this.writePermissions = new Vector<Master>();
    for (int i = 0; i < writePermissions.length; i++)
      this.writePermissions.add(writePermissions[i]);
  }

  /**
   * Adds a write permission.
   */

  public void addWritePermission (Master writePermission)
  {
    if ( this.writePermissions == null ) this.writePermissions = new Vector<Master>();
    this.writePermissions.add(writePermission);
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: User groups
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the user groups.
   */

  public Master[] getUserGroups ()
  {
    if ( this.userGroups == null ) this.userGroups = new Vector<Master>();
    return (Master[])this.userGroups.toArray(new Master[this.userGroups.size()]);
  }

  /**
   * Sets the user groups.
   */

  public void setUserGroups (Master[] userGroups)
  {
    this.userGroups = new Vector<Master>();
    for (int i = 0; i < userGroups.length; i++)
      this.userGroups.add(userGroups[i]);
  }

  /**
   * Adds a user group.
   */

  public void addUserGroup (Master userGroup)
  {
    if ( this.userGroups == null ) this.userGroups = new Vector<Master>();
    this.userGroups.add(userGroup);
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Create permissions
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Returns the create permissions
   */

  public int[] getCreatePermissions()
  {
    if ( this.createPermissions == null ) this.createPermissions = new IntList();
    return this.createPermissions.toIntArray();
  }

  /**
   * Sets the create permissions.
   */

  public void setCreatePermissions (int[] createPermissions)
  {
    this.createPermissions = new IntList();
    for (int i = 0; i < createPermissions.length; i++)
      this.createPermissions.add(createPermissions[i]);
  }

  /**
   * Adds a create permissions.
   */

  public void addCreatePermissions (int createPermission)
  {
    if ( this.createPermissions == null ) this.createPermissions = new IntList();
    this.createPermissions.add(createPermission);
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Reference attributes
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   
  /**
   * Returns the reference attributes of the document represented by this master.
   */

  public Map getRefAttribs ()
  {
    if ( this.refAttribs == null ) this.refAttribs = new HashMap();
    return new HashMap(this.refAttribs);
  }
   
  /**
   * Sets the reference attributes of the document represented by this master.
   */

  public void setRefAttribs (Map refAttribs)
  {
    this.refAttribs = new HashMap(refAttribs);
  }

  /**
   * Adds a reference attribute.
   */

  public void addRefAttrib (String name, Object value)
  {
    if ( this.refAttribs == null ) this.refAttribs = new HashMap();
    this.refAttribs.put(name, value);
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Other
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  public String[] getAllRefPaths() {
    return null;
  }
}
