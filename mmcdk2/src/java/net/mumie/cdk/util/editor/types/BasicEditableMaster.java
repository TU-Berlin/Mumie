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

package net.mumie.cdk.util.editor.types;

import net.mumie.cocoon.checkin.EditableMaster;
import net.mumie.cocoon.checkin.Master;
import net.mumie.cocoon.checkin.MasterException;
import net.mumie.cocoon.checkin.SimpleEditableMaster;
import net.mumie.cocoon.notions.DocType;

/**
 * This editable master handles the common meta information and acts as a base
 * for all specific editable masters.
 * 
 * @author Markus Gronau <a href="mailto:gronau@math.tu-berlin.de">gronau@math.tu-berlin.de</a>
 * @version <code>$Id$</code>
 */
public class BasicEditableMaster extends SimpleEditableMaster {

  public static EditableMaster createEditableMaster(Master master)
      throws MasterException {
    switch (master.getType()) {
    case DocType.IMAGE:
      return new ImageEditableMaster(master);
    case DocType.APPLET:
      return new AppletEditableMaster(master);
    case DocType.JAR:
      return new JarEditableMaster(master);
    case DocType.PAGE:
      return new PageEditableMaster(master);
    case DocType.CSS_STYLESHEET:
      return new CSSEditableMaster(master);
    case DocType.XSL_STYLESHEET:
      return new XSLEditableMaster(master);
    }
    throw new MasterException("Unknown document type of master: "
        + master.getTypeName());
  }

  public BasicEditableMaster(Master master) throws MasterException {
    // common master information
    setName(master.getName());
    setDescription(master.getDescription());
    setCopyright(master.getCopyright());
    setChangelog(master.getChangelog());
    setAuthors(master.getAuthors());
    setPath(master.getPath());
    setOldPath(master.getOldPath());
    setVersion(master.getVersion());

    setNature(master.getNature());
    setType(master.getType());
    setTypeName(master.getTypeName());
    setContentType(master.getContentType());
    setComponents(master.getComponents());
    setGDIMEntries(master.getGDIMEntries());

    // (still) unused methods

    // setLid(master.getLid());
    // setGenericDocPath(master.getGenericDocPath());
    // // setSummaryPath(master.getSummaryPath());
    // // setSemesterPath(master.getSemesterPath());
    // // setTutorPath(master.getTutorPath());
    // // setELClassPath(master.getELClassPath());
    // // setLoginName(master.getLoginName());
    // // setPassword(master.getPassword());
    // // setFirstName(master.getFirstName());
    // // setSurname(master.getSurname());
    // // setEmail(master.getEmail());
    // setCode(master.getCode());
    // setTheme(master.getTheme());
    // setLinks(master.getLinks());
    // setAttachables(master.getAttachables());
    // setMembers(master.getMembers());
    // setThemes(master.getThemes());
    // setReadPermissions(master.getReadPermissions());
    // setWritePermissions(master.getWritePermissions());
    // setUserGroups(master.getUserGroups());
    // setCategory(master.getCategory());
    // setId(master.getId());
    // setGenericDocId(master.getGenericDocId());
    // setVCThread(master.getVCThread());
    // setDuration(master.getDuration());
    // // setTimeframeStart(master.getTimeframeStart());
    // // setTimeframeEnd(master.getTimeframeEnd());
    // setRefAttribs(master.getRefAttribs());
    // setCreatePermissions(master.getCreatePermissions());
  }
}

/**
 * This editable master handles the applet specific meta information.
 * 
 */
class AppletEditableMaster extends BasicEditableMaster {

  public AppletEditableMaster(Master master) throws MasterException {
    super(master);
    setQualifiedName(master.getQualifiedName());
    setCorrectorPath(master.getCorrectorPath());
    setInfoPagePath(master.getInfoPagePath());
    setThumbnailPath(master.getThumbnailPath());
    setSourceType(master.getSourceType());
    setWidth(master.getWidth());
    setHeight(master.getHeight());
  }
}

/**
 * This editable master handles the image specific meta information.
 * 
 */
class ImageEditableMaster extends BasicEditableMaster {

  public ImageEditableMaster(Master master) throws MasterException {
    super(master);
    setWidth(master.getWidth());
    setHeight(master.getHeight());
  }
}

/**
 * This editable master handles the page specific meta information.
 * 
 */
class PageEditableMaster extends BasicEditableMaster {

  public PageEditableMaster(Master master) throws MasterException {
    super(master);

  }
}

/**
 * This editable master handles the jar file specific meta information.
 * 
 */
class JarEditableMaster extends BasicEditableMaster {

  public JarEditableMaster(Master master) throws MasterException {
    super(master);
    setPath(master.getPath());
  }
}

/**
 * This editable master handles the css style sheet specific meta information.
 * 
 */
class CSSEditableMaster extends BasicEditableMaster {

  public CSSEditableMaster(Master master) throws MasterException {
    super(master);
  }
}

/**
 * This editable master handles the xsl style sheet specific meta information.
 * 
 */
class XSLEditableMaster extends BasicEditableMaster {

  public XSLEditableMaster(Master master) throws MasterException {
    super(master);
  }
}
