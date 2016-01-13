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

package net.mumie.cocoon.checkin.documents;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import net.mumie.cocoon.checkin.AbstractContentObjectToCheckin;
import net.mumie.cocoon.checkin.CheckinException;
import net.mumie.cocoon.checkin.Content;
import net.mumie.cocoon.checkin.GDIMEntry;
import net.mumie.cocoon.checkin.Master;
import net.mumie.cocoon.checkin.MasterException;
import net.mumie.cocoon.checkin.Source;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.DbTable;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.Lang;
import net.mumie.cocoon.notions.Nature;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.RefType;
import net.mumie.cocoon.notions.Theme;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.pseudodocs.User;
import net.mumie.cocoon.util.LogUtil;
import net.mumie.cocoon.xml.MetaXMLElement;
import org.apache.avalon.framework.service.ServiceException;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Abstract base class for documents to checkin.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: AbstractDocumentToCheckin.java,v 1.32 2009/06/16 16:06:23 rassy Exp $</code>
 */

public abstract class AbstractDocumentToCheckin extends AbstractContentObjectToCheckin
  implements DocumentToCheckin
{
  // --------------------------------------------------------------------------------
  // Global variables
  // --------------------------------------------------------------------------------

  /**
   * The content of this document. If the document has no content (generic documents), this
   * is <code>null</code>.
   */

  protected Content content;

  /**
   * The source of this document. If the document has no source, this is <code>null</code>.
   */

  protected Source source;

  /**
   * The vc thread of this content object, given as its id. If the content object has
   * no vc thread (generic documents), this is {@link Id#UNDEFINED Id.UNDEFINED}.
   */

  protected int vcThreadId;

  /**
   * The version of this document, or <code>-1</code> if this document has no veersion.
   */

  protected int version;

  /**
   * The id of the document to be replaced by this one. This is the latest document in
   * the same vc thread as this document. If the document has no old id (first version,
   * generic document), this is {@link Id#UNDEFINED Id.UNDEFINED}.
   */

  protected int oldId;

  /**
   * The path of the document to be replaced by this one. This is the latest document in the
   * same vc thread as this document. Usually, the paths of the two document are equal; only
   * if the document is moved or renamed they differ. If the document has no old path (first
   * version, generic document), this is <code>null</code>.
   */

  protected String oldPath = null;

  /**
   * Whether this document is a new version of an existing document.
   */

  protected boolean isNewVersion;

  // --------------------------------------------------------------------------------
  // Lifecycle related methods
  // --------------------------------------------------------------------------------

  /**
   * Resets all global variables except the constant ones to their initial values.
   */

  protected void resetVariables ()
  {
    this.id = Id.UNDEFINED;
    this.path = null;
    this.master = null;
    this.content = null;
    this.source = null;
    this.vcThreadId = Id.UNDEFINED;
    this.version = -1;
    this.oldId = Id.UNDEFINED;
    this.isNewVersion = false;
  }

  // --------------------------------------------------------------------------------
  // Version and vc thread
  // --------------------------------------------------------------------------------

  /**
   * Returns the vc thread of this document, given as its id. If the content object has
   * no vc thread (generic documents), this is {@link Id#UNDEFINED Id.UNDEFINED}.
   */

  public int getVcThreadId()
  {
    return this.vcThreadId;
  }

  /**
   * Auxiliary method to set {@link #vcThreadId}, {@link #version}, {@link #oldId},
   * {@link #oldPath}, and {@link #isNewVersion}. Should be called when the document is not
   * generic.
   */

  protected void setupVersion (DbHelper dbHelper)
    throws CheckinException, MasterException, SQLException
  {
    final String METHOD_NAME = "setupVersion";
    this.logDebug(METHOD_NAME + " 1/2: Started");

    // Get the path of the document that would be replaced by this one
    String replacePath = this.master.getOldPath();
    if ( replacePath == null ) replacePath = this.path;

    // Retrieve vc thread and version of the document to be replaced (if any) from
    // the database:
    ResultSet resultSet = dbHelper.queryDataByPath
      (this.type,
       replacePath,
       new String[] {DbColumn.ID, DbColumn.VC_THREAD, DbColumn.VERSION},
       true);

    // Decide whether this is a new version:
    this.isNewVersion = resultSet.next();

    // Set vc thread, version, etc.:
    if ( this.isNewVersion )
      {
        this.oldId = resultSet.getInt(DbColumn.ID);
        this.vcThreadId = resultSet.getInt(DbColumn.VC_THREAD);
        this.version = resultSet.getInt(DbColumn.VERSION) + 1;
        this.oldPath = replacePath;
      }
    else
      {
        this.vcThreadId =
          dbHelper.storeVcThread(this.type, this.master.getName());
        this.version = 1;
      }

    this.logDebug
      (METHOD_NAME + " 2/2: Done." +
       " this.isNewVersion = " + this.isNewVersion +
       ", this.oldId = " + this.oldId +
       ", this.vcThreadId = " + this.vcThreadId +
       ", this.version = " + this.version +
       ", this.oldPath = " + this.oldPath +
       ", DEBUG replacePath = " + replacePath);
  }

  // --------------------------------------------------------------------------------
  // Getting id of existing
  // --------------------------------------------------------------------------------

  /**
   * Returns the id of the document with this path in the database, or
   * {@link Id#UNDEFINED Id.UNDEFINED} if there is no such document.
   */

  protected int getIdOfExisting (DbHelper dbHelper)
    throws CheckinException, SQLException
  {
    final String METHOD_NAME = "getIdOfExisting";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    ResultSet resultSet = dbHelper.queryDataByPath
      (this.type, this.path, new String[] {DbColumn.ID}, false);
    int id = (resultSet.next() ? resultSet.getInt(DbColumn.ID) : Id.UNDEFINED);
    this.logDebug(METHOD_NAME + " 2/2: Done, id = " + id);
    return id;
  }

  // --------------------------------------------------------------------------------
  // Checking permissions
  // --------------------------------------------------------------------------------

  /**
   * Checks if the specified user has permission to create new documents of this
   * type. Throws an exception if not.
   */

  protected void checkCreatePermission (User user)
    throws CheckinException, ServiceException 
  {
    if ( !user.hasCreatePermission(this.type) )
      throw new CheckinException
        (this.path +
         ": No permission to create (or overwrite) documents of type: "
         + DocType.nameFor(this.type));
  }

  /**
   * If this is a new version of an existing document, checks if the specified user has
   * permission to add a new version; otherwise, checks if the user has permission to create
   * new documents of this type. Throws an exception if not.
   */

  protected void checkPermissions (User user)
    throws CheckinException, ServiceException 
  {
    if ( this.isNewVersion )
      {
        // Check if the user is allowed to add a new version:
        if ( !user.hasWritePermission(this.type, this.vcThreadId) )
          throw new CheckinException
            (this.path + ": No permission to add a new version");
      }
    else
      {
        // Check if the user is allowed to create new documents of this type:
        checkCreatePermission(user);
      }
  }

  // --------------------------------------------------------------------------------
  // Checking-in corrector
  // --------------------------------------------------------------------------------

  /**
   * Checks-in the corrector
   */

  protected void checkinCorrector (DbHelper dbHelper)
    throws SQLException, MasterException, CheckinException
  {
    final String METHOD_NAME = "checkinCorrector";
    this.logDebug(METHOD_NAME + " 1/3: Started");

    String correctorPath = this.master.getCorrectorPath();
    if ( correctorPath == null )
      this.logDebug(METHOD_NAME + " 2/3: No corrector specified");
    else
      {
        ResultSet resultSet = dbHelper.queryDataByPath
          (DocType.JAVA_CLASS, correctorPath, new String[] {DbColumn.ID}, true);
        if ( !resultSet.next() )
          throw new CheckinException("No Corrector found for path: " + correctorPath);
        int correctorId = resultSet.getInt(DbColumn.ID);
        this.logDebug(METHOD_NAME + " 2/3: correctorId = " + correctorId);
        dbHelper.updateDatum
          (this.type, this.id, DbColumn.CORRECTOR, correctorId);
      }
    this.logDebug(METHOD_NAME + " 3/3: Done");
  }

  /**
   * Checks-in an e-learning class refernce
   */

  protected void checkinELClass (DbHelper dbHelper)
    throws MasterException, SQLException, CheckinException
  {
    final String METHOD_NAME = "checkinELClass";
    this.logDebug(METHOD_NAME + " 1/3: Started");

    String elClassPath = this.master.getELClassPath();
    if ( elClassPath == null )
      this.logDebug(METHOD_NAME + " 2/3: No e-learning class specified");
    else
      {
        ResultSet resultSet = dbHelper.queryPseudoDocDataByPath
          (PseudoDocType.CLASS, elClassPath, new String[] {DbColumn.ID});
        if ( !resultSet.next() )
          throw new CheckinException("No e-learning class found for path: " + elClassPath);
        int elClassId = resultSet.getInt(DbColumn.ID);
        this.logDebug(METHOD_NAME + " 2/3: elClassId = " + elClassId);
        dbHelper.updateDatum
          (this.type, this.id, DbColumn.CLASS, elClassId);
      }
    this.logDebug(METHOD_NAME + " 3/3: Done");
  }

  // --------------------------------------------------------------------------------
  // Checking-in info page reference
  // --------------------------------------------------------------------------------

  /**
   * Checks-in the info page reference
   */

  protected void checkinInfoPage (DbHelper dbHelper)
    throws SQLException, MasterException, CheckinException
  {
    final String METHOD_NAME = "checkinInfoPage";
    this.logDebug(METHOD_NAME + " 1/3: Started");

    String infoPagePath = this.master.getInfoPagePath();
    if ( infoPagePath == null )
      this.logDebug(METHOD_NAME + " 2/3: No info page specified");
    else
      {
        ResultSet resultSet = dbHelper.queryDataByPath
          (DocType.GENERIC_PAGE, infoPagePath, new String[] {DbColumn.ID}, false);
        if ( !resultSet.next() )
          throw new CheckinException("No info page found for path: " + infoPagePath);
        int infoPageId = resultSet.getInt(DbColumn.ID);
        this.logDebug(METHOD_NAME + " 2/3: infoPageId = " + infoPageId);
        dbHelper.updateDatum
          (this.type, this.id, DbColumn.INFO_PAGE, infoPageId);
      }
    this.logDebug(METHOD_NAME + " 3/3: Done");
  }

  // --------------------------------------------------------------------------------
  // Checking-in thumbnail reference
  // --------------------------------------------------------------------------------

  /**
   * Checks-in the thumbnail reference
   */

  protected void checkinThumbnail (DbHelper dbHelper)
    throws SQLException, MasterException, CheckinException
  {
    final String METHOD_NAME = "checkinThumbnail";
    this.logDebug(METHOD_NAME + " 1/3: Started");

    String thumbnailPath = this.master.getThumbnailPath();
    if ( thumbnailPath == null )
      this.logDebug(METHOD_NAME + " 2/3: No thumbnail specified");
    else
      {
        ResultSet resultSet = dbHelper.queryDataByPath
          (DocType.GENERIC_IMAGE, thumbnailPath, new String[] {DbColumn.ID}, false);
        if ( !resultSet.next() )
          throw new CheckinException("No thumbnail found for path: " + thumbnailPath);
        int thumbnailId = resultSet.getInt(DbColumn.ID);
        this.logDebug(METHOD_NAME + " 2/3: thumbnailId = " + thumbnailId);
        dbHelper.updateDatum
          (this.type, this.id, DbColumn.THUMBNAIL, thumbnailId);
      }
    this.logDebug(METHOD_NAME + " 3/3: Done");
  }

  // --------------------------------------------------------------------------------
  // Checking-in summary reference
  // --------------------------------------------------------------------------------

  /**
   * Checks-in the summary reference
   */

  protected void checkinSummary (DbHelper dbHelper)
    throws SQLException, MasterException, CheckinException
  {
    final String METHOD_NAME = "checkinSummary";
    this.logDebug(METHOD_NAME + " 1/3: Started");

    String summaryPath = this.master.getSummaryPath();
    if ( summaryPath == null )
      this.logDebug(METHOD_NAME + " 2/3: No summary specified");
    else
      {
        ResultSet resultSet = dbHelper.queryDataByPath
          (DocType.GENERIC_SUMMARY, summaryPath, new String[] {DbColumn.ID}, false);
        if ( !resultSet.next() )
          throw new CheckinException("No generic summary found for path: " + summaryPath);
        int summaryId = resultSet.getInt(DbColumn.ID);
        this.logDebug(METHOD_NAME + " 2/3: summaryId = " + summaryId);
        dbHelper.updateDatum
          (this.type, this.id, DbColumn.SUMMARY, summaryId);
      }
    this.logDebug(METHOD_NAME + " 3/3: Done");
  }

  // --------------------------------------------------------------------------------
  // Checking-in references
  // --------------------------------------------------------------------------------

  /**
   * Checks-in the specified references with the specified reference type using the
   * specified db helpser.
   */

  protected void checkinReferences (Master[] refTargets, int refType, DbHelper dbHelper)
    throws MasterException, CheckinException, SQLException
  {
    final String METHOD_NAME = "checkinReferences";
    this.logDebug
      (METHOD_NAME + " 1/2: Started." +
       " refTargets = " + mastersToString(refTargets) +
       ", refType = " + refType);
    for (int i = 0; i < refTargets.length; i++)
      {
        Master refTarget = refTargets[i];
        if ( refTarget.getNature() != Nature.DOCUMENT )
          throw new CheckinException
            ("Expected a document reference, but found: " + refTarget.getTypeName());
        int toDocType = refTarget.getType();
        String toDocPath = refTarget.getPath();
        int toDocId = this.getIdForPath(toDocType, toDocPath, this.path, dbHelper);
        String lid = refTarget.getLid();
        Map attribs = refTarget.getRefAttribs();
        dbHelper.storeReference
          (this.type, this.id, toDocType, toDocId, lid, refType, attribs);
      }
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Checks-in the references.
   */

  public void checkinReferences (DbHelper dbHelper)
    throws CheckinException, MasterException, SQLException
  {
    final String METHOD_NAME = "checkinReferences";
    this.logDebug(METHOD_NAME + " 1/2: Started");

    // Components:
    this.checkinReferences
      (this.master.getComponents(), RefType.COMPONENT, dbHelper);

    // Links:
    this.checkinReferences
      (this.master.getLinks(), RefType.LINK, dbHelper);

    // Attachables:
    this.checkinReferences
      (this.master.getAttachables(), RefType.ATTACHABLE, dbHelper);
    
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // Checking-in authors
  // --------------------------------------------------------------------------------

  /**
   * Checks-in the author relatinships of this document.
   */

  public void checkinAuthorships (DbHelper dbHelper, Master defaults)
    throws CheckinException, MasterException, SQLException
  {
    final String METHOD_NAME = "checkinAuthorships";
    this.logDebug(METHOD_NAME + " 1/3: Started");

    Master[] authors = this.master.getAuthors();
    this.logDebug(METHOD_NAME + " 2/3: authors: " + mastersToString(authors));

    for (int i = 0; i < authors.length; i++)
      {
        Master author = authors[i];
        if ( author.getNature() != Nature.PSEUDO_DOCUMENT ||
             author.getType() != PseudoDocType.USER )
          throw new CheckinException
            ("Expected a user, but found: " + author.getTypeName());
        String authorPath = author.getPath();
        int authorId =
          this.getPseudoDocIdForPath(PseudoDocType.USER, authorPath, this.path, dbHelper);
        dbHelper.storeAuthorship(this.type, this.id, authorId);
      }

    this.logDebug(METHOD_NAME + " 3/3: Done");
  }

  // --------------------------------------------------------------------------------
  // Checking-in theme map entries
  // --------------------------------------------------------------------------------

  /**
   * Checks-in the theme map entries of this document.
   */

  protected void checkinThemeMapEntries (DbHelper dbHelper)
    throws CheckinException, MasterException, SQLException
  {
    final String METHOD_NAME = "checkinThemeMapEntries";
    this.logDebug(METHOD_NAME + " 1/3: Started");

    GDIMEntry[] gdims = this.master.getGDIMEntries();
    this.logDebug(METHOD_NAME + " 2/3: gdims: ");// + mastersToString(themes));

    for (int i = 0; i < gdims.length; i++)
      {
        GDIMEntry gdim = gdims[i];
        String themePath = gdim.getThemePath();
        String langPath = gdim.getLang();
        String langCode = gdim.getLangCode();
        int themeId =
          (themePath != null
           ? this.getPseudoDocIdForPath(PseudoDocType.THEME, themePath, this.path, dbHelper)
           : Theme.DEFAULT);
	int langId = Lang.DEFAULT;
	if ( langPath != null )
	  // case of a language given by path. This takes precedence if a lang-code is also given.
	  langId = this.getPseudoDocIdForPath(PseudoDocType.LANGUAGE, langPath, this.path, dbHelper);
	else if ( langCode != null )
	  // case of a language given by code
	  langId = this.getLangIdForCode(langCode, dbHelper);
        int genericDocType = DocType.genericOf[this.type];
        String genericDocPath = gdim.getGenericDocPath();
        int genericDocId = dbHelper.getIdForPath(genericDocType, genericDocPath);
        dbHelper.storeThemeMapEntry(this.type, themeId, langId, genericDocId, this.id);
      }

    this.logDebug(METHOD_NAME + " 2/3: Done");
  }

  /**
   * Deletes all entries in the theme map which refer to the old id.
   */

  protected void deleteGDIMEntries (DbHelper dbHelper)
    throws SQLException
  {
    final String METHOD_NAME = "deleteGDIMEntries";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    dbHelper.deleteGDIMEntries(this.type, this.oldId);
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // Updating references
  // --------------------------------------------------------------------------------

  /**
   * Updates all references pointing to this document.
   */

  protected void updateRefTargets (DbHelper dbHelper)
    throws SQLException
  {
    final String METHOD_NAME = "updateRefTargets";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    for (int fromDocType = DocType.first; fromDocType < DocType.last; fromDocType++)
      if ( DbTable.REF[fromDocType][this.type] != null )
	dbHelper.updateRefTargets(fromDocType, this.type, this.oldId, this.id);
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // Updating corrector
  // --------------------------------------------------------------------------------

  /**
   * Updates all corrector entries pointing to this document.
   */

  protected void updateCorrector (DbHelper dbHelper)
    throws SQLException
  {
    final String METHOD_NAME = "updateCorrector";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    for (int docType = DocType.first; docType < DocType.last; docType++)
      {
        if ( DocType.hasCorrector[docType] )
          dbHelper.updateCorrector(docType, this.oldId, this.id);
      }
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // Utilities to checkin
  // --------------------------------------------------------------------------------

  /**
   * Either stores or updates the data contained in the specified map. If this
   * document does not yet exist in the database, it is created. Otherwise, it is
   * updated.
   */

  protected void storeOrUpdateData (DbHelper dbHelper, Map data)
    throws CheckinException
  {
    final String METHOD_NAME = "storeOrUpdateData";
    this.logDebug(METHOD_NAME + " 1/3: Started");
    try
      {
        int id = this.getIdOfExisting(dbHelper);
        if ( id == Id.UNDEFINED )
          {
            // Create new document:
            this.logDebug(METHOD_NAME + " 2/3: New Document");
            this.id = dbHelper.storeData(this.type, data);
          }
        else
          {
            // Update existing document:
            this.logDebug(METHOD_NAME + " 2/3: Document exists");
            this.id = id;
            dbHelper.updateData(this.type, this.id, data);
          }
        this.logDebug(METHOD_NAME + " 3/3: Done");
      }
    catch (Exception exception)
      {
        throw new CheckinException(this.getIdentification(), exception);
      }
  }

  // --------------------------------------------------------------------------------
  // Utilities to prepare the map containing the checkin data
  // --------------------------------------------------------------------------------

  /**
   * Inserts the category into the specifed map.
   */

  protected void categoryToData (Map data)
    throws MasterException
  {
    data.put(DbColumn.CATEGORY, new Integer(this.master.getCategory()));
  }

  //DEPRECATED
  /**
   * Inserts the vc thread into the specifed map.
   */

  protected void vcThreadToData (Map data)
    throws MasterException
  {
    data.put(DbColumn.VC_THREAD, new Integer(this.vcThreadId));
  }

  //DEPRECATED
  /**
   * Inserts the version into the specifed map.
   */

  protected void versionToData (Map data)
    throws MasterException
  {
    data.put(DbColumn.VERSION, new Integer(this.version));
  }

  /**
   * Inserts the qualified name into the specifed map.
   */

  protected void qualifiedNameToData (Map data)
    throws MasterException
  {
    data.put(DbColumn.QUALIFIED_NAME, this.master.getQualifiedName());
  }

  /**
   * Inserts the copyright into the specified map.
   */

  protected final void copyrightToData (Map data)
    throws MasterException
  {
    data.put(DbColumn.COPYRIGHT, this.master.getCopyright());
  }

  /**
   * Inserts the <em>is_wrapper</em> flag into the specified map, provided there is such e flag.
   */

  protected void isWrapperToData (Map data)
    throws MasterException
  {
    Boolean isWrapper = this.master.getIsWrapper();
    if ( isWrapper != null )
      data.put(DbColumn.IS_WRAPPER, isWrapper);
  }

  /**
   * Inserts the timeframe start into the specified map.
   */

  protected final void timeframeStartToData (Map data)
    throws MasterException
  {
    data.put(DbColumn.TIMEFRAME_START, new Timestamp(this.master.getTimeframeStart()));
  }

  /**
   * Inserts the timeframe end into the specified map.
   */

  protected final void timeframeEndToData (Map data)
    throws MasterException
  {
    data.put(DbColumn.TIMEFRAME_END, new Timestamp(this.master.getTimeframeEnd()));
  }

  /**
   * Inserts the width into the specifed map.
   */

  protected void widthToData (Map data)
    throws MasterException
  {
    data.put(DbColumn.WIDTH, new Integer(this.master.getWidth()));
  }

  /**
   * Inserts the height into the specifed map.
   */

  protected void heightToData (Map data)
    throws MasterException
  {
    data.put(DbColumn.HEIGHT, new Integer(this.master.getHeight()));
  }

  /**
   * Inserts the duration into the specifed map.
   */

  protected void durationToData (Map data)
    throws MasterException
  {
    data.put(DbColumn.DURATION, new Integer(this.master.getDuration()));
  }

  /**
   * Inserts the content type into the specifed map.
   */

  protected void contentTypeToData (Map data)
    throws MasterException
  {
    data.put(DbColumn.CONTENT_TYPE, new Integer(this.master.getContentType()));
  }

  /**
   * Inserts the content length into the specifed map.
   */

  protected void contentLengthToData (Map data)
    throws MasterException, CheckinException
  {
    data.put(DbColumn.CONTENT_LENGTH, new Long(this.content.getLength()));
  }

  /**
   * Inserts the content to the specifed map, as a string.
   */

  protected void contentAsStringToData (Map data)
    throws MasterException, IOException, CheckinException
  {
    data.put(DbColumn.CONTENT, getAsString(this.content.getInputStream()));
  }

  /**
   * Inserts the source type into the specifed map.
   */

  protected void sourceTypeToData (Map data)
    throws MasterException
  {
    data.put(DbColumn.SOURCE_TYPE, new Integer(this.master.getSourceType()));
  }

  /**
   * Inserts the source length into the specifed map.
   */

  protected void sourceLengthToData (Map data)
    throws MasterException, CheckinException
  {
    data.put(DbColumn.SOURCE_LENGTH, new Long(this.source.getLength()));
  }

  /**
   * Inserts the source to the specifed map.
   * Returns: whether there is a source.
   */

  protected boolean sourceToData (Map data)
    throws MasterException, IOException, CheckinException
  {
    if ( source != null )
      {
	data.put(DbColumn.SOURCE, getAsString(this.source.getInputStream()));
	return true;
      }
    else
      return false;
  }

  // --------------------------------------------------------------------------------
  // Utilities for check-in
  // --------------------------------------------------------------------------------

  /**
   * Checks-in the reference to the section this document is contained in.
   */

  protected void checkinVCData (DbHelper dbHelper)
    throws SQLException,CheckinException
  {
    final String METHOD_NAME = "checkinVCData";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    Map data = new HashMap();
    try
      {
	this.vcThreadToData(data);
	this.versionToData(data);
      }
    catch(MasterException me)
      {
	throw new CheckinException(this.getIdentification(), me);
      }
    dbHelper.updateData(this.type, this.id, data);
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * Reads the data from the specified input stream and returns them as a string.
   */

  protected static String getAsString (InputStream inputStream)
    throws IOException
  {
    InputStreamReader reader = new InputStreamReader(inputStream);
    StringWriter writer = new StringWriter();
    final int BUFFER_SIZE =1024;
    char[] buffer = new char[BUFFER_SIZE];
    int length;
    while ( (length = reader.read(buffer, 0, BUFFER_SIZE)) != -1 )
      writer.write(buffer, 0, length);
    writer.flush();
    return writer.toString();
  }
}
