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
import java.sql.SQLException;
import java.util.Map;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.Nature;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.pseudodocs.User;
import net.mumie.cocoon.service.AbstractJapsService;
import net.mumie.cocoon.service.AbstractJapsServiceable;
import net.mumie.cocoon.util.PasswordEncryptor;
import net.mumie.cocoon.util.PathTokenizer;
import net.mumie.cocoon.xml.MetaXMLElement;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.activity.Disposable;
import org.w3c.dom.Element;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import net.mumie.cocoon.checkin.documents.DocumentToCheckin;
import net.mumie.cocoon.checkin.pseudodocs.PseudoDocumentToCheckin;

/**
 * Abstract base class for pseudo-documents and documents to checkin.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: AbstractContentObjectToCheckin.java,v 1.18 2008/09/08 08:48:25 rassy Exp $</code>
 */

public abstract class AbstractContentObjectToCheckin extends AbstractJapsService
  implements ContentObjectToCheckin, Recyclable, Disposable
{
  // --------------------------------------------------------------------------------
  // Global variables
  // --------------------------------------------------------------------------------

  /**
   * The nature of this content object. 
   */

  protected int nature;

  /**
   * The type of this content object.
   */

  protected int type;

  /**
   * The id of this content object, or {@link Id#UNDEFINED Id.UNDEFINED} if the id is not
   * knwon yet.
   */

  protected int id;

  /**
   * The path of this content object
   */

  protected String path;

  /**
   * The master of this content object.
   */

  protected Master master;

  /**
   * The path tokenizer to tokenize checkin paths.
   */

  protected PathTokenizer pathTokenizer = new PathTokenizer();

  /**
   * Auxiliary object for the toSAX methods.
   */

  protected MetaXMLElement xmlElement = new MetaXMLElement();

  // --------------------------------------------------------------------------------
  // Getting nature, type, id, path
  // --------------------------------------------------------------------------------

  /**
   * Returns the nature of this content object. The return value is either
   * {@link Nature#DOCUMENT Nature.DOCUMENT} (for a proper document) or
   * {@link Nature#PSEUDO_DOCUMENT Nature.PSEUDO_DOCUMENT} (for a pseudo-document).
   */

  public int getNature ()
  {
    return this.nature;
  }

  /**
   * Returns the type of this content object, as numerical code. If the content object is a
   * proper document, this is one of the type constants defined in
   * {@link DocType DocType}. If the content object is a pseudo document, this is one of the
   * type constants defined in {@link PseudoDocType PseudoDocType}.
   */

  public int getType ()
  {
    return this.type;
  }

  /**
   * Returns the id of this content object, or {@link Id#UNDEFINED Id.UNDEFINED} if the id
   * is not known yet.
   */

  public int getId ()
  {
    return this.id;
  }

  /**
   * Returns the path of this content object.
   */

  public String getPath ()
  {
    return this.path;
  }

  // --------------------------------------------------------------------------------
  // Checkin
  // --------------------------------------------------------------------------------

  /**
   * Called from {@link #checkin checkin} when <code>stage</code> is 1. This implementation
   * does nothing. Extending classes might overwrite this.
   */

  protected void checkin_1 (DbHelper dbHelper,
                            User user,
                            PasswordEncryptor encryptor,
                            Master defaults)
    throws CheckinException
  {
    // Does nothing
  }

  /**
   * Checking in "contained_in" references.
   * Called from {@link #checkin checkin} when <code>stage</code> is 2. This implementation
   * is proposed to be universal and NOT overwritten by extending classes.
   */

  protected void checkin_2 (DbHelper dbHelper,
                            User user,
                            PasswordEncryptor encryptor,
                            Master defaults)
    throws CheckinException
  {
    try
      {
        final String METHOD_NAME = "checkin_2";
        this.logDebug(METHOD_NAME + " 1/2: Started");

        this.checkinContainedIn(dbHelper);

        this.logDebug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
        throw new CheckinException(this.getIdentification(), exception);
      }
  }

  /**
   * Checkin stage 3. Checks-in all referential data.
   */

  protected void checkin_3 (DbHelper dbHelper,
                            User user,
                            PasswordEncryptor encryptor,
                            Master defaults)
    throws CheckinException
  {
    try
      {
        final String METHOD_NAME = "checkin_3";
        this.logDebug(METHOD_NAME + " 1/2: Started");
        this.checkinReadPermissions(dbHelper, defaults);
        this.checkinWritePermissions(dbHelper, defaults);
        this.logDebug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
        throw new CheckinException(this.getIdentification(), exception);
      }
  }

  /**
   * Called from {@link #checkin checkin} when <code>stage</code> is 4. This implementation
   * does nothing. Extending classes might overwrite this.
   */

  protected void checkin_4 (DbHelper dbHelper,
                            User user,
                            PasswordEncryptor encryptor,
                            Master defaults)
    throws CheckinException
  {
    // Does nothing
  }

  /**
   * Calls {@link #checkin_1 checkin_1}, {@link #checkin_2 checkin_2},
   * {@link #checkin_3 checkin_3}, or {@link #checkin_4 checkin_4} if <code>stage</code> is
   * 1 resp. 2 resp. 3 resp. 4.
   */

  public void checkin (int stage,
                       DbHelper dbHelper,
                       User user,
                       PasswordEncryptor encryptor,
                       Master defaults)
    throws CheckinException
  {
    final String METHOD_NAME = "checkin";
    this.logDebug(METHOD_NAME + " 1/2: Started. stage = " + stage);
    switch ( stage )
      {
      case 1 :
        checkin_1(dbHelper, user, encryptor, defaults);
        break;
      case 2 :
        checkin_2(dbHelper, user, encryptor, defaults);
        break;
      case 3 :
        checkin_3(dbHelper, user, encryptor, defaults);
        break;
      case 4 :
        checkin_4(dbHelper, user, encryptor, defaults);
        break;
      }
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // Utilities for check-in
  // --------------------------------------------------------------------------------

  /**
   * Checks-in the reference to the section this document is contained in.
   * TODO: Should be moved to AbstractDocumentToCheckIn, since this method
   * is specific to documents.
   */

  protected void checkinContainedIn (DbHelper dbHelper)
    throws SQLException
  {
    final String METHOD_NAME = "checkinContainedIn";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    int contained_in = this.getContainedIn(dbHelper);
    if (contained_in == Id.UNDEFINED)
      throw new SQLException(this.path + ": section to be contained in does not exist.");
    dbHelper.updateDatum
      (this.type, this.id, DbColumn.CONTAINED_IN, contained_in);
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // Checking-in read permissions
  // --------------------------------------------------------------------------------

  /**
   * Checks-in the read permissions of this document.
   */

  public void checkinReadPermissions (DbHelper dbHelper, Master defaults)
    throws CheckinException, MasterException, SQLException
  {
    final String METHOD_NAME = "checkinReadPermissions";
    this.logDebug(METHOD_NAME + " 1/3: Started");

    Master[] readPermissions = this.master.getReadPermissions();
    if ( readPermissions.length == 0 )
      readPermissions = defaults.getReadPermissions();
    this.logDebug
      (METHOD_NAME + " 2/3: readPermissions: " + mastersToString(readPermissions));

    for (int i = 0; i < readPermissions.length; i++)
      {
        Master readPermission = readPermissions[i];
        if ( readPermission.getNature() != Nature.PSEUDO_DOCUMENT ||
             readPermission.getType() != PseudoDocType.USER_GROUP )
          throw new CheckinException
            ("Expected a user group, but found: " +
             readPermission.getTypeName());
        String userGroupPath = readPermission.getPath();
        int userGroupId =
          dbHelper.getPseudoDocIdForPath(PseudoDocType.USER_GROUP, userGroupPath);
        dbHelper.storeReadPermission(this.nature, this.type, this.id, userGroupId);
      }

    this.logDebug(METHOD_NAME + " 3/3: Done");
  }

  // --------------------------------------------------------------------------------
  // Checking-in write permissions
  // --------------------------------------------------------------------------------

  /**
   * Checks-in the write permissions of this document.
   */

  public void checkinWritePermissions (DbHelper dbHelper, Master defaults)
    throws CheckinException, MasterException, SQLException
  {
    final String METHOD_NAME = "checkinWritePermissions";
    this.logDebug(METHOD_NAME + " 1/3: Started");

    Master[] writePermissions = this.master.getWritePermissions();
    if ( writePermissions.length == 0 )
      writePermissions = defaults.getWritePermissions();
    this.logDebug
      (METHOD_NAME + " 2/3: writePermissions: " + mastersToString(writePermissions));

    for (int i = 0; i < writePermissions.length; i++)
      {
        Master writePermission = writePermissions[i];
        if ( writePermission.getNature() != Nature.PSEUDO_DOCUMENT ||
             writePermission.getType() != PseudoDocType.USER_GROUP )
          throw new CheckinException
            ("Expected a user group, but found: " +
             writePermission.getTypeName());
        String userGroupPath = writePermission.getPath();
        int userGroupId =
          dbHelper.getPseudoDocIdForPath(PseudoDocType.USER_GROUP, userGroupPath);
	switch ( this.nature )
	  {
	  case Nature.DOCUMENT: 
	    {	    
	      dbHelper.storeDocumentWritePermission(this.type,((DocumentToCheckin)this).getVcThreadId(),userGroupId);
	      break;
	    }
	  case Nature.PSEUDO_DOCUMENT: 
	    {
	      dbHelper.storePseudoDocWritePermission(this.type,this.id,userGroupId);
	      break;
	    }
	  default:
	    throw new SQLException
	      ("Method " + METHOD_NAME + " no proper nature of document ('document' or 'pseudo_document') specified: " + this.nature);
	  }
      }
	this.logDebug(METHOD_NAME + " 3/3: Done");
  }

  // --------------------------------------------------------------------------------
  // Utilities to prepare the map with the checkin data
  // --------------------------------------------------------------------------------

  /**
   * Inserts the name into the specified map.
   */

  protected void nameToData (Map data)
    throws MasterException
  {
    data.put(DbColumn.NAME, this.master.getName());
  }

  /**
   * Inserts the description into the specified map.
   */

  protected void descriptionToData (Map data)
    throws MasterException
  {
    data.put(DbColumn.DESCRIPTION, this.master.getDescription());
  }

  /**
   * Inserts the pure name into the specified map. This method does <strong>not</strong>
   * work for sections, because their pure names are extracted differently from the path
   * then with other (pseudo-)documents.
   */

  protected void pureNameToData (Map data)
    throws MasterException
  {
    this.pathTokenizer.tokenize(this.path);
    data.put(DbColumn.PURE_NAME, this.pathTokenizer.getPureName());
  }

  /**
   * Inserts the <em>hide</em> flag into the specified map, provided there is such e flag.
   */

  protected void hideToData (Map data)
    throws MasterException
  {
    Boolean hide = this.master.getHide();
    if ( hide != null )
      data.put(DbColumn.HIDE, hide);
  }

  // --------------------------------------------------------------------------------
  // Utilities
  // --------------------------------------------------------------------------------

  /**
   * Returns the id of the section this content object is contained in.
   */

  protected int getContainedIn (DbHelper dbHelper)
    throws SQLException 
  {
    final String METHOD_NAME = "getContainedIn";
    this.pathTokenizer.tokenize(this.path);
    String sectionPath = this.pathTokenizer.getSectionPath();
    int sectionId = dbHelper.getSectionIdForPath(sectionPath);
    this.logDebug(METHOD_NAME + " 2/2: Done. sectionId = " + sectionId);
    return sectionId;
  }

  /**
   * Resolves the prefix keywords of the specified path with respect to the specified
   * context path.
   */

  protected String resolvePathPrefixKeyword (String path, String contextPath)
    throws IllegalArgumentException
  {
    final String METHOD_NAME = "resolvePathPrefixKeyword";
    this.logDebug
      (METHOD_NAME + " 1/2: Started." +
       " path = " + path +
       ", contextPath = " + contextPath);
    String resolvedPath;
    this.pathTokenizer.tokenize(path);
    String prefixKeyword = this.pathTokenizer.getPrefixKeyword();
    String pathWithoutPrefixKeyword = this.pathTokenizer.getPathWithoutPrefixKeyword();
    if ( prefixKeyword == null )
      resolvedPath = pathWithoutPrefixKeyword;
    else
      {
        this.pathTokenizer.tokenize(contextPath);
        if ( this.pathTokenizer.getPrefixKeyword() != null )
          throw new IllegalArgumentException
            ("Context path must not begin with a prefix keyword: " + contextPath);
        if ( prefixKeyword.equals("current:") )
          {
            String contextSectionPath = this.pathTokenizer.getSectionPath();
            resolvedPath =
              (contextSectionPath == null
               ? pathWithoutPrefixKeyword
               : contextSectionPath + File.separator + pathWithoutPrefixKeyword);
          }
        else if ( prefixKeyword.equals("parent:") )
          {
            String contextSectionPath = this.pathTokenizer.getSectionPath();
            if ( contextSectionPath == null )
              throw new IllegalArgumentException
                ("Context path has no parent: " + contextPath);
            this.pathTokenizer.tokenize(contextSectionPath);
            String parentSectionPath = this.pathTokenizer.getSectionPath();
            resolvedPath =
              (parentSectionPath == null
               ? pathWithoutPrefixKeyword
               : parentSectionPath + File.separator + pathWithoutPrefixKeyword);
          }
        else
          throw new IllegalArgumentException
            ("Unknown prefix keyword: " + prefixKeyword);
      }
    this.logDebug(METHOD_NAME + " 2/2: Done. resolvedPath = " + resolvedPath);
    return resolvedPath;
  }

  /**
   * Returns the id for the document with the specified type and path. The path may contain
   * prefix keywords, i.e., <code>"current:"</code> or <code>"parent:"</code>. These prefix
   * keywords are resloved before with respect to the specified context path.
   */

  protected int getIdForPath (int type,
                              String path,
                              String contextPath,
                              DbHelper dbHelper)
    throws SQLException, IllegalArgumentException
  {
    return dbHelper.getIdForPath
      (type, this.resolvePathPrefixKeyword(path, contextPath));
  }

  /**
   * Returns the id for the pseudo-document with the specified type and path. The path may
   * contain prefix keywords, i.e., <code>"current:"</code> or <code>"parent:"</code>. These
   * prefix keywords are resloved before with respect to the specified context path.
   */

  protected int getPseudoDocIdForPath (int type,
                                       String path,
                                       String contextPath,
                                       DbHelper dbHelper)
    throws SQLException, IllegalArgumentException
  {
    return dbHelper.getPseudoDocIdForPath
      (type, this.resolvePathPrefixKeyword(path, contextPath));
  }

  /**
   * Returns the id for the language with the specified code.
   */

  protected int getLangIdForCode (String code,DbHelper dbHelper)
    throws SQLException, IllegalArgumentException
  {
    return dbHelper.queryLangIdByCode(code);
  }

  /**
   * Auxiliary method to create log messages. Returns a string containing the paths of the
   * specified masters, separated by spaces.
   */

  protected static String mastersToString (Master[] masters)
  {
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < masters.length; i++)
      {
        String path = null;
        try
          {
            path = masters[i].getPath();
          }
        catch ( MasterException exception )
          {
            path = "#ERROR";
          }
        if ( i > 0 ) buffer.append(" ");
        buffer.append(path != null ? path : "#NONE");
      }
    return buffer.toString();
  }

  // --------------------------------------------------------------------------------
  // To-SAX methods
  // --------------------------------------------------------------------------------

  /**
   * Same as {@link #toSAX(ContentHandler,boolean) toSAX(contentHandler, true)}.
   */

  public void toSAX (ContentHandler contentHandler)
    throws SAXException 
  {
    this.toSAX(contentHandler, true);
  }
}
