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

package net.mumie.cocoon.checkin.pseudodocs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import net.mumie.cocoon.checkin.AbstractContentObjectToCheckin;
import net.mumie.cocoon.checkin.CheckinException;
import net.mumie.cocoon.checkin.Content;
import net.mumie.cocoon.checkin.Master;
import net.mumie.cocoon.checkin.Source;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.pseudodocs.User;
import net.mumie.cocoon.util.PasswordEncryptor;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public abstract class AbstractPseudoDocumentToCheckin extends AbstractContentObjectToCheckin
  implements PseudoDocumentToCheckin
{
  // --------------------------------------------------------------------------------
  // Lifecycle related methods
  // --------------------------------------------------------------------------------

  /**
   * Resets all global variables which are not constant and do not represent services
   * to their initial values. This implementation does nothing. Extending classes may
   * overwrite this.
   */

  protected void resetVariables ()
  {
    // Does nothing
  }

  /**
   * Initializes this pseudo-document with the specified master and path. The
   * content and source must be <code>null</code>; otherwise, an exception is thrown.
   */

  public void setup (Master master, Content content, Source source, String path)
    throws CheckinException
  {
    final String METHOD_NAME = "setup";
    this.logDebug(METHOD_NAME + " 1/2: Started. path = " + path);
    this.path = path;
    this.master = master;
    if ( content != null )
      throw new CheckinException
        (this.path + ": Pseudo-documents must not have content");
    if ( source != null )
      throw new CheckinException
        (this.path + ": Pseudo-documents must not have a source");
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // Getting id of existing
  // --------------------------------------------------------------------------------

  /**
   * Returns the id of the pseudo-document with this path in the database, or
   * {@link Id#UNDEFINED Id.UNDEFINED} if there is no such pseudo-document.
   */

  protected int getIdOfExisting (DbHelper dbHelper)
    throws SQLException, CheckinException
  {
    final String METHOD_NAME = "getIdOfExisting";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    ResultSet resultSet = dbHelper.queryPseudoDocDataByPath
      (this.type, this.path, new String[] {DbColumn.ID});
    int id = (resultSet.next() ? resultSet.getInt(DbColumn.ID) : Id.UNDEFINED);
    this.logDebug(METHOD_NAME + " 2/2: Done, id = " + id);
    return id;
  }

  // --------------------------------------------------------------------------------
  // Checkin
  // --------------------------------------------------------------------------------

  /**
   * Either stores or updates the data contained in the specified map. If this
   * pseudo-document does not yet exist in the database, it is created. Otherwise, it is
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
            // Create new pseudo-document:
            this.logDebug(METHOD_NAME + " 2/3: New pseudo-document");
            this.id = dbHelper.storePseudoDocData(this.type, data);
          }
        else
          {
            // Update existing pseudo-document:
            this.logDebug(METHOD_NAME + " 2/3: Pseudo-document exists");
            this.id = id;
            dbHelper.updatePseudoDocData(this.type, this.id, data);
          }
        this.logDebug(METHOD_NAME + " 3/3: Done");
      }
    catch (Exception exception)
      {
        throw new CheckinException(this.getIdentification(), exception);
      }
  }

  // --------------------------------------------------------------------------------
  // Utilities for check-in
  // --------------------------------------------------------------------------------

  /**
   * Checks-in the reference to the section this document is contained in.
   * Overrides checkinContainedIn (DbHelper) in {@link AbstractContentObjectToCheckin AbstractContentObjectToCheckin}.
   */

  protected void checkinContainedIn (DbHelper dbHelper)
    throws SQLException
  {
    final String METHOD_NAME = "checkinContainedIn";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    HashMap map = new HashMap();
    int contained_in = this.getContainedIn(dbHelper);
    if (contained_in == Id.UNDEFINED)
      throw new SQLException(this.path + ": section to be contained in does not exist.");
    map.put(DbColumn.CONTAINED_IN, Integer.toString(contained_in));
    dbHelper.updatePseudoDocData
      (this.type, this.id, map);
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // To-SAX methods
  // --------------------------------------------------------------------------------

  /**
   * Writes the XML representation of this pseudo-document as SAX events to the specified
   * content handler. If <code>ownDocument</code> is true, the <code>startDocument</code>
   * and <code>endDocument</code> methods of the content handler are called before resp.
   * after. Otherwise, these calls are suppressed.
   */

  public void toSAX (ContentHandler contentHandler, boolean ownDocument)
    throws SAXException
  {
    if ( ownDocument )
      contentHandler.startDocument();

    this.xmlElement.reset();
    this.xmlElement.setLocalName(PseudoDocType.nameFor[this.type]);
    if ( this.id != Id.UNDEFINED )
      this.xmlElement.addAttribute(XMLAttribute.ID, this.id);
    if ( this.path != null )
      this.xmlElement.addAttribute(XMLAttribute.PATH, this.path);
    this.xmlElement.toSAX(contentHandler);

    if ( ownDocument )
      contentHandler.endDocument();
  }
}
