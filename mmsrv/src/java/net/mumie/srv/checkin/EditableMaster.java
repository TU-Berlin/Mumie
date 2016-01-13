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

/**
 * <p>
 * Represents the editable master of a document or pseudo-document.
 * </p>
 * <p>
 * This interface provides read/write access to the metainfos contained in the
 * master file of a document or pseudo-document. It can be used with any types
 * of documents and pseudo-documents. For each metainfo, a method
 * <code>getXxxx()</code> and <code>setXxxx(YY)</code> exist where Xxxx
 * denotes the metainfo and YY the value. The return type depends on the
 * metainfo. If a metainfo is requested that does not exist for the type of
 * (pseudo-)document the master file belongs to, an exception is thrown (for
 * example, if the <code>Master</code> object represents the master file of a
 * "page" document, and {@link #getWidth getWidth} is called, an exception is
 * thrown because pages do not have the "width" metainfo).
 * </p>
 * 
 * @author Markus Gronau <a
 *         href="mailto:gronau@math.tu-berlin.de">gronau@math.tu-berlin.de</a>
 * @version <code>$Id: EditableMaster.java,v 1.4 2009/10/07 00:36:15 rassy Exp $</code>
 */
public interface EditableMaster extends Master {

  /**
   * Sets the path specified by this master. May be <code>null</code>.
   */

  public void setPath(String path) throws MasterException;

  /**
   * Sets the nature (document or pseudo-document).
   */

  public void setNature(int nature) throws MasterException;

  /**
   * Sets the type.
   */

  public void setType(int type) throws MasterException;

  /**
   * Sets the type name.
   */

  public void setTypeName(String typeName) throws MasterException;

  /**
   * Sets the category. Throws an exception if the category can not be set.
   */

  public void setCategory(int category) throws MasterException;

  /**
   * Sets the id specified by this master or {@link Id#UNDEFINED UNDEFINED} if
   * undefined.
   */

  public void setId(int id) throws MasterException;

  /**
   * Sets the lid specified by this master. May be <code>null</code>.
   */

  public void setLid(String lid) throws MasterException;

  /**
   * Only with theme references; sets the id of the generic document which is
   * implemented by the document represented by this master, or
   * {@link Id#UNDEFINED UNDEFINED} if no such id is set.
   */

  public void setGenericDocPath(String genDocPath) throws MasterException;

  /**
   * Sets the name.
   */

  public void setName(String name) throws MasterException;

  /**
   * Sets the description. May be <code>null</code>.
   */

  public void setDescription(String description) throws MasterException;

  /**
   * Sets the copyright note. May be <code>null</code>.
   */

  public void setCopyright(String copyright) throws MasterException;

  /**
   * Sets the changelog note. May be <code>null</code>.
   */

  public void setChangelog(String changelog) throws MasterException;

  /**
   * Sets the qualified name
   */

  public void setQualifiedName(String qualifiedName) throws MasterException;

  /**
   * Sets the version, or -1 if not defined.
   */

  public void setVersion(int version) throws MasterException;

  /**
   * Sets the vc thread, or {@link Id#UNDEFINED UNDEFINED} if not set.
   */

  public void setVCThread(int vcThread) throws MasterException;

  /**
   * Sets the width. Throws an exception if the width can not be set.
   */

  public void setWidth(int width) throws MasterException;

  /**
   * Sets the height. Throws an exception if the height can not be set.
   */

  public void setHeight(int height) throws MasterException;

  /**
   * Sets the duration. Throws an exception if the duration can not be set.
   */

  public void setDuration(int duration) throws MasterException;

  /**
   * Sets the corrector path. May be <code>null</code>.
   */

  public void setCorrectorPath(String correctorPath) throws MasterException;

  /**
   * Sets the info page path. May be <code>null</code>.
   */

  public void setInfoPagePath(String infoPagePath) throws MasterException;

  /**
   * Sets the thumbnail path. May be <code>null</code>.
   */

  public void setThumbnailPath(String thumbnailPath) throws MasterException;

  /**
   * Sets the summary path. May be <code>null</code>.
   */

  public void setSummaryPath(String summaryPath) throws MasterException;

  /**
   * Sets the semester path. May be <code>null</code>.
   */

  public void setSemesterPath(String semesterPath) throws MasterException;

  /**
   * Sets the tutor path. May be <code>null</code>.
   */

  public void setTutorPath(String tutorPath) throws MasterException;

  /**
   * Returns the <em>hide</em> flag.
   */

  public Boolean getHide ();

  /**
   * Sets the <em>hide</em> flag. If null, the flag is deleted. 
   */

  public void setHide (Boolean hide);

  /**
   * Sets the <em>hide</em> flag
   */

  public void setHide (boolean hide);

  /**
   * Returns the <em>is_wrapper</em> flag.
   */

  public Boolean getIsWrapper ();

  /**
   * Sets the <em>is_wrapper</em> flag. If null, the flag is deleted. 
   */

  public void setIsWrapper (Boolean isWrapper);

  /**
   * Sets the <em>is_wrapper</em> flag
   */

  public void setIsWrapper (boolean isWrapper);

  /**
   * Sets the theme master. May be <code>null</code>.
   */

  public void setTheme(Master theme) throws MasterException;

  /**
   * Sets the path of the e-learning class of the document represented by the
   * specified master root element. May be <code>null</code>.
   */

  public void setELClassPath(String elClassPath) throws MasterException;

  /**
   * Sets the content type. Throws an exception if the content type can not be
   * sets.
   */

  public void setContentType(int contentType) throws MasterException;

  /**
   * Sets the timeframe start. Throws an exception if the timeframe start can
   * not be set.
   */

  public void setTimeframeStart(long timeFrameStart) throws MasterException;

  /**
   * Sets the timeframe end. Throws an exception if the timeframe end can not be
   * set.
   */

  public void setTimeframeEnd(long timeFrameEnd) throws MasterException;

  /**
   * Sets the login name.
   */

  public void setLoginName(String loginName) throws MasterException;

  /**
   * Sets the password.
   */

  public void setPassword(String password) throws MasterException;

  /**
   * Sets the first name.
   */

  public void setFirstName(String firstName) throws MasterException;

  /**
   * Sets the surname.
   */

  public void setSurname(String surName) throws MasterException;

  /**
   * Sets the email.
   */

  public void setEmail(String email) throws MasterException;

  /**
   * Sets the code.
   */

  public void setCode(String code) throws MasterException;

  /**
   * Sets the components of the document represented by this master
   */

  public void setComponents(Master[] components) throws MasterException;

  /**
   * Adds a component.
   */

  public void addComponent (Master component) throws MasterException;

  /**
   * Sets the links in the document represented by this master
   */

  public void setLinks(Master[] links) throws MasterException;

  /**
   * Adds a link.
   */

  public void addLink (Master link) throws MasterException;

  /**
   * Sets the members of the document represented by this master
   */

  public void setMembers(Master[] members) throws MasterException;

  /**
   * Adds an member.
   */

  public void addMember (Master member) throws MasterException;

  /**
   * Sets the authors of the document represented by this master
   */

  public void setAuthors(Master[] authors) throws MasterException;

  /**
   * Adds an author.
   */

  public void addAuthor (Master author) throws MasterException;

  /**
   * Sets the GDIM entries of the document represented by this master
   */

  public void setGDIMEntries(GDIMEntry[] gDIMEntries) throws MasterException;

  /**
   * Adds a GDIM entry.
   */

  public void addDIMEntry (GDIMEntry gdimEntry) throws MasterException;

  /**
   * Sets the read permissions of the document represented by this master
   */

  public void setReadPermissions(Master[] readPermissions)
      throws MasterException;

  /**
   * Adds a read permission.
   */

  public void addReadPermission (Master readPermission) throws MasterException;

  /**
   * Sets the write permissions of the document represented by this master
   */

  public void setWritePermissions(Master[] writePermissions)
      throws MasterException;

  /**
   * Adds a write permission.
   */

  public void addWritePermission (Master writePermission) throws MasterException;

  /**
   * Sets the user groups of the user represented by this master
   */

  public void setUserGroups(Master[] userGroups) throws MasterException;

  /**
   * Adds a user group.
   */

  public void addUserGroup (Master userGroup) throws MasterException;

  /**
   * Sets the label.
   */

  public void setLabel (String label)
    throws MasterException;

  /**
   * Sets the points
   */

  public void setPoints (Integer points)
    throws MasterException;

  /**
   * Sets the document types the user group represented by this master can
   * create new documents for.
   */

  public void setCreatePermissions(int[] createPermissions)
    throws MasterException;

  /**
   * Adds a create permissions.
   */

  public void addCreatePermissions (int createPermission) throws MasterException;
}
